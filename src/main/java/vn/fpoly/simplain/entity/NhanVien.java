package vn.fpoly.simplain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

/**
 * Thực thể Nhân viên ánh xạ bảng nhan_vien trong cơ sở dữ liệu Simpl4.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nhan_vien")
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_nhan_vien", nullable = false, unique = true, length = 50)
    private String maNhanVien;

    @NotBlank(message = "Họ và tên nhân viên không được để trống")
    @Nationalized
    @Column(name = "ho_ten", nullable = false, length = 150, columnDefinition = "nvarchar(150)")
    private String hoTen;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Column(name = "ten_dang_nhap", nullable = false, unique = true, length = 100)
    private String tenDangNhap;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "mat_khau_hash", nullable = false, length = 255)
    private String matKhauHash;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "email", length = 150)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_vai_tro_id", nullable = false)
    private VaiTro vaiTro;

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

    public String getTenVaiTro() {
        return vaiTro != null ? vaiTro.getTenVaiTro() : "";
    }

    public String getMaVaiTro() {
        return vaiTro != null ? vaiTro.getMaVaiTro() : "";
    }
}
