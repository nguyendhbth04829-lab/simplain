/**
 * Tác giả: Gia Minh
 * Chức năng: US15 - Controller phục vụ giao diện Web Thymeleaf Đánh giá sản phẩm
 */
package vn.fpoly.simplain.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.DanhGiaResponse;
import vn.fpoly.simplain.dto.TaoDanhGiaRequest;
import vn.fpoly.simplain.service.DanhGiaSanPhamService;

import java.util.List;

/**
 * Controller phục vụ các màn hình web quản lý đánh giá sản phẩm của khách hàng.
 */
@Controller
@RequestMapping("/khach-hang/danh-gia")
public class DanhGiaSanPhamController {

    private final DanhGiaSanPhamService danhGiaSanPhamService;

    public DanhGiaSanPhamController(DanhGiaSanPhamService danhGiaSanPhamService) {
        this.danhGiaSanPhamService = danhGiaSanPhamService;
    }

    /**
     * Màn hình danh sách các đánh giá của khách hàng.
     */
    @GetMapping
    public String hienThiDanhSach(@RequestParam(defaultValue = "1") Long idKhachHang, Model model) {
        try {
            List<DanhGiaResponse> danhSach = danhGiaSanPhamService.layDanhSachTheoKhachHang(idKhachHang);
            model.addAttribute("danhSachDanhGia", danhSach);
            model.addAttribute("idKhachHang", idKhachHang);
            model.addAttribute("soLuong", danhSach.size());
        } catch (Exception e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            model.addAttribute("danhSachDanhGia", List.of());
            model.addAttribute("idKhachHang", idKhachHang);
            model.addAttribute("soLuong", 0);
        }
        return "khach-hang/danh-gia/danh-sach";
    }

    /**
     * Màn hình form viết đánh giá cho sản phẩm đã mua.
     */
    @GetMapping("/viet")
    public String hienThiFormViet(
            @RequestParam(defaultValue = "1") Long idKhachHang,
            @RequestParam(defaultValue = "1") Long maSanPhamId,
            @RequestParam(defaultValue = "1") Long maChiTietDonHangId,
            Model model) {
        model.addAttribute("idKhachHang", idKhachHang);
        if (!model.containsAttribute("danhGiaRequest")) {
            model.addAttribute("danhGiaRequest", new TaoDanhGiaRequest(maSanPhamId, maChiTietDonHangId, 5, "", List.of()));
        }
        return "khach-hang/danh-gia/form";
    }

    /**
     * Submit form viết đánh giá sản phẩm.
     */
    @PostMapping("/viet")
    public String xuLyGuiDanhGia(
            @RequestParam Long idKhachHang,
            @Valid @ModelAttribute("danhGiaRequest") TaoDanhGiaRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("idKhachHang", idKhachHang);
            return "khach-hang/danh-gia/form";
        }

        try {
            danhGiaSanPhamService.taoDanhGia(idKhachHang, request);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Cảm ơn bạn đã gửi đánh giá cho sản phẩm!");
            return "redirect:/khach-hang/danh-gia?idKhachHang=" + idKhachHang;
        } catch (Exception e) {
            model.addAttribute("idKhachHang", idKhachHang);
            model.addAttribute("thongBaoLoi", e.getMessage());
            return "khach-hang/danh-gia/form";
        }
    }
}
