/**
 * Tác giả: Gia Minh
 * Chức năng: Kiểm thử giao diện Cổng thông tin trang chủ TrangChuController
 */
package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.repository.KhachHangRepository;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử Cổng thông tin TrangChuController")
class TrangChuControllerTest {

    private MockMvc mockMvc;

    @Mock
    private KhachHangRepository khachHangRepository;

    @InjectMocks
    private TrangChuController trangChuController;

    @BeforeEach
    void khoiTao() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(trangChuController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @DisplayName("GET / - Trả về view trang-chu với danh sách khách hàng và ID mặc định")
    void testHienThiTrangChuGoc() throws Exception {
        KhachHang khachHang = new KhachHang();
        khachHang.setId(1L);
        khachHang.setHoTen("Nguyễn Gia Minh");
        khachHang.setEmail("minh@example.com");

        when(khachHangRepository.findAll()).thenReturn(List.of(khachHang));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("trang-chu"))
                .andExpect(model().attributeExists("idKhachHangHienTai"))
                .andExpect(model().attributeExists("danhSachKhachHang"))
                .andExpect(model().attribute("idKhachHangHienTai", 1L));
    }

    @Test
    @DisplayName("GET /trang-chu?idKhachHang=2 - Nhận đúng tham số ID khách hàng tùy chọn")
    void testHienThiTrangChuVoiIdTuyChon() throws Exception {
        when(khachHangRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/trang-chu").param("idKhachHang", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("trang-chu"))
                .andExpect(model().attribute("idKhachHangHienTai", 2L));
    }
}
