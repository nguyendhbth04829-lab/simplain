IF OBJECT_ID(N'dbo.nha_cung_cap_sku_bao_gia', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.nha_cung_cap_sku_bao_gia (
        id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_nha_cung_cap_sku_bao_gia PRIMARY KEY,
        ma_ncc_id BIGINT NOT NULL,
        ma_bien_the_id BIGINT NOT NULL,
        gia_bao DECIMAL(18,2) NOT NULL,
        thoi_diem_bao_gia DATETIME2 NOT NULL,
        ghi_chu NVARCHAR(500) NULL,
        CONSTRAINT FK_bao_gia_ncc FOREIGN KEY (ma_ncc_id) REFERENCES dbo.nha_cung_cap(id),
        CONSTRAINT FK_bao_gia_bien_the FOREIGN KEY (ma_bien_the_id) REFERENCES dbo.bien_the_san_pham(id),
        CONSTRAINT CK_bao_gia_duong CHECK (gia_bao > 0)
    );
    CREATE INDEX IX_bao_gia_ncc_bien_the_thoi_diem
        ON dbo.nha_cung_cap_sku_bao_gia(ma_ncc_id, ma_bien_the_id, thoi_diem_bao_gia, id);
END;

IF OBJECT_ID(N'dbo.bien_the_san_pham_gia_ban_lich_su', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bien_the_san_pham_gia_ban_lich_su (
        id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bien_the_san_pham_gia_ban_lich_su PRIMARY KEY,
        ma_bien_the_id BIGINT NOT NULL,
        gia_ban DECIMAL(18,2) NOT NULL,
        thoi_diem DATETIME2 NOT NULL,
        loai_su_kien NVARCHAR(30) NOT NULL,
        CONSTRAINT FK_gia_ban_lich_su_bien_the FOREIGN KEY (ma_bien_the_id) REFERENCES dbo.bien_the_san_pham(id),
        CONSTRAINT CK_gia_ban_lich_su_duong CHECK (gia_ban >= 0)
    );
    CREATE INDEX IX_gia_ban_lich_su_bien_the_thoi_diem
        ON dbo.bien_the_san_pham_gia_ban_lich_su(ma_bien_the_id, thoi_diem, id);
    CREATE UNIQUE INDEX UX_gia_ban_lich_su_moc_ban_dau
        ON dbo.bien_the_san_pham_gia_ban_lich_su(ma_bien_the_id)
        WHERE loai_su_kien = N'MOC_BAN_DAU';
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'dbo.bien_the_san_pham_gia_ban_lich_su')
      AND name = N'UX_gia_ban_lich_su_moc_ban_dau'
)
BEGIN
    CREATE UNIQUE INDEX UX_gia_ban_lich_su_moc_ban_dau
        ON dbo.bien_the_san_pham_gia_ban_lich_su(ma_bien_the_id)
        WHERE loai_su_kien = N'MOC_BAN_DAU';
END;

-- Ghi nhận giá niêm yết hiện có làm mốc bắt đầu theo dõi; không suy đoán lịch sử trước thời điểm chạy migration.
INSERT INTO dbo.bien_the_san_pham_gia_ban_lich_su(ma_bien_the_id, gia_ban, thoi_diem, loai_su_kien)
SELECT bt.id, bt.gia_ban, SYSDATETIME(), N'MOC_BAN_DAU'
FROM dbo.bien_the_san_pham bt
WHERE NOT EXISTS (
    SELECT 1
    FROM dbo.bien_the_san_pham_gia_ban_lich_su ls
    WHERE ls.ma_bien_the_id = bt.id AND ls.loai_su_kien = N'MOC_BAN_DAU'
);
