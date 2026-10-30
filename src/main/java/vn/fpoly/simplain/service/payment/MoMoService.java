package vn.fpoly.simplain.service.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.fpoly.simplain.dto.PaymentUrlResponse;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.PhuongThucThanhToan;
import vn.fpoly.simplain.util.CryptoUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Dịch vụ Tích hợp Cổng Ví điện tử MoMo (US20).
 * - Tiêu chí 1: Generate URL quét QR MoMo (HMAC-SHA256).
 * - Tiêu chí 2: Xác thực chữ ký IPN Webhook và Return URL từ MoMo.
 */
@Slf4j
@Service
public class MoMoService {

    public static final String MOMO_DEFAULT_PARTNER_CODE = "MOMOSIMPLAIN";
    public static final String MOMO_DEFAULT_ACCESS_KEY = "KL98394821";
    public static final String MOMO_DEFAULT_SECRET_KEY = "SECRET98394821";
    public static final String MOMO_DEFAULT_ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sinh URL Thanh toán và Mã QR thanh toán qua Ví MoMo (US20 - Tiêu chí 1).
     */
    public PaymentUrlResponse taoUrlThanhToan(DonHang donHang, String maGiaoDichNoiBo, String ipAddress, String returnUrl, PhuongThucThanhToan pttt) {
        String partnerCode = MOMO_DEFAULT_PARTNER_CODE;
        String accessKey = MOMO_DEFAULT_ACCESS_KEY;
        String secretKey = MOMO_DEFAULT_SECRET_KEY;
        String endpoint = MOMO_DEFAULT_ENDPOINT;

        if (pttt != null && pttt.getCauHinh() != null && !pttt.getCauHinh().isBlank()) {
            try {
                JsonNode json = objectMapper.readTree(pttt.getCauHinh());
                if (json.has("partner_code")) partnerCode = json.get("partner_code").asText().trim();
                if (json.has("access_key")) accessKey = json.get("access_key").asText().trim();
                if (json.has("secret_key")) secretKey = json.get("secret_key").asText().trim();
                if (json.has("endpoint")) endpoint = json.get("endpoint").asText().trim();
            } catch (Exception e) {
                log.warn("Không thể parse cấu hình MoMo, sử dụng cấu hình mặc định: {}", e.getMessage());
            }
        }

        BigDecimal soTien = donHang.getTongThanhToan() != null ? donHang.getTongThanhToan() : BigDecimal.ZERO;
        String amount = String.valueOf(soTien.longValue());
        String orderId = maGiaoDichNoiBo;
        String requestId = "REQ_" + System.currentTimeMillis();
        String orderInfo = "Thanh toan don hang #" + donHang.getMaDonHang() + " qua Vi MoMo";
        String ipnUrl = returnUrl.replace("/thanh-toan/momo-return", "/api/payment/momo-ipn");
        String extraData = "";
        String requestType = "captureWallet";

        // Chuỗi ký chuẩn của MoMo All-in-one
        String rawSignature = "accessKey=" + accessKey
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + ipnUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + returnUrl
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        String signature = CryptoUtils.hmacSHA256(secretKey, rawSignature);

        // URL MoMo QR deep-link hoặc web gateway
        String paymentUrl = endpoint + "?partnerCode=" + partnerCode
                + "&orderId=" + orderId
                + "&amount=" + amount
                + "&orderInfo=" + URLEncoderWrapper(orderInfo)
                + "&signature=" + signature;

        // Mã QR Ví điện tử MoMo
        String qrCodeUrl = "https://img.vietqr.io/image/970422-0901234567-compact2.png"
                + "?amount=" + amount
                + "&addInfo=MOMO%20DH%20" + donHang.getMaDonHang()
                + "&accountName=SIMPLAIN%20MOMO";

        log.info("Generate MoMo URL thành công cho đơn [{}], OrderId: {}", donHang.getMaDonHang(), orderId);

        return PaymentUrlResponse.builder()
                .paymentUrl(paymentUrl)
                .qrCodeUrl(qrCodeUrl)
                .maDonHang(donHang.getMaDonHang())
                .maGiaoDichNoiBo(maGiaoDichNoiBo)
                .soTien(soTien)
                .tenPhuongThuc("Ví điện tử MoMo (MoMo Wallet)")
                .maPhuongThuc("MOMO_WALLET")
                .thoiGianHetHanPhut(15)
                .build();
    }

    /**
     * Xác thực chữ ký số webhook IPN từ MoMo (US20 - Tiêu chí 2).
     */
    public boolean xacThucChuKy(Map<String, String> params, String secretKey) {
        if (params == null || params.isEmpty()) {
            return false;
        }

        String signature = params.get("signature");
        if (signature == null || signature.isBlank()) {
            return false;
        }

        String secret = (secretKey != null && !secretKey.isBlank()) ? secretKey : MOMO_DEFAULT_SECRET_KEY;

        String accessKey = params.getOrDefault("accessKey", "");
        String amount = params.getOrDefault("amount", "");
        String extraData = params.getOrDefault("extraData", "");
        String message = params.getOrDefault("message", "");
        String orderId = params.getOrDefault("orderId", "");
        String orderInfo = params.getOrDefault("orderInfo", "");
        String orderType = params.getOrDefault("orderType", "");
        String partnerCode = params.getOrDefault("partnerCode", "");
        String payType = params.getOrDefault("payType", "");
        String requestId = params.getOrDefault("requestId", "");
        String responseTime = params.getOrDefault("responseTime", "");
        String resultCode = params.getOrDefault("resultCode", "");
        String transId = params.getOrDefault("transId", "");

        String rawSignature = "accessKey=" + accessKey
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&message=" + message
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&orderType=" + orderType
                + "&partnerCode=" + partnerCode
                + "&payType=" + payType
                + "&requestId=" + requestId
                + "&responseTime=" + responseTime
                + "&resultCode=" + resultCode
                + "&transId=" + transId;

        String calculated = CryptoUtils.hmacSHA256(secret, rawSignature);
        return calculated.equalsIgnoreCase(signature);
    }

    private String URLEncoderWrapper(String val) {
        try {
            return java.net.URLEncoder.encode(val, java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return val;
        }
    }
}
