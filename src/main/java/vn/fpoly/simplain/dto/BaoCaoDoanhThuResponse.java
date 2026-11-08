package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO dữ liệu Báo cáo Thống kê Doanh thu chuyên sâu cho SIMPLAIN CMS.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaoCaoDoanhThuResponse {

    // --- Tiêu chí lọc ---
    private String kyThongKe;
    private LocalDate tuNgay;
    private LocalDate denNgay;
    private String kenhBan;

    // --- Chỉ số tài chính then chốt (KPIs) ---
    @Builder.Default
    private BigDecimal tongDoanhThu = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal tongDoanhThuTruocDo = BigDecimal.ZERO;

    @Builder.Default
    private Double tyLeTangTruongDoanhThu = 0.0;

    @Builder.Default
    private long tongDonHang = 0;

    @Builder.Default
    private long soDonHoanThanh = 0;

    @Builder.Default
    private long soDonHuy = 0;

    @Builder.Default
    private Double tyLeHoanThanh = 0.0;

    @Builder.Default
    private BigDecimal giaTriTrungBinhDon = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal doanhThuOnline = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal doanhThuPos = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal tongTienHangChuaGiam = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal tongTienGiamVoucher = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal tongTienGiamDiem = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal tongPhiVanChuyen = BigDecimal.ZERO;

    // --- Dữ liệu Biểu đồ (Chart.js) ---
    @Builder.Default
    private List<String> nhanThoiGian = new ArrayList<>();

    @Builder.Default
    private List<BigDecimal> duLieuDoanhThu = new ArrayList<>();

    @Builder.Default
    private List<Long> duLieuSoDon = new ArrayList<>();

    @Builder.Default
    private List<BigDecimal> duLieuDoanhThuOnline = new ArrayList<>();

    @Builder.Default
    private List<BigDecimal> duLieuDoanhThuPos = new ArrayList<>();

    @Builder.Default
    private List<String> nhanPhuongThuc = new ArrayList<>();

    @Builder.Default
    private List<BigDecimal> duLieuPhuongThuc = new ArrayList<>();

    // --- Danh sách phân tích ---
    @Builder.Default
    private List<TopSanPhamDoanhThuDto> topSanPham = new ArrayList<>();

    @Builder.Default
    private List<ChiTietNgayDoanhThuDto> chiTietCacNgay = new ArrayList<>();
}
