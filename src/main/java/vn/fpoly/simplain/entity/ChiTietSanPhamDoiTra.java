package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thực thể Chi tiết sản phẩm đổi trả, ánh xạ bảng 'chi_tiet_san_pham_doi_tra'.
 * Lưu chi tiết sản phẩm cần đổi/trả trong một yêu cầu RMA (US24).
 */
@Entity
@Table(name = "chi_tiet_san_pham_doi_tra")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietSanPhamDoiTra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_yeu_cau_doi_tra_id", nullable = false)
    private YeuCauDoiTra yeuCauDoiTra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chi_tiet_don_hang_id", nullable = false)
    private ChiTietDonHang chiTietDonHang;

    @Builder.Default
    @Column(name = "so_luong_tra", nullable = false)
    private Integer soLuongTra = 1;

    @Column(name = "ma_bien_the_moi_id")
    private Long maBienTheMoiId;

    @Column(name = "tinh_trang_san_pham", length = 255)
    private String tinhTrangSanPham;
}
