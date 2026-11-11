package vn.fpoly.simplain.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter loại bỏ hoàn toàn cơ chế tự động chèn ";jsessionid=..." vào URL của Servlet Container (Tomcat) và Thymeleaf.
 * Đảm bảo 100% URL hiển thị sạch sẽ, chuẩn SEO và không bao giờ xuất hiện chuỗi session rác.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DisableUrlSessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(httpResponse) {
            @Override
            public String encodeRedirectURL(String url) {
                return url;
            }

            @Override
            public String encodeURL(String url) {
                return url;
            }
        };

        chain.doFilter(request, wrappedResponse);
    }
}
