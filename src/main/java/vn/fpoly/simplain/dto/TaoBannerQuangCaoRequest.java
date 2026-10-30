package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form DTO phục vụ Admin tạo / cập nhật Banner Quảng Cáo (US30 - Mod 6 - Khoa).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoBannerQuangCaoRequest {

    private Long id;

    @NotBlank(message = "Tiêu đề banner không được để trống")
    @Size(min = 3, max = 150, message = "Tiêu đề banner phải từ 3 đến 150 ký tự")
    private String tieuDe;

    @NotBlank(message = "Vị trí hiển thị không được để trống")
    private String viTriHienThi;

    @Size(max = 500, message = "Đường dẫn đích không được vượt quá 500 ký tự")
    private String duongDanDich;

    private String hinhAnhUrl;

    @Builder.Default
    private Boolean kichHoat = true;
}
