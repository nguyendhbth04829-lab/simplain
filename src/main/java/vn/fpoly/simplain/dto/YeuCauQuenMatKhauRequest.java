package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO yêu cầu gửi mã OTP quên mật khẩu qua Gmail.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu gửi mã OTP xác thực lấy lại mật khẩu qua Gmail")
public class YeuCauQuenMatKhauRequest {

    @Schema(description = "Địa chỉ Gmail / Email đã đăng ký trong hệ thống", example = "customer@gmail.com")
    @NotBlank(message = "Vui lòng nhập địa chỉ Gmail / Email của bạn")
    @Email(message = "Địa chỉ email không đúng định dạng chuẩn (VD: tenban@gmail.com)")
    private String email;
}
