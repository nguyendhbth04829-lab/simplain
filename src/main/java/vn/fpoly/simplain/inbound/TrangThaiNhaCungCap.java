package vn.fpoly.simplain.inbound;

public enum TrangThaiNhaCungCap {
    TIEM_NANG("Tiềm năng", "Đang trong giai đoạn đánh giá và tiếp cận"),
    DANG_THOA_THUAN("Đang thỏa thuận", "Đang thương lượng điều kiện hợp tác"),
    DANG_HOP_TAC("Đang hợp tác", "Đang sẵn sàng cung ứng"),
    TAM_NGUNG("Tạm ngưng", "Tạm dừng tiếp nhận nhập kho"),
    THOA_THUAN_THAT_BAI("Thỏa thuận thất bại", "Có thể bắt đầu thỏa thuận lại"),
    NGUNG_HOP_TAC("Ngừng hợp tác", "Đã kết thúc quan hệ hợp tác");

    private final String nhan;
    private final String moTa;

    TrangThaiNhaCungCap(String nhan, String moTa) {
        this.nhan = nhan;
        this.moTa = moTa;
    }

    public String getNhan() {
        return nhan;
    }

    public String getMoTa() {
        return moTa;
    }

    public boolean choPhepChuyenSang(TrangThaiNhaCungCap trangThaiMoi) {
        if (trangThaiMoi == null) return false;
        return switch (this) {
            case TIEM_NANG -> trangThaiMoi == DANG_THOA_THUAN;
            case DANG_THOA_THUAN -> trangThaiMoi == DANG_HOP_TAC || trangThaiMoi == THOA_THUAN_THAT_BAI;
            case THOA_THUAN_THAT_BAI -> trangThaiMoi == DANG_THOA_THUAN;
            case DANG_HOP_TAC -> trangThaiMoi == TAM_NGUNG;
            case TAM_NGUNG -> trangThaiMoi == DANG_HOP_TAC;
            case NGUNG_HOP_TAC -> false;
        };
    }
}
