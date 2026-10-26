/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - Repository cấu hình hạng thành viên
 */
package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.HangThanhVien;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface HangThanhVienRepository extends JpaRepository<HangThanhVien, Long> {

    Optional<HangThanhVien> findByTenHang(String tenHang);

    List<HangThanhVien> findAllByOrderByChiTieuNamToiThieuAsc();

    /**
     * Lấy hạng mặc định thấp nhất khi đăng ký mới.
     */
    Optional<HangThanhVien> findTopByOrderByChiTieuNamToiThieuAsc();

    /**
     * Tìm hạng cao nhất mà mức chi tiêu năm hiện tại đạt được.
     */
    Optional<HangThanhVien> findTopByChiTieuNamToiThieuLessThanEqualOrderByChiTieuNamToiThieuDesc(BigDecimal chiTieu);

    /**
     * Tìm hạng tiếp theo cần phấn đấu.
     */
    Optional<HangThanhVien> findFirstByChiTieuNamToiThieuGreaterThanOrderByChiTieuNamToiThieuAsc(BigDecimal chiTieu);
}
