package vn.fpoly.simplain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO nhận dữ liệu từ Dev 4 (Mod 4 - Giỏ hàng & Thanh toán) để Insert đơn hàng & chi tiết đơn hàng (US21 - Tiêu chí 1).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoDonHangMod4Request {

    /**
     * ID khách hàng sở hữu đơn hàng (Null nếu là khách vãng lai).
     */
    private Long maKhachHangId;

    /**
     * Họ tên người nhận hàng.
     */
    @NotBlank(message = "Tên người nhận hàng không được để trống!")
    private String tenNguoiNhan;

    /**
     * Số điện thoại liên hệ khi giao hàng.
     */
    @NotBlank(message = "Số điện thoại người nhận hàng không được để trống!")
    private String soDienThoaiNhan;

    /**
     * Địa chỉ giao hàng đầy đủ chi tiết.
     */
    @NotBlank(message = "Địa chỉ giao hàng không được để trống!")
    private String diaChiGiaoHang;

    /**
     * Ghi chú đơn hàng cho shipper hoặc cửa hàng.
     */
    private String ghiChu;

    /**
     * Mã giảm giá ID (nếu có áp dụng).
     */
    private Long maGiamGiaId;

    /**
     * ID Phương thức thanh toán được chọn (US18).
     */
    @NotNull(message = "Phương thức thanh toán ID không được để trống!")
    private Long maPhuongThucId;

    /**
     * Mã trạng thái đơn hàng mong muốn khởi tạo (Mặc định: CHO_XAC_NHAN).
     */
    @Builder.Default
    private String maTrangThai = "CHO_XAC_NHAN";

    /**
     * Tổng tiền hàng trước khi áp dụng voucher và giảm giá.
     */
    @NotNull(message = "Tổng tiền hàng không được để trống!")
    @DecimalMin(value = "0.0", message = "Tổng tiền hàng không được âm")
    private BigDecimal tongTienHang;

    /**
     * Tiền voucher giảm giá.
     */
    @Builder.Default
    private BigDecimal tienGiamGia = BigDecimal.ZERO;

    /**
     * Số tiền điểm Coolcash sử dụng (1 điểm = 1 VNĐ).
     */
    @Builder.Default
    private BigDecimal tienCoolcashDung = BigDecimal.ZERO;

    /**
     * Phí vận chuyển giao hàng.
     */
    @Builder.Default
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;

    /**
     * Tổng số tiền khách hàng cần thanh toán = Hàng - Voucher - Điểm + Ship.
     */
    @NotNull(message = "Tổng thanh toán không được để trống!")
    @DecimalMin(value = "0.0", message = "Tổng thanh toán không được âm")
    private BigDecimal tongThanhToan;

    /**
     * Danh sách mặt hàng chi tiết trong đơn hàng.
     */
    @Valid
    @NotEmpty(message = "Danh sách chi tiết mặt hàng trong đơn không được để trống!")
    private List<ChiTietDonHangItemRequest> danhSachChiTiet;
}
