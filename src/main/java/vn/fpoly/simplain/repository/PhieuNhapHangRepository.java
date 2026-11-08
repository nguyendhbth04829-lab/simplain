package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.PhieuNhapHang;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuNhapHangRepository extends JpaRepository<PhieuNhapHang, Long> {

    Optional<PhieuNhapHang> findByMaPhieuNhap(String maPhieuNhap);

    boolean existsByMaPhieuNhap(String maPhieuNhap);

    @Query("SELECT p FROM PhieuNhapHang p WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR LOWER(p.maPhieuNhap) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR LOWER(p.ghiChu) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) AND " +
           "(:maNhaCungCapId IS NULL OR p.maNhaCungCapId = :maNhaCungCapId) AND " +
           "(:trangThai IS NULL OR :trangThai = '' OR p.trangThai = :trangThai) AND " +
           "(:tuNgay IS NULL OR p.ngayNhap >= :tuNgay) AND " +
           "(:denNgay IS NULL OR p.ngayNhap <= :denNgay) " +
           "ORDER BY p.id DESC")
    Page<PhieuNhapHang> timKiemPhanTrang(
            @Param("tuKhoa") String tuKhoa,
            @Param("maNhaCungCapId") Long maNhaCungCapId,
            @Param("trangThai") String trangThai,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable);

    List<PhieuNhapHang> findTop10ByOrderByNgayNhapDesc();

    long countByTrangThai(String trangThai);

    @Query("SELECT COALESCE(SUM(p.tongTien), 0) FROM PhieuNhapHang p WHERE p.trangThai = 'DA_NHAP'")
    java.math.BigDecimal tinhTongTienNhapKhoHoanTat();

    @Query("SELECT COALESCE(SUM(p.tongSoLuong), 0) FROM PhieuNhapHang p WHERE p.trangThai = 'DA_NHAP'")
    Long tinhTongSoLuongNhapKhoHoanTat();
}
