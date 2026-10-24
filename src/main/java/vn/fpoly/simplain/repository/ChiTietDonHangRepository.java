package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.ChiTietDonHang;

import java.util.List;

/**
 * Repository thao tác dữ liệu bảng 'chi_tiet_don_hang'.
 */
@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {

    /**
     * Lấy toàn bộ danh sách sản phẩm trong một đơn hàng.
     */
    List<ChiTietDonHang> findByDonHangId(Long donHangId);
}
