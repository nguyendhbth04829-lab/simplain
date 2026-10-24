package vn.fpoly.simplain.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.fpoly.simplain.config.JwtProperties;
import vn.fpoly.simplain.dto.VerifyTokenResponse;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.VaiTro;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Dịch vụ xử lý cấp phát, phân tích và xác minh JWT Token cho khách hàng.
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Cấp phát JWT Token từ thông tin thực thể khách hàng.
     *
     * @param khachHang Thông tin khách hàng đã xác thực
     * @return Chuỗi JWT Token ký điện tử HS256
     */
    public String generateToken(KhachHang khachHang) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", khachHang.getId());
        extraClaims.put("maKhachHang", khachHang.getMaKhachHang());
        extraClaims.put("hoTen", khachHang.getHoTen());
        extraClaims.put("email", khachHang.getEmail());
        extraClaims.put("soDienThoai", khachHang.getSoDienThoai());
        extraClaims.put("vaiTro", "CUSTOMER");

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(khachHang.getMaKhachHang())
                .claims(extraClaims)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Cấp phát JWT Token cho Nhân viên đăng nhập CMS quản trị (US02).
     * Gắn thông tin phân quyền vai trò (Role-based claims): ADMIN, CSKH, BAN_HANG,...
     *
     * @param nhanVien Thực thể nhân viên đã xác thực
     * @param vaiTro Thực thể vai trò tương ứng của nhân viên
     * @return Chuỗi JWT Token ký số HS256
     */
    public String generateStaffToken(NhanVien nhanVien, VaiTro vaiTro) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", nhanVien.getId());
        extraClaims.put("maNhanVien", nhanVien.getMaNhanVien());
        extraClaims.put("hoTen", nhanVien.getHoTen());
        extraClaims.put("tenDangNhap", nhanVien.getTenDangNhap());
        extraClaims.put("email", nhanVien.getEmail());
        extraClaims.put("soDienThoai", nhanVien.getSoDienThoai());
        extraClaims.put("userType", "STAFF");
        extraClaims.put("maVaiTro", vaiTro != null ? vaiTro.getMaVaiTro() : "STAFF");
        extraClaims.put("tenVaiTro", vaiTro != null ? vaiTro.getTenVaiTro() : "");
        extraClaims.put("vaiTro", vaiTro != null ? vaiTro.getMaVaiTro() : "STAFF");

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(nhanVien.getMaNhanVien())
                .claims(extraClaims)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Xác minh tính hợp lệ và trích xuất dữ liệu từ JWT Token.
     *
     * @param token Chuỗi JWT Token
     * @return Kết quả xác minh chi tiết
     */
    public VerifyTokenResponse verifyToken(String token) {
        try {
            Claims claims = extractAllClaims(token);

            String maKhachHang = claims.get("maKhachHang", String.class);
            if (maKhachHang == null) {
                maKhachHang = claims.getSubject();
            }

            return VerifyTokenResponse.builder()
                    .valid(true)
                    .maKhachHang(maKhachHang)
                    .email(claims.get("email", String.class))
                    .hoTen(claims.get("hoTen", String.class))
                    .vaiTro(claims.get("vaiTro", String.class))
                    .expiresAt(claims.getExpiration())
                    .message("JWT Token hợp lệ và còn hiệu lực.")
                    .build();

        } catch (ExpiredJwtException e) {
            log.warn("JWT Token đã hết hạn: {}", e.getMessage());
            return VerifyTokenResponse.builder()
                    .valid(false)
                    .expiresAt(e.getClaims() != null ? e.getClaims().getExpiration() : null)
                    .message("JWT Token đã hết hạn sử dụng. Vui lòng đăng nhập lại.")
                    .build();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT Token không hợp lệ hoặc bị can thiệp: {}", e.getMessage());
            return VerifyTokenResponse.builder()
                    .valid(false)
                    .message("JWT Token không hợp lệ, chữ ký số không khớp hoặc định dạng sai.")
                    .build();
        }
    }

    /**
     * Trích xuất mã khách hàng (Subject) từ JWT Token.
     *
     * @param token Chuỗi token
     * @return Mã khách hàng
     */
    public String extractMaKhachHang(String token) {
        Claims claims = extractAllClaims(token);
        String code = claims.get("maKhachHang", String.class);
        return code != null ? code : claims.getSubject();
    }

    /**
     * Kiểm tra xem Token có hợp lệ và chưa hết hạn không.
     *
     * @param token Chuỗi token
     * @return true nếu token hợp lệ
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Trích xuất toàn bộ Claims trong Token.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(cleanToken(token))
                .getPayload();
    }

    /**
     * Chuẩn hóa token nếu có tiền tố Bearer.
     */
    public String cleanToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return token != null ? token.trim() : "";
    }

    public long getExpirationMs() {
        return jwtProperties.getExpirationMs();
    }
}
