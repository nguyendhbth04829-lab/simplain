package vn.fpoly.simplain.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.entity.FormDang;
import vn.fpoly.simplain.service.FormDangService;

import java.util.List;

/**
 * Controller MVC Quản lý Form dáng [form_dang] trong CMS Admin.
 * Hỗ trợ đầy đủ CRUD, Tìm kiếm từ khóa, Lọc trạng thái kích hoạt và Phân trang chuẩn SIMPLAIN.
 */
@Controller
@RequestMapping("/admin/form-dang")
public class FormDangController {

    private final FormDangService formDangService;

    @Autowired
    public FormDangController(FormDangService formDangService) {
        this.formDangService = formDangService;
    }

    @GetMapping({"", "/"})
    public String chuyenHuongMacDinh() {
        return "redirect:/admin/form-dang/hien-thi";
    }

    /**
     * Hiển thị danh sách form dáng có phân trang, tìm kiếm và lọc trạng thái.
     */
    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @RequestParam(defaultValue = "0") Integer page,
                          @RequestParam(required = false) String tuKhoa,
                          @RequestParam(required = false) Boolean kichHoat) {
        if (page == null || page < 0) {
            page = 0;
        }
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<FormDang> pageData = formDangService.layDanhSachPhanTrang(tuKhoa, kichHoat, pageable);

        model.addAttribute("listFormDang", pageData.getContent());
        model.addAttribute("pageData", pageData);
        if (!model.containsAttribute("formDang")) {
            model.addAttribute("formDang", new FormDang());
        }

        model.addAttribute("page", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("kichHoatLoc", kichHoat);
        model.addAttribute("tongFormDang", formDangService.demTongSo());

        return "admin/form-dang/hien-thi";
    }

    /**
     * Xử lý thêm mới form dáng.
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("formDang") FormDang formDang,
                      BindingResult result,
                      Model model,
                      RedirectAttributes redirectAttributes,
                      @RequestParam(defaultValue = "0") Integer page) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu form dáng chưa hợp lệ, vui lòng kiểm tra lại!");
            return hienThi(model, page, null, null);
        }
        try {
            FormDang saved = formDangService.them(formDang);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Thêm mới form dáng [" + saved.getTenFormDang() + "] thành công!");
            return "redirect:/admin/form-dang/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return hienThi(model, page, null, null);
        }
    }

    /**
     * Xử lý cập nhật thông tin form dáng.
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("formDang") FormDang formDang,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         @RequestParam(defaultValue = "0") Integer page) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu cập nhật form dáng chưa hợp lệ!");
            return hienThi(model, page, null, null);
        }
        try {
            FormDang updated = formDangService.sua(formDang.getId(), formDang);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật form dáng [" + updated.getTenFormDang() + "] thành công!");
            return "redirect:/admin/form-dang/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return hienThi(model, page, null, null);
        }
    }

    /**
     * Đổi nhanh trạng thái (Đang sử dụng / Ngưng sử dụng).
     */
    @GetMapping("/doi-trang-thai")
    public String doiTrangThai(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            FormDang fd = formDangService.doiTrangThai(id);
            String trangThai = Boolean.TRUE.equals(fd.getKichHoat()) ? "ĐANG SỬ DỤNG" : "NGƯNG SỬ DỤNG";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái form dáng [" + fd.getTenFormDang() + "] sang: " + trangThai);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/form-dang/hien-thi";
    }

    /**
     * Xóa form dáng (có kiểm tra ràng buộc không cho xóa nếu có sản phẩm liên kết).
     */
    @GetMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            formDangService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Xóa form dáng thành công!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/form-dang/hien-thi";
    }

    /**
     * Thao tác hàng loạt trên các form dáng đã chọn (Kích hoạt / Ngưng sử dụng hàng loạt).
     */
    @PostMapping("/thao-tac-hang-loat")
    public String thaoTacHangLoat(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            @RequestParam(name = "hanhDong") String hanhDong,
            RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng chọn ít nhất một form dáng để thực hiện!");
            return "redirect:/admin/form-dang/hien-thi";
        }

        int count = 0;
        boolean targetStatus = "KICH_HOAT".equalsIgnoreCase(hanhDong);
        for (Long id : ids) {
            try {
                FormDang fd = formDangService.layTheoId(id);
                if (fd.getKichHoat() == null || fd.getKichHoat() != targetStatus) {
                    formDangService.capNhatTrangThai(id, targetStatus);
                    count++;
                }
            } catch (Exception ignored) {
            }
        }

        String actionText = targetStatus ? "kích hoạt" : "ngưng sử dụng";
        redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                "Đã " + actionText + " thành công " + count + " form dáng đã chọn.");
        return "redirect:/admin/form-dang/hien-thi";
    }
}
