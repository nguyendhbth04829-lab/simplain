package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.DanhGiaSanPham;

import java.util.List;
import java.util.Optional;

/**
 * Repository truy xuất dữ liệu đánh giá sản phẩm [danh_gia_san_pham] (US15).
 */
@Repository
public interface DanhGiaSanPhamRepository extends JpaRepository<DanhGiaSanPham, Long> {

    /**
     * Lấy toàn bộ đánh giá của một sản phẩm, sắp xếp mới nhất lên đầu.
     */
    List<DanhGiaSanPham> findByMaSanPhamIdOrderByNgayTaoDesc(Long maSanPhamId);

    /**
     * Lấy các đánh giá của một khách hàng cụ thể.
     */
    List<DanhGiaSanPham> findByMaKhachHangIdOrderByNgayTaoDesc(Long maKhachHangId);

    /**
     * Kiểm tra xem một chi tiết đơn hàng cụ thể đã được đánh giá hay chưa (US15 - Tiêu chí 1).
     */
    boolean existsByMaChiTietDonHangId(Long maChiTietDonHangId);

    /**
     * Kiểm tra xem khách hàng đã từng đánh giá sản phẩm này chưa.
     */
    boolean existsByMaKhachHangIdAndMaSanPhamId(Long maKhachHangId, Long maSanPhamId);

    /**
     * Lấy bản ghi đánh giá theo mã chi tiết đơn hàng.
     */
    Optional<DanhGiaSanPham> findByMaChiTietDonHangId(Long maChiTietDonHangId);

    /**
     * Đếm tổng số lượng đánh giá của một sản phẩm.
     */
    long countByMaSanPhamId(Long maSanPhamId);

    /**
     * Đếm số lượng đánh giá theo mức số sao (1, 2, 3, 4, 5 sao).
     */
    long countByMaSanPhamIdAndSoSao(Long maSanPhamId, Integer soSao);

    /**
     * Đếm số lượng đánh giá theo mức số sao trên toàn hệ thống.
     */
    long countBySoSao(Integer soSao);

    /**
     * Đếm số lượng đánh giá chưa được Admin phản hồi.
     */
    long countByPhanHoiAdminIsNull();

    /**
     * Đếm số lượng đánh giá đã được Admin phản hồi.
     */
    long countByPhanHoiAdminIsNotNull();

    /**
     * Tính điểm sao trung bình của sản phẩm (thang điểm 5.0).
     */
    @Query("SELECT AVG(CAST(d.soSao AS double)) FROM DanhGiaSanPham d WHERE d.maSanPhamId = :sanPhamId")
    Double tinhSaoTrungBinh(@Param("sanPhamId") Long sanPhamId);

    /**
     * Tính điểm sao trung bình trên toàn bộ đánh giá của cửa hàng.
     */
    @Query("SELECT AVG(CAST(d.soSao AS double)) FROM DanhGiaSanPham d")
    Double tinhSaoTrungBinhToanCuaHang();

    /**
     * Tìm kiếm và lọc danh sách đánh giá cho màn hình Quản trị viên (Admin Review Management).
     */
    @Query("SELECT d FROM DanhGiaSanPham d " +
           "LEFT JOIN d.khachHang kh " +
           "LEFT JOIN d.sanPham sp " +
           "WHERE (:tuKhoa IS NULL OR :tuKhoa = '' OR LOWER(kh.hoTen) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) " +
           "       OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) " +
           "       OR LOWER(CAST(d.noiDung AS string)) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "AND (:soSao IS NULL OR d.soSao = :soSao) " +
           "AND (:chuaPhanHoi IS NULL OR (:chuaPhanHoi = true AND d.phanHoiAdmin IS NULL) OR (:chuaPhanHoi = false AND d.phanHoiAdmin IS NOT NULL)) " +
           "ORDER BY d.ngayTao DESC")
    Page<DanhGiaSanPham> timKiemDanhGiaAdmin(@Param("tuKhoa") String tuKhoa,
                                             @Param("soSao") Integer soSao,
                                             @Param("chuaPhanHoi") Boolean chuaPhanHoi,
                                             Pageable pageable);
}
