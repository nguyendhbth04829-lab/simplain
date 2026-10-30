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
import vn.fpoly.simplain.entity.DanhMuc;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.repository.DanhMucRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;
import vn.fpoly.simplain.repository.SanPhamYeuThichRepository;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động Quản lý Sản phẩm Yêu thích (Wishlist - US14).
 * Chạy lệnh: .\mvnw.cmd -Dtest=SanPhamYeuThichControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Sản phẩm Yêu thích (Wishlist - US14)")
class SanPhamYeuThichControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SanPhamYeuThichRepository sanPhamYeuThichRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    private static final String EMAIL_TEST = "us14.wishlist@gmail.com";
    private static final String SDT_TEST = "0914888999";
    private static final String MA_SP_TEST = "SP-US14-01";
    private static final String SLUG_SP_TEST = "sp-us14-ao-thun";
    private static final String SLUG_DM_TEST = "dm-us14-test";

    private DanhMuc danhMucTest;
    private SanPham sanPhamTest;

    @BeforeEach
    void setUp() {
        donDep();

        danhMucTest = danhMucRepository.save(DanhMuc.builder()
                .tenDanhMuc("Danh Mục Test US14")
                .duongDanSlug(SLUG_DM_TEST)
                .thuTuHienThi(1)
                .kichHoat(true)
                .build());

        sanPhamTest = sanPhamRepository.save(SanPham.builder()
                .maSanPham(MA_SP_TEST)
                .tenSanPham("Áo Thun Test Yêu Thích US14")
                .duongDanSlug(SLUG_SP_TEST)
                .maDanhMucId(danhMucTest.getId())
                .chatLieu("Cotton 100%")
                .formDang("Regular Fit")
                .moTaChiTiet("Mô tả chi tiết sản phẩm thời trang phục vụ kiểm thử US14.")
                .giaCoBan(new BigDecimal("199000"))
                .kichHoat(true)
                .noiBat(true)
                .build());
    }

    @AfterEach
    void tearDown() {
        donDep();
    }

    private void donDep() {
        khachHangRepository.findByEmail(EMAIL_TEST).ifPresent(kh -> {
            sanPhamYeuThichRepository.xoaTatCaTheoKhachHang(kh.getId());
            khachHangRepository.delete(kh);
        });
        khachHangRepository.findBySoDienThoai(SDT_TEST).ifPresent(kh -> {
            sanPhamYeuThichRepository.xoaTatCaTheoKhachHang(kh.getId());
            khachHangRepository.delete(kh);
        });

        sanPhamRepository.findByMaSanPham(MA_SP_TEST).ifPresent(sp -> {
            sanPhamYeuThichRepository.findAll().stream()
                    .filter(yt -> yt.getMaSanPhamId().equals(sp.getId()))
                    .forEach(sanPhamYeuThichRepository::delete);
            sanPhamRepository.delete(sp);
        });

        danhMucRepository.findByDuongDanSlug(SLUG_DM_TEST).ifPresent(danhMucRepository::delete);
    }

    private Cookie dangKyVaLayCookie() throws Exception {
        MvcResult res = mockMvc.perform(post("/khach-hang/dang-ky")
                        .param("hoTen", "Khách Hàng Wishlist")
                        .param("email", EMAIL_TEST)
                        .param("soDienThoai", SDT_TEST)
                        .param("matKhau", "Pass123@")
                        .param("xacNhanMatKhau", "Pass123@"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        Cookie cookie = res.getResponse().getCookie(KhachHangController.COOKIE_TOKEN);
        assertNotNull(cookie);
        return cookie;
    }

    @Test
    @DisplayName("US14 - Tiêu chí 2: Chưa đăng nhập truy cập /khach-hang/yeu-thich chuyển hướng sang đăng nhập")
    void us14_ChuaDangNhap_XemYeuThich_ChuyenHuong() throws Exception {
        mockMvc.perform(get("/khach-hang/yeu-thich"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/dang-nhap"))
                .andExpect(flash().attribute("loiDangNhap", containsString("Vui lòng đăng nhập")));
    }

    @Test
    @DisplayName("US14 - Tiêu chí 2: Đã đăng nhập xem trang Danh sách yêu thích thành công")
    void us14_DaDangNhap_XemYeuThich_ThanhCong() throws Exception {
        Cookie cookie = dangKyVaLayCookie();

        mockMvc.perform(get("/khach-hang/yeu-thich")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/yeu-thich"))
                .andExpect(model().attributeExists("danhSachSanPham"))
                .andExpect(model().attributeExists("tongSoYeuThich"));
    }

    @Test
    @DisplayName("US14 - Tiêu chí 1: Chưa đăng nhập nhấn tim trả về mã 401 Unauthorized và cờ chuaDangNhap")
    void us14_ChuaDangNhap_ToggleYeuThich_BaoLoi401() throws Exception {
        mockMvc.perform(post("/khach-hang/yeu-thich/toggle/" + sanPhamTest.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.thanhCong", is(false)))
                .andExpect(jsonPath("$.chuaDangNhap", is(true)))
                .andExpect(jsonPath("$.thongBao", containsString("Vui lòng đăng nhập")));
    }

    @Test
    @DisplayName("US14 - Tiêu chí 1: Nhấn tim lần 1 thêm vào yêu thích, nhấn tim lần 2 bỏ khỏi yêu thích")
    void us14_DaDangNhap_ToggleYeuThich_ThemVaBo_ThanhCong() throws Exception {
        Cookie cookie = dangKyVaLayCookie();
        KhachHang kh = khachHangRepository.findByEmail(EMAIL_TEST).orElseThrow();

        // 1. Nhấn tim lần 1 -> Thêm vào san_pham_yeu_thich
        mockMvc.perform(post("/khach-hang/yeu-thich/toggle/" + sanPhamTest.getId())
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong", is(true)))
                .andExpect(jsonPath("$.daYeuThich", is(true)))
                .andExpect(jsonPath("$.tongSoYeuThich", is(1)))
                .andExpect(jsonPath("$.thongBao", containsString("Đã thêm")));

        assertTrue(sanPhamYeuThichRepository.existsByMaKhachHangIdAndMaSanPhamId(kh.getId(), sanPhamTest.getId()));

        // 2. Nhấn tim lần 2 -> Xóa khỏi san_pham_yeu_thich
        mockMvc.perform(post("/khach-hang/yeu-thich/toggle/" + sanPhamTest.getId())
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong", is(true)))
                .andExpect(jsonPath("$.daYeuThich", is(false)))
                .andExpect(jsonPath("$.tongSoYeuThich", is(0)))
                .andExpect(jsonPath("$.thongBao", containsString("Đã bỏ")));

        assertFalse(sanPhamYeuThichRepository.existsByMaKhachHangIdAndMaSanPhamId(kh.getId(), sanPhamTest.getId()));
    }

    @Test
    @DisplayName("US14 - Tiêu chí 1 & 2: Xóa sản phẩm khỏi danh sách yêu thích qua endpoint GET /xoa/{id}")
    void us14_XoaKhoiYeuThich_ThanhCong() throws Exception {
        Cookie cookie = dangKyVaLayCookie();
        KhachHang kh = khachHangRepository.findByEmail(EMAIL_TEST).orElseThrow();

        // Thêm vào yêu thích trước
        mockMvc.perform(post("/khach-hang/yeu-thich/toggle/" + sanPhamTest.getId())
                        .cookie(cookie))
                .andExpect(status().isOk());
        assertTrue(sanPhamYeuThichRepository.existsByMaKhachHangIdAndMaSanPhamId(kh.getId(), sanPhamTest.getId()));

        // Gọi action xóa
        mockMvc.perform(get("/khach-hang/yeu-thich/xoa/" + sanPhamTest.getId())
                        .cookie(cookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khach-hang/yeu-thich"))
                .andExpect(flash().attribute("thongBaoThanhCong", containsString("Đã xóa sản phẩm khỏi danh sách yêu thích")));

        assertFalse(sanPhamYeuThichRepository.existsByMaKhachHangIdAndMaSanPhamId(kh.getId(), sanPhamTest.getId()));
    }

    @Test
    @DisplayName("US14: API lấy số lượng sản phẩm yêu thích /khach-hang/yeu-thich/so-luong")
    void us14_LaySoLuongYeuThich_Api_ThanhCong() throws Exception {
        Cookie cookie = dangKyVaLayCookie();

        // Ban đầu = 0
        mockMvc.perform(get("/khach-hang/yeu-thich/so-luong")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.soLuong", is(0)))
                .andExpect(jsonPath("$.chuaDangNhap", is(false)));

        // Sau khi thả tim = 1
        mockMvc.perform(post("/khach-hang/yeu-thich/toggle/" + sanPhamTest.getId())
                        .cookie(cookie))
                .andExpect(status().isOk());

        mockMvc.perform(get("/khach-hang/yeu-thich/so-luong")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.soLuong", is(1)));
    }
}
