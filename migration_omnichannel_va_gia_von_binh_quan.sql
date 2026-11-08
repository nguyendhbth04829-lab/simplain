-- ====================================================================================
-- SIMPLAIN STORE - MIGRATION SCRIPT: OMNICHANNEL & GIÁ VỐN BÌNH QUÂN GIA QUYỀN (AVCO)
-- Phiên bản: 2026.11 - Tích hợp Bán hàng Đa kênh Online & POS, Giá vốn bình quân
-- Cơ sở dữ liệu: Simpl4 (Microsoft SQL Server)
-- ====================================================================================

USE Simpl4;
GO

PRINT N'------------------------------------------------------------------------------------';
PRINT N'[BƯỚC 1] BỔ SUNG CỘT GIA_GOC CHO BẢNG CHI_TIET_DON_HANG (LƯU GIÁ VỐN TẠI THỜI ĐIỂM BÁN)';
PRINT N'------------------------------------------------------------------------------------';

IF NOT EXISTS (
    SELECT 1 
    FROM sys.columns 
    WHERE object_id = OBJECT_ID(N'chi_tiet_don_hang') 
      AND name = N'gia_goc'
)
BEGIN
    ALTER TABLE chi_tiet_don_hang 
    ADD gia_goc DECIMAL(18,2) NOT NULL CONSTRAINT DF_chi_tiet_don_hang_gia_goc DEFAULT (0);
    PRINT N'=> Đã tạo thành công cột gia_goc DECIMAL(18,2) trong bảng chi_tiet_don_hang.';
END
ELSE
BEGIN
    PRINT N'=> Cột gia_goc đã tồn tại trong bảng chi_tiet_don_hang. Bỏ qua tạo mới.';
END
GO

PRINT N'------------------------------------------------------------------------------------';
PRINT N'[BƯỚC 2] ĐỒNG BỘ DỮ LIỆU GIÁ VỐN CHO CÁC ĐƠN HÀNG LỊCH SỬ';
PRINT N'------------------------------------------------------------------------------------';

UPDATE ct
SET ct.gia_goc = ISNULL(bt.gia_goc, 0)
FROM chi_tiet_don_hang ct
INNER JOIN bien_the_san_pham bt ON ct.ma_bien_the_id = bt.id
WHERE ct.gia_goc IS NULL OR ct.gia_goc = 0;

PRINT N'=> Đã cập nhật giá vốn ban đầu cho các dòng chi tiết đơn hàng cũ.';
GO

PRINT N'------------------------------------------------------------------------------------';
PRINT N'[BƯỚC 3] TỐI ƯU HÓA INDEX PHỤC VỤ TRUY VẤN VÀ BÁO CÁO OMNICHANNEL';
PRINT N'------------------------------------------------------------------------------------';

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes 
    WHERE name = N'idx_don_hang_loai_trangthai' 
      AND object_id = OBJECT_ID(N'don_hang')
)
BEGIN
    CREATE NONCLUSTERED INDEX idx_don_hang_loai_trangthai
    ON don_hang (loai_don_hang, ma_trang_thai_id)
    INCLUDE (ma_don_hang, tong_thanh_toan, ngay_tao);
    PRINT N'=> Đã tạo Nonclustered Index idx_don_hang_loai_trangthai.';
END
ELSE
BEGIN
    PRINT N'=> Index idx_don_hang_loai_trangthai đã tồn tại.';
END
GO

PRINT N'------------------------------------------------------------------------------------';
PRINT N'[BƯỚC 4] CHUẨN HÓA KÊNH BÁN HÀNG LOAI_DON_HANG (ONLINE vs TAI_QUAY)';
PRINT N'------------------------------------------------------------------------------------';

-- Chuẩn hóa tất cả các đơn hàng chưa gán loại đơn thành 'ONLINE'
UPDATE don_hang
SET loai_don_hang = 'ONLINE'
WHERE loai_don_hang IS NULL OR loai_don_hang = '';

PRINT N'=> Đã chuẩn hóa toàn bộ đơn hàng mặc định thành kênh ONLINE.';
GO

PRINT N'------------------------------------------------------------------------------------';
PRINT N'[BƯỚC 5] DỮ LIỆU MẪU MINH HỌA: GIÁ VỐN BÌNH QUÂN DI ĐỘNG KHI NHẬP HÀNG & BÁN HÀNG';
PRINT N'------------------------------------------------------------------------------------';

-- Kiểm tra xem đã có biến thể để chạy demo tính giá vốn bình quân di động
DECLARE @BienTheDemoId BIGINT;
SELECT TOP 1 @BienTheDemoId = id FROM bien_the_san_pham WHERE so_luong > 0;

IF @BienTheDemoId IS NOT NULL
BEGIN
    DECLARE @TonHienTai INT;
    DECLARE @GiaGocHienTai DECIMAL(18,2);
    
    SELECT @TonHienTai = so_luong, @GiaGocHienTai = gia_goc 
    FROM bien_the_san_pham 
    WHERE id = @BienTheDemoId;
    
    PRINT N'=> Biến thể kiểm tra Demo ID: ' + CAST(@BienTheDemoId AS NVARCHAR(20));
    PRINT N'   Tồn kho trước nhập: ' + CAST(@TonHienTai AS NVARCHAR(20)) + N' chiếc';
    PRINT N'   Giá vốn cũ: ' + FORMAT(@GiaGocHienTai, 'N0') + N' đ';
    
    -- Giả lập: Nhập thêm 20 chiếc với giá nhập 150.000 đ
    -- Công thức: Giá vốn mới = (Tồn cũ * Giá cũ + SL nhập * Giá nhập) / (Tồn cũ + SL nhập)
    DECLARE @SlNhapDemo INT = 20;
    DECLARE @GiaNhapDemo DECIMAL(18,2) = 150000;
    DECLARE @GiaGocMoi DECIMAL(18,2);
    
    SET @GiaGocMoi = ROUND(((@TonHienTai * @GiaGocHienTai) + (@SlNhapDemo * @GiaNhapDemo)) / (@TonHienTai + @SlNhapDemo), 2);
    
    PRINT N'   Giả lập nhập lô mới: 20 chiếc @ 150.000 đ';
    PRINT N'   => Giá vốn bình quân sau nhập tính được: ' + FORMAT(@GiaGocMoi, 'N0') + N' đ';
END
GO

PRINT N'------------------------------------------------------------------------------------';
PRINT N'[HOÀN TẤT] MIGRATION DATABASE OMNICHANNEL & GIÁ VỐN BÌNH QUÂN THÀNH CÔNG!';
PRINT N'------------------------------------------------------------------------------------';
