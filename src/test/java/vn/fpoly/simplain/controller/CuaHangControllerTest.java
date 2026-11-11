package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động giao diện Web MVC & Validate chuyên nghiệp cho chức năng
 * Storefront: Tìm kiếm và lọc danh sách sản phẩm cho Khách hàng (US10 - Mod 2 / Storefront).
 *
 * <p>Tiêu chí kiểm thử US10:
 * 1. Hiển thị Grid sản phẩm (chỉ hiển thị sản phẩm đang mở bán, làm giàu ảnh đại diện, màu sắc, size, tồn kho).
 * 2. Bộ lọc Filter theo Giá (giaTu, giaDen, mucGia), Màu sắc (maMauSacId), Size (maKichThuocId), Danh mục (Cha/Con).
 * 3. Tìm kiếm text theo từ khóa (tuKhoa) và Validate khoảng giá không hợp lệ.
 *
 * <p>Chạy bằng lệnh: .\mvnw.cmd -Dtest=CuaHangControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Web MVC Storefront Tìm kiếm & Lọc danh sách sản phẩm (US10)")
class CuaHangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichThuocRepository kichThuocRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    private static final String SLUG_DM_CHA = "dm-cha-us10-test";
    private static final String SLUG_DM_CON = "dm-con-us10-test";

    private static final String MA_SP_1 = "SP-US10-POLO-01";
    private static final String MA_SP_2 = "SP-US10-SHORT-02";
    private static final String MA_SP_AN = "SP-US10-HIDDEN-03";

    private static final String MA_SKU_1 = "SKU-US10-POLO-01-NAVY-L";
    private static final String MA_SKU_2 = "SKU-US10-SHORT-02-BLACK-M";

    private static final String HEX_MAU_1 = "#1E40AF";
    private static final String HEX_MAU_2 = "#111827";
    private static final String CODE_SIZE_1 = "L-US10";
    private static final String CODE_SIZE_2 = "M-US10";

    private Long dmChaId;
    private Long dmConId;
    private Long sp1Id;
    private Long sp2Id;
    private Long mau1Id;
    private Long mau2Id;
    private Long size1Id;
    private Long size2Id;

    @BeforeEach
    void setUp() {
        donDepDuLieuTest();

        DanhMuc dmCha = danhMucRepository.save(DanhMuc.builder()
                .tenDanhMuc("Danh mục Cha US10 Test")
                .duongDanSlug(SLUG_DM_CHA)
                .thuTuHienThi(1)
                .kichHoat(true)
                .build());
        dmChaId = dmCha.getId();

        DanhMuc dmCon = danhMucRepository.save(DanhMuc.builder()
                .maDanhMucCha(dmChaId)
                .tenDanhMuc("Danh mục Con US10 Test")
                .duongDanSlug(SLUG_DM_CON)
                .thuTuHienThi(2)
                .kichHoat(true)
                .build());
        dmConId = dmCon.getId();

        MauSac mau1 = mauSacRepository.save(MauSac.builder()
                .maMau(HEX_MAU_1)
                .tenMau("Xanh Navy US10 Test")
                .build());
        mau1Id = mau1.getId();

        MauSac mau2 = mauSacRepository.save(MauSac.builder()
                .maMau(HEX_MAU_2)
                .tenMau("Đen Graphite US10 Test")
                .build());
        mau2Id = mau2.getId();

        KichThuoc size1 = kichThuocRepository.save(KichThuoc.builder()
                .maKichThuoc(CODE_SIZE_1)
                .tenKichThuoc("Size L US10 Test")
                .build());
        size1Id = size1.getId();

        KichThuoc size2 = kichThuocRepository.save(KichThuoc.builder()
                .maKichThuoc(CODE_SIZE_2)
                .tenKichThuoc("Size M US10 Test")
                .build());
        size2Id = size2.getId();

        // Sản phẩm 1: Gắn với Danh mục Con, giá 280.000đ, Màu 1 + Size 1
        SanPham sp1 = sanPhamRepository.save(SanPham.builder()
                .maDanhMucId(dmConId)
                .maSanPham(MA_SP_1)
                .tenSanPham("Áo Polo US10 Coolmax Kháng Khuẩn")
                .duongDanSlug("ao-polo-us10-coolmax-khang-khuan")
                .chatLieu("Sợi Coolmax Thoáng Khí")
                .formDang("Regular Fit")
                .moTaChiTiet("Mô tả chi tiết áo polo US10 kháng khuẩn và thấm hút mồ hôi vượt trội.")
                .giaCoBan(new BigDecimal("280000"))
                .noiBat(true)
                .kichHoat(true)
                .build());
        sp1Id = sp1.getId();

        BienTheSanPham bt1 = bienTheSanPhamRepository.save(BienTheSanPham.builder()
                .maSanPhamId(sp1Id)
                .maMauSacId(mau1Id)
                .maKichThuocId(size1Id)
                .maSku(MA_SKU_1)
                .maVachBarcode("BC-" + MA_SKU_1)
                .soLuong(35)
                .giaBan(new BigDecimal("280000"))
                .giaGoc(new BigDecimal("170000"))
                .kichHoat(true)
                .build());

        hinhAnhRepository.save(HinhAnh.builder()
                .maSanPhamId(sp1Id)
                .maBienTheId(bt1.getId())
                .duongDanAnh("https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=500")
                .laAnhDaiDien(true)
                .thuTu(1)
                .build());

        // Sản phẩm 2: Gắn với Danh mục Cha, giá 165.000đ, Màu 2 + Size 2
        SanPham sp2 = sanPhamRepository.save(SanPham.builder()
                .maDanhMucId(dmChaId)
                .maSanPham(MA_SP_2)
                .tenSanPham("Quần Short US10 Thể Thao Siêu Nhẹ")
                .duongDanSlug("quan-short-us10-the-thao-sieu-nhe")
                .chatLieu("Polyester Co Giãn 4 Chiều")
                .formDang("Sport Fit")
                .moTaChiTiet("Quần short chạy bộ US10 siêu nhẹ có túi khóa kéo chống nước.")
                .giaCoBan(new BigDecimal("165000"))
                .noiBat(false)
                .kichHoat(true)
                .build());
        sp2Id = sp2.getId();

        bienTheSanPhamRepository.save(BienTheSanPham.builder()
                .maSanPhamId(sp2Id)
                .maMauSacId(mau2Id)
                .maKichThuocId(size2Id)
                .maSku(MA_SKU_2)
                .maVachBarcode("BC-" + MA_SKU_2)
                .soLuong(20)
                .giaBan(new BigDecimal("165000"))
                .giaGoc(new BigDecimal("100000"))
                .kichHoat(true)
                .build());

        // Sản phẩm 3: Đã ngừng kinh doanh (kichHoat = false) -> Không được xuất hiện trên Storefront
        sanPhamRepository.save(SanPham.builder()
                .maDanhMucId(dmChaId)
                .maSanPham(MA_SP_AN)
                .tenSanPham("Sản phẩm Ngừng Bán US10")
                .duongDanSlug("san-pham-ngung-ban-us10")
                .chatLieu("Cotton")
                .formDang("Slim Fit")
                .moTaChiTiet("Sản phẩm đã ngừng kinh doanh không hiển thị trên Storefront.")
                .giaCoBan(new BigDecimal("199000"))
                .noiBat(false)
                .kichHoat(false)
                .build());
    }

    @AfterEach
    void tearDown() {
        donDepDuLieuTest();
    }

    private void donDepDuLieuTest() {
        for (String sku : new String[]{MA_SKU_1, MA_SKU_2}) {
            bienTheSanPhamRepository.findByMaSkuIgnoreCase(sku).ifPresent(bt -> {
                jdbcTemplate.update("DELETE FROM chi_tiet_gio_hang WHERE ma_bien_the_id = ?", bt.getId());
                jdbcTemplate.update("DELETE FROM chi_tiet_flash_sale WHERE ma_bien_the_id = ?", bt.getId());
                hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(bt.getId())
                        .forEach(hinhAnhRepository::delete);
                bienTheSanPhamRepository.delete(bt);
            });
        }
        for (String maSp : new String[]{MA_SP_1, MA_SP_2, MA_SP_AN}) {
            sanPhamRepository.findByMaSanPhamIgnoreCase(maSp).ifPresent(sp -> {
                hinhAnhRepository.findByMaSanPhamIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(sp.getId())
                        .forEach(hinhAnhRepository::delete);
                bienTheSanPhamRepository.findByMaSanPhamIdOrderByIdDesc(sp.getId())
                        .forEach(bt -> {
                            jdbcTemplate.update("DELETE FROM chi_tiet_gio_hang WHERE ma_bien_the_id = ?", bt.getId());
                            jdbcTemplate.update("DELETE FROM chi_tiet_flash_sale WHERE ma_bien_the_id = ?", bt.getId());
                            bienTheSanPhamRepository.delete(bt);
                        });
                sanPhamRepository.delete(sp);
            });
        }
        danhMucRepository.findByDuongDanSlug(SLUG_DM_CON).ifPresent(danhMucRepository::delete);
        danhMucRepository.findByDuongDanSlug(SLUG_DM_CHA).ifPresent(danhMucRepository::delete);
        mauSacRepository.findByMaMauIgnoreCase(HEX_MAU_1).ifPresent(mauSacRepository::delete);
        mauSacRepository.findByMaMauIgnoreCase(HEX_MAU_2).ifPresent(mauSacRepository::delete);
        kichThuocRepository.findByMaKichThuocIgnoreCase(CODE_SIZE_1).ifPresent(kichThuocRepository::delete);
        kichThuocRepository.findByMaKichThuocIgnoreCase(CODE_SIZE_2).ifPresent(kichThuocRepository::delete);
    }

    @Test
    @DisplayName("1. Hiển thị Grid sản phẩm trên Storefront (/cua-hang) & ẩn sản phẩm ngừng kinh doanh (US10)")
    void testHienThiGridSanPhamStorefront() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cua-hang"));

        mockMvc.perform(get("/cua-hang").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/cua-hang"))
                .andExpect(model().attributeExists("danhSachSanPham", "danhSachDanhMuc", "danhSachMauSac", "danhSachKichThuoc"))
                .andExpect(content().string(containsString("Áo Polo US10 Coolmax Kháng Khuẩn")))
                .andExpect(content().string(containsString("Quần Short US10 Thể Thao Siêu Nhẹ")))
                .andExpect(content().string(not(containsString("Sản phẩm Ngừng Bán US10"))));
    }

    @Test
    @DisplayName("2. Tìm kiếm text theo từ khóa tên sản phẩm, chất liệu hoặc mã SKU (US10)")
    void testTimKiemTextStorefront() throws Exception {
        // Tìm theo từ khóa chất liệu "Coolmax" -> Chỉ trả về Sản phẩm 1
        mockMvc.perform(get("/cua-hang")
                        .param("tuKhoa", "Coolmax"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/cua-hang"))
                .andExpect(content().string(containsString("Áo Polo US10 Coolmax Kháng Khuẩn")))
                .andExpect(content().string(not(containsString("Quần Short US10 Thể Thao Siêu Nhẹ"))));

        // Tìm theo mã SKU biến thể -> Chỉ trả về Sản phẩm 2
        mockMvc.perform(get("/cua-hang")
                        .param("tuKhoa", MA_SKU_2))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Quần Short US10 Thể Thao Siêu Nhẹ")))
                .andExpect(content().string(not(containsString("Áo Polo US10 Coolmax Kháng Khuẩn"))));
    }

    @Test
    @DisplayName("3. Bộ lọc kết hợp theo Danh mục (Cha/Con), Khoảng giá, Màu sắc và Kích thước/Size (US10)")
    void testBoLocTheoGiaMauSizeDanhMuc() throws Exception {
        // 3a. Lọc theo Danh mục Cha -> Tự động lấy cả SP thuộc Danh mục Cha (SP2) và Danh mục Con (SP1)
        mockMvc.perform(get("/cua-hang")
                        .param("maDanhMucId", String.valueOf(dmChaId)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Áo Polo US10 Coolmax Kháng Khuẩn")))
                .andExpect(content().string(containsString("Quần Short US10 Thể Thao Siêu Nhẹ")));

        // 3b. Lọc theo Danh mục Con -> Chỉ lấy SP1
        mockMvc.perform(get("/cua-hang")
                        .param("maDanhMucId", String.valueOf(dmConId)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Áo Polo US10 Coolmax Kháng Khuẩn")))
                .andExpect(content().string(not(containsString("Quần Short US10 Thể Thao Siêu Nhẹ"))));

        // 3c. Lọc theo Màu sắc 1 + Size 1 -> Chỉ lấy SP1
        mockMvc.perform(get("/cua-hang")
                        .param("maMauSacId", String.valueOf(mau1Id))
                        .param("maKichThuocId", String.valueOf(size1Id)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Áo Polo US10 Coolmax Kháng Khuẩn")))
                .andExpect(content().string(not(containsString("Quần Short US10 Thể Thao Siêu Nhẹ"))));

        // 3d. Lọc theo Khoảng giá (250.000đ -> 300.000đ) kết hợp Danh mục Cha -> Chỉ lấy SP1 (280.000đ), loại SP2 (165.000đ)
        mockMvc.perform(get("/cua-hang")
                        .param("maDanhMucId", String.valueOf(dmChaId))
                        .param("giaTu", "250000")
                        .param("giaDen", "300000"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Áo Polo US10 Coolmax Kháng Khuẩn")))
                .andExpect(content().string(not(containsString("Quần Short US10 Thể Thao Siêu Nhẹ"))));

        // 3e. Lọc theo mức giá nhanh "duoi-200k" kết hợp Danh mục Cha -> Chỉ lấy SP2 (165.000đ)
        mockMvc.perform(get("/cua-hang")
                        .param("maDanhMucId", String.valueOf(dmChaId))
                        .param("mucGia", "duoi-200k"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Quần Short US10 Thể Thao Siêu Nhẹ")))
                .andExpect(content().string(not(containsString("Áo Polo US10 Coolmax Kháng Khuẩn"))));
    }

    @Test
    @DisplayName("4. Validate khoảng giá lọc không hợp lệ (Giá từ > Giá đến hoặc Giá âm) hiển thị cảnh báo rõ ràng (US10)")
    void testValidateKhoangGiaKhongHopLe() throws Exception {
        // Trường hợp Giá từ > Giá đến
        mockMvc.perform(get("/cua-hang")
                        .param("giaTu", "500000")
                        .param("giaDen", "100000"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/cua-hang"))
                .andExpect(model().attributeExists("loiLoc"))
                .andExpect(content().string(containsString("Khoảng giá lọc không hợp lệ")));

        // Trường hợp Giá âm
        mockMvc.perform(get("/cua-hang")
                        .param("giaTu", "-50000"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/cua-hang"))
                .andExpect(model().attributeExists("loiLoc"))
                .andExpect(content().string(containsString("không được nhỏ hơn 0 VNĐ")));
    }

    @Test
    @DisplayName("5. Hiển thị mục Voucher hot và Hạng người dùng lên trang chủ Storefront")
    void testHienThiVoucherVaHangNguoiDungTrangChu() throws Exception {
        mockMvc.perform(get("/cua-hang"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/cua-hang"))
                .andExpect(model().attributeExists("dsVoucherHot"))
                .andExpect(content().string(containsString("Mã Giảm Giá")))
                .andExpect(content().string(containsString("Hạng")));
    }
}
