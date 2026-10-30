package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.entity.SanPhamTrongCombo;
import vn.fpoly.simplain.entity.SanPhamTrongComboId;

import java.util.List;

/**
 * Kho dữ liệu ánh xạ sản phẩm trong gói combo [san_pham_trong_combo] (US28 - Mod 6).
 */
@Repository
public interface SanPhamTrongComboRepository extends JpaRepository<SanPhamTrongCombo, SanPhamTrongComboId> {

    List<SanPhamTrongCombo> findByMaComboId(Long maComboId);

    long countByMaComboId(Long maComboId);

    @Modifying
    @Query("DELETE FROM SanPhamTrongCombo s WHERE s.maComboId = :maComboId")
    void xoaTheoMaComboId(@Param("maComboId") Long maComboId);

    @Query("SELECT s.maSanPhamId FROM SanPhamTrongCombo s WHERE s.maComboId = :maComboId")
    List<Long> layDanhSachMaSanPhamIdTheoComboId(@Param("maComboId") Long maComboId);

    @Query("SELECT sp FROM SanPham sp JOIN SanPhamTrongCombo s ON sp.id = s.maSanPhamId WHERE s.maComboId = :maComboId ORDER BY sp.id DESC")
    List<SanPham> layDanhSachSanPhamTheoComboId(@Param("maComboId") Long maComboId);

    boolean existsByMaComboIdAndMaSanPhamId(Long maComboId, Long maSanPhamId);
}
