-- =========================================================================================================
-- FILE SCRIPT MIGRATION: 5 DANH MỤC CHA, 19 DANH MỤC CON & ĐẦY ĐỦ THUỘC TÍNH SẢN PHẨM CHUYÊN NGHIỆP
-- Hệ thống: SIMPLAIN STORE
-- Cơ sở dữ liệu: SQL Server (Simpl4)
-- Hỗ trợ đầy đủ: Thuộc tính Kích thước, Màu sắc, Chất liệu, Form dáng, Bảng size và Biến thể SKU mẫu
-- =========================================================================================================

SET NOCOUNT ON;
PRINT N'===> BẮT ĐẦU CẬP NHẬT CẤU TRÚC 5 DANH MỤC CHA VÀ 19 DANH MỤC CON CHUYÊN NGHIỆP...';

-- =========================================================================================================
-- 1. BẢNG MÀU SẮC THỜI TRANG (ĐẦY ĐỦ BẢNG MÃ HEX & TÊN TIẾNG VIỆT)
-- =========================================================================================================
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'mau_sac')
BEGIN
    PRINT N'Bảng mau_sac chưa tồn tại, vui lòng kiểm tra Hibernate DDL.';
END
ELSE
BEGIN
    DECLARE @dsMau TABLE (maMau VARCHAR(20), tenMau NVARCHAR(100));
    INSERT INTO @dsMau VALUES
        ('#000000', N'Đen'),
        ('#FFFFFF', N'Trắng'),
        ('#1E3A8A', N'Xanh Navy'),
        ('#64748B', N'Xám Tiêu'),
        ('#8B4513', N'Nâu Da Bò'),
        ('#C0C0C0', N'Bạc Sang Trọng'),
        ('#FFD700', N'Vàng Ánh Kim'),
        ('#F472B6', N'Hồng Pastel'),
        ('#D2B48C', N'Be Vintage'),
        ('#047857', N'Xanh Rêu'),
        ('#B91C1C', N'Đỏ Đô');

    MERGE INTO [mau_sac] AS Target
    USING @dsMau AS Source
    ON (Target.[ma_mau] = Source.maMau)
    WHEN NOT MATCHED THEN
        INSERT ([ma_mau], [ten_mau])
        VALUES (Source.maMau, Source.tenMau);
    PRINT N'-> Đã hoàn tất đồng bộ danh sách bảng màu sắc thời trang.';
END
GO

-- =========================================================================================================
-- 2. BẢNG KÍCH THƯỚC CHUẨN THEO TỪNG NGÀNH HÀNG (ÁO/ĐẦM, QUẦN SỐ, GIÀY DÉP, PHỤ KIỆN)
-- =========================================================================================================
IF EXISTS (SELECT 1 FROM sys.tables WHERE name = 'kich_thuoc')
BEGIN
    DECLARE @dsSize TABLE (maSize VARCHAR(50), tenSize NVARCHAR(100));
    INSERT INTO @dsSize VALUES
        -- Size chữ áo, áo khoác, đầm, đồ bộ
        ('XS', N'Size XS'), ('S', N'Size S'), ('M', N'Size M'), ('L', N'Size L'),
        ('XL', N'Size XL'), ('2XL', N'Size 2XL'), ('3XL', N'Size 3XL'), ('Freesize', N'Freesize'),
        -- Size số quần nam & nữ
        ('28', N'Size 28'), ('29', N'Size 29'), ('30', N'Size 30'), ('31', N'Size 31'),
        ('32', N'Size 32'), ('33', N'Size 33'), ('34', N'Size 34'),
        -- Size giày dép nữ & nam
        ('35', N'Size 35'), ('36', N'Size 36'), ('37', N'Size 37'), ('38', N'Size 38'),
        ('39', N'Size 39'), ('40', N'Size 40'), ('41', N'Size 41'), ('42', N'Size 42'),
        ('43', N'Size 43'), ('44', N'Size 44'),
        -- Size túi xách, balo, ví
        ('Tiêu chuẩn', N'Tiêu chuẩn'), ('Mini', N'Mini (Dưới 20cm)'), ('Medium', N'Medium (20-30cm)'), ('Large', N'Large (Trên 30cm)'),
        -- Size trang sức & phụ kiện
        ('Mặt 38mm', N'Mặt 38mm'), ('Mặt 40mm', N'Mặt 40mm'), ('Mặt 42mm', N'Mặt 42mm'),
        ('Ni 16', N'Ni 16 (Chu vi 52mm)'), ('Ni 17', N'Ni 17 (Chu vi 54mm)'), ('Ni 18', N'Ni 18 (Chu vi 57mm)'),
        ('Dài 115cm', N'Dài 115cm (Bụng < 85cm)'), ('Dài 120cm', N'Dài 120cm (Bụng 85-95cm)'), ('Dài 125cm', N'Dài 125cm (Bụng > 95cm)');

    MERGE INTO [kich_thuoc] AS Target
    USING @dsSize AS Source
    ON (Target.[ma_kich_thuoc] = Source.maSize)
    WHEN NOT MATCHED THEN
        INSERT ([ma_kich_thuoc], [ten_kich_thuoc])
        VALUES (Source.maSize, Source.tenSize);
    PRINT N'-> Đã hoàn tất đồng bộ danh mục kích thước chuẩn ngành hàng.';
END
GO

