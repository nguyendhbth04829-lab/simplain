package vn.fpoly.simplain.inbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NhapKhoResponse {

    private String trangThai;
    private String thongBao;
    private String maPhieu;
    private String nhaCungCap;
    private BigDecimal tongTien;
    private LocalDateTime ngayNhap;
    private List<ChiTietKetQua> chiTiet;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiTietKetQua {
        private String maSku;
        private Integer soLuongNhap;
        private Integer tonTruoc;
        private Integer tonSau;
        private BigDecimal donGiaNhap;
        private BigDecimal giaGocMoi;
    }
}
