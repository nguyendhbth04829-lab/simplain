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
import vn.fpoly.simplain.dto.AuthResponse;
import vn.fpoly.simplain.dto.CustomerProfileResponse;
import vn.fpoly.simplain.dto.LoginRequest;
import vn.fpoly.simplain.dto.RegisterRequest;
import vn.fpoly.simplain.service.AuthService;

/**
 * Controller điều hướng giao diện xác thực khách hàng hoàn toàn bằng Thymeleaf.
 * Hỗ trợ cả cơ chế form submit chuẩn của Thymeleaf (BindingResult, validation)
 * và lưu trữ JWT Token vào Cookie cho người dùng.
 */
@Tag(name = "Giao diện Web Xác thực Thymeleaf", description = "Các trang HTML Thymeleaf phong cách Coolmate")
@Controller
public class AuthViewController {

    private final AuthService authService;

    public AuthViewController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Mở giao diện đăng nhập Thymeleaf.
     */
    @Operation(summary = "Mở giao diện đăng nhập Thymeleaf")
    @GetMapping("/auth/login")
    public String showLoginPage(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginRequest());
        }
        model.addAttribute("pageTitle", "Đăng nhập — Simplain x Coolmate");
        return "auth/login";
    }

    /**
     * Xử lý đăng nhập bằng form submit chuẩn Thymeleaf.
     */
    @Operation(summary = "Xử lý form đăng nhập Thymeleaf")
    @PostMapping("/auth/login")
    public String handleLogin(@Valid @ModelAttribute("loginForm") LoginRequest loginForm,
                              BindingResult bindingResult,
                              HttpServletResponse response,
                              Model model) {
        model.addAttribute("pageTitle", "Đăng nhập — Simplain x Coolmate");

        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            AuthResponse authResponse = authService.login(loginForm);
            // Lưu JWT Token vào Cookie
            Cookie tokenCookie = new Cookie("simplain_token", authResponse.getToken());
            tokenCookie.setPath("/");
            tokenCookie.setMaxAge(86400); // 24 giờ
            tokenCookie.setHttpOnly(false);
            response.addCookie(tokenCookie);

            return "redirect:/account";
        } catch (ResponseStatusException ex) {
            model.addAttribute("errorMessage", ex.getReason());
            return "auth/login";
        }
    }

    /**
     * Mở giao diện đăng ký Thymeleaf.
     */
    @Operation(summary = "Mở giao diện đăng ký Thymeleaf")
    @GetMapping("/auth/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterRequest());
        }
        model.addAttribute("pageTitle", "Đăng ký thành viên — Simplain x Coolmate");
        return "auth/register";
    }

    /**
     * Xử lý đăng ký bằng form submit chuẩn Thymeleaf.
     */
    @Operation(summary = "Xử lý form đăng ký Thymeleaf")
    @PostMapping("/auth/register")
    public String handleRegister(@Valid @ModelAttribute("registerForm") RegisterRequest registerForm,
                                 BindingResult bindingResult,
                                 HttpServletResponse response,
                                 Model model) {
        model.addAttribute("pageTitle", "Đăng ký thành viên — Simplain x Coolmate");

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            AuthResponse authResponse = authService.register(registerForm);
            // Lưu JWT Token vào Cookie
            Cookie tokenCookie = new Cookie("simplain_token", authResponse.getToken());
            tokenCookie.setPath("/");
            tokenCookie.setMaxAge(86400);
            tokenCookie.setHttpOnly(false);
            response.addCookie(tokenCookie);

            return "redirect:/account";
        } catch (ResponseStatusException ex) {
            model.addAttribute("errorMessage", ex.getReason());
            return "auth/register";
        }
    }

    /**
     * Mở giao diện thông tin tài khoản hội viên qua Thymeleaf.
     */
    @Operation(summary = "Mở giao diện thông tin tài khoản hội viên")
    @GetMapping("/account")
    public String showAccountPage(@CookieValue(name = "simplain_token", required = false) String cookieToken,
                                  Model model) {
        model.addAttribute("pageTitle", "Tài khoản của tôi — Simplain Club");

        if (cookieToken != null && !cookieToken.isBlank()) {
            try {
                CustomerProfileResponse profile = authService.getCurrentUserProfile("Bearer " + cookieToken);
                model.addAttribute("user", profile);
            } catch (Exception ignored) {
                // Token cookie không hợp lệ hoặc hết hạn
            }
        }

        return "account/profile";
    }

    /**
     * Đăng xuất tài khoản, xóa Cookie JWT Token và quay về trang đăng nhập.
     */
    @Operation(summary = "Đăng xuất tài khoản khách hàng")
    @GetMapping("/auth/logout")
    public String handleLogout(HttpServletResponse response) {
        Cookie tokenCookie = new Cookie("simplain_token", "");
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0);
        response.addCookie(tokenCookie);
        return "redirect:/auth/login";
    }

    /**
     * Điều hướng mặc định của /auth sang trang đăng nhập.
     */
    @GetMapping("/auth")
    public String redirectToLogin() {
        return "redirect:/auth/login";
    }
}
