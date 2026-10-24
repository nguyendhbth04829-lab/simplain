package vn.fpoly.simplain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import vn.fpoly.simplain.dto.ApiResponse;
import vn.fpoly.simplain.dto.DashboardOverviewResponse;
import vn.fpoly.simplain.service.DashboardService;
import vn.fpoly.simplain.service.StaffAuthService;

/**
 * Controller cung cấp REST API thống kê Bảng điều khiển Dashboard tổng quan cho Admin (US05).
 * CHỈ CÓ ADMIN MỚI ĐƯỢC PHÉP TRUY CẬP VÀ XEM DASHBOARD TỔNG QUAN.
 */
@Tag(name = "Dashboard Quản Trị CMS (US05)", description = "Các API thống kê số lượng người dùng, đơn hàng hôm nay, doanh thu trong ngày và biểu đồ tổng quan - Chỉ dành cho Quản trị viên (ADMIN)")
@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final StaffAuthService staffAuthService;

    public DashboardController(DashboardService dashboardService, StaffAuthService staffAuthService) {
        this.dashboardService = dashboardService;
        this.staffAuthService = staffAuthService;
    }

    /**
     * API Lấy toàn bộ số liệu thống kê tổng quan Dashboard và biểu đồ 7 ngày gần nhất (US05).
     * YÊU CẦU PHÂN QUYỀN: Chỉ có Quản trị viên (ADMIN) mới có quyền truy cập.
     * Xác thực thông qua Header Authorization (Bearer Token) hoặc Cookie 'simplain_cms_token'.
     *
     * @param authHeader  Chuỗi JWT Token trong Header Authorization (dạng 'Bearer <token>')
     * @param cookieToken Chuỗi JWT Token trong Cookie trình duyệt CMS
     * @return DTO DashboardOverviewResponse
     */
    @Operation(
            summary = "Lấy dữ liệu thống kê Dashboard tổng quan (Chỉ dành cho Admin - US05)",
            description = "Trả về tổng số người dùng (Khách hàng + Nhân viên), đơn hàng hôm nay, doanh thu trong ngày và chuỗi số liệu biểu đồ 7 ngày phong cách Coolmate. YÊU CẦU QUYỀN QUẢN TRỊ VIÊN (ADMIN)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy dữ liệu Dashboard thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực hoặc JWT Token không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Từ chối truy cập: Chỉ có tài khoản Quản trị viên (ADMIN) mới có quyền xem Dashboard")
    })
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> getDashboardOverview(
            @Parameter(description = "Header Authorization chứa Bearer Token của Admin", example = "Bearer eyJhbGciOi...")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "Cookie xác thực CMS quản trị của Admin")
            @CookieValue(value = CmsViewController.CMS_COOKIE_NAME, required = false) String cookieToken) {

        String token = (authHeader != null && !authHeader.isBlank()) ? authHeader : cookieToken;
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Vui lòng cung cấp JWT Token trong Header Authorization (dạng 'Bearer <token>') hoặc Cookie CMS để truy cập Dashboard.");
        }

        // Kiểm tra phân quyền nghiêm ngặt: Chỉ có ADMIN mới có quyền truy cập
        staffAuthService.validateAdminAccess(token);

        DashboardOverviewResponse overview = dashboardService.getDashboardOverview();
        return ResponseEntity.ok(ApiResponse.<DashboardOverviewResponse>builder()
                .success(true)
                .message("Lấy số liệu Dashboard tổng quan thành công (Quản trị viên)")
                .data(overview)
                .build());
    }
}
