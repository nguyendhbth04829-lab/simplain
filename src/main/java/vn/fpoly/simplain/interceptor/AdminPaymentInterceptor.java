package vn.fpoly.simplain.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.service.NhanVienService;

@Component
public class AdminPaymentInterceptor implements HandlerInterceptor {

    private final NhanVienService nhanVienService;

    public AdminPaymentInterceptor(NhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_admin_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null || token.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Cần đăng nhập tài khoản nhân viên.");
            return false;
        }

        XacThucNhanVienResponse nhanVien;
        try {
            nhanVien = nhanVienService.layThongTinNhanVienTuToken(token);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Phiên đăng nhập không hợp lệ hoặc đã hết hạn.");
            return false;
        }

        if (nhanVien == null || (!"ADMIN".equalsIgnoreCase(nhanVien.getMaVaiTro())
                && (nhanVien.getDanhSachQuyen() == null || !nhanVien.getDanhSachQuyen().contains("THANH_TOAN")))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Tài khoản không có quyền THANH_TOAN.");
            return false;
        }
        return true;
    }
}
