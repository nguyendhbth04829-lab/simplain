package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Response kết quả thanh toán POS thành công (Dùng để in biên lai / hóa đơn bán lẻ).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosThanhToanResponse {
    private boolean success;
    private String message;
    private Long donHangId;
    private String maDonHang;
    private String loaiDonHang;
    private String tenKhachHang;
    private String soDienThoaiKhach;
    private String tenNhanVien;
    private String tenPhuongThucThanhToan;
    private BigDecimal tongTienHang;
    private BigDecimal tienGiamGia;
    private BigDecimal tienCoolcashDung;
    private BigDecimal tongThanhToan;
    private BigDecimal tienKhachDua;
    private BigDecimal tienThuaTraKhach;
    private BigDecimal soDuCoolcashConLai;
    private BigDecimal diemTichMoi;
    private String tenHangThanhVien;
    private LocalDateTime ngayTao;
    private String formattedNgayTao;

    @Builder.Default
    private List<PosItemDto> danhSachMatHang = new ArrayList<>();
}
