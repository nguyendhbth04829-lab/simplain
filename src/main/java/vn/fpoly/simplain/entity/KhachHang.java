/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - Quản lý hồ sơ khách hàng và Đổi mật khẩu
 * Thực thể ánh xạ trực tiếp bảng khach_hang trong CSDL Simpl4
 * Cập nhật xác nhận commit US12: 24/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Thực thể ánh xạ bảng khach_hang trong CSDL Simpl4.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "khach_hang")
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Mã khách hàng không được để trống")
    @Column(name = "ma_khach_hang", nullable = false, length = 50, unique = true)
    private String maKhachHang;

    @NotBlank(message = "Họ tên không được để trống")
    @Column(name = "ho_ten", nullable = false, length = 150)
    private String hoTen;

    @Column(name = "so_dien_thoai", length = 20, unique = true)
    private String soDienThoai;

    @Column(name = "email", length = 150, unique = true)
    private String email;

    @Column(name = "mat_khau_hash", length = 255)
    private String matKhauHash;

    @Column(name = "anh_dai_dien", length = 500)
    private String anhDaiDien;

    @Column(name = "gioi_tinh", length = 10)
    private String gioiTinh;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Builder.Default
    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai = 1;

    @Builder.Default
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();
}
