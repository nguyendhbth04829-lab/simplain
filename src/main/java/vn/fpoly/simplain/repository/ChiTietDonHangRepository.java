package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.ChiTietDonHang;

import java.util.List;

/**
 * Repository truy xuất dữ liệu chi tiết đơn hàng [chi_tiet_don_hang] (US15).
 */
@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {

    /**
     * Lấy danh sách các mặt hàng trong một đơn hàng.
     */
    List<ChiTietDonHang> findByMaDonHangId(Long maDonHangId);

    /**
     * Lấy trực tiếp mã biến thể ID theo chi tiết đơn hàng ID (Tránh LazyInitializationException).
     */
    @Query("SELECT c.maBienTheId FROM ChiTietDonHang c WHERE c.id = :id")
    Long findMaBienTheIdById(@Param("id") Long id);

    /**
     * Lấy trực tiếp mã SKU theo chi tiết đơn hàng ID (Tránh LazyInitializationException).
     */
    @Query("SELECT c.maSku FROM ChiTietDonHang c WHERE c.id = :id")
    String findMaSkuById(@Param("id") Long id);

    /**
     * Lấy trực tiếp tên sản phẩm theo chi tiết đơn hàng ID.
     */
    @Query("SELECT c.tenSanPham FROM ChiTietDonHang c WHERE c.id = :id")
    String findTenSanPhamById(@Param("id") Long id);

    /**
     * Lấy trực tiếp đơn giá theo chi tiết đơn hàng ID.
     */
    @Query("SELECT c.donGia FROM ChiTietDonHang c WHERE c.id = :id")
    java.math.BigDecimal findDonGiaById(@Param("id") Long id);

    /**
     * Lấy các chi tiết đơn hàng đã mua sản phẩm này thuộc đơn hàng đã hoàn thành (US15 - Tiêu chí 1).
     */
    @Query("SELECT ct FROM ChiTietDonHang ct " +
           "JOIN ct.donHang dh " +
           "JOIN dh.trangThaiDonHang tt " +
           "JOIN ct.bienThe bt " +
           "WHERE dh.khachHang.id = :khachHangId " +
           "  AND bt.maSanPhamId = :sanPhamId " +
           "  AND (tt.maTrangThai = 'HOAN_THANH' OR tt.id = 4)")
    List<ChiTietDonHang> findChiTietDonHangHoanThanhTheoSanPham(@Param("khachHangId") Long khachHangId,
                                                               @Param("sanPhamId") Long sanPhamId);

    /**
     * Thống kê danh sách Top sản phẩm mang lại doanh thu cao nhất trong khoảng thời gian.
     */
    @Query("SELECT c.tenSanPham, SUM(c.soLuong), SUM(c.thanhTien) " +
           "FROM ChiTietDonHang c JOIN c.donHang d " +
           "WHERE d.ngayTao BETWEEN :batDau AND :ketThuc " +
           "  AND d.trangThaiDonHang.maTrangThai IN ('HOAN_THANH', 'DA_DOI_HANG') " +
           "GROUP BY c.tenSanPham " +
           "ORDER BY SUM(c.thanhTien) DESC")
    List<Object[]> findTopSanPhamTheoDoanhThu(@Param("batDau") java.time.LocalDateTime batDau,
                                              @Param("ketThuc") java.time.LocalDateTime ketThuc,
                                              org.springframework.data.domain.Pageable pageable);
}
