package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.BienDongDiem;

import java.util.List;

/**
 * Kho dữ liệu Biến động điểm Loyalty / Coolcash (US16).
 */
@Repository
public interface BienDongDiemRepository extends JpaRepository<BienDongDiem, Long> {

    List<BienDongDiem> findByMaKhachHangIdOrderByNgayTaoDesc(Long maKhachHangId);

    boolean existsByMaDonHangIdAndLoaiBienDong(Long maDonHangId, String loaiBienDong);
}
