package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Schema(description = "Mã vận đơn thực tế do ĐVVC bên thứ 3 cấp", example = "GHTK-HN12345678")
    private String maVanDon;

    @Schema(description = "Tên hoặc mã ĐVVC bên thứ 3 (GHN, GHTK, Viettel Post, VNPost, J&T)")
    private String donViVanChuyen;

    @Builder.Default
    @Schema(description = "Đánh dấu hàng bị mất / thất lạc trên đường giao (Tồn kho KHÔNG được cộng lại)")
    private Boolean hangBiThatLac = false;

    @Schema(description = "Danh sách ID các chi tiết mặt hàng bị mất trong đơn (nếu mất một phần hoặc chọn cụ thể)")
    private List<Long> chiTietBiMatIds;

    @Schema(description = "Ghi chú / lý do thay đổi trạng thái vận đơn", example = "Đã xuất kho và bàn giao bưu tá")
    private String ghiChu;

    @Schema(description = "ID nhân viên thực hiện (nếu truyền tường minh qua API)", example = "1")
    private Long nguoiThucHienId;
}
