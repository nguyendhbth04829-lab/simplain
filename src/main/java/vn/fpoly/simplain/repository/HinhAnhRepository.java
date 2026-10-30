package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.HinhAnh;

import java.util.List;

/**
 * Kho dữ liệu cho thực thể Hình ảnh sản phẩm & biến thể [hinh_anh] (US09).
 */
@Repository
public interface HinhAnhRepository extends JpaRepository<HinhAnh, Long> {

    /**
     * Lấy danh sách hình ảnh của một biến thể theo thứ tự ưu tiên (ảnh đại diện lên đầu).
     */
    List<HinhAnh> findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(Long maBienTheId);

    /**
     * Lấy danh sách hình ảnh của một sản phẩm gốc theo thứ tự ưu tiên.
     */
    List<HinhAnh> findByMaSanPhamIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(Long maSanPhamId);

    /**
     * Lấy danh sách hình ảnh cấp sản phẩm (không gắn với biến thể SKU) theo thứ tự ưu tiên (ảnh bìa đại diện lên đầu).
     */
    List<HinhAnh> findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(Long maSanPhamId);

    /**
     * Lấy danh sách hình ảnh cấp sản phẩm theo thứ tự hiển thị.
     */
    List<HinhAnh> findByMaSanPhamIdAndMaBienTheIdIsNullOrderByThuTuAscIdAsc(Long maSanPhamId);

    /**
     * Đếm số lượng hình ảnh cấp sản phẩm (không gắn với SKU).
     */
    long countByMaSanPhamIdAndMaBienTheIdIsNull(Long maSanPhamId);

    /**
     * Xóa tất cả hình ảnh cấp sản phẩm.
     */
    @org.springframework.transaction.annotation.Transactional
    void deleteByMaSanPhamIdAndMaBienTheIdIsNull(Long maSanPhamId);

    /**
     * Đếm số lượng hình ảnh gắn với một biến thể SKU.
     */
    long countByMaBienTheId(Long maBienTheId);

    /**
     * Xóa tất cả hình ảnh gắn trực tiếp với một biến thể SKU.
     */
    @org.springframework.transaction.annotation.Transactional
    void deleteByMaBienTheId(Long maBienTheId);
}
