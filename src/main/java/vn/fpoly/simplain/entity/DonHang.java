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

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thực thể Đơn hàng, ánh xạ bảng 'don_hang' trong cơ sở dữ liệu.
 * Dùng để tính toán doanh thu trong ngày, số lượng đơn hàng hôm nay và vẽ biểu đồ Dashboard (US05).
 */
@Entity
@Table(name = "don_hang")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_don_hang", unique = true, nullable = false, length = 50)
    private String maDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang_id")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_trang_thai_id", nullable = false)
    private TrangThaiDonHang trangThai;

    @Column(name = "ma_van_don", length = 50)
    private String maVanDon;

    @Builder.Default
    @Column(name = "tong_tien_hang", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTienHang = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "tien_giam_gia", precision = 18, scale = 2)
    private BigDecimal tienGiamGia = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "tien_coolcash_dung", precision = 18, scale = 2)
    private BigDecimal tienCoolcashDung = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "phi_van_chuyen", precision = 18, scale = 2)
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "tong_thanh_toan", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongThanhToan = BigDecimal.ZERO;

    @Column(name = "ten_nguoi_nhan", nullable = false, length = 150)
    private String tenNguoiNhan;

    @Column(name = "so_dien_thoai_nhan", nullable = false, length = 20)
    private String soDienThoaiNhan;

    @Column(name = "dia_chi_giao_hang", nullable = false, length = 500)
    private String diaChiGiaoHang;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.tongThanhToan == null) {
            this.tongThanhToan = BigDecimal.ZERO;
        }
    }
}
