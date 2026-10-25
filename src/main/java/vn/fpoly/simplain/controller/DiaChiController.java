/**
 * Tác giả: Gia Minh
 * Chức năng: US13 - Controller phục vụ giao diện Web Thymeleaf quản lý Sổ địa chỉ khách hàng
 * Cập nhật xác nhận commit US13: 25/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.CapNhatDiaChiRequest;
import vn.fpoly.simplain.dto.DiaChiResponse;
import vn.fpoly.simplain.dto.ThemDiaChiRequest;
import vn.fpoly.simplain.service.DanhMucHanhChinhService;
import vn.fpoly.simplain.service.DiaChiService;

import java.util.List;

/**
 * Controller phục vụ các màn hình web quản lý sổ địa chỉ.
 */
@Controller
@RequestMapping("/khach-hang/dia-chi")
public class DiaChiController {

    private final DiaChiService diaChiService;
    private final DanhMucHanhChinhService danhMucHanhChinhService;

    public DiaChiController(DiaChiService diaChiService, DanhMucHanhChinhService danhMucHanhChinhService) {
        this.diaChiService = diaChiService;
        this.danhMucHanhChinhService = danhMucHanhChinhService;
    }

    /**
     * Màn hình danh sách địa chỉ của khách hàng.
     */
    @GetMapping
    public String hienThiDanhSach(@RequestParam(defaultValue = "1") Long idKhachHang, Model model) {
        try {
            List<DiaChiResponse> danhSachDiaChi = diaChiService.layDanhSachDiaChi(idKhachHang);
            model.addAttribute("danhSachDiaChi", danhSachDiaChi);
            model.addAttribute("idKhachHang", idKhachHang);
        } catch (Exception e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
        }
        return "khach-hang/dia-chi/danh-sach";
    }

    /**
     * Màn hình form thêm mới địa chỉ.
     */
    @GetMapping("/them")
    public String hienThiFormThem(@RequestParam(defaultValue = "1") Long idKhachHang, Model model) {
        model.addAttribute("idKhachHang", idKhachHang);
        if (!model.containsAttribute("diaChiRequest")) {
            model.addAttribute("diaChiRequest", new ThemDiaChiRequest("", "", "", "", "", "", false));
        }
        napDuLieuDropdown(model, null, null);
        model.addAttribute("laThemMoi", true);
        return "khach-hang/dia-chi/form";
    }

    /**
     * Xử lý submit form thêm mới địa chỉ.
     */
    @PostMapping("/them")
    public String xuLyThemMoi(
            @RequestParam Long idKhachHang,
            @Valid @ModelAttribute("diaChiRequest") ThemDiaChiRequest diaChiRequest,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("idKhachHang", idKhachHang);
            model.addAttribute("laThemMoi", true);
            napDuLieuDropdown(model, diaChiRequest.tinhThanh(), diaChiRequest.quanHuyen());
            return "khach-hang/dia-chi/form";
        }

