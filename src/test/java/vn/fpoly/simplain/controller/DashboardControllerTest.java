package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.fpoly.simplain.config.GlobalExceptionHandler;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Kiểm thử MockMvc REST API Dashboard tổng quan US05 (DashboardController).
 * Đảm bảo tiêu chí: CHỈ CÓ ADMIN MỚI ĐƯỢC XEM DASHBOARD TỔNG QUAN.
 */
@DisplayName("Kiểm thử MockMvc REST API Dashboard US05 - Phân quyền Admin")
public class DashboardControllerTest {

    private MockMvc mockMvc;
    private DashboardService dashboardService;
    private StaffAuthService staffAuthService;
    private String adminToken;
    private String cskhToken;

    @BeforeEach
    void setUp() {
        DashboardServiceTest.InMemoryDonHangRepository donHangRepository = new DashboardServiceTest.InMemoryDonHangRepository();
        DashboardServiceTest.InMemoryTrangThaiDonHangRepository trangThaiDonHangRepository = new DashboardServiceTest.InMemoryTrangThaiDonHangRepository();
        AuthServiceTest.InMemoryKhachHangRepository khachHangRepository = new AuthServiceTest.InMemoryKhachHangRepository();
        StaffAuthServiceTest.InMemoryNhanVienRepository nhanVienRepository = new StaffAuthServiceTest.InMemoryNhanVienRepository();
        StaffAuthServiceTest.InMemoryVaiTroRepository vaiTroRepository = new StaffAuthServiceTest.InMemoryVaiTroRepository();

        dashboardService = new DashboardService(
                donHangRepository,
                trangThaiDonHangRepository,
                khachHangRepository,
                nhanVienRepository
        );

        khachHangRepository.save(KhachHang.builder().maKhachHang("KH001").hoTen("Trần Mua Hàng").build());
        nhanVienRepository.save(NhanVien.builder().maNhanVien("NV001").hoTen("Admin").build());
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

        // Lấy token Quản trị viên (ADMIN)
        adminToken = staffAuthService.login(StaffLoginRequest.builder()
                .tenDangNhap("superadmin")
                .matKhau("Admin@123")
                .build()).getToken();

        // Lấy token Chăm sóc khách hàng (CSKH)
        cskhToken = staffAuthService.login(StaffLoginRequest.builder()
                .tenDangNhap("cskh_01")
                .matKhau("Staff@123")
                .build()).getToken();

        DashboardController controller = new DashboardController(dashboardService, staffAuthService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("API TC01 - GET /api/admin/dashboard/overview với Bearer Token ADMIN: Thành công 200 OK")
    void testGetDashboardOverview_AdminHeader_Success() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/overview")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Quản trị viên")))
                .andExpect(jsonPath("$.data.tongNguoiDung", is(5))) // 1 khách hàng + 4 nhân viên seed
                .andExpect(jsonPath("$.data.donHangHomNay", notNullValue()))
                .andExpect(jsonPath("$.data.doanhThuHomNay", notNullValue()))
                .andExpect(jsonPath("$.data.formattedDoanhThuHomNay", notNullValue()))
                .andExpect(jsonPath("$.data.bieuDoDoanhThu7Ngay", hasSize(7)))
                .andExpect(jsonPath("$.data.thongKeTrangThaiDonHang", notNullValue()))
                .andExpect(jsonPath("$.data.donHangMoiNhat", notNullValue()));
    }

    @Test
    @DisplayName("API TC02 - GET /api/admin/dashboard/overview với Cookie simplain_cms_token ADMIN: Thành công 200 OK")
    void testGetDashboardOverview_AdminCookie_Success() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/overview")
                        .cookie(new Cookie("simplain_cms_token", adminToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.tongNguoiDung", notNullValue()));
    }

    @Test
    @DisplayName("API TC03 - GET /api/admin/dashboard/overview với Bearer Token CSKH: Bị từ chối 403 Forbidden")
    void testGetDashboardOverview_NonAdminRole_Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/overview")
                        .header("Authorization", "Bearer " + cskhToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Từ chối truy cập: Chức năng Dashboard tổng quan chỉ dành riêng cho Quản trị viên (ADMIN)")));
    }

    @Test
    @DisplayName("API TC04 - GET /api/admin/dashboard/overview không truyền Token: Bị từ chối 401 Unauthorized")
    void testGetDashboardOverview_MissingToken_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/overview")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Vui lòng cung cấp JWT Token")));
    }
}
