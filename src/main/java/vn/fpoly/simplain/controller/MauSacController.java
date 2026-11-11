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
import vn.fpoly.simplain.entity.MauSac;
import vn.fpoly.simplain.repository.KichThuocRepository;
import vn.fpoly.simplain.repository.MauSacRepository;
import vn.fpoly.simplain.service.MauSacService;

/**
 * Controller MVC Quản lý Màu sắc sản phẩm [mau_sac] (Hex / Mã màu & Tên màu) trên giao diện Web (US07).
 */
@Controller
@RequestMapping("/admin/mau-sac")
public class MauSacController {

    private final MauSacService mauSacService;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;

    @Autowired
    public MauSacController(MauSacService mauSacService,
                            MauSacRepository mauSacRepository,
                            KichThuocRepository kichThuocRepository) {
        this.mauSacService = mauSacService;
        this.mauSacRepository = mauSacRepository;
        this.kichThuocRepository = kichThuocRepository;
    }

    /**
     * Hiển thị danh sách màu sắc có phân trang, tìm kiếm và bảng chọn màu Hex trực quan.
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
        Page<MauSac> mauSacPage = mauSacService.layDanhSachPhanTrang(tuKhoa, pageable);

        model.addAttribute("listMauSac", mauSacPage.getContent());
        model.addAttribute("pageData", mauSacPage);
        if (!model.containsAttribute("mauSac")) {
            model.addAttribute("mauSac", MauSac.builder().maMau("#000000").build());
        }
        model.addAttribute("page", page);
        model.addAttribute("totalPages", mauSacPage.getTotalPages());
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("tongMauSac", mauSacRepository.count());
        model.addAttribute("tongKichThuoc", kichThuocRepository.count());

        return "admin/mau-sac/hien-thi";
    }

    /**
     * Xử lý thêm mới màu sắc (Hex / Mã màu).
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("mauSac") MauSac mauSac,
                      BindingResult result,
                      Model model,
                      RedirectAttributes redirectAttributes,
                      @RequestParam(defaultValue = "0") Integer page) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu màu sắc chưa hợp lệ, vui lòng kiểm tra lại mã màu (Hex) và tên màu!");
            return hienThi(model, page, null);
        }

        try {
            MauSac saved = mauSacService.them(mauSac);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Thêm mới màu sắc [" + saved.getTenMau() + " (" + saved.getMaMau() + ")] thành công!");
            return "redirect:/admin/mau-sac/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return hienThi(model, page, null);
        }
    }

    /**
     * Hiển thị màn hình chi tiết và cập nhật màu sắc.
     */
    @GetMapping("/view")
    public String view(Model model,
                       @RequestParam Long id,
                       RedirectAttributes redirectAttributes) {
        try {
            MauSac ms = mauSacService.layTheoId(id);
            model.addAttribute("mauSac", ms);
            return "admin/mau-sac/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/mau-sac/hien-thi";
        }
    }

    /**
     * Xử lý cập nhật thông tin màu sắc.
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("mauSac") MauSac mauSac,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu cập nhật màu sắc chưa hợp lệ, vui lòng kiểm tra lại!");
            return "admin/mau-sac/view";
        }

        try {
            MauSac updated = mauSacService.sua(mauSac.getId(), mauSac);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật màu sắc [" + updated.getTenMau() + " (" + updated.getMaMau() + ")] thành công!");
            return "redirect:/admin/mau-sac/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return "admin/mau-sac/view";
        }
    }

    /**
     * Xử lý xóa màu sắc (Chặn xóa nếu đang có biến thể hoặc hình ảnh sản phẩm sử dụng).
     */
    @GetMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            mauSacService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Xóa màu sắc thành công!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/mau-sac/hien-thi";
    }

    /**
     * Chuyển đổi trạng thái hoạt động (Còn hoạt động / Tạm ngưng) của màu sắc.
     */
    @GetMapping("/doi-trang-thai")
    public String doiTrangThai(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            MauSac ms = mauSacService.doiTrangThai(id);
            String trangThaiText = Boolean.TRUE.equals(ms.getKichHoat()) ? "CÒN HOẠT ĐỘNG" : "TẠM NGƯNG";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái màu sắc [" + ms.getTenMau() + "] sang: " + trangThaiText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/mau-sac/hien-thi";
    }

    /**
     * Thao tác hàng loạt trên danh sách ID màu sắc được chọn (Còn hoạt động hoặc Tạm ngưng).
     */
    @PostMapping("/thao-tac-hang-loat")
    public String thaoTacHangLoat(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "hanhDong") String hanhDong,
                                  RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng tích chọn ít nhất một màu sắc để thao tác hàng loạt!");
            return "redirect:/admin/mau-sac/hien-thi";
        }

        boolean batHoatDong = "KICH_HOAT".equalsIgnoreCase(hanhDong);
        int soLuong = 0;
        for (Long id : ids) {
            try {
                MauSac ms = mauSacService.layTheoId(id);
                if (ms != null && !Boolean.valueOf(batHoatDong).equals(ms.getKichHoat())) {
                    mauSacService.doiTrangThai(id);
                    soLuong++;
                }
            } catch (Exception ignored) {
            }
        }

        String trangThaiMoi = batHoatDong ? "CÒN HOẠT ĐỘNG" : "TẠM NGƯNG";
        redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                "Đã chuyển trạng thái " + soLuong + " màu sắc đã chọn sang: " + trangThaiMoi + " thành công!");
        return "redirect:/admin/mau-sac/hien-thi";
    }
}
