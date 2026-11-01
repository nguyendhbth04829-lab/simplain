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
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.QuenMatKhauOtp;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.QuenMatKhauOtpRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động tính năng Quên mật khẩu và Đặt lại mật khẩu qua Gmail OTP.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Quên mật khẩu & Xác thực Gmail OTP")
class QuenMatKhauControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private QuenMatKhauOtpRepository quenMatKhauOtpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String testEmail;
    private String testSdt;
    private String testMaKh;
    private Long testKhachHangId;

    @BeforeEach
    void setUp() {
        long timestamp = System.currentTimeMillis() % 100000000;
        testEmail = "test_otp_" + timestamp + "@gmail.com";
        testSdt = "09" + String.format("%08d", timestamp);
        testMaKh = "KH_OTP_" + (timestamp % 100000);

        KhachHang kh = KhachHang.builder()
                .maKhachHang(testMaKh)
                .hoTen("Nguyễn Thử Nghiệm OTP")
                .email(testEmail)
                .soDienThoai(testSdt)
                .matKhauHash(passwordEncoder.encode("OldPass@123"))
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build();
        kh = khachHangRepository.save(kh);
        testKhachHangId = kh.getId();
    }

    @AfterEach
    void tearDown() {
        donDep();
    }

    private void donDep() {
        if (testEmail != null) {
            try {
                quenMatKhauOtpRepository.deleteAll(quenMatKhauOtpRepository.findByEmailAndDaSuDungFalse(testEmail));
            } catch (Exception ignored) {}
        }
        if (testKhachHangId != null) {
            try {
                khachHangRepository.deleteById(testKhachHangId);
            } catch (Exception ignored) {}
        }
    }

    @Test
    @DisplayName("1. Hiển thị màn hình Quên mật khẩu khách hàng thành công")
    void hienThiManHinhQuenMatKhau_ThanhCong() throws Exception {
        mockMvc.perform(get("/khach-hang/quen-mat-khau"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/quen-mat-khau"))
                .andExpect(model().attributeExists("yeuCauQuenMatKhauRequest"))
                .andExpect(model().attributeExists("xacNhanDoiMatKhauRequest"));
    }

    @Test
    @DisplayName("2. Gửi mã OTP thành công khi email tồn tại trong hệ thống")
    void guiOtp_EmailTonTai_ThanhCong() throws Exception {
        mockMvc.perform(post("/khach-hang/quen-mat-khau/gui-otp")
                        .param("email", testEmail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/quen-mat-khau?buoc=2&email=" + testEmail))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        QuenMatKhauOtp otp = quenMatKhauOtpRepository
                .findTopByEmailAndLoaiNguoiDungAndDaSuDungFalseOrderByThoiGianTaoDesc(testEmail, "KHACH_HANG")
                .orElse(null);

        assertNotNull(otp);
        assertNotNull(otp.getMaOtp());
        assertTrue(otp.getMaOtp().matches("^[0-9]{6}$"));
    }

    @Test
    @DisplayName("3. Báo lỗi khi email không tồn tại trong hệ thống")
    void guiOtp_EmailKhongTonTai_BaoLoi() throws Exception {
        mockMvc.perform(post("/khach-hang/quen-mat-khau/gui-otp")
                        .param("email", "khongtontai9999@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/quen-mat-khau"))
                .andExpect(model().attribute("thongBaoLoi", containsString("Không tìm thấy tài khoản")));
    }

    @Test
    @DisplayName("4. Xác thực mã OTP đúng và đổi mật khẩu mới thành công")
    void xacNhanOtpVaDoiMatKhau_ThanhCong() throws Exception {
        // Gửi OTP
        mockMvc.perform(post("/khach-hang/quen-mat-khau/gui-otp")
                        .param("email", testEmail))
                .andExpect(status().is3xxRedirection());

        QuenMatKhauOtp otp = quenMatKhauOtpRepository
                .findTopByEmailAndLoaiNguoiDungAndDaSuDungFalseOrderByThoiGianTaoDesc(testEmail, "KHACH_HANG")
                .orElseThrow();

        // Nhập OTP và mật khẩu mới
        mockMvc.perform(post("/khach-hang/quen-mat-khau/dat-lai")
                        .param("email", testEmail)
                        .param("maOtp", otp.getMaOtp())
                        .param("matKhauMoi", "NewPass@2026")
                        .param("xacNhanMatKhauMoi", "NewPass@2026"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/dang-nhap"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        KhachHang updated = khachHangRepository.findByEmail(testEmail).orElseThrow();
        assertTrue(passwordEncoder.matches("NewPass@2026", updated.getMatKhauHash()));
    }

    @Test
    @DisplayName("5. Báo lỗi khi nhập sai mã OTP")
    void xacNhanOtp_SaiMaOtp_BaoLoi() throws Exception {
        mockMvc.perform(post("/khach-hang/quen-mat-khau/gui-otp")
                        .param("email", testEmail))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/khach-hang/quen-mat-khau/dat-lai")
                        .param("email", testEmail)
                        .param("maOtp", "000000") // Mã sai
                        .param("matKhauMoi", "NewPass@2026")
                        .param("xacNhanMatKhauMoi", "NewPass@2026"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/quen-mat-khau"))
                .andExpect(model().attribute("thongBaoLoi", containsString("Mã OTP không chính xác")));
    }
}
