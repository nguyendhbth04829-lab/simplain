package vn.fpoly.simplain.inbound;

/**
 * Trạng thái nghiệp vụ của phiếu nhập kho.
 * Quy tắc chuyển trạng thái được kiểm soát tại đây thay vì phụ thuộc vào giao diện.
 */
public enum TrangThaiPhieuNhapKho {

    CHO_NHAP("Chờ nhập"),
    DA_NHAP("Đã nhập"),
    DA_HUY("Đã hủy");

    private final String nhan;

    TrangThaiPhieuNhapKho(String nhan) {
        this.nhan = nhan;
    }

    public String getNhan() {
        return nhan;
    }

    /**
     * Chỉ cho phép các bước chuyển đã được định nghĩa trước.
     * Phiếu đã nhập hoặc đã hủy là trạng thái cuối, không thể thay đổi tiếp.
     */
    public boolean coTheChuyenSang(TrangThaiPhieuNhapKho trangThaiMoi) {
        if (trangThaiMoi == null) {
            return false;
        }
        return switch (this) {
            case CHO_NHAP -> trangThaiMoi == DA_NHAP || trangThaiMoi == DA_HUY;
            case DA_NHAP, DA_HUY -> false;
        };
    }
}
