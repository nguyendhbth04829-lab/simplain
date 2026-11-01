package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO đại diện cho một dòng biến thể trong Ma trận phân loại hàng loạt SIMPLAIN Matrix.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BienTheHangLoatItemRequest {

    private Long maSanPhamId;
    private String maSanPham;
    private String tenSanPham;

    @NotNull(message = "Màu sắc không được để trống!")
    private Long maMauSacId;

    private String tenMau;
    private String maHex;

    @NotNull(message = "Kích thước (Size) không được để trống!")
    private Long maKichThuocId;

    private String maKichThuoc;
    private String tenKichThuoc;

    private String maSku;

    private String maVachBarcode;

    @NotNull(message = "Giá bán không được để trống!")
    @DecimalMin(value = "0.0", message = "Giá bán không được âm")
    private BigDecimal giaBan;

    private BigDecimal giaGoc;

    @NotNull(message = "Số lượng tồn kho không được để trống!")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private Integer soLuong;

    @Builder.Default
    private Boolean kichHoat = true;
}
