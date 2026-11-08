package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Đối tượng truyền dữ liệu (DTO) khi Nhân viên đăng nhập vào hệ thống quản trị CMS.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dữ liệu đăng nhập hệ thống CMS dành cho Nhân viên")
public class DangNhapNhanVienRequest {

    @Schema(description = "Tên đăng nhập hệ thống", example = "superadmin")
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String tenDangNhap;

    @Schema(description = "Mật khẩu tài khoản nhân viên", example = "Admin@123")
    @NotBlank(message = "Mật khẩu không được để trống")
    private String matKhau;
}
