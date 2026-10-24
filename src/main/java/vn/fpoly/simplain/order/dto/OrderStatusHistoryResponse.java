package vn.fpoly.simplain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO phản hồi lịch sử chuyển trạng thái của đơn hàng (US22, US23).
 */
@Schema(description = "Thông tin dòng lịch sử trạng thái đơn hàng")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryResponse {

    @Schema(description = "ID bản ghi lịch sử", example = "1")
    private Long id;

    @Schema(description = "Mã trạng thái", example = "DANG_GIAO")
    private String maTrangThai;

    @Schema(description = "Tên trạng thái hiển thị", example = "Đang giao hàng")
    private String tenTrangThai;

    @Schema(description = "Mã màu hex để hiển thị giao diện", example = "#8B5CF6")
    private String maMauHex;

    @Schema(description = "ID nhân viên thực hiện chuyển trạng thái", example = "2")
    private Long nguoiThucHienId;

    @Schema(description = "Ghi chú tiến trình", example = "Đã xuất kho và bàn giao bưu tá")
    private String ghiChu;

    @Schema(description = "Thời gian chuyển trạng thái", example = "2026-10-24T21:40:00")
    private LocalDateTime ngayChuyen;
}
