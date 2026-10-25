/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - Thực thể hồ sơ hội viên và ví điểm Coolcash trong CSDL Simpl4
 */
package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Thực thể hồ sơ hội viên và số dư điểm thưởng Coolcash.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ho_so_hoi_vien")
public class HoSoHoiVien {

    @Id
    @Column(name = "ma_khach_hang_id")
    private Long maKhachHangId;

    @Column(name = "ma_hang_hien_tai_id", nullable = false)
    private Long maHangHienTaiId;

    @Builder.Default
    @Column(name = "so_du_coolcash", nullable = false)
    private BigDecimal soDuCoolcash = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "tong_chi_tieu_nam", nullable = false)
    private BigDecimal tongChiTieuNam = BigDecimal.ZERO;
}
