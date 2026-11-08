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
 * Thực thể Giao dịch thanh toán [giao_dich_thanh_toan] (US20).
 * Tiêu chí 3: Lưu giao_dich_thanh_toan.
 * Phục vụ đối soát, ghi nhận kết quả thanh toán từ các cổng VNPay, MoMo, COD.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "giao_dich_thanh_toan")
public class GiaoDichThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_don_hang_id", referencedColumnName = "id", nullable = false)
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_phuong_thuc_id", referencedColumnName = "id", nullable = false)
    private PhuongThucThanhToan phuongThucThanhToan;

    @Column(name = "ma_giao_dich_noi_bo", length = 100, nullable = false, unique = true)
    private String maGiaoDichNoiBo;

    @Column(name = "ma_giao_dich_cong", length = 100)
    private String maGiaoDichCong;

    @Column(name = "so_tien", precision = 18, scale = 2, nullable = false)
    private BigDecimal soTien;

    @Column(name = "trang_thai", length = 50, nullable = false)
    private String trangThai; // CHO_THANH_TOAN, THANH_CONG, THAT_BAI, DA_HUY

    @Nationalized
    @Column(name = "du_lieu_phan_hoi", columnDefinition = "nvarchar(max)")
    private String duLieuPhanHoi;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.trangThai == null || this.trangThai.isBlank()) {
            this.trangThai = "CHO_THANH_TOAN";
        }
    }
}
