package vn.fpoly.simplain.inbound;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inbound/nha-cung-cap/{nhaCungCapId}/sku")
public class NhaCungCapSkuApiController {
    private final NhaCungCapSkuService service;

    public NhaCungCapSkuApiController(NhaCungCapSkuService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<String>> laySku(@PathVariable Long nhaCungCapId) {
        return ResponseEntity.ok(service.layMaSku(nhaCungCapId));
    }

    @GetMapping("/options")
    public ResponseEntity<List<NhaCungCapSkuOption>> laySkuDuocPhanCong(@PathVariable Long nhaCungCapId) {
        return ResponseEntity.ok(service.layDanhSachSkuDuocPhanCong(nhaCungCapId));
    }

    @PutMapping
    public ResponseEntity<List<String>> capNhatSku(@PathVariable Long nhaCungCapId,
                                                    @Valid @RequestBody NhaCungCapSkuRequest request) {
        return ResponseEntity.ok(service.capNhatLienKet(nhaCungCapId, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> loiValidate(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
                .findFirst().orElse("Danh sách SKU chưa hợp lệ");
        return loi(HttpStatus.BAD_REQUEST, "DU_LIEU_KHONG_HOP_LE", message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> loiDuLieu(IllegalArgumentException ex) {
        return loi(HttpStatus.BAD_REQUEST, "DU_LIEU_KHONG_HOP_LE", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> loi(HttpStatus status, String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("trangThai", code);
        body.put("thongBao", message);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
