package vn.fpoly.simplain.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.fpoly.simplain.entity.PhuongThucThanhToan;
import vn.fpoly.simplain.service.PhuongThucThanhToanService;

/**
 * Quản lý cấu hình cổng thanh toán cho Admin (US18).
 * Giao diện Thymeleaf để test tay, phong cách Coolmate tối giản.
 */
@Controller
@RequestMapping("/admin/phuong-thuc-thanh-toan")
public class PhuongThucThanhToanController {

    private final PhuongThucThanhToanService phuongThucThanhToanService;

    public PhuongThucThanhToanController(PhuongThucThanhToanService phuongThucThanhToanService) {
        this.phuongThucThanhToanService = phuongThucThanhToanService;
    }

    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @RequestParam(defaultValue = "0") Integer page,
                          @RequestParam(required = false) String ten,
                          @RequestParam(required = false) Boolean kichHoat,
                          @RequestParam(required = false) Boolean trucTuyen) {
        Page<PhuongThucThanhToan> trang = phuongThucThanhToanService.layTrang(page, ten, kichHoat, trucTuyen);
        model.addAttribute("listPhuongThuc", trang.getContent());
        if (!model.containsAttribute("phuongThuc")) {
            model.addAttribute("phuongThuc", new PhuongThucThanhToan());
        }
        model.addAttribute("page", page);
        model.addAttribute("totalPages", trang.getTotalPages());
        model.addAttribute("ten", ten);
        model.addAttribute("kichHoat", kichHoat);
        model.addAttribute("trucTuyen", trucTuyen);
        return "admin/phuong-thuc-thanh-toan/hien-thi";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("phuongThuc") PhuongThucThanhToan phuongThuc,
                      BindingResult result, Model model,
                      @RequestParam(defaultValue = "0") Integer page) {
        if (!result.hasErrors()) {
            String loi = phuongThucThanhToanService.luu(phuongThuc);
            if (loi != null) {
                result.rejectValue("maPhuongThuc", "trungMa", loi);
            }
        }
        if (result.hasErrors()) {
            Page<PhuongThucThanhToan> trang = phuongThucThanhToanService.layTrang(page, null);
            model.addAttribute("listPhuongThuc", trang.getContent());
            model.addAttribute("page", page);
            model.addAttribute("totalPages", trang.getTotalPages());
            return "admin/phuong-thuc-thanh-toan/hien-thi";
        }
        return "redirect:/admin/phuong-thuc-thanh-toan/hien-thi";
    }

    @GetMapping("/view")
    public String view(Model model, @RequestParam Long id) {
        model.addAttribute("phuongThuc", phuongThucThanhToanService.timTheoId(id));
        return "admin/phuong-thuc-thanh-toan/view";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("phuongThuc") PhuongThucThanhToan phuongThuc,
                         BindingResult result) {
        if (!result.hasErrors()) {
            String loi = phuongThucThanhToanService.luu(phuongThuc);
            if (loi != null) {
                result.rejectValue("maPhuongThuc", "trungMa", loi);
            }
        }
        if (result.hasErrors()) {
            return "admin/phuong-thuc-thanh-toan/view";
        }
        return "redirect:/admin/phuong-thuc-thanh-toan/hien-thi";
    }

    /**
     * Bật/tắt nhanh cổng thanh toán (tiêu chí chính US18).
     * Xóa mềm bằng cách gạt tắt, không có nút xóa riêng.
     */
    @GetMapping("/doi-trang-thai")
    public String doiTrangThai(@RequestParam Long id) {
        phuongThucThanhToanService.doiTrangThai(id);
        return "redirect:/admin/phuong-thuc-thanh-toan/hien-thi";
    }
}
