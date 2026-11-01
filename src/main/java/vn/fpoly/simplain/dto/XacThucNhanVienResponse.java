package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kết quả xác thực đăng nhập trả về khi Nhân viên đăng nhập CMS thành công.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kết quả xác thực Nhân viên CMS kèm JWT Token và vai trò")
public class XacThucNhanVienResponse {

    @Schema(description = "JSON Web Token (JWT) được cấp phát", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Loại Token", example = "Bearer")
    @Builder.Default
    private String loaiToken = "Bearer";

    @Schema(description = "ID nhân viên", example = "1")
    private Long id;

    @Schema(description = "Mã nhân viên", example = "NV001")
    private String maNhanVien;

    @Schema(description = "Họ và tên nhân viên", example = "Nguyễn Văn Admin")
    private String hoTen;

    @Schema(description = "Tên đăng nhập", example = "superadmin")
    private String tenDangNhap;

    @Schema(description = "Mã vai trò (Role-based)", example = "ADMIN")
    private String maVaiTro;

    @Schema(description = "Tên vai trò hiển thị", example = "Quản trị viên tối cao")
    private String tenVaiTro;

    @Schema(description = "Email nhân viên", example = "admin@simplaintore.vn")
    private String email;

    @Schema(description = "Số điện thoại", example = "0987654321")
    private String soDienThoai;

    @Schema(description = "Trạng thái tài khoản (1: Hoạt động)", example = "1")
    private Integer trangThai;

    @Schema(description = "Danh sách mã quyền hạn được cấp (Dynamic RBAC)", example = "[\"DASHBOARD\", \"SAN_PHAM\", \"BAN_HANG_POS\"]")
    private java.util.List<String> danhSachQuyen;

    @Schema(description = "Thông báo kết quả", example = "Đăng nhập CMS thành công!")
    private String thongBao;
}
