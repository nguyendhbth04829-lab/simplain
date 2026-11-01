package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Hóa đơn chờ tại quầy [hoa_don_cho] (POS đa tab / Multi-tab Bill Counter).
 * Phục vụ nhân viên bán hàng tại quầy mở nhiều tab hóa đơn cùng lúc mà không bị mất dữ liệu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hoa_don_cho")
public class HoaDonCho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_hoa_don", nullable = false, unique = true, length = 50)
    private String maHoaDon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_nhan_vien_id", referencedColumnName = "id")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_khach_hang_id", referencedColumnName = "id")
    private KhachHang khachHang;

    @Nationalized
    @Column(name = "ten_khach_hang", length = 150, columnDefinition = "nvarchar(150)")
    private String tenKhachHang;

    @Column(name = "so_dien_thoai_khach", length = 20)
    private String soDienThoaiKhach;

    @Nationalized
    @Column(name = "ghi_chu", length = 500, columnDefinition = "nvarchar(500)")
    private String ghiChu;

    @Builder.Default
    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = "CHO_THANH_TOAN"; // "CHO_THANH_TOAN", "DA_THANH_TOAN", "DA_HUY"

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @OneToMany(mappedBy = "hoaDonCho", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChiTietHoaDonCho> danhSachChiTiet = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.trangThai == null) {
            this.trangThai = "CHO_THANH_TOAN";
        }
        if (this.tenKhachHang == null || this.tenKhachHang.isBlank()) {
            this.tenKhachHang = "Khách lẻ tại quầy";
        }
    }
}
