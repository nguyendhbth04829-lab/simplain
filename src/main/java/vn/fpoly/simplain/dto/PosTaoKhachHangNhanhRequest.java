package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request tạo nhanh khách hàng trực tiếp từ màn hình POS.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosTaoKhachHangNhanhRequest {

    @NotBlank(message = "Họ và tên khách hàng không được để trống")
    private String hoTen;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String soDienThoai;

    private String email;
}
