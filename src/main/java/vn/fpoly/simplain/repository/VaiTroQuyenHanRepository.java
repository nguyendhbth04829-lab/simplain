package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.VaiTroQuyenHan;

import java.util.Collection;
import java.util.List;

/**
 * Repository truy xuất bảng liên kết vai_tro_quyen_han.
 */
@Repository
public interface VaiTroQuyenHanRepository extends JpaRepository<VaiTroQuyenHan, Long> {

    List<VaiTroQuyenHan> findByMaVaiTroId(Long maVaiTroId);

    List<VaiTroQuyenHan> findByMaVaiTroIdIn(Collection<Long> maVaiTroIds);

    @Transactional
    @Modifying
    @Query("DELETE FROM VaiTroQuyenHan q WHERE q.maVaiTroId = :maVaiTroId")
    void deleteByMaVaiTroId(@Param("maVaiTroId") Long maVaiTroId);

    boolean existsByMaVaiTroIdAndMaQuyen(Long maVaiTroId, String maQuyen);
}
