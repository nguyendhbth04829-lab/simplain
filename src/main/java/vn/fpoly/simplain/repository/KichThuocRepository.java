package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.KichThuoc;

import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Kích thước sản phẩm [kich_thuoc] (US07).
 */
@Repository
public interface KichThuocRepository extends JpaRepository<KichThuoc, Long> {

    /**
     * Lấy danh sách kích thước đang kích hoạt.
     */
    java.util.List<KichThuoc> findByKichHoatTrueOrderByIdAsc();

    default java.util.List<KichThuoc> findByKichHoatTrueOrderByThuTuHienThiAsc() {
        return findByKichHoatTrueOrderByIdAsc();
    }

    /**
     * Tìm kiếm phân trang kích thước theo mã size (S, M, L, XL...) hoặc tên mô tả kích thước kèm lọc trạng thái.
     */
    @Query("SELECT kt FROM KichThuoc kt WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(kt.maKichThuoc) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(kt.tenKichThuoc) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) AND " +
           "(:kichHoat IS NULL OR kt.kichHoat = :kichHoat)")
    Page<KichThuoc> timKiemPhanTrang(@Param("tuKhoa") String tuKhoa, @Param("kichHoat") Boolean kichHoat, Pageable pageable);

    default Page<KichThuoc> timKiemPhanTrang(String tuKhoa, Pageable pageable) {
        return timKiemPhanTrang(tuKhoa, null, pageable);
    }

    /**
     * Tìm kích thước theo mã kích thước (không phân biệt hoa thường).
     */
    Optional<KichThuoc> findByMaKichThuocIgnoreCase(String maKichThuoc);

    /**
     * Kiểm tra mã kích thước đã tồn tại hay chưa.
     */
    boolean existsByMaKichThuocIgnoreCase(String maKichThuoc);

    /**
     * Kiểm tra mã kích thước đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByMaKichThuocIgnoreCaseAndIdNot(String maKichThuoc, Long id);

    /**
     * Kiểm tra tên kích thước đã tồn tại hay chưa.
     */
    boolean existsByTenKichThuocIgnoreCase(String tenKichThuoc);

    /**
     * Kiểm tra tên kích thước đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByTenKichThuocIgnoreCaseAndIdNot(String tenKichThuoc, Long id);
}
