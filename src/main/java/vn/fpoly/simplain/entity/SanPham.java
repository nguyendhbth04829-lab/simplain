/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - Thực thể ánh xạ bảng san_pham trong CSDL Simpl4
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
 * Thực thể sản phẩm trong hệ thống Simplain.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "san_pham")
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_danh_muc_id", nullable = false)
    private Long maDanhMucId;

    @Column(name = "ma_bo_suu_tap_id")
    private Long maBoSuuTapId;

    @Column(name = "ma_san_pham", nullable = false, length = 50, unique = true)
    private String maSanPham;

    @Column(name = "ten_san_pham", nullable = false, length = 255)
    private String tenSanPham;

    @Column(name = "duong_dan_slug", nullable = false, length = 255, unique = true)
    private String duongDanSlug;

    @Column(name = "chat_lieu", length = 150)
    private String chatLieu;

    @Column(name = "form_dang", length = 100)
    private String formDang;

    @Column(name = "mo_ta_chi_tiet")
    private String moTaChiTiet;

    @Column(name = "huong_dan_chon_size")
    private String huongDanChonSize;

    @Builder.Default
    @Column(name = "gia_co_ban", nullable = false)
    private BigDecimal giaCoBan = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "noi_bat")
    private Boolean noiBat = false;

    @Builder.Default
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
}
