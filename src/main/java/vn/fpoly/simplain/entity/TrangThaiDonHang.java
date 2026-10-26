/**
 * Tác giả: Gia Minh
 * Chức năng: Thực thể trạng thái đơn hàng trong CSDL Simpl4
 * Cập nhật xác nhận commit US15: 26/10/2026 - Gia Minh
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

/**
 * Thực thể trạng thái đơn hàng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trang_thai_don_hang")
public class TrangThaiDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_trang_thai", nullable = false, length = 50, unique = true)
    private String maTrangThai;

    @Column(name = "ten_trang_thai", nullable = false, length = 100)
    private String tenTrangThai;

    @Column(name = "ma_mau_hex", length = 10)
    private String maMauHex;
}
