package vn.fpoly.simplain.paymentmethod;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Dữ liệu trả về của phương thức thanh toán.
 */
@Schema(description = "Phương thức thanh toán trả về cho client")
public record PaymentMethodResponse(

        @Schema(description = "Id tự tăng", example = "1")
        Long id,

        @Schema(description = "Mã duy nhất", example = "COD")
        String maPhuongThuc,

        @Schema(description = "Tên hiển thị", example = "Thanh toán khi nhận hàng")
        String tenPhuongThuc,

        @Schema(description = "true = trực tuyến, false = COD", example = "false")
        Boolean laTrucTuyen,

        @Schema(description = "true = đang bật, false = đã tắt", example = "true")
        Boolean kichHoat) {

    /**
     * Chuyển entity sang DTO để Controller không trả entity trực tiếp.
     *
     * @param entity thực thể trong DB
     * @return DTO kết quả
     */
    public static PaymentMethodResponse from(PaymentMethod entity) {
        return new PaymentMethodResponse(
                entity.getId(),
                entity.getMaPhuongThuc(),
                entity.getTenPhuongThuc(),
                entity.getLaTrucTuyen(),
                entity.getKichHoat());
    }
}
