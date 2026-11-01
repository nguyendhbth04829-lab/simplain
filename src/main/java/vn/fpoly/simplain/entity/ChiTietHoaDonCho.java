package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Thực thể Mặt hàng trong Hóa đơn chờ [chi_tiet_hoa_don_cho] (POS).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chi_tiet_hoa_don_cho")
public class ChiTietHoaDonCho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_hoa_don_cho_id", nullable = false)
    private Long maHoaDonChoId;

    @Column(name = "ma_bien_the_id", nullable = false)
    private Long maBienTheId;

    @Builder.Default
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 1;

    @Builder.Default
    @Column(name = "don_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGia = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "thanh_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal thanhTien = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_hoa_don_cho_id", insertable = false, updatable = false)
    private HoaDonCho hoaDonCho;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_bien_the_id", insertable = false, updatable = false)
    private BienTheSanPham bienThe;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        if (this.soLuong == null || this.soLuong < 1) {
            this.soLuong = 1;
        }
        if (this.donGia == null) {
            this.donGia = BigDecimal.ZERO;
        }
        this.thanhTien = this.donGia.multiply(BigDecimal.valueOf(this.soLuong));
    }
}
