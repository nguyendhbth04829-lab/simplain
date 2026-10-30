package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.BaiVietBlog;
import vn.fpoly.simplain.entity.BannerQuangCao;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.repository.BaiVietBlogRepository;
import vn.fpoly.simplain.repository.BannerQuangCaoRepository;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.service.BannerQuangCaoService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử Web MVC tích hợp chức năng Admin Quản Lý Nội Dung Blog & Banner QC (US30 - Mod 6 - Khoa).
 * - Tiêu chí 1: CRUD bai_viet_blog (SEO friendly).
 * - Tiêu chí 2: CRUD banner_quang_cao tại các vị trí hiển thị trên Web.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Kiểm thử Web MVC Quản Lý Nội Dung Blog & Banner QC (US30)")
class QuanLyNoiDungControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BaiVietBlogRepository baiVietBlogRepository;

    @Autowired
    private BannerQuangCaoRepository bannerQuangCaoRepository;

    @Autowired
    private BannerQuangCaoService bannerQuangCaoService;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    private NhanVien tacGiaTest;

    @BeforeEach
    void setUp() {
        tacGiaTest = nhanVienRepository.findAll().stream().findFirst().orElseGet(() -> {
            NhanVien nv = NhanVien.builder()
                    .maNhanVien("NV_TEST_US30")
                    .hoTen("Nhân Viên Nội Dung Test")
                    .tenDangNhap("admin_noidung_test")
                    .email("noidung_test@simplain.vn")
                    .soDienThoai("0988777666")
                    .matKhauHash("123456")
                    .trangThai(1)
                    .build();
            return nhanVienRepository.save(nv);
        });
    }

    // =========================================================================
    // TIÊU CHÍ 1: CRUD BÀI VIẾT BLOG (SEO FRIENDLY)
    // =========================================================================

    @Test
    @DisplayName("1. Hiển thị danh sách bài viết Blog thành công")
    void testHienThiDanhSachBlog_ThanhCong() throws Exception {
        BaiVietBlog blog = BaiVietBlog.builder()
                .maTacGiaId(tacGiaTest.getId())
                .chuyenMuc("Thời trang nam")
                .tieuDe("Cách Phối Đồ Với Áo Sơ Mi Nam Đẹp Lịch Lãm")
                .duongDanSlug("cach-phoi-do-voi-ao-so-mi-nam-dep-lich-lam")
                .moTaNgan("Hướng dẫn phối áo sơ mi công sở và dạo phố")
                .noiDungHtml("<p>Nội dung chi tiết bài viết blog thời trang...</p>")
                .anhDaiDienUrl("/uploads/blog/so-mi.jpg")
                .ngayXuatBan(LocalDateTime.now())
                .build();
        baiVietBlogRepository.save(blog);

        mockMvc.perform(get("/admin/noi-dung/blog"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/noi-dung/blog/hien-thi"))
                .andExpect(model().attributeExists("trangBlog"))
                .andExpect(model().attributeExists("danhSachChuyenMuc"))
                .andExpect(model().attribute("menuActive", "blog"))
                .andExpect(content().string(containsString("Cách Phối Đồ Với Áo Sơ Mi Nam Đẹp Lịch Lãm")))
                .andExpect(content().string(containsString("cach-phoi-do-voi-ao-so-mi-nam-dep-lich-lam")));
    }

    @Test
    @DisplayName("2. Hiển thị form soạn thảo bài viết Blog mới thành công")
    void testHienThiFormTaoMoiBlog_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/noi-dung/blog/tao-moi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/noi-dung/blog/tao-moi"))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    @DisplayName("3. Tạo mới bài viết Blog thành công với SEO Slug chuẩn")
    void testTaoMoiBlog_ThanhCong() throws Exception {
        mockMvc.perform(post("/admin/noi-dung/blog/tao-moi")
                        .param("chuyenMuc", "Mẹo phối đồ")
                        .param("tieuDe", "5 Bí Quyết Chọn Size Áo Thun Chuẩn Cho Nam")
                        .param("duongDanSlug", "5-bi-quyet-chon-size-ao-thun-chuan-cho-nam")
                        .param("moTaNgan", "Bí quyết chọn size áo thun nam vừa vặn tôn dáng...")
                        .param("noiDungHtml", "<h3>1. Đo vòng ngực</h3><p>Chi tiết cách đo...</p>")
                        .param("anhDaiDienUrl", "https://images.unsplash.com/photo-test")
                        .param("maTacGiaId", tacGiaTest.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/noi-dung/blog"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertTrue(baiVietBlogRepository.existsByDuongDanSlugIgnoreCase("5-bi-quyet-chon-size-ao-thun-chuan-cho-nam"));
    }

    @Test
    @DisplayName("4. Tạo mới bài viết Blog thất bại khi thiếu tiêu đề hoặc nội dung")
    void testTaoMoiBlog_ThatBai_KhiThieuTruongBatBuoc() throws Exception {
        mockMvc.perform(post("/admin/noi-dung/blog/tao-moi")
                        .param("chuyenMuc", "Thời trang")
                        .param("tieuDe", "") // Tiêu đề rỗng
                        .param("duongDanSlug", "bai-viet-loi")
                        .param("noiDungHtml", "")) // Nội dung rỗng
                .andExpect(status().isOk())
                .andExpect(view().name("admin/noi-dung/blog/tao-moi"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("5. Tạo mới bài viết Blog thất bại khi Slug SEO bị trùng lặp")
    void testTaoMoiBlog_ThatBai_KhiTrungSlug() throws Exception {
        BaiVietBlog existing = BaiVietBlog.builder()
                .maTacGiaId(tacGiaTest.getId())
                .chuyenMuc("Tin tức")
                .tieuDe("Bài viết đã có từ trước")
                .duongDanSlug("bai-viet-slug-da-ton-tai")
                .noiDungHtml("<p>Nội dung</p>")
                .build();
        baiVietBlogRepository.save(existing);

        mockMvc.perform(post("/admin/noi-dung/blog/tao-moi")
                        .param("chuyenMuc", "Tin tức")
                        .param("tieuDe", "Bài viết thứ hai cố tình trùng slug")
                        .param("duongDanSlug", "bai-viet-slug-da-ton-tai")
                        .param("noiDungHtml", "<p>Nội dung 2</p>"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/noi-dung/blog/tao-moi"))
                .andExpect(model().attributeExists("thongBaoLoi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("đã tồn tại trong hệ thống")));
    }

    @Test
    @DisplayName("6. Hiển thị form chỉnh sửa bài viết Blog thành công")
    void testHienThiFormChinhSuaBlog_ThanhCong() throws Exception {
        BaiVietBlog blog = BaiVietBlog.builder()
                .maTacGiaId(tacGiaTest.getId())
                .chuyenMuc("Lookbook")
                .tieuDe("Bộ Sưu Tập Thu Đông Mới Nhất 2026")
                .duongDanSlug("bo-suu-tap-thu-dong-2026")
                .noiDungHtml("<p>Nội dung lookbook...</p>")
                .build();
        BaiVietBlog saved = baiVietBlogRepository.save(blog);

        mockMvc.perform(get("/admin/noi-dung/blog/chinh-sua/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/noi-dung/blog/tao-moi"))
                .andExpect(model().attribute("isEdit", true))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attribute("request", hasProperty("tieuDe", is("Bộ Sưu Tập Thu Đông Mới Nhất 2026"))));
    }

    @Test
    @DisplayName("7. Cập nhật bài viết Blog thành công")
    void testCapNhatBlog_ThanhCong() throws Exception {
        BaiVietBlog blog = BaiVietBlog.builder()
                .maTacGiaId(tacGiaTest.getId())
                .chuyenMuc("Phong cách")
                .tieuDe("Tiêu Đề Ban Đầu")
                .duongDanSlug("tieu-de-ban-dau")
                .noiDungHtml("<p>Ban đầu</p>")
                .build();
        BaiVietBlog saved = baiVietBlogRepository.save(blog);

        mockMvc.perform(post("/admin/noi-dung/blog/chinh-sua/" + saved.getId())
                        .param("chuyenMuc", "Phong cách nâng cao")
                        .param("tieuDe", "Tiêu Đề Đã Được Nâng Cấp 2026")
                        .param("duongDanSlug", "tieu-de-da-duoc-nang-cap-2026")
                        .param("moTaNgan", "Mô tả mới cập nhật")
                        .param("noiDungHtml", "<p>Nội dung đã được biên tập lại hoàn chỉnh.</p>"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/noi-dung/blog"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        BaiVietBlog updated = baiVietBlogRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Tiêu Đề Đã Được Nâng Cấp 2026", updated.getTieuDe());
        assertEquals("tieu-de-da-duoc-nang-cap-2026", updated.getDuongDanSlug());
        assertEquals("Phong cách nâng cao", updated.getChuyenMuc());
    }

    @Test
    @DisplayName("8. Xóa bài viết Blog thành công")
    void testXoaBlog_ThanhCong() throws Exception {
        BaiVietBlog blog = BaiVietBlog.builder()
                .maTacGiaId(tacGiaTest.getId())
                .chuyenMuc("Xóa")
                .tieuDe("Bài viết sắp bị xóa")
                .duongDanSlug("bai-viet-sap-bi-xoa")
                .noiDungHtml("<p>Tạm biệt</p>")
                .build();
        BaiVietBlog saved = baiVietBlogRepository.save(blog);

        mockMvc.perform(post("/admin/noi-dung/blog/xoa/" + saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/noi-dung/blog"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertFalse(baiVietBlogRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("9. API tự động tạo slug SEO tiếng Việt không dấu chuẩn xác")
    void testApiTaoSlug_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/noi-dung/blog/api/tao-slug")
                        .param("tieuDe", "Áo Thun Nam Cổ Tròn Đẹp & Lịch Lãm 2026!"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug", is("ao-thun-nam-co-tron-dep-lich-lam-2026")));
    }

    // =========================================================================
    // TIÊU CHÍ 2: CRUD BANNER QUẢNG CÁO TẠI CÁC VỊ TRÍ TRÊN WEB
    // =========================================================================

    @Test
    @DisplayName("10. Hiển thị danh sách Banner Quảng cáo thành công")
    void testHienThiDanhSachBanner_ThanhCong() throws Exception {
        BannerQuangCao banner = BannerQuangCao.builder()
                .tieuDe("Banner Hero Mùa Hè 2026")
                .hinhAnhUrl("https://simplain.vn/images/banner-hero.jpg")
                .duongDanDich("/cua-hang?mua=he")
                .viTriHienThi("HERO_SLIDER")
                .kichHoat(true)
                .build();
        bannerQuangCaoRepository.save(banner);

        mockMvc.perform(get("/admin/noi-dung/banner"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/noi-dung/banner/hien-thi"))
                .andExpect(model().attributeExists("trangBanner"))
                .andExpect(model().attribute("menuActive", "banner"))
                .andExpect(content().string(containsString("Banner Hero Mùa Hè 2026")))
                .andExpect(content().string(containsString("Slide Hero Trang chủ")));
    }

    @Test
    @DisplayName("11. Hiển thị form tạo mới Banner thành công")
    void testHienThiFormTaoMoiBanner_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/noi-dung/banner/tao-moi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/noi-dung/banner/tao-moi"))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    @DisplayName("12. Tạo mới Banner Quảng Cáo tại các vị trí hiển thị chuẩn trên Web")
    void testTaoMoiBanner_ThanhCong_TaiCacViTriWeb() throws Exception {
        // Vị trí 1: HERO_SLIDER
        mockMvc.perform(post("/admin/noi-dung/banner/tao-moi")
                        .param("tieuDe", "Banner Slide Đầu Trang Chủ")
                        .param("viTriHienThi", "HERO_SLIDER")
                        .param("hinhAnhUrl", "https://simplain.vn/banners/hero1.png")
                        .param("duongDanDich", "/khuyen-mai")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/noi-dung/banner"));

        // Vị trí 2: HOME_MIDDLE
        mockMvc.perform(post("/admin/noi-dung/banner/tao-moi")
                        .param("tieuDe", "Banner Giữa Trang Chủ")
                        .param("viTriHienThi", "HOME_MIDDLE")
                        .param("hinhAnhUrl", "https://simplain.vn/banners/middle.png")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection());

        // Vị trí 3: STORE_TOP
        mockMvc.perform(post("/admin/noi-dung/banner/tao-moi")
                        .param("tieuDe", "Banner Đầu Cửa Hàng")
                        .param("viTriHienThi", "STORE_TOP")
                        .param("hinhAnhUrl", "https://simplain.vn/banners/store.png")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection());

        // Vị trí 4: POPUP
        mockMvc.perform(post("/admin/noi-dung/banner/tao-moi")
                        .param("tieuDe", "Popup Ưu Đãi Đầu Trang")
                        .param("viTriHienThi", "POPUP")
                        .param("hinhAnhUrl", "https://simplain.vn/banners/popup.png")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection());

        // Vị trí 5: FOOTER
        mockMvc.perform(post("/admin/noi-dung/banner/tao-moi")
                        .param("tieuDe", "Banner Chân Trang Cuối")
                        .param("viTriHienThi", "FOOTER")
                        .param("hinhAnhUrl", "https://simplain.vn/banners/footer.png")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection());

        List<BannerQuangCao> heroBanners = bannerQuangCaoService.layBannerTheoViTri("HERO_SLIDER");
        assertFalse(heroBanners.isEmpty());
        assertTrue(heroBanners.stream().anyMatch(b -> b.getTieuDe().equals("Banner Slide Đầu Trang Chủ")));
    }

    @Test
    @DisplayName("13. Tạo mới Banner thất bại khi thiếu hình ảnh hoặc tiêu đề")
    void testTaoMoiBanner_ThatBai_KhiThieuDuLieu() throws Exception {
        // Thiếu hình ảnh URL
        mockMvc.perform(post("/admin/noi-dung/banner/tao-moi")
                        .param("tieuDe", "Banner Không Có Hình Ảnh")
                        .param("viTriHienThi", "HERO_SLIDER")
                        .param("hinhAnhUrl", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/noi-dung/banner/tao-moi"))
                .andExpect(model().attributeExists("thongBaoLoi"));
    }

    @Test
    @DisplayName("14. Cập nhật Banner Quảng Cáo thành công")
    void testCapNhatBanner_ThanhCong() throws Exception {
        BannerQuangCao banner = BannerQuangCao.builder()
                .tieuDe("Banner Ban Đầu")
                .hinhAnhUrl("https://simplain.vn/old.png")
                .viTriHienThi("STORE_TOP")
                .kichHoat(true)
                .build();
        BannerQuangCao saved = bannerQuangCaoRepository.save(banner);

        mockMvc.perform(post("/admin/noi-dung/banner/chinh-sua/" + saved.getId())
                        .param("tieuDe", "Banner Sau Khi Đổi Nội Dung")
                        .param("viTriHienThi", "POPUP")
                        .param("hinhAnhUrl", "https://simplain.vn/new.png")
                        .param("duongDanDich", "/san-pham-hot")
                        .param("kichHoat", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/noi-dung/banner"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        BannerQuangCao updated = bannerQuangCaoRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Banner Sau Khi Đổi Nội Dung", updated.getTieuDe());
        assertEquals("POPUP", updated.getViTriHienThi());
        assertFalse(updated.getKichHoat());
    }

    @Test
    @DisplayName("15. Bật / tắt chuyển đổi trạng thái kích hoạt Banner")
    void testDoiTrangThaiKichHoatBanner_ThanhCong() throws Exception {
        BannerQuangCao banner = BannerQuangCao.builder()
                .tieuDe("Banner Toggle Trạng Thái")
                .hinhAnhUrl("https://simplain.vn/toggle.png")
                .viTriHienThi("HERO_SLIDER")
                .kichHoat(true)
                .build();
        BannerQuangCao saved = bannerQuangCaoRepository.save(banner);

        // Bật -> Tắt
        mockMvc.perform(post("/admin/noi-dung/banner/doi-trang-thai/" + saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/noi-dung/banner"));

        BannerQuangCao afterFirstToggle = bannerQuangCaoRepository.findById(saved.getId()).orElseThrow();
        assertFalse(afterFirstToggle.getKichHoat());

        // Tắt -> Bật lại
        mockMvc.perform(post("/admin/noi-dung/banner/doi-trang-thai/" + saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/noi-dung/banner"));

        BannerQuangCao afterSecondToggle = bannerQuangCaoRepository.findById(saved.getId()).orElseThrow();
        assertTrue(afterSecondToggle.getKichHoat());
    }

    @Test
    @DisplayName("16. Xóa Banner Quảng Cáo thành công")
    void testXoaBanner_ThanhCong() throws Exception {
        BannerQuangCao banner = BannerQuangCao.builder()
                .tieuDe("Banner Sắp Xóa")
                .hinhAnhUrl("https://simplain.vn/delete.png")
                .viTriHienThi("FOOTER")
                .kichHoat(true)
                .build();
        BannerQuangCao saved = bannerQuangCaoRepository.save(banner);

        mockMvc.perform(post("/admin/noi-dung/banner/xoa/" + saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/noi-dung/banner"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertFalse(bannerQuangCaoRepository.findById(saved.getId()).isPresent());
    }
}
