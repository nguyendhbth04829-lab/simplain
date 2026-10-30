package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.PhuongThucThanhToan;

import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu Phương thức thanh toán (US18).
 */
@Repository
public interface PhuongThucThanhToanRepository extends JpaRepository<PhuongThucThanhToan, Long> {

    Optional<PhuongThucThanhToan> findByMaPhuongThuc(String maPhuongThuc);

    Optional<PhuongThucThanhToan> findByMaPhuongThucIgnoreCase(String maPhuongThuc);

    boolean existsByMaPhuongThucIgnoreCase(String maPhuongThuc);

    boolean existsByMaPhuongThucIgnoreCaseAndIdNot(String maPhuongThuc, Long id);

    List<PhuongThucThanhToan> findByKichHoatTrueOrderByIdAsc();

    @Query("SELECT p FROM PhuongThucThanhToan p WHERE " +
           "(:tuKhoa IS NULL OR LOWER(p.maPhuongThuc) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR LOWER(p.tenPhuongThuc) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR LOWER(p.moTa) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) AND " +
           "(:laTrucTuyen IS NULL OR p.laTrucTuyen = :laTrucTuyen) AND " +
           "(:kichHoat IS NULL OR p.kichHoat = :kichHoat)")
    Page<PhuongThucThanhToan> timKiemPhanTrang(
            @Param("tuKhoa") String tuKhoa,
            @Param("laTrucTuyen") Boolean laTrucTuyen,
            @Param("kichHoat") Boolean kichHoat,
            Pageable pageable);
}
