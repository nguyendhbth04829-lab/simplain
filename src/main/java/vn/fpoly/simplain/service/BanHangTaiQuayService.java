package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.*;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.exception.KhongDuTonKhoException;
import vn.fpoly.simplain.exception.TrungSoDienThoaiException;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý nghiệp vụ Bán hàng trực tiếp tại quầy (POS) cho SIMPLAIN STORE.
 * Hỗ trợ:
 * 1. Quản lý đa Tab Hóa đơn chờ (Multi-tab POS bills).
 * 2. Tìm kiếm biến thể SKU qua Tên, Mã SKU hoặc Máy quét mã vạch Barcode.
 * 3. Kiểm tra tồn kho thời gian thực & Trừ kho nguyên tử (Atomic decrement).
 * 4. Tìm kiếm / Thêm nhanh thông tin khách hàng tại quầy.
 * 5. Thanh toán trực tiếp tại quầy (Tiền mặt / Chuyển khoản QR), tự động hoàn thành đơn,
 *    ghi log trạng thái, giao dịch thanh toán và tích điểm Loyalty hội viên.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BanHangTaiQuayService {

    private final HoaDonChoRepository hoaDonChoRepository;
    private final ChiTietHoaDonChoRepository chiTietHoaDonChoRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final HinhAnhRepository hinhAnhRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final GiaoDichThanhToanRepository giaoDichThanhToanRepository;
    private final LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;
    private final LoyaltyHoiVienService loyaltyHoiVienService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Lấy danh sách tất cả các Tab Hóa đơn chờ đang mở tại quầy.
     * Nếu chưa có tab nào, tự động tạo mới Hóa đơn chờ số 1 để thu ngân sử dụng ngay.
     */
    @Transactional
    public List<PosHoaDonChoDto> layDanhSachHoaDonCho() {
        List<HoaDonCho> dsHoaDon = hoaDonChoRepository.findByTrangThaiOrderByNgayTaoAsc("CHO_THANH_TOAN");
        if (dsHoaDon.isEmpty()) {
            HoaDonCho hdcMoi = taoHoaDonChoEntity(null);
            dsHoaDon = List.of(hdcMoi);
        }

        List<PosHoaDonChoDto> result = new ArrayList<>();
        for (HoaDonCho hdc : dsHoaDon) {
            result.add(chuyenDoiSangDto(hdc));
        }
        return result;
    }

    /**
     * Tạo thêm 1 Tab Hóa đơn chờ mới tại quầy.
     */
    @Transactional
    public PosHoaDonChoDto taoHoaDonChoMoi(NhanVien nhanVien) {
        long soTabHienTai = hoaDonChoRepository.countByTrangThai("CHO_THANH_TOAN");
        if (soTabHienTai >= 15) {
            throw new IllegalStateException("Đã đạt giới hạn tối đa 15 hóa đơn chờ cùng lúc! Vui lòng thanh toán hoặc hủy bớt hóa đơn chờ.");
        }
        HoaDonCho hdc = taoHoaDonChoEntity(nhanVien);
        return chuyenDoiSangDto(hdc);
    }

    /**
     * Đóng / Hủy một tab hóa đơn chờ.
     */
    @Transactional
    public List<PosHoaDonChoDto> dongHoaDonCho(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID hóa đơn chờ không được để trống!");
        }
        HoaDonCho hdc = hoaDonChoRepository.findById(id).orElse(null);
        if (hdc != null) {
            chiTietHoaDonChoRepository.deleteByMaHoaDonChoId(hdc.getId());
            hoaDonChoRepository.delete(hdc);
            log.info("POS: Đã đóng hóa đơn chờ #{}", hdc.getMaHoaDon());
        }
        return layDanhSachHoaDonCho();
    }

    /**
     * Thêm biến thể sản phẩm vào một tab hóa đơn chờ (hoặc quét mã vạch).
     */
    @Transactional
    public PosHoaDonChoDto themMatHangVaoHoaDonCho(PosThemMatHangRequest req) {
        if (req == null || req.getMaHoaDonChoId() == null || req.getMaBienTheId() == null) {
            throw new IllegalArgumentException("Dữ liệu thêm mặt hàng không hợp lệ!");
        }

        HoaDonCho hdc = hoaDonChoRepository.findById(req.getMaHoaDonChoId())
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn chờ không tồn tại trên hệ thống!"));

        if (!"CHO_THANH_TOAN".equals(hdc.getTrangThai())) {
            throw new IllegalStateException("Hóa đơn này không còn ở trạng thái chờ thanh toán!");
        }

        BienTheSanPham bt = bienTheSanPhamRepository.findById(req.getMaBienTheId())
                .orElseThrow(() -> new IllegalArgumentException("Biến thể sản phẩm không tồn tại!"));

        if (!Boolean.TRUE.equals(bt.getKichHoat())) {
            throw new IllegalArgumentException("Biến thể sản phẩm SKU [" + bt.getMaSku() + "] đang tạm ngưng kinh doanh!");
        }

        int tonKho = (bt.getSoLuong() != null) ? bt.getSoLuong() : 0;
        int soLuongThem = (req.getSoLuong() != null && req.getSoLuong() > 0) ? req.getSoLuong() : 1;

        if (tonKho <= 0) {
            throw new IllegalArgumentException("Sản phẩm SKU [" + bt.getMaSku() + "] đã hết hàng trong kho!");
        }

        // Kiểm tra xem mặt hàng đã có sẵn trong tab hóa đơn chờ này chưa
        Optional<ChiTietHoaDonCho> optExisting = chiTietHoaDonChoRepository
                .findByMaHoaDonChoIdAndMaBienTheId(hdc.getId(), bt.getId());

        if (optExisting.isPresent()) {
            ChiTietHoaDonCho existing = optExisting.get();
            int soLuongMoi = existing.getSoLuong() + soLuongThem;
            if (soLuongMoi > tonKho) {
                throw new IllegalArgumentException("Không thể thêm! Tồn kho hiện có chỉ còn " + tonKho +
                        " sản phẩm (trong giỏ đã có " + existing.getSoLuong() + ").");
            }
            existing.setSoLuong(soLuongMoi);
            existing.setThanhTien(existing.getDonGia().multiply(BigDecimal.valueOf(soLuongMoi)));
            chiTietHoaDonChoRepository.save(existing);
        } else {
            if (soLuongThem > tonKho) {
                throw new IllegalArgumentException("Không thể thêm! Số lượng yêu cầu " + soLuongThem +
                        " vượt quá tồn kho hiện có (" + tonKho + ").");
            }
            BigDecimal donGia = (bt.getGiaBan() != null) ? bt.getGiaBan() : BigDecimal.ZERO;
            ChiTietHoaDonCho chiTietMoi = ChiTietHoaDonCho.builder()
                    .maHoaDonChoId(hdc.getId())
                    .maBienTheId(bt.getId())
                    .soLuong(soLuongThem)
                    .donGia(donGia)
                    .thanhTien(donGia.multiply(BigDecimal.valueOf(soLuongThem)))
                    .build();
            chiTietHoaDonChoRepository.save(chiTietMoi);
        }

        return chuyenDoiSangDto(hoaDonChoRepository.findById(hdc.getId()).orElseThrow());
    }

    /**
     * Cập nhật số lượng mặt hàng trong tab hóa đơn chờ (nút + / - hoặc gõ số lượng).
     */
    @Transactional
    public PosHoaDonChoDto capNhatSoLuong(PosCapNhatSoLuongRequest req) {
        if (req == null || req.getMaChiTietId() == null || req.getSoLuong() == null) {
            throw new IllegalArgumentException("Thông tin cập nhật số lượng không hợp lệ!");
        }

        ChiTietHoaDonCho chiTiet = chiTietHoaDonChoRepository.findById(req.getMaChiTietId())
                .orElseThrow(() -> new IllegalArgumentException("Dòng mặt hàng không tồn tại!"));

        Long hdcId = chiTiet.getMaHoaDonChoId();

        if (req.getSoLuong() <= 0) {
            chiTietHoaDonChoRepository.delete(chiTiet);
        } else {
            BienTheSanPham bt = bienTheSanPhamRepository.findById(chiTiet.getMaBienTheId()).orElseThrow();
            int tonKho = (bt.getSoLuong() != null) ? bt.getSoLuong() : 0;
            if (req.getSoLuong() > tonKho) {
                throw new IllegalArgumentException("Không đủ hàng! Tồn kho SKU [" + bt.getMaSku() + "] chỉ còn " + tonKho + " cái.");
            }
            chiTiet.setSoLuong(req.getSoLuong());
            chiTiet.setThanhTien(chiTiet.getDonGia().multiply(BigDecimal.valueOf(req.getSoLuong())));
            chiTietHoaDonChoRepository.save(chiTiet);
        }

        return chuyenDoiSangDto(hoaDonChoRepository.findById(hdcId).orElseThrow());
    }

    /**
     * Xóa mặt hàng khỏi tab hóa đơn chờ.
     */
    @Transactional
    public PosHoaDonChoDto xoaMatHang(Long chiTietId) {
        if (chiTietId == null) {
            throw new IllegalArgumentException("ID mặt hàng không được để trống!");
        }
        ChiTietHoaDonCho chiTiet = chiTietHoaDonChoRepository.findById(chiTietId)
                .orElseThrow(() -> new IllegalArgumentException("Dòng mặt hàng không tồn tại!"));
        Long hdcId = chiTiet.getMaHoaDonChoId();
        chiTietHoaDonChoRepository.delete(chiTiet);

        return chuyenDoiSangDto(hoaDonChoRepository.findById(hdcId).orElseThrow());
    }

    /**
     * Gán / Cập nhật khách hàng vào Hóa đơn chờ.
     */
    @Transactional
    public PosHoaDonChoDto capNhatKhachHangChoHoaDon(Long hoaDonChoId, Long khachHangId, String ten, String sdt) {
        HoaDonCho hdc = hoaDonChoRepository.findById(hoaDonChoId)
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn chờ không tồn tại!"));

        if (khachHangId != null) {
            KhachHang kh = khachHangRepository.findById(khachHangId).orElse(null);
            if (kh != null) {
                hdc.setKhachHang(kh);
                hdc.setTenKhachHang(kh.getHoTen());
                hdc.setSoDienThoaiKhach(kh.getSoDienThoai());
            }
        } else {
            hdc.setKhachHang(null);
            hdc.setTenKhachHang((ten != null && !ten.isBlank()) ? ten.trim() : "Khách lẻ tại quầy");
            hdc.setSoDienThoaiKhach((sdt != null && !sdt.isBlank()) ? sdt.trim() : "");
        }

        hoaDonChoRepository.save(hdc);
        return chuyenDoiSangDto(hdc);
    }

    /**
     * Tìm kiếm biến thể sản phẩm cho POS:
     * - Hỗ trợ gõ mã SKU, mã vạch Barcode (máy quét), hoặc tên sản phẩm.
     */
    @Transactional(readOnly = true)
    public List<PosItemDto> timKiemBienTheChoPos(String tuKhoa) {
        String kw = (tuKhoa != null) ? tuKhoa.trim() : "";
        List<BienTheSanPham> dsBienThe;

        // 1. Nếu quét mã vạch Barcode hoặc gõ SKU chính xác
        if (!kw.isEmpty()) {
            Optional<BienTheSanPham> optBarcode = bienTheSanPhamRepository.findByMaVachBarcodeIgnoreCase(kw);
            if (optBarcode.isPresent() && Boolean.TRUE.equals(optBarcode.get().getKichHoat())) {
                dsBienThe = List.of(optBarcode.get());
                return mapSangPosItemDtoList(dsBienThe);
            }

            Optional<BienTheSanPham> optSku = bienTheSanPhamRepository.findByMaSkuIgnoreCase(kw);
            if (optSku.isPresent() && Boolean.TRUE.equals(optSku.get().getKichHoat())) {
                dsBienThe = List.of(optSku.get());
                return mapSangPosItemDtoList(dsBienThe);
            }
        }

        // 2. Tìm kiếm mở rộng theo SKU hoặc Barcode
        dsBienThe = bienTheSanPhamRepository.timKiemBienTheChoPos(kw, PageRequest.of(0, 30));

        // 3. Nếu tìm theo tên sản phẩm
        if (dsBienThe.isEmpty() && !kw.isEmpty()) {
            List<SanPham> dsSp = sanPhamRepository.findAll().stream()
                    .filter(sp -> Boolean.TRUE.equals(sp.getKichHoat()) &&
                            sp.getTenSanPham() != null &&
                            sp.getTenSanPham().toLowerCase().contains(kw.toLowerCase()))
                    .limit(10)
                    .toList();

            List<BienTheSanPham> dsBtTheoSp = new ArrayList<>();
            for (SanPham sp : dsSp) {
                dsBtTheoSp.addAll(bienTheSanPhamRepository.findByMaSanPhamIdOrderByIdDesc(sp.getId()).stream()
                        .filter(bt -> Boolean.TRUE.equals(bt.getKichHoat()))
                        .toList());
            }
            dsBienThe = dsBtTheoSp.stream().limit(30).toList();
        }

        return mapSangPosItemDtoList(dsBienThe);
    }

    /**
     * Tìm kiếm khách hàng theo Tên, SĐT hoặc Mã KH.
     * Hỗ trợ tìm kiếm thông minh: Tự động chuẩn hóa số điện thoại loại bỏ ký tự rác (dấu cách, dấu chấm, dấu gạch ngang).
     */
    @Transactional(readOnly = true)
    public List<KhachHang> timKiemKhachHangChoPos(String tuKhoa) {
        String kw = (tuKhoa != null) ? tuKhoa.trim() : "";
        String sdtDigits = kw.replaceAll("[^0-9]", "");
        if (sdtDigits.startsWith("84") && sdtDigits.length() >= 10) {
            sdtDigits = "0" + sdtDigits.substring(2);
        }
        return khachHangRepository.timKiemKhachHangChoPos(kw, sdtDigits, PageRequest.of(0, 15));
    }

    /**
     * Tạo nhanh khách hàng mới ngay tại quầy thu ngân.
     * - Tự động chuẩn hóa số điện thoại (chỉ lưu số, đồng bộ đầu số 0...).
     * - Kiểm tra định dạng số điện thoại chuẩn di động Việt Nam.
     * - Kiểm tra trùng lặp số điện thoại: Không cho phép tạo mới nếu số đã có trong hệ thống,
     *   báo lỗi rõ ràng kèm thông tin khách hàng đang sở hữu để hỗ trợ thu ngân chọn ngay.
     * - Tự động tạo hồ sơ hội viên Loyalty để tích điểm ngay lần mua đầu.
     */
    @Transactional
    public KhachHang taoKhachHangNhanh(PosTaoKhachHangNhanhRequest req) {
        if (req == null || req.getHoTen() == null || req.getHoTen().isBlank()) {
            throw new IllegalArgumentException("Họ và tên khách hàng không được để trống!");
        }
        if (req.getSoDienThoai() == null || req.getSoDienThoai().isBlank()) {
            throw new IllegalArgumentException("Số điện thoại khách hàng không được để trống!");
        }

        String hoTenChuan = req.getHoTen().trim();
        String sdtRaw = req.getSoDienThoai().trim();
        String sdtChuan = sdtRaw.replaceAll("[^0-9]", "");
        if (sdtChuan.startsWith("84") && sdtChuan.length() >= 10) {
            sdtChuan = "0" + sdtChuan.substring(2);
        }
        if (sdtChuan.isBlank()) {
            sdtChuan = sdtRaw;
        }

        // 1. Kiểm tra định dạng số điện thoại di động Việt Nam (10 số, đầu 03, 05, 07, 08, 09)
        if (!sdtChuan.matches("^0[35789][0-9]{8}$")) {
            throw new IllegalArgumentException("Số điện thoại '" + sdtRaw + "' không đúng định dạng (phải gồm 10 chữ số, bắt đầu bằng 03, 05, 07, 08, 09; ví dụ: 0912345678, 0388888888)!");
        }

        // 2. Kiểm tra trùng lặp số điện thoại trong hệ thống
        Optional<KhachHang> existing = khachHangRepository.findBySoDienThoai(sdtChuan);
        if (existing.isEmpty() && !sdtRaw.equals(sdtChuan)) {
            existing = khachHangRepository.findBySoDienThoai(sdtRaw);
        }

        if (existing.isPresent()) {
            throw new TrungSoDienThoaiException(sdtChuan, existing.get());
        }

        // 3. Kiểm tra email nếu có nhập
        String emailChuan = null;
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            String emailTrim = req.getEmail().trim().toLowerCase();
            if (!emailTrim.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new IllegalArgumentException("Địa chỉ email không đúng định dạng (VD: example@gmail.com)!");
            }
            if (khachHangRepository.existsByEmail(emailTrim)) {
                throw new IllegalArgumentException("Địa chỉ email '" + emailTrim + "' đã tồn tại trong hệ thống!");
            }
            emailChuan = emailTrim;
        } else {
            emailChuan = sdtChuan + "@pos.simplain.vn";
        }

        String maKh = "KH" + System.currentTimeMillis();
        KhachHang kh = KhachHang.builder()
                .maKhachHang(maKh)
                .hoTen(hoTenChuan)
                .soDienThoai(sdtChuan)
                .email(emailChuan)
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build();

        KhachHang khMoi = khachHangRepository.save(kh);
        loyaltyHoiVienService.layHoacTaoHoSoHoiVien(khMoi.getId());
        return khMoi;
    }

    /**
     * Thanh toán Hóa đơn bán hàng tại quầy (POS):
     * 1. Trừ tồn kho thật nguyên tử (Atomic decrement).
     * 2. Insert don_hang (loai_don_hang = 'TAI_QUAY', trang_thai = 'HOAN_THANH').
     * 3. Insert chi_tiet_don_hang.
     * 4. Ghi log lịch sử trạng thái (lich_su_trang_thai_don_hang).
     * 5. Ghi log giao dịch thanh toán (giao_dich_thanh_toan).
     * 6. Tự động tích điểm Loyalty nếu khách có tài khoản hội viên.
     * 7. Xóa hoặc kết thúc hóa đơn chờ.
     */
    @Transactional(rollbackFor = Exception.class)
    public PosThanhToanResponse thanhToan(PosThanhToanRequest req, NhanVien nhanVienThuNgan) {
        if (req == null || req.getMaHoaDonChoId() == null) {
            throw new IllegalArgumentException("Thông tin thanh toán không hợp lệ!");
        }

        HoaDonCho hdc = hoaDonChoRepository.findById(req.getMaHoaDonChoId())
                .orElseThrow(() -> new IllegalArgumentException("Hóa đơn chờ không tồn tại!"));

        List<ChiTietHoaDonCho> dsChiTiet = chiTietHoaDonChoRepository.findByMaHoaDonChoIdOrderByIdAsc(hdc.getId());
        if (dsChiTiet.isEmpty()) {
            throw new IllegalArgumentException("Hóa đơn chưa có bất kỳ mặt hàng nào, không thể thanh toán!");
        }

        // 1. Kiểm tra Phương thức thanh toán
        PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(req.getMaPhuongThucId())
                .orElseThrow(() -> new IllegalArgumentException("Phương thức thanh toán được chọn không tồn tại!"));

        // 2. Kiểm tra tồn kho trước khi trừ
        for (ChiTietHoaDonCho item : dsChiTiet) {
            BienTheSanPham bt = bienTheSanPhamRepository.findById(item.getMaBienTheId())
                    .orElseThrow(() -> new IllegalArgumentException("Biến thể ID " + item.getMaBienTheId() + " không tồn tại!"));

            int tonKhoHienCo = (bt.getSoLuong() != null) ? bt.getSoLuong() : 0;
            if (tonKhoHienCo < item.getSoLuong()) {
                throw new KhongDuTonKhoException(bt.getId(), bt.getMaSku(), item.getSoLuong(), tonKhoHienCo);
            }
        }

        // 3. Trừ tồn kho nguyên tử (Atomic Concurrency-safe)
        for (ChiTietHoaDonCho item : dsChiTiet) {
            int rowsUpdated = bienTheSanPhamRepository.truTonKhoAtomic(item.getMaBienTheId(), item.getSoLuong());
            if (rowsUpdated != 1) {
                BienTheSanPham bt = bienTheSanPhamRepository.findById(item.getMaBienTheId()).orElseThrow();
                throw new KhongDuTonKhoException(bt.getId(), bt.getMaSku(), item.getSoLuong(), bt.getSoLuong());
            }
        }

        // 4. Tính toán tiền
        BigDecimal tongTienHang = BigDecimal.ZERO;
        for (ChiTietHoaDonCho item : dsChiTiet) {
            tongTienHang = tongTienHang.add(item.getThanhTien());
        }

        BigDecimal tienGiamGia = (req.getTienGiamGia() != null && req.getTienGiamGia().compareTo(BigDecimal.ZERO) > 0)
                ? req.getTienGiamGia()
                : BigDecimal.ZERO;

        BigDecimal tienCoolcashDung = (req.getTienCoolcashDung() != null && req.getTienCoolcashDung().compareTo(BigDecimal.ZERO) > 0)
                ? req.getTienCoolcashDung()
                : BigDecimal.ZERO;

        // 5. Khách hàng & Tự động lưu khách hàng mới nếu có nhập SĐT
        KhachHang khachHang = null;
        if (req.getMaKhachHangId() != null) {
            khachHang = khachHangRepository.findById(req.getMaKhachHangId()).orElse(null);
        } else if (hdc.getKhachHang() != null) {
            khachHang = hdc.getKhachHang();
        } else if (req.getSoDienThoaiKhach() != null && !req.getSoDienThoaiKhach().isBlank() && !"0000000000".equals(req.getSoDienThoaiKhach().trim())) {
            // Khách có cung cấp SĐT: Tìm xem đã có tài khoản chưa, nếu chưa có thì TỰ ĐỘNG LƯU KHÁCH HÀNG MỚI!
            String sdtRaw = req.getSoDienThoaiKhach().trim();
            String sdtChuan = sdtRaw.replaceAll("[^0-9]", "");
            if (sdtChuan.startsWith("84") && sdtChuan.length() >= 10) {
                sdtChuan = "0" + sdtChuan.substring(2);
            }
            if (sdtChuan.isBlank()) {
                sdtChuan = sdtRaw;
            }
            final String sdtFinal = sdtChuan;
            khachHang = khachHangRepository.findBySoDienThoai(sdtFinal).orElseGet(() -> {
                String tenKh = (req.getTenKhachHang() != null && !req.getTenKhachHang().isBlank()) ? req.getTenKhachHang().trim() : "Khách hàng " + sdtFinal;
                KhachHang moi = KhachHang.builder()
                        .maKhachHang("KH" + System.currentTimeMillis())
                        .hoTen(tenKh)
                        .soDienThoai(sdtFinal)
                        .email(sdtFinal + "@pos.simplain.vn")
                        .trangThai(1)
                        .ngayTao(LocalDateTime.now())
                        .build();
                KhachHang khSaved = khachHangRepository.save(moi);
                loyaltyHoiVienService.layHoacTaoHoSoHoiVien(khSaved.getId());
                log.info("POS: Đã tự động tạo hồ sơ khách hàng mới #{} ({}) tại quầy", khSaved.getId(), khSaved.getHoTen());
                return khSaved;
            });
        }

        // Kiểm tra sử dụng điểm tích lũy Coolcash giảm giá
        if (tienCoolcashDung.compareTo(BigDecimal.ZERO) > 0) {
            if (khachHang == null || khachHang.getId() == null) {
                throw new IllegalArgumentException("Khách vãng lai không có tài khoản tích điểm! Vui lòng chọn hoặc nhập SĐT khách hàng để sử dụng điểm giảm giá.");
            }
            HoSoHoiVien hoSoHienTai = loyaltyHoiVienService.layHoacTaoHoSoHoiVien(khachHang.getId());
            BigDecimal soDuHienCo = (hoSoHienTai.getSoDuCoolcash() != null) ? hoSoHienTai.getSoDuCoolcash() : BigDecimal.ZERO;
            if (tienCoolcashDung.compareTo(soDuHienCo) > 0) {
                throw new IllegalArgumentException("Số điểm giảm giá yêu cầu (" + tienCoolcashDung + " đ) vượt quá số dư điểm hiện có (" + soDuHienCo + " đ) của khách hàng!");
            }
        }

        BigDecimal tongThanhToan = tongTienHang.subtract(tienGiamGia).subtract(tienCoolcashDung);
        if (tongThanhToan.compareTo(BigDecimal.ZERO) < 0) {
            tongThanhToan = BigDecimal.ZERO;
        }

        BigDecimal tienKhachDua = (req.getTienKhachDua() != null) ? req.getTienKhachDua() : BigDecimal.ZERO;

        // Nếu là tiền mặt thì kiểm tra tiền khách đưa
        if ("TIEN_MAT".equalsIgnoreCase(pttt.getMaPhuongThuc())) {
            if (tienKhachDua.compareTo(tongThanhToan) < 0) {
                throw new IllegalArgumentException("Tiền khách đưa (" + tienKhachDua + " đ) chưa đủ để thanh toán tổng tiền (" + tongThanhToan + " đ)!");
            }
        } else {
            // Thanh toán chuyển khoản / QR thì tiền khách đưa mặc định = tổng thanh toán
            if (tienKhachDua.compareTo(BigDecimal.ZERO) <= 0) {
                tienKhachDua = tongThanhToan;
            }
        }

        BigDecimal tienThuaTraKhach = tienKhachDua.subtract(tongThanhToan);
        if (tienThuaTraKhach.compareTo(BigDecimal.ZERO) < 0) {
            tienThuaTraKhach = BigDecimal.ZERO;
        }

        String tenNguoiNhan = (req.getTenKhachHang() != null && !req.getTenKhachHang().isBlank())
                ? req.getTenKhachHang().trim()
                : ((khachHang != null) ? khachHang.getHoTen() : "Khách lẻ tại quầy");

        String sdtNhan = (req.getSoDienThoaiKhach() != null && !req.getSoDienThoaiKhach().isBlank())
                ? req.getSoDienThoaiKhach().trim()
                : ((khachHang != null && khachHang.getSoDienThoai() != null) ? khachHang.getSoDienThoai() : "0000000000");

        // 6. Trạng thái HOAN_THANH (Bán tại quầy giao hàng & thanh toán tức thì)
        TrangThaiDonHang ttHoanThanh = trangThaiDonHangRepository.findByMaTrangThai("HOAN_THANH")
                .orElseGet(() -> trangThaiDonHangRepository.findById(4L)
                        .orElseThrow(() -> new IllegalStateException("Hệ thống chưa thiết lập trạng thái HOAN_THANH!")));

        String maDonHang = "DH-POS-" + System.currentTimeMillis();

        DonHang donHang = DonHang.builder()
                .maDonHang(maDonHang)
                .loaiDonHang("TAI_QUAY")
                .khachHang(khachHang)
                .trangThaiDonHang(ttHoanThanh)
                .phuongThucThanhToan(pttt)
                .nhanVien(nhanVienThuNgan)
                .tongTienHang(tongTienHang)
                .tienGiamGia(tienGiamGia)
                .tienCoolcashDung(tienCoolcashDung)
                .phiVanChuyen(BigDecimal.ZERO)
                .tongThanhToan(tongThanhToan)
                .tienKhachDua(tienKhachDua)
                .tienThuaTraKhach(tienThuaTraKhach)
                .tenNguoiNhan(tenNguoiNhan)
                .soDienThoaiNhan(sdtNhan)
                .diaChiGiaoHang("Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE")
                .ghiChu(req.getGhiChu() != null ? req.getGhiChu().trim() : "Bán hàng tại quầy")
                .ngayTao(LocalDateTime.now())
                .build();

        DonHang donHangSaved = donHangRepository.save(donHang);

        // 7. Insert chi_tiet_don_hang
        List<PosItemDto> dsMatHangResponse = new ArrayList<>();
        Map<Long, SanPham> mapSp = sanPhamRepository.findAll().stream()
                .collect(Collectors.toMap(SanPham::getId, sp -> sp, (a, b) -> a));

        for (ChiTietHoaDonCho item : dsChiTiet) {
            BienTheSanPham bt = bienTheSanPhamRepository.findById(item.getMaBienTheId()).orElseThrow();
            SanPham sp = mapSp.get(bt.getMaSanPhamId());
            String tenSp = (sp != null) ? sp.getTenSanPham() : "Sản phẩm " + bt.getMaSku();

            ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                    .maDonHangId(donHangSaved.getId())
                    .maBienTheId(bt.getId())
                    .tenSanPham(tenSp)
                    .maSku(bt.getMaSku())
                    .donGia(item.getDonGia())
                    .soLuong(item.getSoLuong())
                    .thanhTien(item.getThanhTien())
                    .build();
            chiTietDonHangRepository.save(chiTiet);

            dsMatHangResponse.add(PosItemDto.builder()
                    .id(item.getId())
                    .maBienTheId(bt.getId())
                    .maSku(bt.getMaSku())
                    .maVachBarcode(bt.getMaVachBarcode())
                    .tenSanPham(tenSp)
                    .donGia(item.getDonGia())
                    .soLuong(item.getSoLuong())
                    .thanhTien(item.getThanhTien())
                    .build());
        }

        // 8. Ghi log lịch sử trạng thái đơn hàng (US22)
        try {
            LichSuTrangThaiDonHang logStatus = LichSuTrangThaiDonHang.builder()
                    .donHang(donHangSaved)
                    .trangThaiMoi(ttHoanThanh)
                    .nguoiThucHien(nhanVienThuNgan)
                    .ghiChu("Bán hàng và thanh toán trực tiếp tại quầy thu ngân (" + pttt.getTenPhuongThuc() + ")")
                    .ngayChuyen(donHangSaved.getNgayTao())
                    .build();
            lichSuTrangThaiDonHangRepository.save(logStatus);
        } catch (Exception ex) {
            log.warn("Không thể ghi log lịch sử đơn hàng POS: {}", ex.getMessage());
        }

        // 9. Ghi log giao dịch thanh toán thành công (US20)
        try {
            String maTxn = "TXN-POS-" + System.currentTimeMillis();
            GiaoDichThanhToan txn = GiaoDichThanhToan.builder()
                    .donHang(donHangSaved)
                    .phuongThucThanhToan(pttt)
                    .maGiaoDichNoiBo(maTxn)
                    .maGiaoDichCong("POS-COUNTER")
                    .soTien(donHangSaved.getTongThanhToan())
                    .trangThai("THANH_CONG")
                    .duLieuPhanHoi("Thanh toán thành công tại quầy thu ngân")
                    .ngayTao(donHangSaved.getNgayTao())
                    .build();
            giaoDichThanhToanRepository.save(txn);
        } catch (Exception ex) {
            log.warn("Không thể ghi giao dịch thanh toán POS: {}", ex.getMessage());
        }

        // 10. Trừ điểm đã dùng & Tích lũy điểm Loyalty mới cho hội viên
        BigDecimal soDuCoolcashConLai = BigDecimal.ZERO;
        String tenHangThanhVien = "Khách lẻ";

        if (khachHang != null && khachHang.getId() != null) {
            // Trừ điểm nếu khách có áp dụng giảm giá bằng điểm Coolcash
            if (tienCoolcashDung.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    loyaltyHoiVienService.truDiemThanhToan(khachHang.getId(), donHangSaved.getId(), donHangSaved.getMaDonHang(), tienCoolcashDung);
                    log.info("POS: Đã trừ {} điểm Coolcash của khách hàng #{}", tienCoolcashDung, khachHang.getId());
                } catch (Exception ex) {
                    log.warn("POS: Lỗi trừ điểm Coolcash: {}", ex.getMessage());
                }
            }

            // Tích lũy điểm mới và tự động nâng hạng
            try {
                HoSoHoiVien hoSoSau = loyaltyHoiVienService.xuLyTichDiemVaThangHang(donHangSaved.getId());
                if (hoSoSau != null) {
                    soDuCoolcashConLai = (hoSoSau.getSoDuCoolcash() != null) ? hoSoSau.getSoDuCoolcash() : BigDecimal.ZERO;
                }
                HoiVienResponse hoiVienInfo = loyaltyHoiVienService.layThongTinHoiVien(khachHang.getId());
                if (hoiVienInfo != null) {
                    tenHangThanhVien = hoiVienInfo.getTenHang();
                }
                log.info("POS: Đã tích điểm Loyalty tự động cho khách hàng #{}, số dư mới: {} đ", khachHang.getId(), soDuCoolcashConLai);
            } catch (Exception ex) {
                log.warn("POS: Lỗi tích điểm Loyalty: {}", ex.getMessage());
            }
        }

        // 11. Xóa hóa đơn chờ đã thanh toán xong
        chiTietHoaDonChoRepository.deleteByMaHoaDonChoId(hdc.getId());
        hoaDonChoRepository.delete(hdc);

        log.info("POS: Thanh toán thành công đơn hàng tại quầy #{}, Tổng tiền: {} đ, Tiền khách đưa: {} đ, Tiền thối: {} đ",
                donHangSaved.getMaDonHang(), donHangSaved.getTongThanhToan(), donHangSaved.getTienKhachDua(), donHangSaved.getTienThuaTraKhach());

        return PosThanhToanResponse.builder()
                .success(true)
                .message("Thanh toán đơn hàng tại quầy thành công!")
                .donHangId(donHangSaved.getId())
                .maDonHang(donHangSaved.getMaDonHang())
                .loaiDonHang(donHangSaved.getLoaiDonHang())
                .tenKhachHang(donHangSaved.getTenNguoiNhan())
                .soDienThoaiKhach(donHangSaved.getSoDienThoaiNhan())
                .tenNhanVien(nhanVienThuNgan != null ? nhanVienThuNgan.getHoTen() : "Thu ngân SIMPLAIN")
                .tenPhuongThucThanhToan(pttt.getTenPhuongThuc())
                .tongTienHang(donHangSaved.getTongTienHang())
                .tienGiamGia(donHangSaved.getTienGiamGia())
                .tienCoolcashDung(donHangSaved.getTienCoolcashDung())
                .tongThanhToan(donHangSaved.getTongThanhToan())
                .tienKhachDua(donHangSaved.getTienKhachDua())
                .tienThuaTraKhach(donHangSaved.getTienThuaTraKhach())
                .soDuCoolcashConLai(soDuCoolcashConLai)
                .tenHangThanhVien(tenHangThanhVien)
                .ngayTao(donHangSaved.getNgayTao())
                .formattedNgayTao(donHangSaved.getNgayTao().format(DATE_FORMATTER))
                .danhSachMatHang(dsMatHangResponse)
                .build();
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private HoaDonCho taoHoaDonChoEntity(NhanVien nhanVien) {
        long soThuTu = System.currentTimeMillis() % 100000;
        String maHdc = "HDC-" + String.format("%04d", soThuTu);
        HoaDonCho hdc = HoaDonCho.builder()
                .maHoaDon(maHdc)
                .nhanVien(nhanVien)
                .tenKhachHang("Khách lẻ tại quầy")
                .trangThai("CHO_THANH_TOAN")
                .ngayTao(LocalDateTime.now())
                .build();
        return hoaDonChoRepository.save(hdc);
    }

    private PosHoaDonChoDto chuyenDoiSangDto(HoaDonCho hdc) {
        List<ChiTietHoaDonCho> dsChiTiet = chiTietHoaDonChoRepository.findByMaHoaDonChoIdOrderByIdAsc(hdc.getId());

        List<PosItemDto> dsItems = new ArrayList<>();
        BigDecimal tongTien = BigDecimal.ZERO;
        int tongSoLuong = 0;

        Map<Long, SanPham> mapSp = sanPhamRepository.findAll().stream()
                .collect(Collectors.toMap(SanPham::getId, sp -> sp, (a, b) -> a));
        Map<Long, MauSac> mapMs = mauSacRepository.findAll().stream()
                .collect(Collectors.toMap(MauSac::getId, ms -> ms, (a, b) -> a));
        Map<Long, KichThuoc> mapKt = kichThuocRepository.findAll().stream()
                .collect(Collectors.toMap(KichThuoc::getId, kt -> kt, (a, b) -> a));

        for (ChiTietHoaDonCho it : dsChiTiet) {
            BienTheSanPham bt = bienTheSanPhamRepository.findById(it.getMaBienTheId()).orElse(null);
            if (bt == null) continue;

            SanPham sp = mapSp.get(bt.getMaSanPhamId());
            MauSac ms = mapMs.get(bt.getMaMauSacId());
            KichThuoc kt = mapKt.get(bt.getMaKichThuocId());

            List<HinhAnh> dsAnh = hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(bt.getId());
            String anhUrl = (!dsAnh.isEmpty()) ? dsAnh.get(0).getDuongDanAnh() : null;

            dsItems.add(PosItemDto.builder()
                    .id(it.getId())
                    .maBienTheId(bt.getId())
                    .maSku(bt.getMaSku())
                    .maVachBarcode(bt.getMaVachBarcode())
                    .tenSanPham(sp != null ? sp.getTenSanPham() : "Sản phẩm")
                    .tenMauSac(ms != null ? ms.getTenMau() : "")
                    .maMauHex(ms != null ? ms.getMaMau() : "#000000")
                    .tenKichThuoc(kt != null ? kt.getMaKichThuoc() : "")
                    .donGia(it.getDonGia())
                    .soLuong(it.getSoLuong())
                    .tonKhoHienTai(bt.getSoLuong())
                    .thanhTien(it.getThanhTien())
                    .anhDaiDienUrl(anhUrl)
                    .build());

            tongTien = tongTien.add(it.getThanhTien());
            tongSoLuong += it.getSoLuong();
        }

        return PosHoaDonChoDto.builder()
                .id(hdc.getId())
                .maHoaDon(hdc.getMaHoaDon())
                .maKhachHangId(hdc.getKhachHang() != null ? hdc.getKhachHang().getId() : null)
                .tenKhachHang(hdc.getTenKhachHang())
                .soDienThoaiKhach(hdc.getSoDienThoaiKhach())
                .maNhanVienId(hdc.getNhanVien() != null ? hdc.getNhanVien().getId() : null)
                .tenNhanVien(hdc.getNhanVien() != null ? hdc.getNhanVien().getHoTen() : "Thu ngân")
                .ghiChu(hdc.getGhiChu())
                .trangThai(hdc.getTrangThai())
                .ngayTao(hdc.getNgayTao())
                .tongTienHang(tongTien)
                .tongSoLuong(tongSoLuong)
                .danhSachMatHang(dsItems)
                .build();
    }

    private List<PosItemDto> mapSangPosItemDtoList(List<BienTheSanPham> dsBienThe) {
        if (dsBienThe == null || dsBienThe.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, SanPham> mapSp = sanPhamRepository.findAll().stream()
                .collect(Collectors.toMap(SanPham::getId, sp -> sp, (a, b) -> a));
        Map<Long, MauSac> mapMs = mauSacRepository.findAll().stream()
                .collect(Collectors.toMap(MauSac::getId, ms -> ms, (a, b) -> a));
        Map<Long, KichThuoc> mapKt = kichThuocRepository.findAll().stream()
                .collect(Collectors.toMap(KichThuoc::getId, kt -> kt, (a, b) -> a));

        List<PosItemDto> result = new ArrayList<>();
        for (BienTheSanPham bt : dsBienThe) {
            SanPham sp = mapSp.get(bt.getMaSanPhamId());
            MauSac ms = mapMs.get(bt.getMaMauSacId());
            KichThuoc kt = mapKt.get(bt.getMaKichThuocId());

            List<HinhAnh> dsAnh = hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(bt.getId());
            String anhUrl = (!dsAnh.isEmpty()) ? dsAnh.get(0).getDuongDanAnh() : null;

            result.add(PosItemDto.builder()
                    .maBienTheId(bt.getId())
                    .maSku(bt.getMaSku())
                    .maVachBarcode(bt.getMaVachBarcode())
                    .tenSanPham(sp != null ? sp.getTenSanPham() : "Sản phẩm")
                    .tenMauSac(ms != null ? ms.getTenMau() : "")
                    .maMauHex(ms != null ? ms.getMaMau() : "#000000")
                    .tenKichThuoc(kt != null ? kt.getMaKichThuoc() : "")
                    .donGia(bt.getGiaBan())
                    .tonKhoHienTai(bt.getSoLuong())
                    .anhDaiDienUrl(anhUrl)
                    .build());
        }
        return result;
    }
}
