package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.CapNhatSoLuongRequest;
import vn.fpoly.simplain.dto.GioHangResponse;
import vn.fpoly.simplain.dto.ThemComboVaoGioRequest;
import vn.fpoly.simplain.dto.ThemGioHangRequest;
import vn.fpoly.simplain.dto.ThongTinKhachHangResponse;
import vn.fpoly.simplain.entity.GioHang;
import vn.fpoly.simplain.entity.GoiCombo;
import vn.fpoly.simplain.service.GioHangService;
import vn.fpoly.simplain.service.GoiComboService;
import vn.fpoly.simplain.service.KhachHangService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller xử lý Giỏ hàng và Quản lý bó hàng Combo (US17).
 * - Tiêu chí 1: Thêm/Sửa/Xóa biến thể vào giỏ.
 * - Tiêu chí 2: Tính năng bó hàng Combo (Sinh nhom_combo_token).
 */
@Slf4j
@Controller
@RequestMapping("/gio-hang")
@RequiredArgsConstructor
public class GioHangController {

    public static final String COOKIE_TOKEN = "jwt_token";
    public static final String COOKIE_CART = "simplain_cart_id";

    private final GioHangService gioHangService;
    private final KhachHangService khachHangService;
    private final GoiComboService goiComboService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Tự động chuyển chuỗi rỗng "" thành null để tránh lỗi TypeMismatchException với kiểu số Long/Integer
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Hiển thị giao diện Giỏ hàng (US17).
     */
    @GetMapping
    public String hienThiGioHang(Model model, HttpServletRequest request, HttpServletResponse response) {
        Long khachHangId = layKhachHangIdTuCookie(request);
        Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

        GioHang gioHang = gioHangService.layHoacTaoGioHang(khachHangId, cartIdVangLai);
        luuCartIdVaoCookie(response, gioHang.getId());

        GioHangResponse gioHangResponse = gioHangService.layThongTinGioHang(khachHangId, gioHang.getId());
        model.addAttribute("gioHang", gioHangResponse);

        // Nạp danh sách các gói Combo kích hoạt để hiển thị trong Giỏ hàng (US28)
        if (goiComboService != null) {
            try {
                org.springframework.data.domain.Page<GoiCombo> pageCombo = goiComboService.layDanhSachGoiCombo(
                        null, true, org.springframework.data.domain.PageRequest.of(0, 6));
                model.addAttribute("danhSachComboGoiY", pageCombo.getContent());
            } catch (Exception ignored) {
                model.addAttribute("danhSachComboGoiY", java.util.Collections.emptyList());
            }
        }

        // Thông tin khách hàng (nếu đã đăng nhập)
        if (khachHangId != null) {
            try {
                String token = layTokenTuCookie(request);
                ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);
                model.addAttribute("khachHang", thongTin);
            } catch (Exception ignored) {
            }
        }

