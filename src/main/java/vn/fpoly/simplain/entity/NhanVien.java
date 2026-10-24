package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Thực thể Nhân viên, ánh xạ bảng 'nhan_vien' trong cơ sở dữ liệu.
 * Chịu trách nhiệm quản lý thông tin nhân viên đăng nhập vào hệ thống CMS quản trị (US02).
 */
@Entity
@Table(name = "nhan_vien")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_nhan_vien", unique = true, nullable = false, length = 50)
    private String maNhanVien;

    @Column(name = "ho_ten", nullable = false, length = 150)
    private String hoTen;

    @Column(name = "ten_dang_nhap", unique = true, nullable = false, length = 100)
    private String tenDangNhap;

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

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

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
