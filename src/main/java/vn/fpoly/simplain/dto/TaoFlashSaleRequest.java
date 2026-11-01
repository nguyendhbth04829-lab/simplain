package vn.fpoly.simplain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Form DTO phục vụ Admin thiết lập khung giờ Flash Sale & gán biến thể (US29 - Mod 6 - Khoa).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoFlashSaleRequest {

    private Long id;

    @NotBlank(message = "Tiêu đề đợt Flash Sale không được để trống")
    @Size(min = 3, max = 150, message = "Tiêu đề đợt Flash Sale phải từ 3 đến 150 ký tự")
    private String tieuDe;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime thoiGianBatDau;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime thoiGianKetThuc;

    @Builder.Default
    private Boolean kichHoat = true;

    @NotEmpty(message = "Vui lòng chọn ít nhất 1 biến thể sản phẩm tham gia Flash Sale")
    @Valid
    @Builder.Default
    private List<ChiTietFlashSaleFormItem> danhSachChiTiet = new ArrayList<>();
}
