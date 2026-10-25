/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - REST API Quản lý danh sách sản phẩm yêu thích (Wishlist) của khách hàng
 */
package vn.fpoly.simplain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fpoly.simplain.dto.SanPhamYeuThichResponse;
import vn.fpoly.simplain.dto.ThemYeuThichRequest;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.dto.TrangThaiYeuThichResponse;
import vn.fpoly.simplain.service.SanPhamYeuThichService;

import java.util.List;

/**
 * REST API phục vụ chức năng Danh sách sản phẩm yêu thích (US14).
 *
 * <p>Xem tài liệu và gọi thử trực tiếp tại Swagger UI: /docs -&gt; nhóm "Sản phẩm yêu thích".
 */
@Tag(name = "Sản phẩm yêu thích", description = "API quản lý danh sách sản phẩm yêu thích (Wishlist) của khách hàng (US14)")
@RestController
@RequestMapping("/api/khach-hang/yeu-thich")
public class SanPhamYeuThichApiController {

    private final SanPhamYeuThichService sanPhamYeuThichService;

    public SanPhamYeuThichApiController(SanPhamYeuThichService sanPhamYeuThichService) {
        this.sanPhamYeuThichService = sanPhamYeuThichService;
    }

    @Operation(summary = "Lấy danh sách sản phẩm yêu thích", description = "Trả về danh sách tất cả sản phẩm mà khách hàng hiện tại đã thả tim / lưu vào yêu thích")
    @GetMapping
    public ResponseEntity<List<SanPhamYeuThichResponse>> layDanhSach(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang) {
        return ResponseEntity.ok(sanPhamYeuThichService.layDanhSachYeuThich(idKhachHang));
    }

    @Operation(summary = "Thêm sản phẩm vào yêu thích (qua URL)", description = "Thêm một sản phẩm cụ thể theo ID vào danh sách yêu thích")
    @PostMapping("/{maSanPhamId}")
    public ResponseEntity<SanPhamYeuThichResponse> themVaoYeuThich(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @PathVariable Long maSanPhamId) {
        SanPhamYeuThichResponse phanHoi = sanPhamYeuThichService.themVaoYeuThich(idKhachHang, maSanPhamId);
        return ResponseEntity.status(HttpStatus.CREATED).body(phanHoi);
    }

    @Operation(summary = "Thêm sản phẩm vào yêu thích (qua Body JSON)", description = "Thêm sản phẩm vào danh sách yêu thích gửi kèm body request")
    @PostMapping
    public ResponseEntity<SanPhamYeuThichResponse> themVaoYeuThichBody(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @Valid @RequestBody ThemYeuThichRequest yeuCau) {
        SanPhamYeuThichResponse phanHoi = sanPhamYeuThichService.themVaoYeuThich(idKhachHang, yeuCau.maSanPhamId());
        return ResponseEntity.status(HttpStatus.CREATED).body(phanHoi);
    }

    @Operation(summary = "Xóa sản phẩm khỏi yêu thích", description = "Bỏ thích và xóa sản phẩm khỏi danh sách yêu thích của khách hàng")
    @DeleteMapping("/{maSanPhamId}")
    public ResponseEntity<ThongBaoResponse> xoaKhoiYeuThich(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @PathVariable Long maSanPhamId) {
        return ResponseEntity.ok(sanPhamYeuThichService.xoaKhoiYeuThich(idKhachHang, maSanPhamId));
    }

    @Operation(summary = "Bật / Tắt trạng thái yêu thích (Toggle tim)", description = "Nếu sản phẩm chưa thích thì thêm vào, nếu đã thích thì xóa đi")
    @PostMapping("/toggle/{maSanPhamId}")
    public ResponseEntity<TrangThaiYeuThichResponse> toggleYeuThich(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @PathVariable Long maSanPhamId) {
        return ResponseEntity.ok(sanPhamYeuThichService.toggleYeuThich(idKhachHang, maSanPhamId));
    }

    @Operation(summary = "Kiểm tra sản phẩm đã được yêu thích chưa", description = "Kiểm tra trạng thái tim của sản phẩm để hiển thị trên giao diện")
    @GetMapping("/kiem-tra/{maSanPhamId}")
    public ResponseEntity<TrangThaiYeuThichResponse> kiemTraDaYeuThich(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @PathVariable Long maSanPhamId) {
        boolean daThich = sanPhamYeuThichService.kiemTraDaYeuThich(idKhachHang, maSanPhamId);
        String thongDiep = daThich ? "Sản phẩm đã có trong danh sách yêu thích" : "Sản phẩm chưa có trong danh sách yêu thích";
        return ResponseEntity.ok(new TrangThaiYeuThichResponse(maSanPhamId, daThich, thongDiep));
    }

    @Operation(summary = "Đếm số lượng sản phẩm yêu thích", description = "Trả về tổng số sản phẩm yêu thích của khách hàng")
    @GetMapping("/dem")
    public ResponseEntity<Long> demSoLuongYeuThich(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang) {
        return ResponseEntity.ok(sanPhamYeuThichService.demSoLuongYeuThich(idKhachHang));
    }
}
