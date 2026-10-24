package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO đại diện cho một đơn hàng mới nhất trên Dashboard tổng quan (US05).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin tóm tắt đơn hàng mới nhất")
public class RecentOrderDto {

    @Schema(description = "Mã đơn hàng", example = "DH20261024-001")
    private String orderCode;

    @Schema(description = "Tên khách hàng / người nhận", example = "Trần Mua Hàng")
    private String customerName;

    @Schema(description = "Số điện thoại", example = "0901234567")
    private String customerPhone;

    @Schema(description = "Tổng tiền thanh toán", example = "450000")
    private BigDecimal totalAmount;

    @Schema(description = "Tổng tiền đã định dạng VND", example = "450.000 ₫")
    private String formattedAmount;

    @Schema(description = "Mã trạng thái", example = "HOAN_THANH")
    private String statusCode;

    @Schema(description = "Tên trạng thái", example = "Đã giao hàng")
    private String statusName;

    @Schema(description = "Mã màu Hex trạng thái", example = "#10b981")
    private String statusColorHex;

    @Schema(description = "Thời gian đặt hàng")
    private LocalDateTime createdAt;

    @Schema(description = "Thời gian đã định dạng", example = "10:35 24/10/2026")
    private String formattedCreatedAt;
}
