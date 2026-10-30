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
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Đối tượng truyền dữ liệu (DTO) khi khách hàng đăng ký tài khoản mới.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dữ liệu đăng ký tài khoản khách hàng")
public class DangKyRequest {

    @Schema(description = "Họ và tên khách hàng", example = "Nguyễn Văn An")
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 150, message = "Họ và tên tối đa 150 ký tự")
    private String hoTen;

    @Schema(description = "Địa chỉ email", example = "nguyenvanan@gmail.com")
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng (VD: example@gmail.com)")
    @Size(max = 150, message = "Email tối đa 150 ký tự")
    private String email;

    @Schema(description = "Số điện thoại di động Việt Nam", example = "0987654321")
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng 10 số (VD: 0912345678)")
    private String soDienThoai;

    @Schema(description = "Mật khẩu đăng nhập", example = "MatKhau123@")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String matKhau;

    @Schema(description = "Xác nhận lại mật khẩu", example = "MatKhau123@")
    private String xacNhanMatKhau;

    @Schema(description = "Giới tính (Nam, Nữ, Khác)", example = "Nam")
    private String gioiTinh;

    @Schema(description = "Ngày sinh", example = "2000-01-15")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngaySinh;
}
