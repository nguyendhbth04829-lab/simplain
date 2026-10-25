/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - Kiểm thử tầng REST API SanPhamYeuThichApiController
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
import vn.fpoly.simplain.dto.SanPhamYeuThichResponse;
import vn.fpoly.simplain.dto.ThemYeuThichRequest;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.dto.TrangThaiYeuThichResponse;
import vn.fpoly.simplain.exception.ChuaDangNhapException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.exception.XuLyLoiHeThong;
import vn.fpoly.simplain.service.SanPhamYeuThichService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử tầng REST API SanPhamYeuThichApiController (US14)")
class SanPhamYeuThichApiControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SanPhamYeuThichService sanPhamYeuThichService;

    @InjectMocks
    private SanPhamYeuThichApiController sanPhamYeuThichApiController;

    private SanPhamYeuThichResponse sanPhamYeuThichMau;

    @BeforeEach
    void khoiTao() {
        mockMvc = MockMvcBuilders.standaloneSetup(sanPhamYeuThichApiController)
                .setControllerAdvice(new XuLyLoiHeThong())
                .build();

        objectMapper = new ObjectMapper();

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
    @DisplayName("GET /api/khach-hang/yeu-thich: Lấy danh sách thành công")
    void layDanhSach_ThanhCong() throws Exception {
        when(sanPhamYeuThichService.layDanhSachYeuThich(1L)).thenReturn(List.of(sanPhamYeuThichMau));

        mockMvc.perform(get("/api/khach-hang/yeu-thich")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tenSanPham", is("Áo Thun Nam Simplain")))
                .andExpect(jsonPath("$[0].maSanPham", is("SP001")));
    }

    @Test
    @DisplayName("GET /api/khach-hang/yeu-thich: Chưa đăng nhập trả về 401")
    void layDanhSach_ChuaDangNhap_TraVe401() throws Exception {
        when(sanPhamYeuThichService.layDanhSachYeuThich(null))
                .thenThrow(new ChuaDangNhapException("Vui lòng đăng nhập"));

        mockMvc.perform(get("/api/khach-hang/yeu-thich"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.maTrangThai", is(401)));
    }

    @Test
    @DisplayName("POST /api/khach-hang/yeu-thich/{id}: Thêm vào yêu thích qua Path Variable thành công trả về 201")
    void themVaoYeuThich_Path_ThanhCong() throws Exception {
        when(sanPhamYeuThichService.themVaoYeuThich(1L, 100L)).thenReturn(sanPhamYeuThichMau);

        mockMvc.perform(post("/api/khach-hang/yeu-thich/100")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maSanPhamId", is(100)))
                .andExpect(jsonPath("$.tenSanPham", is("Áo Thun Nam Simplain")));
    }

    @Test
    @DisplayName("POST /api/khach-hang/yeu-thich: Thêm vào yêu thích qua Request Body thành công trả về 201")
    void themVaoYeuThich_Body_ThanhCong() throws Exception {
        ThemYeuThichRequest req = new ThemYeuThichRequest(100L);
        when(sanPhamYeuThichService.themVaoYeuThich(1L, 100L)).thenReturn(sanPhamYeuThichMau);

        mockMvc.perform(post("/api/khach-hang/yeu-thich")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maSanPhamId", is(100)));
    }

    @Test
    @DisplayName("POST /api/khach-hang/yeu-thich: Dữ liệu body rỗng trả về 400")
    void themVaoYeuThich_BodyRong_TraVe400() throws Exception {
        ThemYeuThichRequest req = new ThemYeuThichRequest(null);

        mockMvc.perform(post("/api/khach-hang/yeu-thich")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.maTrangThai", is(400)));
    }

    @Test
    @DisplayName("DELETE /api/khach-hang/yeu-thich/{id}: Xóa khỏi yêu thích thành công")
    void xoaKhoiYeuThich_ThanhCong() throws Exception {
        when(sanPhamYeuThichService.xoaKhoiYeuThich(1L, 100L))
                .thenReturn(new ThongBaoResponse("Đã xóa sản phẩm khỏi danh sách yêu thích"));

        mockMvc.perform(delete("/api/khach-hang/yeu-thich/100")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thongDiep", is("Đã xóa sản phẩm khỏi danh sách yêu thích")));
    }

    @Test
    @DisplayName("DELETE /api/khach-hang/yeu-thich/{id}: Không tìm thấy sản phẩm trả về 404")
    void xoaKhoiYeuThich_KhongTimThay_TraVe404() throws Exception {
        when(sanPhamYeuThichService.xoaKhoiYeuThich(1L, 999L))
                .thenThrow(new KhongTimThayException("Sản phẩm chưa có trong danh sách yêu thích của bạn"));

        mockMvc.perform(delete("/api/khach-hang/yeu-thich/999")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.maTrangThai", is(404)));
    }

    @Test
    @DisplayName("POST /api/khach-hang/yeu-thich/toggle/{id}: Toggle yêu thích thành công")
    void toggleYeuThich_ThanhCong() throws Exception {
        TrangThaiYeuThichResponse tt = new TrangThaiYeuThichResponse(100L, true, "Đã thêm sản phẩm vào danh sách yêu thích");
        when(sanPhamYeuThichService.toggleYeuThich(1L, 100L)).thenReturn(tt);

        mockMvc.perform(post("/api/khach-hang/yeu-thich/toggle/100")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.daYeuThich", is(true)))
                .andExpect(jsonPath("$.maSanPhamId", is(100)));
    }

    @Test
    @DisplayName("GET /api/khach-hang/yeu-thich/kiem-tra/{id}: Kiểm tra trạng thái yêu thích")
    void kiemTraDaYeuThich_ThanhCong() throws Exception {
        when(sanPhamYeuThichService.kiemTraDaYeuThich(1L, 100L)).thenReturn(true);

        mockMvc.perform(get("/api/khach-hang/yeu-thich/kiem-tra/100")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.daYeuThich", is(true)))
                .andExpect(jsonPath("$.maSanPhamId", is(100)));
    }

    @Test
    @DisplayName("GET /api/khach-hang/yeu-thich/dem: Đếm số lượng sản phẩm yêu thích")
    void demSoLuongYeuThich_ThanhCong() throws Exception {
        when(sanPhamYeuThichService.demSoLuongYeuThich(1L)).thenReturn(3L);

        mockMvc.perform(get("/api/khach-hang/yeu-thich/dem")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(3)));
    }
}
