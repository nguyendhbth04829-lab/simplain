package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO phản hồi kết quả đăng nhập hệ thống CMS quản trị cho Nhân viên (US02).
 * Chứa mã token JWT có gắn thông tin định danh và quyền hạn (Role-based claims).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Phản hồi kết quả đăng nhập CMS nhân viên kèm JWT Token và vai trò")
public class StaffAuthResponse {

    @Schema(description = "Chuỗi JWT Token có gắn Role-based claims", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Builder.Default
    @Schema(description = "Loại token xác thực", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Thời gian hết hạn của token tính theo mili-giây", example = "86400000")
    private Long expiresInMs;

    @Schema(description = "Mã nhân viên", example = "NV001")
    private String maNhanVien;

    @Schema(description = "Họ và tên nhân viên", example = "Nguyễn Văn Admin")
    private String hoTen;

    @Schema(description = "Tên đăng nhập hệ thống", example = "superadmin")
    private String tenDangNhap;

    @Schema(description = "Địa chỉ email", example = "admin@simplain.vn")
    private String email;

    @Schema(description = "Số điện thoại", example = "0901112233")
    private String soDienThoai;

    @Schema(description = "Mã vai trò nhân viên (Role code)", example = "ADMIN")
    private String maVaiTro;

    @Schema(description = "Tên hiển thị của vai trò", example = "Quản trị viên tối cao")
    private String tenVaiTro;

    @Schema(description = "Trạng thái tài khoản (1: Hoạt động, 0: Khóa)", example = "1")
    private Integer trangThai;

    @Schema(description = "Thông báo kết quả", example = "Đăng nhập CMS thành công với quyền Quản trị viên tối cao")
    private String message;
}
