package vn.fpoly.simplain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO yêu cầu thông tin từng món hàng khi đặt hàng (US21).
 */
@Schema(description = "Thông tin sản phẩm/biến thể trong đơn hàng")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequest {

    @Schema(description = "ID biến thể sản phẩm trong bảng bien_the_san_pham", example = "1")
    @NotNull(message = "ID biến thể không được để trống")
    private Long bienTheId;

    @Schema(description = "Mã SKU của biến thể", example = "AO-POLO-TRANG-L")
    @NotBlank(message = "Mã SKU không được để trống")
    private String maSku;

    @Schema(description = "Tên hiển thị của sản phẩm", example = "Áo Polo Nam Classic")
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String tenSanPham;

    @Schema(description = "Đơn giá của sản phẩm tại thời điểm mua", example = "199000")
    @NotNull(message = "Đơn giá không được để trống")
    @DecimalMin(value = "0.0", message = "Đơn giá không được âm")
    private BigDecimal donGia;

    @Schema(description = "Số lượng mua", example = "2")
    @NotNull(message = "Số lượng mua không được để trống")
    @Min(value = 1, message = "Số lượng mua tối thiểu là 1")
    private Integer soLuong;

    @Schema(description = "Mã combo (nếu sản phẩm thuộc gói combo)", example = "1")
    private Long maComboId;

    @Schema(description = "Token nhóm combo để nhận diện các món cùng combo", example = "COMBO-TOKEN-123")
    private String nhomComboToken;
}
