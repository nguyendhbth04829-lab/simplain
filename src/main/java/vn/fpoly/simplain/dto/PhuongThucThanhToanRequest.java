package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Thêm/Sửa phương thức thanh toán (US18).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhuongThucThanhToanRequest {

    private Long id;

    @NotBlank(message = "Mã phương thức thanh toán không được để trống")
    @Size(min = 2, max = 50, message = "Mã phương thức phải từ 2 đến 50 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Mã phương thức chỉ gồm chữ cái, số, gạch dưới hoặc gạch ngang (VD: VNPAY_QR)")
    private String maPhuongThuc;

    @NotBlank(message = "Tên phương thức thanh toán không được để trống")
    @Size(max = 100, message = "Tên phương thức tối đa 100 ký tự")
    private String tenPhuongThuc;

    @Builder.Default
    private Boolean laTrucTuyen = false;

    @Builder.Default
    private Boolean kichHoat = true;

    @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
    private String moTa;

    @Size(max = 255, message = "Biểu tượng tối đa 255 ký tự")
    private String bieuTuong;

    private String cauHinh;
}
