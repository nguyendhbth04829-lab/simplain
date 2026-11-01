package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dữ liệu cập nhật hồ sơ cá nhân của nhân viên / quản trị viên.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dữ liệu cập nhật thông tin cá nhân của nhân sự CMS")
public class CapNhatHoSoNhanVienRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 150, message = "Họ và tên từ 2 đến 150 ký tự")
    private String hoTen;

    @Pattern(regexp = "^(?:|(0|\\+84)[0-9\\s.-]{9,13})$", message = "Số điện thoại không đúng định dạng Việt Nam (VD: 0912345678)")
    private String soDienThoai;

    @Pattern(regexp = "^(?:|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$", message = "Email không đúng định dạng chuẩn (VD: ten@domain.com)")
    @Size(max = 150, message = "Email tối đa 150 ký tự")
    private String email;

    @Size(max = 100, message = "Mật khẩu tối đa 100 ký tự")
    private String matKhauMoi;

    private String xacNhanMatKhau;
}
