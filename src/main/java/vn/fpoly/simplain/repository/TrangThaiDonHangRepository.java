package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.TrangThaiDonHang;

import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Trạng thái đơn hàng [trang_thai_don_hang].
 */
@Repository
public interface TrangThaiDonHangRepository extends JpaRepository<TrangThaiDonHang, Long> {

    /**
     * Tìm trạng thái đơn hàng theo mã định danh (CHO_XAC_NHAN, DANG_XU_LY, DANG_GIAO, HOAN_THANH, DA_HUY).
     */
    Optional<TrangThaiDonHang> findByMaTrangThai(String maTrangThai);

    /**
     * Kiểm tra mã trạng thái đã tồn tại hay chưa.
     */
    boolean existsByMaTrangThai(String maTrangThai);
}
