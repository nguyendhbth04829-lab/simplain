-- Bảng lưu trữ liên kết Phân quyền động giữa Vai trò và Chức năng hệ thống
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'vai_tro_quyen_han')
BEGIN
    CREATE TABLE vai_tro_quyen_han (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        ma_vai_tro_id BIGINT NOT NULL,
        ma_quyen VARCHAR(50) NOT NULL,
        CONSTRAINT FK_vai_tro_quyen_han_vai_tro FOREIGN KEY (ma_vai_tro_id) REFERENCES vai_tro(id) ON DELETE CASCADE
    );

    CREATE UNIQUE INDEX UQ_vai_tro_quyen ON vai_tro_quyen_han(ma_vai_tro_id, ma_quyen);
END
GO

-- Seed quyền mặc định cho các vai trò hiện có
-- 1. ADMIN (ID 1) - Toàn quyền
IF EXISTS (SELECT 1 FROM vai_tro WHERE id = 1)
BEGIN
    INSERT INTO vai_tro_quyen_han (ma_vai_tro_id, ma_quyen)
    SELECT 1, q.ma_quyen
    FROM (
        VALUES 
            ('DASHBOARD'), ('SAN_PHAM'), ('BAN_HANG_POS'), ('DON_HANG'), 
            ('DOI_TRA'), ('KHUYEN_MAI'), ('THANH_TOAN'), ('NOI_DUNG'), 
            ('KHACH_HANG'), ('HE_THONG')
    ) AS q(ma_quyen)
    WHERE NOT EXISTS (
        SELECT 1 FROM vai_tro_quyen_han WHERE ma_vai_tro_id = 1 AND ma_quyen = q.ma_quyen
    );
END
GO

-- 2. CSKH (ID 2) - Chăm sóc khách hàng: Dashboard, Đổi trả, Đơn hàng, Khách hàng, Sản phẩm (tra cứu)
IF EXISTS (SELECT 1 FROM vai_tro WHERE id = 2)
BEGIN
    INSERT INTO vai_tro_quyen_han (ma_vai_tro_id, ma_quyen)
    SELECT 2, q.ma_quyen
    FROM (
        VALUES 
            ('DASHBOARD'), ('DOI_TRA'), ('DON_HANG'), ('KHACH_HANG'), ('SAN_PHAM')
    ) AS q(ma_quyen)
    WHERE NOT EXISTS (
        SELECT 1 FROM vai_tro_quyen_han WHERE ma_vai_tro_id = 2 AND ma_quyen = q.ma_quyen
    );
END
GO

-- 3. BAN_HANG (ID 3) - Thu ngân quầy POS: Dashboard, Bán hàng POS, Đơn hàng, Sản phẩm (tra cứu tồn), Khuyến mãi (tra cứu voucher/combo/flash sale)
IF EXISTS (SELECT 1 FROM vai_tro WHERE id = 3)
BEGIN
    INSERT INTO vai_tro_quyen_han (ma_vai_tro_id, ma_quyen)
    SELECT 3, q.ma_quyen
    FROM (
        VALUES 
            ('DASHBOARD'), ('BAN_HANG_POS'), ('DON_HANG'), ('SAN_PHAM'), ('KHUYEN_MAI')
    ) AS q(ma_quyen)
    WHERE NOT EXISTS (
        SELECT 1 FROM vai_tro_quyen_han WHERE ma_vai_tro_id = 3 AND ma_quyen = q.ma_quyen
    );
END
GO
