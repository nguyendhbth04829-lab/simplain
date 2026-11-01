package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

/**
 * Thực thể Banner Quảng cáo [banner_quang_cao] (US30 - Mod 6 / Nội dung - Khoa).
 * - Tiêu chí 2: CRUD banner_quang_cao tại các vị trí hiển thị trên Web.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "banner_quang_cao")
public class BannerQuangCao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Tiêu đề banner không được để trống")
    @Size(min = 3, max = 150, message = "Tiêu đề banner phải từ 3 đến 150 ký tự")
    @Nationalized
    @Column(name = "tieu_de", nullable = false, length = 150, columnDefinition = "nvarchar(150)")
    private String tieuDe;

    @NotBlank(message = "Hình ảnh banner không được để trống")
    @Column(name = "hinh_anh_url", nullable = false, length = 500)
    private String hinhAnhUrl;

    @Column(name = "duong_dan_dich", length = 500)
    private String duongDanDich;

    @NotBlank(message = "Vị trí hiển thị không được để trống")
    @Column(name = "vi_tri_hien_thi", nullable = false, length = 50)
    private String viTriHienThi;

    @Builder.Default
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;

    @PrePersist
    public void prePersist() {
        if (this.kichHoat == null) {
            this.kichHoat = true;
        }
    }

    // Trường tạm phục vụ hiển thị nhãn vị trí tiếng Việt
    @Transient
    public String getTenViTriHienThi() {
        if (viTriHienThi == null) return "Chưa xác định";
        return switch (viTriHienThi.toUpperCase()) {
            case "HERO_SLIDER", "HOME_HERO" -> "Slide chính Trang chủ (Hero)";
            case "HOME_MIDDLE", "HOME_SUB" -> "Banner giữa Trang chủ";
            case "STORE_TOP", "STORE_SIDEBAR" -> "Banner Cửa hàng / Danh mục";
            case "POPUP", "POPUP_PROMO" -> "Popup Khuyến mãi";
            case "FOOTER", "FOOTER_BOTTOM" -> "Banner Chân trang (Footer)";
            default -> viTriHienThi;
        };
    }
}
