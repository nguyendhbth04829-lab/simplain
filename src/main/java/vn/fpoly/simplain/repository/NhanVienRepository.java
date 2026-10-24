package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.NhanVien;

import java.util.Optional;

/**
 * Kho dữ liệu thao tác với thực thể NhanVien (Bảng 'nhan_vien').
 * Hỗ trợ tra cứu nhân viên theo tên đăng nhập, email, số điện thoại phục vụ đăng nhập CMS (US02).
 */
@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {

    /**
     * Tìm nhân viên theo tên đăng nhập chính xác.
     *
     * @param tenDangNhap Tên đăng nhập
     * @return Optional chứa nhân viên
     */
    Optional<NhanVien> findByTenDangNhap(String tenDangNhap);

    /**
     * Tìm nhân viên theo email.
     *
     * @param email Địa chỉ email
     * @return Optional chứa nhân viên
     */
    Optional<NhanVien> findByEmail(String email);

    /**
     * Tìm nhân viên theo số điện thoại.
     *
     * @param soDienThoai Số điện thoại
     * @return Optional chứa nhân viên
     */
    Optional<NhanVien> findBySoDienThoai(String soDienThoai);

    /**
     * Tìm nhân viên theo mã nhân viên.
     *
     * @param maNhanVien Mã nhân viên (ví dụ: NV001)
     * @return Optional chứa nhân viên
     */
    Optional<NhanVien> findByMaNhanVien(String maNhanVien);

    /**
     * Tìm nhân viên linh hoạt theo tên đăng nhập, email hoặc số điện thoại.
     *
     * @param identifier Chuỗi định danh đăng nhập
     * @return Optional chứa nhân viên
     */
    @Query("SELECT nv FROM NhanVien nv WHERE nv.tenDangNhap = :identifier OR nv.email = :identifier OR nv.soDienThoai = :identifier")
    Optional<NhanVien> findByIdentifier(@Param("identifier") String identifier);
}
