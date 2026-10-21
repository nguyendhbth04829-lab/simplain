package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dữ liệu yêu cầu đổi mật khẩu tài khoản khách hàng (US12).
 */
@Schema(description = "Dữ liệu yêu cầu đổi mật khẩu")
public record DoiMatKhauRequest(

        @Schema(description = "Mật khẩu hiện tại", example = "MatKhauCu123@")
        @NotBlank(message = "Mật khẩu hiện tại không được để trống")
        String matKhauCu,

        @Schema(description = "Mật khẩu mới (tối thiểu 6 ký tự)", example = "MatKhauMoi456@")
        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Size(min = 6, max = 50, message = "Mật khẩu mới phải từ 6 đến 50 ký tự")
        String matKhauMoi,

        @Schema(description = "Xác nhận mật khẩu mới", example = "MatKhauMoi456@")
        @NotBlank(message = "Xác nhận mật khẩu mới không được để trống")
        String xacNhanMatKhauMoi
) {
}
