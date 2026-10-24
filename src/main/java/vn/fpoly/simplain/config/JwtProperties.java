package vn.fpoly.simplain.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình thuộc tính cho JWT Token.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "simplain.jwt")
public class JwtProperties {

    /**
     * Khóa bí mật dùng để ký và verify JWT (tối thiểu 256 bits).
     */
    private String secret = "simplain-secret-key-for-jwt-token-authentication-secure-256-bits-2026!";

    /**
     * Thời gian sống của Token tính bằng mili-giây (mặc định 24h = 86400000 ms).
     */
    private long expirationMs = 86400000L;

    /**
     * Tên đơn vị phát hành token.
     */
    private String issuer = "simplain-auth";
}
