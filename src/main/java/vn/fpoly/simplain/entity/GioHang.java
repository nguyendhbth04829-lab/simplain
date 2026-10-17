package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Giỏ hàng của khách hàng (US17).
 * Mỗi khách chỉ có 1 giỏ (ma_khach_hang_id UNIQUE).
 * Ánh xạ bảng gio_hang trong DB Simpl4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gio_hang")
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_khach_hang_id", unique = true)
    private Long maKhachHangId;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat = LocalDateTime.now();
}
