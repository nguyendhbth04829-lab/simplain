package vn.fpoly.simplain.inbound;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lich_su_hop_tac_nha_cung_cap")
public class LichSuHopTacNhaCungCap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_ncc_id", nullable = false)
    private NhaCungCap nhaCungCap;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_hoat_dong", nullable = false, length = 40)
    private LoaiHoatDongHopTac loaiHoatDong;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_truoc", length = 30)
    private TrangThaiNhaCungCap trangThaiTruoc;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_moi", length = 30)
    private TrangThaiNhaCungCap trangThaiMoi;

    @Nationalized
    @Column(name = "ghi_chu", length = 1000)
    private String ghiChu;

    @Nationalized
    @Column(name = "ket_qua", length = 100)
    private String ketQua;

    @Nationalized
    @Column(name = "ket_qua_day_du", length = 500)
    private String ketQuaDayDu;

    @Column(name = "ma_nhan_vien_id")
    private Long maNhanVienId;

    @Nationalized
    @Column(name = "ten_nhan_vien", length = 150)
    private String tenNhanVien;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    void ganNgayTao() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}
