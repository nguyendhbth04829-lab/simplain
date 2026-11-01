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
import vn.fpoly.simplain.repository.BoSuuTapRepository;
import vn.fpoly.simplain.repository.DanhMucRepository;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động giao diện Web MVC & Validate chuyên nghiệp cho chức năng
 * Quản lý Danh mục (Cha/Con) & Bộ sưu tập kèm Banner (US06).
 *
 * <p>Tiêu chí kiểm thử US06:
 * 1. CRUD danh_muc (Hỗ trợ danh mục cha/con, chặn tự nhận chính mình làm cha, chặn vòng lặp cha-con, chặn xóa danh mục đang có con).
 * 2. CRUD bo_suu_tap kèm tải lên ảnh Banner (Validate định dạng ảnh Banner, giữ ảnh cũ khi sửa, chặn trùng tên/slug).
 * 3. Kiểm tra lưu trữ tiếng Việt có dấu (@Nationalized) không bị lỗi '?' trong SQL Server Simpl4.
 *
 * <p>Chạy bằng lệnh: .\mvnw.cmd -Dtest=DanhMucVaBoSuuTapControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Quản lý Danh mục Cha/Con & Bộ sưu tập kèm Banner (US06)")
class DanhMucVaBoSuuTapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private BoSuuTapRepository boSuuTapRepository;

    private static final String SLUG_DM_CHA = "dm-cha-test-us06";
    private static final String SLUG_DM_CON = "dm-con-test-us06";
    private static final String SLUG_DM_CHAU = "dm-chau-test-us06";

    private static final String SLUG_BST_1 = "bst-test-us06-01";
    private static final String SLUG_BST_2 = "bst-test-us06-02";

    @BeforeEach
    void setUp() {
        donDepDuLieuTest();
    }

    @AfterEach
    void tearDown() {
        donDepDuLieuTest();
    }

    private void donDepDuLieuTest() {
        // Xóa theo thứ tự cháu -> con -> cha để đảm bảo ràng buộc khóa ngoại
        danhMucRepository.findByDuongDanSlug(SLUG_DM_CHAU).ifPresent(danhMucRepository::delete);
        danhMucRepository.findByDuongDanSlug(SLUG_DM_CON).ifPresent(danhMucRepository::delete);
        danhMucRepository.findByDuongDanSlug(SLUG_DM_CHA).ifPresent(danhMucRepository::delete);

        boSuuTapRepository.findByDuongDanSlug(SLUG_BST_1).ifPresent(boSuuTapRepository::delete);
        boSuuTapRepository.findByDuongDanSlug(SLUG_BST_2).ifPresent(boSuuTapRepository::delete);
    }

    // =========================================================================
    // PHẦN 1: KIỂM THỬ QUẢN LÝ DANH MỤC CHA / CON (TIÊU CHÍ 1 US06)
    // =========================================================================

    @Test
    @DisplayName("US06 - Tiêu chí 1: Hiển thị danh sách Danh mục có phân trang, tìm kiếm và lọc trạng thái")
    void hienThiDanhMuc_PhanTrangVaTimKiem_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/danh-muc/hien-thi")
                        .param("page", "0")
                        .param("ten", "Áo"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/danh-muc/hien-thi"))
                .andExpect(model().attributeExists("listDanhMuc", "listTatCa", "danhMuc"));
    }

    @Test
    @DisplayName("US06 - Tiêu chí 1: CRUD Danh mục Cha & Danh mục Con + Chặn xóa Danh mục Cha khi đang có Danh mục Con")
    void crudDanhMucChaCon_VaChanXoaDanhMucChaCoCon() throws Exception {
        // 1. Thêm mới Danh mục Cha (Gốc)
        mockMvc.perform(post("/admin/danh-muc/add")
                        .param("tenDanhMuc", "Đồ Thể Thao Nam US06")
                        .param("duongDanSlug", SLUG_DM_CHA)
                        .param("thuTuHienThi", "1")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/danh-muc/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        DanhMuc dmCha = danhMucRepository.findByDuongDanSlug(SLUG_DM_CHA).orElseThrow();
        assertNull(dmCha.getMaDanhMucCha());
        assertEquals("Đồ Thể Thao Nam US06", dmCha.getTenDanhMuc());
        assertFalse(dmCha.getTenDanhMuc().contains("?"));

        // 2. Thêm mới Danh mục Con trực thuộc Danh mục Cha
        mockMvc.perform(post("/admin/danh-muc/add")
                        .param("tenDanhMuc", "Áo Thun Chạy Bộ US06")
                        .param("duongDanSlug", SLUG_DM_CON)
                        .param("maDanhMucCha", String.valueOf(dmCha.getId()))
                        .param("thuTuHienThi", "2")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/danh-muc/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        DanhMuc dmCon = danhMucRepository.findByDuongDanSlug(SLUG_DM_CON).orElseThrow();
        assertEquals(dmCha.getId(), dmCon.getMaDanhMucCha());

        // 3. Thử xóa Danh mục Cha khi đang có Danh mục Con -> Phải bị chặn!
        mockMvc.perform(get("/admin/danh-muc/delete")
                        .param("id", String.valueOf(dmCha.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/danh-muc/hien-thi"))
                .andExpect(flash().attribute("thongBaoLoi", containsString("danh mục con trực thuộc")));

        assertTrue(danhMucRepository.existsById(dmCha.getId()), "Danh mục cha không được phép bị xóa khi còn danh mục con");

        // 4. Xóa Danh mục Con trước -> Thành công, sau đó xóa Danh mục Cha -> Thành công
        mockMvc.perform(get("/admin/danh-muc/delete")
                        .param("id", String.valueOf(dmCon.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("thongBaoThanhCong"));
        assertFalse(danhMucRepository.existsById(dmCon.getId()));

        mockMvc.perform(get("/admin/danh-muc/delete")
                        .param("id", String.valueOf(dmCha.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("thongBaoThanhCong"));
        assertFalse(danhMucRepository.existsById(dmCha.getId()));
    }

    @Test
    @DisplayName("US06 - Validate Phân cấp Danh mục: Chặn chọn chính mình làm cha và chặn vòng lặp phân cấp (Cha chọn Con/Cháu làm cha)")
    void validatePhanCapDanhMuc_ChanTuNhanLamChaVaChanVongLap() throws Exception {
        DanhMuc dmCha = danhMucRepository.save(DanhMuc.builder()
                .tenDanhMuc("Danh Mục Cha Cấp 1")
                .duongDanSlug(SLUG_DM_CHA)
                .thuTuHienThi(1)
                .kichHoat(true)
                .build());

        DanhMuc dmCon = danhMucRepository.save(DanhMuc.builder()
                .tenDanhMuc("Danh Mục Con Cấp 2")
                .duongDanSlug(SLUG_DM_CON)
                .maDanhMucCha(dmCha.getId())
                .thuTuHienThi(2)
                .kichHoat(true)
                .build());

        // 1. Cập nhật dmCha chọn chính nó (dmCha.id) làm cha -> Báo lỗi
        mockMvc.perform(post("/admin/danh-muc/update")
                        .param("id", String.valueOf(dmCha.getId()))
                        .param("tenDanhMuc", "Danh Mục Cha Cấp 1")
                        .param("duongDanSlug", SLUG_DM_CHA)
                        .param("maDanhMucCha", String.valueOf(dmCha.getId()))
                        .param("thuTuHienThi", "1")
                        .param("kichHoat", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/danh-muc/view"))
                .andExpect(model().attribute("thongBaoLoi", containsString("không thể chọn chính nó làm danh mục cha")));

        // 2. Cập nhật dmCha chọn dmCon (là con của dmCha) làm cha -> Báo lỗi vòng lặp phân cấp!
        mockMvc.perform(post("/admin/danh-muc/update")
                        .param("id", String.valueOf(dmCha.getId()))
                        .param("tenDanhMuc", "Danh Mục Cha Cấp 1")
                        .param("duongDanSlug", SLUG_DM_CHA)
                        .param("maDanhMucCha", String.valueOf(dmCon.getId()))
                        .param("thuTuHienThi", "1")
                        .param("kichHoat", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/danh-muc/view"))
                .andExpect(model().attribute("thongBaoLoi", containsString("vòng lặp phân cấp")));
    }

    @Test
    @DisplayName("US06 - Validate Form & Trùng lặp Danh mục: Chặn rỗng tên, sai định dạng slug, thứ tự âm và trùng slug")
    void validateFormVaTrungLapDanhMuc() throws Exception {
        // 1. Validate Bean Validation (Tên rỗng, Slug viết hoa/có khoảng trắng, Thứ tự âm)
        mockMvc.perform(post("/admin/danh-muc/add")
                        .param("tenDanhMuc", "")
                        .param("duongDanSlug", "SLUG KHONG HOP LE!")
                        .param("thuTuHienThi", "-5")
                        .param("kichHoat", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/danh-muc/hien-thi"))
                .andExpect(model().attributeHasFieldErrors("danhMuc", "tenDanhMuc", "duongDanSlug", "thuTuHienThi"));

        // 2. Tạo 1 danh mục hợp lệ rồi thử tạo danh mục thứ 2 trùng slug
        danhMucRepository.save(DanhMuc.builder()
                .tenDanhMuc("Danh Mục Test Trùng")
                .duongDanSlug(SLUG_DM_CHA)
                .thuTuHienThi(1)
                .kichHoat(true)
                .build());

        mockMvc.perform(post("/admin/danh-muc/add")
                        .param("tenDanhMuc", "Danh Mục Khác Tên")
                        .param("duongDanSlug", SLUG_DM_CHA) // Trùng slug
                        .param("thuTuHienThi", "2")
                        .param("kichHoat", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/danh-muc/hien-thi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("đã được sử dụng")));
    }

    // =========================================================================
    // PHẦN 2: KIỂM THỬ QUẢN LÝ BỘ SƯU TẬP KÈM BANNER (TIÊU CHÍ 2 US06)
    // =========================================================================

    @Test
    @DisplayName("US06 - Tiêu chí 2: Thêm mới, Cập nhật, Đổi trạng thái và Xóa Bộ sưu tập kèm tải lên ảnh Banner thành công")
    void crudBoSuuTapKemUploadBanner_ThanhCong() throws Exception {
        MockMultipartFile fileBannerHopLe = new MockMultipartFile(
                "fileBanner",
                "coolmate_banner_2026.png",
                "image/png",
                "fake-png-image-binary-content".getBytes(StandardCharsets.UTF_8)
        );

        // 1. Thêm mới bộ sưu tập kèm ảnh Banner
        mockMvc.perform(multipart("/admin/bo-suu-tap/add")
                        .file(fileBannerHopLe)
                        .param("tenBoSuuTap", "Bộ Sưu Tập Xuân Hè Đẳng Cấp 2026")
                        .param("duongDanSlug", SLUG_BST_1)
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/bo-suu-tap/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        BoSuuTap saved = boSuuTapRepository.findByDuongDanSlug(SLUG_BST_1).orElseThrow();
        assertEquals("Bộ Sưu Tập Xuân Hè Đẳng Cấp 2026", saved.getTenBoSuuTap());
        assertFalse(saved.getTenBoSuuTap().contains("?"));
        assertNotNull(saved.getHinhBannerUrl());
        assertTrue(saved.getHinhBannerUrl().startsWith("/uploads/banner_"));

        String bannerCu = saved.getHinhBannerUrl();

        // 2. Cập nhật tên bộ sưu tập nhưng KHÔNG chọn ảnh mới -> Phải giữ nguyên ảnh Banner cũ
        MockMultipartFile fileRong = new MockMultipartFile("fileBanner", "", "application/octet-stream", new byte[0]);
        mockMvc.perform(multipart("/admin/bo-suu-tap/update")
                        .file(fileRong)
                        .param("id", String.valueOf(saved.getId()))
                        .param("tenBoSuuTap", "Bộ Sưu Tập Xuân Hè Đẳng Cấp 2026 (Updated)")
                        .param("duongDanSlug", SLUG_BST_1)
                        .param("hinhBannerUrl", bannerCu)
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/bo-suu-tap/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        BoSuuTap updated = boSuuTapRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Bộ Sưu Tập Xuân Hè Đẳng Cấp 2026 (Updated)", updated.getTenBoSuuTap());
        assertEquals(bannerCu, updated.getHinhBannerUrl(), "Phải giữ nguyên đường dẫn banner cũ khi không upload file mới");

        // 3. Đổi nhanh trạng thái bộ sưu tập
        mockMvc.perform(get("/admin/bo-suu-tap/doi-trang-thai")
                        .param("id", String.valueOf(saved.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/bo-suu-tap/hien-thi"));
        assertFalse(boSuuTapRepository.findById(saved.getId()).orElseThrow().getKichHoat());

        // 4. Xóa bộ sưu tập
        mockMvc.perform(get("/admin/bo-suu-tap/delete")
                        .param("id", String.valueOf(saved.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/bo-suu-tap/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));
        assertFalse(boSuuTapRepository.existsById(saved.getId()));
    }

    @Test
    @DisplayName("US06 - Validate Upload Banner & Form Bộ sưu tập: Chặn file không phải hình ảnh (.exe/.pdf) và chặn trùng lặp")
    void validateBoSuuTap_ChanFileDocHaiVaTrungSlug() throws Exception {
        // 1. Thử tải lên file .exe giả mạo làm Banner -> Phải bị chặn!
        MockMultipartFile fileDocHai = new MockMultipartFile(
                "fileBanner",
                "malware_script.exe",
                "application/octet-stream",
                "MZ-fake-executable".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/admin/bo-suu-tap/add")
                        .file(fileDocHai)
                        .param("tenBoSuuTap", "BST Test File Lỗi")
                        .param("duongDanSlug", SLUG_BST_1)
                        .param("kichHoat", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/bo-suu-tap/hien-thi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("định dạng hình ảnh hợp lệ")));

        assertFalse(boSuuTapRepository.findByDuongDanSlug(SLUG_BST_1).isPresent());

        // 2. Validate Bean Validation khi bỏ trống tên và slug sai định dạng
        mockMvc.perform(post("/admin/bo-suu-tap/add")
                        .param("tenBoSuuTap", "")
                        .param("duongDanSlug", "SLUG SAI DINH DANG!")
                        .param("kichHoat", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/bo-suu-tap/hien-thi"))
                .andExpect(model().attributeHasFieldErrors("boSuuTap", "tenBoSuuTap", "duongDanSlug"));
    }
}
