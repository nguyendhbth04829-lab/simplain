package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.VaiTro;

import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Vai trò.
 */
@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, Long> {

    /**
     * Tìm vai trò theo mã (ví dụ: ADMIN, CSKH, BAN_HANG).
     *
     * @param maVaiTro mã vai trò
     * @return Vai trò tương ứng nếu có
     */
    Optional<VaiTro> findByMaVaiTro(String maVaiTro);

    /**
     * Kiểm tra mã vai trò đã tồn tại hay chưa.
     */
    boolean existsByMaVaiTro(String maVaiTro);

    /**
     * Kiểm tra mã vai trò đã tồn tại cho một ID khác hay không (dùng khi cập nhật).
     *
     * @param maVaiTro mã vai trò
     * @param id ID của vai trò đang sửa
     * @return true nếu đã có bản ghi khác sử dụng mã này
     */
    boolean existsByMaVaiTroAndIdNot(String maVaiTro, Long id);

    /**
     * Tìm kiếm vai trò theo tên hoặc mã có phân trang.
     *
     * @param ten từ khóa tìm kiếm tên
     * @param ma từ khóa tìm kiếm mã
     * @param pageable đối tượng phân trang
     * @return Trang kết quả danh sách vai trò
     */
    org.springframework.data.domain.Page<VaiTro> findByTenVaiTroContainingIgnoreCaseOrMaVaiTroContainingIgnoreCase(
            String ten, String ma, org.springframework.data.domain.Pageable pageable);
}
