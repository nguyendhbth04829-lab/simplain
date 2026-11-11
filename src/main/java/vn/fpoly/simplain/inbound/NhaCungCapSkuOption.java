package vn.fpoly.simplain.inbound;

public record NhaCungCapSkuOption(Long sanPhamId, String maSanPham, String tenSanPham, String anhSanPhamUrl,
                                  String maSku, Long bienTheId, String anhSkuUrl, java.util.List<NhaCungCapSkuThuocTinh> thuocTinh,
                                  Integer tonKho, boolean kichHoat) {}
