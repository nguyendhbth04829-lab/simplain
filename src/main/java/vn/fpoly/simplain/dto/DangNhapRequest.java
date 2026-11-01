package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Đối tượng truyền dữ liệu (DTO) khi khách hàng đăng nhập tài khoản.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dữ liệu đăng nhập tài khoản khách hàng")
public class DangNhapRequest {

    @Schema(description = "Tài khoản đăng nhập (Email hoặc Số điện thoại)", example = "nguyenvanan@gmail.com")
    @NotBlank(message = "Email hoặc Số điện thoại không được để trống")
    private String taiKhoan;

    @Schema(description = "Mật khẩu đăng nhập", example = "MatKhau123@")
    @NotBlank(message = "Mật khẩu không được để trống")
    private String matKhau;
}
