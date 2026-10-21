package vn.fpoly.simplain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ngoại lệ khi tài khoản đã bị khóa không cho phép thao tác (HTTP 403).
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class TaiKhoanBiKhoaException extends RuntimeException {
    public TaiKhoanBiKhoaException(String thongDiep) {
        super(thongDiep);
    }
}
