package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.VaiTro;

import java.util.Optional;

/**
 * Kho dữ liệu thao tác với thực thể VaiTro (Bảng 'vai_tro').
 */
@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, Long> {

    /**
     * Tìm kiếm vai trò theo mã định danh duy nhất (ví dụ: ADMIN, CSKH, BAN_HANG).
     *
     * @param maVaiTro Mã vai trò
     * @return Optional chứa thông tin vai trò nếu tìm thấy
     */
    Optional<VaiTro> findByMaVaiTro(String maVaiTro);
}
