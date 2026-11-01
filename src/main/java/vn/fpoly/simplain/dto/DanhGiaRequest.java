package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO nhận dữ liệu gửi Đánh giá sản phẩm từ phía khách hàng (US15).
 * Tiêu chí 1: ma_chi_tiet_don_hang_id
 * Tiêu chí 2: Upload JSON ảnh
 * Tiêu chí 3: Rating 1-5 sao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DanhGiaRequest {

    @NotNull(message = "Mã sản phẩm không được để trống")
    private Long maSanPhamId;

    @NotNull(message = "Vui lòng chọn chi tiết đơn hàng đã mua để đánh giá")
    private Long maChiTietDonHangId;

    @NotNull(message = "Vui lòng chọn số sao đánh giá (1-5 sao)")
    @Min(value = 1, message = "Số sao đánh giá tối thiểu là 1 sao")
    @Max(value = 5, message = "Số sao đánh giá tối đa là 5 sao")
    private Integer soSao;

    @NotBlank(message = "Nội dung nhận xét không được để trống")
    @Size(min = 5, max = 2000, message = "Nội dung đánh giá phải từ 5 đến 2000 ký tự")
    private String noiDung;

    /**
     * Danh sách đường dẫn ảnh URL trực tiếp (tùy chọn hoặc upload file).
     */
    private List<String> danhSachAnhUrls;

    /**
     * Đường dẫn slug của sản phẩm (dùng để chuyển hướng sau khi đánh giá).
     */
    private String duongDanSlug;
}
