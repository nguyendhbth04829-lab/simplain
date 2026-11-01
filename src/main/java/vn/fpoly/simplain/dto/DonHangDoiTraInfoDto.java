package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.fpoly.simplain.entity.DonHang;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO dữ liệu đơn hàng và các mặt hàng hỗ trợ tạo biểu mẫu Đổi/Trả (US24).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin đơn hàng hiển thị trên form Đổi/Trả (US24)")
public class DonHangDoiTraInfoDto {

    private DonHang donHang;
    private LocalDateTime ngayHoanThanh;
    private boolean hopLeDeDoiTra;
    private String lyDoKhongHopLe;
    @Builder.Default
    private List<ItemDoiTraFormDto> danhSachItem = new ArrayList<>();
}
