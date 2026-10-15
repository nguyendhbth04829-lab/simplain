package vn.fpoly.simplain.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình tài liệu API (Swagger giống FastAPI docs).
 * Không cần cấu hình thêm: mở /docs là dùng được ngay.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Tiêu đề và mô tả hiện trên trang Swagger.
     *
     * @return thông tin OpenAPI tiếng Việt
     */
    @Bean
    public OpenAPI openApiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Simplain API")
                        .description("Tài liệu API dùng chung cho cả nhóm. "
                                + "Mở /docs để gọi thử như FastAPI docs.")
                        .version("0.0.1")
                        .contact(new Contact().name("Nhóm Simplain")));
    }
}
