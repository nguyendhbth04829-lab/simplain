package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO nhận dữ liệu tạo nhanh đơn hàng trên Dashboard để kiểm thử realtime doanh thu & biểu đồ (US05).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoDonHangNhanhRequest {

    @Size(max = 50, message = "Mã đơn hàng không được vượt quá 50 ký tự")
    private String maDonHang;

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(min = 2, max = 150, message = "Tên người nhận phải từ 2 đến 150 ký tự")
    private String tenNguoiNhan;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    @Pattern(regexp = "^(0[3|5|7|8|9])+([0-9]{8})$", message = "Số điện thoại phải gồm 10 chữ số hợp lệ (VD: 0901234567)")
    private String soDienThoaiNhan;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    @Size(min = 5, max = 500, message = "Địa chỉ giao hàng phải từ 5 đến 500 ký tự")
    private String diaChiGiaoHang;

    @NotNull(message = "Tổng tiền hàng không được để trống")
    @DecimalMin(value = "1000", message = "Tổng tiền hàng phải từ 1.000đ trở lên")
    @DecimalMax(value = "500000000", message = "Tổng tiền hàng không được vượt quá 500.000.000đ")
    private BigDecimal tongTienHang;

    @DecimalMin(value = "0", message = "Tiền giảm giá không được âm")
    private BigDecimal tienGiamGia;

    @DecimalMin(value = "0", message = "Phí vận chuyển không được âm")
    private BigDecimal phiVanChuyen;

    @NotBlank(message = "Vui lòng chọn trạng thái đơn hàng")
    private String maTrangThai;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String ghiChu;
}
