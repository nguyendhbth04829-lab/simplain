package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.VaiTro;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.repository.VaiTroRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động giao diện Web MVC cho chức năng Xác thực Nhân viên CMS (US02).
 *
 * <p>Tiêu chí kiểm thử:
 * 1. Check phân quyền (Role-based).
 * 2. Từ chối đăng nhập nếu trạng thái nhân viên bị khóa.
 *
 * <p>Chạy bằng lệnh: .\mvnw.cmd -Dtest=NhanVienControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Xác thực Nhân viên CMS (US02)")
class NhanVienControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String TEST_USER_LOCKED = "test_staff_locked";
    private final String TEST_USER_ACTIVE = "test_staff_active";

    @BeforeEach
    void setUp() {
        donDep();
    }

    @AfterEach
    void tearDown() {
        donDep();
    }

    private void donDep() {
        nhanVienRepository.findByTenDangNhap(TEST_USER_LOCKED).ifPresent(nhanVienRepository::delete);
        nhanVienRepository.findByTenDangNhap(TEST_USER_ACTIVE).ifPresent(nhanVienRepository::delete);
    }

    @Test
    @DisplayName("US02 - Check phân quyền Role-based: Đăng nhập Web CMS thành công với vai trò ADMIN")
    void dangNhapCms_ThanhCong_RoleAdmin() throws Exception {
        MvcResult res = mockMvc.perform(post("/admin/dang-nhap")
                        .param("tenDangNhap", "superadmin")
                        .param("matKhau", "hashed_pass_123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(cookie().exists(NhanVienController.COOKIE_ADMIN_TOKEN))
                .andReturn();

        Cookie adminCookie = res.getResponse().getCookie(NhanVienController.COOKIE_ADMIN_TOKEN);
        assertNotNull(adminCookie);

        mockMvc.perform(get("/admin/dashboard").cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("nhanVien"));
    }

    @Test
    @DisplayName("US02 - Check phân quyền Role-based: Đăng nhập Web CMS thành công với vai trò CSKH")
    void dangNhapCms_ThanhCong_RoleCskh() throws Exception {
        mockMvc.perform(post("/admin/dang-nhap")
                        .param("tenDangNhap", "cskh_01")
                        .param("matKhau", "hashed_pass_456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(cookie().exists(NhanVienController.COOKIE_ADMIN_TOKEN));
    }

    @Test
    @DisplayName("US02 - Tiêu chí 2: Từ chối đăng nhập trên giao diện Web khi trạng thái nhân viên bị khóa (trang_thai = 0)")
    void dangNhapCms_TuChoiKhiBiKhoa() throws Exception {
        VaiTro vaiTroAdmin = vaiTroRepository.findByMaVaiTro("ADMIN").orElse(null);

        NhanVien nhanVienKhoa = NhanVien.builder()
                .maNhanVien("NV_TEST_LOCK")
                .hoTen("Nhân Viên Bị Khóa")
                .tenDangNhap(TEST_USER_LOCKED)
                .matKhauHash(passwordEncoder.encode("Pass123@"))
                .vaiTro(vaiTroAdmin)
                .trangThai(0) // BỊ KHÓA
                .ngayTao(LocalDateTime.now())
                .build();

        nhanVienRepository.save(nhanVienKhoa);

        mockMvc.perform(post("/admin/dang-nhap")
                        .param("tenDangNhap", TEST_USER_LOCKED)
                        .param("matKhau", "Pass123@"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dang-nhap"))
                .andExpect(model().attribute("loiKhoaTaiKhoan", containsString("bị khóa")));
    }

    @Test
    @DisplayName("Đăng nhập thất bại khi sai mật khẩu - Hiển thị thông báo lỗi trên trang đăng nhập CMS")
    void dangNhapCms_SaiMatKhau_Loi() throws Exception {
        mockMvc.perform(post("/admin/dang-nhap")
                        .param("tenDangNhap", "superadmin")
                        .param("matKhau", "SaiMatKhauHoanToan"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dang-nhap"))
                .andExpect(model().attribute("loiDangNhap", containsString("không chính xác")));
    }

    @Test
    @DisplayName("Truy cập Dashboard khi chưa đăng nhập sẽ chuyển hướng về trang đăng nhập CMS")
    void truyCapDashboard_ChuaDangNhap_ChuyenHuong() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dang-nhap"));
    }
}
