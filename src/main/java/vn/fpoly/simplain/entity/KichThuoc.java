package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

/**
 * Thực thể Kích thước sản phẩm [kich_thuoc] lưu trữ mã size (S, M, L, XL, 2XL...) và tên mô tả kích thước (US07).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kich_thuoc")
public class KichThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Mã kích thước không được để trống")
    @Size(min = 1, max = 50, message = "Mã kích thước phải từ 1 đến 50 ký tự")
    @Pattern(
            regexp = "^[A-Za-z0-9_-]{1,50}$",
            message = "Mã kích thước chỉ gồm chữ cái, chữ số, dấu gạch ngang (VD: S, M, L, XL, 2XL, 30)"
    )
    @Column(name = "ma_kich_thuoc", nullable = false, unique = true, length = 50)
    private String maKichThuoc;

    @NotBlank(message = "Tên kích thước không được để trống")
    @Size(min = 1, max = 50, message = "Tên kích thước phải từ 1 đến 50 ký tự")
    @Nationalized
    @Column(name = "ten_kich_thuoc", nullable = false, length = 50, columnDefinition = "nvarchar(50)")
    private String tenKichThuoc;

    @Builder.Default
    @Column(name = "kich_hoat", nullable = false)
    private Boolean kichHoat = true;

    /**
     * Số lượng biến thể sản phẩm đang sử dụng kích thước này (Phục vụ hiển thị & kiểm tra trạng thái).
     */
    @Transient
    private long soBienTheSuDung;
}
