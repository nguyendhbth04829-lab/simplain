package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.BienTheSanPham;

import java.util.Optional;

/**
 * Repository thao tác dữ liệu bảng 'bien_the_san_pham' (SKU & tồn kho).
 */
@Repository
public interface BienTheSanPhamRepository extends JpaRepository<BienTheSanPham, Long> {

    /**
     * Tìm biến thể theo mã SKU.
     */
    Optional<BienTheSanPham> findByMaSku(String maSku);
}
