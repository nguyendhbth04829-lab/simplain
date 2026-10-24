package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.TrangThaiDonHang;

import java.util.Optional;

/**
 * Kho dữ liệu thao tác với thực thể TrangThaiDonHang (bảng 'trang_thai_don_hang').
 */
@Repository
public interface TrangThaiDonHangRepository extends JpaRepository<TrangThaiDonHang, Long> {

    /**
     * Tìm trạng thái đơn hàng theo mã định danh (ví dụ: CHO_XAC_NHAN, DANG_GIAO, HOAN_THANH, DA_HUY).
     *
     * @param maTrangThai Mã trạng thái
     * @return Optional trạng thái nếu tìm thấy
     */
    Optional<TrangThaiDonHang> findByMaTrangThai(String maTrangThai);
}
