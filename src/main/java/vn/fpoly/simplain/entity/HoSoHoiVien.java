package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ho_so_hoi_vien")
public class HoSoHoiVien {
    @Id
    @Column(name = "ma_khach_hang_id")
    private Long maKhachHangId;

    @Column(name = "ma_hang_hien_tai_id", nullable = false)
    private Long maHangHienTaiId;

    @Column(name = "so_du_coolcash", nullable = false, precision = 18, scale = 2)
    private BigDecimal soDuCoolcash;

    @Column(name = "tong_chi_tieu_nam", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongChiTieuNam;
}
