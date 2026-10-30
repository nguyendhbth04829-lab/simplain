package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thực thể Biến động điểm Loyalty / Coolcash [bien_dong_diem] (US16).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bien_dong_diem")
public class BienDongDiem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_khach_hang_id", nullable = false)
    private Long maKhachHangId;

    @Column(name = "ma_don_hang_id")
    private Long maDonHangId;

    @Column(name = "loai_bien_dong", nullable = false, length = 50)
    private String loaiBienDong;

    @Column(name = "so_diem", nullable = false, precision = 18, scale = 2)
    private BigDecimal soDiem;

    @Nationalized
    @Column(name = "noi_dung", nullable = false, length = 500, columnDefinition = "nvarchar(500)")
    private String noiDung;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang_id", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don_hang_id", insertable = false, updatable = false)
    private DonHang donHang;

    @PrePersist
    protected void onCreate() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
    }
}
