package vn.fpoly.simplain.inbound;

/**
 * Kết quả phân bổ một SKU lên hai kênh bán.
 *
 * <p>Simplain chỉ có một cột tồn kho vật lý ({@code bien_the_san_pham.so_luong}).
 * Vì vậy {@code soLuongKeOffline} và {@code soLuongWebOnline} là hai góc nhìn
 * của <strong>cùng một lượng tồn</strong>, không phải hai rổ được cộng dồn.
 * Một chiếc áo bán ở quầy sẽ làm giảm đúng lượng hiển thị trên web và ngược lại.</p>
 *
 * <p>Record là bất biến: cùng một snapshot đầu vào luôn cho cùng một kết quả.</p>
 */
public record PhanBoTonKho(
        Long bienTheId,
        String maSku,
        int tonKhoVatLy,
        boolean hienThiKeOffline,
        boolean hienThiWebOnline,
        String nguonTonKho,
        LyDoPhanBo lyDo
) {

    public static final String NGUON_DUNG_CHUNG = "DUNG_CHUNG";

    /**
     * Số lượng khả dụng để bán tại kệ Offline.
     * Không hiển thị thì trả về 0, không bao giờ vượt quá tồn kho vật lý.
     */
    public int soLuongKeOffline() {
        return hienThiKeOffline ? tonKhoVatLy : 0;
    }

    /**
     * Số lượng khả dụng để bán trên Web Online.
     * Không hiển thị thì trả về 0, không bao giờ vượt quá tồn kho vật lý.
     */
    public int soLuongWebOnline() {
        return hienThiWebOnline ? tonKhoVatLy : 0;
    }
}
