package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO dữ liệu tổng hợp Bảng điều khiển Dashboard tổng quan cho Admin (US05).
 * Đáp ứng đầy đủ tiêu chí:
 * 1. Thống kê tổng số lượng người dùng (Khách hàng + Nhân viên).
 * 2. Số lượng đơn hàng hôm nay.
 * 3. Doanh thu trong ngày.
 * 4. Chuỗi số liệu biểu đồ doanh thu 7 ngày gần nhất và cơ cấu trạng thái đơn.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Số liệu tổng quan Dashboard Admin bao gồm người dùng, đơn hàng hôm nay, doanh thu trong ngày và biểu đồ thống kê")
public class DashboardOverviewResponse {

    // --- Tiêu chí 1: Thống kê tổng số lượng người dùng ---
    @Schema(description = "Tổng số người dùng toàn hệ thống (Khách hàng + Nhân viên)", example = "1250")
    private Long tongNguoiDung;

    @Schema(description = "Tổng số khách hàng đăng ký", example = "1242")
    private Long tongKhachHang;

    @Schema(description = "Tổng số nhân viên trong hệ thống", example = "8")
    private Long tongNhanVien;

    @Schema(description = "Tỷ lệ tăng trưởng người dùng so với tuần trước (%)", example = "+12.5%")
    private String tyLeTangTruongNguoiDung;

    // --- Tiêu chí 2: Đơn hàng hôm nay ---
    @Schema(description = "Số lượng đơn hàng phát sinh trong ngày hôm nay", example = "28")
    private Long donHangHomNay;

    @Schema(description = "Tổng số đơn hàng tích lũy toàn hệ thống", example = "3540")
    private Long tongDonHang;

    @Schema(description = "Tỷ lệ tăng trưởng đơn hàng so với hôm qua (%)", example = "+8.2%")
    private String tyLeTangTruongDonHang;

    // --- Tiêu chí 3: Doanh thu trong ngày ---
    @Schema(description = "Doanh thu trong ngày hôm nay", example = "14250000")
    private BigDecimal doanhThuHomNay;

    @Schema(description = "Doanh thu hôm nay định dạng tiền tệ VND", example = "14.250.000 ₫")
    private String formattedDoanhThuHomNay;

    @Schema(description = "Tổng doanh thu toàn hệ thống từ trước đến nay", example = "852400000")
    private BigDecimal tongDoanhThu;

    @Schema(description = "Tổng doanh thu tích lũy định dạng tiền tệ VND", example = "852.400.000 ₫")
    private String formattedTongDoanhThu;

    @Schema(description = "Giá trị đơn hàng trung bình (AOV)", example = "508.900 ₫")
    private String formattedGiaTriTrungBinhDon;

    // --- Dữ liệu phục vụ vẽ Biểu đồ tổng quan phong cách Coolmate ---
    @Schema(description = "Số liệu doanh thu 7 ngày gần nhất phục vụ biểu đồ cột")
    private List<DailyRevenueDto> bieuDoDoanhThu7Ngay;

    @Schema(description = "Tỷ lệ phân bổ trạng thái đơn hàng phục vụ biểu đồ tròn / tiến độ")
    private List<OrderStatusStatDto> thongKeTrangThaiDonHang;

    @Schema(description = "Danh sách các đơn hàng mới nhất hôm nay")
    private List<RecentOrderDto> donHangMoiNhat;
}
