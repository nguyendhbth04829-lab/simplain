package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.DiaChi;

import java.util.List;
import java.util.Optional;

/**
 * Repository truy xuất dữ liệu sổ địa chỉ giao hàng khách hàng (US13).
 */
@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, Long> {

    /**
     * Lấy danh sách sổ địa chỉ của một khách hàng, ưu tiên địa chỉ mặc định lên đầu.
     */
    List<DiaChi> findByMaKhachHangIdOrderByLaMacDinhDescIdDesc(Long maKhachHangId);

    /**
     * Lấy một địa chỉ cụ thể theo ID và đảm bảo thuộc đúng khách hàng.
     */
    Optional<DiaChi> findByIdAndMaKhachHangId(Long id, Long maKhachHangId);

    /**
     * Lấy địa chỉ giao hàng mặc định hiện tại của khách hàng.
     */
    Optional<DiaChi> findByMaKhachHangIdAndLaMacDinhTrue(Long maKhachHangId);

    /**
     * Đếm số lượng địa chỉ mà khách hàng đang lưu.
     */
    long countByMaKhachHangId(Long maKhachHangId);

    /**
     * Đặt tất cả địa chỉ của khách hàng này về trạng thái không mặc định.
     */
    @Modifying
    @Query("UPDATE DiaChi d SET d.laMacDinh = false WHERE d.maKhachHangId = :khachHangId")
    void boMacDinhTatCaTheoKhachHang(@Param("khachHangId") Long khachHangId);
}
