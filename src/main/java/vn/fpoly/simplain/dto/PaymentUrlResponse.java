package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO trả về thông tin URL thanh toán và mã QR (US20 - Tiêu chí 1).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUrlResponse {

    /**
     * URL chuyển tiếp tới Cổng thanh toán trực tuyến (VNPay / MoMo).
     */
    private String paymentUrl;

    /**
     * URL hình ảnh mã QR quét thanh toán trực tiếp (VietQR / VNPay QR / MoMo QR).
     */
    private String qrCodeUrl;

    /**
     * Mã đơn hàng liên kết.
     */
    private String maDonHang;

    /**
     * Mã giao dịch nội bộ được sinh ra (TxnRef).
     */
    private String maGiaoDichNoiBo;

    /**
     * Số tiền thanh toán (VNĐ).
     */
    private BigDecimal soTien;

    /**
     * Tên phương thức / Cổng thanh toán.
     */
    private String tenPhuongThuc;

    /**
     * Mã phương thức thanh toán (VNPAY_QR, MOMO_WALLET, COD).
     */
    private String maPhuongThuc;

    /**
     * Thời gian hết hạn thanh toán (phút).
     */
    @Builder.Default
    private Integer thoiGianHetHanPhut = 15;
}
