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
 * Thực thể Chi tiết đơn hàng [chi_tiet_don_hang] trong cơ sở dữ liệu Simpl4 (US15).
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

    @Nationalized
    @Column(name = "ten_san_pham", nullable = false, length = 255, columnDefinition = "nvarchar(255)")
    private String tenSanPham;

    @Column(name = "ma_sku", nullable = false, length = 100)
    private String maSku;

    @Column(name = "don_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGia;

    /**
     * Giá vốn bình quân gia quyền tại thời điểm bán (để tính chính xác lãi lỗ / COGS).
     */
    @Column(name = "gia_goc", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal giaGoc = BigDecimal.ZERO;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "thanh_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal thanhTien;

    /**
     * Trạng thái riêng của từng món hàng trong đơn:
     * - BINH_THUONG: Bình thường, giao bình thường hoặc hoàn thành
     * - MAT_HANG: Bị thất lạc / mất trên đường giao (Tồn kho KHÔNG cộng lại)
     */
    @Column(name = "trang_thai_item", length = 50)
    @Builder.Default
    private String trangThaiItem = "BINH_THUONG";

    @Nationalized
    @Column(name = "ghi_chu_item", length = 255)
    private String ghiChuItem;

    @Column(name = "ngay_bao_mat")
    private LocalDateTime ngayBaoMat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don_hang_id", insertable = false, updatable = false)
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bien_the_id", insertable = false, updatable = false)
    private BienTheSanPham bienThe;
}
