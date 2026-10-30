package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

/**
 * Thực thể Bài viết Blog [bai_viet_blog] (US30 - Mod 6 / Nội dung - Khoa).
 * - Tiêu chí 1: CRUD bai_viet_blog (SEO friendly).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bai_viet_blog")
public class BaiVietBlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Tác giả bài viết không được để trống")
    @Column(name = "ma_tac_gia_id", nullable = false)
    private Long maTacGiaId;

    @NotBlank(message = "Chuyên mục bài viết không được để trống")
    @Size(min = 2, max = 100, message = "Chuyên mục phải từ 2 đến 100 ký tự")
    @Nationalized
    @Column(name = "chuyen_muc", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String chuyenMuc;

    @NotBlank(message = "Tiêu đề bài viết không được để trống")
    @Size(min = 5, max = 255, message = "Tiêu đề bài viết phải từ 5 đến 255 ký tự")
    @Nationalized
    @Column(name = "tieu_de", nullable = false, length = 255, columnDefinition = "nvarchar(255)")
    private String tieuDe;

    @NotBlank(message = "Đường dẫn slug SEO không được để trống")
    @Size(min = 3, max = 255, message = "Đường dẫn slug phải từ 3 đến 255 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug chỉ chứa chữ thường không dấu, số và dấu gạch ngang (VD: xu-huong-thoi-trang-nam-2026)")
    @Column(name = "duong_dan_slug", nullable = false, length = 255, unique = true)
    private String duongDanSlug;

    @Size(max = 500, message = "Mô tả ngắn SEO không được vượt quá 500 ký tự")
    @Nationalized
    @Column(name = "mo_ta_ngan", length = 500, columnDefinition = "nvarchar(500)")
    private String moTaNgan;

    @NotBlank(message = "Nội dung bài viết không được để trống")
    @Nationalized
    @Column(name = "noi_dung_html", nullable = false, columnDefinition = "ntext")
    private String noiDungHtml;

    @Column(name = "anh_dai_dien_url", length = 500)
    private String anhDaiDienUrl;

    @Builder.Default
    @Column(name = "ngay_xuat_ban")
    private LocalDateTime ngayXuatBan = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tac_gia_id", insertable = false, updatable = false)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private NhanVien tacGia;

    @PrePersist
    public void prePersist() {
        if (this.ngayXuatBan == null) {
            this.ngayXuatBan = LocalDateTime.now();
        }
    }

    // Trường tạm phục vụ hiển thị
    @Transient
    private String tenTacGia;
}
