package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO yêu cầu kiểm tra tính hợp lệ của JWT Token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu kiểm tra JWT Token")
public class VerifyTokenRequest {

    @NotBlank(message = "Token không được để trống")
    @Schema(description = "Chuỗi JWT Token cần kiểm tra", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
}
