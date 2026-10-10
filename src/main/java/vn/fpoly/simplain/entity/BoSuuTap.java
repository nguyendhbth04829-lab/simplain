package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
    @Column(name = "ten_bo_suu_tap", nullable = false, length = 150)
    private String tenBoSuuTap;

    @NotBlank(message = "Đường dẫn slug không được để trống")
    @Column(name = "duong_dan_slug", nullable = false, length = 150, unique = true)
    private String duongDanSlug;

    @Column(name = "hinh_banner_url", length = 500)
    private String hinhBannerUrl;

    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;
}
