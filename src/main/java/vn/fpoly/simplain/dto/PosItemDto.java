package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO hiển thị từng mặt hàng trong giỏ hóa đơn bán hàng tại quầy (POS).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosItemDto {
    private Long id;                // ID chi_tiet_hoa_don_cho
    private Long maBienTheId;       // ID biến thể SKU
    private String maSku;
    private String maVachBarcode;
    private String tenSanPham;
    private String tenMauSac;
    private String maMauHex;
    private String tenKichThuoc;
    private BigDecimal donGia;
    private Integer soLuong;
    private Integer tonKhoHienTai;
    private BigDecimal thanhTien;
    private String anhDaiDienUrl;
}
