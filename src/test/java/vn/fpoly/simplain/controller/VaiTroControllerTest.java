package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.entity.VaiTro;
import vn.fpoly.simplain.repository.VaiTroRepository;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động giao diện Web MVC cho chức năng Quản lý danh sách Vai trò (US03).
 *
 * <p>Tiêu chí chấp nhận:
 * 1. Thêm/Sửa/Xóa vai_tro.
 * 2. Không cho xóa vai trò đang có nhân viên sử dụng.
 *
 * <p>Chạy bằng lệnh: .\mvnw.cmd -Dtest=VaiTroControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Quản lý Vai trò (US03)")
class VaiTroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    private final String MA_VAI_TRO_TEST = "TEST_ROLE_01";
    private final String MA_VAI_TRO_TEST_DELETE = "TEST_ROLE_FREE";

    @BeforeEach
    void setUp() {
        donDep();
    }

    @AfterEach
    void tearDown() {
        donDep();
    }

    private void donDep() {
        vaiTroRepository.findByMaVaiTro(MA_VAI_TRO_TEST).ifPresent(vaiTroRepository::delete);
        vaiTroRepository.findByMaVaiTro(MA_VAI_TRO_TEST_DELETE).ifPresent(vaiTroRepository::delete);
    }

    @Test
    @DisplayName("Hiển thị danh sách vai trò có phân trang thành công")
    void hienThiDanhSachVaiTro_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/vai-tro/hien-thi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/vai-tro/hien-thi"))
                .andExpect(model().attributeExists("pageData"))
                .andExpect(model().attributeExists("soNhanVienMap"));
    }

    @Test
    @DisplayName("US03 - Thêm mới vai trò trên giao diện Web thành công")
    void themVaiTro_ThanhCong() throws Exception {
        mockMvc.perform(post("/admin/vai-tro/them")
                        .param("maVaiTro", MA_VAI_TRO_TEST)
                        .param("tenVaiTro", "Vai trò thử nghiệm")
                        .param("moTa", "Mô tả quyền hạn thử nghiệm")
                        .param("trangThai", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/vai-tro/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        VaiTro saved = vaiTroRepository.findByMaVaiTro(MA_VAI_TRO_TEST).orElse(null);
        assertNotNull(saved);
        assertEquals("Vai trò thử nghiệm", saved.getTenVaiTro());
    }

    @Test
    @DisplayName("Thêm mới vai trò thất bại khi trùng mã")
    void themVaiTro_TrungMa_Loi() throws Exception {
        mockMvc.perform(post("/admin/vai-tro/them")
                        .param("maVaiTro", "ADMIN")
                        .param("tenVaiTro", "Quản trị viên trùng lặp")
                        .param("trangThai", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/vai-tro/hien-thi"))
                .andExpect(flash().attribute("thongBaoLoi", containsString("đã tồn tại")));
    }

    @Test
    @DisplayName("Thêm mới vai trò thất bại khi để trống tên hoặc mã sai định dạng")
    void themVaiTro_ValidateLoi() throws Exception {
        mockMvc.perform(post("/admin/vai-tro/them")
                        .param("maVaiTro", "ma sai dinh dang")
                        .param("tenVaiTro", "")
                        .param("trangThai", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/vai-tro/hien-thi"))
                .andExpect(model().attributeHasFieldErrors("vaiTroRequest", "maVaiTro", "tenVaiTro"));
    }

    @Test
    @DisplayName("US03 - Cập nhật thông tin vai trò trên giao diện Web thành công")
    void suaVaiTro_ThanhCong() throws Exception {
        VaiTro vt = vaiTroRepository.save(VaiTro.builder()
                .maVaiTro(MA_VAI_TRO_TEST)
                .tenVaiTro("Tên Cũ")
                .moTa("Mô tả cũ")
                .trangThai(1)
                .build());

        mockMvc.perform(post("/admin/vai-tro/sua/" + vt.getId())
                        .param("id", String.valueOf(vt.getId()))
                        .param("maVaiTro", MA_VAI_TRO_TEST)
                        .param("tenVaiTro", "Tên Mới Đã Sửa")
                        .param("moTa", "Mô tả mới")
                        .param("trangThai", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/vai-tro/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        VaiTro updated = vaiTroRepository.findById(vt.getId()).orElseThrow();
        assertEquals("Tên Mới Đã Sửa", updated.getTenVaiTro());
    }

    @Test
    @DisplayName("US03 - Tiêu chí 2: Không cho xóa vai trò khi đang có nhân viên sử dụng")
    void xoaVaiTro_TuChoiKhiCoNhanVien() throws Exception {
        VaiTro vaiTroAdmin = vaiTroRepository.findByMaVaiTro("ADMIN").orElseThrow();

        mockMvc.perform(get("/admin/vai-tro/xoa/" + vaiTroAdmin.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/vai-tro/hien-thi"))
                .andExpect(flash().attribute("thongBaoLoi", containsString("nhân viên đang sử dụng")));

        assertTrue(vaiTroRepository.existsById(vaiTroAdmin.getId()));
    }

    @Test
    @DisplayName("US03 - Tiêu chí 1: Xóa thành công vai trò khi không có nhân viên nào sử dụng")
    void xoaVaiTro_ThanhCongKhiKhongCoNhanVien() throws Exception {
        VaiTro vt = vaiTroRepository.save(VaiTro.builder()
                .maVaiTro(MA_VAI_TRO_TEST_DELETE)
                .tenVaiTro("Vai trò tự do để xóa")
                .trangThai(1)
                .build());

        mockMvc.perform(get("/admin/vai-tro/xoa/" + vt.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/vai-tro/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertFalse(vaiTroRepository.existsById(vt.getId()));
    }
}
