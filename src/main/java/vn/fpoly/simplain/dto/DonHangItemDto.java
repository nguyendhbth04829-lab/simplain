package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO hiển thị từng mặt hàng trong đơn hàng kèm ảnh đại diện và phân loại (US23).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin mặt hàng trong đơn hàng (US23)")
public class DonHangItemDto {

    private Long id;
    private Long maBienTheId;
    private Long maSanPhamId;
    private String tenSanPham;
    private String maSku;
    private BigDecimal donGia;
    private Integer soLuong;
    private BigDecimal thanhTien;
    private String hinhAnhUrl;
    private String tenMau;
    private String tenKichThuoc;
    private String slug;
}
