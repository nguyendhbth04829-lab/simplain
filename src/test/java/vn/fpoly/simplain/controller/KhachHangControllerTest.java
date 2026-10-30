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
import vn.fpoly.simplain.entity.DiaChi;
import vn.fpoly.simplain.repository.DiaChiRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động giao diện Web MVC cho KhachHangController (US01).
 * Chạy bằng lệnh: .\mvnw.cmd -Dtest=KhachHangControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Xác thực Khách hàng (US01)")
class KhachHangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private DiaChiRepository diaChiRepository;

    @BeforeEach
    void setUp() {
        donDep();
    }

    @AfterEach
    void tearDown() {
        donDep();
    }

    private void donDep() {
        xoaTheoEmail("test.khachhang@gmail.com");
        xoaTheoEmail("hoangminh.test@gmail.com");
        xoaTheoEmail("phamd.test@gmail.com");
        xoaTheoEmail("vuve.test@gmail.com");
        xoaTheoEmail("us12.user1@gmail.com");
        xoaTheoEmail("us12.user2@gmail.com");
        xoaTheoEmail("us12.capnhat@gmail.com");
        xoaTheoEmail("us13.user@gmail.com");
        xoaTheoSdt("0987111222");
        xoaTheoSdt("0988777666");
        xoaTheoSdt("0977888999");
        xoaTheoSdt("0966555444");
        xoaTheoSdt("0912111222");
        xoaTheoSdt("0912333444");
        xoaTheoSdt("0912555666");
        xoaTheoSdt("0913999888");
    }

    private void xoaTheoEmail(String email) {
        khachHangRepository.findByEmail(email).ifPresent(kh -> {
            diaChiRepository.deleteAll(diaChiRepository.findByMaKhachHangIdOrderByLaMacDinhDescIdDesc(kh.getId()));
            khachHangRepository.delete(kh);
        });
    }

    private void xoaTheoSdt(String sdt) {
        khachHangRepository.findBySoDienThoai(sdt).ifPresent(kh -> {
            diaChiRepository.deleteAll(diaChiRepository.findByMaKhachHangIdOrderByLaMacDinhDescIdDesc(kh.getId()));
            khachHangRepository.delete(kh);
        });
    }

    @Test
    @DisplayName("Hiển thị màn hình đăng nhập và đăng ký thành công")
    void hienThiManHinh_ThanhCong() throws Exception {
        mockMvc.perform(get("/khach-hang/dang-nhap"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/dang-nhap"));

        mockMvc.perform(get("/khach-hang/dang-ky"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/dang-ky"));
    }

    @Test
    @DisplayName("Đăng ký tài khoản trên giao diện Web thành công - Chuyển hướng và cấp Cookie JWT")
    void dangKy_ThanhCong() throws Exception {
        mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Nguyễn Văn An")
                        .param("email", "test.khachhang@gmail.com")
                        .param("soDienThoai", "0987111222")
                        .param("matKhau", "MatKhau123@")
                        .param("xacNhanMatKhau", "MatKhau123@")
                        .param("gioiTinh", "Nam")
                        .param("ngaySinh", "2000-01-15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/thong-tin"))
                .andExpect(cookie().exists(KhachHangController.COOKIE_TOKEN));
    }

    @Test
    @DisplayName("Validate Form đăng ký khi để trống họ tên hoặc sai số điện thoại")
    void dangKy_ValidateForm_Loi() throws Exception {
        mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "")
                        .param("email", "test.blank@gmail.com")
                        .param("soDienThoai", "12345")
                        .param("matKhau", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/dang-ky"))
                .andExpect(model().attributeHasFieldErrors("dangKyRequest", "hoTen", "soDienThoai", "matKhau"));
    }

    @Test
    @DisplayName("Đăng nhập thành công trên giao diện Web bằng Email và SĐT")
    void dangNhap_ThanhCong() throws Exception {
        mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Hoàng Minh")
                        .param("email", "hoangminh.test@gmail.com")
                        .param("soDienThoai", "0988777666")
                        .param("matKhau", "SecretPass123")
                        .param("xacNhanMatKhau", "SecretPass123"))
                .andExpect(status().is3xxRedirection());

        // Đăng nhập bằng Email
        mockMvc.perform(post("/khach-hang/dang-nhap")
                        .param("taiKhoan", "hoangminh.test@gmail.com")
                        .param("matKhau", "SecretPass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/thong-tin"))
                .andExpect(cookie().exists(KhachHangController.COOKIE_TOKEN));

        // Đăng nhập bằng SĐT
        mockMvc.perform(post("/khach-hang/dang-nhap")
                        .param("taiKhoan", "0988777666")
                        .param("matKhau", "SecretPass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/thong-tin"))
                .andExpect(cookie().exists(KhachHangController.COOKIE_TOKEN));
    }

    @Test
    @DisplayName("Đăng nhập thất bại khi sai mật khẩu - Hiển thị thông báo lỗi trên giao diện")
    void dangNhap_SaiMatKhau_Loi() throws Exception {
        mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Phạm D")
                        .param("email", "phamd.test@gmail.com")
                        .param("soDienThoai", "0977888999")
                        .param("matKhau", "DungMatKhau123")
                        .param("xacNhanMatKhau", "DungMatKhau123"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/khach-hang/dang-nhap")
                        .param("taiKhoan", "phamd.test@gmail.com")
                        .param("matKhau", "SaiMatKhau123"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/dang-nhap"))
                .andExpect(model().attribute("loiDangNhap", containsString("không chính xác")));
    }

    @Test
    @DisplayName("Xác thực Cookie JWT Token và xem trang thông tin cá nhân")
    void xemThongTin_CoCookieToken_ThanhCong() throws Exception {
        MvcResult res = mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Vũ Văn E")
                        .param("email", "vuve.test@gmail.com")
                        .param("soDienThoai", "0966555444")
                        .param("matKhau", "Pass123456")
                        .param("xacNhanMatKhau", "Pass123456"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie jwtCookie = res.getResponse().getCookie(KhachHangController.COOKIE_TOKEN);
        assertNotNull(jwtCookie);

        mockMvc.perform(get("/khach-hang/thong-tin").cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/thong-tin"))
                .andExpect(model().attributeExists("thongTin"))
                .andExpect(model().attribute("jwtToken", notNullValue()));
    }

    @Test
    @DisplayName("Truy cập trang thông tin khi chưa đăng nhập sẽ chuyển hướng về trang đăng nhập")
    void xemThongTin_ChuaDangNhap_ChuyenHuong() throws Exception {
        mockMvc.perform(get("/khach-hang/thong-tin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/dang-nhap"));
    }

    @Test
    @DisplayName("US12 - Tiêu chí 1: Cập nhật Tên, SĐT, Email, Giới tính, Ngày sinh thành công")
    void us12_CapNhatThongTin_ThanhCong() throws Exception {
        // 1. Đăng ký tài khoản ban đầu
        MvcResult res = mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Khách Hàng Ban Đầu")
                        .param("email", "us12.user1@gmail.com")
                        .param("soDienThoai", "0912111222")
                        .param("matKhau", "PassBanDau123@")
                        .param("xacNhanMatKhau", "PassBanDau123@"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie jwtCookie = res.getResponse().getCookie(KhachHangController.COOKIE_TOKEN);
        assertNotNull(jwtCookie);

        // 2. Cập nhật thông tin cá nhân mới
        mockMvc.perform(post("/khach-hang/cap-nhat-thong-tin")
                        .cookie(jwtCookie)
                        .param("hoTen", "Khách Hàng Đã Cập Nhật")
                        .param("email", "us12.capnhat@gmail.com")
                        .param("soDienThoai", "0912333444")
                        .param("gioiTinh", "Nam")
                        .param("ngaySinh", "1998-05-20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/thong-tin"))
                .andExpect(flash().attribute("thongBaoThanhCong", containsString("Cập nhật thông tin cá nhân thành công")))
                .andExpect(cookie().exists(KhachHangController.COOKIE_TOKEN));
    }

    @Test
    @DisplayName("US12 - Tiêu chí 1: Báo lỗi khi cập nhật email trùng với tài khoản khác")
    void us12_CapNhatThongTin_TrungEmail_Loi() throws Exception {
        // Tài khoản 1
        mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Người Dùng 1")
                        .param("email", "us12.user1@gmail.com")
                        .param("soDienThoai", "0912111222")
                        .param("matKhau", "Pass123@")
                        .param("xacNhanMatKhau", "Pass123@"))
                .andExpect(status().is3xxRedirection());

        // Tài khoản 2
        MvcResult res2 = mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Người Dùng 2")
                        .param("email", "us12.user2@gmail.com")
                        .param("soDienThoai", "0912555666")
                        .param("matKhau", "Pass123@")
                        .param("xacNhanMatKhau", "Pass123@"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie jwtCookie2 = res2.getResponse().getCookie(KhachHangController.COOKIE_TOKEN);

        // Tài khoản 2 cố ý sửa email thành email của Tài khoản 1
        mockMvc.perform(post("/khach-hang/cap-nhat-thong-tin")
                        .cookie(jwtCookie2)
                        .param("hoTen", "Người Dùng 2")
                        .param("email", "us12.user1@gmail.com")
                        .param("soDienThoai", "0912555666"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/thong-tin"))
                .andExpect(model().attribute("loiCapNhat", containsString("đã được sử dụng")));
    }

    @Test
    @DisplayName("US12 - Tiêu chí 2: Đổi mật khẩu thành công và đăng nhập được bằng mật khẩu mới")
    void us12_DoiMatKhau_ThanhCong() throws Exception {
        // 1. Đăng ký tài khoản
        MvcResult res = mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Đổi Mật Khẩu User")
                        .param("email", "us12.user1@gmail.com")
                        .param("soDienThoai", "0912111222")
                        .param("matKhau", "MatKhauCu123@")
                        .param("xacNhanMatKhau", "MatKhauCu123@"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie jwtCookie = res.getResponse().getCookie(KhachHangController.COOKIE_TOKEN);

        // 2. Thực hiện đổi mật khẩu
        mockMvc.perform(post("/khach-hang/doi-mat-khau")
                        .cookie(jwtCookie)
                        .param("matKhauHienTai", "MatKhauCu123@")
                        .param("matKhauMoi", "MatKhauMoi456@")
                        .param("xacNhanMatKhauMoi", "MatKhauMoi456@"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/thong-tin"))
                .andExpect(flash().attribute("thongBaoThanhCong", containsString("Đổi mật khẩu tài khoản thành công")))
                .andExpect(cookie().exists(KhachHangController.COOKIE_TOKEN));

        // 3. Đăng nhập lại bằng mật khẩu mới thành công
        mockMvc.perform(post("/khach-hang/dang-nhap")
                        .param("taiKhoan", "us12.user1@gmail.com")
                        .param("matKhau", "MatKhauMoi456@"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/thong-tin"));
    }

    @Test
    @DisplayName("US12 - Tiêu chí 2: Báo lỗi khi nhập sai mật khẩu hiện tại hoặc xác nhận mật khẩu không khớp")
    void us12_DoiMatKhau_KiemTraLoi() throws Exception {
        MvcResult res = mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Test Pass Validation")
                        .param("email", "us12.user1@gmail.com")
                        .param("soDienThoai", "0912111222")
                        .param("matKhau", "MatKhauCu123@")
                        .param("xacNhanMatKhau", "MatKhauCu123@"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie jwtCookie = res.getResponse().getCookie(KhachHangController.COOKIE_TOKEN);

        // Trường hợp 1: Nhập sai mật khẩu hiện tại
        mockMvc.perform(post("/khach-hang/doi-mat-khau")
                        .cookie(jwtCookie)
                        .param("matKhauHienTai", "SaiMatKhau999@")
                        .param("matKhauMoi", "MatKhauMoi456@")
                        .param("xacNhanMatKhauMoi", "MatKhauMoi456@"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/thong-tin"))
                .andExpect(model().attribute("loiDoiMatKhau", containsString("không chính xác")));

        // Trường hợp 2: Xác nhận mật khẩu mới không khớp
        mockMvc.perform(post("/khach-hang/doi-mat-khau")
                        .cookie(jwtCookie)
                        .param("matKhauHienTai", "MatKhauCu123@")
                        .param("matKhauMoi", "MatKhauMoi456@")
                        .param("xacNhanMatKhauMoi", "MatKhauKhac789@"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/thong-tin"))
                .andExpect(model().attributeHasFieldErrors("doiMatKhauRequest", "xacNhanMatKhauMoi"));
    }

    // ==========================================
    // US13: QUẢN LÝ SỔ ĐỊA CHỈ GIAO HÀNG
    // ==========================================

    @Test
    @DisplayName("US13: Chưa đăng nhập truy cập /khach-hang/so-dia-chi sẽ chuyển hướng sang đăng nhập")
    void us13_SoDiaChi_ChuaDangNhap_ChuyenHuong() throws Exception {
        mockMvc.perform(get("/khach-hang/so-dia-chi"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/dang-nhap"))
                .andExpect(flash().attribute("loiDangNhap", containsString("Vui lòng đăng nhập")));
    }

    @Test
    @DisplayName("US13: Đã đăng nhập truy cập /khach-hang/so-dia-chi hiển thị đúng view và tab sổ địa chỉ")
    void us13_SoDiaChi_DaDangNhap_ThanhCong() throws Exception {
        MvcResult res = mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Nguyễn Sổ Địa Chỉ")
                        .param("email", "us13.user@gmail.com")
                        .param("soDienThoai", "0913999888")
                        .param("matKhau", "Pass123@")
                        .param("xacNhanMatKhau", "Pass123@"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie jwtCookie = res.getResponse().getCookie(KhachHangController.COOKIE_TOKEN);

        mockMvc.perform(get("/khach-hang/so-dia-chi")
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/thong-tin"))
                .andExpect(model().attribute("tabActive", "so-dia-chi"))
                .andExpect(model().attributeExists("danhSachDiaChi"));
    }

    @Test
    @DisplayName("US13 - Tiêu chí 1, 2, 3: Thêm địa chỉ mới, tự động đặt làm mặc định khi là địa chỉ đầu tiên")
    void us13_ThemDiaChi_TuDongMacDinh_ThanhCong() throws Exception {
        MvcResult res = mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Khách Hàng Địa Chỉ 1")
                        .param("email", "us13.user@gmail.com")
                        .param("soDienThoai", "0913999888")
                        .param("matKhau", "Pass123@")
                        .param("xacNhanMatKhau", "Pass123@"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie jwtCookie = res.getResponse().getCookie(KhachHangController.COOKIE_TOKEN);

        // Thêm địa chỉ thứ 1
        mockMvc.perform(post("/khach-hang/dia-chi/them")
                        .cookie(jwtCookie)
                        .param("tenNguoiNhan", "Nguyễn Hoàng Minh")
                        .param("soDienThoai", "0988777666")
                        .param("tinhThanh", "Thành phố Hà Nội")
                        .param("quanHuyen", "Quận Cầu Giấy")
                        .param("phuongXa", "Phường Dịch Vọng Hậu")
                        .param("diaChiChiTiet", "Số 123 Duy Tân")
                        .param("laMacDinh", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/thong-tin?tab=so-dia-chi"))
                .andExpect(flash().attribute("thongBaoThanhCong", containsString("Thêm mới địa chỉ giao hàng thành công")));

        var khOpt = khachHangRepository.findByEmail("us13.user@gmail.com");
        assertTrue(khOpt.isPresent());
        List<DiaChi> danhSach = diaChiRepository.findByMaKhachHangIdOrderByLaMacDinhDescIdDesc(khOpt.get().getId());
        assertEquals(1, danhSach.size());
        DiaChi dc1 = danhSach.get(0);
        assertEquals("Nguyễn Hoàng Minh", dc1.getTenNguoiNhan());
        assertEquals("Thành phố Hà Nội", dc1.getTinhThanh());
        assertEquals("Quận Cầu Giấy", dc1.getQuanHuyen());
        assertEquals("Phường Dịch Vọng Hậu", dc1.getPhuongXa());
        // Do là địa chỉ duy nhất, hệ thống tự động gán là mặc định = true
        assertTrue(dc1.getLaMacDinh());
    }

    @Test
    @DisplayName("US13 - Tiêu chí 1: Cập nhật địa chỉ và lấy thông tin qua API JSON")
    void us13_SuaDiaChi_VaLayApi_ThanhCong() throws Exception {
        MvcResult res = mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Khách Hàng Địa Chỉ 2")
                        .param("email", "us13.user@gmail.com")
                        .param("soDienThoai", "0913999888")
                        .param("matKhau", "Pass123@")
                        .param("xacNhanMatKhau", "Pass123@"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie jwtCookie = res.getResponse().getCookie(KhachHangController.COOKIE_TOKEN);

        // Thêm địa chỉ
        mockMvc.perform(post("/khach-hang/dia-chi/them")
                        .cookie(jwtCookie)
                        .param("tenNguoiNhan", "Người Nhận Cũ")
                        .param("soDienThoai", "0988777666")
                        .param("tinhThanh", "Thành phố Hà Nội")
                        .param("quanHuyen", "Quận Cầu Giấy")
                        .param("phuongXa", "Phường Dịch Vọng Hậu")
                        .param("diaChiChiTiet", "Số 1 Cũ")
                        .param("laMacDinh", "true"))
                .andExpect(status().is3xxRedirection());

        var kh = khachHangRepository.findByEmail("us13.user@gmail.com").orElseThrow();
        DiaChi dcCu = diaChiRepository.findByMaKhachHangIdOrderByLaMacDinhDescIdDesc(kh.getId()).get(0);

        // Kiểm tra API lấy chi tiết địa chỉ dạng JSON
        mockMvc.perform(get("/khach-hang/dia-chi/api/" + dcCu.getId())
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dcCu.getId().intValue())))
                .andExpect(jsonPath("$.tenNguoiNhan", is("Người Nhận Cũ")))
                .andExpect(jsonPath("$.tinhThanh", is("Thành phố Hà Nội")));

        // Sửa địa chỉ
        mockMvc.perform(post("/khach-hang/dia-chi/sua")
                        .cookie(jwtCookie)
                        .param("id", dcCu.getId().toString())
                        .param("tenNguoiNhan", "Người Nhận Mới")
                        .param("soDienThoai", "0977888999")
                        .param("tinhThanh", "Thành phố Hồ Chí Minh")
                        .param("quanHuyen", "Quận 1")
                        .param("phuongXa", "Phường Bến Nghé")
                        .param("diaChiChiTiet", "Số 99 Lê Duẩn")
                        .param("laMacDinh", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/thong-tin?tab=so-dia-chi"))
                .andExpect(flash().attribute("thongBaoThanhCong", containsString("Cập nhật địa chỉ giao hàng thành công")));

        DiaChi dcMoi = diaChiRepository.findById(dcCu.getId()).orElseThrow();
        assertEquals("Người Nhận Mới", dcMoi.getTenNguoiNhan());
        assertEquals("0977888999", dcMoi.getSoDienThoai());
        assertEquals("Thành phố Hồ Chí Minh", dcMoi.getTinhThanh());
        assertEquals("Quận 1", dcMoi.getQuanHuyen());
        assertEquals("Phường Bến Nghé", dcMoi.getPhuongXa());
    }

    @Test
    @DisplayName("US13 - Tiêu chí 3: Đặt làm mặc định đảm bảo duy nhất 1 địa chỉ mặc định")
    void us13_DatMacDinh_DuyNhatMotMacDinh_ThanhCong() throws Exception {
        MvcResult res = mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Khách Hàng Mặc Định")
                        .param("email", "us13.user@gmail.com")
                        .param("soDienThoai", "0913999888")
                        .param("matKhau", "Pass123@")
                        .param("xacNhanMatKhau", "Pass123@"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie jwtCookie = res.getResponse().getCookie(KhachHangController.COOKIE_TOKEN);

        // Thêm địa chỉ 1 (mặc định)
        mockMvc.perform(post("/khach-hang/dia-chi/them")
                        .cookie(jwtCookie)
                        .param("tenNguoiNhan", "Địa Chỉ Một")
                        .param("soDienThoai", "0988777666")
                        .param("tinhThanh", "Thành phố Hà Nội")
                        .param("quanHuyen", "Quận Cầu Giấy")
                        .param("phuongXa", "Phường Dịch Vọng Hậu")
                        .param("diaChiChiTiet", "Nhà riêng 1")
                        .param("laMacDinh", "true"))
                .andExpect(status().is3xxRedirection());

        // Thêm địa chỉ 2 (không mặc định)
        mockMvc.perform(post("/khach-hang/dia-chi/them")
                        .cookie(jwtCookie)
                        .param("tenNguoiNhan", "Địa Chỉ Hai")
                        .param("soDienThoai", "0977888999")
                        .param("tinhThanh", "Thành phố Hồ Chí Minh")
                        .param("quanHuyen", "Quận 1")
                        .param("phuongXa", "Phường Bến Nghé")
                        .param("diaChiChiTiet", "Văn phòng 2")
                        .param("laMacDinh", "false"))
                .andExpect(status().is3xxRedirection());

        var kh = khachHangRepository.findByEmail("us13.user@gmail.com").orElseThrow();
        List<DiaChi> ds = diaChiRepository.findByMaKhachHangIdOrderByLaMacDinhDescIdDesc(kh.getId());
        assertEquals(2, ds.size());
        DiaChi dc1 = ds.stream().filter(d -> d.getTenNguoiNhan().equals("Địa Chỉ Một")).findFirst().orElseThrow();
        DiaChi dc2 = ds.stream().filter(d -> d.getTenNguoiNhan().equals("Địa Chỉ Hai")).findFirst().orElseThrow();
        assertTrue(dc1.getLaMacDinh());
        assertFalse(dc2.getLaMacDinh());

        // Gọi action Đặt làm mặc định cho Địa Chỉ Hai
        mockMvc.perform(get("/khach-hang/dia-chi/dat-mac-dinh/" + dc2.getId())
                        .cookie(jwtCookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/thong-tin?tab=so-dia-chi"))
                .andExpect(flash().attribute("thongBaoThanhCong", containsString("Đã thiết lập làm địa chỉ giao hàng mặc định")));

        // Kiểm tra lại trong DB: dc2 là mặc định, dc1 không còn là mặc định
        DiaChi dc1Sau = diaChiRepository.findById(dc1.getId()).orElseThrow();
        DiaChi dc2Sau = diaChiRepository.findById(dc2.getId()).orElseThrow();
        assertFalse(dc1Sau.getLaMacDinh());
        assertTrue(dc2Sau.getLaMacDinh());
    }

    @Test
    @DisplayName("US13 - Tiêu chí 1 & 3: Xóa địa chỉ mặc định tự động chỉ định địa chỉ kế tiếp làm mặc định")
    void us13_XoaDiaChi_TuDongChuyenMacDinh() throws Exception {
        MvcResult res = mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Khách Hàng Xóa")
                        .param("email", "us13.user@gmail.com")
                        .param("soDienThoai", "0913999888")
                        .param("matKhau", "Pass123@")
                        .param("xacNhanMatKhau", "Pass123@"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie jwtCookie = res.getResponse().getCookie(KhachHangController.COOKIE_TOKEN);

        // Thêm địa chỉ 1
        mockMvc.perform(post("/khach-hang/dia-chi/them")
                        .cookie(jwtCookie)
                        .param("tenNguoiNhan", "Địa Chỉ Sẽ Bị Xóa")
                        .param("soDienThoai", "0988777666")
                        .param("tinhThanh", "Thành phố Hà Nội")
                        .param("quanHuyen", "Quận Cầu Giấy")
                        .param("phuongXa", "Phường Dịch Vọng Hậu")
                        .param("diaChiChiTiet", "Nhà số 1")
                        .param("laMacDinh", "true"))
                .andExpect(status().is3xxRedirection());

        // Thêm địa chỉ 2
        mockMvc.perform(post("/khach-hang/dia-chi/them")
                        .cookie(jwtCookie)
                        .param("tenNguoiNhan", "Địa Chỉ Sẽ Thành Mặc Định")
                        .param("soDienThoai", "0977888999")
                        .param("tinhThanh", "Thành phố Đà Nẵng")
                        .param("quanHuyen", "Quận Hải Châu")
                        .param("phuongXa", "Phường Hải Châu I")
                        .param("diaChiChiTiet", "Nhà số 2")
                        .param("laMacDinh", "false"))
                .andExpect(status().is3xxRedirection());

        var kh = khachHangRepository.findByEmail("us13.user@gmail.com").orElseThrow();
        DiaChi dc1 = diaChiRepository.findByMaKhachHangIdOrderByLaMacDinhDescIdDesc(kh.getId())
                .stream().filter(d -> d.getTenNguoiNhan().equals("Địa Chỉ Sẽ Bị Xóa")).findFirst().orElseThrow();
        DiaChi dc2 = diaChiRepository.findByMaKhachHangIdOrderByLaMacDinhDescIdDesc(kh.getId())
                .stream().filter(d -> d.getTenNguoiNhan().equals("Địa Chỉ Sẽ Thành Mặc Định")).findFirst().orElseThrow();

        // Xóa địa chỉ 1 (đang là mặc định)
        mockMvc.perform(get("/khach-hang/dia-chi/xoa/" + dc1.getId())
                        .cookie(jwtCookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/thong-tin?tab=so-dia-chi"))
                .andExpect(flash().attribute("thongBaoThanhCong", containsString("Đã xóa địa chỉ giao hàng thành công")));

        // Kiểm tra dc1 không còn tồn tại
        assertFalse(diaChiRepository.existsById(dc1.getId()));

        // Kiểm tra dc2 tự động được nâng cấp lên làm địa chỉ mặc định
        DiaChi dc2Sau = diaChiRepository.findById(dc2.getId()).orElseThrow();
        assertTrue(dc2Sau.getLaMacDinh());
    }
}
