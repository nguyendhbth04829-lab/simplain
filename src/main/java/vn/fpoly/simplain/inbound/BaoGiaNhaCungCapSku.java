package vn.fpoly.simplain.inbound;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;
import vn.fpoly.simplain.entity.BienTheSanPham;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nha_cung_cap_sku_bao_gia", indexes = {
        @Index(name = "IX_bao_gia_ncc_bien_the_thoi_diem", columnList = "ma_ncc_id, ma_bien_the_id, thoi_diem_bao_gia, id")
})
public class BaoGiaNhaCungCapSku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_ncc_id", nullable = false)
    private NhaCungCap nhaCungCap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bien_the_id", nullable = false)
    private BienTheSanPham bienThe;

    @Column(name = "gia_bao", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBao;

    @Column(name = "thoi_diem_bao_gia", nullable = false)
    private LocalDateTime thoiDiemBaoGia;

    @Nationalized
    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @PrePersist
    void onCreate() {
        if (thoiDiemBaoGia == null) thoiDiemBaoGia = LocalDateTime.now();
    }
}
