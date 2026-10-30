package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.DonHangKhachHangDto;
import vn.fpoly.simplain.dto.HuyDonHangRequest;
import vn.fpoly.simplain.dto.ThongTinKhachHangResponse;
import vn.fpoly.simplain.service.DonHangService;
import vn.fpoly.simplain.service.KhachHangService;
import vn.fpoly.simplain.service.LoyaltyHoiVienService;
import vn.fpoly.simplain.service.SanPhamYeuThichService;

import java.util.Map;

/**
 * Controller phục vụ Khách hàng theo dõi Lịch sử đơn hàng cá nhân (US23 - Mod 5 - Hưng).
 * - Tiêu chí 1: Liệt kê danh sách đơn hàng cá nhân (tabs lọc Shopee, tìm kiếm, phân trang).
 * - Tiêu chí 2: Xem chi tiết trạng thái đơn hàng, mã vận đơn, hành trình vận chuyển.
 * - Form Validation: Form hủy đơn hàng chuyên nghiệp (chọn lý do, kiểm tra trạng thái hợp lệ, chống IDOR).
 */
@Slf4j
@Controller
@RequestMapping("/khach-hang/don-hang")
@RequiredArgsConstructor
public class DonHangKhachHangController {

    private final DonHangService donHangService;
    private final KhachHangService khachHangService;
    private final LoyaltyHoiVienService loyaltyHoiVienService;
    private final SanPhamYeuThichService sanPhamYeuThichService;

