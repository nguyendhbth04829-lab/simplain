package vn.fpoly.simplain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VoucherCheckoutItem(
        String maCode,
        String tenVoucher,
        String loaiGiamGia,
        BigDecimal giaTriGiam,
        BigDecimal giamToiDa,
        BigDecimal giaTriDonToiThieu,
        LocalDateTime ngayKetThuc
) {
}
