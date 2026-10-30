package vn.fpoly.simplain.controller.advice;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.fpoly.simplain.controller.NhanVienController;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.enums.ChucNangHeThong;
import vn.fpoly.simplain.service.NhanVienService;

import java.util.HashSet;
import java.util.Set;

/**
 * Controller Advice tự động nạp thông tin Nhân viên và Tập quyền hạn (quyenSet)
 * vào Model của tất cả các trang quản trị CMS (/admin/**).
 */
@ControllerAdvice(basePackages = "vn.fpoly.simplain.controller")
public class AdminGlobalModelAttributes {

    private final NhanVienService nhanVienService;

    @Autowired
    public AdminGlobalModelAttributes(NhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    @ModelAttribute("nhanVienHienTai")
    public XacThucNhanVienResponse napNhanVienHienTai(HttpServletRequest request, HttpSession session) {
        String uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/admin")) {
            return null;
        }

        // Ưu tiên session
        if (session != null) {
            Object sessionUser = session.getAttribute("nhanVienHienTai");
            if (sessionUser instanceof XacThucNhanVienResponse) {
                return (XacThucNhanVienResponse) sessionUser;
            }
        }

        // Kiểm tra cookie JWT
        String token = layTokenTuCookie(request);
        if (token != null) {
            try {
                XacThucNhanVienResponse user = nhanVienService.layThongTinNhanVienTuToken(token);
                if (session != null) {
                    session.setAttribute("nhanVienHienTai", user);
                }
                return user;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @ModelAttribute("nhanVien")
    public XacThucNhanVienResponse napNhanVien(HttpServletRequest request, HttpSession session) {
        return napNhanVienHienTai(request, session);
    }

    @ModelAttribute("quyenSet")
    public Set<String> napQuyenSet(HttpServletRequest request, HttpSession session) {
        String uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/admin")) {
            return new HashSet<>();
        }

        XacThucNhanVienResponse user = napNhanVienHienTai(request, session);
        Set<String> set = new HashSet<>();
        if (user != null) {
            if ("ADMIN".equalsIgnoreCase(user.getMaVaiTro())) {
                set.add("ALL");
                for (ChucNangHeThong cn : ChucNangHeThong.values()) {
                    set.add(cn.getMaQuyen());
                }
            } else if (user.getDanhSachQuyen() != null) {
                set.addAll(user.getDanhSachQuyen());
            }
        }
        return set;
    }

    private String layTokenTuCookie(HttpServletRequest request) {
        if (request == null || request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (NhanVienController.COOKIE_ADMIN_TOKEN.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
