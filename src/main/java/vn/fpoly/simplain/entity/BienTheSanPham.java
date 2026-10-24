package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Thực thể Biến thể sản phẩm (SKU), ánh xạ bảng 'bien_the_san_pham'.
 * Quản lý giá bán, giá gốc và số lượng tồn kho để phục vụ trừ kho khi tạo đơn hàng (US21).
 */
@Entity
@Table(name = "bien_the_san_pham")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BienTheSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_san_pham_id")
    private Long maSanPhamId;

    @Column(name = "ma_mau_sac_id")
    private Long maMauSacId;

    @Column(name = "ma_kich_thuoc_id")
    private Long maKichThuocId;

    @Column(name = "ma_sku", nullable = false, unique = true, length = 100)
    private String maSku;

    @Column(name = "ma_vach_barcode", unique = true, length = 100)
    private String maVachBarcode;

    @Builder.Default
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 0;

    @Column(name = "gia_ban", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBan;

    @Builder.Default
    @Column(name = "gia_goc", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaGoc = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;
}
