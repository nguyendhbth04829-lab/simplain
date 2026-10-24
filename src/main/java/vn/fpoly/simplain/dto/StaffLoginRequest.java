package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO yêu cầu đăng nhập vào hệ thống CMS quản trị cho Nhân viên (US02).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu đăng nhập vào hệ thống CMS quản trị")
public class StaffLoginRequest {

    @NotBlank(message = "Vui lòng nhập tên đăng nhập, email hoặc số điện thoại")
    @Schema(description = "Tên đăng nhập, email hoặc số điện thoại nhân viên", example = "superadmin")
    private String tenDangNhap;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Schema(description = "Mật khẩu đăng nhập CMS", example = "Admin@123")
    private String matKhau;
}
