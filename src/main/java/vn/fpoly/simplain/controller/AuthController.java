package vn.fpoly.simplain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.fpoly.simplain.dto.ApiResponse;
import vn.fpoly.simplain.dto.AuthResponse;
import vn.fpoly.simplain.dto.CustomerProfileResponse;
import vn.fpoly.simplain.dto.LoginRequest;
import vn.fpoly.simplain.dto.RegisterRequest;
import vn.fpoly.simplain.dto.VerifyTokenRequest;
import vn.fpoly.simplain.dto.VerifyTokenResponse;
import vn.fpoly.simplain.service.AuthService;

/**
 * Controller cung cấp các API RESTful xác thực khách hàng (US01).
 * Hỗ trợ Đăng ký, Đăng nhập, Cấp phát và Kiểm tra tính hợp lệ của JWT Token.
 */
@Tag(name = "Xác thực Khách hàng (US01)", description = "Các API xác thực khách hàng: Đăng ký, Đăng nhập, Cấp phát & Verify JWT")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * API Đăng ký tài khoản khách hàng mới.
     * Kiểm tra hợp lệ dữ liệu, mã hóa mật khẩu bằng BCrypt và trả về JWT Token.
     */
    @Operation(summary = "Đăng ký tài khoản khách hàng mới")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Đăng ký tài khoản khách hàng thành công!", response));
    }

    /**
     * API Đăng nhập khách hàng bằng email hoặc số điện thoại.
     * So khớp mật khẩu mã hóa BCrypt và cấp phát JWT Token mới.
     */
    @Operation(summary = "Đăng nhập tài khoản khách hàng bằng Email/SĐT")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Đăng nhập thành công!", response));
    }

    /**
     * API Xác minh tính hợp lệ của JWT Token (phương thức POST).
     */
    @Operation(summary = "Xác minh (Verify) tính hợp lệ của JWT Token qua Body")
    @PostMapping("/verify-token")
    public ResponseEntity<ApiResponse<VerifyTokenResponse>> verifyToken(@Valid @RequestBody VerifyTokenRequest request) {
        VerifyTokenResponse response = authService.verifyToken(request.getToken());
        return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
    }

    /**
     * API Xác minh tính hợp lệ của JWT Token (phương thức GET qua query param).
     */
    @Operation(summary = "Xác minh (Verify) tính hợp lệ của JWT Token qua Query Param")
    @GetMapping("/verify-token")
    public ResponseEntity<ApiResponse<VerifyTokenResponse>> verifyTokenGet(
            @Parameter(description = "Chuỗi JWT Token cần kiểm tra")
            @RequestParam String token) {
        VerifyTokenResponse response = authService.verifyToken(token);
        return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
    }

    /**
     * API Lấy thông tin hồ sơ của khách hàng hiện tại từ Bearer Token.
     */
    @Operation(summary = "Lấy thông tin tài khoản hiện tại từ Bearer JWT Token")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getCurrentUser(
            @Parameter(description = "Header định danh Bearer <token>")
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        CustomerProfileResponse profile = authService.getCurrentUserProfile(authHeader);
        return ResponseEntity.ok(ApiResponse.ok("Lấy thông tin tài khoản thành công!", profile));
    }
}
