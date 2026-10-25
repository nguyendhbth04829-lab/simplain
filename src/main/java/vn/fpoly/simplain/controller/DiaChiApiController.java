/**
 * Tác giả: Gia Minh
 * Chức năng: US13 - REST API Quản lý sổ địa chỉ giao hàng và danh mục hành chính Tỉnh / Huyện / Xã
 * Cập nhật xác nhận commit US13: 25/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.fpoly.simplain.dto.CapNhatDiaChiRequest;
import vn.fpoly.simplain.dto.DiaChiResponse;
import vn.fpoly.simplain.dto.ThemDiaChiRequest;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.service.DanhMucHanhChinhService;
import vn.fpoly.simplain.service.DiaChiService;

import java.util.List;

/**
 * REST API phục vụ chức năng quản lý Sổ địa chỉ (US13).
 *
 * <p>Xem tài liệu và gọi thử trực tiếp tại Swagger UI: /docs -&gt; nhóm "Sổ địa chỉ khách hàng".
 */
@Tag(name = "Sổ địa chỉ khách hàng", description = "API quản lý sổ địa chỉ giao hàng và danh mục hành chính (US13)")
@RestController
@RequestMapping("/api/khach-hang/dia-chi")
public class DiaChiApiController {

    private final DiaChiService diaChiService;
    private final DanhMucHanhChinhService danhMucHanhChinhService;

    public DiaChiApiController(DiaChiService diaChiService, DanhMucHanhChinhService danhMucHanhChinhService) {
        this.diaChiService = diaChiService;
        this.danhMucHanhChinhService = danhMucHanhChinhService;
    }

    @Operation(summary = "Lấy danh sách địa chỉ của khách hàng", description = "Trả về danh sách tất cả địa chỉ nhận hàng của khách hàng hiện tại")
    @GetMapping
    public ResponseEntity<List<DiaChiResponse>> layDanhSach(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang) {
        return ResponseEntity.ok(diaChiService.layDanhSachDiaChi(idKhachHang));
    }

    @Operation(summary = "Xem chi tiết một địa chỉ", description = "Lấy thông tin chi tiết địa chỉ theo ID (chỉ xem được địa chỉ của chính mình)")
    @GetMapping("/{id}")
    public ResponseEntity<DiaChiResponse> layChiTiet(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @PathVariable Long id) {
        return ResponseEntity.ok(diaChiService.layChiTietDiaChi(idKhachHang, id));
    }

    @Operation(summary = "Thêm mới địa chỉ nhận hàng", description = "Tạo địa chỉ giao hàng mới, tự động đặt làm mặc định nếu là địa chỉ đầu tiên")
    @PostMapping
    public ResponseEntity<DiaChiResponse> themMoi(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @Valid @RequestBody ThemDiaChiRequest yeuCau) {
        DiaChiResponse phanHoi = diaChiService.themDiaChi(idKhachHang, yeuCau);
        return ResponseEntity.status(HttpStatus.CREATED).body(phanHoi);
    }

    @Operation(summary = "Cập nhật địa chỉ nhận hàng", description = "Chỉnh sửa thông tin địa chỉ giao hàng của khách hàng")
    @PutMapping("/{id}")
    public ResponseEntity<DiaChiResponse> capNhat(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @PathVariable Long id,
            @Valid @RequestBody CapNhatDiaChiRequest yeuCau) {
        return ResponseEntity.ok(diaChiService.capNhatDiaChi(idKhachHang, id, yeuCau));
    }

    @Operation(summary = "Xóa địa chỉ nhận hàng", description = "Xóa địa chỉ; nếu xóa địa chỉ mặc định, tự động kế thừa địa chỉ còn lại")
    @DeleteMapping("/{id}")
    public ResponseEntity<ThongBaoResponse> xoa(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @PathVariable Long id) {
        return ResponseEntity.ok(diaChiService.xoaDiaChi(idKhachHang, id));
    }

    @Operation(summary = "Đặt làm địa chỉ mặc định", description = "Gán địa chỉ chỉ định làm mặc định và tự động hủy cờ các địa chỉ khác")
    @PatchMapping("/{id}/mac-dinh")
    public ResponseEntity<DiaChiResponse> datLamMacDinh(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @PathVariable Long id) {
        return ResponseEntity.ok(diaChiService.datLamMacDinh(idKhachHang, id));
    }

    @Operation(summary = "Danh sách Tỉnh / Thành phố", description = "Lấy danh mục 63 Tỉnh / Thành phố Việt Nam phục vụ dropdown cấp 1")
    @GetMapping("/danh-muc/tinh-thanh")
    public ResponseEntity<List<String>> layDanhSachTinhThanh() {
        return ResponseEntity.ok(danhMucHanhChinhService.layDanhSachTinhThanh());
    }

    @Operation(summary = "Danh sách Quận / Huyện theo Tỉnh", description = "Lấy danh mục Quận / Huyện thuộc Tỉnh phục vụ dropdown cấp 2")
    @GetMapping("/danh-muc/quan-huyen")
    public ResponseEntity<List<String>> layDanhSachQuanHuyen(@RequestParam String tinhThanh) {
        return ResponseEntity.ok(danhMucHanhChinhService.layDanhSachQuanHuyen(tinhThanh));
    }

    @Operation(summary = "Danh sách Phường / Xã theo Quận / Huyện", description = "Lấy danh mục Phường / Xã thuộc Quận Huyện phục vụ dropdown cấp 3")
    @GetMapping("/danh-muc/phuong-xa")
    public ResponseEntity<List<String>> layDanhSachPhuongXa(
            @RequestParam String tinhThanh,
            @RequestParam String quanHuyen) {
        return ResponseEntity.ok(danhMucHanhChinhService.layDanhSachPhuongXa(tinhThanh, quanHuyen));
    }
}
