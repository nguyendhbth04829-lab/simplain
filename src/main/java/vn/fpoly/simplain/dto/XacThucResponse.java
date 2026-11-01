package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kết quả xác thực trả về khi khách hàng đăng ký hoặc đăng nhập thành công.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kết quả xác thực kèm JWT Token của khách hàng")
public class XacThucResponse {

    @Schema(description = "JSON Web Token (JWT) được cấp phát", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Loại Token", example = "Bearer")
    @Builder.Default
    private String loaiToken = "Bearer";

    @Schema(description = "ID định danh của khách hàng", example = "1")
    private Long id;

    @Schema(description = "Mã khách hàng tự sinh", example = "KH123456")
    private String maKhachHang;

    @Schema(description = "Họ và tên khách hàng", example = "Nguyễn Văn An")
    private String hoTen;

    @Schema(description = "Địa chỉ email", example = "nguyenvanan@gmail.com")
    private String email;

    @Schema(description = "Số điện thoại", example = "0987654321")
    private String soDienThoai;

    @Schema(description = "Đường dẫn ảnh đại diện", example = "/uploads/avatar_123.jpg")
    private String anhDaiDien;

    @Schema(description = "Thông báo kết quả", example = "Đăng nhập thành công!")
    private String thongBao;
}
