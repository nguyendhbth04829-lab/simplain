package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.VaiTro;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.repository.VaiTroRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động giao diện Web MVC cho chức năng Quản lý danh sách Nhân viên (US04).
 *
 * <p>Tiêu chí kiểm thử cốt lõi:
 * 1. CRUD nhan_vien trên Web MVC (Thêm, Sửa, Xóa, Tìm kiếm & Phân trang).
 * 2. Gán vai trò cho nhân viên (Role assignment & liên kết US03).
 * 3. Active / Deactive tài khoản nhân sự (Liên kết chặn đăng nhập US02).
 * 4. Bảo vệ dữ liệu: Chặn khóa / xóa superadmin hoặc tài khoản đang đăng nhập.
 *
 * <p>Chạy bằng lệnh: .\mvnw.cmd -Dtest=QuanLyNhanVienControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Quản lý Nhân sự CRUD & Phân quyền (US04)")
class QuanLyNhanVienControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String TEST_USERNAME_1 = "test_staff_us04_1";
    private final String TEST_USERNAME_2 = "test_staff_us04_2";
    private final String TEST_MA_NV_1 = "NV_TEST_04_1";
    private final String TEST_MA_NV_2 = "NV_TEST_04_2";

    @BeforeEach
    void setUp() {
        donDep();
    }

    @AfterEach
    void tearDown() {
        donDep();
    }

    private void donDep() {
        nhanVienRepository.findByTenDangNhap(TEST_USERNAME_1).ifPresent(nhanVienRepository::delete);
        nhanVienRepository.findByTenDangNhap(TEST_USERNAME_2).ifPresent(nhanVienRepository::delete);
        nhanVienRepository.findByMaNhanVien(TEST_MA_NV_1).ifPresent(nhanVienRepository::delete);
        nhanVienRepository.findByMaNhanVien(TEST_MA_NV_2).ifPresent(nhanVienRepository::delete);
    }

    @Test
    @DisplayName("US04 - Tiêu chí 1: Hiển thị danh sách nhân viên có phân trang và tìm kiếm trên giao diện Web")
    void hienThiDanhSachNhanVien_PhanTrangVaTimKiem_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/nhan-vien/hien-thi")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/nhan-vien/hien-thi"))
                .andExpect(model().attributeExists("pageData"))
                .andExpect(model().attributeExists("danhSachVaiTro"));

        mockMvc.perform(get("/admin/nhan-vien/hien-thi")
                        .param("tuKhoa", "superadmin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/nhan-vien/hien-thi"))
                .andExpect(model().attribute("tuKhoa", "superadmin"));
    }

    @Test
    @DisplayName("US04 - Tiêu chí 1 & 2: Thêm mới nhân viên trên giao diện Web, gán vai trò CSKH và mã hóa BCrypt")
    void themNhanVien_GanVaiTro_ThanhCong() throws Exception {
        VaiTro vaiTroCskh = vaiTroRepository.findByMaVaiTro("CSKH")
                .orElseThrow(() -> new IllegalStateException("Cần có vai trò CSKH trong DB"));

        mockMvc.perform(post("/admin/nhan-vien/them")
                        .param("maNhanVien", TEST_MA_NV_1)
                        .param("hoTen", "Nhân Viên Thử Nghiệm")
                        .param("tenDangNhap", TEST_USERNAME_1)
                        .param("matKhau", "Simplain@2026")
                        .param("soDienThoai", "0912345678")
                        .param("email", "test04@simplain.vn")
                        .param("maVaiTroId", String.valueOf(vaiTroCskh.getId()))
                        .param("trangThai", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/nhan-vien/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        NhanVien saved = nhanVienRepository.findByTenDangNhap(TEST_USERNAME_1).orElse(null);
        assertNotNull(saved);
        assertTrue(passwordEncoder.matches("Simplain@2026", saved.getMatKhauHash()));
        assertEquals(vaiTroCskh.getId(), saved.getVaiTro().getId());
    }

    @Test
    @DisplayName("US04 - Tiêu chí 1 & 2: Thêm mới nhân viên vai trò Thu ngân tại quầy (BAN_HANG) với mã nhân viên chứa gạch ngang và số điện thoại định dạng")
    void themNhanVien_ThuNganTaiQuay_MaCoGachNgang_ThanhCong() throws Exception {
        VaiTro vaiTroThuNgan = vaiTroRepository.findByMaVaiTro("BAN_HANG")
                .orElseThrow(() -> new IllegalStateException("Cần có vai trò BAN_HANG trong DB"));

        final String maNhanVienThuNgan = "NV-TN-04-1";
        final String tenDangNhapThuNgan = "thungan_test_04";
        nhanVienRepository.findByMaNhanVien(maNhanVienThuNgan).ifPresent(nhanVienRepository::delete);
        nhanVienRepository.findByTenDangNhap(tenDangNhapThuNgan).ifPresent(nhanVienRepository::delete);

        try {
            mockMvc.perform(post("/admin/nhan-vien/them")
                            .param("maNhanVien", maNhanVienThuNgan)
                            .param("hoTen", "Nhân Viên Thu Ngân Quầy")
                            .param("tenDangNhap", tenDangNhapThuNgan)
                            .param("matKhau", "ThuNgan@123")
                            .param("soDienThoai", "0988-123-456")
                            .param("email", "thungan@simplain.vn")
                            .param("maVaiTroId", String.valueOf(vaiTroThuNgan.getId()))
                            .param("trangThai", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/nhan-vien/hien-thi"))
                    .andExpect(flash().attributeExists("thongBaoThanhCong"));

            NhanVien saved = nhanVienRepository.findByTenDangNhap(tenDangNhapThuNgan).orElse(null);
            assertNotNull(saved);
            assertEquals(maNhanVienThuNgan, saved.getMaNhanVien());
            assertEquals("BAN_HANG", saved.getVaiTro().getMaVaiTro());
            assertEquals("0988123456", saved.getSoDienThoai());
            assertTrue(passwordEncoder.matches("ThuNgan@123", saved.getMatKhauHash()));
        } finally {
            nhanVienRepository.findByMaNhanVien(maNhanVienThuNgan).ifPresent(nhanVienRepository::delete);
            nhanVienRepository.findByTenDangNhap(tenDangNhapThuNgan).ifPresent(nhanVienRepository::delete);
        }
    }

    @Test
    @DisplayName("US04 - Validate: Báo lỗi khi trùng tên đăng nhập hoặc trùng mã nhân viên")
    void themNhanVien_LoiTrungThongTin() throws Exception {
        VaiTro vaiTroAdmin = vaiTroRepository.findByMaVaiTro("ADMIN").orElseThrow();

        // Trùng tên đăng nhập superadmin
        mockMvc.perform(post("/admin/nhan-vien/them")
                        .param("maNhanVien", TEST_MA_NV_1)
                        .param("hoTen", "Nhân Viên Trùng Username")
                        .param("tenDangNhap", "superadmin")
                        .param("matKhau", "123456")
                        .param("maVaiTroId", String.valueOf(vaiTroAdmin.getId()))
                        .param("trangThai", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/nhan-vien/hien-thi"))
                .andExpect(flash().attribute("thongBaoLoi", containsString("đã có người sử dụng")));

        // Trùng mã nhân viên NV001
        mockMvc.perform(post("/admin/nhan-vien/them")
                        .param("maNhanVien", "NV001")
                        .param("hoTen", "Nhân Viên Trùng Mã")
                        .param("tenDangNhap", TEST_USERNAME_1)
                        .param("matKhau", "123456")
                        .param("maVaiTroId", String.valueOf(vaiTroAdmin.getId()))
                        .param("trangThai", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/nhan-vien/hien-thi"))
                .andExpect(flash().attribute("thongBaoLoi", containsString("đã tồn tại")));
    }

    @Test
    @DisplayName("US04 - Tiêu chí 1 & 2: Cập nhật nhân viên trên giao diện Web và đổi sang vai trò BAN_HANG")
    void suaNhanVien_DoiVaiTro_ThanhCong() throws Exception {
        VaiTro vaiTroCskh = vaiTroRepository.findByMaVaiTro("CSKH").orElseThrow();
        VaiTro vaiTroBanHang = vaiTroRepository.findByMaVaiTro("BAN_HANG").orElseThrow();

        NhanVien nv = nhanVienRepository.save(NhanVien.builder()
                .maNhanVien(TEST_MA_NV_1)
                .hoTen("Tên Cũ")
                .tenDangNhap(TEST_USERNAME_1)
                .matKhauHash(passwordEncoder.encode("PassOld123"))
                .vaiTro(vaiTroCskh)
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build());

        mockMvc.perform(post("/admin/nhan-vien/sua/" + nv.getId())
                        .param("id", String.valueOf(nv.getId()))
                        .param("maNhanVien", TEST_MA_NV_1)
                        .param("hoTen", "Tên Mới Sau Khi Sửa")
                        .param("tenDangNhap", TEST_USERNAME_1)
                        .param("maVaiTroId", String.valueOf(vaiTroBanHang.getId()))
                        .param("trangThai", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/nhan-vien/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        NhanVien updated = nhanVienRepository.findById(nv.getId()).orElseThrow();
        assertEquals("Tên Mới Sau Khi Sửa", updated.getHoTen());
        assertEquals("BAN_HANG", updated.getVaiTro().getMaVaiTro());
    }

    @Test
    @DisplayName("US04 - Tiêu chí 3: Active / Deactive (Đổi trạng thái) trên Web và liên kết từ chối đăng nhập US02")
    void doiTrangThaiNhanVien_LienKetUS02_ThanhCong() throws Exception {
        VaiTro vaiTroCskh = vaiTroRepository.findByMaVaiTro("CSKH").orElseThrow();

        NhanVien nv = nhanVienRepository.save(NhanVien.builder()
                .maNhanVien(TEST_MA_NV_1)
                .hoTen("Nhân Viên Thử Khóa")
                .tenDangNhap(TEST_USERNAME_1)
                .matKhauHash(passwordEncoder.encode("PassLock123"))
                .vaiTro(vaiTroCskh)
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build());

        // 1. Chuyển trạng thái sang Khóa (Deactivate) trên Web
        mockMvc.perform(get("/admin/nhan-vien/doi-trang-thai/" + nv.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/nhan-vien/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertEquals(0, nhanVienRepository.findById(nv.getId()).orElseThrow().getTrangThai());

        // 2. Đăng nhập CMS bị từ chối vì tài khoản bị khóa
        mockMvc.perform(post("/admin/dang-nhap")
                        .param("tenDangNhap", TEST_USERNAME_1)
                        .param("matKhau", "PassLock123"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dang-nhap"))
                .andExpect(model().attribute("loiKhoaTaiKhoan", containsString("bị khóa")));

        // 3. Kích hoạt lại (Active)
        mockMvc.perform(get("/admin/nhan-vien/doi-trang-thai/" + nv.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/nhan-vien/hien-thi"));

        assertEquals(1, nhanVienRepository.findById(nv.getId()).orElseThrow().getTrangThai());

        // 4. Đăng nhập lại thành công
        mockMvc.perform(post("/admin/dang-nhap")
                        .param("tenDangNhap", TEST_USERNAME_1)
                        .param("matKhau", "PassLock123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @DisplayName("US04 - Bảo vệ dữ liệu: Chặn khóa hoặc xóa tài khoản Super Admin root")
    void chanKhoaVaXoaSuperadmin() throws Exception {
        NhanVien superadmin = nhanVienRepository.findByTenDangNhap("superadmin")
                .orElseThrow(() -> new IllegalStateException("Phải có superadmin trong DB"));

        // Chặn khóa
        mockMvc.perform(get("/admin/nhan-vien/doi-trang-thai/" + superadmin.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/nhan-vien/hien-thi"))
                .andExpect(flash().attribute("thongBaoLoi", containsString("Không được phép khóa tài khoản Quản trị viên tối cao")));

        // Chặn xóa
        mockMvc.perform(get("/admin/nhan-vien/xoa/" + superadmin.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/nhan-vien/hien-thi"))
                .andExpect(flash().attribute("thongBaoLoi", containsString("Không được phép xóa tài khoản Quản trị viên tối cao")));
    }

    @Test
    @DisplayName("US04 - Tiêu chí 1: Xóa thành công khi nhân viên không có ràng buộc dữ liệu")
    void xoaNhanVien_KhongRangBuoc_ThanhCong() throws Exception {
        VaiTro vaiTroBanHang = vaiTroRepository.findByMaVaiTro("BAN_HANG").orElseThrow();

        NhanVien nv = nhanVienRepository.save(NhanVien.builder()
                .maNhanVien(TEST_MA_NV_1)
                .hoTen("Nhân Viên Sẽ Xóa")
                .tenDangNhap(TEST_USERNAME_1)
                .matKhauHash(passwordEncoder.encode("Pass123"))
                .vaiTro(vaiTroBanHang)
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build());

        mockMvc.perform(get("/admin/nhan-vien/xoa/" + nv.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/nhan-vien/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertFalse(nhanVienRepository.existsById(nv.getId()));
    }

    @Test
    @DisplayName("Kiểm tra phông chữ tiếng Việt Unicode (Đ, ỗ, ễ, ả, ị, ượ) không bị biến thành dấu '?' trong SQL Server")
    void kiemTraLuuTiengVietCoDauDayDu_KhongLoiPhongChu() throws Exception {
        VaiTro adminRole = vaiTroRepository.findByMaVaiTro("ADMIN").orElseThrow();
        assertEquals("Quản trị viên tối cao", adminRole.getTenVaiTro());
        assertFalse(adminRole.getTenVaiTro().contains("?"));

        String hoTenPhucTap = "Đỗ Nguyễn Hoàng Bảo Trân";
        mockMvc.perform(post("/admin/nhan-vien/them")
                        .param("maNhanVien", TEST_MA_NV_2)
                        .param("hoTen", hoTenPhucTap)
                        .param("tenDangNhap", TEST_USERNAME_2)
                        .param("matKhau", "Simplain@2026")
                        .param("maVaiTroId", String.valueOf(adminRole.getId()))
                        .param("trangThai", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/nhan-vien/hien-thi"));

        NhanVien dbSaved = nhanVienRepository.findByTenDangNhap(TEST_USERNAME_2).orElseThrow();
        assertEquals(hoTenPhucTap, dbSaved.getHoTen());
        assertFalse(dbSaved.getHoTen().contains("?"));
    }

    @Test
    @DisplayName("US04 - Xem chi tiết và Sửa nhân viên Web MVC, kiểm tra tương thích menu quản trị và đăng nhập linh hoạt")
    void suaNhanVien_ViewChiTietVaDangNhap_ThanhCong() throws Exception {
        NhanVien bh1 = nhanVienRepository.findByTenDangNhap("bh1").orElse(null);
        if (bh1 != null) {
            bh1.setTrangThai(1);
            nhanVienRepository.save(bh1);

            // 1. Kiểm tra màn hình xem chi tiết / chỉnh sửa không bị lỗi Thymeleaf SpringEL
            mockMvc.perform(get("/admin/nhan-vien/view/" + bh1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/nhan-vien/view"))
                    .andExpect(model().attributeExists("nhanVien"))
                    .andExpect(model().attributeExists("nhanVienRequest"))
                    .andExpect(model().attributeExists("danhSachVaiTro"));

            // 2. Kiểm tra submit form sửa nhân viên
            mockMvc.perform(post("/admin/nhan-vien/sua/" + bh1.getId())
                            .param("id", String.valueOf(bh1.getId()))
                            .param("maNhanVien", bh1.getMaNhanVien())
                            .param("hoTen", bh1.getHoTen())
                            .param("tenDangNhap", bh1.getTenDangNhap())
                            .param("soDienThoai", bh1.getSoDienThoai() != null ? bh1.getSoDienThoai() : "")
                            .param("email", bh1.getEmail() != null ? bh1.getEmail() : "")
                            .param("maVaiTroId", String.valueOf(bh1.getVaiTro().getId()))
                            .param("trangThai", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/nhan-vien/hien-thi"))
                    .andExpect(flash().attributeExists("thongBaoThanhCong"));

            // 3. Đăng nhập bằng mã nhân viên (BH1) hoặc username (bh1)
            var loginResult = mockMvc.perform(post("/admin/dang-nhap")
                            .param("tenDangNhap", "BH1")
                            .param("matKhau", "hashed_pass_123"))
                    .andExpect(status().is3xxRedirection())
                    .andReturn();

            jakarta.servlet.http.Cookie tokenCookie = loginResult.getResponse().getCookie(NhanVienController.COOKIE_ADMIN_TOKEN);
            jakarta.servlet.http.HttpSession loginSession = loginResult.getRequest().getSession();

            // 4. Truy cập Dashboard với tài khoản thu ngân bán hàng
            mockMvc.perform(get("/admin/dashboard")
                            .cookie(tokenCookie)
                            .session((org.springframework.mock.web.MockHttpSession) loginSession))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/dashboard"));
        }
    }
}
