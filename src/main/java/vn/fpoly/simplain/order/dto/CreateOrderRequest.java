package vn.fpoly.simplain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO yêu cầu tạo mới đơn hàng từ dữ liệu Checkout (US21).
 */
@Schema(description = "Dữ liệu yêu cầu tạo mới đơn hàng")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @Schema(description = "ID khách hàng đặt đơn (nếu có tài khoản)", example = "1")
    private Long maKhachHangId;

    @Schema(description = "Họ và tên người nhận hàng", example = "Nguyễn Văn Hưng")
    @NotBlank(message = "Tên người nhận không được để trống")
    private String tenNguoiNhan;

    @Schema(description = "Số điện thoại người nhận hàng", example = "0987654321")
    @NotBlank(message = "Số điện thoại nhận không được để trống")
    private String soDienThoaiNhan;

    @Schema(description = "Địa chỉ chi tiết nhận hàng", example = "Số 123 Đường Cầu Giấy, Hà Nội")
    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String diaChiGiaoHang;

    @Schema(description = "Ghi chú giao hàng", example = "Giao giờ hành chính, gọi trước khi đến")
    private String ghiChu;

    @Schema(description = "Tổng tiền hàng trước giảm giá và ship", example = "398000")
    @NotNull(message = "Tổng tiền hàng không được để trống")
    @DecimalMin(value = "0.0", message = "Tổng tiền hàng không được âm")
    private BigDecimal tongTienHang;

    @Schema(description = "Số tiền giảm giá qua voucher (nếu có)", example = "30000")
    @Builder.Default
    private BigDecimal tienGiamGia = BigDecimal.ZERO;

    @Schema(description = "Số tiền thanh toán bằng điểm Coolcash tích lũy (nếu có)", example = "10000")
    @Builder.Default
    private BigDecimal tienCoolcashDung = BigDecimal.ZERO;

    @Schema(description = "Phí vận chuyển", example = "20000")
    @Builder.Default
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;

    @Schema(description = "Tổng số tiền khách phải thanh toán", example = "378000")
    @NotNull(message = "Tổng thanh toán không được để trống")
    @DecimalMin(value = "0.0", message = "Tổng thanh toán không được âm")
    private BigDecimal tongThanhToan;

    @Schema(description = "ID mã giảm giá đã áp dụng (nếu có)", example = "5")
    private Long maGiamGiaId;

    @Schema(description = "Danh sách sản phẩm được mua trong đơn")
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<@Valid CreateOrderItemRequest> items;
}
