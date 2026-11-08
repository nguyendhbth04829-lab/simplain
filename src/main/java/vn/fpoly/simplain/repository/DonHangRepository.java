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
     * Tính tổng doanh thu thực tế trong khoảng thời gian (Chỉ tính các đơn giao thành công HOAN_THANH hoặc DA_DOI_HANG).
     * Loại trừ các đơn chưa hoàn thành (CHO_XAC_NHAN, DANG_XU_LY, DANG_GIAO), đơn hủy (DA_HUY) và đơn đã hoàn tiền (DA_TRA_HANG).
     */
    @Query("SELECT COALESCE(SUM(d.tongThanhToan), 0) FROM DonHang d " +
           "WHERE d.ngayTao BETWEEN :batDau AND :ketThuc " +
           "AND d.trangThaiDonHang.maTrangThai IN ('HOAN_THANH', 'DA_DOI_HANG')")
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
     * Tính tổng doanh thu toàn hệ thống của các đơn đã giao thành công (HOAN_THANH hoặc DA_DOI_HANG).
     * Khi đơn hàng giao thành công (HOAN_THANH) -> Doanh thu được cộng.
     * Khi khách trả hàng hoàn tiền xong (DA_TRA_HANG) -> Doanh thu tự động giảm tương ứng.
     */
    @Query("SELECT COALESCE(SUM(d.tongThanhToan), 0) FROM DonHang d " +
           "WHERE d.trangThaiDonHang.maTrangThai IN ('HOAN_THANH', 'DA_DOI_HANG')")
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
           "(:maTrangThai IS NULL OR :maTrangThai = '' OR d.trangThaiDonHang.maTrangThai = :maTrangThai) AND " +
           "(:loaiDonHang IS NULL OR :loaiDonHang = '' OR d.loaiDonHang = :loaiDonHang) " +
           "ORDER BY d.id DESC")
    org.springframework.data.domain.Page<DonHang> timKiemPhanTrang(
            @Param("tuKhoa") String tuKhoa,
            @Param("maTrangThai") String maTrangThai,
            @Param("loaiDonHang") String loaiDonHang,
            org.springframework.data.domain.Pageable pageable);

    default org.springframework.data.domain.Page<DonHang> timKiemPhanTrang(
            String tuKhoa,
            String maTrangThai,
            org.springframework.data.domain.Pageable pageable) {
        return timKiemPhanTrang(tuKhoa, maTrangThai, null, pageable);
    }

    long countByLoaiDonHang(String loaiDonHang);

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
           "AND (:maTrangThai IS NULL OR :maTrangThai = '' OR :maTrangThai = 'TAT_CA' OR d.trangThaiDonHang.maTrangThai = :maTrangThai OR (:maTrangThai = 'DANG_GIAO' AND d.trangThaiDonHang.maTrangThai = 'THAT_LAC_MOT_PHAN') OR (:maTrangThai = 'DA_HUY' AND d.trangThaiDonHang.maTrangThai = 'HANG_THAT_LAC')) " +
           "AND (:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           "     LOWER(d.maDonHang) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "     LOWER(d.tenNguoiNhan) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "     d.soDienThoaiNhan LIKE CONCAT('%', :tuKhoa, '%') OR " +
           "     LOWER(d.maVanDon) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "ORDER BY d.id DESC, d.ngayTao DESC")
    org.springframework.data.domain.Page<DonHang> timKiemDonHangCuaKhachHang(
            @Param("khachHangId") Long khachHangId,
            @Param("soDienThoai") String soDienThoai,
            @Param("maTrangThai") String maTrangThai,
            @Param("tuKhoa") String tuKhoa,
            org.springframework.data.domain.Pageable pageable);

    /**
     * Tìm kiếm và phân trang đơn hàng của khách hàng nâng cao với lọc giá và phương thức thanh toán.
     */
    @Query("SELECT d FROM DonHang d WHERE " +
           "((:khachHangId IS NOT NULL AND d.khachHang.id = :khachHangId) OR " +
           " (:soDienThoai IS NOT NULL AND :soDienThoai <> '' AND d.khachHang IS NULL AND d.soDienThoaiNhan = :soDienThoai)) " +
           "AND (:maTrangThai IS NULL OR :maTrangThai = '' OR :maTrangThai = 'TAT_CA' OR d.trangThaiDonHang.maTrangThai = :maTrangThai OR (:maTrangThai = 'DANG_GIAO' AND d.trangThaiDonHang.maTrangThai = 'THAT_LAC_MOT_PHAN') OR (:maTrangThai = 'DA_HUY' AND d.trangThaiDonHang.maTrangThai = 'HANG_THAT_LAC')) " +
           "AND (:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           "     LOWER(d.maDonHang) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "     LOWER(d.tenNguoiNhan) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "     d.soDienThoaiNhan LIKE CONCAT('%', :tuKhoa, '%') OR " +
           "     LOWER(d.maVanDon) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "AND (:minGia IS NULL OR d.tongThanhToan >= :minGia) " +
           "AND (:maxGia IS NULL OR d.tongThanhToan <= :maxGia) " +
           "AND (:maPhuongThuc IS NULL OR :maPhuongThuc = '' OR (d.phuongThucThanhToan IS NOT NULL AND d.phuongThucThanhToan.maPhuongThuc = :maPhuongThuc)) " +
           "ORDER BY d.id DESC, d.ngayTao DESC")
    org.springframework.data.domain.Page<DonHang> timKiemDonHangCuaKhachHangNangCao(
            @Param("khachHangId") Long khachHangId,
            @Param("soDienThoai") String soDienThoai,
            @Param("maTrangThai") String maTrangThai,
            @Param("tuKhoa") String tuKhoa,
            @Param("minGia") BigDecimal minGia,
            @Param("maxGia") BigDecimal maxGia,
            @Param("maPhuongThuc") String maPhuongThuc,
            org.springframework.data.domain.Pageable pageable);

    /**
     * Đếm số đơn hàng của khách hàng theo mã trạng thái (US23).
     */
    @Query("SELECT COUNT(d) FROM DonHang d WHERE " +
           "((:khachHangId IS NOT NULL AND d.khachHang.id = :khachHangId) OR " +
           " (:soDienThoai IS NOT NULL AND :soDienThoai <> '' AND d.khachHang IS NULL AND d.soDienThoaiNhan = :soDienThoai)) " +
           "AND (:maTrangThai IS NULL OR :maTrangThai = '' OR :maTrangThai = 'TAT_CA' OR d.trangThaiDonHang.maTrangThai = :maTrangThai OR (:maTrangThai = 'DANG_GIAO' AND d.trangThaiDonHang.maTrangThai = 'THAT_LAC_MOT_PHAN') OR (:maTrangThai = 'DA_HUY' AND d.trangThaiDonHang.maTrangThai = 'HANG_THAT_LAC'))")
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

    /**
     * Đếm tổng số lượng đơn hàng của một khách hàng theo ID.
     */
    long countByKhachHangId(Long khachHangId);

    /**
     * Lấy 5 đơn hàng mới nhất của khách hàng theo ID.
     */
    java.util.List<DonHang> findTop5ByKhachHangIdOrderByIdDesc(Long khachHangId);

    /**
     * Thống kê tỷ trọng doanh thu và số lượng đơn hàng theo phương thức thanh toán.
     */
    @Query("SELECT COALESCE(p.tenPhuongThuc, 'Khác'), COUNT(d), COALESCE(SUM(d.tongThanhToan), 0) " +
           "FROM DonHang d LEFT JOIN d.phuongThucThanhToan p " +
           "WHERE d.ngayTao BETWEEN :batDau AND :ketThuc " +
           "  AND d.trangThaiDonHang.maTrangThai IN ('HOAN_THANH', 'DA_DOI_HANG') " +
           "GROUP BY p.tenPhuongThuc " +
           "ORDER BY SUM(d.tongThanhToan) DESC")
    java.util.List<Object[]> thongKeTheoPhuongThucThanhToan(@Param("batDau") java.time.LocalDateTime batDau,
                                                           @Param("ketThuc") java.time.LocalDateTime ketThuc);

    /**
     * Đếm số đơn hàng COD đang xử lý hoặc đang giao của khách hàng hoặc số điện thoại (Chống spam gom đơn COD).
     */
    @Query("SELECT COUNT(d) FROM DonHang d WHERE " +
           "((:khachHangId IS NOT NULL AND d.khachHang.id = :khachHangId) OR " +
           " (:soDienThoai IS NOT NULL AND :soDienThoai <> '' AND d.soDienThoaiNhan = :soDienThoai)) " +
           "AND d.trangThaiDonHang.maTrangThai IN ('CHO_XAC_NHAN', 'DANG_XU_LY', 'DANG_GIAO') " +
           "AND (d.phuongThucThanhToan IS NOT NULL AND (UPPER(d.phuongThucThanhToan.maPhuongThuc) = 'COD' OR d.phuongThucThanhToan.laTrucTuyen = false))")
    long demDonHangCodDangXuLy(@Param("khachHangId") Long khachHangId, @Param("soDienThoai") String soDienThoai);

    /**
     * Đếm số đơn hàng COD đã từng bị giao thất bại / từ chối nhận / bom hàng trong quá khứ (Anti-Boom / Anti-Fraud).
     */
    @Query("SELECT COUNT(DISTINCT d) FROM DonHang d " +
           "WHERE ((:khachHangId IS NOT NULL AND d.khachHang.id = :khachHangId) OR " +
           "       (:soDienThoai IS NOT NULL AND :soDienThoai <> '' AND d.soDienThoaiNhan = :soDienThoai)) " +
           "AND d.trangThaiDonHang.maTrangThai = 'DA_HUY' " +
           "AND (d.phuongThucThanhToan IS NOT NULL AND (UPPER(d.phuongThucThanhToan.maPhuongThuc) = 'COD' OR d.phuongThucThanhToan.laTrucTuyen = false)) " +
           "AND (EXISTS (SELECT 1 FROM LichSuTrangThaiDonHang ls WHERE ls.donHang.id = d.id AND ls.trangThaiMoi.maTrangThai = 'DANG_GIAO') " +
           "     OR (d.ghiChu IS NOT NULL AND (LOWER(d.ghiChu) LIKE '%bom%' OR LOWER(d.ghiChu) LIKE '%từ chối%' OR LOWER(d.ghiChu) LIKE '%không nhận%' OR LOWER(d.ghiChu) LIKE '%không nghe máy%')))")
    long demDonHangCodGiaoThatBai(@Param("khachHangId") Long khachHangId, @Param("soDienThoai") String soDienThoai);

    /**
     * Lấy danh sách các đơn hàng có phát sinh bồi thường từ ĐVVC (US35).
     */
    @Query("SELECT d FROM DonHang d WHERE d.trangThaiBoiThuongDvvc IS NOT NULL " +
           "OR d.trangThaiDonHang.maTrangThai IN ('HANG_THAT_LAC', 'THAT_LAC_MOT_PHAN') " +
           "ORDER BY d.id DESC")
    List<DonHang> layTatCaDonHangBoiThuongDvvc();

    /**
     * Tìm kiếm và lọc danh sách đơn hàng bồi thường từ ĐVVC theo trạng thái, hãng và từ khóa (US35).
     */
    @Query("SELECT d FROM DonHang d WHERE " +
           "(d.trangThaiBoiThuongDvvc IS NOT NULL OR d.trangThaiDonHang.maTrangThai IN ('HANG_THAT_LAC', 'THAT_LAC_MOT_PHAN')) " +
           "AND (:trangThaiBoiThuong IS NULL OR :trangThaiBoiThuong = '' OR d.trangThaiBoiThuongDvvc = :trangThaiBoiThuong) " +
           "AND (:donViVanChuyen IS NULL OR :donViVanChuyen = '' OR LOWER(d.donViVanChuyen) LIKE LOWER(CONCAT('%', :donViVanChuyen, '%'))) " +
           "AND (:tuKhoa IS NULL OR :tuKhoa = '' OR LOWER(d.maDonHang) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) " +
           "     OR (d.maVanDon IS NOT NULL AND LOWER(d.maVanDon) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "     OR (d.maDoiSoatBoiThuong IS NOT NULL AND LOWER(d.maDoiSoatBoiThuong) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "     OR LOWER(d.tenNguoiNhan) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) " +
           "     OR d.soDienThoaiNhan LIKE CONCAT('%', :tuKhoa, '%')) " +
           "ORDER BY d.id DESC")
    List<DonHang> timKiemDonHangBoiThuongDvvc(
            @Param("trangThaiBoiThuong") String trangThaiBoiThuong,
            @Param("donViVanChuyen") String donViVanChuyen,
            @Param("tuKhoa") String tuKhoa);
}
