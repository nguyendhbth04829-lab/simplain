/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - Repository hồ sơ hội viên
 */
package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.HoSoHoiVien;

@Repository
public interface HoSoHoiVienRepository extends JpaRepository<HoSoHoiVien, Long> {
}
