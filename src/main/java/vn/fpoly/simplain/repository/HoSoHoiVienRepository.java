package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.HoSoHoiVien;

import java.util.Optional;

/**
 * Kho dữ liệu Hồ sơ hội viên Loyalty / Coolcash (US16).
 */
@Repository
public interface HoSoHoiVienRepository extends JpaRepository<HoSoHoiVien, Long> {

    Optional<HoSoHoiVien> findByMaKhachHangId(Long maKhachHangId);
}
