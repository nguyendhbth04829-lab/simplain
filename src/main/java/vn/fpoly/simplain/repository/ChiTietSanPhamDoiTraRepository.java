package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.ChiTietSanPhamDoiTra;

import java.util.List;

/**
 * Kho dữ liệu cho thực thể Chi tiết sản phẩm đổi/trả [chi_tiet_san_pham_doi_tra] (US24).
 */
@Repository
public interface ChiTietSanPhamDoiTraRepository extends JpaRepository<ChiTietSanPhamDoiTra, Long> {

    /**
     * Lấy danh sách các sản phẩm đổi/trả thuộc về một phiếu RMA.
     */
    List<ChiTietSanPhamDoiTra> findByMaYeuCauDoiTraId(Long maYeuCauDoiTraId);

    /**
     * Lấy danh sách các yêu cầu đổi trả có chứa chi tiết đơn hàng cụ thể.
     */
    List<ChiTietSanPhamDoiTra> findByMaChiTietDonHangId(Long maChiTietDonHangId);
}
