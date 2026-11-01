package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thực thể ánh xạ sản phẩm áp dụng trong gói combo [san_pham_trong_combo] (US28 - Mod 6).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "san_pham_trong_combo")
@IdClass(SanPhamTrongComboId.class)
public class SanPhamTrongCombo {

    @Id
    @Column(name = "ma_combo_id", nullable = false)
    private Long maComboId;

    @Id
    @Column(name = "ma_san_pham_id", nullable = false)
    private Long maSanPhamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_combo_id", insertable = false, updatable = false)
    private GoiCombo goiCombo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_san_pham_id", insertable = false, updatable = false)
    private SanPham sanPham;
    public SanPhamTrongCombo(Long maComboId, Long maSanPhamId) {
        this.maComboId = maComboId;
        this.maSanPhamId = maSanPhamId;
    }

    public SanPhamTrongCombo(SanPhamTrongComboId id) {
        if (id != null) {
            this.maComboId = id.getMaComboId();
            this.maSanPhamId = id.getMaSanPhamId();
        }
    }
}
