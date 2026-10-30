package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO trả về kết quả tính toán chi phí Checkout động qua AJAX (US19).
 * - Công thức: Tổng thanh toán = Hàng - Voucher - Điểm + Ship.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutTinhTienResponse {

    private boolean hopLe;
    private String thongBao;
    private BigDecimal tongTienHang;
    private BigDecimal tienGiamGia;
    private BigDecimal tienCoolcashDung;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongThanhToan;
    private String maVoucher;
    private Long maGiamGiaId;
    private BigDecimal soTienConThieu;
    private BigDecimal soDiemCoolcash;
}
