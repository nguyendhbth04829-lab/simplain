package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.ThongTinKhachHangResponse;
import vn.fpoly.simplain.dto.YeuThichToggleResponse;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.service.KhachHangService;
import vn.fpoly.simplain.service.SanPhamYeuThichService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller phục vụ chức năng Quản lý Sản phẩm Yêu thích (Wishlist - US14).
 * Hỗ trợ:
 * 1. Thao tác nhấn tim toggle qua Ajax hoặc link trực tiếp (Tiêu chí 1).
 * 2. Hiển thị trang Danh sách yêu thích với giao diện chuẩn Simplain (Tiêu chí 2).
 */
@Controller
@RequestMapping("/khach-hang/yeu-thich")
public class SanPhamYeuThichController {

    private final SanPhamYeuThichService sanPhamYeuThichService;
    private final KhachHangService khachHangService;

    @Autowired
    public SanPhamYeuThichController(SanPhamYeuThichService sanPhamYeuThichService,
                                     KhachHangService khachHangService) {
        this.sanPhamYeuThichService = sanPhamYeuThichService;
        this.khachHangService = khachHangService;
    }

    /**
     * Hiển thị trang Danh sách sản phẩm yêu thích (US14 - Tiêu chí 2).
     */
    @GetMapping({"", "/"})
    public String hienThiDanhSachYeuThich(HttpServletRequest request,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập để xem danh sách sản phẩm yêu thích!");
            return "redirect:/khach-hang/dang-nhap";
        }

        ThongTinKhachHangResponse thongTin;
        try {
            thongTin = khachHangService.layThongTinTuToken(token);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap";
        }

        List<SanPham> danhSachSanPham = sanPhamYeuThichService.layDanhSachSanPhamYeuThich(thongTin.getId());
        long tongSoYeuThich = sanPhamYeuThichService.demSoLuongYeuThich(thongTin.getId());

        model.addAttribute("khachHangDangNhap", thongTin);
        model.addAttribute("thongTin", thongTin);
        model.addAttribute("danhSachSanPham", danhSachSanPham);
        model.addAttribute("tongSoYeuThich", tongSoYeuThich);

        return "khach-hang/yeu-thich";
    }

    /**
     * API Ajax Toggle Trạng thái yêu thích sản phẩm khi click icon trái tim (US14 - Tiêu chí 1).
     */
    @PostMapping("/toggle/{id}")
    @ResponseBody
    public ResponseEntity<YeuThichToggleResponse> toggleYeuThich(@PathVariable("id") Long sanPhamId,
                                                                 HttpServletRequest request) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(YeuThichToggleResponse.builder()
                            .thanhCong(false)
                            .chuaDangNhap(true)
                            .thongBao("Vui lòng đăng nhập để thêm sản phẩm vào danh sách yêu thích!")
                            .build());
        }

        ThongTinKhachHangResponse thongTin;
        try {
            thongTin = khachHangService.layThongTinTuToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(YeuThichToggleResponse.builder()
                            .thanhCong(false)
                            .chuaDangNhap(true)
                            .thongBao("Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại!")
                            .build());
        }

        try {
            YeuThichToggleResponse res = sanPhamYeuThichService.toggleYeuThich(thongTin.getId(), sanPhamId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(YeuThichToggleResponse.builder()
                            .thanhCong(false)
                            .thongBao(e.getMessage())
                            .build());
        }
    }

    /**
     * Xóa sản phẩm khỏi danh sách yêu thích (US14 - Tiêu chí 1 & 2).
     */
    @GetMapping("/xoa/{id}")
    public String xoaKhoiYeuThich(@PathVariable("id") Long sanPhamId,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap";
        }

        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);
            sanPhamYeuThichService.xoaKhoiYeuThich(thongTin.getId(), sanPhamId);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa sản phẩm khỏi danh sách yêu thích!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiThaoTac", e.getMessage());
        }

        return "redirect:/khach-hang/yeu-thich";
    }

    /**
     * API lấy số lượng sản phẩm yêu thích của khách hàng để cập nhật Badge Header.
     */
    @GetMapping("/so-luong")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> laySoLuongYeuThich(HttpServletRequest request) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            return ResponseEntity.ok(Map.of("soLuong", 0, "chuaDangNhap", true));
        }
        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);
            long soLuong = sanPhamYeuThichService.demSoLuongYeuThich(thongTin.getId());
            return ResponseEntity.ok(Map.of("soLuong", soLuong, "chuaDangNhap", false));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("soLuong", 0, "chuaDangNhap", true));
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
