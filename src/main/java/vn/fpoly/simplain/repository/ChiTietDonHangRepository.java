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
}
