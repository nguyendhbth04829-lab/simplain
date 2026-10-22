package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dia_chi")
public class DiaChi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_khach_hang_id", nullable = false)
    private Long maKhachHangId;

    @Column(name = "ten_nguoi_nhan", nullable = false, length = 150)
    private String tenNguoiNhan;

    @Column(name = "so_dien_thoai", nullable = false, length = 20)
    private String soDienThoai;

    @Column(name = "tinh_thanh", nullable = false, length = 100)
    private String tinhThanh;

    @Column(name = "quan_huyen", nullable = false, length = 100)
    private String quanHuyen;

    @Column(name = "phuong_xa", nullable = false, length = 100)
    private String phuongXa;

    @Column(name = "dia_chi_chi_tiet", nullable = false, length = 255)
    private String diaChiChiTiet;

    @Column(name = "la_mac_dinh")
    private Boolean laMacDinh;
}
