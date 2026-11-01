package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO tiếp nhận yêu cầu Đặt hàng / Checkout từ Khách hàng (US19).
 * - Tiêu chí 1: Tính Tổng tiền = Hàng - Voucher - Điểm + Ship.
 * - Tiêu chí 2: Xác nhận địa chỉ và phương thức thanh toán.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatHangRequest {

    /**
     * Tên người nhận hàng.
     */
    @NotBlank(message = "Vui lòng nhập họ tên người nhận hàng!")
    private String tenNguoiNhan;

    /**
     * Số điện thoại liên lạc khi giao hàng.
     */
    @NotBlank(message = "Vui lòng nhập số điện thoại người nhận hàng!")
    private String soDienThoaiNhan;

    /**
     * Địa chỉ giao hàng chi tiết (Số nhà, đường, phường, quận, tỉnh/thành phố).
     */
    @NotBlank(message = "Vui lòng nhập địa chỉ giao nhận hàng cụ thể!")
    private String diaChiGiaoHang;

    /**
     * Tỉnh / Thành phố được chọn (US13).
     */
    private String tinhThanh;

    /**
     * Quận / Huyện được chọn (US13).
     */
    private String quanHuyen;

    /**
     * Phường / Xã được chọn (US13).
     */
    private String phuongXa;

    /**
     * Địa chỉ chi tiết (số nhà, ngõ ngách, tên đường) (US13).
     */
    private String diaChiChiTiet;

    /**
     * Ghi chú đơn hàng cho shipper hoặc cửa hàng.
     */
    private String ghiChu;

    /**
     * ID Cổng / Phương thức thanh toán được chọn (US18).
     */
    @NotNull(message = "Vui lòng chọn phương thức thanh toán!")
    private Long phuongThucThanhToanId;

    /**
     * Mã voucher giảm giá (nếu có áp dụng).
     */
    private String maVoucher;

    /**
     * Số điểm Coolcash muốn sử dụng (1 điểm = 1 VNĐ).
     */
    private BigDecimal soDiemCoolcash;

    /**
     * ID địa chỉ đã lưu trong Sổ địa chỉ (US13, nếu khách chọn từ danh sách).
     */
    private Long diaChiId;
}
