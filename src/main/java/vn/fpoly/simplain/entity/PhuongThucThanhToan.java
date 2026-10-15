package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Phương thức thanh toán (COD, VNPay, MoMo).
 * Ánh xạ bảng phuong_thuc_thanh_toan trong DB Simpl4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phuong_thuc_thanh_toan")
public class PhuongThucThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Mã phương thức không được để trống")
    @Column(name = "ma_phuong_thuc", nullable = false, length = 50, unique = true)
    private String maPhuongThuc;

    @NotBlank(message = "Tên phương thức không được để trống")
    @Column(name = "ten_phuong_thuc", nullable = false, length = 100)
    private String tenPhuongThuc;

    @Column(name = "la_truc_tuyen")
    private Boolean laTrucTuyen = false;

    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;
}
