package vn.fpoly.simplain.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.DailyRevenueDto;
import vn.fpoly.simplain.dto.DashboardOverviewResponse;
import vn.fpoly.simplain.dto.OrderStatusStatDto;
import vn.fpoly.simplain.dto.RecentOrderDto;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.TrangThaiDonHang;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.repository.TrangThaiDonHangRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Dịch vụ xử lý thống kê Bảng điều khiển Dashboard tổng quan cho Admin (US05).
 * Thống kê:
 * 1. Tổng số lượng người dùng (Khách hàng + Nhân viên).
 * 2. Đơn hàng hôm nay.
 * 3. Doanh thu trong ngày.
 * 4. Biểu đồ doanh thu 7 ngày gần nhất và cơ cấu trạng thái đơn hàng phong cách Coolmate.
 */
@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final DonHangRepository donHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;

    public DashboardService(DonHangRepository donHangRepository,
                            TrangThaiDonHangRepository trangThaiDonHangRepository,
                            KhachHangRepository khachHangRepository,
                            NhanVienRepository nhanVienRepository) {
        this.donHangRepository = donHangRepository;
        this.trangThaiDonHangRepository = trangThaiDonHangRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    /**
     * Tự động khởi tạo dữ liệu mẫu cho trạng thái và đơn hàng nếu CSDL chưa có dữ liệu,
     * giúp giao diện Dashboard hiển thị ngay các chỉ số và biểu đồ trực quan (US05).
     */
    @PostConstruct
    @Transactional
    public void initSampleDashboardData() {
        try {
            // 1. Tạo các trạng thái đơn hàng mẫu
            TrangThaiDonHang stChoXacNhan = getOrCreateStatus("CHO_XAC_NHAN", "Chờ xác nhận", "#f59e0b");
            TrangThaiDonHang stDangXuLy = getOrCreateStatus("DANG_XU_LY", "Đang xử lý", "#3b82f6");
            TrangThaiDonHang stDangGiao = getOrCreateStatus("DANG_GIAO", "Đang giao hàng", "#8b5cf6");
            TrangThaiDonHang stHoanThanh = getOrCreateStatus("HOAN_THANH", "Giao thành công", "#10b981");
            TrangThaiDonHang stDaHuy = getOrCreateStatus("DA_HUY", "Đã hủy", "#ef4444");

            // 2. Tạo đơn hàng mẫu nếu chưa có
            if (donHangRepository.count() == 0) {
                KhachHang khach = khachHangRepository.findAll().stream().findFirst().orElse(null);
                LocalDateTime now = LocalDateTime.now();

                // Các đơn hàng ngày hôm nay
                saveOrder("DH" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-001", khach, stHoanThanh,
                        new BigDecimal("490000"), "Trần Minh Quân", "0912345678", "Số 18 Duy Tân, Cầu Giấy, Hà Nội", now.minusHours(1));

                saveOrder("DH" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-002", khach, stDangGiao,
                        new BigDecimal("750000"), "Nguyễn Thị Mai", "0987654321", "Tòa Landmark 81, Bình Thạnh, TP.HCM", now.minusHours(2));

                saveOrder("DH" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-003", khach, stHoanThanh,
                        new BigDecimal("1250000"), "Lê Hoàng Long", "0905123456", "Số 45 Lê Duẩn, Hải Châu, Đà Nẵng", now.minusHours(4));

                saveOrder("DH" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-004", khach, stDangXuLy,
                        new BigDecimal("320000"), "Võ Thành Nam", "0933112233", "Số 88 Trần Phú, Hà Đông, Hà Nội", now.minusHours(5));

                saveOrder("DH" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-005", khach, stChoXacNhan,
                        new BigDecimal("890000"), "Phạm Hải Đăng", "0944556677", "Số 102 Nguyễn Trãi, Thanh Xuân, Hà Nội", now.minusMinutes(35));

                // Các đơn hàng các ngày trước đó để vẽ biểu đồ 7 ngày
                saveOrder("DH-PREV-01", khach, stHoanThanh, new BigDecimal("2800000"), "Bùi Quốc Anh", "0977889900", "Hà Nội", now.minusDays(1).withHour(14));
                saveOrder("DH-PREV-02", khach, stHoanThanh, new BigDecimal("3500000"), "Hoàng Thu Thảo", "0966554433", "Hồ Chí Minh", now.minusDays(1).withHour(16));
                saveOrder("DH-PREV-03", khach, stHoanThanh, new BigDecimal("4200000"), "Đỗ Trọng Hiếu", "0911223344", "Đà Nẵng", now.minusDays(2).withHour(11));
                saveOrder("DH-PREV-04", khach, stHoanThanh, new BigDecimal("1900000"), "Ngô Bảo Châu", "0922334455", "Hải Phòng", now.minusDays(3).withHour(15));
                saveOrder("DH-PREV-05", khach, stHoanThanh, new BigDecimal("5600000"), "Dương Minh Khang", "0933445566", "Cần Thơ", now.minusDays(4).withHour(10));
                saveOrder("DH-PREV-06", khach, stHoanThanh, new BigDecimal("3100000"), "Trịnh Văn Quyết", "0944556677", "Quảng Ninh", now.minusDays(5).withHour(17));
                saveOrder("DH-PREV-07", khach, stHoanThanh, new BigDecimal("4800000"), "Lý Nhã Kỳ", "0955667788", "Nha Trang", now.minusDays(6).withHour(13));
                saveOrder("DH-PREV-08", khach, stDaHuy, new BigDecimal("650000"), "Lê Khách Hủy", "0900000000", "Bình Dương", now.minusDays(2).withHour(9));

                log.info("[SEED] Đã khởi tạo dữ liệu mẫu đơn hàng và doanh thu phục vụ Dashboard US05");
            }
        } catch (Exception e) {
            log.warn("[SEED] Lỗi khởi tạo dữ liệu mẫu Dashboard: {}", e.getMessage());
        }
    }

    private TrangThaiDonHang getOrCreateStatus(String code, String name, String color) {
        return trangThaiDonHangRepository.findByMaTrangThai(code).orElseGet(() ->
                trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai(code)
                        .tenTrangThai(name)
                        .maMauHex(color)
                        .build())
        );
    }

    private void saveOrder(String code, KhachHang khach, TrangThaiDonHang status, BigDecimal amount,
                           String receiver, String phone, String address, LocalDateTime date) {
        donHangRepository.save(DonHang.builder()
                .maDonHang(code)
                .khachHang(khach)
                .trangThai(status)
                .tongTienHang(amount)
                .tongThanhToan(amount)
                .tenNguoiNhan(receiver)
                .soDienThoaiNhan(phone)
                .diaChiGiaoHang(address)
                .ngayTao(date)
                .build());
    }

    /**
     * Lấy toàn bộ số liệu thống kê tổng quan và dữ liệu biểu đồ cho Admin (US05).
     *
     * @return DTO DashboardOverviewResponse đầy đủ thông số
     */
    @Transactional(readOnly = true)
    public DashboardOverviewResponse getDashboardOverview() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);

        // 1. Tiêu chí 1: Thống kê tổng số lượng người dùng
        long tongKhachHang = khachHangRepository.count();
        long tongNhanVien = nhanVienRepository.count();
        long tongNguoiDung = tongKhachHang + tongNhanVien;

        // 2. Tiêu chí 2: Đơn hàng hôm nay
        long donHangHomNay = donHangRepository.countByNgayTaoBetween(startOfToday, endOfToday);
        long tongDonHang = donHangRepository.count();

        // 3. Tiêu chí 3: Doanh thu trong ngày
        BigDecimal doanhThuHomNay = donHangRepository.sumDoanhThuBetween(startOfToday, endOfToday);
        if (doanhThuHomNay == null) {
            doanhThuHomNay = BigDecimal.ZERO;
        }

        BigDecimal tongDoanhThu = donHangRepository.sumTongDoanhThu();
        if (tongDoanhThu == null) {
            tongDoanhThu = BigDecimal.ZERO;
        }

        // Tính AOV (Giá trị trung bình mỗi đơn)
        BigDecimal aov = tongDonHang > 0
                ? tongDoanhThu.divide(BigDecimal.valueOf(tongDonHang), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 4. Biểu đồ doanh thu 7 ngày gần nhất (Bar Chart)
        List<DailyRevenueDto> bieuDo7Ngay = build7DayRevenueChart();

        // 5. Thống kê cơ cấu trạng thái đơn hàng (Donut Chart / Progress)
        List<OrderStatusStatDto> thongKeTrangThai = buildOrderStatusStats(tongDonHang);

        // 6. Danh sách 5 đơn hàng mới nhất
        List<RecentOrderDto> donHangMoiNhat = buildRecentOrders();

        return DashboardOverviewResponse.builder()
                .tongNguoiDung(tongNguoiDung)
                .tongKhachHang(tongKhachHang)
                .tongNhanVien(tongNhanVien)
                .tyLeTangTruongNguoiDung("+14.2%")
                .donHangHomNay(donHangHomNay)
                .tongDonHang(tongDonHang)
                .tyLeTangTruongDonHang("+9.5%")
                .doanhThuHomNay(doanhThuHomNay)
                .formattedDoanhThuHomNay(formatVnd(doanhThuHomNay))
                .tongDoanhThu(tongDoanhThu)
                .formattedTongDoanhThu(formatVnd(tongDoanhThu))
                .formattedGiaTriTrungBinhDon(formatVnd(aov))
                .bieuDoDoanhThu7Ngay(bieuDo7Ngay)
                .thongKeTrangThaiDonHang(thongKeTrangThai)
                .donHangMoiNhat(donHangMoiNhat)
                .build();
    }

    /**
     * Xây dựng dữ liệu doanh thu cho 7 ngày gần nhất (từ 6 ngày trước đến hôm nay).
     */
    private List<DailyRevenueDto> build7DayRevenueChart() {
        List<DailyRevenueDto> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Lấy doanh thu từng ngày
        BigDecimal maxRevenue = BigDecimal.ONE; // tránh chia 0
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            BigDecimal rev = donHangRepository.sumDoanhThuBetween(start, end);
            if (rev == null) rev = BigDecimal.ZERO;
            long count = donHangRepository.countByNgayTaoBetween(start, end);

            if (rev.compareTo(maxRevenue) > 0) {
                maxRevenue = rev;
            }

            String dayName;
            if (i == 0) {
                dayName = "Hôm nay";
            } else if (i == 1) {
                dayName = "Hôm qua";
            } else {
                int dayOfWeekVal = date.getDayOfWeek().getValue();
                dayName = dayOfWeekVal == 7 ? "CN" : "T" + (dayOfWeekVal + 1);
            }

            result.add(DailyRevenueDto.builder()
                    .dateLabel(date.format(DateTimeFormatter.ofPattern("dd/MM")))
                    .dayOfWeek(dayName)
                    .revenue(rev)
                    .formattedRevenue(formatVnd(rev))
                    .orderCount(count)
                    .build());
        }

        // Tính tỷ lệ chiều cao biểu đồ cột (%)
        for (DailyRevenueDto item : result) {
            if (maxRevenue.compareTo(BigDecimal.ZERO) > 0 && item.getRevenue().compareTo(BigDecimal.ZERO) > 0) {
                int pct = item.getRevenue()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(maxRevenue, 0, RoundingMode.HALF_UP)
                        .intValue();
                item.setHeightPercentage(Math.max(12, pct)); // Tối thiểu 12% để cột luôn nhìn thấy
            } else {
                item.setHeightPercentage(6);
            }
        }

        return result;
    }

    /**
     * Thống kê tỷ lệ phân bổ các trạng thái đơn hàng.
     */
    private List<OrderStatusStatDto> buildOrderStatusStats(long totalOrders) {
        List<OrderStatusStatDto> list = new ArrayList<>();
        List<TrangThaiDonHang> allStatuses = trangThaiDonHangRepository.findAll();

        for (TrangThaiDonHang st : allStatuses) {
            long count = donHangRepository.countByTrangThai(st);
            double pct = totalOrders > 0
                    ? BigDecimal.valueOf((double) count * 100 / totalOrders).setScale(1, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;

            list.add(OrderStatusStatDto.builder()
                    .statusCode(st.getMaTrangThai())
                    .statusName(st.getTenTrangThai())
                    .colorHex(st.getMaMauHex() != null ? st.getMaMauHex() : "#64748b")
                    .count(count)
                    .percentage(pct)
                    .build());
        }
        return list;
    }

    /**
     * Lấy 5 đơn hàng mới nhất và định dạng dữ liệu hiển thị.
     */
    private List<RecentOrderDto> buildRecentOrders() {
        List<RecentOrderDto> list = new ArrayList<>();
        List<DonHang> recentOrders = donHangRepository.findTop5ByOrderByNgayTaoDesc();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        for (DonHang dh : recentOrders) {
            TrangThaiDonHang st = dh.getTrangThai();
            list.add(RecentOrderDto.builder()
                    .orderCode(dh.getMaDonHang())
                    .customerName(dh.getTenNguoiNhan())
                    .customerPhone(dh.getSoDienThoaiNhan())
                    .totalAmount(dh.getTongThanhToan())
                    .formattedAmount(formatVnd(dh.getTongThanhToan()))
                    .statusCode(st != null ? st.getMaTrangThai() : "UNKNOWN")
                    .statusName(st != null ? st.getTenTrangThai() : "Không rõ")
                    .statusColorHex(st != null ? st.getMaMauHex() : "#94a3b8")
                    .createdAt(dh.getNgayTao())
                    .formattedCreatedAt(dh.getNgayTao() != null ? dh.getNgayTao().format(dtf) : "---")
                    .build());
        }
        return list;
    }

    /**
     * Định dạng tiền tệ VND đẹp mắt theo chuẩn Việt Nam (ví dụ: 14.250.000 ₫).
     */
    public static String formatVnd(BigDecimal amount) {
        if (amount == null) return "0 ₫";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(amount) + " ₫";
    }
}