-- =========================================================================================================
-- 3. BẢNG CHẤT LIỆU ĐẶC THÙ CHO CÁC MẶT HÀNG THỜI TRANG & PHỤ KIỆN
-- =========================================================================================================
IF EXISTS (SELECT 1 FROM sys.tables WHERE name = 'chat_lieu')
BEGIN
    DECLARE @dsChatLieu TABLE (tenCL NVARCHAR(255), moTa NVARCHAR(500));
    INSERT INTO @dsChatLieu VALUES
        (N'Cotton Compact 100%', N'Sợi bông chải kỹ mềm mịn, thấm hút mồ hôi tối ưu'),
        (N'Cotton Pima cao cấp', N'Bông Pima dệt thoi mịn màng chống bai giãn xù lông'),
        (N'Vải Oxford dệt thoi', N'Chất liệu sơ mi đứng form, bền màu, thoáng mát'),
        (N'Vải đũi tự nhiên', N'Mát nhẹ, thấm hút mồ hôi, thân thiện môi trường'),
        (N'Dạ Tweed sang trọng', N'Vải dạ dệt dày dặn giữ nhiệt tốt, phong cách quý phái'),
        (N'Denim 12oz dệt thoi', N'Vải jean cotton dệt chéo đứng dáng, bền bỉ'),
        (N'Denim co giãn Spandex', N'Jean pha sợi co giãn thoải mái khi vận động'),
        (N'Kaki cotton twill', N'Kaki dày dặn, không bai xù, chuẩn form công sở'),
        (N'Tuyết mưa cao cấp', N'Vải may quần tây co giãn nhẹ, không nhăn nhàu'),
        (N'Tơ sống óng ánh', N'Vải tơ mềm mại bắt sáng cao cấp cho đầm váy nữ'),
        (N'Voan hoa chiffon', N'Vải voan mỏng nhẹ bay bổng, nữ tính duyên dáng'),
        (N'Lụa Satin cao cấp', N'Bề mặt bóng mịn, mềm mát, tôn nét sang trọng'),
        (N'Nỉ da cá chân cua 380gsm', N'Nỉ bông dệt dày dặn, giữ ấm tối ưu cho mùa đông'),
        (N'Thun thể thao Spandex', N'Co giãn 4 chiều, khô nhanh, chuyên dụng tập luyện'),
        (N'Da bò hạt Epsom', N'Da thuộc cao cấp dập vân chống trầy xước nước'),
        (N'Da Mastrotto Ý', N'Da bò nhập khẩu Ý mềm mịn tự nhiên bền đẹp'),
        (N'Vải Canvas chống nước', N'Vải bố dệt sợi mật độ cao phủ PU chống thấm'),
        (N'Bạc Ý 925 xi bạch kim', N'Bạc chuẩn 92.5% sáng bóng, chống xỉn màu oxy hóa'),
        (N'Titanium mạ PVD siêu bền', N'Hợp kim titan siêu nhẹ chống ăn mòn, mạ chân không'),
        (N'Thép không gỉ 316L', N'Thép chuyên dụng đồng hồ cao cấp chống rỉ sét'),
        (N'Da bò sáp nguyên tấm', N'Da bò mộc dẻo dai, càng dùng càng bóng đẹp'),
        (N'Vải Flyknit thoáng khí', N'Sợi dệt nguyên khối co giãn ôm chân êm ái'),
        (N'Da Nappa cao cấp', N'Da cừu hoặc bê thuộc mềm mịn, ôm chân'),
        (N'Cao su lưu hóa chống trơn', N'Đế giày đúc cao su nguyên khối bám đường vượt trội'),
        (N'Len Cashmere cao cấp', N'Sợi len dệt siêu mịn màng, ấm áp êm ái cho mùa đông');

    MERGE INTO [chat_lieu] AS Target
    USING @dsChatLieu AS Source
    ON (Target.[ten_chat_lieu] = Source.tenCL)
    WHEN NOT MATCHED THEN
        INSERT ([ten_chat_lieu], [mo_ta], [kich_hoat])
        VALUES (Source.tenCL, Source.moTa, 1);
    PRINT N'-> Đã hoàn tất đồng bộ danh mục chất liệu đặc thù.';
END
GO

-- =========================================================================================================
-- 4. BẢNG FORM DÁNG CHUẨN CHO TỪNG NHÓM SẢN PHẨM
-- =========================================================================================================
IF EXISTS (SELECT 1 FROM sys.tables WHERE name = 'form_dang')
BEGIN
    DECLARE @dsFormDang TABLE (tenFD NVARCHAR(255), moTa NVARCHAR(500));
    INSERT INTO @dsFormDang VALUES
        (N'Regular Fit', N'Dáng suông tiêu chuẩn vừa vặn thoải mái'),
        (N'Slim Fit', N'Dáng ôm nhẹ tôn đường nét cơ thể'),
        (N'Oversize', N'Dáng rộng phóng khoáng trẻ trung'),
        (N'Boxy Streetwear', N'Thân áo hộp rộng ngắn phong cách đường phố'),
        (N'Loose Fit', N'Dáng rộng rãi tạo cảm giác thoải mái tối đa'),
        (N'Ống suông (Straight Leg)', N'Quần ống đứng thẳng từ đùi xuống gấu'),
        (N'Ống côn (Tapered)', N'Quần thon dần về cổ chân hiện đại'),
        (N'Ống rộng (Wide Leg)', N'Quần ống suông rộng cá tính che khuyết điểm'),
        (N'Slim Crop', N'Quần ống ôm nhẹ trên mắt cá chân tôn chiều cao'),
        (N'Xòe chữ A (A-Line)', N'Đầm váy chiết eo xòe rộng nhẹ nhàng'),
        (N'Ôm body quyến rũ', N'Thiết kế ôm sát tôn trọn 3 vòng gợi cảm'),
        (N'Xếp ly thanh lịch', N'Chi tiết ly dập tinh tế sang trọng'),
        (N'Streetwear Boxy', N'Set đồ / Hoodie dáng rộng vuông vắn'),
        (N'Athletic Slim', N'Form đồ tập thể thao ôm nhẹ vận động linh hoạt'),
        (N'Túi bao tử (Chest Bag)', N'Đeo chéo trước ngực năng động'),
        (N'Balo nắp cuộn (Roll-top)', N'Thiết kế cuộn nắp chống tràn nước thời trang'),
        (N'Ví gập đôi Bifold', N'Ví tiền gập đôi kinh điển nhỏ gọn'),
        (N'Mặt tròn cổ điển', N'Thiết kế mặt đồng hồ tròn thanh lịch'),
        (N'Dây xích Cuban cá tính', N'Kiểu dáng mắt xích đan cài mạnh mẽ'),
        (N'Mũ lưỡi trai (Cap)', N'Mũ kết che nắng thể thao hiện đại'),
        (N'Mũ Bucket vành tròn', N'Mũ xô vành tròn phong cách dạo phố'),
        (N'Low-Top cổ thấp', N'Giày thể thao sneaker cổ thấp linh hoạt'),
        (N'High-Top cổ cao', N'Giày sneaker cổ cao bảo vệ cổ chân cá tính'),
        (N'Derby công sở', N'Giày tây cột dây thanh lịch chuyên nghiệp'),
        (N'Loafer xỏ tiện lợi', N'Giày mọi không dây tiện dụng sang trọng');

    MERGE INTO [form_dang] AS Target
    USING @dsFormDang AS Source
    ON (Target.[ten_form_dang] = Source.tenFD)
    WHEN NOT MATCHED THEN
        INSERT ([ten_form_dang], [mo_ta], [kich_hoat])
        VALUES (Source.tenFD, Source.moTa, 1);
    PRINT N'-> Đã hoàn tất đồng bộ danh mục form dáng chuẩn.';
