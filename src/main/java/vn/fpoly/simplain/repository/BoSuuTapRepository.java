package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.BoSuuTap;

import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Bộ sưu tập thời trang [bo_suu_tap] kèm Banner (US06).
 */
@Repository
public interface BoSuuTapRepository extends JpaRepository<BoSuuTap, Long> {

    /**
     * Tìm kiếm bộ sưu tập theo tên.
     */
    Page<BoSuuTap> findAllByTenBoSuuTapContainingIgnoreCase(String tenBoSuuTap, Pageable pageable);

    /**
     * Tìm kiếm kết hợp theo từ khóa (tên hoặc slug) và trạng thái kích hoạt.
     */
    @Query("SELECT bst FROM BoSuuTap bst WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(bst.tenBoSuuTap) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(bst.duongDanSlug) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) AND " +
           "(:kichHoat IS NULL OR bst.kichHoat = :kichHoat)")
    Page<BoSuuTap> timKiemPhanTrang(@Param("tuKhoa") String tuKhoa,
                                    @Param("kichHoat") Boolean kichHoat,
                                    Pageable pageable);

    /**
     * Tìm bộ sưu tập theo đường dẫn slug.
     */
    Optional<BoSuuTap> findByDuongDanSlug(String duongDanSlug);

    /**
     * Kiểm tra đường dẫn slug đã tồn tại hay chưa.
     */
    boolean existsByDuongDanSlugIgnoreCase(String duongDanSlug);

    /**
     * Kiểm tra đường dẫn slug đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByDuongDanSlugIgnoreCaseAndIdNot(String duongDanSlug, Long id);

    /**
     * Kiểm tra tên bộ sưu tập đã tồn tại hay chưa.
     */
    boolean existsByTenBoSuuTapIgnoreCase(String tenBoSuuTap);

    /**
     * Kiểm tra tên bộ sưu tập đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByTenBoSuuTapIgnoreCaseAndIdNot(String tenBoSuuTap, Long id);
}
