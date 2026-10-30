package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO chứa thông tin biến thể SKU khả dụng để khách lựa chọn khi đổi hàng (US24 - Tiêu chí 2).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Biến thể sản phẩm khả dụng để đổi (US24)")
public class BienTheDoiTraDto {

    private Long id;
    private String maSku;
    private Long maSanPhamId;
    private String tenSanPham;
    private String tenMau;
    private String tenKichThuoc;
    private BigDecimal giaBan;
    private Integer soLuongTon;
    private String hinhAnhUrl;
}
