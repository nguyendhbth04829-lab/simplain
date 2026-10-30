package vn.fpoly.simplain.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Cấu hình tự động kiểm tra, khắc phục lỗi phông chữ tiếng Việt (Mojibake / dấu '?')
 * và khởi tạo dữ liệu mẫu chuẩn cho Trạng thái đơn hàng & Đơn hàng phục vụ Dashboard (US05).
 */
@Configuration
public class DataEncodingFixerConfig {

    @Bean
    public CommandLineRunner khacPhucLoiPhongChuDatabase(@Autowired JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                // 1. Khắc phục bảng vai_tro (US02, US03, US04)
                jdbcTemplate.update(
                        "UPDATE [vai_tro] SET [ten_vai_tro] = N'Quản trị viên tối cao', [mo_ta] = N'Toàn quyền cấu hình hệ thống' " +
                        "WHERE [ma_vai_tro] = 'ADMIN' AND ([ten_vai_tro] LIKE '%?%' OR [mo_ta] LIKE '%?%')");
                jdbcTemplate.update(
                        "UPDATE [vai_tro] SET [ten_vai_tro] = N'Chăm sóc khách hàng', [mo_ta] = N'Duyệt yêu cầu đổi trả và hỗ trợ' " +
                        "WHERE [ma_vai_tro] = 'CSKH' AND ([ten_vai_tro] LIKE '%?%' OR [ten_vai_tro] LIKE 'Cham%' OR [mo_ta] LIKE '%?%')");
                jdbcTemplate.update(
                        "UPDATE [vai_tro] SET [ten_vai_tro] = N'Thu ngân quầy POS', [mo_ta] = N'Tạo đơn tại cửa hàng' " +
                        "WHERE [ma_vai_tro] = 'BAN_HANG' AND ([ten_vai_tro] LIKE '%?%' OR [mo_ta] LIKE '%?%')");

                // 2. Khắc phục bảng nhan_vien (US02, US04)
                jdbcTemplate.update(
                        "UPDATE [nhan_vien] SET [ho_ten] = N'Nguyễn Văn Admin' " +
                        "WHERE [ma_nhan_vien] = 'NV001' AND ([ho_ten] LIKE '%?%' OR [ho_ten] LIKE '%Van%')");
                jdbcTemplate.update(
                        "UPDATE [nhan_vien] SET [ho_ten] = N'Lê CSKH' " +
                        "WHERE [ma_nhan_vien] = 'NV002' AND [ho_ten] LIKE '%?%'");

                // 3. Khắc phục bảng khach_hang (US01)
                jdbcTemplate.update(
                        "UPDATE [khach_hang] SET [ho_ten] = N'Trần Mua Hàng' " +
                        "WHERE [ma_khach_hang] = 'KH001' AND [ho_ten] LIKE '%?%'");
                jdbcTemplate.update(
                        "UPDATE [khach_hang] SET [ho_ten] = N'Đỗ Nguyên' " +
                        "WHERE [ho_ten] LIKE 'D? Nguy%'");

                // 4. Khắc phục bảng dia_chi (US13)
                jdbcTemplate.update(
                        "UPDATE [dia_chi] SET [ten_nguoi_nhan] = N'Trần Mua Hàng', [tinh_thanh] = N'Hà Nội', " +
                        "[quan_huyen] = N'Cầu Giấy', [phuong_xa] = N'Dịch Vọng', [dia_chi_chi_tiet] = N'Số 12 ngõ 45 phố Duy Tân' " +
                        "WHERE [id] = 1 AND ([ten_nguoi_nhan] LIKE '%?%' OR [tinh_thanh] LIKE '%?%' OR [dia_chi_chi_tiet] LIKE '%?%')");
                jdbcTemplate.update(
                        "UPDATE [dia_chi] SET [ten_nguoi_nhan] = N'Bố Trần Mua Hàng', [tinh_thanh] = N'Hải Phòng', " +
                        "[quan_huyen] = N'Lê Chân', [phuong_xa] = N'An Dương', [dia_chi_chi_tiet] = N'Số 88 đường Lạch Tray' " +
                        "WHERE [id] = 2 AND ([ten_nguoi_nhan] LIKE '%?%' OR [tinh_thanh] LIKE '%?%' OR [dia_chi_chi_tiet] LIKE '%?%')");

                // 5. Khắc phục bảng goi_combo (US28)
                jdbcTemplate.update(
                        "UPDATE [goi_combo] SET [ten_combo] = N'Tự Chọn Box 3 Quần Lót' " +
                        "WHERE [ma_combo] = 'BOX3-UNDERWEAR' AND [ten_combo] LIKE '%?%'");

                // 6. Khắc phục bảng ma_giam_gia (US26)
                jdbcTemplate.update(
                        "UPDATE [ma_giam_gia] SET [ten_voucher] = N'Giảm 20% tối đa 40k' " +
                        "WHERE [ma_code] = 'COOL20' AND [ten_voucher] LIKE '%?%'");
                jdbcTemplate.update(
                        "UPDATE [ma_giam_gia] SET [ten_voucher] = N'Freeship đơn bất kỳ' " +
                        "WHERE [ma_code] = 'FREESHIP30K' AND [ten_voucher] LIKE '%?%'");

                // 7. Khắc phục bảng phuong_thuc_thanh_toan (US18)
                jdbcTemplate.update(
                        "UPDATE [phuong_thuc_thanh_toan] SET [ten_phuong_thuc] = N'Thanh toán tiền mặt khi nhận hàng' " +
                        "WHERE [ma_phuong_thuc] = 'COD' AND [ten_phuong_thuc] LIKE '%?%'");
                jdbcTemplate.update(
                        "UPDATE [phuong_thuc_thanh_toan] SET [ten_phuong_thuc] = N'Cổng thanh toán VNPay (Quét QR)' " +
                        "WHERE [ma_phuong_thuc] = 'VNPAY_QR' AND [ten_phuong_thuc] LIKE '%?%'");
                jdbcTemplate.update(
                        "UPDATE [phuong_thuc_thanh_toan] SET [ten_phuong_thuc] = N'Ví điện tử MoMo' " +
                        "WHERE [ma_phuong_thuc] = 'MOMO_WALLET' AND [ten_phuong_thuc] LIKE '%?%'");

                // 8. Khắc phục bảng yeu_cau_doi_tra & chi_tiet_san_pham_doi_tra (US24, US25)
                jdbcTemplate.update(
                        "UPDATE [yeu_cau_doi_tra] SET [ly_do] = N'Mặc chật bụng' " +
                        "WHERE [ma_yeu_cau_rma] = 'RMA-001' AND [ly_do] LIKE '%?%'");
                jdbcTemplate.update(
                        "UPDATE [chi_tiet_san_pham_doi_tra] SET [tinh_trang_san_pham] = N'Còn nguyên tem mác' " +
                        "WHERE [id] = 1 AND [tinh_trang_san_pham] LIKE '%?%'");

                // 9. Khắc phục bảng flash_sale (US29)
                jdbcTemplate.update(
                        "UPDATE [flash_sale] SET [tieu_de] = N'Deal Sốc Giữa Trưa' " +
                        "WHERE [id] = 1 AND [tieu_de] LIKE '%?%'");

                // 9b. Khắc phục bảng danh_muc & bo_suu_tap (US06)
                jdbcTemplate.update(
                        "UPDATE [danh_muc] SET [ten_danh_muc] = N'Áo Thun Nam' " +
                        "WHERE [duong_dan_slug] = 'ao-thun-nam' AND [ten_danh_muc] LIKE '%?%'");
                jdbcTemplate.update(
                        "UPDATE [danh_muc] SET [ten_danh_muc] = N'Quần Short Nam' " +
                        "WHERE [duong_dan_slug] = 'quan-short-nam' AND [ten_danh_muc] LIKE '%?%'");
                jdbcTemplate.update(
                        "UPDATE [danh_muc] SET [ten_danh_muc] = N'Quần Lót Nam' " +
                        "WHERE [duong_dan_slug] = 'quan-lot-nam' AND [ten_danh_muc] LIKE '%?%'");
                jdbcTemplate.update(
                        "UPDATE [bo_suu_tap] SET [ten_bo_suu_tap] = N'Bộ Sưu Tập Chạy Bộ Pro' " +
                        "WHERE [duong_dan_slug] = 'bst-chay-bo-pro' AND [ten_bo_suu_tap] LIKE '%?%'");
                jdbcTemplate.update(
                        "UPDATE [bo_suu_tap] SET [ten_bo_suu_tap] = N'Mặc Hàng Ngày Essentials' " +
                        "WHERE [duong_dan_slug] = 'bst-mac-hang-ngay' AND [ten_bo_suu_tap] LIKE '%?%'");

                // 10. Khởi tạo danh mục trạng thái đơn hàng [trang_thai_don_hang] nếu chưa có (US05, US21, US22)
                Integer countTrangThai = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM [trang_thai_don_hang]", Integer.class);
                if (countTrangThai != null && countTrangThai == 0) {
                    jdbcTemplate.execute(
                            "SET IDENTITY_INSERT [trang_thai_don_hang] ON; " +
                            "INSERT INTO [trang_thai_don_hang] ([id], [ma_trang_thai], [ten_trang_thai], [ma_mau_hex]) VALUES " +
                            "(1, 'CHO_XAC_NHAN', N'Chờ xác nhận', '#F59E0B'), " +
                            "(2, 'DANG_XU_LY', N'Đang xử lý', '#3B82F6'), " +
                            "(3, 'DANG_GIAO', N'Đang giao hàng', '#8B5CF6'), " +
                            "(4, 'HOAN_THANH', N'Hoàn thành', '#10B981'), " +
                            "(5, 'DA_HUY', N'Đã hủy', '#EF4444'); " +
                            "SET IDENTITY_INSERT [trang_thai_don_hang] OFF;");
                }

                // 11. Khởi tạo dữ liệu đơn hàng mẫu [don_hang] nếu bảng đang trống để phục vụ biểu đồ Dashboard (US05)
                Integer countDonHang = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM [don_hang]", Integer.class);
                if (countDonHang != null && countDonHang == 0) {
                    jdbcTemplate.execute(
                            "INSERT INTO [don_hang] ([ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], " +
                            "[tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], " +
                            "[ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao]) VALUES " +
                            "('CM-1001', 1, 4, 1, 'GHTK881001', 520000, 40000, 0, 0, 480000, N'Trần Mua Hàng', '0901234567', N'Số 12 ngõ 45 phố Duy Tân, Cầu Giấy, Hà Nội', N'Giao giờ hành chính', DATEADD(hour, -1, GETDATE())), " +
                            "('CM-1002', 1, 3, NULL, 'GHTK881002', 350000, 0, 10000, 25000, 365000, N'Nguyễn Minh Tuấn', '0912888999', N'88 Láng Hạ, Đống Đa, Hà Nội', N'Gọi trước khi giao 15 phút', DATEADD(hour, -2, GETDATE())), " +
                            "('CM-1003', NULL, 1, NULL, NULL, 299000, 0, 0, 0, 299000, N'Lê Hoàng Nam', '0987654321', N'15 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh', N'Đơn khách đặt nhanh Combo', GETDATE()), " +
                            "('CM-0998', 1, 4, NULL, 'GHTK880998', 680000, 0, 0, 0, 680000, N'Trần Mua Hàng', '0901234567', N'Số 12 ngõ 45 phố Duy Tân, Cầu Giấy, Hà Nội', N'Đơn hoàn thành hôm qua', DATEADD(day, -1, GETDATE())), " +
                            "('CM-0997', NULL, 4, 2, 'GHTK880997', 420000, 30000, 0, 30000, 420000, N'Phạm Quốc Bảo', '0933444555', N'24 Lê Duẩn, Hải Châu, Đà Nẵng', N'Freeship 30k', DATEADD(day, -2, GETDATE())), " +
                            "('CM-0996', 1, 4, NULL, 'GHTK880996', 890000, 0, 20000, 0, 870000, N'Vũ Đình Trọng', '0977111222', N'102 Trần Phú, Nha Trang, Khánh Hòa', N'Áo Polo + Quần Jeans', DATEADD(day, -3, GETDATE())), " +
                            "('CM-0995', NULL, 5, NULL, NULL, 250000, 0, 0, 25000, 275000, N'Đặng Văn Lâm', '0966333444', N'Số 88 đường Lạch Tray, Lê Chân, Hải Phòng', N'Khách đổi ý chọn lại size', DATEADD(day, -4, GETDATE())), " +
                            "('CM-0994', 1, 4, 1, 'GHTK880994', 550000, 40000, 0, 0, 510000, N'Hoàng Đức Anh', '0944555666', N'56 Cầu Giấy, Hà Nội', N'Đã giao thành công', DATEADD(day, -5, GETDATE()));");
                }

                // 12. Khởi tạo dữ liệu mẫu Màu sắc [mau_sac] nếu bảng đang trống (US07)
                Integer countMauSac = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM [mau_sac]", Integer.class);
                if (countMauSac != null && countMauSac == 0) {
                    jdbcTemplate.execute(
                            "INSERT INTO [mau_sac] ([ma_mau], [ten_mau]) VALUES " +
                            "('#000000', N'Đen'), " +
                            "('#FFFFFF', N'Trắng'), " +
                            "('#1E3A8A', N'Xanh Navy'), " +
                            "('#4B5563', N'Xám Tiêu'), " +
                            "('#065F46', N'Xanh Rêu'), " +
                            "('#7F1D1D', N'Đỏ Đô');");
                }

                // 13. Khởi tạo dữ liệu mẫu Kích thước [kich_thuoc] nếu bảng đang trống (US07)
                Integer countKichThuoc = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM [kich_thuoc]", Integer.class);
                if (countKichThuoc != null && countKichThuoc == 0) {
                    jdbcTemplate.execute(
                            "INSERT INTO [kich_thuoc] ([ma_kich_thuoc], [ten_kich_thuoc]) VALUES " +
                            "('S', 'S'), " +
                            "('M', 'M'), " +
                            "('L', 'L'), " +
                            "('XL', 'XL'), " +
                            "('2XL', '2XL'), " +
                            "('3XL', '3XL');");
                }

                // Chuẩn hóa tên màu sắc & kích thước ngắn gọn, loại bỏ text cân nặng gây vỡ bảng
                try {
                    jdbcTemplate.execute(
                            "UPDATE [kich_thuoc] SET [ten_kich_thuoc] = [ma_kich_thuoc] WHERE [ten_kich_thuoc] LIKE N'%kg%'; " +
                            "UPDATE [mau_sac] SET [ten_mau] = N'Đen' WHERE [ten_mau] = N'Đen Tuyền'; " +
                            "UPDATE [mau_sac] SET [ten_mau] = N'Trắng' WHERE [ten_mau] = N'Trắng Tinh Khôi'; " +
                            "UPDATE [mau_sac] SET [ten_mau] = N'Xám Tiêu' WHERE [ten_mau] = N'Xám Heather'; " +
                            "UPDATE [bien_the_san_pham] SET [ma_sku] = 'SP-POLO-EXCOOL-01-DEN-S' WHERE [ma_sku] = 'SP-POLO-EXCOOL-01-MAU1-SIZE1'; " +
                            "UPDATE [bien_the_san_pham] SET [ma_sku] = 'SP-POLO-EXCOOL-01-DODO-3XL' WHERE [ma_sku] = 'SP-POLO-EXCOOL-01-MAU2-SIZE2';"
                    );
                } catch (Exception ignored) {}

                // 14. Khởi tạo Danh mục chuẩn & Sản phẩm mẫu [san_pham] nếu bảng đang trống (US08)
                Integer countDanhMucAoPolo = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM [danh_muc] WHERE [duong_dan_slug] = 'ao-polo-nam'", Integer.class);
                if (countDanhMucAoPolo != null && countDanhMucAoPolo == 0) {
                    jdbcTemplate.execute(
                            "INSERT INTO [danh_muc] ([ma_danh_muc_cha], [ten_danh_muc], [duong_dan_slug], [thu_tu_hien_thi], [kich_hoat]) VALUES " +
                            "(NULL, N'Áo Polo Nam', 'ao-polo-nam', 1, 1);");
                }
                Integer countDanhMucAoThun = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM [danh_muc] WHERE [duong_dan_slug] = 'ao-thun-nam'", Integer.class);
                if (countDanhMucAoThun != null && countDanhMucAoThun == 0) {
                    jdbcTemplate.execute(
                            "INSERT INTO [danh_muc] ([ma_danh_muc_cha], [ten_danh_muc], [duong_dan_slug], [thu_tu_hien_thi], [kich_hoat]) VALUES " +
                            "(NULL, N'Áo Thun Nam', 'ao-thun-nam', 2, 1);");
                }

                Integer countSanPham = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM [san_pham]", Integer.class);
                if (countSanPham != null && countSanPham == 0) {
                    Long dmPoloId = jdbcTemplate.queryForObject(
                            "SELECT TOP 1 [id] FROM [danh_muc] WHERE [duong_dan_slug] = 'ao-polo-nam' ORDER BY [id] ASC", Long.class);
                    Long dmThunId = jdbcTemplate.queryForObject(
                            "SELECT TOP 1 [id] FROM [danh_muc] WHERE [duong_dan_slug] = 'ao-thun-nam' ORDER BY [id] ASC", Long.class);
                    Long bstDauTienId = jdbcTemplate.queryForObject(
                            "SELECT TOP 1 [id] FROM [bo_suu_tap] ORDER BY [id] ASC", Long.class);

                    if (dmPoloId != null && dmThunId != null) {
                        jdbcTemplate.update(
                                "INSERT INTO [san_pham] ([ma_danh_muc_id], [ma_bo_suu_tap_id], [ma_san_pham], [ten_san_pham], [duong_dan_slug], " +
                                "[chat_lieu], [form_dang], [mo_ta_chi_tiet], [huong_dan_chon_size], [gia_co_ban], [noi_bat], [kich_hoat], [ngay_tao]) VALUES " +
                                "(?, ?, 'SP-POLO-EXCOOL-01', N'Áo Polo Nam Excool Công Nghệ Làm Mát', 'ao-polo-nam-excool-cong-nghe-lam-mat', " +
                                "N'Vải Excool Công nghệ Làm Mát', N'Regular Fit', " +
                                "N'Áo Polo nam ứng dụng sợi Sorona co giãn 4 chiều, chống tia UV UPF 50+, thoáng khí và nhanh khô gấp 2 lần cotton thường.', " +
                                "N'Size S (48-58kg) | Size M (59-67kg) | Size L (68-75kg) | Size XL (76-83kg) | Size 2XL (84-92kg)', 299000, 1, 1, GETDATE()), " +
                                "(?, ?, 'SP-TSHIRT-COMPACT-02', N'Áo Thun Nam Cotton Compact 220gsm', 'ao-thun-nam-cotton-compact-220gsm', " +
                                "N'100% Cotton Compact 2 chiều', N'Relaxed Fit', " +
                                "N'Áo thun trơn Basic SIMPLAIN STORE dệt từ sợi Cotton Compact hạn chế xù lông, bề mặt vải mịn màng và giữ form cổ áo bền bỉ.', " +
                                "N'Size S (48-58kg) | Size M (59-67kg) | Size L (68-75kg) | Size XL (76-83kg)', 199000, 1, 1, GETDATE()), " +
                                "(?, NULL, 'SP-POLO-CAFE-03', N'Áo Polo Nam Sợi Café Khử Mùi Tối Ưu', 'ao-polo-nam-soi-cafe-khu-mui-toi-uu', " +
                                "N'Vải Café Khử Mùi Phối Spandex', N'Slim Fit', " +
                                "N'Chất liệu vải làm từ bã cà phê tái chế thân thiện môi trường, kiểm soát mùi cơ thể vượt trội và siêu nhẹ.', " +
                                "N'Size M (59-67kg) | Size L (68-75kg) | Size XL (76-83kg)', 329000, 0, 1, GETDATE())",
                                dmPoloId, bstDauTienId, dmThunId, bstDauTienId, dmPoloId
                        );
                    }
                }

                // 15. Khởi tạo dữ liệu mẫu Biến thể SKU [bien_the_san_pham] & Hình ảnh [hinh_anh] nếu đang trống (US09)
                Integer countBienThe = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM [bien_the_san_pham]", Integer.class);
                if (countBienThe != null && countBienThe == 0) {
                    Long spPoloId = jdbcTemplate.queryForObject(
                            "SELECT TOP 1 [id] FROM [san_pham] ORDER BY [id] ASC", Long.class);
                    Long mauDenId = jdbcTemplate.queryForObject(
                            "SELECT TOP 1 [id] FROM [mau_sac] ORDER BY [id] ASC", Long.class);
                    Long sizeLId = jdbcTemplate.queryForObject(
                            "SELECT TOP 1 [id] FROM [kich_thuoc] ORDER BY [id] ASC", Long.class);

                    if (spPoloId != null && mauDenId != null && sizeLId != null) {
                        jdbcTemplate.update(
                                "INSERT INTO [bien_the_san_pham] ([ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], " +
                                "[ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES " +
                                "(?, ?, ?, 'SP-POLO-EXCOOL-01-MAU1-SIZE1', 'BC-SP-POLO-EXCOOL-01-M1S1', 45, 299000, 180000, 1)",
                                spPoloId, mauDenId, sizeLId
                        );
                        Long btDauTienId = jdbcTemplate.queryForObject(
                                "SELECT TOP 1 [id] FROM [bien_the_san_pham] WHERE [ma_sku] = 'SP-POLO-EXCOOL-01-MAU1-SIZE1'", Long.class);
                        if (btDauTienId != null) {
                            jdbcTemplate.update(
                                    "INSERT INTO [hinh_anh] ([ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES " +
                                    "(?, ?, 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=500&auto=format&fit=crop&q=80', 1, 1), " +
                                    "(?, ?, 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&auto=format&fit=crop&q=80', 0, 2)",
                                    spPoloId, btDauTienId, spPoloId, btDauTienId
                            );
                        }
                    }
                }

                // 15b. Bổ sung thêm biến thể Màu 2 + Size 2 cho SP-POLO-EXCOOL-01 kèm ảnh riêng & tồn kho khác nhau phục vụ US11
                Long spPoloMainId = jdbcTemplate.queryForObject(
                        "SELECT TOP 1 [id] FROM [san_pham] WHERE [ma_san_pham] = 'SP-POLO-EXCOOL-01'", Long.class);
                if (spPoloMainId != null) {
                    Integer countBtPolo = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM [bien_the_san_pham] WHERE [ma_san_pham_id] = ?", Integer.class, spPoloMainId);
                    if (countBtPolo != null && countBtPolo < 2) {
                        Long mauThuHaiId = jdbcTemplate.queryForObject(
                                "SELECT TOP 1 [id] FROM [mau_sac] ORDER BY [id] DESC", Long.class);
                        Long sizeThuHaiId = jdbcTemplate.queryForObject(
                                "SELECT TOP 1 [id] FROM [kich_thuoc] ORDER BY [id] DESC", Long.class);
                        if (mauThuHaiId != null && sizeThuHaiId != null) {
                            Integer daTonTaiCap = jdbcTemplate.queryForObject(
                                    "SELECT COUNT(*) FROM [bien_the_san_pham] WHERE [ma_san_pham_id] = ? AND [ma_mau_sac_id] = ? AND [ma_kich_thuoc_id] = ?",
                                    Integer.class, spPoloMainId, mauThuHaiId, sizeThuHaiId);
                            if (daTonTaiCap != null && daTonTaiCap == 0) {
                                jdbcTemplate.update(
                                        "INSERT INTO [bien_the_san_pham] ([ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], " +
                                        "[ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES " +
                                        "(?, ?, ?, 'SP-POLO-EXCOOL-01-MAU2-SIZE2', 'BC-SP-POLO-EXCOOL-01-M2S2', 28, 309000, 185000, 1)",
                                        spPoloMainId, mauThuHaiId, sizeThuHaiId
                                );
                                Long btPolo2Id = jdbcTemplate.queryForObject(
                                        "SELECT TOP 1 [id] FROM [bien_the_san_pham] WHERE [ma_sku] = 'SP-POLO-EXCOOL-01-MAU2-SIZE2'", Long.class);
                                if (btPolo2Id != null) {
                                    jdbcTemplate.update(
                                            "INSERT INTO [hinh_anh] ([ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES " +
                                            "(?, ?, 'https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=600&auto=format&fit=crop&q=80', 1, 1), " +
                                            "(?, ?, 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600&auto=format&fit=crop&q=80', 0, 2)",
                                            spPoloMainId, btPolo2Id, spPoloMainId, btPolo2Id
                                    );
                                }
                            }
                        }
                    }
                }

                // 16. Bổ sung biến thể SKU & hình ảnh đa dạng Màu/Size cho các sản phẩm mẫu phục vụ lọc Storefront (US10)
                Long spTshirtId = jdbcTemplate.queryForObject(
                        "SELECT TOP 1 [id] FROM [san_pham] WHERE [ma_san_pham] = 'SP-TSHIRT-COMPACT-02'", Long.class);
                if (spTshirtId != null) {
                    Integer countBtTshirt = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM [bien_the_san_pham] WHERE [ma_san_pham_id] = ?", Integer.class, spTshirtId);
                    if (countBtTshirt != null && countBtTshirt == 0) {
                        Long mauTrangId = jdbcTemplate.queryForObject(
                                "SELECT TOP 1 [id] FROM [mau_sac] ORDER BY [id] DESC", Long.class);
                        Long sizeMId = jdbcTemplate.queryForObject(
                                "SELECT TOP 1 [id] FROM [kich_thuoc] ORDER BY [id] DESC", Long.class);
                        if (mauTrangId != null && sizeMId != null) {
                            jdbcTemplate.update(
                                    "INSERT INTO [bien_the_san_pham] ([ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], " +
                                    "[ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES " +
                                    "(?, ?, ?, 'SP-TSHIRT-COMPACT-02-TRANG-M', 'BC-SP-TSHIRT-02-TRM', 60, 199000, 120000, 1)",
                                    spTshirtId, mauTrangId, sizeMId
                            );
                            Long btTshirtId = jdbcTemplate.queryForObject(
                                    "SELECT TOP 1 [id] FROM [bien_the_san_pham] WHERE [ma_sku] = 'SP-TSHIRT-COMPACT-02-TRANG-M'", Long.class);
                            if (btTshirtId != null) {
                                jdbcTemplate.update(
                                        "INSERT INTO [hinh_anh] ([ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES " +
                                        "(?, ?, 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&auto=format&fit=crop&q=80', 1, 1)",
                                        spTshirtId, btTshirtId
                                );
                            }
                        }
                    }
                }

                Long spCafeId = jdbcTemplate.queryForObject(
                        "SELECT TOP 1 [id] FROM [san_pham] WHERE [ma_san_pham] = 'SP-POLO-CAFE-03'", Long.class);
                if (spCafeId != null) {
                    Integer countBtCafe = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM [bien_the_san_pham] WHERE [ma_san_pham_id] = ?", Integer.class, spCafeId);
                    if (countBtCafe != null && countBtCafe == 0) {
                        Long mauDauId = jdbcTemplate.queryForObject(
                                "SELECT TOP 1 [id] FROM [mau_sac] ORDER BY [id] ASC", Long.class);
                        Long sizeDauId = jdbcTemplate.queryForObject(
                                "SELECT TOP 1 [id] FROM [kich_thuoc] ORDER BY [id] ASC", Long.class);
                        if (mauDauId != null && sizeDauId != null) {
                            jdbcTemplate.update(
                                    "INSERT INTO [bien_the_san_pham] ([ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], " +
                                    "[ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES " +
                                    "(?, ?, ?, 'SP-POLO-CAFE-03-DEN-S', 'BC-SP-POLO-CAFE-03-DS', 30, 329000, 210000, 1)",
                                    spCafeId, mauDauId, sizeDauId
                            );
                            Long btCafeId = jdbcTemplate.queryForObject(
                                    "SELECT TOP 1 [id] FROM [bien_the_san_pham] WHERE [ma_sku] = 'SP-POLO-CAFE-03-DEN-S'", Long.class);
                            if (btCafeId != null) {
                                jdbcTemplate.update(
                                        "INSERT INTO [hinh_anh] ([ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES " +
                                        "(?, ?, 'https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=500&auto=format&fit=crop&q=80', 1, 1)",
                                        spCafeId, btCafeId
                                );
                            }
                        }
                    }
                }

                // 16. Khởi tạo danh mục Hạng thành viên Loyalty [hang_thanh_vien] nếu đang trống (US16)
                Integer countHang = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM [hang_thanh_vien]", Integer.class);
                if (countHang != null && countHang == 0) {
                    jdbcTemplate.execute(
                            "SET IDENTITY_INSERT [hang_thanh_vien] ON; " +
                            "INSERT INTO [hang_thanh_vien] ([id], [ten_hang], [chi_tieu_nam_toi_thieu], [phan_tram_hoan_tien]) VALUES " +
                            "(1, N'Đồng', 0, 1.0), " +
                            "(2, N'Bạc', 1000000, 3.0), " +
                            "(3, N'Vàng', 3000000, 5.0), " +
                            "(4, N'Bạch Kim', 6000000, 8.0); " +
                            "SET IDENTITY_INSERT [hang_thanh_vien] OFF;");
                }
            } catch (Exception ignored) {
            }
        };
    }
}
