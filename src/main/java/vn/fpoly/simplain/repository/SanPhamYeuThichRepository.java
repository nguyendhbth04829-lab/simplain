package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.SanPhamYeuThich;

import java.util.List;
import java.util.Optional;

/**
 * Repository truy xuất dữ liệu danh sách sản phẩm yêu thích (US14).
 */
@Repository
public interface SanPhamYeuThichRepository extends JpaRepository<SanPhamYeuThich, Long> {

    /**
     * Kiểm tra xem khách hàng đã thích sản phẩm này chưa.
     */
    boolean existsByMaKhachHangIdAndMaSanPhamId(Long maKhachHangId, Long maSanPhamId);

    /**
     * Lấy bản ghi yêu thích theo mã khách hàng và mã sản phẩm.
     */
    Optional<SanPhamYeuThich> findByMaKhachHangIdAndMaSanPhamId(Long maKhachHangId, Long maSanPhamId);

    /**
     * Lấy toàn bộ danh sách yêu thích của khách hàng, sắp xếp theo thời gian thêm mới nhất lên đầu.
     */
    List<SanPhamYeuThich> findByMaKhachHangIdOrderByNgayThemDesc(Long maKhachHangId);

    /**
     * Đếm tổng số lượng sản phẩm khách hàng đang yêu thích.
     */
    long countByMaKhachHangId(Long maKhachHangId);

    /**
     * Xóa sản phẩm khỏi danh sách yêu thích của khách hàng.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM SanPhamYeuThich y WHERE y.maKhachHangId = :khachHangId AND y.maSanPhamId = :sanPhamId")
    void xoaYeuThich(@Param("khachHangId") Long khachHangId, @Param("sanPhamId") Long sanPhamId);

    /**
     * Xóa toàn bộ sản phẩm yêu thích của khách hàng (dùng khi dọn dẹp hoặc xóa tài khoản).
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM SanPhamYeuThich y WHERE y.maKhachHangId = :khachHangId")
    void xoaTatCaTheoKhachHang(@Param("khachHangId") Long khachHangId);

    /**
     * Lấy danh sách ID các sản phẩm mà khách hàng đã thả tim.
     */
    @Query("SELECT y.maSanPhamId FROM SanPhamYeuThich y WHERE y.maKhachHangId = :khachHangId")
    List<Long> findMaSanPhamIdsByMaKhachHangId(@Param("khachHangId") Long khachHangId);
}
