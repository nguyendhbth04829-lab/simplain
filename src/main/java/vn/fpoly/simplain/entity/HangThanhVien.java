package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;

/**
 * Thực thể Hạng thành viên Loyalty [hang_thanh_vien] (US16).
 * Tiêu chí 2: Đạt mốc chi tiêu tự động lên Hạng.
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

    @Nationalized
    @Column(name = "ten_hang", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String tenHang;

    @Column(name = "chi_tieu_nam_toi_thieu", nullable = false, precision = 18, scale = 2)
    private BigDecimal chiTieuNamToiThieu;

    @Column(name = "phan_tram_hoan_tien", nullable = false, precision = 5, scale = 2)
    private BigDecimal phanTramHoanTien;
}
