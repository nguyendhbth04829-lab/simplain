-- Simpl4 database schema extracted and formatted from the supplied PDF.
-- Note: This preserves the schema statements shown in the PDF. The PDF does not show explicit FOREIGN KEY constraints.

CREATE DATABASE [Simpl4];
GO
USE [Simpl4];
GO

CREATE TABLE [vai_tro] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_vai_tro] varchar(50) UNIQUE NOT NULL,
    [ten_vai_tro] nvarchar(100) NOT NULL,
    [mo_ta] nvarchar(255),
    [trang_thai] int NOT NULL DEFAULT (1))
GO

CREATE TABLE [nhan_vien] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_nhan_vien] varchar(50) UNIQUE NOT NULL,
    [ho_ten] nvarchar(150) NOT NULL,
    [ten_dang_nhap] varchar(100) UNIQUE NOT NULL,
    [mat_khau_hash] varchar(255) NOT NULL,
    [so_dien_thoai] varchar(20),
    [email] varchar(150),
    [ma_vai_tro_id] bigint NOT NULL,
    [trang_thai] int NOT NULL DEFAULT (1),
    [ngay_tao] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [khach_hang] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_khach_hang] varchar(50) UNIQUE NOT NULL,
    [ho_ten] nvarchar(150) NOT NULL,
    [so_dien_thoai] varchar(20) UNIQUE,
    [email] varchar(150) UNIQUE,
    [mat_khau_hash] varchar(255),
    [anh_dai_dien] varchar(500),
    [gioi_tinh] nvarchar(10),
    [ngay_sinh] date,
    [trang_thai] int NOT NULL DEFAULT (1),
    [ngay_tao] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [dia_chi] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_khach_hang_id] bigint NOT NULL,
    [ten_nguoi_nhan] nvarchar(150) NOT NULL,
    [so_dien_thoai] varchar(20) NOT NULL,
    [tinh_thanh] nvarchar(100) NOT NULL,
    [quan_huyen] nvarchar(100) NOT NULL,
    [phuong_xa] nvarchar(100) NOT NULL,
    [dia_chi_chi_tiet] nvarchar(255) NOT NULL,
    [la_mac_dinh] bit DEFAULT (0))
GO

CREATE TABLE [danh_muc] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_danh_muc_cha] bigint,
    [ten_danh_muc] nvarchar(150) NOT NULL,
    [duong_dan_slug] varchar(150) UNIQUE NOT NULL,
    [thu_tu_hien_thi] int DEFAULT (0),
    [kich_hoat] bit DEFAULT (1))
GO

CREATE TABLE [bo_suu_tap] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ten_bo_suu_tap] nvarchar(150) NOT NULL,
    [duong_dan_slug] varchar(150) UNIQUE NOT NULL,
    [hinh_banner_url] varchar(500),
    [kich_hoat] bit DEFAULT (1))
GO

CREATE TABLE [mau_sac] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_mau] varchar(50) UNIQUE NOT NULL,
    [ten_mau] nvarchar(100) NOT NULL)
GO

CREATE TABLE [kich_thuoc] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_kich_thuoc] varchar(50) UNIQUE NOT NULL,
    [ten_kich_thuoc] nvarchar(50) NOT NULL)
GO

CREATE TABLE [san_pham] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_danh_muc_id] bigint NOT NULL,
    [ma_bo_suu_tap_id] bigint,
    [ma_san_pham] varchar(50) UNIQUE NOT NULL,
    [ten_san_pham] nvarchar(255) NOT NULL,
    [duong_dan_slug] varchar(255) UNIQUE NOT NULL,
    [chat_lieu] nvarchar(150),
    [form_dang] nvarchar(100),
    [mo_ta_chi_tiet] ntext,
    [huong_dan_chon_size] ntext,
    [gia_co_ban] decimal(18,2) NOT NULL DEFAULT (0),
    [noi_bat] bit DEFAULT (0),
    [kich_hoat] bit DEFAULT (1),
    [ngay_tao] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [bien_the_san_pham] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_san_pham_id] bigint NOT NULL,
    [ma_mau_sac_id] bigint NOT NULL,
    [ma_kich_thuoc_id] bigint NOT NULL,
    [ma_sku] varchar(100) UNIQUE NOT NULL,
    [ma_vach_barcode] varchar(100) UNIQUE,
    [so_luong] int NOT NULL DEFAULT (0),
    [gia_ban] decimal(18,2) NOT NULL,
    [gia_goc] decimal(18,2) NOT NULL DEFAULT (0),
    [kich_hoat] bit DEFAULT (1))
GO

CREATE TABLE [hinh_anh] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_san_pham_id] bigint NOT NULL,
    [ma_bien_the_id] bigint,
    [duong_dan_anh] varchar(500) NOT NULL,
    [la_anh_dai_dien] bit DEFAULT (0),
    [thu_tu] int DEFAULT (0))
GO

