package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Thực thể Lịch sử trạng thái đơn hàng, ánh xạ bảng 'lich_su_trang_thai_don_hang'.
 * Ghi lại nhật ký mỗi lần chuyển trạng thái đơn hàng (US22).
 */
@Entity
@Table(name = "lich_su_trang_thai_don_hang")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichSuTrangThaiDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don_hang_id", nullable = false)
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_trang_thai_moi_id", nullable = false)
    private TrangThaiDonHang trangThaiMoi;

    @Column(name = "nguoi_thuc_hien_id")
    private Long nguoiThucHienId;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ngay_chuyen")
    private LocalDateTime ngayChuyen;

    @PrePersist
    public void prePersist() {
        if (this.ngayChuyen == null) {
            this.ngayChuyen = LocalDateTime.now();
        }
    }
}
