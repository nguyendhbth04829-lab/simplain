package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.HoiVienResponse;
import vn.fpoly.simplain.dto.ThongTinKhachHangResponse;
import vn.fpoly.simplain.entity.HoSoHoiVien;
import vn.fpoly.simplain.service.KhachHangService;
import vn.fpoly.simplain.service.LoyaltyHoiVienService;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller phục vụ quản lý Hội viên, Tích điểm Coolcash và Thăng hạng tự động (US16).
 * Tiêu chí 1: Đơn hàng hoàn thành cộng % điểm (so_du_coolcash).
 * Tiêu chí 2: Đạt mốc tự động lên Hạng (hang_thanh_vien).
 */
@Controller
public class HoiVienController {

    private final LoyaltyHoiVienService loyaltyHoiVienService;
    private final KhachHangService khachHangService;

    @Autowired
    public HoiVienController(LoyaltyHoiVienService loyaltyHoiVienService,
                             KhachHangService khachHangService) {
        this.loyaltyHoiVienService = loyaltyHoiVienService;
        this.khachHangService = khachHangService;
    }

    /**
     * Màn hình Quản lý Hội viên & Thẻ tích điểm Coolcash của Khách hàng (US16).
     */
    @GetMapping("/khach-hang/hoi-vien")
    public String hienThiTrangHoiVien(HttpServletRequest request,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập để xem thông tin Hội viên Coolcash!");
            return "redirect:/khach-hang/dang-nhap";
        }

        try {
            ThongTinKhachHangResponse kh = khachHangService.layThongTinTuToken(token);
            HoiVienResponse hoiVien = loyaltyHoiVienService.layThongTinHoiVien(kh.getId());

            model.addAttribute("thongTin", kh);
            model.addAttribute("hoiVien", hoiVien);
            model.addAttribute("tabActive", "hoi-vien");
            return "khach-hang/hoi-vien";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap";
        }
    }

    /**
     * API JSON lấy thông tin Hội viên & Thẻ Coolcash của khách hàng đang đăng nhập.
     */
    @GetMapping("/khach-hang/hoi-vien/api/thong-tin")
    @ResponseBody
    public ResponseEntity<?> layThongTinHoiVienApi(HttpServletRequest request) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("loi", "Chưa đăng nhập"));
        }

        try {
            ThongTinKhachHangResponse kh = khachHangService.layThongTinTuToken(token);
            HoiVienResponse res = loyaltyHoiVienService.layThongTinHoiVien(kh.getId());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("loi", e.getMessage()));
        }
    }

    /**
     * API Hệ thống / Nhân viên: Hoàn thành đơn hàng và tự động tích điểm Coolcash + Thăng hạng (US16).
     */
    @PostMapping("/api/don-hang/{id}/hoan-thanh-va-tich-diem")
    @ResponseBody
    public ResponseEntity<?> hoanThanhDonHangVaTichDiem(@PathVariable("id") Long donHangId) {
        try {
            HoSoHoiVien hoSo = loyaltyHoiVienService.hoanThanhDonHangVaTichDiem(donHangId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("thanhCong", true);
            resp.put("thongBao", "Đã cập nhật đơn hàng sang HOÀN THÀNH và tự động tích điểm Coolcash + xét thăng hạng thành công!");
            if (hoSo != null) {
                resp.put("maKhachHangId", hoSo.getMaKhachHangId());
                resp.put("soDuCoolcashMoi", hoSo.getSoDuCoolcash());
                resp.put("tongChiTieuNamMoi", hoSo.getTongChiTieuNam());
                resp.put("maHangHienTaiId", hoSo.getMaHangHienTaiId());
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("thanhCong", false, "loi", e.getMessage()));
        }
    }

    private String layTokenTuCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (KhachHangController.COOKIE_TOKEN.equals(cookie.getName())
                    && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
