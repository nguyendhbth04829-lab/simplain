package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.BaiVietBlog;

import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý dữ liệu Bài viết Blog (US30 - Mod 6 / Nội dung - Khoa).
 */
@Repository
public interface BaiVietBlogRepository extends JpaRepository<BaiVietBlog, Long> {

    Optional<BaiVietBlog> findByDuongDanSlug(String duongDanSlug);

    boolean existsByDuongDanSlugIgnoreCase(String duongDanSlug);

    boolean existsByDuongDanSlugIgnoreCaseAndIdNot(String duongDanSlug, Long id);

    @Query("SELECT b FROM BaiVietBlog b " +
           "WHERE (:tuKhoa IS NULL OR :tuKhoa = '' OR LOWER(b.tieuDe) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR LOWER(b.chuyenMuc) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "AND (:chuyenMuc IS NULL OR :chuyenMuc = '' OR b.chuyenMuc = :chuyenMuc) " +
           "ORDER BY b.ngayXuatBan DESC")
    Page<BaiVietBlog> timKiemBaiViet(@Param("tuKhoa") String tuKhoa,
                                     @Param("chuyenMuc") String chuyenMuc,
                                     Pageable pageable);

    @Query("SELECT DISTINCT b.chuyenMuc FROM BaiVietBlog b ORDER BY b.chuyenMuc ASC")
    List<String> layDanhSachChuyenMuc();
}
