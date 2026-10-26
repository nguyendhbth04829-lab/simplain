/**
 * Tác giả: Gia Minh
 * Chức năng: US15 - REST API Đánh giá sản phẩm đã mua và thống kê sao
 * Cập nhật xác nhận commit US15: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fpoly.simplain.dto.DanhGiaResponse;
import vn.fpoly.simplain.dto.TaoDanhGiaRequest;
import vn.fpoly.simplain.dto.ThongKeDanhGiaResponse;
import vn.fpoly.simplain.service.DanhGiaSanPhamService;

import java.util.List;

/**
 * REST API phục vụ chức năng Đánh giá sản phẩm (US15).
 *
 * <p>Xem tài liệu và gọi thử trực tiếp tại Swagger UI: /docs -&gt; nhóm "Đánh giá sản phẩm".
 */
@Tag(name = "Đánh giá sản phẩm", description = "API gửi đánh giá 1-5 sao, bình luận và upload ảnh cho sản phẩm đã mua (US15)")
@RestController
@RequestMapping("/api/khach-hang/danh-gia")
public class DanhGiaSanPhamApiController {

    private final DanhGiaSanPhamService danhGiaSanPhamService;

    public DanhGiaSanPhamApiController(DanhGiaSanPhamService danhGiaSanPhamService) {
        this.danhGiaSanPhamService = danhGiaSanPhamService;
    }

    @Operation(summary = "Gửi đánh giá sản phẩm đã mua", description = "Đánh giá 1-5 sao, nhận xét và danh sách ảnh cho sản phẩm từ đơn hàng đã hoàn tất")
    @PostMapping
    public ResponseEntity<DanhGiaResponse> taoDanhGia(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang,
            @Valid @RequestBody TaoDanhGiaRequest yeuCau) {
        DanhGiaResponse phanHoi = danhGiaSanPhamService.taoDanhGia(idKhachHang, yeuCau);
        return ResponseEntity.status(HttpStatus.CREATED).body(phanHoi);
    }

    @Operation(summary = "Lấy danh sách đánh giá theo sản phẩm", description = "Trả về tất cả đánh giá của một sản phẩm hiển thị trên trang chi tiết")
    @GetMapping("/san-pham/{maSanPhamId}")
    public ResponseEntity<List<DanhGiaResponse>> layTheoSanPham(@PathVariable Long maSanPhamId) {
        return ResponseEntity.ok(danhGiaSanPhamService.layDanhSachTheoSanPham(maSanPhamId));
    }

    @Operation(summary = "Thống kê điểm sao của sản phẩm", description = "Trả về điểm trung bình và số lượng đánh giá theo từng mức 1 đến 5 sao")
    @GetMapping("/san-pham/{maSanPhamId}/thong-ke")
    public ResponseEntity<ThongKeDanhGiaResponse> layThongKe(@PathVariable Long maSanPhamId) {
        return ResponseEntity.ok(danhGiaSanPhamService.layThongKeDanhGia(maSanPhamId));
    }

    @Operation(summary = "Lấy danh sách đánh giá của khách hàng", description = "Trả về danh sách tất cả các đánh giá mà khách hàng hiện tại đã gửi")
    @GetMapping("/cua-toi")
    public ResponseEntity<List<DanhGiaResponse>> layDanhSachCuaToi(
            @Parameter(description = "ID khách hàng xác thực", example = "1")
            @RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang) {
        return ResponseEntity.ok(danhGiaSanPhamService.layDanhSachTheoKhachHang(idKhachHang));
    }
}
