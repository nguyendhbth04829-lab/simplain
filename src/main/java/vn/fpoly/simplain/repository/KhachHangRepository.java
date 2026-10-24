package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.KhachHang;

import java.util.Optional;

/**
 * Repository thao tác dữ liệu khách hàng.
 */
@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {

    Optional<KhachHang> findByEmail(String email);

    Optional<KhachHang> findBySoDienThoai(String soDienThoai);

    Optional<KhachHang> findByMaKhachHang(String maKhachHang);

    boolean existsByEmail(String email);

    boolean existsBySoDienThoai(String soDienThoai);

    boolean existsByMaKhachHang(String maKhachHang);
}
