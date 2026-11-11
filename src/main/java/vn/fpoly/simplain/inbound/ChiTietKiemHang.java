package vn.fpoly.simplain.inbound;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "chi_tiet_kiem_hang")
public class ChiTietKiemHang {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "phieu_nhap_id", nullable = false)
    private PhieuNhapKho phieuNhapKho;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "chi_tiet_don_mua_id", nullable = false)
    private ChiTietDonMuaHang chiTietDonMua;

    @Column(name = "so_luong_da_giao", nullable = false)
    private Integer soLuongDaGiao;

    @Column(name = "so_luong_dat", nullable = false)
    private Integer soLuongDat;

    @Column(name = "so_luong_tu_choi", nullable = false)
    private Integer soLuongTuChoi;

    @Column(name = "so_luong_da_tra", nullable = false)
    private Integer soLuongDaTra;

    @Nationalized @Column(name = "ly_do_tu_choi", length = 1000)
    private String lyDoTuChoi;
}
