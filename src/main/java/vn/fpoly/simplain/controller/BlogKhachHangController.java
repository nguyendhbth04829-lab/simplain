package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.fpoly.simplain.dto.HoiVienResponse;
import vn.fpoly.simplain.dto.ThongTinKhachHangResponse;
import vn.fpoly.simplain.entity.BaiVietBlog;
import vn.fpoly.simplain.entity.BannerQuangCao;
import vn.fpoly.simplain.service.*;

import java.util.List;

/**
 * Controller phục vụ Khách hàng xem Tin tức & Bài viết Blog (US30 - Mod 6 / Nội dung - Khoa).
 * - Xem danh sách bài viết blog (/blog, /bai-viet)
 * - Xem chi tiết bài viết chuẩn SEO Friendly (/blog/{slug}, /bai-viet/{slug})
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class BlogKhachHangController {

    private final BaiVietBlogService baiVietBlogService;
    private final BannerQuangCaoService bannerQuangCaoService;
    private final KhachHangService khachHangService;
    private final SanPhamYeuThichService sanPhamYeuThichService;
    private final LoyaltyHoiVienService loyaltyHoiVienService;

    /**
     * Màn hình danh sách bài viết Blog dành cho Khách hàng.
     */
    @GetMapping({"/blog", "/bai-viet"})
    public String hienThiDanhSachBlog(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "chuyenMuc", defaultValue = "TAT_CA") String chuyenMuc,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            HttpServletRequest request,
            Model model) {

        napThongTinKhachHang(request, model);

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<BaiVietBlog> trangBlog = baiVietBlogService.layDanhSachBaiViet(tuKhoa, chuyenMuc, pageable);
        List<String> danhSachChuyenMuc = baiVietBlogService.layDanhSachChuyenMuc();

        // Lấy 4 bài viết mới nhất cho sidebar
        Page<BaiVietBlog> baiVietMoiNhat = baiVietBlogService.layDanhSachBaiViet(null, null, PageRequest.of(0, 4));

        // Banner quảng cáo đầu trang blog nếu có
        List<BannerQuangCao> bannerBlog = bannerQuangCaoService.layBannerTheoViTri("STORE_TOP");

        model.addAttribute("trangBlog", trangBlog);
        model.addAttribute("danhSachChuyenMuc", danhSachChuyenMuc);
        model.addAttribute("baiVietMoiNhat", baiVietMoiNhat.getContent());
        model.addAttribute("bannerBlog", !bannerBlog.isEmpty() ? bannerBlog.get(0) : null);
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("chuyenMucHienTai", chuyenMuc);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "khach-hang/blog/danh-sach";
    }

    /**
     * Màn hình Chi tiết Bài viết Blog theo đường dẫn SEO Friendly Slug.
     */
    @GetMapping({"/blog/{slug}", "/bai-viet/{slug}"})
    public String hienThiChiTietBlog(
            @PathVariable("slug") String slug,
            HttpServletRequest request,
            Model model) {

        napThongTinKhachHang(request, model);

        try {
            BaiVietBlog blog = baiVietBlogService.layTheoSlug(slug);

            // Các bài viết liên quan (cùng chuyên mục hoặc mới nhất)
            Page<BaiVietBlog> baiVietLienQuan = baiVietBlogService.layDanhSachBaiViet(null, blog.getChuyenMuc(), PageRequest.of(0, 4));
            List<BaiVietBlog> dsLienQuan = baiVietLienQuan.getContent().stream()
                    .filter(b -> !b.getId().equals(blog.getId()))
                    .limit(3)
                    .toList();

            model.addAttribute("blog", blog);
            model.addAttribute("danhSachLienQuan", dsLienQuan);
            model.addAttribute("danhSachChuyenMuc", baiVietBlogService.layDanhSachChuyenMuc());

            return "khach-hang/blog/chi-tiet";
        } catch (IllegalArgumentException e) {
            log.warn("Không tìm thấy bài viết với slug [{}]: {}", slug, e.getMessage());
            return "redirect:/blog";
        }
    }

    // =========================================================================
    // HELPER: NẠP THÔNG TIN KHÁCH HÀNG
    // =========================================================================
    private void napThongTinKhachHang(HttpServletRequest request, Model model) {
        if (request == null || request.getCookies() == null) return;
        for (Cookie cookie : request.getCookies()) {
            if (KhachHangController.COOKIE_TOKEN.equals(cookie.getName())
                    && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                try {
                    ThongTinKhachHangResponse kh = khachHangService.layThongTinTuToken(cookie.getValue());
                    if (kh != null) {
                        model.addAttribute("khachHangDangNhap", kh);
                        List<Long> dsYeuThich = sanPhamYeuThichService.layDanhSachMaSanPhamYeuThich(kh.getId());
                        model.addAttribute("soLuongYeuThich", dsYeuThich != null ? dsYeuThich.size() : 0);
                        if (loyaltyHoiVienService != null) {
                            HoiVienResponse hoiVien = loyaltyHoiVienService.layThongTinHoiVien(kh.getId());
                            model.addAttribute("hoiVien", hoiVien);
                        }
                    }
                } catch (Exception ignored) {
                }
                break;
            }
        }
    }
}
