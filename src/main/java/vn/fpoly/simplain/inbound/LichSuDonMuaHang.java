package vn.fpoly.simplain.inbound;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "lich_su_don_mua_hang")
public class LichSuDonMuaHang {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "don_mua_id", nullable = false)
    private DonMuaHang donMuaHang;

    @Column(name = "loai_hoat_dong", nullable = false, length = 40)
    private String loaiHoatDong;

    @Column(name = "trang_thai_truoc", length = 40)
    private String trangThaiTruoc;

    @Column(name = "trang_thai_moi", length = 40)
    private String trangThaiMoi;

    @Nationalized @Column(name = "ghi_chu", length = 1000)
    private String ghiChu;

    @Column(name = "ma_nhan_vien_id")
    private Long maNhanVienId;

    @Nationalized @Column(name = "ten_nhan_vien", length = 150)
    private String tenNhanVien;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() { if (ngayTao == null) ngayTao = LocalDateTime.now(); }
}
