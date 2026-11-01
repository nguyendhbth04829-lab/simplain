package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO phản hồi kết quả sau khi Tạo mới Đơn hàng và Trừ tồn kho thành công (US21).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoDonHangResponse {

    private boolean success;

    private String message;

    private Long donHangId;

    private String maDonHang;

    private BigDecimal tongThanhToan;

    private String trangThai;

    private LocalDateTime ngayTao;

    private Integer tongSoLuongSanPham;

    private List<CapNhatTonKhoDto> danhSachTonKhoCapNhat;
}