CREATE TABLE [goi_combo] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_combo] varchar(50) UNIQUE NOT NULL,
    [ten_combo] nvarchar(255) NOT NULL,
    [duong_dan_slug] varchar(255) UNIQUE NOT NULL,
    [gia_combo] decimal(18,2) NOT NULL,
    [so_mon_quy_dinh] int NOT NULL DEFAULT (2),
    [mo_ta] ntext,
    [kich_hoat] bit DEFAULT (1))
GO

CREATE TABLE [san_pham_trong_combo] ([ma_combo_id] bigint NOT NULL,
    [ma_san_pham_id] bigint NOT NULL,PRIMARY KEY ([ma_combo_id],
    [ma_san_pham_id]))
GO

CREATE TABLE [gio_hang] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_khach_hang_id] bigint UNIQUE,
    [ngay_cap_nhat] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [chi_tiet_gio_hang] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_gio_hang_id] bigint NOT NULL,
    [ma_bien_the_id] bigint NOT NULL,
    [ma_combo_id] bigint,
    [nhom_combo_token] varchar(50),
    [so_luong] int NOT NULL DEFAULT (1),
    [ngay_them] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [san_pham_yeu_thich] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_khach_hang_id] bigint NOT NULL,
    [ma_san_pham_id] bigint NOT NULL,
    [ngay_them] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [ma_giam_gia] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_code] varchar(50) UNIQUE NOT NULL,
    [ten_voucher] nvarchar(150) NOT NULL,
    [loai_giam_gia] varchar(20) NOT NULL,
    [gia_tri_giam] decimal(18,2) NOT NULL,
    [giam_toi_da] decimal(18,2),
    [gia_tri_don_toi_thieu] decimal(18,2) NOT NULL DEFAULT (0),
    [tong_so_luong] int,
    [so_luong_da_dung] int NOT NULL DEFAULT (0),
    [gioi_han_moi_user] int NOT NULL DEFAULT (1),
    [ngay_bat_dau] datetime2 NOT NULL,
    [ngay_ket_thuc] datetime2 NOT NULL,
    [kich_hoat] bit DEFAULT (1))
GO

CREATE TABLE [voucher_so_huu] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_khach_hang_id] bigint NOT NULL,
    [ma_giam_gia_id] bigint NOT NULL,
    [da_su_dung] bit DEFAULT (0),
    [ngay_nhan] datetime2 DEFAULT (GETDATE()),
    [ngay_su_dung] datetime2)
GO

CREATE TABLE [trang_thai_don_hang] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_trang_thai] varchar(50) UNIQUE NOT NULL,
    [ten_trang_thai] nvarchar(100) NOT NULL,
    [ma_mau_hex] varchar(10))
GO

CREATE TABLE [don_hang] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_don_hang] varchar(50) UNIQUE NOT NULL,
    [ma_khach_hang_id] bigint,
    [ma_trang_thai_id] bigint NOT NULL,
    [ma_giam_gia_id] bigint,
    [ma_van_don] varchar(50),
    [tong_tien_hang] decimal(18,2) NOT NULL DEFAULT (0),
    [tien_giam_gia] decimal(18,2) DEFAULT (0),
    [tien_coolcash_dung] decimal(18,2) DEFAULT (0),
    [phi_van_chuyen] decimal(18,2) DEFAULT (0),
    [tong_thanh_toan] decimal(18,2) NOT NULL DEFAULT (0),
    [ten_nguoi_nhan] nvarchar(150) NOT NULL,
    [so_dien_thoai_nhan] varchar(20) NOT NULL,
    [dia_chi_giao_hang] nvarchar(500) NOT NULL,
    [ghi_chu] nvarchar(500),
    [ngay_tao] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [chi_tiet_don_hang] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_don_hang_id] bigint NOT NULL,
    [ma_bien_the_id] bigint NOT NULL,
    [ma_combo_id] bigint,
    [nhom_combo_token] varchar(50),
    [ten_san_pham] nvarchar(255) NOT NULL,
    [ma_sku] varchar(100) NOT NULL,
    [don_gia] decimal(18,2) NOT NULL,
    [so_luong] int NOT NULL,
    [thanh_tien] decimal(18,2) NOT NULL)
GO

CREATE TABLE [lich_su_trang_thai_don_hang] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_don_hang_id] bigint NOT NULL,
    [ma_trang_thai_moi_id] bigint NOT NULL,
    [nguoi_thuc_hien_id] bigint,
    [ghi_chu] nvarchar(500),
    [ngay_chuyen] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [phuong_thuc_thanh_toan] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_phuong_thuc] varchar(50) UNIQUE NOT NULL,
    [ten_phuong_thuc] nvarchar(100) NOT NULL,
    [la_truc_tuyen] bit DEFAULT (0),
    [kich_hoat] bit DEFAULT (1))
GO

