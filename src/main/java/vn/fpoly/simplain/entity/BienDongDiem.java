/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - Thực thể biến động điểm thưởng Coolcash trong CSDL Simpl4
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
 * Thực thể lịch sử cộng / trừ điểm thưởng Coolcash.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bien_dong_diem")
public class BienDongDiem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_khach_hang_id", nullable = false)
    private Long maKhachHangId;

    @Column(name = "ma_don_hang_id")
    private Long maDonHangId;

    @Column(name = "loai_bien_dong", nullable = false, length = 20)
    private String loaiBienDong;

    @Column(name = "so_diem", nullable = false)
    private BigDecimal soDiem;

    @Column(name = "noi_dung", nullable = false, length = 255)
    private String noiDung;

    @Builder.Default
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();
}
