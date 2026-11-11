package vn.fpoly.simplain.inbound;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.DecimalMax;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DonMuaHangRequest {
    @NotNull(message = "Vui lòng chọn nhà cung cấp")
    private Long nhaCungCapId;

    private String ghiChu;

    @NotEmpty(message = "Đơn mua phải có ít nhất một SKU")
    private List<@Valid ChiTiet> danhSachHang;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ChiTiet {
        @NotBlank(message = "Mã SKU không được để trống")
        private String maSku;
        @NotNull @Min(value = 1, message = "Số lượng đặt tối thiểu là 1") @Max(value = 999999, message = "Số lượng đặt tối đa là 999.999")
        private Integer soLuongDat;
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá chốt phải lớn hơn 0")
        @DecimalMax(value = "999999999", message = "Giá chốt vượt giới hạn")
        private BigDecimal donGiaChot;
    }
}