END
GO

-- =========================================================================================================
-- 5. CÂY DANH MỤC: ĐÚNG 5 DANH MỤC CHA VÀ 19 DANH MỤC CON CHUẨN CHỈNH
-- =========================================================================================================
IF EXISTS (SELECT 1 FROM sys.tables WHERE name = 'danh_muc')
BEGIN
    PRINT N'===> ĐỒNG BỘ 5 DANH MỤC CHA VÀ 19 DANH MỤC CON...';

    -- 1. Danh mục Cha: Thời trang nam (thoi-trang-nam)
    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'thoi-trang-nam')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat])
        VALUES (N'Thời trang nam', 'thoi-trang-nam', NULL, 1, 1);
    DECLARE @idNam BIGINT = (SELECT TOP 1 [id] FROM [danh_muc] WHERE [duong_dan_slug] = 'thoi-trang-nam');

    -- Con của Thời trang nam: Áo, Quần, Áo khoác
    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'ao-nam')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Áo', 'ao-nam', @idNam, 1, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Áo', [ma_danh_muc_cha] = @idNam WHERE [duong_dan_slug] = 'ao-nam';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'quan-nam')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Quần', 'quan-nam', @idNam, 2, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Quần', [ma_danh_muc_cha] = @idNam WHERE [duong_dan_slug] = 'quan-nam';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'ao-khoac-nam')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Áo khoác', 'ao-khoac-nam', @idNam, 3, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Áo khoác', [ma_danh_muc_cha] = @idNam WHERE [duong_dan_slug] = 'ao-khoac-nam';

    -- 2. Danh mục Cha: Thời trang nữ (thoi-trang-nu)
    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'thoi-trang-nu')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat])
        VALUES (N'Thời trang nữ', 'thoi-trang-nu', NULL, 2, 1);
    DECLARE @idNu BIGINT = (SELECT TOP 1 [id] FROM [danh_muc] WHERE [duong_dan_slug] = 'thoi-trang-nu');

    -- Con của Thời trang nữ: Áo, Váy, Đầm, Quần
    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'ao-nu')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Áo', 'ao-nu', @idNu, 1, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Áo', [ma_danh_muc_cha] = @idNu WHERE [duong_dan_slug] = 'ao-nu';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'vay-nu')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Váy', 'vay-nu', @idNu, 2, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Váy', [ma_danh_muc_cha] = @idNu WHERE [duong_dan_slug] = 'vay-nu';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'dam-nu')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Đầm', 'dam-nu', @idNu, 3, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Đầm', [ma_danh_muc_cha] = @idNu WHERE [duong_dan_slug] = 'dam-nu';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'quan-nu')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Quần', 'quan-nu', @idNu, 4, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Quần', [ma_danh_muc_cha] = @idNu WHERE [duong_dan_slug] = 'quan-nu';

    -- 3. Danh mục Cha: Giày dép (giay-dep)
    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'giay-dep')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat])
        VALUES (N'Giày dép', 'giay-dep', NULL, 3, 1);
    DECLARE @idGiay BIGINT = (SELECT TOP 1 [id] FROM [danh_muc] WHERE [duong_dan_slug] = 'giay-dep');

    -- Con của Giày dép: Sneaker, Sandal, Dép
    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'sneaker')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Sneaker', 'sneaker', @idGiay, 1, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Sneaker', [ma_danh_muc_cha] = @idGiay WHERE [duong_dan_slug] = 'sneaker';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'sandal')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Sandal', 'sandal', @idGiay, 2, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Sandal', [ma_danh_muc_cha] = @idGiay WHERE [duong_dan_slug] = 'sandal';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'dep')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Dép', 'dep', @idGiay, 3, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Dép', [ma_danh_muc_cha] = @idGiay WHERE [duong_dan_slug] = 'dep';

    -- 4. Danh mục Cha: Ví và balo (vi-va-balo)
    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'vi-va-balo')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat])
        VALUES (N'Ví và balo', 'vi-va-balo', NULL, 4, 1);
    DECLARE @idBalo BIGINT = (SELECT TOP 1 [id] FROM [danh_muc] WHERE [duong_dan_slug] = 'vi-va-balo');

    -- Con của Ví và balo: Túi xách, Balo, Ví
    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'tui-xach')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Túi xách', 'tui-xach', @idBalo, 1, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Túi xách', [ma_danh_muc_cha] = @idBalo WHERE [duong_dan_slug] = 'tui-xach';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'balo')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Balo', 'balo', @idBalo, 2, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Balo', [ma_danh_muc_cha] = @idBalo WHERE [duong_dan_slug] = 'balo';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'vi')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Ví', 'vi', @idBalo, 3, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Ví', [ma_danh_muc_cha] = @idBalo WHERE [duong_dan_slug] = 'vi';

    -- 5. Danh mục Cha: Phụ kiện (phu-kien)
    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'phu-kien')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat])
        VALUES (N'Phụ kiện', 'phu-kien', NULL, 5, 1);
    DECLARE @idPK BIGINT = (SELECT TOP 1 [id] FROM [danh_muc] WHERE [duong_dan_slug] = 'phu-kien');

    -- Con của Phụ kiện: Đồng hồ, Kính, Mũ, Thắt lưng, Trang sức, Khăn choàng
    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'dong-ho')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Đồng hồ', 'dong-ho', @idPK, 1, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Đồng hồ', [ma_danh_muc_cha] = @idPK WHERE [duong_dan_slug] = 'dong-ho';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'kinh')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Kính', 'kinh', @idPK, 2, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Kính', [ma_danh_muc_cha] = @idPK WHERE [duong_dan_slug] = 'kinh';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'mu')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Mũ', 'mu', @idPK, 3, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Mũ', [ma_danh_muc_cha] = @idPK WHERE [duong_dan_slug] = 'mu';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'that-lung')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Thắt lưng', 'that-lung', @idPK, 4, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Thắt lưng', [ma_danh_muc_cha] = @idPK WHERE [duong_dan_slug] = 'that-lung';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'trang-suc')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Trang sức', 'trang-suc', @idPK, 5, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Trang sức', [ma_danh_muc_cha] = @idPK WHERE [duong_dan_slug] = 'trang-suc';

    IF NOT EXISTS (SELECT 1 FROM [danh_muc] WHERE [duong_dan_slug] = 'khan-choang')
        INSERT INTO [danh_muc] ([ten_danh_muc], [duong_dan_slug], [ma_danh_muc_cha], [thu_tu_hien_thi], [kich_hoat]) VALUES (N'Khăn choàng', 'khan-choang', @idPK, 6, 1);
    ELSE UPDATE [danh_muc] SET [ten_danh_muc] = N'Khăn choàng', [ma_danh_muc_cha] = @idPK WHERE [duong_dan_slug] = 'khan-choang';

    PRINT N'-> Đã hoàn tất khởi tạo 5 Danh mục Cha và 19 Danh mục Con chuẩn xác!';
