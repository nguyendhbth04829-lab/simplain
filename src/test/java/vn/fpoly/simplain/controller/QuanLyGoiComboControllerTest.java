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
import vn.fpoly.simplain.entity.GoiCombo;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.entity.SanPhamTrongCombo;
import vn.fpoly.simplain.entity.SanPhamTrongComboId;
import vn.fpoly.simplain.repository.GoiComboRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;
import vn.fpoly.simplain.repository.SanPhamTrongComboRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử Web MVC tích hợp chức năng Admin Thiết Lập Gói Combo Kích Cầu (US28 - Mod 6 / Bán hàng - Khoa).
 * - Tiêu chí 1: Tạo gói giá fix (VD: Tự chọn 3 áo giá 299k).
 * - Tiêu chí 2: Map san_pham_trong_combo các áo được áp dụng.
 * - Validation:
 *   + Validate mã combo không trùng, đúng định dạng.
 *   + Validate slug không trùng, đúng chuẩn URL thân thiện.
 *   + Validate giá combo >= 1000đ.
 *   + Validate số món quy định >= 2.
 *   + Validate số sản phẩm chọn trong combo >= số món quy định.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Kiểm thử Web MVC Admin Thiết Lập Gói Combo Kích Cầu (US28)")
class QuanLyGoiComboControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GoiComboRepository goiComboRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private SanPhamTrongComboRepository sanPhamTrongComboRepository;

    @Autowired
    private EntityManager entityManager;

    private GoiCombo comboMau;
    private List<SanPham> danhSachSanPhamTest = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // Đảm bảo có ít nhất 4 sản phẩm để test mapping combo
        danhSachSanPhamTest = sanPhamRepository.findAll();
        if (danhSachSanPhamTest.size() < 4) {
            for (int i = danhSachSanPhamTest.size() + 1; i <= 5; i++) {
                SanPham sp = SanPham.builder()
                        .maSanPham("SP_TEST_CB_" + i)
                        .tenSanPham("Áo Thun Nam Simplain Test " + i)
                        .duongDanSlug("ao-thun-nam-simplain-test-" + i)
                        .chatLieu("100% Cotton")
                        .formDang("Regular Fit")
                        .giaCoBan(BigDecimal.valueOf(149000))
                        .kichHoat(true)
                        .build();
                sp = sanPhamRepository.saveAndFlush(sp);
                danhSachSanPhamTest.add(sp);
            }
        }

        // Tạo gói combo mẫu
        comboMau = goiComboRepository.findByMaComboIgnoreCase("COMBO_TEST_MAU").orElse(null);
        if (comboMau == null) {
            comboMau = GoiCombo.builder()
                    .maCombo("COMBO_TEST_MAU")
                    .tenCombo("Combo Mẫu 3 Áo Thun 299K")
                    .duongDanSlug("combo-mau-3-ao-thun-299k")
                    .giaCombo(BigDecimal.valueOf(299000))
                    .soMonQuyDinh(3)
                    .moTa("Combo mẫu phục vụ kiểm thử đơn vị")
                    .kichHoat(true)
                    .build();
            comboMau = goiComboRepository.saveAndFlush(comboMau);

            // Gán 3 sản phẩm vào combo mẫu
            for (int i = 0; i < 3 && i < danhSachSanPhamTest.size(); i++) {
                Long spId = danhSachSanPhamTest.get(i).getId();
                sanPhamTrongComboRepository.save(new SanPhamTrongCombo(new SanPhamTrongComboId(comboMau.getId(), spId)));
            }
            sanPhamTrongComboRepository.flush();
        }

        entityManager.clear();
    }

    @Test
    @DisplayName("TC1: Hiển thị màn hình danh sách gói combo kích cầu thành công")
    void testHienThiDanhSachCombo_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/combo/hien-thi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/combo/hien-thi"))
                .andExpect(model().attributeExists("trangCombo"))
                .andExpect(model().attributeExists("tuKhoa"))
                .andExpect(model().attributeExists("trangThaiHienTai"));
    }

    @Test
    @DisplayName("TC2: Tìm kiếm và lọc danh sách gói combo theo từ khóa và trạng thái")
    void testTimKiemGoiCombo_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/combo/hien-thi")
                        .param("tuKhoa", "COMBO_TEST_MAU")
                        .param("trangThai", "DANG_HOAT_DONG"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/combo/hien-thi"))
                .andExpect(model().attributeExists("trangCombo"));
    }

    @Test
    @DisplayName("TC3: Hiển thị form tạo mới gói combo kích cầu kèm danh sách sản phẩm khả dụng")
    void testHienThiFormTaoMoi_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/combo/tao-moi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/combo/tao-moi"))
                .andExpect(model().attributeExists("req"))
                .andExpect(model().attributeExists("danhSachTatCaSanPham"))
                .andExpect(model().attribute("isEditMode", false));
    }

    @Test
    @DisplayName("TC4: Admin tạo mới gói giá fix 299k (chọn 3 áo) và map san_pham_trong_combo thành công")
    void testTaoMoiCombo_GiaFixVaMapSanPham_ThanhCong() throws Exception {
        Long spId1 = danhSachSanPhamTest.get(0).getId();
        Long spId2 = danhSachSanPhamTest.get(1).getId();
        Long spId3 = danhSachSanPhamTest.get(2).getId();

        mockMvc.perform(post("/admin/combo/tao-moi")
                        .param("maCombo", "COMBO-3AO-299K")
                        .param("tenCombo", "Tự Chọn 3 Áo Thun Cotton Giá 299K")
                        .param("duongDanSlug", "tu-chon-3-ao-thun-cotton-gia-299k")
                        .param("giaCombo", "299000")
                        .param("soMonQuyDinh", "3")
                        .param("moTa", "Gói combo kích cầu giá sốc chọn 3 áo thun tự do")
                        .param("kichHoat", "true")
                        .param("danhSachSanPhamIds", String.valueOf(spId1), String.valueOf(spId2), String.valueOf(spId3)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/combo/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        // Kiểm tra database lưu đúng combo và bảng quan hệ san_pham_trong_combo
        GoiCombo comboMoi = goiComboRepository.findByMaComboIgnoreCase("COMBO-3AO-299K").orElse(null);
        assertNotNull(comboMoi, "Gói combo mới phải được lưu thành công vào CSDL");
        assertEquals("Tự Chọn 3 Áo Thun Cotton Giá 299K", comboMoi.getTenCombo());
        assertEquals(0, BigDecimal.valueOf(299000).compareTo(comboMoi.getGiaCombo()));
        assertEquals(3, comboMoi.getSoMonQuyDinh());

        // Kiểm tra ánh xạ san_pham_trong_combo có đúng 3 bản ghi
        long soSanPhamMap = sanPhamTrongComboRepository.countByMaComboId(comboMoi.getId());
        assertEquals(3, soSanPhamMap, "Bảng san_pham_trong_combo phải lưu đúng 3 sản phẩm được chọn");
        assertTrue(sanPhamTrongComboRepository.existsByMaComboIdAndMaSanPhamId(comboMoi.getId(), spId1));
        assertTrue(sanPhamTrongComboRepository.existsByMaComboIdAndMaSanPhamId(comboMoi.getId(), spId2));
        assertTrue(sanPhamTrongComboRepository.existsByMaComboIdAndMaSanPhamId(comboMoi.getId(), spId3));
    }

    @Test
    @DisplayName("TC5: Validate bắt buộc khi để trống các trường (mã, tên, slug, giá, số món, sản phẩm)")
    void testTaoMoiCombo_LoiValidate_DeTrongCacTruongBatBuoc() throws Exception {
        mockMvc.perform(post("/admin/combo/tao-moi")
                        .param("maCombo", "")
                        .param("tenCombo", "")
                        .param("duongDanSlug", "")
                        .param("giaCombo", "")
                        .param("soMonQuyDinh", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/combo/tao-moi"))
                .andExpect(model().attributeHasFieldErrors("req", "maCombo"))
                .andExpect(model().attributeHasFieldErrors("req", "tenCombo"))
                .andExpect(model().attributeHasFieldErrors("req", "duongDanSlug"))
                .andExpect(model().attributeHasFieldErrors("req", "giaCombo"))
                .andExpect(model().attributeHasFieldErrors("req", "soMonQuyDinh"))
                .andExpect(model().attributeHasFieldErrors("req", "danhSachSanPhamIds"));
    }

    @Test
    @DisplayName("TC6: Validate lỗi nghiệp vụ khi số lượng sản phẩm áp dụng nhỏ hơn số món quy định")
    void testTaoMoiCombo_LoiValidate_SoSanPhamNhoHonSoMonQuyDinh() throws Exception {
        Long spId1 = danhSachSanPhamTest.get(0).getId();
        Long spId2 = danhSachSanPhamTest.get(1).getId();

        // Yêu cầu chọn 4 món nhưng chỉ gán 2 sản phẩm vào combo
        mockMvc.perform(post("/admin/combo/tao-moi")
                        .param("maCombo", "COMBO-LOI-SL")
                        .param("tenCombo", "Combo Test Lỗi Số Lượng")
                        .param("duongDanSlug", "combo-test-loi-so-luong")
                        .param("giaCombo", "350000")
                        .param("soMonQuyDinh", "4")
                        .param("danhSachSanPhamIds", String.valueOf(spId1), String.valueOf(spId2)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/combo/tao-moi"))
                .andExpect(model().attributeExists("thongBaoLoi"));

        assertFalse(goiComboRepository.existsByMaComboIgnoreCase("COMBO-LOI-SL"));
    }

    @Test
    @DisplayName("TC7: Validate trùng mã combo đã tồn tại trong hệ thống")
    void testTaoMoiCombo_LoiValidate_TrungMaCombo() throws Exception {
        Long spId1 = danhSachSanPhamTest.get(0).getId();
        Long spId2 = danhSachSanPhamTest.get(1).getId();

        // Sử dụng lại mã COMBO_TEST_MAU đã có sẵn
        mockMvc.perform(post("/admin/combo/tao-moi")
                        .param("maCombo", "COMBO_TEST_MAU")
                        .param("tenCombo", "Combo Trùng Mã Test")
                        .param("duongDanSlug", "combo-trung-ma-test")
                        .param("giaCombo", "250000")
                        .param("soMonQuyDinh", "2")
                        .param("danhSachSanPhamIds", String.valueOf(spId1), String.valueOf(spId2)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/combo/tao-moi"))
                .andExpect(model().attributeExists("thongBaoLoi"));
    }

    @Test
    @DisplayName("TC8: Validate trùng đường dẫn slug đã tồn tại trong hệ thống")
    void testTaoMoiCombo_LoiValidate_TrungDuongDanSlug() throws Exception {
        Long spId1 = danhSachSanPhamTest.get(0).getId();
        Long spId2 = danhSachSanPhamTest.get(1).getId();

        // Sử dụng lại slug combo-mau-3-ao-thun-299k đã có sẵn
        mockMvc.perform(post("/admin/combo/tao-moi")
                        .param("maCombo", "COMBO_MOI_SLUG_TRUNG")
                        .param("tenCombo", "Combo Mới Trùng Slug")
                        .param("duongDanSlug", "combo-mau-3-ao-thun-299k")
                        .param("giaCombo", "250000")
                        .param("soMonQuyDinh", "2")
                        .param("danhSachSanPhamIds", String.valueOf(spId1), String.valueOf(spId2)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/combo/tao-moi"))
                .andExpect(model().attributeExists("thongBaoLoi"));
    }

    @Test
    @DisplayName("TC9: Hiển thị form chỉnh sửa gói combo và nạp danh sách sản phẩm đã map")
    void testHienThiFormChinhSua_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/combo/chinh-sua/" + comboMau.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/combo/tao-moi"))
                .andExpect(model().attribute("isEditMode", true))
                .andExpect(model().attributeExists("req"))
                .andExpect(model().attributeExists("danhSachTatCaSanPham"));
    }

    @Test
    @DisplayName("TC10: Admin cập nhật thông tin gói combo và cập nhật lại map san_pham_trong_combo")
    void testCapNhatCombo_ThanhCong() throws Exception {
        Long spId1 = danhSachSanPhamTest.get(0).getId();
        Long spId3 = danhSachSanPhamTest.get(2).getId();

        mockMvc.perform(post("/admin/combo/chinh-sua/" + comboMau.getId())
                        .param("maCombo", comboMau.getMaCombo())
                        .param("tenCombo", "Combo Mẫu 3 Áo Thun Đã Update Tên")
                        .param("duongDanSlug", comboMau.getDuongDanSlug())
                        .param("giaCombo", "319000")
                        .param("soMonQuyDinh", "2")
                        .param("moTa", "Mô tả combo đã chỉnh sửa")
                        .param("kichHoat", "true")
                        .param("danhSachSanPhamIds", String.valueOf(spId1), String.valueOf(spId3)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/combo/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.flush();
        entityManager.clear();

        GoiCombo capNhat = goiComboRepository.findById(comboMau.getId()).orElse(null);
        assertNotNull(capNhat);
        assertEquals("Combo Mẫu 3 Áo Thun Đã Update Tên", capNhat.getTenCombo());
        assertEquals(0, BigDecimal.valueOf(319000).compareTo(capNhat.getGiaCombo()));
        assertEquals(2, capNhat.getSoMonQuyDinh());

        // Kiểm tra danh sách map sản phẩm đã đổi thành spId1 và spId3
        long count = sanPhamTrongComboRepository.countByMaComboId(capNhat.getId());
        assertEquals(2, count);
        assertTrue(sanPhamTrongComboRepository.existsByMaComboIdAndMaSanPhamId(capNhat.getId(), spId1));
        assertTrue(sanPhamTrongComboRepository.existsByMaComboIdAndMaSanPhamId(capNhat.getId(), spId3));
    }

    @Test
    @DisplayName("TC11: Toggle bật / tắt trạng thái kích hoạt của gói combo")
    void testToggleKichHoat_ThanhCong() throws Exception {
        boolean trangThaiCu = comboMau.getKichHoat();

        mockMvc.perform(post("/admin/combo/toggle/" + comboMau.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/combo/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.flush();
        entityManager.clear();

        GoiCombo sauToggle = goiComboRepository.findById(comboMau.getId()).orElse(null);
        assertNotNull(sauToggle);
        assertEquals(!trangThaiCu, sauToggle.getKichHoat());
    }

    @Test
    @DisplayName("TC12: Xóa gói combo và cascaded dọn sạch bảng san_pham_trong_combo")
    void testXoaCombo_ThanhCong() throws Exception {
        // Tạo một combo riêng để xóa
        GoiCombo comboXoa = GoiCombo.builder()
                .maCombo("COMBO_DE_XOA")
                .tenCombo("Combo Dùng Để Xóa")
                .duongDanSlug("combo-dung-de-xoa")
                .giaCombo(BigDecimal.valueOf(199000))
                .soMonQuyDinh(2)
                .kichHoat(false)
                .build();
        comboXoa = goiComboRepository.saveAndFlush(comboXoa);

        Long spId1 = danhSachSanPhamTest.get(0).getId();
        Long spId2 = danhSachSanPhamTest.get(1).getId();
        sanPhamTrongComboRepository.save(new SanPhamTrongCombo(new SanPhamTrongComboId(comboXoa.getId(), spId1)));
        sanPhamTrongComboRepository.save(new SanPhamTrongCombo(new SanPhamTrongComboId(comboXoa.getId(), spId2)));
        sanPhamTrongComboRepository.flush();

        Long comboXoaId = comboXoa.getId();

        mockMvc.perform(post("/admin/combo/xoa/" + comboXoaId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/combo/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.flush();
        entityManager.clear();

        assertFalse(goiComboRepository.existsById(comboXoaId), "Combo phải bị xóa khỏi CSDL");
        assertEquals(0, sanPhamTrongComboRepository.countByMaComboId(comboXoaId), "Ánh xạ sản phẩm trong combo phải được dọn dẹp sạch sẽ");
    }

    @Test
    @DisplayName("TC13: REST API sinh mã combo tự động")
    void testApiSinhMaCombo_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/combo/api/sinh-ma"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maCombo", startsWith("COMBO-")));
    }

    @Test
    @DisplayName("TC14: REST API sinh đường dẫn slug tự động từ tên tiếng Việt có dấu")
    void testApiSinhSlug_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/combo/api/sinh-slug")
                        .param("ten", "Combo 3 Áo Thun Nam Thoáng Khí 2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("combo-3-ao-thun-nam-thoang-khi-2026")));
    }

    @Test
    @DisplayName("TC15: REST API kiểm tra mã combo đã tồn tại hay chưa")
    void testApiKiemTraMaCombo_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/combo/api/kiem-tra-ma")
                        .param("maCombo", "COMBO_TEST_MAU"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tonTai", is(true)));

        mockMvc.perform(get("/admin/combo/api/kiem-tra-ma")
                        .param("maCombo", "COMBO_CHUA_CO_BAO_GIO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tonTai", is(false)));
    }
}
