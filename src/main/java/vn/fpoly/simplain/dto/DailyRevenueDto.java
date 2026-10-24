package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO đại diện cho số liệu doanh thu và đơn hàng của một ngày phục vụ biểu đồ (US05).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Số liệu doanh thu và số đơn hàng theo từng ngày")
public class DailyRevenueDto {

    @Schema(description = "Nhãn ngày (ví dụ: 18/10, 19/10)", example = "24/10")
    private String dateLabel;

    @Schema(description = "Thứ trong tuần (T2, T3, T4, T5, T6, T7, CN)", example = "Hôm nay")
    private String dayOfWeek;

    @Schema(description = "Doanh thu trong ngày", example = "12500000")
    private BigDecimal revenue;

    @Schema(description = "Doanh thu đã định dạng tiền tệ VND", example = "12.500.000 ₫")
    private String formattedRevenue;

    @Schema(description = "Số lượng đơn hàng hoàn thành trong ngày", example = "18")
    private Long orderCount;

    @Schema(description = "Tỷ lệ chiều cao cột biểu đồ (%)", example = "75")
    private Integer heightPercentage;
}
