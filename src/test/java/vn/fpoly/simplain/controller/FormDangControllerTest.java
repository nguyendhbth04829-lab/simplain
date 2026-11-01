package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.FormDang;
import vn.fpoly.simplain.repository.FormDangRepository;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động Web MVC cho Quản lý danh mục Form dáng (US32 - Mod 2 - Hưng).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Kiểm thử Web MVC Quản lý Form dáng (US32)")
class FormDangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FormDangRepository formDangRepository;

    private FormDang formDangMau;

    @BeforeEach
    void setUp() {
        formDangMau = new FormDang();
        formDangMau.setTenFormDang("Oversize Test " + System.currentTimeMillis());
        formDangMau.setMoTa("Form áo rộng thoải mái");
        formDangMau.setKichHoat(true);
        formDangMau = formDangRepository.save(formDangMau);
    }

    @Test
    @DisplayName("US32 - Hiển thị danh sách form dáng thành công")
    void testHienThi() throws Exception {
        mockMvc.perform(get("/admin/form-dang/hien-thi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/form-dang/hien-thi"))
                .andExpect(model().attributeExists("listFormDang"))
                .andExpect(model().attributeExists("pageData"));
    }

    @Test
    @DisplayName("US32 - Thêm mới form dáng hợp lệ")
    void testThemMoiThanhCong() throws Exception {
        mockMvc.perform(post("/admin/form-dang/add")
                        .param("tenFormDang", "Slim Fit Test " + System.currentTimeMillis())
                        .param("moTa", "Form ôm sát cơ thể")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/form-dang/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));
    }

    @Test
    @DisplayName("US32 - Thêm mới form dáng lỗi khi để trống tên")
    void testThemMoiLoiTenTrong() throws Exception {
        mockMvc.perform(post("/admin/form-dang/add")
                        .param("tenFormDang", "")
                        .param("moTa", "Mô tả"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/form-dang/hien-thi"))
                .andExpect(model().attributeExists("thongBaoLoi"));
    }

    @Test
    @DisplayName("US32 - Chuyển trạng thái kích hoạt form dáng")
    void testChuyenTrangThai() throws Exception {
        mockMvc.perform(get("/admin/form-dang/doi-trang-thai")
                        .param("id", formDangMau.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/form-dang/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        FormDang updated = formDangRepository.findById(formDangMau.getId()).orElseThrow();
        assertFalse(updated.getKichHoat());
    }
}
