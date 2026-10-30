package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

/**
 * Thực thể ghi nhận Lịch sử thay đổi trạng thái đơn hàng và hành trình vận đơn (US22 - Mod 5 - Hưng).
 * Ánh xạ bảng [lich_su_trang_thai_don_hang] trong cơ sở dữ liệu Simpl4.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lich_su_trang_thai_don_hang")
public class LichSuTrangThaiDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_don_hang_id", referencedColumnName = "id", nullable = false)
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_trang_thai_moi_id", referencedColumnName = "id", nullable = false)
    private TrangThaiDonHang trangThaiMoi;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nguoi_thuc_hien_id", referencedColumnName = "id")
    private NhanVien nguoiThucHien;

    @Nationalized
    @Column(name = "ghi_chu", length = 500, columnDefinition = "nvarchar(500)")
    private String ghiChu;

    @Column(name = "ngay_chuyen")
    private LocalDateTime ngayChuyen;

    @PrePersist
    protected void onCreate() {
        if (this.ngayChuyen == null) {
            this.ngayChuyen = LocalDateTime.now();
        }
    }
}
