package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.fpoly.simplain.entity.YeuCauDoiTra;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO chi tiết phiếu Yêu cầu Đổi/Trả (RMA) phục vụ xem trạng thái và tiến trình (US24).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin chi tiết yêu cầu RMA đổi/trả online (US24)")
public class YeuCauDoiTraDetailDto {

    private YeuCauDoiTra yeuCauDoiTra;
    private String tenTrangThaiTiengViet;
    private String badgeClass;
    private String tenNganHang;
    private String soTaiKhoan;
    private String tenChuTaiKhoan;
    private int tongSoLuongTra;
    private BigDecimal tongTienUocTinh;
    @Builder.Default
    private List<ChiTietSanPhamDoiTraDto> danhSachSanPhamDoiTra = new ArrayList<>();

    /**
     * Danh sách các URL hình ảnh minh chứng để hiển thị gallery trên giao diện
     */
    public List<String> getDanhSachHinhAnh() {
        if (yeuCauDoiTra != null) {
            return yeuCauDoiTra.getDanhSachHinhAnh();
        }
        return new ArrayList<>();
    }
}
