package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO hiển thị thông tin hồ sơ và phân quyền của Nhân viên đăng nhập CMS (US02).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin hồ sơ và quyền hạn của nhân viên trong hệ thống CMS")
public class StaffProfileResponse {

    @Schema(description = "ID bản ghi nhân viên", example = "1")
    private Long id;

    @Schema(description = "Mã nhân viên", example = "NV001")
    private String maNhanVien;

    @Schema(description = "Họ và tên nhân viên", example = "Nguyễn Văn Admin")
    private String hoTen;

    @Schema(description = "Tên đăng nhập", example = "superadmin")
    private String tenDangNhap;

    @Schema(description = "Địa chỉ email", example = "admin@simplain.vn")
    private String email;

    @Schema(description = "Số điện thoại", example = "0901112233")
    private String soDienThoai;

    @Schema(description = "Mã vai trò (ADMIN, CSKH, BAN_HANG)", example = "ADMIN")
    private String maVaiTro;

    @Schema(description = "Tên hiển thị vai trò", example = "Quản trị viên tối cao")
    private String tenVaiTro;

    @Schema(description = "Mô tả vai trò", example = "Toàn quyền cấu hình hệ thống")
    private String moTaVaiTro;

    @Schema(description = "Trạng thái tài khoản (1: Hoạt động, 0: Khóa)", example = "1")
    private Integer trangThai;

    @Schema(description = "Thời điểm tạo tài khoản")
    private LocalDateTime ngayTao;
}
