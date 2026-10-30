package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO biểu diễn một Bó hàng Combo trong giỏ hàng (US17 - Tiêu chí 2: Sinh nhom_combo_token).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BocComboDto {

    private String nhomComboToken;
    private Long maComboId;
    private String maCombo;
    private String tenCombo;
    private String duongDanSlug;
    private BigDecimal giaCombo;
    private Integer soLuong;
    private BigDecimal thanhTien;
    private List<ItemGioHangDto> danhSachMonCon;
}
