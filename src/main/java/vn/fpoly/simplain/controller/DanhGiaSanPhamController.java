package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.DanhGiaRequest;
import vn.fpoly.simplain.dto.ThongTinKhachHangResponse;
import vn.fpoly.simplain.entity.ChiTietDonHang;
import vn.fpoly.simplain.entity.DanhGiaSanPham;
import vn.fpoly.simplain.service.DanhGiaSanPhamService;
import vn.fpoly.simplain.service.KhachHangService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller tiếp nhận và xử lý Đánh giá sản phẩm đã mua từ khách hàng (US15).
 * 1. Check điều kiện ma_chi_tiet_don_hang_id đã hoàn thành.
 * 2. Upload JSON ảnh.
 * 3. Rating 1-5 sao.
 */
@Controller
@RequestMapping("/khach-hang/danh-gia")
public class DanhGiaSanPhamController {

    private final DanhGiaSanPhamService danhGiaSanPhamService;
    private final KhachHangService khachHangService;

    @Autowired
    public DanhGiaSanPhamController(DanhGiaSanPhamService danhGiaSanPhamService,
                                   KhachHangService khachHangService) {
        this.danhGiaSanPhamService = danhGiaSanPhamService;
        this.khachHangService = khachHangService;
    }

    /**
     * Tiếp nhận gửi Đánh giá sản phẩm từ khách hàng (US15 - Đầy đủ 3 tiêu chí).
     */
    @PostMapping("/gui")
    public String guiDanhGia(@Valid @ModelAttribute("danhGiaRequest") DanhGiaRequest req,
                             BindingResult result,
                             @RequestParam(value = "anhFiles", required = false) MultipartFile[] anhFiles,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập để gửi đánh giá sản phẩm!");
            return "redirect:/khach-hang/dang-nhap";
        }

        ThongTinKhachHangResponse thongTin;
        try {
            thongTin = khachHangService.layThongTinTuToken(token);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap";
        }

        String slug = req.getDuongDanSlug();
        String redirectTarget = (slug != null && !slug.isBlank())
                ? "redirect:/san-pham/" + slug + "#khungDanhGia"
                : "redirect:/cua-hang/san-pham/" + req.getMaSanPhamId() + "#khungDanhGia";

        if (result.hasErrors()) {
            String loiValidation = result.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("loiDanhGia", loiValidation);
            return redirectTarget;
        }

        try {
            danhGiaSanPhamService.themDanhGia(thongTin.getId(), req, anhFiles);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Cảm ơn bạn đã gửi đánh giá sản phẩm! Đánh giá của bạn giúp ích rất nhiều cho cộng đồng.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDanhGia", e.getMessage());
        }

        return redirectTarget;
    }

    /**
     * API Ajax kiểm tra khách hàng có đủ điều kiện đánh giá sản phẩm hay không (US15 - Tiêu chí 1).
     */
    @GetMapping("/kiem-tra-dieu-kien/{sanPhamId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> kiemTraDieuKien(@PathVariable("sanPhamId") Long sanPhamId,
                                                               HttpServletRequest request) {
        Map<String, Object> res = new HashMap<>();
        String token = layTokenTuCookie(request);
        if (token == null) {
            res.put("duDieuKien", false);
            res.put("coQuyen", false);
            res.put("chuaDangNhap", true);
            res.put("canDangNhap", true);
            res.put("thongBao", "Vui lòng đăng nhập để đánh giá sản phẩm!");
            return ResponseEntity.ok(res);
        }

        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);
            List<ChiTietDonHang> dsDuDieuKien = danhGiaSanPhamService.layDanhSachChiTietDonHangDuDieuKien(thongTin.getId(), sanPhamId);

            if (dsDuDieuKien.isEmpty()) {
                res.put("duDieuKien", false);
                res.put("coQuyen", false);
                res.put("chuaDangNhap", false);
                res.put("canDangNhap", false);
                res.put("thongBao", "Bạn chỉ có thể đánh giá sản phẩm sau khi đã mua và đơn hàng được giao thành công (HOAN_THANH)!");
                res.put("danhSachChiTiet", Collections.emptyList());
            } else {
                res.put("duDieuKien", true);
                res.put("coQuyen", true);
                res.put("chuaDangNhap", false);
                res.put("canDangNhap", false);
                res.put("thongBao", "Đủ điều kiện đánh giá");
                
                // Trích xuất các chi tiết đơn hàng
                List<Map<String, Object>> dtos = dsDuDieuKien.stream().map(ct -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", ct.getId());
                    m.put("maDonHangId", ct.getMaDonHangId());
                    m.put("tenSanPham", ct.getTenSanPham());
                    m.put("maSku", ct.getMaSku());
                    m.put("donGia", ct.getDonGia());
                    m.put("soLuong", ct.getSoLuong());
                    return m;
                }).toList();
                res.put("danhSachChiTiet", dtos);
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("duDieuKien", false);
            res.put("chuaDangNhap", true);
            res.put("thongBao", "Phiên đăng nhập không hợp lệ!");
            return ResponseEntity.ok(res);
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
