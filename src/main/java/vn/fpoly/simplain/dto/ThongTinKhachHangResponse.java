package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Thông tin hồ sơ khách hàng trả về khi xác minh JWT Token thành công.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hồ sơ thông tin chi tiết của khách hàng")
public class ThongTinKhachHangResponse {

    @Schema(description = "ID khách hàng", example = "1")
    private Long id;

    @Schema(description = "Mã khách hàng", example = "KH123456")
    private String maKhachHang;

    @Schema(description = "Họ và tên", example = "Nguyễn Văn An")
    private String hoTen;

    @Schema(description = "Số điện thoại", example = "0987654321")
    private String soDienThoai;

    @Schema(description = "Địa chỉ email", example = "nguyenvanan@gmail.com")
    private String email;

    @Schema(description = "Đường dẫn ảnh đại diện", example = "/uploads/avatar_123.jpg")
    private String anhDaiDien;

    @Schema(description = "Giới tính", example = "Nam")
    private String gioiTinh;

    @Schema(description = "Ngày sinh", example = "2000-01-15")
    private LocalDate ngaySinh;

    @Schema(description = "Trạng thái tài khoản (1: Đang hoạt động, 0: Bị khóa)", example = "1")
    private Integer trangThai;

    @Schema(description = "Thời điểm tạo tài khoản")
    private LocalDateTime ngayTao;
}
