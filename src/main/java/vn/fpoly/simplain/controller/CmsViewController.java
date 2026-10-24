package vn.fpoly.simplain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import vn.fpoly.simplain.dto.DashboardOverviewResponse;
import vn.fpoly.simplain.dto.StaffAuthResponse;
import vn.fpoly.simplain.dto.StaffLoginRequest;
import vn.fpoly.simplain.dto.StaffProfileResponse;
import vn.fpoly.simplain.service.DashboardService;
import vn.fpoly.simplain.service.StaffAuthService;

/**
 * Controller điều hướng giao diện Web Quản trị CMS cho Nhân viên (US02, US05) bằng Thymeleaf thuần.
 * Hỗ trợ đăng nhập, lưu trữ token JWT trong Cookie, kiểm tra phân quyền (Role-based)
 * và hiển thị Biểu đồ Dashboard tổng quan doanh thu, đơn hàng hôm nay và người dùng.
 */
@Tag(name = "Giao diện Web Quản trị CMS Thymeleaf", description = "Các trang HTML Thymeleaf cho hệ thống CMS quản trị nội bộ")
@Controller
public class CmsViewController {

    public static final String CMS_COOKIE_NAME = "simplain_cms_token";

    private final StaffAuthService staffAuthService;
    private final DashboardService dashboardService;

    public CmsViewController(StaffAuthService staffAuthService, DashboardService dashboardService) {
        this.staffAuthService = staffAuthService;
        this.dashboardService = dashboardService;
    }

    /**
     * Mở giao diện trang Đăng nhập CMS quản trị.
     */
    @Operation(summary = "Mở giao diện Đăng nhập CMS quản trị")
    @GetMapping({"/admin/login", "/cms/login"})
    public String showCmsLoginPage(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new StaffLoginRequest());
        }
        model.addAttribute("pageTitle", "Đăng nhập Hệ thống Quản trị CMS — Simplain Admin");
        return "admin/login";
    }

    /**
     * Xử lý đăng nhập CMS bằng Form POST Thymeleaf chuẩn (không dùng JS).
     */
    @Operation(summary = "Xử lý form Đăng nhập CMS")
    @PostMapping({"/admin/login", "/cms/login"})
    public String handleCmsLogin(@Valid @ModelAttribute("loginForm") StaffLoginRequest loginForm,
                                 BindingResult bindingResult,
                                 HttpServletResponse response,
                                 Model model) {
        model.addAttribute("pageTitle", "Đăng nhập Hệ thống Quản trị CMS — Simplain Admin");

        if (bindingResult.hasErrors()) {
            return "admin/login";
        }

        try {
            StaffAuthResponse authResponse = staffAuthService.login(loginForm);

            // Lưu JWT Token vào Cookie trình duyệt cho phân vùng CMS
            Cookie tokenCookie = new Cookie(CMS_COOKIE_NAME, authResponse.getToken());
            tokenCookie.setPath("/");
            tokenCookie.setMaxAge(86400); // 24 giờ
            tokenCookie.setHttpOnly(false);
            response.addCookie(tokenCookie);

            return "redirect:/admin";
        } catch (ResponseStatusException ex) {
            model.addAttribute("errorMessage", ex.getReason());
            return "admin/login";
        }
    }

    /**
     * Trang Tổng quan CMS Dashboard quản trị.
     * Tự động kiểm tra Token JWT từ Cookie, xác thực vai trò và trạng thái nhân viên.
     * PHÂN QUYỀN NGHIÊM NGẶT (US05): CHỈ CÓ QUẢN TRỊ VIÊN (ADMIN) MỚI CÓ THỂ XEM DASHBOARD TỔNG QUAN.
     * Các vai trò khác (CSKH, BAN_HANG, KHO,...) sẽ bị từ chối truy cập và chuyển đến màn hình 403 Forbidden.
     */
    @Operation(summary = "Mở giao diện CMS Dashboard quản trị (Chỉ dành cho Admin)")
    @GetMapping({"/admin", "/cms", "/admin/dashboard"})
    public String showCmsDashboard(@CookieValue(value = CMS_COOKIE_NAME, required = false) String token,
                                   HttpServletResponse response,
                                   Model model) {
        if (token == null || token.isBlank()) {
            return "redirect:/admin/login";
        }

        try {
            StaffProfileResponse staff = staffAuthService.getStaffProfileFromToken(token);
            model.addAttribute("staff", staff);

            // Kiểm tra phân quyền: CHỈ CÓ ADMIN MỚI CÓ THỂ XEM DASHBOARD TỔNG QUAN
            if (!"ADMIN".equalsIgnoreCase(staff.getMaVaiTro())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                model.addAttribute("pageTitle", "Truy cập bị từ chối — Quyền hạn không đủ");
                model.addAttribute("deniedReason",
                        "Chỉ có tài khoản Quản trị viên (ADMIN) mới có quyền truy cập Bảng điều khiển Dashboard tổng quan và Báo cáo tài chính doanh thu hệ thống.");
                return "admin/access-denied";
            }

            model.addAttribute("pageTitle", "CMS Bảng Điều Khiển Quản Trị — " + staff.getTenVaiTro());

            // Thống kê Dashboard tổng quan (US05: Doanh thu trong ngày, Đơn hàng hôm nay, Người dùng, Biểu đồ)
            DashboardOverviewResponse dashboard = dashboardService.getDashboardOverview();
            model.addAttribute("dashboard", dashboard);

            return "admin/dashboard";
        } catch (Exception e) {
            return "redirect:/admin/login";
        }
    }

    /**
     * Đăng xuất khỏi hệ thống CMS: Xóa cookie JWT quản trị và chuyển hướng về trang đăng nhập.
     */
    @Operation(summary = "Đăng xuất khỏi hệ thống CMS")
    @GetMapping({"/admin/logout", "/cms/logout"})
    public String handleCmsLogout(HttpServletResponse response) {
        Cookie tokenCookie = new Cookie(CMS_COOKIE_NAME, "");
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0);
        response.addCookie(tokenCookie);
        return "redirect:/admin/login?logout=true";
    }
}
