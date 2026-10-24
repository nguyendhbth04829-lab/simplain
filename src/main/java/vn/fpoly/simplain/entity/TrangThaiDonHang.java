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
 * Thực thể Trạng thái đơn hàng, ánh xạ bảng 'trang_thai_don_hang' trong cơ sở dữ liệu.
 * Đại diện cho các bước trong quy trình xử lý đơn: CHO_XAC_NHAN, DANG_GIAO, HOAN_THANH, DA_HUY,...
 */
@Entity
@Table(name = "trang_thai_don_hang")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrangThaiDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_trang_thai", unique = true, nullable = false, length = 50)
    private String maTrangThai;

    @Column(name = "ten_trang_thai", nullable = false, length = 100)
    private String tenTrangThai;

    @Column(name = "ma_mau_hex", length = 10)
    private String maMauHex;
}
