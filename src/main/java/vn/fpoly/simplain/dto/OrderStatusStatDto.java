package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho tỷ lệ phân bổ trạng thái đơn hàng (US05).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thống kê phân bổ theo trạng thái đơn hàng")
public class OrderStatusStatDto {

    @Schema(description = "Mã trạng thái", example = "HOAN_THANH")
    private String statusCode;

    @Schema(description = "Tên trạng thái hiển thị", example = "Giao hàng thành công")
    private String statusName;

    @Schema(description = "Mã màu Hex nhận diện", example = "#10b981")
    private String colorHex;

    @Schema(description = "Số lượng đơn hàng ở trạng thái này", example = "42")
    private Long count;

    @Schema(description = "Tỷ lệ phần trăm trên tổng số đơn (%)", example = "52.5")
    private Double percentage;
}
