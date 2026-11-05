package vn.fpoly.simplain.inbound;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Nhập kho & Nhà cung cấp", description = "API quản lý Nhà cung cấp và nhập hàng vào kho")
@RestController
@RequestMapping("/api/inbound")
public class InboundApiController {

    private final InboundService inboundService;
    private final PhieuNhapKhoRepository phieuNhapKhoRepository;

    @Autowired
    public InboundApiController(InboundService inboundService,
                                PhieuNhapKhoRepository phieuNhapKhoRepository) {
        this.inboundService = inboundService;
        this.phieuNhapKhoRepository = phieuNhapKhoRepository;
    }

    @Operation(summary = "Lấy danh sách tất cả Nhà cung cấp")
    @GetMapping("/nha-cung-cap")
    public ResponseEntity<List<NhaCungCap>> layDanhSachNhaCungCap() {
        return ResponseEntity.ok(inboundService.layTatCaNhaCungCap());
    }

    @Operation(summary = "Tạo mới một Nhà cung cấp")
    @PostMapping("/nha-cung-cap")
    public ResponseEntity<NhaCungCap> taoNhaCungCap(@Valid @RequestBody NhaCungCapRequest request) {
        NhaCungCap ncc = inboundService.taoNhaCungCap(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ncc);
    }

    @Operation(summary = "Cập nhật thông tin Nhà cung cấp")
    @PutMapping("/nha-cung-cap/{id}")
    public ResponseEntity<NhaCungCap> capNhatNhaCungCap(@PathVariable Long id, @RequestBody NhaCungCapRequest request) {
        return ResponseEntity.ok(inboundService.capNhatNhaCungCap(id, request));
    }

    @Operation(summary = "Xóa/Ngưng hoạt động Nhà cung cấp")
    @DeleteMapping("/nha-cung-cap/{id}")
    public ResponseEntity<Void> xoaNhaCungCap(@PathVariable Long id) {
        inboundService.xoaNhaCungCap(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Chuyển đổi trạng thái Hoạt động / Tạm ngưng Nhà cung cấp")
    @PutMapping("/nha-cung-cap/{id}/trang-thai")
    public ResponseEntity<NhaCungCap> toggleTrangThaiNcc(@PathVariable Long id) {
        return ResponseEntity.ok(inboundService.toggleTrangThaiNcc(id));
    }

    @Operation(summary = "Thực hiện nhập kho hàng hóa từ Nhà cung cấp")
    @PostMapping("/nhap-kho")
    public ResponseEntity<NhapKhoResponse> nhapKho(@Valid @RequestBody NhapKhoRequest request) {
        NhapKhoResponse response = inboundService.thucHienNhapKho(request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Lấy chi tiết một Phiếu nhập kho")
    @GetMapping("/phieu-nhap-kho/{id}")
    public ResponseEntity<Map<String, Object>> layChiTietPhieuNhapKho(@PathVariable Long id) {
        PhieuNhapKho p = phieuNhapKhoRepository.findWithDetailsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu nhập kho có ID: " + id));

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id", p.getId());
        res.put("maPhieu", p.getMaPhieu());
        res.put("nhaCungCap", p.getNhaCungCap() != null ? p.getNhaCungCap().getTenNcc() : "N/A");
        res.put("maNcc", p.getNhaCungCap() != null ? p.getNhaCungCap().getMaNcc() : "");
        res.put("ngayNhap", p.getNgayNhap() != null ? p.getNgayNhap().toString() : "");
        res.put("tongTien", p.getTongTien());
        res.put("trangThai", p.getTrangThai() != null ? p.getTrangThai().getNhan() : "");
        res.put("ghiChu", p.getGhiChu() != null ? p.getGhiChu() : "");

        java.util.List<Map<String, Object>> chiTiets = new java.util.ArrayList<>();
        if (p.getChiTietList() != null) {
            for (ChiTietPhieuNhapKho ct : p.getChiTietList()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("maSku", ct.getBienThe() != null ? ct.getBienThe().getMaSku() : "N/A");
                item.put("soLuongNhap", ct.getSoLuongNhap());
                item.put("donGiaNhap", ct.getDonGiaNhap());
                item.put("thanhTien", ct.getThanhTien());
                chiTiets.add(item);
            }
        }
        res.put("chiTietList", chiTiets);

        return ResponseEntity.ok(res);
    }

    /**
     * Chuẩn hóa lỗi nghiệp vụ thành một hợp đồng JSON thống nhất cho mọi máy khách.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> xuLyLoiXacThuc(MethodArgumentNotValidException ex) {
        String thongBao = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Dữ liệu nhập kho chưa hợp lệ");
        return ResponseEntity.badRequest().body(taoPhanHoiLoi("DU_LIEU_KHONG_HOP_LE", thongBao));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> xuLyLoiDuLieu(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(taoPhanHoiLoi("DU_LIEU_KHONG_HOP_LE", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> xuLyLoiTrangThai(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(taoPhanHoiLoi("XUNG_DOT_TRANG_THAI", ex.getMessage()));
    }

    private Map<String, Object> taoPhanHoiLoi(String maLoi, String thongBao) {
        Map<String, Object> phanHoi = new LinkedHashMap<>();
        phanHoi.put("trangThai", maLoi);
        phanHoi.put("thongBao", thongBao);
        // Giữ khóa message để tương thích với các đoạn JavaScript hiện có.
        phanHoi.put("message", thongBao);
        return phanHoi;
    }
}
