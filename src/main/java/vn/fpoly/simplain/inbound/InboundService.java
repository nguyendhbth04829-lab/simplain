package vn.fpoly.simplain.inbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Service điều phối nghiệp vụ nhập kho theo mô hình deterministic.
 *
 * <p>Các bất biến được bảo vệ ở tầng hệ thống, không phụ thuộc giao diện:</p>
 * <ul>
 *     <li>Toàn bộ danh sách hàng được kiểm tra và chuẩn hóa trước khi ghi bất kỳ dữ liệu nào.</li>
 *     <li>Các dòng trùng SKU được gộp thành một dòng duy nhất, xử lý theo thứ tự SKU ổn định.</li>
 *     <li>Biến thể được khóa bi quan trước khi tính tồn kho và giá vốn, tránh ghi đè khi nhập đồng thời.</li>
 *     <li>Phiếu nhập chỉ chuyển trạng thái qua {@link PhieuNhapKho#chuyenSang(TrangThaiPhieuNhapKho)}.</li>
 * </ul>
 */
@Service
public class InboundService {

    private static final int SO_LUONG_TOI_DA = 999_999;
    private static final int DO_DAI_TOI_DA_MA_NCC = 50;
    private static final DateTimeFormatter DINH_DANG_MA_PHIEU = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final Pattern MA_NCC_HOP_LE = Pattern.compile("^[A-Z0-9_-]{3,50}$");
    private static final Pattern EMAIL_HOP_LE = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final NhaCungCapRepository nhaCungCapRepository;
    private final PhieuNhapKhoRepository phieuNhapKhoRepository;
    private final NhatKyKhoRepository nhatKyKhoRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    public InboundService(NhaCungCapRepository nhaCungCapRepository,
                          PhieuNhapKhoRepository phieuNhapKhoRepository,
                          NhatKyKhoRepository nhatKyKhoRepository,
                          BienTheSanPhamRepository bienTheSanPhamRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.phieuNhapKhoRepository = phieuNhapKhoRepository;
        this.nhatKyKhoRepository = nhatKyKhoRepository;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
    }

    /**
     * Ghi nhận một lần nhập kho trong đúng một transaction.
     * Nếu bất kỳ dòng hàng nào không hợp lệ, toàn bộ phiếu bị hủy bỏ và không có tồn kho nào thay đổi.
     */
    @Transactional
    public NhapKhoResponse thucHienNhapKho(NhapKhoRequest req, Long nhanVienId) {
        if (req == null) {
            throw new IllegalArgumentException("Dữ liệu nhập kho không được để trống");
        }

        String maNhaCungCap = chuanHoaChuoi(req.getMaNhaCungCap());
        if (maNhaCungCap.isEmpty()) {
            throw new IllegalArgumentException("Mã nhà cung cấp không được để trống");
        }
        if (req.getDanhSachHang() == null || req.getDanhSachHang().isEmpty()) {
            throw new IllegalArgumentException("Danh sách mặt hàng nhập không được rỗng");
        }

        NhaCungCap ncc = nhaCungCapRepository.findByMaNccIgnoreCase(maNhaCungCap)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Nhà cung cấp có mã: " + maNhaCungCap));
        if (!Boolean.TRUE.equals(ncc.getKichHoat())) {
            throw new IllegalStateException("Nhà cung cấp [" + ncc.getTenNcc() + "] đang tạm ngưng hoạt động, không thể nhập kho");
        }

        // Giai đoạn 1: chuẩn hóa toàn bộ đầu vào, không chạm vào cơ sở dữ liệu.
        List<DongNhapChuan> danhSachChuan = chuanHoaDanhSachHang(req.getDanhSachHang());

        // Giai đoạn 2: khóa biến thể theo đúng thứ tự SKU đã sắp xếp để tránh deadlock giữa các transaction.
        List<DongNhapDaKhoa> danhSachDaKhoa = new ArrayList<>(danhSachChuan.size());
        for (DongNhapChuan dong : danhSachChuan) {
            BienTheSanPham bienThe = bienTheSanPhamRepository.findByMaSkuIgnoreCaseForUpdate(dong.maSku())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy biến thể SKU: " + dong.maSku()));

            if (!Boolean.TRUE.equals(bienThe.getKichHoat())) {
                throw new IllegalStateException("Biến thể SKU [" + bienThe.getMaSku() + "] đang tạm ngưng, không thể nhập kho");
            }

            int tonTruoc = bienThe.getSoLuong() != null ? bienThe.getSoLuong() : 0;
            long tonSau = (long) tonTruoc + dong.soLuong();
            if (tonSau > SO_LUONG_TOI_DA) {
                throw new IllegalArgumentException("Tồn kho của SKU " + bienThe.getMaSku()
                        + " sẽ vượt giới hạn " + SO_LUONG_TOI_DA + " sau khi nhập");
            }

            danhSachDaKhoa.add(new DongNhapDaKhoa(dong, bienThe));
        }

        // Giai đoạn 3: chỉ sau khi mọi dòng đều hợp lệ mới tạo phiếu và ghi sổ cái.
        LocalDateTime now = LocalDateTime.now();
        String maPhieu = taoMaPhieu(now);

        PhieuNhapKho phieu = PhieuNhapKho.builder()
                .maPhieu(maPhieu)
                .nhaCungCap(ncc)
                .maNhanVienId(nhanVienId)
                .ngayNhap(now)
                .trangThai(TrangThaiPhieuNhapKho.CHO_NHAP)
                .ghiChu(chuanHoaChuoi(req.getGhiChu()))
                .tongTien(BigDecimal.ZERO)
                .chiTietList(new ArrayList<>())
                .build();

        BigDecimal tongTienPhieu = BigDecimal.ZERO;
        List<NhapKhoResponse.ChiTietKetQua> danhSachKetQua = new ArrayList<>();

        for (DongNhapDaKhoa dongDaKhoa : danhSachDaKhoa) {
            DongNhapChuan dong = dongDaKhoa.dongNhap();
            BienTheSanPham bienThe = dongDaKhoa.bienThe();

            int tonTruoc = bienThe.getSoLuong() != null ? bienThe.getSoLuong() : 0;
            int tonSau = tonTruoc + dong.soLuong();

            // Giá vốn bình quân gia quyền di động, dùng tổng giá trị chính xác của lô hàng.
            BigDecimal giaGocCu = bienThe.getGiaGoc() != null ? bienThe.getGiaGoc() : BigDecimal.ZERO;
            BigDecimal tongGiaTriCu = giaGocCu.multiply(BigDecimal.valueOf(tonTruoc));
            BigDecimal giaGocMoi = tongGiaTriCu.add(dong.tongGiaTri())
                    .divide(BigDecimal.valueOf(tonSau), 2, RoundingMode.HALF_UP);

            bienThe.setSoLuong(tonSau);
            bienThe.setGiaGoc(giaGocMoi);
            bienTheSanPhamRepository.save(bienThe);

            NhatKyKho log = NhatKyKho.builder()
                    .maBienTheId(bienThe.getId())
                    .loaiBienDong("NHAP_KHO")
                    .soLuongThayDoi(dong.soLuong())
                    .tonTruoc(tonTruoc)
                    .tonSau(tonSau)
                    .maChungTu(maPhieu)
                    .ngayTao(now)
                    .build();
            nhatKyKhoRepository.save(log);

            ChiTietPhieuNhapKho chiTiet = ChiTietPhieuNhapKho.builder()
                    .phieuNhapKho(phieu)
                    .bienThe(bienThe)
                    .soLuongNhap(dong.soLuong())
                    .donGiaNhap(dong.donGiaBinhQuan())
                    .thanhTien(dong.tongGiaTri())
                    .build();
            phieu.getChiTietList().add(chiTiet);
            tongTienPhieu = tongTienPhieu.add(dong.tongGiaTri());

            danhSachKetQua.add(NhapKhoResponse.ChiTietKetQua.builder()
                    .maSku(bienThe.getMaSku())
                    .soLuongNhap(dong.soLuong())
                    .tonTruoc(tonTruoc)
                    .tonSau(tonSau)
                    .donGiaNhap(dong.donGiaBinhQuan())
                    .giaGocMoi(giaGocMoi)
                    .build());
        }

        phieu.setTongTien(tongTienPhieu);
        phieu.chuyenSang(TrangThaiPhieuNhapKho.DA_NHAP);
        phieuNhapKhoRepository.save(phieu);

        return NhapKhoResponse.builder()
                .trangThai("THANH_CONG")
                .thongBao("Đã nhập kho thành công lô hàng vào hệ thống")
                .maPhieu(maPhieu)
                .nhaCungCap(ncc.getTenNcc())
                .tongTien(tongTienPhieu)
                .ngayNhap(now)
                .chiTiet(danhSachKetQua)
                .build();
    }

    /**
     * Gộp các dòng trùng SKU và sắp xếp theo mã SKU.
     * Nhờ đó cùng một tập dòng hàng luôn cho ra cùng một kết quả, bất kể thứ tự gửi lên.
     */
    private List<DongNhapChuan> chuanHoaDanhSachHang(List<NhapKhoRequest.ChiTietNhapItem> danhSachHang) {
        Map<String, DongNhapTam> gopTheoSku = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (NhapKhoRequest.ChiTietNhapItem item : danhSachHang) {
            if (item == null) {
                throw new IllegalArgumentException("Dòng hàng nhập kho không được để trống");
            }

            String maSku = chuanHoaChuoi(item.getMaSku());
            if (maSku.isEmpty()) {
                throw new IllegalArgumentException("Mã SKU không được để trống");
            }

            Integer soLuongNhap = item.getSoLuongNhap();
            if (soLuongNhap == null || soLuongNhap <= 0) {
                throw new IllegalArgumentException("Số lượng nhập của SKU " + maSku + " phải > 0");
            }

            BigDecimal donGiaNhap = item.getDonGiaNhap();
            if (donGiaNhap == null || donGiaNhap.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Đơn giá nhập của SKU " + maSku + " phải > 0");
            }

            DongNhapTam dongTam = gopTheoSku.computeIfAbsent(maSku.toUpperCase(Locale.ROOT), key -> new DongNhapTam(maSku));
            dongTam.soLuong += soLuongNhap;
            dongTam.tongGiaTri = dongTam.tongGiaTri.add(donGiaNhap.multiply(BigDecimal.valueOf(soLuongNhap)));

            if (dongTam.soLuong > SO_LUONG_TOI_DA) {
                throw new IllegalArgumentException("Tổng số lượng nhập của SKU " + maSku
                        + " vượt giới hạn " + SO_LUONG_TOI_DA);
            }
        }

        List<DongNhapChuan> ketQua = new ArrayList<>(gopTheoSku.size());
        for (DongNhapTam dongTam : gopTheoSku.values()) {
            BigDecimal donGiaBinhQuan = dongTam.tongGiaTri
                    .divide(BigDecimal.valueOf(dongTam.soLuong), 2, RoundingMode.HALF_UP);
            ketQua.add(new DongNhapChuan(dongTam.maSku, dongTam.soLuong, donGiaBinhQuan, dongTam.tongGiaTri));
        }
        return ketQua;
    }

    private String taoMaPhieu(LocalDateTime now) {
        String hauTo = UUID.randomUUID().toString().replace("-", "")
                .substring(0, 6).toUpperCase(Locale.ROOT);
        return "PNK-" + DINH_DANG_MA_PHIEU.format(now) + "-" + hauTo;
    }

    private String chuanHoaChuoi(String giaTri) {
        return giaTri == null ? "" : giaTri.trim();
    }

    private record DongNhapChuan(String maSku, int soLuong, BigDecimal donGiaBinhQuan, BigDecimal tongGiaTri) {
    }

    private record DongNhapDaKhoa(DongNhapChuan dongNhap, BienTheSanPham bienThe) {
    }

    private static final class DongNhapTam {
        private final String maSku;
        private int soLuong;
        private BigDecimal tongGiaTri = BigDecimal.ZERO;

        private DongNhapTam(String maSku) {
            this.maSku = maSku;
        }
    }

    @Transactional(readOnly = true)
    public List<NhaCungCap> layTatCaNhaCungCap() {
        // Thứ tự luôn ổn định theo mã, không phụ thuộc thứ tự chèn hay id tự tăng.
        return nhaCungCapRepository.findAll(Sort.by(Sort.Direction.ASC, "maNcc"));
    }

    @Transactional
    public NhaCungCap taoNhaCungCap(NhaCungCapRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Dữ liệu nhà cung cấp không được để trống");
        }

        String tenNcc = chuanHoaKhoangTrang(req.getTenNcc());
        if (tenNcc.isEmpty()) {
            throw new IllegalArgumentException("Tên nhà cung cấp / Xưởng may không được để trống");
        }

        String maNcc = chuanHoaMaNcc(req.getMaNcc(), tenNcc);
        String soDienThoai = chuanHoaSoDienThoai(req.getSoDienThoai());
        String email = chuanHoaEmail(req.getEmail());
        String diaChi = chuanHoaKhoangTrang(req.getDiaChi());
        String ghiChu = chuanHoaKhoangTrang(req.getGhiChu());

        // Kiểm tra trùng trước khi ghi để cùng một đầu vào luôn cho cùng một kết quả hoặc cùng một lỗi.
        if (nhaCungCapRepository.existsByMaNccIgnoreCase(maNcc)) {
            throw new IllegalArgumentException("Mã nhà cung cấp " + maNcc + " đã tồn tại trong hệ thống");
        }
        if (nhaCungCapRepository.existsByTenNccIgnoreCase(tenNcc)) {
            throw new IllegalArgumentException("Nhà cung cấp / Xưởng may [" + tenNcc + "] đã tồn tại trong hệ thống");
        }

        NhaCungCap ncc = NhaCungCap.builder()
                .maNcc(maNcc)
                .tenNcc(tenNcc)
                .soDienThoai(chuanHoaHoacNull(soDienThoai))
                .email(chuanHoaHoacNull(email))
                .diaChi(chuanHoaHoacNull(diaChi))
                .ghiChu(chuanHoaHoacNull(ghiChu))
                .kichHoat(true)
                .build();

        return nhaCungCapRepository.save(ncc);
    }

    @Transactional
    public NhaCungCap capNhatNhaCungCap(Long id, NhaCungCapRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Dữ liệu nhà cung cấp không được để trống");
        }

        NhaCungCap ncc = nhaCungCapRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Nhà cung cấp có ID: " + id));

        if (req.getTenNcc() != null) {
            String tenNcc = chuanHoaKhoangTrang(req.getTenNcc());
            if (tenNcc.isEmpty()) {
                throw new IllegalArgumentException("Tên nhà cung cấp / Xưởng may không được để trống");
            }
            if (!tenNcc.equalsIgnoreCase(ncc.getTenNcc())
                    && nhaCungCapRepository.existsByTenNccIgnoreCaseAndIdNot(tenNcc, id)) {
                throw new IllegalArgumentException("Nhà cung cấp / Xưởng may [" + tenNcc + "] đã tồn tại trong hệ thống");
            }
            ncc.setTenNcc(tenNcc);
        }
        if (req.getSoDienThoai() != null) {
            ncc.setSoDienThoai(chuanHoaHoacNull(chuanHoaSoDienThoai(req.getSoDienThoai())));
        }
        if (req.getEmail() != null) {
            ncc.setEmail(chuanHoaHoacNull(chuanHoaEmail(req.getEmail())));
        }
        if (req.getDiaChi() != null) {
            ncc.setDiaChi(chuanHoaHoacNull(chuanHoaKhoangTrang(req.getDiaChi())));
        }
        if (req.getGhiChu() != null) {
            ncc.setGhiChu(chuanHoaHoacNull(chuanHoaKhoangTrang(req.getGhiChu())));
        }
        if (req.getKichHoat() != null) {
            ncc.setKichHoat(req.getKichHoat());
        }

        return nhaCungCapRepository.save(ncc);
    }

    /**
     * Chuẩn hóa mã nhà cung cấp theo một quy tắc duy nhất.
     * Nếu người dùng bỏ trống, mã được sinh ổn định từ chính tên đã chuẩn hóa (slug + hash),
     * nhờ đó cùng một tên luôn cho cùng một mã thay vì phụ thuộc thời điểm tạo.
     */
    private String chuanHoaMaNcc(String maNcc, String tenNcc) {
        String ma = chuanHoaChuoi(maNcc).toUpperCase(Locale.ROOT);
        if (ma.isEmpty()) {
            ma = sinhMaNccTuTen(tenNcc);
        }
        if (ma.length() > DO_DAI_TOI_DA_MA_NCC || !MA_NCC_HOP_LE.matcher(ma).matches()) {
            throw new IllegalArgumentException(
                    "Mã nhà cung cấp chỉ gồm chữ cái không dấu, chữ số, dấu gạch ngang hoặc gạch dưới (3-50 ký tự)");
        }
        return ma;
    }

    private String sinhMaNccTuTen(String tenNcc) {
        String slug = boDauTiengViet(tenNcc)
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (slug.length() > 32) {
            slug = slug.substring(0, 32).replaceAll("-+$", "");
        }
        String hauTo = sha256Hex(tenNcc).substring(0, 6).toUpperCase(Locale.ROOT);
        return "NCC-" + (slug.isEmpty() ? hauTo : slug + "-" + hauTo);
    }

    private String chuanHoaSoDienThoai(String soDienThoai) {
        String raw = chuanHoaChuoi(soDienThoai);
        if (raw.isEmpty()) {
            return "";
        }
        boolean coDauCong = raw.startsWith("+");
        String chuSo = raw.replaceAll("[^0-9]", "");
        if (chuSo.length() < 9 || chuSo.length() > 15) {
            throw new IllegalArgumentException("Số điện thoại phải chứa từ 9 đến 15 chữ số");
        }
        return (coDauCong ? "+" : "") + chuSo;
    }

    private String chuanHoaEmail(String email) {
        String chuanHoa = chuanHoaChuoi(email).toLowerCase(Locale.ROOT);
        if (chuanHoa.isEmpty()) {
            return "";
        }
        if (!EMAIL_HOP_LE.matcher(chuanHoa).matches()) {
            throw new IllegalArgumentException("Email nhà cung cấp không đúng định dạng");
        }
        return chuanHoa;
    }

    private String chuanHoaKhoangTrang(String giaTri) {
        return chuanHoaChuoi(giaTri).replaceAll("\\s+", " ");
    }

    private String chuanHoaHoacNull(String giaTri) {
        String chuanHoa = chuanHoaChuoi(giaTri);
        return chuanHoa.isEmpty() ? null : chuanHoa;
    }

    private String boDauTiengViet(String giaTri) {
        String khongDau = Normalizer.normalize(giaTri, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return khongDau.replace('đ', 'd').replace('Đ', 'D');
    }

    private String sha256Hex(String giaTri) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bam = digest.digest(giaTri.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bam);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Không thể khởi tạo thuật toán SHA-256", e);
        }
    }

    @Transactional
    public void xoaNhaCungCap(Long id) {
        NhaCungCap ncc = nhaCungCapRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Nhà cung cấp có ID: " + id));
        ncc.setKichHoat(false);
        nhaCungCapRepository.save(ncc);
    }

    @Transactional
    public NhaCungCap toggleTrangThaiNcc(Long id) {
        NhaCungCap ncc = nhaCungCapRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Nhà cung cấp có ID: " + id));
        boolean trangThaiMoi = !Boolean.TRUE.equals(ncc.getKichHoat());
        ncc.setKichHoat(trangThaiMoi);
        return nhaCungCapRepository.save(ncc);
    }
}
