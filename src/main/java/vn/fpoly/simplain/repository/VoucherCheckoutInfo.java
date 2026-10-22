package vn.fpoly.simplain.repository;

import java.math.BigDecimal;

public record VoucherCheckoutInfo(Long id, String maCode, String loaiGiamGia,
                                  BigDecimal giaTriGiam, BigDecimal giamToiDa,
                                  BigDecimal giaTriDonToiThieu) {
}
