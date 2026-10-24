package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.YeuCauDoiTra;

import java.util.List;
import java.util.Optional;

/**
 * Repository thao tác dữ liệu bảng 'yeu_cau_doi_tra'.
 */
@Repository
public interface YeuCauDoiTraRepository extends JpaRepository<YeuCauDoiTra, Long> {

    /**
     * Tìm yêu cầu RMA theo mã định danh (VD: RMA-20261024-0001).
     */
    Optional<YeuCauDoiTra> findByMaYeuCauRma(String maYeuCauRma);

    /**
     * Lấy danh sách yêu cầu RMA theo khách hàng (US24).
     */
    List<YeuCauDoiTra> findByKhachHangIdOrderByNgayTaoDesc(Long khachHangId);

    /**
     * Lấy toàn bộ danh sách yêu cầu RMA theo trạng thái để CSKH xử lý duyệt (US25).
     */
    List<YeuCauDoiTra> findByTrangThaiOrderByNgayTaoDesc(String trangThai);

    /**
     * Lấy tất cả yêu cầu RMA mới nhất.
     */
    List<YeuCauDoiTra> findAllByOrderByNgayTaoDesc();
}
