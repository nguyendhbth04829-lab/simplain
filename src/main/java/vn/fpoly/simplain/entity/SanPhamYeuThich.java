package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Thực thể Sản phẩm yêu thích (Wishlist) [san_pham_yeu_thich] (US14).
 * Lưu thông tin khách hàng thả tim/lưu sản phẩm vào danh sách yêu thích.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "san_pham_yeu_thich")
public class SanPhamYeuThich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_khach_hang_id", nullable = false)
    private Long maKhachHangId;

    @Column(name = "ma_san_pham_id", nullable = false)
    private Long maSanPhamId;

    @Column(name = "ngay_them")
    private LocalDateTime ngayThem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang_id", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_san_pham_id", insertable = false, updatable = false)
    private SanPham sanPham;

    @PrePersist
    protected void onCreate() {
        if (this.ngayThem == null) {
            this.ngayThem = LocalDateTime.now();
        }
    }
}
