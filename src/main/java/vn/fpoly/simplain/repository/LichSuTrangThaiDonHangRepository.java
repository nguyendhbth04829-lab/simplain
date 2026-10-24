package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.LichSuTrangThaiDonHang;

import java.util.List;

/**
 * Repository thao tác dữ liệu bảng 'lich_su_trang_thai_don_hang'.
 */
@Repository
public interface LichSuTrangThaiDonHangRepository extends JpaRepository<LichSuTrangThaiDonHang, Long> {

    /**
     * Lấy toàn bộ lịch sử chuyển trạng thái của một đơn hàng, sắp xếp theo thời gian mới nhất (US22, US23).
     */
    List<LichSuTrangThaiDonHang> findByDonHangIdOrderByNgayChuyenDesc(Long donHangId);
}
