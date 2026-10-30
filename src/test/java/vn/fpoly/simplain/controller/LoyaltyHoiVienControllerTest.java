package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.dto.HoiVienResponse;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;
import vn.fpoly.simplain.service.LoyaltyHoiVienService;
import vn.fpoly.simplain.util.JwtUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động Hệ thống Tự động Tích điểm Loyalty (Coolcash) & Thăng hạng Hội viên (US16).
 * Bao phủ đầy đủ 2 tiêu chí:
 * 1. Đơn hàng hoàn thành cộng % điểm (so_du_coolcash).
 * 2. Đạt mốc tự động lên Hạng (hang_thanh_vien).
 *
 * Chạy lệnh: .\mvnw.cmd -Dtest=LoyaltyHoiVienControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Tự động Tích Điểm Coolcash & Thăng Hạng Hội Viên (US16)")
class LoyaltyHoiVienControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoyaltyHoiVienService loyaltyHoiVienService;

    @Autowired
    private HangThanhVienRepository hangThanhVienRepository;

    @Autowired
    private HoSoHoiVienRepository hoSoHoiVienRepository;

    @Autowired
    private BienDongDiemRepository bienDongDiemRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    private static final String EMAIL_TEST = "us16.loyalty@gmail.com";
    private static final String SDT_TEST = "0916888777";

    private KhachHang khachHangTest;
    private TrangThaiDonHang trangThaiHoanThanh;
    private TrangThaiDonHang trangThaiDangXuLy;
    private HangThanhVien hangDong;
    private HangThanhVien hangBac;
    private HangThanhVien hangVang;
    private HangThanhVien hangBachKim;

    @BeforeEach
    void setUp() {
        donDep();

        // 1. Khởi tạo 4 Hạng thành viên chuẩn
        loyaltyHoiVienService.khoiTaoHangThanhVienMacDinh();
        hangDong = hangThanhVienRepository.findByTenHangIgnoreCase("Đồng")
                .orElseThrow(() -> new IllegalStateException("Thiếu hạng Đồng"));
        hangBac = hangThanhVienRepository.findByTenHangIgnoreCase("Bạc")
                .orElseThrow(() -> new IllegalStateException("Thiếu hạng Bạc"));
        hangVang = hangThanhVienRepository.findByTenHangIgnoreCase("Vàng")
                .orElseThrow(() -> new IllegalStateException("Thiếu hạng Vàng"));
        hangBachKim = hangThanhVienRepository.findByTenHangIgnoreCase("Bạch Kim")
                .orElseThrow(() -> new IllegalStateException("Thiếu hạng Bạch Kim"));

        // 2. Trạng thái đơn hàng
        trangThaiHoanThanh = trangThaiDonHangRepository.findById(4L).orElseGet(() ->
                trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("HOAN_THANH")
                        .tenTrangThai("Hoàn thành")
                        .maMauHex("#10b981")
                        .build())
        );

        trangThaiDangXuLy = trangThaiDonHangRepository.findById(1L).orElseGet(() ->
                trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("DANG_XU_LY")
                        .tenTrangThai("Đang xử lý")
                        .maMauHex("#f59e0b")
                        .build())
        );

        // 3. Khách hàng test
        khachHangTest = khachHangRepository.save(KhachHang.builder()
                .maKhachHang("KH-US16-TEST")
                .hoTen("Trần Hội Viên US16")
                .email(EMAIL_TEST)
                .soDienThoai(SDT_TEST)
                .matKhauHash("$2a$10$abcdefghijklmnopqrstuvwxyz123456")
                .gioiTinh("Nam")
                .trangThai(1)
                .build());

        // 4. Khởi tạo hồ sơ hội viên ban đầu (Hạng Đồng - 0đ, 0 Coolcash)
        loyaltyHoiVienService.layHoacTaoHoSoHoiVien(khachHangTest.getId());
    }

    @AfterEach
    void tearDown() {
        donDep();
    }

    private void donDep() {
        if (khachHangTest != null && khachHangTest.getId() != null) {
            bienDongDiemRepository.deleteAll(bienDongDiemRepository.findByMaKhachHangIdOrderByNgayTaoDesc(khachHangTest.getId()));
            hoSoHoiVienRepository.findByMaKhachHangId(khachHangTest.getId()).ifPresent(hoSoHoiVienRepository::delete);
        }

        donHangRepository.findAll().forEach(dh -> {
            if (dh.getMaDonHang() != null && dh.getMaDonHang().startsWith("DH-US16")) {
                bienDongDiemRepository.deleteAll(
                        bienDongDiemRepository.findAll().stream()
                                .filter(b -> dh.getId().equals(b.getMaDonHangId()))
                                .toList()
                );
                donHangRepository.delete(dh);
            }
        });

        khachHangRepository.findByEmail(EMAIL_TEST).ifPresent(kh -> {
            hoSoHoiVienRepository.findByMaKhachHangId(kh.getId()).ifPresent(hoSoHoiVienRepository::delete);
            khachHangRepository.delete(kh);
        });
    }

    private Cookie taoCookieDangNhap() {
        String token = jwtUtil.generateToken(khachHangTest);
        return new Cookie(KhachHangController.COOKIE_TOKEN, token);
    }

    @Test
    @DisplayName("TC1: Tiêu chí 1 - Đơn hàng hoàn thành tự động cộng % điểm Coolcash chính xác và ghi nhận biến động điểm")
    void testTichDiemCoolcash_KhiDonHangHoanThanh() {
        // Đơn hàng hoàn thành trị giá 500,000 đ
        // Khách hàng đang ở Hạng Đồng (1.0% hoàn tiền) -> Số điểm cộng = 500,000 * 1% = 5,000 Coolcash
        DonHang donHang = donHangRepository.save(DonHang.builder()
                .maDonHang("DH-US16-TC1")
                .khachHang(khachHangTest)
                .trangThaiDonHang(trangThaiHoanThanh)
                .tenNguoiNhan("Trần Hội Viên US16")
                .soDienThoaiNhan(SDT_TEST)
                .diaChiGiaoHang("123 Lê Duẩn, Hà Nội")
                .tongTienHang(new BigDecimal("500000"))
                .tongThanhToan(new BigDecimal("500000"))
                .ngayTao(LocalDateTime.now())
                .build());

        // Kích hoạt xử lý tích điểm
        HoSoHoiVien hoSoCapNhat = loyaltyHoiVienService.xuLyTichDiemVaThangHang(donHang.getId());

        // Kiểm tra Tiêu chí 1: Số dư Coolcash được cộng chính xác
        assertEquals(0, new BigDecimal("5000").compareTo(hoSoCapNhat.getSoDuCoolcash()));
        assertEquals(0, new BigDecimal("500000").compareTo(hoSoCapNhat.getTongChiTieuNam()));

        // Kiểm tra bảng bien_dong_diem đã ghi nhận lịch sử giao dịch
        assertTrue(bienDongDiemRepository.existsByMaDonHangIdAndLoaiBienDong(donHang.getId(), LoyaltyHoiVienService.LOAI_BIEN_DONG_CONG));
        BienDongDiem bd = bienDongDiemRepository.findByMaKhachHangIdOrderByNgayTaoDesc(khachHangTest.getId()).get(0);
        assertEquals(0, new BigDecimal("5000").compareTo(bd.getSoDiem()));
        assertTrue(bd.getNoiDung().contains("DH-US16-TC1"));
    }

    @Test
    @DisplayName("TC2: Tiêu chí 1 - Chống cộng điểm trùng lặp khi một đơn hàng hoàn thành được gọi xử lý nhiều lần")
    void testChongCongDiemTrungLap() {
        DonHang donHang = donHangRepository.save(DonHang.builder()
                .maDonHang("DH-US16-TRUNGLAP")
                .khachHang(khachHangTest)
                .trangThaiDonHang(trangThaiHoanThanh)
                .tenNguoiNhan("Trần Hội Viên US16")
                .soDienThoaiNhan(SDT_TEST)
                .diaChiGiaoHang("123 Lê Duẩn, Hà Nội")
                .tongTienHang(new BigDecimal("400000"))
                .tongThanhToan(new BigDecimal("400000"))
                .ngayTao(LocalDateTime.now())
                .build());

        // Lần 1: Cộng 4,000 Coolcash
        loyaltyHoiVienService.xuLyTichDiemVaThangHang(donHang.getId());

        // Lần 2: Cố tình gọi lại trên cùng một đơn hàng
        HoSoHoiVien hoSoLan2 = loyaltyHoiVienService.xuLyTichDiemVaThangHang(donHang.getId());

        // Số dư vẫn là 4,000, không bị cộng x2 thành 8,000
        assertEquals(0, new BigDecimal("4000").compareTo(hoSoLan2.getSoDuCoolcash()));
        assertEquals(0, new BigDecimal("400000").compareTo(hoSoLan2.getTongChiTieuNam()));
    }

    @Test
    @DisplayName("TC3: Tiêu chí 1 - Từ chối tích điểm khi đơn hàng chưa ở trạng thái HOAN_THANH")
    void testTuChoiTichDiem_KhiDonHangChuaHoanThanh() {
        DonHang donHangChuaXong = donHangRepository.save(DonHang.builder()
                .maDonHang("DH-US16-CHUAXONG")
                .khachHang(khachHangTest)
                .trangThaiDonHang(trangThaiDangXuLy)
                .tenNguoiNhan("Trần Hội Viên US16")
                .soDienThoaiNhan(SDT_TEST)
                .diaChiGiaoHang("123 Lê Duẩn, Hà Nội")
                .tongTienHang(new BigDecimal("300000"))
                .tongThanhToan(new BigDecimal("300000"))
                .ngayTao(LocalDateTime.now())
                .build());

        // Kỳ vọng ném ra ngoại lệ IllegalStateException
        assertThrows(IllegalStateException.class, () ->
                loyaltyHoiVienService.xuLyTichDiemVaThangHang(donHangChuaXong.getId())
        );

        // Số dư điểm vẫn là 0
        HoSoHoiVien hs = hoSoHoiVienRepository.findByMaKhachHangId(khachHangTest.getId()).orElseThrow();
        assertEquals(0, BigDecimal.ZERO.compareTo(hs.getSoDuCoolcash()));
    }

    @Test
    @DisplayName("TC4: Tiêu chí 2 - Tự động lên hạng Bạc (1tr), Vàng (3tr) và Bạch Kim (6tr) khi đạt mốc chi tiêu")
    void testTuDongThangHang_KhiDatMocChiTieu() {
        // Đơn hàng 1: 1,200,000 đ (> 1,000,000 đ) -> Từ Đồng lên Hạng Bạc
        DonHang dh1 = donHangRepository.save(DonHang.builder()
                .maDonHang("DH-US16-LEN-BAC")
                .khachHang(khachHangTest)
                .trangThaiDonHang(trangThaiHoanThanh)
                .tenNguoiNhan("Trần Hội Viên US16")
                .soDienThoaiNhan(SDT_TEST)
                .diaChiGiaoHang("Hà Nội")
                .tongTienHang(new BigDecimal("1200000"))
                .tongThanhToan(new BigDecimal("1200000"))
                .ngayTao(LocalDateTime.now())
                .build());

        HoSoHoiVien hs1 = loyaltyHoiVienService.xuLyTichDiemVaThangHang(dh1.getId());
        assertEquals(hangBac.getId(), hs1.getMaHangHienTaiId(), "Khách hàng phải tự động được nâng lên Hạng Bạc");
        assertEquals(0, new BigDecimal("12000").compareTo(hs1.getSoDuCoolcash()), "Hạng Đồng 1% của 1,200,000 = 12,000 Coolcash");

        // Đơn hàng 2: Khi đã là Hạng Bạc (tỷ lệ hoàn 3%), mua thêm 2,000,000 đ
        // Tổng chi tiêu = 1,200,000 + 2,000,000 = 3,200,000 đ (> 3,000,000 đ) -> Tự động lên Hạng Vàng!
        // Điểm cộng đơn 2 theo tỷ lệ hạng Bạc (3% của 2,000,000 đ = 60,000 Coolcash)
        DonHang dh2 = donHangRepository.save(DonHang.builder()
                .maDonHang("DH-US16-LEN-VANG")
                .khachHang(khachHangTest)
                .trangThaiDonHang(trangThaiHoanThanh)
                .tenNguoiNhan("Trần Hội Viên US16")
                .soDienThoaiNhan(SDT_TEST)
                .diaChiGiaoHang("Hà Nội")
                .tongTienHang(new BigDecimal("2000000"))
                .tongThanhToan(new BigDecimal("2000000"))
                .ngayTao(LocalDateTime.now())
                .build());

        HoSoHoiVien hs2 = loyaltyHoiVienService.xuLyTichDiemVaThangHang(dh2.getId());
        assertEquals(hangVang.getId(), hs2.getMaHangHienTaiId(), "Khách hàng phải tự động được nâng lên Hạng Vàng");
        // Số dư = 12,000 (đơn 1) + 60,000 (đơn 2) = 72,000 Coolcash
        assertEquals(0, new BigDecimal("72000").compareTo(hs2.getSoDuCoolcash()));
        assertEquals(0, new BigDecimal("3200000").compareTo(hs2.getTongChiTieuNam()));

        // Đơn hàng 3: Khi đã là Hạng Vàng (tỷ lệ hoàn 5%), mua thêm 3,000,000 đ
        // Tổng chi tiêu = 3,200,000 + 3,000,000 = 6,200,000 đ (> 6,000,000 đ) -> Tự động lên Hạng Bạch Kim (VIP)!
        // Điểm cộng đơn 3 theo tỷ lệ hạng Vàng (5% của 3,000,000 đ = 150,000 Coolcash)
        DonHang dh3 = donHangRepository.save(DonHang.builder()
                .maDonHang("DH-US16-LEN-BACHKIM")
                .khachHang(khachHangTest)
                .trangThaiDonHang(trangThaiHoanThanh)
                .tenNguoiNhan("Trần Hội Viên US16")
                .soDienThoaiNhan(SDT_TEST)
                .diaChiGiaoHang("Hà Nội")
                .tongTienHang(new BigDecimal("3000000"))
                .tongThanhToan(new BigDecimal("3000000"))
                .ngayTao(LocalDateTime.now())
                .build());

        HoSoHoiVien hs3 = loyaltyHoiVienService.xuLyTichDiemVaThangHang(dh3.getId());
        assertEquals(hangBachKim.getId(), hs3.getMaHangHienTaiId(), "Khách hàng phải tự động được nâng lên Hạng Bạch Kim");
        // Số dư = 72,000 + 150,000 = 222,000 Coolcash
        assertEquals(0, new BigDecimal("222000").compareTo(hs3.getSoDuCoolcash()));
        assertEquals(0, new BigDecimal("6200000").compareTo(hs3.getTongChiTieuNam()));
    }

    @Test
    @DisplayName("TC5: Màn hình giao diện Hội viên và API trả về thẻ Coolcash, tiến trình thăng hạng và lịch sử biến động điểm")
    void testGiaoDienVaApiHoiVien() throws Exception {
        Cookie authCookie = taoCookieDangNhap();

        // 1. Kiểm tra màn hình HTML /khach-hang/hoi-vien
        mockMvc.perform(get("/khach-hang/hoi-vien")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/hoi-vien"))
                .andExpect(model().attributeExists("hoiVien"))
                .andExpect(model().attributeExists("thongTin"));

        // 2. Kiểm tra API JSON /khach-hang/hoi-vien/api/thong-tin
        mockMvc.perform(get("/khach-hang/hoi-vien/api/thong-tin")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenHang", notNullValue()))
                .andExpect(jsonPath("$.soDuCoolcash", notNullValue()))
                .andExpect(jsonPath("$.tongChiTieuNam", notNullValue()))
                .andExpect(jsonPath("$.phanTramTienTrinh", notNullValue()));

        // 3. Kiểm tra API kích hoạt hoàn thành đơn hàng và tự động tích điểm
        DonHang dhTestApi = donHangRepository.save(DonHang.builder()
                .maDonHang("DH-US16-API-TRIGGER")
                .khachHang(khachHangTest)
                .trangThaiDonHang(trangThaiDangXuLy)
                .tenNguoiNhan("Trần Hội Viên US16")
                .soDienThoaiNhan(SDT_TEST)
                .diaChiGiaoHang("Hà Nội")
                .tongTienHang(new BigDecimal("200000"))
                .tongThanhToan(new BigDecimal("200000"))
                .ngayTao(LocalDateTime.now())
                .build());

        mockMvc.perform(post("/api/don-hang/" + dhTestApi.getId() + "/hoan-thanh-va-tich-diem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong", is(true)))
                .andExpect(jsonPath("$.soDuCoolcashMoi", notNullValue()));

        // Xác nhận đơn hàng đã chuyển sang hoàn thành trong database
        DonHang dhSau = donHangRepository.findById(dhTestApi.getId()).orElseThrow();
        assertEquals(4L, dhSau.getTrangThaiDonHang().getId());
    }
}
