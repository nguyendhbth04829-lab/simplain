package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO thông tin chi tiết hồ sơ khách hàng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hồ sơ chi tiết của khách hàng")
public class CustomerProfileResponse {

    @Schema(description = "ID bản ghi", example = "1")
    private Long id;

    @Schema(description = "Mã khách hàng", example = "KH001")
    private String maKhachHang;

    @Schema(description = "Họ và tên", example = "Nguyễn Văn Duy")
    private String hoTen;

    @Schema(description = "Số điện thoại", example = "0987654321")
    private String soDienThoai;

    @Schema(description = "Email", example = "duy.nguyen@coolmate.me")
    private String email;

    @Schema(description = "Ảnh đại diện", example = "https://m.coolmate.me/avatar.png")
    private String anhDaiDien;

    @Schema(description = "Giới tính", example = "Nam")
    private String gioiTinh;

    @Schema(description = "Ngày sinh", example = "2000-01-01")
    private LocalDate ngaySinh;

    @Schema(description = "Trạng thái tài khoản (1: Hoạt động, 0: Khóa)", example = "1")
    private Integer trangThai;

    @Schema(description = "Ngày đăng ký tài khoản")
    private LocalDateTime ngayTao;
}
