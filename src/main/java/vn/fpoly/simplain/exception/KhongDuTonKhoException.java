package vn.fpoly.simplain.exception;

import lombok.Getter;

/**
 * Ngoại lệ ném ra khi số lượng tồn kho của biến thể sản phẩm không đủ để đáp ứng đơn hàng (US21 - Tiêu chí 2).
 * Đảm bảo tính toàn vẹn dữ liệu: Rollback toàn bộ giao dịch, không trừ dở dang.
 */
@Getter
public class KhongDuTonKhoException extends RuntimeException {

    private final Long maBienTheId;
    private final String maSku;
    private final Integer soLuongYeuCau;
    private final Integer soLuongHienCo;

    public KhongDuTonKhoException(Long maBienTheId, String maSku, Integer soLuongYeuCau, Integer soLuongHienCo) {
        super(String.format("Biến thể SKU [%s] (ID: %d) chỉ còn %d cái trong kho, không đủ số lượng để tạo đơn (%d cái)!",
                maSku, maBienTheId, soLuongHienCo, soLuongYeuCau));
        this.maBienTheId = maBienTheId;
        this.maSku = maSku;
        this.soLuongYeuCau = soLuongYeuCau;
        this.soLuongHienCo = soLuongHienCo;
    }

    public KhongDuTonKhoException(String message) {
        super(message);
        this.maBienTheId = null;
        this.maSku = null;
        this.soLuongYeuCau = null;
        this.soLuongHienCo = null;
    }
}
