package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Cập nhật số lượng sản phẩm hoặc combo trong giỏ hàng (US17).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapNhatSoLuongRequest {

    private Long chiTietGioHangId; // Dùng khi cập nhật sản phẩm lẻ

    private String nhomComboToken; // Dùng khi cập nhật cả bó combo

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    private Integer soLuong;
}
