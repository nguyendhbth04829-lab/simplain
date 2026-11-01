package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

/**
 * Thực thể ánh xạ bảng [trang_thai_don_hang] trong cơ sở dữ liệu Simpl4.
 * Lưu danh mục các trạng thái đơn hàng: CHO_XAC_NHAN, DANG_XU_LY, DANG_GIAO, HOAN_THANH, DA_HUY.
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

    @Column(name = "ma_trang_thai", length = 50, unique = true, nullable = false)
    private String maTrangThai;

    @Nationalized
    @Column(name = "ten_trang_thai", length = 100, nullable = false, columnDefinition = "nvarchar(100)")
    private String tenTrangThai;

    @Column(name = "ma_mau_hex", length = 10)
    private String maMauHex;
}
