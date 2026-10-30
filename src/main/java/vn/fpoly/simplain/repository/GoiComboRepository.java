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
           "ORDER BY g.id DESC")
    Page<GoiCombo> timKiemGoiCombo(@Param("tuKhoa") String tuKhoa,
                                  @Param("kichHoat") Boolean kichHoat,
                                  Pageable pageable);
}
