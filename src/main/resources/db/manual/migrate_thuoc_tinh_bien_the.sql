SET XACT_ABORT ON;
BEGIN TRANSACTION;

IF OBJECT_ID(N'dbo.thuoc_tinh_bien_the', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.thuoc_tinh_bien_the (
        id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_thuoc_tinh_bien_the PRIMARY KEY,
        ma_thuoc_tinh VARCHAR(80) NOT NULL,
        ten_thuoc_tinh NVARCHAR(120) NOT NULL,
        kich_hoat BIT NOT NULL CONSTRAINT DF_thuoc_tinh_bien_the_kich_hoat DEFAULT (1),
        thu_tu INT NOT NULL CONSTRAINT DF_thuoc_tinh_bien_the_thu_tu DEFAULT (0),
        CONSTRAINT UQ_thuoc_tinh_bien_the_ma UNIQUE (ma_thuoc_tinh)
    );
END;

IF OBJECT_ID(N'dbo.bien_the_san_pham_thuoc_tinh', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bien_the_san_pham_thuoc_tinh (
        id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_bien_the_san_pham_thuoc_tinh PRIMARY KEY,
        ma_bien_the_id BIGINT NOT NULL,
        ma_thuoc_tinh_id BIGINT NOT NULL,
        gia_tri NVARCHAR(500) NOT NULL,
        CONSTRAINT UQ_bien_the_thuoc_tinh UNIQUE (ma_bien_the_id, ma_thuoc_tinh_id),
        CONSTRAINT FK_bien_the_thuoc_tinh_bien_the FOREIGN KEY (ma_bien_the_id)
            REFERENCES dbo.bien_the_san_pham(id),
        CONSTRAINT FK_bien_the_thuoc_tinh_thuoc_tinh FOREIGN KEY (ma_thuoc_tinh_id)
            REFERENCES dbo.thuoc_tinh_bien_the(id)
    );
END;

IF NOT EXISTS (SELECT 1 FROM dbo.thuoc_tinh_bien_the WHERE ma_thuoc_tinh = 'mauSac')
    INSERT INTO dbo.thuoc_tinh_bien_the (ma_thuoc_tinh, ten_thuoc_tinh, kich_hoat, thu_tu)
    VALUES ('mauSac', N'Màu', 1, 10);

IF NOT EXISTS (SELECT 1 FROM dbo.thuoc_tinh_bien_the WHERE ma_thuoc_tinh = 'kichThuoc')
    INSERT INTO dbo.thuoc_tinh_bien_the (ma_thuoc_tinh, ten_thuoc_tinh, kich_hoat, thu_tu)
    VALUES ('kichThuoc', N'Kích thước', 1, 20);

INSERT INTO dbo.bien_the_san_pham_thuoc_tinh (ma_bien_the_id, ma_thuoc_tinh_id, gia_tri)
SELECT bt.id, tt.id, ms.ten_mau
FROM dbo.bien_the_san_pham bt
JOIN dbo.mau_sac ms ON ms.id = bt.ma_mau_sac_id
JOIN dbo.thuoc_tinh_bien_the tt ON tt.ma_thuoc_tinh = 'mauSac'
WHERE NULLIF(LTRIM(RTRIM(ms.ten_mau)), N'') IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM dbo.bien_the_san_pham_thuoc_tinh x
      WHERE x.ma_bien_the_id = bt.id AND x.ma_thuoc_tinh_id = tt.id
  );

INSERT INTO dbo.bien_the_san_pham_thuoc_tinh (ma_bien_the_id, ma_thuoc_tinh_id, gia_tri)
SELECT bt.id, tt.id, kt.ten_kich_thuoc
FROM dbo.bien_the_san_pham bt
JOIN dbo.kich_thuoc kt ON kt.id = bt.ma_kich_thuoc_id
JOIN dbo.thuoc_tinh_bien_the tt ON tt.ma_thuoc_tinh = 'kichThuoc'
WHERE NULLIF(LTRIM(RTRIM(kt.ten_kich_thuoc)), N'') IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM dbo.bien_the_san_pham_thuoc_tinh x
      WHERE x.ma_bien_the_id = bt.id AND x.ma_thuoc_tinh_id = tt.id
  );

COMMIT TRANSACTION;
