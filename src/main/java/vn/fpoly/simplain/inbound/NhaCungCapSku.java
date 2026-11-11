package vn.fpoly.simplain.inbound;

import jakarta.persistence.*;
import lombok.*;
import vn.fpoly.simplain.entity.BienTheSanPham;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "nha_cung_cap_sku",
        uniqueConstraints = @UniqueConstraint(name = "UQ_nha_cung_cap_sku", columnNames = {"ma_ncc_id", "ma_bien_the_id"}))
public class NhaCungCapSku {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "ma_ncc_id", nullable = false)
    private NhaCungCap nhaCungCap;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "ma_bien_the_id", nullable = false)
    private BienTheSanPham bienThe;

    @Column(name = "ngay_lien_ket", nullable = false)
    private LocalDateTime ngayLienKet;

    @PrePersist
    public void prePersist() { if (ngayLienKet == null) ngayLienKet = LocalDateTime.now(); }
}
