package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.VideoSanPham;

import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Video sản phẩm [video_san_pham].
 * Mỗi sản phẩm có 1 video quảng cáo/review thực tế.
 */
@Repository
public interface VideoSanPhamRepository extends JpaRepository<VideoSanPham, Long> {

    Optional<VideoSanPham> findByMaSanPhamId(Long maSanPhamId);

    Optional<VideoSanPham> findByMaSanPhamIdAndKichHoatTrue(Long maSanPhamId);

    List<VideoSanPham> findByKichHoatTrue(Sort sort);

    boolean existsByMaSanPhamId(Long maSanPhamId);

    boolean existsByMaSanPhamIdAndIdNot(Long maSanPhamId, Long id);

    @Query("SELECT v FROM VideoSanPham v LEFT JOIN SanPham sp ON v.maSanPhamId = sp.id " +
           "WHERE (:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(v.tieuDe) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(v.moTa) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) AND " +
           "(:kichHoat IS NULL OR v.kichHoat = :kichHoat)")
    Page<VideoSanPham> timKiemPhanTrang(@Param("tuKhoa") String tuKhoa,
                                        @Param("kichHoat") Boolean kichHoat,
                                        Pageable pageable);
}
