/**
 * Tác giả: Gia Minh
 * Chức năng: US15 - DTO gửi đánh giá sản phẩm đã mua
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Yêu cầu gửi đánh giá cho sản phẩm từ đơn hàng đã hoàn tất.
 */
@Schema(description = "Dữ liệu yêu cầu gửi đánh giá sản phẩm")
public record TaoDanhGiaRequest(

        @Schema(description = "ID của sản phẩm", example = "10")
        @NotNull(message = "Mã sản phẩm không được để trống")
        Long maSanPhamId,

        @Schema(description = "ID chi tiết đơn hàng đã mua", example = "100")
        @NotNull(message = "Mã chi tiết đơn hàng không được để trống")
        Long maChiTietDonHangId,

        @Schema(description = "Số sao đánh giá (1 đến 5)", example = "5")
        @NotNull(message = "Số sao đánh giá không được để trống")
        @Min(value = 1, message = "Số sao tối thiểu là 1")
        @Max(value = 5, message = "Số sao tối đa là 5")
        Integer soSao,

        @Schema(description = "Nội dung nhận xét chi tiết", example = "Áo mặc rất mát, form chuẩn, đóng gói cẩn thận!")
        @Size(max = 2000, message = "Nội dung đánh giá tối đa 2000 ký tự")
        String noiDung,

        @Schema(description = "Danh sách URL ảnh chụp thực tế sản phẩm")
        List<String> danhSachAnh
) {
}
