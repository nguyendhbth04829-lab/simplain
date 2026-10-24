package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.ChiTietSanPhamDoiTra;

import java.util.List;

/**
 * Repository thao tác dữ liệu bảng 'chi_tiet_san_pham_doi_tra'.
 */
@Repository
public interface ChiTietSanPhamDoiTraRepository extends JpaRepository<ChiTietSanPhamDoiTra, Long> {

    /**
     * Lấy danh sách sản phẩm theo mã yêu cầu đổi trả RMA (US24, US25).
     */
    List<ChiTietSanPhamDoiTra> findByYeuCauDoiTraId(Long yeuCauDoiTraId);
}
