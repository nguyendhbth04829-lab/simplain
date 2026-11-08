package vn.fpoly.simplain.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.fpoly.simplain.dto.BaoCaoDoanhThuResponse;
import vn.fpoly.simplain.dto.ChiTietNgayDoanhThuDto;
import vn.fpoly.simplain.service.ThongKeDoanhThuService;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * Controller MVC quản lý Báo cáo Thống kê Doanh thu chuyên sâu cho SIMPLAIN CMS.
 * Cung cấp giao diện trực quan, tinh gọn, dễ theo dõi cho chủ shop và kế toán.
 */
@Slf4j
@Controller
@RequestMapping("/admin/thong-ke-doanh-thu")
@RequiredArgsConstructor
public class ThongKeDoanhThuController {

    private final ThongKeDoanhThuService thongKeDoanhThuService;

    /**
     * Màn hình Báo cáo Thống kê Doanh thu được hợp nhất hoàn toàn vào Dashboard CMS.
     * Tự động điều hướng và đồng bộ các tham số bộ lọc sang /admin/dashboard.
     */
    @GetMapping({"", "/", "/index"})
    public String hienThiBaoCao(
            @RequestParam(name = "kyThongKe", required = false, defaultValue = "7_NGAY") String kyThongKe,
            @RequestParam(name = "tuNgay", required = false) String tuNgay,
            @RequestParam(name = "denNgay", required = false) String denNgay,
            @RequestParam(name = "kenhBan", required = false, defaultValue = "ALL") String kenhBan) {

        StringBuilder url = new StringBuilder("redirect:/admin/dashboard?kyThongKe=").append(kyThongKe);
        if (tuNgay != null && !tuNgay.isBlank()) {
            url.append("&tuNgay=").append(tuNgay.trim());
        }
        if (denNgay != null && !denNgay.isBlank()) {
            url.append("&denNgay=").append(denNgay.trim());
        }
        if (kenhBan != null && !kenhBan.isBlank()) {
            url.append("&kenhBan=").append(kenhBan.trim());
        }
        return url.toString();
    }

    /**
     * Xuất dữ liệu bảng kê doanh thu ra định dạng CSV (Hỗ trợ mở trực tiếp trên Microsoft Excel tiếng Việt).
     */
    @GetMapping("/xuat-csv")
    public void xuatCsv(
            @RequestParam(name = "kyThongKe", required = false, defaultValue = "30_NGAY") String kyThongKe,
            @RequestParam(name = "tuNgay", required = false) String tuNgay,
            @RequestParam(name = "denNgay", required = false) String denNgay,
            @RequestParam(name = "kenhBan", required = false, defaultValue = "ALL") String kenhBan,
            HttpServletResponse response) throws IOException {

        BaoCaoDoanhThuResponse baoCao = thongKeDoanhThuService.taoBaoCaoDoanhThu(kyThongKe, tuNgay, denNgay, kenhBan);

        String fileName = "Bao-Cao-Doanh-Thu-SIMPLAIN-" + baoCao.getTuNgay() + "_den_" + baoCao.getDenNgay() + ".csv";
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Ghi UTF-8 BOM để Excel tự nhận diện đúng bảng mã tiếng Việt
        response.getOutputStream().write(0xEF);
        response.getOutputStream().write(0xBB);
        response.getOutputStream().write(0xBF);

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));

        writer.println("BÁO CÁO DOANH THU HỆ THỐNG SIMPLAIN MENSWEAR");
        writer.println("Khoảng thời gian," + baoCao.getTuNgay() + " đến " + baoCao.getDenNgay());
        writer.println("Kênh bán hàng," + ("ONLINE".equals(baoCao.getKenhBan()) ? "Online Website" : ("POS".equals(baoCao.getKenhBan()) ? "Tại quầy POS" : "Toàn bộ các kênh")));
        writer.println("Tổng doanh thu thực nhận (VNĐ)," + baoCao.getTongDoanhThu());
        writer.println("Tổng số đơn hàng," + baoCao.getTongDonHang());
        writer.println("Số đơn hoàn thành," + baoCao.getSoDonHoanThanh());
        writer.println("Số đơn hủy," + baoCao.getSoDonHuy());
        writer.println("Giá trị đơn trung bình (AOV)," + baoCao.getGiaTriTrungBinhDon());
        writer.println();

        writer.println("Ngày,Số đơn phát sinh,Số đơn hoàn thành,Số đơn hủy,Doanh thu Online (VNĐ),Doanh thu POS (VNĐ),Tổng doanh thu ngày (VNĐ),Tiền giảm giá (Voucher+Điểm)");

        for (ChiTietNgayDoanhThuDto ct : baoCao.getChiTietCacNgay()) {
            writer.printf("\"%s\",%d,%d,%d,%s,%s,%s,%s\n",
                    ct.getNgay(),
                    ct.getSoDon(),
                    ct.getSoDonHoanThanh(),
                    ct.getSoDonHuy(),
                    ct.getDoanhThuOnline(),
                    ct.getDoanhThuPos(),
                    ct.getTongDoanhThu(),
                    ct.getTienGiamGia()
            );
        }

        writer.flush();
    }
}
