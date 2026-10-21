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
import vn.fpoly.simplain.dto.CapNhatHoSoRequest;
import vn.fpoly.simplain.dto.DoiMatKhauRequest;
import vn.fpoly.simplain.dto.HoSoKhachHangResponse;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.service.KhachHangService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử tầng Giao diện Web KhachHangController (US12)")
class KhachHangControllerTest {

    private MockMvc mockMvc;

    @Mock
    private KhachHangService khachHangService;

    @InjectMocks
    private KhachHangController khachHangController;

    private HoSoKhachHangResponse hoSoMau;

    @BeforeEach
    void khoiTao() {
        mockMvc = MockMvcBuilders.standaloneSetup(khachHangController).build();

        hoSoMau = new HoSoKhachHangResponse(
                1L,
                "KH0001",
                "Nguyễn Văn A",
                "0987654321",
                "nguyenvana@gmail.com",
                "https://example.com/avatar.jpg",
                "Nam",
                LocalDate.of(1995, 8, 15),
                1,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("GET /khach-hang/ho-so trả về view khach-hang/ho-so")
    void hienThiHoSo_TraVeViewHopLe() throws Exception {
        when(khachHangService.layHoSo(1L)).thenReturn(hoSoMau);

        mockMvc.perform(get("/khach-hang/ho-so").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/ho-so"))
                .andExpect(model().attributeExists("hoSo"))
                .andExpect(model().attributeExists("capNhatRequest"));
    }

    @Test
    @DisplayName("POST /khach-hang/cap-nhat redirect về /khach-hang/ho-so khi hợp lệ")
    void xuLyCapNhat_HopLe_Redirect() throws Exception {
        when(khachHangService.capNhatHoSo(eq(1L), any(CapNhatHoSoRequest.class))).thenReturn(hoSoMau);

        mockMvc.perform(post("/khach-hang/cap-nhat")
                        .param("id", "1")
                        .param("hoTen", "Nguyễn Văn B")
                        .param("soDienThoai", "0912345678")
                        .param("email", "b@gmail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/ho-so?id=1"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));
    }

    @Test
    @DisplayName("GET /khach-hang/doi-mat-khau trả về view khach-hang/doi-mat-khau")
    void hienThiDoiMatKhau_TraVeViewHopLe() throws Exception {
        mockMvc.perform(get("/khach-hang/doi-mat-khau").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/doi-mat-khau"))
                .andExpect(model().attributeExists("doiMatKhauRequest"));
    }

    @Test
    @DisplayName("POST /khach-hang/doi-mat-khau redirect về /khach-hang/doi-mat-khau khi thành công")
    void xuLyDoiMatKhau_ThanhCong_Redirect() throws Exception {
        when(khachHangService.doiMatKhau(eq(1L), any(DoiMatKhauRequest.class)))
                .thenReturn(new ThongBaoResponse("Đổi mật khẩu thành công"));

        mockMvc.perform(post("/khach-hang/doi-mat-khau")
                        .param("id", "1")
                        .param("matKhauCu", "MatKhauCu123@")
                        .param("matKhauMoi", "MatKhauMoi456@")
                        .param("xacNhanMatKhauMoi", "MatKhauMoi456@"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/doi-mat-khau?id=1"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));
    }
}
