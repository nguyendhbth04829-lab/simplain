package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thực thể ánh xạ bảng [don_hang] trong cơ sở dữ liệu Simpl4.
 * Phục vụ thống kê số lượng đơn hàng hôm nay, doanh thu trong ngày và biểu đồ tổng quan (US05).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "don_hang")
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_don_hang", length = 50, unique = true, nullable = false)
    private String maDonHang;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_khach_hang_id", referencedColumnName = "id")
    private KhachHang khachHang;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_trang_thai_id", referencedColumnName = "id", nullable = false)
    private TrangThaiDonHang trangThaiDonHang;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_phuong_thuc_id", referencedColumnName = "id")
    private PhuongThucThanhToan phuongThucThanhToan;

    @Column(name = "ma_giam_gia_id")
    private Long maGiamGiaId;

    @Column(name = "ma_van_don", length = 50)
    private String maVanDon;

    @Column(name = "loai_don_hang", length = 20)
    @Builder.Default
    private String loaiDonHang = "ONLINE"; // "ONLINE" hoặc "TAI_QUAY"

    @Column(name = "tien_khach_dua", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal tienKhachDua = BigDecimal.ZERO;

    @Column(name = "tien_thua_tra_khach", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal tienThuaTraKhach = BigDecimal.ZERO;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_nhan_vien_id", referencedColumnName = "id")
    private NhanVien nhanVien;

    @Column(name = "tong_tien_hang", precision = 18, scale = 2, nullable = false)
    private BigDecimal tongTienHang;

    @Column(name = "tien_giam_gia", precision = 18, scale = 2)
    private BigDecimal tienGiamGia;

    @Column(name = "tien_coolcash_dung", precision = 18, scale = 2)
    private BigDecimal tienCoolcashDung;

    @Column(name = "phi_van_chuyen", precision = 18, scale = 2)
    private BigDecimal phiVanChuyen;

    @Column(name = "tong_thanh_toan", precision = 18, scale = 2, nullable = false)
    private BigDecimal tongThanhToan;

    @Nationalized
    @Column(name = "ten_nguoi_nhan", length = 150, nullable = false, columnDefinition = "nvarchar(150)")
    private String tenNguoiNhan;

    @Column(name = "so_dien_thoai_nhan", length = 20, nullable = false)
    private String soDienThoaiNhan;

    @Nationalized
    @Column(name = "dia_chi_giao_hang", length = 500, nullable = false, columnDefinition = "nvarchar(500)")
    private String diaChiGiaoHang;

    @Nationalized
    @Column(name = "ghi_chu", length = 500, columnDefinition = "nvarchar(500)")
    private String ghiChu;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.tongTienHang == null) {
            this.tongTienHang = BigDecimal.ZERO;
        }
        if (this.tienGiamGia == null) {
            this.tienGiamGia = BigDecimal.ZERO;
        }
        if (this.tienCoolcashDung == null) {
            this.tienCoolcashDung = BigDecimal.ZERO;
        }
        if (this.phiVanChuyen == null) {
            this.phiVanChuyen = BigDecimal.ZERO;
        }
        if (this.tongThanhToan == null) {
            this.tongThanhToan = BigDecimal.ZERO;
        }
        if (this.loaiDonHang == null) {
            this.loaiDonHang = "ONLINE";
        }
        if (this.tienKhachDua == null) {
            this.tienKhachDua = BigDecimal.ZERO;
        }
        if (this.tienThuaTraKhach == null) {
            this.tienThuaTraKhach = BigDecimal.ZERO;
        }
    }
}
