package vn.fpoly.simplain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import vn.fpoly.simplain.config.CatalogAndCarrierDataConfig;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DanhMucVaSanPhamChuyenNghiepTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CatalogAndCarrierDataConfig catalogAndCarrierDataConfig;

    @Test
    @DisplayName("Kiểm tra khởi tạo 5 Danh mục Cha và 19 Danh mục Con chuẩn chỉnh")
    void testCayDanhMucChuan() {
        // Chạy khởi tạo
        assertDoesNotThrow(() -> catalogAndCarrierDataConfig.run());

        // 1. Kiểm tra 5 Danh mục Cha
        List<String> chaSlugs = List.of("thoi-trang-nam", "thoi-trang-nu", "giay-dep", "vi-va-balo", "phu-kien");
        for (String slug : chaSlugs) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [danh_muc] WHERE [duong_dan_slug] = ? AND [ma_danh_muc_cha] IS NULL",
                    Integer.class, slug);
            assertNotNull(count);
            assertTrue(count > 0, "Thiếu danh mục cha: " + slug);
        }

        // 2. Kiểm tra 19 Danh mục Con
        List<String> conSlugs = List.of(
                // Nam
                "ao-nam", "quan-nam", "ao-khoac-nam",
                // Nữ
                "ao-nu", "vay-nu", "dam-nu", "quan-nu",
                // Giày dép
                "sneaker", "sandal", "dep",
                // Ví và balo
                "tui-xach", "balo", "vi",
                // Phụ kiện
                "dong-ho", "kinh", "mu", "that-lung", "trang-suc", "khan-choang"
        );

        for (String slug : conSlugs) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [danh_muc] WHERE [duong_dan_slug] = ? AND [ma_danh_muc_cha] IS NOT NULL",
                    Integer.class, slug);
            assertNotNull(count);
            assertTrue(count > 0, "Thiếu danh mục con: " + slug);
        }

        assertEquals(19, conSlugs.size(), "Số lượng danh mục con phải chính xác là 19");

        // 3. Kiểm tra sản phẩm có đầy đủ thuộc tính chuyên nghiệp
        List<Map<String, Object>> spList = jdbcTemplate.queryForList(
                "SELECT ma_san_pham, ten_san_pham, chat_lieu, form_dang, huong_dan_chon_size, mo_ta_chi_tiet, gia_co_ban " +
                "FROM [san_pham] WHERE [ma_san_pham] LIKE 'SP-%'");

        assertFalse(spList.isEmpty(), "Danh sách sản phẩm mẫu không được rỗng");
        for (Map<String, Object> sp : spList) {
            assertNotNull(sp.get("chat_lieu"), "Chất liệu không được null cho SP: " + sp.get("ma_san_pham"));
            assertNotNull(sp.get("form_dang"), "Form dáng không được null cho SP: " + sp.get("ma_san_pham"));
            assertNotNull(sp.get("huong_dan_chon_size"), "Hướng dẫn size không được null cho SP: " + sp.get("ma_san_pham"));
            assertNotNull(sp.get("mo_ta_chi_tiet"), "Mô tả chi tiết không được null cho SP: " + sp.get("ma_san_pham"));
        }

        // 4. Kiểm tra tồn tại biến thể SKU cho các sản phẩm
        Integer countBienThe = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM [bien_the_san_pham] WHERE [ma_sku] LIKE 'SP-%'", Integer.class);
        assertNotNull(countBienThe);
        assertTrue(countBienThe >= 19, "Tổng số biến thể SKU phải >= 19");
    }
}
