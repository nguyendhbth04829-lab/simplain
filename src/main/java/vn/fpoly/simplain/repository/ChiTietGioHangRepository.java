package vn.fpoly.simplain.repository;

import vn.fpoly.simplain.entity.ChiTietGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Truy vấn dòng hàng trong giỏ.
 */
@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, Long> {
    List<ChiTietGioHang> findByMaGioHangIdOrderByNgayThemDesc(Long maGioHangId);

    List<ChiTietGioHang> findByMaGioHangIdAndNhomComboToken(Long maGioHangId, String nhomComboToken);

    void deleteByMaGioHangIdAndNhomComboToken(Long maGioHangId, String nhomComboToken);

    /**
     * Tìm dòng trùng để cộng dồn: cùng biến thể + cùng combo + cùng token.
     * So sánh null an toàn ở tầng Service.
     */
    List<ChiTietGioHang> findByMaGioHangIdAndMaBienTheId(Long maGioHangId, Long maBienTheId);
}
