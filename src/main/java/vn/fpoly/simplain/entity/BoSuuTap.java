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
 * Thực thể Bộ sưu tập thời trang [bo_suu_tap] kèm hình ảnh Banner chiến dịch (US06).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bo_suu_tap")
public class BoSuuTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Tên bộ sưu tập không được để trống")
    @Size(min = 2, max = 150, message = "Tên bộ sưu tập phải từ 2 đến 150 ký tự")
    @Nationalized
    @Column(name = "ten_bo_suu_tap", nullable = false, length = 150, columnDefinition = "nvarchar(150)")
    private String tenBoSuuTap;

    @NotBlank(message = "Đường dẫn slug không được để trống")
    @Size(min = 2, max = 150, message = "Đường dẫn slug phải từ 2 đến 150 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug chỉ gồm chữ thường không dấu, chữ số và dấu gạch ngang (VD: bst-thu-dong-2026)")
    @Column(name = "duong_dan_slug", nullable = false, length = 150, unique = true)
    private String duongDanSlug;

    @Size(max = 500, message = "Đường dẫn ảnh banner không được vượt quá 500 ký tự")
    @Column(name = "hinh_banner_url", length = 500)
    private String hinhBannerUrl;

    @Builder.Default
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;

    /**
     * Số lượng sản phẩm đang thuộc bộ sưu tập này (phục vụ hiển thị trên giao diện Web).
     */
    @Transient
    private long soSanPham;
}
