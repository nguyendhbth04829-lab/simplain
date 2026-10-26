/**
 * Tác giả: Gia Minh
 * Chức năng: Repository đơn hàng
 */
package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.DonHang;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {
    Optional<DonHang> findByMaDonHang(String maDonHang);
    List<DonHang> findAllByMaKhachHangId(Long maKhachHangId);
}
