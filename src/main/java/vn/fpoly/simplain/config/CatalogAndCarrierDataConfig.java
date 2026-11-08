package vn.fpoly.simplain.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Cấu hình tự động khởi tạo và đồng bộ Danh mục chuẩn (8 Cha, 32 Con) theo yêu cầu cửa hàng thời trang:
 * 1. Áo: Áo thun, Áo polo, Áo sơ mi, Áo khoác.
 * 2. Quần: Quần jean, Quần kaki, Quần tây, Quần short.
 * 3. Trang phục nữ: Váy, Đầm, Chân váy, Áo kiểu.
 * 4. Trang phục khác: Đồ bộ, Hoodie, Sweater, Đồ thể thao.
 * 5. Túi và ví: Túi đeo chéo, Balo, Túi xách, Ví.
 * 6. Trang sức: Đồng hồ, Vòng tay, Dây chuyền, Nhẫn.
 * 7. Phụ kiện cá nhân: Mũ, Thắt lưng, Kính mắt, Khăn choàng.
 * 8. Giày dép: Sneaker, Sandal, Dép, Giày da.
 *
 * Đồng thời khởi tạo trạng thái [HANG_THAT_LAC], màu sắc, kích thước và sản phẩm mẫu đầy đủ thuộc tính chuyên nghiệp.
 */
