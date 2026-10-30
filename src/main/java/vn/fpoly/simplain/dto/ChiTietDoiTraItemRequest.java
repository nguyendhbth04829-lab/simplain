package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho một sản phẩm trong đơn cũ được tick chọn đổi/trả (US24 - Tiêu chí 1 & 2).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin mặt hàng yêu cầu đổi/trả trong đơn cũ (US24)")
public class ChiTietDoiTraItemRequest {

    @Schema(description = "Tick chọn mặt hàng lỗi cần đổi/trả")
    private Boolean selected;

    @Schema(description = "ID dòng chi tiết đơn hàng [chi_tiet_don_hang.id]")
    private Long chiTietDonHangId;

    @Schema(description = "Số lượng muốn đổi/trả (tối thiểu 1, tối đa bằng số lượng đã mua)")
    private Integer soLuongTra;

    @Schema(description = "Mô tả tình trạng lỗi của sản phẩm (VD: bung chỉ, rách vải, sai size/màu...)")
    private String tinhTrangSanPham;

    @Schema(description = "Mã biến thể mới (SKU mới) muốn đổi sang (khi hình thức là ĐỔI HÀNG)")
    private Long maBienTheMoiId;
}
