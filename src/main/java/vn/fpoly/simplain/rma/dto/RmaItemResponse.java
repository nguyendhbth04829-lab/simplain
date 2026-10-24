package vn.fpoly.simplain.rma.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO phản hồi chi tiết từng sản phẩm trong yêu cầu RMA (US24, US25).
 */
@Schema(description = "Chi tiết sản phẩm trong yêu cầu đổi trả")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RmaItemResponse {

    @Schema(description = "ID dòng chi tiết đổi trả", example = "1")
    private Long id;

    @Schema(description = "ID dòng chi tiết đơn hàng cũ", example = "10")
    private Long chiTietDonHangId;

    @Schema(description = "Tên sản phẩm", example = "Áo Polo Nam Classic")
    private String tenSanPham;

    @Schema(description = "Mã SKU cũ đã mua", example = "AO-POLO-TRANG-L")
    private String maSku;

    @Schema(description = "Đơn giá sản phẩm", example = "199000")
    private BigDecimal donGia;

    @Schema(description = "Số lượng muốn đổi/trả", example = "1")
    private Integer soLuongTra;

    @Schema(description = "ID biến thể mới muốn đổi sang (nếu có)", example = "2")
    private Long maBienTheMoiId;

    @Schema(description = "Tình trạng sản phẩm lỗi", example = "Rách cúc áo")
    private String tinhTrangSanPham;
}
