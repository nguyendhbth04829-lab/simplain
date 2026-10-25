/**
 * Tác giả: Gia Minh
 * Chức năng: US13 - Kiểm thử tầng giao diện Web DiaChiController
 * Cập nhật xác nhận commit US13: 25/10/2026 - Gia Minh
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
import vn.fpoly.simplain.dto.CapNhatDiaChiRequest;
import vn.fpoly.simplain.dto.DiaChiResponse;
import vn.fpoly.simplain.dto.ThemDiaChiRequest;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.service.DanhMucHanhChinhService;
import vn.fpoly.simplain.service.DiaChiService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử tầng Giao diện Web DiaChiController (US13)")
class DiaChiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DiaChiService diaChiService;

    @Mock
    private DanhMucHanhChinhService danhMucHanhChinhService;

    @InjectMocks
    private DiaChiController diaChiController;

    private DiaChiResponse diaChiMau;

    @BeforeEach
    void khoiTao() {
        mockMvc = MockMvcBuilders.standaloneSetup(diaChiController).build();

        diaChiMau = new DiaChiResponse(
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
    @DisplayName("GET /khach-hang/dia-chi: Hiển thị trang danh sách địa chỉ thành công")
    void hienThiDanhSach_ThanhCong() throws Exception {
        when(diaChiService.layDanhSachDiaChi(1L)).thenReturn(List.of(diaChiMau));

        mockMvc.perform(get("/khach-hang/dia-chi").param("idKhachHang", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/dia-chi/danh-sach"))
                .andExpect(model().attributeExists("danhSachDiaChi"))
                .andExpect(model().attribute("idKhachHang", 1L));
    }

    @Test
    @DisplayName("GET /khach-hang/dia-chi/them: Hiển thị form thêm mới địa chỉ")
    void hienThiFormThem_ThanhCong() throws Exception {
        when(danhMucHanhChinhService.layDanhSachTinhThanh()).thenReturn(List.of("Hà Nội", "Đà Nẵng"));

        mockMvc.perform(get("/khach-hang/dia-chi/them").param("idKhachHang", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/dia-chi/form"))
                .andExpect(model().attribute("laThemMoi", true))
                .andExpect(model().attributeExists("diaChiRequest"))
                .andExpect(model().attributeExists("danhSachTinh"));
    }

    @Test
    @DisplayName("POST /khach-hang/dia-chi/them: Submit form hợp lệ thì chuyển hướng về danh sách")
    void xuLyThemMoi_HopLe_ChuyenHuong() throws Exception {
        when(diaChiService.themDiaChi(eq(1L), any(ThemDiaChiRequest.class))).thenReturn(diaChiMau);

        mockMvc.perform(post("/khach-hang/dia-chi/them")
                        .param("idKhachHang", "1")
                        .param("tenNguoiNhan", "Nguyễn Văn A")
                        .param("soDienThoai", "0987654321")
                        .param("tinhThanh", "Hà Nội")
                        .param("quanHuyen", "Quận Cầu Giấy")
                        .param("phuongXa", "Phường Dịch Vọng")
                        .param("diaChiChiTiet", "Số 123 đường Xuân Thủy")
                        .param("laMacDinh", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/dia-chi?idKhachHang=1"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));
    }

    @Test
    @DisplayName("POST /khach-hang/dia-chi/them: Dữ liệu không hợp lệ trả lại form hiển thị lỗi")
    void xuLyThemMoi_LoiValidation_TraLaiForm() throws Exception {
        mockMvc.perform(post("/khach-hang/dia-chi/them")
                        .param("idKhachHang", "1")
                        .param("tenNguoiNhan", "") // Trống tên
                        .param("soDienThoai", "123")) // Sai định dạng SĐT
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/dia-chi/form"))
                .andExpect(model().attributeHasFieldErrors("diaChiRequest", "tenNguoiNhan", "soDienThoai"));
    }

    @Test
    @DisplayName("GET /khach-hang/dia-chi/sua/{id}: Hiển thị form cập nhật địa chỉ")
    void hienThiFormSua_ThanhCong() throws Exception {
        when(diaChiService.layChiTietDiaChi(1L, 10L)).thenReturn(diaChiMau);
        when(danhMucHanhChinhService.layDanhSachTinhThanh()).thenReturn(List.of("Hà Nội"));
        when(danhMucHanhChinhService.layDanhSachQuanHuyen("Hà Nội")).thenReturn(List.of("Quận Cầu Giấy"));
        when(danhMucHanhChinhService.layDanhSachPhuongXa("Hà Nội", "Quận Cầu Giấy")).thenReturn(List.of("Phường Dịch Vọng"));

        mockMvc.perform(get("/khach-hang/dia-chi/sua/10").param("idKhachHang", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/dia-chi/form"))
                .andExpect(model().attribute("laThemMoi", false))
                .andExpect(model().attribute("idDiaChi", 10L))
                .andExpect(model().attributeExists("diaChiRequest"));
    }

    @Test
    @DisplayName("POST /khach-hang/dia-chi/sua/{id}: Submit form cập nhật hợp lệ thì chuyển hướng")
    void xuLyCapNhat_HopLe_ChuyenHuong() throws Exception {
        when(diaChiService.capNhatDiaChi(eq(1L), eq(10L), any(CapNhatDiaChiRequest.class))).thenReturn(diaChiMau);

        mockMvc.perform(post("/khach-hang/dia-chi/sua/10")
                        .param("idKhachHang", "1")
                        .param("tenNguoiNhan", "Nguyễn Văn A")
                        .param("soDienThoai", "0987654321")
                        .param("tinhThanh", "Hà Nội")
                        .param("quanHuyen", "Quận Cầu Giấy")
                        .param("phuongXa", "Phường Dịch Vọng")
                        .param("diaChiChiTiet", "Số 123 đường Xuân Thủy")
                        .param("laMacDinh", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/dia-chi?idKhachHang=1"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));
    }

    @Test
    @DisplayName("POST /khach-hang/dia-chi/xoa/{id}: Xử lý xóa địa chỉ thành công")
    void xuLyXoa_ThanhCong() throws Exception {
        when(diaChiService.xoaDiaChi(1L, 10L)).thenReturn(new ThongBaoResponse("Đã xóa địa chỉ thành công!"));

        mockMvc.perform(post("/khach-hang/dia-chi/xoa/10")
                        .param("idKhachHang", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/dia-chi?idKhachHang=1"))
                .andExpect(flash().attribute("thongBaoThanhCong", "Đã xóa địa chỉ thành công!"));

        verify(diaChiService).xoaDiaChi(1L, 10L);
    }

    @Test
    @DisplayName("POST /khach-hang/dia-chi/mac-dinh/{id}: Xử lý đặt địa chỉ làm mặc định thành công")
    void xuLyDatMacDinh_ThanhCong() throws Exception {
        when(diaChiService.datLamMacDinh(1L, 10L)).thenReturn(diaChiMau);

        mockMvc.perform(post("/khach-hang/dia-chi/mac-dinh/10")
                        .param("idKhachHang", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/dia-chi?idKhachHang=1"))
                .andExpect(flash().attribute("thongBaoThanhCong", "Đã đặt địa chỉ làm mặc định!"));

        verify(diaChiService).datLamMacDinh(1L, 10L);
    }
}
