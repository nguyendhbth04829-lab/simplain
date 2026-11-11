package vn.fpoly.simplain.inbound;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "lan_giao_hang")
public class LanGiaoHang {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "don_mua_id", nullable = false)
    private DonMuaHang donMuaHang;

    @Column(name = "lan_thu", nullable = false)
    private Integer lanThu;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    @Builder.Default
    private TrangThaiLanGiaoHang trangThai = TrangThaiLanGiaoHang.DANG_GIAO;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Nationalized @Column(name = "ly_do_that_bai", length = 1000)
    private String lyDoThatBai;

    @Column(name = "ben_chiu_trach_nhiem", length = 30)
    private String benChiuTrachNhiem;

    @Nationalized @Column(name = "bang_chung", length = 1000)
    private String bangChung;
}
