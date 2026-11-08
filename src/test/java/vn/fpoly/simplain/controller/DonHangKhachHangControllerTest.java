package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.dto.CapNhatTrangThaiVanDonRequest;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;
import vn.fpoly.simplain.service.LoyaltyHoiVienService;
import vn.fpoly.simplain.service.QuanLyVanDonService;
import vn.fpoly.simplain.util.JwtUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động Web MVC cho chức năng Khách hàng theo dõi Lịch sử đơn hàng cá nhân (US23).
 * - Tiêu chí 1: Liệt kê danh sách đơn hàng cá nhân, lọc tab trạng thái, tìm kiếm.
 * - Tiêu chí 2: Xem chi tiết trạng thái đơn hàng, mã vận đơn, hành trình vận chuyển.
 * - Form Validation & Bảo mật: Chống IDOR, kiểm tra tính hợp lệ của form hủy đơn và hoàn kho.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Theo dõi Lịch sử đơn hàng cá nhân (US23)")
class DonHangKhachHangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;

    @Autowired
    private HoSoHoiVienRepository hoSoHoiVienRepository;

    @Autowired
    private LoyaltyHoiVienService loyaltyHoiVienService;

    @Autowired
    private QuanLyVanDonService quanLyVanDonService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private KhachHang khachHangA;
    private KhachHang khachHangB;
    private Cookie cookieKhachA;
    private Cookie cookieKhachB;
    private TrangThaiDonHang ttChoXacNhan;
    private TrangThaiDonHang ttDangGiao;
    private TrangThaiDonHang ttHoanThanh;
    private TrangThaiDonHang ttDaHuy;
    private PhuongThucThanhToan ptttTest;
    private BienTheSanPham bienTheTest;

    @BeforeEach
    void setUp() {
        long timestamp = System.currentTimeMillis();

        // 1. Khởi tạo trạng thái đơn hàng
        ttChoXacNhan = trangThaiDonHangRepository.findByMaTrangThai("CHO_XAC_NHAN")
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("CHO_XAC_NHAN").tenTrangThai("Chờ xác nhận").maMauHex("#F59E0B").build()));

        ttDangGiao = trangThaiDonHangRepository.findByMaTrangThai("DANG_GIAO")
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("DANG_GIAO").tenTrangThai("Đang giao hàng").maMauHex("#8B5CF6").build()));

        ttHoanThanh = trangThaiDonHangRepository.findByMaTrangThai("HOAN_THANH")
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("HOAN_THANH").tenTrangThai("Hoàn thành").maMauHex("#10B981").build()));

        ttDaHuy = trangThaiDonHangRepository.findByMaTrangThai("DA_HUY")
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("DA_HUY").tenTrangThai("Đã hủy").maMauHex("#EF4444").build()));

        // 2. Phương thức thanh toán
        ptttTest = phuongThucThanhToanRepository.findAll().stream().findFirst()
                .orElseGet(() -> phuongThucThanhToanRepository.save(PhuongThucThanhToan.builder()
                        .maPhuongThuc("COD").tenPhuongThuc("Thanh toán khi nhận hàng").kichHoat(true).laTrucTuyen(false).build()));

        // 3. Biến thể sản phẩm test
        bienTheTest = bienTheSanPhamRepository.findAll().stream().findFirst()
                .orElseGet(() -> bienTheSanPhamRepository.save(BienTheSanPham.builder()
                        .maSanPhamId(1L).maMauSacId(1L).maKichThuocId(1L)
                        .maSku("SKU-US23-" + timestamp)
                        .giaBan(new BigDecimal("300000"))
                        .giaGoc(new BigDecimal("350000"))
                        .soLuong(100)
                        .build()));

        // 4. Khách hàng A (chính) và Khách hàng B (để test IDOR)
        String sdtA = String.format("0981%06d", (timestamp % 1000000));
        khachHangA = khachHangRepository.save(KhachHang.builder()
                .maKhachHang("KHA" + timestamp)
                .hoTen("Nguyễn Khách Hàng A")
                .soDienThoai(sdtA)
                .email(sdtA + "@gmail.com")
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build());
        cookieKhachA = new Cookie(KhachHangController.COOKIE_TOKEN, jwtUtil.generateToken(khachHangA));

        String sdtB = String.format("0982%06d", (timestamp % 1000000));
        khachHangB = khachHangRepository.save(KhachHang.builder()
                .maKhachHang("KHB" + timestamp)
                .hoTen("Trần Khách Hàng B")
                .soDienThoai(sdtB)
                .email(sdtB + "@gmail.com")
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build());
        cookieKhachB = new Cookie(KhachHangController.COOKIE_TOKEN, jwtUtil.generateToken(khachHangB));
    }

    private DonHang taoDonHangTest(KhachHang kh, TrangThaiDonHang tt, String maVanDon, BigDecimal coolcash) {
        String maDh = "DH" + System.currentTimeMillis() + (int) (Math.random() * 900 + 100);
        DonHang dh = donHangRepository.save(DonHang.builder()
                .maDonHang(maDh)
                .khachHang(kh)
                .trangThaiDonHang(tt)
                .phuongThucThanhToan(ptttTest)
                .maVanDon(maVanDon)
                .loaiDonHang("ONLINE")
                .tongTienHang(new BigDecimal("300000"))
                .tienGiamGia(BigDecimal.ZERO)
                .tienCoolcashDung(coolcash != null ? coolcash : BigDecimal.ZERO)
                .phiVanChuyen(BigDecimal.ZERO)
                .tongThanhToan(new BigDecimal("300000").subtract(coolcash != null ? coolcash : BigDecimal.ZERO))
                .tenNguoiNhan(kh.getHoTen())
                .soDienThoaiNhan(kh.getSoDienThoai())
                .diaChiGiaoHang("123 Cầu Giấy, Hà Nội")
                .ghiChu("Giao giờ hành chính")
                .ngayTao(LocalDateTime.now())
                .build());

        chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .maDonHangId(dh.getId())
                .maBienTheId(bienTheTest.getId())
                .tenSanPham("Áo Thun Nam Cao Cấp US23")
                .maSku(bienTheTest.getMaSku())
                .donGia(new BigDecimal("300000"))
                .soLuong(1)
                .thanhTien(new BigDecimal("300000"))
                .build());

        return dh;
    }

    @Test
    @DisplayName("TC1: Chưa đăng nhập truy cập /khach-hang/don-hang chuyển hướng về trang đăng nhập")
    void testKhongDangNhap_ChuyenHuongDangNhap() throws Exception {
        mockMvc.perform(get("/khach-hang/don-hang"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/dang-nhap?redirect=/khach-hang/don-hang"));
    }

    @Test
    @DisplayName("TC2: Khách hàng đăng nhập xem danh sách đơn hàng cá nhân thành công")
    void testXemDanhSachDonHang_DangNhapThanhCong() throws Exception {
        taoDonHangTest(khachHangA, ttChoXacNhan, null, BigDecimal.ZERO);
        taoDonHangTest(khachHangA, ttDangGiao, "SMPL-VD-TEST123", BigDecimal.ZERO);

        mockMvc.perform(get("/khach-hang/don-hang").cookie(cookieKhachA))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/don-hang/danh-sach"))
                .andExpect(model().attributeExists("danhSachDonHang"))
                .andExpect(model().attributeExists("demTrangThai"))
                .andExpect(model().attributeExists("thongTin"));
    }

    @Test
    @DisplayName("TC3: Lọc đơn hàng theo Tab trạng thái (Chờ xác nhận, Đang giao, Hoàn thành)")
    void testLocDonHang_TheoTrangThai() throws Exception {
        taoDonHangTest(khachHangA, ttChoXacNhan, null, BigDecimal.ZERO);
        taoDonHangTest(khachHangA, ttDangGiao, "SMPL-VD-TEST456", BigDecimal.ZERO);

        mockMvc.perform(get("/khach-hang/don-hang")
                        .cookie(cookieKhachA)
                        .param("trangThai", "CHO_XAC_NHAN"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/don-hang/danh-sach"))
                .andExpect(model().attribute("trangThaiHienTai", is("CHO_XAC_NHAN")));
    }

    @Test
    @DisplayName("TC4: Tìm kiếm đơn hàng theo Mã đơn hoặc từ khóa")
    void testTimKiemDonHang_TheoTuKhoa() throws Exception {
        DonHang dh = taoDonHangTest(khachHangA, ttChoXacNhan, null, BigDecimal.ZERO);

        mockMvc.perform(get("/khach-hang/don-hang")
                        .cookie(cookieKhachA)
                        .param("q", dh.getMaDonHang()))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/don-hang/danh-sach"))
                .andExpect(model().attribute("tuKhoaHienTai", is(dh.getMaDonHang())));
    }

    @Test
    @DisplayName("TC5: Xem chi tiết trạng thái đơn hàng & mã vận đơn (US23 - Tiêu chí 2)")
    void testXemChiTietDonHang_CoMaVanDonVaHanhTrinh() throws Exception {
        String maVanDon = "SMPL-VD-TRACKING-999";
        DonHang dh = taoDonHangTest(khachHangA, ttDangGiao, maVanDon, BigDecimal.ZERO);

        // Ghi log lịch sử trạng thái
        lichSuTrangThaiDonHangRepository.save(LichSuTrangThaiDonHang.builder()
                .donHang(dh)
                .trangThaiMoi(ttDangGiao)
                .ghiChu("Đã giao cho đơn vị vận chuyển GHTK")
                .ngayChuyen(LocalDateTime.now())
                .build());

        mockMvc.perform(get("/khach-hang/don-hang/" + dh.getMaDonHang()).cookie(cookieKhachA))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/don-hang/chi-tiet"))
                .andExpect(model().attributeExists("donHangDto"))
                .andExpect(model().attribute("donHangDto", hasProperty("tenDonViVanChuyen", notNullValue())));
    }

    @Test
    @DisplayName("TC6: Bảo mật IDOR - Khách hàng B không thể xem đơn hàng của Khách hàng A")
    void testBaoMatIDOR_KhachBKhongXemDuocDonKhachA() throws Exception {
        DonHang dhCuaA = taoDonHangTest(khachHangA, ttChoXacNhan, null, BigDecimal.ZERO);

        // Khách B cố tình truy cập vào mã đơn của Khách A
        mockMvc.perform(get("/khach-hang/don-hang/" + dhCuaA.getMaDonHang()).cookie(cookieKhachB))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/don-hang"))
                .andExpect(flash().attributeExists("loiTruyCap"));
    }

    @Test
    @DisplayName("TC7: Form Validate hủy đơn hàng - Bắt buộc chọn lý do hủy")
    void testValidateFormHuyDon_LoiKhiChuaChonLyDo() throws Exception {
        DonHang dh = taoDonHangTest(khachHangA, ttChoXacNhan, null, BigDecimal.ZERO);

        // Gửi form rỗng không có lyDoHuy
        mockMvc.perform(post("/khach-hang/don-hang/" + dh.getMaDonHang() + "/huy")
                        .cookie(cookieKhachA)
                        .param("lyDoHuy", "")
                        .param("ghiChuChiTiet", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/don-hang/chi-tiet"))
                .andExpect(model().attributeExists("loiFormHuy"));
    }

    @Test
    @DisplayName("TC8: Khách hàng hủy đơn hàng hợp lệ khi ở trạng thái Chờ xác nhận - Hoàn tồn kho và điểm Coolcash")
    void testKhachHangHuyDon_ThanhCongVaHoanTonKhoCoolcash() throws Exception {
        // Nạp sẵn 50.000 điểm cho khách A
        HoSoHoiVien hs = loyaltyHoiVienService.layHoacTaoHoSoHoiVien(khachHangA.getId());
        hs.setSoDuCoolcash(new BigDecimal("50000"));
        hoSoHoiVienRepository.save(hs);

        // Đơn hàng có dùng 20.000 điểm Coolcash
        DonHang dh = taoDonHangTest(khachHangA, ttChoXacNhan, null, new BigDecimal("20000"));

        int tonKhoTruoc = bienTheSanPhamRepository.findById(bienTheTest.getId()).orElseThrow().getSoLuong();

        // Gửi form hủy đơn hợp lệ
        mockMvc.perform(post("/khach-hang/don-hang/" + dh.getMaDonHang() + "/huy")
                        .cookie(cookieKhachA)
                        .param("lyDoHuy", "Tôi muốn thay đổi địa chỉ nhận hàng")
                        .param("ghiChuChiTiet", "Đổi sang địa chỉ văn phòng"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/don-hang/" + dh.getMaDonHang()))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        // Làm sạch L1 Cache của EntityManager để đọc dữ liệu mới nhất từ DB
        entityManager.clear();

        // Kiểm tra trạng thái đơn đã đổi sang DA_HUY
        DonHang dhSau = donHangRepository.findById(dh.getId()).orElseThrow();
        assertEquals("DA_HUY", dhSau.getTrangThaiDonHang().getMaTrangThai());

        // Kiểm tra tồn kho đã được hoàn lại 1 cái
        int tonKhoSau = bienTheSanPhamRepository.findById(bienTheTest.getId()).orElseThrow().getSoLuong();
        assertEquals(tonKhoTruoc + 1, tonKhoSau);

        // Kiểm tra điểm Coolcash đã được hoàn trả lại ví hội viên
        HoSoHoiVien hsSau = hoSoHoiVienRepository.findByMaKhachHangId(khachHangA.getId()).orElseThrow();
        assertTrue(hsSau.getSoDuCoolcash().compareTo(new BigDecimal("50000")) >= 0);
    }

    @Test
    @DisplayName("TC9: Không thể hủy đơn hàng khi đơn đã ở trạng thái Đang giao hàng")
    void testKhongTheHuyDon_KhiDangGiaoHang() throws Exception {
        DonHang dh = taoDonHangTest(khachHangA, ttDangGiao, "SMPL-VD-FAIL-CANCEL", BigDecimal.ZERO);

        mockMvc.perform(post("/khach-hang/don-hang/" + dh.getMaDonHang() + "/huy")
                        .cookie(cookieKhachA)
                        .param("lyDoHuy", "Tôi không có nhu cầu mua nữa")
                        .param("ghiChuChiTiet", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/don-hang/" + dh.getMaDonHang()))
                .andExpect(flash().attributeExists("loiHuyDon"));

        // Đơn hàng vẫn giữ nguyên trạng thái DANG_GIAO
        DonHang dhSau = donHangRepository.findById(dh.getId()).orElseThrow();
        assertEquals("DANG_GIAO", dhSau.getTrangThaiDonHang().getMaTrangThai());
    }

    @Test
    @DisplayName("TC10: Đơn hàng có nhiều sản phẩm - Danh sách hiển thị sản phẩm chính & số lượng rõ ràng, Chi tiết hiển thị toàn bộ sản phẩm")
    void testDonHangNhieuSanPham_DanhSachHienThiSanPhamChinhVaSoLuong() throws Exception {
        String maDh = "DH" + System.currentTimeMillis() + (int) (Math.random() * 900 + 100);
        DonHang dh = donHangRepository.save(DonHang.builder()
                .maDonHang(maDh)
                .khachHang(khachHangA)
                .trangThaiDonHang(ttChoXacNhan)
                .phuongThucThanhToan(ptttTest)
                .loaiDonHang("ONLINE")
                .tongTienHang(new BigDecimal("850000"))
                .tienGiamGia(BigDecimal.ZERO)
                .tienCoolcashDung(BigDecimal.ZERO)
                .phiVanChuyen(BigDecimal.ZERO)
                .tongThanhToan(new BigDecimal("850000"))
                .tenNguoiNhan(khachHangA.getHoTen())
                .soDienThoaiNhan(khachHangA.getSoDienThoai())
                .diaChiGiaoHang("123 Cầu Giấy, Hà Nội")
                .ghiChu("Đơn gồm nhiều sản phẩm")
                .ngayTao(LocalDateTime.now())
                .build());

        chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .maDonHangId(dh.getId())
                .maBienTheId(bienTheTest.getId())
                .tenSanPham("Áo Thun Polo Simplain")
                .maSku("POLO-001")
                .donGia(new BigDecimal("300000"))
                .soLuong(2)
                .thanhTien(new BigDecimal("600000"))
                .build());

        chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .maDonHangId(dh.getId())
                .maBienTheId(bienTheTest.getId())
                .tenSanPham("Quần Short Kaki Nam")
                .maSku("SHORT-002")
                .donGia(new BigDecimal("250000"))
                .soLuong(1)
                .thanhTien(new BigDecimal("250000"))
                .build());

        // 1. Kiểm tra màn hình danh sách đơn hàng:
        // Đơn hàng hiển thị sản phẩm chính, số lượng món và thông báo gồm 2 loại sản phẩm
        mockMvc.perform(get("/khach-hang/don-hang")
                        .cookie(cookieKhachA)
                        .param("q", maDh))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/don-hang/danh-sach"))
                .andExpect(content().string(containsString("Áo Thun Polo Simplain")))
                .andExpect(content().string(containsString("Đơn hàng gồm")))
                .andExpect(content().string(containsString("Xem thêm")))
                .andExpect(content().string(containsString("3 món")));

        // 2. Kiểm tra màn hình chi tiết đơn hàng:
        // Phải hiển thị đầy đủ cả 2 sản phẩm và chi tiết số lượng
        mockMvc.perform(get("/khach-hang/don-hang/" + maDh).cookie(cookieKhachA))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/don-hang/chi-tiet"))
                .andExpect(content().string(containsString("Toàn Bộ Mặt Hàng Đã Đặt")))
                .andExpect(content().string(containsString("Áo Thun Polo Simplain")))
                .andExpect(content().string(containsString("Quần Short Kaki Nam")))
                .andExpect(content().string(containsString("2 loại sản phẩm")))
                .andExpect(content().string(containsString("Tổng 3 món")));
    }

    @Test
    @DisplayName("Kiểm tra đơn hàng Thất lạc một phần: hiển thị đúng tiến trình stepper, phân loại món mất/giữ nguyên và form hoàn tiền")
    void testXemChiTietDonHangThatLacMotPhanVaFormHoanTien() throws Exception {
        long ts = System.currentTimeMillis();
        String maDh = "DH_TEST_LOST_" + ts;

        TrangThaiDonHang ttThatLacMotPhan = trangThaiDonHangRepository.findByMaTrangThai("THAT_LAC_MOT_PHAN")
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("THAT_LAC_MOT_PHAN").tenTrangThai("Thất lạc một phần hàng").maMauHex("#EA580C").build()));

        PhuongThucThanhToan ptOnline = phuongThucThanhToanRepository.findByMaPhuongThuc("ZALOPAY")
                .orElseGet(() -> phuongThucThanhToanRepository.save(PhuongThucThanhToan.builder()
                        .maPhuongThuc("ZALOPAY").tenPhuongThuc("Ví điện tử ZaloPay").kichHoat(true).laTrucTuyen(true).build()));

        DonHang dh = donHangRepository.save(DonHang.builder()
                .maDonHang(maDh)
                .khachHang(khachHangA)
                .soDienThoaiNhan(khachHangA.getSoDienThoai())
                .tenNguoiNhan(khachHangA.getHoTen())
                .diaChiGiaoHang("123 Phố Huế, Hai Bà Trưng, Hà Nội")
                .trangThaiDonHang(ttThatLacMotPhan)
                .phuongThucThanhToan(ptOnline)
                .tongTienHang(new BigDecimal("550000"))
                .tongThanhToan(new BigDecimal("550000"))
                .ngayTao(LocalDateTime.now())
                .loaiDonHang("ONLINE")
                .trangThaiHoanTien("CHO_HOAN_TIEN")
                .ghiChuHoanTien("Thất lạc 1/2 món. Số tiền hoàn trả cho sản phẩm bị mất: 300000 đ.")
                .build());

        // Món 1: Bị mất hàng
        ChiTietDonHang itemMat = chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .maDonHangId(dh.getId())
                .maBienTheId(bienTheTest.getId())
                .tenSanPham("Áo Sơ Mi Oxford Nam")
                .maSku("SOMI-LOST-01")
                .donGia(new BigDecimal("300000"))
                .soLuong(1)
                .thanhTien(new BigDecimal("300000"))
                .trangThaiItem("MAT_HANG")
                .ghiChuItem("Bị mất trên đường giao")
                .build());

        // Món 2: Giữ nguyên
        ChiTietDonHang itemGiu = chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .maDonHangId(dh.getId())
                .maBienTheId(bienTheTest.getId())
                .tenSanPham("Quần Jean Nam Regular Fit")
                .maSku("JEAN-KEEP-02")
                .donGia(new BigDecimal("250000"))
                .soLuong(1)
                .thanhTien(new BigDecimal("250000"))
                .trangThaiItem("BINH_THUONG")
                .build());

        // 1. Kiểm tra màn hình chi tiết: Không bị quay về Đặt hàng (stepperIndex = 3), có banner sự cố, form hoàn tiền mở sẵn
        mockMvc.perform(get("/khach-hang/don-hang/" + maDh).cookie(cookieKhachA))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/don-hang/chi-tiet"))
                .andExpect(model().attribute("donHangDto", hasProperty("stepperIndex", is(3))))
                .andExpect(content().string(containsString("Sự cố vận chuyển: Đơn hàng bị thất lạc một phần sản phẩm")))
                .andExpect(content().string(containsString("BỊ THẤT LẠC KHI GIAO (ĐANG HOÀN TIỀN)")))
                .andExpect(content().string(containsString("GIỮ NGUYÊN (ĐANG GIAO)")))
                .andExpect(content().string(containsString("CUNG CẤP TÀI KHOẢN NGÂN HÀNG ĐỂ NHẬN HOÀN TIỀN")))
                .andExpect(content().string(containsString("Thất lạc 1/2 món")));

        // 2. Khách hàng gửi thông tin tài khoản ngân hàng để nhận hoàn tiền
        mockMvc.perform(post("/khach-hang/don-hang/" + maDh + "/gui-thong-tin-hoan-tien")
                        .cookie(cookieKhachA)
                        .param("nganHang", "Vietcombank (VCB)")
                        .param("soTaiKhoan", "0123456789")
                        .param("chuTaiKhoan", "NGUYEN VAN A")
                        .param("ghiChu", "Hoàn sớm giúp em ạ"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/don-hang/" + maDh));

        // 3. Kiểm tra thông tin đã lưu vào DB
        DonHang updatedDh = donHangRepository.findById(dh.getId()).orElseThrow();
        assertEquals("Vietcombank (VCB)", updatedDh.getNganHangHoanTien());
        assertEquals("0123456789", updatedDh.getSoTaiKhoanHoanTien());
        assertEquals("NGUYEN VAN A", updatedDh.getChuTaiKhoanHoanTien());

        // 4. Màn hình chi tiết sau khi đã gửi thông tin: Hiển thị banner đã tiếp nhận thông tin và số tài khoản
        mockMvc.perform(get("/khach-hang/don-hang/" + maDh).cookie(cookieKhachA))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ĐÃ TIẾP NHẬN THÔNG TIN - ĐANG CHỜ SHOP HOÀN TIỀN")))
                .andExpect(content().string(containsString("0123456789")))
                .andExpect(content().string(containsString("Vietcombank (VCB)")));
    }

    @Test
    @DisplayName("Kiểm tra Combo đóng gói chung: khi chọn 1 món combo bị mất, tự động cascade mất TOÀN BỘ combo, không cộng kho và hoàn tiền đúng gói")
    void testComboDongGoiChungMatHangToanBoCombo() throws Exception {
        long ts = System.currentTimeMillis();
        String maDh = "DH_COMBO_LOST_" + ts;

        TrangThaiDonHang ttDangGiaoLoc = trangThaiDonHangRepository.findByMaTrangThai("DANG_GIAO")
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("DANG_GIAO").tenTrangThai("Đang giao hàng").maMauHex("#3B82F6").build()));

        TrangThaiDonHang ttThatLacMotPhan = trangThaiDonHangRepository.findByMaTrangThai("THAT_LAC_MOT_PHAN")
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("THAT_LAC_MOT_PHAN").tenTrangThai("Thất lạc một phần hàng").maMauHex("#EA580C").build()));

        PhuongThucThanhToan ptOnline = phuongThucThanhToanRepository.findByMaPhuongThuc("VNPAY")
                .orElseGet(() -> phuongThucThanhToanRepository.save(PhuongThucThanhToan.builder()
                        .maPhuongThuc("VNPAY").tenPhuongThuc("Cổng VNPAY").kichHoat(true).laTrucTuyen(true).build()));

        DonHang dh = donHangRepository.save(DonHang.builder()
                .maDonHang(maDh)
                .khachHang(khachHangA)
                .soDienThoaiNhan(khachHangA.getSoDienThoai())
                .tenNguoiNhan(khachHangA.getHoTen())
                .diaChiGiaoHang("456 Lê Lợi, Quận 1, TP HCM")
                .trangThaiDonHang(ttDangGiaoLoc)
                .phuongThucThanhToan(ptOnline)
                .tongTienHang(new BigDecimal("1000000"))
                .tongThanhToan(new BigDecimal("1000000"))
                .ngayTao(LocalDateTime.now())
                .loaiDonHang("ONLINE")
                .build());

        String comboToken = "COMBO-TEST-" + ts;
        // Món 1 thuộc Combo (Master: giữ giá combo 800k)
        ChiTietDonHang itemCombo1 = chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .maDonHangId(dh.getId())
                .maBienTheId(bienTheTest.getId())
                .tenSanPham("Gói Combo Công Sở: Áo Sơ Mi Cao Cấp")
                .maSku("CB-SOMI-" + ts)
                .donGia(new BigDecimal("800000"))
                .soLuong(1)
                .thanhTien(new BigDecimal("800000"))
                .nhomComboToken(comboToken)
                .trangThaiItem("BINH_THUONG")
                .build());

        // Món 2 thuộc Combo (Thành phần kèm theo: đơn giá 0đ)
        ChiTietDonHang itemCombo2 = chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .maDonHangId(dh.getId())
                .maBienTheId(bienTheTest.getId())
                .tenSanPham("Quần Tây Âu May Đo Kèm Combo")
                .maSku("CB-QUAN-" + ts)
                .donGia(BigDecimal.ZERO)
                .soLuong(1)
                .thanhTien(BigDecimal.ZERO)
                .nhomComboToken(comboToken)
                .trangThaiItem("BINH_THUONG")
                .build());

        // Món 3 Đơn lẻ (Không thuộc combo: 200k)
        ChiTietDonHang itemDonLe = chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .maDonHangId(dh.getId())
                .maBienTheId(bienTheTest.getId())
                .tenSanPham("Cà Vạt Lụa Đơn Lẻ")
                .maSku("CAVAT-SING-" + ts)
                .donGia(new BigDecimal("200000"))
                .soLuong(1)
                .thanhTien(new BigDecimal("200000"))
                .nhomComboToken(null)
                .maComboId(null)
                .trangThaiItem("BINH_THUONG")
                .build());

        int tonKhoTruocKhiBaoMat = bienTheSanPhamRepository.findById(bienTheTest.getId()).orElseThrow().getSoLuong();

        // Admin chỉ chọn 1 item (itemCombo2) báo mất, hệ thống phải tự động cascade toàn bộ gói Combo (cả itemCombo1)
        quanLyVanDonService.capNhatTrangThaiVanDon(dh.getId(), CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("THAT_LAC_MOT_PHAN")
                .ghiChu("Kiện hàng combo bị rách, thất lạc toàn bộ gói đóng gói chung")
                .chiTietBiMatIds(List.of(itemCombo2.getId())) // CHỈ TRUYỀN 1 ID CỦA COMBO
                .build(), null);

        // 1. Kiểm tra database chi tiết đơn hàng
        ChiTietDonHang item1SauBao = chiTietDonHangRepository.findById(itemCombo1.getId()).orElseThrow();
        ChiTietDonHang item2SauBao = chiTietDonHangRepository.findById(itemCombo2.getId()).orElseThrow();
        ChiTietDonHang donLeSauBao = chiTietDonHangRepository.findById(itemDonLe.getId()).orElseThrow();

        // Cả 2 item của Combo đều phải chuyển sang MAT_HANG
        assertEquals("MAT_HANG", item1SauBao.getTrangThaiItem());
        assertTrue(item1SauBao.getGhiChuItem().contains("Đóng gói chung theo gói Combo"));

        assertEquals("MAT_HANG", item2SauBao.getTrangThaiItem());
        assertTrue(item2SauBao.getGhiChuItem().contains("Đóng gói chung theo gói Combo"));

        // Sản phẩm đơn lẻ không bị ảnh hưởng, vẫn là BINH_THUONG
        assertEquals("BINH_THUONG", donLeSauBao.getTrangThaiItem());

        // 2. Tồn kho tuyệt đối không được cộng lại vì hàng đã thất lạc
        int tonKhoSauKhiBaoMat = bienTheSanPhamRepository.findById(bienTheTest.getId()).orElseThrow().getSoLuong();
        assertEquals(tonKhoTruocKhiBaoMat, tonKhoSauKhiBaoMat, "Hàng thất lạc tuyệt đối KHÔNG được cộng lại tồn kho!");

        // 3. Đơn hàng cập nhật trạng thái THAT_LAC_MOT_PHAN và ghi nhận đúng tổng tiền hoàn gói combo (800.000đ)
        DonHang dhSauBao = donHangRepository.findById(dh.getId()).orElseThrow();
        assertEquals("THAT_LAC_MOT_PHAN", dhSauBao.getTrangThaiDonHang().getMaTrangThai());
        assertEquals("CHO_HOAN_TIEN", dhSauBao.getTrangThaiHoanTien());
        assertTrue(dhSauBao.getGhiChuHoanTien().contains("800000"));

        // 4. Màn hình phía khách hàng: hiển thị banner Đóng gói chung theo combo bị mất và form hoàn tiền
        mockMvc.perform(get("/khach-hang/don-hang/" + maDh).cookie(cookieKhachA))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/don-hang/chi-tiet"))
                .andExpect(model().attribute("donHangDto", hasProperty("stepperIndex", is(3))))
                .andExpect(content().string(containsString("Sự cố vận chuyển: Đơn hàng bị thất lạc một phần sản phẩm")))
                .andExpect(content().string(containsString("Đóng gói chung theo gói Combo - Mất toàn bộ combo")))
                .andExpect(content().string(containsString("GIỮ NGUYÊN (ĐANG GIAO)")))
                .andExpect(content().string(containsString("Cà Vạt Lụa Đơn Lẻ")));
    }
}
