package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.BaiVietBlog;
import vn.fpoly.simplain.entity.BannerQuangCao;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.repository.BaiVietBlogRepository;
import vn.fpoly.simplain.repository.BannerQuangCaoRepository;
import vn.fpoly.simplain.repository.NhanVienRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử Web MVC tích hợp chức năng Khách Hàng Xem Blog & Slug SEO + Hiển thị Banner Web (US30).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Kiểm thử Khách Hàng Xem Blog Theo Slug SEO & Banner Web (US30)")
class BlogKhachHangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BaiVietBlogRepository baiVietBlogRepository;

    @Autowired
    private BannerQuangCaoRepository bannerQuangCaoRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    private BaiVietBlog blogTest;

    @BeforeEach
    void setUp() {
        NhanVien tacGia = nhanVienRepository.findAll().stream().findFirst().orElseGet(() -> {
            NhanVien nv = NhanVien.builder()
                    .maNhanVien("NV_TEST_BLOG_CLIENT")
                    .hoTen("Biên tập viên Simplain")
                    .tenDangNhap("editor_blog")
                    .email("editor@simplain.vn")
                    .matKhauHash("123456")
                    .trangThai(1)
                    .build();
            return nhanVienRepository.save(nv);
        });

        blogTest = BaiVietBlog.builder()
                .maTacGiaId(tacGia.getId())
                .chuyenMuc("Mẹo phối đồ")
                .tieuDe("Cách Phối Đồ Với Áo Polo Nam Trẻ Trung Năng Động")
                .duongDanSlug("cach-phoi-do-voi-ao-polo-nam-tre-trung-nang-dong")
                .moTaNgan("Chia sẻ bí quyết phối đồ với áo polo nam chuẩn phong thái thời trang...")
                .noiDungHtml("<h2>1. Phối cùng quần jean</h2><p>Polo phối cùng jean tạo cảm giác trẻ trung...</p>")
                .anhDaiDienUrl("/uploads/blog/polo.jpg")
                .ngayXuatBan(LocalDateTime.now())
                .build();
        blogTest = baiVietBlogRepository.save(blogTest);
    }

    @Test
    @DisplayName("1. Khách hàng truy cập danh sách Blog /blog thành công")
    void testHienThiDanhSachBlog_ThanhCong() throws Exception {
        mockMvc.perform(get("/blog"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/blog/danh-sach"))
                .andExpect(model().attributeExists("trangBlog"))
                .andExpect(model().attributeExists("danhSachChuyenMuc"))
                .andExpect(content().string(containsString("Cách Phối Đồ Với Áo Polo Nam Trẻ Trung Năng Động")))
                .andExpect(content().string(containsString("cach-phoi-do-voi-ao-polo-nam-tre-trung-nang-dong")));
    }

    @Test
    @DisplayName("2. Khách hàng xem chi tiết Blog theo URL Slug SEO /blog/{slug} thành công")
    void testHienThiChiTietBlogTheoSlug_ThanhCong() throws Exception {
        mockMvc.perform(get("/blog/" + blogTest.getDuongDanSlug()))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/blog/chi-tiet"))
                .andExpect(model().attributeExists("blog"))
                .andExpect(model().attribute("blog", hasProperty("duongDanSlug", is("cach-phoi-do-voi-ao-polo-nam-tre-trung-nang-dong"))))
                .andExpect(content().string(containsString("Polo phối cùng jean tạo cảm giác trẻ trung")));
    }

    @Test
    @DisplayName("3. Khách hàng xem chi tiết với slug không tồn tại sẽ chuyển hướng về /blog")
    void testChiTietBlog_SlugKhongTonTai_ChuyenHuong() throws Exception {
        mockMvc.perform(get("/blog/slug-khong-ton-tai-trong-he-thong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog"));
    }

    @Test
    @DisplayName("4. Trang Cửa hàng /cua-hang hiển thị Banner Quảng Cáo (US30) thành công")
    void testCuaHang_HienThiBannerQuangCao() throws Exception {
        BannerQuangCao banner = BannerQuangCao.builder()
                .tieuDe("Banner Hero Mùa Đông 2026")
                .hinhAnhUrl("https://simplain.vn/banners/winter.jpg")
                .duongDanDich("/cua-hang?bst=mua-dong")
                .viTriHienThi("HERO_SLIDER")
                .kichHoat(true)
                .build();
        bannerQuangCaoRepository.save(banner);

        mockMvc.perform(get("/cua-hang"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("danhSachBannerQuangCao"))
                .andExpect(content().string(containsString("Banner Hero Mùa Đông 2026")))
                .andExpect(content().string(containsString("/cua-hang?bst=mua-dong")));
    }
}
