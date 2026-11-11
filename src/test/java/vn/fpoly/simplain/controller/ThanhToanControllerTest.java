package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.dto.ThemGioHangRequest;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;
import vn.fpoly.simplain.service.GioHangService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động tính năng Checkout đơn hàng (US19).
 * - Tiêu chí 1: Tính Tổng tiền = Hàng - Voucher - Điểm + Ship.
 * - Tiêu chí 2: Xác nhận địa chỉ và phương thức thanh toán.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Checkout đơn hàng & Tính tiền (US19)")
class ThanhToanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private ChiTietGioHangRepository chiTietGioHangRepository;

    @Autowired
    private GioHangService gioHangService;

    private PhuongThucThanhToan ptttTest;
    private BienTheSanPham bienTheTest;
    private GioHang gioHangTest;

    private static final String MA_PTTT_TEST = "CHECKOUT_COD_TEST";
    private static final String MA_SKU_TEST = "SKU-CHECKOUT-TEST";

    @BeforeEach
    void setUp() {
        // 1. Tạo Cổng thanh toán Test (US18)
        ptttTest = phuongThucThanhToanRepository.findByMaPhuongThucIgnoreCase(MA_PTTT_TEST)
                .orElseGet(() -> phuongThucThanhToanRepository.save(PhuongThucThanhToan.builder()
                        .maPhuongThuc(MA_PTTT_TEST)
                        .tenPhuongThuc("Thanh toán Checkout Test")
                        .laTrucTuyen(false)
                        .kichHoat(true)
                        .moTa("Cổng thanh toán test")
                        .bieuTuong("fa-solid fa-money-bill")
                        .build()));

        // 2. Tạo hoặc lấy một Biến thể có sẵn để test
        Optional<BienTheSanPham> optBt = bienTheSanPhamRepository.findAll().stream()
                .filter(b -> b.getSoLuong() != null && b.getSoLuong() > 5)
                .findFirst();

        if (optBt.isPresent()) {
            bienTheTest = optBt.get();
        } else {
            bienTheTest = bienTheSanPhamRepository.save(BienTheSanPham.builder()
                    .maSanPhamId(1L)
                    .maMauSacId(1L)
                    .maKichThuocId(1L)
                    .maSku(MA_SKU_TEST)
                    .soLuong(50)
                    .giaBan(new BigDecimal("200000"))
                    .giaGoc(new BigDecimal("250000"))
                    .kichHoat(true)
                    .build());
        }

        // 3. Tạo giỏ hàng vãng lai test
        gioHangTest = gioHangRepository.save(GioHang.builder()
                .maKhachHangId(null)
                .build());

        // Thêm biến thể vào giỏ
        gioHangService.themBienTheVaoGio(null, gioHangTest.getId(),
                ThemGioHangRequest.builder()
                        .maBienTheId(bienTheTest.getId())
                        .soLuong(2)
                        .build());
    }

    @AfterEach
    void tearDown() {
        try {
            if (gioHangTest != null && gioHangTest.getId() != null) {
                chiTietGioHangRepository.deleteByMaGioHangId(gioHangTest.getId());
                gioHangRepository.deleteById(gioHangTest.getId());
            }
        } catch (Exception ignored) {
        }
    }

    // =========================================================================
    // 1. KIỂM THỬ HIỂN THỊ MÀN HÌNH CHECKOUT (GET /thanh-toan)
    // =========================================================================

    @Test
    @DisplayName("TC1 - Màn hình Checkout hiển thị thành công khi có giỏ hàng")
    void hienThiTrangThanhToan_coGioHang_thanhCong() throws Exception {
        mockMvc.perform(get("/thanh-toan")
                        .cookie(new Cookie("simplain_cart_id", String.valueOf(gioHangTest.getId()))))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/thanh-toan"))
                .andExpect(model().attributeExists("gioHang"))
                .andExpect(model().attributeExists("dsPhuongThuc"))
                .andExpect(model().attributeExists("chiPhi"))
                .andExpect(model().attributeExists("datHangRequest"));
    }

    @Test
    @DisplayName("TC2 - Giỏ hàng rỗng tự động chuyển hướng về trang Giỏ hàng")
    void hienThiTrangThanhToan_gioHangRong_redirectVeGioHang() throws Exception {
        // Tạo giỏ rỗng
        GioHang gioHangRong = gioHangRepository.save(GioHang.builder().maKhachHangId(null).build());

        mockMvc.perform(get("/thanh-toan")
                        .cookie(new Cookie("simplain_cart_id", String.valueOf(gioHangRong.getId()))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gio-hang"))
                .andExpect(flash().attributeExists("thongBaoLoi"));

        gioHangRepository.delete(gioHangRong);
    }

    // =========================================================================
    // 2. KIỂM THỬ TÍNH TOÁN CHI PHÍ ĐỘNG (AC 1: Tổng = Hàng - Voucher - Điểm + Ship)
    // =========================================================================

    @Test
    @DisplayName("TC3 - Tính toán chi phí: Áp dụng mã SIMPLAIN10 giảm 10%")
    void tinhPhiDong_apDungVoucher10_thanhCong() throws Exception {
        mockMvc.perform(post("/thanh-toan/tinh-phi")
                        .cookie(new Cookie("simplain_cart_id", String.valueOf(gioHangTest.getId())))
                        .param("maVoucher", "SIMPLAIN10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hopLe", is(true)))
                .andExpect(jsonPath("$.tienGiamGia").isNumber())
                .andExpect(jsonPath("$.tongThanhToan").isNumber());
    }

    @Test
    @DisplayName("TC4 - Tính toán chi phí: Áp dụng mã FREESHIP phí vận chuyển bằng 0đ")
    void tinhPhiDong_apDungFreeship_thanhCong() throws Exception {
        mockMvc.perform(post("/thanh-toan/tinh-phi")
                        .cookie(new Cookie("simplain_cart_id", String.valueOf(gioHangTest.getId())))
                        .param("maVoucher", "FREESHIP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hopLe", is(true)))
                .andExpect(jsonPath("$.phiVanChuyen", is(0)));
    }

    // =========================================================================
    // 3. KIỂM THỬ ĐẶT HÀNG / CHECKOUT (POST /thanh-toan/dat-hang)
    // =========================================================================

    @Test
    @DisplayName("TC5 - Đặt hàng thành công: Lưu đơn hàng, trừ tồn kho và dọn giỏ")
    void datHang_thanhCong() throws Exception {
        int tonKhoTruoc = bienTheTest.getSoLuong();

        mockMvc.perform(post("/thanh-toan/dat-hang")
                        .cookie(new Cookie("simplain_cart_id", String.valueOf(gioHangTest.getId())))
                        .param("tenNguoiNhan", "Người Test Simplain")
                        .param("soDienThoaiNhan", "0912345678")
                        .param("diaChiGiaoHang", "Số 123 Đường Cầu Giấy, Hà Nội")
                        .param("ghiChu", "Giao trong giờ hành chính")
                        .param("phuongThucThanhToanId", String.valueOf(ptttTest.getId()))
                        .param("maVoucher", "SIMPLAIN10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/thanh-toan/thanh-cong/**"));

        // Kiểm tra tồn kho đã bị trừ đúng 2 cái
        BienTheSanPham btCapNhat = bienTheSanPhamRepository.findById(bienTheTest.getId()).orElseThrow();
        assertEquals(tonKhoTruoc - 2, btCapNhat.getSoLuong());

        // Kiểm tra giỏ hàng đã được dọn sạch
        long soLuongTrongGio = chiTietGioHangRepository.countByMaGioHangId(gioHangTest.getId());
        assertEquals(0, soLuongTrongGio);
    }

    @Test
    @DisplayName("TC6 - Đặt hàng thất bại khi thiếu thông tin người nhận")
    void datHang_thieuThongTin_thatBai() throws Exception {
        mockMvc.perform(post("/thanh-toan/dat-hang")
                        .cookie(new Cookie("simplain_cart_id", String.valueOf(gioHangTest.getId())))
                        .param("tenNguoiNhan", "") // để trống tên
                        .param("soDienThoaiNhan", "")
                        .param("diaChiGiaoHang", "")
                        .param("phuongThucThanhToanId", String.valueOf(ptttTest.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/thanh-toan"))
                .andExpect(flash().attributeExists("thongBaoLoi"));
    }

    @Test
    @DisplayName("TC7 - Bỏ chọn Voucher: Tính toán lại chi phí khi hủy voucher (maVoucher rỗng)")
    void tinhPhiAsync_boChonVoucher_khongConGiamGia() throws Exception {
        // Khi bỏ chọn voucher (maVoucher rỗng), tiền giảm giá phải bằng 0 và tổng thanh toán hoàn lại đầy đủ
        mockMvc.perform(post("/thanh-toan/tinh-phi")
                        .cookie(new Cookie("simplain_cart_id", String.valueOf(gioHangTest.getId())))
                        .param("maVoucher", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tienGiamGia").value(0))
                .andExpect(jsonPath("$.tongThanhToan").value(500000.0));
    }
}
