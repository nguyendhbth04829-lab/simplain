package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO Thêm gói Combo vào giỏ hàng (US17 - Tiêu chí 2: Bó hàng combo sinh nhom_combo_token).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemComboVaoGioRequest {

    @NotNull(message = "Vui lòng chọn gói Combo")
    private Long maComboId;

    @NotEmpty(message = "Vui lòng chọn đầy đủ các món trong gói Combo")
    private List<Long> danhSachBienTheIds;

    @Builder.Default
    @NotNull(message = "Số lượng combo không được để trống")
    @Min(value = 1, message = "Số lượng combo tối thiểu là 1")
    private Integer soLuongCombo = 1;
}
