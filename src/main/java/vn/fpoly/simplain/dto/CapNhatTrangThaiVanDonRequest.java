package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Yêu cầu Cập nhật trạng thái đơn hàng & mã vận đơn (US22 - Mod 5 - Hưng).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu cập nhật trạng thái đơn hàng & mã vận đơn (US22)")
public class CapNhatTrangThaiVanDonRequest {

    @NotBlank(message = "Mã trạng thái đơn hàng không được để trống")
    @Schema(description = "Mã trạng thái mới (CHO_XAC_NHAN, DANG_XU_LY, DANG_GIAO, HOAN_THANH, DA_HUY)", example = "DANG_GIAO")
    private String maTrangThai;

    @Schema(description = "Mã vận đơn thực tế (nếu có, e.g. GHN, ViettelPost, SPX)", example = "SPX123456789VN")
    private String maVanDon;

    @Schema(description = "Ghi chú / lý do thay đổi trạng thái vận đơn", example = "Đã xuất kho và bàn giao bưu tá")
    private String ghiChu;

    @Schema(description = "ID nhân viên thực hiện (nếu truyền tường minh qua API)", example = "1")
    private Long nguoiThucHienId;
}
