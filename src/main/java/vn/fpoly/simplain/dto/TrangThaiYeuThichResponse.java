/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - DTO phản hồi trạng thái thả tim / yêu thích sản phẩm
 * Cập nhật xác nhận commit US14: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Trạng thái yêu thích sản phẩm của khách hàng (dùng cho nút thả tim/toggle).
 */
@Schema(description = "Kết quả kiểm tra hoặc thay đổi trạng thái yêu thích sản phẩm")
public record TrangThaiYeuThichResponse(

        @Schema(description = "ID của sản phẩm", example = "10")
        Long maSanPhamId,

        @Schema(description = "Trạng thái sản phẩm hiện tại đã được yêu thích hay chưa", example = "true")
        Boolean daYeuThich,

        @Schema(description = "Thông điệp kết quả", example = "Đã thêm sản phẩm vào danh sách yêu thích")
        String thongDiep
) {
}
