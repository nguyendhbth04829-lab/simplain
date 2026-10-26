/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - Repository biến động điểm thưởng Coolcash
 */
package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.BienDongDiem;

import java.util.List;

@Repository
public interface BienDongDiemRepository extends JpaRepository<BienDongDiem, Long> {

    List<BienDongDiem> findAllByMaKhachHangIdOrderByNgayTaoDesc(Long maKhachHangId);

    boolean existsByMaDonHangIdAndLoaiBienDong(Long maDonHangId, String loaiBienDong);
}
