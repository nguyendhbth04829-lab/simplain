/**
 * Tác giả: Gia Minh
 * Chức năng: US15 - Kiểm thử tầng REST API DanhGiaSanPhamApiController
 * Cập nhật xác nhận commit US15: 26/10/2026 - Gia Minh
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
import vn.fpoly.simplain.dto.DanhGiaResponse;
import vn.fpoly.simplain.dto.TaoDanhGiaRequest;
import vn.fpoly.simplain.dto.ThongKeDanhGiaResponse;
import vn.fpoly.simplain.exception.ChuaDangNhapException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.exception.XungDotDuLieuException;
import vn.fpoly.simplain.exception.XuLyLoiHeThong;
import vn.fpoly.simplain.service.DanhGiaSanPhamService;

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
@DisplayName("Kiểm thử tầng REST API DanhGiaSanPhamApiController (US15)")
class DanhGiaSanPhamApiControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private DanhGiaSanPhamService danhGiaSanPhamService;

    @InjectMocks
    private DanhGiaSanPhamApiController danhGiaSanPhamApiController;

    private DanhGiaResponse danhGiaMau;

    @BeforeEach
    void khoiTao() {
        mockMvc = MockMvcBuilders.standaloneSetup(danhGiaSanPhamApiController)
                .setControllerAdvice(new XuLyLoiHeThong())
                .build();

        objectMapper = new ObjectMapper();

        danhGiaMau = new DanhGiaResponse(
                1L,
                10L,
                "Áo Polo Nam Simplain",
                1L,
                "Nguyễn Văn A",
                50L,
                5,
                "Áo rất đẹp",
                List.of("https://img.com/1.jpg"),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("POST /api/khach-hang/danh-gia: Tạo đánh giá thành công trả về 201")
    void taoDanhGia_ThanhCong() throws Exception {
        TaoDanhGiaRequest req = new TaoDanhGiaRequest(10L, 50L, 5, "Áo rất đẹp", List.of("https://img.com/1.jpg"));

        when(danhGiaSanPhamService.taoDanhGia(eq(1L), any(TaoDanhGiaRequest.class))).thenReturn(danhGiaMau);

        mockMvc.perform(post("/api/khach-hang/danh-gia")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.soSao", is(5)))
                .andExpect(jsonPath("$.tenSanPham", is("Áo Polo Nam Simplain")));
    }

    @Test
    @DisplayName("POST /api/khach-hang/danh-gia: Validation lỗi khi số sao < 1 hoặc > 5 trả về 400")
    void taoDanhGia_SoSaoKhongHopLe_TraVe400() throws Exception {
        TaoDanhGiaRequest req = new TaoDanhGiaRequest(10L, 50L, 6, "Quá 5 sao", List.of());

        mockMvc.perform(post("/api/khach-hang/danh-gia")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.maTrangThai", is(400)));
    }

    @Test
    @DisplayName("POST /api/khach-hang/danh-gia: Đã từng đánh giá trước đó trả về 409 Xung đột")
    void taoDanhGia_XungDot_TraVe409() throws Exception {
        TaoDanhGiaRequest req = new TaoDanhGiaRequest(10L, 50L, 5, "Đánh giá lại", List.of());

        when(danhGiaSanPhamService.taoDanhGia(eq(1L), any(TaoDanhGiaRequest.class)))
                .thenThrow(new XungDotDuLieuException("Sản phẩm đã được đánh giá"));

        mockMvc.perform(post("/api/khach-hang/danh-gia")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.maTrangThai", is(409)));
    }

    @Test
    @DisplayName("GET /api/khach-hang/danh-gia/san-pham/{id}: Lấy danh sách đánh giá theo sản phẩm thành công")
    void layTheoSanPham_ThanhCong() throws Exception {
        when(danhGiaSanPhamService.layDanhSachTheoSanPham(10L)).thenReturn(List.of(danhGiaMau));

        mockMvc.perform(get("/api/khach-hang/danh-gia/san-pham/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tenKhachHang", is("Nguyễn Văn A")));
    }

    @Test
    @DisplayName("GET /api/khach-hang/danh-gia/san-pham/{id}/thong-ke: Lấy thống kê sao thành công")
    void layThongKe_ThanhCong() throws Exception {
        ThongKeDanhGiaResponse tk = new ThongKeDanhGiaResponse(10L, 4.8, 10, 8, 2, 0, 0, 0);
        when(danhGiaSanPhamService.layThongKeDanhGia(10L)).thenReturn(tk);

        mockMvc.perform(get("/api/khach-hang/danh-gia/san-pham/10/thong-ke"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diemTrungBinh", is(4.8)))
                .andExpect(jsonPath("$.tongSoDanhGia", is(10)));
    }
}
