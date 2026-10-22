package vn.fpoly.simplain.dto;

import vn.fpoly.simplain.entity.DiaChi;
import vn.fpoly.simplain.entity.PhuongThucThanhToan;

import java.math.BigDecimal;

public record CheckoutKetQua(
        Long maKhachHangId,
        DiaChi diaChi,
        PhuongThucThanhToan phuongThucThanhToan,
        String maGiamGia,
        BigDecimal tongTienHang,
        BigDecimal tienGiamGia,
        BigDecimal tienCoolcashDung,
        BigDecimal phiVanChuyen,
        BigDecimal tongThanhToan,
        int tongSoLuong
) {
}
