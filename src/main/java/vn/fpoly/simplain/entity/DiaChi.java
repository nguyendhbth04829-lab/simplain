package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

/**
 * Thực thể Sổ địa chỉ giao hàng ánh xạ bảng [dia_chi] trong cơ sở dữ liệu (US13).
 * Mỗi khách hàng có thể lưu nhiều địa chỉ nhận hàng và thiết lập 1 địa chỉ mặc định.
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

    @Column(name = "ma_khach_hang_id", nullable = false)
    private Long maKhachHangId;

    @NotBlank(message = "Họ tên người nhận không được để trống")
    @Nationalized
    @Column(name = "ten_nguoi_nhan", nullable = false, length = 150, columnDefinition = "nvarchar(150)")
    private String tenNguoiNhan;

    @NotBlank(message = "Số điện thoại nhận hàng không được để trống")
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng Việt Nam")
    @Column(name = "so_dien_thoai", nullable = false, length = 20)
    private String soDienThoai;

    @NotBlank(message = "Vui lòng chọn Tỉnh / Thành phố")
    @Nationalized
    @Column(name = "tinh_thanh", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String tinhThanh;

    @NotBlank(message = "Vui lòng chọn Quận / Huyện")
    @Nationalized
    @Column(name = "quan_huyen", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String quanHuyen;

    @NotBlank(message = "Vui lòng chọn Phường / Xã")
    @Nationalized
    @Column(name = "phuong_xa", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String phuongXa;

    @NotBlank(message = "Địa chỉ chi tiết (số nhà, ngõ, tên đường) không được để trống")
    @Nationalized
    @Column(name = "dia_chi_chi_tiet", nullable = false, length = 255, columnDefinition = "nvarchar(255)")
    private String diaChiChiTiet;

    @Builder.Default
    @Column(name = "la_mac_dinh")
    private Boolean laMacDinh = false;

    /**
     * Trả về chuỗi địa chỉ giao hàng đầy đủ chuẩn Việt Nam.
     */
    public String getDiaChiDayDu() {
        return diaChiChiTiet + ", " + phuongXa + ", " + quanHuyen + ", " + tinhThanh;
    }
}
