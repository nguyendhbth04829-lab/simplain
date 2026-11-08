package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.fpoly.simplain.entity.BienDongDiem;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO hiển thị thông tin Hội viên, Hạng thành viên và Tích điểm Coolcash (US16).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoiVienResponse {

    private Long maKhachHangId;
    private String maKhachHang;
    private String hoTen;

    // Thông tin Hạng hiện tại
    private Long maHangHienTaiId;
    private String tenHang;
    private BigDecimal phanTramHoanTien;
    private BigDecimal chiTieuToiThieuHangHienTai;

    // Số dư điểm và Chi tiêu
    private BigDecimal soDuCoolcash;
    private BigDecimal tongChiTieuNam;

    // Thông tin tiến trình lên hạng tiếp theo
    private String tenHangTiepTheo;
    private BigDecimal chiTieuMocTiepTheo;
    private BigDecimal chiTieuCanThemDeLenHang;
    private Integer phanTramTienTrinh; // 0 -> 100%

    // Lịch sử biến động điểm gần đây
    private List<BienDongDiem> danhSachBienDong;
}
