package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request thực hiện thanh toán hóa đơn bán hàng tại quầy (POS).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosThanhToanRequest {

    @NotNull(message = "ID hóa đơn chờ không được để trống")
    private Long maHoaDonChoId;

    private Long maKhachHangId;

    private String tenKhachHang;

    private String soDienThoaiKhach;

    @NotNull(message = "Vui lòng chọn phương thức thanh toán")
    private Long maPhuongThucId;

    @Builder.Default
    private BigDecimal tienGiamGia = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal tienCoolcashDung = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal tienKhachDua = BigDecimal.ZERO;

    private String ghiChu;
}
