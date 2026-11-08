package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request thêm mặt hàng vào tab hóa đơn chờ tại quầy.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosThemMatHangRequest {

    @NotNull(message = "ID hóa đơn chờ không được để trống")
    private Long maHoaDonChoId;

    @NotNull(message = "ID biến thể sản phẩm không được để trống")
    private Long maBienTheId;

    @Builder.Default
    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    private Integer soLuong = 1;
}
