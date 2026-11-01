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
import org.springframework.test.web.servlet.MvcResult;
import vn.fpoly.simplain.dto.ThongKeDashboardResponse;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.NhanVienRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động giao diện Web MVC & Validate chuyên nghiệp cho chức năng Biểu đồ Dashboard tổng quan (US05).
 *
 * <p>Tiêu chí kiểm thử US05:
 * 1. Thống kê chính xác tổng số lượng người dùng (Khách hàng + Nhân viên), đơn hàng hôm nay, và doanh thu trong ngày.
 * 2. Tổng hợp đầy đủ dữ liệu cho 3 biểu đồ Chart.js (Doanh thu & Số đơn theo ngày, Tỷ trọng trạng thái đơn, Cơ cấu người dùng).
 * 3. Kiểm thử cập nhật thời gian thực: Khi tạo đơn hàng mới (Hoàn thành vs Đã hủy), số đơn hôm nay và doanh thu trong ngày thay đổi chính xác.
 * 4. Validate chuyên nghiệp:
 *    - Chặn lọc ngày thống kê trong tương lai (> hôm nay).
 *    - Chặn chu kỳ ngày biểu đồ nằm ngoài khoảng [1..90].
 *    - Validate Bean Validation trên form tạo đơn nhanh (tên người nhận, SĐT 10 số, địa chỉ, tổng tiền >= 1.000đ).
 *    - Validate nghiệp vụ: Tiền giảm giá > Tổng tiền hàng, Trùng mã đơn hàng, Trạng thái đơn hàng không tồn tại.
 *
 * <p>Chạy bằng lệnh: .\mvnw.cmd -Dtest=DashboardControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Biểu đồ Dashboard Tổng quan & Validate (US05)")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    private Cookie adminCookie;

    private static final String TEST_MA_DON_1 = "CM-TEST-US05-01";
    private static final String TEST_MA_DON_HUY = "CM-TEST-US05-HUY";
    private static final String TEST_MA_DON_UNICODE = "CM-TEST-US05-VI";

    @BeforeEach
    void setUp() throws Exception {
        donDepDuLieuTest();
        adminCookie = dangNhapLayCookieAdmin();
    }

    @AfterEach
    void tearDown() {
        donDepDuLieuTest();
    }

    private void donDepDuLieuTest() {
        try {
            jdbcTemplate.execute("DELETE FROM chi_tiet_san_pham_doi_tra WHERE ma_yeu_cau_doi_tra_id IN (SELECT id FROM yeu_cau_doi_tra WHERE ma_don_hang_id IN (SELECT id FROM don_hang WHERE ma_don_hang IN ('" + TEST_MA_DON_1 + "', '" + TEST_MA_DON_HUY + "', '" + TEST_MA_DON_UNICODE + "')))");
        } catch (Exception ignored) {}
        try {
            jdbcTemplate.execute("DELETE FROM yeu_cau_doi_tra WHERE ma_don_hang_id IN (SELECT id FROM don_hang WHERE ma_don_hang IN ('" + TEST_MA_DON_1 + "', '" + TEST_MA_DON_HUY + "', '" + TEST_MA_DON_UNICODE + "'))");
        } catch (Exception ignored) {}
        try {
            jdbcTemplate.execute("DELETE FROM chi_tiet_don_hang WHERE ma_don_hang_id IN (SELECT id FROM don_hang WHERE ma_don_hang IN ('" + TEST_MA_DON_1 + "', '" + TEST_MA_DON_HUY + "', '" + TEST_MA_DON_UNICODE + "'))");
        } catch (Exception ignored) {}
        try {
            jdbcTemplate.execute("DELETE FROM lich_su_don_hang WHERE ma_don_hang_id IN (SELECT id FROM don_hang WHERE ma_don_hang IN ('" + TEST_MA_DON_1 + "', '" + TEST_MA_DON_HUY + "', '" + TEST_MA_DON_UNICODE + "'))");
        } catch (Exception ignored) {}
        donHangRepository.findByMaDonHang(TEST_MA_DON_1).ifPresent(donHangRepository::delete);
        donHangRepository.findByMaDonHang(TEST_MA_DON_HUY).ifPresent(donHangRepository::delete);
        donHangRepository.findByMaDonHang(TEST_MA_DON_UNICODE).ifPresent(donHangRepository::delete);
    }

    private Cookie dangNhapLayCookieAdmin() throws Exception {
        MvcResult loginRes = mockMvc.perform(post("/admin/dang-nhap")
                        .param("tenDangNhap", "superadmin")
                        .param("matKhau", "hashed_pass_123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andReturn();

        Cookie cookie = loginRes.getResponse().getCookie(NhanVienController.COOKIE_ADMIN_TOKEN);
        assertNotNull(cookie, "Phải lấy được JWT cookie của Admin khi đăng nhập CMS");
        return cookie;
    }

    @Test
    @DisplayName("US05 - Tiêu chí 1: Hiển thị Dashboard với đầy đủ thống kê Tổng người dùng, Đơn hàng hôm nay, Doanh thu trong ngày và 3 Biểu đồ")
    void hienThiDashboard_ThongKeDayDuVaChinhXac() throws Exception {
        long expectedKhachHang = khachHangRepository.count();
        long expectedNhanVien = nhanVienRepository.count();
        long expectedTongNguoiDung = expectedKhachHang + expectedNhanVien;

        MvcResult result = mockMvc.perform(get("/admin/dashboard").cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("thongKe", "tongSoLuongNguoiDung", "soDonHangHomNay", "doanhThuTrongNgay"))
                .andExpect(model().attribute("tongSoLuongNguoiDung", expectedTongNguoiDung))
                .andExpect(content().string(containsString("Biểu đồ Tổng quan SIMPLAIN STORE")))
                .andExpect(content().string(containsString("bieuDoDoanhThuVaDonHang")))
                .andExpect(content().string(containsString("bieuDoTrangThaiDonHang")))
                .andExpect(content().string(containsString("bieuDoCoCauNguoiDung")))
                .andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        ThongKeDashboardResponse thongKe = (ThongKeDashboardResponse) modelMap.get("thongKe");
        assertNotNull(thongKe);

        // Kiểm tra dữ liệu người dùng khớp tuyệt đối với DB
        assertEquals(expectedTongNguoiDung, thongKe.getTongSoLuongNguoiDung());
        assertEquals(expectedKhachHang, thongKe.getTongKhachHang());
        assertEquals(expectedNhanVien, thongKe.getTongNhanVien());

        // Kiểm tra dữ liệu biểu đồ 7 ngày mặc định
        assertEquals(7, thongKe.getSoNgayBieuDo());
        assertEquals(7, thongKe.getNhanNgayBieuDo().size());
        assertEquals(7, thongKe.getDuLieuDoanhThuTheoNgay().size());
        assertEquals(7, thongKe.getDuLieuSoDonTheoNgay().size());

        // Kiểm tra biểu đồ trạng thái đơn hàng (5 trạng thái) & cơ cấu người dùng (4 nhóm)
        assertEquals(5, thongKe.getNhanTrangThaiDonHang().size());
        assertEquals(5, thongKe.getDuLieuSoDonTheoTrangThai().size());
        assertEquals(4, thongKe.getNhanCoCauNguoiDung().size());
        assertEquals(4, thongKe.getDuLieuCoCauNguoiDung().size());
    }

    @Test
    @DisplayName("US05 - Cập nhật Realtime: Tạo đơn hàng HOAN_THANH làm tăng Số đơn hôm nay (+1) và Doanh thu trong ngày chính xác")
    void taoDonHangHomNay_CapNhatSoDonVaDoanhThuRealtime() throws Exception {
        // 1. Lấy số liệu trước khi tạo đơn
        MvcResult beforeRes = mockMvc.perform(get("/admin/dashboard").cookie(adminCookie))
                .andExpect(status().isOk())
                .andReturn();
        ThongKeDashboardResponse truoc = (ThongKeDashboardResponse) beforeRes.getModelAndView().getModel().get("thongKe");
        long soDonTruoc = truoc.getSoDonHangHomNay();
        BigDecimal doanhThuTruoc = truoc.getDoanhThuTrongNgay();

        // 2. Tạo đơn hàng mới hôm nay: Tiền hàng 500.000đ - Giảm giá 50.000đ + Ship 25.000đ = 475.000đ
        mockMvc.perform(post("/admin/dashboard/tao-don-nhanh")
                        .cookie(adminCookie)
                        .param("maDonHang", TEST_MA_DON_1)
                        .param("tenNguoiNhan", "Trần Quốc Bảo")
                        .param("soDienThoaiNhan", "0912345678")
                        .param("diaChiGiaoHang", "120 Lê Lợi, Quận 1, TP. Hồ Chí Minh")
                        .param("tongTienHang", "500000")
                        .param("tienGiamGia", "50000")
                        .param("phiVanChuyen", "25000")
                        .param("maTrangThai", "HOAN_THANH")
                        .param("ghiChu", "Đơn test tự động US05"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("thongBaoThanhCongDashboard"));

        // 3. Kiểm tra lại Dashboard sau khi tạo đơn
        MvcResult afterRes = mockMvc.perform(get("/admin/dashboard").cookie(adminCookie))
                .andExpect(status().isOk())
                .andReturn();
        ThongKeDashboardResponse sau = (ThongKeDashboardResponse) afterRes.getModelAndView().getModel().get("thongKe");

        assertEquals(soDonTruoc + 1, sau.getSoDonHangHomNay(), "Số đơn hàng hôm nay phải tăng thêm 1");
        assertEquals(0, doanhThuTruoc.add(new BigDecimal("475000")).compareTo(sau.getDoanhThuTrongNgay()),
                "Doanh thu trong ngày phải tăng đúng 475.000đ");
    }

    @Test
    @DisplayName("US05 - Nghiệp vụ Doanh thu: Đơn hàng ĐÃ HỦY (DA_HUY) tăng tổng số đơn nhưng KHÔNG cộng vào Doanh thu trong ngày")
    void taoDonHangDaHuy_KhongCongVaoDoanhThuTrongNgay() throws Exception {
        MvcResult beforeRes = mockMvc.perform(get("/admin/dashboard").cookie(adminCookie))
                .andExpect(status().isOk())
                .andReturn();
        ThongKeDashboardResponse truoc = (ThongKeDashboardResponse) beforeRes.getModelAndView().getModel().get("thongKe");
        long soDonTruoc = truoc.getSoDonHangHomNay();
        long soDonHuyTruoc = truoc.getSoDonHuyHomNay();
        BigDecimal doanhThuTruoc = truoc.getDoanhThuTrongNgay();

        // Tạo đơn hàng trạng thái DA_HUY trị giá 800.000đ
        mockMvc.perform(post("/admin/dashboard/tao-don-nhanh")
                        .cookie(adminCookie)
                        .param("maDonHang", TEST_MA_DON_HUY)
                        .param("tenNguoiNhan", "Khách Hủy Đơn")
                        .param("soDienThoaiNhan", "0987654321")
                        .param("diaChiGiaoHang", "45 Cầu Giấy, Hà Nội")
                        .param("tongTienHang", "800000")
                        .param("tienGiamGia", "0")
                        .param("phiVanChuyen", "0")
                        .param("maTrangThai", "DA_HUY"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));

        MvcResult afterRes = mockMvc.perform(get("/admin/dashboard").cookie(adminCookie))
                .andExpect(status().isOk())
                .andReturn();
        ThongKeDashboardResponse sau = (ThongKeDashboardResponse) afterRes.getModelAndView().getModel().get("thongKe");

        assertEquals(soDonTruoc + 1, sau.getSoDonHangHomNay(), "Tổng số đơn phát sinh hôm nay vẫn tăng 1");
        assertEquals(soDonHuyTruoc + 1, sau.getSoDonHuyHomNay(), "Số đơn hủy hôm nay tăng 1");
        assertEquals(0, doanhThuTruoc.compareTo(sau.getDoanhThuTrongNgay()),
                "Đơn đã hủy tuyệt đối không được tính vào doanh thu trong ngày");
    }

    @Test
    @DisplayName("US05 - Validate Bộ lọc: Chặn lọc ngày thống kê trong tương lai và chặn chu kỳ biểu đồ ngoài khoảng [1..90]")
    void validateBoLocDashboard_NgayTuongLaiVaChuKyKhongHopLe() throws Exception {
        // 1. Ngày thống kê ở tương lai (hôm nay + 5 ngày)
        String ngayTuongLai = LocalDate.now().plusDays(5).toString();
        mockMvc.perform(get("/admin/dashboard")
                        .cookie(adminCookie)
                        .param("ngayThongKe", ngayTuongLai))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("thongBaoLoiBoLoc", containsString("Ngày thống kê không được lớn hơn ngày hiện tại")));

        // 2. Chu kỳ biểu đồ > 90 ngày
        mockMvc.perform(get("/admin/dashboard")
                        .cookie(adminCookie)
                        .param("soNgayBieuDo", "120"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("thongBaoLoiBoLoc", containsString("Khoảng thời gian biểu đồ phải nằm trong khoảng từ 1 đến 90 ngày")));

        // 3. Chu kỳ biểu đồ <= 0
        mockMvc.perform(get("/admin/dashboard")
                        .cookie(adminCookie)
                        .param("soNgayBieuDo", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("thongBaoLoiBoLoc", containsString("Khoảng thời gian biểu đồ phải nằm trong khoảng từ 1 đến 90 ngày")));
    }

    @Test
    @DisplayName("US05 - Validate Form (Bean Validation): Báo lỗi khi bỏ trống người nhận, SĐT sai định dạng, tiền hàng < 1.000đ")
    void validateFormTaoDonNhanh_DuLieuKhongHopLe() throws Exception {
        mockMvc.perform(post("/admin/dashboard/tao-don-nhanh")
                        .cookie(adminCookie)
                        .param("tenNguoiNhan", "") // Bỏ trống
                        .param("soDienThoaiNhan", "12345") // Sai SĐT 10 số VN
                        .param("diaChiGiaoHang", "ABC") // Quá ngắn (< 5 ký tự)
                        .param("tongTienHang", "500") // Nhỏ hơn 1.000đ
                        .param("maTrangThai", "HOAN_THANH"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeHasFieldErrors(
                        "taoDonHangNhanhRequest",
                        "tenNguoiNhan",
                        "soDienThoaiNhan",
                        "diaChiGiaoHang",
                        "tongTienHang"
                ))
                .andExpect(model().attribute("moModalTaoDon", true))
                .andExpect(model().attributeExists("thongBaoLoiDashboard"));
    }

    @Test
    @DisplayName("US05 - Validate Nghiệp vụ: Chặn tiền giảm giá lớn hơn tổng tiền hàng và chặn trùng mã đơn hàng")
    void validateNghiepVuTaoDonNhanh_GiamGiaVuotTienHangVaTrungMaDon() throws Exception {
        // 1. Tiền giảm giá (600.000đ) > Tổng tiền hàng (400.000đ)
        mockMvc.perform(post("/admin/dashboard/tao-don-nhanh")
                        .cookie(adminCookie)
                        .param("maDonHang", TEST_MA_DON_1)
                        .param("tenNguoiNhan", "Nguyễn Văn Kiểm Thử")
                        .param("soDienThoaiNhan", "0901234567")
                        .param("diaChiGiaoHang", "100 Nguyễn Thị Minh Khai, Quận 3")
                        .param("tongTienHang", "400000")
                        .param("tienGiamGia", "600000")
                        .param("phiVanChuyen", "20000")
                        .param("maTrangThai", "HOAN_THANH"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("thongBaoLoiDashboard", containsString("Tiền giảm giá không được lớn hơn tổng tiền hàng")));

        // 2. Tạo 1 đơn hợp lệ trước
        mockMvc.perform(post("/admin/dashboard/tao-don-nhanh")
                        .cookie(adminCookie)
                        .param("maDonHang", TEST_MA_DON_1)
                        .param("tenNguoiNhan", "Nguyễn Văn Kiểm Thử")
                        .param("soDienThoaiNhan", "0901234567")
                        .param("diaChiGiaoHang", "100 Nguyễn Thị Minh Khai, Quận 3")
                        .param("tongTienHang", "400000")
                        .param("tienGiamGia", "50000")
                        .param("phiVanChuyen", "20000")
                        .param("maTrangThai", "HOAN_THANH"))
                .andExpect(status().is3xxRedirection());

        // 3. Tạo đơn thứ 2 trùng mã TEST_MA_DON_1 -> Phải bị chặn
        mockMvc.perform(post("/admin/dashboard/tao-don-nhanh")
                        .cookie(adminCookie)
                        .param("maDonHang", TEST_MA_DON_1)
                        .param("tenNguoiNhan", "Người Nhận Thứ Hai")
                        .param("soDienThoaiNhan", "0909998887")
                        .param("diaChiGiaoHang", "200 Điện Biên Phủ, Bình Thạnh")
                        .param("tongTienHang", "300000")
                        .param("tienGiamGia", "0")
                        .param("phiVanChuyen", "0")
                        .param("maTrangThai", "CHO_XAC_NHAN"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("thongBaoLoiDashboard", containsString("đã tồn tại trên hệ thống")));
    }

    @Test
    @DisplayName("US05 - Kiểm tra tiếng Việt có dấu phức tạp trên đơn hàng Dashboard không bị lỗi '?' trong SQL Server")
    void kiemTraTiengVietUnicodeTrenDonHangDashboard() throws Exception {
        String tenCoDau = "Đặng Nguyễn Quỳnh Như";
        String diaChiCoDau = "Số 88 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh";

        mockMvc.perform(post("/admin/dashboard/tao-don-nhanh")
                        .cookie(adminCookie)
                        .param("maDonHang", TEST_MA_DON_UNICODE)
                        .param("tenNguoiNhan", tenCoDau)
                        .param("soDienThoaiNhan", "0938112233")
                        .param("diaChiGiaoHang", diaChiCoDau)
                        .param("tongTienHang", "650000")
                        .param("tienGiamGia", "0")
                        .param("phiVanChuyen", "0")
                        .param("maTrangThai", "DANG_GIAO"))
                .andExpect(status().is3xxRedirection());

        DonHang saved = donHangRepository.findByMaDonHang(TEST_MA_DON_UNICODE).orElseThrow();
        assertEquals(tenCoDau, saved.getTenNguoiNhan());
        assertEquals(diaChiCoDau, saved.getDiaChiGiaoHang());
        assertFalse(saved.getTenNguoiNhan().contains("?"));
        assertFalse(saved.getDiaChiGiaoHang().contains("?"));
    }
}
