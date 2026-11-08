package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO phản hồi kết quả kiểm tra tính hợp lệ của mã giảm giá (US27 - Tiêu chí 2).
 * Cung cấp thông báo lỗi chi tiết, rõ ràng để người dùng nhận biết ngay lập tức.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateVoucherResultDto {

    private boolean hopLe;
    private String thongBao;
    private String maCode;
    private Long voucherId;
    private String tenVoucher;
    private String loaiGiamGia;
    private BigDecimal giaTriGiam;
    private BigDecimal giamToiDa;
    private BigDecimal giaTriDonToiThieu;
    private BigDecimal tienGiam;
    private BigDecimal soTienConThieu;
    private BigDecimal tongThanhToanMoi;
    private String lyDoChiTiet;
}
