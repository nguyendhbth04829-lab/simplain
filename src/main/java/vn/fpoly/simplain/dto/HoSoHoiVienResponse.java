/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - DTO thông tin chi tiết hồ sơ hội viên và quyền lợi hoàn tiền Coolcash
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Phản hồi chi tiết hồ sơ hội viên, hạng thẻ và số dư điểm Coolcash.
 */
@Schema(description = "Thông tin chi tiết hồ sơ hội viên và số dư điểm Coolcash")
public record HoSoHoiVienResponse(

        @Schema(description = "ID khách hàng", example = "1")
        Long maKhachHangId,

        @Schema(description = "Họ tên khách hàng", example = "Nguyễn Văn A")
        String hoTen,

        @Schema(description = "ID hạng thành viên hiện tại", example = "2")
        Long maHangHienTaiId,

        @Schema(description = "Tên hạng thành viên", example = "Thành viên Bạc")
        String tenHangHienTai,

        @Schema(description = "Tỷ lệ hoàn tiền theo hạng (%)", example = "3.0")
        BigDecimal phanTramHoanTien,

        @Schema(description = "Số dư điểm Coolcash khả dụng (VNĐ)", example = "150000")
        BigDecimal soDuCoolcash,

        @Schema(description = "Tổng chi tiêu tích lũy trong năm (VNĐ)", example = "2500000")
        BigDecimal tongChiTieuNam,

        @Schema(description = "Tên hạng thành viên tiếp theo", example = "Thành viên Vàng")
        String tenHangTiepTheo,

        @Schema(description = "Chi tiêu cần thêm để nâng hạng tiếp theo (VNĐ)", example = "2500000")
        BigDecimal chiTieuCanThemDeLenHang
) {
}
