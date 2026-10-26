/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - Thực thể ánh xạ bảng san_pham_yeu_thich trong CSDL Simpl4
 * Cập nhật xác nhận commit US14: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Thực thể sản phẩm yêu thích (Wishlist) của khách hàng.
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

    @NotNull(message = "Mã khách hàng không được để trống")
    @Column(name = "ma_khach_hang_id", nullable = false)
    private Long maKhachHangId;

    @NotNull(message = "Mã sản phẩm không được để trống")
    @Column(name = "ma_san_pham_id", nullable = false)
    private Long maSanPhamId;

    @Builder.Default
    @Column(name = "ngay_them")
    private LocalDateTime ngayThem = LocalDateTime.now();
}
