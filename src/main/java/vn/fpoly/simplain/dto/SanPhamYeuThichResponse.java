/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - DTO phản hồi thông tin sản phẩm trong danh sách yêu thích
 * Cập nhật xác nhận commit US14: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.entity.SanPhamYeuThich;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Dữ liệu chi tiết của một sản phẩm trong danh sách yêu thích.
 */
@Schema(description = "Thông tin sản phẩm trong danh sách yêu thích của khách hàng")
public record SanPhamYeuThichResponse(

        @Schema(description = "ID bản ghi yêu thích", example = "1")
        Long id,

        @Schema(description = "ID của sản phẩm", example = "10")
        Long maSanPhamId,

        @Schema(description = "Mã quản lý sản phẩm", example = "SP001")
        String maSanPham,

        @Schema(description = "Tên gọi sản phẩm", example = "Áo Thun Cotton Compact")
        String tenSanPham,

        @Schema(description = "Đường dẫn slug thân thiện", example = "ao-thun-cotton-compact")
        String duongDanSlug,

        @Schema(description = "Giá cơ bản của sản phẩm (VNĐ)", example = "199000")
        BigDecimal giaCoBan,

        @Schema(description = "Chất liệu sản phẩm", example = "100% Cotton")
        String chatLieu,

        @Schema(description = "Form dáng sản phẩm", example = "Regular Fit")
        String formDang,

        @Schema(description = "Thời điểm thêm vào danh sách yêu thích")
        LocalDateTime ngayThem,

        @Schema(description = "Trạng thái kinh doanh của sản phẩm", example = "true")
        Boolean kichHoat
) {
    public static SanPhamYeuThichResponse taoMoi(SanPhamYeuThich yt, SanPham sp) {
        if (yt == null) {
            return null;
        }
        return new SanPhamYeuThichResponse(
                yt.getId(),
                yt.getMaSanPhamId(),
                sp != null ? sp.getMaSanPham() : null,
                sp != null ? sp.getTenSanPham() : "Sản phẩm không xác định",
                sp != null ? sp.getDuongDanSlug() : null,
                sp != null ? sp.getGiaCoBan() : BigDecimal.ZERO,
                sp != null ? sp.getChatLieu() : null,
                sp != null ? sp.getFormDang() : null,
                yt.getNgayThem(),
                sp != null && Boolean.TRUE.equals(sp.getKichHoat())
        );
    }
}
