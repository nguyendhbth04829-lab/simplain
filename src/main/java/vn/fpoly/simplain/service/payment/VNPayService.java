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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Dịch vụ Tích hợp Cổng thanh toán VNPay (US20).
 * - Tiêu chí 1: Generate URL quét QR thanh toán theo chuẩn VNPay V2.1.0 (HMAC-SHA512).
 * - Tiêu chí 2: Xác thực chữ ký IPN Webhook và Return URL từ VNPay.
 */
@Slf4j
@Service
public class VNPayService {

    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND = "pay";
    public static final String VNP_DEFAULT_TMN_CODE = "SIMPLAIN01";
    public static final String VNP_DEFAULT_HASH_SECRET = "RAI9EFCJ891283JKSDH21";
    public static final String VNP_DEFAULT_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sinh URL Thanh toán và Mã QR thanh toán qua Cổng VNPay (US20 - Tiêu chí 1).
     *
     * @param donHang         Đơn hàng cần thanh toán
     * @param maGiaoDichNoiBo Mã giao dịch nội bộ duy nhất (vnp_TxnRef)
     * @param ipAddress       Địa chỉ IP của người dùng
     * @param returnUrl       URL điều hướng sau khi thanh toán thành công
     * @param pttt            Cấu hình cổng thanh toán VNPay trong cơ sở dữ liệu
     * @return DTO chứa paymentUrl và qrCodeUrl
     */
    public PaymentUrlResponse taoUrlThanhToan(DonHang donHang, String maGiaoDichNoiBo, String ipAddress, String returnUrl, PhuongThucThanhToan pttt) {
        String tmnCode = VNP_DEFAULT_TMN_CODE;
        String hashSecret = VNP_DEFAULT_HASH_SECRET;
        String payUrl = VNP_DEFAULT_PAY_URL;

        // Trích xuất cấu hình JSON từ bảng phuong_thuc_thanh_toan nếu có
        if (pttt != null && pttt.getCauHinh() != null && !pttt.getCauHinh().isBlank()) {
            try {
                JsonNode json = objectMapper.readTree(pttt.getCauHinh());
                if (json.has("tmn_code") && !json.get("tmn_code").asText().isBlank()) {
                    tmnCode = json.get("tmn_code").asText().trim();
                } else if (json.has("vnp_TmnCode")) {
                    tmnCode = json.get("vnp_TmnCode").asText().trim();
                }

                if (json.has("hash_secret") && !json.get("hash_secret").asText().isBlank()) {
                    hashSecret = json.get("hash_secret").asText().trim();
                } else if (json.has("vnp_HashSecret")) {
                    hashSecret = json.get("vnp_HashSecret").asText().trim();
                }

                if (json.has("url") && !json.get("url").asText().isBlank()) {
                    payUrl = json.get("url").asText().trim();
                } else if (json.has("vnp_Url")) {
                    payUrl = json.get("vnp_Url").asText().trim();
                }
            } catch (Exception e) {
                log.warn("Không thể parse cấu hình JSON cổng VNPay, sử dụng cấu hình mặc định: {}", e.getMessage());
            }
        }

        BigDecimal soTien = donHang.getTongThanhToan() != null ? donHang.getTongThanhToan() : BigDecimal.ZERO;
        long amountVnpay = soTien.multiply(new BigDecimal(100)).longValue();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String createDate = now.format(formatter);
        String expireDate = now.plusMinutes(15).format(formatter);

        String ip = (ipAddress != null && !ipAddress.isBlank() && !"0:0:0:0:0:0:0:1".equals(ipAddress))
                ? ipAddress
                : "127.0.0.1";

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", VNP_VERSION);
        vnpParams.put("vnp_Command", VNP_COMMAND);
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amountVnpay));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", maGiaoDichNoiBo);
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang #" + donHang.getMaDonHang());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_IpAddr", ip);
        vnpParams.put("vnp_CreateDate", createDate);
        vnpParams.put("vnp_ExpireDate", expireDate);

        // 1. Sắp xếp các tham số theo bảng chữ cái alphabet (ASCII)
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext(); ) {
            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                try {
                    // Mã hóa URL chuẩn US-ASCII theo đặc tả VNPay
                    String encodedField = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString());
                    String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());

                    hashData.append(fieldName).append('=').append(encodedValue);
                    query.append(encodedField).append('=').append(encodedValue);

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                } catch (Exception e) {
                    log.error("Lỗi mã hóa tham số VNPay: {}", e.getMessage());
                }
            }
        }

        // 2. Ký số HMAC-SHA512
        String secureHash = CryptoUtils.hmacSHA512(hashSecret, hashData.toString());
        String paymentUrl = payUrl + "?" + query.toString() + "&vnp_SecureHash=" + secureHash;

        // 3. Tạo link mã QR VietQR / VNPay QR quét chuyển khoản tức thì
        String qrCodeUrl = "https://img.vietqr.io/image/970407-19036789999-compact2.png"
                + "?amount=" + soTien.longValue()
                + "&addInfo=TT%20DH%20" + donHang.getMaDonHang()
                + "&accountName=SIMPLAIN%20STORE";

        log.info("Generate VNPay URL thành công cho đơn [{}], TxnRef: {}", donHang.getMaDonHang(), maGiaoDichNoiBo);

        return PaymentUrlResponse.builder()
                .paymentUrl(paymentUrl)
                .qrCodeUrl(qrCodeUrl)
                .maDonHang(donHang.getMaDonHang())
                .maGiaoDichNoiBo(maGiaoDichNoiBo)
                .soTien(soTien)
                .tenPhuongThuc("Cổng thanh toán VNPay (VNPAY-QR)")
                .maPhuongThuc("VNPAY_QR")
                .thoiGianHetHanPhut(15)
                .build();
    }

    /**
     * Xác thực chữ ký bảo mật checksum trả về từ VNPay (US20 - Tiêu chí 2).
     *
     * @param requestParams Map chứa tất cả các query parameters nhận được từ VNPay
     * @param hashSecret    Khóa bí mật
     * @return true nếu chữ ký hợp lệ, false nếu bị can thiệp/sai lệch
     */
    public boolean xacThucChuKy(Map<String, String> requestParams, String hashSecret) {
        if (requestParams == null || requestParams.isEmpty()) {
            return false;
        }

        String vnpSecureHash = requestParams.get("vnp_SecureHash");
        if (vnpSecureHash == null || vnpSecureHash.isBlank()) {
            return false;
        }

        String secret = (hashSecret != null && !hashSecret.isBlank()) ? hashSecret : VNP_DEFAULT_HASH_SECRET;

        // Lấy toàn bộ các trường vnp_* ngoại trừ vnp_SecureHash và vnp_SecureHashType
        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if (key != null && key.startsWith("vnp_")
                    && !key.equals("vnp_SecureHash")
                    && !key.equals("vnp_SecureHashType")
                    && val != null && !val.isEmpty()) {
                fields.put(key, val);
            }
        }

        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext(); ) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            try {
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());
                hashData.append(fieldName).append('=').append(encodedValue);
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            } catch (Exception e) {
                log.error("Lỗi khi encode dữ liệu xác thực VNPay: {}", e.getMessage());
            }
        }

        String calculatedHash = CryptoUtils.hmacSHA512(secret, hashData.toString());
        boolean hopLe = calculatedHash.equalsIgnoreCase(vnpSecureHash);

        if (!hopLe) {
            log.warn("Chữ ký VNPay không khớp! Mong đợi: {}, Nhận được: {}", calculatedHash, vnpSecureHash);
        }
        return hopLe;
    }

    /**
     * Dịch mã phản hồi `vnp_ResponseCode` sang thông báo tiếng Việt thân thiện.
     */
    public String giaiMaLoiVNPay(String vnpResponseCode) {
        if (vnpResponseCode == null) return "Không nhận được mã phản hồi từ cổng thanh toán!";
        switch (vnpResponseCode) {
            case "00":
                return "Giao dịch thanh toán thành công!";
            case "07":
                return "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới gian lận/bất thường).";
            case "09":
                return "Thẻ/Tài khoản của bạn chưa đăng ký dịch vụ InternetBanking tại ngân hàng.";
            case "10":
                return "Xác thực thông tin thẻ/tài khoản không đúng quá 3 lần.";
            case "11":
                return "Đã hết hạn chờ thanh toán. Vui lòng thực hiện lại giao dịch.";
            case "12":
                return "Thẻ/Tài khoản của bạn hiện đang bị khóa.";
            case "24":
                return "Bạn đã hủy giao dịch thanh toán trên cổng VNPay.";
            case "51":
                return "Tài khoản của bạn không đủ số dư để thực hiện giao dịch.";
            case "65":
                return "Tài khoản của bạn đã vượt quá hạn mức giao dịch trong ngày.";
            case "75":
                return "Ngân hàng thanh toán đang bảo trì hệ thống.";
            case "79":
                return "Nhập sai mật khẩu thanh toán (OTP) quá số lần quy định.";
            default:
                return "Giao dịch không thành công (Mã lỗi VNPay: " + vnpResponseCode + ").";
        }
    }
}
