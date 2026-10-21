/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - Quản lý hồ sơ khách hàng
 * Giao diện Spring Data JPA truy vấn dữ liệu bảng khach_hang
 */
package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.KhachHang;

import java.util.Optional;

/**
 * Giao diện truy vấn dữ liệu cho thực thể KhachHang.
 */
@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {

    Optional<KhachHang> findByMaKhachHang(String maKhachHang);

    Optional<KhachHang> findByEmail(String email);

    Optional<KhachHang> findBySoDienThoai(String soDienThoai);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsBySoDienThoaiAndIdNot(String soDienThoai, Long id);
}
