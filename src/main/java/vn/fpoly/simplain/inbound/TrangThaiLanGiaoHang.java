package vn.fpoly.simplain.inbound;

public enum TrangThaiLanGiaoHang {
    DANG_GIAO("Đang giao"),
    THAT_BAI("Giao thất bại"),
    DA_GIAO("Đã giao");

    private final String nhan;

    TrangThaiLanGiaoHang(String nhan) { this.nhan = nhan; }

    public String getNhan() { return nhan; }
}
