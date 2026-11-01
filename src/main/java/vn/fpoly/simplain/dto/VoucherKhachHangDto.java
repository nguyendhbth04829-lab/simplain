package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO hiển thị thông tin Voucher cho Khách hàng trên Giao diện và API (US27).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherKhachHangDto {

    private Long id; // Id voucher_so_huu (nếu đã thu thập) hoặc Id voucher
    private Long voucherId;
    private String maCode;
    private String tenVoucher;
    private String loaiGiamGia;
    private BigDecimal giaTriGiam;
    private BigDecimal giamToiDa;
    private BigDecimal giaTriDonToiThieu;
    private Integer tongSoLuong;
    private Integer soLuongDaDung;
    private Integer gioiHanMoiUser;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private String moTa;

    // Trạng thái sở hữu cá nhân
    private boolean daThuThap;
    private boolean daSuDung;
    private LocalDateTime ngayNhan;
    private LocalDateTime ngaySuDung;

    // Trạng thái tính toán hiển thị
    private boolean khaDung; // Còn hạn, chưa dùng, còn lượt
    private String trangThaiHienThi; // KHA_DUNG, DA_SU_DUNG, HET_HAN, HET_LUOT, CHUA_BAT_DAU
    private String trangThaiLabel;
    private String trangThaiBadgeClass;
    private String dieuKienApDung;
    private String hanSuDungFormatted;

    public String getGiaTriGiamFormatted() {
        if ("PERCENT".equalsIgnoreCase(this.loaiGiamGia)) {
            return (this.giaTriGiam != null) ? this.giaTriGiam.stripTrailingZeros().toPlainString() + "%" : "0%";
        } else {
            if (this.giaTriGiam == null) return "0 đ";
            long val = this.giaTriGiam.longValue();
            if (val >= 1000 && val % 1000 == 0) {
                return (val / 1000) + "k";
            }
            return String.format("%,d đ", val);
        }
    }

    public String getGiamToiDaFormatted() {
        if (this.giamToiDa == null || this.giamToiDa.compareTo(BigDecimal.ZERO) <= 0) {
            return "";
        }
        long val = this.giamToiDa.longValue();
        if (val >= 1000 && val % 1000 == 0) {
            return "Tối đa " + (val / 1000) + "k";
        }
        return String.format("Tối đa %,d đ", val);
    }
}
