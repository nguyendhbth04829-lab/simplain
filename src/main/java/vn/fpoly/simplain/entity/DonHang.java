/**
 * Tác giả: Gia Minh
 * Chức năng: Thực thể đơn hàng trong CSDL Simpl4
 * Cập nhật xác nhận commit US15: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thực thể đơn hàng.
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

    @Column(name = "ma_don_hang", nullable = false, length = 50, unique = true)
    private String maDonHang;

    @Column(name = "ma_khach_hang_id")
    private Long maKhachHangId;

    @Column(name = "ma_trang_thai_id", nullable = false)
    private Long maTrangThaiId;

    @Column(name = "ma_giam_gia_id")
    private Long maGiamGiaId;

    @Column(name = "ma_van_don", length = 50)
    private String maVanDon;

    @Builder.Default
    @Column(name = "tong_tien_hang", nullable = false)
    private BigDecimal tongTienHang = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "tien_giam_gia")
    private BigDecimal tienGiamGia = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "tien_coolcash_dung")
    private BigDecimal tienCoolcashDung = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "phi_van_chuyen")
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "tong_thanh_toan", nullable = false)
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
}
