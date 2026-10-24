/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - Kiểm thử tầng REST API KhachHangApiController
 * Cập nhật xác nhận commit US12: 24/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import vn.fpoly.simplain.dto.CapNhatHoSoRequest;
import vn.fpoly.simplain.dto.DoiMatKhauRequest;
import vn.fpoly.simplain.dto.HoSoKhachHangResponse;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.exception.ChuaDangNhapException;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.exception.XungDotDuLieuException;
import vn.fpoly.simplain.exception.XuLyLoiHeThong;
import vn.fpoly.simplain.service.KhachHangService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử tầng REST API KhachHangApiController (US12)")
class KhachHangApiControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private KhachHangService khachHangService;

    @InjectMocks
    private KhachHangApiController khachHangApiController;

    private HoSoKhachHangResponse hoSoMau;

    @BeforeEach
    void khoiTao() {
        mockMvc = MockMvcBuilders.standaloneSetup(khachHangApiController)
                .setControllerAdvice(new XuLyLoiHeThong())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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

    // -------------------------------------------------------------
    // GET /api/khach-hang/ho-so
    // -------------------------------------------------------------

    @Test
    @DisplayName("GET /api/khach-hang/ho-so trả về 200 OK khi có header X-Customer-Id hợp lệ")
    void layHoSo_CoHeaderHopLe_TraVe200() throws Exception {
        when(khachHangService.layHoSo(1L)).thenReturn(hoSoMau);

        mockMvc.perform(get("/api/khach-hang/ho-so")
                        .header("X-Customer-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.maKhachHang", is("KH0001")))
                .andExpect(jsonPath("$.hoTen", is("Nguyễn Văn A")))
                .andExpect(jsonPath("$.soDienThoai", is("0987654321")))
                .andExpect(jsonPath("$.email", is("nguyenvana@gmail.com")));
    }

    @Test
    @DisplayName("GET /api/khach-hang/ho-so trả về 401 Unauthorized khi thiếu header X-Customer-Id")
    void layHoSo_ThieuHeader_TraVe401() throws Exception {
        when(khachHangService.layHoSo(null))
                .thenThrow(new ChuaDangNhapException("Khách hàng chưa đăng nhập hoặc thiếu thông tin định danh (X-Customer-Id)"));

        mockMvc.perform(get("/api/khach-hang/ho-so")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.maTrangThai", is(401)))
                .andExpect(jsonPath("$.trangThai", is("CHUA_DANG_NHAP")));
    }

    @Test
    @DisplayName("GET /api/khach-hang/ho-so trả về 404 Not Found khi khách hàng không tồn tại")
    void layHoSo_KhongTonTai_TraVe404() throws Exception {
        when(khachHangService.layHoSo(99L))
                .thenThrow(new KhongTimThayException("Không tìm thấy thông tin khách hàng với ID: 99"));

        mockMvc.perform(get("/api/khach-hang/ho-so")
                        .header("X-Customer-Id", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.maTrangThai", is(404)))
                .andExpect(jsonPath("$.thongDiep", is("Không tìm thấy thông tin khách hàng với ID: 99")));
    }

    // -------------------------------------------------------------
    // PUT /api/khach-hang/cap-nhat
    // -------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/khach-hang/cap-nhat trả về 200 OK khi dữ liệu hợp lệ")
    void capNhatHoSo_DuLieuHopLe_TraVe200() throws Exception {
        CapNhatHoSoRequest yeuCau = new CapNhatHoSoRequest(
                "Nguyễn Văn B",
                "0912345678",
                "nguyenvanb@gmail.com",
                "Nam",
                LocalDate.of(1996, 5, 20),
                "https://example.com/avatar2.jpg"
        );

        when(khachHangService.capNhatHoSo(eq(1L), any(CapNhatHoSoRequest.class)))
                .thenReturn(hoSoMau);

        mockMvc.perform(put("/api/khach-hang/cap-nhat")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(yeuCau)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("PUT /api/khach-hang/cap-nhat trả về 400 Bad Request khi họ tên bị trống")
    void capNhatHoSo_HoTenTrong_TraVe400() throws Exception {
        CapNhatHoSoRequest yeuCau = new CapNhatHoSoRequest(
                "",
                "0912345678",
                "nguyenvanb@gmail.com",
                "Nam",
                null,
                null
        );

        mockMvc.perform(put("/api/khach-hang/cap-nhat")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(yeuCau)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.maTrangThai", is(400)))
                .andExpect(jsonPath("$.chiTietLoi.hoTen", is("Họ và tên không được để trống")));
    }

    @Test
    @DisplayName("PUT /api/khach-hang/cap-nhat trả về 400 Bad Request khi email sai định dạng")
    void capNhatHoSo_EmailSaiDinhDang_TraVe400() throws Exception {
        CapNhatHoSoRequest yeuCau = new CapNhatHoSoRequest(
                "Nguyễn Văn B",
                "0912345678",
                "email-khong-hop-le",
                "Nam",
                null,
                null
        );

        mockMvc.perform(put("/api/khach-hang/cap-nhat")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(yeuCau)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.maTrangThai", is(400)))
                .andExpect(jsonPath("$.chiTietLoi.email", is("Email không đúng định dạng")));
    }

    @Test
    @DisplayName("PUT /api/khach-hang/cap-nhat trả về 409 Conflict khi email bị trùng lặp")
    void capNhatHoSo_EmailTrungLap_TraVe409() throws Exception {
        CapNhatHoSoRequest yeuCau = new CapNhatHoSoRequest(
                "Nguyễn Văn B",
                "0912345678",
                "trunglap@gmail.com",
                "Nam",
                null,
                null
        );

        when(khachHangService.capNhatHoSo(eq(1L), any(CapNhatHoSoRequest.class)))
                .thenThrow(new XungDotDuLieuException("Email 'trunglap@gmail.com' đã được sử dụng bởi tài khoản khác"));

        mockMvc.perform(put("/api/khach-hang/cap-nhat")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(yeuCau)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.maTrangThai", is(409)))
                .andExpect(jsonPath("$.thongDiep", is("Email 'trunglap@gmail.com' đã được sử dụng bởi tài khoản khác")));
    }

    // -------------------------------------------------------------
    // PUT /api/khach-hang/doi-mat-khau
    // -------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/khach-hang/doi-mat-khau trả về 200 OK khi đổi mật khẩu thành công")
    void doiMatKhau_HopLe_TraVe200() throws Exception {
        DoiMatKhauRequest yeuCau = new DoiMatKhauRequest(
                "MatKhauCu123@",
                "MatKhauMoi456@",
                "MatKhauMoi456@"
        );

        when(khachHangService.doiMatKhau(eq(1L), any(DoiMatKhauRequest.class)))
                .thenReturn(new ThongBaoResponse("Đổi mật khẩu thành công"));

        mockMvc.perform(put("/api/khach-hang/doi-mat-khau")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(yeuCau)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thongDiep", is("Đổi mật khẩu thành công")));
    }

    @Test
    @DisplayName("PUT /api/khach-hang/doi-mat-khau trả về 400 khi mật khẩu mới dưới 6 ký tự")
    void doiMatKhau_MatKhauNgan_TraVe400() throws Exception {
        DoiMatKhauRequest yeuCau = new DoiMatKhauRequest(
                "MatKhauCu123@",
                "123",
                "123"
        );

        mockMvc.perform(put("/api/khach-hang/doi-mat-khau")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(yeuCau)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.maTrangThai", is(400)))
                .andExpect(jsonPath("$.chiTietLoi.matKhauMoi", is("Mật khẩu mới phải từ 6 đến 50 ký tự")));
    }

    @Test
    @DisplayName("PUT /api/khach-hang/doi-mat-khau trả về 400 khi mật khẩu cũ không đúng")
    void doiMatKhau_SaiMatKhauCu_TraVe400() throws Exception {
        DoiMatKhauRequest yeuCau = new DoiMatKhauRequest(
                "MatKhauSai!",
                "MatKhauMoi456@",
                "MatKhauMoi456@"
        );

        when(khachHangService.doiMatKhau(eq(1L), any(DoiMatKhauRequest.class)))
                .thenThrow(new DuLieuKhongHopLeException("Mật khẩu hiện tại không chính xác"));

        mockMvc.perform(put("/api/khach-hang/doi-mat-khau")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(yeuCau)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.maTrangThai", is(400)))
                .andExpect(jsonPath("$.thongDiep", is("Mật khẩu hiện tại không chính xác")));
    }
}
