package vn.fpoly.simplain.inbound;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NhapKhoRequest {

    @NotBlank(message = "Mã nhà cung cấp không được để trống")
    private String maNhaCungCap;

    private String ghiChu;

    @NotEmpty(message = "Danh sách mặt hàng nhập không được để trống")
    private List<@Valid ChiTietNhapItem> danhSachHang;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiTietNhapItem {

        @NotBlank(message = "Mã SKU không được để trống")
        private String maSku;

        @NotNull(message = "Số lượng nhập không được để trống")
        @Min(value = 1, message = "Số lượng nhập tối thiểu là 1")
        private Integer soLuongNhap;

        @NotNull(message = "Đơn giá nhập không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Đơn giá nhập phải lớn hơn 0")
        private BigDecimal donGiaNhap;
    }
}
