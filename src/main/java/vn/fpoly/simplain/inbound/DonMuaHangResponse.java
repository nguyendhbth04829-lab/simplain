package vn.fpoly.simplain.inbound;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DonMuaHangResponse(
        Long id,
        String maDon,
        Long nhaCungCapId,
        String maNcc,
        String tenNcc,
        String trangThai,
        String nhanTrangThai,
        LocalDateTime ngayTao,
        BigDecimal tongTien,
        String dieuKienGiaoHang,
        String ghiChu,
        List<ChiTiet> chiTiet,
        List<LanGiao> lanGiao,
        List<HoatDong> lichSu
) {
    public record ChiTiet(String maSku, String tenSanPham, int soLuongDat, int soLuongDaNhan,
                          BigDecimal donGiaChot, BigDecimal thanhTien) {}
    public record LanGiao(Long id, int lanThu, String trangThai, String nhanTrangThai,
                          LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc,
                          String lyDoThatBai, String benChiuTrachNhiem, String bangChung) {}
    public record HoatDong(String loaiHoatDong, String trangThaiTruoc, String trangThaiMoi,
                           String ghiChu, String tenNhanVien, LocalDateTime ngayTao) {}
}
