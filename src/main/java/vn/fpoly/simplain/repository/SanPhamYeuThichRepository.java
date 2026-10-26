/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - Repository thao tác dữ liệu bảng san_pham_yeu_thich
 * Cập nhật xác nhận commit US14: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.SanPhamYeuThich;

import java.util.List;
import java.util.Optional;

/**
 * Kho chứa dữ liệu sản phẩm yêu thích (Wishlist).
 */
@Repository
public interface SanPhamYeuThichRepository extends JpaRepository<SanPhamYeuThich, Long> {

    /**
     * Lấy danh sách sản phẩm yêu thích của một khách hàng, sắp xếp theo thời gian thêm mới nhất trước.
     */
    List<SanPhamYeuThich> findAllByMaKhachHangIdOrderByNgayThemDesc(Long maKhachHangId);

    /**
     * Tìm bản ghi yêu thích theo khách hàng và sản phẩm cụ thể.
     */
    Optional<SanPhamYeuThich> findByMaKhachHangIdAndMaSanPhamId(Long maKhachHangId, Long maSanPhamId);

    /**
     * Kiểm tra xem khách hàng đã thích sản phẩm này chưa.
     */
    boolean existsByMaKhachHangIdAndMaSanPhamId(Long maKhachHangId, Long maSanPhamId);

    /**
     * Xóa sản phẩm khỏi danh sách yêu thích của khách hàng.
     */
    void deleteByMaKhachHangIdAndMaSanPhamId(Long maKhachHangId, Long maSanPhamId);

    /**
     * Đếm tổng số sản phẩm yêu thích của khách hàng.
     */
    long countByMaKhachHangId(Long maKhachHangId);
}
