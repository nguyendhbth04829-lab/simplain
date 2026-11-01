package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Đối tượng truyền dữ liệu (DTO) khi thêm mới hoặc chỉnh sửa Nhân viên (US04).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dữ liệu thêm mới hoặc cập nhật Nhân viên")
public class NhanVienRequest {

    @Schema(description = "ID nhân viên (khi cập nhật)", example = "1")
    private Long id;

    @Schema(description = "Mã định danh nhân viên", example = "NV003")
    @NotBlank(message = "Mã nhân viên không được để trống")
    @Size(min = 3, max = 50, message = "Mã nhân viên phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_-]{3,50}$", message = "Mã nhân viên chỉ gồm chữ cái, chữ số, gạch ngang và gạch dưới (VD: NV003, NV-TN-01, NV_KHO_01)")
    private String maNhanVien;

    @Schema(description = "Họ và tên nhân viên", example = "Nguyễn Văn B")
    @NotBlank(message = "Họ và tên nhân viên không được để trống")
    @Size(max = 150, message = "Họ và tên tối đa 150 ký tự")
    private String hoTen;

    @Schema(description = "Tên đăng nhập hệ thống CMS", example = "nhanvien01")
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 100, message = "Tên đăng nhập phải từ 3 đến 100 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_.]+$", message = "Tên đăng nhập chỉ gồm chữ cái không dấu, chữ số, dấu chấm hoặc gạch dưới")
    private String tenDangNhap;

    @Schema(description = "Mật khẩu (Bắt buộc khi tạo mới, để trống nếu không đổi khi cập nhật)", example = "NhanVien@123")
    private String matKhau;

    @Schema(description = "Số điện thoại liên hệ", example = "0987654321")
    @Pattern(regexp = "^(?:|(0|\\+84)[0-9\\s.-]{9,13})$", message = "Số điện thoại không đúng định dạng Việt Nam (VD: 0987654321 hoặc +84987654321)")
    private String soDienThoai;

    @Schema(description = "Địa chỉ email liên hệ", example = "nhanvien@simplain.vn")
    @Pattern(regexp = "^(?:|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$", message = "Email không đúng định dạng chuẩn (VD: ten@domain.com)")
    @Size(max = 150, message = "Email tối đa 150 ký tự")
    private String email;

    @Schema(description = "ID Vai trò gán cho nhân viên", example = "2")
    @NotNull(message = "Vui lòng chọn vai trò cho nhân viên")
    private Long maVaiTroId;

    @Schema(description = "Trạng thái nhân viên (1: Đang làm, 0: Đã nghỉ/Khóa)", example = "1")
    @Builder.Default
    private Integer trangThai = 1;
}
