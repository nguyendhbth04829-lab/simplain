package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.fpoly.simplain.config.JwtProperties;
import vn.fpoly.simplain.dto.StaffLoginRequest;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.service.AuthServiceTest;
import vn.fpoly.simplain.service.DashboardService;
import vn.fpoly.simplain.service.DashboardServiceTest;
import vn.fpoly.simplain.service.JwtService;
import vn.fpoly.simplain.service.StaffAuthService;
import vn.fpoly.simplain.service.StaffAuthServiceTest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Kiểm thử MockMvc điều hướng giao diện Web CMS Thymeleaf (CmsViewController).
 * Đảm bảo: CHỈ CÓ ADMIN MỚI CÓ THỂ XEM DASHBOARD TỔNG QUAN.
 */
@DisplayName("Kiểm thử MockMvc Phân quyền Giao diện CMS (CmsViewController)")
public class CmsViewControllerTest {

    private MockMvc mockMvc;
    private StaffAuthService staffAuthService;
    private DashboardService dashboardService;
    private String adminToken;
    private String cskhToken;

    @BeforeEach
    void setUp() {
        StaffAuthServiceTest.InMemoryNhanVienRepository nhanVienRepository = new StaffAuthServiceTest.InMemoryNhanVienRepository();
        StaffAuthServiceTest.InMemoryVaiTroRepository vaiTroRepository = new StaffAuthServiceTest.InMemoryVaiTroRepository();
        DashboardServiceTest.InMemoryDonHangRepository donHangRepository = new DashboardServiceTest.InMemoryDonHangRepository();
        DashboardServiceTest.InMemoryTrangThaiDonHangRepository trangThaiDonHangRepository = new DashboardServiceTest.InMemoryTrangThaiDonHangRepository();
        AuthServiceTest.InMemoryKhachHangRepository khachHangRepository = new AuthServiceTest.InMemoryKhachHangRepository();

        khachHangRepository.save(KhachHang.builder().maKhachHang("KH001").hoTen("Khách Mua").build());
        nhanVienRepository.save(NhanVien.builder().maNhanVien("NV001").hoTen("Admin").build());

        dashboardService = new DashboardService(
                donHangRepository,
                trangThaiDonHangRepository,
                khachHangRepository,
                nhanVienRepository
        );
        dashboardService.initSampleDashboardData();

        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-for-simplain-auth-service-256-bits-super-secure!");
        jwtProperties.setExpirationMs(3600000L);
        JwtService jwtService = new JwtService(jwtProperties);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        staffAuthService = new StaffAuthService(
                nhanVienRepository,
                vaiTroRepository,
                jwtService,
                passwordEncoder,
                jwtProperties
        );
        staffAuthService.initDefaultStaffData();

        adminToken = staffAuthService.login(StaffLoginRequest.builder()
                .tenDangNhap("superadmin")
                .matKhau("Admin@123")
                .build()).getToken();

        cskhToken = staffAuthService.login(StaffLoginRequest.builder()
                .tenDangNhap("cskh_01")
                .matKhau("Staff@123")
                .build()).getToken();

        CmsViewController controller = new CmsViewController(staffAuthService, dashboardService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("VIEW TC01 - Chưa đăng nhập truy cập /admin: Tự động chuyển hướng về trang /admin/login")
    void testShowDashboard_NoToken_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login"));
    }

    @Test
    @DisplayName("VIEW TC02 - Quản trị viên (ADMIN) truy cập /admin: Hiển thị giao diện Dashboard thành công")
    void testShowDashboard_AdminRole_ShowsDashboardView() throws Exception {
        mockMvc.perform(get("/admin")
                        .cookie(new Cookie(CmsViewController.CMS_COOKIE_NAME, adminToken)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("staff"))
                .andExpect(model().attributeExists("dashboard"))
                .andExpect(model().attribute("pageTitle", containsString("CMS Bảng Điều Khiển Quản Trị")));
    }

    @Test
    @DisplayName("VIEW TC03 - Nhân viên CSKH truy cập /admin: Bị từ chối (403 Forbidden) và hiển thị trang access-denied")
    void testShowDashboard_CskhRole_ShowsAccessDeniedView() throws Exception {
        mockMvc.perform(get("/admin")
                        .cookie(new Cookie(CmsViewController.CMS_COOKIE_NAME, cskhToken)))
                .andExpect(status().isForbidden())
                .andExpect(view().name("admin/access-denied"))
                .andExpect(model().attributeExists("staff"))
                .andExpect(model().attributeExists("deniedReason"))
                .andExpect(model().attribute("pageTitle", containsString("Truy cập bị từ chối")));
    }

    @Test
    @DisplayName("VIEW TC04 - Nhân viên CSKH truy cập /admin/dashboard: Bị từ chối (403 Forbidden)")
    void testShowDashboard_CskhRole_AccessDenied_RouteAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard")
                        .cookie(new Cookie(CmsViewController.CMS_COOKIE_NAME, cskhToken)))
                .andExpect(status().isForbidden())
                .andExpect(view().name("admin/access-denied"));
    }

    @Test
    @DisplayName("VIEW TC05 - Mở trang đăng nhập /admin/login: Trả về 200 OK và view admin/login")
    void testShowCmsLoginPage() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attributeExists("loginForm"));
    }

    @Test
    @DisplayName("VIEW TC06 - Đăng xuất /admin/logout: Xóa cookie và chuyển hướng về /admin/login?logout=true")
    void testHandleCmsLogout() throws Exception {
        mockMvc.perform(get("/admin/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login?logout=true"));
    }
}
