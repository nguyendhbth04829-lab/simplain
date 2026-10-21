/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - Ngoại lệ không tìm thấy tài nguyên (HTTP 404)
 */
package vn.fpoly.simplain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ngoại lệ khi không tìm thấy dữ liệu yêu cầu (HTTP 404).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class KhongTimThayException extends RuntimeException {
    public KhongTimThayException(String thongDiep) {
        super(thongDiep);
    }
}
