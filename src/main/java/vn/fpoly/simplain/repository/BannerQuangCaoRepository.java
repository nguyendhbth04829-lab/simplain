package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.BannerQuangCao;

import java.util.List;

/**
 * Repository quản lý dữ liệu Banner Quảng Cáo (US30 - Mod 6 / Nội dung - Khoa).
 */
@Repository
public interface BannerQuangCaoRepository extends JpaRepository<BannerQuangCao, Long> {

    List<BannerQuangCao> findByViTriHienThiAndKichHoatTrueOrderByIdDesc(String viTriHienThi);

    @Query("SELECT b FROM BannerQuangCao b " +
           "WHERE (:tuKhoa IS NULL OR :tuKhoa = '' OR LOWER(b.tieuDe) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
           "AND (:viTri IS NULL OR :viTri = '' OR b.viTriHienThi = :viTri) " +
           "AND (:kichHoat IS NULL OR b.kichHoat = :kichHoat) " +
           "ORDER BY b.id DESC")
    Page<BannerQuangCao> timKiemBanner(@Param("tuKhoa") String tuKhoa,
                                       @Param("viTri") String viTri,
                                       @Param("kichHoat") Boolean kichHoat,
                                       Pageable pageable);
}
