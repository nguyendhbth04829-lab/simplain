package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request cập nhật số lượng của một mặt hàng trong tab hóa đơn chờ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosCapNhatSoLuongRequest {

    @NotNull(message = "ID dòng chi tiết không được để trống")
    private Long maChiTietId;

    @NotNull(message = "Số lượng mới không được để trống")
    private Integer soLuong;
}
