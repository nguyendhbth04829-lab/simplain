package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO đại diện cho một sản phẩm trong đơn hàng hiển thị trên form Đổi/Trả (US24).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mặt hàng trong đơn cũ để tick chọn đổi/trả (US24)")
public class ItemDoiTraFormDto {

    private Long chiTietDonHangId;
    private Long maBienTheId;
    private Long maSanPhamId;
    private String tenSanPham;
    private String maSku;
    private String tenMau;
    private String tenKichThuoc;
    private BigDecimal donGia;
    private Integer soLuongMua;
    private BigDecimal thanhTien;
    private String hinhAnhUrl;

    /**
     * Danh sách các biến thể SKU cùng sản phẩm còn hàng trong kho để khách hàng chọn đổi sang.
     */
    @Builder.Default
    private List<BienTheDoiTraDto> danhSachBienTheDoi = new ArrayList<>();
}
