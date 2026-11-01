package vn.fpoly.simplain.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.fpoly.simplain.interceptor.PhanQuyenAdminInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final PhanQuyenAdminInterceptor phanQuyenAdminInterceptor;

    @Autowired
    public WebConfig(PhanQuyenAdminInterceptor phanQuyenAdminInterceptor) {
        this.phanQuyenAdminInterceptor = phanQuyenAdminInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(phanQuyenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/dang-nhap", "/admin/dang-xuat", "/admin/quen-mat-khau", "/admin/quen-mat-khau/**", "/uploads/**");
    }
}
