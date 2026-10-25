/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - Thực thể hạng thành viên trong CSDL Simpl4
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
 * Thực thể cấu hình các bậc hạng thành viên (Loyalty Tiers).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hang_thanh_vien")
public class HangThanhVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ten_hang", nullable = false, length = 50, unique = true)
    private String tenHang;

    @Column(name = "chi_tieu_nam_toi_thieu", nullable = false)
    private BigDecimal chiTieuNamToiThieu;

    @Column(name = "phan_tram_hoan_tien", nullable = false)
    private BigDecimal phanTramHoanTien;
}
