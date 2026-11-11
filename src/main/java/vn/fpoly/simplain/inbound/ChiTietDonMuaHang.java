package vn.fpoly.simplain.inbound;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import vn.fpoly.simplain.entity.BienTheSanPham;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "chi_tiet_don_mua_hang")
public class ChiTietDonMuaHang {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "don_mua_id", nullable = false)
    private DonMuaHang donMuaHang;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "ma_bien_the_id", nullable = false)
    private BienTheSanPham bienThe;

    @Column(name = "ma_sku", nullable = false, length = 100)
    private String maSku;

    @Column(name = "so_luong_dat", nullable = false)
    private Integer soLuongDat;

    @Builder.Default
    @Column(name = "so_luong_da_nhan", nullable = false)
    private Integer soLuongDaNhan = 0;

    @Column(name = "don_gia_chot", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGiaChot;

    @Column(name = "thanh_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal thanhTien;

    @Nationalized
    @Column(name = "ten_san_pham_chot", length = 255)
    private String tenSanPhamChot;
}
