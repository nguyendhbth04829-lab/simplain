package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.GiaoDichThanhToan;

import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý dữ liệu Giao dịch thanh toán [giao_dich_thanh_toan] (US20).
 * Tiêu chí 3: Lưu giao_dich_thanh_toan.
 */
@Repository
public interface GiaoDichThanhToanRepository extends JpaRepository<GiaoDichThanhToan, Long> {

    Optional<GiaoDichThanhToan> findByMaGiaoDichNoiBo(String maGiaoDichNoiBo);

    Optional<GiaoDichThanhToan> findByMaGiaoDichCong(String maGiaoDichCong);

    List<GiaoDichThanhToan> findByDonHangIdOrderByIdDesc(Long donHangId);

    List<GiaoDichThanhToan> findByDonHangMaDonHangOrderByIdDesc(String maDonHang);

    Optional<GiaoDichThanhToan> findTopByDonHangIdOrderByIdDesc(Long donHangId);

    Optional<GiaoDichThanhToan> findTopByDonHangMaDonHangOrderByIdDesc(String maDonHang);

    boolean existsByMaGiaoDichNoiBo(String maGiaoDichNoiBo);

    long countByTrangThai(String trangThai);

    @Query("SELECT g FROM GiaoDichThanhToan g " +
           "WHERE (:tuKhoa IS NULL OR :tuKhoa = '' " +
           "       OR LOWER(g.maGiaoDichNoiBo) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) " +
           "       OR LOWER(g.maGiaoDichCong) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) " +
           "       OR LOWER(g.donHang.maDonHang) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) " +
           "       OR LOWER(g.donHang.tenNguoiNhan) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) " +
           "       OR LOWER(g.donHang.soDienThoaiNhan) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "  AND (:trangThai IS NULL OR :trangThai = '' OR g.trangThai = :trangThai) " +
           "  AND (:maPhuongThucId IS NULL OR g.phuongThucThanhToan.id = :maPhuongThucId) " +
           "ORDER BY g.id DESC")
    Page<GiaoDichThanhToan> timKiemPhanTrang(
            @Param("tuKhoa") String tuKhoa,
            @Param("trangThai") String trangThai,
            @Param("maPhuongThucId") Long maPhuongThucId,
            Pageable pageable
    );
}
