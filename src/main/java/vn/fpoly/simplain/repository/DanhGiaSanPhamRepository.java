/**
 * Tác giả: Gia Minh
 * Chức năng: US15 - Repository đánh giá sản phẩm
 * Cập nhật xác nhận commit US15: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.DanhGiaSanPham;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhGiaSanPhamRepository extends JpaRepository<DanhGiaSanPham, Long> {

    List<DanhGiaSanPham> findAllByMaSanPhamIdOrderByNgayTaoDesc(Long maSanPhamId);

    List<DanhGiaSanPham> findAllByMaKhachHangIdOrderByNgayTaoDesc(Long maKhachHangId);

    Optional<DanhGiaSanPham> findByMaChiTietDonHangId(Long maChiTietDonHangId);

    boolean existsByMaChiTietDonHangId(Long maChiTietDonHangId);

    long countByMaSanPhamId(Long maSanPhamId);

    @Query("SELECT AVG(d.soSao) FROM DanhGiaSanPham d WHERE d.maSanPhamId = :maSanPhamId")
    Double tinhDiemTrungBinh(Long maSanPhamId);
}
