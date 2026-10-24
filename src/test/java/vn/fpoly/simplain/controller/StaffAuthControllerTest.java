package vn.fpoly.simplain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.fpoly.simplain.config.GlobalExceptionHandler;
import vn.fpoly.simplain.config.JwtProperties;
import vn.fpoly.simplain.dto.StaffAuthResponse;
import vn.fpoly.simplain.dto.StaffLoginRequest;
import vn.fpoly.simplain.service.JwtService;
import vn.fpoly.simplain.service.StaffAuthService;
import vn.fpoly.simplain.service.StaffAuthServiceTest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Kiểm thử MockMvc REST API Xác thực Quản trị CMS US02 (StaffAuthController).
 */
@DisplayName("Kiểm thử MockMvc REST API Xác thực CMS US02 (StaffAuthController)")
public class StaffAuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private StaffAuthService staffAuthService;

    @BeforeEach
    void setUp() {
        StaffAuthServiceTest.InMemoryVaiTroRepository vaiTroRepository = new StaffAuthServiceTest.InMemoryVaiTroRepository();
        StaffAuthServiceTest.InMemoryNhanVienRepository nhanVienRepository = new StaffAuthServiceTest.InMemoryNhanVienRepository();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-for-simplain-auth-service-256-bits-super-secure!");
        jwtProperties.setExpirationMs(3600000L);
        JwtService jwtService = new JwtService(jwtProperties);

        staffAuthService = new StaffAuthService(
                nhanVienRepository,
                vaiTroRepository,
                jwtService,
                passwordEncoder,
                jwtProperties
        );
        staffAuthService.initDefaultStaffData();

        StaffAuthController controller = new StaffAuthController(staffAuthService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("API TC01 - POST /api/admin/auth/login: Đăng nhập thành công trả về 200 OK và JWT Token")
    void testLoginApi_Success() throws Exception {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("superadmin")
                .matKhau("Admin@123")
                .build();

        mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.maVaiTro", is("ADMIN")))
                .andExpect(jsonPath("$.data.maNhanVien", is("NV001")))
                .andExpect(jsonPath("$.data.trangThai", is(1)));
    }

    @Test
    @DisplayName("API TC02 - POST /api/admin/auth/login: Tiêu chí 2 - Chặn tài khoản bị khóa trả về 403 Forbidden")
    void testLoginApi_LockedAccount() throws Exception {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("nhanvien_khoa")
                .matKhau("Staff@123")
                .build();

        mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("bị khóa hoặc ngừng hoạt động")));
    }

    @Test
    @DisplayName("API TC03 - POST /api/admin/auth/login: Sai mật khẩu trả về 401 Unauthorized")
    void testLoginApi_WrongPassword() throws Exception {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("superadmin")
                .matKhau("WrongPassword123")
                .build();

        mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("không chính xác")));
    }

    @Test
    @DisplayName("API TC04 - POST /api/admin/auth/login: Thiếu trường bắt buộc trả về 400 Bad Request")
    void testLoginApi_MissingFields() throws Exception {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("")
                .matKhau("")
                .build();

        mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("API TC05 - GET /api/admin/auth/me: Lấy hồ sơ nhân viên thành công qua JWT Bearer Token")
    void testGetCurrentStaff_Success() throws Exception {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("cskh_01")
                .matKhau("Staff@123")
                .build();
        StaffAuthResponse authResponse = staffAuthService.login(request);

        mockMvc.perform(get("/api/admin/auth/me")
                        .header("Authorization", "Bearer " + authResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.maNhanVien", is("NV002")))
                .andExpect(jsonPath("$.data.maVaiTro", is("CSKH")));
    }

    @Test
    @DisplayName("API TC06 - GET /api/admin/auth/me: Không gửi Header Authorization trả về 401 Unauthorized")
    void testGetCurrentStaff_NoToken() throws Exception {
        mockMvc.perform(get("/api/admin/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)));
    }
}
