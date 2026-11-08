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

    @Autowired
    private KhachHangRepository khachHangRepository;

    private PhuongThucThanhToan ptttTest;
    private BienTheSanPham bienTheTest;
    private GioHang gioHangTest;
    private KhachHang khachHangTest;

    private static final String MA_PTTT_TEST = "CHECKOUT_COD_TEST";
    private static final String MA_SKU_TEST = "SKU-CHECKOUT-TEST";

    @BeforeEach
    void setUp() {
        // 1. Tạo Cổng thanh toán Test (US18)
        ptttTest = phuongThucThanhToanRepository.findByMaPhuongThucIgnoreCase(MA_PTTT_TEST)
                .orElseGet(() -> PhuongThucThanhToan.builder()
                        .maPhuongThuc(MA_PTTT_TEST)
                        .tenPhuongThuc("Thanh toán Checkout Test")
                        .laTrucTuyen(false)
                        .moTa("Cổng thanh toán test")
                        .bieuTuong("fa-solid fa-money-bill")
                        .build());
        ptttTest.setKichHoat(true);
        ptttTest = phuongThucThanhToanRepository.save(ptttTest);

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

        // 3. Tạo khách hàng và giỏ hàng test mới hoàn toàn cho mỗi lần test (tránh vướng đơn cũ bị chặn COD)
        khachHangTest = khachHangRepository.save(KhachHang.builder()
                .maKhachHang("KH-TEST-" + System.nanoTime())
                .hoTen("Khách Test Checkout")
                .email("test.checkout." + System.nanoTime() + "@simplain.vn")
                .soDienThoai("099" + String.format("%07d", Math.abs(System.nanoTime() % 10000000)))
                .matKhauHash("secret")
                .trangThai(1)
                .build());

        gioHangTest = gioHangRepository.save(GioHang.builder()
                .maKhachHangId(khachHangTest.getId())
                .build());
        chiTietGioHangRepository.deleteByMaGioHangId(gioHangTest.getId());

        // Thêm biến thể vào giỏ
        gioHangService.themBienTheVaoGio(khachHangTest.getId(), gioHangTest.getId(),
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
                        .sessionAttr("khachHangHienTai", khachHangTest)
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
        // Xóa sạch giỏ hàng để giỏ rỗng
        chiTietGioHangRepository.deleteByMaGioHangId(gioHangTest.getId());

        mockMvc.perform(get("/thanh-toan")
                        .sessionAttr("khachHangHienTai", khachHangTest)
                        .cookie(new Cookie("simplain_cart_id", String.valueOf(gioHangTest.getId()))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gio-hang"))
                .andExpect(flash().attributeExists("thongBaoLoi"));
    }

    // =========================================================================
    // 2. KIỂM THỬ TÍNH TOÁN CHI PHÍ ĐỘNG (AC 1: Tổng = Hàng - Voucher - Điểm + Ship)
    // =========================================================================

    @Test
    @DisplayName("TC3 - Tính toán chi phí: Áp dụng mã SIMPLAIN10 giảm 10%")
    void tinhPhiDong_apDungVoucher10_thanhCong() throws Exception {
        mockMvc.perform(post("/thanh-toan/tinh-phi")
                        .sessionAttr("khachHangHienTai", khachHangTest)
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
                        .sessionAttr("khachHangHienTai", khachHangTest)
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

        String sdtTest = "098" + String.format("%07d", Math.abs(System.nanoTime() % 10000000));
        mockMvc.perform(post("/thanh-toan/dat-hang")
                        .sessionAttr("khachHangHienTai", khachHangTest)
                        .cookie(new Cookie("simplain_cart_id", String.valueOf(gioHangTest.getId())))
                        .param("tenNguoiNhan", "Người Test Simplain")
                        .param("soDienThoaiNhan", sdtTest)
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
                        .sessionAttr("khachHangHienTai", khachHangTest)
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
                        .sessionAttr("khachHangHienTai", khachHangTest)
                        .cookie(new Cookie("simplain_cart_id", String.valueOf(gioHangTest.getId())))
                        .param("maVoucher", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tienGiamGia").value(0))
                .andExpect(jsonPath("$.tongThanhToan").isNumber());
    }

    @Test
    @DisplayName("TC8 - Hiển thị màn hình Gateway ZaloPay và xử lý Return thành công")
    void hienThiZaloPayGateway_vaReturn_thanhCong() throws Exception {
        mockMvc.perform(get("/thanh-toan/zalopay-gateway")
                        .param("apptransid", "261031_123456")
                        .param("amount", "500000")
                        .param("maDonHang", "DH123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/zalopay-gateway"))
                .andExpect(model().attribute("apptransid", is("261031_123456")))
                .andExpect(model().attribute("amount", is(500000L)));

        mockMvc.perform(get("/thanh-toan/zalopay-return")
                        .param("status", "1")
                        .param("apptransid", "261031_123456")
                        .param("maDonHang", "DH_TEST_NOT_FOUND"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/thanh-toan/thanh-cong/DH_TEST_NOT_FOUND"));
    }

    @Test
    @DisplayName("TC9 - Mua ngay 1 sản phẩm: Chỉ mua sản phẩm đang xem, giỏ hàng hiện tại được giữ nguyên")
    void muaNgay_chiMuaSanPhamDangXem_vaKhongXoaGioHang() throws Exception {
        long soLuongTrongGioTruoc = chiTietGioHangRepository.countByMaGioHangId(gioHangTest.getId());
        assertTrue(soLuongTrongGioTruoc > 0);

        String sdtTest = "097" + String.format("%07d", Math.abs(System.nanoTime() % 10000000));
        mockMvc.perform(post("/thanh-toan/dat-hang")
                        .sessionAttr("khachHangHienTai", khachHangTest)
                        .cookie(new Cookie("simplain_cart_id", String.valueOf(gioHangTest.getId())))
                        .param("tenNguoiNhan", "Người Mua Ngay")
                        .param("soDienThoaiNhan", sdtTest)
                        .param("diaChiGiaoHang", "Số 456 Kim Mã, Ba Đình, Hà Nội")
                        .param("phuongThucThanhToanId", String.valueOf(ptttTest.getId()))
                        .param("bienTheId", String.valueOf(bienTheTest.getId()))
                        .param("soLuong", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/thanh-toan/thanh-cong/**"));

        // Kiểm tra giỏ hàng KHÔNG bị xóa, vẫn còn nguyên
        long soLuongTrongGioSau = chiTietGioHangRepository.countByMaGioHangId(gioHangTest.getId());
        assertEquals(soLuongTrongGioTruoc, soLuongTrongGioSau);
    }
}
