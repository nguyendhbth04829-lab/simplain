package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Dòng hàng trong giỏ (US17).
 * ma_bien_the_id tham chiếu biến thể của Mod 2 (chưa có entity nên lưu ID).
 * Các dòng cùng nhom_combo_token thuộc 1 bó combo (ma_combo_id của Khoa - US28).
 */
@Data
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

    @NotNull(message = "Biến thể không được để trống")
    @Column(name = "ma_bien_the_id", nullable = false)
    private Long maBienTheId;

    @Column(name = "ma_combo_id")
    private Long maComboId;

    @Column(name = "nhom_combo_token", length = 50)
    private String nhomComboToken;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 1;

    @Column(name = "ngay_them")
    private LocalDateTime ngayThem = LocalDateTime.now();
}
