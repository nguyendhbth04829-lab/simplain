package vn.fpoly.simplain.repository;

import vn.fpoly.simplain.entity.PhuongThucThanhToan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Truy vấn phương thức thanh toán.
 */
@Repository
public interface PhuongThucThanhToanRepository extends JpaRepository<PhuongThucThanhToan, Long> {
    Page<PhuongThucThanhToan> findAllByTenPhuongThucContainingIgnoreCase(String tenPhuongThuc, Pageable pageable);

    /**
     * Tìm theo mã HOẶC tên + lọc kích hoạt + trực tuyến.
     * Gõ "COD" hay "tiền mặt" đều ra.
     */
    @Query("SELECT p FROM PhuongThucThanhToan p WHERE " +
           "(:ten IS NULL OR :ten = '' OR LOWER(p.tenPhuongThuc) LIKE LOWER(CONCAT('%', :ten, '%')) " +
           "OR LOWER(p.maPhuongThuc) LIKE LOWER(CONCAT('%', :ten, '%'))) " +
           "AND (:kichHoat IS NULL OR p.kichHoat = :kichHoat) " +
           "AND (:trucTuyen IS NULL OR p.laTrucTuyen = :trucTuyen)")
    Page<PhuongThucThanhToan> timKiem(@Param("ten") String ten,
                                      @Param("kichHoat") Boolean kichHoat,
                                      @Param("trucTuyen") Boolean trucTuyen,
                                      Pageable pageable);

    List<PhuongThucThanhToan> findByKichHoatTrue();

    boolean existsByMaPhuongThuc(String maPhuongThuc);

    boolean existsByMaPhuongThucAndIdNot(String maPhuongThuc, Long id);
}
