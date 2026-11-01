package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.MaGiamGia;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository truy vấn dữ liệu Chương trình Mã giảm giá / Voucher [ma_giam_gia] (US26).
 */
@Repository
public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, Long> {

    /**
     * Kiểm tra mã code đã tồn tại chưa (không phân biệt hoa thường).
     */
    boolean existsByMaCodeIgnoreCase(String maCode);

    /**
     * Kiểm tra mã code đã tồn tại trên bản ghi khác chưa (dùng khi cập nhật).
     */
    boolean existsByMaCodeIgnoreCaseAndIdNot(String maCode, Long id);

    /**
     * Tìm voucher theo mã code.
     */
    Optional<MaGiamGia> findByMaCodeIgnoreCase(String maCode);

    /**
     * Tìm kiếm và phân trang mã giảm giá dành cho Admin với các tiêu chí lọc đa chiều.
     */
    @Query("""
        SELECT m FROM MaGiamGia m
        WHERE (:tuKhoa IS NULL OR :tuKhoa = '' OR LOWER(m.maCode) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR LOWER(m.tenVoucher) LIKE LOWER(CONCAT('%', :tuKhoa, '%')))
          AND (:loaiGiamGia IS NULL OR :loaiGiamGia = '' OR m.loaiGiamGia = :loaiGiamGia)
          AND (:kichHoat IS NULL OR m.kichHoat = :kichHoat)
        ORDER BY m.id DESC
    """)
    Page<MaGiamGia> timKiemPhanTrang(
            @Param("tuKhoa") String tuKhoa,
            @Param("loaiGiamGia") String loaiGiamGia,
            @Param("kichHoat") Boolean kichHoat,
            Pageable pageable
    );

    /**
     * Đếm số voucher đang hoạt động trong khung giờ.
     */
    long countByKichHoatTrueAndNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(
            LocalDateTime now1, LocalDateTime now2
    );

    /**
     * Đếm số voucher sắp diễn ra.
     */
    long countByKichHoatTrueAndNgayBatDauAfter(LocalDateTime now);

    /**
     * Đếm số voucher đã kết thúc / hết hạn.
     */
    long countByNgayKetThucBefore(LocalDateTime now);

    /**
     * Đếm số voucher đang bị tạm ngưng kích hoạt.
     */
    long countByKichHoatFalse();

    /**
     * Lấy danh sách các voucher đang kích hoạt và chưa hết hạn để hiển thị cho Khách hàng thu thập (US27).
     */
    @Query("""
        SELECT m FROM MaGiamGia m
        WHERE m.kichHoat = true
          AND m.ngayKetThuc >= :now
        ORDER BY m.id DESC
    """)
    java.util.List<MaGiamGia> layDanhSachVoucherKhaDungChoKhachHang(@Param("now") LocalDateTime now);
}
