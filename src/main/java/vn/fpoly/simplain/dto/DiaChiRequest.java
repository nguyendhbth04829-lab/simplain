package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dữ liệu Form Thêm / Cập nhật Địa chỉ giao hàng (US13).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiRequest {

    private Long id;

    @NotBlank(message = "Họ tên người nhận không được để trống")
    @Size(max = 150, message = "Họ tên người nhận tối đa 150 ký tự")
    private String tenNguoiNhan;

    @NotBlank(message = "Số điện thoại nhận hàng không được để trống")
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng (VD: 0912345678)")
    private String soDienThoai;

    @NotBlank(message = "Vui lòng chọn Tỉnh / Thành phố")
    private String tinhThanh;

    @NotBlank(message = "Vui lòng chọn Quận / Huyện")
    private String quanHuyen;

    @NotBlank(message = "Vui lòng chọn Phường / Xã")
    private String phuongXa;

    @NotBlank(message = "Địa chỉ chi tiết (số nhà, ngõ, tên đường) không được để trống")
    @Size(max = 255, message = "Địa chỉ chi tiết tối đa 255 ký tự")
    private String diaChiChiTiet;

    @Builder.Default
    private Boolean laMacDinh = false;
}
