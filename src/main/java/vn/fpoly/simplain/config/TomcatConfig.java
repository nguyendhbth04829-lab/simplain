package vn.fpoly.simplain.config;

import jakarta.servlet.MultipartConfigElement;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình chuyên sâu cho Tomcat Connector trong Spring Boot 4.
 * Nâng giới hạn maxPartCount lên 10000 (mặc định Tomcat chỉ có 50 parts khiến form nhiều sản phẩm combo/flash sale bị lỗi).
 * Nâng maxParameterCount lên 20000 và maxPostSize lên 200MB.
 */
@Configuration
public class TomcatConfig {

    private static final Logger log = LoggerFactory.getLogger(TomcatConfig.class);

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers((TomcatConnectorCustomizer) (Connector connector) -> {
                connector.setMaxPartCount(10000);
                connector.setMaxParameterCount(20000);
                connector.setMaxPostSize(200 * 1024 * 1024);
                if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?> protocol) {
                    protocol.setMaxSwallowSize(200 * 1024 * 1024);
                    protocol.setMaxSavePostSize(200 * 1024 * 1024);
                }
                log.info("Đã cấu hình Tomcat Connector: maxPartCount=10000, maxParameterCount=20000, maxPostSize=200MB");
            });
        };
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement(null, 50L * 1024 * 1024, 200L * 1024 * 1024, 2 * 1024 * 1024);
    }
}
