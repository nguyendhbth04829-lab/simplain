package vn.fpoly.simplain.inbound;

/**
 * Lý do một SKU được/không được phân bổ lên kệ Offline hoặc hiển thị Web Online.
 * Dùng enum thay vì chuỗi tự do để kết quả luôn so sánh được và không phụ thuộc ngôn ngữ hiển thị.
 */
public enum LyDoPhanBo {
    SAN_SANG,
    KHONG_CO_BIEN_THE,
    BIEN_THE_NGUNG_KINH_DOANH,
    SAN_PHAM_NGUNG_KINH_DOANH,
    HET_HANG
}