        try {
            diaChiService.themDiaChi(idKhachHang, diaChiRequest);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Thêm địa chỉ nhận hàng mới thành công!");
            return "redirect:/khach-hang/dia-chi?idKhachHang=" + idKhachHang;
        } catch (Exception e) {
            model.addAttribute("idKhachHang", idKhachHang);
            model.addAttribute("laThemMoi", true);
            model.addAttribute("thongBaoLoi", e.getMessage());
            napDuLieuDropdown(model, diaChiRequest.tinhThanh(), diaChiRequest.quanHuyen());
            return "khach-hang/dia-chi/form";
        }
    }

    /**
     * Màn hình form cập nhật địa chỉ.
     */
    @GetMapping("/sua/{id}")
    public String hienThiFormSua(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Long idKhachHang,
            Model model) {
        try {
            DiaChiResponse diaChi = diaChiService.layChiTietDiaChi(idKhachHang, id);
            model.addAttribute("idKhachHang", idKhachHang);
            model.addAttribute("idDiaChi", id);
            if (!model.containsAttribute("diaChiRequest")) {
                CapNhatDiaChiRequest req = new CapNhatDiaChiRequest(
                        diaChi.tenNguoiNhan(),
                        diaChi.soDienThoai(),
                        diaChi.tinhThanh(),
                        diaChi.quanHuyen(),
                        diaChi.phuongXa(),
                        diaChi.diaChiChiTiet(),
                        diaChi.laMacDinh()
                );
                model.addAttribute("diaChiRequest", req);
            }
            napDuLieuDropdown(model, diaChi.tinhThanh(), diaChi.quanHuyen());
            model.addAttribute("laThemMoi", false);
            return "khach-hang/dia-chi/form";
        } catch (Exception e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/khach-hang/dia-chi?idKhachHang=" + idKhachHang;
        }
    }

    /**
     * Xử lý submit form cập nhật địa chỉ.
     */
    @PostMapping("/sua/{id}")
    public String xuLyCapNhat(
            @PathVariable Long id,
            @RequestParam Long idKhachHang,
            @Valid @ModelAttribute("diaChiRequest") CapNhatDiaChiRequest diaChiRequest,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("idKhachHang", idKhachHang);
            model.addAttribute("idDiaChi", id);
            model.addAttribute("laThemMoi", false);
            napDuLieuDropdown(model, diaChiRequest.tinhThanh(), diaChiRequest.quanHuyen());
            return "khach-hang/dia-chi/form";
        }

        try {
            diaChiService.capNhatDiaChi(idKhachHang, id, diaChiRequest);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Cập nhật địa chỉ nhận hàng thành công!");
            return "redirect:/khach-hang/dia-chi?idKhachHang=" + idKhachHang;
        } catch (Exception e) {
            model.addAttribute("idKhachHang", idKhachHang);
            model.addAttribute("idDiaChi", id);
            model.addAttribute("laThemMoi", false);
            model.addAttribute("thongBaoLoi", e.getMessage());
            napDuLieuDropdown(model, diaChiRequest.tinhThanh(), diaChiRequest.quanHuyen());
            return "khach-hang/dia-chi/form";
        }
    }

    /**
     * Xử lý xóa địa chỉ.
     */
    @PostMapping("/xoa/{id}")
    public String xuLyXoa(
            @PathVariable Long id,
            @RequestParam Long idKhachHang,
            RedirectAttributes redirectAttributes) {
        try {
            diaChiService.xoaDiaChi(idKhachHang, id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa địa chỉ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/khach-hang/dia-chi?idKhachHang=" + idKhachHang;
    }

    /**
     * Xử lý đặt địa chỉ làm mặc định.
     */
    @PostMapping("/mac-dinh/{id}")
    public String xuLyDatMacDinh(
            @PathVariable Long id,
            @RequestParam Long idKhachHang,
            RedirectAttributes redirectAttributes) {
        try {
            diaChiService.datLamMacDinh(idKhachHang, id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã đặt địa chỉ làm mặc định!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/khach-hang/dia-chi?idKhachHang=" + idKhachHang;
    }

    private void napDuLieuDropdown(Model model, String tinhThanh, String quanHuyen) {
        List<String> danhSachTinh = danhMucHanhChinhService.layDanhSachTinhThanh();
        model.addAttribute("danhSachTinh", danhSachTinh);

        if (tinhThanh != null && !tinhThanh.isBlank()) {
            model.addAttribute("danhSachHuyen", danhMucHanhChinhService.layDanhSachQuanHuyen(tinhThanh));
        } else {
            model.addAttribute("danhSachHuyen", List.of());
        }

        if (tinhThanh != null && quanHuyen != null && !quanHuyen.isBlank()) {
            model.addAttribute("danhSachXa", danhMucHanhChinhService.layDanhSachPhuongXa(tinhThanh, quanHuyen));
        } else {
            model.addAttribute("danhSachXa", List.of());
        }
    }
}
