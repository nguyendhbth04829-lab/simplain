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
import vn.fpoly.simplain.entity.DanhMuc;
import vn.fpoly.simplain.service.DanhMucService;

/**
 * Controller MVC Quản lý Danh mục sản phẩm hỗ trợ phân cấp Cha / Con trên giao diện Web (US06).
 */
@Controller
@RequestMapping("/admin/danh-muc")
public class DanhMucController {

    private final DanhMucService danhMucService;

    @Autowired
    public DanhMucController(DanhMucService danhMucService) {
        this.danhMucService = danhMucService;
    }

    /**
     * Hiển thị danh sách danh mục có phân cấp Cha / Con, tìm kiếm theo từ khóa, lọc theo danh mục cha & trạng thái.
     */
    @GetMapping({"", "/", "/hien-thi"})
    public String hienThi(Model model,
                          @RequestParam(defaultValue = "0") Integer page,
                          @RequestParam(required = false) String ten,
                          @RequestParam(required = false) Long maDanhMucCha,
                          @RequestParam(required = false) Boolean kichHoat,
                          @RequestParam(defaultValue = "cay") String cheDoXem) {
        if (page == null || page < 0) {
            page = 0;
        }
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("thuTuHienThi").ascending().and(Sort.by("id").descending()));
        Page<DanhMuc> danhMucPage = danhMucService.layDanhSachPhanTrang(ten, maDanhMucCha, kichHoat, pageable);

        model.addAttribute("listDanhMuc", danhMucPage.getContent());
        model.addAttribute("pageData", danhMucPage);
        model.addAttribute("listTatCa", danhMucService.layTatCa());
        model.addAttribute("listDanhMucGoc", danhMucService.layDanhSachDanhMucGoc());
        model.addAttribute("cayDanhMuc", danhMucService.layCayDanhMuc(ten, kichHoat));
        model.addAttribute("thongKePhanCap", danhMucService.demThongKePhanCap());
        model.addAttribute("cheDoXem", (cheDoXem != null && !cheDoXem.isBlank()) ? cheDoXem : "cay");

        if (!model.containsAttribute("danhMuc")) {
            model.addAttribute("danhMuc", new DanhMuc());
        }
        model.addAttribute("page", page);
        model.addAttribute("totalPages", danhMucPage.getTotalPages());
        model.addAttribute("ten", ten != null ? ten : "");
        model.addAttribute("maDanhMucChaLoc", maDanhMucCha);
        model.addAttribute("kichHoatLoc", kichHoat);

        return "admin/danh-muc/hien-thi";
    }

    /**
     * Xử lý thêm mới danh mục sản phẩm (Hỗ trợ chọn danh mục cha).
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("danhMuc") DanhMuc danhMuc,
                      BindingResult result,
                      Model model,
                      RedirectAttributes redirectAttributes,
                      @RequestParam(defaultValue = "0") Integer page) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu danh mục chưa hợp lệ, vui lòng kiểm tra lại các trường báo đỏ!");
            return hienThi(model, page, null, null, null, "cay");
        }

        try {
            DanhMuc saved = danhMucService.them(danhMuc);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Thêm mới danh mục [" + saved.getTenDanhMuc() + "] thành công!");
            return "redirect:/admin/danh-muc/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return hienThi(model, page, null, null, null, "cay");
        }
    }

    /**
     * Hiển thị màn hình chi tiết và cập nhật danh mục.
     */
    @GetMapping("/view")
    public String view(Model model,
                       @RequestParam Long id,
                       RedirectAttributes redirectAttributes) {
        try {
            DanhMuc dm = danhMucService.layTheoId(id);
            model.addAttribute("danhMuc", dm);
            model.addAttribute("listTatCa", danhMucService.layTatCa());
            model.addAttribute("listDanhMucGoc", danhMucService.layDanhSachDanhMucGoc());
            model.addAttribute("danhMucConList", dm.getDanhMucConList());
            return "admin/danh-muc/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/danh-muc/hien-thi";
        }
    }

    /**
     * Xử lý cập nhật danh mục và kiểm tra phân cấp Danh mục Cha / Con.
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("danhMuc") DanhMuc danhMuc,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu cập nhật danh mục chưa hợp lệ, vui lòng kiểm tra lại!");
            model.addAttribute("listTatCa", danhMucService.layTatCa());
            model.addAttribute("listDanhMucGoc", danhMucService.layDanhSachDanhMucGoc());
            try {
                DanhMuc dm = danhMucService.layTheoId(danhMuc.getId());
                model.addAttribute("danhMucConList", dm.getDanhMucConList());
            } catch (Exception ignored) {
            }
            return "admin/danh-muc/view";
        }

        try {
            DanhMuc updated = danhMucService.sua(danhMuc.getId(), danhMuc);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật danh mục [" + updated.getTenDanhMuc() + "] thành công!");
            return "redirect:/admin/danh-muc/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            model.addAttribute("listTatCa", danhMucService.layTatCa());
            model.addAttribute("listDanhMucGoc", danhMucService.layDanhSachDanhMucGoc());
            try {
                DanhMuc dm = danhMucService.layTheoId(danhMuc.getId());
                model.addAttribute("danhMucConList", dm.getDanhMucConList());
            } catch (Exception ignored) {
            }
            return "admin/danh-muc/view";
        }
    }

    /**
     * Đổi nhanh trạng thái Kích hoạt / Đã ẩn của danh mục.
     */
    @GetMapping("/doi-trang-thai")
    public String doiTrangThai(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            DanhMuc dm = danhMucService.doiTrangThai(id);
            String trangThaiText = Boolean.TRUE.equals(dm.getKichHoat()) ? "HOẠT ĐỘNG" : "ĐÃ ẨN";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái danh mục [" + dm.getTenDanhMuc() + "] sang: " + trangThaiText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/danh-muc/hien-thi";
    }

    /**
     * Xử lý xóa danh mục (Chặn xóa nếu đang có danh mục con hoặc đang có sản phẩm).
     */
    @GetMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            danhMucService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Xóa danh mục sản phẩm thành công!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/danh-muc/hien-thi";
    }

    /**
     * Thao tác hàng loạt trên danh sách ID danh mục được chọn (Kích hoạt hoặc Ngưng hoạt động).
     */
    @PostMapping("/thao-tac-hang-loat")
    public String thaoTacHangLoat(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "hanhDong") String hanhDong,
                                  RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng tích chọn ít nhất một danh mục để thao tác hàng loạt!");
            return "redirect:/admin/danh-muc/hien-thi";
        }

        boolean batHoatDong = "KICH_HOAT".equalsIgnoreCase(hanhDong);
        int soLuong = 0;
        for (Long id : ids) {
            try {
                DanhMuc dm = danhMucService.layTheoId(id);
                if (dm != null && !Boolean.valueOf(batHoatDong).equals(dm.getKichHoat())) {
                    danhMucService.doiTrangThai(id);
                    soLuong++;
                }
            } catch (Exception ignored) {
            }
        }

        String trangThaiMoi = batHoatDong ? "CÒN HOẠT ĐỘNG" : "NGƯNG HOẠT ĐỘNG";
        redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                "Đã chuyển trạng thái " + soLuong + " danh mục đã chọn sang: " + trangThaiMoi + " thành công!");
        return "redirect:/admin/danh-muc/hien-thi";
    }
}
