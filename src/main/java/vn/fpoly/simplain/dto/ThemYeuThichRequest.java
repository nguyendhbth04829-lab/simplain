/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - DTO yêu cầu thêm sản phẩm vào danh sách yêu thích
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Yêu cầu thêm sản phẩm vào danh sách yêu thích (Wishlist).
 */
@Schema(description = "Dữ liệu yêu cầu thêm sản phẩm vào danh sách yêu thích")
public record ThemYeuThichRequest(

        @Schema(description = "ID của sản phẩm cần thêm", example = "1")
        @NotNull(message = "Mã sản phẩm không được để trống")
        Long maSanPhamId
) {
}
