package vn.fpoly.simplain.inbound;

public enum LoaiHoatDongHopTac {
    TAO_HO_SO("Tạo hồ sơ"),
    THAY_DOI_TRANG_THAI("Thay đổi trạng thái"),
    GOI_DIEN("Gọi điện"),
    GUI_EMAIL("Gửi email"),
    THUONG_LUONG("Thương lượng"),
    TRAO_DOI_BAO_GIA("Trao đổi báo giá"),
    GHI_CHU("Ghi chú"),
    KHAC("Khác");

    private final String nhan;

    LoaiHoatDongHopTac(String nhan) {
        this.nhan = nhan;
    }

    public String getNhan() {
        return nhan;
    }

    public boolean laHoatDongThuCong() {
        return this != TAO_HO_SO && this != THAY_DOI_TRANG_THAI;
    }
}
