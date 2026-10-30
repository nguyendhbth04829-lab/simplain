package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.GioHang;

import java.util.Optional;

/**
 * Kho dữ liệu Giỏ hàng (US17).
 */
@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Long> {

    Optional<GioHang> findByMaKhachHangId(Long maKhachHangId);
}
