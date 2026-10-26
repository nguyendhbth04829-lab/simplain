/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - Repository thao tác dữ liệu bảng san_pham
 * Cập nhật xác nhận commit US14: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.SanPham;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Kho chứa dữ liệu sản phẩm.
 */
@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

    Optional<SanPham> findByMaSanPham(String maSanPham);

    Optional<SanPham> findByDuongDanSlug(String duongDanSlug);

    List<SanPham> findByIdIn(Collection<Long> danhSachId);
}
