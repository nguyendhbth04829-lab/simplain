package vn.fpoly.simplain.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.fpoly.simplain.entity.KhachHang;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Tiện ích xử lý cấp phát, ký số và xác minh JWT Token cho Khách hàng.
 */
@Component
public class JwtUtil {

    /**
     * Khóa bí mật dùng để ký JWT (tối thiểu 256 bits cho HS256).
     */
    @Value("${jwt.secret:SimplainSecretKeyForCustomerAuthenticationMustBeLongEnough2026!}")
    private String secretKey;

    /**
     * Thời gian sống của JWT Token (mặc định 24 giờ tính bằng mili-giây).
     */
    @Value("${jwt.expiration:86400000}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Cấp phát JWT token từ thực thể Khách hàng.
     *
     * @param khachHang thông tin khách hàng
     * @return chuỗi JWT token
     */
    public String generateToken(KhachHang khachHang) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", khachHang.getId());
        claims.put("maKhachHang", khachHang.getMaKhachHang());
        claims.put("hoTen", khachHang.getHoTen());
        claims.put("email", khachHang.getEmail());
        claims.put("soDienThoai", khachHang.getSoDienThoai());
        claims.put("role", "ROLE_KHACH_HANG");
        claims.put("userType", "KHACH_HANG");

        String subject = (khachHang.getEmail() != null && !khachHang.getEmail().isBlank())
                ? khachHang.getEmail()
                : khachHang.getSoDienThoai();

        return generateToken(subject, claims);
    }

    /**
     * Cấp phát JWT token từ thực thể Nhân viên với phân quyền Role-based.
     *
     * @param nhanVien thông tin nhân viên
     * @return chuỗi JWT token
     */
    public String generateToken(vn.fpoly.simplain.entity.NhanVien nhanVien) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", nhanVien.getId());
        claims.put("maNhanVien", nhanVien.getMaNhanVien());
        claims.put("hoTen", nhanVien.getHoTen());
        claims.put("tenDangNhap", nhanVien.getTenDangNhap());
        String role = (nhanVien.getVaiTro() != null) ? nhanVien.getVaiTro().getMaVaiTro() : "ROLE_NHAN_VIEN";
        String tenVaiTro = (nhanVien.getVaiTro() != null) ? nhanVien.getVaiTro().getTenVaiTro() : "Nhân viên";
        claims.put("role", role);
        claims.put("tenVaiTro", tenVaiTro);
        claims.put("userType", "NHAN_VIEN");

        return generateToken(nhanVien.getTenDangNhap(), claims);
    }

    /**
     * Cấp phát token với subject và các claims tùy biến.
     *
     * @param subject đối tượng định danh
     * @param claims các thuộc tính mở rộng
     * @return chuỗi JWT token
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Giải mã và lấy toàn bộ Claims từ token.
     *
     * @param token chuỗi JWT token
     * @return Claims chứa dữ liệu payload
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Lấy Subject (email/sđt) từ token.
     *
     * @param token chuỗi JWT token
     * @return Subject
     */
    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Lấy ID khách hàng từ claims trong token.
     *
     * @param token chuỗi JWT token
     * @return ID khách hàng hoặc null
     */
    public Long extractCustomerId(String token) {
        Object idObj = extractAllClaims(token).get("id");
        if (idObj instanceof Number) {
            return ((Number) idObj).longValue();
        }
        return null;
    }

    /**
     * Lấy mã khách hàng từ claims trong token.
     *
     * @param token chuỗi JWT token
     * @return mã khách hàng
     */
    public String extractMaKhachHang(String token) {
        Object val = extractAllClaims(token).get("maKhachHang");
        return val != null ? val.toString() : null;
    }

    /**
     * Lấy vai trò (Role) từ claims trong token.
     *
     * @param token chuỗi JWT token
     * @return mã vai trò (ADMIN, CSKH, BAN_HANG, ...)
     */
    public String extractRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    /**
     * Lấy tên vai trò từ claims trong token.
     *
     * @param token chuỗi JWT token
     * @return tên vai trò (Quản trị viên, Chăm sóc khách hàng, ...)
     */
    public String extractTenVaiTro(String token) {
        Object ten = extractAllClaims(token).get("tenVaiTro");
        return ten != null ? ten.toString() : null;
    }

    /**
     * Kiểm tra tính hợp lệ và hạn dùng của token.
     *
     * @param token chuỗi JWT token
     * @return true nếu token hợp lệ và còn hạn, ngược lại false
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Kiểm tra token đã hết hạn chưa.
     *
     * @param token chuỗi JWT token
     * @return true nếu đã hết hạn, ngược lại false
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }
}
