/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - REST API xem hồ sơ, cập nhật thông tin cá nhân và đổi mật khẩu
 * Cập nhật xác nhận commit US12: 24/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.fpoly.simplain.dto.CapNhatHoSoRequest;
import vn.fpoly.simplain.dto.DoiMatKhauRequest;
import vn.fpoly.simplain.dto.HoSoKhachHangResponse;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.service.KhachHangService;

/**
 * Controller cung cấp REST API cho chức năng Hồ sơ khách hàng và Đổi mật khẩu (US12).
 *
 * <p>Xem và gọi thử trực tiếp tại Swagger UI: /docs -&gt; nhóm "Hồ sơ khách hàng".
 */
@Tag(name = "Hồ sơ khách hàng", description = "API quản lý thông tin cá nhân và đổi mật khẩu tài khoản khách hàng (US12)")
@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangApiController {

    private final KhachHangService khachHangService;

    public KhachHangApiController(KhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    /**
     * Xem thông tin hồ sơ cá nhân của khách hàng.
     *
     * @param idKhachHang ID khách hàng truyền qua header X-Customer-Id
     * @return hồ sơ chi tiết khách hàng
     */
    @Operation(summary = "Xem thông tin hồ sơ cá nhân", description = "Lấy thông tin tài khoản của khách hàng đang đăng nhập")
    @GetMapping("/ho-so")
    public ResponseEntity<HoSoKhachHangResponse> layHoSo(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang) {
        HoSoKhachHangResponse phanHoi = khachHangService.layHoSo(idKhachHang);
        return ResponseEntity.ok(phanHoi);
    }

    /**
     * Cập nhật thông tin cá nhân khách hàng (Họ tên, SĐT, Email, Giới tính, Ngày sinh, Ảnh).
     *
     * @param idKhachHang ID khách hàng truyền qua header X-Customer-Id
     * @param yeuCau      thông tin cập nhật
     * @return hồ sơ khách hàng sau khi cập nhật
     */
    @Operation(summary = "Cập nhật thông tin cá nhân", description = "Cập nhật họ tên, số điện thoại, email, giới tính, ngày sinh và ảnh đại diện")
    @PutMapping("/cap-nhat")
    public ResponseEntity<HoSoKhachHangResponse> capNhatHoSo(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @Valid @RequestBody CapNhatHoSoRequest yeuCau) {
        HoSoKhachHangResponse phanHoi = khachHangService.capNhatHoSo(idKhachHang, yeuCau);
        return ResponseEntity.ok(phanHoi);
    }

    /**
     * Đổi mật khẩu tài khoản khách hàng.
     *
     * @param idKhachHang ID khách hàng truyền qua header X-Customer-Id
     * @param yeuCau      thông tin mật khẩu cũ và mới
     * @return thông báo đổi mật khẩu thành công
     */
    @Operation(summary = "Đổi mật khẩu tài khoản", description = "Kiểm tra mật khẩu cũ và cập nhật mật khẩu mới đã băm")
    @PutMapping("/doi-mat-khau")
    public ResponseEntity<ThongBaoResponse> doiMatKhau(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @Valid @RequestBody DoiMatKhauRequest yeuCau) {
        ThongBaoResponse phanHoi = khachHangService.doiMatKhau(idKhachHang, yeuCau);
        return ResponseEntity.ok(phanHoi);
    }
}
