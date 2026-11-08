USE Simpl4;
GO

PRINT N'------------------------------------------------------------------------------------';
PRINT N'[BƯỚC 1] BỔ SUNG CÁC CỘT TRẠNG THÁI CHO TỪNG CHI TIẾT ĐƠN HÀNG [CHI_TIET_DON_HANG]';
PRINT N'------------------------------------------------------------------------------------';

IF NOT EXISTS (
    SELECT 1 
    FROM sys.columns 
    WHERE object_id = OBJECT_ID(N'chi_tiet_don_hang') 
      AND name = N'trang_thai_item'
)
BEGIN
    ALTER TABLE chi_tiet_don_hang 
    ADD trang_thai_item VARCHAR(50) NOT NULL CONSTRAINT DF_chi_tiet_don_hang_trang_thai_item DEFAULT ('BINH_THUONG');
    PRINT N'=> Đã tạo thành công cột trang_thai_item trong bảng chi_tiet_don_hang.';
END
ELSE
BEGIN
    PRINT N'=> Cột trang_thai_item đã tồn tại trong bảng chi_tiet_don_hang.';
END
GO

IF NOT EXISTS (
    SELECT 1 
    FROM sys.columns 
    WHERE object_id = OBJECT_ID(N'chi_tiet_don_hang') 
      AND name = N'ghi_chu_item'
)
BEGIN
    ALTER TABLE chi_tiet_don_hang 
    ADD ghi_chu_item NVARCHAR(255) NULL;
    PRINT N'=> Đã tạo thành công cột ghi_chu_item trong bảng chi_tiet_don_hang.';
END
ELSE
BEGIN
    PRINT N'=> Cột ghi_chu_item đã tồn tại trong bảng chi_tiet_don_hang.';
END
GO

IF NOT EXISTS (
    SELECT 1 
    FROM sys.columns 
    WHERE object_id = OBJECT_ID(N'chi_tiet_don_hang') 
      AND name = N'ngay_bao_mat'
)
BEGIN
    ALTER TABLE chi_tiet_don_hang 
    ADD ngay_bao_mat DATETIME2 NULL;
    PRINT N'=> Đã tạo thành công cột ngay_bao_mat trong bảng chi_tiet_don_hang.';
END
ELSE
BEGIN
    PRINT N'=> Cột ngay_bao_mat đã tồn tại trong bảng chi_tiet_don_hang.';
END
GO

PRINT N'------------------------------------------------------------------------------------';
PRINT N'[BƯỚC 2] BỔ SUNG TRẠNG THÁI THAT_LAC_MOT_PHAN VÀO BẢNG TRANG_THAI_DON_HANG';
PRINT N'------------------------------------------------------------------------------------';

IF NOT EXISTS (
    SELECT 1 
    FROM trang_thai_don_hang 
    WHERE ma_trang_thai = 'THAT_LAC_MOT_PHAN'
)
BEGIN
    INSERT INTO trang_thai_don_hang (ma_trang_thai, ten_trang_thai, ma_mau_hex)
    VALUES ('THAT_LAC_MOT_PHAN', N'Thất lạc một phần hàng', '#EA580C');
    PRINT N'=> Đã tạo trạng thái THAT_LAC_MOT_PHAN trong bảng trang_thai_don_hang.';
END
ELSE
BEGIN
    PRINT N'=> Trạng thái THAT_LAC_MOT_PHAN đã tồn tại.';
END
GO

PRINT N'------------------------------------------------------------------------------------';
PRINT N'[BƯỚC 3] CHUẨN HÓA DỮ LIỆU HIỆN CÓ';
PRINT N'------------------------------------------------------------------------------------';

UPDATE chi_tiet_don_hang 
SET trang_thai_item = 'BINH_THUONG' 
WHERE trang_thai_item IS NULL OR trang_thai_item = '';

PRINT N'=> Đã chuẩn hóa toàn bộ chi_tiet_don_hang thành trạng thái BINH_THUONG.';
GO
