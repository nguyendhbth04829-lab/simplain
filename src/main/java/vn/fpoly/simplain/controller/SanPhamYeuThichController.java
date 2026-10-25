/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - Controller phục vụ giao diện Web Thymeleaf quản lý Danh sách sản phẩm yêu thích (Wishlist)
 */
package vn.fpoly.simplain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.SanPhamYeuThichResponse;
import vn.fpoly.simplain.service.SanPhamYeuThichService;

import java.util.List;

/**
 * Controller phục vụ màn hình web danh sách sản phẩm yêu thích của khách hàng.
 */
@Controller
@RequestMapping("/khach-hang/yeu-thich")
public class SanPhamYeuThichController {

    private final SanPhamYeuThichService sanPhamYeuThichService;

    public SanPhamYeuThichController(SanPhamYeuThichService sanPhamYeuThichService) {
        this.sanPhamYeuThichService = sanPhamYeuThichService;
    }

    /**
     * Màn hình danh sách sản phẩm yêu thích của khách hàng.
     */
    @GetMapping
    public String hienThiDanhSach(@RequestParam(defaultValue = "1") Long idKhachHang, Model model) {
        try {
            List<SanPhamYeuThichResponse> danhSachYeuThich = sanPhamYeuThichService.layDanhSachYeuThich(idKhachHang);
            model.addAttribute("danhSachYeuThich", danhSachYeuThich);
            model.addAttribute("idKhachHang", idKhachHang);
            model.addAttribute("soLuong", danhSachYeuThich.size());
        } catch (Exception e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            model.addAttribute("danhSachYeuThich", List.of());
            model.addAttribute("idKhachHang", idKhachHang);
            model.addAttribute("soLuong", 0);
        }
        return "khach-hang/yeu-thich/danh-sach";
    }

    /**
     * Thêm sản phẩm vào danh sách yêu thích từ giao diện web.
     */
    @PostMapping("/them")
    public String xuLyThemYeuThich(
            @RequestParam Long idKhachHang,
            @RequestParam Long maSanPhamId,
            RedirectAttributes redirectAttributes) {
        try {
            sanPhamYeuThichService.themVaoYeuThich(idKhachHang, maSanPhamId);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã thêm sản phẩm vào danh sách yêu thích!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/khach-hang/yeu-thich?idKhachHang=" + idKhachHang;
    }

    /**
     * Xóa sản phẩm khỏi danh sách yêu thích từ giao diện web.
     */
    @PostMapping("/xoa")
    public String xuLyXoaYeuThich(
            @RequestParam Long idKhachHang,
            @RequestParam Long maSanPhamId,
            RedirectAttributes redirectAttributes) {
        try {
            sanPhamYeuThichService.xoaKhoiYeuThich(idKhachHang, maSanPhamId);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa sản phẩm khỏi danh sách yêu thích!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/khach-hang/yeu-thich?idKhachHang=" + idKhachHang;
    }
}
