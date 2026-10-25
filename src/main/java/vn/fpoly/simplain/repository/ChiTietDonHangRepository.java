/**
 * Tác giả: Gia Minh
 * Chức năng: Repository chi tiết đơn hàng
 */
package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.ChiTietDonHang;

import java.util.List;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {
    List<ChiTietDonHang> findAllByMaDonHangId(Long maDonHangId);
}
