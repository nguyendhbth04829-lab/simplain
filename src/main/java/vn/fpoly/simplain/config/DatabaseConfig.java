package vn.fpoly.simplain.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Cấu hình kết nối cơ sở dữ liệu linh hoạt.
 * Kết nối SQL Server Simpl4 nếu có sẵn, hoặc tự động kích hoạt H2 in-memory
 * nếu SQL Server chưa được bật để đảm bảo hệ thống luôn khởi động thành công.
 */
@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:}")
    private String dbUrl;

    @Value("${spring.datasource.username:sa}")
    private String dbUsername;

    @Value("${spring.datasource.password:123}")
    private String dbPassword;

    /**
     * Khởi tạo DataSource thông minh: tự nhận diện SQL Server hoặc H2 fallback.
     *
     * @return DataSource hoạt động ổn định
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        if (dbUrl != null && dbUrl.contains("sqlserver") && isSqlServerAvailable("localhost", 1433)) {
            log.info("[DATABASE] Đã kết nối thành công tới Microsoft SQL Server (database: Simpl4)");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            config.setUsername(dbUsername);
            config.setPassword(dbPassword);
            config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            config.setMaximumPoolSize(10);
            return new HikariDataSource(config);
        }

        log.info("[DATABASE] SQL Server (localhost:1433) chưa bật. Tự động dùng H2 in-memory database để app chạy ổn định!");
        HikariConfig h2Config = new HikariConfig();
        h2Config.setJdbcUrl("jdbc:h2:mem:simpl4;DB_CLOSE_DELAY=-1;MODE=MSSQLServer");
        h2Config.setDriverClassName("org.h2.Driver");
        h2Config.setUsername("sa");
        h2Config.setPassword("");
        h2Config.setMaximumPoolSize(10);
        return new HikariDataSource(h2Config);
    }

    private boolean isSqlServerAvailable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1200);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
