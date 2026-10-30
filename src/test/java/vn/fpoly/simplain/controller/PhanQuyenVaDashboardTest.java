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
import vn.fpoly.simplain.dto.VaiTroRequest;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.VaiTro;
import vn.fpoly.simplain.enums.ChucNangHeThong;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.repository.VaiTroQuyenHanRepository;
import vn.fpoly.simplain.repository.VaiTroRepository;
import vn.fpoly.simplain.service.NhanVienService;
import vn.fpoly.simplain.service.VaiTroService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Bộ kiểm thử Tự động: Phân quyền Động (Dynamic RBAC) & Giao diện Dashboard Trang chủ theo Vai trò.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Phân quyền Động (RBAC) & Dashboard Trang chủ CMS")
class PhanQuyenVaDashboardTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private VaiTroService vaiTroService;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private VaiTroQuyenHanRepository vaiTroQuyenHanRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ROLE_TEST_CODE = "TEST_ROLE_CUSTOM";
    private static final String USER_TEST_BAN_HANG = "user_ban_hang_test";

    @BeforeEach
    void setUp() {
        donDep();
    }

    @AfterEach
    void tearDown() {
        donDep();
    }

    private void donDep() {
        nhanVienRepository.findByTenDangNhap(USER_TEST_BAN_HANG).ifPresent(nhanVienRepository::delete);
        vaiTroRepository.findByMaVaiTro(ROLE_TEST_CODE).ifPresent(vt -> {
            vaiTroQuyenHanRepository.deleteAll(vaiTroQuyenHanRepository.findByMaVaiTroId(vt.getId()));
            vaiTroRepository.delete(vt);
        });
    }

    @Test
    @DisplayName("1. Điều hướng /admin và /admin/ về /admin/dashboard")
    void dieuHuongAdminRoot_VeDashboard() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));

        mockMvc.perform(get("/admin/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @DisplayName("2. ADMIN có toàn quyền: Truy cập được Dashboard, Phân quyền vai trò, Đổi trả, POS")
    void admin_ToanQuyenHeThong() throws Exception {
        // Đăng nhập tài khoản superadmin
        MvcResult loginRes = mockMvc.perform(post("/admin/dang-nhap")
                        .param("tenDangNhap", "superadmin")
                        .param("matKhau", "hashed_pass_123"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie adminCookie = loginRes.getResponse().getCookie(NhanVienController.COOKIE_ADMIN_TOKEN);
        assertNotNull(adminCookie);

        // Truy cập Dashboard
        mockMvc.perform(get("/admin/dashboard").cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("nhanVien"))
                .andExpect(model().attributeExists("quyenSet"));

        // Truy cập Quản lý Vai trò (Hệ thống)
        mockMvc.perform(get("/admin/vai-tro/hien-thi").cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/vai-tro/hien-thi"));

        // Truy cập Quản lý Đổi/Trả RMA
        mockMvc.perform(get("/admin/doi-tra/hien-thi").cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/doi-tra/hien-thi"));
    }

    @Test
    @DisplayName("3. CSKH có quyền Đổi trả, Đơn hàng nhưng BỊ CHẶN khi truy cập Phân quyền Vai trò hoặc Nhân viên")
    void cskh_PhanQuyenDong_BiChanKhiKhongCoQuyen() throws Exception {
        // Đăng nhập tài khoản cskh_01
        MvcResult loginRes = mockMvc.perform(post("/admin/dang-nhap")
                        .param("tenDangNhap", "cskh_01")
                        .param("matKhau", "hashed_pass_456"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie cskhCookie = loginRes.getResponse().getCookie(NhanVienController.COOKIE_ADMIN_TOKEN);
        assertNotNull(cskhCookie);

        // CSKH truy cập Đổi trả (ĐƯỢC PHÉP)
        mockMvc.perform(get("/admin/doi-tra/hien-thi").cookie(cskhCookie))
                .andExpect(status().isOk());

        // CSKH truy cập Phân quyền vai trò (BỊ CHẶN -> Redirect về Dashboard kèm thông báo)
        mockMvc.perform(get("/admin/vai-tro/hien-thi").cookie(cskhCookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/dashboard?tuChoiQuyen=*"));

        // CSKH truy cập Nhân viên (BỊ CHẶN -> Redirect về Dashboard kèm thông báo)
        mockMvc.perform(get("/admin/nhan-vien/hien-thi").cookie(cskhCookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/dashboard?tuChoiQuyen=*"));
    }

    @Test
    @DisplayName("4. BAN_HANG (Thu ngân POS) có quyền POS, nhưng BỊ CHẶN khi truy cập Đổi trả RMA")
    void banHang_PhanQuyenDong_BiChanKhiTruyCapDoiTra() throws Exception {
        VaiTro roleBanHang = vaiTroRepository.findByMaVaiTro("BAN_HANG")
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy vai trò BAN_HANG"));

        NhanVien nhanVienBanHang = NhanVien.builder()
                .maNhanVien("NV_TEST_POS")
                .hoTen("Nhân Viên Bán Hàng Test")
                .tenDangNhap(USER_TEST_BAN_HANG)
                .matKhauHash(passwordEncoder.encode("Pass123@"))
                .vaiTro(roleBanHang)
                .trangThai(1)
                .build();
        nhanVienRepository.save(nhanVienBanHang);

        MvcResult loginRes = mockMvc.perform(post("/admin/dang-nhap")
                        .param("tenDangNhap", USER_TEST_BAN_HANG)
                        .param("matKhau", "Pass123@"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie posCookie = loginRes.getResponse().getCookie(NhanVienController.COOKIE_ADMIN_TOKEN);
        assertNotNull(posCookie);

        // Thu ngân POS truy cập Bán hàng tại quầy (ĐƯỢC PHÉP)
        mockMvc.perform(get("/admin/ban-hang-tai-quay").cookie(posCookie))
                .andExpect(status().isOk());

        // Thu ngân POS truy cập Đổi trả RMA (BỊ CHẶN -> Redirect)
        mockMvc.perform(get("/admin/doi-tra/hien-thi").cookie(posCookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/dashboard?tuChoiQuyen=*"));
    }

    @Test
    @DisplayName("5. Mở rộng tương lai: Tạo vai trò mới với quyền hạn tùy chỉnh động (Custom RBAC)")
    void taoVaiTroMoi_VaCapQuyenDong() throws Exception {
        List<String> quyenCustom = Arrays.asList("SAN_PHAM", "NOI_DUNG");

        VaiTroRequest request = VaiTroRequest.builder()
                .maVaiTro(ROLE_TEST_CODE)
                .tenVaiTro("Vai trò Quản lý Nội dung & Hàng hóa")
                .moTa("Vai trò thử nghiệm mở rộng trong tương lai")
                .trangThai(1)
                .danhSachQuyen(quyenCustom)
                .build();

        // 1. Thêm mới vai trò kèm quyền
        VaiTro saved = vaiTroService.them(request);
        assertNotNull(saved.getId());

        // 2. Tra cứu danh sách quyền đã lưu
        List<String> quyenDaLuu = vaiTroService.layDanhSachQuyenCuaVaiTro(saved.getId());
        assertEquals(2, quyenDaLuu.size());
        assertTrue(quyenDaLuu.contains("SAN_PHAM"));
        assertTrue(quyenDaLuu.contains("NOI_DUNG"));
        assertFalse(quyenDaLuu.contains("HE_THONG"));

        // 3. Cập nhật thêm quyền KHUYEN_MAI
        request.setDanhSachQuyen(Arrays.asList("SAN_PHAM", "NOI_DUNG", "KHUYEN_MAI"));
        vaiTroService.sua(saved.getId(), request);

        List<String> quyenSauCapNhat = vaiTroService.layDanhSachQuyenCuaVaiTro(saved.getId());
        assertEquals(3, quyenSauCapNhat.size());
        assertTrue(quyenSauCapNhat.contains("KHUYEN_MAI"));
    }

    @Test
    @DisplayName("6. AJAX Request bị chặn quyền sẽ trả về HTTP 403 JSON")
    void ajaxRequest_TuChoiQuyen_TraVe403Json() throws Exception {
        MvcResult loginRes = mockMvc.perform(post("/admin/dang-nhap")
                        .param("tenDangNhap", "cskh_01")
                        .param("matKhau", "hashed_pass_456"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie cskhCookie = loginRes.getResponse().getCookie(NhanVienController.COOKIE_ADMIN_TOKEN);
        assertNotNull(cskhCookie);

        // Gọi AJAX tới đường dẫn phân quyền hệ thống mà CSKH không có
        mockMvc.perform(get("/admin/vai-tro/hien-thi")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .cookie(cskhCookie))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Tài khoản của bạn không có quyền truy cập chức năng [HE_THONG]")));
    }
}
