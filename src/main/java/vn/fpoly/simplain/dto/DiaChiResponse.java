/**
 * Tác giả: Gia Minh
 * Chức năng: US13 - DTO thông tin chi tiết địa chỉ nhận hàng trả về cho người dùng
 * Cập nhật xác nhận commit US13: 25/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import vn.fpoly.simplain.entity.DiaChi;

/**
 * Phản hồi chi tiết thông tin địa chỉ giao hàng.
 */
@Schema(description = "Thông tin địa chỉ giao hàng của khách hàng")
public record DiaChiResponse(

        @Schema(description = "ID địa chỉ", example = "1")
        Long id,

        @Schema(description = "Mã khách hàng sở hữu địa chỉ", example = "1")
        Long maKhachHangId,

        @Schema(description = "Họ và tên người nhận", example = "Nguyễn Văn A")
        String tenNguoiNhan,

        @Schema(description = "Số điện thoại nhận hàng", example = "0987654321")
        String soDienThoai,

        @Schema(description = "Tỉnh / Thành phố", example = "Hà Nội")
        String tinhThanh,

        @Schema(description = "Quận / Huyện", example = "Quận Cầu Giấy")
        String quanHuyen,

        @Schema(description = "Phường / Xã", example = "Phường Dịch Vọng")
        String phuongXa,

        @Schema(description = "Địa chỉ chi tiết (số nhà, tên đường)", example = "Số 123, đường Xuân Thủy")
        String diaChiChiTiet,

        @Schema(description = "Địa chỉ đầy đủ ghép từ các cấp hành chính", example = "Số 123, đường Xuân Thủy, Phường Dịch Vọng, Quận Cầu Giấy, Hà Nội")
        String diaChiDayDu,

        @Schema(description = "Cờ đánh dấu địa chỉ mặc định", example = "true")
        Boolean laMacDinh
) {
    public static DiaChiResponse tuThucThe(DiaChi dc) {
        if (dc == null) {
            return null;
        }
        String dayDu = dc.getDiaChiChiTiet() + ", " + dc.getPhuongXa() + ", " + dc.getQuanHuyen() + ", " + dc.getTinhThanh();
        return new DiaChiResponse(
                dc.getId(),
                dc.getMaKhachHangId(),
                dc.getTenNguoiNhan(),
                dc.getSoDienThoai(),
                dc.getTinhThanh(),
                dc.getQuanHuyen(),
                dc.getPhuongXa(),
                dc.getDiaChiChiTiet(),
                dayDu,
                Boolean.TRUE.equals(dc.getLaMacDinh())
        );
    }
}
