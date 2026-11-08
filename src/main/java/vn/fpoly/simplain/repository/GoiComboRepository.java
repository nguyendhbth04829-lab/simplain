package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.GoiCombo;

import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu Gói combo sản phẩm (US17 & US28 - Mod 6).
 */
@Repository
public interface GoiComboRepository extends JpaRepository<GoiCombo, Long> {

    List<GoiCombo> findByKichHoatTrue();

    Optional<GoiCombo> findByMaCombo(String maCombo);

    Optional<GoiCombo> findByMaComboIgnoreCase(String maCombo);

    Optional<GoiCombo> findByDuongDanSlug(String duongDanSlug);

    boolean existsByMaComboIgnoreCase(String maCombo);

    boolean existsByMaComboIgnoreCaseAndIdNot(String maCombo, Long id);

    boolean existsByDuongDanSlugIgnoreCase(String duongDanSlug);

    boolean existsByDuongDanSlugIgnoreCaseAndIdNot(String duongDanSlug, Long id);

    @Query("SELECT g FROM GoiCombo g WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR LOWER(g.tenCombo) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR LOWER(g.maCombo) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "AND (:kichHoat IS NULL OR g.kichHoat = :kichHoat) " +
           "AND (:soMonQuyDinh IS NULL OR g.soMonQuyDinh = :soMonQuyDinh) " +
           "AND (:giaTu IS NULL OR g.giaCombo >= :giaTu) " +
           "AND (:giaDen IS NULL OR g.giaCombo <= :giaDen) " +
           "ORDER BY g.id DESC")
    Page<GoiCombo> timKiemGoiCombo(@Param("tuKhoa") String tuKhoa,
                                  @Param("kichHoat") Boolean kichHoat,
                                  @Param("soMonQuyDinh") Integer soMonQuyDinh,
                                  @Param("giaTu") java.math.BigDecimal giaTu,
                                  @Param("giaDen") java.math.BigDecimal giaDen,
                                  Pageable pageable);

    default Page<GoiCombo> timKiemGoiCombo(String tuKhoa, Boolean kichHoat, Pageable pageable) {
        return timKiemGoiCombo(tuKhoa, kichHoat, null, null, null, pageable);
    }
}
