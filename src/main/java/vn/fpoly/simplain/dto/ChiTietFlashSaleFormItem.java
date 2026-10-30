package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DTO chứa thông tin biến thể tham gia Flash Sale trong Form tạo / cập nhật (US29).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietFlashSaleFormItem {

    @NotNull(message = "Biến thể sản phẩm không hợp lệ")
    private Long maBienTheId;

    @NotNull(message = "Giá sốc Flash Sale không được để trống")
    @DecimalMin(value = "1000.0", message = "Giá Flash Sale tối thiểu phải từ 1.000 VNĐ")
    private BigDecimal giaFlashSale;

    @NotNull(message = "Số lượng mở bán không được để trống")
    @Min(value = 1, message = "Số lượng mở bán tối thiểu là 1")
    private Integer soLuongMoBan;

    private Integer soLuongDaBan;

    // Các trường hiển thị bổ trợ trên giao diện
    private String maSku;
    private String tenSanPham;
    private String tenMauSac;
    private String maMauHex;
    private String tenKichThuoc;
    private BigDecimal giaBanGoc;
    private Integer tonKhoHienTai;
    private String anhDaiDienUrl;

    public int getPhanTramGiamGia() {
        if (giaBanGoc == null || giaBanGoc.compareTo(BigDecimal.ZERO) <= 0 || giaFlashSale == null) {
            return 0;
        }
        if (giaFlashSale.compareTo(giaBanGoc) >= 0) {
            return 0;
        }
        BigDecimal diff = giaBanGoc.subtract(giaFlashSale);
        return diff.multiply(BigDecimal.valueOf(100)).divide(giaBanGoc, 0, RoundingMode.HALF_UP).intValue();
    }
}
