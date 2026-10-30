package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Form DTO phục vụ Admin tạo / cập nhật Bài viết Blog (US30 - Mod 6 - Khoa).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoBaiVietBlogRequest {

    private Long id;

    private Long maTacGiaId;

    @NotBlank(message = "Chuyên mục bài viết không được để trống")
    @Size(min = 2, max = 100, message = "Chuyên mục bài viết phải từ 2 đến 100 ký tự")
    private String chuyenMuc;

    @NotBlank(message = "Tiêu đề bài viết không được để trống")
    @Size(min = 5, max = 255, message = "Tiêu đề bài viết phải từ 5 đến 255 ký tự")
    private String tieuDe;

    @NotBlank(message = "Đường dẫn slug SEO không được để trống")
    @Size(min = 3, max = 255, message = "Đường dẫn slug phải từ 3 đến 255 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug chỉ chứa chữ thường không dấu, số và dấu gạch ngang (VD: meo-phoi-do-nam-dep)")
    private String duongDanSlug;

    @Size(max = 500, message = "Mô tả ngắn SEO không được vượt quá 500 ký tự")
    private String moTaNgan;

    @NotBlank(message = "Nội dung bài viết không được để trống")
    private String noiDungHtml;

    private String anhDaiDienUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime ngayXuatBan;
}
