package vn.fpoly.simplain.repository;

import java.math.BigDecimal;

/** Projection thông tin sản phẩm/biến thể dùng để hiển thị giỏ hàng. */
public interface ThongTinSanPhamGioHang {
    Long getMaBienTheId();

    String getTenSanPham();

    String getTenMau();

    String getTenKichThuoc();

    BigDecimal getGiaBan();

    BigDecimal getGiaGoc();

    String getHinhAnhUrl();
}
