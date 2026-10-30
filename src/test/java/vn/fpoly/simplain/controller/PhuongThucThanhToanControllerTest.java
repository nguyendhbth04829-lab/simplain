package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.entity.PhuongThucThanhToan;
import vn.fpoly.simplain.repository.PhuongThucThanhToanRepository;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động Quản lý Cổng thanh toán (US18).
 * - Tiêu chí 1: CRUD bảng phuong_thuc_thanh_toan (COD, VNPay, MoMo).
 * - Tính năng cốt lõi: Bật/Tắt cấu hình cổng thanh toán (đơn lẻ và hàng loạt).
 * Chạy lệnh: .\mvnw.cmd -Dtest=PhuongThucThanhToanControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Cổng thanh toán & Bật/Tắt kích hoạt (US18)")
class PhuongThucThanhToanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PhuongThucThanhToanRepository repository;

    private static final String MA_TEST_1 = "ZALOPAY_TEST";
    private static final String MA_TEST_2 = "APPLE_PAY_TEST";

    private PhuongThucThanhToan ptttTest1;
    private PhuongThucThanhToan ptttTest2;

    @BeforeEach
    void setUp() {
        // Tạo 2 phương thức thanh toán thử nghiệm
        ptttTest1 = repository.save(PhuongThucThanhToan.builder()
                .maPhuongThuc(MA_TEST_1)
                .tenPhuongThuc("Ví điện tử ZaloPay Test")
                .laTrucTuyen(true)
                .kichHoat(true)
                .moTa("Thanh toán quét mã ZaloPay")
                .bieuTuong("fa-solid fa-wallet text-sky-500")
                .cauHinh("{\"app_id\": \"12345\", \"sandbox\": true}")
                .build());

        ptttTest2 = repository.save(PhuongThucThanhToan.builder()
                .maPhuongThuc(MA_TEST_2)
                .tenPhuongThuc("Apple Pay Test")
                .laTrucTuyen(true)
                .kichHoat(false)
                .moTa("Thanh toán một chạm qua thiết bị Apple")
                .bieuTuong("fa-brands fa-apple text-slate-800")
                .cauHinh("{\"merchant_id\": \"merchant.vn.simplain\"}")
                .build());
    }

    @AfterEach
    void tearDown() {
        try {
            repository.findByMaPhuongThucIgnoreCase(MA_TEST_1).ifPresent(p -> repository.deleteById(p.getId()));
            repository.findByMaPhuongThucIgnoreCase(MA_TEST_2).ifPresent(p -> repository.deleteById(p.getId()));
            repository.findByMaPhuongThucIgnoreCase("SHOPEEPAY_TEST").ifPresent(p -> repository.deleteById(p.getId()));
        } catch (Exception ignored) {
        }
    }

    // =========================================================================
    // 1. KIỂM THỬ XEM DANH SÁCH (READ TRONG CRUD)
    // =========================================================================

    @Test
    @DisplayName("TC1 - Hiển thị danh sách Phương thức thanh toán thành công (GET /admin/phuong-thuc-thanh-toan/hien-thi)")
    void hienThiDanhSach_thanhCong() throws Exception {
        mockMvc.perform(get("/admin/phuong-thuc-thanh-toan/hien-thi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/phuong-thuc-thanh-toan/hien-thi"))
                .andExpect(model().attributeExists("danhSachPhuongThuc"))
                .andExpect(model().attributeExists("tongSo"))
                .andExpect(model().attributeExists("soDangBat"))
                .andExpect(model().attributeExists("soDangTat"))
                .andExpect(model().attribute("menuActive", is("phuong-thuc-thanh-toan")));
    }

    // =========================================================================
    // 2. KIỂM THỬ THÊM MỚI (CREATE TRONG CRUD)
    // =========================================================================

    @Test
    @DisplayName("TC2.1 - Thêm mới phương thức thanh toán thành công")
    void themPhuongThucThanhToan_thanhCong() throws Exception {
        mockMvc.perform(post("/admin/phuong-thuc-thanh-toan/them")
                        .param("maPhuongThuc", "SHOPEEPAY_TEST")
                        .param("tenPhuongThuc", "Ví điện tử ShopeePay Test")
                        .param("laTrucTuyen", "true")
                        .param("kichHoat", "true")
                        .param("moTa", "Thanh toán giảm giá qua ShopeePay")
                        .param("bieuTuong", "fa-solid fa-wallet text-orange-500")
                        .param("cauHinh", "{\"client_id\": \"shopee_01\"}"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/phuong-thuc-thanh-toan/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertTrue(repository.existsByMaPhuongThucIgnoreCase("SHOPEEPAY_TEST"));
    }

    @Test
    @DisplayName("TC2.2 - Thêm thất bại khi trùng mã phương thức đã có")
    void themPhuongThucThanhToan_thatBai_khiTrungMa() throws Exception {
        // MA_TEST_1 đã tồn tại trong setUp
        mockMvc.perform(post("/admin/phuong-thuc-thanh-toan/them")
                        .param("maPhuongThuc", MA_TEST_1)
                        .param("tenPhuongThuc", "Tên Trùng")
                        .param("laTrucTuyen", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/phuong-thuc-thanh-toan/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoLoi"));
    }

    // =========================================================================
    // 3. KIỂM THỬ CHỈNH SỬA & CẤU HÌNH (UPDATE TRONG CRUD)
    // =========================================================================

    @Test
    @DisplayName("TC3 - Cập nhật cấu hình cổng thanh toán thành công")
    void suaPhuongThucThanhToan_thanhCong() throws Exception {
        mockMvc.perform(post("/admin/phuong-thuc-thanh-toan/sua/" + ptttTest1.getId())
                        .param("maPhuongThuc", MA_TEST_1)
                        .param("tenPhuongThuc", "ZaloPay QR Đã Cập Nhật")
                        .param("laTrucTuyen", "true")
                        .param("kichHoat", "false")
                        .param("moTa", "Mô tả mới")
                        .param("cauHinh", "{\"app_id\": \"99999\", \"sandbox\": false}"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/phuong-thuc-thanh-toan/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        PhuongThucThanhToan sauSua = repository.findById(ptttTest1.getId()).orElseThrow();
        assertEquals("ZaloPay QR Đã Cập Nhật", sauSua.getTenPhuongThuc());
        assertFalse(sauSua.getKichHoat());
        assertTrue(sauSua.getCauHinh().contains("99999"));
    }

    // =========================================================================
    // 4. KIỂM THỬ TÍNH NĂNG BẬT/TẮT CỔNG THANH TOÁN (US18 CORE FEATURE)
    // =========================================================================

    @Test
    @DisplayName("TC4.1 - Bật/Tắt cổng thanh toán qua AJAX 1-click thành công")
    void batTatKichHoat_quaAjax_thanhCong() throws Exception {
        // Ban đầu ptttTest1.kichHoat = true -> Bấm gạt switch sẽ chuyển thành false
        mockMvc.perform(post("/admin/phuong-thuc-thanh-toan/toggle/" + ptttTest1.getId())
                        .header("X-Requested-With", "XMLHttpRequest")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.kichHoat", is(false)));

        PhuongThucThanhToan sauGat = repository.findById(ptttTest1.getId()).orElseThrow();
        assertFalse(sauGat.getKichHoat(), "Trạng thái kích hoạt trong DB phải chuyển sang false (Đang Tắt)");

        // Gạt switch lần 2 -> Chuyển ngược lại thành true
        mockMvc.perform(post("/admin/phuong-thuc-thanh-toan/toggle/" + ptttTest1.getId())
                        .header("X-Requested-With", "XMLHttpRequest")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.kichHoat", is(true)));

        PhuongThucThanhToan sauGat2 = repository.findById(ptttTest1.getId()).orElseThrow();
        assertTrue(sauGat2.getKichHoat(), "Trạng thái kích hoạt trong DB phải chuyển sang true (Đang Bật)");
    }

    @Test
    @DisplayName("TC4.1b - Đổi trạng thái cổng thanh toán qua GET /doi-trang-thai giống các chức năng khác")
    void doiTrangThai_quaGetRequest_thanhCong() throws Exception {
        // ptttTest1 đang kichHoat = true -> gọi /doi-trang-thai sẽ chuyển sang false
        mockMvc.perform(get("/admin/phuong-thuc-thanh-toan/doi-trang-thai")
                        .param("id", String.valueOf(ptttTest1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/phuong-thuc-thanh-toan/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertFalse(repository.findById(ptttTest1.getId()).orElseThrow().getKichHoat());
    }

    @Test
    @DisplayName("TC4.2 - Thao tác hàng loạt: Bật và Tắt kích hoạt nhiều cổng thanh toán")
    void thaoTacHangLoat_batVaTatHangLoat_thanhCong() throws Exception {
        List<Long> ids = Arrays.asList(ptttTest1.getId(), ptttTest2.getId());

        // 1. Tắt hàng loạt
        mockMvc.perform(post("/admin/phuong-thuc-thanh-toan/thao-tac-hang-loat")
                        .param("ids", String.valueOf(ptttTest1.getId()), String.valueOf(ptttTest2.getId()))
                        .param("hanhDong", "tat"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertFalse(repository.findById(ptttTest1.getId()).orElseThrow().getKichHoat());
        assertFalse(repository.findById(ptttTest2.getId()).orElseThrow().getKichHoat());

        // 2. Bật hàng loạt
        mockMvc.perform(post("/admin/phuong-thuc-thanh-toan/thao-tac-hang-loat")
                        .param("ids", String.valueOf(ptttTest1.getId()), String.valueOf(ptttTest2.getId()))
                        .param("hanhDong", "bat"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertTrue(repository.findById(ptttTest1.getId()).orElseThrow().getKichHoat());
        assertTrue(repository.findById(ptttTest2.getId()).orElseThrow().getKichHoat());
    }

    // =========================================================================
    // 5. KIỂM THỬ XÓA & API LẤY CHI TIẾT (DELETE & API)
    // =========================================================================

    @Test
    @DisplayName("TC5.1 - Xóa phương thức thanh toán thành công khi chưa phát sinh giao dịch")
    void xoaPhuongThucThanhToan_thanhCong() throws Exception {
        Long idCanXoa = ptttTest2.getId();
        mockMvc.perform(post("/admin/phuong-thuc-thanh-toan/xoa/" + idCanXoa))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/phuong-thuc-thanh-toan/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertFalse(repository.existsById(idCanXoa), "Phương thức thanh toán phải bị xóa khỏi hệ thống");
    }

    @Test
    @DisplayName("TC5.2 - API lấy chi tiết cổng thanh toán dạng JSON phục vụ Modal Sửa (GET /api/{id})")
    void apiLayChiTiet_thanhCong() throws Exception {
        mockMvc.perform(get("/admin/phuong-thuc-thanh-toan/api/" + ptttTest1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ptttTest1.getId().intValue())))
                .andExpect(jsonPath("$.maPhuongThuc", is(MA_TEST_1)))
                .andExpect(jsonPath("$.tenPhuongThuc", is("Ví điện tử ZaloPay Test")))
                .andExpect(jsonPath("$.kichHoat", is(true)));
    }
}
