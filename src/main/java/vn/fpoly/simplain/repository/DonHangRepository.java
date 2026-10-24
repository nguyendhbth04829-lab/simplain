package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.DonHang;

import java.util.List;
import java.util.Optional;

/**
 * Repository thao tác dữ liệu bảng 'don_hang'.
 */
@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {

    /**
     * Tìm đơn hàng theo mã định danh đơn hàng (VD: DH20261024-0001).
     */
    Optional<DonHang> findByMaDonHang(String maDonHang);

    /**
     * Lấy danh sách đơn hàng của một khách hàng, sắp xếp mới nhất lên đầu (US23).
     */
    List<DonHang> findByKhachHangIdOrderByNgayTaoDesc(Long khachHangId);

    /**
     * Lấy tất cả đơn hàng sắp xếp theo ngày tạo giảm dần.
     */
    List<DonHang> findAllByOrderByNgayTaoDesc();
}
