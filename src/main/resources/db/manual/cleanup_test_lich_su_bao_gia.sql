SET NOCOUNT ON;

DECLARE @NccId BIGINT = (SELECT id FROM dbo.nha_cung_cap WHERE ma_ncc = N'2222');
DECLARE @BienTheId BIGINT = (SELECT id FROM dbo.bien_the_san_pham WHERE ma_sku = N'SP-SIMPLAIN-465-EN-S');

IF @NccId IS NULL OR @BienTheId IS NULL
    THROW 50001, 'Khong tim thay cap NCC/SKU kiem thu.', 1;

DELETE FROM dbo.nha_cung_cap_sku_bao_gia
WHERE ma_ncc_id = @NccId AND ma_bien_the_id = @BienTheId
  AND LEFT(ghi_chu, LEN(N'[TEST-LICHSU] ')) = N'[TEST-LICHSU] ';
DECLARE @BaoGiaDaXoa INT = @@ROWCOUNT;

DELETE FROM dbo.bien_the_san_pham_gia_ban_lich_su
WHERE ma_bien_the_id = @BienTheId AND loai_su_kien = N'TEST_LICHSU';
DECLARE @GiaBanDaXoa INT = @@ROWCOUNT;

SELECT @BaoGiaDaXoa AS so_bao_gia_kiem_thu_da_xoa,
       @GiaBanDaXoa AS so_moc_gia_ban_kiem_thu_da_xoa;
