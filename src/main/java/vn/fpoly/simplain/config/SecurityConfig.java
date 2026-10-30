package vn.fpoly.simplain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cấu hình bảo mật và mã hóa mật khẩu cho hệ thống.
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean mã hóa mật khẩu theo thuật toán BCrypt.
     *
     * @return đối tượng PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
