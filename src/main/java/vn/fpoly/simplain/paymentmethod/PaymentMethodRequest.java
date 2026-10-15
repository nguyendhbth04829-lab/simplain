package vn.fpoly.simplain.paymentmethod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dữ liệu đầu vào khi tạo/sửa phương thức thanh toán.
 * Ví dụ JSON: { "maPhuongThuc": "COD", "tenPhuongThuc": "Thanh toán khi nhận hàng" }
 */
@Schema(description = "Thông tin phương thức thanh toán gửi lên")
public record PaymentMethodRequest(

        @Schema(description = "Mã duy nhất, viết hoa không dấu", example = "COD")
        @NotBlank(message = "Mã phương thức không được để trống")
        @Size(max = 50, message = "Mã phương thức tối đa 50 ký tự")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "Mã phương thức chỉ gồm chữ hoa, số và gạch dưới")
        String maPhuongThuc,

        @Schema(description = "Tên hiển thị cho khách hàng", example = "Thanh toán khi nhận hàng")
        @NotBlank(message = "Tên phương thức không được để trống")
        @Size(max = 100, message = "Tên phương thức tối đa 100 ký tự")
        String tenPhuongThuc,

        @Schema(description = "true = cổng trực tuyến (VNPay/MoMo), false = COD", example = "false")
        Boolean laTrucTuyen,

        @Schema(description = "true = bật, false = tạm tắt", example = "true")
        Boolean kichHoat) {
}
