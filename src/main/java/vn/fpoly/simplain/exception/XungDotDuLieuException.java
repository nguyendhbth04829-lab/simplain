/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - Ngoại lệ xung đột dữ liệu trùng lặp (HTTP 409)
 */
package vn.fpoly.simplain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ngoại lệ khi dữ liệu bị xung đột hoặc trùng lặp (HTTP 409).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class XungDotDuLieuException extends RuntimeException {
    public XungDotDuLieuException(String thongDiep) {
        super(thongDiep);
    }
}
