package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.controller.GioHangController;
import vn.fpoly.simplain.dto.CheckoutTinhTienResponse;
import vn.fpoly.simplain.dto.DatHangRequest;
import vn.fpoly.simplain.dto.GioHangResponse;
import vn.fpoly.simplain.dto.ThongTinKhachHangResponse;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.GioHang;
import vn.fpoly.simplain.service.DonHangService;
import vn.fpoly.simplain.service.GioHangService;
import vn.fpoly.simplain.service.KhachHangService;
import vn.fpoly.simplain.service.PhuongThucThanhToanService;
import vn.fpoly.simplain.service.CheckoutOrderService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/thanh-toan")
@RequiredArgsConstructor
public class CheckoutController {

    private final DonHangService donHangService;
    private final GioHangService gioHangService;
    private final KhachHangService khachHangService;
    private final PhuongThucThanhToanService phuongThucThanhToanService;
    private final CheckoutOrderService checkoutOrderService;

    @GetMapping
    public String hienThi(HttpServletRequest request, HttpServletResponse response, Model model,
                          RedirectAttributes redirectAttributes) {
        Long khachHangId = layKhachHangId(request);
        GioHang gioHang = gioHangService.layHoacTaoGioHang(khachHangId, layCartId(request));
        luuCartId(response, gioHang.getId());

        GioHangResponse thongTinGioHang = gioHangService.layThongTinGioHang(khachHangId, gioHang.getId());
        if (thongTinGioHang == null || Boolean.TRUE.equals(thongTinGioHang.getGioHangRong())) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Giỏ hàng đang trống.");
            return "redirect:/gio-hang";
        }

        CheckoutTinhTienResponse chiPhi = donHangService.tinhToanChiPhi(
                thongTinGioHang.getTongTienHang(), null, BigDecimal.ZERO, khachHangId);
        model.addAttribute("gioHang", thongTinGioHang);
        model.addAttribute("chiPhi", chiPhi);
        model.addAttribute("dsPhuongThuc", phuongThucThanhToanService.layDanhSachDangKichHoat());
        model.addAttribute("datHangRequest", new DatHangRequest());
        model.addAttribute("daDangNhap", khachHangId != null);
        return "khach-hang/checkout";
    }

    @PostMapping("/dat-hang")
    public String datHang(@Valid @ModelAttribute("datHangRequest") DatHangRequest request,
                          BindingResult result, HttpServletRequest httpRequest,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng kiểm tra thông tin nhận hàng.");
            return "redirect:/thanh-toan";
        }

        try {
            // US19 chỉ tính tiền hàng và phí vận chuyển; không áp dụng voucher/điểm từ các US khác.
            request.setMaVoucher(null);
            request.setSoDiemCoolcash(BigDecimal.ZERO);
            DonHang donHang = checkoutOrderService.taoDonHang(
                    layKhachHangId(httpRequest), layCartId(httpRequest), request);
            return "redirect:/thanh-toan/thanh-cong/" + donHang.getMaDonHang();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/thanh-toan";
        }
    }

    @GetMapping("/thanh-cong/{maDonHang}")
    public String hienThiDonHang(@PathVariable("maDonHang") String maDonHang, Model model) {
        DonHang donHang = donHangService.layTheoMaDonHang(maDonHang);
        model.addAttribute("donHang", donHang);
        model.addAttribute("danhSachChiTiet", donHangService.layChiTietTheoDonHangId(donHang.getId()));
        return "khach-hang/order-success";
    }

    private Long layKhachHangId(HttpServletRequest request) {
        String token = layCookie(request, GioHangController.COOKIE_TOKEN);
        if (token == null) return null;
        try {
            ThongTinKhachHangResponse khachHang = khachHangService.layThongTinTuToken(token);
            return khachHang != null ? khachHang.getId() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private Long layCartId(HttpServletRequest request) {
        String cartId = layCookie(request, GioHangController.COOKIE_CART);
        if (cartId == null) return null;
        try {
            return Long.valueOf(cartId);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String layCookie(HttpServletRequest request, String tenCookie) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (tenCookie.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void luuCartId(HttpServletResponse response, Long cartId) {
        Cookie cookie = new Cookie(GioHangController.COOKIE_CART, String.valueOf(cartId));
        cookie.setPath("/");
        cookie.setMaxAge(30 * 86400);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
