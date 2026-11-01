package vn.fpoly.simplain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Khóa phức hợp cho bảng ánh xạ [san_pham_trong_combo] (US28 - Mod 6).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamTrongComboId implements Serializable {

    private Long maComboId;
    private Long maSanPhamId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPhamTrongComboId that = (SanPhamTrongComboId) o;
        return Objects.equals(maComboId, that.maComboId) &&
               Objects.equals(maSanPhamId, that.maSanPhamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maComboId, maSanPhamId);
    }
}
