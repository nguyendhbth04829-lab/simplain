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
import java.util.List;
import vn.fpoly.simplain.entity.KichThuoc;
import vn.fpoly.simplain.repository.KichThuocRepository;
import vn.fpoly.simplain.repository.MauSacRepository;
import vn.fpoly.simplain.service.KichThuocService;

/**
 * Controller MVC Quản lý Kích thước sản phẩm [kich_thuoc] (S, M, L, XL, 2XL...) trên giao diện Web (US07).
 */
@Controller
@RequestMapping("/admin/kich-thuoc")
public class KichThuocController {

    private final KichThuocService kichThuocService;
    private final KichThuocRepository kichThuocRepository;
    private final MauSacRepository mauSacRepository;

    @Autowired
    public KichThuocController(KichThuocService kichThuocService,
                               KichThuocRepository kichThuocRepository,
                               MauSacRepository mauSacRepository) {
        this.kichThuocService = kichThuocService;
        this.kichThuocRepository = kichThuocRepository;
        this.mauSacRepository = mauSacRepository;
    }

    /**
     * Hiển thị danh sách kích thước có phân trang và tìm kiếm.
     */
    @GetMapping({"", "/", "/hien-thi"})
    public String hienThi(Model model,
                          @RequestParam(defaultValue = "0") Integer page,
                          @RequestParam(required = false) String tuKhoa) {
        if (page == null || page < 0) {
            page = 0;
        }
        int size = 6;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<KichThuoc> kichThuocPage = kichThuocService.layDanhSachPhanTrang(tuKhoa, pageable);

        model.addAttribute("listKichThuoc", kichThuocPage.getContent());
        model.addAttribute("pageData", kichThuocPage);
        if (!model.containsAttribute("kichThuoc")) {
            model.addAttribute("kichThuoc", new KichThuoc());
        }
        model.addAttribute("page", page);
        model.addAttribute("totalPages", kichThuocPage.getTotalPages());
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("tongKichThuoc", kichThuocRepository.count());
        model.addAttribute("tongMauSac", mauSacRepository.count());

        return "admin/kich-thuoc/hien-thi";
    }

    /**
     * Xử lý thêm mới kích thước sản phẩm (S, M, L, XL...).
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("kichThuoc") KichThuoc kichThuoc,
                      BindingResult result,
                      Model model,
                      RedirectAttributes redirectAttributes,
                      @RequestParam(defaultValue = "0") Integer page) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu kích thước chưa hợp lệ, vui lòng kiểm tra lại mã size và tên kích thước!");
            return hienThi(model, page, null);
        }

        try {
            KichThuoc saved = kichThuocService.them(kichThuoc);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Thêm mới kích thước [" + saved.getMaKichThuoc() + " - " + saved.getTenKichThuoc() + "] thành công!");
            return "redirect:/admin/kich-thuoc/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return hienThi(model, page, null);
        }
    }

    /**
     * Hiển thị màn hình chi tiết và cập nhật kích thước.
     */
    @GetMapping("/view")
    public String view(Model model,
                       @RequestParam Long id,
                       RedirectAttributes redirectAttributes) {
        try {
            KichThuoc kt = kichThuocService.layTheoId(id);
            model.addAttribute("kichThuoc", kt);
            return "admin/kich-thuoc/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/kich-thuoc/hien-thi";
        }
    }

    /**
     * Xử lý cập nhật thông tin kích thước.
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("kichThuoc") KichThuoc kichThuoc,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu cập nhật kích thước chưa hợp lệ, vui lòng kiểm tra lại!");
            return "admin/kich-thuoc/view";
        }

        try {
            KichThuoc updated = kichThuocService.sua(kichThuoc.getId(), kichThuoc);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật kích thước [" + updated.getMaKichThuoc() + " - " + updated.getTenKichThuoc() + "] thành công!");
            return "redirect:/admin/kich-thuoc/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return "admin/kich-thuoc/view";
        }
    }

    /**
     * Xử lý xóa kích thước (Chặn xóa nếu đang có biến thể sản phẩm sử dụng).
     */
    @GetMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            kichThuocService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Xóa kích thước thành công!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/kich-thuoc/hien-thi";
    }

    /**
     * Chuyển đổi trạng thái hoạt động (Còn hoạt động / Tạm ngưng) của kích thước.
     */
    @GetMapping("/doi-trang-thai")
    public String doiTrangThai(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            KichThuoc kt = kichThuocService.doiTrangThai(id);
            String trangThaiText = Boolean.TRUE.equals(kt.getKichHoat()) ? "CÒN HOẠT ĐỘNG" : "TẠM NGƯNG";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái kích thước [" + kt.getMaKichThuoc() + "] sang: " + trangThaiText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/kich-thuoc/hien-thi";
    }

    /**
     * Thao tác hàng loạt trên danh sách ID kích thước được chọn (Còn hoạt động hoặc Tạm ngưng).
     */
    @PostMapping("/thao-tac-hang-loat")
    public String thaoTacHangLoat(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "hanhDong") String hanhDong,
                                  RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng tích chọn ít nhất một kích thước để thao tác hàng loạt!");
            return "redirect:/admin/kich-thuoc/hien-thi";
        }

        boolean batHoatDong = "KICH_HOAT".equalsIgnoreCase(hanhDong);
        int soLuong = 0;
        for (Long id : ids) {
            try {
                KichThuoc kt = kichThuocService.layTheoId(id);
                if (kt != null && !Boolean.valueOf(batHoatDong).equals(kt.getKichHoat())) {
                    kichThuocService.doiTrangThai(id);
                    soLuong++;
                }
            } catch (Exception ignored) {
            }
        }

        String trangThaiMoi = batHoatDong ? "CÒN HOẠT ĐỘNG" : "TẠM NGƯNG";
        redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                "Đã chuyển trạng thái " + soLuong + " kích thước đã chọn sang: " + trangThaiMoi + " thành công!");
        return "redirect:/admin/kich-thuoc/hien-thi";
    }
}
