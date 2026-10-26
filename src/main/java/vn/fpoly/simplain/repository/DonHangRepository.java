/**
 * Tác giả: Gia Minh
 * Chức năng: Repository đơn hàng
 * Cập nhật xác nhận commit US15: 26/10/2026 - Gia Minh
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
