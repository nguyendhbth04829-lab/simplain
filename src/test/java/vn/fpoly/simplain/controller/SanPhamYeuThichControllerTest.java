/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - Kiểm thử tầng giao diện Web SanPhamYeuThichController
 * Cập nhật xác nhận commit US14: 26/10/2026 - Gia Minh
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
import vn.fpoly.simplain.dto.SanPhamYeuThichResponse;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.service.SanPhamYeuThichService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử tầng Giao diện Web SanPhamYeuThichController (US14)")
class SanPhamYeuThichControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SanPhamYeuThichService sanPhamYeuThichService;

    @InjectMocks
    private SanPhamYeuThichController sanPhamYeuThichController;

    private SanPhamYeuThichResponse sanPhamYeuThichMau;

    @BeforeEach
    void khoiTao() {
        mockMvc = MockMvcBuilders.standaloneSetup(sanPhamYeuThichController).build();

        sanPhamYeuThichMau = new SanPhamYeuThichResponse(
                1L,
                100L,
                "SP001",
                "Áo Thun Nam Simplain",
                "ao-thun-nam-simplain",
                new BigDecimal("199000"),
                "100% Cotton",
                "Regular",
                LocalDateTime.now(),
                true
        );
    }

    @Test
    @DisplayName("GET /khach-hang/yeu-thich: Hiển thị danh sách sản phẩm yêu thích thành công")
    void hienThiDanhSach_ThanhCong() throws Exception {
        when(sanPhamYeuThichService.layDanhSachYeuThich(1L)).thenReturn(List.of(sanPhamYeuThichMau));

        mockMvc.perform(get("/khach-hang/yeu-thich").param("idKhachHang", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/yeu-thich/danh-sach"))
                .andExpect(model().attributeExists("danhSachYeuThich"))
                .andExpect(model().attribute("idKhachHang", 1L))
                .andExpect(model().attribute("soLuong", 1));
    }

    @Test
    @DisplayName("POST /khach-hang/yeu-thich/them: Thêm sản phẩm yêu thích và chuyển hướng")
    void xuLyThemYeuThich_ThanhCong() throws Exception {
        when(sanPhamYeuThichService.themVaoYeuThich(1L, 100L)).thenReturn(sanPhamYeuThichMau);

        mockMvc.perform(post("/khach-hang/yeu-thich/them")
                        .param("idKhachHang", "1")
                        .param("maSanPhamId", "100"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/yeu-thich?idKhachHang=1"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        verify(sanPhamYeuThichService).themVaoYeuThich(1L, 100L);
    }

    @Test
    @DisplayName("POST /khach-hang/yeu-thich/xoa: Xóa sản phẩm yêu thích và chuyển hướng")
    void xuLyXoaYeuThich_ThanhCong() throws Exception {
        when(sanPhamYeuThichService.xoaKhoiYeuThich(1L, 100L))
                .thenReturn(new ThongBaoResponse("Đã xóa sản phẩm khỏi danh sách yêu thích"));

        mockMvc.perform(post("/khach-hang/yeu-thich/xoa")
                        .param("idKhachHang", "1")
                        .param("maSanPhamId", "100"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/yeu-thich?idKhachHang=1"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        verify(sanPhamYeuThichService).xoaKhoiYeuThich(1L, 100L);
    }
}
