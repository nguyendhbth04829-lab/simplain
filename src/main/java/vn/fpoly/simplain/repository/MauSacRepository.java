package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.MauSac;

import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Màu sắc sản phẩm [mau_sac] (US07).
 */
@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Long> {

    /**
     * Tìm kiếm phân trang màu sắc theo mã màu (Hex) hoặc tên màu.
     */
    @Query("SELECT ms FROM MauSac ms WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(ms.maMau) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(ms.tenMau) LIKE LOWER(CONCAT('%', :tuKhoa, '%')))")
    Page<MauSac> timKiemPhanTrang(@Param("tuKhoa") String tuKhoa, Pageable pageable);

    /**
     * Tìm màu sắc theo mã màu (không phân biệt hoa thường).
     */
    Optional<MauSac> findByMaMauIgnoreCase(String maMau);

    /**
     * Kiểm tra mã màu đã tồn tại hay chưa.
     */
    boolean existsByMaMauIgnoreCase(String maMau);

    /**
     * Kiểm tra mã màu đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByMaMauIgnoreCaseAndIdNot(String maMau, Long id);

    /**
     * Kiểm tra tên màu sắc đã tồn tại hay chưa.
     */
    boolean existsByTenMauIgnoreCase(String tenMau);

    /**
     * Kiểm tra tên màu sắc đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByTenMauIgnoreCaseAndIdNot(String tenMau, Long id);
}
