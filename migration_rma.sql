-- =========================================================================
-- HỆ THỐNG SIMPLAIN STORE - SCRIPT BỔ SUNG CHO CHỨC NĂNG ĐỔI TRẢ (RMA - US24)
-- Tương thích: Microsoft SQL Server (database Simpl4)
-- =========================================================================

USE Simpl4;
GO

-- 1. Bổ sung các cột mở rộng cho bảng yeu_cau_doi_tra nếu chưa có
IF COL_LENGTH('yeu_cau_doi_tra', 'hinh_anh_minh_chung') IS NULL
BEGIN
    ALTER TABLE yeu_cau_doi_tra ADD hinh_anh_minh_chung NVARCHAR(2000) NULL;
    PRINT N'-> Đã thêm cột hinh_anh_minh_chung vào bảng yeu_cau_doi_tra';
END
ELSE
BEGIN
    ALTER TABLE yeu_cau_doi_tra ALTER COLUMN hinh_anh_minh_chung NVARCHAR(2000) NULL;
    PRINT N'-> Đã cập nhật độ dài cột hinh_anh_minh_chung thành NVARCHAR(2000)';
END;
GO

IF COL_LENGTH('yeu_cau_doi_tra', 'ghi_chu_admin') IS NULL
BEGIN
    ALTER TABLE yeu_cau_doi_tra ADD ghi_chu_admin NVARCHAR(500) NULL;
    PRINT N'-> Đã thêm cột ghi_chu_admin vào bảng yeu_cau_doi_tra';
END;
GO

-- 2. Bổ sung các cột thông tin người nhận và địa chỉ nhận hàng / thu hồi hàng (Chuẩn Shopee)
IF COL_LENGTH('yeu_cau_doi_tra', 'ten_nguoi_nhan') IS NULL
BEGIN
    ALTER TABLE yeu_cau_doi_tra ADD ten_nguoi_nhan NVARCHAR(150) NULL;
    PRINT N'-> Đã thêm cột ten_nguoi_nhan vào bảng yeu_cau_doi_tra';
END;
GO

IF COL_LENGTH('yeu_cau_doi_tra', 'so_dien_thoai_nhan') IS NULL
BEGIN
    ALTER TABLE yeu_cau_doi_tra ADD so_dien_thoai_nhan VARCHAR(20) NULL;
    PRINT N'-> Đã thêm cột so_dien_thoai_nhan vào bảng yeu_cau_doi_tra';
END;
GO

IF COL_LENGTH('yeu_cau_doi_tra', 'dia_chi_nhan_hang') IS NULL
BEGIN
    ALTER TABLE yeu_cau_doi_tra ADD dia_chi_nhan_hang NVARCHAR(500) NULL;
    PRINT N'-> Đã thêm cột dia_chi_nhan_hang vào bảng yeu_cau_doi_tra';
END;
GO

IF COL_LENGTH('yeu_cau_doi_tra', 'ghi_chu_giao_hang') IS NULL
BEGIN
    ALTER TABLE yeu_cau_doi_tra ADD ghi_chu_giao_hang NVARCHAR(255) NULL;
    PRINT N'-> Đã thêm cột ghi_chu_giao_hang vào bảng yeu_cau_doi_tra';
END;
GO

IF COL_LENGTH('yeu_cau_doi_tra', 'hinh_anh_bill') IS NULL
BEGIN
    ALTER TABLE yeu_cau_doi_tra ADD hinh_anh_bill NVARCHAR(1000) NULL;
    PRINT N'-> Đã thêm cột hinh_anh_bill vào bảng yeu_cau_doi_tra';
END;
GO

