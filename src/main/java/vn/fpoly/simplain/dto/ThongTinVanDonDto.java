package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO chứa thông tin Vận đơn được cấp tự động từ Đơn vị Vận chuyển bên thứ 3 (GHN, GHTK, Viettel Post, VNPost, J&T).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongTinVanDonDto {
    private String maVanDon;
    private String maDonVi;
    private String tenDonVi;
    private String loaiDichVu;
    private String linkTraCuu;
    private LocalDate ngayGiaoDuKien;
    private BigDecimal phiVanChuyenUocTinh;
    private String trangThaiKetNoi;
    private String thongDiep;
}
