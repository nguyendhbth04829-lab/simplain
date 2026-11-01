package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;

/**
 * Thực thể Chi tiết sản phẩm đổi/trả [chi_tiet_san_pham_doi_tra] (US24).
 * Lưu thông tin từng món hàng được chọn đổi/trả trong đơn cũ, tình trạng lỗi và SKU mới (nếu đổi hàng).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chi_tiet_san_pham_doi_tra")
public class ChiTietSanPhamDoiTra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_yeu_cau_doi_tra_id", nullable = false)
    private Long maYeuCauDoiTraId;

    @Column(name = "ma_chi_tiet_don_hang_id", nullable = false)
    private Long maChiTietDonHangId;

    @Column(name = "so_luong_tra", nullable = false)
    private Integer soLuongTra;

    /**
     * SKU/Biến thể mới muốn đổi sang (nếu hình thức là DOI_HANG).
     * Nullable nếu là trả hàng hoàn tiền (TRA_HANG_HOAN_TIEN).
     */
    @Column(name = "ma_bien_the_moi_id")
    private Long maBienTheMoiId;

    @Nationalized
    @Column(name = "tinh_trang_san_pham", length = 255, columnDefinition = "nvarchar(255)")
    private String tinhTrangSanPham;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_yeu_cau_doi_tra_id", insertable = false, updatable = false)
    private YeuCauDoiTra yeuCauDoiTra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chi_tiet_don_hang_id", insertable = false, updatable = false)
    private ChiTietDonHang chiTietDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bien_the_moi_id", insertable = false, updatable = false)
    private BienTheSanPham bienTheMoi;
}
