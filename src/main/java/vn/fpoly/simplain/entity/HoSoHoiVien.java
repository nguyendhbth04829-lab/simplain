package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Thực thể Hồ sơ hội viên [ho_so_hoi_vien] (US16).
 * Tiêu chí 1: Đơn hàng hoàn thành cộng % điểm (so_du_coolcash).
 * Tiêu chí 2: Đạt mốc tự động lên Hạng (hang_thanh_vien).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ho_so_hoi_vien")
public class HoSoHoiVien {

    @Id
    @Column(name = "ma_khach_hang_id", nullable = false)
    private Long maKhachHangId;

    @Column(name = "ma_hang_hien_tai_id", nullable = false)
    private Long maHangHienTaiId;

    @Builder.Default
    @Column(name = "so_du_coolcash", nullable = false, precision = 18, scale = 2)
    private BigDecimal soDuCoolcash = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "tong_chi_tieu_nam", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongChiTieuNam = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang_id", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_hang_hien_tai_id", insertable = false, updatable = false)
    private HangThanhVien hangThanhVien;
}
