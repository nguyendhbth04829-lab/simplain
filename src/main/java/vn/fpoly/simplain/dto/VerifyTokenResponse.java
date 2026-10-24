package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO kết quả sau khi xác minh tính hợp lệ của JWT Token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kết quả xác minh JWT Token")
public class VerifyTokenResponse {

    @Schema(description = "Token có hợp lệ hay không", example = "true")
    private boolean valid;

    @Schema(description = "Mã khách hàng được trích xuất từ Token", example = "KH001")
    private String maKhachHang;

    @Schema(description = "Email được trích xuất từ Token", example = "duy.nguyen@coolmate.me")
    private String email;

    @Schema(description = "Họ tên được trích xuất từ Token", example = "Nguyễn Văn Duy")
    private String hoTen;

    @Schema(description = "Vai trò", example = "CUSTOMER")
    private String vaiTro;

    @Schema(description = "Thời điểm hết hạn của Token")
    private Date expiresAt;

    @Schema(description = "Thông điệp kiểm tra", example = "JWT Token hợp lệ và còn hiệu lực.")
    private String message;
}
