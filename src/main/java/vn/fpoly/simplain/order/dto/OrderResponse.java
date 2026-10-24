package vn.fpoly.simplain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO phản hồi kết quả tạo hoặc xem thông tin đơn hàng (US21, US23).
 */
@Schema(description = "Thông tin kết quả đơn hàng")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    @Schema(description = "ID đơn hàng trong cơ sở dữ liệu", example = "1")
    private Long id;

    @Schema(description = "Mã đơn hàng duy nhất", example = "DH-20261024-A1B2C3")
    private String maDonHang;

    @Schema(description = "Mã trạng thái đơn hàng", example = "CHO_XAC_NHAN")
    private String maTrangThai;

    @Schema(description = "Tên hiển thị của trạng thái", example = "Chờ xác nhận")
    private String tenTrangThai;

    @Schema(description = "Tên người nhận", example = "Nguyễn Văn Hưng")
    private String tenNguoiNhan;

    @Schema(description = "Số điện thoại nhận", example = "0987654321")
    private String soDienThoaiNhan;

    @Schema(description = "Địa chỉ giao hàng", example = "Số 123 Đường Cầu Giấy, Hà Nội")
    private String diaChiGiaoHang;

    @Schema(description = "Mã vận đơn (nếu đã có đơn vị vận chuyển tiếp nhận)", example = "VTP123456789")
    private String maVanDon;

    @Schema(description = "Tổng tiền hàng", example = "398000")
    private BigDecimal tongTienHang;

    @Schema(description = "Tiền giảm giá", example = "30000")
    private BigDecimal tienGiamGia;

    @Schema(description = "Tiền Coolcash đã dùng", example = "10000")
    private BigDecimal tienCoolcashDung;

    @Schema(description = "Phí vận chuyển", example = "20000")
    private BigDecimal phiVanChuyen;

    @Schema(description = "Tổng thanh toán cuối cùng", example = "378000")
    private BigDecimal tongThanhToan;

    @Schema(description = "Ghi chú giao hàng", example = "Giao giờ hành chính")
    private String ghiChu;

    @Schema(description = "Thời gian tạo đơn hàng", example = "2026-10-24T21:30:00")
    private LocalDateTime ngayTao;

    @Schema(description = "Thông điệp phản hồi từ hệ thống", example = "Tạo đơn hàng thành công và đã trừ tồn kho")
    private String thongBao;

    @Schema(description = "Danh sách chi tiết các món hàng trong đơn")
    private List<OrderItemResponse> items;
}
