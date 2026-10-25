/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - Kiểm thử tầng REST API HoiVienLoyaltyApiController
 */
package vn.fpoly.simplain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.fpoly.simplain.dto.BienDongDiemResponse;
import vn.fpoly.simplain.dto.HoSoHoiVienResponse;
import vn.fpoly.simplain.dto.SuDungDiemRequest;
import vn.fpoly.simplain.entity.HangThanhVien;
import vn.fpoly.simplain.exception.ChuaDangNhapException;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.exception.XuLyLoiHeThong;
import vn.fpoly.simplain.service.HoiVienLoyaltyService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử tầng REST API HoiVienLoyaltyApiController (US16)")
class HoiVienLoyaltyApiControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private HoiVienLoyaltyService hoiVienLoyaltyService;

    @InjectMocks
    private HoiVienLoyaltyApiController hoiVienLoyaltyApiController;

    private HoSoHoiVienResponse hoSoMau;

    @BeforeEach
    void khoiTao() {
        mockMvc = MockMvcBuilders.standaloneSetup(hoiVienLoyaltyApiController)
                .setControllerAdvice(new XuLyLoiHeThong())
                .build();

        objectMapper = new ObjectMapper();

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
    @DisplayName("GET /api/khach-hang/hoi-vien: Xem hồ sơ hội viên thành công")
    void layHoSo_ThanhCong() throws Exception {
        when(hoiVienLoyaltyService.layHoSoHoiVien(1L)).thenReturn(hoSoMau);

        mockMvc.perform(get("/api/khach-hang/hoi-vien")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenHangHienTai", is("Thành viên Bạc")))
                .andExpect(jsonPath("$.soDuCoolcash", is(50000)));
    }

    @Test
    @DisplayName("GET /api/khach-hang/hoi-vien: Chưa đăng nhập trả về 401")
    void layHoSo_ChuaDangNhap_TraVe401() throws Exception {
        when(hoiVienLoyaltyService.layHoSoHoiVien(null))
                .thenThrow(new ChuaDangNhapException("Vui lòng đăng nhập"));

        mockMvc.perform(get("/api/khach-hang/hoi-vien"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.maTrangThai", is(401)));
    }

    @Test
    @DisplayName("GET /api/khach-hang/hoi-vien/lich-su-diem: Lấy lịch sử biến động điểm")
    void layLichSuDiem_ThanhCong() throws Exception {
        BienDongDiemResponse bd = new BienDongDiemResponse(1L, 1L, 100L, "CONG_DIEM", new BigDecimal("30000"), "Tích điểm", LocalDateTime.now());
        when(hoiVienLoyaltyService.layLichSuBienDongDiem(1L)).thenReturn(List.of(bd));

        mockMvc.perform(get("/api/khach-hang/hoi-vien/lich-su-diem")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].loaiBienDong", is("CONG_DIEM")));
    }

    @Test
    @DisplayName("POST /api/khach-hang/hoi-vien/su-dung-diem: Sử dụng điểm thành công")
    void suDungDiem_ThanhCong() throws Exception {
        SuDungDiemRequest req = new SuDungDiemRequest(new BigDecimal("20000"), 100L);
        BienDongDiemResponse bd = new BienDongDiemResponse(1L, 1L, 100L, "TRU_DIEM", new BigDecimal("20000"), "Trừ điểm", LocalDateTime.now());

        when(hoiVienLoyaltyService.suDungDiemCoolcash(eq(1L), any(SuDungDiemRequest.class))).thenReturn(bd);

        mockMvc.perform(post("/api/khach-hang/hoi-vien/su-dung-diem")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loaiBienDong", is("TRU_DIEM")))
                .andExpect(jsonPath("$.soDiem", is(20000)));
    }

    @Test
    @DisplayName("GET /api/khach-hang/hoi-vien/hang-thanh-vien: Lấy danh sách các bậc hạng")
    void layDanhSachHang_ThanhCong() throws Exception {
        HangThanhVien h1 = HangThanhVien.builder().id(1L).tenHang("Mới").chiTieuNamToiThieu(BigDecimal.ZERO).phanTramHoanTien(new BigDecimal("1.0")).build();
        when(hoiVienLoyaltyService.layDanhSachHangThanhVien()).thenReturn(List.of(h1));

        mockMvc.perform(get("/api/khach-hang/hoi-vien/hang-thanh-vien"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tenHang", is("Mới")));
    }
}