        return "khach-hang/gio-hang";
    }

    /**
     * API Lấy thông tin chi tiết một Gói Combo (danh sách sản phẩm & biến thể) để phục vụ modal chọn nhanh
     */
    @GetMapping("/api/combo/{comboId}")
    @ResponseBody
    public ResponseEntity<?> layChiTietComboApi(@PathVariable("comboId") Long comboId) {
        try {
            GoiCombo combo = goiComboService.layTheoId(comboId);
            return ResponseEntity.ok(combo);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * Thêm biến thể lẻ vào giỏ hàng qua JSON (US17 - Tiêu chí 1).
     */
    @PostMapping(value = "/them", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> themBienTheVaoGioJson(
            @RequestBody ThemGioHangRequest req,
            HttpServletRequest request,
            HttpServletResponse response) {
        return thucHienThemBienThe(req, request, response);
    }

    /**
     * Thêm biến thể lẻ vào giỏ hàng qua Form submit thông thường.
     */
    @PostMapping("/them")
    @ResponseBody
    public ResponseEntity<?> themBienTheVaoGioForm(
            @ModelAttribute ThemGioHangRequest req,
            HttpServletRequest request,
            HttpServletResponse response) {
        return thucHienThemBienThe(req, request, response);
    }

    private ResponseEntity<?> thucHienThemBienThe(
            ThemGioHangRequest req,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            Long khachHangId = layKhachHangIdTuCookie(request);
            Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

            GioHang gioHang = gioHangService.layHoacTaoGioHang(khachHangId, cartIdVangLai);
            luuCartIdVaoCookie(response, gioHang.getId());

            gioHangService.themBienTheVaoGio(khachHangId, gioHang.getId(), req);
            int tongSoLuong = gioHangService.demTongSoLuong(khachHangId, gioHang.getId());

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Đã thêm sản phẩm vào giỏ hàng!");
            res.put("tongSoLuong", tongSoLuong);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Lỗi khi thêm biến thể vào giỏ: {}", e.getMessage());
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * Bó hàng Combo vào giỏ qua JSON: Sinh nhom_combo_token duy nhất (US17 - Tiêu chí 2).
     */
    @PostMapping(value = "/them-combo", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> themComboVaoGioJson(
            @RequestBody ThemComboVaoGioRequest req,
            HttpServletRequest request,
            HttpServletResponse response) {
        return thucHienThemCombo(req, request, response);
    }

    /**
     * Bó hàng Combo vào giỏ qua Form submit.
     */
    @PostMapping("/them-combo")
    @ResponseBody
    public ResponseEntity<?> themComboVaoGioForm(
            @ModelAttribute ThemComboVaoGioRequest req,
            HttpServletRequest request,
            HttpServletResponse response) {
        return thucHienThemCombo(req, request, response);
    }

    private ResponseEntity<?> thucHienThemCombo(
            ThemComboVaoGioRequest req,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            Long khachHangId = layKhachHangIdTuCookie(request);
            Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

            GioHang gioHang = gioHangService.layHoacTaoGioHang(khachHangId, cartIdVangLai);
            luuCartIdVaoCookie(response, gioHang.getId());

            String token = gioHangService.themComboVaoGio(khachHangId, gioHang.getId(), req);
            int tongSoLuong = gioHangService.demTongSoLuong(khachHangId, gioHang.getId());

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Đã thêm gói Combo vào giỏ hàng thành công!");
            res.put("nhomComboToken", token);
            res.put("tongSoLuong", tongSoLuong);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Lỗi khi thêm combo vào giỏ: {}", e.getMessage());
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * Cập nhật số lượng của biến thể lẻ hoặc bó combo qua JSON (AJAX Fetch).
     */
    @PostMapping(value = "/cap-nhat", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> capNhatSoLuongJson(
            @RequestBody CapNhatSoLuongRequest req,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            Long khachHangId = layKhachHangIdTuCookie(request);
            Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

            GioHang gioHang = gioHangService.layHoacTaoGioHang(khachHangId, cartIdVangLai);
            luuCartIdVaoCookie(response, gioHang.getId());

            gioHangService.capNhatSoLuong(khachHangId, gioHang.getId(), req);
            GioHangResponse gioHangRes = gioHangService.layThongTinGioHang(khachHangId, gioHang.getId());

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Cập nhật số lượng thành công!");
            res.put("gioHang", gioHangRes);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật số lượng qua JSON: {}", e.getMessage());
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * Cập nhật số lượng của biến thể lẻ hoặc bó combo qua Form Submit (x-www-form-urlencoded).
     */
    @PostMapping("/cap-nhat")
    public Object capNhatSoLuongForm(
            @ModelAttribute CapNhatSoLuongRequest req,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        boolean isAjax = laYeuCauAjax(request);

        try {
            Long khachHangId = layKhachHangIdTuCookie(request);
            Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

            GioHang gioHang = gioHangService.layHoacTaoGioHang(khachHangId, cartIdVangLai);
            luuCartIdVaoCookie(response, gioHang.getId());

            gioHangService.capNhatSoLuong(khachHangId, gioHang.getId(), req);
            GioHangResponse gioHangRes = gioHangService.layThongTinGioHang(khachHangId, gioHang.getId());

            if (isAjax) {
                Map<String, Object> res = new HashMap<>();
                res.put("success", true);
                res.put("message", "Cập nhật số lượng thành công!");
                res.put("gioHang", gioHangRes);
                return ResponseEntity.ok(res);
            }

            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Cập nhật số lượng thành công!");
            return "redirect:/gio-hang";
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật số lượng qua Form: {}", e.getMessage());
            if (isAjax) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(err);
            }
            redirectAttributes.addFlashAttribute("loiGioHang", e.getMessage());
            return "redirect:/gio-hang";
        }
    }

    /**
     * Xóa 1 mục lẻ hoặc bó combo (US17 - Tiêu chí 1).
     */
    @PostMapping("/xoa/{id}")
    public Object xoaChiTiet(
            @PathVariable("id") Long id,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        boolean isAjax = laYeuCauAjax(request);
        try {
            Long khachHangId = layKhachHangIdTuCookie(request);
            Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

            GioHang gioHang = gioHangService.layHoacTaoGioHang(khachHangId, cartIdVangLai);
            luuCartIdVaoCookie(response, gioHang.getId());

            gioHangService.xoaChiTiet(khachHangId, gioHang.getId(), id);
            GioHangResponse gioHangRes = gioHangService.layThongTinGioHang(khachHangId, gioHang.getId());

            if (isAjax) {
                Map<String, Object> res = new HashMap<>();
                res.put("success", true);
                res.put("message", "Đã xóa mục khỏi giỏ hàng!");
                res.put("gioHang", gioHangRes);
                return ResponseEntity.ok(res);
            }

            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa mục khỏi giỏ hàng!");
            return "redirect:/gio-hang";
        } catch (Exception e) {
            log.error("Lỗi khi xóa mục khỏi giỏ: {}", e.getMessage());
            if (isAjax) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(err);
            }
            redirectAttributes.addFlashAttribute("loiGioHang", e.getMessage());
            return "redirect:/gio-hang";
        }
    }

    /**
     * Xóa trọn vẹn Bó Combo theo token (US17 - Tiêu chí 2).
     */
    @PostMapping("/xoa-combo/{token}")
    public Object xoaCombo(
            @PathVariable("token") String token,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        boolean isAjax = laYeuCauAjax(request);
        try {
            Long khachHangId = layKhachHangIdTuCookie(request);
            Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

            GioHang gioHang = gioHangService.layHoacTaoGioHang(khachHangId, cartIdVangLai);
            luuCartIdVaoCookie(response, gioHang.getId());

            gioHangService.xoaCombo(khachHangId, gioHang.getId(), token);
            GioHangResponse gioHangRes = gioHangService.layThongTinGioHang(khachHangId, gioHang.getId());

            if (isAjax) {
                Map<String, Object> res = new HashMap<>();
                res.put("success", true);
                res.put("message", "Đã xóa bó Combo khỏi giỏ hàng!");
                res.put("gioHang", gioHangRes);
                return ResponseEntity.ok(res);
            }

            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa bó Combo khỏi giỏ hàng!");
            return "redirect:/gio-hang";
        } catch (Exception e) {
            log.error("Lỗi khi xóa combo khỏi giỏ: {}", e.getMessage());
            if (isAjax) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(err);
            }
            redirectAttributes.addFlashAttribute("loiGioHang", e.getMessage());
            return "redirect:/gio-hang";
        }
    }

    /**
     * Xóa sạch toàn bộ giỏ hàng (US17 - Tiêu chí 1).
     */
    @PostMapping("/xoa-het")
    public Object xoaHetGioHang(
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        boolean isAjax = laYeuCauAjax(request);
        try {
            Long khachHangId = layKhachHangIdTuCookie(request);
            Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

            GioHang gioHang = gioHangService.layHoacTaoGioHang(khachHangId, cartIdVangLai);
            luuCartIdVaoCookie(response, gioHang.getId());

            gioHangService.xoaHetGioHang(khachHangId, gioHang.getId());

            if (isAjax) {
                Map<String, Object> res = new HashMap<>();
                res.put("success", true);
                res.put("message", "Đã xóa sạch giỏ hàng!");
                return ResponseEntity.ok(res);
            }

            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã làm trống giỏ hàng!");
            return "redirect:/gio-hang";
        } catch (Exception e) {
            log.error("Lỗi khi xóa hết giỏ hàng: {}", e.getMessage());
            if (isAjax) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(err);
            }
            redirectAttributes.addFlashAttribute("loiGioHang", e.getMessage());
            return "redirect:/gio-hang";
        }
    }

    /**
     * API lấy số lượng mặt hàng trong giỏ (phục vụ hiển thị badge giỏ hàng trên Header).
     */
    @GetMapping("/api/dem-so-luong")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> laySoLuongGioHang(HttpServletRequest request) {
        Long khachHangId = layKhachHangIdTuCookie(request);
        Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

        int count = gioHangService.demTongSoLuong(khachHangId, cartIdVangLai);
        Map<String, Object> res = new HashMap<>();
        res.put("tongSoLuong", count);
        return ResponseEntity.ok(res);
    }

    /**
     * API trả về toàn bộ dữ liệu giỏ hàng dạng JSON.
     */
    @GetMapping("/api/chi-tiet")
    @ResponseBody
    public ResponseEntity<GioHangResponse> layChiTietGioHangApi(HttpServletRequest request) {
        Long khachHangId = layKhachHangIdTuCookie(request);
        Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

        GioHangResponse res = gioHangService.layThongTinGioHang(khachHangId, cartIdVangLai);
        return ResponseEntity.ok(res);
    }

    // ==========================================
    // CÁC HÀM TIỆN ÍCH HỖ TRỢ XÁC THỰC VÀ COOKIE
    // ==========================================

    private Long layKhachHangIdTuCookie(HttpServletRequest request) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            return null;
        }
        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);
            return thongTin != null ? thongTin.getId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Long layCartIdVangLaiTuCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (COOKIE_CART.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                try {
                    return Long.parseLong(cookie.getValue().trim());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return null;
    }

    private void luuCartIdVaoCookie(HttpServletResponse response, Long cartId) {
        if (cartId == null || response == null) return;
        Cookie cookie = new Cookie(COOKIE_CART, String.valueOf(cartId));
        cookie.setPath("/");
        cookie.setMaxAge(30 * 86400); // 30 ngày
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
    }

    private String layTokenTuCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (COOKIE_TOKEN.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private boolean laYeuCauAjax(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        String contentType = request.getHeader("Content-Type");
        return "XMLHttpRequest".equalsIgnoreCase(requestedWith)
                || (accept != null && accept.contains("application/json"))
                || (contentType != null && contentType.contains("application/json"));
    }
}
