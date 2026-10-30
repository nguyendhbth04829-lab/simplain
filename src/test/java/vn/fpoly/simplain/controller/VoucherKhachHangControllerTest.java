package vn.fpoly.simplain.controller;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.MaGiamGia;
import vn.fpoly.simplain.entity.VoucherSoHuu;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.MaGiamGiaRepository;
import vn.fpoly.simplain.repository.VoucherSoHuuRepository;
import vn.fpoly.simplain.util.JwtUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tích hợp Web MVC cho chức năng Khách hàng Thu thập và Áp dụng Mã giảm giá (US27 - Mod 6 / Voucher - Khoa).
 * - Tiêu chí 1: Lưu voucher vào voucher_so_huu (Ví voucher cá nhân).
 * - Tiêu chí 2: API Validate check mã còn lượt / có hợp lệ với đơn hàng không (Kèm thông báo lỗi rõ ràng).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Kiểm thử Web MVC Khách hàng Thu thập & Áp dụng Mã giảm giá (US27)")
class VoucherKhachHangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MaGiamGiaRepository maGiamGiaRepository;

    @Autowired
    private VoucherSoHuuRepository voucherSoHuuRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EntityManager entityManager;

    private KhachHang khachHangTest;
    private Cookie cookieKhachHang;

    private MaGiamGia voucherKhaDungPercent;
    private MaGiamGia voucherKhaDungFixed;
    private MaGiamGia voucherHetHan;
    private MaGiamGia voucherChuaDienRa;
    private MaGiamGia voucherHetLuot;
    private MaGiamGia voucherHetNganSach;
    private MaGiamGia voucherTamNgung;

    @BeforeEach
    void setUp() {
        long ts = System.currentTimeMillis();

        // 1. Khởi tạo khách hàng test
        String sdt = String.format("0977%06d", (ts % 1000000));
        khachHangTest = khachHangRepository.saveAndFlush(KhachHang.builder()
                .maKhachHang("KH-VOUCHER-" + ts)
                .hoTen("Nguyễn Test Voucher")
                .soDienThoai(sdt)
                .email("test.voucher." + ts + "@simplain.vn")
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build());

        cookieKhachHang = new Cookie(KhachHangController.COOKIE_TOKEN, jwtUtil.generateToken(khachHangTest));

        LocalDateTime now = LocalDateTime.now();

        // 2. Voucher hợp lệ loại PERCENT (Giảm 20% tối đa 50k, đơn từ 200k)
        voucherKhaDungPercent = maGiamGiaRepository.saveAndFlush(MaGiamGia.builder()
                .maCode("TEST_PCT_" + ts)
                .tenVoucher("Ưu đãi 20% đơn từ 200k")
                .loaiGiamGia("PERCENT")
                .giaTriGiam(BigDecimal.valueOf(20))
                .giamToiDa(BigDecimal.valueOf(50000))
                .giaTriDonToiThieu(BigDecimal.valueOf(200000))
                .tongSoLuong(100)
                .soLuongDaDung(5)
                .gioiHanMoiUser(1)
                .nganSach(BigDecimal.valueOf(10000000))
                .nganSachDaDung(BigDecimal.valueOf(200000))
                .ngayBatDau(now.minusDays(5))
                .ngayKetThuc(now.plusDays(15))
                .kichHoat(true)
                .build());

        // 3. Voucher hợp lệ loại FIXED_AMOUNT (Giảm 30k, đơn từ 150k)
        voucherKhaDungFixed = maGiamGiaRepository.saveAndFlush(MaGiamGia.builder()
                .maCode("TEST_FIX_" + ts)
                .tenVoucher("Giảm ngay 30k đơn từ 150k")
                .loaiGiamGia("FIXED_AMOUNT")
                .giaTriGiam(BigDecimal.valueOf(30000))
                .giaTriDonToiThieu(BigDecimal.valueOf(150000))
                .tongSoLuong(50)
                .soLuongDaDung(0)
                .gioiHanMoiUser(2)
                .nganSach(BigDecimal.valueOf(5000000))
                .nganSachDaDung(BigDecimal.ZERO)
                .ngayBatDau(now.minusDays(2))
                .ngayKetThuc(now.plusDays(20))
                .kichHoat(true)
                .build());

        // 4. Voucher đã hết hạn
        voucherHetHan = maGiamGiaRepository.saveAndFlush(MaGiamGia.builder()
                .maCode("TEST_EXPIRED_" + ts)
                .tenVoucher("Voucher đã quá hạn")
                .loaiGiamGia("PERCENT")
                .giaTriGiam(BigDecimal.valueOf(10))
                .giamToiDa(BigDecimal.valueOf(20000))
                .giaTriDonToiThieu(BigDecimal.valueOf(100000))
                .tongSoLuong(10)
                .soLuongDaDung(0)
                .ngayBatDau(now.minusDays(10))
                .ngayKetThuc(now.minusDays(1))
                .kichHoat(true)
                .build());

        // 5. Voucher chưa diễn ra
        voucherChuaDienRa = maGiamGiaRepository.saveAndFlush(MaGiamGia.builder()
                .maCode("TEST_FUTURE_" + ts)
                .tenVoucher("Voucher sự kiện sắp tới")
                .loaiGiamGia("PERCENT")
                .giaTriGiam(BigDecimal.valueOf(15))
                .giamToiDa(BigDecimal.valueOf(30000))
                .giaTriDonToiThieu(BigDecimal.valueOf(100000))
                .tongSoLuong(10)
                .soLuongDaDung(0)
                .ngayBatDau(now.plusDays(5))
                .ngayKetThuc(now.plusDays(20))
                .kichHoat(true)
                .build());

        // 6. Voucher đã hết lượt toàn sàn (tongSoLuong = soLuongDaDung)
        voucherHetLuot = maGiamGiaRepository.saveAndFlush(MaGiamGia.builder()
                .maCode("TEST_OUT_OF_STOCK_" + ts)
                .tenVoucher("Voucher cháy hàng")
                .loaiGiamGia("FIXED_AMOUNT")
                .giaTriGiam(BigDecimal.valueOf(20000))
                .giaTriDonToiThieu(BigDecimal.valueOf(100000))
                .tongSoLuong(10)
                .soLuongDaDung(10)
                .nganSach(BigDecimal.valueOf(1000000))
                .nganSachDaDung(BigDecimal.valueOf(200000))
                .ngayBatDau(now.minusDays(1))
                .ngayKetThuc(now.plusDays(10))
                .kichHoat(true)
                .build());

        // 7. Voucher đã hết ngân sách
        voucherHetNganSach = maGiamGiaRepository.saveAndFlush(MaGiamGia.builder()
                .maCode("TEST_OUT_BUDGET_" + ts)
                .tenVoucher("Voucher vượt ngân sách")
                .loaiGiamGia("PERCENT")
                .giaTriGiam(BigDecimal.valueOf(50))
                .giamToiDa(BigDecimal.valueOf(100000))
                .giaTriDonToiThieu(BigDecimal.valueOf(100000))
                .tongSoLuong(100)
                .soLuongDaDung(10)
                .nganSach(BigDecimal.valueOf(500000))
                .nganSachDaDung(BigDecimal.valueOf(500000))
                .ngayBatDau(now.minusDays(1))
                .ngayKetThuc(now.plusDays(10))
                .kichHoat(true)
                .build());

        // 8. Voucher tạm ngưng kích hoạt
        voucherTamNgung = maGiamGiaRepository.saveAndFlush(MaGiamGia.builder()
                .maCode("TEST_INACTIVE_" + ts)
                .tenVoucher("Voucher bị khóa")
                .loaiGiamGia("FIXED_AMOUNT")
                .giaTriGiam(BigDecimal.valueOf(10000))
                .giaTriDonToiThieu(BigDecimal.valueOf(50000))
                .ngayBatDau(now.minusDays(1))
                .ngayKetThuc(now.plusDays(10))
                .kichHoat(false)
                .build());

        entityManager.clear();
    }

    // =========================================================================
    // TEST GIAO DIỆN & TIÊU CHÍ 1: THU THẬP VOUCHER VÀO VÍ VOUCHER_SO_HUU
    // =========================================================================

    @Test
    @DisplayName("TC1: Hiển thị giao diện Kho Voucher & Ví Voucher của Khách hàng thành công")
    void testHienThiKhoVoucher() throws Exception {
        mockMvc.perform(get("/khach-hang/voucher")
                        .cookie(cookieKhachHang)
                        .param("tab", "san-co"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/voucher"))
                .andExpect(model().attributeExists("dsSanCo"))
                .andExpect(model().attributeExists("dsViCuaToi"))
                .andExpect(model().attributeExists("tongVoucherKhaDung"));
    }

    @Test
    @DisplayName("TC2: Khách hàng chưa đăng nhập khi thu thập voucher qua API -> Trả về HTTP 401 & yêu cầu đăng nhập")
    void testThuThapVoucher_ChuaDangNhap() throws Exception {
        mockMvc.perform(post("/khach-hang/api/voucher/thu-thap/" + voucherKhaDungPercent.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.thanhCong").value(false))
                .andExpect(jsonPath("$.yeuCauDangNhap").value(true))
                .andExpect(jsonPath("$.thongBao", containsString("Vui lòng đăng nhập")));
    }

    @Test
    @DisplayName("TC3: Khách hàng đã đăng nhập thu thập voucher thành công vào bảng voucher_so_huu (Tiêu chí 1)")
    void testThuThapVoucher_ThanhCong() throws Exception {
        mockMvc.perform(post("/khach-hang/api/voucher/thu-thap/" + voucherKhaDungPercent.getId())
                        .cookie(cookieKhachHang))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.maCode").value(voucherKhaDungPercent.getMaCode()))
                .andExpect(jsonPath("$.thongBao", containsString("Thu thập thành công")));

        // Xác thực trong cơ sở dữ liệu: Phải có 1 bản ghi trong voucher_so_huu với da_su_dung = false
        entityManager.clear();
        boolean exists = voucherSoHuuRepository.existsByMaKhachHangIdAndMaGiamGiaIdAndDaSuDung(
                khachHangTest.getId(), voucherKhaDungPercent.getId(), false);
        assertTrue(exists, "Bản ghi voucher_so_huu phải được lưu vào CSDL cho khách hàng");
    }

    @Test
    @DisplayName("TC4: Chặn thu thập trùng voucher khi đã có trong ví chưa dùng -> Thông báo rõ ràng đã có trong ví")
    void testChanThuThap_TrungVoucherDaCoTrongVi() throws Exception {
        // Thu thập lần 1 thành công
        voucherSoHuuRepository.saveAndFlush(VoucherSoHuu.builder()
                .maKhachHangId(khachHangTest.getId())
                .maGiamGiaId(voucherKhaDungPercent.getId())
                .daSuDung(false)
                .ngayNhan(LocalDateTime.now())
                .build());
        entityManager.clear();

        // Thu thập lần 2 -> Chặn và báo lỗi rõ ràng
        mockMvc.perform(post("/khach-hang/api/voucher/thu-thap/" + voucherKhaDungPercent.getId())
                        .cookie(cookieKhachHang))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false))
                .andExpect(jsonPath("$.thongBao", containsString("đã lưu mã giảm giá")));
    }

    @Test
    @DisplayName("TC5: Chặn thu thập voucher khi vượt quá giới hạn mỗi khách hàng (gioiHanMoiUser)")
    void testChanThuThap_VuotQuaGioiHanMoiUser() throws Exception {
        // voucherKhaDungPercent có gioiHanMoiUser = 1
        // Đã nhận 1 lần và đã sử dụng
        voucherSoHuuRepository.saveAndFlush(VoucherSoHuu.builder()
                .maKhachHangId(khachHangTest.getId())
                .maGiamGiaId(voucherKhaDungPercent.getId())
                .daSuDung(true)
                .ngayNhan(LocalDateTime.now().minusDays(1))
                .ngaySuDung(LocalDateTime.now())
                .build());
        entityManager.clear();

        // Cố nhận tiếp lần 2
        mockMvc.perform(post("/khach-hang/api/voucher/thu-thap/" + voucherKhaDungPercent.getId())
                        .cookie(cookieKhachHang))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false))
                .andExpect(jsonPath("$.thongBao", containsString("đã đạt giới hạn nhận mã")));
    }

    @Test
    @DisplayName("TC6: Chặn thu thập voucher đã hết hạn sử dụng -> Báo lỗi kèm thời gian hết hạn")
    void testChanThuThap_VoucherDaHetHan() throws Exception {
        mockMvc.perform(post("/khach-hang/api/voucher/thu-thap/" + voucherHetHan.getId())
                        .cookie(cookieKhachHang))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false))
                .andExpect(jsonPath("$.thongBao", containsString("đã hết hạn sử dụng")));
    }

    @Test
    @DisplayName("TC7: Chặn thu thập voucher chưa đến thời gian áp dụng -> Báo lỗi kèm thời gian bắt đầu")
    void testChanThuThap_VoucherChuaDienRa() throws Exception {
        mockMvc.perform(post("/khach-hang/api/voucher/thu-thap/" + voucherChuaDienRa.getId())
                        .cookie(cookieKhachHang))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false))
                .andExpect(jsonPath("$.thongBao", containsString("chưa đến thời gian áp dụng")));
    }

    @Test
    @DisplayName("TC8: Chặn thu thập voucher khi đã hết lượt toàn sàn -> Báo lỗi hết lượt")
    void testChanThuThap_VoucherHetLuotToanSan() throws Exception {
        mockMvc.perform(post("/khach-hang/api/voucher/thu-thap/" + voucherHetLuot.getId())
                        .cookie(cookieKhachHang))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false))
                .andExpect(jsonPath("$.thongBao", containsString("hết lượt")));
    }

    @Test
    @DisplayName("TC9: Chặn thu thập voucher khi hết ngân sách chương trình -> Báo lỗi hết ngân sách")
    void testChanThuThap_VoucherHetNganSach() throws Exception {
        mockMvc.perform(post("/khach-hang/api/voucher/thu-thap/" + voucherHetNganSach.getId())
                        .cookie(cookieKhachHang))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false))
                .andExpect(jsonPath("$.thongBao", containsString("hạn mức ngân sách")));
    }

    // =========================================================================
    // TIÊU CHÍ 2: API VALIDATE CHECK MÃ CÒN LƯỢT / CÓ HỢP LỆ VỚI ĐƠN HÀNG KHÔNG
    // =========================================================================

    @Test
    @DisplayName("TC10: API Validate voucher hợp lệ theo PERCENT -> Tính tiền giảm chính xác, đúng trần giảm tối đa (Tiêu chí 2)")
    void testApiValidate_PercentHopLe() throws Exception {
        // Đơn hàng 300,000 đ (> đơn tối thiểu 200k). Giảm 20% = 60k. Trần tối đa 50k -> Tiền giảm thực tế = 50,000 đ
        mockMvc.perform(post("/khach-hang/api/voucher/kiem-tra")
                        .cookie(cookieKhachHang)
                        .param("maCode", voucherKhaDungPercent.getMaCode())
                        .param("tongTienHang", "300000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hopLe").value(true))
                .andExpect(jsonPath("$.maCode").value(voucherKhaDungPercent.getMaCode()))
                .andExpect(jsonPath("$.tienGiam").value(50000))
                .andExpect(jsonPath("$.tongThanhToanMoi").value(250000))
                .andExpect(jsonPath("$.thongBao", containsString("Áp dụng thành công")));
    }

    @Test
    @DisplayName("TC11: API Validate voucher hợp lệ theo FIXED_AMOUNT -> Tính tiền giảm chính xác (Tiêu chí 2)")
    void testApiValidate_FixedAmountHopLe() throws Exception {
        // Đơn hàng 200,000 đ (> đơn tối thiểu 150k). Giảm 30k -> Tiền giảm thực tế = 30,000 đ
        mockMvc.perform(post("/khach-hang/api/voucher/kiem-tra")
                        .cookie(cookieKhachHang)
                        .param("maCode", voucherKhaDungFixed.getMaCode())
                        .param("tongTienHang", "200000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hopLe").value(true))
                .andExpect(jsonPath("$.maCode").value(voucherKhaDungFixed.getMaCode()))
                .andExpect(jsonPath("$.tienGiam").value(30000))
                .andExpect(jsonPath("$.tongThanhToanMoi").value(170000))
                .andExpect(jsonPath("$.thongBao", containsString("Áp dụng thành công")));
    }

    @Test
    @DisplayName("TC12: API Validate báo lỗi rõ ràng khi đơn hàng chưa đạt giá trị tối thiểu (kèm số tiền còn thiếu)")
    void testApiValidate_ChuaDatDonToiThieu() throws Exception {
        // Đơn hàng chỉ 120,000 đ trong khi đơn tối thiểu 200,000 đ -> Thiếu 80,000 đ
        mockMvc.perform(post("/khach-hang/api/voucher/kiem-tra")
                        .cookie(cookieKhachHang)
                        .param("maCode", voucherKhaDungPercent.getMaCode())
                        .param("tongTienHang", "120000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hopLe").value(false))
                .andExpect(jsonPath("$.tienGiam").value(0))
                .andExpect(jsonPath("$.soTienConThieu").value(80000))
                .andExpect(jsonPath("$.thongBao", containsString("chưa đạt giá trị tối thiểu")))
                .andExpect(jsonPath("$.thongBao", containsString("cần mua thêm")));
    }

    @Test
    @DisplayName("TC13: API Validate báo lỗi khi mã voucher không tồn tại")
    void testApiValidate_MaKhongTonTai() throws Exception {
        mockMvc.perform(post("/khach-hang/api/voucher/kiem-tra")
                        .cookie(cookieKhachHang)
                        .param("maCode", "MA_AO_123456")
                        .param("tongTienHang", "500000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hopLe").value(false))
                .andExpect(jsonPath("$.thongBao", containsString("không tồn tại trên hệ thống")));
    }

    @Test
    @DisplayName("TC14: API Validate báo lỗi khi voucher đang tạm ngưng")
    void testApiValidate_VoucherTamNgung() throws Exception {
        mockMvc.perform(post("/khach-hang/api/voucher/kiem-tra")
                        .cookie(cookieKhachHang)
                        .param("maCode", voucherTamNgung.getMaCode())
                        .param("tongTienHang", "200000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hopLe").value(false))
                .andExpect(jsonPath("$.thongBao", containsString("đang tạm ngưng áp dụng")));
    }

    @Test
    @DisplayName("TC15: API Lấy danh sách voucher khả dụng trong ví (/khach-hang/api/voucher/vi-voucher)")
    void testApiLayDanhSachViVoucher() throws Exception {
        // Lưu 1 voucher vào ví
        voucherSoHuuRepository.saveAndFlush(VoucherSoHuu.builder()
                .maKhachHangId(khachHangTest.getId())
                .maGiamGiaId(voucherKhaDungPercent.getId())
                .daSuDung(false)
                .ngayNhan(LocalDateTime.now())
                .build());
        entityManager.clear();

        mockMvc.perform(get("/khach-hang/api/voucher/vi-voucher")
                        .cookie(cookieKhachHang))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].maCode").value(voucherKhaDungPercent.getMaCode()))
                .andExpect(jsonPath("$[0].khaDung").value(true));
    }
}
