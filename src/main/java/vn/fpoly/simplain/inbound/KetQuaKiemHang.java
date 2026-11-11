package vn.fpoly.simplain.inbound;

public enum KetQuaKiemHang {
    CHAP_NHAN_TOAN_BO("Chấp nhận toàn bộ"),
    CHAP_NHAN_MOT_PHAN("Chấp nhận một phần"),
    TU_CHOI_TOAN_BO("Từ chối toàn bộ");

    private final String nhan;

    KetQuaKiemHang(String nhan) { this.nhan = nhan; }

    public String getNhan() { return nhan; }
}
