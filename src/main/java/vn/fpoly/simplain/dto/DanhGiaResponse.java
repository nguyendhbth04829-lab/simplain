/**
 * Tác giả: Gia Minh
 * Chức năng: US15 - DTO phản hồi chi tiết đánh giá sản phẩm
 * Cập nhật xác nhận commit US15: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import vn.fpoly.simplain.entity.DanhGiaSanPham;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Dữ liệu chi tiết của một đánh giá sản phẩm.
 */
@Schema(description = "Thông tin chi tiết đánh giá sản phẩm")
public record DanhGiaResponse(

        @Schema(description = "ID bản ghi đánh giá", example = "1")
        Long id,

        @Schema(description = "ID sản phẩm", example = "10")
        Long maSanPhamId,

        @Schema(description = "Tên sản phẩm", example = "Áo Thun Cotton Compact")
        String tenSanPham,

        @Schema(description = "ID khách hàng đánh giá", example = "1")
        Long maKhachHangId,

        @Schema(description = "Họ tên khách hàng đánh giá", example = "Nguyễn Văn A")
        String tenKhachHang,

        @Schema(description = "ID chi tiết đơn hàng", example = "100")
        Long maChiTietDonHangId,

        @Schema(description = "Số sao đánh giá (1 đến 5)", example = "5")
        Integer soSao,

        @Schema(description = "Nội dung nhận xét", example = "Chất vải đẹp, đúng mô tả")
        String noiDung,

        @Schema(description = "Danh sách URL ảnh đính kèm")
        List<String> danhSachAnh,

        @Schema(description = "Thời điểm gửi đánh giá")
        LocalDateTime ngayTao
) {
    public static DanhGiaResponse taoMoi(DanhGiaSanPham dg, String tenSanPham, String tenKhachHang, List<String> danhSachAnh) {
        if (dg == null) {
            return null;
        }
        return new DanhGiaResponse(
                dg.getId(),
                dg.getMaSanPhamId(),
                tenSanPham != null ? tenSanPham : "Sản phẩm không xác định",
                dg.getMaKhachHangId(),
                tenKhachHang != null ? tenKhachHang : "Khách hàng Simplain",
                dg.getMaChiTietDonHangId(),
                dg.getSoSao(),
                dg.getNoiDung(),
                danhSachAnh != null ? danhSachAnh : Collections.emptyList(),
                dg.getNgayTao()
        );
    }
}
