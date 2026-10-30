package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.entity.BoSuuTap;
import vn.fpoly.simplain.entity.DanhMuc;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.repository.BoSuuTapRepository;
import vn.fpoly.simplain.repository.DanhMucRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động giao diện Web MVC & Validate chuyên nghiệp cho chức năng
 * Quản lý thông tin chung Sản phẩm (US08).
 *
 * <p>Tiêu chí kiểm thử US08:
 * 1. Tạo san_pham gắn với danh mục (ma_danh_muc_id) và bộ sưu tập tùy chọn (ma_bo_suu_tap_id).
 * 2. Nhập chất liệu (chat_lieu), form dáng (form_dang), mô tả chi tiết (mo_ta_chi_tiet), hướng dẫn chọn size.
 * 3. Validate chặt chẽ các trường bắt buộc, chuẩn hóa mã SP in hoa, chặn trùng lặp mã/tên/slug, chặn giá âm.
 * 4. Kiểm tra tính liên kết giữa Sản phẩm (US08) và Danh mục (US06): Không cho xóa danh mục đang có sản phẩm.
 *
 * <p>Chạy bằng lệnh: .\mvnw.cmd -Dtest=SanPhamControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Quản lý thông tin chung Sản phẩm (US08)")
class SanPhamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private BoSuuTapRepository boSuuTapRepository;

    @Autowired
    private vn.fpoly.simplain.repository.HinhAnhRepository hinhAnhRepository;

    private static final String SLUG_DM_TEST_1 = "dm-san-pham-test-us08-1";
    private static final String SLUG_DM_TEST_2 = "dm-san-pham-test-us08-2";
    private static final String SLUG_BST_TEST = "bst-san-pham-test-us08";

    private static final String MA_SP_TEST_1 = "SP-US08-TEST-01";
    private static final String MA_SP_TEST_2 = "SP-US08-TEST-02";
    private static final String SLUG_SP_TEST_1 = "ao-polo-the-thao-test-us08-01";
    private static final String SLUG_SP_TEST_2 = "ao-thun-oversize-test-us08-02";

    private DanhMuc dmTest1;
    private DanhMuc dmTest2;
    private BoSuuTap bstTest;

    @BeforeEach
    void setUp() {
        donDepDuLieuTest();

        dmTest1 = danhMucRepository.save(DanhMuc.builder()
                .tenDanhMuc("Danh Mục Áo Polo Test US08")
                .duongDanSlug(SLUG_DM_TEST_1)
                .thuTuHienThi(10)
                .kichHoat(true)
                .build());

        dmTest2 = danhMucRepository.save(DanhMuc.builder()
                .tenDanhMuc("Danh Mục Áo Thun Test US08")
                .duongDanSlug(SLUG_DM_TEST_2)
                .thuTuHienThi(11)
                .kichHoat(true)
                .build());

        bstTest = boSuuTapRepository.save(BoSuuTap.builder()
                .tenBoSuuTap("Bộ Sưu Tập Hè Năng Động US08")
                .duongDanSlug(SLUG_BST_TEST)
                .kichHoat(true)
                .build());
    }

    @AfterEach
    void tearDown() {
        donDepDuLieuTest();
    }

    private void donDepDuLieuTest() {
        sanPhamRepository.findByMaSanPhamIgnoreCase(MA_SP_TEST_1).ifPresent(sp -> {
            hinhAnhRepository.deleteByMaSanPhamIdAndMaBienTheIdIsNull(sp.getId());
            sanPhamRepository.delete(sp);
        });
        sanPhamRepository.findByMaSanPhamIgnoreCase(MA_SP_TEST_2).ifPresent(sp -> {
            hinhAnhRepository.deleteByMaSanPhamIdAndMaBienTheIdIsNull(sp.getId());
            sanPhamRepository.delete(sp);
        });
        sanPhamRepository.findByDuongDanSlug(SLUG_SP_TEST_1).ifPresent(sp -> {
            hinhAnhRepository.deleteByMaSanPhamIdAndMaBienTheIdIsNull(sp.getId());
            sanPhamRepository.delete(sp);
        });
        sanPhamRepository.findByDuongDanSlug(SLUG_SP_TEST_2).ifPresent(sp -> {
            hinhAnhRepository.deleteByMaSanPhamIdAndMaBienTheIdIsNull(sp.getId());
            sanPhamRepository.delete(sp);
        });

        danhMucRepository.findByDuongDanSlug(SLUG_DM_TEST_1).ifPresent(danhMucRepository::delete);
        danhMucRepository.findByDuongDanSlug(SLUG_DM_TEST_2).ifPresent(danhMucRepository::delete);
        boSuuTapRepository.findByDuongDanSlug(SLUG_BST_TEST).ifPresent(boSuuTapRepository::delete);
    }

    @Test
    @DisplayName("US08 - Hiển thị danh sách Sản phẩm có phân trang, tìm kiếm từ khóa, lọc theo danh mục & form dáng")
    void hienThiDanhSachSanPham_PhanTrangVaLoc_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/san-pham/hien-thi")
                        .param("page", "0")
                        .param("tuKhoa", "Polo")
                        .param("formDang", "Regular Fit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/san-pham/hien-thi"))
                .andExpect(model().attributeExists(
                        "listSanPham", "listDanhMuc", "listBoSuuTap", "sanPham",
                        "tongSanPham", "sanPhamDangBan", "sanPhamNoiBat"));
    }

    @Test
    @DisplayName("US08 - Tiêu chí 1 & 2: Tạo mới san_pham gắn với danh mục, nhập chất liệu, form dáng, mô tả chi tiết, cập nhật và xóa thành công")
    void crudSanPham_GanDanhMucVaChatLieuFormDangMoTa_ThanhCong() throws Exception {
        // 1. Tạo mới sản phẩm với mã chữ thường (sp-us08-test-01) -> Tự động chuẩn hóa IN HOA
        mockMvc.perform(post("/admin/san-pham/add")
                        .param("maDanhMucId", String.valueOf(dmTest1.getId()))
                        .param("maBoSuuTapId", String.valueOf(bstTest.getId()))
                        .param("maSanPham", "sp-us08-test-01")
                        .param("tenSanPham", "Áo Polo Nam Pique Cotton Đẳng Cấp US08")
                        .param("duongDanSlug", SLUG_SP_TEST_1)
                        .param("chatLieu", "100% Cotton Compact 2 chiều định lượng 230gsm")
                        .param("formDang", "Regular Fit")
                        .param("moTaChiTiet", "Áo Polo nam dệt mắt chim thoáng khí, thấm hút mồ hôi vượt trội, co giãn thoải mái cả ngày.")
                        .param("huongDanChonSize", "Size S (50-58kg) | Size M (59-67kg) | Size L (68-75kg)")
                        .param("giaCoBan", "299000")
                        .param("noiBat", "true")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/san-pham/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        SanPham saved = sanPhamRepository.findByMaSanPhamIgnoreCase(MA_SP_TEST_1).orElseThrow();
        assertEquals(MA_SP_TEST_1, saved.getMaSanPham(), "Mã sản phẩm phải được tự động chuẩn hóa IN HOA");
        assertEquals(dmTest1.getId(), saved.getMaDanhMucId(), "Phải gắn đúng ID danh mục đã chọn");
        assertEquals(bstTest.getId(), saved.getMaBoSuuTapId(), "Phải gắn đúng ID bộ sưu tập đã chọn");
        assertEquals("Áo Polo Nam Pique Cotton Đẳng Cấp US08", saved.getTenSanPham());
        assertEquals("100% Cotton Compact 2 chiều định lượng 230gsm", saved.getChatLieu());
        assertEquals("Regular Fit", saved.getFormDang());
        assertTrue(saved.getMoTaChiTiet().contains("thoáng khí, thấm hút mồ hôi vượt trội"));
        assertFalse(saved.getTenSanPham().contains("?"), "Không được lỗi phông chữ tiếng Việt");
        assertFalse(saved.getChatLieu().contains("?"), "Chất liệu không được lỗi phông chữ tiếng Việt");
        assertFalse(saved.getMoTaChiTiet().contains("?"), "Mô tả chi tiết không được lỗi phông chữ tiếng Việt");
        assertEquals(0, new BigDecimal("299000").compareTo(saved.getGiaCoBan()));
        assertTrue(saved.getNoiBat());
        assertTrue(saved.getKichHoat());

        // 2. Cập nhật thông tin sản phẩm sang Danh mục 2, đổi Chất liệu, Form dáng và Mô tả chi tiết
        mockMvc.perform(post("/admin/san-pham/update")
                        .param("id", String.valueOf(saved.getId()))
                        .param("maDanhMucId", String.valueOf(dmTest2.getId()))
                        .param("maSanPham", MA_SP_TEST_1)
                        .param("tenSanPham", "Áo Thun Nam Excool Phiên Bản Mới US08")
                        .param("duongDanSlug", SLUG_SP_TEST_2)
                        .param("chatLieu", "Vải Excool Công nghệ Làm Mát Chống UV")
                        .param("formDang", "Slim Fit")
                        .param("moTaChiTiet", "Mô tả đã cập nhật: Công nghệ dệt sợi Sorona siêu nhẹ, làm mát tức thì khi vận động.")
                        .param("huongDanChonSize", "Size M (59-67kg) | Size L (68-75kg) | Size XL (76-84kg)")
                        .param("giaCoBan", "349000")
                        .param("noiBat", "false")
                        .param("kichHoat", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/san-pham/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        SanPham updated = sanPhamRepository.findById(saved.getId()).orElseThrow();
        assertEquals(dmTest2.getId(), updated.getMaDanhMucId());
        assertNull(updated.getMaBoSuuTapId());
        assertEquals("Áo Thun Nam Excool Phiên Bản Mới US08", updated.getTenSanPham());
        assertEquals("Vải Excool Công nghệ Làm Mát Chống UV", updated.getChatLieu());
        assertEquals("Slim Fit", updated.getFormDang());
        assertTrue(updated.getMoTaChiTiet().contains("làm mát tức thì"));
        assertEquals(0, new BigDecimal("349000").compareTo(updated.getGiaCoBan()));

        // 3. Đổi nhanh trạng thái Nổi bật và Trạng thái kinh doanh
        mockMvc.perform(get("/admin/san-pham/doi-noi-bat")
                        .param("id", String.valueOf(saved.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/san-pham/hien-thi"));
        assertTrue(sanPhamRepository.findById(saved.getId()).orElseThrow().getNoiBat());

        mockMvc.perform(get("/admin/san-pham/doi-trang-thai")
                        .param("id", String.valueOf(saved.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/san-pham/hien-thi"));
        assertFalse(sanPhamRepository.findById(saved.getId()).orElseThrow().getKichHoat());

        // 4. Xóa sản phẩm
        mockMvc.perform(get("/admin/san-pham/delete")
                        .param("id", String.valueOf(saved.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/san-pham/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        assertFalse(sanPhamRepository.existsById(saved.getId()));
    }

    @Test
    @DisplayName("US08 - Validate Form & Trùng lặp: Chặn thiếu danh mục/chất liệu/form dáng/mô tả, giá âm và chặn trùng mã/tên/slug")
    void validateSanPham_ThieuTruongBatBuocVaTrungLap() throws Exception {
        // 1. Validate Bean Validation khi bỏ trống danh mục, chất liệu, form dáng, mô tả quá ngắn và giá âm
        mockMvc.perform(post("/admin/san-pham/add")
                        .param("maSanPham", "SP INVALID @")
                        .param("tenSanPham", "")
                        .param("duongDanSlug", "SLUG SAI DINH DANG!")
                        .param("chatLieu", "")
                        .param("formDang", "")
                        .param("moTaChiTiet", "Ngắn")
                        .param("giaCoBan", "-50000"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/san-pham/hien-thi"))
                .andExpect(model().attributeHasFieldErrors(
                        "sanPham",
                        "maDanhMucId", "maSanPham", "tenSanPham", "duongDanSlug",
                        "chatLieu", "formDang", "moTaChiTiet", "giaCoBan"));

        // 2. Tạo 1 sản phẩm hợp lệ rồi kiểm tra chặn trùng Mã SP, Tên SP, Slug
        sanPhamRepository.save(SanPham.builder()
                .maDanhMucId(dmTest1.getId())
                .maSanPham(MA_SP_TEST_1)
                .tenSanPham("Sản Phẩm Gốc Kiểm Tra Trùng US08")
                .duongDanSlug(SLUG_SP_TEST_1)
                .chatLieu("100% Cotton Compact")
                .formDang("Regular Fit")
                .moTaChiTiet("Mô tả chi tiết sản phẩm gốc phục vụ kiểm thử chống trùng lặp dữ liệu.")
                .giaCoBan(new BigDecimal("250000"))
                .noiBat(false)
                .kichHoat(true)
                .build());

        // 2a. Thử thêm trùng Mã sản phẩm
        mockMvc.perform(post("/admin/san-pham/add")
                        .param("maDanhMucId", String.valueOf(dmTest1.getId()))
                        .param("maSanPham", MA_SP_TEST_1)
                        .param("tenSanPham", "Sản Phẩm Tên Khác")
                        .param("duongDanSlug", SLUG_SP_TEST_2)
                        .param("chatLieu", "100% Cotton")
                        .param("formDang", "Slim Fit")
                        .param("moTaChiTiet", "Mô tả chi tiết hợp lệ trên 10 ký tự cho sản phẩm test.")
                        .param("giaCoBan", "250000"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/san-pham/hien-thi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("Mã sản phẩm")));

        // 2b. Thử thêm trùng Tên sản phẩm
        mockMvc.perform(post("/admin/san-pham/add")
                        .param("maDanhMucId", String.valueOf(dmTest1.getId()))
                        .param("maSanPham", MA_SP_TEST_2)
                        .param("tenSanPham", "Sản Phẩm Gốc Kiểm Tra Trùng US08")
                        .param("duongDanSlug", SLUG_SP_TEST_2)
                        .param("chatLieu", "100% Cotton")
                        .param("formDang", "Slim Fit")
                        .param("moTaChiTiet", "Mô tả chi tiết hợp lệ trên 10 ký tự cho sản phẩm test.")
                        .param("giaCoBan", "250000"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/san-pham/hien-thi"))
                .andExpect(model().attribute("thongBaoLoi", containsString("Tên sản phẩm")));
    }

    @Test
    @DisplayName("US08 <-> US06 Liên kết dữ liệu: Chặn xóa Danh mục khi đang có Sản phẩm gắn vào danh mục đó")
    void chanXoaDanhMucKhiDangCoSanPham() throws Exception {
        SanPham sp = sanPhamRepository.save(SanPham.builder()
                .maDanhMucId(dmTest1.getId())
                .maSanPham(MA_SP_TEST_1)
                .tenSanPham("Sản Phẩm Gắn Chặt Danh Mục US08")
                .duongDanSlug(SLUG_SP_TEST_1)
                .chatLieu("100% Cotton Compact")
                .formDang("Regular Fit")
                .moTaChiTiet("Sản phẩm đang gắn vào danh mục dmTest1 để kiểm tra khóa bảo vệ khi xóa danh mục.")
                .giaCoBan(new BigDecimal("199000"))
                .kichHoat(true)
                .build());

        // Thử xóa Danh mục dmTest1 khi đang có sản phẩm sp -> Phải bị chặn!
        mockMvc.perform(get("/admin/danh-muc/delete")
                        .param("id", String.valueOf(dmTest1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/danh-muc/hien-thi"))
                .andExpect(flash().attribute("thongBaoLoi", containsString("sản phẩm thuộc danh mục này")));

        assertTrue(danhMucRepository.existsById(dmTest1.getId()), "Danh mục đang có sản phẩm không được phép bị xóa");

        // Sau khi xóa sản phẩm sp thì danh mục dmTest1 có thể xóa bình thường
        sanPhamRepository.deleteById(sp.getId());
    }

    @Test
    @DisplayName("Quản lý Album ảnh Sản phẩm: Upload ảnh bìa & ảnh phụ, chọn ảnh bìa và xóa ảnh tự động đôn ảnh")
    void quanLyAlbumAnhSanPham_UploadVaChonAnhBiaVaXoaAnh() throws Exception {
        SanPham sp = sanPhamRepository.save(SanPham.builder()
                .maDanhMucId(dmTest1.getId())
                .maSanPham(MA_SP_TEST_1)
                .tenSanPham("Sản Phẩm Test Quản Lý Album Ảnh")
                .duongDanSlug(SLUG_SP_TEST_1)
                .chatLieu("100% Cotton Compact")
                .formDang("Regular Fit")
                .moTaChiTiet("Mô tả sản phẩm test quản lý hình ảnh sàn TMĐT SIMPLAIN.")
                .giaCoBan(new BigDecimal("299000"))
                .kichHoat(true)
                .build());

        // 1. Tải lên 2 ảnh qua API REST
        String url1 = "https://images.unsplash.com/photo-sp-1";
        String url2 = "https://images.unsplash.com/photo-sp-2";

        mockMvc.perform(post("/admin/san-pham/api/upload-anh")
                        .param("sanPhamId", String.valueOf(sp.getId()))
                        .param("duongDanAnhUrl", url1 + "," + url2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.danhSachHinhAnh.length()").value(2));

        var dsAnh = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sp.getId());
        assertEquals(2, dsAnh.size());
        assertTrue(dsAnh.get(0).getLaAnhDaiDien(), "Ảnh đầu tiên phải được tự động làm ảnh bìa");
        assertFalse(dsAnh.get(1).getLaAnhDaiDien(), "Ảnh thứ hai là ảnh phụ");

        Long idAnhBiaCu = dsAnh.get(0).getId();
        Long idAnhPhu = dsAnh.get(1).getId();

        // 2. Đặt ảnh phụ thứ hai làm ảnh bìa chính
        mockMvc.perform(post("/admin/san-pham/api/chon-anh-dai-dien")
                        .param("sanPhamId", String.valueOf(sp.getId()))
                        .param("hinhAnhId", String.valueOf(idAnhPhu)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        var anhMoi = hinhAnhRepository.findById(idAnhPhu).orElseThrow();
        assertTrue(anhMoi.getLaAnhDaiDien(), "Ảnh thứ hai giờ phải là ảnh bìa chính");

        var anhCu = hinhAnhRepository.findById(idAnhBiaCu).orElseThrow();
        assertFalse(anhCu.getLaAnhDaiDien(), "Ảnh bìa cũ giờ trở thành ảnh phụ");

        // 3. Xóa ảnh bìa chính -> Ảnh phụ còn lại phải được tự động đôn lên làm ảnh bìa
        mockMvc.perform(post("/admin/san-pham/api/xoa-anh")
                        .param("sanPhamId", String.valueOf(sp.getId()))
                        .param("hinhAnhId", String.valueOf(idAnhPhu)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.danhSachHinhAnh.length()").value(1));

        var anhConLai = hinhAnhRepository.findById(idAnhBiaCu).orElseThrow();
        assertTrue(anhConLai.getLaAnhDaiDien(), "Ảnh còn lại phải tự động được đôn lên làm ảnh bìa chính");

        // Dọn dẹp
        hinhAnhRepository.deleteByMaSanPhamIdAndMaBienTheIdIsNull(sp.getId());
        sanPhamRepository.deleteById(sp.getId());
    }
}
