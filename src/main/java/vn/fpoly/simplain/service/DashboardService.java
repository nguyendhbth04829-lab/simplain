package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.TaoDonHangNhanhRequest;
import vn.fpoly.simplain.dto.ThongKeDashboardResponse;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.TrangThaiDonHang;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dịch vụ xử lý nghiệp vụ thống kê dữ liệu và biểu đồ tổng quan trên Dashboard (US05).
 *
 * <p>Đáp ứng tiêu chí US05:
 * 1. Thống kê tổng số lượng người dùng (Khách hàng + Nhân sự, phân loại trạng thái & khách hàng mới hôm nay).
 * 2. Thống kê đơn hàng hôm nay (Tổng số đơn, phân loại trạng thái, so sánh tỷ lệ tăng trưởng với hôm qua).
 * 3. Thống kê doanh thu trong ngày (Tổng doanh thu đơn hợp lệ, doanh thu hoàn thành, AOV, so sánh với hôm qua).
 * 4. Tổng hợp dữ liệu cho 3 biểu đồ trực quan (Chart.js) trên giao diện quản trị SIMPLAIN STORE.
 * 5. Validate chặt chẽ bộ lọc ngày thống kê, khoảng ngày biểu đồ và tạo nhanh đơn hàng kiểm thử.
 */
@Service
public class DashboardService {

    private final DonHangRepository donHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final DanhMucRepository danhMucRepository;
    private final BoSuuTapRepository boSuuTapRepository;
    private final VaiTroRepository vaiTroRepository;

    private static final DateTimeFormatter DINH_DANG_NGAY_BIEU_DO = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter DINH_DANG_NGAY_DAY_DU = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    public DashboardService(DonHangRepository donHangRepository,
                            TrangThaiDonHangRepository trangThaiDonHangRepository,
                            KhachHangRepository khachHangRepository,
                            NhanVienRepository nhanVienRepository,
                            DanhMucRepository danhMucRepository,
                            BoSuuTapRepository boSuuTapRepository,
                            VaiTroRepository vaiTroRepository) {
        this.donHangRepository = donHangRepository;
        this.trangThaiDonHangRepository = trangThaiDonHangRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.danhMucRepository = danhMucRepository;
        this.boSuuTapRepository = boSuuTapRepository;
        this.vaiTroRepository = vaiTroRepository;
    }

