/**
 * Tác giả: Gia Minh
 * Chức năng: Thực thể chi tiết đơn hàng trong CSDL Simpl4
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

/**
 * Thực thể chi tiết mặt hàng trong đơn hàng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chi_tiet_don_hang")
public class ChiTietDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_don_hang_id", nullable = false)
    private Long maDonHangId;

    @Column(name = "ma_bien_the_id", nullable = false)
    private Long maBienTheId;

    @Column(name = "ma_combo_id")
    private Long maComboId;

    @Column(name = "nhom_combo_token", length = 50)
    private String nhomComboToken;

    @Column(name = "ten_san_pham", nullable = false, length = 255)
    private String tenSanPham;

    @Column(name = "ma_sku", nullable = false, length = 100)
    private String maSku;

    @Builder.Default
    @Column(name = "don_gia", nullable = false)
    private BigDecimal donGia = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 1;

    @Builder.Default
    @Column(name = "thanh_tien", nullable = false)
    private BigDecimal thanhTien = BigDecimal.ZERO;
}
