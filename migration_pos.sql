-- =========================================================================
-- HỆ THỐNG SIMPLAIN STORE - SCRIPT BỔ SUNG BẢNG & CỘT CHO BÁN HÀNG TẠI QUẦY (POS)
-- Tương thích: Microsoft SQL Server (database Simpl4)
-- =========================================================================

-- CHUYỂN VÀO ĐÚNG DATABASE DỰ ÁN TRƯỚC KHI THỰC THI
USE Simpl4;
GO

-- 1. Bổ sung các cột mới vào bảng [don_hang] phục vụ bán hàng tại quầy
IF COL_LENGTH('don_hang', 'loai_don_hang') IS NULL
BEGIN
    ALTER TABLE don_hang ADD loai_don_hang VARCHAR(20) NOT NULL CONSTRAINT DF_don_hang_loai DEFAULT 'ONLINE';
    PRINT N'-> Đã thêm cột loai_don_hang vào bảng don_hang';
END;

IF COL_LENGTH('don_hang', 'tien_khach_dua') IS NULL
BEGIN
    ALTER TABLE don_hang ADD tien_khach_dua DECIMAL(18, 2) NULL CONSTRAINT DF_don_hang_tien_khach_dua DEFAULT 0;
    PRINT N'-> Đã thêm cột tien_khach_dua vào bảng don_hang';
END;

IF COL_LENGTH('don_hang', 'tien_thua_tra_khach') IS NULL
BEGIN
    ALTER TABLE don_hang ADD tien_thua_tra_khach DECIMAL(18, 2) NULL CONSTRAINT DF_don_hang_tien_thua DEFAULT 0;
    PRINT N'-> Đã thêm cột tien_thua_tra_khach vào bảng don_hang';
END;

IF COL_LENGTH('don_hang', 'ma_nhan_vien_id') IS NULL
BEGIN
    ALTER TABLE don_hang ADD ma_nhan_vien_id BIGINT NULL;
    IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK_don_hang_nhan_vien')
    BEGIN
        ALTER TABLE don_hang ADD CONSTRAINT FK_don_hang_nhan_vien 
        FOREIGN KEY (ma_nhan_vien_id) REFERENCES nhan_vien(id);
    END;
    PRINT N'-> Đã thêm cột ma_nhan_vien_id và khóa ngoại vào bảng don_hang';
END;

-- 2. Tạo bảng [hoa_don_cho] (Quản lý các tab hóa đơn chờ phục vụ tại quầy)
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'hoa_don_cho')
BEGIN
    CREATE TABLE hoa_don_cho (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        ma_hoa_don VARCHAR(50) NOT NULL UNIQUE,
        ma_nhan_vien_id BIGINT NULL,
        ma_khach_hang_id BIGINT NULL,
        ten_khach_hang NVARCHAR(150) NULL,
        so_dien_thoai_khach VARCHAR(20) NULL,
        ghi_chu NVARCHAR(500) NULL,
        trang_thai VARCHAR(30) NOT NULL DEFAULT 'CHO_THANH_TOAN',
        ngay_tao DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT FK_hdc_nhan_vien FOREIGN KEY (ma_nhan_vien_id) REFERENCES nhan_vien(id),
        CONSTRAINT FK_hdc_khach_hang FOREIGN KEY (ma_khach_hang_id) REFERENCES khach_hang(id)
    );
    PRINT N'-> Đã tạo mới bảng hoa_don_cho';
END;

-- 3. Tạo bảng [chi_tiet_hoa_don_cho] (Mặt hàng trong hóa đơn chờ)
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'chi_tiet_hoa_don_cho')
BEGIN
    CREATE TABLE chi_tiet_hoa_don_cho (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        ma_hoa_don_cho_id BIGINT NOT NULL,
        ma_bien_the_id BIGINT NOT NULL,
        so_luong INT NOT NULL DEFAULT 1,
        don_gia DECIMAL(18, 2) NOT NULL DEFAULT 0,
        thanh_tien DECIMAL(18, 2) NOT NULL DEFAULT 0,
        CONSTRAINT FK_cthdc_hoa_don_cho FOREIGN KEY (ma_hoa_don_cho_id) REFERENCES hoa_don_cho(id) ON DELETE CASCADE,
        CONSTRAINT FK_cthdc_bien_the FOREIGN KEY (ma_bien_the_id) REFERENCES bien_the_san_pham(id)
    );
    PRINT N'-> Đã tạo mới bảng chi_tiet_hoa_don_cho';
END;

-- 4. Bổ sung các phương thức thanh toán tại quầy nếu chưa có
IF NOT EXISTS (SELECT * FROM phuong_thuc_thanh_toan WHERE ma_phuong_thuc = 'TIEN_MAT')
BEGIN
    INSERT INTO phuong_thuc_thanh_toan (ma_phuong_thuc, ten_phuong_thuc, la_truc_tuyen, kich_hoat, mo_ta, bieu_tuong)
    VALUES ('TIEN_MAT', N'Tiền mặt tại quầy', 0, 1, N'Khách hàng thanh toán trực tiếp bằng tiền mặt tại quầy thu ngân', 'fa-money-bill-wave');
    PRINT N'-> Đã thêm phương thức TIEN_MAT';
END;

IF NOT EXISTS (SELECT * FROM phuong_thuc_thanh_toan WHERE ma_phuong_thuc = 'CHUYEN_KHOAN')
BEGIN
    INSERT INTO phuong_thuc_thanh_toan (ma_phuong_thuc, ten_phuong_thuc, la_truc_tuyen, kich_hoat, mo_ta, bieu_tuong)
    VALUES ('CHUYEN_KHOAN', N'Chuyển khoản ngân hàng / Quét QR', 1, 1, N'Chuyển khoản trực tiếp qua ngân hàng hoặc quét mã QR quầy thu ngân', 'fa-qrcode');
    PRINT N'-> Đã thêm phương thức CHUYEN_KHOAN';
END;

-- 5. Cho phép khách hàng mua tại quầy không bắt buộc có email (Chuyển sang Filtered Unique Index)
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
DECLARE @ConstraintName NVARCHAR(200);
SELECT @ConstraintName = tc.CONSTRAINT_NAME 
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc 
JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu ON tc.CONSTRAINT_NAME = ccu.CONSTRAINT_NAME 
WHERE tc.TABLE_NAME = 'khach_hang' AND ccu.COLUMN_NAME = 'email' AND tc.CONSTRAINT_TYPE = 'UNIQUE';

IF @ConstraintName IS NOT NULL
BEGIN
    EXEC('ALTER TABLE khach_hang DROP CONSTRAINT ' + @ConstraintName);
    PRINT N'-> Đã gỡ bỏ ràng buộc UNIQUE cũ trên cột email: ' + @ConstraintName;
END;

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'UQ_khach_hang_email_filtered')
BEGIN
    CREATE UNIQUE NONCLUSTERED INDEX UQ_khach_hang_email_filtered
    ON khach_hang(email)
    WHERE email IS NOT NULL AND email <> '';
    PRINT N'-> Đã tạo Filtered Unique Index cho email: chỉ ràng buộc duy nhất khi email khác NULL và rỗng';
END;
