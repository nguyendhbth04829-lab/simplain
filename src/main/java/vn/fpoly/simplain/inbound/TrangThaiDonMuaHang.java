package vn.fpoly.simplain.inbound;

public enum TrangThaiDonMuaHang {
    NHAP("Nháp"),
    CHO_NCC_PHAN_HOI("Chờ nhà cung cấp phản hồi"),
    DANG_THUONG_LUONG("Đang thương lượng đơn mua"),
    DA_CHOT_DON("Đã chốt đơn"),
    DANG_CHUAN_BI_HANG("Đang chuẩn bị hàng"),
    DANG_GIAO("Đang giao"),
    CHO_KIEM_HANG("Chờ kiểm hàng"),
    CHO_GIAO_BU("Chờ giao bổ sung"),
    DA_HOAN_TAT("Đã hoàn tất"),
    DA_HUY("Đã hủy");

    private final String nhan;

    TrangThaiDonMuaHang(String nhan) { this.nhan = nhan; }

    public String getNhan() { return nhan; }

    public boolean choPhepChuyenSang(TrangThaiDonMuaHang moi) {
        if (moi == null) return false;
        return switch (this) {
            case NHAP -> moi == CHO_NCC_PHAN_HOI || moi == DA_HUY;
            case CHO_NCC_PHAN_HOI -> moi == DANG_THUONG_LUONG || moi == DA_CHOT_DON || moi == DA_HUY;
            case DANG_THUONG_LUONG -> moi == DA_CHOT_DON || moi == DA_HUY;
            case DA_CHOT_DON -> moi == DANG_CHUAN_BI_HANG || moi == DA_HUY;
            case DANG_CHUAN_BI_HANG, CHO_GIAO_BU -> moi == DANG_GIAO || moi == DA_HUY;
            case DANG_GIAO -> moi == CHO_KIEM_HANG || moi == CHO_GIAO_BU;
            case CHO_KIEM_HANG -> moi == DA_HOAN_TAT || moi == CHO_GIAO_BU;
            case DA_HOAN_TAT, DA_HUY -> false;
        };
    }
}
