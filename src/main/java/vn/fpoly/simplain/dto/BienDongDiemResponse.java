/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - DTO lịch sử biến động điểm thưởng Coolcash
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import vn.fpoly.simplain.entity.BienDongDiem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Phản hồi chi tiết một giao dịch tích / tiêu điểm Coolcash.
 */
@Schema(description = "Lịch sử biến động điểm thưởng Coolcash")
public record BienDongDiemResponse(

        @Schema(description = "ID bản ghi", example = "1")
        Long id,

        @Schema(description = "ID khách hàng", example = "1")
        Long maKhachHangId,

        @Schema(description = "ID đơn hàng liên quan", example = "100")
        Long maDonHangId,

        @Schema(description = "Loại biến động (CONG_DIEM / TRU_DIEM)", example = "CONG_DIEM")
        String loaiBienDong,

        @Schema(description = "Số điểm biến động", example = "50000")
        BigDecimal soDiem,

        @Schema(description = "Nội dung giải thích giao dịch", example = "Tích điểm hoàn tiền 3% từ đơn hàng DH001")
        String noiDung,

        @Schema(description = "Thời gian ghi nhận")
        LocalDateTime ngayTao
) {
    public static BienDongDiemResponse tuThucThe(BienDongDiem bd) {
        if (bd == null) {
            return null;
        }
        return new BienDongDiemResponse(
                bd.getId(),
                bd.getMaKhachHangId(),
                bd.getMaDonHangId(),
                bd.getLoaiBienDong(),
                bd.getSoDiem(),
                bd.getNoiDung(),
                bd.getNgayTao()
        );
    }
}
