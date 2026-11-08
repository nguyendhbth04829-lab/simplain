package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO dữ liệu tổng hợp chi tiết theo từng ngày trong kỳ báo cáo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietNgayDoanhThuDto {
    private String ngay;
    private long soDon;
    private long soDonHoanThanh;
    private long soDonHuy;
    private BigDecimal doanhThuOnline;
    private BigDecimal doanhThuPos;
    private BigDecimal tongDoanhThu;
    private BigDecimal tienGiamGia;

    public long getSoDonHang() {
        return soDon;
    }

    public BigDecimal getTongChietKhau() {
        return tienGiamGia;
    }
}
