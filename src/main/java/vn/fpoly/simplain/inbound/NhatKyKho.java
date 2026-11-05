package vn.fpoly.simplain.inbound;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nhat_ky_kho")
public class NhatKyKho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_bien_the_id", nullable = false)
    private Long maBienTheId;

    @Column(name = "loai_bien_dong", nullable = false, length = 50)
    private String loaiBienDong; // NHAP_KHO, XUAT_BAN, DOI_TRA

    @Column(name = "so_luong_thay_doi", nullable = false)
    private Integer soLuongThayDoi;

    @Column(name = "ton_truoc", nullable = false)
    private Integer tonTruoc;

    @Column(name = "ton_sau", nullable = false)
    private Integer tonSau;

    @Column(name = "ma_chung_tu", nullable = false, length = 50)
    private String maChungTu;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}
