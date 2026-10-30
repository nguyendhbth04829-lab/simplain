package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO biểu diễn một mặt hàng trong giỏ hàng (US17).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemGioHangDto {

    private Long chiTietId;
    private Long maBienTheId;
    private Long maSanPhamId;
    private String tenSanPham;
    private String duongDanSlug;
    private String maSku;
    private String tenMau;
    private String maMauHex;
    private String tenKichThuoc;
    private String anhDaiDien;
    private BigDecimal donGia;
    private BigDecimal giaBanGoc;
    private Boolean laFlashSale;
    private Integer phanTramGiamGia;
    private Integer soLuong;
    private BigDecimal thanhTien;
    private Integer soLuongTon;
    private Boolean hetHang;
}
