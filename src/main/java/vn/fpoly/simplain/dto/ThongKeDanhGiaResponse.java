/**
 * Tác giả: Gia Minh
 * Chức năng: US15 - DTO thống kê đánh giá sao của sản phẩm
 * Cập nhật xác nhận commit US15: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Thống kê tổng hợp số sao đánh giá của một sản phẩm.
 */
@Schema(description = "Thống kê điểm trung bình và phân bố số sao đánh giá của sản phẩm")
public record ThongKeDanhGiaResponse(

        @Schema(description = "ID sản phẩm", example = "10")
        Long maSanPhamId,

        @Schema(description = "Điểm đánh giá trung bình (1.0 đến 5.0)", example = "4.8")
        Double diemTrungBinh,

        @Schema(description = "Tổng số lượt đánh giá", example = "25")
        long tongSoDanhGia,

        @Schema(description = "Số lượng đánh giá 5 sao", example = "20")
        long soLuong5Sao,

        @Schema(description = "Số lượng đánh giá 4 sao", example = "4")
        long soLuong4Sao,

        @Schema(description = "Số lượng đánh giá 3 sao", example = "1")
        long soLuong3Sao,

        @Schema(description = "Số lượng đánh giá 2 sao", example = "0")
        long soLuong2Sao,

        @Schema(description = "Số lượng đánh giá 1 sao", example = "0")
        long soLuong1Sao
) {
}
