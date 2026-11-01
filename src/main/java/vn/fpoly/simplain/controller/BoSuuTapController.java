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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.entity.BoSuuTap;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.service.BoSuuTapService;
import vn.fpoly.simplain.service.DanhMucService;

import java.util.List;

/**
 * Controller MVC Quản lý Bộ sưu tập thời trang kèm Banner chiến dịch & Gán sản phẩm (US06).
 */
@Controller
@RequestMapping("/admin/bo-suu-tap")
public class BoSuuTapController {

    private final BoSuuTapService boSuuTapService;

    @Autowired
    private DanhMucService danhMucService;

    @Autowired
    public BoSuuTapController(BoSuuTapService boSuuTapService) {
        this.boSuuTapService = boSuuTapService;
    }

    /**
     * Hiển thị danh sách bộ sưu tập có phân trang, tìm kiếm và lọc trạng thái.
     */
    @GetMapping({"", "/", "/hien-thi"})
    public String hienThi(Model model,
                          @RequestParam(defaultValue = "0") Integer page,
                          @RequestParam(required = false) String ten,
                          @RequestParam(required = false) Boolean kichHoat) {
        if (page == null || page < 0) {
            page = 0;
        }
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<BoSuuTap> boSuuTapPage = boSuuTapService.layDanhSachPhanTrang(ten, kichHoat, pageable);

        model.addAttribute("listBoSuuTap", boSuuTapPage.getContent());
        model.addAttribute("pageData", boSuuTapPage);
        model.addAttribute("thongKe", boSuuTapService.demThongKe());
        if (!model.containsAttribute("boSuuTap")) {
            model.addAttribute("boSuuTap", new BoSuuTap());
        }
        model.addAttribute("page", page);
        model.addAttribute("totalPages", boSuuTapPage.getTotalPages());
        model.addAttribute("ten", ten != null ? ten : "");
        model.addAttribute("kichHoatLoc", kichHoat);

        return "admin/bo-suu-tap/hien-thi";
    }

    /**
     * Xử lý thêm mới bộ sưu tập kèm upload ảnh Banner.
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("boSuuTap") BoSuuTap boSuuTap,
                      BindingResult result,
                      Model model,
                      RedirectAttributes redirectAttributes,
                      @RequestParam(defaultValue = "0") Integer page,
                      @RequestParam(value = "fileBanner", required = false) MultipartFile fileBanner) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu bộ sưu tập chưa hợp lệ, vui lòng kiểm tra lại các trường báo đỏ!");
            return hienThi(model, page, null, null);
        }

        try {
            BoSuuTap saved = boSuuTapService.them(boSuuTap, fileBanner);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Thêm mới bộ sưu tập [" + saved.getTenBoSuuTap() + "] thành công!");
            return "redirect:/admin/bo-suu-tap/hien-thi";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return hienThi(model, page, null, null);
        }
    }

    /**
     * Hiển thị màn hình chi tiết, cập nhật bộ sưu tập và danh sách sản phẩm thuộc BST.
     */
    @GetMapping("/view")
    public String view(Model model,
                       @RequestParam Long id,
                       RedirectAttributes redirectAttributes) {
        try {
            BoSuuTap bst = boSuuTapService.layTheoId(id);
            model.addAttribute("boSuuTap", bst);

            List<SanPham> sanPhamTrongBST = boSuuTapService.layDanhSachSanPhamTrongBST(id);
            model.addAttribute("danhSachSanPhamTrongBST", sanPhamTrongBST);

            List<SanPham> sanPhamKhaDung = boSuuTapService.layDanhSachSanPhamKhaDung(id, null, null);
            model.addAttribute("danhSachSanPhamKhaDung", sanPhamKhaDung);

            if (danhMucService != null) {
                model.addAttribute("danhSachDanhMuc", danhMucService.layTatCa());
            }

            return "admin/bo-suu-tap/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/bo-suu-tap/hien-thi";
        }
    }

