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
 * DTO yêu cầu đăng ký tài khoản khách hàng mới với ràng buộc validate chặt chẽ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu đăng ký tài khoản khách hàng mới")
public class RegisterRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 150, message = "Họ và tên phải từ 2 đến 150 ký tự")
    @Pattern(
        regexp = "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s]{2,150}$",
        message = "Họ và tên chỉ được chứa chữ cái tiếng Việt hoặc tiếng Anh, không chứa chữ số hoặc ký tự đặc biệt"
    )
    @Schema(description = "Họ và tên khách hàng", example = "Nguyễn Văn Duy")
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    @Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,10}$",
        message = "Email không đúng định dạng hợp lệ (ví dụ: ten@gmail.com)"
    )
    @Size(max = 150, message = "Email tối đa 150 ký tự")
    @Schema(description = "Địa chỉ email của khách hàng", example = "duy.nguyen@coolmate.me")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
        regexp = "^(0|\\+84)(3[2-9]|5[2689]|7[06-9]|8[1-9]|9[0-9])[0-9]{7}$",
        message = "Số điện thoại phải gồm đúng 10 chữ số của các nhà mạng Việt Nam (đầu 03, 05, 07, 08, 09)"
    )
    @Schema(description = "Số điện thoại liên lạc", example = "0987654321")
    private String soDienThoai;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])\\S{6,50}$",
        message = "Mật khẩu phải từ 6 đến 50 ký tự, không chứa khoảng trắng, bao gồm cả chữ cái và chữ số"
    )
    @Schema(description = "Mật khẩu đăng nhập (từ 6 ký tự, gồm cả chữ và số)", example = "MatKhau123")
    private String matKhau;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    @Schema(description = "Xác nhận lại mật khẩu", example = "MatKhau123")
    private String xacNhanMatKhau;
}
