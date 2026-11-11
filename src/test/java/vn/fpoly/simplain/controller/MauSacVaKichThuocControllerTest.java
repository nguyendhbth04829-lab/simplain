package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.entity.KichThuoc;
import vn.fpoly.simplain.entity.MauSac;
import vn.fpoly.simplain.repository.KichThuocRepository;
import vn.fpoly.simplain.repository.MauSacRepository;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động giao diện Web MVC & Validate chuyên nghiệp cho chức năng
 * Quản lý Màu sắc (Hex/Mã màu) & Kích thước (S, M, L...) (US07).
 *
 * <p>Tiêu chí kiểm thử US07:
 * 1. Thêm / Sửa / Xóa dữ liệu mau_sac (Hex/Mã màu & Tên màu tiếng Việt).
 * 2. Thêm / Sửa / Xóa dữ liệu kich_thuoc (S, M, L, XL...).
 * 3. Validate định dạng Hex, chuẩn hóa mã viết hoa, chặn trùng lặp mã/tên và bảo vệ toàn vẹn dữ liệu khi xóa.
 *
 * <p>Chạy bằng lệnh: .\mvnw.cmd -Dtest=MauSacVaKichThuocControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Quản lý Màu sắc & Kích thước (US07)")
class MauSacVaKichThuocControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichThuocRepository kichThuocRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TEST_HEX_1 = "#0F766E";
    private static final String TEST_HEX_2 = "#9333EA";
    private static final String TEST_SIZE_1 = "4XL_TEST";
    private static final String TEST_SIZE_2 = "5XL_TEST";

    @BeforeEach
    void setUp() {
        donDepDuLieuTest();
    }

    @AfterEach
    void tearDown() {
        donDepDuLieuTest();
    }

    private void donDepDuLieuTest() {
        mauSacRepository.findByMaMauIgnoreCase(TEST_HEX_1).ifPresent(mauSacRepository::delete);
        mauSacRepository.findByMaMauIgnoreCase(TEST_HEX_2).ifPresent(mauSacRepository::delete);
        kichThuocRepository.findByMaKichThuocIgnoreCase(TEST_SIZE_1).ifPresent(kichThuocRepository::delete);
        kichThuocRepository.findByMaKichThuocIgnoreCase(TEST_SIZE_2).ifPresent(kichThuocRepository::delete);
    }

    // =========================================================================
    // PHẦN 1: KIỂM THỬ QUẢN LÝ MÀU SẮC [mau_sac] (HEX / MÃ MÀU)
    // =========================================================================

    @Test
    @DisplayName("US07 - Màu sắc: Hiển thị danh sách màu sắc có phân trang và tìm kiếm theo từ khóa")
    void hienThiMauSac_PhanTrangVaTimKiem_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/mau-sac/hien-thi")
                        .param("page", "0")
                        .param("tuKhoa", "Đen"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/mau-sac/hien-thi"))
                .andExpect(model().attributeExists("listMauSac", "mauSac", "tongMauSac", "tongKichThuoc"));
    }

    @Test
    @DisplayName("US07 - Màu sắc: Thêm mới, Chuẩn hóa Hex in hoa, Cập nhật và Xóa màu sắc thành công")
    void crudMauSac_ThanhCong() throws Exception {
        // 1. Thêm mới với mã Hex chữ thường (#0f766e) -> Tự động chuẩn hóa thành #0F766E
        mockMvc.perform(post("/admin/mau-sac/add")
                        .param("maMau", "#0f766e")
                        .param("tenMau", "Xanh Ngọc Bích Đậm"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/mau-sac/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        MauSac saved = mauSacRepository.findByMaMauIgnoreCase(TEST_HEX_1).orElseThrow();
        assertEquals(TEST_HEX_1, saved.getMaMau());
        assertEquals("Xanh Ngọc Bích Đậm", saved.getTenMau());
        assertFalse(saved.getTenMau().contains("?"));

        // 2. Cập nhật tên màu và mã màu sang #9333EA
        mockMvc.perform(post("/admin/mau-sac/update")
                        .param("id", String.valueOf(saved.getId()))
                        .param("maMau", TEST_HEX_2)
                        .param("tenMau", "Tím Hoàng Gia Mới"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/mau-sac/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        MauSac updated = mauSacRepository.findById(saved.getId()).orElseThrow();
        assertEquals(TEST_HEX_2, updated.getMaMau());
        assertEquals("Tím Hoàng Gia Mới", updated.getTenMau());

        // 3. Xóa màu sắc
        mockMvc.perform(get("/admin/mau-sac/delete")
                        .param("id", String.valueOf(saved.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/mau-sac/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertFalse(mauSacRepository.existsById(saved.getId()));
    }

    @Test
    @DisplayName("US07 - Validate Màu sắc: Chặn mã Hex sai định dạng, chặn rỗng tên và chặn trùng lặp mã màu / tên màu")
    void validateMauSac_SaiHexVaTrungLap() throws Exception {
        // 1. Sai định dạng mã Hex (#GGGGGG) và bỏ trống tên màu
        mockMvc.perform(post("/admin/mau-sac/add")
                        .param("maMau", "#GGGGGG")
                        .param("tenMau", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/mau-sac/hien-thi"))
                .andExpect(model().attributeHasFieldErrors("mauSac", "maMau", "tenMau"));

        // 2. Tạo 1 màu hợp lệ rồi thử thêm trùng mã Hex hoặc trùng tên màu
        mauSacRepository.save(MauSac.builder()
                .maMau(TEST_HEX_1)
                .tenMau("Màu Test Trùng US07")
                .build());

        // Trùng mã màu
        mockMvc.perform(post("/admin/mau-sac/add")
                        .param("maMau", TEST_HEX_1)
                        .param("tenMau", "Tên Màu Khác"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/mau-sac/hien-thi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("đã tồn tại")));

        // Trùng tên màu
        mockMvc.perform(post("/admin/mau-sac/add")
                        .param("maMau", TEST_HEX_2)
                        .param("tenMau", "Màu Test Trùng US07"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/mau-sac/hien-thi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("đã tồn tại")));
    }

    // =========================================================================
    // PHẦN 2: KIỂM THỬ QUẢN LÝ KÍCH THƯỚC [kich_thuoc] (S, M, L, XL...)
    // =========================================================================

    @Test
    @DisplayName("US07 - Kích thước: Hiển thị danh sách kích thước có phân trang và tìm kiếm")
    void hienThiKichThuoc_PhanTrangVaTimKiem_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/kich-thuoc/hien-thi")
                        .param("page", "0")
                        .param("tuKhoa", "Size"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/kich-thuoc/hien-thi"))
                .andExpect(model().attributeExists("listKichThuoc", "kichThuoc", "tongKichThuoc", "tongMauSac"));
    }

    @Test
    @DisplayName("US07 - Kích thước: Thêm mới, Chuẩn hóa mã Size in hoa, Cập nhật và Xóa kích thước thành công")
    void crudKichThuoc_ThanhCong() throws Exception {
        // 1. Thêm mới với mã chữ thường (4xl_test) -> Tự động chuẩn hóa thành 4XL_TEST
        mockMvc.perform(post("/admin/kich-thuoc/add")
                        .param("maKichThuoc", "4xl_test")
                        .param("tenKichThuoc", "Size 4XL Đặc Biệt (100-110kg)"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/kich-thuoc/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        KichThuoc saved = kichThuocRepository.findByMaKichThuocIgnoreCase(TEST_SIZE_1).orElseThrow();
        assertEquals(TEST_SIZE_1, saved.getMaKichThuoc());
        assertEquals("Size 4XL Đặc Biệt (100-110kg)", saved.getTenKichThuoc());
        assertFalse(saved.getTenKichThuoc().contains("?"));

        // 2. Cập nhật sang 5XL_TEST
        mockMvc.perform(post("/admin/kich-thuoc/update")
                        .param("id", String.valueOf(saved.getId()))
                        .param("maKichThuoc", TEST_SIZE_2)
                        .param("tenKichThuoc", "Size 5XL Ngoại Cỡ (110-120kg)"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/kich-thuoc/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        KichThuoc updated = kichThuocRepository.findById(saved.getId()).orElseThrow();
        assertEquals(TEST_SIZE_2, updated.getMaKichThuoc());
        assertEquals("Size 5XL Ngoại Cỡ (110-120kg)", updated.getTenKichThuoc());

        // 3. Xóa kích thước
        mockMvc.perform(get("/admin/kich-thuoc/delete")
                        .param("id", String.valueOf(saved.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/kich-thuoc/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertFalse(kichThuocRepository.existsById(saved.getId()));
    }

    @Test
    @DisplayName("US07 - Validate Kích thước: Chặn mã size chứa khoảng trắng/ký tự lạ và chặn trùng mã size / tên size")
    void validateKichThuoc_SaiDinhDangVaTrungLap() throws Exception {
        // 1. Mã size có khoảng trắng ("SIZE M @") và tên rỗng
        mockMvc.perform(post("/admin/kich-thuoc/add")
                        .param("maKichThuoc", "SIZE M @")
                        .param("tenKichThuoc", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/kich-thuoc/hien-thi"))
                .andExpect(model().attributeHasFieldErrors("kichThuoc", "maKichThuoc", "tenKichThuoc"));

        // 2. Tạo 1 kích thước hợp lệ rồi thử thêm trùng mã hoặc trùng tên
        kichThuocRepository.save(KichThuoc.builder()
                .maKichThuoc(TEST_SIZE_1)
                .tenKichThuoc("Size Test Trùng US07")
                .build());

        // Trùng mã kích thước
        mockMvc.perform(post("/admin/kich-thuoc/add")
                        .param("maKichThuoc", TEST_SIZE_1)
                        .param("tenKichThuoc", "Tên Kích Thước Khác"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/kich-thuoc/hien-thi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("đã tồn tại")));

        // Trùng tên kích thước
        mockMvc.perform(post("/admin/kich-thuoc/add")
                        .param("maKichThuoc", TEST_SIZE_2)
                        .param("tenKichThuoc", "Size Test Trùng US07"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/kich-thuoc/hien-thi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("đã tồn tại")));
    }
}
