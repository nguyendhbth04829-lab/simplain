/**
 * Tác giả: Gia Minh
 * Chức năng: US15 - Kiểm thử tầng giao diện Web DanhGiaSanPhamController
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
import vn.fpoly.simplain.dto.DanhGiaResponse;
import vn.fpoly.simplain.dto.TaoDanhGiaRequest;
import vn.fpoly.simplain.service.DanhGiaSanPhamService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử tầng Giao diện Web DanhGiaSanPhamController (US15)")
class DanhGiaSanPhamControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DanhGiaSanPhamService danhGiaSanPhamService;

    @InjectMocks
    private DanhGiaSanPhamController danhGiaSanPhamController;

    private DanhGiaResponse danhGiaMau;

    @BeforeEach
    void khoiTao() {
        mockMvc = MockMvcBuilders.standaloneSetup(danhGiaSanPhamController).build();

        danhGiaMau = new DanhGiaResponse(
                1L,
                10L,
                "Áo Polo Nam Simplain",
                1L,
                "Nguyễn Văn A",
                50L,
                5,
                "Áo rất đẹp",
                List.of(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("GET /khach-hang/danh-gia: Hiển thị danh sách đánh giá của khách hàng")
    void hienThiDanhSach_ThanhCong() throws Exception {
        when(danhGiaSanPhamService.layDanhSachTheoKhachHang(1L)).thenReturn(List.of(danhGiaMau));

        mockMvc.perform(get("/khach-hang/danh-gia").param("idKhachHang", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/danh-gia/danh-sach"))
                .andExpect(model().attributeExists("danhSachDanhGia"))
                .andExpect(model().attribute("idKhachHang", 1L))
                .andExpect(model().attribute("soLuong", 1));
    }

    @Test
    @DisplayName("GET /khach-hang/danh-gia/viet: Hiển thị form viết đánh giá")
    void hienThiFormViet_ThanhCong() throws Exception {
        mockMvc.perform(get("/khach-hang/danh-gia/viet")
                        .param("idKhachHang", "1")
                        .param("maSanPhamId", "10")
                        .param("maChiTietDonHangId", "50"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/danh-gia/form"))
                .andExpect(model().attributeExists("danhGiaRequest"));
    }

    @Test
    @DisplayName("POST /khach-hang/danh-gia/viet: Gửi đánh giá hợp lệ chuyển hướng về danh sách")
    void xuLyGuiDanhGia_ThanhCong() throws Exception {
        when(danhGiaSanPhamService.taoDanhGia(eq(1L), any(TaoDanhGiaRequest.class))).thenReturn(danhGiaMau);

        mockMvc.perform(post("/khach-hang/danh-gia/viet")
                        .param("idKhachHang", "1")
                        .param("maSanPhamId", "10")
                        .param("maChiTietDonHangId", "50")
                        .param("soSao", "5")
                        .param("noiDung", "Tuyệt vời"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/danh-gia?idKhachHang=1"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        verify(danhGiaSanPhamService).taoDanhGia(eq(1L), any(TaoDanhGiaRequest.class));
    }
}
