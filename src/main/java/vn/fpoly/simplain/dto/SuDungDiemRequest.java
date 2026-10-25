/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - DTO yêu cầu sử dụng điểm Coolcash thanh toán đơn hàng
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Yêu cầu trừ điểm Coolcash để giảm trừ tiền thanh toán.
 */
@Schema(description = "Dữ liệu yêu cầu sử dụng điểm thưởng Coolcash")
public record SuDungDiemRequest(

        @Schema(description = "Số điểm muốn sử dụng (tối thiểu 1000 điểm)", example = "50000")
        @NotNull(message = "Số điểm sử dụng không được để trống")
        @DecimalMin(value = "1000", message = "Số điểm tối thiểu để quy đổi là 1,000 điểm")
        BigDecimal soDiem,

        @Schema(description = "ID đơn hàng áp dụng", example = "100")
        Long maDonHangId
) {
}
