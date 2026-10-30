package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;
import vn.fpoly.simplain.util.JwtUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động Đánh giá sản phẩm đã mua (US15).
 * Bao phủ đầy đủ 3 tiêu chí:
 * 1. Check điều kiện ma_chi_tiet_don_hang_id đã hoàn thành.
 * 2. Upload JSON ảnh.
 * 3. Rating 1-5 sao.
 *
 * Chạy lệnh: .\mvnw.cmd -Dtest=DanhGiaSanPhamControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Đánh Giá Sản Phẩm Đã Mua (US15)")
class DanhGiaSanPhamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private DanhGiaSanPhamRepository danhGiaSanPhamRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichThuocRepository kichThuocRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    private static final String EMAIL_TEST = "us15.tester@gmail.com";
    private static final String SDT_TEST = "0915999888";
    private static final String MA_SP_TEST = "SP-US15-TEST";
    private static final String SLUG_SP_TEST = "sp-us15-ao-polo-the-thao";

    private KhachHang khachHangTest;
    private SanPham sanPhamTest;
    private DanhMuc danhMucTest;
    private MauSac mauSacTest;
    private KichThuoc kichThuocTest;
    private BienTheSanPham bienTheTest;
    private TrangThaiDonHang trangThaiHoanThanh;
    private TrangThaiDonHang trangThaiDangXuLy;
    private DonHang donHangHoanThanh;
    private ChiTietDonHang chiTietDonHangTest;

    @BeforeEach
    void setUp() {
        donDep();

        // 1. Tạo hoặc lấy Trạng thái Đơn hàng Hoàn thành (ID = 4)
        trangThaiHoanThanh = trangThaiDonHangRepository.findById(4L).orElseGet(() -> {
            TrangThaiDonHang tt = TrangThaiDonHang.builder()
                    .maTrangThai("HOAN_THANH")
                    .tenTrangThai("Hoàn thành")
                    .maMauHex("#10b981")
                    .build();
            return trangThaiDonHangRepository.save(tt);
        });

        trangThaiDangXuLy = trangThaiDonHangRepository.findById(1L).orElseGet(() -> {
            TrangThaiDonHang tt = TrangThaiDonHang.builder()
                    .maTrangThai("DANG_XU_LY")
                    .tenTrangThai("Đang xử lý")
                    .maMauHex("#f59e0b")
                    .build();
            return trangThaiDonHangRepository.save(tt);
        });

        // 2. Tạo Khách hàng test
        khachHangTest = khachHangRepository.save(KhachHang.builder()
                .maKhachHang("KH-US15-TEST")
                .hoTen("Nguyễn Văn US15")
                .email(EMAIL_TEST)
                .soDienThoai(SDT_TEST)
                .matKhauHash("$2a$10$abcdefghijklmnopqrstuvwxyz123456")
                .gioiTinh("Nam")
                .trangThai(1)
                .build());

        // 3. Tạo Danh mục & Sản phẩm
        danhMucTest = danhMucRepository.findByDuongDanSlug("dm-us15").orElseGet(() ->
                danhMucRepository.save(DanhMuc.builder()
                        .tenDanhMuc("Danh Mục US15")
                        .duongDanSlug("dm-us15")
                        .thuTuHienThi(1)
                        .kichHoat(true)
                        .build())
        );

        sanPhamTest = sanPhamRepository.save(SanPham.builder()
                .maSanPham(MA_SP_TEST)
                .tenSanPham("Áo Polo Thể Thao US15")
                .duongDanSlug(SLUG_SP_TEST)
                .maDanhMucId(danhMucTest.getId())
                .chatLieu("Excool")
                .formDang("Slim Fit")
                .moTaChiTiet("Mô tả chi tiết áo polo thể thao phục vụ kiểm thử US15.")
                .giaCoBan(new BigDecimal("299000"))
                .kichHoat(true)
                .build());

        // 4. Tạo Màu sắc, Kích thước, Biến thể
        mauSacTest = mauSacRepository.findByMaMauIgnoreCase("#1e293b").orElseGet(() ->
                mauSacRepository.save(MauSac.builder()
                        .maMau("#1e293b")
                        .tenMau("Đen Than US15")
                        .kichHoat(true)
                        .build())
        );

        kichThuocTest = kichThuocRepository.findByMaKichThuocIgnoreCase("L-US15").orElseGet(() ->
                kichThuocRepository.save(KichThuoc.builder()
                        .maKichThuoc("L-US15")
                        .tenKichThuoc("Size L US15")
                        .kichHoat(true)
                        .build())
        );

        bienTheTest = bienTheSanPhamRepository.save(BienTheSanPham.builder()
                .maSanPhamId(sanPhamTest.getId())
                .maMauSacId(mauSacTest.getId())
                .maKichThuocId(kichThuocTest.getId())
                .maSku("SP-US15-L-DEN")
                .maVachBarcode("893000000151")
                .soLuong(50)
                .giaBan(new BigDecimal("299000"))
                .giaGoc(new BigDecimal("350000"))
                .kichHoat(true)
                .build());

        // 5. Tạo Đơn hàng Hoàn thành
        donHangHoanThanh = donHangRepository.save(DonHang.builder()
                .maDonHang("DH-US15-HOANTHANH")
                .khachHang(khachHangTest)
                .trangThaiDonHang(trangThaiHoanThanh)
                .tenNguoiNhan("Nguyễn Văn US15")
                .soDienThoaiNhan(SDT_TEST)
                .diaChiGiaoHang("Số 123 Đường Nam Kỳ Khởi Nghĩa, Quận 3, TP. Hồ Chí Minh")
                .tongTienHang(new BigDecimal("299000"))
                .tongThanhToan(new BigDecimal("299000"))
                .ngayTao(LocalDateTime.now().minusDays(2))
                .build());

        // 6. Tạo Chi tiết đơn hàng thuộc đơn hàng hoàn thành
        chiTietDonHangTest = chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .maDonHangId(donHangHoanThanh.getId())
                .maBienTheId(bienTheTest.getId())
                .tenSanPham(sanPhamTest.getTenSanPham())
                .maSku(bienTheTest.getMaSku())
                .donGia(new BigDecimal("299000"))
                .soLuong(1)
                .thanhTien(new BigDecimal("299000"))
                .build());
    }

    @AfterEach
    void tearDown() {
        donDep();
    }

    private void donDep() {
        danhGiaSanPhamRepository.findAll().forEach(dg -> {
            if (dg.getNoiDung() != null && dg.getNoiDung().contains("US15")) {
                danhGiaSanPhamRepository.delete(dg);
            }
        });

        if (sanPhamTest != null && sanPhamTest.getId() != null) {
            danhGiaSanPhamRepository.findByMaSanPhamIdOrderByNgayTaoDesc(sanPhamTest.getId())
                    .forEach(danhGiaSanPhamRepository::delete);
        }

        chiTietDonHangRepository.findAll().forEach(ct -> {
            if (ct.getMaSku() != null && ct.getMaSku().startsWith("SP-US15")) {
                danhGiaSanPhamRepository.findByMaChiTietDonHangId(ct.getId()).ifPresent(danhGiaSanPhamRepository::delete);
                chiTietDonHangRepository.delete(ct);
            }
        });

        donHangRepository.findAll().forEach(dh -> {
            if (dh.getMaDonHang() != null && dh.getMaDonHang().startsWith("DH-US15")) {
                donHangRepository.delete(dh);
            }
        });

        if (bienTheTest != null && bienTheTest.getId() != null) {
            bienTheSanPhamRepository.delete(bienTheTest);
        }

        if (sanPhamTest != null && sanPhamTest.getId() != null) {
            sanPhamRepository.delete(sanPhamTest);
        }

        danhMucRepository.findByDuongDanSlug("dm-us15").ifPresent(danhMucRepository::delete);
        mauSacRepository.findByMaMauIgnoreCase("#1e293b").ifPresent(mauSacRepository::delete);
        kichThuocRepository.findByMaKichThuocIgnoreCase("L-US15").ifPresent(kichThuocRepository::delete);

        khachHangRepository.findByEmail(EMAIL_TEST).ifPresent(khachHangRepository::delete);
    }

    private Cookie taoCookieDangNhap() {
        String token = jwtUtil.generateToken(khachHangTest);
        return new Cookie(KhachHangController.COOKIE_TOKEN, token);
    }

    @Test
    @DisplayName("TC1: Đánh giá thành công - Đáp ứng đủ 3 tiêu chí (Đơn hoàn thành, Upload JSON ảnh, Rating 1-5 sao)")
    void testDanhGiaThanhCong_DayDuTieuChi() throws Exception {
        Cookie authCookie = taoCookieDangNhap();

        MockMultipartFile file1 = new MockMultipartFile(
                "anhFiles",
                "anh_that_1.jpg",
                "image/jpeg",
                "fake image content 1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "anhFiles",
                "anh_that_2.png",
                "image/png",
                "fake image content 2".getBytes()
        );

        mockMvc.perform(multipart("/khach-hang/danh-gia/gui")
                        .file(file1)
                        .file(file2)
                        .cookie(authCookie)
                        .param("maSanPhamId", String.valueOf(sanPhamTest.getId()))
                        .param("maChiTietDonHangId", String.valueOf(chiTietDonHangTest.getId()))
                        .param("soSao", "5")
                        .param("noiDung", "Sản phẩm chất lượng cực kỳ tốt, vải mềm mát mịn US15!")
                        .param("duongDanSlug", sanPhamTest.getDuongDanSlug()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/san-pham/" + sanPhamTest.getDuongDanSlug() + "#khungDanhGia"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        // Kiểm tra database lưu đúng
        List<DanhGiaSanPham> ds = danhGiaSanPhamRepository.findByMaSanPhamIdOrderByNgayTaoDesc(sanPhamTest.getId());
        assertFalse(ds.isEmpty(), "Phải có ít nhất 1 bản ghi đánh giá được lưu");
        DanhGiaSanPham dg = ds.get(0);

        // Kiểm tra Tiêu chí 1: Đơn hàng hoàn thành & Khách hàng
        assertEquals(khachHangTest.getId(), dg.getMaKhachHangId());
        assertEquals(chiTietDonHangTest.getId(), dg.getMaChiTietDonHangId());

        // Kiểm tra Tiêu chí 3: Rating 5 sao
        assertEquals(5, dg.getSoSao());
        assertTrue(dg.getNoiDung().contains("US15"));

        // Kiểm tra Tiêu chí 2: JSON ảnh được lưu trữ
        assertNotNull(dg.getDanhSachAnhJson());
        assertTrue(dg.getDanhSachAnhJson().contains(".jpg") || dg.getDanhSachAnhJson().contains(".png"));
        assertTrue(dg.getDanhSachAnhJson().startsWith("[") && dg.getDanhSachAnhJson().endsWith("]"));
    }

    @Test
    @DisplayName("TC2: Từ chối đánh giá khi Đơn hàng chưa ở trạng thái HOÀN THÀNH")
    void testTuChoiDanhGia_KhiDonHangChuaHoanThanh() throws Exception {
        Cookie authCookie = taoCookieDangNhap();

        // Tạo đơn hàng ở trạng thái Đang xử lý
        DonHang dhChuaXong = donHangRepository.save(DonHang.builder()
                .maDonHang("DH-US15-DANGXULY")
                .khachHang(khachHangTest)
                .trangThaiDonHang(trangThaiDangXuLy)
                .tenNguoiNhan("Nguyễn Văn US15")
                .soDienThoaiNhan(SDT_TEST)
                .diaChiGiaoHang("Địa chỉ giao hàng tạm thời")
                .tongTienHang(new BigDecimal("299000"))
                .tongThanhToan(new BigDecimal("299000"))
                .build());

        ChiTietDonHang ctChuaXong = chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .maDonHangId(dhChuaXong.getId())
                .maBienTheId(bienTheTest.getId())
                .tenSanPham(sanPhamTest.getTenSanPham())
                .maSku(bienTheTest.getMaSku())
                .donGia(new BigDecimal("299000"))
                .soLuong(1)
                .thanhTien(new BigDecimal("299000"))
                .build());

        mockMvc.perform(multipart("/khach-hang/danh-gia/gui")
                        .cookie(authCookie)
                        .param("maSanPhamId", String.valueOf(sanPhamTest.getId()))
                        .param("maChiTietDonHangId", String.valueOf(ctChuaXong.getId()))
                        .param("soSao", "5")
                        .param("noiDung", "Nhận xét khi chưa nhận được hàng")
                        .param("duongDanSlug", sanPhamTest.getDuongDanSlug()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/san-pham/" + sanPhamTest.getDuongDanSlug() + "#khungDanhGia"))
                .andExpect(flash().attributeExists("loiDanhGia"))
                .andExpect(flash().attribute("loiDanhGia", containsString("Đơn hàng chưa hoàn thành")));

        // Dọn dẹp
        chiTietDonHangRepository.delete(ctChuaXong);
        donHangRepository.delete(dhChuaXong);
    }

    @Test
    @DisplayName("TC3: Từ chối đánh giá trùng lặp lần 2 trên cùng một chi tiết đơn hàng")
    void testTuChoiDanhGia_KhiDaDanhGiaRoi() throws Exception {
        Cookie authCookie = taoCookieDangNhap();

        // Đánh giá lần 1 thành công
        mockMvc.perform(multipart("/khach-hang/danh-gia/gui")
                        .cookie(authCookie)
                        .param("maSanPhamId", String.valueOf(sanPhamTest.getId()))
                        .param("maChiTietDonHangId", String.valueOf(chiTietDonHangTest.getId()))
                        .param("soSao", "4")
                        .param("noiDung", "Đánh giá lần 1 rất tốt US15")
                        .param("duongDanSlug", sanPhamTest.getDuongDanSlug()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/san-pham/" + sanPhamTest.getDuongDanSlug() + "#khungDanhGia"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        // Cố tình đánh giá lần 2 trên cùng chiTietDonHangTest
        mockMvc.perform(multipart("/khach-hang/danh-gia/gui")
                        .cookie(authCookie)
                        .param("maSanPhamId", String.valueOf(sanPhamTest.getId()))
                        .param("maChiTietDonHangId", String.valueOf(chiTietDonHangTest.getId()))
                        .param("soSao", "5")
                        .param("noiDung", "Cố tình đánh giá lần 2 trùng lặp")
                        .param("duongDanSlug", sanPhamTest.getDuongDanSlug()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/san-pham/" + sanPhamTest.getDuongDanSlug() + "#khungDanhGia"))
                .andExpect(flash().attributeExists("loiDanhGia"))
                .andExpect(flash().attribute("loiDanhGia", containsString("đã được bạn gửi đánh giá trước đó rồi")));
    }

    @Test
    @DisplayName("TC4: Bắt lỗi validation khi Rating không hợp lệ (< 1 hoặc > 5)")
    void testBatLoiRatingKhongHopLe() throws Exception {
        Cookie authCookie = taoCookieDangNhap();

        // Số sao = 6 (ngoài khoảng 1-5)
        mockMvc.perform(multipart("/khach-hang/danh-gia/gui")
                        .cookie(authCookie)
                        .param("maSanPhamId", String.valueOf(sanPhamTest.getId()))
                        .param("maChiTietDonHangId", String.valueOf(chiTietDonHangTest.getId()))
                        .param("soSao", "6")
                        .param("noiDung", "Đánh giá vượt mức 5 sao")
                        .param("duongDanSlug", sanPhamTest.getDuongDanSlug()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/san-pham/" + sanPhamTest.getDuongDanSlug() + "#khungDanhGia"))
                .andExpect(flash().attributeExists("loiDanhGia"));

        // Số sao = 0 (dưới 1 sao)
        mockMvc.perform(multipart("/khach-hang/danh-gia/gui")
                        .cookie(authCookie)
                        .param("maSanPhamId", String.valueOf(sanPhamTest.getId()))
                        .param("maChiTietDonHangId", String.valueOf(chiTietDonHangTest.getId()))
                        .param("soSao", "0")
                        .param("noiDung", "Đánh giá 0 sao không hợp lệ")
                        .param("duongDanSlug", sanPhamTest.getDuongDanSlug()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/san-pham/" + sanPhamTest.getDuongDanSlug() + "#khungDanhGia"))
                .andExpect(flash().attributeExists("loiDanhGia"));
    }

    @Test
    @DisplayName("TC5: API kiểm tra điều kiện trả về danh sách chi tiết đơn hàng đủ điều kiện đánh giá")
    void testApiKiemTraDieuKienDanhGia() throws Exception {
        Cookie authCookie = taoCookieDangNhap();

        // Khi đã mua và hoàn thành: coQuyen = true, danhSachChiTiet có ít nhất 1 phần tử
        mockMvc.perform(get("/khach-hang/danh-gia/kiem-tra-dieu-kien/" + sanPhamTest.getId())
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coQuyen", is(true)))
                .andExpect(jsonPath("$.duDieuKien", is(true)))
                .andExpect(jsonPath("$.danhSachChiTiet", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.danhSachChiTiet[0].maSku", is(bienTheTest.getMaSku())));

        // Khi chưa đăng nhập (không có cookie): coQuyen = false, canDangNhap = true
        mockMvc.perform(get("/khach-hang/danh-gia/kiem-tra-dieu-kien/" + sanPhamTest.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coQuyen", is(false)))
                .andExpect(jsonPath("$.canDangNhap", is(true)));
    }
}
