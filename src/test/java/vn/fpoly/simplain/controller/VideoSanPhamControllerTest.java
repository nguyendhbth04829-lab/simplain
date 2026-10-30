package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.entity.BoSuuTap;
import vn.fpoly.simplain.entity.DanhMuc;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.entity.VideoSanPham;
import vn.fpoly.simplain.repository.BoSuuTapRepository;
import vn.fpoly.simplain.repository.DanhMucRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;
import vn.fpoly.simplain.repository.VideoSanPhamRepository;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động Quản lý Video quảng cáo sản phẩm (chọn video từ máy hoặc nhập URL).
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Quản lý Video quảng cáo sản phẩm (File Upload & URL)")
class VideoSanPhamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VideoSanPhamRepository videoSanPhamRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private BoSuuTapRepository boSuuTapRepository;

    private static final String MA_SP_VID_1 = "SP-VID-TEST-01";
    private static final String MA_SP_VID_2 = "SP-VID-TEST-02";
    private static final String SLUG_SP_VID_1 = "sp-video-test-01";
    private static final String SLUG_SP_VID_2 = "sp-video-test-02";
    private static final String SLUG_DM = "dm-video-test";
    private static final String SLUG_BST = "bst-video-test";

    private SanPham sanPhamTest1;
    private SanPham sanPhamTest2;
    private DanhMuc dmTest;
    private BoSuuTap bstTest;

    @BeforeEach
    void setUp() {
        donDep();

        dmTest = danhMucRepository.save(DanhMuc.builder()
                .tenDanhMuc("Danh Mục Video Test")
                .duongDanSlug(SLUG_DM)
                .kichHoat(true)
                .build());

        bstTest = boSuuTapRepository.save(BoSuuTap.builder()
                .tenBoSuuTap("Bộ Sưu Tập Video Test")
                .duongDanSlug(SLUG_BST)
                .kichHoat(true)
                .build());

        sanPhamTest1 = sanPhamRepository.save(SanPham.builder()
                .maSanPham(MA_SP_VID_1)
                .tenSanPham("Sản phẩm Video Test 01")
                .duongDanSlug(SLUG_SP_VID_1)
                .chatLieu("100% Cotton Compact")
                .formDang("Regular Fit")
                .moTaChiTiet("Mô tả chi tiết sản phẩm video test 01")
                .giaCoBan(BigDecimal.valueOf(299000))
                .maDanhMucId(dmTest.getId())
                .maBoSuuTapId(bstTest.getId())
                .kichHoat(true)
                .build());

        sanPhamTest2 = sanPhamRepository.save(SanPham.builder()
                .maSanPham(MA_SP_VID_2)
                .tenSanPham("Sản phẩm Video Test 02")
                .duongDanSlug(SLUG_SP_VID_2)
                .chatLieu("Vải Sorona Excool")
                .formDang("Slim Fit")
                .moTaChiTiet("Mô tả chi tiết sản phẩm video test 02")
                .giaCoBan(BigDecimal.valueOf(399000))
                .maDanhMucId(dmTest.getId())
                .maBoSuuTapId(bstTest.getId())
                .kichHoat(true)
                .build());
    }

    @AfterEach
    void tearDown() {
        donDep();
    }

    private void donDep() {
        videoSanPhamRepository.findAll().forEach(v -> {
            if (v.getTieuDe() != null && v.getTieuDe().contains("Video Test")) {
                videoSanPhamRepository.delete(v);
            }
        });

        sanPhamRepository.findByMaSanPhamIgnoreCase(MA_SP_VID_1).ifPresent(sp -> {
            videoSanPhamRepository.findByMaSanPhamId(sp.getId()).ifPresent(videoSanPhamRepository::delete);
            sanPhamRepository.delete(sp);
        });
        sanPhamRepository.findByMaSanPhamIgnoreCase(MA_SP_VID_2).ifPresent(sp -> {
            videoSanPhamRepository.findByMaSanPhamId(sp.getId()).ifPresent(videoSanPhamRepository::delete);
            sanPhamRepository.delete(sp);
        });

        danhMucRepository.findByDuongDanSlug(SLUG_DM).ifPresent(danhMucRepository::delete);
        boSuuTapRepository.findByDuongDanSlug(SLUG_BST).ifPresent(boSuuTapRepository::delete);
    }

    @Test
    @DisplayName("Hiển thị trang quản lý video sản phẩm thành công")
    void hienThi_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/video-san-pham/hien-thi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/video-san-pham/hien-thi"))
                .andExpect(model().attributeExists("listVideo"))
                .andExpect(model().attributeExists("danhSachSanPham"));
    }

    @Test
    @DisplayName("Thêm video quảng cáo bằng cách nhập đường dẫn URL thành công")
    void themVideo_BangUrl_ThanhCong() throws Exception {
        mockMvc.perform(post("/admin/video-san-pham/add")
                        .param("maSanPhamId", String.valueOf(sanPhamTest1.getId()))
                        .param("tieuDe", "Video Test Qua URL YouTube")
                        .param("videoUrl", "https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                        .param("moTa", "Video mô tả review")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/video-san-pham/hien-thi"))
                .andExpect(flash().attribute("thongBaoThanhCong", containsString("thành công")));

        assertTrue(videoSanPhamRepository.existsByMaSanPhamId(sanPhamTest1.getId()));
    }

    @Test
    @DisplayName("Thêm video quảng cáo bằng cách tải file video từ máy tính thành công")
    void themVideo_BangFileMayTinh_ThanhCong() throws Exception {
        MockMultipartFile fileVideo = new MockMultipartFile(
                "fileVideo",
                "test_polo_review.mp4",
                "video/mp4",
                "FAKE_VIDEO_CONTENT_BYTES_SIMPLAIN".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/admin/video-san-pham/add")
                        .file(fileVideo)
                        .param("maSanPhamId", String.valueOf(sanPhamTest1.getId()))
                        .param("tieuDe", "Video Test Tải File Máy Tính")
                        .param("moTa", "Mô tả video upload")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/video-san-pham/hien-thi"))
                .andExpect(flash().attribute("thongBaoThanhCong", containsString("thành công")));

        VideoSanPham v = videoSanPhamRepository.findByMaSanPhamId(sanPhamTest1.getId()).orElse(null);
        assertNotNull(v);
        assertTrue(v.getVideoUrl().startsWith("/uploads/video_"));
        assertTrue(v.getVideoUrl().endsWith(".mp4"));
    }

    @Test
    @DisplayName("Cập nhật video quảng cáo: thay đổi URL hoặc tải file video mới từ máy")
    void suaVideo_CapNhatFileVaUrl_ThanhCong() throws Exception {
        // 1. Tạo video ban đầu bằng URL
        VideoSanPham initial = videoSanPhamRepository.save(VideoSanPham.builder()
                .maSanPhamId(sanPhamTest1.getId())
                .tieuDe("Video Test Ban Đầu")
                .videoUrl("https://domain.com/video_cu.mp4")
                .kichHoat(true)
                .build());

        // 2. Cập nhật bằng cách upload file mới từ máy tính
        MockMultipartFile fileMoi = new MockMultipartFile(
                "fileVideo",
                "video_moi_update.webm",
                "video/webm",
                "NEW_FILE_BYTES".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/admin/video-san-pham/update")
                        .file(fileMoi)
                        .param("id", String.valueOf(initial.getId()))
                        .param("maSanPhamId", String.valueOf(sanPhamTest1.getId()))
                        .param("tieuDe", "Video Test Đã Cập Nhật File")
                        .param("moTa", "Mô tả mới")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/video-san-pham/hien-thi"))
                .andExpect(flash().attribute("thongBaoThanhCong", containsString("thành công")));

        VideoSanPham updated = videoSanPhamRepository.findById(initial.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals("Video Test Đã Cập Nhật File", updated.getTieuDe());
        assertTrue(updated.getVideoUrl().startsWith("/uploads/video_"));
    }

    @Test
    @DisplayName("Báo lỗi khi không chọn video từ máy và cũng không nhập đường dẫn URL")
    void themVideo_ThieuFileVaUrl_BaoLoi() throws Exception {
        mockMvc.perform(post("/admin/video-san-pham/add")
                        .param("maSanPhamId", String.valueOf(sanPhamTest1.getId()))
                        .param("tieuDe", "Video Test Không Có Nguồn")
                        .param("videoUrl", "")
                        .param("kichHoat", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/video-san-pham/hien-thi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("Vui lòng chọn video từ máy tính hoặc nhập đường dẫn video")));
    }

    @Test
    @DisplayName("Đổi trạng thái hiển thị (Kích hoạt / Tạm ngưng) và Xóa video quảng cáo thành công")
    void doiTrangThaiVaXoa_ThanhCong() throws Exception {
        VideoSanPham v = videoSanPhamRepository.save(VideoSanPham.builder()
                .maSanPhamId(sanPhamTest1.getId())
                .tieuDe("Video Test Đổi Trạng Thái")
                .videoUrl("https://youtube.com/watch?v=123")
                .kichHoat(true)
                .build());

        // Đổi trạng thái sang Tạm ngưng
        mockMvc.perform(get("/admin/video-san-pham/doi-trang-thai").param("id", String.valueOf(v.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/video-san-pham/hien-thi"));

        VideoSanPham vSauDoi = videoSanPhamRepository.findById(v.getId()).orElse(null);
        assertNotNull(vSauDoi);
        assertFalse(vSauDoi.getKichHoat());

        // Xóa video
        mockMvc.perform(get("/admin/video-san-pham/delete").param("id", String.valueOf(v.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/video-san-pham/hien-thi"));

        assertFalse(videoSanPhamRepository.existsById(v.getId()));
    }
}
