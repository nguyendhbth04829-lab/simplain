package vn.fpoly.simplain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO phản hồi thông tin từng món hàng trong đơn (US21, US23).
 */
@Schema(description = "Thông tin chi tiết sản phẩm trong đơn hàng")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    @Schema(description = "ID dòng chi tiết đơn hàng", example = "10")
    private Long id;

    @Schema(description = "ID biến thể sản phẩm", example = "1")
    private Long bienTheId;

    @Schema(description = "Mã SKU", example = "AO-POLO-TRANG-L")
    private String maSku;

    @Schema(description = "Tên sản phẩm", example = "Áo Polo Nam Classic")
    private String tenSanPham;

    @Schema(description = "Đơn giá mua", example = "199000")
    private BigDecimal donGia;

    @Schema(description = "Số lượng mua", example = "2")
    private Integer soLuong;

    @Schema(description = "Thành tiền của dòng sản phẩm", example = "398000")
    private BigDecimal thanhTien;
}
