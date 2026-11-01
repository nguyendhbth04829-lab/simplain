package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO xác nhận mã OTP và đặt lại mật khẩu mới.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Xác nhận mã OTP và đặt lại mật khẩu mới")
public class XacNhanDoiMatKhauRequest {

    @Schema(description = "Địa chỉ email đã nhận mã OTP", example = "customer@gmail.com")
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Địa chỉ email không đúng định dạng")
    private String email;

    @Schema(description = "Mã OTP 6 số nhận từ Gmail", example = "123456")
    @NotBlank(message = "Vui lòng nhập mã OTP xác thực")
    @Pattern(regexp = "^[0-9]{6}$", message = "Mã OTP xác thực phải gồm đúng 6 chữ số")
    private String maOtp;

    @Schema(description = "Mật khẩu mới", example = "NewPass@2026")
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, max = 50, message = "Mật khẩu mới phải từ 6 đến 50 ký tự")
    private String matKhauMoi;

    @Schema(description = "Xác nhận lại mật khẩu mới", example = "NewPass@2026")
    @NotBlank(message = "Vui lòng nhập lại mật khẩu mới")
    private String xacNhanMatKhauMoi;
}
