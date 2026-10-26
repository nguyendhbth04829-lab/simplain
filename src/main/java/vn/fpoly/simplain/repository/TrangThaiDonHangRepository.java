/**
 * Tác giả: Gia Minh
 * Chức năng: Repository trạng thái đơn hàng
 * Cập nhật xác nhận commit US15: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.TrangThaiDonHang;

import java.util.Optional;

@Repository
public interface TrangThaiDonHangRepository extends JpaRepository<TrangThaiDonHang, Long> {
    Optional<TrangThaiDonHang> findByMaTrangThai(String maTrangThai);
}
