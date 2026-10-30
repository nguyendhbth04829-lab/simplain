package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thực thể Chi tiết giỏ hàng [chi_tiet_gio_hang] (US17).
 * Tiêu chí 1: Thêm/Sửa/Xóa biến thể vào giỏ.
 * Tiêu chí 2: Tính năng bó hàng Combo (Sinh nhom_combo_token).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chi_tiet_gio_hang")
public class ChiTietGioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_gio_hang_id", nullable = false)
    private Long maGioHangId;

    @Column(name = "ma_bien_the_id", nullable = false)
    private Long maBienTheId;

    @Column(name = "ma_combo_id")
    private Long maComboId;

    @Column(name = "nhom_combo_token", length = 50)
    private String nhomComboToken;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia")
    private BigDecimal donGia;

    @Column(name = "ngay_them")
    private LocalDateTime ngayThem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_gio_hang_id", insertable = false, updatable = false)
    private GioHang gioHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_bien_the_id", insertable = false, updatable = false)
    private BienTheSanPham bienThe;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_combo_id", insertable = false, updatable = false)
    private GoiCombo goiCombo;

    @PrePersist
    protected void onCreate() {
        if (this.ngayThem == null) {
            this.ngayThem = LocalDateTime.now();
        }
        if (this.soLuong == null || this.soLuong < 1) {
            this.soLuong = 1;
        }
    }
}
