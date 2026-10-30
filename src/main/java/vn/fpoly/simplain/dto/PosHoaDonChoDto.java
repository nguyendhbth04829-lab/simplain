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
 * DTO dữ liệu của 1 Tab Hóa đơn chờ tại quầy (POS).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosHoaDonChoDto {
    private Long id;
    private String maHoaDon;
    private Long maKhachHangId;
    private String tenKhachHang;
    private String soDienThoaiKhach;
    private Long maNhanVienId;
    private String tenNhanVien;
    private String ghiChu;
    private String trangThai;
    private LocalDateTime ngayTao;
    private BigDecimal tongTienHang;
    private Integer tongSoLuong;

    @Builder.Default
    private List<PosItemDto> danhSachMatHang = new ArrayList<>();
}
