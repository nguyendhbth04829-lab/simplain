package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Thực thể Biến thể trong đợt Flash Sale [chi_tiet_flash_sale] (US29 - Mod 6 / Bán hàng - Khoa).
 * - Tiêu chí 2: Gán giá sốc và giới hạn số lượng (so_luong_mo_ban) cho biến thể.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chi_tiet_flash_sale")
public class ChiTietFlashSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Khung giờ Flash Sale không được để trống")
    @Column(name = "ma_flash_sale_id", nullable = false)
    private Long maFlashSaleId;

    @NotNull(message = "Biến thể sản phẩm (SKU) không được để trống")
    @Column(name = "ma_bien_the_id", nullable = false)
    private Long maBienTheId;

    @NotNull(message = "Giá sốc Flash Sale không được để trống")
    @DecimalMin(value = "1000.0", message = "Giá Flash Sale tối thiểu phải từ 1.000 VNĐ")
    @Column(name = "gia_flash_sale", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaFlashSale;

    @NotNull(message = "Số lượng mở bán không được để trống")
    @Min(value = 1, message = "Số lượng mở bán phải từ 1 sản phẩm trở lên")
    @Column(name = "so_luong_mo_ban", nullable = false)
    private Integer soLuongMoBan;

    @Builder.Default
    @Column(name = "so_luong_da_ban", nullable = false)
    private Integer soLuongDaBan = 0;

    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_flash_sale_id", insertable = false, updatable = false)
    private FlashSale flashSale;

    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bien_the_id", insertable = false, updatable = false)
    private BienTheSanPham bienTheSanPham;

    @PrePersist
    public void prePersist() {
        if (this.soLuongDaBan == null) {
            this.soLuongDaBan = 0;
        }
    }

    // Các trường tạm phục vụ hiển thị UI
    @Transient
    private Long maSanPhamId;

    @Transient
    private String maSku;

    @Transient
    private String tenSanPham;

    @Transient
    private String tenMauSac;

    @Transient
    private String maMauHex;

    @Transient
    private String tenKichThuoc;

    @Transient
    private BigDecimal giaBanGoc;

    @Transient
    private Integer tonKhoHienTai;

    @Transient
    private String anhDaiDienUrl;

    @Transient
    public int getPhanTramGiamGia() {
        if (giaBanGoc == null || giaBanGoc.compareTo(BigDecimal.ZERO) <= 0 || giaFlashSale == null) {
            return 0;
        }
        if (giaFlashSale.compareTo(giaBanGoc) >= 0) {
            return 0;
        }
        BigDecimal diff = giaBanGoc.subtract(giaFlashSale);
        return diff.multiply(BigDecimal.valueOf(100)).divide(giaBanGoc, 0, RoundingMode.HALF_UP).intValue();
    }

    @Transient
    public int getPhanTramDaBan() {
        if (soLuongMoBan == null || soLuongMoBan <= 0) {
            return 0;
        }
        int daBan = (soLuongDaBan != null) ? soLuongDaBan : 0;
        int phanTram = (int) Math.round(((double) daBan / soLuongMoBan) * 100);
        return Math.min(100, Math.max(0, phanTram));
    }
}
