package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.VoucherSoHuu;

import java.util.List;
import java.util.Optional;

/**
 * Repository truy vấn dữ liệu Voucher sở hữu của khách hàng [voucher_so_huu] (US27).
 */
@Repository
public interface VoucherSoHuuRepository extends JpaRepository<VoucherSoHuu, Long> {

    /**
     * Lấy danh sách voucher trong ví của một khách hàng (kèm nạp thông tin MaGiamGia).
     */
    @Query("""
        SELECT v FROM VoucherSoHuu v
        JOIN FETCH v.maGiamGia m
        WHERE v.maKhachHangId = :khachHangId
        ORDER BY v.ngayNhan DESC
    """)
    List<VoucherSoHuu> layDanhSachVoucherCuaKhachHang(@Param("khachHangId") Long khachHangId);

    /**
     * Lấy danh sách voucher khả dụng (chưa sử dụng) của khách hàng.
     */
    @Query("""
        SELECT v FROM VoucherSoHuu v
        JOIN FETCH v.maGiamGia m
        WHERE v.maKhachHangId = :khachHangId AND (v.daSuDung IS NULL OR v.daSuDung = false)
        ORDER BY v.ngayNhan DESC
    """)
    List<VoucherSoHuu> layDanhSachChuaSuDung(@Param("khachHangId") Long khachHangId);

    /**
     * Lấy danh sách voucher đã sử dụng của khách hàng.
     */
    @Query("""
        SELECT v FROM VoucherSoHuu v
        JOIN FETCH v.maGiamGia m
        WHERE v.maKhachHangId = :khachHangId AND v.daSuDung = true
        ORDER BY v.ngaySuDung DESC
    """)
    List<VoucherSoHuu> layDanhSachDaSuDung(@Param("khachHangId") Long khachHangId);

    /**
     * Kiểm tra khách hàng đã có voucher này trong ví mà chưa sử dụng hay chưa.
     */
    boolean existsByMaKhachHangIdAndMaGiamGiaIdAndDaSuDung(Long maKhachHangId, Long maGiamGiaId, Boolean daSuDung);

    /**
     * Đếm tổng số lần khách hàng đã nhận voucher này (cả đã dùng và chưa dùng).
     */
    long countByMaKhachHangIdAndMaGiamGiaId(Long maKhachHangId, Long maGiamGiaId);

    /**
     * Đếm số lần khách hàng đã dùng voucher này.
     */
    long countByMaKhachHangIdAndMaGiamGiaIdAndDaSuDungTrue(Long maKhachHangId, Long maGiamGiaId);

    /**
     * Đếm số voucher chưa sử dụng trong ví.
     */
    long countByMaKhachHangIdAndDaSuDungFalse(Long maKhachHangId);

    /**
     * Lấy bản ghi voucher chưa sử dụng đầu tiên để đánh dấu khi đặt hàng.
     */
    Optional<VoucherSoHuu> findFirstByMaKhachHangIdAndMaGiamGiaIdAndDaSuDungFalseOrderByNgayNhanAsc(Long maKhachHangId, Long maGiamGiaId);
}
