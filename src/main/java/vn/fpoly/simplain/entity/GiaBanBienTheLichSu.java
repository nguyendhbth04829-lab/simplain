package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bien_the_san_pham_gia_ban_lich_su", indexes = {
        @Index(name = "IX_gia_ban_lich_su_bien_the_thoi_diem", columnList = "ma_bien_the_id, thoi_diem, id")
})
public class GiaBanBienTheLichSu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bien_the_id", nullable = false)
    private BienTheSanPham bienThe;

    @Column(name = "gia_ban", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "thoi_diem", nullable = false)
    private LocalDateTime thoiDiem;

    @Column(name = "loai_su_kien", nullable = false, length = 30)
    private String loaiSuKien;

    @PrePersist
    void onCreate() {
        if (thoiDiem == null) thoiDiem = LocalDateTime.now();
        if (loaiSuKien == null) loaiSuKien = "DIEU_CHINH";
    }
}
