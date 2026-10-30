package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Thêm biến thể lẻ vào giỏ hàng (US17 - Tiêu chí 1).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemGioHangRequest {

    @NotNull(message = "Vui lòng chọn biến thể sản phẩm (Màu sắc & Kích thước)")
    private Long maBienTheId;

    @Builder.Default
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    private Integer soLuong = 1;
}
