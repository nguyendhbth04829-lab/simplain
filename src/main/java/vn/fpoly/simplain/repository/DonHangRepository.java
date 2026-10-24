package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.TrangThaiDonHang;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Kho dữ liệu thao tác với thực thể DonHang (Bảng 'don_hang').
 * Cung cấp các truy vấn thống kê phục vụ Biểu đồ Dashboard tổng quan (US05).
 */
@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {

    /**
     * Đếm số lượng đơn hàng phát sinh trong khoảng thời gian (ví dụ: ngày hôm nay).
     *
     * @param start Thời điểm bắt đầu
     * @param end Thời điểm kết thúc
     * @return Số lượng đơn hàng
     */
    long countByNgayTaoBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Tính tổng doanh thu thực nhận (tongThanhToan) của các đơn hàng trong khoảng thời gian.
     *
     * @param start Thời điểm bắt đầu
     * @param end Thời điểm kết thúc
     * @return Tổng doanh thu
     */
    @Query("SELECT COALESCE(SUM(d.tongThanhToan), 0) FROM DonHang d WHERE d.ngayTao >= :start AND d.ngayTao <= :end")
    BigDecimal sumDoanhThuBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Tính tổng doanh thu toàn bộ hệ thống từ trước đến nay.
     *
     * @return Tổng doanh thu tích lũy
     */
    @Query("SELECT COALESCE(SUM(d.tongThanhToan), 0) FROM DonHang d")
    BigDecimal sumTongDoanhThu();

    /**
     * Đếm số lượng đơn hàng theo trạng thái cụ thể.
     *
     * @param trangThai Thực thể trạng thái
     * @return Số lượng đơn hàng
     */
    long countByTrangThai(TrangThaiDonHang trangThai);

    /**
     * Lấy 5 đơn hàng mới nhất phát sinh trong hệ thống.
     *
     * @return Danh sách 5 đơn hàng mới nhất
     */
    List<DonHang> findTop5ByOrderByNgayTaoDesc();

    /**
     * Tìm tất cả đơn hàng trong khoảng thời gian sắp xếp theo ngày tạo giảm dần.
     */
    List<DonHang> findByNgayTaoBetweenOrderByNgayTaoDesc(LocalDateTime start, LocalDateTime end);
}
