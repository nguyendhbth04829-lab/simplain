-- =========================================================================================
-- BỔ SUNG DATABASE CHO TÍNH NĂNG:
-- 1. Quên mật khẩu xác thực qua Gmail (Bảng: quen_mat_khau_otp)
-- 2. Cổng thanh toán chuyên nghiệp đa nhà cung cấp (Cột: loai_cong trong phuong_thuc_thanh_toan)
-- =========================================================================================

-- 1. Bảng lưu trữ OTP quên mật khẩu
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'quen_mat_khau_otp')
BEGIN
    CREATE TABLE quen_mat_khau_otp (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        email VARCHAR(150) NOT NULL,
        ma_otp VARCHAR(10) NOT NULL,
        loai_nguoi_dung VARCHAR(20) DEFAULT 'KHACH_HANG', -- KHACH_HANG hoặc NHAN_VIEN
        thoi_gian_tao DATETIME2 DEFAULT SYSDATETIME(),
        thoi_gian_het_han DATETIME2 NOT NULL,
        da_su_dung BIT DEFAULT 0,
        so_lan_sai INT DEFAULT 0
    );
    CREATE INDEX idx_quen_mat_khau_email ON quen_mat_khau_otp(email);
    PRINT 'Đã tạo bảng quen_mat_khau_otp thành công!';
END
GO

-- 2. Thêm cột loai_cong vào bảng phuong_thuc_thanh_toan nếu chưa có
IF NOT EXISTS (
    SELECT * FROM sys.columns 
    WHERE object_id = OBJECT_ID('phuong_thuc_thanh_toan') AND name = 'loai_cong'
)
BEGIN
    ALTER TABLE phuong_thuc_thanh_toan ADD loai_cong VARCHAR(50) DEFAULT 'KHAC';
    PRINT 'Đã thêm cột loai_cong vào bảng phuong_thuc_thanh_toan!';
END
GO

-- 3. Chuẩn hóa dữ liệu loai_cong cho các bản ghi hiện tại
UPDATE phuong_thuc_thanh_toan SET loai_cong = 'COD' WHERE ma_phuong_thuc LIKE '%COD%' OR ma_phuong_thuc LIKE '%TIEN_MAT%';
UPDATE phuong_thuc_thanh_toan SET loai_cong = 'VNPAY' WHERE ma_phuong_thuc LIKE '%VNPAY%';
UPDATE phuong_thuc_thanh_toan SET loai_cong = 'MOMO' WHERE ma_phuong_thuc LIKE '%MOMO%';
UPDATE phuong_thuc_thanh_toan SET loai_cong = 'VIETQR' WHERE ma_phuong_thuc LIKE '%CHUYEN_KHOAN%';
UPDATE phuong_thuc_thanh_toan SET loai_cong = 'KHAC' WHERE loai_cong IS NULL;
GO
