package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.KhachHang;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Khách hàng.
 */
@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {

    /**
     * Tìm khách hàng theo email.
     */
    Optional<KhachHang> findByEmail(String email);

    /**
     * Tìm khách hàng theo số điện thoại.
     */
    Optional<KhachHang> findBySoDienThoai(String soDienThoai);

    /**
     * Tìm khách hàng theo mã khách hàng.
     */
    Optional<KhachHang> findByMaKhachHang(String maKhachHang);

    /**
     * Tìm khách hàng theo email hoặc số điện thoại (dùng cho đăng nhập linh hoạt).
     */
    Optional<KhachHang> findByEmailOrSoDienThoai(String email, String soDienThoai);

    /**
     * Kiểm tra xem email đã tồn tại hay chưa.
     */
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Kiểm tra xem số điện thoại đã tồn tại hay chưa.
     */
    boolean existsBySoDienThoai(String soDienThoai);

    boolean existsBySoDienThoaiAndIdNot(String soDienThoai, Long id);

    /**
     * Kiểm tra xem mã khách hàng đã tồn tại hay chưa.
     */
    boolean existsByMaKhachHang(String maKhachHang);

    /**
     * Đếm số lượng khách hàng theo trạng thái (1: Hoạt động, 0: Bị khóa).
     */
    long countByTrangThai(Integer trangThai);

    /**
     * Đếm số khách hàng đăng ký mới trong khoảng thời gian (US05).
     */
    long countByNgayTaoBetween(LocalDateTime batDau, LocalDateTime ketThuc);

    /**
     * Tìm kiếm nhanh khách hàng cho màn hình bán hàng tại quầy (POS) theo Tên, SĐT hoặc Mã KH.
     * Hỗ trợ tìm kiếm theo số điện thoại đã làm sạch ký tự (loại bỏ dấu cách, chấm, gạch ngang).
     */
    @org.springframework.data.jpa.repository.Query(
        "SELECT k FROM KhachHang k WHERE " +
        "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
        " LOWER(k.hoTen) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
        " LOWER(k.soDienThoai) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
        " (:sdtDigits IS NOT NULL AND :sdtDigits <> '' AND LOWER(k.soDienThoai) LIKE LOWER(CONCAT('%', :sdtDigits, '%'))) OR " +
        " LOWER(k.maKhachHang) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
        "ORDER BY k.id DESC"
    )
    java.util.List<KhachHang> timKiemKhachHangChoPos(
            @org.springframework.data.repository.query.Param("tuKhoa") String tuKhoa,
            @org.springframework.data.repository.query.Param("sdtDigits") String sdtDigits,
            org.springframework.data.domain.Pageable pageable);
}
