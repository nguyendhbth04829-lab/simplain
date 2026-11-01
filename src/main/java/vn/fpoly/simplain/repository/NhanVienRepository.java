package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.NhanVien;

import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Nhân viên (US02, US03, US04).
 */
@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {

    /**
     * Tìm nhân viên theo tên đăng nhập.
     *
     * @param tenDangNhap tên đăng nhập hệ thống
     * @return Nhân viên tương ứng nếu có
     */
    Optional<NhanVien> findByTenDangNhap(String tenDangNhap);

    Optional<NhanVien> findByTenDangNhapIgnoreCase(String tenDangNhap);

    /**
     * Tìm nhân viên theo mã nhân viên.
     *
     * @param maNhanVien mã định danh nhân viên
     * @return Nhân viên tương ứng nếu có
     */
    Optional<NhanVien> findByMaNhanVien(String maNhanVien);

    Optional<NhanVien> findByMaNhanVienIgnoreCase(String maNhanVien);

    /**
     * Tìm nhân viên theo email.
     */
    Optional<NhanVien> findByEmailIgnoreCase(String email);

    /**
     * Kiểm tra tên đăng nhập đã tồn tại chưa.
     */
    boolean existsByTenDangNhap(String tenDangNhap);

    /**
     * Kiểm tra tên đăng nhập đã tồn tại chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByTenDangNhapAndIdNot(String tenDangNhap, Long id);

    /**
     * Kiểm tra mã nhân viên đã tồn tại chưa.
     */
    boolean existsByMaNhanVien(String maNhanVien);

    /**
     * Kiểm tra mã nhân viên đã tồn tại chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByMaNhanVienAndIdNot(String maNhanVien, Long id);

    /**
     * Kiểm tra email đã tồn tại chưa.
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra email đã tồn tại chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Kiểm tra số điện thoại đã tồn tại chưa.
     */
    boolean existsBySoDienThoai(String soDienThoai);

    /**
     * Kiểm tra số điện thoại đã tồn tại chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsBySoDienThoaiAndIdNot(String soDienThoai, Long id);

    /**
     * Đếm số nhân viên đang sử dụng vai trò này (US03 - Kiểm tra trước khi xóa).
     *
     * @param vaiTroId ID của vai trò
     * @return số lượng nhân viên đang gán vai trò này
     */
    long countByVaiTroId(Long vaiTroId);

    /**
     * Kiểm tra có nhân viên nào đang sử dụng vai trò này hay không.
     *
     * @param vaiTroId ID của vai trò
     * @return true nếu có ít nhất 1 nhân viên đang sử dụng
     */
    boolean existsByVaiTroId(Long vaiTroId);

    /**
     * Đếm số nhân viên theo trạng thái hoạt động (1: Đang làm việc, 0: Đã nghỉ/Khóa).
     */
    long countByTrangThai(Integer trangThai);

    /**
     * Tìm kiếm và lọc danh sách nhân viên có phân trang (US04).
     *
     * @param tuKhoa từ khóa tìm theo họ tên, mã NV, tên đăng nhập, email, số điện thoại
     * @param vaiTroId lọc theo ID vai trò (null nếu lấy tất cả)
     * @param trangThai lọc theo trạng thái (null nếu lấy tất cả)
     * @param pageable phân trang
     * @return Trang kết quả nhân viên
     */
    @Query("SELECT nv FROM NhanVien nv WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(nv.hoTen) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(nv.maNhanVien) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(nv.tenDangNhap) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(nv.email) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " nv.soDienThoai LIKE CONCAT('%', :tuKhoa, '%')) AND " +
           "(:vaiTroId IS NULL OR nv.vaiTro.id = :vaiTroId) AND " +
           "(:trangThai IS NULL OR nv.trangThai = :trangThai)")
    Page<NhanVien> timKiemPhanTrang(
            @Param("tuKhoa") String tuKhoa,
            @Param("vaiTroId") Long vaiTroId,
            @Param("trangThai") Integer trangThai,
            Pageable pageable);
}
