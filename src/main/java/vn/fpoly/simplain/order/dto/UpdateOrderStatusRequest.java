package vn.fpoly.simplain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO yêu cầu cập nhật trạng thái đơn hàng và mã vận đơn (US22).
 */
@Schema(description = "Dữ liệu yêu cầu cập nhật trạng thái đơn hàng")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {

    @Schema(
            description = "Mã trạng thái mới (VD: CHO_XAC_NHAN, CHO_GIAO_HANG, DANG_GIAO, DA_GIAO, HOAN_THANH, DA_HUY)",
            example = "DANG_GIAO"
    )
    @NotBlank(message = "Mã trạng thái mới không được để trống")
    private String maTrangThaiMoi;

    @Schema(description = "Mã vận đơn do đơn vị vận chuyển cung cấp", example = "VTP987654321")
    private String maVanDon;

    @Schema(description = "ID nhân viên hoặc quản trị viên thực hiện cập nhật", example = "2")
    private Long nguoiThucHienId;

    @Schema(description = "Ghi chú lý do chuyển trạng thái hoặc tiến trình giao", example = "Đã bàn giao cho bưu tá giao hàng")
    private String ghiChu;
}
