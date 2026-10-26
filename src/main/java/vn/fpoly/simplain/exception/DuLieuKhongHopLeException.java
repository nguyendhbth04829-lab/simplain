/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - Ngoại lệ dữ liệu không hợp lệ (HTTP 400)
 * Cập nhật xác nhận commit US12: 24/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ngoại lệ khi dữ liệu không hợp lệ theo quy tắc nghiệp vụ (HTTP 400).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuLieuKhongHopLeException extends RuntimeException {
    public DuLieuKhongHopLeException(String thongDiep) {
        super(thongDiep);
    }
}