    /**
     * Thêm các sản phẩm được chọn vào bộ sưu tập.
     */
    @PostMapping("/them-san-pham")
    public String themSanPham(@RequestParam("boSuuTapId") Long boSuuTapId,
                              @RequestParam(name = "sanPhamIds", required = false) List<Long> sanPhamIds,
                              RedirectAttributes redirectAttributes) {
        if (sanPhamIds == null || sanPhamIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng tích chọn ít nhất 1 sản phẩm để thêm vào bộ sưu tập!");
            return "redirect:/admin/bo-suu-tap/view?id=" + boSuuTapId;
        }

        try {
            int soLuong = boSuuTapService.themSanPhamVaoBoSuuTap(boSuuTapId, sanPhamIds);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã thêm thành công " + soLuong + " sản phẩm vào bộ sưu tập!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/bo-suu-tap/view?id=" + boSuuTapId;
    }

    /**
     * Gỡ bỏ sản phẩm khỏi bộ sưu tập.
     */
    @GetMapping("/xoa-san-pham")
    public String xoaSanPham(@RequestParam("boSuuTapId") Long boSuuTapId,
                             @RequestParam("sanPhamId") Long sanPhamId,
                             RedirectAttributes redirectAttributes) {
        try {
            boSuuTapService.xoaSanPhamKhoiBoSuuTap(boSuuTapId, sanPhamId);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã gỡ sản phẩm khỏi bộ sưu tập thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/bo-suu-tap/view?id=" + boSuuTapId;
    }

    /**
     * Xử lý cập nhật bộ sưu tập và thay đổi / giữ nguyên ảnh Banner.
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("boSuuTap") BoSuuTap boSuuTap,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         @RequestParam(value = "fileBanner", required = false) MultipartFile fileBanner) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu cập nhật bộ sưu tập chưa hợp lệ, vui lòng kiểm tra lại!");
            try {
                model.addAttribute("danhSachSanPhamTrongBST", boSuuTapService.layDanhSachSanPhamTrongBST(boSuuTap.getId()));
                model.addAttribute("danhSachSanPhamKhaDung", boSuuTapService.layDanhSachSanPhamKhaDung(boSuuTap.getId(), null, null));
                if (danhMucService != null) {
                    model.addAttribute("danhSachDanhMuc", danhMucService.layTatCa());
                }
            } catch (Exception ignored) {
            }
            return "admin/bo-suu-tap/view";
        }

        try {
            BoSuuTap updated = boSuuTapService.sua(boSuuTap.getId(), boSuuTap, fileBanner);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật bộ sưu tập [" + updated.getTenBoSuuTap() + "] thành công!");
            return "redirect:/admin/bo-suu-tap/hien-thi";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            try {
                model.addAttribute("danhSachSanPhamTrongBST", boSuuTapService.layDanhSachSanPhamTrongBST(boSuuTap.getId()));
                model.addAttribute("danhSachSanPhamKhaDung", boSuuTapService.layDanhSachSanPhamKhaDung(boSuuTap.getId(), null, null));
                if (danhMucService != null) {
                    model.addAttribute("danhSachDanhMuc", danhMucService.layTatCa());
                }
            } catch (Exception ignored) {
            }
            return "admin/bo-suu-tap/view";
        }
    }

    /**
     * Đổi nhanh trạng thái Kích hoạt / Đã ẩn của bộ sưu tập.
     */
    @GetMapping("/doi-trang-thai")
    public String doiTrangThai(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            BoSuuTap bst = boSuuTapService.doiTrangThai(id);
            String trangThaiText = Boolean.TRUE.equals(bst.getKichHoat()) ? "HOẠT ĐỘNG" : "ĐÃ ẨN";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái bộ sưu tập [" + bst.getTenBoSuuTap() + "] sang: " + trangThaiText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/bo-suu-tap/hien-thi";
    }

    /**
     * Xử lý xóa bộ sưu tập (Chặn xóa nếu đang có sản phẩm thuộc bộ sưu tập này).
     */
    @GetMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            boSuuTapService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Xóa bộ sưu tập thành công!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/bo-suu-tap/hien-thi";
    }

    /**
     * Thao tác hàng loạt trên danh sách ID bộ sưu tập được chọn (Kích hoạt hoặc Ẩn).
     */
    @PostMapping("/thao-tac-hang-loat")
    public String thaoTacHangLoat(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "hanhDong") String hanhDong,
                                  RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng tích chọn ít nhất một bộ sưu tập để thao tác hàng loạt!");
            return "redirect:/admin/bo-suu-tap/hien-thi";
        }

        boolean batHoatDong = "KICH_HOAT".equalsIgnoreCase(hanhDong);
        int soLuong = 0;
        for (Long id : ids) {
            try {
                BoSuuTap bst = boSuuTapService.layTheoId(id);
                if (bst != null && !Boolean.valueOf(batHoatDong).equals(bst.getKichHoat())) {
                    boSuuTapService.doiTrangThai(id);
                    soLuong++;
                }
            } catch (Exception ignored) {
            }
        }

        String trangThaiMoi = batHoatDong ? "HOẠT ĐỘNG" : "ĐÃ ẨN";
        redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                "Đã chuyển trạng thái " + soLuong + " bộ sưu tập đã chọn sang: " + trangThaiMoi + " thành công!");
        return "redirect:/admin/bo-suu-tap/hien-thi";
    }
}