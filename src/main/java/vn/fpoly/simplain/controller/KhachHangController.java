package vn.fpoly.simplain.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.CapNhatHoSoRequest;
import vn.fpoly.simplain.dto.DoiMatKhauRequest;
import vn.fpoly.simplain.dto.HoSoKhachHangResponse;
import vn.fpoly.simplain.service.KhachHangService;

/**
 * Controller phục vụ giao diện Web Thymeleaf cho chức năng Hồ sơ khách hàng (US12).
 */
@Controller
@RequestMapping("/khach-hang")
public class KhachHangController {

    private final KhachHangService khachHangService;

    public KhachHangController(KhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    /**
     * Hiển thị trang hồ sơ cá nhân và form cập nhật thông tin.
     */
    @GetMapping("/ho-so")
    public String hienThiHoSo(@RequestParam(defaultValue = "1") Long id, Model model) {
        try {
            HoSoKhachHangResponse hoSo = khachHangService.layHoSo(id);
            model.addAttribute("hoSo", hoSo);
            model.addAttribute("idKhachHang", id);
            if (!model.containsAttribute("capNhatRequest")) {
                CapNhatHoSoRequest req = new CapNhatHoSoRequest(
                        hoSo.hoTen(),
                        hoSo.soDienThoai(),
                        hoSo.email(),
                        hoSo.gioiTinh(),
                        hoSo.ngaySinh(),
                        hoSo.anhDaiDien()
                );
                model.addAttribute("capNhatRequest", req);
            }
        } catch (Exception e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
        }
        return "khach-hang/ho-so";
    }

    /**
     * Xử lý gửi form cập nhật hồ sơ cá nhân từ giao diện web.
     */
    @PostMapping("/cap-nhat")
    public String xuLyCapNhat(
            @RequestParam Long id,
            @Valid @ModelAttribute("capNhatRequest") CapNhatHoSoRequest capNhatRequest,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            try {
                HoSoKhachHangResponse hoSo = khachHangService.layHoSo(id);
                model.addAttribute("hoSo", hoSo);
            } catch (Exception ignored) {
            }
            model.addAttribute("idKhachHang", id);
            return "khach-hang/ho-so";
        }

        try {
            khachHangService.capNhatHoSo(id, capNhatRequest);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Cập nhật thông tin cá nhân thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }

        return "redirect:/khach-hang/ho-so?id=" + id;
    }

    /**
     * Hiển thị trang đổi mật khẩu tài khoản.
     */
    @GetMapping("/doi-mat-khau")
    public String hienThiDoiMatKhau(@RequestParam(defaultValue = "1") Long id, Model model) {
        model.addAttribute("idKhachHang", id);
        if (!model.containsAttribute("doiMatKhauRequest")) {
            model.addAttribute("doiMatKhauRequest", new DoiMatKhauRequest("", "", ""));
        }
        return "khach-hang/doi-mat-khau";
    }

    /**
     * Xử lý gửi form đổi mật khẩu từ giao diện web.
     */
    @PostMapping("/doi-mat-khau")
    public String xuLyDoiMatKhau(
            @RequestParam Long id,
            @Valid @ModelAttribute("doiMatKhauRequest") DoiMatKhauRequest doiMatKhauRequest,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("idKhachHang", id);
            return "khach-hang/doi-mat-khau";
        }

        try {
            khachHangService.doiMatKhau(id, doiMatKhauRequest);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đổi mật khẩu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }

        return "redirect:/khach-hang/doi-mat-khau?id=" + id;
    }
}
