package vn.fpoly.simplain.repository;

import vn.fpoly.simplain.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Truy vấn giỏ hàng.
 */
@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Long> {
    Optional<GioHang> findByMaKhachHangId(Long maKhachHangId);
}
