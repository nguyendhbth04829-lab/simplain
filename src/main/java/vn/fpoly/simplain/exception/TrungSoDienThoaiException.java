package vn.fpoly.simplain.exception;

import lombok.Getter;
import vn.fpoly.simplain.entity.KhachHang;

/**
 * Ngoại lệ ném ra khi số điện thoại khách hàng đã tồn tại trong hệ thống.
 * Chứa thông tin khách hàng sở hữu số điện thoại để hỗ trợ hiển thị và thao tác nhanh.
 */
@Getter
public class TrungSoDienThoaiException extends RuntimeException {

    private final String soDienThoai;
    private final Long khachHangId;
    private final String maKhachHang;
    private final String hoTen;

    public TrungSoDienThoaiException(String soDienThoai, KhachHang existingKh) {
        super(String.format("Số điện thoại %s đã tồn tại trong hệ thống (thuộc về khách hàng '%s'%s). Vui lòng kiểm tra lại hoặc tìm kiếm trực tiếp bằng số điện thoại này!",
                soDienThoai,
                existingKh != null ? existingKh.getHoTen() : "",
                (existingKh != null && existingKh.getMaKhachHang() != null && !existingKh.getMaKhachHang().isBlank()) ? " - Mã: " + existingKh.getMaKhachHang() : ""));
        this.soDienThoai = soDienThoai;
        if (existingKh != null) {
            this.khachHangId = existingKh.getId();
            this.maKhachHang = existingKh.getMaKhachHang();
            this.hoTen = existingKh.getHoTen();
        } else {
            this.khachHangId = null;
            this.maKhachHang = null;
            this.hoTen = null;
        }
    }

    public TrungSoDienThoaiException(String message) {
        super(message);
        this.soDienThoai = null;
        this.khachHangId = null;
        this.maKhachHang = null;
        this.hoTen = null;
    }
}
