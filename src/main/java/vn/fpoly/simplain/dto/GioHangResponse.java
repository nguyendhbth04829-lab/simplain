package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO trả về toàn bộ dữ liệu giỏ hàng để hiển thị giao diện (US17).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GioHangResponse {

    private Long gioHangId;
    private Integer tongSoLuongItem;
    private BigDecimal tongTienHang;
    private BigDecimal tienGiamGia;
    private BigDecimal tongThanhToan;
    private List<ItemGioHangDto> danhSachItemLe;
    private List<BocComboDto> danhSachBocCombo;
    private Boolean gioHangRong;
}
