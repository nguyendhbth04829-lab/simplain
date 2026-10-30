package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.LichSuTrangThaiDonHang;

import java.util.List;

/**
 * Kho dữ liệu Lịch sử trạng thái đơn hàng & hành trình vận đơn (US22 - Mod 5 - Hưng).
 */
@Repository
public interface LichSuTrangThaiDonHangRepository extends JpaRepository<LichSuTrangThaiDonHang, Long> {

    List<LichSuTrangThaiDonHang> findByDonHangIdOrderByNgayChuyenDesc(Long donHangId);

    List<LichSuTrangThaiDonHang> findByDonHangIdOrderByNgayChuyenAsc(Long donHangId);

    @Query("SELECT ls FROM LichSuTrangThaiDonHang ls " +
           "JOIN FETCH ls.trangThaiMoi tt " +
           "LEFT JOIN FETCH ls.nguoiThucHien nv " +
           "WHERE ls.donHang.id = :donHangId " +
           "ORDER BY ls.ngayChuyen ASC, ls.id ASC")
    List<LichSuTrangThaiDonHang> layLichSuTheoDonHangIdAsc(@Param("donHangId") Long donHangId);
}
