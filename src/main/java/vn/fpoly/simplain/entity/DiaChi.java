/**
 * Tác giả: Gia Minh
 * Chức năng: US13 - Thực thể ánh xạ bảng dia_chi trong CSDL Simpl4
 * Cập nhật xác nhận commit US13: 25/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thực thể địa chỉ giao hàng của khách hàng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dia_chi")
public class DiaChi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Mã khách hàng không được để trống")
    @Column(name = "ma_khach_hang_id", nullable = false)
    private Long maKhachHangId;

    @NotBlank(message = "Tên người nhận không được để trống")
    @Column(name = "ten_nguoi_nhan", nullable = false, length = 150)
    private String tenNguoiNhan;

    @NotBlank(message = "Số điện thoại nhận hàng không được để trống")
    @Column(name = "so_dien_thoai", nullable = false, length = 20)
    private String soDienThoai;

    @NotBlank(message = "Tỉnh / Thành phố không được để trống")
    @Column(name = "tinh_thanh", nullable = false, length = 100)
    private String tinhThanh;

    @NotBlank(message = "Quận / Huyện không được để trống")
    @Column(name = "quan_huyen", nullable = false, length = 100)
    private String quanHuyen;

    @NotBlank(message = "Phường / Xã không được để trống")
    @Column(name = "phuong_xa", nullable = false, length = 100)
    private String phuongXa;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Column(name = "dia_chi_chi_tiet", nullable = false, length = 255)
    private String diaChiChiTiet;

    @Builder.Default
    @Column(name = "la_mac_dinh")
    private Boolean laMacDinh = false;
}
