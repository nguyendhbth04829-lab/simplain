package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.ChiTietGioHang;

import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu Chi tiết giỏ hàng (US17).
 */
@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, Long> {

    List<ChiTietGioHang> findByMaGioHangIdOrderByNgayThemDesc(Long maGioHangId);

    Optional<ChiTietGioHang> findByMaGioHangIdAndMaBienTheIdAndNhomComboTokenIsNull(Long maGioHangId, Long maBienTheId);

    List<ChiTietGioHang> findByMaGioHangIdAndNhomComboToken(Long maGioHangId, String nhomComboToken);

    List<ChiTietGioHang> findByNhomComboToken(String nhomComboToken);

    void deleteByMaGioHangIdAndNhomComboToken(Long maGioHangId, String nhomComboToken);

    void deleteByMaGioHangId(Long maGioHangId);

    long countByMaGioHangId(Long maGioHangId);
}
