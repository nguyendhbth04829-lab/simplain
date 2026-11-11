package vn.fpoly.simplain.inbound;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TiepNhanDonMuaRequest {
    private String ghiChu;
    @NotEmpty(message = "Nhập số lượng giao và kết quả kiểm cho ít nhất một SKU")
    private List<@Valid ChiTiet> danhSachHang;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ChiTiet {
        @NotBlank private String maSku;
        @NotNull @Min(0) @Max(999999) private Integer soLuongDaGiao;
        @NotNull @Min(0) @Max(999999) private Integer soLuongDat;
        @NotNull @Min(0) @Max(999999) private Integer soLuongTuChoi;
        @NotNull @Min(0) @Max(999999) private Integer soLuongDaTra;
        @Size(max = 1000, message = "Lý do từ chối không được vượt quá 1.000 ký tự")
        private String lyDoTuChoi;
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá thực tế phải lớn hơn 0")
        @DecimalMax(value = "999999999", message = "Giá thực tế vượt giới hạn")
        private BigDecimal donGiaNhapThucTe;
    }
}
