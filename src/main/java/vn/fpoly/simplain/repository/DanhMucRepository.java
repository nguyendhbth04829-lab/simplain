package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.DanhMuc;

import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Danh mục sản phẩm [danh_muc] hỗ trợ danh mục Cha / Con (US06).
 */
@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Long> {

    /**
     * Tìm kiếm danh mục theo tên (không phân biệt hoa thường).
     */
    Page<DanhMuc> findAllByTenDanhMucContainingIgnoreCase(String tenDanhMuc, Pageable pageable);

    /**
     * Tìm kiếm kết hợp theo từ khóa (tên hoặc slug), danh mục cha và trạng thái kích hoạt.
     */
    @Query("SELECT dm FROM DanhMuc dm WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(dm.tenDanhMuc) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(dm.duongDanSlug) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) AND " +
           "(:maDanhMucCha IS NULL OR dm.maDanhMucCha = :maDanhMucCha) AND " +
           "(:kichHoat IS NULL OR dm.kichHoat = :kichHoat)")
    Page<DanhMuc> timKiemPhanTrang(@Param("tuKhoa") String tuKhoa,
                                   @Param("maDanhMucCha") Long maDanhMucCha,
                                   @Param("kichHoat") Boolean kichHoat,
                                   Pageable pageable);

    /**
     * Tìm danh mục theo đường dẫn slug.
     */
    Optional<DanhMuc> findByDuongDanSlug(String duongDanSlug);

    /**
     * Kiểm tra đường dẫn slug đã tồn tại hay chưa.
     */
    boolean existsByDuongDanSlugIgnoreCase(String duongDanSlug);

    /**
     * Kiểm tra đường dẫn slug đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByDuongDanSlugIgnoreCaseAndIdNot(String duongDanSlug, Long id);

    /**
     * Kiểm tra tên danh mục đã tồn tại hay chưa.
     */
    boolean existsByTenDanhMucIgnoreCase(String tenDanhMuc);

    /**
     * Kiểm tra tên danh mục đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByTenDanhMucIgnoreCaseAndIdNot(String tenDanhMuc, Long id);

    /**
     * Kiểm tra danh mục có danh mục con trực thuộc hay không (Chặn xóa danh mục cha đang có con).
     */
    boolean existsByMaDanhMucCha(Long maDanhMucCha);

    /**
     * Đếm số lượng danh mục con trực thuộc của một danh mục cha.
     */
    long countByMaDanhMucCha(Long maDanhMucCha);

    /**
     * Lấy danh sách các danh mục con trực thuộc của một danh mục cha.
     */
    List<DanhMuc> findByMaDanhMucChaOrderByThuTuHienThiAsc(Long maDanhMucCha);

    /**
     * Lấy danh sách các danh mục gốc (không có danh mục cha).
     */
    List<DanhMuc> findByMaDanhMucChaIsNullOrderByThuTuHienThiAsc();
}
