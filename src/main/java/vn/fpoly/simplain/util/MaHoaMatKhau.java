/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - Tiện ích băm và xác thực mật khẩu chuẩn SHA-256
 * Cập nhật xác nhận commit US12: 24/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Tiện ích băm và đối chiếu mật khẩu sử dụng thuật toán chuẩn SHA-256.
 */
@Component
public class MaHoaMatKhau {

    /**
     * Băm mật khẩu thô sang chuỗi hex SHA-256 (64 ký tự).
     *
     * @param matKhauTho mật khẩu chưa băm
     * @return chuỗi hex băm
     */
    public String maHoa(String matKhauTho) {
        if (matKhauTho == null) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(matKhauTho.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Không tìm thấy thuật toán mã hóa SHA-256", e);
        }
    }

    /**
     * Đối chiếu mật khẩu thô với chuỗi băm trong CSDL.
     *
     * @param matKhauTho mật khẩu người dùng nhập
     * @param matKhauDaMaHoa chuỗi băm lưu trong CSDL
     * @return true nếu khớp, false nếu sai
     */
    public boolean kiemTra(String matKhauTho, String matKhauDaMaHoa) {
        if (matKhauTho == null || matKhauDaMaHoa == null) {
            return false;
        }
        String chuoiBam = maHoa(matKhauTho);
        return MessageDigest.isEqual(
                chuoiBam.getBytes(StandardCharsets.UTF_8),
                matKhauDaMaHoa.getBytes(StandardCharsets.UTF_8)
        );
    }
}
