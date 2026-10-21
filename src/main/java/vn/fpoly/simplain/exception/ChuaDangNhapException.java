/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - Ngoại lệ chưa đăng nhập hoặc thiếu định danh (HTTP 401)
 */
package vn.fpoly.simplain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ngoại lệ khi người dùng chưa đăng nhập hoặc thiếu thông tin định danh (HTTP 401).
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class ChuaDangNhapException extends RuntimeException {
    public ChuaDangNhapException(String thongDiep) {
        super(thongDiep);
    }
}
