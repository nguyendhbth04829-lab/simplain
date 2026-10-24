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
import vn.fpoly.simplain.dto.LoginRequest;
import vn.fpoly.simplain.dto.RegisterRequest;
import vn.fpoly.simplain.dto.VerifyTokenRequest;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.service.AuthService;
import vn.fpoly.simplain.service.AuthServiceTest.InMemoryKhachHangRepository;
import vn.fpoly.simplain.service.JwtService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Kiểm thử MockMvc độc lập cho AuthController US01.
 */
@DisplayName("Kiểm thử MockMvc API Xác thực Khách hàng US01 (AuthController)")
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private TestRepo repository;
    private AuthService authService;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        repository = new TestRepo();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-for-simplain-auth-service-256-bits-super-secure!");
        jwtProperties.setExpirationMs(3600000L);
        jwtService = new JwtService(jwtProperties);
        authService = new AuthService(repository, encoder, jwtService);

        AuthController controller = new AuthController(authService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("API POST /api/auth/register - Đăng ký thành công trả về 201 + token")
    void registerApiSuccess() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .hoTen("Nguyễn Văn Duy")
                .email("duy.api@coolmate.me")
                .soDienThoai("0987654321")
                .matKhau("MatKhau@123")
                .xacNhanMatKhau("MatKhau@123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.email").value("duy.api@coolmate.me"));
    }

    @Test
    @DisplayName("API POST /api/auth/register - Thất bại khi email sai định dạng (HTTP 400)")
    void registerApiInvalidEmail() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .hoTen("Nguyễn Văn Duy")
                .email("email-khong-hop-le")
                .soDienThoai("0987654321")
                .matKhau("MatKhau@123")
                .xacNhanMatKhau("MatKhau@123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("API POST /api/auth/register - Thất bại khi Gmail không đủ .com (HTTP 400)")
    void registerApiGmailNotCom() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .hoTen("Nguyễn Văn Duy")
                .email("12345@gmail.co")
                .soDienThoai("0987654321")
                .matKhau("MatKhau123")
                .xacNhanMatKhau("MatKhau123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("đầy đủ là @gmail.com")));
    }

    @Test
    @DisplayName("API POST /api/auth/login - Đăng nhập thành công trả về 200 + token")
    void loginApiSuccess() throws Exception {
        // Đăng ký
        RegisterRequest reg = RegisterRequest.builder()
                .hoTen("Trần Minh Quân")
                .email("quan.api@coolmate.me")
                .soDienThoai("0912345678")
                .matKhau("QuanPass@2026")
                .xacNhanMatKhau("QuanPass@2026")
                .build();
        authService.register(reg);

        LoginRequest login = LoginRequest.builder()
                .identifier("quan.api@coolmate.me")
                .matKhau("QuanPass@2026")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token", notNullValue()));
    }

    @Test
    @DisplayName("API POST /api/auth/login - Đăng nhập sai mật khẩu trả về 401 Unauthorized")
    void loginApiWrongPassword() throws Exception {
        RegisterRequest reg = RegisterRequest.builder()
                .hoTen("Lê Hoàng Nam")
                .email("nam.api@coolmate.me")
                .soDienThoai("0933334444")
                .matKhau("NamPass@123")
                .xacNhanMatKhau("NamPass@123")
                .build();
        authService.register(reg);

        LoginRequest login = LoginRequest.builder()
                .identifier("nam.api@coolmate.me")
                .matKhau("SaiMatKhau@999")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("API POST /api/auth/verify-token - Xác minh token hợp lệ")
    void verifyTokenApi() throws Exception {
        RegisterRequest reg = RegisterRequest.builder()
                .hoTen("Phạm Văn Khôi")
                .email("khoi.api@coolmate.me")
                .soDienThoai("0977889900")
                .matKhau("KhoiPass@123")
                .xacNhanMatKhau("KhoiPass@123")
                .build();
        var authRes = authService.register(reg);

        VerifyTokenRequest verifyReq = new VerifyTokenRequest(authRes.getToken());

        mockMvc.perform(post("/api/auth/verify-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.email").value("khoi.api@coolmate.me"));
    }

    @Test
    @DisplayName("API GET /api/auth/me - Lấy thông tin user hiện tại qua Bearer token")
    void getCurrentUserApi() throws Exception {
        RegisterRequest reg = RegisterRequest.builder()
                .hoTen("Đỗ Tuấn Kiệt")
                .email("kiet.api@coolmate.me")
                .soDienThoai("0966554433")
                .matKhau("KietPass@123")
                .xacNhanMatKhau("KietPass@123")
                .build();
        var authRes = authService.register(reg);

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + authRes.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hoTen").value("Đỗ Tuấn Kiệt"))
                .andExpect(jsonPath("$.data.email").value("kiet.api@coolmate.me"));
    }

    static class TestRepo extends InMemoryKhachHangRepository {}
}
