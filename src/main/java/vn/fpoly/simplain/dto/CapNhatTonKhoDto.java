package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO ghi nhận kết quả trừ tồn kho thực tế của từng biến thể SKU sau khi tạo đơn hàng (US21 - Tiêu chí 2).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapNhatTonKhoDto {

    private Long maBienTheId;

    private String maSku;

    private Integer soLuongDaTru;

    private Integer soLuongConLai;
}
