package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.TaoBaiVietBlogRequest;
import vn.fpoly.simplain.dto.TaoBannerQuangCaoRequest;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.entity.BaiVietBlog;
import vn.fpoly.simplain.entity.BannerQuangCao;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.service.BaiVietBlogService;
import vn.fpoly.simplain.service.BannerQuangCaoService;
import vn.fpoly.simplain.service.NhanVienService;

import java.util.Map;

/**
 * Controller Quản trị Nội dung Website: Bài viết Blog & Banner QC (US30 - Mod 6 / Nội dung - Khoa).
 * - Tiêu chí 1: CRUD bai_viet_blog (SEO friendly).
 * - Tiêu chí 2: CRUD banner_quang_cao tại các vị trí hiển thị trên Web.
 */
@Slf4j
@Controller
@RequestMapping("/admin/noi-dung")
@RequiredArgsConstructor
public class QuanLyNoiDungController {

    public static final String COOKIE_ADMIN_TOKEN = "jwt_admin_token";

    private final BaiVietBlogService baiVietBlogService;
    private final BannerQuangCaoService bannerQuangCaoService;
    private final NhanVienRepository nhanVienRepository;
    private final NhanVienService nhanVienService;

    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/admin/noi-dung/blog";
    }

    // =========================================================================
    // 1. QUẢN LÝ BÀI VIẾT BLOG (TIÊU CHÍ 1: SEO FRIENDLY)
    // =========================================================================

    /**
     * Màn hình danh sách bài viết Blog (US30 - Tiêu chí 1).
     */
    @GetMapping({"/blog", "/blog/hien-thi"})
    public String hienThiDanhSachBlog(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "chuyenMuc", defaultValue = "TAT_CA") String chuyenMuc,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        NhanVien actor = layNhanVienHienTai(request, session);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<BaiVietBlog> trangBlog = baiVietBlogService.layDanhSachBaiViet(tuKhoa, chuyenMuc, pageable);

        model.addAttribute("trangBlog", trangBlog);
        model.addAttribute("danhSachChuyenMuc", baiVietBlogService.layDanhSachChuyenMuc());
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("chuyenMucHienTai", chuyenMuc);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("menuActive", "blog");
        model.addAttribute("nhanVienDangNhap", actor);

        return "admin/noi-dung/blog/hien-thi";
    }

    /**
     * Màn hình Form tạo mới Bài viết Blog.
     */
    @GetMapping("/blog/tao-moi")
    public String hienThiFormTaoMoiBlog(Model model, HttpServletRequest request, HttpSession session) {
        NhanVien actor = layNhanVienHienTai(request, session);
        if (!model.containsAttribute("request")) {
            TaoBaiVietBlogRequest dto = new TaoBaiVietBlogRequest();
            if (actor != null) {
                dto.setMaTacGiaId(actor.getId());
            }
            model.addAttribute("request", dto);
        }

        model.addAttribute("danhSachChuyenMuc", baiVietBlogService.layDanhSachChuyenMuc());
        model.addAttribute("isEdit", false);
        model.addAttribute("menuActive", "blog");
        model.addAttribute("nhanVienDangNhap", actor);

        return "admin/noi-dung/blog/tao-moi";
    }

    /**
     * Xử lý lưu Bài viết Blog tạo mới.
     */
    @PostMapping("/blog/tao-moi")
    public String xuLyTaoMoiBlog(
            @Valid @ModelAttribute("request") TaoBaiVietBlogRequest blogRequest,
            BindingResult bindingResult,
            @RequestParam(name = "fileAnh", required = false) MultipartFile fileAnh,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        NhanVien actor = layNhanVienHienTai(request, session);

        if (bindingResult.hasErrors()) {
            model.addAttribute("danhSachChuyenMuc", baiVietBlogService.layDanhSachChuyenMuc());
            model.addAttribute("isEdit", false);
            model.addAttribute("menuActive", "blog");
            model.addAttribute("nhanVienDangNhap", actor);
            return "admin/noi-dung/blog/tao-moi";
        }

        try {
            Long actorId = actor != null ? actor.getId() : null;
            BaiVietBlog blogMoi = baiVietBlogService.taoMoi(blogRequest, fileAnh, actorId);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Tạo bài viết Blog thành công: [" + blogMoi.getTieuDe() + "] - Slug: " + blogMoi.getDuongDanSlug());
            return "redirect:/admin/noi-dung/blog";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            model.addAttribute("danhSachChuyenMuc", baiVietBlogService.layDanhSachChuyenMuc());
            model.addAttribute("isEdit", false);
            model.addAttribute("menuActive", "blog");
            model.addAttribute("nhanVienDangNhap", actor);
            return "admin/noi-dung/blog/tao-moi";
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi tạo bài viết blog: {}", e.getMessage(), e);
            model.addAttribute("thongBaoLoi", "Có lỗi xảy ra trong quá trình lưu bài viết: " + e.getMessage());
            model.addAttribute("danhSachChuyenMuc", baiVietBlogService.layDanhSachChuyenMuc());
            model.addAttribute("isEdit", false);
            model.addAttribute("menuActive", "blog");
            model.addAttribute("nhanVienDangNhap", actor);
            return "admin/noi-dung/blog/tao-moi";
        }
    }

    /**
     * Màn hình Form chỉnh sửa Bài viết Blog.
     */
    @GetMapping("/blog/chinh-sua/{id}")
    public String hienThiFormChinhSuaBlog(
            @PathVariable("id") Long id,
            Model model,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            BaiVietBlog blog = baiVietBlogService.layTheoId(id);
            TaoBaiVietBlogRequest dto = TaoBaiVietBlogRequest.builder()
                    .id(blog.getId())
                    .maTacGiaId(blog.getMaTacGiaId())
                    .chuyenMuc(blog.getChuyenMuc())
                    .tieuDe(blog.getTieuDe())
                    .duongDanSlug(blog.getDuongDanSlug())
                    .moTaNgan(blog.getMoTaNgan())
                    .noiDungHtml(blog.getNoiDungHtml())
                    .anhDaiDienUrl(blog.getAnhDaiDienUrl())
                    .ngayXuatBan(blog.getNgayXuatBan())
                    .build();

            NhanVien actor = layNhanVienHienTai(request, session);
            model.addAttribute("request", dto);
            model.addAttribute("blogHienTai", blog);
            model.addAttribute("danhSachChuyenMuc", baiVietBlogService.layDanhSachChuyenMuc());
            model.addAttribute("isEdit", true);
            model.addAttribute("menuActive", "blog");
            model.addAttribute("nhanVienDangNhap", actor);

            return "admin/noi-dung/blog/tao-moi";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/noi-dung/blog";
        }
    }

    /**
     * Xử lý lưu Bài viết Blog cập nhật.
     */
    @PostMapping("/blog/chinh-sua/{id}")
    public String xuLyCapNhatBlog(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("request") TaoBaiVietBlogRequest blogRequest,
            BindingResult bindingResult,
            @RequestParam(name = "fileAnh", required = false) MultipartFile fileAnh,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        NhanVien actor = layNhanVienHienTai(request, session);

        if (bindingResult.hasErrors()) {
            model.addAttribute("danhSachChuyenMuc", baiVietBlogService.layDanhSachChuyenMuc());
            model.addAttribute("isEdit", true);
            model.addAttribute("menuActive", "blog");
            model.addAttribute("nhanVienDangNhap", actor);
            return "admin/noi-dung/blog/tao-moi";
        }

        try {
            blogRequest.setId(id);
            BaiVietBlog updated = baiVietBlogService.capNhat(id, blogRequest, fileAnh);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật bài viết Blog thành công: [" + updated.getTieuDe() + "]");
            return "redirect:/admin/noi-dung/blog";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            model.addAttribute("danhSachChuyenMuc", baiVietBlogService.layDanhSachChuyenMuc());
            model.addAttribute("isEdit", true);
            model.addAttribute("menuActive", "blog");
            model.addAttribute("nhanVienDangNhap", actor);
            return "admin/noi-dung/blog/tao-moi";
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật bài viết blog: {}", e.getMessage(), e);
            model.addAttribute("thongBaoLoi", "Có lỗi xảy ra khi cập nhật: " + e.getMessage());
            model.addAttribute("danhSachChuyenMuc", baiVietBlogService.layDanhSachChuyenMuc());
            model.addAttribute("isEdit", true);
            model.addAttribute("menuActive", "blog");
            model.addAttribute("nhanVienDangNhap", actor);
            return "admin/noi-dung/blog/tao-moi";
        }
    }

    /**
     * Xử lý xóa Bài viết Blog.
     */
    @PostMapping("/blog/xoa/{id}")
    public String xuLyXoaBlog(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            baiVietBlogService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa bài viết blog thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Lỗi khi xóa bài viết: " + e.getMessage());
        }
        return "redirect:/admin/noi-dung/blog";
    }

    /**
     * API tự động sinh SEO Slug từ Tiêu đề (gọi bởi Ajax/Fetch phía Frontend).
     */
    @GetMapping("/blog/api/tao-slug")
    @ResponseBody
    public ResponseEntity<Map<String, String>> apiTaoSlug(@RequestParam("tieuDe") String tieuDe) {
        String slug = baiVietBlogService.taoSlugSeo(tieuDe);
        return ResponseEntity.ok(Map.of("slug", slug));
    }

    // =========================================================================
    // 2. QUẢN LÝ BANNER QUẢNG CÁO (TIÊU CHÍ 2: CÁC VỊ TRÍ HIỂN THỊ TRÊN WEB)
    // =========================================================================

    /**
     * Màn hình danh sách Banner Quảng cáo (US30 - Tiêu chí 2).
     */
    @GetMapping({"/banner", "/banner/hien-thi"})
    public String hienThiDanhSachBanner(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "viTri", defaultValue = "TAT_CA") String viTri,
            @RequestParam(name = "kichHoat", required = false) Boolean kichHoat,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        NhanVien actor = layNhanVienHienTai(request, session);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<BannerQuangCao> trangBanner = bannerQuangCaoService.layDanhSachBanner(tuKhoa, viTri, kichHoat, pageable);

        model.addAttribute("trangBanner", trangBanner);
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("viTriHienTai", viTri);
        model.addAttribute("kichHoatHienTai", kichHoat);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("menuActive", "banner");
        model.addAttribute("nhanVienDangNhap", actor);

        return "admin/noi-dung/banner/hien-thi";
    }

    /**
     * Màn hình Form tạo mới Banner Quảng Cáo.
     */
    @GetMapping("/banner/tao-moi")
    public String hienThiFormTaoMoiBanner(Model model, HttpServletRequest request, HttpSession session) {
        NhanVien actor = layNhanVienHienTai(request, session);
        if (!model.containsAttribute("request")) {
            TaoBannerQuangCaoRequest dto = new TaoBannerQuangCaoRequest();
            dto.setViTriHienThi("HERO_SLIDER");
            dto.setKichHoat(true);
            model.addAttribute("request", dto);
        }

        model.addAttribute("isEdit", false);
        model.addAttribute("menuActive", "banner");
        model.addAttribute("nhanVienDangNhap", actor);

        return "admin/noi-dung/banner/tao-moi";
    }

    /**
     * Xử lý lưu Banner Quảng Cáo tạo mới.
     */
    @PostMapping("/banner/tao-moi")
    public String xuLyTaoMoiBanner(
            @Valid @ModelAttribute("request") TaoBannerQuangCaoRequest bannerRequest,
            BindingResult bindingResult,
            @RequestParam(name = "fileAnh", required = false) MultipartFile fileAnh,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        NhanVien actor = layNhanVienHienTai(request, session);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("menuActive", "banner");
            model.addAttribute("nhanVienDangNhap", actor);
            return "admin/noi-dung/banner/tao-moi";
        }

        try {
            BannerQuangCao banner = bannerQuangCaoService.taoMoi(bannerRequest, fileAnh);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Tạo banner quảng cáo thành công: [" + banner.getTieuDe() + "]");
            return "redirect:/admin/noi-dung/banner";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("menuActive", "banner");
            model.addAttribute("nhanVienDangNhap", actor);
            return "admin/noi-dung/banner/tao-moi";
        } catch (Exception e) {
            log.error("Lỗi khi tạo banner quảng cáo: {}", e.getMessage(), e);
            model.addAttribute("thongBaoLoi", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("menuActive", "banner");
            model.addAttribute("nhanVienDangNhap", actor);
            return "admin/noi-dung/banner/tao-moi";
        }
    }

    /**
     * Màn hình Form chỉnh sửa Banner Quảng Cáo.
     */
    @GetMapping("/banner/chinh-sua/{id}")
    public String hienThiFormChinhSuaBanner(
            @PathVariable("id") Long id,
            Model model,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            BannerQuangCao banner = bannerQuangCaoService.layTheoId(id);
            TaoBannerQuangCaoRequest dto = TaoBannerQuangCaoRequest.builder()
                    .id(banner.getId())
                    .tieuDe(banner.getTieuDe())
                    .hinhAnhUrl(banner.getHinhAnhUrl())
                    .duongDanDich(banner.getDuongDanDich())
                    .viTriHienThi(banner.getViTriHienThi())
                    .kichHoat(banner.getKichHoat())
                    .build();

            NhanVien actor = layNhanVienHienTai(request, session);
            model.addAttribute("request", dto);
            model.addAttribute("bannerHienTai", banner);
            model.addAttribute("isEdit", true);
            model.addAttribute("menuActive", "banner");
            model.addAttribute("nhanVienDangNhap", actor);

            return "admin/noi-dung/banner/tao-moi";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/noi-dung/banner";
        }
    }

    /**
     * Xử lý lưu Banner Quảng Cáo cập nhật.
     */
    @PostMapping("/banner/chinh-sua/{id}")
    public String xuLyCapNhatBanner(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("request") TaoBannerQuangCaoRequest bannerRequest,
            BindingResult bindingResult,
            @RequestParam(name = "fileAnh", required = false) MultipartFile fileAnh,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        NhanVien actor = layNhanVienHienTai(request, session);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("menuActive", "banner");
            model.addAttribute("nhanVienDangNhap", actor);
            return "admin/noi-dung/banner/tao-moi";
        }

        try {
            bannerRequest.setId(id);
            BannerQuangCao updated = bannerQuangCaoService.capNhat(id, bannerRequest, fileAnh);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật banner quảng cáo thành công: [" + updated.getTieuDe() + "]");
            return "redirect:/admin/noi-dung/banner";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("menuActive", "banner");
            model.addAttribute("nhanVienDangNhap", actor);
            return "admin/noi-dung/banner/tao-moi";
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật banner: {}", e.getMessage(), e);
            model.addAttribute("thongBaoLoi", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("menuActive", "banner");
            model.addAttribute("nhanVienDangNhap", actor);
            return "admin/noi-dung/banner/tao-moi";
        }
    }

    /**
     * Bật / tắt nhanh trạng thái kích hoạt của Banner.
     */
    @PostMapping("/banner/doi-trang-thai/{id}")
    public String xuLyDoiTrangThaiBanner(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean trangThai = bannerQuangCaoService.chuyenTrangThaiKichHoat(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái banner sang: " + (trangThai ? "Đang kích hoạt" : "Đã tạm dừng"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/noi-dung/banner";
    }

    /**
     * Xử lý xóa Banner Quảng Cáo.
     */
    @PostMapping("/banner/xoa/{id}")
    public String xuLyXoaBanner(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            bannerQuangCaoService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa banner quảng cáo thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Lỗi khi xóa banner: " + e.getMessage());
        }
        return "redirect:/admin/noi-dung/banner";
    }

    // =========================================================================
    // HELPER: LẤY NHÂN VIÊN ĐĂNG NHẬP
    // =========================================================================

    private NhanVien layNhanVienHienTai(HttpServletRequest request, HttpSession session) {
        if (session != null) {
            Object obj = session.getAttribute("nhanVienHienTai");
            if (obj instanceof XacThucNhanVienResponse xacThuc && xacThuc.getId() != null) {
                return nhanVienRepository.findById(xacThuc.getId()).orElse(null);
            }
            if (session.getAttribute("nhanVienDangNhap") instanceof NhanVien nv) {
                return nv;
            }
        }

        if (request != null && request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (COOKIE_ADMIN_TOKEN.equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                    try {
                        XacThucNhanVienResponse xacThuc = nhanVienService.layThongTinNhanVienTuToken(c.getValue().trim());
                        if (xacThuc != null && xacThuc.getId() != null) {
                            return nhanVienRepository.findById(xacThuc.getId()).orElse(null);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        return nhanVienRepository.findAll().stream().findFirst().orElse(null);
    }
}
