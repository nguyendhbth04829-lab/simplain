package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO yêu cầu đăng nhập cho khách hàng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu đăng nhập tài khoản")
public class LoginRequest {

    @NotBlank(message = "Vui lòng nhập email hoặc số điện thoại")
    @Schema(description = "Email hoặc số điện thoại đã đăng ký (ví dụ: duy@gmail.com hoặc 0987654321)", example = "duy.nguyen@coolmate.me")
    private String identifier;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Schema(description = "Mật khẩu đăng nhập", example = "MatKhau123")
    private String matKhau;
}
