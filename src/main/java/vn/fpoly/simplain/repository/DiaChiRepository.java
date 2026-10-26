/**
 * Tác giả: Gia Minh
 * Chức năng: US13 - Giao diện Spring Data JPA truy vấn dữ liệu bảng dia_chi
 * Cập nhật xác nhận commit US13: 25/10/2026 - Gia Minh
 */
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
 * Repository truy vấn và thao tác trên bảng dia_chi.
 */
@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, Long> {

    List<DiaChi> findAllByMaKhachHangIdOrderByIdDesc(Long maKhachHangId);

    Optional<DiaChi> findByIdAndMaKhachHangId(Long id, Long maKhachHangId);

    Optional<DiaChi> findFirstByMaKhachHangIdAndLaMacDinhTrue(Long maKhachHangId);

    Optional<DiaChi> findFirstByMaKhachHangIdOrderByIdAsc(Long maKhachHangId);

    long countByMaKhachHangId(Long maKhachHangId);

    @Modifying
    @Query("UPDATE DiaChi d SET d.laMacDinh = false WHERE d.maKhachHangId = :maKhachHangId")
    void goTatCaMacDinh(@Param("maKhachHangId") Long maKhachHangId);

    @Modifying
    @Query("UPDATE DiaChi d SET d.laMacDinh = false WHERE d.maKhachHangId = :maKhachHangId AND d.id <> :diaChiId")
    void goMacDinhNgoaiTru(@Param("maKhachHangId") Long maKhachHangId, @Param("diaChiId") Long diaChiId);
}
