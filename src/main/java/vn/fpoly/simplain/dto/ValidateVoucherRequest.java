package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO yêu cầu kiểm tra tính hợp lệ của mã giảm giá khi Checkout / Giỏ hàng (US27).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu Validate kiểm tra mã giảm giá (US27)")
public class ValidateVoucherRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    @Schema(description = "Mã code voucher", example = "COOL20")
    private String maCode;

    @Schema(description = "Tổng giá trị tiền hàng hiện tại của giỏ/đơn", example = "350000")
    private BigDecimal tongTienHang;

    @Schema(description = "ID khách hàng (nếu đã đăng nhập)", example = "1")
    private Long khachHangId;
}
