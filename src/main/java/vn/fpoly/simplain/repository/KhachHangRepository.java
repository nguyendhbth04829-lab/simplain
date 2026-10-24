package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.KhachHang;

import java.util.Optional;

/**
 * Repository thao tác dữ liệu bảng 'khach_hang'.
 */
@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {

    Optional<KhachHang> findByMaKhachHang(String maKhachHang);

    Optional<KhachHang> findBySoDienThoai(String soDienThoai);

    Optional<KhachHang> findByEmail(String email);
}
