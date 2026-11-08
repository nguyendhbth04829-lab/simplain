package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO dữ liệu Top sản phẩm mang lại doanh thu cao nhất.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSanPhamDoanhThuDto {
    private String tenSanPham;
    private Long soLuongDaBan;
    private BigDecimal doanhThu;
    private Double tyLeDongGop;

    public Long getSoLuongBan() {
        return soLuongDaBan;
    }
}
