package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.FlashSale;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Kho dữ liệu Khung giờ Flash Sale [flash_sale] (US29 - Mod 6).
 */
@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    List<FlashSale> findByKichHoatTrue();

    @Query("SELECT fs FROM FlashSale fs WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR LOWER(fs.tieuDe) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "AND (:kichHoat IS NULL OR fs.kichHoat = :kichHoat) " +
           "ORDER BY fs.id DESC")
    Page<FlashSale> timKiemFlashSale(@Param("tuKhoa") String tuKhoa,
                                    @Param("kichHoat") Boolean kichHoat,
                                    Pageable pageable);

    @Query("SELECT fs FROM FlashSale fs WHERE " +
           "(:excludeId IS NULL OR fs.id <> :excludeId) " +
           "AND fs.kichHoat = true " +
           "AND fs.thoiGianBatDau < :end " +
           "AND fs.thoiGianKetThuc > :start")
    List<FlashSale> timKhungGioChongCheo(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        @Param("excludeId") Long excludeId);

    @Query("SELECT fs FROM FlashSale fs WHERE fs.kichHoat = true " +
           "AND fs.thoiGianBatDau <= :now AND fs.thoiGianKetThuc >= :now")
    List<FlashSale> timFlashSaleDangDienRa(@Param("now") LocalDateTime now);
}