-- 2. Đảm bảo bảng yeu_cau_doi_tra và chi_tiet_san_pham_doi_tra đã được tạo
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'yeu_cau_doi_tra')
BEGIN
    CREATE TABLE yeu_cau_doi_tra (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        ma_yeu_cau_rma VARCHAR(50) NOT NULL UNIQUE,
        ma_don_hang_id BIGINT NOT NULL,
        ma_khach_hang_id BIGINT NOT NULL,
        loai_doi_tra VARCHAR(50) NOT NULL, -- 'DOI_HANG' hoặc 'TRA_HANG_HOAN_TIEN'
        ly_do NVARCHAR(255) NOT NULL,
        so_tien_hoan DECIMAL(18, 2) NULL,
        thong_tin_ngan_hang NVARCHAR(255) NULL,
        trang_thai VARCHAR(50) NOT NULL DEFAULT 'CHO_XU_LY', -- 'CHO_XU_LY', 'DA_TIEP_NHAN', 'DANG_XU_LY', 'HOAN_THANH', 'TU_CHOI'
        nguoi_duyet_id BIGINT NULL,
        ngay_tao DATETIME2 DEFAULT GETDATE(),
        hinh_anh_minh_chung NVARCHAR(500) NULL,
        ghi_chu_admin NVARCHAR(500) NULL,
        CONSTRAINT FK_rma_don_hang FOREIGN KEY (ma_don_hang_id) REFERENCES don_hang(id),
        CONSTRAINT FK_rma_khach_hang FOREIGN KEY (ma_khach_hang_id) REFERENCES khach_hang(id),
        CONSTRAINT FK_rma_nhan_vien FOREIGN KEY (nguoi_duyet_id) REFERENCES nhan_vien(id)
    );
    PRINT N'-> Đã tạo bảng yeu_cau_doi_tra';
END;
GO

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'chi_tiet_san_pham_doi_tra')
BEGIN
    CREATE TABLE chi_tiet_san_pham_doi_tra (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        ma_yeu_cau_doi_tra_id BIGINT NOT NULL,
        ma_chi_tiet_don_hang_id BIGINT NOT NULL,
        so_luong_tra INT NOT NULL,
        ma_bien_the_moi_id BIGINT NULL,
        tinh_trang_san_pham NVARCHAR(255) NULL,
        CONSTRAINT FK_ctdt_yeu_cau FOREIGN KEY (ma_yeu_cau_doi_tra_id) REFERENCES yeu_cau_doi_tra(id) ON DELETE CASCADE,
        CONSTRAINT FK_ctdt_chi_tiet_don_hang FOREIGN KEY (ma_chi_tiet_don_hang_id) REFERENCES chi_tiet_don_hang(id),
        CONSTRAINT FK_ctdt_bien_the_moi FOREIGN KEY (ma_bien_the_moi_id) REFERENCES bien_the_san_pham(id)
    );
    PRINT N'-> Đã tạo bảng chi_tiet_san_pham_doi_tra';
END;
GO

-- 3. Bổ sung các trạng thái đơn hàng phục vụ quy trình Đổi / Trả hàng chuẩn Shopee
IF NOT EXISTS (SELECT 1 FROM trang_thai_don_hang WHERE ma_trang_thai = 'DANG_DOI_TRA')
BEGIN
    INSERT INTO trang_thai_don_hang (ma_trang_thai, ten_trang_thai, ma_mau_hex)
    VALUES ('DANG_DOI_TRA', N'Đang đổi / trả', '#D97706');
    PRINT N'-> Đã thêm trạng thái đơn hàng DANG_DOI_TRA';
END;
GO

IF NOT EXISTS (SELECT 1 FROM trang_thai_don_hang WHERE ma_trang_thai = 'DA_TRA_HANG')
BEGIN
    INSERT INTO trang_thai_don_hang (ma_trang_thai, ten_trang_thai, ma_mau_hex)
    VALUES ('DA_TRA_HANG', N'Đã trả hàng (Hoàn tiền)', '#E11D48');
    PRINT N'-> Đã thêm trạng thái đơn hàng DA_TRA_HANG';
END;
GO

IF NOT EXISTS (SELECT 1 FROM trang_thai_don_hang WHERE ma_trang_thai = 'DA_DOI_HANG')
BEGIN
    INSERT INTO trang_thai_don_hang (ma_trang_thai, ten_trang_thai, ma_mau_hex)
    VALUES ('DA_DOI_HANG', N'Đã đổi hàng', '#2563EB');
    PRINT N'-> Đã thêm trạng thái đơn hàng DA_DOI_HANG';
END;
GO
