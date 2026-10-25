/**
 * Tác giả: Gia Minh
 * Chức năng: US13 - DTO yêu cầu cập nhật địa chỉ nhận hàng
 * Cập nhật xác nhận commit US13: 25/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dữ liệu yêu cầu cập nhật thông tin địa chỉ.
 */
@Schema(description = "Dữ liệu yêu cầu cập nhật địa chỉ nhận hàng")
public record CapNhatDiaChiRequest(

        @Schema(description = "Họ và tên người nhận hàng", example = "Nguyễn Văn A")
        @NotBlank(message = "Tên người nhận không được để trống")
        @Size(max = 150, message = "Tên người nhận tối đa 150 ký tự")
        String tenNguoiNhan,

        @Schema(description = "Số điện thoại người nhận (10 chữ số)", example = "0987654321")
        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không đúng định dạng Việt Nam (ví dụ 0987654321)")
        String soDienThoai,

        @Schema(description = "Tỉnh / Thành phố", example = "Hà Nội")
        @NotBlank(message = "Tỉnh / Thành phố không được để trống")
        @Size(max = 100, message = "Tên tỉnh thành tối đa 100 ký tự")
        String tinhThanh,

        @Schema(description = "Quận / Huyện", example = "Quận Cầu Giấy")
        @NotBlank(message = "Quận / Huyện không được để trống")
        @Size(max = 100, message = "Tên quận huyện tối đa 100 ký tự")
        String quanHuyen,

        @Schema(description = "Phường / Xã", example = "Phường Dịch Vọng")
        @NotBlank(message = "Phường / Xã không được để trống")
        @Size(max = 100, message = "Tên phường xã tối đa 100 ký tự")
        String phuongXa,

        @Schema(description = "Địa chỉ chi tiết (Số nhà, tên đường, ngõ xóm)", example = "Số 123, đường Xuân Thủy")
        @NotBlank(message = "Địa chỉ chi tiết không được để trống")
        @Size(max = 255, message = "Địa chỉ chi tiết tối đa 255 ký tự")
        String diaChiChiTiet,

        @Schema(description = "Cờ đặt làm địa chỉ mặc định", example = "true")
        Boolean laMacDinh
) {
}
