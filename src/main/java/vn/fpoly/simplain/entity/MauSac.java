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
 * Thực thể Màu sắc sản phẩm [mau_sac] lưu trữ mã màu (Hex/Mã định danh) và tên màu (US07).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mau_sac")
public class MauSac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Mã màu (Hex) không được để trống")
    @Size(min = 2, max = 50, message = "Mã màu phải từ 2 đến 50 ký tự")
    @Pattern(
            regexp = "^(#[0-9A-Fa-f]{3}([0-9A-Fa-f]{3})?|[A-Za-z0-9_-]{2,50})$",
            message = "Mã màu phải là mã Hex hợp lệ (VD: #000000, #1E3A8A) hoặc mã màu viết liền không khoảng trắng"
    )
    @Column(name = "ma_mau", nullable = false, unique = true, length = 50)
    private String maMau;

    @NotBlank(message = "Tên màu sắc không được để trống")
    @Size(min = 2, max = 100, message = "Tên màu sắc phải từ 2 đến 100 ký tự")
    @Nationalized
    @Column(name = "ten_mau", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String tenMau;

    @Builder.Default
    @Column(name = "kich_hoat", nullable = false)
    private Boolean kichHoat = true;

    /**
     * Số lượng biến thể sản phẩm đang sử dụng màu sắc này (Phục vụ hiển thị & kiểm tra trạng thái).
     */
    @Transient
    private long soBienTheSuDung;
}
