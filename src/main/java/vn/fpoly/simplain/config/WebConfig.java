package vn.fpoly.simplain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.fpoly.simplain.interceptor.AdminPaymentInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminPaymentInterceptor adminPaymentInterceptor;

    @Autowired
    public WebConfig(AdminPaymentInterceptor adminPaymentInterceptor) {
        this.adminPaymentInterceptor = adminPaymentInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminPaymentInterceptor)
                .addPathPatterns("/admin/phuong-thuc-thanh-toan/**", "/admin/giao-dich-thanh-toan/**");
    }

}
