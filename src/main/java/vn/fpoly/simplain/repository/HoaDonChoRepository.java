package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.HoaDonCho;

import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý Hóa đơn chờ tại quầy [hoa_don_cho] (POS).
 */
@Repository
public interface HoaDonChoRepository extends JpaRepository<HoaDonCho, Long> {

    /**
     * Lấy danh sách các hóa đơn chờ đang phục vụ (sắp xếp tăng dần theo thời gian tạo).
     */
    List<HoaDonCho> findByTrangThaiOrderByNgayTaoAsc(String trangThai);

    /**
     * Tìm hóa đơn chờ theo mã hóa đơn (VD: HDC-001).
     */
    Optional<HoaDonCho> findByMaHoaDon(String maHoaDon);

    /**
     * Đếm số lượng hóa đơn chờ đang mở.
     */
    long countByTrangThai(String trangThai);
}
