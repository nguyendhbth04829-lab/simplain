package vn.fpoly.simplain.controller;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.MaGiamGia;
import vn.fpoly.simplain.repository.MaGiamGiaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tích hợp Web MVC cho chức năng Admin Tạo chương trình Mã giảm giá (US26 - Mod 6 / Voucher - Khoa).
 * - Tiêu chí 1: Tạo Voucher (% hoặc Tiền mặt).
 * - Tiêu chí 2: Đặt giới hạn ngân sách, lượt dùng tối đa, đơn tối thiểu.
 * - Form Validation: Validate trùng mã, validate % <= 100%, validate giảm tối đa, validate ngày kết thúc > ngày bắt đầu, đơn tối thiểu không âm.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Kiểm thử Web MVC Admin Tạo Chương Trình Mã Giảm Giá (US26)")
class QuanLyMaGiamGiaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MaGiamGiaRepository maGiamGiaRepository;

    @Autowired
    private EntityManager entityManager;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @BeforeEach
    void setUp() {
        // Đảm bảo có sẵn một voucher mẫu để test trùng mã
        if (!maGiamGiaRepository.existsByMaCodeIgnoreCase("VOUCHER_TEST_UNIQUE")) {
            MaGiamGia mau = MaGiamGia.builder()
                    .maCode("VOUCHER_TEST_UNIQUE")
                    .tenVoucher("Voucher Mẫu Kiểm Thử")
                    .loaiGiamGia("PERCENT")
                    .giaTriGiam(BigDecimal.valueOf(15))
                    .giamToiDa(BigDecimal.valueOf(30000))
                    .giaTriDonToiThieu(BigDecimal.valueOf(150000))
                    .tongSoLuong(200)
                    .soLuongDaDung(0)
                    .gioiHanMoiUser(1)
                    .nganSach(BigDecimal.valueOf(5000000))
                    .nganSachDaDung(BigDecimal.ZERO)
                    .ngayBatDau(LocalDateTime.now().minusDays(1))
                    .ngayKetThuc(LocalDateTime.now().plusDays(20))
                    .kichHoat(true)
                    .moTa("Voucher tạo sẵn cho unit test")
                    .build();
            maGiamGiaRepository.saveAndFlush(mau);
        }
        entityManager.clear();
    }

    @Test
    @DisplayName("TC1: Hiển thị màn hình danh sách chương trình mã giảm giá (US26)")
    void testHienThiDanhSach_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/voucher/hien-thi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/voucher/hien-thi"))
                .andExpect(model().attributeExists("trangVoucher"))
                .andExpect(model().attributeExists("thongKe"));
    }

    @Test
    @DisplayName("TC2: Hiển thị form tạo mới chương trình mã giảm giá (US26)")
    void testHienThiFormTaoMoi_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/voucher/tao-moi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/voucher/tao-moi"))
                .andExpect(model().attributeExists("taoRequest"));
    }

    @Test
    @DisplayName("TC3: Tạo Voucher theo PHẦN TRĂM (%) hợp lệ (Tiêu chí 1 & 2)")
    void testTaoVoucher_Percent_ThanhCong() throws Exception {
        String code = "TEST_PCT_20";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(15);

        mockMvc.perform(post("/admin/voucher/tao-moi")
                        .param("maCode", code)
                        .param("tenVoucher", "Chương trình Giảm 20% Chào Hè")
                        .param("loaiGiamGia", "PERCENT")
                        .param("giaTriGiam", "20")
                        .param("giamToiDa", "50000")
                        .param("giaTriDonToiThieu", "250000")
                        .param("tongSoLuong", "500")
                        .param("gioiHanMoiUser", "1")
                        .param("nganSach", "10000000")
                        .param("ngayBatDau", start.format(ISO_FORMATTER))
                        .param("ngayKetThuc", end.format(ISO_FORMATTER))
                        .param("kichHoat", "true")
                        .param("moTa", "Áp dụng cho mọi khách hàng"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/voucher/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.clear();

        Optional<MaGiamGia> opt = maGiamGiaRepository.findByMaCodeIgnoreCase(code);
        assertTrue(opt.isPresent(), "Voucher phải được lưu vào CSDL");
        MaGiamGia v = opt.get();
        assertEquals("PERCENT", v.getLoaiGiamGia());
        assertEquals(0, BigDecimal.valueOf(20).compareTo(v.getGiaTriGiam()));
        assertEquals(0, BigDecimal.valueOf(50000).compareTo(v.getGiamToiDa()));
        assertEquals(0, BigDecimal.valueOf(250000).compareTo(v.getGiaTriDonToiThieu()));
        assertEquals(500, v.getTongSoLuong());
        assertEquals(1, v.getGioiHanMoiUser());
        assertEquals(0, BigDecimal.valueOf(10000000).compareTo(v.getNganSach()));
        assertTrue(v.getKichHoat());
    }

    @Test
    @DisplayName("TC4: Tạo Voucher theo TIỀN MẶT CỐ ĐỊNH hợp lệ (Tiêu chí 1 & 2)")
    void testTaoVoucher_FixedAmount_ThanhCong() throws Exception {
        String code = "TEST_FIXED_50K";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(30);

        mockMvc.perform(post("/admin/voucher/tao-moi")
                        .param("maCode", code)
                        .param("tenVoucher", "Giảm 50.000 đ cho đơn từ 300.000 đ")
                        .param("loaiGiamGia", "FIXED_AMOUNT")
                        .param("giaTriGiam", "50000")
                        .param("giaTriDonToiThieu", "300000")
                        .param("tongSoLuong", "100")
                        .param("gioiHanMoiUser", "2")
                        .param("nganSach", "5000000")
                        .param("ngayBatDau", start.format(ISO_FORMATTER))
                        .param("ngayKetThuc", end.format(ISO_FORMATTER))
                        .param("kichHoat", "true")
                        .param("moTa", "Voucher tiền mặt trực tiếp"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/voucher/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.clear();

        Optional<MaGiamGia> opt = maGiamGiaRepository.findByMaCodeIgnoreCase(code);
        assertTrue(opt.isPresent());
        MaGiamGia v = opt.get();
        assertEquals("FIXED_AMOUNT", v.getLoaiGiamGia());
        assertEquals(0, BigDecimal.valueOf(50000).compareTo(v.getGiaTriGiam()));
        assertEquals(0, BigDecimal.valueOf(300000).compareTo(v.getGiaTriDonToiThieu()));
        assertEquals(100, v.getTongSoLuong());
        assertEquals(2, v.getGioiHanMoiUser());
    }

    @Test
    @DisplayName("TC5: Chặn tạo voucher trùng mã code đã tồn tại trong hệ thống")
    void testChanTaoVoucher_TrungMaCode() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(10);

        mockMvc.perform(post("/admin/voucher/tao-moi")
                        .param("maCode", "voucher_test_unique") // Trùng mã (case-insensitive)
                        .param("tenVoucher", "Cố tình tạo trùng mã")
                        .param("loaiGiamGia", "PERCENT")
                        .param("giaTriGiam", "10")
                        .param("giamToiDa", "20000")
                        .param("giaTriDonToiThieu", "100000")
                        .param("ngayBatDau", start.format(ISO_FORMATTER))
                        .param("ngayKetThuc", end.format(ISO_FORMATTER)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/voucher/tao-moi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("đã tồn tại")));
    }

    @Test
    @DisplayName("TC6: Chặn tạo voucher loại PERCENT có tỷ lệ giảm vượt quá 100%")
    void testChanTaoVoucher_PercentVuotQua100() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(10);

        mockMvc.perform(post("/admin/voucher/tao-moi")
                        .param("maCode", "TEST_OVER_100")
                        .param("tenVoucher", "Giảm 150% phi lý")
                        .param("loaiGiamGia", "PERCENT")
                        .param("giaTriGiam", "150") // 150% > 100%
                        .param("giamToiDa", "50000")
                        .param("giaTriDonToiThieu", "100000")
                        .param("ngayBatDau", start.format(ISO_FORMATTER))
                        .param("ngayKetThuc", end.format(ISO_FORMATTER)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/voucher/tao-moi"))
                .andExpect(model().attributeHasFieldErrors("taoRequest", "giaTriGiam"));
    }

    @Test
    @DisplayName("TC7: Chặn tạo voucher loại PERCENT nếu thiếu Giảm tối đa")
    void testChanTaoVoucher_PercentThieuGiamToiDa() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(10);

        mockMvc.perform(post("/admin/voucher/tao-moi")
                        .param("maCode", "TEST_NO_MAX")
                        .param("tenVoucher", "Giảm 20% không giới hạn trần")
                        .param("loaiGiamGia", "PERCENT")
                        .param("giaTriGiam", "20")
                        // Thiếu giamToiDa
                        .param("giaTriDonToiThieu", "100000")
                        .param("ngayBatDau", start.format(ISO_FORMATTER))
                        .param("ngayKetThuc", end.format(ISO_FORMATTER)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/voucher/tao-moi"))
                .andExpect(model().attributeHasFieldErrors("taoRequest", "giamToiDa"));
    }

    @Test
    @DisplayName("TC8: Chặn tạo voucher loại FIXED_AMOUNT nếu số tiền giảm lớn hơn đơn tối thiểu")
    void testChanTaoVoucher_FixedAmountLonHonDonToiThieu() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(10);

        mockMvc.perform(post("/admin/voucher/tao-moi")
                        .param("maCode", "TEST_FIXED_OVER")
                        .param("tenVoucher", "Giảm 100k cho đơn 50k")
                        .param("loaiGiamGia", "FIXED_AMOUNT")
                        .param("giaTriGiam", "100000") // Giảm 100k
                        .param("giaTriDonToiThieu", "50000") // Đơn chỉ 50k
                        .param("ngayBatDau", start.format(ISO_FORMATTER))
                        .param("ngayKetThuc", end.format(ISO_FORMATTER)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/voucher/tao-moi"))
                .andExpect(model().attributeHasFieldErrors("taoRequest", "giaTriGiam"));
    }

    @Test
    @DisplayName("TC9: Chặn tạo voucher nếu Thời điểm kết thúc diễn ra trước Thời điểm bắt đầu")
    void testChanTaoVoucher_NgayKetThucTruocNgayBatDau() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(5);
        LocalDateTime end = start.minusDays(2); // Kết thúc trước khi bắt đầu!

        mockMvc.perform(post("/admin/voucher/tao-moi")
                        .param("maCode", "TEST_TIME_FAIL")
                        .param("tenVoucher", "Thời gian phi logic")
                        .param("loaiGiamGia", "PERCENT")
                        .param("giaTriGiam", "10")
                        .param("giamToiDa", "20000")
                        .param("giaTriDonToiThieu", "100000")
                        .param("ngayBatDau", start.format(ISO_FORMATTER))
                        .param("ngayKetThuc", end.format(ISO_FORMATTER)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/voucher/tao-moi"))
                .andExpect(model().attributeHasFieldErrors("taoRequest", "ngayKetThuc"));
    }

    @Test
    @DisplayName("TC10: API tự động sinh mã voucher ngẫu nhiên thành công")
    void testApiSinhMaTuDong() throws Exception {
        mockMvc.perform(get("/admin/voucher/api/sinh-ma")
                        .param("loaiGiamGia", "PERCENT")
                        .param("giaTriGiam", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.maCode", startsWith("SMPL20-")));

        mockMvc.perform(get("/admin/voucher/api/sinh-ma")
                        .param("loaiGiamGia", "FIXED_AMOUNT")
                        .param("giaTriGiam", "50000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.maCode", startsWith("SMPL50K-")));
    }

    @Test
    @DisplayName("TC11: API kiểm tra mã code trùng lặp real-time")
    void testApiKiemTraMaCode() throws Exception {
        mockMvc.perform(get("/admin/voucher/api/kiem-tra-ma")
                        .param("maCode", "VOUCHER_TEST_UNIQUE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));

        mockMvc.perform(get("/admin/voucher/api/kiem-tra-ma")
                        .param("maCode", "CODE_CHUA_CO_12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }

    @Test
    @DisplayName("TC12: Bật / Tắt trạng thái kích hoạt của voucher (Toggle Active/Inactive)")
    void testToggleKichHoatVoucher() throws Exception {
        MaGiamGia v = maGiamGiaRepository.findByMaCodeIgnoreCase("VOUCHER_TEST_UNIQUE").orElseThrow();
        boolean trangThaiCu = v.getKichHoat();

        mockMvc.perform(post("/admin/voucher/kich-hoat/" + v.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/voucher/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.clear();
        MaGiamGia updated = maGiamGiaRepository.findById(v.getId()).orElseThrow();
        assertEquals(!trangThaiCu, updated.getKichHoat());
    }
}
