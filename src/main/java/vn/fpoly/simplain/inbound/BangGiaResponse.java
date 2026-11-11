package vn.fpoly.simplain.inbound;

import java.math.BigDecimal;
import java.util.List;

public record BangGiaResponse(List<DiemBaoGia> giaMua, List<DiemGiaBan> giaBan) {
    public record DiemBaoGia(Long id, String thoiDiem, BigDecimal gia, String ghiChu) {}
    public record DiemGiaBan(Long id, String thoiDiem, BigDecimal gia, String loaiSuKien) {}
    public record SanPhamOption(Long id, String maSanPham, String tenSanPham) {}
    public record SkuOption(Long id, String maSku, boolean kichHoat) {}
}