CREATE TABLE [giao_dich_thanh_toan] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_don_hang_id] bigint NOT NULL,
    [ma_phuong_thuc_id] bigint NOT NULL,
    [ma_giao_dich_noi_bo] varchar(100) UNIQUE NOT NULL,
    [ma_giao_dich_cong] varchar(100),
    [so_tien] decimal(18,2) NOT NULL,
    [trang_thai] varchar(50) NOT NULL DEFAULT 'CHO_XU_LY',
    [du_lieu_phan_hoi] ntext,
    [ngay_tao] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [yeu_cau_doi_tra] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_yeu_cau_rma] varchar(50) UNIQUE NOT NULL,
    [ma_don_hang_id] bigint NOT NULL,
    [ma_khach_hang_id] bigint NOT NULL,
    [loai_doi_tra] varchar(50) NOT NULL,
    [ly_do] nvarchar(255) NOT NULL,
    [so_tien_hoan] decimal(18,2) DEFAULT (0),
    [thong_tin_ngan_hang] nvarchar(255),
    [trang_thai] varchar(50) NOT NULL DEFAULT 'CHO_DUYET',
    [nguoi_duyet_id] bigint,
    [ngay_tao] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [chi_tiet_san_pham_doi_tra] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_yeu_cau_doi_tra_id] bigint NOT NULL,
    [ma_chi_tiet_don_hang_id] bigint NOT NULL,
    [so_luong_tra] int NOT NULL DEFAULT (1),
    [ma_bien_the_moi_id] bigint,
    [tinh_trang_san_pham] nvarchar(255))
GO

CREATE TABLE [flash_sale] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [tieu_de] nvarchar(150) NOT NULL,
    [thoi_gian_bat_dau] datetime2 NOT NULL,
    [thoi_gian_ket_thuc] datetime2 NOT NULL,
    [kich_hoat] bit DEFAULT (1))
GO

CREATE TABLE [chi_tiet_flash_sale] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_flash_sale_id] bigint NOT NULL,
    [ma_bien_the_id] bigint NOT NULL,
    [gia_flash_sale] decimal(18,2) NOT NULL,
    [so_luong_mo_ban] int NOT NULL,
    [so_luong_da_ban] int NOT NULL DEFAULT (0))
GO

CREATE TABLE [hang_thanh_vien] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ten_hang] nvarchar(50) UNIQUE NOT NULL,
    [chi_tieu_nam_toi_thieu] decimal(18,2) NOT NULL,
    [phan_tram_hoan_tien] decimal(5,2) NOT NULL)
GO

CREATE TABLE [ho_so_hoi_vien] ([ma_khach_hang_id] bigint PRIMARY KEY,
    [ma_hang_hien_tai_id] bigint NOT NULL,
    [so_du_coolcash] decimal(18,2) NOT NULL DEFAULT (0),
    [tong_chi_tieu_nam] decimal(18,2) NOT NULL DEFAULT (0))
GO

CREATE TABLE [bien_dong_diem] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_khach_hang_id] bigint NOT NULL,
    [ma_don_hang_id] bigint,
    [loai_bien_dong] varchar(20) NOT NULL,
    [so_diem] decimal(18,2) NOT NULL,
    [noi_dung] nvarchar(255) NOT NULL,
    [ngay_tao] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [danh_gia_san_pham] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_san_pham_id] bigint NOT NULL,
    [ma_khach_hang_id] bigint NOT NULL,
    [ma_chi_tiet_don_hang_id] bigint,
    [so_sao] int NOT NULL,
    [noi_dung] ntext,
    [danh_sach_anh_json] ntext,
    [ngay_tao] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [bai_viet_blog] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_tac_gia_id] bigint NOT NULL,
    [chuyen_muc] nvarchar(100) NOT NULL,
    [tieu_de] nvarchar(255) NOT NULL,
    [duong_dan_slug] varchar(255) UNIQUE NOT NULL,
    [mo_ta_ngan] nvarchar(500),
    [noi_dung_html] ntext NOT NULL,
    [anh_dai_dien_url] varchar(500),
    [ngay_xuat_ban] datetime2 DEFAULT (GETDATE()))
GO

CREATE TABLE [banner_quang_cao] ([id] bigint PRIMARY KEY IDENTITY(1, 1),
    [tieu_de] nvarchar(150) NOT NULL,
    [hinh_anh_url] varchar(500) NOT NULL,
    [duong_dan_dich] varchar(500),
    [vi_tri_hien_thi] varchar(50) NOT NULL,
    [kich_hoat] bit DEFAULT (1))
GO

CREATE UNIQUE INDEX [san_pham_yeu_thich_index_0] ON [san_pham_yeu_thich] ([ma_khach_hang_id], [ma_san_pham_id])
GO

CREATE UNIQUE INDEX [voucher_so_huu_index_1] ON [voucher_so_huu] ([ma_khach_hang_id], [ma_giam_gia_id])
GO

CREATE UNIQUE INDEX [chi_tiet_flash_sale_index_2] ON [chi_tiet_flash_sale] ([ma_flash_sale_id], [ma_bien_the_id])
GO

CREATE UNIQUE INDEX [danh_gia_san_pham_index_3] ON [danh_gia_san_pham] ([ma_chi_tiet_don_hang_id])
GO
