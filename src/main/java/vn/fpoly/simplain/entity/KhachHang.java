package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Thực thể Khách hàng ánh xạ bảng khach_hang trong cơ sở dữ liệu.
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

    @Column(name = "ma_khach_hang", nullable = false, unique = true, length = 50)
    private String maKhachHang;

    @NotBlank(message = "Họ và tên không được để trống")
    @Nationalized
    @Column(name = "ho_ten", nullable = false, length = 150, columnDefinition = "nvarchar(150)")
    private String hoTen;

    @Column(name = "so_dien_thoai", unique = true, length = 20)
    private String soDienThoai;

    @Email(message = "Email không đúng định dạng")
    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "mat_khau_hash", length = 255)
    private String matKhauHash;

    @Column(name = "anh_dai_dien", length = 500)
    private String anhDaiDien;

    @Nationalized
    @Column(name = "gioi_tinh", length = 10, columnDefinition = "nvarchar(10)")
    private String gioiTinh;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Builder.Default
    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai = 1;

    @Builder.Default
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.trangThai == null) {
            this.trangThai = 1;
        }
    }
}
