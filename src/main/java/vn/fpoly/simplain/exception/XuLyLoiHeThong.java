/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - Bộ xử lý lỗi tập trung @RestControllerAdvice
 */
package vn.fpoly.simplain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.fpoly.simplain.dto.LoiPhanHoi;

import java.util.HashMap;
import java.util.Map;

/**
 * Xử lý ngoại lệ tập trung trả về thông điệp tiếng Việt.
 */
@RestControllerAdvice
public class XuLyLoiHeThong {

    @ExceptionHandler(KhongTimThayException.class)
    public ResponseEntity<LoiPhanHoi> xuLyKhongTimThay(KhongTimThayException ex) {
        LoiPhanHoi loi = new LoiPhanHoi(
                HttpStatus.NOT_FOUND.value(),
                "KHONG_TIM_THAY",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(loi);
    }

    @ExceptionHandler(DuLieuKhongHopLeException.class)
    public ResponseEntity<LoiPhanHoi> xuLyDuLieuKhongHopLe(DuLieuKhongHopLeException ex) {
        LoiPhanHoi loi = new LoiPhanHoi(
                HttpStatus.BAD_REQUEST.value(),
                "DU_LIEU_KHONG_HOP_LE",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loi);
    }

    @ExceptionHandler(ChuaDangNhapException.class)
    public ResponseEntity<LoiPhanHoi> xuLyChuaDangNhap(ChuaDangNhapException ex) {
        LoiPhanHoi loi = new LoiPhanHoi(
                HttpStatus.UNAUTHORIZED.value(),
                "CHUA_DANG_NHAP",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loi);
    }

    @ExceptionHandler(TaiKhoanBiKhoaException.class)
    public ResponseEntity<LoiPhanHoi> xuLyTaiKhoanBiKhoa(TaiKhoanBiKhoaException ex) {
        LoiPhanHoi loi = new LoiPhanHoi(
                HttpStatus.FORBIDDEN.value(),
                "TAI_KHOAN_BI_KHOA",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(loi);
    }

    @ExceptionHandler(XungDotDuLieuException.class)
    public ResponseEntity<LoiPhanHoi> xuLyXungDotDuLieu(XungDotDuLieuException ex) {
        LoiPhanHoi loi = new LoiPhanHoi(
                HttpStatus.CONFLICT.value(),
                "XUNG_DOT_DU_LIEU",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(loi);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<LoiPhanHoi> xuLyLoiValidation(MethodArgumentNotValidException ex) {
        Map<String, String> chiTietLoi = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            chiTietLoi.put(fe.getField(), fe.getDefaultMessage());
        }
        LoiPhanHoi loi = new LoiPhanHoi(
                HttpStatus.BAD_REQUEST.value(),
                "LOI_VALIDATION",
                "Dữ liệu gửi lên không đúng định dạng",
                chiTietLoi
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loi);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<LoiPhanHoi> xuLyLoiChung(Exception ex) {
        LoiPhanHoi loi = new LoiPhanHoi(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "LOI_HE_THONG",
                "Đã xảy ra lỗi hệ thống: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loi);
    }
}
