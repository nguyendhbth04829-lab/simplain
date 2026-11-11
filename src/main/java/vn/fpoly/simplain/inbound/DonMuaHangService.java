package vn.fpoly.simplain.inbound;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DonMuaHangService {
    private static final DateTimeFormatter MA_DON_TIME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String DIEU_KIEN_GIAO = "Nhà cung cấp chịu trách nhiệm giao đến cửa hàng";

    private final DonMuaHangRepository donMuaRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final BienTheSanPhamRepository bienTheRepository;
    private final SanPhamRepository sanPhamRepository;
    private final NhaCungCapSkuRepository nhaCungCapSkuRepository;
    private final LanGiaoHangRepository lanGiaoRepository;
    private final LichSuDonMuaHangRepository lichSuRepository;
    private final ChiTietKiemHangRepository kiemHangRepository;
    private final PhieuNhapKhoRepository phieuNhapRepository;
    private final InboundService inboundService;
    private final LichSuBaoGiaService lichSuBaoGiaService;

    public DonMuaHangService(DonMuaHangRepository donMuaRepository,
                             NhaCungCapRepository nhaCungCapRepository,
                             BienTheSanPhamRepository bienTheRepository,
                             SanPhamRepository sanPhamRepository,
                             NhaCungCapSkuRepository nhaCungCapSkuRepository,
                             LanGiaoHangRepository lanGiaoRepository,
                             LichSuDonMuaHangRepository lichSuRepository,
                             ChiTietKiemHangRepository kiemHangRepository,
                             PhieuNhapKhoRepository phieuNhapRepository,
                             InboundService inboundService,
                             LichSuBaoGiaService lichSuBaoGiaService) {
        this.donMuaRepository = donMuaRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.bienTheRepository = bienTheRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.nhaCungCapSkuRepository = nhaCungCapSkuRepository;
        this.lanGiaoRepository = lanGiaoRepository;
        this.lichSuRepository = lichSuRepository;
        this.kiemHangRepository = kiemHangRepository;
        this.phieuNhapRepository = phieuNhapRepository;
        this.inboundService = inboundService;
        this.lichSuBaoGiaService = lichSuBaoGiaService;
    }

    @Transactional(readOnly = true)
    public List<DonMuaHangResponse> layDanhSach() {
        return donMuaRepository.findAllByOrderByIdDesc().stream().map(this::taoResponse).toList();
    }

    @Transactional(readOnly = true)
    public DonMuaHangResponse layChiTiet(Long id) {
        return taoResponse(layDonMua(id));
    }

    @Transactional
    public DonMuaHangResponse taoDon(DonMuaHangRequest request, Long nhanVienId, String tenNhanVien) {
        if (request == null) throw new IllegalArgumentException("Dữ liệu đơn mua không được để trống");
        if (request.getNhaCungCapId() == null) throw new IllegalArgumentException("Vui lòng chọn nhà cung cấp");
        NhaCungCap ncc = nhaCungCapRepository.findById(request.getNhaCungCapId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà cung cấp"));
        if (ncc.getTrangThaiHopTac() != TrangThaiNhaCungCap.DANG_HOP_TAC) {
            throw new IllegalStateException("Chỉ nhà cung cấp Đang hợp tác mới được tạo đơn mua");
        }
        if (request.getDanhSachHang() == null || request.getDanhSachHang().isEmpty()) {
            throw new IllegalArgumentException("Đơn mua phải có ít nhất một SKU");
        }

        Map<String, DonMuaHangRequest.ChiTiet> theoSku = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (DonMuaHangRequest.ChiTiet item : request.getDanhSachHang()) {
            if (item == null) throw new IllegalArgumentException("Dòng đơn mua không được để trống");
            String sku = chuanHoa(item.getMaSku());
            if (sku.isEmpty()) throw new IllegalArgumentException("Mã SKU không được để trống");
            if (item.getSoLuongDat() == null || item.getSoLuongDat() <= 0 || item.getSoLuongDat() > 999999) {
                throw new IllegalArgumentException("Số lượng đặt của SKU " + sku + " phải từ 1 đến 999.999");
            }
            if (item.getDonGiaChot() != null) {
                throw new IllegalArgumentException("Giá thỏa thuận chỉ được nhập sau khi lưu nháp, trong bước thương lượng");
            }
            if (theoSku.putIfAbsent(sku, item) != null) {
                throw new IllegalArgumentException("SKU " + sku + " bị lặp trong đơn mua");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        String maDon = "DMH-" + MA_DON_TIME.format(now) + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
        DonMuaHang don = DonMuaHang.builder()
                .maDon(maDon).nhaCungCap(ncc).maNhanVienId(nhanVienId)
                .ngayTao(now).trangThai(TrangThaiDonMuaHang.NHAP)
                .dieuKienGiaoHang(DIEU_KIEN_GIAO)
                .ghiChu(chuanHoa(request.getGhiChu()))
                .chiTietList(new ArrayList<>()).build();

        BigDecimal tongTien = BigDecimal.ZERO;
        for (var entry : theoSku.entrySet()) {
            String sku = entry.getKey();
            var item = entry.getValue();
            BienTheSanPham bienThe = bienTheRepository.findByMaSkuIgnoreCase(sku)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy SKU: " + sku));
            if (!Boolean.TRUE.equals(bienThe.getKichHoat())) {
                throw new IllegalStateException("SKU " + sku + " đang tạm ngưng");
            }
            kiemTraSkuDuocPhanCong(ncc.getId(), bienThe);
            BigDecimal donGia = item.getDonGiaChot() != null ? item.getDonGiaChot() : BigDecimal.ZERO;
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(item.getSoLuongDat()));
            ChiTietDonMuaHang ct = ChiTietDonMuaHang.builder()
                    .donMuaHang(don).bienThe(bienThe).maSku(bienThe.getMaSku())
                    .tenSanPhamChot(layTenSanPham(bienThe)).soLuongDat(item.getSoLuongDat())
                    .soLuongDaNhan(0).donGiaChot(donGia).thanhTien(thanhTien).build();
            don.getChiTietList().add(ct);
            tongTien = tongTien.add(thanhTien);
        }
        don.setTongTien(tongTien);
        DonMuaHang saved = donMuaRepository.save(don);
        ghiLichSu(saved, "TAO_DON", null, saved.getTrangThai(), "Tạo đơn mua ở trạng thái nháp.", nhanVienId, tenNhanVien);
        return taoResponse(saved);
    }

    @Transactional
    public DonMuaHangResponse capNhatDonNhap(Long id, DonMuaHangRequest request, Long nhanVienId, String tenNhanVien) {
        DonMuaHang don = layDonMuaKhoa(id);
        if (don.getTrangThai() != TrangThaiDonMuaHang.NHAP) {
            throw new IllegalStateException("Chỉ được sửa đơn mua ở trạng thái Nháp");
        }
        if (request == null || request.getDanhSachHang() == null || request.getDanhSachHang().isEmpty()) {
            throw new IllegalArgumentException("Đơn mua phải có ít nhất một SKU");
        }
        if (request.getNhaCungCapId() == null) throw new IllegalArgumentException("Vui lòng chọn nhà cung cấp");
        NhaCungCap ncc = nhaCungCapRepository.findById(request.getNhaCungCapId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà cung cấp"));
        if (ncc.getTrangThaiHopTac() != TrangThaiNhaCungCap.DANG_HOP_TAC) {
            throw new IllegalStateException("Chỉ nhà cung cấp Đang hợp tác mới được dùng cho đơn nháp");
        }
        Map<String, DonMuaHangRequest.ChiTiet> theoSku = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (DonMuaHangRequest.ChiTiet item : request.getDanhSachHang()) {
            if (item == null || item.getMaSku() == null || item.getMaSku().isBlank()
                    || item.getSoLuongDat() == null || item.getSoLuongDat() <= 0 || item.getSoLuongDat() > 999999) {
                throw new IllegalArgumentException("Kiểm tra lại SKU và số lượng của đơn mua");
            }
            if (item.getDonGiaChot() != null) {
                throw new IllegalArgumentException("Giá thỏa thuận không thể sửa trong đơn nháp; hãy nhập ở bước thương lượng");
            }
            String sku = chuanHoa(item.getMaSku());
            if (theoSku.putIfAbsent(sku, item) != null) throw new IllegalArgumentException("SKU " + sku + " bị lặp trong đơn mua");
        }
        don.getChiTietList().clear();
        BigDecimal tongTien = BigDecimal.ZERO;
        for (var entry : theoSku.entrySet()) {
            BienTheSanPham bt = bienTheRepository.findByMaSkuIgnoreCase(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy SKU: " + entry.getKey()));
            if (!Boolean.TRUE.equals(bt.getKichHoat())) throw new IllegalStateException("SKU " + entry.getKey() + " đang tạm ngưng");
            kiemTraSkuDuocPhanCong(ncc.getId(), bt);
            var item = entry.getValue();
            BigDecimal donGia = item.getDonGiaChot() != null ? item.getDonGiaChot() : BigDecimal.ZERO;
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(item.getSoLuongDat()));
            don.getChiTietList().add(ChiTietDonMuaHang.builder().donMuaHang(don).bienThe(bt).maSku(bt.getMaSku())
                    .tenSanPhamChot(layTenSanPham(bt)).soLuongDat(item.getSoLuongDat()).soLuongDaNhan(0)
                    .donGiaChot(donGia).thanhTien(thanhTien).build());
            tongTien = tongTien.add(thanhTien);
        }
        don.setNhaCungCap(ncc);
        don.setGhiChu(chuanHoa(request.getGhiChu()));
        don.setTongTien(tongTien);
        DonMuaHang updated = donMuaRepository.save(don);
        ghiLichSu(updated, "SUA_DON_NHAP", updated.getTrangThai(), updated.getTrangThai(),
                "Cập nhật các dòng hàng hoặc thông tin của đơn nháp.", nhanVienId, tenNhanVien);
        return taoResponse(updated);
    }

    @Transactional
    public DonMuaHangResponse capNhatGiaThoaThuan(Long id, DonMuaHangRequest request,
                                                   Long nhanVienId, String tenNhanVien) {
        DonMuaHang don = layDonMuaKhoa(id);
        if (don.getTrangThai() != TrangThaiDonMuaHang.CHO_NCC_PHAN_HOI
                && don.getTrangThai() != TrangThaiDonMuaHang.DANG_THUONG_LUONG) {
            throw new IllegalStateException("Chỉ được ghi giá thỏa thuận khi đơn đang chờ phản hồi hoặc thương lượng");
        }
        if (request == null || request.getNhaCungCapId() == null
                || !request.getNhaCungCapId().equals(don.getNhaCungCap().getId())) {
            throw new IllegalArgumentException("Nhà cung cấp không khớp với đơn mua");
        }
        if (request.getDanhSachHang() == null || request.getDanhSachHang().size() != don.getChiTietList().size()) {
            throw new IllegalArgumentException("Cần nhập giá cho toàn bộ SKU trong đơn mua");
        }

        Map<String, DonMuaHangRequest.ChiTiet> theoSku = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (DonMuaHangRequest.ChiTiet item : request.getDanhSachHang()) {
            if (item == null || item.getMaSku() == null || item.getSoLuongDat() == null
                    || item.getDonGiaChot() == null || item.getDonGiaChot().compareTo(BigDecimal.ZERO) <= 0
                    || item.getDonGiaChot().compareTo(new BigDecimal("999999999")) > 0) {
                throw new IllegalArgumentException("Mỗi SKU cần có số lượng và giá thỏa thuận hợp lệ");
            }
            if (theoSku.putIfAbsent(chuanHoa(item.getMaSku()), item) != null) {
                throw new IllegalArgumentException("SKU " + item.getMaSku() + " bị lặp trong giá thỏa thuận");
            }
        }

        BigDecimal tongTien = BigDecimal.ZERO;
        for (ChiTietDonMuaHang line : don.getChiTietList()) {
            DonMuaHangRequest.ChiTiet item = theoSku.get(line.getMaSku());
            if (item == null || !item.getSoLuongDat().equals(line.getSoLuongDat())) {
                throw new IllegalArgumentException("SKU và số lượng phải khớp với đơn đã gửi cho nhà cung cấp");
            }
            line.setDonGiaChot(item.getDonGiaChot());
            line.setThanhTien(item.getDonGiaChot().multiply(BigDecimal.valueOf(line.getSoLuongDat())));
            tongTien = tongTien.add(line.getThanhTien());
            lichSuBaoGiaService.ghiNhanGiaThoaThuan(don.getNhaCungCap(), line.getBienThe(),
                    item.getDonGiaChot(), don.getMaDon());
        }
        if (theoSku.size() != don.getChiTietList().size()) {
            throw new IllegalArgumentException("Danh sách SKU không khớp với đơn mua");
        }
        don.setTongTien(tongTien);
        DonMuaHang updated = donMuaRepository.save(don);
        ghiLichSu(updated, "CAP_NHAT_GIA_THOA_THUAN", updated.getTrangThai(), updated.getTrangThai(),
                "Đã cập nhật giá thỏa thuận cho các SKU.", nhanVienId, tenNhanVien);
        return taoResponse(updated);
    }

    @Transactional
    public DonMuaHangResponse chuyenTrangThai(Long id, TrangThaiDonMuaHang moi, String ghiChu,
                                               Long nhanVienId, String tenNhanVien) {
        DonMuaHang don = layDonMuaKhoa(id);
        if (moi == TrangThaiDonMuaHang.CHO_NCC_PHAN_HOI) {
            if (don.getNhaCungCap().getTrangThaiHopTac() != TrangThaiNhaCungCap.DANG_HOP_TAC) {
                throw new IllegalStateException("Nhà cung cấp không còn ở trạng thái đang hợp tác; chưa thể gửi đơn mới");
            }
            for (ChiTietDonMuaHang line : don.getChiTietList()) {
                if (line.getDonGiaChot() != null && line.getDonGiaChot().compareTo(BigDecimal.ZERO) > 0) {
                    throw new IllegalStateException("Đơn nháp cũ còn giá thỏa thuận. Hãy sửa và lưu lại đơn nháp để nhập giá ở bước thương lượng.");
                }
                kiemTraSkuDuocPhanCong(don.getNhaCungCap().getId(), line.getBienThe());
                if (!Boolean.TRUE.equals(line.getBienThe().getKichHoat())) {
                    throw new IllegalStateException("SKU " + line.getMaSku() + " đang tạm ngưng");
                }
            }
        }
        if (moi == TrangThaiDonMuaHang.DANG_GIAO || moi == TrangThaiDonMuaHang.CHO_KIEM_HANG
                || moi == TrangThaiDonMuaHang.CHO_GIAO_BU || moi == TrangThaiDonMuaHang.DA_HOAN_TAT) {
            throw new IllegalStateException("Trạng thái này chỉ được cập nhật qua quy trình giao hàng/kiểm hàng");
        }
        if (moi == TrangThaiDonMuaHang.DA_CHOT_DON) {
            BigDecimal tongTien = BigDecimal.ZERO;
            for (ChiTietDonMuaHang ct : don.getChiTietList()) {
                if (ct.getDonGiaChot() == null || ct.getDonGiaChot().compareTo(BigDecimal.ZERO) <= 0
                        || ct.getDonGiaChot().compareTo(new BigDecimal("999999999")) > 0) {
                    throw new IllegalStateException("Cần nhập giá thỏa thuận lớn hơn 0 cho SKU " + ct.getMaSku() + " trước khi chốt đơn");
                }
                if (!Boolean.TRUE.equals(ct.getBienThe().getKichHoat())) {
                    throw new IllegalStateException("SKU " + ct.getMaSku() + " đang tạm ngưng");
                }
                ct.setThanhTien(ct.getDonGiaChot().multiply(BigDecimal.valueOf(ct.getSoLuongDat())));
                tongTien = tongTien.add(ct.getThanhTien());
            }
            don.setTongTien(tongTien);
        }
        chuyen(don, moi, ghiChu, nhanVienId, tenNhanVien);
        return taoResponse(donMuaRepository.save(don));
    }

    @Transactional
    public DonMuaHangResponse batDauLanGiao(Long id, Long nhanVienId, String tenNhanVien) {
        DonMuaHang don = layDonMuaKhoa(id);
        if (don.getTrangThai() != TrangThaiDonMuaHang.DANG_CHUAN_BI_HANG
                && don.getTrangThai() != TrangThaiDonMuaHang.CHO_GIAO_BU) {
            throw new IllegalStateException("Đơn phải ở trạng thái Đang chuẩn bị hàng hoặc Chờ giao bổ sung");
        }
        if (lanGiaoRepository.findFirstByDonMuaHangIdAndTrangThaiOrderByLanThuDesc(id, TrangThaiLanGiaoHang.DANG_GIAO).isPresent()) {
            throw new IllegalStateException("Đơn mua đang có một lần giao chưa kết thúc");
        }
        int lanThu = lanGiaoRepository.countByDonMuaHangId(id) + 1;
        LanGiaoHang lan = lanGiaoRepository.save(LanGiaoHang.builder()
                .donMuaHang(don).lanThu(lanThu).trangThai(TrangThaiLanGiaoHang.DANG_GIAO)
                .ngayBatDau(LocalDateTime.now()).build());
        don.getLanGiaoList().add(lan);
        chuyen(don, TrangThaiDonMuaHang.DANG_GIAO, "Bắt đầu lần giao hàng " + lanThu + ".", nhanVienId, tenNhanVien);
        ghiLichSu(don, "BAT_DAU_GIAO_HANG", don.getTrangThai().name(), don.getTrangThai().name(),
                "Bắt đầu lần giao hàng " + lanThu + " (" + lan.getId() + ").", nhanVienId, tenNhanVien);
        return taoResponse(donMuaRepository.save(don));
    }

    @Transactional
    public DonMuaHangResponse baoGiaoThatBai(Long id, Long lanGiaoId, ThatBaiLanGiaoHangRequest request,
                                               Long nhanVienId, String tenNhanVien) {
        DonMuaHang don = layDonMuaKhoa(id);
        LanGiaoHang lan = layLanGiaoDangMo(don, lanGiaoId);
        String lyDo = chuanHoa(request.getLyDo());
        if (lyDo.isEmpty()) throw new IllegalArgumentException("Cần ghi rõ lý do giao thất bại");
        lan.setTrangThai(TrangThaiLanGiaoHang.THAT_BAI);
        lan.setNgayKetThuc(LocalDateTime.now());
        lan.setLyDoThatBai(lyDo);
        lan.setBenChiuTrachNhiem(chuanHoa(request.getBenChiuTrachNhiem()));
        lan.setBangChung(chuanHoa(request.getBangChung()));
        lanGiaoRepository.save(lan);
        chuyen(don, TrangThaiDonMuaHang.CHO_GIAO_BU, "Lần giao " + lan.getLanThu() + " thất bại: " + lyDo,
                nhanVienId, tenNhanVien);
        return taoResponse(donMuaRepository.save(don));
    }

    @Transactional
    public DonMuaHangResponse xacNhanDaGiao(Long id, Long lanGiaoId, Long nhanVienId, String tenNhanVien) {
        DonMuaHang don = layDonMuaKhoa(id);
        LanGiaoHang lan = layLanGiaoDangMo(don, lanGiaoId);
        lan.setTrangThai(TrangThaiLanGiaoHang.DA_GIAO);
        lan.setNgayKetThuc(LocalDateTime.now());
        lanGiaoRepository.save(lan);
        chuyen(don, TrangThaiDonMuaHang.CHO_KIEM_HANG, "Đã giao hàng, chờ kiểm đếm.", nhanVienId, tenNhanVien);
        return taoResponse(donMuaRepository.save(don));
    }

    @Transactional
    public NhapKhoResponse tiepNhanHang(Long id, TiepNhanDonMuaRequest request,
                                        Long nhanVienId, String tenNhanVien) {
        DonMuaHang don = layDonMuaKhoa(id);
        if (don.getTrangThai() != TrangThaiDonMuaHang.CHO_KIEM_HANG) {
            throw new IllegalStateException("Chỉ đơn đã giao mới được kiểm hàng và nhập kho");
        }
        LanGiaoHang lanGiao = lanGiaoRepository.findFirstByDonMuaHangIdAndTrangThaiOrderByLanThuDesc(
                        id, TrangThaiLanGiaoHang.DA_GIAO)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy lần giao đã xác nhận giao đến cửa hàng"));
        if (request == null || request.getDanhSachHang() == null || request.getDanhSachHang().isEmpty()) {
            throw new IllegalArgumentException("Cần nhập kết quả kiểm hàng cho ít nhất một SKU");
        }

        Map<String, ChiTietDonMuaHang> chiTietTheoSku = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (ChiTietDonMuaHang ct : don.getChiTietList()) chiTietTheoSku.put(ct.getMaSku(), ct);
        Set<String> daNhap = new HashSet<>();
        List<NhapKhoRequest.ChiTietNhapItem> hangDat = new ArrayList<>();
        List<ChiTietKiemHang> ketQuaKiem = new ArrayList<>();
        int tongGiao = 0, tongDat = 0, tongTuChoi = 0;

        for (TiepNhanDonMuaRequest.ChiTiet item : request.getDanhSachHang()) {
            if (item == null) throw new IllegalArgumentException("Dòng kiểm hàng không được để trống");
            String sku = chuanHoa(item.getMaSku());
            ChiTietDonMuaHang ct = chiTietTheoSku.get(sku);
            if (ct == null) throw new IllegalArgumentException("SKU " + sku + " không thuộc đơn mua này");
            if (!daNhap.add(sku.toUpperCase(Locale.ROOT))) throw new IllegalArgumentException("SKU " + sku + " bị lặp trong kết quả kiểm hàng");
            int delivered = giaTri(item.getSoLuongDaGiao());
            int accepted = giaTri(item.getSoLuongDat());
            int rejected = giaTri(item.getSoLuongTuChoi());
            int returned = giaTri(item.getSoLuongDaTra());
            if (delivered <= 0 || accepted + rejected != delivered) {
                throw new IllegalArgumentException("SKU " + sku + ": số đã giao phải bằng số đạt cộng số từ chối");
            }
            if (returned > rejected) throw new IllegalArgumentException("SKU " + sku + ": số đã trả không thể lớn hơn số bị từ chối");
            if (rejected > 0 && chuanHoa(item.getLyDoTuChoi()).isEmpty()) {
                throw new IllegalArgumentException("Cần ghi lý do từ chối SKU " + sku);
            }
            int conThieu = ct.getSoLuongDat() - ct.getSoLuongDaNhan();
            if (delivered > conThieu) {
                throw new IllegalArgumentException("SKU " + sku + " chỉ còn " + conThieu + " sản phẩm cần nhận theo đơn mua");
            }
            if (accepted > 0) {
                BigDecimal giaThucTe = item.getDonGiaNhapThucTe() != null ? item.getDonGiaNhapThucTe() : ct.getDonGiaChot();
                if (giaThucTe.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Giá nhập thực tế của SKU " + sku + " phải lớn hơn 0");
                }
                hangDat.add(NhapKhoRequest.ChiTietNhapItem.builder().maSku(ct.getMaSku())
                        .soLuongNhap(accepted).donGiaNhap(giaThucTe).build());
            }
            ct.setSoLuongDaNhan(ct.getSoLuongDaNhan() + accepted);
            tongGiao += delivered;
            tongDat += accepted;
            tongTuChoi += rejected;
            ketQuaKiem.add(ChiTietKiemHang.builder().chiTietDonMua(ct)
                    .soLuongDaGiao(delivered).soLuongDat(accepted).soLuongTuChoi(rejected)
                    .soLuongDaTra(returned).lyDoTuChoi(chuanHoa(item.getLyDoTuChoi())).build());
        }

        NhapKhoRequest nhapKhoRequest = NhapKhoRequest.builder()
                .maNhaCungCap(don.getNhaCungCap().getMaNcc())
                .ghiChu(chuanHoa(request.getGhiChu())).danhSachHang(hangDat).build();
        NhapKhoResponse nhap = inboundService.thucHienNhapTheoDonMua(nhapKhoRequest, nhanVienId, don);
        PhieuNhapKho phieu = phieuNhapRepository.findByMaPhieu(nhap.getMaPhieu()).orElseThrow();
        phieu.setLanGiaoHang(lanGiao);
        KetQuaKiemHang ketQua = tongTuChoi == 0 ? KetQuaKiemHang.CHAP_NHAN_TOAN_BO
                : (tongDat == 0 ? KetQuaKiemHang.TU_CHOI_TOAN_BO : KetQuaKiemHang.CHAP_NHAN_MOT_PHAN);
        phieu.setKetQuaKiemHang(ketQua);
        phieuNhapRepository.save(phieu);
        for (ChiTietKiemHang line : ketQuaKiem) {
            line.setPhieuNhapKho(phieu);
            kiemHangRepository.save(line);
        }

        boolean daNhanDu = don.getChiTietList().stream().allMatch(ct -> ct.getSoLuongDaNhan() >= ct.getSoLuongDat());
        TrangThaiDonMuaHang trangThaiMoi = daNhanDu ? TrangThaiDonMuaHang.DA_HOAN_TAT : TrangThaiDonMuaHang.CHO_GIAO_BU;
        String note = "Phiếu " + phieu.getMaPhieu() + ": giao " + tongGiao + ", đạt " + tongDat
                + ", từ chối " + tongTuChoi + " (" + ketQua.getNhan() + ").";
        if (!chuanHoa(request.getGhiChu()).isEmpty()) note += " " + chuanHoa(request.getGhiChu());
        chuyen(don, trangThaiMoi, note, nhanVienId, tenNhanVien);
        nhap.setThongBao(tongDat == 0
                ? "Đã ghi nhận hàng bị từ chối; không có sản phẩm nào được cộng tồn kho. Mã phiếu: " + phieu.getMaPhieu()
                : "Đã ghi nhận kiểm hàng. Chỉ " + tongDat + " sản phẩm đạt được cộng tồn kho. Mã phiếu: " + phieu.getMaPhieu());
        return nhap;
    }

    private DonMuaHang layDonMua(Long id) {
        return donMuaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn mua ID " + id));
    }

    private DonMuaHang layDonMuaKhoa(Long id) {
        return donMuaRepository.findByIdForUpdate(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn mua ID " + id));
    }

    private LanGiaoHang layLanGiaoDangMo(DonMuaHang don, Long lanGiaoId) {
        LanGiaoHang lan = lanGiaoRepository.findById(lanGiaoId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lần giao hàng"));
        if (!lan.getDonMuaHang().getId().equals(don.getId()) || lan.getTrangThai() != TrangThaiLanGiaoHang.DANG_GIAO) {
            throw new IllegalStateException("Lần giao hàng không thuộc đơn này hoặc đã kết thúc");
        }
        return lan;
    }

    private void chuyen(DonMuaHang don, TrangThaiDonMuaHang moi, String ghiChu, Long nhanVienId, String tenNhanVien) {
        TrangThaiDonMuaHang cu = don.getTrangThai();
        don.chuyenSang(moi);
        ghiLichSu(don, "CHUYEN_TRANG_THAI", cu.name(), moi.name(), chuanHoa(ghiChu), nhanVienId, tenNhanVien);
    }

    private void ghiLichSu(DonMuaHang don, String loai, TrangThaiDonMuaHang cu, TrangThaiDonMuaHang moi,
                           String ghiChu, Long nhanVienId, String tenNhanVien) {
        ghiLichSu(don, loai, cu == null ? null : cu.name(), moi == null ? null : moi.name(), ghiChu, nhanVienId, tenNhanVien);
    }

    private void ghiLichSu(DonMuaHang don, String loai, String cu, String moi,
                           String ghiChu, Long nhanVienId, String tenNhanVien) {
        lichSuRepository.save(LichSuDonMuaHang.builder().donMuaHang(don).loaiHoatDong(loai)
                .trangThaiTruoc(cu).trangThaiMoi(moi).ghiChu(chuanHoa(ghiChu))
                .maNhanVienId(nhanVienId).tenNhanVien(chuanHoa(tenNhanVien)).ngayTao(LocalDateTime.now()).build());
    }

    private DonMuaHangResponse taoResponse(DonMuaHang don) {
        List<DonMuaHangResponse.ChiTiet> lines = don.getChiTietList().stream().map(ct ->
                new DonMuaHangResponse.ChiTiet(ct.getMaSku(), ct.getTenSanPhamChot(), ct.getSoLuongDat(),
                        ct.getSoLuongDaNhan(), ct.getDonGiaChot(), ct.getThanhTien())).toList();
        List<DonMuaHangResponse.LanGiao> deliveries = don.getLanGiaoList().stream()
                .sorted(Comparator.comparing(LanGiaoHang::getLanThu))
                .map(l -> new DonMuaHangResponse.LanGiao(l.getId(), l.getLanThu(), l.getTrangThai().name(),
                        l.getTrangThai().getNhan(), l.getNgayBatDau(), l.getNgayKetThuc(), l.getLyDoThatBai(),
                        l.getBenChiuTrachNhiem(), l.getBangChung())).toList();
        List<DonMuaHangResponse.HoatDong> history = lichSuRepository.findByDonMuaHangIdOrderByNgayTaoDesc(don.getId()).stream()
                .map(h -> new DonMuaHangResponse.HoatDong(h.getLoaiHoatDong(), h.getTrangThaiTruoc(), h.getTrangThaiMoi(),
                        h.getGhiChu(), h.getTenNhanVien(), h.getNgayTao())).toList();
        return new DonMuaHangResponse(don.getId(), don.getMaDon(), don.getNhaCungCap().getId(),
                don.getNhaCungCap().getMaNcc(), don.getNhaCungCap().getTenNcc(), don.getTrangThai().name(),
                don.getTrangThai().getNhan(), don.getNgayTao(), don.getTongTien(), don.getDieuKienGiaoHang(),
                don.getGhiChu(), lines, deliveries, history);
    }

    private int giaTri(Integer value) { return value == null ? -1 : value; }

    private String layTenSanPham(BienTheSanPham bienThe) {
        return sanPhamRepository.findById(bienThe.getMaSanPhamId()).map(s -> s.getTenSanPham())
                .orElse(bienThe.getMaSku());
    }

    private void kiemTraSkuDuocPhanCong(Long nhaCungCapId, BienTheSanPham bienThe) {
        if (!nhaCungCapSkuRepository.existsByNhaCungCapIdAndBienTheId(nhaCungCapId, bienThe.getId())) {
            throw new IllegalArgumentException("SKU " + bienThe.getMaSku() + " chưa được phân công cho nhà cung cấp này");
        }
    }

    private String chuanHoa(String value) { return value == null ? "" : value.trim(); }
}
