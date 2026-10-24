package vn.fpoly.simplain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import vn.fpoly.simplain.dto.ApiResponse;
import vn.fpoly.simplain.dto.StaffAuthResponse;
import vn.fpoly.simplain.dto.StaffLoginRequest;
import vn.fpoly.simplain.dto.StaffProfileResponse;
import vn.fpoly.simplain.service.StaffAuthService;

/**
 * Controller cung cấp REST API cho module Xác thực Nhân viên CMS Quản trị (US02).
 * Hỗ trợ kiểm tra phân quyền (Role-based) và chặn truy cập nếu tài khoản nhân viên bị khóa.
 */
@Tag(name = "Xác thực Quản trị CMS (US02)", description = "Các API phục vụ đăng nhập, kiểm tra phân quyền Role-based và quản lý phiên làm việc nhân viên CMS")
@RestController
@RequestMapping("/api/admin/auth")
public class StaffAuthController {

    private final StaffAuthService staffAuthService;

    public StaffAuthController(StaffAuthService staffAuthService) {
        this.staffAuthService = staffAuthService;
    }

    /**
     * API Đăng nhập vào hệ thống CMS quản trị cho nhân viên.
     *
     * @param request Thông tin đăng nhập
     * @return Token JWT kèm vai trò nếu đăng nhập thành công
     */
    @Operation(
            summary = "Đăng nhập hệ thống CMS quản trị (US02)",
            description = "Xác thực nhân viên bằng tên đăng nhập/email/sđt và mật khẩu. " +
                    "Kiểm tra phân quyền vai trò (Role-based: ADMIN, CSKH, BAN_HANG) và từ chối đăng nhập nếu tài khoản bị khóa (trang_thai = 0)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đăng nhập CMS thành công, cấp JWT Token kèm thông tin vai trò"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu nhập vào không hợp lệ hoặc thiếu thông tin"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Sai tên đăng nhập hoặc mật khẩu"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Tài khoản nhân viên bị khóa (trang_thai=0) hoặc vai trò không có quyền truy cập CMS")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<StaffAuthResponse>> login(@Valid @RequestBody StaffLoginRequest request) {
        StaffAuthResponse authResponse = staffAuthService.login(request);
        return ResponseEntity.ok(ApiResponse.<StaffAuthResponse>builder()
                .success(true)
                .message("Đăng nhập CMS thành công. Chào mừng " + authResponse.getHoTen() + " (" + authResponse.getTenVaiTro() + ")")
                .data(authResponse)
                .build());
    }

    /**
     * API Lấy thông tin hồ sơ và quyền hạn của nhân viên đang đăng nhập.
     *
     * @param authHeader Header chứa Bearer Token
     * @return Thông tin hồ sơ nhân viên
     */
    @Operation(
            summary = "Lấy thông tin nhân viên hiện tại từ JWT Token",
            description = "Trích xuất thông tin nhân viên và vai trò phân quyền từ chuỗi Bearer JWT Token"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy hồ sơ nhân viên thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token không hợp lệ hoặc đã hết hạn"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền truy cập hoặc tài khoản bị khóa")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StaffProfileResponse>> getCurrentStaff(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng cung cấp JWT Token hợp lệ trong Header Authorization (dạng 'Bearer <token>').");
        }
        StaffProfileResponse profile = staffAuthService.getStaffProfileFromToken(authHeader);
        return ResponseEntity.ok(ApiResponse.<StaffProfileResponse>builder()
                .success(true)
                .message("Lấy thông tin nhân viên thành công")
                .data(profile)
                .build());
    }
}
