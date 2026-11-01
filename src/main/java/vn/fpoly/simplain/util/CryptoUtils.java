package vn.fpoly.simplain.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Tiện ích mã hóa HMAC-SHA512 và HMAC-SHA256 phục vụ ký số và xác thực chữ ký
 * checksum cho các cổng thanh toán VNPay và MoMo (US20 - Tiêu chí 1 & 2).
 */
@Slf4j
public final class CryptoUtils {

    private CryptoUtils() {
    }

    /**
     * Băm chuỗi dữ liệu bằng thuật toán HMAC-SHA512 (Chuẩn VNPay V2.1.0).
     *
     * @param key  Khóa bí mật (Hash Secret)
     * @param data Chuỗi dữ liệu cần băm
     * @return Chuỗi Hexadecimal (viết thường)
     */
    public static String hmacSHA512(String key, String data) {
        if (key == null || data == null) {
            return "";
        }
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            log.error("Lỗi khi băm HMAC-SHA512: {}", ex.getMessage());
            return "";
        }
    }

    /**
     * Băm chuỗi dữ liệu bằng thuật toán HMAC-SHA256 (Chuẩn Ví điện tử MoMo).
     *
     * @param key  Khóa bí mật (Secret Key)
     * @param data Chuỗi dữ liệu cần băm
     * @return Chuỗi Hexadecimal (viết thường)
     */
    public static String hmacSHA256(String key, String data) {
        if (key == null || data == null) {
            return "";
        }
        try {
            Mac hmac256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac256.init(secretKey);
            byte[] result = hmac256.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            log.error("Lỗi khi băm HMAC-SHA256: {}", ex.getMessage());
            return "";
        }
    }
}
