package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Dữ liệu yêu cầu cập nhật thông tin cá nhân khách hàng (US12).
 */
@Schema(description = "Dữ liệu yêu cầu cập nhật hồ sơ khách hàng")
public record CapNhatHoSoRequest(

        @Schema(description = "Họ và tên khách hàng", example = "Nguyễn Văn A")
        @NotBlank(message = "Họ và tên không được để trống")
        @Size(max = 150, message = "Họ và tên tối đa 150 ký tự")
        String hoTen,

        @Schema(description = "Số điện thoại di động (10 chữ số)", example = "0987654321")
        @Pattern(regexp = "^(0|\\+84)[0-9]{9}$|^$", message = "Số điện thoại không đúng định dạng Việt Nam (ví dụ 0987654321)")
        String soDienThoai,

        @Schema(description = "Địa chỉ email", example = "nguyenvana@gmail.com")
        @Email(message = "Email không đúng định dạng")
        @Size(max = 150, message = "Email tối đa 150 ký tự")
        String email,

        @Schema(description = "Giới tính (Nam, Nữ, Khác)", example = "Nam")
        @Size(max = 10, message = "Giới tính tối đa 10 ký tự")
        String gioiTinh,

        @Schema(description = "Ngày sinh", example = "1998-05-20")
        @Past(message = "Ngày sinh phải là ngày trong quá khứ")
        LocalDate ngaySinh,

        @Schema(description = "Đường dẫn ảnh đại diện", example = "https://example.com/avatar.jpg")
        @Size(max = 500, message = "Đường dẫn ảnh đại diện tối đa 500 ký tự")
        String anhDaiDien
) {
}
