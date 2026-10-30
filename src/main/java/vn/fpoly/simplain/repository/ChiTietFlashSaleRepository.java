package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.ChiTietFlashSale;

import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu Biến thể trong Flash Sale [chi_tiet_flash_sale] (US29 - Mod 6).
 */
@Repository
public interface ChiTietFlashSaleRepository extends JpaRepository<ChiTietFlashSale, Long> {

    List<ChiTietFlashSale> findByMaFlashSaleId(Long maFlashSaleId);

    Optional<ChiTietFlashSale> findByMaFlashSaleIdAndMaBienTheId(Long maFlashSaleId, Long maBienTheId);

    long countByMaFlashSaleId(Long maFlashSaleId);

    boolean existsByMaFlashSaleIdAndMaBienTheId(Long maFlashSaleId, Long maBienTheId);

    @Modifying
    @Query("DELETE FROM ChiTietFlashSale c WHERE c.maFlashSaleId = :maFlashSaleId")
    void xoaTheoMaFlashSaleId(@Param("maFlashSaleId") Long maFlashSaleId);

    @Query("SELECT c FROM ChiTietFlashSale c JOIN c.flashSale fs " +
           "WHERE c.maBienTheId = :bienTheId " +
           "AND fs.kichHoat = true " +
           "AND fs.thoiGianBatDau <= :now AND fs.thoiGianKetThuc >= :now")
    List<ChiTietFlashSale> timChiTietFlashSaleDangApDung(@Param("bienTheId") Long bienTheId, @Param("now") java.time.LocalDateTime now);

    @Query("SELECT c FROM ChiTietFlashSale c JOIN c.flashSale fs " +
           "WHERE fs.kichHoat = true " +
           "AND fs.thoiGianBatDau <= :now AND fs.thoiGianKetThuc >= :now")
    List<ChiTietFlashSale> timTatCaChiTietFlashSaleDangApDung(@Param("now") java.time.LocalDateTime now);
}