    /**
     * Màn hình danh sách đơn hàng cá nhân của khách hàng (US23 - Tiêu chí 1).
     */
    @GetMapping
    public String hienThiDanhSachDonHang(
            @RequestParam(name = "trangThai", defaultValue = "TAT_CA") String trangThai,
            @RequestParam(name = "q", required = false) String tuKhoa,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập để xem lịch sử đơn hàng của bạn");
            return "redirect:/khach-hang/dang-nhap?redirect=/khach-hang/don-hang";
        }

        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);

            if (page < 0) page = 0;
            if (size <= 0) size = 10;

            Page<DonHangKhachHangDto> pageResult = donHangService.layDanhSachDonHangCuaKhachHang(
                    thongTin.getId(),
                    thongTin.getSoDienThoai(),
                    trangThai,
                    tuKhoa,
                    PageRequest.of(page, size)
            );

            Map<String, Long> demTrangThai = donHangService.demSoLuongDonHangTheoCacTrangThai(
                    thongTin.getId(),
                    thongTin.getSoDienThoai()
            );

            model.addAttribute("thongTin", thongTin);
            model.addAttribute("danhSachDonHang", pageResult);
            model.addAttribute("demTrangThai", demTrangThai);
            model.addAttribute("trangThaiHienTai", trangThai);
            model.addAttribute("tuKhoaHienTai", tuKhoa != null ? tuKhoa : "");
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", pageResult.getTotalPages());
            model.addAttribute("totalElements", pageResult.getTotalElements());

            try {
                model.addAttribute("soLuongYeuThich", sanPhamYeuThichService.demSoLuongYeuThich(thongTin.getId()));
                model.addAttribute("hoiVien", loyaltyHoiVienService.layThongTinHoiVien(thongTin.getId()));
            } catch (Exception ignored) {
            }

            return "khach-hang/don-hang/danh-sach";

        } catch (Exception e) {
            log.warn("Lỗi xác thực xem lịch sử đơn hàng: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap?redirect=/khach-hang/don-hang";
        }
    }

    /**
     * Màn hình xem chi tiết trạng thái đơn hàng & mã vận đơn (US23 - Tiêu chí 2).
     */
    @GetMapping("/{maDonHang}")
    public String hienThiChiTietDonHang(
            @PathVariable("maDonHang") String maDonHang,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập để xem chi tiết đơn hàng");
            return "redirect:/khach-hang/dang-nhap?redirect=/khach-hang/don-hang/" + maDonHang;
        }

        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);

            DonHangKhachHangDto dto = donHangService.layChiTietDonHangCuaKhachHang(
                    maDonHang,
                    thongTin.getId(),
                    thongTin.getSoDienThoai()
            );

            model.addAttribute("thongTin", thongTin);
            model.addAttribute("donHangDto", dto);

            if (!model.containsAttribute("huyDonHangRequest")) {
                model.addAttribute("huyDonHangRequest", new HuyDonHangRequest());
            }

            try {
                model.addAttribute("soLuongYeuThich", sanPhamYeuThichService.demSoLuongYeuThich(thongTin.getId()));
                model.addAttribute("hoiVien", loyaltyHoiVienService.layThongTinHoiVien(thongTin.getId()));
            } catch (Exception ignored) {
            }

            return "khach-hang/don-hang/chi-tiet";

        } catch (IllegalArgumentException e) {
            log.warn("Lỗi truy cập chi tiết đơn hàng: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("loiTruyCap", e.getMessage());
            return "redirect:/khach-hang/don-hang";
        } catch (Exception e) {
            log.error("Lỗi phiên đăng nhập khi xem đơn hàng: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap";
        }
    }

    /**
     * Xử lý gửi biểu mẫu Hủy đơn hàng từ phía khách hàng (US23 - Form Validation).
     */
    @PostMapping("/{maDonHang}/huy")
    public String xuLyHuyDonHang(
            @PathVariable("maDonHang") String maDonHang,
            @Valid @ModelAttribute("huyDonHangRequest") HuyDonHangRequest req,
            BindingResult result,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập để thực hiện thao tác");
            return "redirect:/khach-hang/dang-nhap";
        }

        ThongTinKhachHangResponse thongTin;
        try {
            thongTin = khachHangService.layThongTinTuToken(token);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn");
            return "redirect:/khach-hang/dang-nhap";
        }

        // Validate tùy chỉnh cho lý do "Lý do khác"
        if ("Lý do khác".equalsIgnoreCase(req.getLyDoHuy())
                && (req.getGhiChuChiTiet() == null || req.getGhiChuChiTiet().isBlank())) {
            result.rejectValue("ghiChuChiTiet", "error.req", "Vui lòng nhập chi tiết lý do hủy đơn hàng!");
        }

        if (result.hasErrors()) {
            try {
                DonHangKhachHangDto dto = donHangService.layChiTietDonHangCuaKhachHang(
                        maDonHang,
                        thongTin.getId(),
                        thongTin.getSoDienThoai()
                );
                model.addAttribute("thongTin", thongTin);
                model.addAttribute("donHangDto", dto);
                model.addAttribute("soLuongYeuThich", sanPhamYeuThichService.demSoLuongYeuThich(thongTin.getId()));
                model.addAttribute("loiFormHuy", "Dữ liệu hủy đơn không hợp lệ, vui lòng kiểm tra lại!");
                return "khach-hang/don-hang/chi-tiet";
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("loiTruyCap", ex.getMessage());
                return "redirect:/khach-hang/don-hang";
            }
        }

        try {
            donHangService.khachHangHuyDonHang(maDonHang, thongTin.getId(), thongTin.getSoDienThoai(), req);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã hủy đơn hàng #" + maDonHang + " thành công!");
            return "redirect:/khach-hang/don-hang/" + maDonHang;
        } catch (Exception e) {
            log.warn("Lỗi khi hủy đơn hàng #{}: {}", maDonHang, e.getMessage());
            redirectAttributes.addFlashAttribute("loiHuyDon", e.getMessage());
            return "redirect:/khach-hang/don-hang/" + maDonHang;
        }
    }

    // =========================================================================
    // TIỆN ÍCH
    // =========================================================================

    private String layTokenTuCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (KhachHangController.COOKIE_TOKEN.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
