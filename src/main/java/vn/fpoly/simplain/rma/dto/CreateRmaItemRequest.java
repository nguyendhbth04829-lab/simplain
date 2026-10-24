package vn.fpoly.simplain.rma.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO thông tin từng sản phẩm lỗi muốn đổi/trả trong đơn hàng (US24).
 */
@Schema(description = "Thông tin sản phẩm cần đổi trả")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRmaItemRequest {

    @Schema(description = "ID dòng chi tiết đơn hàng (chi_tiet_don_hang) chứa sản phẩm muốn đổi/trả", example = "10")
    @NotNull(message = "ID chi tiết đơn hàng không được để trống")
    private Long chiTietDonHangId;

    @Schema(description = "Số lượng sản phẩm muốn đổi/trả", example = "1")
    @NotNull(message = "Số lượng trả không được để trống")
    @Min(value = 1, message = "Số lượng đổi trả tối thiểu là 1")
    private Integer soLuongTra;

    @Schema(description = "ID biến thể mới muốn đổi sang (nếu chọn hình thức Đổi Hàng)", example = "2")
    private Long maBienTheMoiId;

    @Schema(description = "Mô tả tình trạng lỗi của sản phẩm", example = "Rách đường chỉ may ở tay áo, sai kích thước")
    @NotBlank(message = "Tình trạng sản phẩm không được để trống")
    private String tinhTrangSanPham;
}
