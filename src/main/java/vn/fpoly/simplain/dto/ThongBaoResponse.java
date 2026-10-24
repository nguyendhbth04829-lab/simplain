/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - DTO thông điệp kết quả thao tác
 * Cập nhật xác nhận commit US12: 24/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Thông điệp phản hồi kết quả thao tác.
 */
@Schema(description = "Thông điệp kết quả thao tác")
public record ThongBaoResponse(

        @Schema(description = "Nội dung thông báo", example = "Đổi mật khẩu thành công")
        String thongDiep,

        @Schema(description = "Thời điểm thực hiện")
        LocalDateTime thoiGian
) {
    public ThongBaoResponse(String thongDiep) {
        this(thongDiep, LocalDateTime.now());
    }
}
