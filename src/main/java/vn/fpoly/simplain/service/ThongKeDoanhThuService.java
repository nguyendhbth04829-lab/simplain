package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.BaoCaoDoanhThuResponse;
import vn.fpoly.simplain.dto.ChiTietNgayDoanhThuDto;
import vn.fpoly.simplain.dto.TopSanPhamDoanhThuDto;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.repository.ChiTietDonHangRepository;
import vn.fpoly.simplain.repository.DonHangRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service tính toán và tổng hợp dữ liệu Báo cáo Thống kê Doanh thu chuyên sâu cho SIMPLAIN CMS.
 * Cung cấp các chỉ số tài chính chuẩn xác, tỷ trọng theo kênh, biểu đồ doanh thu và Top sản phẩm bán chạy.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThongKeDoanhThuService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;

    private static final DateTimeFormatter FMT_NGAY_LABEL = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter FMT_NGAY_TABLE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Tạo báo cáo doanh thu theo kỳ thống kê, khoảng ngày và kênh bán hàng.
     */
    @Transactional(readOnly = true)
    public BaoCaoDoanhThuResponse taoBaoCaoDoanhThu(String kyThongKe, String tuNgayStr, String denNgayStr, String kenhBan) {
        LocalDate homNay = LocalDate.now();
        LocalDate tuNgay;
        LocalDate denNgay;

        String kyChuan = (kyThongKe != null && !kyThongKe.isBlank()) ? kyThongKe.trim().toUpperCase() : "30_NGAY";
        String kenhChuan = (kenhBan != null && !kenhBan.isBlank()) ? kenhBan.trim().toUpperCase() : "ALL";

        switch (kyChuan) {
            case "HOM_NAY":
                tuNgay = homNay;
                denNgay = homNay;
                break;
            case "HOM_QUA":
                tuNgay = homNay.minusDays(1);
                denNgay = homNay.minusDays(1);
                break;
            case "7_NGAY":
                tuNgay = homNay.minusDays(6);
                denNgay = homNay;
                break;
            case "THANG_NAY":
                tuNgay = homNay.withDayOfMonth(1);
                denNgay = homNay;
                break;
            case "THANG_TRUOC":
                LocalDate thangTruoc = homNay.minusMonths(1);
                tuNgay = thangTruoc.withDayOfMonth(1);
                denNgay = thangTruoc.withDayOfMonth(thangTruoc.lengthOfMonth());
                break;
            case "NAM_NAY":
                tuNgay = homNay.withDayOfYear(1);
                denNgay = homNay;
                break;
            case "TUY_CHON":
                try {
                    tuNgay = (tuNgayStr != null && !tuNgayStr.isBlank()) ? LocalDate.parse(tuNgayStr.trim()) : homNay.minusDays(29);
                    denNgay = (denNgayStr != null && !denNgayStr.isBlank()) ? LocalDate.parse(denNgayStr.trim()) : homNay;
                } catch (Exception ex) {
                    tuNgay = homNay.minusDays(29);
                    denNgay = homNay;
                }
                break;
            case "30_NGAY":
            default:
                kyChuan = "30_NGAY";
                tuNgay = homNay.minusDays(29);
                denNgay = homNay;
                break;
        }

        if (tuNgay.isAfter(denNgay)) {
            LocalDate temp = tuNgay;
            tuNgay = denNgay;
            denNgay = temp;
        }

        LocalDateTime batDau = tuNgay.atStartOfDay();
        LocalDateTime ketThuc = denNgay.atTime(LocalTime.MAX);

        // 1. Lấy tất cả đơn hàng trong khoảng thời gian
        List<DonHang> danhSachDonGoc = donHangRepository.findByNgayTaoBetweenOrderByNgayTaoAsc(batDau, ketThuc);

        // 2. Lọc theo kênh bán hàng nếu có yêu cầu
        List<DonHang> danhSachDon = danhSachDonGoc.stream().filter(dh -> {
            boolean isPos = "TAI_QUAY".equalsIgnoreCase(dh.getLoaiDonHang());
            if ("ONLINE".equals(kenhChuan)) return !isPos;
            if ("POS".equals(kenhChuan)) return isPos;
            return true;
        }).collect(Collectors.toList());

        // 3. Phân loại đơn hàng
        List<DonHang> donThanhCong = danhSachDon.stream().filter(dh ->
                dh.getTrangThaiDonHang() != null &&
                        ("HOAN_THANH".equalsIgnoreCase(dh.getTrangThaiDonHang().getMaTrangThai()) ||
                         "DA_DOI_HANG".equalsIgnoreCase(dh.getTrangThaiDonHang().getMaTrangThai()))
        ).collect(Collectors.toList());

        List<DonHang> donHuy = danhSachDon.stream().filter(dh ->
                dh.getTrangThaiDonHang() != null &&
                        "DA_HUY".equalsIgnoreCase(dh.getTrangThaiDonHang().getMaTrangThai())
        ).collect(Collectors.toList());

        // 4. Tính toán các chỉ số KPI
        BigDecimal tongDoanhThu = donThanhCong.stream()
                .map(d -> d.getTongThanhToan() != null ? d.getTongThanhToan() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal doanhThuOnline = donThanhCong.stream()
                .filter(d -> !"TAI_QUAY".equalsIgnoreCase(d.getLoaiDonHang()))
                .map(d -> d.getTongThanhToan() != null ? d.getTongThanhToan() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal doanhThuPos = donThanhCong.stream()
                .filter(d -> "TAI_QUAY".equalsIgnoreCase(d.getLoaiDonHang()))
                .map(d -> d.getTongThanhToan() != null ? d.getTongThanhToan() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tongTienHangChuaGiam = donThanhCong.stream()
                .map(d -> d.getTongTienHang() != null ? d.getTongTienHang() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tongTienGiamVoucher = donThanhCong.stream()
                .map(d -> d.getTienGiamGia() != null ? d.getTienGiamGia() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tongTienGiamDiem = donThanhCong.stream()
                .map(d -> d.getTienCoolcashDung() != null ? d.getTienCoolcashDung() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tongPhiVanChuyen = donThanhCong.stream()
                .map(d -> d.getPhiVanChuyen() != null ? d.getPhiVanChuyen() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long tongDonHang = danhSachDon.size();
        long soDonHoanThanh = donThanhCong.size();
        long soDonHuy = donHuy.size();

        double tyLeHoanThanh = (tongDonHang > 0)
                ? BigDecimal.valueOf((soDonHoanThanh * 100.0) / tongDonHang).setScale(1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        BigDecimal giaTriTrungBinhDon = (soDonHoanThanh > 0)
                ? tongDoanhThu.divide(BigDecimal.valueOf(soDonHoanThanh), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 5. Tính tỷ lệ tăng trưởng so với kỳ trước liền kề
        long soNgayTrongKy = ChronoUnit.DAYS.between(tuNgay, denNgay) + 1;
        LocalDate tuNgayTruoc = tuNgay.minusDays(soNgayTrongKy);
        LocalDate denNgayTruoc = tuNgay.minusDays(1);
        BigDecimal doanhThuKyTruoc = donHangRepository.tinhDoanhThuTrongKhoang(
                tuNgayTruoc.atStartOfDay(), denNgayTruoc.atTime(LocalTime.MAX)
        );
        if (doanhThuKyTruoc == null) doanhThuKyTruoc = BigDecimal.ZERO;

        double tyLeTangTruong = 0.0;
        if (doanhThuKyTruoc.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal chenhLech = tongDoanhThu.subtract(doanhThuKyTruoc);
            tyLeTangTruong = chenhLech.multiply(BigDecimal.valueOf(100))
                    .divide(doanhThuKyTruoc, 1, RoundingMode.HALF_UP).doubleValue();
        } else if (tongDoanhThu.compareTo(BigDecimal.ZERO) > 0) {
            tyLeTangTruong = 100.0;
        }

        // 6. Phân tích chi tiết theo từng ngày (Dữ liệu Biểu đồ + Bảng kê)
        List<String> nhanThoiGian = new ArrayList<>();
        List<BigDecimal> duLieuDoanhThu = new ArrayList<>();
        List<Long> duLieuSoDon = new ArrayList<>();
        List<BigDecimal> duLieuDoanhThuOnline = new ArrayList<>();
        List<BigDecimal> duLieuDoanhThuPos = new ArrayList<>();
        List<ChiTietNgayDoanhThuDto> chiTietCacNgay = new ArrayList<>();

        Map<LocalDate, List<DonHang>> donTheoNgayMap = danhSachDon.stream()
                .collect(Collectors.groupingBy(d -> d.getNgayTao().toLocalDate()));

        for (LocalDate loop = tuNgay; !loop.isAfter(denNgay); loop = loop.plusDays(1)) {
            nhanThoiGian.add(loop.format(FMT_NGAY_LABEL));

            List<DonHang> donTrongNgay = donTheoNgayMap.getOrDefault(loop, Collections.emptyList());
            long countDon = donTrongNgay.size();

            List<DonHang> donTcNgay = donTrongNgay.stream().filter(dh ->
                    dh.getTrangThaiDonHang() != null &&
                            ("HOAN_THANH".equalsIgnoreCase(dh.getTrangThaiDonHang().getMaTrangThai()) ||
                             "DA_DOI_HANG".equalsIgnoreCase(dh.getTrangThaiDonHang().getMaTrangThai()))
            ).collect(Collectors.toList());

            long countTc = donTcNgay.size();
            long countHuy = donTrongNgay.stream().filter(dh ->
                    dh.getTrangThaiDonHang() != null &&
                            "DA_HUY".equalsIgnoreCase(dh.getTrangThaiDonHang().getMaTrangThai())
            ).count();

            BigDecimal revOnline = donTcNgay.stream()
                    .filter(d -> !"TAI_QUAY".equalsIgnoreCase(d.getLoaiDonHang()))
                    .map(d -> d.getTongThanhToan() != null ? d.getTongThanhToan() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal revPos = donTcNgay.stream()
                    .filter(d -> "TAI_QUAY".equalsIgnoreCase(d.getLoaiDonHang()))
                    .map(d -> d.getTongThanhToan() != null ? d.getTongThanhToan() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal revTotal = revOnline.add(revPos);

            BigDecimal discountNgay = donTcNgay.stream()
                    .map(d -> {
                        BigDecimal v = d.getTienGiamGia() != null ? d.getTienGiamGia() : BigDecimal.ZERO;
                        BigDecimal p = d.getTienCoolcashDung() != null ? d.getTienCoolcashDung() : BigDecimal.ZERO;
                        return v.add(p);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            duLieuDoanhThu.add(revTotal);
            duLieuSoDon.add(countDon);
            duLieuDoanhThuOnline.add(revOnline);
            duLieuDoanhThuPos.add(revPos);

            chiTietCacNgay.add(ChiTietNgayDoanhThuDto.builder()
                    .ngay(loop.format(FMT_NGAY_TABLE))
                    .soDon(countDon)
                    .soDonHoanThanh(countTc)
                    .soDonHuy(countHuy)
                    .doanhThuOnline(revOnline)
                    .doanhThuPos(revPos)
                    .tongDoanhThu(revTotal)
                    .tienGiamGia(discountNgay)
                    .build());
        }

        // Đảo ngược danh sách bảng kê để ngày mới nhất nằm trên cùng
        Collections.reverse(chiTietCacNgay);

        // 7. Thống kê tỷ trọng theo phương thức thanh toán
        List<String> nhanPhuongThuc = new ArrayList<>();
        List<BigDecimal> duLieuPhuongThuc = new ArrayList<>();
        try {
            List<Object[]> ptttData = donHangRepository.thongKeTheoPhuongThucThanhToan(batDau, ketThuc);
            for (Object[] row : ptttData) {
                if (row != null && row.length >= 3) {
                    String tenPt = (String) row[0];
                    BigDecimal revPt = (BigDecimal) row[2];
                    nhanPhuongThuc.add(tenPt != null ? tenPt : "Khác");
                    duLieuPhuongThuc.add(revPt != null ? revPt : BigDecimal.ZERO);
                }
            }
        } catch (Exception ex) {
            log.warn("Lỗi thống kê PTTT: {}", ex.getMessage());
        }

        // 8. Top 5 sản phẩm mang lại doanh thu cao nhất
        List<TopSanPhamDoanhThuDto> topSanPham = new ArrayList<>();
        try {
            List<Object[]> topData = chiTietDonHangRepository.findTopSanPhamTheoDoanhThu(batDau, ketThuc, PageRequest.of(0, 5));
            for (Object[] row : topData) {
                if (row != null && row.length >= 3) {
                    String tenSp = (String) row[0];
                    Long sl = (Long) row[1];
                    BigDecimal dt = (BigDecimal) row[2];
                    double tyLe = (tongDoanhThu.compareTo(BigDecimal.ZERO) > 0 && dt != null)
                            ? dt.multiply(BigDecimal.valueOf(100)).divide(tongDoanhThu, 1, RoundingMode.HALF_UP).doubleValue()
                            : 0.0;

                    topSanPham.add(TopSanPhamDoanhThuDto.builder()
                            .tenSanPham(tenSp)
                            .soLuongDaBan(sl)
                            .doanhThu(dt != null ? dt : BigDecimal.ZERO)
                            .tyLeDongGop(tyLe)
                            .build());
                }
            }
        } catch (Exception ex) {
            log.warn("Lỗi thống kê Top sản phẩm: {}", ex.getMessage());
        }

        return BaoCaoDoanhThuResponse.builder()
                .kyThongKe(kyChuan)
                .tuNgay(tuNgay)
                .denNgay(denNgay)
                .kenhBan(kenhChuan)
                .tongDoanhThu(tongDoanhThu)
                .tongDoanhThuTruocDo(doanhThuKyTruoc)
                .tyLeTangTruongDoanhThu(tyLeTangTruong)
                .tongDonHang(tongDonHang)
                .soDonHoanThanh(soDonHoanThanh)
                .soDonHuy(soDonHuy)
                .tyLeHoanThanh(tyLeHoanThanh)
                .giaTriTrungBinhDon(giaTriTrungBinhDon)
                .doanhThuOnline(doanhThuOnline)
                .doanhThuPos(doanhThuPos)
                .tongTienHangChuaGiam(tongTienHangChuaGiam)
                .tongTienGiamVoucher(tongTienGiamVoucher)
                .tongTienGiamDiem(tongTienGiamDiem)
                .tongPhiVanChuyen(tongPhiVanChuyen)
                .nhanThoiGian(nhanThoiGian)
                .duLieuDoanhThu(duLieuDoanhThu)
                .duLieuSoDon(duLieuSoDon)
                .duLieuDoanhThuOnline(duLieuDoanhThuOnline)
                .duLieuDoanhThuPos(duLieuDoanhThuPos)
                .nhanPhuongThuc(nhanPhuongThuc)
                .duLieuPhuongThuc(duLieuPhuongThuc)
                .topSanPham(topSanPham)
                .chiTietCacNgay(chiTietCacNgay)
                .build();
    }
}
