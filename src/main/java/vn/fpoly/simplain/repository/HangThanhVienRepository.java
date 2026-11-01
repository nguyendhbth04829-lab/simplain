package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.HangThanhVien;

import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu Hạng thành viên Loyalty (US16).
 */
@Repository
public interface HangThanhVienRepository extends JpaRepository<HangThanhVien, Long> {

    List<HangThanhVien> findAllByOrderByChiTieuNamToiThieuAsc();

    List<HangThanhVien> findAllByOrderByChiTieuNamToiThieuDesc();

    Optional<HangThanhVien> findByTenHangIgnoreCase(String tenHang);

    Optional<HangThanhVien> findFirstByOrderByChiTieuNamToiThieuAsc();
}
