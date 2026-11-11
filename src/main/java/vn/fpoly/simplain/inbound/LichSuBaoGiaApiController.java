package vn.fpoly.simplain.inbound;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inbound/lich-su-bao-gia")
public class LichSuBaoGiaApiController {
    private final LichSuBaoGiaService service;

    public LichSuBaoGiaApiController(LichSuBaoGiaService service) {
        this.service = service;
    }

    @GetMapping("/nha-cung-cap/{nhaCungCapId}/san-pham")
    public ResponseEntity<?> laySanPham(@PathVariable Long nhaCungCapId) {
        return ResponseEntity.ok(service.laySanPham(nhaCungCapId));
    }

    @GetMapping("/nha-cung-cap/{nhaCungCapId}/sku-options")
    public ResponseEntity<?> laySkuDeChon(@PathVariable Long nhaCungCapId) {
        return ResponseEntity.ok(service.laySkuDeChon(nhaCungCapId));
    }

    @GetMapping("/nha-cung-cap/{nhaCungCapId}/san-pham/{sanPhamId}/sku")
    public ResponseEntity<?> laySku(@PathVariable Long nhaCungCapId, @PathVariable Long sanPhamId) {
        return ResponseEntity.ok(service.laySku(nhaCungCapId, sanPhamId));
    }

    @GetMapping("/nha-cung-cap/{nhaCungCapId}/sku/{bienTheId}")
    public ResponseEntity<?> layLichSu(@PathVariable Long nhaCungCapId, @PathVariable Long bienTheId) {
        return ResponseEntity.ok(service.layLichSu(nhaCungCapId, bienTheId));
    }

    @PostMapping
    public ResponseEntity<?> ghiNhan(@Valid @RequestBody LichSuBaoGiaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.ghiNhanBaoGia(request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> loiValidate(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
                .findFirst().orElse("Dữ liệu báo giá chưa hợp lệ");
        return loi(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> loiDuLieu(IllegalArgumentException ex) {
        return loi(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> loi(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("trangThai", "DU_LIEU_KHONG_HOP_LE");
        body.put("thongBao", message);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
