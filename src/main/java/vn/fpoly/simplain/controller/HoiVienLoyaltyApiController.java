/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - REST API Quản lý hồ sơ hội viên và ví điểm Coolcash
 */
package vn.fpoly.simplain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fpoly.simplain.dto.BienDongDiemResponse;
import vn.fpoly.simplain.dto.HoSoHoiVienResponse;
import vn.fpoly.simplain.dto.SuDungDiemRequest;
import vn.fpoly.simplain.entity.HangThanhVien;
import vn.fpoly.simplain.service.HoiVienLoyaltyService;

import java.util.List;

/**
 * REST API phục vụ chức năng Khách hàng thân thiết và tích điểm Coolcash (US16).
 *
 * <p>Xem tài liệu và gọi thử trực tiếp tại Swagger UI: /docs -&gt; nhóm "Hội viên & Điểm thưởng Coolcash".
 */
@Tag(name = "Hội viên & Điểm thưởng Coolcash", description = "API quản lý hạng thành viên, ví điểm thưởng Coolcash và tự động tích điểm (US16)")
@RestController
@RequestMapping("/api/khach-hang/hoi-vien")
public class HoiVienLoyaltyApiController {

    private final HoiVienLoyaltyService hoiVienLoyaltyService;

    public HoiVienLoyaltyApiController(HoiVienLoyaltyService hoiVienLoyaltyService) {
        this.hoiVienLoyaltyService = hoiVienLoyaltyService;
    }

    @Operation(summary = "Xem hồ sơ hội viên và quyền lợi", description = "Lấy thông tin hạng thành viên hiện tại, tỷ lệ hoàn tiền, số dư Coolcash và tiến trình thăng hạng")
    @GetMapping
    public ResponseEntity<HoSoHoiVienResponse> layHoSo(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang) {
        return ResponseEntity.ok(hoiVienLoyaltyService.layHoSoHoiVien(idKhachHang));
    }

    @Operation(summary = "Lịch sử biến động điểm thưởng Coolcash", description = "Xem danh sách các lần cộng điểm hoàn tiền từ đơn hàng hoặc trừ điểm khi thanh toán")
    @GetMapping("/lich-su-diem")
    public ResponseEntity<List<BienDongDiemResponse>> layLichSuDiem(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang) {
        return ResponseEntity.ok(hoiVienLoyaltyService.layLichSuBienDongDiem(idKhachHang));
    }

    @Operation(summary = "Sử dụng điểm thưởng Coolcash thanh toán", description = "Khách hàng đổi điểm Coolcash để trừ trực tiếp vào số tiền cần thanh toán của đơn hàng")
    @PostMapping("/su-dung-diem")
    public ResponseEntity<BienDongDiemResponse> suDungDiem(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @Valid @RequestBody SuDungDiemRequest yeuCau) {
        return ResponseEntity.ok(hoiVienLoyaltyService.suDungDiemCoolcash(idKhachHang, yeuCau));
    }

    @Operation(summary = "Tự động tích điểm khi đơn hàng hoàn thành", description = "Kích hoạt sự kiện tích điểm theo tỷ lệ % hạng thành viên và tự động thăng hạng")
    @PostMapping("/tich-diem-don-hang/{maDonHangId}")
    public ResponseEntity<BienDongDiemResponse> tichDiemDonHang(@PathVariable Long maDonHangId) {
        BienDongDiemResponse phanHoi = hoiVienLoyaltyService.tichDiemDonHangHoanThanh(maDonHangId);
        return ResponseEntity.ok(phanHoi);
    }

    @Operation(summary = "Danh sách các bậc hạng thành viên", description = "Lấy bảng cấu hình quyền lợi hoàn tiền và mức chi tiêu của tất cả các hạng")
    @GetMapping("/hang-thanh-vien")
    public ResponseEntity<List<HangThanhVien>> layDanhSachHang() {
        return ResponseEntity.ok(hoiVienLoyaltyService.layDanhSachHangThanhVien());
    }
}
