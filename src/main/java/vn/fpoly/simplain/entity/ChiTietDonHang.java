package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;

/**
 * Thực thể Chi tiết đơn hàng [chi_tiet_don_hang] trong cơ sở dữ liệu Simpl4 (US15).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chi_tiet_don_hang")
public class ChiTietDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_don_hang_id", nullable = false)
    private Long maDonHangId;

    @Column(name = "ma_bien_the_id", nullable = false)
    private Long maBienTheId;

    @Column(name = "ma_combo_id")
    private Long maComboId;

    @Column(name = "nhom_combo_token", length = 50)
    private String nhomComboToken;

    @Nationalized
    @Column(name = "ten_san_pham", nullable = false, length = 255, columnDefinition = "nvarchar(255)")
    private String tenSanPham;

    @Column(name = "ma_sku", nullable = false, length = 100)
    private String maSku;

    @Column(name = "don_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "thanh_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal thanhTien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don_hang_id", insertable = false, updatable = false)
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bien_the_id", insertable = false, updatable = false)
    private BienTheSanPham bienThe;
}
