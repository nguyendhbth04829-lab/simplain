SET NOCOUNT ON;
SET XACT_ABORT ON;

DECLARE @NccId BIGINT = (SELECT id FROM dbo.nha_cung_cap WHERE ma_ncc = N'2222');
DECLARE @BienTheId BIGINT = (SELECT id FROM dbo.bien_the_san_pham WHERE ma_sku = N'SP-SIMPLAIN-465-EN-S');

IF @NccId IS NULL OR @BienTheId IS NULL
    THROW 50001, 'Khong tim thay NCC 2222 hoac SKU SP-SIMPLAIN-465-EN-S de tao du lieu kiem thu.', 1;

IF NOT EXISTS (SELECT 1 FROM dbo.nha_cung_cap_sku WHERE ma_ncc_id = @NccId AND ma_bien_the_id = @BienTheId)
    THROW 50002, 'SKU kiem thu khong duoc phan cong cho NCC 2222.', 1;

DECLARE @BaoGia TABLE (
    ky_hieu NVARCHAR(20) NOT NULL PRIMARY KEY,
    gia DECIMAL(18,2) NOT NULL,
    thoi_diem DATETIME2 NOT NULL
);

INSERT INTO @BaoGia(ky_hieu, gia, thoi_diem) VALUES
    (N'2021-06', 55000, '2021-06-10T09:00:00'),
    (N'2021-12', 57000, '2021-12-15T14:30:00'),
    (N'2022-06', 60000, '2022-06-20T10:15:00'),
    (N'2022-12', 62000, '2022-12-10T16:00:00'),
    (N'2023-06', 65000, '2023-06-05T11:20:00'),
    (N'2023-12', 64000, '2023-12-18T13:10:00'),
    (N'2024-06', 68000, '2024-06-12T08:45:00'),
    (N'2024-12', 70000, '2024-12-20T15:30:00'),
    (N'2025-06', 73000, '2025-06-15T10:00:00'),
    (N'2025-12', 75000, '2025-12-18T09:30:00'),
    (N'2026-06', 76000, '2026-06-20T14:15:00'),
    (N'2026-10', 78000, '2026-10-01T11:00:00');

;WITH sample_points(n) AS (
    SELECT 0 UNION ALL SELECT n + 1 FROM sample_points WHERE n < 23
)
INSERT INTO @BaoGia(ky_hieu, gia, thoi_diem)
SELECT N'DENSE-P-' + RIGHT(N'00' + CONVERT(NVARCHAR(2), n + 1), 2),
       78000 + n * 65 + (n % 4) * 90,
       DATEADD(MINUTE, n * 5, CONVERT(DATETIME2, '2026-11-10T08:00:00'))
FROM sample_points
OPTION (MAXRECURSION 24);

;WITH sample_points(n) AS (
    SELECT 0 UNION ALL SELECT n + 1 FROM sample_points WHERE n < 23
)
INSERT INTO @BaoGia(ky_hieu, gia, thoi_diem)
SELECT N'DENSE-V-' + RIGHT(N'00' + CONVERT(NVARCHAR(2), n + 1), 2),
       15000 + n * 14000 + (n % 3) * 5000,
       DATEADD(MINUTE, n, CONVERT(DATETIME2, '2026-11-10T14:00:00'))
FROM sample_points
OPTION (MAXRECURSION 24);

DECLARE @GiaBan TABLE (
    thoi_diem DATETIME2 NOT NULL PRIMARY KEY,
    gia DECIMAL(18,2) NOT NULL
);

INSERT INTO @GiaBan(thoi_diem, gia) VALUES
    ('2021-03-01T10:00:00', 85000),
    ('2021-11-01T10:00:00', 89000),
    ('2022-07-01T10:00:00', 93000),
    ('2023-03-01T10:00:00', 95000),
    ('2023-11-01T10:00:00', 99000),
    ('2024-05-01T10:00:00', 103000),
    ('2024-11-01T10:00:00', 108000),
    ('2025-05-01T10:00:00', 112000),
    ('2025-11-01T10:00:00', 117000),
    ('2026-04-01T10:00:00', 120000);

;WITH sample_points(n) AS (
    SELECT 0 UNION ALL SELECT n + 1 FROM sample_points WHERE n < 23
)
INSERT INTO @GiaBan(thoi_diem, gia)
SELECT DATEADD(MINUTE, n * 5, CONVERT(DATETIME2, '2026-11-10T12:00:00')),
       110000 + n * 70 + (n % 5) * 120
FROM sample_points
OPTION (MAXRECURSION 24);

BEGIN TRY
    BEGIN TRANSACTION;

    INSERT INTO dbo.nha_cung_cap_sku_bao_gia(ma_ncc_id, ma_bien_the_id, gia_bao, thoi_diem_bao_gia, ghi_chu)
    SELECT @NccId, @BienTheId, seed.gia, seed.thoi_diem, N'[TEST-LICHSU] Báo giá ' + seed.ky_hieu
    FROM @BaoGia seed
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.nha_cung_cap_sku_bao_gia quote
        WHERE quote.ma_ncc_id = @NccId
          AND quote.ma_bien_the_id = @BienTheId
          AND quote.ghi_chu = N'[TEST-LICHSU] Báo giá ' + seed.ky_hieu
    );
    DECLARE @SoBaoGiaThem INT = @@ROWCOUNT;

    INSERT INTO dbo.bien_the_san_pham_gia_ban_lich_su(ma_bien_the_id, gia_ban, thoi_diem, loai_su_kien)
    SELECT @BienTheId, seed.gia, seed.thoi_diem, N'TEST_LICHSU'
    FROM @GiaBan seed
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.bien_the_san_pham_gia_ban_lich_su price
        WHERE price.ma_bien_the_id = @BienTheId
          AND price.loai_su_kien = N'TEST_LICHSU'
          AND price.thoi_diem = seed.thoi_diem
    );
    DECLARE @SoGiaBanThem INT = @@ROWCOUNT;

    COMMIT TRANSACTION;
    SELECT @SoBaoGiaThem AS so_bao_gia_da_them, @SoGiaBanThem AS so_moc_gia_ban_da_them,
           @NccId AS nha_cung_cap_id, @BienTheId AS bien_the_id;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
