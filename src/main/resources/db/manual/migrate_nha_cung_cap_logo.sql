IF COL_LENGTH(N'dbo.nha_cung_cap', N'anh_logo') IS NULL
BEGIN
    ALTER TABLE dbo.nha_cung_cap ADD anh_logo NVARCHAR(500) NULL;
END;
