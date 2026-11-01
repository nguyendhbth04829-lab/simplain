package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Gói combo sản phẩm [goi_combo] (US17 & US28 - Mod 6).
 * Phục vụ thiết lập Gói Combo kích cầu giá fix và bó hàng combo tự chọn.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "goi_combo")
public class GoiCombo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Mã gói combo không được để trống")
    @Size(min = 3, max = 50, message = "Mã gói combo phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Mã combo chỉ gồm chữ cái, chữ số, gạch ngang (-) hoặc gạch dưới (_)")
    @Column(name = "ma_combo", nullable = false, length = 50, unique = true)
    private String maCombo;

    @NotBlank(message = "Tên gói combo không được để trống")
    @Size(min = 3, max = 255, message = "Tên gói combo phải từ 3 đến 255 ký tự")
    @Nationalized
    @Column(name = "ten_combo", nullable = false, length = 255, columnDefinition = "nvarchar(255)")
    private String tenCombo;

    @NotBlank(message = "Đường dẫn slug không được để trống")
    @Size(min = 3, max = 255, message = "Đường dẫn slug phải từ 3 đến 255 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug chỉ gồm chữ thường không dấu, số và gạch ngang (VD: tu-chon-3-ao-polo-299k)")
    @Column(name = "duong_dan_slug", nullable = false, length = 255, unique = true)
    private String duongDanSlug;

    @NotNull(message = "Giá trọn gói combo không được để trống")
    @DecimalMin(value = "1000.0", message = "Giá combo tối thiểu là 1.000 VNĐ")
    @DecimalMax(value = "999999999.0", message = "Giá combo không được vượt quá 999.999.999 VNĐ")
    @Column(name = "gia_combo", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaCombo;

    @NotNull(message = "Số món quy định trong combo không được để trống")
    @Min(value = 2, message = "Gói combo phải có tối thiểu 2 món quy định")
    @Max(value = 20, message = "Số món quy định trong combo tối đa là 20 món")
    @Column(name = "so_mon_quy_dinh", nullable = false)
    private Integer soMonQuyDinh;

    @Nationalized
    @Column(name = "mo_ta", columnDefinition = "ntext")
    private String moTa;

    @Builder.Default
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;

    /**
     * Trường tạm: Số lượng sản phẩm được gán trong combo để hiển thị danh sách (US28).
     */
    @Transient
    @Builder.Default
    private int soLuongSanPhamApDung = 0;

    /**
     * Trường tạm: Danh sách thực thể sản phẩm áp dụng trong combo (US28).
     */
    @Transient
    @Builder.Default
    private List<SanPham> danhSachSanPham = new ArrayList<>();

    /**
     * Trường tạm: Danh sách ID sản phẩm áp dụng trong combo (US28).
     */
    @Transient
    @Builder.Default
    private List<Long> danhSachMaSanPham = new ArrayList<>();
}
