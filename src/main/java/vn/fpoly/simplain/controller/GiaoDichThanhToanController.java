package vn.fpoly.simplain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.fpoly.simplain.entity.GiaoDichThanhToan;
import vn.fpoly.simplain.entity.PhuongThucThanhToan;
import vn.fpoly.simplain.service.GiaoDichThanhToanService;
import vn.fpoly.simplain.service.PhuongThucThanhToanService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller Quản lý Lịch sử Giao dịch thanh toán cho Admin (US20 - Tiêu chí 3).
 * Liên kết đồng bộ với Cổng thanh toán (US18) và Checkout Đơn hàng (US19).
 */
@Slf4j
@Controller
@RequestMapping("/admin/giao-dich-thanh-toan")
@RequiredArgsConstructor
public class GiaoDichThanhToanController {

    private final GiaoDichThanhToanService giaoDichThanhToanService;
    private final PhuongThucThanhToanService phuongThucThanhToanService;

    /**
     * Màn hình danh sách lịch sử giao dịch thanh toán (US20).
     */
    @GetMapping("/hien-thi")
    public String hienThiDanhSach(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "trangThai", required = false) String trangThai,
            @RequestParam(name = "maPhuongThucId", required = false) Long maPhuongThucId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<GiaoDichThanhToan> trangGiaoDich = giaoDichThanhToanService.layDanhSachPhanTrang(
                tuKhoa, trangThai, maPhuongThucId, pageable
        );

        List<PhuongThucThanhToan> dsPhuongThuc = phuongThucThanhToanService.layTatCa();

        // Thống kê nhanh
        long tongSoGiaoDich = trangGiaoDich.getTotalElements();
        long soThanhCong = trangGiaoDich.getContent().stream()
                .filter(g -> "THANH_CONG".equals(g.getTrangThai())).count();
        BigDecimal tongTienThanhCong = trangGiaoDich.getContent().stream()
                .filter(g -> "THANH_CONG".equals(g.getTrangThai()) && g.getSoTien() != null)
                .map(GiaoDichThanhToan::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("trangGiaoDich", trangGiaoDich);
        model.addAttribute("dsPhuongThuc", dsPhuongThuc);
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("trangThai", trangThai != null ? trangThai : "");
        model.addAttribute("maPhuongThucId", maPhuongThucId);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("tongSo", tongSoGiaoDich);
        model.addAttribute("soThanhCong", soThanhCong);
        model.addAttribute("tongTienThanhCong", tongTienThanhCong);

        return "admin/giao-dich-thanh-toan/hien-thi";
    }
}
