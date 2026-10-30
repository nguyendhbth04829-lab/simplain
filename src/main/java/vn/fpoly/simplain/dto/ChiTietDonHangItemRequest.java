package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO đại diện cho một mặt hàng chi tiết trong đơn hàng gửi từ Mod 4 sang Mod 5 (US21 - Tiêu chí 1).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHangItemRequest {

    @NotNull(message = "Mã biến thể ID không được để trống")
    private Long maBienTheId;

    @NotNull(message = "Số lượng sản phẩm không được để trống")
    @Min(value = 1, message = "Số lượng đặt hàng tối thiểu phải là 1")
    private Integer soLuong;

    @NotNull(message = "Đơn giá không được để trống")
    @DecimalMin(value = "0.0", message = "Đơn giá không được âm")
    private BigDecimal donGia;

    @NotNull(message = "Thành tiền không được để trống")
    @DecimalMin(value = "0.0", message = "Thành tiền không được âm")
    private BigDecimal thanhTien;

    private String tenSanPham;

    private String maSku;

    private Long maComboId;

    private String nhomComboToken;
}
