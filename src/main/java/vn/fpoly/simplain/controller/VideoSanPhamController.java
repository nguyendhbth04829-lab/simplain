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
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.entity.VideoSanPham;
import vn.fpoly.simplain.service.SanPhamService;
import vn.fpoly.simplain.service.VideoSanPhamService;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * Controller MVC Quản lý Video quảng cáo sản phẩm [video_san_pham] trong CMS Admin.
 * Mỗi sản phẩm có 1 video quảng cáo (1-1).
 * Hỗ trợ chọn video từ máy hoặc nhập URL, Xem trước Video, Tìm kiếm từ khóa, Lọc trạng thái và Phân trang chuẩn SIMPLAIN.
 */
@Controller
@RequestMapping("/admin/video-san-pham")
public class VideoSanPhamController {

    private final VideoSanPhamService videoSanPhamService;
    private final SanPhamService sanPhamService;

    @Autowired
    public VideoSanPhamController(VideoSanPhamService videoSanPhamService,
                                  SanPhamService sanPhamService) {
        this.videoSanPhamService = videoSanPhamService;
        this.sanPhamService = sanPhamService;
    }

    @GetMapping({"", "/"})
    public String chuyenHuongMacDinh() {
        return "redirect:/admin/video-san-pham/hien-thi";
    }

    /**
     * Hiển thị danh sách Video quảng cáo sản phẩm có phân trang, tìm kiếm và lọc trạng thái.
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
        Page<VideoSanPham> pageData = videoSanPhamService.layDanhSachPhanTrang(tuKhoa, kichHoat, pageable);

        List<SanPham> danhSachSanPham = sanPhamService.layTatCa();

        model.addAttribute("listVideo", pageData.getContent());
        model.addAttribute("pageData", pageData);
        model.addAttribute("danhSachSanPham", danhSachSanPham);

        if (!model.containsAttribute("videoSanPham")) {
            model.addAttribute("videoSanPham", new VideoSanPham());
        }

        model.addAttribute("page", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("kichHoatLoc", kichHoat);
        model.addAttribute("tongVideo", videoSanPhamService.demTongSo());

        return "admin/video-san-pham/hien-thi";
    }

    /**
     * Xử lý thêm mới Video quảng cáo cho sản phẩm (chọn file từ máy hoặc nhập URL).
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("videoSanPham") VideoSanPham videoSanPham,
                      BindingResult result,
                      @RequestParam(value = "fileVideo", required = false) MultipartFile fileVideo,
                      Model model,
                      RedirectAttributes redirectAttributes,
                      @RequestParam(defaultValue = "0") Integer page) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu video quảng cáo chưa hợp lệ, vui lòng kiểm tra lại!");
            return hienThi(model, page, null, null);
        }
        try {
            VideoSanPham saved = videoSanPhamService.them(videoSanPham, fileVideo);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Thêm mới video quảng cáo cho sản phẩm [" + saved.getTenSanPham() + "] thành công!");
            return "redirect:/admin/video-san-pham/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return hienThi(model, page, null, null);
        }
    }

    /**
     * Xử lý cập nhật thông tin Video quảng cáo sản phẩm (chọn file mới từ máy hoặc sửa URL).
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("videoSanPham") VideoSanPham videoSanPham,
                         BindingResult result,
                         @RequestParam(value = "fileVideo", required = false) MultipartFile fileVideo,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         @RequestParam(defaultValue = "0") Integer page) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu cập nhật video quảng cáo chưa hợp lệ!");
            return hienThi(model, page, null, null);
        }
        try {
            VideoSanPham saved = videoSanPhamService.sua(videoSanPham.getId(), videoSanPham, fileVideo);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật video quảng cáo cho sản phẩm [" + saved.getTenSanPham() + "] thành công!");
            return "redirect:/admin/video-san-pham/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return hienThi(model, page, null, null);
        }
    }

    /**
     * Chuyển đổi trạng thái hiển thị (Kích hoạt / Tạm ngưng) của Video quảng cáo.
     */
    @GetMapping("/doi-trang-thai")
    public String doiTrangThai(@RequestParam("id") Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            VideoSanPham updated = videoSanPhamService.doiTrangThai(id);
            String trangThaiText = Boolean.TRUE.equals(updated.getKichHoat()) ? "Đang hiển thị" : "Tạm ngưng";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Chuyển trạng thái video của sản phẩm [" + updated.getTenSanPham() + "] sang '" + trangThaiText + "' thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/video-san-pham/hien-thi";
    }

    /**
     * Xóa Video quảng cáo khỏi hệ thống.
     */
    @GetMapping("/delete")
    public String delete(@RequestParam("id") Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            videoSanPhamService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Xóa video quảng cáo thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Không thể xóa video: " + e.getMessage());
        }
        return "redirect:/admin/video-san-pham/hien-thi";
    }

    /**
     * Thao tác hàng loạt trên các video quảng cáo đã chọn (Kích hoạt / Tạm ngưng hàng loạt).
     */
    @PostMapping("/thao-tac-hang-loat")
    public String thaoTacHangLoat(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            @RequestParam(name = "hanhDong") String hanhDong,
            RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng chọn ít nhất một video quảng cáo để thực hiện!");
            return "redirect:/admin/video-san-pham/hien-thi";
        }

        int count = 0;
        boolean targetStatus = "KICH_HOAT".equalsIgnoreCase(hanhDong);
        for (Long id : ids) {
            try {
                VideoSanPham v = videoSanPhamService.layTheoId(id);
                if (v.getKichHoat() == null || v.getKichHoat() != targetStatus) {
                    videoSanPhamService.capNhatTrangThai(id, targetStatus);
                    count++;
                }
            } catch (Exception ignored) {
            }
        }

        String actionText = targetStatus ? "kích hoạt" : "tạm ngưng";
        redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                "Đã " + actionText + " thành công " + count + " video quảng cáo đã chọn.");
        return "redirect:/admin/video-san-pham/hien-thi";
    }
}
