package vn.fpoly.simplain.controller;

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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động giao diện Web MVC & Validate chuyên nghiệp cho chức năng
 * Tạo Biến thể sản phẩm (SKU), Quản lý Tồn kho và Upload nhiều hình ảnh (US09).
 *
 * <p>Tiêu chí kiểm thử US09:
 * 1. Map Màu sắc + Kích thước tạo bien_the_san_pham (Chặn tạo trùng cặp Sản phẩm + Màu + Size, chặn trùng SKU).
 * 2. Quản lý Giá bán / Giá gốc, Số lượng tồn kho và cập nhật nhanh tồn kho.
 * 3. Upload nhiều hinh_anh cùng lúc cho biến thể, chọn ảnh đại diện, xóa từng ảnh, chặn file độc hại (.exe).
 * 4. Kiểm tra liên kết khóa ngoại bảo vệ dữ liệu với Sản phẩm (US08), Màu sắc & Kích thước (US07).
 *
 * <p>Chạy bằng lệnh: .\mvnw.cmd -Dtest=BienTheSanPhamControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Biến thể sản phẩm (SKU), Tồn kho & Upload nhiều hình ảnh (US09)")
class BienTheSanPhamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BienTheSanPhamRepository bienTheRepository;

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichThuocRepository kichThuocRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    private static final String SLUG_DM_US09 = "dm-test-us09";
    private static final String MA_SP_US09 = "SP-TEST-US09";
    private static final String SLUG_SP_US09 = "ao-polo-test-us09";
    private static final String HEX_MAU_1 = "#112233";
    private static final String HEX_MAU_2 = "#445566";
    private static final String SIZE_1 = "L_US09";
    private static final String SIZE_2 = "XL_US09";

    private static final String SKU_TEST_1 = "SP-TEST-US09-NAVY-L";
    private static final String SKU_TEST_2 = "SP-TEST-US09-XAM-XL";

    private DanhMuc dmTest;
    private SanPham spTest;
    private MauSac mau1;
    private MauSac mau2;
    private KichThuoc size1;
    private KichThuoc size2;

    @BeforeEach
    void setUp() {
        donDepDuLieuTest();

        dmTest = danhMucRepository.save(DanhMuc.builder()
                .tenDanhMuc("Danh Mục Test US09")
                .duongDanSlug(SLUG_DM_US09)
                .thuTuHienThi(99)
                .kichHoat(true)
                .build());

        spTest = sanPhamRepository.save(SanPham.builder()
                .maDanhMucId(dmTest.getId())
                .maSanPham(MA_SP_US09)
                .tenSanPham("Áo Polo Thể Thao Test US09")
                .duongDanSlug(SLUG_SP_US09)
                .chatLieu("100% Cotton Compact")
                .formDang("Regular Fit")
                .moTaChiTiet("Sản phẩm gốc phục vụ kiểm thử tự động tạo biến thể SKU và tồn kho US09.")
                .giaCoBan(new BigDecimal("299000"))
                .noiBat(true)
                .kichHoat(true)
                .build());

        mau1 = mauSacRepository.save(MauSac.builder()
                .maMau(HEX_MAU_1)
                .tenMau("Xanh Navy US09")
                .build());

        mau2 = mauSacRepository.save(MauSac.builder()
                .maMau(HEX_MAU_2)
                .tenMau("Xám Khói US09")
                .build());

        size1 = kichThuocRepository.save(KichThuoc.builder()
                .maKichThuoc(SIZE_1)
                .tenKichThuoc("Size L Chuẩn US09")
                .build());

        size2 = kichThuocRepository.save(KichThuoc.builder()
                .maKichThuoc(SIZE_2)
                .tenKichThuoc("Size XL Chuẩn US09")
                .build());
    }

    @AfterEach
    void tearDown() {
        donDepDuLieuTest();
    }

    private void donDepDuLieuTest() {
        bienTheRepository.findByMaSkuIgnoreCase(SKU_TEST_1).ifPresent(bt -> {
            jdbcTemplate.update("DELETE FROM chi_tiet_gio_hang WHERE ma_bien_the_id = ?", bt.getId());
            jdbcTemplate.update("DELETE FROM chi_tiet_flash_sale WHERE ma_bien_the_id = ?", bt.getId());
            hinhAnhRepository.deleteAll(hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(bt.getId()));
            bienTheRepository.delete(bt);
        });
        bienTheRepository.findByMaSkuIgnoreCase(SKU_TEST_2).ifPresent(bt -> {
            jdbcTemplate.update("DELETE FROM chi_tiet_gio_hang WHERE ma_bien_the_id = ?", bt.getId());
            jdbcTemplate.update("DELETE FROM chi_tiet_flash_sale WHERE ma_bien_the_id = ?", bt.getId());
            hinhAnhRepository.deleteAll(hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(bt.getId()));
            bienTheRepository.delete(bt);
        });

        sanPhamRepository.findByMaSanPhamIgnoreCase(MA_SP_US09).ifPresent(sp -> {
            hinhAnhRepository.deleteAll(hinhAnhRepository.findByMaSanPhamIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(sp.getId()));
            List<BienTheSanPham> dsBt = bienTheRepository.findByMaSanPhamIdOrderByIdDesc(sp.getId());
            for (BienTheSanPham bt : dsBt) {
                jdbcTemplate.update("DELETE FROM chi_tiet_gio_hang WHERE ma_bien_the_id = ?", bt.getId());
                jdbcTemplate.update("DELETE FROM chi_tiet_flash_sale WHERE ma_bien_the_id = ?", bt.getId());
                hinhAnhRepository.deleteAll(hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(bt.getId()));
                bienTheRepository.delete(bt);
            }
            sanPhamRepository.delete(sp);
        });

        danhMucRepository.findByDuongDanSlug(SLUG_DM_US09).ifPresent(danhMucRepository::delete);
        mauSacRepository.findByMaMauIgnoreCase(HEX_MAU_1).ifPresent(mauSacRepository::delete);
        mauSacRepository.findByMaMauIgnoreCase(HEX_MAU_2).ifPresent(mauSacRepository::delete);
        kichThuocRepository.findByMaKichThuocIgnoreCase(SIZE_1).ifPresent(kichThuocRepository::delete);
        kichThuocRepository.findByMaKichThuocIgnoreCase(SIZE_2).ifPresent(kichThuocRepository::delete);
    }

    @Test
    @DisplayName("US09 - Hiển thị danh sách Biến thể SKU có phân trang, lọc đa tiêu chí và thống kê KPI tồn kho")
    void hienThiDanhSachBienThe_PhanTrangVaThongKeTonKho_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/bien-the/hien-thi")
                        .param("page", "0")
                        .param("maSanPhamId", String.valueOf(spTest.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/bien-the/hien-thi"))
                .andExpect(model().attributeExists(
                        "listBienThe", "listSanPham", "listMauSac", "listKichThuoc",
                        "bienThe", "tongBienThe", "tongTonKho", "soSkuSapHetHang", "tongHinhAnh"));
    }

    @Test
    @DisplayName("US09 - Tiêu chí 1, 2 & 3: Map Màu + Size tạo bien_the_san_pham, quản lý Giá bán/Giá gốc/Tồn kho và Upload nhiều hình ảnh thành công")
    void taoBienThe_QuanLyTonKhoGiaVaUploadNhieuAnh_ThanhCong() throws Exception {
        MockMultipartFile anhMatTruoc = new MockMultipartFile(
                "filesAnh", "polo_navy_front.png", "image/png",
                "fake-png-content-front".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile anhMatSau = new MockMultipartFile(
                "filesAnh", "polo_navy_back.jpg", "image/jpeg",
                "fake-jpg-content-back".getBytes(StandardCharsets.UTF_8));

        // 1. Tạo mới biến thể kết hợp (spTest + mau1 + size1) kèm upload 2 ảnh cùng lúc
        mockMvc.perform(multipart("/admin/bien-the/add")
                        .file(anhMatTruoc)
                        .file(anhMatSau)
                        .param("maSanPhamId", String.valueOf(spTest.getId()))
                        .param("maMauSacId", String.valueOf(mau1.getId()))
                        .param("maKichThuocId", String.valueOf(size1.getId()))
                        .param("maSku", "sp-test-us09-navy-l") // Tự động chuẩn hóa IN HOA
                        .param("maVachBarcode", "")            // Để trống -> Tự sinh BC-...
                        .param("giaBan", "299000")
                        .param("giaGoc", "175000")
                        .param("soLuong", "120")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/bien-the/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        BienTheSanPham saved = bienTheRepository.findByMaSkuIgnoreCase(SKU_TEST_1).orElseThrow();
        assertEquals(SKU_TEST_1, saved.getMaSku());
        assertEquals("BC-" + SKU_TEST_1, saved.getMaVachBarcode());
        assertEquals(120, saved.getSoLuong());
        assertEquals(0, new BigDecimal("299000").compareTo(saved.getGiaBan()));
        assertEquals(0, new BigDecimal("175000").compareTo(saved.getGiaGoc()));

        // Kiểm tra 2 hình ảnh đã được lưu vào bảng [hinh_anh] và ảnh đầu tiên là ảnh đại diện
        List<HinhAnh> dsAnh = hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(saved.getId());
        assertEquals(2, dsAnh.size(), "Phải lưu đủ 2 hình ảnh đã upload");
        assertTrue(dsAnh.get(0).getLaAnhDaiDien(), "Ảnh đầu tiên phải tự động làm ảnh đại diện");
        assertFalse(dsAnh.get(1).getLaAnhDaiDien());

        // 2. Cập nhật nhanh Tồn kho & Giá bán ngay trên bảng danh sách
        mockMvc.perform(post("/admin/bien-the/cap-nhat-ton-kho")
                        .param("id", String.valueOf(saved.getId()))
                        .param("soLuong", "85")
                        .param("giaBan", "279000")
                        .param("giaGoc", "175000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/bien-the/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        BienTheSanPham afterStockUpdate = bienTheRepository.findById(saved.getId()).orElseThrow();
        assertEquals(85, afterStockUpdate.getSoLuong());
        assertEquals(0, new BigDecimal("279000").compareTo(afterStockUpdate.getGiaBan()));

        // 3. Đổi ảnh đại diện sang ảnh thứ 2 và xóa ảnh thứ 1
        Long idAnhThuHai = dsAnh.get(1).getId();
        mockMvc.perform(get("/admin/bien-the/chon-anh-dai-dien")
                        .param("hinhAnhId", String.valueOf(idAnhThuHai))
                        .param("bienTheId", String.valueOf(saved.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/bien-the/view?id=" + saved.getId()));

        assertTrue(hinhAnhRepository.findById(idAnhThuHai).orElseThrow().getLaAnhDaiDien());

        mockMvc.perform(get("/admin/bien-the/xoa-anh")
                        .param("hinhAnhId", String.valueOf(dsAnh.get(0).getId()))
                        .param("bienTheId", String.valueOf(saved.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/bien-the/view?id=" + saved.getId()));

        assertEquals(1, hinhAnhRepository.countByMaBienTheId(saved.getId()));

        // 4. Xóa biến thể SKU (Tự động dọn dẹp ảnh đính kèm và xóa thành công)
        mockMvc.perform(get("/admin/bien-the/delete")
                        .param("id", String.valueOf(saved.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/bien-the/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertFalse(bienTheRepository.existsById(saved.getId()));
    }

    @Test
    @DisplayName("US09 - Validate Chuyên nghiệp: Chặn trùng cặp (Sản phẩm + Màu + Size), chặn trùng SKU, chặn tồn kho/giá âm và chặn file .exe")
    void validateBienThe_ChanTrungMauSizeVaFileDocHai() throws Exception {
        // 1. Validate Bean Validation khi bỏ trống Màu/Size, giá bán âm và tồn kho âm
        mockMvc.perform(post("/admin/bien-the/add")
                        .param("maSanPhamId", String.valueOf(spTest.getId()))
                        .param("maSku", "SKU SAI @")
                        .param("giaBan", "-10000")
                        .param("giaGoc", "-5000")
                        .param("soLuong", "-20"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/bien-the/hien-thi"))
                .andExpect(model().attributeHasFieldErrors(
                        "bienThe", "maMauSacId", "maKichThuocId", "maSku", "giaBan", "giaGoc", "soLuong"));

        // 2. Tạo 1 biến thể hợp lệ (spTest + mau1 + size1)
        bienTheRepository.save(BienTheSanPham.builder()
                .maSanPhamId(spTest.getId())
                .maMauSacId(mau1.getId())
                .maKichThuocId(size1.getId())
                .maSku(SKU_TEST_1)
                .maVachBarcode("BC-" + SKU_TEST_1)
                .soLuong(50)
                .giaBan(new BigDecimal("299000"))
                .giaGoc(new BigDecimal("180000"))
                .kichHoat(true)
                .build());

        // 2a. Thử tạo biến thể thứ 2 khác mã SKU nhưng TRÙNG cặp (Sản phẩm + Màu 1 + Size 1) -> Phải bị chặn!
        mockMvc.perform(post("/admin/bien-the/add")
                        .param("maSanPhamId", String.valueOf(spTest.getId()))
                        .param("maMauSacId", String.valueOf(mau1.getId()))
                        .param("maKichThuocId", String.valueOf(size1.getId()))
                        .param("maSku", SKU_TEST_2)
                        .param("giaBan", "299000")
                        .param("giaGoc", "180000")
                        .param("soLuong", "30"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/bien-the/hien-thi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("đã tồn tại trên hệ thống")));

        // 2b. Thử tải lên file độc hại (.exe) khi tạo biến thể (spTest + mau2 + size2) -> Phải bị chặn!
        MockMultipartFile fileDocHai = new MockMultipartFile(
                "filesAnh", "trojan.exe", "application/octet-stream",
                "MZ-executable".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/admin/bien-the/add")
                        .file(fileDocHai)
                        .param("maSanPhamId", String.valueOf(spTest.getId()))
                        .param("maMauSacId", String.valueOf(mau2.getId()))
                        .param("maKichThuocId", String.valueOf(size2.getId()))
                        .param("maSku", SKU_TEST_2)
                        .param("giaBan", "299000")
                        .param("giaGoc", "180000")
                        .param("soLuong", "30"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/bien-the/hien-thi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("Chỉ chấp nhận định dạng hình ảnh")));

        assertFalse(bienTheRepository.findByMaSkuIgnoreCase(SKU_TEST_2).isPresent());
    }

    @Test
    @DisplayName("US09 <-> US08 & US07 Liên kết dữ liệu: Chặn xóa Sản phẩm, Màu sắc, Kích thước khi đang có Biến thể SKU sử dụng")
    void chanXoaSanPhamMauSacKichThuocKhiDangCoBienTheSku() throws Exception {
        BienTheSanPham bt = bienTheRepository.save(BienTheSanPham.builder()
                .maSanPhamId(spTest.getId())
                .maMauSacId(mau1.getId())
                .maKichThuocId(size1.getId())
                .maSku(SKU_TEST_1)
                .maVachBarcode("BC-" + SKU_TEST_1)
                .soLuong(25)
                .giaBan(new BigDecimal("299000"))
                .giaGoc(new BigDecimal("180000"))
                .kichHoat(true)
                .build());

        // 1. Thử xóa Sản phẩm gốc (US08) khi đang có SKU -> Bị chặn!
        mockMvc.perform(get("/admin/san-pham/delete")
                        .param("id", String.valueOf(spTest.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/san-pham/hien-thi"))
                .andExpect(flash().attribute("thongBaoLoi", containsString("biến thể SKU")));
        assertTrue(sanPhamRepository.existsById(spTest.getId()));

        // 2. Thử xóa Màu sắc (US07) khi đang có SKU -> Bị chặn!
        mockMvc.perform(get("/admin/mau-sac/delete")
                        .param("id", String.valueOf(mau1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/mau-sac/hien-thi"))
                .andExpect(flash().attribute("thongBaoLoi", containsString("đang được sử dụng")));
        assertTrue(mauSacRepository.existsById(mau1.getId()));

        // 3. Thử xóa Kích thước (US07) khi đang có SKU -> Bị chặn!
        mockMvc.perform(get("/admin/kich-thuoc/delete")
                        .param("id", String.valueOf(size1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/kich-thuoc/hien-thi"))
                .andExpect(flash().attribute("thongBaoLoi", containsString("biến thể sản phẩm sử dụng")));
        assertTrue(kichThuocRepository.existsById(size1.getId()));

        bienTheRepository.deleteById(bt.getId());
    }

    @Test
    @DisplayName("Kiểm thử API tự động sinh mã SKU và Barcode (US09 nâng cao)")
    void testApiSinhMaTuDong_SkuVaBarcode() throws Exception {
        mockMvc.perform(get("/admin/bien-the/api/sinh-ma")
                        .param("maSanPhamId", String.valueOf(spTest.getId()))
                        .param("maMauSacId", String.valueOf(mau1.getId()))
                        .param("maKichThuocId", String.valueOf(size1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku", org.hamcrest.Matchers.notNullValue()))
                .andExpect(jsonPath("$.barcode", org.hamcrest.Matchers.startsWith("BC-")));
    }

    @Test
    @DisplayName("Kiểm thử tính năng Thêm nhanh ma trận biến thể SIMPLAIN Matrix (Nhiều biến thể 1 lần)")
    void testThemNhanhBienTheHangLoatSimplainMatrix() throws Exception {
        String jsonPayload = """
                {
                    "maSanPhamId": %d,
                    "danhSachBienThe": [
                        {
                            "maMauSacId": %d,
                            "tenMau": "Xanh Navy",
                            "maHex": "#112233",
                            "maKichThuocId": %d,
                            "maKichThuoc": "L",
                            "giaBan": 250000,
                            "giaGoc": 300000,
                            "soLuong": 30
                        },
                        {
                            "maMauSacId": %d,
                            "tenMau": "Xám Khói",
                            "maHex": "#445566",
                            "maKichThuocId": %d,
                            "maKichThuoc": "XL",
                            "giaBan": 260000,
                            "giaGoc": 310000,
                            "soLuong": 40
                        }
                    ]
                }
                """.formatted(spTest.getId(), mau1.getId(), size1.getId(), mau2.getId(), size2.getId());

        mockMvc.perform(post("/admin/bien-the/api/them-hang-loat")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", org.hamcrest.Matchers.is(true)))
                .andExpect(jsonPath("$.soLuong", org.hamcrest.Matchers.is(2)));

        // Kiểm tra cơ sở dữ liệu đã có 2 biến thể này
        List<BienTheSanPham> bienTheList = bienTheRepository.findByMaSanPhamIdOrderByIdDesc(spTest.getId());
        assertTrue(bienTheList.size() >= 2, "Hệ thống phải lưu thành công ít nhất 2 biến thể từ ma trận SIMPLAIN");
        for (BienTheSanPham bt : bienTheList) {
            assertNotNull(bt.getMaSku(), "Mã SKU phải được tự sinh tự động");
            assertNotNull(bt.getMaVachBarcode(), "Mã vạch Barcode phải được tự sinh tự động");
        }
    }
}
