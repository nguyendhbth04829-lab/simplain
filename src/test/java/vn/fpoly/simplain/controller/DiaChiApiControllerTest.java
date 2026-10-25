/**
 * Tác giả: Gia Minh
 * Chức năng: US13 - Kiểm thử tầng REST API DiaChiApiController
 * Cập nhật xác nhận commit US13: 25/10/2026 - Gia Minh
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
import vn.fpoly.simplain.dto.CapNhatDiaChiRequest;
import vn.fpoly.simplain.dto.DiaChiResponse;
import vn.fpoly.simplain.dto.ThemDiaChiRequest;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.exception.ChuaDangNhapException;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.exception.XuLyLoiHeThong;
import vn.fpoly.simplain.service.DanhMucHanhChinhService;
import vn.fpoly.simplain.service.DiaChiService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử tầng REST API DiaChiApiController (US13)")
class DiaChiApiControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private DiaChiService diaChiService;

    @Mock
    private DanhMucHanhChinhService danhMucHanhChinhService;

    @InjectMocks
    private DiaChiApiController diaChiApiController;

    private DiaChiResponse diaChiResponseMau;

    @BeforeEach
    void khoiTao() {
        mockMvc = MockMvcBuilders.standaloneSetup(diaChiApiController)
                .setControllerAdvice(new XuLyLoiHeThong())
                .build();

        objectMapper = new ObjectMapper();

        diaChiResponseMau = new DiaChiResponse(
                10L,
                1L,
                "Nguyễn Văn A",
                "0987654321",
                "Hà Nội",
                "Quận Cầu Giấy",
                "Phường Dịch Vọng",
                "Số 123 đường Xuân Thủy",
                "Số 123 đường Xuân Thủy, Phường Dịch Vọng, Quận Cầu Giấy, Hà Nội",
                true
        );
    }

    @Test
    @DisplayName("GET /api/khach-hang/dia-chi: Lấy danh sách thành công")
    void layDanhSach_ThanhCong() throws Exception {
        when(diaChiService.layDanhSachDiaChi(1L)).thenReturn(List.of(diaChiResponseMau));

        mockMvc.perform(get("/api/khach-hang/dia-chi")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tenNguoiNhan", is("Nguyễn Văn A")))
                .andExpect(jsonPath("$[0].tinhThanh", is("Hà Nội")))
                .andExpect(jsonPath("$[0].laMacDinh", is(true)));
    }

    @Test
    @DisplayName("GET /api/khach-hang/dia-chi: Chưa đăng nhập trả về 401")
    void layDanhSach_ChuaDangNhap_TraVe401() throws Exception {
        when(diaChiService.layDanhSachDiaChi(null))
                .thenThrow(new ChuaDangNhapException("Vui lòng đăng nhập"));

        mockMvc.perform(get("/api/khach-hang/dia-chi"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.maTrangThai", is(401)));
    }

    @Test
    @DisplayName("GET /api/khach-hang/dia-chi/{id}: Xem chi tiết thành công")
    void layChiTiet_ThanhCong() throws Exception {
        when(diaChiService.layChiTietDiaChi(1L, 10L)).thenReturn(diaChiResponseMau);

        mockMvc.perform(get("/api/khach-hang/dia-chi/10")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.soDienThoai", is("0987654321")));
    }

    @Test
    @DisplayName("GET /api/khach-hang/dia-chi/{id}: Không tìm thấy trả về 404")
    void layChiTiet_KhongTimThay_TraVe404() throws Exception {
        when(diaChiService.layChiTietDiaChi(1L, 999L))
                .thenThrow(new KhongTimThayException("Không tìm thấy địa chỉ"));

        mockMvc.perform(get("/api/khach-hang/dia-chi/999")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.maTrangThai", is(404)));
    }

    @Test
    @DisplayName("POST /api/khach-hang/dia-chi: Thêm địa chỉ mới thành công trả về 201")
    void themMoi_ThanhCong() throws Exception {
        ThemDiaChiRequest req = new ThemDiaChiRequest(
                "Nguyễn Văn A",
                "0987654321",
                "Hà Nội",
                "Quận Cầu Giấy",
                "Phường Dịch Vọng",
                "Số 123",
                true
        );

        when(diaChiService.themDiaChi(eq(1L), any(ThemDiaChiRequest.class))).thenReturn(diaChiResponseMau);

        mockMvc.perform(post("/api/khach-hang/dia-chi")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.tenNguoiNhan", is("Nguyễn Văn A")));
    }

    @Test
    @DisplayName("POST /api/khach-hang/dia-chi: Dữ liệu validation không hợp lệ trả về 400")
    void themMoi_ValidationLoi_TraVe400() throws Exception {
        ThemDiaChiRequest req = new ThemDiaChiRequest(
                "", // Để trống tên
                "123", // SĐT sai định dạng
                "",
                "",
                "",
                "",
                false
        );

        mockMvc.perform(post("/api/khach-hang/dia-chi")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.maTrangThai", is(400)));
    }

    @Test
    @DisplayName("PUT /api/khach-hang/dia-chi/{id}: Cập nhật địa chỉ thành công")
    void capNhat_ThanhCong() throws Exception {
        CapNhatDiaChiRequest req = new CapNhatDiaChiRequest(
                "Trần Thị C",
                "0909090909",
                "Hà Nội",
                "Quận Cầu Giấy",
                "Phường Dịch Vọng",
                "Số 456",
                false
        );

        when(diaChiService.capNhatDiaChi(eq(1L), eq(10L), any(CapNhatDiaChiRequest.class)))
                .thenReturn(diaChiResponseMau);

        mockMvc.perform(put("/api/khach-hang/dia-chi/10")
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    @DisplayName("DELETE /api/khach-hang/dia-chi/{id}: Xóa thành công")
    void xoa_ThanhCong() throws Exception {
        when(diaChiService.xoaDiaChi(1L, 10L))
                .thenReturn(new ThongBaoResponse("Xóa địa chỉ nhận hàng thành công"));

        mockMvc.perform(delete("/api/khach-hang/dia-chi/10")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thongDiep", is("Xóa địa chỉ nhận hàng thành công")));
    }

    @Test
    @DisplayName("PATCH /api/khach-hang/dia-chi/{id}/mac-dinh: Đặt mặc định thành công")
    void datLamMacDinh_ThanhCong() throws Exception {
        when(diaChiService.datLamMacDinh(1L, 10L)).thenReturn(diaChiResponseMau);

        mockMvc.perform(patch("/api/khach-hang/dia-chi/10/mac-dinh")
                        .header("X-Customer-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.laMacDinh", is(true)));
    }

    @Test
    @DisplayName("GET /api/khach-hang/dia-chi/danh-muc/tinh-thanh: Lấy danh mục tỉnh thành công")
    void layDanhSachTinhThanh_ThanhCong() throws Exception {
        when(danhMucHanhChinhService.layDanhSachTinhThanh()).thenReturn(List.of("Hà Nội", "Đà Nẵng", "TP Hồ Chí Minh"));

        mockMvc.perform(get("/api/khach-hang/dia-chi/danh-muc/tinh-thanh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Hà Nội")));
    }

    @Test
    @DisplayName("GET /api/khach-hang/dia-chi/danh-muc/quan-huyen: Lấy danh mục quận huyện theo tỉnh")
    void layDanhSachQuanHuyen_ThanhCong() throws Exception {
        when(danhMucHanhChinhService.layDanhSachQuanHuyen("Hà Nội")).thenReturn(List.of("Quận Ba Đình", "Quận Cầu Giấy"));

        mockMvc.perform(get("/api/khach-hang/dia-chi/danh-muc/quan-huyen")
                        .param("tinhThanh", "Hà Nội"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1]", is("Quận Cầu Giấy")));
    }

    @Test
    @DisplayName("GET /api/khach-hang/dia-chi/danh-muc/phuong-xa: Lấy danh mục phường xã")
    void layDanhSachPhuongXa_ThanhCong() throws Exception {
        when(danhMucHanhChinhService.layDanhSachPhuongXa("Hà Nội", "Quận Cầu Giấy"))
                .thenReturn(List.of("Phường Dịch Vọng", "Phường Quan Hoa"));

        mockMvc.perform(get("/api/khach-hang/dia-chi/danh-muc/phuong-xa")
                        .param("tinhThanh", "Hà Nội")
                        .param("quanHuyen", "Quận Cầu Giấy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is("Phường Dịch Vọng")));
    }
}
