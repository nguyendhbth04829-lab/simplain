package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.DonHang;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Đơn hàng [don_hang] phục vụ thống kê Dashboard tổng quan (US05).
 */
@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {

    /**
     * Tìm đơn hàng theo mã đơn hàng.
     */
    Optional<DonHang> findByMaDonHang(String maDonHang);

    /**
     * Đếm tổng số đơn hàng phát sinh trong khoảng thời gian (VD: Đơn hàng hôm nay).
     */
    long countByNgayTaoBetween(LocalDateTime batDau, LocalDateTime ketThuc);

    /**
     * Đếm số đơn hàng theo mã trạng thái (CHO_XAC_NHAN, DANG_XU_LY, DANG_GIAO, HOAN_THANH, DA_HUY).
     */
    @Query("SELECT COUNT(d) FROM DonHang d WHERE d.trangThaiDonHang.maTrangThai = :maTrangThai")
    long demTheoMaTrangThai(@Param("maTrangThai") String maTrangThai);

    /**
     * Đếm số đơn hàng theo mã trạng thái trong khoảng thời gian cụ thể.
     */
    @Query("SELECT COUNT(d) FROM DonHang d WHERE d.trangThaiDonHang.maTrangThai = :maTrangThai " +
           "AND d.ngayTao BETWEEN :batDau AND :ketThuc")
    long demTheoMaTrangThaiTrongKhoang(@Param("maTrangThai") String maTrangThai,
                                       @Param("batDau") LocalDateTime batDau,
                                       @Param("ketThuc") LocalDateTime ketThuc);

    /**
     * Tính tổng doanh thu trong khoảng thời gian (Loại trừ các đơn hàng đã hủy DA_HUY).
     */
    @Query("SELECT COALESCE(SUM(d.tongThanhToan), 0) FROM DonHang d " +
           "WHERE d.ngayTao BETWEEN :batDau AND :ketThuc " +
           "AND d.trangThaiDonHang.maTrangThai <> 'DA_HUY'")
    BigDecimal tinhDoanhThuTrongKhoang(@Param("batDau") LocalDateTime batDau,
                                       @Param("ketThuc") LocalDateTime ketThuc);

    /**
     * Tính tổng doanh thu thực tế của các đơn đã hoàn thành (HOAN_THANH) trong khoảng thời gian.
     */
    @Query("SELECT COALESCE(SUM(d.tongThanhToan), 0) FROM DonHang d " +
           "WHERE d.ngayTao BETWEEN :batDau AND :ketThuc " +
           "AND d.trangThaiDonHang.maTrangThai = 'HOAN_THANH'")
    BigDecimal tinhDoanhThuHoanThanhTrongKhoang(@Param("batDau") LocalDateTime batDau,
                                                @Param("ketThuc") LocalDateTime ketThuc);

    /**
     * Tính tổng doanh thu toàn hệ thống (Loại trừ đơn đã hủy DA_HUY).
     */
    @Query("SELECT COALESCE(SUM(d.tongThanhToan), 0) FROM DonHang d " +
           "WHERE d.trangThaiDonHang.maTrangThai <> 'DA_HUY'")
    BigDecimal tinhTongDoanhThuToanHeThong();

    /**
     * Lấy danh sách đơn hàng trong khoảng thời gian để tổng hợp biểu đồ theo ngày/giờ.
     */
    List<DonHang> findByNgayTaoBetweenOrderByNgayTaoAsc(LocalDateTime batDau, LocalDateTime ketThuc);

    /**
     * Đếm số đơn hàng hợp lệ mà khách hàng đã áp dụng mã giảm giá này (US27).
     * Loại trừ đơn đã hủy để khách hàng có thể dùng lại voucher nếu đơn cũ bị hủy.
     */
    @Query("SELECT COUNT(d) FROM DonHang d WHERE d.khachHang.id = :khachHangId AND d.maGiamGiaId = :maGiamGiaId AND (d.trangThaiDonHang IS NULL OR d.trangThaiDonHang.maTrangThai <> 'DA_HUY')")
    long demSoDonApDungVoucherCuaKhachHang(@Param("khachHangId") Long khachHangId, @Param("maGiamGiaId") Long maGiamGiaId);

    /**
     * Tìm kiếm phân trang đơn hàng theo từ khóa và trạng thái (US21).
     */
    @Query("SELECT d FROM DonHang d WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(d.maDonHang) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(d.tenNguoiNhan) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(d.soDienThoaiNhan) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) AND " +
           "(:maTrangThai IS NULL OR :maTrangThai = '' OR d.trangThaiDonHang.maTrangThai = :maTrangThai) " +
           "ORDER BY d.id DESC")
    org.springframework.data.domain.Page<DonHang> timKiemPhanTrang(
            @Param("tuKhoa") String tuKhoa,
            @Param("maTrangThai") String maTrangThai,
            org.springframework.data.domain.Pageable pageable);

    /**
     * Lấy 6 đơn hàng mới nhất để hiển thị bảng theo dõi nhanh trên Dashboard.
     */
    List<DonHang> findTop6ByOrderByNgayTaoDesc();

    /**
     * Tìm kiếm và phân trang đơn hàng của khách hàng (US23).
     * Hỗ trợ tìm kiếm theo cả mã khách hàng liên kết hoặc số điện thoại nhận hàng.
     */
    @Query("SELECT d FROM DonHang d WHERE " +
           "((:khachHangId IS NOT NULL AND d.khachHang.id = :khachHangId) OR " +
           " (:soDienThoai IS NOT NULL AND :soDienThoai <> '' AND d.khachHang IS NULL AND d.soDienThoaiNhan = :soDienThoai)) " +
           "AND (:maTrangThai IS NULL OR :maTrangThai = '' OR :maTrangThai = 'TAT_CA' OR d.trangThaiDonHang.maTrangThai = :maTrangThai) " +
           "AND (:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           "     LOWER(d.maDonHang) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "     LOWER(d.tenNguoiNhan) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "     d.soDienThoaiNhan LIKE CONCAT('%', :tuKhoa, '%') OR " +
           "     LOWER(d.maVanDon) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "ORDER BY d.ngayTao DESC")
    org.springframework.data.domain.Page<DonHang> timKiemDonHangCuaKhachHang(
            @Param("khachHangId") Long khachHangId,
            @Param("soDienThoai") String soDienThoai,
            @Param("maTrangThai") String maTrangThai,
            @Param("tuKhoa") String tuKhoa,
            org.springframework.data.domain.Pageable pageable);

    /**
     * Đếm số đơn hàng của khách hàng theo mã trạng thái (US23).
     */
    @Query("SELECT COUNT(d) FROM DonHang d WHERE " +
           "((:khachHangId IS NOT NULL AND d.khachHang.id = :khachHangId) OR " +
           " (:soDienThoai IS NOT NULL AND :soDienThoai <> '' AND d.khachHang IS NULL AND d.soDienThoaiNhan = :soDienThoai)) " +
           "AND (:maTrangThai IS NULL OR :maTrangThai = '' OR :maTrangThai = 'TAT_CA' OR d.trangThaiDonHang.maTrangThai = :maTrangThai)")
    long demDonHangCuaKhachHangTheoTrangThai(
            @Param("khachHangId") Long khachHangId,
            @Param("soDienThoai") String soDienThoai,
            @Param("maTrangThai") String maTrangThai);

    /**
     * Tìm đơn hàng theo mã đơn và xác thực quyền sở hữu của khách hàng (US23 - Bảo mật chống IDOR).
     */
    @Query("SELECT d FROM DonHang d WHERE d.maDonHang = :maDonHang " +
           "AND ((:khachHangId IS NOT NULL AND d.khachHang.id = :khachHangId) OR " +
           "     (:soDienThoai IS NOT NULL AND :soDienThoai <> '' AND d.khachHang IS NULL AND d.soDienThoaiNhan = :soDienThoai))")
    Optional<DonHang> timTheoMaDonVaKhachHang(
            @Param("maDonHang") String maDonHang,
            @Param("khachHangId") Long khachHangId,
            @Param("soDienThoai") String soDienThoai);
}
