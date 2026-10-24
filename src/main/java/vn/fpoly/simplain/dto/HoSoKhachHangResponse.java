/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - DTO phản hồi thông tin chi tiết hồ sơ khách hàng
 * Cập nhật xác nhận commit US12: 24/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import vn.fpoly.simplain.entity.KhachHang;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Thông tin hồ sơ khách hàng trả về cho người dùng.
 */
@Schema(description = "Hồ sơ chi tiết của khách hàng")
public record HoSoKhachHangResponse(

        @Schema(description = "ID khách hàng", example = "1")
        Long id,

        @Schema(description = "Mã khách hàng", example = "KH0001")
        String maKhachHang,

        @Schema(description = "Họ và tên", example = "Nguyễn Văn A")
        String hoTen,

        @Schema(description = "Số điện thoại", example = "0987654321")
        String soDienThoai,

        @Schema(description = "Email", example = "nguyenvana@gmail.com")
        String email,

        @Schema(description = "Đường dẫn ảnh đại diện", example = "https://example.com/avatar.jpg")
        String anhDaiDien,

        @Schema(description = "Giới tính", example = "Nam")
        String gioiTinh,

        @Schema(description = "Ngày sinh", example = "1998-05-20")
        LocalDate ngaySinh,

        @Schema(description = "Trạng thái tài khoản (1: Hoạt động, 0: Khóa)", example = "1")
        Integer trangThai,

        @Schema(description = "Ngày tạo tài khoản")
        LocalDateTime ngayTao
) {
    public static HoSoKhachHangResponse tuThucThe(KhachHang kh) {
        if (kh == null) {
            return null;
        }
        return new HoSoKhachHangResponse(
                kh.getId(),
                kh.getMaKhachHang(),
                kh.getHoTen(),
                kh.getSoDienThoai(),
                kh.getEmail(),
                kh.getAnhDaiDien(),
                kh.getGioiTinh(),
                kh.getNgaySinh(),
                kh.getTrangThai(),
                kh.getNgayTao()
        );
    }
}
