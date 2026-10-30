package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dữ liệu biểu mẫu Hủy đơn hàng từ phía Khách hàng (US23).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dữ liệu yêu cầu hủy đơn hàng từ phía khách hàng (US23)")
public class HuyDonHangRequest {

    @Schema(description = "Lý do hủy đơn hàng được chọn từ danh mục", example = "Tôi muốn thay đổi địa chỉ nhận hàng")
    @NotBlank(message = "Vui lòng chọn lý do hủy đơn hàng!")
    private String lyDoHuy;

    @Schema(description = "Ghi chú chi tiết thêm khi hủy đơn", example = "Địa chỉ mới: Số 45 Lê Duẩn, Đà Nẵng")
    private String ghiChuChiTiet;
}
