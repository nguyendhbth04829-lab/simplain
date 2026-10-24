package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO kết quả sau khi đăng ký hoặc đăng nhập thành công.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kết quả xác thực chứa JWT Token và thông tin khách hàng")
public class AuthResponse {

    @Schema(description = "Mã JWT Token để gắn vào Header Authorization: Bearer <token>", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Loại token", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Thời gian hết hạn tính bằng mili-giây", example = "86400000")
    private Long expiresIn;

    @Schema(description = "Mã định danh khách hàng", example = "KH001")
    private String maKhachHang;

    @Schema(description = "Họ và tên khách hàng", example = "Nguyễn Văn Duy")
    private String hoTen;

    @Schema(description = "Email khách hàng", example = "duy.nguyen@coolmate.me")
    private String email;

    @Schema(description = "Số điện thoại", example = "0987654321")
    private String soDienThoai;

    @Schema(description = "Vai trò trong hệ thống", example = "CUSTOMER")
    @Builder.Default
    private String vaiTro = "CUSTOMER";

    @Schema(description = "Thông điệp phản hồi", example = "Đăng nhập thành công!")
    private String message;
}
