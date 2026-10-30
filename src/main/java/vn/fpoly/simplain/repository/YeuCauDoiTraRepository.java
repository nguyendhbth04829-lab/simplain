package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.YeuCauDoiTra;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Yêu cầu Đổi/Trả (RMA) [yeu_cau_doi_tra] (US24).
 */
@Repository
public interface YeuCauDoiTraRepository extends JpaRepository<YeuCauDoiTra, Long> {

    /**
     * Tìm yêu cầu RMA theo mã yêu cầu duy nhất.
     */
    Optional<YeuCauDoiTra> findByMaYeuCauRma(String maYeuCauRma);

    /**
     * Kiểm tra mã RMA đã tồn tại hay chưa.
     */
    boolean existsByMaYeuCauRma(String maYeuCauRma);

    /**
     * Tìm yêu cầu RMA theo mã và ID khách hàng (Bảo mật chống IDOR).
     */
    Optional<YeuCauDoiTra> findByMaYeuCauRmaAndMaKhachHangId(String maYeuCauRma, Long maKhachHangId);

    /**
     * Lấy danh sách các yêu cầu RMA của một đơn hàng.
     */
    List<YeuCauDoiTra> findByMaDonHangIdOrderByIdDesc(Long maDonHangId);

    /**
     * Kiểm tra xem đơn hàng đã có yêu cầu RMA nào đang trong trạng thái xử lý chưa (tránh tạo trùng lặp).
     */
    boolean existsByMaDonHangIdAndTrangThaiIn(Long maDonHangId, Collection<String> trangThais);

    /**
     * Lấy danh sách phân trang các yêu cầu đổi trả của một khách hàng.
     */
    Page<YeuCauDoiTra> findByMaKhachHangIdOrderByNgayTaoDesc(Long maKhachHangId, Pageable pageable);

    /**
     * Đếm tổng số yêu cầu đổi trả của khách hàng.
     */
    long countByMaKhachHangId(Long maKhachHangId);

    /**
     * Tìm kiếm và phân trang yêu cầu RMA của khách hàng theo từ khóa và trạng thái.
     */
    @Query("SELECT r FROM YeuCauDoiTra r " +
           "WHERE r.maKhachHangId = :khachHangId " +
           "  AND (:trangThai IS NULL OR :trangThai = '' OR :trangThai = 'TAT_CA' OR r.trangThai = :trangThai) " +
           "  AND (:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           "       LOWER(r.maYeuCauRma) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "       LOWER(r.lyDo) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "ORDER BY r.ngayTao DESC")
    Page<YeuCauDoiTra> timKiemCuaKhachHang(@Param("khachHangId") Long khachHangId,
                                          @Param("trangThai") String trangThai,
                                          @Param("tuKhoa") String tuKhoa,
                                          Pageable pageable);

    /**
     * Tìm kiếm và phân trang tất cả yêu cầu Đổi/Trả cho CSKH & Admin (US25 - Tiêu chí 1).
     */
    @Query("SELECT r FROM YeuCauDoiTra r " +
           "LEFT JOIN r.donHang dh " +
           "LEFT JOIN r.khachHang kh " +
           "WHERE (:trangThai IS NULL OR :trangThai = '' OR :trangThai = 'TAT_CA' OR r.trangThai = :trangThai) " +
           "  AND (:loaiDoiTra IS NULL OR :loaiDoiTra = '' OR :loaiDoiTra = 'TAT_CA' OR r.loaiDoiTra = :loaiDoiTra) " +
           "  AND (:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           "       LOWER(r.maYeuCauRma) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "       LOWER(dh.maDonHang) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "       LOWER(kh.hoTen) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "       kh.soDienThoai LIKE CONCAT('%', :tuKhoa, '%') OR " +
           "       LOWER(r.lyDo) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "ORDER BY r.ngayTao DESC")
    Page<YeuCauDoiTra> timKiemPhanTrangChoAdmin(
            @Param("tuKhoa") String tuKhoa,
            @Param("trangThai") String trangThai,
            @Param("loaiDoiTra") String loaiDoiTra,
            Pageable pageable);

    /**
     * Đếm số lượng yêu cầu RMA theo trạng thái phục vụ hiển thị số lượng trên Tab (US25).
     */
    long countByTrangThai(String trangThai);
}