@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class CatalogAndCarrierDataConfig implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("Bắt đầu đồng bộ danh mục chuẩn 5 Cha, 19 Con và thuộc tính sản phẩm chuyên nghiệp...");

            // 1. Khởi tạo trạng thái Đơn hàng thất lạc do ĐVVC nếu chưa có
            try {
                khoiTaoTrangThaiDonHang();
            } catch (Exception ex) {
                log.warn("Cảnh báo khởi tạo trạng thái đơn hàng: {}", ex.getMessage());
            }

            // 2. Bổ sung Kích thước và Màu sắc thời trang
            try {
                khoiTaoThuocTinhKichThuocVaMauSac();
            } catch (Exception ex) {
                log.warn("Cảnh báo khởi tạo kích thước/màu sắc: {}", ex.getMessage());
            }

            // 3. Khởi tạo 5 Danh mục Cha và 19 Danh mục Con chuẩn chỉnh
            try {
                khoiTaoCayDanhMucChuan();
            } catch (Exception ex) {
                log.error("Lỗi khởi tạo cây danh mục: {}", ex.getMessage(), ex);
            }

            // 4. Khởi tạo 19 Sản phẩm mẫu chuẩn cho các danh mục mới với đầy đủ thuộc tính
            try {
                khoiTaoSanPhamMauChoCacDanhMuc();
            } catch (Exception ex) {
                log.error("Lỗi khởi tạo sản phẩm mẫu: {}", ex.getMessage(), ex);
            }

            log.info("Hoàn tất đồng bộ danh mục 5 Cha, 19 Con và sản phẩm mẫu thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi đồng bộ danh mục và sản phẩm mẫu: {}", e.getMessage(), e);
        }
    }

    private void khoiTaoTrangThaiDonHang() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM [trang_thai_don_hang] WHERE [ma_trang_thai] = 'HANG_THAT_LAC'", Integer.class);
        if (count != null && count == 0) {
            jdbcTemplate.execute(
                    "INSERT INTO [trang_thai_don_hang] ([ma_trang_thai], [ten_trang_thai], [ma_mau_hex]) " +
                    "VALUES ('HANG_THAT_LAC', N'Hàng thất lạc', '#DC2626')");
            log.info("Đã bổ sung trạng thái [HANG_THAT_LAC] vào bảng trang_thai_don_hang.");
        } else {
            jdbcTemplate.execute(
                    "UPDATE [trang_thai_don_hang] SET [ten_trang_thai] = N'Hàng thất lạc', [ma_mau_hex] = '#DC2626' " +
                    "WHERE [ma_trang_thai] = 'HANG_THAT_LAC'");
        }

        Integer countPartial = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM [trang_thai_don_hang] WHERE [ma_trang_thai] = 'THAT_LAC_MOT_PHAN'", Integer.class);
        if (countPartial != null && countPartial == 0) {
            jdbcTemplate.execute(
                    "INSERT INTO [trang_thai_don_hang] ([ma_trang_thai], [ten_trang_thai], [ma_mau_hex]) " +
                    "VALUES ('THAT_LAC_MOT_PHAN', N'Thất lạc một phần hàng', '#EA580C')");
            log.info("Đã bổ sung trạng thái [THAT_LAC_MOT_PHAN] vào bảng trang_thai_don_hang.");
        } else {
            jdbcTemplate.execute(
                    "UPDATE [trang_thai_don_hang] SET [ten_trang_thai] = N'Thất lạc một phần hàng', [ma_mau_hex] = '#EA580C' " +
                    "WHERE [ma_trang_thai] = 'THAT_LAC_MOT_PHAN'");
        }
    }

    private void khoiTaoThuocTinhKichThuocVaMauSac() {
        // 1. Bổ sung Kích thước chuẩn cho các ngành hàng thời trang
        String[][] sizes = {
                // Size chữ áo / đầm / thể thao
                {"XS", "Size XS"}, {"S", "Size S"}, {"M", "Size M"}, {"L", "Size L"},
                {"XL", "Size XL"}, {"2XL", "Size 2XL"}, {"3XL", "Size 3XL"}, {"Freesize", "Freesize"},
                // Size số quần nam / nữ
                {"28", "Size 28"}, {"29", "Size 29"}, {"30", "Size 30"}, {"31", "Size 31"},
                {"32", "Size 32"}, {"33", "Size 33"}, {"34", "Size 34"},
                // Size số giày dép (nữ & nam)
                {"35", "Size 35"}, {"36", "Size 36"}, {"37", "Size 37"}, {"38", "Size 38"},
                {"39", "Size 39"}, {"40", "Size 40"}, {"41", "Size 41"}, {"42", "Size 42"},
                {"43", "Size 43"}, {"44", "Size 44"},
                // Size túi, ví, balo
                {"TIEU-CHUAN", "Tiêu chuẩn"}, {"MINI", "Mini"}, {"MEDIUM", "Medium"}, {"LARGE", "Large"},
                // Size trang sức & phụ kiện
                {"MAT-38MM", "Mặt 38mm"}, {"MAT-40MM", "Mặt 40mm"}, {"MAT-42MM", "Mặt 42mm"},
                {"NI-16", "Ni 16"}, {"NI-17", "Ni 17"}, {"NI-18", "Ni 18"},
                {"DAI-115CM", "Dài 115cm"}, {"DAI-120CM", "Dài 120cm"}, {"DAI-125CM", "Dài 125cm"}
        };
        for (String[] s : sizes) {
            try {
                Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM [kich_thuoc] WHERE [ma_kich_thuoc] = ? OR [ten_kich_thuoc] = ?",
                        Integer.class, s[0], s[1]);
                if (count != null && count == 0) {
                    jdbcTemplate.update(
                            "INSERT INTO [kich_thuoc] ([ma_kich_thuoc], [ten_kich_thuoc], [kich_hoat]) VALUES (?, ?, 1)",
                            s[0], s[1]);
                }
            } catch (Exception ex) {
                log.warn("Bỏ qua kích thước [{}]: {}", s[0], ex.getMessage());
            }
        }

        // 2. Bổ sung Màu sắc thời trang
        Object[][] colors = {
                {"#000000", "Đen"},
                {"#FFFFFF", "Trắng"},
                {"#1E3A8A", "Xanh Navy"},
                {"#64748B", "Xám Tiêu"},
                {"#8B4513", "Nâu Da Bò"},
                {"#C0C0C0", "Bạc Sang Trọng"},
                {"#FFD700", "Vàng Ánh Kim"},
                {"#F472B6", "Hồng Pastel"},
                {"#D2B48C", "Be Vintage"},
                {"#047857", "Xanh Rêu"},
                {"#B91C1C", "Đỏ Đô"}
        };
        for (Object[] c : colors) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [mau_sac] WHERE [ma_mau] = ?", Integer.class, c[0]);
            if (count != null && count == 0) {
                jdbcTemplate.update(
                        "INSERT INTO [mau_sac] ([ma_mau], [ten_mau]) VALUES (?, ?)", c[0], c[1]);
            }
        }

        // 3. Bổ sung Chất liệu đặc thù cho cả 8 ngành hàng vào bảng [chat_lieu]
        String[][] dsChatLieu = {
                {"Cotton Compact 100%", "Sợi bông chải kỹ mềm mịn, thấm hút mồ hôi tối ưu"},
                {"Cotton Pima cao cấp", "Bông Pima dệt thoi mịn màng chống bai giãn xù lông"},
                {"Vải Oxford dệt thoi", "Chất liệu sơ mi đứng form, bền màu, thoáng mát"},
                {"Vải đũi tự nhiên", "Mát nhẹ, thấm hút mồ hôi, thân thiện môi trường"},
                {"Dạ Tweed sang trọng", "Vải dạ dệt dày dặn giữ nhiệt tốt, phong cách quý phái"},
                {"Denim 12oz dệt thoi", "Vải jean cotton dệt chéo đứng dáng, bền bỉ"},
                {"Denim co giãn Spandex", "Jean pha sợi co giãn thoải mái khi vận động"},
                {"Kaki cotton twill", "Kaki dày dặn, không bai xù, chuẩn form công sở"},
                {"Tuyết mưa cao cấp", "Vải may quần tây co giãn nhẹ, không nhăn nhàu"},
                {"Tơ sống óng ánh", "Vải tơ mềm mại bắt sáng cao cấp cho đầm váy nữ"},
                {"Voan hoa chiffon", "Vải voan mỏng nhẹ bay bổng, nữ tính duyên dáng"},
                {"Lụa Satin cao cấp", "Bề mặt bóng mịn, mềm mát, tôn nét sang trọng"},
                {"Nỉ da cá chân cua 380gsm", "Nỉ bông dệt dày dặn, giữ ấm tối ưu cho mùa đông"},
                {"Thun thể thao Spandex", "Co giãn 4 chiều, khô nhanh, chuyên dụng tập luyện"},
                {"Da bò hạt Epsom", "Da thuộc cao cấp dập vân chống trầy xước nước"},
                {"Da Mastrotto Ý", "Da bò nhập khẩu Ý mềm mịn tự nhiên bền đẹp"},
                {"Vải Canvas chống nước", "Vải bố dệt sợi mật độ cao phủ PU chống thấm"},
                {"Bạc Ý 925 xi bạch kim", "Bạc chuẩn 92.5% sáng bóng, chống xỉn màu oxy hóa"},
                {"Titanium mạ PVD siêu bền", "Hợp kim titan siêu nhẹ chống ăn mòn, mạ chân không"},
                {"Thép không gỉ 316L", "Thép chuyên dụng đồng hồ cao cấp chống rỉ sét"},
                {"Da bò sáp nguyên tấm", "Da bò mộc dẻo dai, càng dùng càng bóng đẹp"},
                {"Vải Flyknit thoáng khí", "Sợi dệt nguyên khối co giãn ôm chân êm ái"},
                {"Da Nappa cao cấp", "Da cừu hoặc bê thuộc mềm mịn, ôm chân"},
                {"Cao su lưu hóa chống trơn", "Đế giày đúc cao su nguyên khối bám đường vượt trội"},
                {"Len Cashmere cao cấp", "Sợi len dệt siêu mịn màng, ấm áp êm ái cho mùa đông"}
        };
        for (String[] cl : dsChatLieu) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [chat_lieu] WHERE [ten_chat_lieu] = ?", Integer.class, cl[0]);
            if (count != null && count == 0) {
                jdbcTemplate.update(
                        "INSERT INTO [chat_lieu] ([ten_chat_lieu], [mo_ta], [kich_hoat]) VALUES (?, ?, 1)", cl[0], cl[1]);
            }
        }

        // 4. Bổ sung Form dáng đặc thù cho cả 8 ngành hàng vào bảng [form_dang]
        String[][] dsFormDang = {
                {"Regular Fit", "Dáng suông tiêu chuẩn vừa vặn thoải mái"},
                {"Slim Fit", "Dáng ôm nhẹ tôn đường nét cơ thể"},
                {"Oversize", "Dáng rộng phóng khoáng trẻ trung"},
                {"Boxy Streetwear", "Thân áo hộp rộng ngắn phong cách đường phố"},
                {"Loose Fit", "Dáng rộng rãi tạo cảm giác thoải mái tối đa"},
                {"Ống suông (Straight Leg)", "Quần ống đứng thẳng từ đùi xuống gấu"},
                {"Ống côn (Tapered)", "Quần thon dần về cổ chân hiện đại"},
                {"Ống rộng (Wide Leg)", "Quần ống suông rộng cá tính che khuyết điểm"},
                {"Slim Crop", "Quần ống ôm nhẹ trên mắt cá chân tôn chiều cao"},
                {"Xòe chữ A (A-Line)", "Đầm váy chiết eo xòe rộng nhẹ nhàng"},
                {"Ôm body quyến rũ", "Thiết kế ôm sát tôn trọn 3 vòng gợi cảm"},
                {"Xếp ly thanh lịch", "Chi tiết ly dập tinh tế sang trọng"},
                {"Streetwear Boxy", "Set đồ / Hoodie dáng rộng vuông vắn"},
                {"Athletic Slim", "Form đồ tập thể thao ôm nhẹ vận động linh hoạt"},
                {"Túi bao tử (Chest Bag)", "Đeo chéo trước ngực năng động"},
                {"Balo nắp cuộn (Roll-top)", "Thiết kế cuộn nắp chống tràn nước thời trang"},
                {"Ví gập đôi Bifold", "Ví tiền gập đôi kinh điển nhỏ gọn"},
                {"Mặt tròn cổ điển", "Thiết kế mặt đồng hồ tròn thanh lịch"},
                {"Dây xích Cuban cá tính", "Kiểu dáng mắt xích đan cài mạnh mẽ"},
                {"Mũ lưỡi trai (Cap)", "Mũ kết che nắng thể thao hiện đại"},
                {"Mũ Bucket vành tròn", "Mũ xô vành tròn phong cách dạo phố"},
                {"Low-Top cổ thấp", "Giày thể thao sneaker cổ thấp linh hoạt"},
                {"High-Top cổ cao", "Giày sneaker cổ cao bảo vệ cổ chân cá tính"},
                {"Derby công sở", "Giày tây cột dây thanh lịch chuyên nghiệp"},
                {"Loafer xỏ tiện lợi", "Giày mọi không dây tiện dụng sang trọng"}
        };
        for (String[] fd : dsFormDang) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [form_dang] WHERE [ten_form_dang] = ?", Integer.class, fd[0]);
            if (count != null && count == 0) {
                jdbcTemplate.update(
                        "INSERT INTO [form_dang] ([ten_form_dang], [mo_ta], [kich_hoat]) VALUES (?, ?, 1)", fd[0], fd[1]);
            }
        }
    }

    private void khoiTaoCayDanhMucChuan() {
        // Cấu trúc 5 Danh mục Cha và 19 Danh mục Con theo đúng yêu cầu nghiệp vụ chuyên nghiệp:
        // 1. Thời trang nam: Áo, Quần, Áo khoác.
        // 2. Thời trang nữ: Áo, Váy, Đầm, Quần.
        // 3. Giày dép: Sneaker, Sandal, Dép.
        // 4. Ví và balo: Túi xách, Balo, Ví.
        // 5. Phụ kiện: Đồng hồ, Kính, Mũ, Thắt lưng, Trang sức, Khăn choàng.
        record DanhMucConInfo(String ten, String slug) {}
        record DanhMucChaInfo(String ten, String slug, int thuTu, List<DanhMucConInfo> danhMucCon) {}

        List<DanhMucChaInfo> cayDanhMuc = List.of(
                // 1. Thời trang nam
                new DanhMucChaInfo("Thời trang nam", "thoi-trang-nam", 1, List.of(
                        new DanhMucConInfo("Áo", "ao-nam"),
                        new DanhMucConInfo("Quần", "quan-nam"),
                        new DanhMucConInfo("Áo khoác", "ao-khoac-nam")
                )),
                // 2. Thời trang nữ
                new DanhMucChaInfo("Thời trang nữ", "thoi-trang-nu", 2, List.of(
                        new DanhMucConInfo("Áo", "ao-nu"),
                        new DanhMucConInfo("Váy", "vay-nu"),
                        new DanhMucConInfo("Đầm", "dam-nu"),
                        new DanhMucConInfo("Quần", "quan-nu")
                )),
                // 3. Giày dép
                new DanhMucChaInfo("Giày dép", "giay-dep", 3, List.of(
                        new DanhMucConInfo("Sneaker", "sneaker"),
                        new DanhMucConInfo("Sandal", "sandal"),
                        new DanhMucConInfo("Dép", "dep")
                )),
                // 4. Ví và balo
                new DanhMucChaInfo("Ví và balo", "vi-va-balo", 4, List.of(
                        new DanhMucConInfo("Túi xách", "tui-xach"),
                        new DanhMucConInfo("Balo", "balo"),
                        new DanhMucConInfo("Ví", "vi")
                )),
                // 5. Phụ kiện
                new DanhMucChaInfo("Phụ kiện", "phu-kien", 5, List.of(
                        new DanhMucConInfo("Đồng hồ", "dong-ho"),
                        new DanhMucConInfo("Kính", "kinh"),
                        new DanhMucConInfo("Mũ", "mu"),
                        new DanhMucConInfo("Thắt lưng", "that-lung"),
                        new DanhMucConInfo("Trang sức", "trang-suc"),
                        new DanhMucConInfo("Khăn choàng", "khan-choang")
                ))
        );

        for (DanhMucChaInfo cha : cayDanhMuc) {
            // 1. Tạo hoặc cập nhật Danh mục Cha
            Long chaId = layHoacTaoDanhMuc(cha.ten(), cha.slug(), null, cha.thuTu());

            // 2. Tạo hoặc cập nhật từng Danh mục Con
            int thuTuCon = 1;
            for (DanhMucConInfo con : cha.danhMucCon()) {
                layHoacTaoDanhMuc(con.ten(), con.slug(), chaId, thuTuCon++);
            }
        }

        // Tự động map và đồng bộ dữ liệu cũ (nếu có các slug cũ hoặc sản phẩm cũ)
        try {
            // Map các danh mục con cũ sang danh mục con chuẩn mới
            Long aoNamId = layIdDanhMucTheoSlug("ao-nam");
            if (aoNamId != null) {
                jdbcTemplate.update("UPDATE [san_pham] SET [ma_danh_muc_id] = ? WHERE [ma_danh_muc_id] IN (SELECT [id] FROM [danh_muc] WHERE [duong_dan_slug] IN ('ao', 'ao-polo', 'ao-thun', 'ao-so-mi', 'ao-polo-nam', 'ao-thun-nam'))", aoNamId);
            }
            Long quanNamId = layIdDanhMucTheoSlug("quan-nam");
            if (quanNamId != null) {
                jdbcTemplate.update("UPDATE [san_pham] SET [ma_danh_muc_id] = ? WHERE [ma_danh_muc_id] IN (SELECT [id] FROM [danh_muc] WHERE [duong_dan_slug] IN ('quan', 'quan-jean', 'quan-kaki', 'quan-tay', 'quan-short', 'quan-short-nam'))", quanNamId);
            }
            Long khoacNamId = layIdDanhMucTheoSlug("ao-khoac-nam");
            if (khoacNamId != null) {
                jdbcTemplate.update("UPDATE [san_pham] SET [ma_danh_muc_id] = ? WHERE [ma_danh_muc_id] IN (SELECT [id] FROM [danh_muc] WHERE [duong_dan_slug] IN ('ao-khoac'))", khoacNamId);
            }
            Long aoNuId = layIdDanhMucTheoSlug("ao-nu");
            if (aoNuId != null) {
                jdbcTemplate.update("UPDATE [san_pham] SET [ma_danh_muc_id] = ? WHERE [ma_danh_muc_id] IN (SELECT [id] FROM [danh_muc] WHERE [duong_dan_slug] IN ('ao-kieu'))", aoNuId);
            }
            Long vayNuId = layIdDanhMucTheoSlug("vay-nu");
            if (vayNuId != null) {
                jdbcTemplate.update("UPDATE [san_pham] SET [ma_danh_muc_id] = ? WHERE [ma_danh_muc_id] IN (SELECT [id] FROM [danh_muc] WHERE [duong_dan_slug] IN ('chan-vay', 'vay'))", vayNuId);
            }
            Long damNuId = layIdDanhMucTheoSlug("dam-nu");
            if (damNuId != null) {
                jdbcTemplate.update("UPDATE [san_pham] SET [ma_danh_muc_id] = ? WHERE [ma_danh_muc_id] IN (SELECT [id] FROM [danh_muc] WHERE [duong_dan_slug] IN ('dam'))", damNuId);
            }
            Long viId = layIdDanhMucTheoSlug("vi");
            if (viId != null) {
                jdbcTemplate.update("UPDATE [san_pham] SET [ma_danh_muc_id] = ? WHERE [ma_danh_muc_id] IN (SELECT [id] FROM [danh_muc] WHERE [duong_dan_slug] IN ('vi-bop'))", viId);
            }
            Long muId = layIdDanhMucTheoSlug("mu");
            if (muId != null) {
                jdbcTemplate.update("UPDATE [san_pham] SET [ma_danh_muc_id] = ? WHERE [ma_danh_muc_id] IN (SELECT [id] FROM [danh_muc] WHERE [duong_dan_slug] IN ('mu-non'))", muId);
            }
            Long kinhId = layIdDanhMucTheoSlug("kinh");
            if (kinhId != null) {
                jdbcTemplate.update("UPDATE [san_pham] SET [ma_danh_muc_id] = ? WHERE [ma_danh_muc_id] IN (SELECT [id] FROM [danh_muc] WHERE [duong_dan_slug] IN ('kinh-mat'))", kinhId);
            }
            Long trangSucId = layIdDanhMucTheoSlug("trang-suc");
            if (trangSucId != null) {
                jdbcTemplate.update("UPDATE [san_pham] SET [ma_danh_muc_id] = ? WHERE [ma_danh_muc_id] IN (SELECT [id] FROM [danh_muc] WHERE [duong_dan_slug] IN ('vong-tay', 'day-chuyen', 'nhan'))", trangSucId);
            }
        } catch (Exception ex) {
            log.warn("Lỗi khi đồng bộ sản phẩm cũ sang danh mục mới: {}", ex.getMessage());
        }
    }

    private Long layIdDanhMucTheoSlug(String slug) {
        try {
            return jdbcTemplate.queryForObject("SELECT TOP 1 [id] FROM [danh_muc] WHERE [duong_dan_slug] = ?", Long.class, slug);
        } catch (Exception e) {
            return null;
        }
    }

    private Long layHoacTaoDanhMuc(String ten, String slug, Long maCha, int thuTu) {
        try {
            List<Long> ids = jdbcTemplate.query(
                    "SELECT [id] FROM [danh_muc] WHERE [duong_dan_slug] = ?", (rs, rowNum) -> rs.getLong("id"), slug);
            if (!ids.isEmpty()) {
                Long existingId = ids.get(0);
                if (maCha != null) {
                    jdbcTemplate.update("UPDATE [danh_muc] SET [ma_danh_muc_cha] = ?, [ten_danh_muc] = ? WHERE [id] = ?", maCha, ten, existingId);
                } else {
                    jdbcTemplate.update("UPDATE [danh_muc] SET [ma_danh_muc_cha] = NULL, [ten_danh_muc] = ? WHERE [id] = ?", ten, existingId);
                }
                return existingId;
            }

            jdbcTemplate.update(
                    "INSERT INTO [danh_muc] ([ma_danh_muc_cha], [ten_danh_muc], [duong_dan_slug], [thu_tu_hien_thi], [kich_hoat]) " +
                    "VALUES (?, ?, ?, ?, 1)",
                    maCha, ten, slug, thuTu);

            return jdbcTemplate.queryForObject(
                    "SELECT TOP 1 [id] FROM [danh_muc] WHERE [duong_dan_slug] = ?", Long.class, slug);
        } catch (Exception e) {
            log.warn("Lỗi tạo/lấy danh mục slug={}: {}", slug, e.getMessage());
            return null;
        }
    }

    private void khoiTaoSanPhamMauChoCacDanhMuc() {
        // Cấu trúc sản phẩm mẫu chuyên nghiệp chuẩn 5 Danh mục Cha và 19 Danh mục Con
        record BienTheMau(String sizeCode, String mauHex, String mauTen, int soLuong) {}
        record SpMau(String maSp, String tenSp, String slug, String slugDm, String chatLieu, String formDang,
                     String sizeHd, String moTa, long giaBan, String imgUrl, List<BienTheMau> bienTheList) {}

        List<SpMau> listSp = List.of(
                // ==========================================
                // 1. THỜI TRANG NAM (3 danh mục con: Áo, Quần, Áo khoác)
                // ==========================================
                new SpMau("SP-NAM-POLO-01", "Áo Thun Polo Nam Pique Cotton Kháng Khuẩn Simplain", "ao-thun-polo-nam-pique-cotton", "ao-nam",
                        "Cotton Pima cao cấp", "Regular Fit",
                        "Size S: 50 - 58kg | Size M: 59 - 66kg | Size L: 67 - 74kg | Size XL: 75 - 82kg",
                        "Áo polo nam chất liệu Cotton Pima dệt cá sấu thoáng mát, co giãn nhẹ và thấm hút mồ hôi tối ưu. Cổ áo dệt bo gân đứng form, không bai dão sau nhiều lần giặt. Phong cách lịch lãm, phù hợp cả đi làm và đi chơi.",
                        289000, "https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=600",
                        List.of(
                                new BienTheMau("M", "#000000", "Đen", 50),
                                new BienTheMau("L", "#000000", "Đen", 50),
                                new BienTheMau("L", "#FFFFFF", "Trắng", 50),
                                new BienTheMau("XL", "#1E3A8A", "Xanh Navy", 50)
                        )),
                new SpMau("SP-NAM-JEAN-02", "Quần Jean Nam Slimfit Denim 12.5oz Co Giãn Cao Cấp", "quan-jean-nam-slimfit-denim-12oz", "quan-nam",
                        "Denim co giãn Spandex", "Slim Fit",
                        "Size 29: Vòng bụng 75-77cm | Size 30: Vòng bụng 78-80cm | Size 31: Vòng bụng 81-83cm | Size 32: Vòng bụng 84-86cm",
                        "Quần jean nam thiết kế ống côn ôm nhẹ tôn dáng, sợi Spandex co giãn 4 chiều cho cảm giác vận động thoải mái tối đa. Khóa kéo kim loại YKK bền bỉ và wash màu phong trần hiện đại.",
                        420000, "https://images.unsplash.com/photo-1542272604-780c96856592?w=600",
                        List.of(
                                new BienTheMau("29", "#1E3A8A", "Xanh Navy", 40),
                                new BienTheMau("30", "#1E3A8A", "Xanh Navy", 45),
                                new BienTheMau("31", "#1E3A8A", "Xanh Navy", 45),
                                new BienTheMau("32", "#000000", "Đen", 35)
                        )),
                new SpMau("SP-NAM-KHOAC-03", "Áo Khoác Gió Bomber 2 Lớp Chống Nước Cản Gió Simplain", "ao-khoac-gio-bomber-nam-2-lop", "ao-khoac-nam",
                        "Vải Oxford dệt thoi", "Boxy Streetwear",
                        "Size M: Cao 1m65 - 1m72 (58-65kg) | Size L: 1m70 - 1m77 (66-74kg) | Size XL: 1m75 - 1m84 (75-84kg)",
                        "Áo khoác gió bomber phong cách streetwear nam tính, cấu tạo 2 lớp chống thấm nước và chắn gió hiệu quả. Túi khóa kéo tiện dụng cùng bo thun cổ tay giữ ấm tối đa trong mùa se lạnh.",
                        450000, "https://images.unsplash.com/photo-1544441893-675973e31985?w=600",
                        List.of(
                                new BienTheMau("M", "#000000", "Đen", 30),
                                new BienTheMau("L", "#000000", "Đen", 40),
                                new BienTheMau("XL", "#64748B", "Xám Tiêu", 30)
                        )),

                // ==========================================
                // 2. THỜI TRANG NỮ (4 danh mục con: Áo, Váy, Đầm, Quần)
                // ==========================================
                new SpMau("SP-NU-AOKIEU-04", "Áo Kiểu Nữ Cổ Vuông Tay Phồng Lụa Satin Cao Cấp", "ao-kieu-nu-co-vuong-tay-phong", "ao-nu",
                        "Lụa Satin cao cấp", "Regular Fit",
                        "Size S: Ngực 82-86cm (43-49kg) | Size M: Ngực 87-90cm (50-56kg) | Size L: Ngực 91-95cm (57-63kg)",
                        "Áo kiểu nữ thiết kế cổ vuông thanh lịch khoe xương quai xanh quyến rũ, tay bồng tiểu thư che khuyết điểm bắp tay. Bề mặt vải lụa óng ánh mềm mượt, mang lại vẻ đẹp nhẹ nhàng, sang trọng.",
                        260000, "https://images.unsplash.com/photo-1564257631407-4deb1f99d992?w=600",
                        List.of(
                                new BienTheMau("S", "#FFFFFF", "Trắng", 40),
                                new BienTheMau("M", "#FFFFFF", "Trắng", 40),
                                new BienTheMau("M", "#F472B6", "Hồng Pastel", 40)
                        )),
                new SpMau("SP-NU-CHANVAY-05", "Chân Váy Chữ A Xếp Ly Kaki Hàn Quốc Tôn Dáng", "chan-vay-chu-a-xep-ly-kaki-nu", "vay-nu",
                        "Kaki cotton twill", "Xếp ly thanh lịch",
                        "Size S: Vòng eo 64-67cm (43-48kg) | Size M: Vòng eo 68-71cm (49-54kg) | Size L: Vòng eo 72-76cm (55-60kg)",
                        "Chân váy xếp ly cạp cao hack chiều cao cực chuẩn, may kèm lớp quần bảo hộ kín đáo giúp các nàng tự tin hoạt động. Chất vải kaki dày dặn giữ nếp ly sắc nét sau khi giặt.",
                        240000, "https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=600",
                        List.of(
                                new BienTheMau("S", "#FFFFFF", "Trắng", 35),
                                new BienTheMau("M", "#FFFFFF", "Trắng", 35),
                                new BienTheMau("M", "#000000", "Đen", 40)
                        )),
                new SpMau("SP-NU-DAMMAXI-06", "Đầm Maxi Dáng Xòe Voan Hoa Chiffon 2 Lớp Duyên Dáng", "dam-maxi-dang-xoe-voan-hoa-chiffon", "dam-nu",
                        "Voan hoa chiffon", "Xòe chữ A (A-Line)",
                        "Size S: 42-49kg (Ngực 82-86cm) | Size M: 50-56kg (Ngực 87-91cm) | Size L: 57-64kg (Ngực 92-96cm)",
                        "Đầm voan hoa 2 lớp thướt tha, lớp lót lụa trong êm mát kín đáo. Thiết kế chiết eo tôn vóc dáng thon thả, thích hợp diện dạo phố, chụp ảnh du lịch hoặc tham gia các buổi tiệc ngoài trời.",
                        480000, "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600",
                        List.of(
                                new BienTheMau("S", "#F472B6", "Hồng Pastel", 30),
                                new BienTheMau("M", "#F472B6", "Hồng Pastel", 40),
                                new BienTheMau("L", "#F472B6", "Hồng Pastel", 30)
                        )),
                new SpMau("SP-NU-QUANTAY-07", "Quần Tây Nữ Ống Suông Cạp Cao Hack Dáng Simplain", "quan-tay-nu-ong-suong-cap-cao", "quan-nu",
                        "Tuyết mưa cao cấp", "Ống rộng (Wide Leg)",
                        "Size S: Vòng eo 63-66cm (43-48kg) | Size M: Vòng eo 67-70cm (49-54kg) | Size L: Vòng eo 71-75cm (55-60kg)",
                        "Quần tây nữ cạp cao phối khóa ẩn thanh lịch, ống suông dài chuẩn tỷ lệ hack chân dài miên man. Vải tuyết mưa dệt đanh sợi, chống nhăn và chống bám bụi hiệu quả.",
                        350000, "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=600",
                        List.of(
                                new BienTheMau("S", "#000000", "Đen", 30),
                                new BienTheMau("M", "#000000", "Đen", 40),
                                new BienTheMau("M", "#D2B48C", "Be Vintage", 30)
                        )),

                // ==========================================
                // 3. GIÀY DÉP (3 danh mục con: Sneaker, Sandal, Dép)
                // ==========================================
                new SpMau("SP-GIAY-SNEAKER-08", "Giày Sneaker Thể Thao Đệm Khí Êm Chân Simplain Runner", "giay-sneaker-the-thao-dem-khi-simplain", "sneaker",
                        "Vải Flyknit thoáng khí", "Low-Top cổ thấp",
                        "Size 39: Chân 24.1 - 24.5cm | Size 40: 24.6 - 25.0cm | Size 41: 25.1 - 25.5cm | Size 42: 25.6 - 26.0cm",
                        "Giày sneaker thể thao với thân dệt Flyknit nguyên khối co giãn ôm sát mu bàn chân, đế đệm trợ lực êm ái bảo vệ khớp xương khi đi bộ hay chạy nhảy. Đế ngoài phủ cao su chống mài mòn và bám đường tối ưu.",
                        590000, "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=600",
                        List.of(
                                new BienTheMau("40", "#FFFFFF", "Trắng", 40),
                                new BienTheMau("41", "#FFFFFF", "Trắng", 50),
                                new BienTheMau("42", "#000000", "Đen", 40)
                        )),
                new SpMau("SP-GIAY-SANDAL-09", "Giày Sandal Nam Nữ Quai Dù Đế Bánh Mì Siêu Êm", "giay-sandal-nam-nu-quai-du-de-banh-mi", "sandal",
                        "Cao su lưu hóa chống trơn", "Low-Top cổ thấp",
                        "Size 38: Chân dài 23.5 - 24.0cm | Size 39: 24.1 - 24.5cm | Size 40: 24.6 - 25.0cm | Size 41: 25.1 - 25.5cm",
                        "Sandal quai hậu bản dù jacquard chịu lực cao, khóa dán thông minh điều chỉnh độ ôm chân linh hoạt. Đế bánh mì đúc cao su non siêu nhẹ, êm ái chống trơn trượt hoàn hảo khi đi mưa.",
                        320000, "https://images.unsplash.com/photo-1603808033192-082d6919d3e1?w=600",
                        List.of(
                                new BienTheMau("39", "#000000", "Đen", 30),
                                new BienTheMau("40", "#000000", "Đen", 40),
                                new BienTheMau("41", "#D2B48C", "Be Vintage", 30)
                        )),
                new SpMau("SP-GIAY-DEP-10", "Dép Quai Ngang Đúc Nguyên Khối Chống Trơn Trượt Simplain", "dep-quai-ngang-duc-nguyen-khoi", "dep",
                        "Cao su lưu hóa chống trơn", "Loafer xỏ tiện lợi",
                        "Size 39: Chân 24cm | Size 40: Chân 24.5-25cm | Size 41: Chân 25.5cm | Size 42: Chân 26cm",
                        "Dép bánh mì quai ngang đúc nguyên khối từ hạt nhựa EVA cao cấp, trọng lượng siêu nhẹ và chống nước tuyệt đối. Đế vân gai kim cương chống trơn trượt an toàn cả trong nhà tắm lẫn đi dạo ngoài trời.",
                        150000, "https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=600",
                        List.of(
                                new BienTheMau("40", "#000000", "Đen", 50),
                                new BienTheMau("41", "#000000", "Đen", 50),
                                new BienTheMau("41", "#FFFFFF", "Trắng", 40)
                        )),

                // ==========================================
                // 4. VÍ VÀ BALO (3 danh mục con: Túi xách, Balo, Ví)
                // ==========================================
                new SpMau("SP-TUI-XACH-11", "Túi Xách Da Bò Epsom Nữ Khóa Gập Thanh Lịch Simplain", "tui-xach-da-bo-epsom-nu-thanh-lich", "tui-xach",
                        "Da bò hạt Epsom", "Túi bao tử (Chest Bag)",
                        "Kích thước tiêu chuẩn: Chiều dài 24cm x Rộng 8cm x Cao 16cm (Chứa vừa iPad Mini, điện thoại, ví dài)",
                        "Túi xách nữ chất liệu da bò Epsom dập vân tinh tế chống trầy xước và kháng nước, phụ kiện khóa xoay mạ vàng sáng bóng sang trọng. Dây đeo da phối xích có thể điều chỉnh đeo chéo hoặc xách tay linh hoạt.",
                        520000, "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600",
                        List.of(
                                new BienTheMau("Freesize", "#000000", "Đen", 30),
                                new BienTheMau("Freesize", "#8B4513", "Nâu Da Bò", 30)
                        )),
                new SpMau("SP-BALO-LAPTOP-12", "Balo Laptop Công Sở Kháng Nước Đa Năng Simplain 20L", "balo-laptop-cong-so-khang-nuoc-simplain", "balo",
                        "Vải Canvas chống nước", "Balo nắp cuộn (Roll-top)",
                        "Form chuẩn 20L: 43cm x 30cm x 14cm (Có ngăn chống sốc chuyên dụng đựng laptop 15.6 inch)",
                        "Balo công sở thiết kế tối giản thông minh, chất vải Canvas 900D phủ tráng PU chống thấm nước khi đi mưa. Đệm lưng tổ ong thoáng khí thoát nhiệt, quai đeo trợ lực chống gù lưng hiệu quả.",
                        420000, "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600",
                        List.of(
                                new BienTheMau("Freesize", "#000000", "Đen", 50),
                                new BienTheMau("Freesize", "#64748B", "Xám Tiêu", 40)
                        )),
                new SpMau("SP-VI-DABO-13", "Ví Nam Da Bò Sáp Bifold Khâu Tay Thủ Công Cao Cấp", "vi-nam-da-bo-sap-bifold-khau-tay", "vi",
                        "Da bò sáp nguyên tấm", "Ví gập đôi Bifold",
                        "Kích thước gập: 11.5cm x 9.5cm x 1.5cm (Gồm 2 ngăn tiền chính và 8 ngăn thẻ ATM/CCCD)",
                        "Ví nam da bò sáp Crazy Horse nhập khẩu nguyên tấm, càng sử dụng lâu chất da càng bóng mềm và lên màu vintage độc nhất. Từng đường kim mũi chỉ được khâu thủ công tỉ mỉ, độ bền trên 10 năm.",
                        290000, "https://images.unsplash.com/photo-1627123424574-724758594e93?w=600",
                        List.of(
                                new BienTheMau("Freesize", "#8B4513", "Nâu Da Bò", 40),
                                new BienTheMau("Freesize", "#000000", "Đen", 40)
                        )),

                // ==========================================
                // 5. PHỤ KIỆN (6 danh mục con: Đồng hồ, Kính, Mũ, Thắt lưng, Trang sức, Khăn choàng)
                // ==========================================
                new SpMau("SP-PK-DONGHO-14", "Đồng Hồ Nam Dây Thép Không Gỉ Mặt Kính Sapphire Simplain Classic", "dong-ho-nam-thep-khong-gi-mat-sapphire", "dong-ho",
                        "Thép không gỉ 316L", "Mặt tròn cổ điển",
                        "Đường kính mặt 40mm | Độ dày vỏ 8.5mm | Chiều dài dây 220mm (Kính Sapphire chống xước, chống nước 5ATM)",
                        "Đồng hồ nam phong cách doanh nhân lịch lãm, bộ máy Quartz chuẩn xác từ Nhật Bản. Toàn bộ vỏ và dây đeo chế tác từ thép không gỉ 316L chống ăn mòn tuyệt đối, mặt kính tinh thể Sapphire chống va đập trầy xước.",
                        890000, "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=600",
                        List.of(
                                new BienTheMau("Freesize", "#C0C0C0", "Bạc Sang Trọng", 30),
                                new BienTheMau("Freesize", "#000000", "Đen", 20)
                        )),
                new SpMau("SP-PK-KINHMAT-15", "Kính Mát Unisex Gọng Titanium Phân Cực Polarized Chống UV400", "kinh-mat-unisex-gong-titanium-polarized", "kinh",
                        "Titanium mạ PVD siêu bền", "Mặt tròn cổ điển",
                        "Chiều ngang tròng: 54mm | Cầu kính: 18mm | Chiều dài càng kính: 142mm (Khối lượng siêu nhẹ chỉ 18g)",
                        "Kính mát phân cực cao cấp gọng hợp kim Titanium siêu nhẹ, chịu lực uốn cong linh hoạt không biến dạng. Tròng kính tráng gương 9 lớp bảo vệ mắt tối đa khỏi 100% tia tử ngoại UV400 và chống lóa đèn xe ban đêm.",
                        350000, "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=600",
                        List.of(
                                new BienTheMau("Freesize", "#000000", "Đen", 50),
                                new BienTheMau("Freesize", "#C0C0C0", "Bạc Sang Trọng", 30)
                        )),
                new SpMau("SP-PK-MUNON-16", "Mũ Lưỡi Trai Classic Simplain Thêu Nổi 100% Cotton", "mu-luoi-trai-classic-simplain-cotton", "mu",
                        "Cotton Compact 100%", "Mũ lưỡi trai (Cap)",
                        "Freesize: Vòng đầu từ 55 - 60cm (Khóa đồng phía sau có thể nới rộng / thu hẹp dễ dàng)",
                        "Nón kết lưỡi trai form dáng 6 múi cứng cáp, thêu logo Simplain nổi 3D tinh xảo. Chất vải kaki cotton chải kỹ thấm hút mồ hôi tối ưu, đem lại cảm giác thoáng mát cả ngày dài dạo phố hay chơi thể thao.",
                        150000, "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=600",
                        List.of(
                                new BienTheMau("Freesize", "#000000", "Đen", 60),
                                new BienTheMau("Freesize", "#FFFFFF", "Trắng", 40),
                                new BienTheMau("Freesize", "#D2B48C", "Be Vintage", 30)
                        )),
                new SpMau("SP-PK-THAT-LUNG-17", "Thắt Lưng Nam Da Bò Sáp Khóa Kim Loại Tự Động Cao Cấp", "that-lung-nam-da-bo-khoa-tu-dong-cao-cap", "that-lung",
                        "Da bò sáp nguyên tấm", "Regular Fit",
                        "Bản rộng 3.5cm x Chiều dài 120cm (Phù hợp vòng bụng từ 75cm đến 105cm, dễ cắt bớt dây)",
                        "Dây nịt nam da bò thuộc 2 lớp dẻo dai nguyên tấm, không bong tróc hay rạn nứt bề mặt. Khóa ray trượt tự động hợp kim mạ crom sáng bóng, thao tác cài mở êm mượt và sang trọng.",
                        290000, "https://images.unsplash.com/photo-1624222247344-550fb60583dc?w=600",
                        List.of(
                                new BienTheMau("Freesize", "#000000", "Đen", 50),
                                new BienTheMau("Freesize", "#8B4513", "Nâu Da Bò", 40)
                        )),
                new SpMau("SP-PK-TRANGSUC-18", "Vòng Tay Bạc Ý 925 Xi Bạch Kim Mắt Xích Cuban Nam Nữ", "vong-tay-bac-y-925-cuban-chain", "trang-suc",
                        "Bạc Ý 925 xi bạch kim", "Dây xích Cuban cá tính",
                        "Chiều dài lắc 18cm + 3cm mắt xích mở rộng (Bản xích dày 5mm, khối lượng 14.2 gram)",
                        "Lắc tay bạc chuẩn 92.5% phủ lớp mạ Rhodium bạch kim bóng bẩy, chống ố xỉn và không gây kích ứng da. Thiết kế mắt xích Cuban đan cài mạnh mẽ, phong cách streetwear thời thượng nâng tầm cổ tay.",
                        390000, "https://images.unsplash.com/photo-1611591475152-47795d074242?w=600",
                        List.of(
                                new BienTheMau("Freesize", "#C0C0C0", "Bạc Sang Trọng", 40)
                        )),
                new SpMau("SP-PK-KHANCHOANG-19", "Khăn Choàng Cổ Len Cashmere Giữ Ấm Mùa Đông Cao Cấp", "khan-choang-co-len-cashmere-cao-cap", "khan-choang",
                        "Len Cashmere cao cấp", "Regular Fit",
                        "Kích thước tiêu chuẩn: Chiều dài 200cm x Rộng 70cm (Khối lượng 260g siêu nhẹ giữ nhiệt tối ưu)",
                        "Khăn choàng cổ dệt từ sợi len Cashmere cao cấp kết hợp lông cừu tự nhiên, mang lại cảm giác mềm mại như nhung, không gây dặm ngứa da cổ. Hai đầu khăn đan tua rua cổ điển thanh lịch, giữ ấm cơ thể hoàn hảo trong ngày đông giá rét.",
                        310000, "https://images.unsplash.com/photo-1520903920243-00d872a2d1c9?w=600",
                        List.of(
                                new BienTheMau("Freesize", "#D2B48C", "Be Vintage", 35),
                                new BienTheMau("Freesize", "#64748B", "Xám Tiêu", 35),
                                new BienTheMau("Freesize", "#8B4513", "Nâu Da Bò", 25)
                        ))
        );

        for (SpMau sp : listSp) {
            try {
                // Kiểm tra xem sản phẩm đã tồn tại theo mã chưa
                Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM [san_pham] WHERE [ma_san_pham] = ?", Integer.class, sp.maSp());

                Long spId;
                // Lấy ID danh mục con theo slug
                Long dmId = layIdDanhMucTheoSlug(sp.slugDm());
                if (dmId == null) {
                    log.warn("Chưa tìm thấy danh mục con có slug={}", sp.slugDm());
                    continue;
                }

                if (count != null && count > 0) {
                    // Cập nhật thông tin chuẩn chỉnh nếu đã có sản phẩm
                    jdbcTemplate.update(
                            "UPDATE [san_pham] SET [ma_danh_muc_id] = ?, [ten_san_pham] = ?, [duong_dan_slug] = ?, " +
                            "[chat_lieu] = ?, [form_dang] = ?, [huong_dan_chon_size] = ?, [mo_ta_chi_tiet] = ?, [gia_co_ban] = ? " +
                            "WHERE [ma_san_pham] = ?",
                            dmId, sp.tenSp(), sp.slug(), sp.chatLieu(), sp.formDang(), sp.sizeHd(), sp.moTa(), sp.giaBan(), sp.maSp());

                    spId = jdbcTemplate.queryForObject(
                            "SELECT TOP 1 [id] FROM [san_pham] WHERE [ma_san_pham] = ?", Long.class, sp.maSp());
                } else {
                    // 1. Chèn sản phẩm mới
                    jdbcTemplate.update(
                            "INSERT INTO [san_pham] ([ma_danh_muc_id], [ma_san_pham], [ten_san_pham], [duong_dan_slug], " +
                            "[chat_lieu], [form_dang], [huong_dan_chon_size], [mo_ta_chi_tiet], [gia_co_ban], [noi_bat], [kich_hoat], [ngay_tao]) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1, 1, GETDATE())",
                            dmId, sp.maSp(), sp.tenSp(), sp.slug(), sp.chatLieu(), sp.formDang(), sp.sizeHd(), sp.moTa(), sp.giaBan());

                    spId = jdbcTemplate.queryForObject(
                            "SELECT TOP 1 [id] FROM [san_pham] WHERE [ma_san_pham] = ?", Long.class, sp.maSp());
                }

                if (spId == null) continue;

                // 2. Chèn/Cập nhật hình ảnh sản phẩm đại diện
                Integer countImg = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM [hinh_anh] WHERE [ma_san_pham_id] = ? AND [la_anh_dai_dien] = 1", Integer.class, spId);
                if (countImg == null || countImg == 0) {
                    jdbcTemplate.update(
                            "INSERT INTO [hinh_anh] ([ma_san_pham_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) " +
                            "VALUES (?, ?, 1, 1)",
                            spId, sp.imgUrl());
                }

                // 3. Chèn các biến thể SKU cho sản phẩm
                for (BienTheMau bt : sp.bienTheList()) {
                    Long mauId = jdbcTemplate.queryForObject(
                            "SELECT TOP 1 [id] FROM [mau_sac] WHERE [ma_mau] = ? OR [ten_mau] = ?", Long.class, bt.mauHex(), bt.mauTen());
                    if (mauId == null) {
                        mauId = jdbcTemplate.queryForObject("SELECT TOP 1 [id] FROM [mau_sac] ORDER BY [id] ASC", Long.class);
                    }

                    Long sizeId = jdbcTemplate.queryForObject(
                            "SELECT TOP 1 [id] FROM [kich_thuoc] WHERE [ma_kich_thuoc] = ?", Long.class, bt.sizeCode());
                    if (sizeId == null) {
                        sizeId = jdbcTemplate.queryForObject("SELECT TOP 1 [id] FROM [kich_thuoc] ORDER BY [id] ASC", Long.class);
                    }

                    String skuCode = sp.maSp() + "-" + bt.sizeCode() + "-" + (bt.mauTen().equalsIgnoreCase("Đen") ? "BLK" :
                            bt.mauTen().equalsIgnoreCase("Trắng") ? "WHT" :
                            bt.mauTen().equalsIgnoreCase("Xanh Navy") ? "NVY" :
                            bt.mauTen().equalsIgnoreCase("Hồng Pastel") ? "PNK" :
                            bt.mauTen().equalsIgnoreCase("Nâu Da Bò") ? "BRN" :
                            bt.mauTen().equalsIgnoreCase("Be Vintage") ? "BEG" :
                            bt.mauTen().equalsIgnoreCase("Bạc Sang Trọng") ? "SLV" : "VAR");

                    Integer countSku = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM [bien_the_san_pham] WHERE [ma_sku] = ?", Integer.class, skuCode);

                    if (countSku == null || countSku == 0) {
                        String barcode = "BC-" + Math.abs(skuCode.hashCode() % 100000000);
                        long giaGoc = (long) (sp.giaBan() * 1.3);

                        jdbcTemplate.update(
                                "INSERT INTO [bien_the_san_pham] ([ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], " +
                                "[ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)",
                                spId, mauId, sizeId, skuCode, barcode, bt.soLuong(), sp.giaBan(), giaGoc);
                    }
                }

                log.info("Đã khởi tạo thành công sản phẩm chuyên nghiệp: [{}] - {} (Danh mục con: {})", sp.maSp(), sp.tenSp(), sp.slugDm());
            } catch (Exception ex) {
                log.warn("Lỗi khi tạo sản phẩm mẫu [{}]: {}", sp.maSp(), ex.getMessage());
            }
        }

        // Đồng bộ hướng dẫn size mặc định cho các sản phẩm cũ nếu còn thiếu
        try {
            jdbcTemplate.update("UPDATE [san_pham] SET [huong_dan_chon_size] = N'Size tiêu chuẩn theo form dáng Châu Á (Xem chi tiết từng biến thể size)' WHERE [huong_dan_chon_size] IS NULL OR LTRIM(RTRIM([huong_dan_chon_size])) = ''");
        } catch (Exception ignored) {}
    }
}