    /**
     * Lấy toàn bộ dữ liệu thống kê tổng quan và dữ liệu biểu đồ Dashboard theo ngày và khoảng ngày.
     *
     * @param ngayThongKe  ngày cần xem thống kê (mặc định: hôm nay, không được vượt quá ngày hiện tại)
     * @param soNgayBieuDo số ngày hiển thị trên biểu đồ xu hướng (mặc định: 7 ngày, giới hạn 1..90 ngày)
     * @return ThongKeDashboardResponse chứa đầy đủ KPI và mảng dữ liệu Chart.js
     */
    @Transactional(readOnly = true)
    public ThongKeDashboardResponse layThongKeDashboard(LocalDate ngayThongKe, Integer soNgayBieuDo) {
        LocalDate homNay = LocalDate.now();
        LocalDate ngayChon = (ngayThongKe != null) ? ngayThongKe : homNay;

        // Validate 1: Ngày thống kê không được nằm trong tương lai
        if (ngayChon.isAfter(homNay)) {
            throw new IllegalArgumentException(
                    "Ngày thống kê không được lớn hơn ngày hiện tại (" + homNay.format(DINH_DANG_NGAY_DAY_DU) + ")!"
            );
        }

        // Validate 2: Số ngày biểu đồ phải nằm trong khoảng hợp lệ [1..90]
        int soNgay = (soNgayBieuDo != null) ? soNgayBieuDo : 7;
        if (soNgay < 1 || soNgay > 90) {
            throw new IllegalArgumentException("Khoảng thời gian biểu đồ phải nằm trong khoảng từ 1 đến 90 ngày!");
        }

        LocalDateTime batDauNgay = ngayChon.atStartOfDay();
        LocalDateTime ketThucNgay = ngayChon.atTime(LocalTime.MAX);

        LocalDate homQua = ngayChon.minusDays(1);
        LocalDateTime batDauHomQua = homQua.atStartOfDay();
        LocalDateTime ketThucHomQua = homQua.atTime(LocalTime.MAX);

        // =========================================================================
        // 1. THỐNG KÊ TỔNG SỐ LƯỢNG NGƯỜI DÙNG (KHÁCH HÀNG + NHÂN VIÊN)
        // =========================================================================
        long tongKhachHang = khachHangRepository.count();
        long khachHangHoatDong = khachHangRepository.countByTrangThai(1);
        long khachHangBiKhoa = khachHangRepository.countByTrangThai(0);
        long khachHangMoiTrongNgay = khachHangRepository.countByNgayTaoBetween(batDauNgay, ketThucNgay);

        long tongNhanVien = nhanVienRepository.count();
        long nhanVienDangLam = nhanVienRepository.countByTrangThai(1);
        long nhanVienDaNghi = nhanVienRepository.countByTrangThai(0);

        long tongSoLuongNguoiDung = tongKhachHang + tongNhanVien;

        // =========================================================================
        // 2. THỐNG KÊ ĐƠN HÀNG TRONG NGÀY (HÔM NAY / NGÀY ĐƯỢC CHỌN)
        // =========================================================================
        long soDonHangHomNay = donHangRepository.countByNgayTaoBetween(batDauNgay, ketThucNgay);
        long soDonHoanThanhHomNay = donHangRepository.demTheoMaTrangThaiTrongKhoang("HOAN_THANH", batDauNgay, ketThucNgay);
        long soDonHuyHomNay = donHangRepository.demTheoMaTrangThaiTrongKhoang("DA_HUY", batDauNgay, ketThucNgay);
        long soDonDangXuLyHomNay = Math.max(0L, soDonHangHomNay - soDonHoanThanhHomNay - soDonHuyHomNay);

        long soDonHomQua = donHangRepository.countByNgayTaoBetween(batDauHomQua, ketThucHomQua);
        double tyLeTangTruongDonHang = tinhTyLeTangTruong(BigDecimal.valueOf(soDonHangHomNay), BigDecimal.valueOf(soDonHomQua));

        // =========================================================================
        // 3. THỐNG KÊ DOANH THU TRONG NGÀY (VNĐ)
        // =========================================================================
        BigDecimal doanhThuTrongNgay = damBaoKhongNull(donHangRepository.tinhDoanhThuTrongKhoang(batDauNgay, ketThucNgay));
        BigDecimal doanhThuHoanThanhTrongNgay = damBaoKhongNull(donHangRepository.tinhDoanhThuHoanThanhTrongKhoang(batDauNgay, ketThucNgay));
        BigDecimal doanhThuHomQua = damBaoKhongNull(donHangRepository.tinhDoanhThuTrongKhoang(batDauHomQua, ketThucHomQua));

        double tyLeTangTruongDoanhThu = tinhTyLeTangTruong(doanhThuTrongNgay, doanhThuHomQua);

        long soDonHopLeHomNay = Math.max(0L, soDonHangHomNay - soDonHuyHomNay);
        BigDecimal giaTriDonTrungBinhHomNay = (soDonHopLeHomNay > 0)
                ? doanhThuTrongNgay.divide(BigDecimal.valueOf(soDonHopLeHomNay), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // =========================================================================
        // 4. THỐNG KÊ TỔNG QUAN TOÀN HỆ THỐNG
        // =========================================================================
        long tongDonHangToanHeThong = donHangRepository.count();
        BigDecimal tongDoanhThuToanHeThong = damBaoKhongNull(donHangRepository.tinhTongDoanhThuToanHeThong());
        long tongDanhMuc = danhMucRepository.count();
        long tongBoSuuTap = boSuuTapRepository.count();
        long tongVaiTro = vaiTroRepository.count();

        // =========================================================================
        // 5. DỮ LIỆU BIỂU ĐỒ 1: XU HƯỚNG DOANH THU & ĐƠN HÀNG THEO NGÀY
        // =========================================================================
        List<String> nhanNgayBieuDo = new ArrayList<>();
        List<BigDecimal> duLieuDoanhThuTheoNgay = new ArrayList<>();
        List<Long> duLieuSoDonTheoNgay = new ArrayList<>();

        LocalDate ngayBatDauBieuDo = ngayChon.minusDays(soNgay - 1L);
        for (int i = 0; i < soNgay; i++) {
            LocalDate ngayLoop = ngayBatDauBieuDo.plusDays(i);
            LocalDateTime dauNgayLoop = ngayLoop.atStartOfDay();
            LocalDateTime cuoiNgayLoop = ngayLoop.atTime(LocalTime.MAX);

            nhanNgayBieuDo.add(ngayLoop.format(DINH_DANG_NGAY_BIEU_DO));
            BigDecimal doanhThuNgay = damBaoKhongNull(donHangRepository.tinhDoanhThuTrongKhoang(dauNgayLoop, cuoiNgayLoop));
            long soDonNgay = donHangRepository.countByNgayTaoBetween(dauNgayLoop, cuoiNgayLoop);

            duLieuDoanhThuTheoNgay.add(doanhThuNgay);
            duLieuSoDonTheoNgay.add(soDonNgay);
        }

        // =========================================================================
        // 6. DỮ LIỆU BIỂU ĐỒ 2: TỶ TRỌNG TRẠNG THÁI ĐƠN HÀNG TOÀN HỆ THỐNG
        // =========================================================================
        List<String> nhanTrangThaiDonHang = Arrays.asList(
                "Chờ xác nhận",
                "Đang xử lý",
                "Đang giao hàng",
                "Hoàn thành",
                "Đã hủy"
        );
        List<Long> duLieuSoDonTheoTrangThai = Arrays.asList(
                donHangRepository.demTheoMaTrangThai("CHO_XAC_NHAN"),
                donHangRepository.demTheoMaTrangThai("DANG_XU_LY"),
                donHangRepository.demTheoMaTrangThai("DANG_GIAO"),
                donHangRepository.demTheoMaTrangThai("HOAN_THANH"),
                donHangRepository.demTheoMaTrangThai("DA_HUY")
        );

        // =========================================================================
        // 7. DỮ LIỆU BIỂU ĐỒ 3: CƠ CẤU NGƯỜI DÙNG HỆ THỐNG
        // =========================================================================
        List<String> nhanCoCauNguoiDung = Arrays.asList(
                "KH Hoạt động",
                "KH Bị khóa",
                "NV Đang làm",
                "NV Đã nghỉ/Khóa"
        );
        List<Long> duLieuCoCauNguoiDung = Arrays.asList(
                khachHangHoatDong,
                khachHangBiKhoa,
                nhanVienDangLam,
                nhanVienDaNghi
        );

        // =========================================================================
        // 8. DANH SÁCH 6 ĐƠN HÀNG MỚI NHẤT
        // =========================================================================
        List<DonHang> danhSachDonHangMoiNhat = donHangRepository.findTop6ByOrderByNgayTaoDesc();

        return ThongKeDashboardResponse.builder()
                .tongSoLuongNguoiDung(tongSoLuongNguoiDung)
                .tongKhachHang(tongKhachHang)
                .khachHangHoatDong(khachHangHoatDong)
                .khachHangBiKhoa(khachHangBiKhoa)
                .khachHangMoiTrongNgay(khachHangMoiTrongNgay)
                .tongNhanVien(tongNhanVien)
                .nhanVienDangLam(nhanVienDangLam)
                .nhanVienDaNghi(nhanVienDaNghi)
                .soDonHangHomNay(soDonHangHomNay)
                .soDonHoanThanhHomNay(soDonHoanThanhHomNay)
                .soDonDangXuLyHomNay(soDonDangXuLyHomNay)
                .soDonHuyHomNay(soDonHuyHomNay)
                .soDonHomQua(soDonHomQua)
                .tyLeTangTruongDonHang(tyLeTangTruongDonHang)
                .doanhThuTrongNgay(doanhThuTrongNgay)
                .doanhThuHoanThanhTrongNgay(doanhThuHoanThanhTrongNgay)
                .doanhThuHomQua(doanhThuHomQua)
                .tyLeTangTruongDoanhThu(tyLeTangTruongDoanhThu)
                .giaTriDonTrungBinhHomNay(giaTriDonTrungBinhHomNay)
                .tongDonHangToanHeThong(tongDonHangToanHeThong)
                .tongDoanhThuToanHeThong(tongDoanhThuToanHeThong)
                .tongDanhMuc(tongDanhMuc)
                .tongBoSuuTap(tongBoSuuTap)
                .tongVaiTro(tongVaiTro)
                .nhanNgayBieuDo(nhanNgayBieuDo)
                .duLieuDoanhThuTheoNgay(duLieuDoanhThuTheoNgay)
                .duLieuSoDonTheoNgay(duLieuSoDonTheoNgay)
                .nhanTrangThaiDonHang(nhanTrangThaiDonHang)
                .duLieuSoDonTheoTrangThai(duLieuSoDonTheoTrangThai)
                .nhanCoCauNguoiDung(nhanCoCauNguoiDung)
                .duLieuCoCauNguoiDung(duLieuCoCauNguoiDung)
                .danhSachDonHangMoiNhat(danhSachDonHangMoiNhat)
                .ngayThongKe(ngayChon)
                .soNgayBieuDo(soNgay)
                .build();
    }

    /**
     * Tạo nhanh đơn hàng trên Dashboard để Admin kiểm thử trực tiếp sự thay đổi của
     * số lượng đơn hàng hôm nay, doanh thu trong ngày và biểu đồ (US05).
     */
    @Transactional
    public DonHang taoDonHangNhanh(TaoDonHangNhanhRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dữ liệu đơn hàng không được để trống!");
        }

        // 1. Chuẩn hóa & Validate Mã đơn hàng
        String maDonHang;
        if (request.getMaDonHang() != null && !request.getMaDonHang().isBlank()) {
            maDonHang = request.getMaDonHang().trim().toUpperCase();
            if (!maDonHang.matches("^[A-Z0-9_-]{3,50}$")) {
                throw new IllegalArgumentException("Mã đơn hàng chỉ được chứa chữ cái, chữ số, dấu gạch ngang (-) hoặc gạch dưới (_), từ 3 đến 50 ký tự!");
            }
            if (donHangRepository.findByMaDonHang(maDonHang).isPresent()) {
                throw new IllegalArgumentException("Mã đơn hàng [" + maDonHang + "] đã tồn tại trên hệ thống!");
            }
        } else {
            maDonHang = "CM-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        }

        // 2. Validate Tiền hàng, Giảm giá và Phí vận chuyển
        BigDecimal tongTienHang = request.getTongTienHang();
        if (tongTienHang == null || tongTienHang.compareTo(BigDecimal.valueOf(1000)) < 0) {
            throw new IllegalArgumentException("Tổng tiền hàng phải từ 1.000đ trở lên!");
        }

        BigDecimal tienGiamGia = (request.getTienGiamGia() != null) ? request.getTienGiamGia() : BigDecimal.ZERO;
        if (tienGiamGia.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tiền giảm giá không được âm!");
        }
        if (tienGiamGia.compareTo(tongTienHang) > 0) {
            throw new IllegalArgumentException("Tiền giảm giá không được lớn hơn tổng tiền hàng!");
        }

        BigDecimal phiVanChuyen = (request.getPhiVanChuyen() != null) ? request.getPhiVanChuyen() : BigDecimal.ZERO;
        if (phiVanChuyen.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Phí vận chuyển không được âm!");
        }

        // 3. Validate Trạng thái đơn hàng
        if (request.getMaTrangThai() == null || request.getMaTrangThai().isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn trạng thái đơn hàng!");
        }
        String maTrangThaiChuan = request.getMaTrangThai().trim().toUpperCase();
        TrangThaiDonHang trangThai = trangThaiDonHangRepository.findByMaTrangThai(maTrangThaiChuan)
                .orElseThrow(() -> new IllegalArgumentException("Trạng thái đơn hàng [" + maTrangThaiChuan + "] không tồn tại trên hệ thống!"));

        // 4. Tính Tổng thanh toán
        BigDecimal tongThanhToan = tongTienHang.subtract(tienGiamGia).add(phiVanChuyen);

        // 5. Gán khách hàng mặc định nếu có
        KhachHang khachHang = khachHangRepository.findAll().stream().findFirst().orElse(null);

        DonHang donHangMoi = DonHang.builder()
                .maDonHang(maDonHang)
                .khachHang(khachHang)
                .trangThaiDonHang(trangThai)
                .maVanDon("VD-" + maDonHang)
                .tongTienHang(tongTienHang)
                .tienGiamGia(tienGiamGia)
                .tienCoolcashDung(BigDecimal.ZERO)
                .phiVanChuyen(phiVanChuyen)
                .tongThanhToan(tongThanhToan)
                .tenNguoiNhan(request.getTenNguoiNhan().trim())
                .soDienThoaiNhan(request.getSoDienThoaiNhan().trim())
                .diaChiGiaoHang(request.getDiaChiGiaoHang().trim())
                .ghiChu(request.getGhiChu() != null && !request.getGhiChu().isBlank()
                        ? request.getGhiChu().trim()
                        : "Đơn hàng tạo nhanh từ Dashboard SIMPLAIN STORE")
                .ngayTao(LocalDateTime.now())
                .build();

        return donHangRepository.save(donHangMoi);
    }

    private BigDecimal damBaoKhongNull(BigDecimal giaTri) {
        return giaTri != null ? giaTri : BigDecimal.ZERO;
    }

    private double tinhTyLeTangTruong(BigDecimal hienTai, BigDecimal truocDo) {
        if (truocDo == null || truocDo.compareTo(BigDecimal.ZERO) == 0) {
            return (hienTai != null && hienTai.compareTo(BigDecimal.ZERO) > 0) ? 100.0 : 0.0;
        }
        return hienTai.subtract(truocDo)
                .multiply(BigDecimal.valueOf(100))
                .divide(truocDo, 1, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
