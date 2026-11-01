package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dữ liệu đổi mật khẩu tài khoản khách hàng (US12).
 * Tiêu chí 2: Cho phép Đổi mật khẩu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dữ liệu đổi mật khẩu tài khoản khách hàng (US12)")
public class DoiMatKhauRequest {

    @Schema(description = "Mật khẩu hiện tại của tài khoản", example = "MatKhauCu123@")
    @NotBlank(message = "Vui lòng nhập mật khẩu hiện tại")
    private String matKhauHienTai;

    @Schema(description = "Mật khẩu mới", example = "MatKhauMoi456@")
    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    private String matKhauMoi;

    @Schema(description = "Xác nhận lại mật khẩu mới", example = "MatKhauMoi456@")
    @NotBlank(message = "Vui lòng xác nhận mật khẩu mới")
    private String xacNhanMatKhauMoi;
}
