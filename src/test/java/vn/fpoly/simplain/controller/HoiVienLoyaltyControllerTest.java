/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - Kiểm thử tầng giao diện Web HoiVienLoyaltyController
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
import vn.fpoly.simplain.dto.BienDongDiemResponse;
import vn.fpoly.simplain.dto.HoSoHoiVienResponse;
import vn.fpoly.simplain.entity.HangThanhVien;
import vn.fpoly.simplain.service.HoiVienLoyaltyService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử tầng Giao diện Web HoiVienLoyaltyController (US16)")
class HoiVienLoyaltyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HoiVienLoyaltyService hoiVienLoyaltyService;

    @InjectMocks
    private HoiVienLoyaltyController hoiVienLoyaltyController;

    private HoSoHoiVienResponse hoSoMau;

    @BeforeEach
    void khoiTao() {
        mockMvc = MockMvcBuilders.standaloneSetup(hoiVienLoyaltyController).build();

        hoSoMau = new HoSoHoiVienResponse(
                1L,
                "Nguyễn Văn A",
                2L,
                "Thành viên Bạc",
                new BigDecimal("3.0"),
                new BigDecimal("50000"),
                new BigDecimal("2500000"),
                "Thành viên Vàng",
                new BigDecimal("2500000")
        );
    }

    @Test
    @DisplayName("GET /khach-hang/hoi-vien: Hiển thị trang tổng quan hội viên thành công")
    void hienThiTongQuan_ThanhCong() throws Exception {
        when(hoiVienLoyaltyService.layHoSoHoiVien(1L)).thenReturn(hoSoMau);
        when(hoiVienLoyaltyService.layLichSuBienDongDiem(1L)).thenReturn(List.of());
        when(hoiVienLoyaltyService.layDanhSachHangThanhVien()).thenReturn(List.of());

        mockMvc.perform(get("/khach-hang/hoi-vien").param("idKhachHang", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/hoi-vien/tong-quan"))
                .andExpect(model().attributeExists("hoSo"))
                .andExpect(model().attributeExists("lichSuDiem"))
                .andExpect(model().attributeExists("danhSachHang"))
                .andExpect(model().attribute("idKhachHang", 1L));
    }
}
