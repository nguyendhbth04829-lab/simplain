package vn.fpoly.simplain.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cấu hình bảo mật và mã hóa mật khẩu cho hệ thống Simplain.
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean mã hóa mật khẩu theo thuật toán BCrypt với độ an toàn cao (12 vòng băm).
     *
     * @return đối tượng PasswordEncoder mã hóa BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Bean xử lý JSON ObjectMapper dùng chung.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