END
GO

-- =========================================================================================================
-- 6. TỰ ĐỘNG CHÈN 19 SẢN PHẨM MẪU ĐẠI DIỆN CHO 19 DANH MỤC CON VỚI ĐẦY ĐỦ THUỘC TÍNH & SKU
-- =========================================================================================================
PRINT N'===> ĐỒNG BỘ 19 MẶT HÀNG ĐẠI DIỆN VỚI ĐẦY ĐỦ THUỘC TÍNH CHUYÊN NGHIỆP...';

DECLARE @tblSp TABLE (
    maSp VARCHAR(50), tenSp NVARCHAR(255), slug VARCHAR(255), slugDm VARCHAR(100),
    chatLieu NVARCHAR(255), formDang NVARCHAR(255), sizeHd NVARCHAR(500), moTa NVARCHAR(MAX),
    giaBan BIGINT, imgUrl VARCHAR(500)
);

INSERT INTO @tblSp VALUES
-- 1. Thời trang nam: Áo
('SP-NAM-POLO-01', N'Áo Thun Polo Nam Pique Cotton Kháng Khuẩn Simplain', 'ao-thun-polo-nam-pique-cotton', 'ao-nam',
 N'Cotton Pima cao cấp', N'Regular Fit',
 N'Size S: 50 - 58kg | Size M: 59 - 66kg | Size L: 67 - 74kg | Size XL: 75 - 82kg',
 N'Áo polo nam chất liệu Cotton Pima dệt cá sấu thoáng mát, co giãn nhẹ và thấm hút mồ hôi tối ưu. Cổ áo dệt bo gân đứng form, không bai dão sau nhiều lần giặt. Phong cách lịch lãm, phù hợp cả đi làm và đi chơi.',
 289000, 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=600'),

-- 2. Thời trang nam: Quần
('SP-NAM-JEAN-02', N'Quần Jean Nam Slimfit Denim 12.5oz Co Giãn Cao Cấp', 'quan-jean-nam-slimfit-denim-12oz', 'quan-nam',
 N'Denim co giãn Spandex', N'Slim Fit',
 N'Size 29: Vòng bụng 75-77cm | Size 30: Vòng bụng 78-80cm | Size 31: Vòng bụng 81-83cm | Size 32: Vòng bụng 84-86cm',
 N'Quần jean nam thiết kế ống côn ôm nhẹ tôn dáng, sợi Spandex co giãn 4 chiều cho cảm giác vận động thoải mái tối đa. Khóa kéo kim loại YKK bền bỉ và wash màu phong trần hiện đại.',
 420000, 'https://images.unsplash.com/photo-1542272604-780c96856592?w=600'),

-- 3. Thời trang nam: Áo khoác
('SP-NAM-KHOAC-03', N'Áo Khoác Gió Bomber 2 Lớp Chống Nước Cản Gió Simplain', 'ao-khoac-gio-bomber-nam-2-lop', 'ao-khoac-nam',
 N'Vải Oxford dệt thoi', N'Boxy Streetwear',
 N'Size M: Cao 1m65 - 1m72 (58-65kg) | Size L: 1m70 - 1m77 (66-74kg) | Size XL: 1m75 - 1m84 (75-84kg)',
 N'Áo khoác gió bomber phong cách streetwear nam tính, cấu tạo 2 lớp chống thấm nước và chắn gió hiệu quả. Túi khóa kéo tiện dụng cùng bo thun cổ tay giữ ấm tối đa trong mùa se lạnh.',
 450000, 'https://images.unsplash.com/photo-1544441893-675973e31985?w=600'),

-- 4. Thời trang nữ: Áo
('SP-NU-AOKIEU-04', N'Áo Kiểu Nữ Cổ Vuông Tay Phồng Lụa Satin Cao Cấp', 'ao-kieu-nu-co-vuong-tay-phong', 'ao-nu',
 N'Lụa Satin cao cấp', N'Regular Fit',
 N'Size S: Ngực 82-86cm (43-49kg) | Size M: Ngực 87-90cm (50-56kg) | Size L: Ngực 91-95cm (57-63kg)',
 N'Áo kiểu nữ thiết kế cổ vuông thanh lịch khoe xương quai xanh quyến rũ, tay bồng tiểu thư che khuyết điểm bắp tay. Bề mặt vải lụa óng ánh mềm mượt, mang lại vẻ đẹp nhẹ nhàng, sang trọng.',
 260000, 'https://images.unsplash.com/photo-1564257631407-4deb1f99d992?w=600'),

-- 5. Thời trang nữ: Váy
('SP-NU-CHANVAY-05', N'Chân Váy Chữ A Xếp Ly Kaki Hàn Quốc Tôn Dáng', 'chan-vay-chu-a-xep-ly-kaki-nu', 'vay-nu',
 N'Kaki cotton twill', N'Xếp ly thanh lịch',
 N'Size S: Vòng eo 64-67cm (43-48kg) | Size M: Vòng eo 68-71cm (49-54kg) | Size L: Vòng eo 72-76cm (55-60kg)',
 N'Chân váy xếp ly cạp cao hack chiều cao cực chuẩn, may kèm lớp quần bảo hộ kín đáo giúp các nàng tự tin hoạt động. Chất vải kaki dày dặn giữ nếp ly sắc nét sau khi giặt.',
 240000, 'https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=600'),

-- 6. Thời trang nữ: Đầm
('SP-NU-DAMMAXI-06', N'Đầm Maxi Dáng Xòe Voan Hoa Chiffon 2 Lớp Duyên Dáng', 'dam-maxi-dang-xoe-voan-hoa-chiffon', 'dam-nu',
 N'Voan hoa chiffon', N'Xòe chữ A (A-Line)',
 N'Size S: 42-49kg (Ngực 82-86cm) | Size M: 50-56kg (Ngực 87-91cm) | Size L: 57-64kg (Ngực 92-96cm)',
 N'Đầm voan hoa 2 lớp thướt tha, lớp lót lụa trong êm mát kín đáo. Thiết kế chiết eo tôn vóc dáng thon thả, thích hợp diện dạo phố, chụp ảnh du lịch hoặc tham gia các buổi tiệc ngoài trời.',
 480000, 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600'),

-- 7. Thời trang nữ: Quần
('SP-NU-QUANTAY-07', N'Quần Tây Nữ Ống Suông Cạp Cao Hack Dáng Simplain', 'quan-tay-nu-ong-suong-cap-cao', 'quan-nu',
 N'Tuyết mưa cao cấp', N'Ống rộng (Wide Leg)',
 N'Size S: Vòng eo 63-66cm (43-48kg) | Size M: Vòng eo 67-70cm (49-54kg) | Size L: Vòng eo 71-75cm (55-60kg)',
 N'Quần tây nữ cạp cao phối khóa ẩn thanh lịch, ống suông dài chuẩn tỷ lệ hack chân dài miên man. Vải tuyết mưa dệt đanh sợi, chống nhăn và chống bám bụi hiệu quả.',
 350000, 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=600'),

-- 8. Giày dép: Sneaker
('SP-GIAY-SNEAKER-08', N'Giày Sneaker Thể Thao Đệm Khí Êm Chân Simplain Runner', 'giay-sneaker-the-thao-dem-khi-simplain', 'sneaker',
 N'Vải Flyknit thoáng khí', N'Low-Top cổ thấp',
 N'Size 39: Chân 24.1 - 24.5cm | Size 40: 24.6 - 25.0cm | Size 41: 25.1 - 25.5cm | Size 42: 25.6 - 26.0cm',
 N'Giày sneaker thể thao với thân dệt Flyknit nguyên khối co giãn ôm sát mu bàn chân, đế đệm trợ lực êm ái bảo vệ khớp xương khi đi bộ hay chạy nhảy. Đế ngoài phủ cao su chống mài mòn và bám đường tối ưu.',
 590000, 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=600'),

-- 9. Giày dép: Sandal
('SP-GIAY-SANDAL-09', N'Giày Sandal Nam Nữ Quai Dù Đế Bánh Mì Siêu Êm', 'giay-sandal-nam-nu-quai-du-de-banh-mi', 'sandal',
 N'Cao su lưu hóa chống trơn', N'Low-Top cổ thấp',
 N'Size 38: Chân dài 23.5 - 24.0cm | Size 39: 24.1 - 24.5cm | Size 40: 24.6 - 25.0cm | Size 41: 25.1 - 25.5cm',
 N'Sandal quai hậu bản dù jacquard chịu lực cao, khóa dán thông minh điều chỉnh độ ôm chân linh hoạt. Đế bánh mì đúc cao su non siêu nhẹ, êm ái chống trơn trượt hoàn hảo khi đi mưa.',
 320000, 'https://images.unsplash.com/photo-1603808033192-082d6919d3e1?w=600'),

-- 10. Giày dép: Dép
('SP-GIAY-DEP-10', N'Dép Quai Ngang Đúc Nguyên Khối Chống Trơn Trượt Simplain', 'dep-quai-ngang-duc-nguyen-khoi', 'dep',
 N'Cao su lưu hóa chống trơn', N'Loafer xỏ tiện lợi',
 N'Size 39: Chân 24cm | Size 40: Chân 24.5-25cm | Size 41: Chân 25.5cm | Size 42: Chân 26cm',
 N'Dép bánh mì quai ngang đúc nguyên khối từ hạt nhựa EVA cao cấp, trọng lượng siêu nhẹ và chống nước tuyệt đối. Đế vân gai kim cương chống trơn trượt an toàn cả trong nhà tắm lẫn đi dạo ngoài trời.',
 150000, 'https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=600'),

-- 11. Ví và balo: Túi xách
('SP-TUI-XACH-11', N'Túi Xách Da Bò Epsom Nữ Khóa Gập Thanh Lịch Simplain', 'tui-xach-da-bo-epsom-nu-thanh-lich', 'tui-xach',
 N'Da bò hạt Epsom', N'Túi bao tử (Chest Bag)',
 N'Kích thước tiêu chuẩn: Chiều dài 24cm x Rộng 8cm x Cao 16cm (Chứa vừa iPad Mini, điện thoại, ví dài)',
 N'Túi xách nữ chất liệu da bò Epsom dập vân tinh tế chống trầy xước và kháng nước, phụ kiện khóa xoay mạ vàng sáng bóng sang trọng. Dây đeo da phối xích có thể điều chỉnh đeo chéo hoặc xách tay linh hoạt.',
 520000, 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600'),

-- 12. Ví và balo: Balo
('SP-BALO-LAPTOP-12', N'Balo Laptop Công Sở Kháng Nước Đa Năng Simplain 20L', 'balo-laptop-cong-so-khang-nuoc-simplain', 'balo',
 N'Vải Canvas chống nước', N'Balo nắp cuộn (Roll-top)',
 N'Form chuẩn 20L: 43cm x 30cm x 14cm (Có ngăn chống sốc chuyên dụng đựng laptop 15.6 inch)',
 N'Balo công sở thiết kế tối giản thông minh, chất vải Canvas 900D phủ tráng PU chống thấm nước khi đi mưa. Đệm lưng tổ ong thoáng khí thoát nhiệt, quai đeo trợ lực chống gù lưng hiệu quả.',
 420000, 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600'),

-- 13. Ví và balo: Ví
('SP-VI-DABO-13', N'Ví Nam Da Bò Sáp Bifold Khâu Tay Thủ Công Cao Cấp', 'vi-nam-da-bo-sap-bifold-khau-tay', 'vi',
 N'Da bò sáp nguyên tấm', N'Ví gập đôi Bifold',
 N'Kích thước gập: 11.5cm x 9.5cm x 1.5cm (Gồm 2 ngăn tiền chính và 8 ngăn thẻ ATM/CCCD)',
 N'Ví nam da bò sáp Crazy Horse nhập khẩu nguyên tấm, càng sử dụng lâu chất da càng bóng mềm và lên màu vintage độc nhất. Từng đường kim mũi chỉ được khâu thủ công tỉ mỉ, độ bền trên 10 năm.',
 290000, 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=600'),

-- 14. Phụ kiện: Đồng hồ
('SP-PK-DONGHO-14', N'Đồng Hồ Nam Dây Thép Không Gỉ Mặt Kính Sapphire Simplain Classic', 'dong-ho-nam-thep-khong-gi-mat-sapphire', 'dong-ho',
 N'Thép không gỉ 316L', N'Mặt tròn cổ điển',
 N'Đường kính mặt 40mm | Độ dày vỏ 8.5mm | Chiều dài dây 220mm (Kính Sapphire chống xước, chống nước 5ATM)',
 N'Đồng hồ nam phong cách doanh nhân lịch lãm, bộ máy Quartz chuẩn xác từ Nhật Bản. Toàn bộ vỏ và dây đeo chế tác từ thép không gỉ 316L chống ăn mòn tuyệt đối, mặt kính tinh thể Sapphire chống va đập trầy xước.',
 890000, 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=600'),

-- 15. Phụ kiện: Kính
('SP-PK-KINHMAT-15', N'Kính Mát Unisex Gọng Titanium Phân Cực Polarized Chống UV400', 'kinh-mat-unisex-gong-titanium-polarized', 'kinh',
 N'Titanium mạ PVD siêu bền', N'Mặt tròn cổ điển',
 N'Chiều ngang tròng: 54mm | Cầu kính: 18mm | Chiều dài càng kính: 142mm (Khối lượng siêu nhẹ chỉ 18g)',
 N'Kính mát phân cực cao cấp gọng hợp kim Titanium siêu nhẹ, chịu lực uốn cong linh hoạt không biến dạng. Tròng kính tráng gương 9 lớp bảo vệ mắt tối đa khỏi 100% tia tử ngoại UV400 và chống lóa đèn xe ban đêm.',
 350000, 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=600'),

-- 16. Phụ kiện: Mũ
('SP-PK-MUNON-16', N'Mũ Lưỡi Trai Classic Simplain Thêu Nổi 100% Cotton', 'mu-luoi-trai-classic-simplain-cotton', 'mu',
 N'Cotton Compact 100%', N'Mũ lưỡi trai (Cap)',
 N'Freesize: Vòng đầu từ 55 - 60cm (Khóa đồng phía sau có thể nới rộng / thu hẹp dễ dàng)',
 N'Nón kết lưỡi trai form dáng 6 múi cứng cáp, thêu logo Simplain nổi 3D tinh xảo. Chất vải kaki cotton chải kỹ thấm hút mồ hôi tối ưu, đem lại cảm giác thoáng mát cả ngày dài dạo phố hay chơi thể thao.',
 150000, 'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=600'),

-- 17. Phụ kiện: Thắt lưng
('SP-PK-THAT-LUNG-17', N'Thắt Lưng Nam Da Bò Sáp Khóa Kim Loại Tự Động Cao Cấp', 'that-lung-nam-da-bo-khoa-tu-dong-cao-cap', 'that-lung',
 N'Da bò sáp nguyên tấm', N'Regular Fit',
 N'Bản rộng 3.5cm x Chiều dài 120cm (Phù hợp vòng bụng từ 75cm đến 105cm, dễ cắt bớt dây)',
 N'Dây nịt nam da bò thuộc 2 lớp dẻo dai nguyên tấm, không bong tróc hay rạn nứt bề mặt. Khóa ray trượt tự động hợp kim mạ crom sáng bóng, thao tác cài mở êm mượt và sang trọng.',
 290000, 'https://images.unsplash.com/photo-1624222247344-550fb60583dc?w=600'),

-- 18. Phụ kiện: Trang sức
('SP-PK-TRANGSUC-18', N'Vòng Tay Bạc Ý 925 Xi Bạch Kim Mắt Xích Cuban Nam Nữ', 'vong-tay-bac-y-925-cuban-chain', 'trang-suc',
 N'Bạc Ý 925 xi bạch kim', N'Dây xích Cuban cá tính',
 N'Chiều dài lắc 18cm + 3cm mắt xích mở rộng (Bản xích dày 5mm, khối lượng 14.2 gram)',
 N'Lắc tay bạc chuẩn 92.5% phủ lớp mạ Rhodium bạch kim bóng bẩy, chống ố xỉn và không gây kích ứng da. Thiết kế mắt xích Cuban đan cài mạnh mẽ, phong cách streetwear thời thượng nâng tầm cổ tay.',
 390000, 'https://images.unsplash.com/photo-1611591475152-47795d074242?w=600'),

-- 19. Phụ kiện: Khăn choàng
('SP-PK-KHANCHOANG-19', N'Khăn Choàng Cổ Len Cashmere Giữ Ấm Mùa Đông Cao Cấp', 'khan-choang-co-len-cashmere-cao-cap', 'khan-choang',
 N'Len Cashmere cao cấp', N'Regular Fit',
 N'Kích thước tiêu chuẩn: Chiều dài 200cm x Rộng 70cm (Khối lượng 260g siêu nhẹ giữ nhiệt tối ưu)',
 N'Khăn choàng cổ dệt từ sợi len Cashmere cao cấp kết hợp lông cừu tự nhiên, mang lại cảm giác mềm mại như nhung, không gây dặm ngứa da cổ. Hai đầu khăn đan tua rua cổ điển thanh lịch, giữ ấm cơ thể hoàn hảo trong ngày đông giá rét.',
 310000, 'https://images.unsplash.com/photo-1520903920243-00d872a2d1c9?w=600');

-- Vòng lặp cập nhật hoặc thêm sản phẩm vào bảng san_pham
DECLARE @curMaSp VARCHAR(50), @curTenSp NVARCHAR(255), @curSlug VARCHAR(255), @curSlugDm VARCHAR(100);
DECLARE @curCL NVARCHAR(255), @curFD NVARCHAR(255), @curSizeHd NVARCHAR(500), @curMoTa NVARCHAR(MAX);
DECLARE @curGia BIGINT, @curImg VARCHAR(500);

DECLARE cur CURSOR FOR SELECT maSp, tenSp, slug, slugDm, chatLieu, formDang, sizeHd, moTa, giaBan, imgUrl FROM @tblSp;
OPEN cur;
FETCH NEXT FROM cur INTO @curMaSp, @curTenSp, @curSlug, @curSlugDm, @curCL, @curFD, @curSizeHd, @curMoTa, @curGia, @curImg;

WHILE @@FETCH_STATUS = 0
BEGIN
    DECLARE @dmConId BIGINT = (SELECT TOP 1 [id] FROM [danh_muc] WHERE [duong_dan_slug] = @curSlugDm);
    IF @dmConId IS NOT NULL
    BEGIN
        IF EXISTS (SELECT 1 FROM [san_pham] WHERE [ma_san_pham] = @curMaSp)
        BEGIN
            UPDATE [san_pham]
            SET [ma_danh_muc_id] = @dmConId,
                [ten_san_pham] = @curTenSp,
                [duong_dan_slug] = @curSlug,
                [chat_lieu] = @curCL,
                [form_dang] = @curFD,
                [huong_dan_chon_size] = @curSizeHd,
                [mo_ta_chi_tiet] = @curMoTa,
                [gia_co_ban] = @curGia
            WHERE [ma_san_pham] = @curMaSp;
        END
        ELSE
        BEGIN
            INSERT INTO [san_pham] (
                [ma_danh_muc_id], [ma_san_pham], [ten_san_pham], [duong_dan_slug],
                [chat_lieu], [form_dang], [huong_dan_chon_size], [mo_ta_chi_tiet],
                [gia_co_ban], [noi_bat], [kich_hoat], [ngay_tao]
            ) VALUES (
                @dmConId, @curMaSp, @curTenSp, @curSlug,
                @curCL, @curFD, @curSizeHd, @curMoTa,
                @curGia, 1, 1, GETDATE()
            );
        END

        DECLARE @curSpId BIGINT = (SELECT TOP 1 [id] FROM [san_pham] WHERE [ma_san_pham] = @curMaSp);

        -- Hình ảnh đại diện
        IF NOT EXISTS (SELECT 1 FROM [hinh_anh] WHERE [ma_san_pham_id] = @curSpId AND [la_anh_dai_dien] = 1)
        BEGIN
            INSERT INTO [hinh_anh] ([ma_san_pham_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu])
            VALUES (@curSpId, @curImg, 1, 1);
        END
    END

    FETCH NEXT FROM cur INTO @curMaSp, @curTenSp, @curSlug, @curSlugDm, @curCL, @curFD, @curSizeHd, @curMoTa, @curGia, @curImg;
END
CLOSE cur;
DEALLOCATE cur;

PRINT N'-> Đã hoàn tất đồng bộ 19 sản phẩm đại diện!';

-- =========================================================================================================
-- 7. CHÈN CÁC BIẾN THỂ SKU MẪU CHO 19 SẢN PHẨM TRÊN
-- =========================================================================================================
PRINT N'===> ĐỒNG BỘ CÁC BIẾN THỂ SKU MẪU...';

DECLARE @tblSku TABLE (
    maSp VARCHAR(50), sizeCode VARCHAR(50), mauHex VARCHAR(20), mauTen NVARCHAR(100),
    skuSuffix VARCHAR(20), soLuong INT
);

INSERT INTO @tblSku VALUES
-- 1. Áo Polo Nam (M đen, L đen, L trắng, XL navy)
('SP-NAM-POLO-01', 'M', '#000000', N'Đen', 'BLK', 50),
('SP-NAM-POLO-01', 'L', '#000000', N'Đen', 'BLK', 50),
('SP-NAM-POLO-01', 'L', '#FFFFFF', N'Trắng', 'WHT', 50),
('SP-NAM-POLO-01', 'XL', '#1E3A8A', N'Xanh Navy', 'NVY', 50),

-- 2. Quần Jean Nam (29 navy, 30 navy, 31 navy, 32 đen)
('SP-NAM-JEAN-02', '29', '#1E3A8A', N'Xanh Navy', 'NVY', 40),
('SP-NAM-JEAN-02', '30', '#1E3A8A', N'Xanh Navy', 'NVY', 45),
('SP-NAM-JEAN-02', '31', '#1E3A8A', N'Xanh Navy', 'NVY', 45),
('SP-NAM-JEAN-02', '32', '#000000', N'Đen', 'BLK', 35),

-- 3. Áo Khoác Gió Bomber (M đen, L đen, XL xám tiêu)
('SP-NAM-KHOAC-03', 'M', '#000000', N'Đen', 'BLK', 30),
('SP-NAM-KHOAC-03', 'L', '#000000', N'Đen', 'BLK', 40),
('SP-NAM-KHOAC-03', 'XL', '#64748B', N'Xám Tiêu', 'GRY', 30),

-- 4. Áo Kiểu Nữ (S trắng, M trắng, M hồng pastel)
('SP-NU-AOKIEU-04', 'S', '#FFFFFF', N'Trắng', 'WHT', 40),
('SP-NU-AOKIEU-04', 'M', '#FFFFFF', N'Trắng', 'WHT', 40),
('SP-NU-AOKIEU-04', 'M', '#F472B6', N'Hồng Pastel', 'PNK', 40),

-- 5. Chân Váy Chữ A (S trắng, M trắng, M đen)
('SP-NU-CHANVAY-05', 'S', '#FFFFFF', N'Trắng', 'WHT', 35),
('SP-NU-CHANVAY-05', 'M', '#FFFFFF', N'Trắng', 'WHT', 35),
('SP-NU-CHANVAY-05', 'M', '#000000', N'Đen', 'BLK', 40),

-- 6. Đầm Maxi (S hồng, M hồng, L hồng)
('SP-NU-DAMMAXI-06', 'S', '#F472B6', N'Hồng Pastel', 'PNK', 30),
('SP-NU-DAMMAXI-06', 'M', '#F472B6', N'Hồng Pastel', 'PNK', 40),
('SP-NU-DAMMAXI-06', 'L', '#F472B6', N'Hồng Pastel', 'PNK', 30),

-- 7. Quần Tây Nữ (S đen, M đen, M be)
('SP-NU-QUANTAY-07', 'S', '#000000', N'Đen', 'BLK', 30),
('SP-NU-QUANTAY-07', 'M', '#000000', N'Đen', 'BLK', 40),
('SP-NU-QUANTAY-07', 'M', '#D2B48C', N'Be Vintage', 'BEG', 30),

-- 8. Sneaker (40 trắng, 41 trắng, 42 đen)
('SP-GIAY-SNEAKER-08', '40', '#FFFFFF', N'Trắng', 'WHT', 40),
('SP-GIAY-SNEAKER-08', '41', '#FFFFFF', N'Trắng', 'WHT', 50),
('SP-GIAY-SNEAKER-08', '42', '#000000', N'Đen', 'BLK', 40),

-- 9. Sandal (39 đen, 40 đen, 41 be)
('SP-GIAY-SANDAL-09', '39', '#000000', N'Đen', 'BLK', 30),
('SP-GIAY-SANDAL-09', '40', '#000000', N'Đen', 'BLK', 40),
('SP-GIAY-SANDAL-09', '41', '#D2B48C', N'Be Vintage', 'BEG', 30),

-- 10. Dép (40 đen, 41 đen, 41 trắng)
('SP-GIAY-DEP-10', '40', '#000000', N'Đen', 'BLK', 50),
('SP-GIAY-DEP-10', '41', '#000000', N'Đen', 'BLK', 50),
('SP-GIAY-DEP-10', '41', '#FFFFFF', N'Trắng', 'WHT', 40),

-- 11. Túi xách (Freesize đen, Freesize nâu bò)
('SP-TUI-XACH-11', 'Freesize', '#000000', N'Đen', 'BLK', 30),
('SP-TUI-XACH-11', 'Freesize', '#8B4513', N'Nâu Da Bò', 'BRN', 30),

-- 12. Balo (Freesize đen, Freesize xám tiêu)
('SP-BALO-LAPTOP-12', 'Freesize', '#000000', N'Đen', 'BLK', 50),
('SP-BALO-LAPTOP-12', 'Freesize', '#64748B', N'Xám Tiêu', 'GRY', 40),

-- 13. Ví (Freesize nâu bò, Freesize đen)
('SP-VI-DABO-13', 'Freesize', '#8B4513', N'Nâu Da Bò', 'BRN', 40),
('SP-VI-DABO-13', 'Freesize', '#000000', N'Đen', 'BLK', 40),

-- 14. Đồng hồ (Freesize bạc, Freesize đen)
('SP-PK-DONGHO-14', 'Freesize', '#C0C0C0', N'Bạc Sang Trọng', 'SLV', 30),
('SP-PK-DONGHO-14', 'Freesize', '#000000', N'Đen', 'BLK', 20),

-- 15. Kính mát (Freesize đen, Freesize bạc)
('SP-PK-KINHMAT-15', 'Freesize', '#000000', N'Đen', 'BLK', 50),
('SP-PK-KINHMAT-15', 'Freesize', '#C0C0C0', N'Bạc Sang Trọng', 'SLV', 30),

-- 16. Mũ (Freesize đen, Freesize trắng, Freesize be)
('SP-PK-MUNON-16', 'Freesize', '#000000', N'Đen', 'BLK', 60),
('SP-PK-MUNON-16', 'Freesize', '#FFFFFF', N'Trắng', 'WHT', 40),
('SP-PK-MUNON-16', 'Freesize', '#D2B48C', N'Be Vintage', 'BEG', 30),

-- 17. Thắt lưng (Freesize đen, Freesize nâu bò)
('SP-PK-THAT-LUNG-17', 'Freesize', '#000000', N'Đen', 'BLK', 50),
('SP-PK-THAT-LUNG-17', 'Freesize', '#8B4513', N'Nâu Da Bò', 'BRN', 40),

-- 18. Trang sức lắc tay (Freesize bạc)
('SP-PK-TRANGSUC-18', 'Freesize', '#C0C0C0', N'Bạc Sang Trọng', 'SLV', 40),

-- 19. Khăn choàng len (Freesize be, Freesize xám, Freesize nâu bò)
('SP-PK-KHANCHOANG-19', 'Freesize', '#D2B48C', N'Be Vintage', 'BEG', 35),
('SP-PK-KHANCHOANG-19', 'Freesize', '#64748B', N'Xám Tiêu', 'GRY', 35),
('SP-PK-KHANCHOANG-19', 'Freesize', '#8B4513', N'Nâu Da Bò', 'BRN', 25);

DECLARE @sMaSp VARCHAR(50), @sSize VARCHAR(50), @sMauHex VARCHAR(20), @sMauTen NVARCHAR(100), @sSuffix VARCHAR(20), @sQty INT;
DECLARE curSku CURSOR FOR SELECT maSp, sizeCode, mauHex, mauTen, skuSuffix, soLuong FROM @tblSku;
OPEN curSku;
FETCH NEXT FROM curSku INTO @sMaSp, @sSize, @sMauHex, @sMauTen, @sSuffix, @sQty;

WHILE @@FETCH_STATUS = 0
BEGIN
    DECLARE @spId BIGINT = (SELECT TOP 1 [id] FROM [san_pham] WHERE [ma_san_pham] = @sMaSp);
    DECLARE @sizeId BIGINT = (SELECT TOP 1 [id] FROM [kich_thuoc] WHERE [ma_kich_thuoc] = @sSize);
    DECLARE @mauId BIGINT = (SELECT TOP 1 [id] FROM [mau_sac] WHERE [ma_mau] = @sMauHex OR [ten_mau] = @sMauTen);
    DECLARE @giaBase BIGINT = (SELECT TOP 1 [gia_co_ban] FROM [san_pham] WHERE [id] = @spId);

    IF @spId IS NOT NULL AND @sizeId IS NOT NULL AND @mauId IS NOT NULL
    BEGIN
        DECLARE @skuCode VARCHAR(100) = @sMaSp + '-' + @sSize + '-' + @sSuffix;
        DECLARE @barCode VARCHAR(50) = 'BC-' + CAST(ABS(CHECKSUM(@skuCode)) % 100000000 AS VARCHAR(20));
        DECLARE @giaGoc BIGINT = CAST(@giaBase * 1.3 AS BIGINT);

        IF NOT EXISTS (SELECT 1 FROM [bien_the_san_pham] WHERE [ma_sku] = @skuCode)
        BEGIN
            INSERT INTO [bien_the_san_pham] (
                [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id],
                [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]
            ) VALUES (
                @spId, @mauId, @sizeId,
                @skuCode, @barCode, @sQty, @giaBase, @giaGoc, 1
            );
        END
    END

    FETCH NEXT FROM curSku INTO @sMaSp, @sSize, @sMauHex, @sMauTen, @sSuffix, @sQty;
END
CLOSE curSku;
DEALLOCATE curSku;

PRINT N'===> HOÀN TẤT 100% CẬP NHẬT 5 DANH MỤC CHA, 19 DANH MỤC CON VÀ 19 SẢN PHẨM MẪU KÈM ĐẦY ĐỦ THUỘC TÍNH!';
GO
