package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.ChiTietHoaDonCho;

import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý Mặt hàng trong Hóa đơn chờ [chi_tiet_hoa_don_cho] (POS).
 */
@Repository
public interface ChiTietHoaDonChoRepository extends JpaRepository<ChiTietHoaDonCho, Long> {

    /**
     * Lấy danh sách mặt hàng thuộc một hóa đơn chờ.
     */
    List<ChiTietHoaDonCho> findByMaHoaDonChoIdOrderByIdAsc(Long maHoaDonChoId);

    /**
     * Tìm mặt hàng theo hóa đơn chờ và biến thể sản phẩm (kiểm tra đã có trong tab chưa).
     */
    Optional<ChiTietHoaDonCho> findByMaHoaDonChoIdAndMaBienTheId(Long maHoaDonChoId, Long maBienTheId);

    /**
     * Xóa toàn bộ mặt hàng thuộc một hóa đơn chờ.
     */
    void deleteByMaHoaDonChoId(Long maHoaDonChoId);
}
