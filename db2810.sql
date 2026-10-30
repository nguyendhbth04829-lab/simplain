USE [master]
GO

/****** Object:  Database [Simpl4] ******/
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'Simpl4')
BEGIN
    CREATE DATABASE [Simpl4]
END
GO

USE [Simpl4]
GO

-- Chot an toan: Ngan chan tuyet doi viec tao bang vao master neu khong vao duoc Simpl4
IF DB_NAME() <> 'Simpl4'
BEGIN
    RAISERROR(N'Loi nghiem trong: Script khong the ket noi toi database Simpl4! Dung thuc thi ngay lap tuc de bao ve master.', 20, 1) WITH LOG;
    SET NOEXEC ON;
END
GO

/****** Object:  Table [dbo].[bai_viet_blog]    Script Date: 28/10/2026 6:37:09 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[bai_viet_blog](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_tac_gia_id] [bigint] NOT NULL,
	[chuyen_muc] [nvarchar](100) NOT NULL,
	[tieu_de] [nvarchar](255) NOT NULL,
	[duong_dan_slug] [varchar](255) NOT NULL,
	[mo_ta_ngan] [nvarchar](500) NULL,
	[noi_dung_html] [ntext] NOT NULL,
	[anh_dai_dien_url] [varchar](500) NULL,
	[ngay_xuat_ban] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[banner_quang_cao]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[banner_quang_cao](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[tieu_de] [nvarchar](150) NOT NULL,
	[hinh_anh_url] [varchar](500) NOT NULL,
	[duong_dan_dich] [varchar](500) NULL,
	[vi_tri_hien_thi] [varchar](50) NOT NULL,
	[kich_hoat] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[bien_dong_diem]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[bien_dong_diem](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_khach_hang_id] [bigint] NOT NULL,
	[ma_don_hang_id] [bigint] NULL,
	[loai_bien_dong] [varchar](50) NULL,
	[so_diem] [decimal](18, 2) NOT NULL,
	[noi_dung] [nvarchar](500) NULL,
	[ngay_tao] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[bien_the_san_pham]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[bien_the_san_pham](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_san_pham_id] [bigint] NOT NULL,
	[ma_mau_sac_id] [bigint] NOT NULL,
	[ma_kich_thuoc_id] [bigint] NOT NULL,
	[ma_sku] [varchar](100) NOT NULL,
	[ma_vach_barcode] [varchar](100) NULL,
	[so_luong] [int] NOT NULL,
	[gia_ban] [decimal](18, 2) NOT NULL,
	[gia_goc] [decimal](18, 2) NOT NULL,
	[kich_hoat] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[bo_suu_tap]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[bo_suu_tap](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ten_bo_suu_tap] [nvarchar](150) NOT NULL,
	[duong_dan_slug] [varchar](150) NOT NULL,
	[hinh_banner_url] [varchar](500) NULL,
	[kich_hoat] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[chat_lieu]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[chat_lieu](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ten_chat_lieu] [nvarchar](100) NOT NULL,
	[mo_ta] [nvarchar](255) NULL,
	[kich_hoat] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[chi_tiet_don_hang]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[chi_tiet_don_hang](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_don_hang_id] [bigint] NOT NULL,
	[ma_bien_the_id] [bigint] NOT NULL,
	[ma_combo_id] [bigint] NULL,
	[nhom_combo_token] [varchar](50) NULL,
	[ten_san_pham] [nvarchar](255) NOT NULL,
	[ma_sku] [varchar](100) NOT NULL,
	[don_gia] [decimal](18, 2) NOT NULL,
	[so_luong] [int] NOT NULL,
	[thanh_tien] [decimal](18, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[chi_tiet_flash_sale]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[chi_tiet_flash_sale](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_flash_sale_id] [bigint] NOT NULL,
	[ma_bien_the_id] [bigint] NOT NULL,
	[gia_flash_sale] [decimal](18, 2) NOT NULL,
	[so_luong_mo_ban] [int] NOT NULL,
	[so_luong_da_ban] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[chi_tiet_gio_hang]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[chi_tiet_gio_hang](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_gio_hang_id] [bigint] NOT NULL,
	[ma_bien_the_id] [bigint] NOT NULL,
	[ma_combo_id] [bigint] NULL,
	[nhom_combo_token] [varchar](50) NULL,
	[so_luong] [int] NOT NULL,
	[ngay_them] [datetime2](7) NULL,
	[don_gia] [numeric](38, 2) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[chi_tiet_hoa_don_cho]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[chi_tiet_hoa_don_cho](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_hoa_don_cho_id] [bigint] NOT NULL,
	[ma_bien_the_id] [bigint] NOT NULL,
	[so_luong] [int] NOT NULL,
	[don_gia] [decimal](18, 2) NOT NULL,
	[thanh_tien] [decimal](18, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[chi_tiet_san_pham_doi_tra]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[chi_tiet_san_pham_doi_tra](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_yeu_cau_doi_tra_id] [bigint] NOT NULL,
	[ma_chi_tiet_don_hang_id] [bigint] NOT NULL,
	[so_luong_tra] [int] NOT NULL,
	[ma_bien_the_moi_id] [bigint] NULL,
	[tinh_trang_san_pham] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[danh_gia_san_pham]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[danh_gia_san_pham](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_san_pham_id] [bigint] NOT NULL,
	[ma_khach_hang_id] [bigint] NOT NULL,
	[ma_chi_tiet_don_hang_id] [bigint] NULL,
	[so_sao] [int] NOT NULL,
	[noi_dung] [ntext] NULL,
	[danh_sach_anh_json] [ntext] NULL,
	[ngay_tao] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[danh_muc]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[danh_muc](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_danh_muc_cha] [bigint] NULL,
	[ten_danh_muc] [nvarchar](150) NOT NULL,
	[duong_dan_slug] [varchar](150) NOT NULL,
	[thu_tu_hien_thi] [int] NULL,
	[kich_hoat] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[dia_chi]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[dia_chi](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_khach_hang_id] [bigint] NOT NULL,
	[ten_nguoi_nhan] [nvarchar](150) NOT NULL,
	[so_dien_thoai] [varchar](20) NOT NULL,
	[tinh_thanh] [nvarchar](100) NOT NULL,
	[quan_huyen] [nvarchar](100) NOT NULL,
	[phuong_xa] [nvarchar](100) NOT NULL,
	[dia_chi_chi_tiet] [nvarchar](255) NOT NULL,
	[la_mac_dinh] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[don_hang]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[don_hang](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_don_hang] [varchar](50) NOT NULL,
	[ma_khach_hang_id] [bigint] NULL,
	[ma_trang_thai_id] [bigint] NOT NULL,
	[ma_giam_gia_id] [bigint] NULL,
	[ma_van_don] [varchar](50) NULL,
	[tong_tien_hang] [decimal](18, 2) NOT NULL,
	[tien_giam_gia] [decimal](18, 2) NULL,
	[tien_coolcash_dung] [decimal](18, 2) NULL,
	[phi_van_chuyen] [decimal](18, 2) NULL,
	[tong_thanh_toan] [decimal](18, 2) NOT NULL,
	[ten_nguoi_nhan] [nvarchar](150) NOT NULL,
	[so_dien_thoai_nhan] [varchar](20) NOT NULL,
	[dia_chi_giao_hang] [nvarchar](500) NOT NULL,
	[ghi_chu] [nvarchar](500) NULL,
	[ngay_tao] [datetime2](7) NULL,
	[ma_phuong_thuc_id] [bigint] NULL,
	[loai_don_hang] [varchar](20) NOT NULL,
	[tien_khach_dua] [decimal](18, 2) NULL,
	[tien_thua_tra_khach] [decimal](18, 2) NULL,
	[ma_nhan_vien_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[flash_sale]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[flash_sale](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[tieu_de] [nvarchar](150) NOT NULL,
	[thoi_gian_bat_dau] [datetime2](7) NOT NULL,
	[thoi_gian_ket_thuc] [datetime2](7) NOT NULL,
	[kich_hoat] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[form_dang]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[form_dang](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ten_form_dang] [nvarchar](100) NOT NULL,
	[mo_ta] [nvarchar](255) NULL,
	[kich_hoat] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[giao_dich_thanh_toan]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[giao_dich_thanh_toan](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_don_hang_id] [bigint] NOT NULL,
	[ma_phuong_thuc_id] [bigint] NOT NULL,
	[ma_giao_dich_noi_bo] [varchar](100) NOT NULL,
	[ma_giao_dich_cong] [varchar](100) NULL,
	[so_tien] [decimal](18, 2) NOT NULL,
	[trang_thai] [varchar](50) NOT NULL,
	[du_lieu_phan_hoi] [nvarchar](max) NULL,
	[ngay_tao] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[gio_hang]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[gio_hang](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_khach_hang_id] [bigint] NULL,
	[ngay_cap_nhat] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[goi_combo]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[goi_combo](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_combo] [varchar](50) NOT NULL,
	[ten_combo] [nvarchar](255) NOT NULL,
	[duong_dan_slug] [varchar](255) NOT NULL,
	[gia_combo] [decimal](18, 2) NOT NULL,
	[so_mon_quy_dinh] [int] NOT NULL,
	[mo_ta] [ntext] NULL,
	[kich_hoat] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[hang_thanh_vien]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[hang_thanh_vien](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ten_hang] [nvarchar](100) NULL,
	[chi_tieu_nam_toi_thieu] [decimal](18, 2) NOT NULL,
	[phan_tram_hoan_tien] [decimal](5, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[hinh_anh]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[hinh_anh](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_san_pham_id] [bigint] NOT NULL,
	[ma_bien_the_id] [bigint] NULL,
	[duong_dan_anh] [varchar](500) NOT NULL,
	[la_anh_dai_dien] [bit] NULL,
	[thu_tu] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ho_so_hoi_vien]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ho_so_hoi_vien](
	[ma_khach_hang_id] [bigint] NOT NULL,
	[ma_hang_hien_tai_id] [bigint] NOT NULL,
	[so_du_coolcash] [decimal](18, 2) NOT NULL,
	[tong_chi_tieu_nam] [decimal](18, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_khach_hang_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[hoa_don_cho]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[hoa_don_cho](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_hoa_don] [varchar](50) NOT NULL,
	[ma_nhan_vien_id] [bigint] NULL,
	[ma_khach_hang_id] [bigint] NULL,
	[ten_khach_hang] [nvarchar](150) NULL,
	[so_dien_thoai_khach] [varchar](20) NULL,
	[ghi_chu] [nvarchar](500) NULL,
	[trang_thai] [varchar](30) NOT NULL,
	[ngay_tao] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[khach_hang]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[khach_hang](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_khach_hang] [varchar](50) NOT NULL,
	[ho_ten] [nvarchar](150) NOT NULL,
	[so_dien_thoai] [varchar](20) NULL,
	[email] [varchar](150) NULL,
	[mat_khau_hash] [varchar](255) NULL,
	[anh_dai_dien] [varchar](500) NULL,
	[gioi_tinh] [nvarchar](10) NULL,
	[ngay_sinh] [date] NULL,
	[trang_thai] [int] NOT NULL,
	[ngay_tao] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[kich_thuoc]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[kich_thuoc](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_kich_thuoc] [varchar](50) NOT NULL,
	[ten_kich_thuoc] [nvarchar](50) NOT NULL,
	[kich_hoat] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[lich_su_trang_thai_don_hang]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[lich_su_trang_thai_don_hang](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_don_hang_id] [bigint] NOT NULL,
	[ma_trang_thai_moi_id] [bigint] NOT NULL,
	[nguoi_thuc_hien_id] [bigint] NULL,
	[ghi_chu] [nvarchar](500) NULL,
	[ngay_chuyen] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ma_giam_gia]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ma_giam_gia](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_code] [varchar](50) NOT NULL,
	[ten_voucher] [nvarchar](150) NOT NULL,
	[loai_giam_gia] [varchar](20) NOT NULL,
	[gia_tri_giam] [decimal](18, 2) NOT NULL,
	[giam_toi_da] [decimal](18, 2) NULL,
	[gia_tri_don_toi_thieu] [decimal](18, 2) NOT NULL,
	[tong_so_luong] [int] NULL,
	[so_luong_da_dung] [int] NOT NULL,
	[gioi_han_moi_user] [int] NOT NULL,
	[ngay_bat_dau] [datetime2](7) NOT NULL,
	[ngay_ket_thuc] [datetime2](7) NOT NULL,
	[kich_hoat] [bit] NULL,
	[ngan_sach] [decimal](18, 2) NULL,
	[ngan_sach_da_dung] [decimal](18, 2) NOT NULL,
	[mo_ta] [nvarchar](500) NULL,
	[ngay_tao] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[mau_sac]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[mau_sac](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_mau] [varchar](50) NOT NULL,
	[ten_mau] [nvarchar](100) NOT NULL,
	[kich_hoat] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[nhan_vien]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[nhan_vien](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_nhan_vien] [varchar](50) NOT NULL,
	[ho_ten] [nvarchar](150) NOT NULL,
	[ten_dang_nhap] [varchar](100) NOT NULL,
	[mat_khau_hash] [varchar](255) NOT NULL,
	[so_dien_thoai] [varchar](20) NULL,
	[email] [varchar](150) NULL,
	[ma_vai_tro_id] [bigint] NOT NULL,
	[trang_thai] [int] NOT NULL,
	[ngay_tao] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[phuong_thuc_thanh_toan]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[phuong_thuc_thanh_toan](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_phuong_thuc] [varchar](50) NOT NULL,
	[ten_phuong_thuc] [nvarchar](100) NOT NULL,
	[la_truc_tuyen] [bit] NULL,
	[kich_hoat] [bit] NULL,
	[mo_ta] [nvarchar](255) NULL,
	[bieu_tuong] [varchar](255) NULL,
	[cau_hinh] [nvarchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[san_pham]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[san_pham](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_danh_muc_id] [bigint] NOT NULL,
	[ma_bo_suu_tap_id] [bigint] NULL,
	[ma_san_pham] [varchar](50) NOT NULL,
	[ten_san_pham] [nvarchar](255) NOT NULL,
	[duong_dan_slug] [varchar](255) NOT NULL,
	[chat_lieu] [nvarchar](150) NULL,
	[form_dang] [nvarchar](100) NULL,
	[mo_ta_chi_tiet] [ntext] NULL,
	[huong_dan_chon_size] [ntext] NULL,
	[gia_co_ban] [decimal](18, 2) NOT NULL,
	[noi_bat] [bit] NULL,
	[kich_hoat] [bit] NULL,
	[ngay_tao] [datetime2](7) NULL,
	[ma_chat_lieu_id] [bigint] NULL,
	[ma_form_dang_id] [bigint] NULL,
	[video_url] [nvarchar](500) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[san_pham_trong_combo]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[san_pham_trong_combo](
	[ma_combo_id] [bigint] NOT NULL,
	[ma_san_pham_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ma_combo_id] ASC,
	[ma_san_pham_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[san_pham_yeu_thich]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[san_pham_yeu_thich](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_khach_hang_id] [bigint] NOT NULL,
	[ma_san_pham_id] [bigint] NOT NULL,
	[ngay_them] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[trang_thai_don_hang]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[trang_thai_don_hang](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_trang_thai] [varchar](50) NOT NULL,
	[ten_trang_thai] [nvarchar](100) NOT NULL,
	[ma_mau_hex] [varchar](10) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[vai_tro]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[vai_tro](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_vai_tro] [varchar](50) NOT NULL,
	[ten_vai_tro] [nvarchar](100) NOT NULL,
	[mo_ta] [nvarchar](255) NULL,
	[trang_thai] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[vai_tro_quyen_han]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[vai_tro_quyen_han](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_vai_tro_id] [bigint] NOT NULL,
	[ma_quyen] [varchar](50) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[video_san_pham]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[video_san_pham](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_san_pham_id] [bigint] NOT NULL,
	[tieu_de] [nvarchar](255) NOT NULL,
	[video_url] [nvarchar](500) NOT NULL,
	[mo_ta] [nvarchar](500) NULL,
	[kich_hoat] [bit] NOT NULL,
	[ngay_tao] [datetime2](7) NULL,
	[ngay_cap_nhat] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[voucher_so_huu]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[voucher_so_huu](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_khach_hang_id] [bigint] NOT NULL,
	[ma_giam_gia_id] [bigint] NOT NULL,
	[da_su_dung] [bit] NULL,
	[ngay_nhan] [datetime2](7) NULL,
	[ngay_su_dung] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[yeu_cau_doi_tra]    Script Date: 28/10/2026 6:37:10 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[yeu_cau_doi_tra](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ma_yeu_cau_rma] [varchar](50) NOT NULL,
	[ma_don_hang_id] [bigint] NOT NULL,
	[ma_khach_hang_id] [bigint] NOT NULL,
	[loai_doi_tra] [varchar](50) NOT NULL,
	[ly_do] [nvarchar](255) NOT NULL,
	[so_tien_hoan] [decimal](18, 2) NULL,
	[thong_tin_ngan_hang] [nvarchar](255) NULL,
	[trang_thai] [varchar](50) NOT NULL,
	[nguoi_duyet_id] [bigint] NULL,
	[ngay_tao] [datetime2](7) NULL,
	[hinh_anh_minh_chung] [nvarchar](2000) NULL,
	[ghi_chu_admin] [nvarchar](500) NULL,
	[ten_nguoi_nhan] [nvarchar](150) NULL,
	[so_dien_thoai_nhan] [varchar](20) NULL,
	[dia_chi_nhan_hang] [nvarchar](500) NULL,
	[ghi_chu_giao_hang] [nvarchar](255) NULL,
	[hinh_anh_bill] [nvarchar](1000) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[bai_viet_blog] ON 
GO
INSERT [dbo].[bai_viet_blog] ([id], [ma_tac_gia_id], [chuyen_muc], [tieu_de], [duong_dan_slug], [mo_ta_ngan], [noi_dung_html], [anh_dai_dien_url], [ngay_xuat_ban]) VALUES (19, 1, N'Thời trang nam', N'Siêu Ưu Đãi Giảm Giá 50% Toàn Bộ Bộ Sưu Tập Giày Sneaker', N'sieu-uu-dai-giam-gia-50-toan-bo-bo-suu-tap-giay-sneaker', N'gggggggggggggggggggggggggg', N'hhhhhhhhhhhhhhhhhhhhhhhhhhhhh', N'/uploads/blog/9ba9fa42-9249-4ec9-8cfc-94783bcfbc24.webp', CAST(N'2026-10-27T22:41:00.0000000' AS DateTime2))
GO
SET IDENTITY_INSERT [dbo].[bai_viet_blog] OFF
GO
SET IDENTITY_INSERT [dbo].[banner_quang_cao] ON 
GO
INSERT [dbo].[banner_quang_cao] ([id], [tieu_de], [hinh_anh_url], [duong_dan_dich], [vi_tri_hien_thi], [kich_hoat]) VALUES (28, N'Siêu Ưu Đãi Giảm Giá 50% Toàn Bộ Bộ Sưu Tập Giày Sneaker', N'/uploads/banner/6a3f0bbe-1f2f-475f-bed3-ae8a28fdb728.png', N'', N'FOOTER', 1)
GO
SET IDENTITY_INSERT [dbo].[banner_quang_cao] OFF
GO
SET IDENTITY_INSERT [dbo].[bien_dong_diem] ON 
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (53, 652, 341, N'CONG_HOAN_THANH_DON', CAST(7000.00 AS Decimal(18, 2)), N'Tích lũy 1.00% Coolcash từ đơn hàng #DH-POS-1793005887469 giao thành công', CAST(N'2026-10-26T16:11:27.5342227' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (54, 652, 342, N'CONG_HOAN_THANH_DON', CAST(7000.00 AS Decimal(18, 2)), N'Tích lũy 1.00% Coolcash từ đơn hàng #DH-POS-1793006078932 giao thành công', CAST(N'2026-10-26T16:14:39.0025443' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (55, 654, 343, N'CONG_HOAN_THANH_DON', CAST(2500.00 AS Decimal(18, 2)), N'Tích lũy 1.00% Coolcash từ đơn hàng #DH-POS-1793006079964 giao thành công', CAST(N'2026-10-26T16:14:39.9948353' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (56, 654, 344, N'TRU_THANH_TOAN_DON', CAST(-20000.00 AS Decimal(18, 2)), N'Sử dụng 20000 Coolcash thanh toán đơn hàng #DH-POS-1793006080175', CAST(N'2026-10-26T16:14:40.1969822' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (57, 654, 344, N'CONG_HOAN_THANH_DON', CAST(2300.00 AS Decimal(18, 2)), N'Tích lũy 1.00% Coolcash từ đơn hàng #DH-POS-1793006080175 giao thành công', CAST(N'2026-10-26T16:14:40.2122882' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (58, 652, 345, N'CONG_HOAN_THANH_DON', CAST(21000.00 AS Decimal(18, 2)), N'Tích lũy 3.00% Coolcash từ đơn hàng #DH-POS-1793007402358 giao thành công', CAST(N'2026-10-26T16:36:42.4351691' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (59, 659, 346, N'CONG_HOAN_THANH_DON', CAST(2500.00 AS Decimal(18, 2)), N'Tích lũy 1.00% Coolcash từ đơn hàng #DH-POS-1793007403387 giao thành công', CAST(N'2026-10-26T16:36:43.4201235' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (60, 659, 347, N'TRU_THANH_TOAN_DON', CAST(-20000.00 AS Decimal(18, 2)), N'Sử dụng 20000 Coolcash thanh toán đơn hàng #DH-POS-1793007403606', CAST(N'2026-10-26T16:36:43.6249766' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (61, 659, 347, N'CONG_HOAN_THANH_DON', CAST(2300.00 AS Decimal(18, 2)), N'Tích lũy 1.00% Coolcash từ đơn hàng #DH-POS-1793007403606 giao thành công', CAST(N'2026-10-26T16:36:43.6358559' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (62, 652, 348, N'CONG_HOAN_THANH_DON', CAST(21000.00 AS Decimal(18, 2)), N'Tích lũy 3.00% Coolcash từ đơn hàng #DH-POS-1793007979455 giao thành công', CAST(N'2026-10-26T16:46:19.5310141' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (63, 661, 349, N'CONG_HOAN_THANH_DON', CAST(2500.00 AS Decimal(18, 2)), N'Tích lũy 1.00% Coolcash từ đơn hàng #DH-POS-1793007980426 giao thành công', CAST(N'2026-10-26T16:46:20.4579626' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (64, 661, 350, N'TRU_THANH_TOAN_DON', CAST(-20000.00 AS Decimal(18, 2)), N'Sử dụng 20000 Coolcash thanh toán đơn hàng #DH-POS-1793007980628', CAST(N'2026-10-26T16:46:20.6453496' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (65, 661, 350, N'CONG_HOAN_THANH_DON', CAST(2300.00 AS Decimal(18, 2)), N'Tích lũy 1.00% Coolcash từ đơn hàng #DH-POS-1793007980628 giao thành công', CAST(N'2026-10-26T16:46:20.6561182' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (66, 685, 353, N'HOAN_DIEM_HUY_DON', CAST(20000.00 AS Decimal(18, 2)), N'Hoàn lại 20000 Coolcash từ đơn hàng bị hủy #DH1793010354285182', CAST(N'2026-10-26T17:25:54.3593150' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (67, 703, 363, N'HOAN_DIEM_HUY_DON', CAST(20000.00 AS Decimal(18, 2)), N'Hoàn lại 20000 Coolcash từ đơn hàng bị hủy #DH1793010551234334', CAST(N'2026-10-26T17:29:11.3131860' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (68, 14, 371, N'CONG_HOAN_THANH_DON', CAST(1620.00 AS Decimal(18, 2)), N'Tích lũy 1.00% Coolcash từ đơn hàng #DH1793084473518810 giao thành công', CAST(N'2026-10-27T14:02:21.4060864' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (69, 781, 434, N'HOAN_DIEM_HUY_DON', CAST(20000.00 AS Decimal(18, 2)), N'Hoàn lại 20000 Coolcash từ đơn hàng bị hủy #DH1793085794425882', CAST(N'2026-10-27T14:23:14.4937734' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (70, 14, 632, N'CONG_HOAN_THANH_DON', CAST(2800.00 AS Decimal(18, 2)), N'Tích lũy 1.00% Coolcash từ đơn hàng #DH1793096505543169 giao thành công', CAST(N'2026-10-27T17:22:04.9981774' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (71, 14, 727, N'CONG_HOAN_THANH_DON', CAST(2220.00 AS Decimal(18, 2)), N'Tích lũy 1.00% Coolcash từ đơn hàng #DH1793097816831292 giao thành công', CAST(N'2026-10-27T17:44:09.5647127' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (72, 14, 975, N'TRU_THANH_TOAN_DON', CAST(-1.00 AS Decimal(18, 2)), N'Sử dụng 1 Coolcash thanh toán đơn hàng #DH1793103396493333', CAST(N'2026-10-27T19:16:36.5547661' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (79, 1455, 1012, N'HOAN_DIEM_HUY_DON', CAST(20000.00 AS Decimal(18, 2)), N'Hoàn lại 20000 Coolcash từ đơn hàng bị hủy #DH1793104467400470', CAST(N'2026-10-27T19:34:27.4551585' AS DateTime2))
GO
INSERT [dbo].[bien_dong_diem] ([id], [ma_khach_hang_id], [ma_don_hang_id], [loai_bien_dong], [so_diem], [noi_dung], [ngay_tao]) VALUES (86, 14, 1053, N'CONG_HOAN_THANH_DON', CAST(2220.00 AS Decimal(18, 2)), N'Tích lũy 1.00% Coolcash từ đơn hàng #DH1793156817965824 giao thành công', CAST(N'2026-10-28T10:11:38.0181478' AS DateTime2))
GO
SET IDENTITY_INSERT [dbo].[bien_dong_diem] OFF
GO
SET IDENTITY_INSERT [dbo].[bien_the_san_pham] ON 
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (1, 1, 1, 1, N'SP-POLO-EXCOOL-01-DEN-S', N'BC-SP-POLO-EXCOOL-01-M1S1', 37, CAST(250000.00 AS Decimal(18, 2)), CAST(299000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (5, 2, 1, 1, N'SP-TSHIRT-COMPACT-02-DEN-TUYEN-S', N'BC-SP-TSHIRT-COMPACT-02-DEN-TUYEN-S', 17, CAST(192000.00 AS Decimal(18, 2)), CAST(1000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (9, 3, 1, 1, N'SP-POLO-CAFE-03-DEN-S', N'BC-SP-POLO-CAFE-03-DS', 29, CAST(329000.00 AS Decimal(18, 2)), CAST(210000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (23, 1, 6, 6, N'SP-POLO-EXCOOL-01-DODO-3XL', N'BC-SP-POLO-EXCOOL-01-M2S2', 29, CAST(299000.00 AS Decimal(18, 2)), CAST(29000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (629, 1, 1, 2, N'SP-POLO-EXCOOL-01-DEN-M', N'BC-SP-POLO-EXCOOL-01-DENTUYEN-M', 103, CAST(260000.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (630, 1, 1, 3, N'SP-POLO-EXCOOL-01-DEN-L', N'BC-SP-POLO-EXCOOL-01-DENTUYEN-L', 80, CAST(299000.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (645, 1, 5, 4, N'SP-POLO-EXCOOL-01-XANHREU-XL', N'BC-SP-POLO-EXCOOL-01-XANHREU-XL', 49, CAST(299000.00 AS Decimal(18, 2)), CAST(299000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (651, 1, 1, 4, N'SP-POLO-EXCOOL-01-DEN-XL', N'BC-SP-POLO-EXCOOL-01-DENTUYEN-XL', 1, CAST(299000.00 AS Decimal(18, 2)), CAST(29000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (652, 1, 5, 5, N'SP-POLO-EXCOOL-01-XANHREU-2XL', N'BC-SP-POLO-EXCOOL-01-XANHREU-2XL', 1, CAST(299000.00 AS Decimal(18, 2)), CAST(29000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (653, 1, 3, 5, N'SP-POLO-EXCOOL-01-XANHNAVY-2XL', N'BC-SP-POLO-EXCOOL-01-XANHNAVY-2XL', 1, CAST(299000.00 AS Decimal(18, 2)), CAST(29000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (654, 1, 2, 5, N'SP-POLO-EXCOOL-01-TRANGTINHKHOI-2XL', N'BC-SP-POLO-EXCOOL-01-TRANGTINHKHOI-2XL', 1, CAST(299000.00 AS Decimal(18, 2)), CAST(29000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (655, 1, 2, 6, N'SP-POLO-EXCOOL-01-TRANGTINHKHOI-3XL', N'BC-SP-POLO-EXCOOL-01-TRANGTINHKHOI-3XL', 1, CAST(299000.00 AS Decimal(18, 2)), CAST(29000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (656, 1, 2, 4, N'SP-POLO-EXCOOL-01-TRANGTINHKHOI-XL', N'BC-SP-POLO-EXCOOL-01-TRANGTINHKHOI-XL', 1, CAST(299000.00 AS Decimal(18, 2)), CAST(29000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (662, 2, 1, 4, N'SP-TSHIRT-COMPACT-02-DENTUYEN-XL', N'BC-SP-TSHIRT-COMPACT-02-DENTUYEN-XL', 50, CAST(299000.00 AS Decimal(18, 2)), CAST(299000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (663, 2, 2, 4, N'SP-TSHIRT-COMPACT-02-TRANGTINHKHOI-XL', N'BC-SP-TSHIRT-COMPACT-02-TRANGTINHKHOI-XL', 50, CAST(299000.00 AS Decimal(18, 2)), CAST(299000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (664, 2, 5, 6, N'SP-TSHIRT-COMPACT-02-XANHREU-3XL', N'BC-SP-TSHIRT-COMPACT-02-XANHREU-3XL', 50, CAST(299000.00 AS Decimal(18, 2)), CAST(299000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (670, 1, 2, 1, N'SP-POLO-EXCOOL-01-TRANGTINHKHOI-S', N'BC-SP-POLO-EXCOOL-01-TRANGTINHKHOI-S', 20, CAST(299000.00 AS Decimal(18, 2)), CAST(299000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (671, 2, 2, 1, N'SP-TSHIRT-COMPACT-02-TRANGTINHKHOI-S', N'BC-SP-TSHIRT-COMPACT-02-TRANGTINHKHOI-S', 25, CAST(199000.00 AS Decimal(18, 2)), CAST(199000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (672, 1, 2, 3, N'SP-POLO-EXCOOL-01-TRANGTINHKHOI-L', N'BC-SP-POLO-EXCOOL-01-TRANGTINHKHOI-L', 48, CAST(299000.00 AS Decimal(18, 2)), CAST(1.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (673, 2, 2, 5, N'SP-TSHIRT-COMPACT-02-TRANGTINHKHOI-2XL', N'BC-SP-TSHIRT-COMPACT-02-TRANGTINHKHOI-2XL', 50, CAST(199000.00 AS Decimal(18, 2)), CAST(1000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (674, 2, 2, 6, N'SP-TSHIRT-COMPACT-02-TRANGTINHKHOI-3XL', N'BC-SP-TSHIRT-COMPACT-02-TRANGTINHKHOI-3XL', 50, CAST(199000.00 AS Decimal(18, 2)), CAST(1.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (675, 2, 4, 5, N'SP-TSHIRT-COMPACT-02-XAMHEATHER-2XL', N'BC-SP-TSHIRT-COMPACT-02-XAMHEATHER-2XL', 49, CAST(199000.00 AS Decimal(18, 2)), CAST(1.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (676, 2, 4, 6, N'SP-TSHIRT-COMPACT-02-XAMHEATHER-3XL', N'BC-SP-TSHIRT-COMPACT-02-XAMHEATHER-3XL', 50, CAST(199000.00 AS Decimal(18, 2)), CAST(1.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (677, 3, 5, 1, N'SP-POLO-CAFE-03-XANHREU-S', N'BC-SP-POLO-CAFE-03-XANHREU-S', 50, CAST(329000.00 AS Decimal(18, 2)), CAST(1.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (678, 3, 5, 3, N'SP-POLO-CAFE-03-XANHREU-L', N'BC-SP-POLO-CAFE-03-XANHREU-L', 49, CAST(329000.00 AS Decimal(18, 2)), CAST(329000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (679, 3, 6, 1, N'SP-POLO-CAFE-03-DODO-S', N'BC-SP-POLO-CAFE-03-DODO-S', 49, CAST(329000.00 AS Decimal(18, 2)), CAST(329000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[bien_the_san_pham] ([id], [ma_san_pham_id], [ma_mau_sac_id], [ma_kich_thuoc_id], [ma_sku], [ma_vach_barcode], [so_luong], [gia_ban], [gia_goc], [kich_hoat]) VALUES (680, 3, 6, 3, N'SP-POLO-CAFE-03-DODO-L', N'BC-SP-POLO-CAFE-03-DODO-L', 49, CAST(329000.00 AS Decimal(18, 2)), CAST(329000.00 AS Decimal(18, 2)), 1)
GO
SET IDENTITY_INSERT [dbo].[bien_the_san_pham] OFF
GO
SET IDENTITY_INSERT [dbo].[bo_suu_tap] ON 
GO
INSERT [dbo].[bo_suu_tap] ([id], [ten_bo_suu_tap], [duong_dan_slug], [hinh_banner_url], [kich_hoat]) VALUES (1, N'hello', N'de-men', N'123', 1)
GO
INSERT [dbo].[bo_suu_tap] ([id], [ten_bo_suu_tap], [duong_dan_slug], [hinh_banner_url], [kich_hoat]) VALUES (2, N'hello2', N'de-men2', N'/uploads/1791627601949_sg-11134301-7reo3-m2xh8abdo3tz11.webp', 1)
GO
SET IDENTITY_INSERT [dbo].[bo_suu_tap] OFF
GO
SET IDENTITY_INSERT [dbo].[chat_lieu] ON 
GO
INSERT [dbo].[chat_lieu] ([id], [ten_chat_lieu], [mo_ta], [kich_hoat]) VALUES (1, N'100% Cotton Compact 220gsm', N'Thấm hút mồ hôi, co giãn 2 chiều, chống xù lông', 1)
GO
INSERT [dbo].[chat_lieu] ([id], [ten_chat_lieu], [mo_ta], [kich_hoat]) VALUES (2, N'Vải Excool Công nghệ Làm Mát', N'Siêu nhẹ, thoáng khí, chống nhăn tự nhiên', 1)
GO
INSERT [dbo].[chat_lieu] ([id], [ten_chat_lieu], [mo_ta], [kich_hoat]) VALUES (3, N'95% Cotton Organic - 5% Spandex', N'Mềm mại, co giãn đàn hồi 4 chiều vượt trội', 1)
GO
INSERT [dbo].[chat_lieu] ([id], [ten_chat_lieu], [mo_ta], [kich_hoat]) VALUES (4, N'Vải Modal Sợi Sồi Sinh Học', N'Mát lạnh tự nhiên, mềm mượt và kháng khuẩn', 1)
GO
INSERT [dbo].[chat_lieu] ([id], [ten_chat_lieu], [mo_ta], [kich_hoat]) VALUES (5, N'Vải Pique Mắt Chim Cao Cấp', N'Đứng form dáng polo, giữ màu bền đẹp', 1)
GO
INSERT [dbo].[chat_lieu] ([id], [ten_chat_lieu], [mo_ta], [kich_hoat]) VALUES (6, N'Vải Nỉ Da Cá Cotton French Terry', N'Giữ nhiệt ấm áp, êm ái thoáng mát', 1)
GO
INSERT [dbo].[chat_lieu] ([id], [ten_chat_lieu], [mo_ta], [kich_hoat]) VALUES (7, N'Vải Linen Tự Nhiên (Vải Đũi)', N'Thoáng mát, mộc mạc, thấm hút nhanh', 1)
GO
INSERT [dbo].[chat_lieu] ([id], [ten_chat_lieu], [mo_ta], [kich_hoat]) VALUES (8, N'Vải Café Khử Mùi Phối Spandex', N'Khử mùi tự nhiên từ bã café, co giãn nhẹ', 1)
GO
INSERT [dbo].[chat_lieu] ([id], [ten_chat_lieu], [mo_ta], [kich_hoat]) VALUES (9, N'100% Cotton Compact 2 chiều', N'Thấm hút mồ hôi, co giãn 2 chiều, chống xù lông', 1)
GO
SET IDENTITY_INSERT [dbo].[chat_lieu] OFF
GO
SET IDENTITY_INSERT [dbo].[chi_tiet_don_hang] ON 
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (25, 1, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(299000.00 AS Decimal(18, 2)), 1, CAST(299000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (62, 262, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(299000.00 AS Decimal(18, 2)), 2, CAST(598000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (63, 263, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(299000.00 AS Decimal(18, 2)), 2, CAST(598000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (70, 281, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(299000.00 AS Decimal(18, 2)), 2, CAST(598000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (71, 282, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(299000.00 AS Decimal(18, 2)), 2, CAST(598000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (72, 283, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(299000.00 AS Decimal(18, 2)), 2, CAST(598000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (73, 284, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(299000.00 AS Decimal(18, 2)), 1, CAST(299000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (74, 295, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(299000.00 AS Decimal(18, 2)), 2, CAST(598000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (75, 296, 1, NULL, NULL, N'Áo Thun Nam US21', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(150000.00 AS Decimal(18, 2)), 4, CAST(600000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (76, 297, 1, NULL, NULL, N'Sản phẩm tra cứu', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 1, CAST(100000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (77, 298, 1, NULL, NULL, N'Áo Thun Nam US21', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(150000.00 AS Decimal(18, 2)), 4, CAST(600000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (78, 299, 1, NULL, NULL, N'Sản phẩm tra cứu', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 1, CAST(100000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (79, 300, 1, NULL, NULL, N'Áo Thun Nam US21', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(150000.00 AS Decimal(18, 2)), 4, CAST(600000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (80, 301, 1, NULL, NULL, N'Sản phẩm tra cứu', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 1, CAST(100000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (81, 302, 1, NULL, NULL, N'Áo Thun Nam US21', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(150000.00 AS Decimal(18, 2)), 4, CAST(600000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (82, 303, 1, NULL, NULL, N'Sản phẩm tra cứu', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 1, CAST(100000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (83, 304, 1, NULL, NULL, N'Áo Thun Nam US21', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(150000.00 AS Decimal(18, 2)), 4, CAST(600000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (84, 305, 1, NULL, NULL, N'Sản phẩm tra cứu', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 1, CAST(100000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (85, 306, 1, NULL, NULL, N'Áo Thun Nam US21', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(150000.00 AS Decimal(18, 2)), 4, CAST(600000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (86, 307, 1, NULL, NULL, N'Sản phẩm tra cứu', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 1, CAST(100000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (87, 313, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(299000.00 AS Decimal(18, 2)), 2, CAST(598000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (88, 314, 1, NULL, NULL, N'Áo Polo Excool', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(200000.00 AS Decimal(18, 2)), 1, CAST(200000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (89, 315, 1, NULL, NULL, N'Áo Thun Nam US21', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(150000.00 AS Decimal(18, 2)), 4, CAST(600000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (90, 316, 1, NULL, NULL, N'Sản phẩm tra cứu', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 1, CAST(100000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (91, 322, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(299000.00 AS Decimal(18, 2)), 2, CAST(598000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (92, 323, 645, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Xanh Rêu / Size XL (76-83kg))', N'SP-POLO-EXCOOL-01-XANHREU-XL', CAST(299000.00 AS Decimal(18, 2)), 1, CAST(299000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (93, 324, 1, NULL, NULL, N'Áo Thun Nam US21', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(150000.00 AS Decimal(18, 2)), 4, CAST(600000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (94, 325, 1, NULL, NULL, N'Sản phẩm tra cứu', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 1, CAST(100000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (95, 326, 680, NULL, NULL, N'Áo Polo Nam Sợi Café Khử Mùi Tối Ưu (Đỏ Đô / Size L (68-75kg))', N'SP-POLO-CAFE-03-DODO-L', CAST(329000.00 AS Decimal(18, 2)), 1, CAST(329000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (96, 327, 1, NULL, NULL, N'Sản phẩm US22 Vận Đơn', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(150000.00 AS Decimal(18, 2)), 2, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (97, 328, 1, NULL, NULL, N'Áo Thun Nam US21', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(150000.00 AS Decimal(18, 2)), 4, CAST(600000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (98, 329, 1, NULL, NULL, N'Sản phẩm hoàn kho', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 4, CAST(400000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (99, 330, 1, NULL, NULL, N'Sản phẩm tra cứu', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 1, CAST(100000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (100, 331, 1, NULL, NULL, N'Sản phẩm US22 Vận Đơn', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(150000.00 AS Decimal(18, 2)), 2, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (101, 332, 1, NULL, NULL, N'Áo Thun Nam US21', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(150000.00 AS Decimal(18, 2)), 4, CAST(600000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (102, 333, 1, NULL, NULL, N'Sản phẩm test quy trình', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 1, CAST(100000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (103, 334, 1, NULL, NULL, N'Sản phẩm hoàn kho', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 4, CAST(400000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (104, 335, 1, NULL, NULL, N'Sản phẩm tra cứu', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(100000.00 AS Decimal(18, 2)), 1, CAST(100000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (105, 336, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 3, CAST(750000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (106, 337, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 3, CAST(750000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (107, 338, 679, NULL, NULL, N'Áo Polo Nam Sợi Café Khử Mùi Tối Ưu', N'SP-POLO-CAFE-03-DODO-S', CAST(329000.00 AS Decimal(18, 2)), 1, CAST(329000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (108, 339, 678, NULL, NULL, N'Áo Polo Nam Sợi Café Khử Mùi Tối Ưu', N'SP-POLO-CAFE-03-XANHREU-L', CAST(329000.00 AS Decimal(18, 2)), 1, CAST(329000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (109, 339, 675, NULL, NULL, N'Áo Thun Nam Cotton Compact 220gsm', N'SP-TSHIRT-COMPACT-02-XAMHEATHER-2XL', CAST(199000.00 AS Decimal(18, 2)), 1, CAST(199000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (110, 339, 672, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-TRANGTINHKHOI-L', CAST(299000.00 AS Decimal(18, 2)), 1, CAST(299000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (111, 340, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 1, CAST(250000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (112, 341, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 3, CAST(750000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (113, 342, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 3, CAST(750000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (114, 343, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 1, CAST(250000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (115, 344, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 1, CAST(250000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (116, 345, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 3, CAST(750000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (117, 346, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 1, CAST(250000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (118, 347, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 1, CAST(250000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (119, 348, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 3, CAST(750000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (120, 349, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 1, CAST(250000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (121, 350, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 1, CAST(250000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (122, 351, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (123, 352, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (124, 353, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (125, 354, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (126, 355, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (127, 356, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (128, 357, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (129, 358, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (130, 359, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (131, 360, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (132, 361, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (133, 362, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (134, 363, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (135, 364, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (136, 365, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (137, 366, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (138, 367, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (139, 368, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (140, 369, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (141, 370, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (142, 371, 5, NULL, NULL, N'Áo Thun Nam Cotton Compact 220gsm (Đen Tuyền / Size S (48-58kg))', N'SP-TSHIRT-COMPACT-02-DEN-TUYEN-S', CAST(192000.00 AS Decimal(18, 2)), 1, CAST(192000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (203, 432, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (204, 433, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (205, 434, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (206, 435, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (207, 436, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (208, 437, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (209, 438, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (210, 439, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (211, 440, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (212, 441, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (403, 632, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 1, CAST(250000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (498, 727, 5, NULL, NULL, N'Áo Thun Nam Cotton Compact 220gsm (Đen Tuyền / Size S (48-58kg))', N'SP-TSHIRT-COMPACT-02-DEN-TUYEN-S', CAST(192000.00 AS Decimal(18, 2)), 1, CAST(192000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (746, 975, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 1, CAST(250000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (747, 983, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 2, CAST(500000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (748, 984, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 2, CAST(500000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (749, 985, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 2, CAST(500000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (774, 1010, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (775, 1011, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (776, 1012, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (777, 1013, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (778, 1014, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (779, 1015, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (780, 1016, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (781, 1017, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (782, 1018, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (783, 1019, 1, NULL, NULL, N'Áo Thun Nam Cao Cấp US23', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(300000.00 AS Decimal(18, 2)), 1, CAST(300000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (803, 1046, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 2, CAST(500000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (804, 1047, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 2, CAST(500000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (805, 1048, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen Tuyền / Size S (48-58kg))', N'SP-POLO-EXCOOL-01-MAU1-SIZE1', CAST(250000.00 AS Decimal(18, 2)), 2, CAST(500000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (806, 1049, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen / S)', N'SP-POLO-EXCOOL-01-DEN-S', CAST(250000.00 AS Decimal(18, 2)), 2, CAST(500000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (808, 1051, 9, NULL, NULL, N'Áo Polo Nam Sợi Café Khử Mùi Tối Ưu (Đen / S)', N'SP-POLO-CAFE-03-DEN-S', CAST(329000.00 AS Decimal(18, 2)), 1, CAST(329000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (809, 1051, 5, NULL, NULL, N'Áo Thun Nam Cotton Compact 220gsm (Đen / S)', N'SP-TSHIRT-COMPACT-02-DEN-TUYEN-S', CAST(192000.00 AS Decimal(18, 2)), 1, CAST(192000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (810, 1052, 5, NULL, NULL, N'Áo Thun Nam Cotton Compact 220gsm (Đen / S)', N'SP-TSHIRT-COMPACT-02-DEN-TUYEN-S', CAST(192000.00 AS Decimal(18, 2)), 1, CAST(192000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (811, 1053, 5, NULL, NULL, N'Áo Thun Nam Cotton Compact 220gsm (Đen / S)', N'SP-TSHIRT-COMPACT-02-DEN-TUYEN-S', CAST(192000.00 AS Decimal(18, 2)), 1, CAST(192000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_don_hang] ([id], [ma_don_hang_id], [ma_bien_the_id], [ma_combo_id], [nhom_combo_token], [ten_san_pham], [ma_sku], [don_gia], [so_luong], [thanh_tien]) VALUES (812, 1054, 1, NULL, NULL, N'Áo Polo Nam Excool Công Nghệ Làm Mát (Đen / S)', N'SP-POLO-EXCOOL-01-DEN-S', CAST(250000.00 AS Decimal(18, 2)), 2, CAST(500000.00 AS Decimal(18, 2)))
GO
SET IDENTITY_INSERT [dbo].[chi_tiet_don_hang] OFF
GO
SET IDENTITY_INSERT [dbo].[chi_tiet_flash_sale] ON 
GO
INSERT [dbo].[chi_tiet_flash_sale] ([id], [ma_flash_sale_id], [ma_bien_the_id], [gia_flash_sale], [so_luong_mo_ban], [so_luong_da_ban]) VALUES (235, 155, 23, CAST(240000.00 AS Decimal(18, 2)), 10, 0)
GO
INSERT [dbo].[chi_tiet_flash_sale] ([id], [ma_flash_sale_id], [ma_bien_the_id], [gia_flash_sale], [so_luong_mo_ban], [so_luong_da_ban]) VALUES (236, 155, 629, CAST(240000.00 AS Decimal(18, 2)), 10, 0)
GO
INSERT [dbo].[chi_tiet_flash_sale] ([id], [ma_flash_sale_id], [ma_bien_the_id], [gia_flash_sale], [so_luong_mo_ban], [so_luong_da_ban]) VALUES (237, 155, 630, CAST(222000.00 AS Decimal(18, 2)), 10, 0)
GO
SET IDENTITY_INSERT [dbo].[chi_tiet_flash_sale] OFF
GO
SET IDENTITY_INSERT [dbo].[chi_tiet_hoa_don_cho] ON 
GO
INSERT [dbo].[chi_tiet_hoa_don_cho] ([id], [ma_hoa_don_cho_id], [ma_bien_the_id], [so_luong], [don_gia], [thanh_tien]) VALUES (12, 14, 1, 1, CAST(250000.00 AS Decimal(18, 2)), CAST(250000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[chi_tiet_hoa_don_cho] ([id], [ma_hoa_don_cho_id], [ma_bien_the_id], [so_luong], [don_gia], [thanh_tien]) VALUES (20, 9, 678, 1, CAST(329000.00 AS Decimal(18, 2)), CAST(329000.00 AS Decimal(18, 2)))
GO
SET IDENTITY_INSERT [dbo].[chi_tiet_hoa_don_cho] OFF
GO
SET IDENTITY_INSERT [dbo].[chi_tiet_san_pham_doi_tra] ON 
GO
INSERT [dbo].[chi_tiet_san_pham_doi_tra] ([id], [ma_yeu_cau_doi_tra_id], [ma_chi_tiet_don_hang_id], [so_luong_tra], [ma_bien_the_moi_id], [tinh_trang_san_pham]) VALUES (62, 62, 142, 1, NULL, N'gggggggggggg')
GO
INSERT [dbo].[chi_tiet_san_pham_doi_tra] ([id], [ma_yeu_cau_doi_tra_id], [ma_chi_tiet_don_hang_id], [so_luong_tra], [ma_bien_the_moi_id], [tinh_trang_san_pham]) VALUES (184, 184, 403, 1, 672, N'KKKKKKKKKK')
GO
INSERT [dbo].[chi_tiet_san_pham_doi_tra] ([id], [ma_yeu_cau_doi_tra_id], [ma_chi_tiet_don_hang_id], [so_luong_tra], [ma_bien_the_moi_id], [tinh_trang_san_pham]) VALUES (275, 275, 498, 1, NULL, N'kkkkkkkkkk')
GO
SET IDENTITY_INSERT [dbo].[chi_tiet_san_pham_doi_tra] OFF
GO
SET IDENTITY_INSERT [dbo].[danh_muc] ON 
GO
INSERT [dbo].[danh_muc] ([id], [ma_danh_muc_cha], [ten_danh_muc], [duong_dan_slug], [thu_tu_hien_thi], [kich_hoat]) VALUES (1, NULL, N'de', N'nguyen1', 0, 1)
GO
INSERT [dbo].[danh_muc] ([id], [ma_danh_muc_cha], [ten_danh_muc], [duong_dan_slug], [thu_tu_hien_thi], [kich_hoat]) VALUES (17, NULL, N'Áo Polo Nam', N'ao-polo-nam', 1, 1)
GO
INSERT [dbo].[danh_muc] ([id], [ma_danh_muc_cha], [ten_danh_muc], [duong_dan_slug], [thu_tu_hien_thi], [kich_hoat]) VALUES (18, NULL, N'Áo Thun Nam', N'ao-thun-nam', 2, 1)
GO
INSERT [dbo].[danh_muc] ([id], [ma_danh_muc_cha], [ten_danh_muc], [duong_dan_slug], [thu_tu_hien_thi], [kich_hoat]) VALUES (1443, 1, N'333', N'333', 0, 1)
GO
SET IDENTITY_INSERT [dbo].[danh_muc] OFF
GO
SET IDENTITY_INSERT [dbo].[dia_chi] ON 
GO
INSERT [dbo].[dia_chi] ([id], [ma_khach_hang_id], [ten_nguoi_nhan], [so_dien_thoai], [tinh_thanh], [quan_huyen], [phuong_xa], [dia_chi_chi_tiet], [la_mac_dinh]) VALUES (1, 1, N'Trần Mua Hàng', N'0901234567', N'Hà Nội', N'Cầu Giấy', N'Dịch Vọng', N'Số 12 ngõ 45 phố Duy Tân', 1)
GO
INSERT [dbo].[dia_chi] ([id], [ma_khach_hang_id], [ten_nguoi_nhan], [so_dien_thoai], [tinh_thanh], [quan_huyen], [phuong_xa], [dia_chi_chi_tiet], [la_mac_dinh]) VALUES (2, 1, N'Bố Trần Mua Hàng', N'0988888888', N'Hải Phòng', N'Lê Chân', N'An Dương', N'Số 88 đường Lạch Tray', 0)
GO
INSERT [dbo].[dia_chi] ([id], [ma_khach_hang_id], [ten_nguoi_nhan], [so_dien_thoai], [tinh_thanh], [quan_huyen], [phuong_xa], [dia_chi_chi_tiet], [la_mac_dinh]) VALUES (21, 14, N'Nguyeen Nguyene', N'0983823668', N'Đồng Nai', N'Biên Hòa', N'Tam Hiệp', N'123', 1)
GO
INSERT [dbo].[dia_chi] ([id], [ma_khach_hang_id], [ten_nguoi_nhan], [so_dien_thoai], [tinh_thanh], [quan_huyen], [phuong_xa], [dia_chi_chi_tiet], [la_mac_dinh]) VALUES (76, 14, N'Nguyeen Nguyene', N'0983823668', N'Cần Thơ', N'Ninh Kiều', N'An Bình', N'123', 0)
GO
SET IDENTITY_INSERT [dbo].[dia_chi] OFF
GO
SET IDENTITY_INSERT [dbo].[don_hang] ON 
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1, N'CM-1001', 1, 4, 1, N'GHTK881001', CAST(520000.00 AS Decimal(18, 2)), CAST(40000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(480000.00 AS Decimal(18, 2)), N'Trần Mua Hàng', N'0901234567', N'Số 12 ngõ 45 phố Duy Tân, Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-17T17:55:24.6700000' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (2, N'CM-1002', 1, 3, NULL, N'GHTK881002', CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(10000.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(365000.00 AS Decimal(18, 2)), N'Nguyễn Minh Tuấn', N'0912888999', N'88 Láng Hạ, Đống Đa, Hà Nội', N'Gọi trước khi giao 15 phút', CAST(N'2026-10-17T16:55:24.6700000' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (3, N'CM-1003', NULL, 1, NULL, NULL, CAST(299000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(299000.00 AS Decimal(18, 2)), N'Lê Hoàng Nam', N'0987654321', N'15 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh', N'Đơn khách đặt nhanh Combo', CAST(N'2026-10-17T18:55:24.6700000' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (4, N'CM-0998', 1, 4, NULL, N'GHTK880998', CAST(680000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(680000.00 AS Decimal(18, 2)), N'Trần Mua Hàng', N'0901234567', N'Số 12 ngõ 45 phố Duy Tân, Cầu Giấy, Hà Nội', N'Đơn hoàn thành hôm qua', CAST(N'2026-10-16T18:55:24.6700000' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (5, N'CM-0997', NULL, 4, 2, N'GHTK880997', CAST(420000.00 AS Decimal(18, 2)), CAST(30000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(30000.00 AS Decimal(18, 2)), CAST(420000.00 AS Decimal(18, 2)), N'Phạm Quốc Bảo', N'0933444555', N'24 Lê Duẩn, Hải Châu, Đà Nẵng', N'Freeship 30k', CAST(N'2026-10-15T18:55:24.6700000' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (6, N'CM-0996', 1, 4, NULL, N'GHTK880996', CAST(890000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(20000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(870000.00 AS Decimal(18, 2)), N'Vũ Đình Trọng', N'0977111222', N'102 Trần Phú, Nha Trang, Khánh Hòa', N'Áo Polo + Quần Jeans', CAST(N'2026-10-14T18:55:24.6700000' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (7, N'CM-0995', NULL, 5, NULL, NULL, CAST(250000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(275000.00 AS Decimal(18, 2)), N'Đặng Văn Lâm', N'0966333444', N'Số 88 đường Lạch Tray, Lê Chân, Hải Phòng', N'Khách đổi ý chọn lại size', CAST(N'2026-10-13T18:55:24.6700000' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (8, N'CM-0994', 1, 4, 1, N'GHTK880994', CAST(550000.00 AS Decimal(18, 2)), CAST(40000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(510000.00 AS Decimal(18, 2)), N'Hoàng Đức Anh', N'0944555666', N'56 Cầu Giấy, Hà Nội', N'Đã giao thành công', CAST(N'2026-10-12T18:55:24.6700000' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (262, N'DH1792919666026889', NULL, 1, NULL, NULL, CAST(598000.00 AS Decimal(18, 2)), CAST(59800.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(538200.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-25T16:14:26.0264908' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (263, N'DH1792919729602454', NULL, 1, NULL, NULL, CAST(598000.00 AS Decimal(18, 2)), CAST(59800.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(538200.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-25T16:15:29.6028362' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (281, N'DH1792919786016515', NULL, 1, NULL, NULL, CAST(598000.00 AS Decimal(18, 2)), CAST(59800.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(538200.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-25T16:16:26.0199504' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (282, N'DH1792920284519349', NULL, 1, NULL, NULL, CAST(598000.00 AS Decimal(18, 2)), CAST(59800.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(538200.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-25T16:24:44.5224223' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (283, N'DH1792921290567865', NULL, 1, NULL, NULL, CAST(598000.00 AS Decimal(18, 2)), CAST(59800.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(538200.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-25T16:41:30.5714151' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (284, N'DH1792921552852505', 14, 1, NULL, NULL, CAST(299000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(30000.00 AS Decimal(18, 2)), CAST(329000.00 AS Decimal(18, 2)), N'Nguyeen Nguyene', N'0983823668', N'123, Bình Thủy, Bình Thủy, Cần Thơ', N'', CAST(N'2026-10-25T16:45:52.8554932' AS DateTime2), NULL, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (285, N'DH_TEST_US20_1792922404057', NULL, 2, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:00:04.0572314' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (286, N'DH_TEST_US20_1792922404476', NULL, 2, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:00:04.4763428' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (287, N'DH_TEST_US20_1792922404599', NULL, 1, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:00:04.5997846' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (288, N'DH_TEST_US20_1792922404637', NULL, 1, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:00:04.6375304' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (289, N'DH_TEST_US20_1792922404664', NULL, 1, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:00:04.6644096' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (290, N'DH_TEST_US20_1792922444797', NULL, 2, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:00:44.7975408' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (291, N'DH_TEST_US20_1792922445241', NULL, 2, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:00:45.2419760' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (292, N'DH_TEST_US20_1792922445373', NULL, 1, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:00:45.3734814' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (293, N'DH_TEST_US20_1792922445414', NULL, 1, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:00:45.4148167' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (294, N'DH_TEST_US20_1792922445443', NULL, 1, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:00:45.4433603' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (295, N'DH1792922447154146', NULL, 1, NULL, NULL, CAST(598000.00 AS Decimal(18, 2)), CAST(59800.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(538200.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-25T17:00:47.1544029' AS DateTime2), 149, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (296, N'DH1792924747774236', NULL, 1, NULL, NULL, CAST(600000.00 AS Decimal(18, 2)), CAST(10000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(615000.00 AS Decimal(18, 2)), N'Nguyễn Hưng Dev 5', N'0912345678', N'123 Phố Huế, Phường Phạm Đình Hổ, Quận Hai Bà Trưng, Hà Nội', N'Giao hàng giờ hành chính', CAST(N'2026-10-25T17:39:07.7808678' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (297, N'DH1792924748709238', NULL, 1, NULL, NULL, CAST(100000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(100000.00 AS Decimal(18, 2)), N'Lê Tra Cứu', N'0911223344', N'456 Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:39:08.7106590' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (298, N'DH1792924853536281', NULL, 1, NULL, NULL, CAST(600000.00 AS Decimal(18, 2)), CAST(10000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(615000.00 AS Decimal(18, 2)), N'Nguyễn Hưng Dev 5', N'0912345678', N'123 Phố Huế, Phường Phạm Đình Hổ, Quận Hai Bà Trưng, Hà Nội', N'Giao hàng giờ hành chính', CAST(N'2026-10-25T17:40:53.5470872' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (299, N'DH1792924854861135', NULL, 1, NULL, NULL, CAST(100000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(100000.00 AS Decimal(18, 2)), N'Lê Tra Cứu', N'0911223344', N'456 Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:40:54.8646504' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (300, N'DH1792924907362827', NULL, 1, NULL, NULL, CAST(600000.00 AS Decimal(18, 2)), CAST(10000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(615000.00 AS Decimal(18, 2)), N'Nguyễn Hưng Dev 5', N'0912345678', N'123 Phố Huế, Phường Phạm Đình Hổ, Quận Hai Bà Trưng, Hà Nội', N'Giao hàng giờ hành chính', CAST(N'2026-10-25T17:41:47.3701023' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (301, N'DH1792924908656740', NULL, 1, NULL, NULL, CAST(100000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(100000.00 AS Decimal(18, 2)), N'Lê Tra Cứu', N'0911223344', N'456 Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:41:48.6567267' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (302, N'DH1792924952646372', NULL, 1, NULL, NULL, CAST(600000.00 AS Decimal(18, 2)), CAST(10000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(615000.00 AS Decimal(18, 2)), N'Nguyễn Hưng Dev 5', N'0912345678', N'123 Phố Huế, Phường Phạm Đình Hổ, Quận Hai Bà Trưng, Hà Nội', N'Giao hàng giờ hành chính', CAST(N'2026-10-25T17:42:32.6539251' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (303, N'DH1792924953598187', NULL, 1, NULL, NULL, CAST(100000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(100000.00 AS Decimal(18, 2)), N'Lê Tra Cứu', N'0911223344', N'456 Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:42:33.6010060' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (304, N'DH1792925039283568', NULL, 1, NULL, NULL, CAST(600000.00 AS Decimal(18, 2)), CAST(10000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(615000.00 AS Decimal(18, 2)), N'Nguyễn Hưng Dev 5', N'0912345678', N'123 Phố Huế, Phường Phạm Đình Hổ, Quận Hai Bà Trưng, Hà Nội', N'Giao hàng giờ hành chính', CAST(N'2026-10-25T17:43:59.2901932' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (305, N'DH1792925040479467', NULL, 1, NULL, NULL, CAST(100000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(100000.00 AS Decimal(18, 2)), N'Lê Tra Cứu', N'0911223344', N'456 Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:44:00.4819836' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (306, N'DH1792925083658474', NULL, 1, NULL, NULL, CAST(600000.00 AS Decimal(18, 2)), CAST(10000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(615000.00 AS Decimal(18, 2)), N'Nguyễn Hưng Dev 5', N'0912345678', N'123 Phố Huế, Phường Phạm Đình Hổ, Quận Hai Bà Trưng, Hà Nội', N'Giao hàng giờ hành chính', CAST(N'2026-10-25T17:44:43.6681700' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (307, N'DH1792925085086429', NULL, 1, NULL, NULL, CAST(100000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(100000.00 AS Decimal(18, 2)), N'Lê Tra Cứu', N'0911223344', N'456 Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:44:45.0887065' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (308, N'DH_TEST_US20_1792925087918', NULL, 2, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:44:47.9180121' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (309, N'DH_TEST_US20_1792925088001', NULL, 2, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:44:48.0010822' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (310, N'DH_TEST_US20_1792925088057', NULL, 1, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:44:48.0570232' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (311, N'DH_TEST_US20_1792925088090', NULL, 1, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:44:48.0914519' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (312, N'DH_TEST_US20_1792925088112', NULL, 1, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:44:48.1127102' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (313, N'DH1792925089006433', NULL, 1, NULL, NULL, CAST(598000.00 AS Decimal(18, 2)), CAST(59800.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(538200.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-25T17:44:49.0098076' AS DateTime2), 149, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (314, N'DH1792925166912826', NULL, 1, NULL, NULL, CAST(200000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(20000.00 AS Decimal(18, 2)), CAST(220000.00 AS Decimal(18, 2)), N'Khách Hàng Test API', N'0987654321', N'789 Phố Bà Triệu, Hà Nội', N'Giao buổi sáng', CAST(N'2026-10-25T17:46:06.9484006' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (315, N'DH1792925860735356', NULL, 1, NULL, NULL, CAST(600000.00 AS Decimal(18, 2)), CAST(10000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(615000.00 AS Decimal(18, 2)), N'Nguyễn Hưng Dev 5', N'0912345678', N'123 Phố Huế, Phường Phạm Đình Hổ, Quận Hai Bà Trưng, Hà Nội', N'Giao hàng giờ hành chính', CAST(N'2026-10-25T17:57:40.7419497' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (316, N'DH1792925861077361', NULL, 1, NULL, NULL, CAST(100000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(100000.00 AS Decimal(18, 2)), N'Lê Tra Cứu', N'0911223344', N'456 Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:57:41.0796985' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (317, N'DH_TEST_US20_1792925861154', NULL, 2, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:57:41.1546644' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (318, N'DH_TEST_US20_1792925861239', NULL, 2, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:57:41.2399554' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (319, N'DH_TEST_US20_1792925861303', NULL, 1, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:57:41.3036341' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (320, N'DH_TEST_US20_1792925861331', NULL, 1, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:57:41.3312906' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (321, N'DH_TEST_US20_1792925861354', NULL, 1, NULL, NULL, CAST(350000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(350000.00 AS Decimal(18, 2)), N'Trần Thanh Toán', N'0912345678', N'123 Phố Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T17:57:41.3541465' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (322, N'DH1792925861682120', NULL, 1, NULL, NULL, CAST(598000.00 AS Decimal(18, 2)), CAST(59800.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(538200.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-25T17:57:41.6832617' AS DateTime2), 149, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (323, N'DH1792939800999598', 14, 1, NULL, NULL, CAST(299000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(30000.00 AS Decimal(18, 2)), CAST(329000.00 AS Decimal(18, 2)), N'Nguyeen Nguyene', N'0983823668', N'123, Tam Hiệp, Biên Hòa, Đồng Nai', N'', CAST(N'2026-10-25T21:50:01.0074897' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (324, N'DH1792940336298634', NULL, 1, NULL, NULL, CAST(600000.00 AS Decimal(18, 2)), CAST(10000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(615000.00 AS Decimal(18, 2)), N'Nguyễn Hưng Dev 5', N'0912345678', N'123 Phố Huế, Phường Phạm Đình Hổ, Quận Hai Bà Trưng, Hà Nội', N'Giao hàng giờ hành chính', CAST(N'2026-10-25T21:58:56.3075723' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (325, N'DH1792940337651306', NULL, 1, NULL, NULL, CAST(100000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(100000.00 AS Decimal(18, 2)), N'Lê Tra Cứu', N'0911223344', N'456 Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-25T21:58:57.6534997' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (326, N'DH1792940848730450', 14, 2, NULL, NULL, CAST(329000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(329000.00 AS Decimal(18, 2)), N'Nguyeen Nguyene', N'0983823668', N'123, Tam Hiệp, Biên Hòa, Đồng Nai', N'', CAST(N'2026-10-25T22:07:28.7327739' AS DateTime2), 2, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (327, N'DH1793001558167852', NULL, 3, NULL, N'GHN-HN-998877', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Trần Vận Đơn', N'0988776655', N'789 Hoàng Hoa Thám, Hà Nội', NULL, CAST(N'2026-10-26T14:59:18.1754648' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (328, N'DH1793001558565554', NULL, 1, NULL, NULL, CAST(600000.00 AS Decimal(18, 2)), CAST(10000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(615000.00 AS Decimal(18, 2)), N'Nguyễn Hưng Dev 5', N'0912345678', N'123 Phố Huế, Phường Phạm Đình Hổ, Quận Hai Bà Trưng, Hà Nội', N'Giao hàng giờ hành chính', CAST(N'2026-10-26T14:59:18.5654573' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (329, N'DH1793001558670965', NULL, 5, NULL, NULL, CAST(400000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(400000.00 AS Decimal(18, 2)), N'Phạm Hủy Đơn', N'0912345678', N'123 Phố Huế, Hà Nội', NULL, CAST(N'2026-10-26T14:59:18.6728650' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (330, N'DH1793001559524831', NULL, 4, NULL, N'GHN-SMPL-888999', CAST(100000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(100000.00 AS Decimal(18, 2)), N'Lê Tra Cứu', N'0911223344', N'456 Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-26T14:59:19.5242131' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (331, N'DH1793003624684790', NULL, 3, NULL, N'GHN-HN-998877', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Trần Vận Đơn', N'0988776655', N'789 Hoàng Hoa Thám, Hà Nội', NULL, CAST(N'2026-10-26T15:33:44.6912236' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (332, N'DH1793003625159580', NULL, 1, NULL, NULL, CAST(600000.00 AS Decimal(18, 2)), CAST(10000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(615000.00 AS Decimal(18, 2)), N'Nguyễn Hưng Dev 5', N'0912345678', N'123 Phố Huế, Phường Phạm Đình Hổ, Quận Hai Bà Trưng, Hà Nội', N'Giao hàng giờ hành chính', CAST(N'2026-10-26T15:33:45.1590184' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (333, N'DH1793003625273519', NULL, 4, NULL, N'GHN-VALIDATE-123', CAST(100000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(100000.00 AS Decimal(18, 2)), N'Lê Shopee Flow', N'0988776655', N'456 Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-26T15:33:45.2735773' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (334, N'DH1793003625494561', NULL, 5, NULL, NULL, CAST(400000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(400000.00 AS Decimal(18, 2)), N'Phạm Hủy Đơn', N'0912345678', N'123 Phố Huế, Hà Nội', NULL, CAST(N'2026-10-26T15:33:45.4964483' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (335, N'DH1793003626306160', NULL, 1, NULL, NULL, CAST(100000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(100000.00 AS Decimal(18, 2)), N'Lê Tra Cứu', N'0911223344', N'456 Cầu Giấy, Hà Nội', NULL, CAST(N'2026-10-26T15:33:46.3061503' AS DateTime2), 1, N'ONLINE', NULL, NULL, NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (336, N'DH-POS-1793004621018', NULL, 4, NULL, NULL, CAST(750000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(700000.00 AS Decimal(18, 2)), N'Anh Hoàng Bán Lẻ', N'0977889900', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'Khách mua trực tiếp tại quầy', CAST(N'2026-10-26T15:50:21.0185028' AS DateTime2), 226, N'TAI_QUAY', CAST(1000000.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (337, N'DH-POS-1793004679039', NULL, 4, NULL, NULL, CAST(750000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(700000.00 AS Decimal(18, 2)), N'Anh Hoàng Bán Lẻ', N'0977889900', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'Khách mua trực tiếp tại quầy', CAST(N'2026-10-26T15:51:19.0396782' AS DateTime2), 226, N'TAI_QUAY', CAST(1000000.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (338, N'DH-POS-1793004855083', NULL, 4, NULL, NULL, CAST(329000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(329000.00 AS Decimal(18, 2)), N'Khách lẻ tại quầy', N'0000000000', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'', CAST(N'2026-10-26T15:54:15.0838992' AS DateTime2), 226, N'TAI_QUAY', CAST(1000000.00 AS Decimal(18, 2)), CAST(671000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (339, N'DH-POS-1793004883469', NULL, 4, NULL, NULL, CAST(827000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(827000.00 AS Decimal(18, 2)), N'Khách lẻ tại quầy', N'0000000000', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'', CAST(N'2026-10-26T15:54:43.4694445' AS DateTime2), 226, N'TAI_QUAY', CAST(1000000.00 AS Decimal(18, 2)), CAST(173000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (340, N'DH1793005168287457', 14, 5, NULL, NULL, CAST(250000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(30000.00 AS Decimal(18, 2)), CAST(280000.00 AS Decimal(18, 2)), N'Nguyeen Nguyene', N'0983823668', N'123, Tam Hiệp, Biên Hòa, Đồng Nai', N'', CAST(N'2026-10-26T15:59:28.2917628' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (341, N'DH-POS-1793005887469', 652, 4, NULL, NULL, CAST(750000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(700000.00 AS Decimal(18, 2)), N'Anh Hoàng Bán Lẻ', N'0977889900', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'Khách mua trực tiếp tại quầy', CAST(N'2026-10-26T16:11:27.4695387' AS DateTime2), 226, N'TAI_QUAY', CAST(1000000.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (342, N'DH-POS-1793006078932', 652, 4, NULL, NULL, CAST(750000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(700000.00 AS Decimal(18, 2)), N'Anh Hoàng Bán Lẻ', N'0977889900', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'Khách mua trực tiếp tại quầy', CAST(N'2026-10-26T16:14:38.9320790' AS DateTime2), 226, N'TAI_QUAY', CAST(1000000.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (343, N'DH-POS-1793006079964', 654, 4, NULL, NULL, CAST(250000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(250000.00 AS Decimal(18, 2)), N'Khách Test Loyalty', N'096679791', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'Bán hàng tại quầy', CAST(N'2026-10-26T16:14:39.9646074' AS DateTime2), 226, N'TAI_QUAY', CAST(250000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (344, N'DH-POS-1793006080175', 654, 4, NULL, NULL, CAST(250000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(20000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(230000.00 AS Decimal(18, 2)), N'Khách Test Loyalty', N'096679791', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'Bán hàng tại quầy', CAST(N'2026-10-26T16:14:40.1752325' AS DateTime2), 226, N'TAI_QUAY', CAST(230000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (345, N'DH-POS-1793007402358', 652, 4, NULL, NULL, CAST(750000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(700000.00 AS Decimal(18, 2)), N'Anh Hoàng Bán Lẻ', N'0977889900', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'Khách mua trực tiếp tại quầy', CAST(N'2026-10-26T16:36:42.3586466' AS DateTime2), 226, N'TAI_QUAY', CAST(1000000.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (346, N'DH-POS-1793007403387', 659, 4, NULL, NULL, CAST(250000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(250000.00 AS Decimal(18, 2)), N'Khách Test Loyalty', N'0966403232', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'Bán hàng tại quầy', CAST(N'2026-10-26T16:36:43.3874634' AS DateTime2), 226, N'TAI_QUAY', CAST(250000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (347, N'DH-POS-1793007403606', 659, 4, NULL, NULL, CAST(250000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(20000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(230000.00 AS Decimal(18, 2)), N'Khách Test Loyalty', N'0966403232', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'Bán hàng tại quầy', CAST(N'2026-10-26T16:36:43.6069008' AS DateTime2), 226, N'TAI_QUAY', CAST(230000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (348, N'DH-POS-1793007979455', 652, 4, NULL, NULL, CAST(750000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(700000.00 AS Decimal(18, 2)), N'Anh Hoàng Bán Lẻ', N'0977889900', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'Khách mua trực tiếp tại quầy', CAST(N'2026-10-26T16:46:19.4556551' AS DateTime2), 226, N'TAI_QUAY', CAST(1000000.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (349, N'DH-POS-1793007980426', 661, 4, NULL, NULL, CAST(250000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(250000.00 AS Decimal(18, 2)), N'Khách Test Loyalty', N'0966980293', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'Bán hàng tại quầy', CAST(N'2026-10-26T16:46:20.4266585' AS DateTime2), 226, N'TAI_QUAY', CAST(250000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (350, N'DH-POS-1793007980628', 661, 4, NULL, NULL, CAST(250000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(20000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(230000.00 AS Decimal(18, 2)), N'Khách Test Loyalty', N'0966980293', N'Mua và nhận hàng trực tiếp tại quầy SIMPLAIN STORE', N'Bán hàng tại quầy', CAST(N'2026-10-26T16:46:20.6282529' AS DateTime2), 226, N'TAI_QUAY', CAST(230000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), 1)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (351, N'DH1793010353378160', 681, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981353337', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:25:53.3833853' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (352, N'DH1793010353570850', 683, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981353535', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:25:53.5703128' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (353, N'DH1793010354285182', 685, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(20000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(280000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981354202', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:25:54.2850468' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (354, N'DH1793010354446716', 687, 3, NULL, N'SMPL-VD-TRACKING-999', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981354412', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:25:54.4463725' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (355, N'DH1793010354555603', 689, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981354529', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:25:54.5569948' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (356, N'DH1793010354565588', 689, 3, NULL, N'SMPL-VD-TEST123', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981354529', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:25:54.5651662' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (357, N'DH1793010354690806', 691, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981354663', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:25:54.6909559' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (358, N'DH1793010354771470', 693, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981354744', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:25:54.7712008' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (359, N'DH1793010354777407', 693, 3, NULL, N'SMPL-VD-TEST456', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981354744', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:25:54.7779502' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (360, N'DH1793010354887400', 695, 3, NULL, N'SMPL-VD-FAIL-CANCEL', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981354861', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:25:54.8876011' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (361, N'DH1793010550340162', 699, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981550305', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:29:10.3440165' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (362, N'DH1793010550523898', 701, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981550483', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:29:10.5233426' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (363, N'DH1793010551234334', 703, 5, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(20000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(280000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981551147', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:29:11.2343135' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (364, N'DH1793010551394271', 705, 3, NULL, N'SMPL-VD-TRACKING-999', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981551358', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:29:11.3945774' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (365, N'DH1793010551710970', 707, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981551577', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:29:11.7104780' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (366, N'DH1793010551717986', 707, 3, NULL, N'SMPL-VD-TEST123', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981551577', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:29:11.7174815' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (367, N'DH1793010551840846', 709, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981551812', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:29:11.8403679' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (368, N'DH1793010551932792', 711, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981551905', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:29:11.9327003' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (369, N'DH1793010551938982', 711, 3, NULL, N'SMPL-VD-TEST456', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981551905', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:29:11.9384238' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (370, N'DH1793010552034856', 713, 3, NULL, N'SMPL-VD-FAIL-CANCEL', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981552010', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-26T17:29:12.0346975' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (371, N'DH1793084473518810', 14, 4, NULL, N'SPX-23116202', CAST(192000.00 AS Decimal(18, 2)), CAST(30000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(162000.00 AS Decimal(18, 2)), N'Nguyeen Nguyene', N'0983823668', N'123, Tam Hiệp, Biên Hòa, Đồng Nai', N'', CAST(N'2026-10-27T14:01:13.5214284' AS DateTime2), 2, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (432, N'DH1793085794130782', 777, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981794106', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T14:23:14.1306659' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (433, N'DH1793085794198838', 779, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981794175', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T14:23:14.1981646' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (434, N'DH1793085794425882', 781, 5, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(20000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(280000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981794353', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T14:23:14.4253244' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (435, N'DH1793085794558833', 783, 3, NULL, N'SMPL-VD-TRACKING-999', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981794535', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T14:23:14.5582326' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (436, N'DH1793085794677696', 785, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981794650', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T14:23:14.6770837' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (437, N'DH1793085794682111', 785, 3, NULL, N'SMPL-VD-TEST123', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981794650', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T14:23:14.6825065' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (438, N'DH1793085794777560', 787, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981794756', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T14:23:14.7772270' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (439, N'DH1793085794851743', 789, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981794829', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T14:23:14.8518282' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (440, N'DH1793085794859813', 789, 3, NULL, N'SMPL-VD-TEST456', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981794829', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T14:23:14.8595987' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (441, N'DH1793085794947152', 791, 3, NULL, N'SMPL-VD-FAIL-CANCEL', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981794924', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T14:23:14.9473855' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (632, N'DH1793096505543169', 14, 6, NULL, N'VTP-59239998', CAST(250000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(30000.00 AS Decimal(18, 2)), CAST(280000.00 AS Decimal(18, 2)), N'Nguyeen Nguyene', N'0983823668', N'123, Tam Hiệp, Biên Hòa, Đồng Nai', N'', CAST(N'2026-10-27T17:21:45.5524098' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (727, N'DH1793097816831292', 14, 7, NULL, N'SPX-81371244', CAST(192000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(30000.00 AS Decimal(18, 2)), CAST(222000.00 AS Decimal(18, 2)), N'Nguyeen Nguyene', N'0983823668', N'123, Tam Hiệp, Biên Hòa, Đồng Nai', N'', CAST(N'2026-10-27T17:43:36.8382710' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (975, N'DH1793103396493333', 14, 1, 73, NULL, CAST(250000.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), CAST(1.00 AS Decimal(18, 2)), CAST(30000.00 AS Decimal(18, 2)), CAST(254999.00 AS Decimal(18, 2)), N'Nguyeen Nguyene', N'0983823668', N'123, Tam Hiệp, Biên Hòa, Đồng Nai', N'', CAST(N'2026-10-27T19:16:36.5016636' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (983, N'DH1793104297799604', NULL, 1, NULL, NULL, CAST(500000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(450000.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-27T19:31:37.8077291' AS DateTime2), 149, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (984, N'DH1793104382199909', NULL, 1, NULL, NULL, CAST(500000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(450000.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-27T19:33:02.2081210' AS DateTime2), 149, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (985, N'DH1793104430977655', NULL, 1, NULL, NULL, CAST(500000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(450000.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-27T19:33:50.9861941' AS DateTime2), 149, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1010, N'DH1793104467193101', 1451, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981467176', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T19:34:27.1973813' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1011, N'DH1793104467246731', 1453, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981467228', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T19:34:27.2460265' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1012, N'DH1793104467400470', 1455, 5, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(20000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(280000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981467348', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T19:34:27.4005230' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1013, N'DH1793104467502770', 1457, 3, NULL, N'SMPL-VD-TRACKING-999', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981467487', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T19:34:27.5034858' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1014, N'DH1793104467591161', 1459, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981467576', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T19:34:27.5914398' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1015, N'DH1793104467596244', 1459, 3, NULL, N'SMPL-VD-TEST123', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981467576', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T19:34:27.5960534' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1016, N'DH1793104467665401', 1461, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981467649', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T19:34:27.6659558' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1017, N'DH1793104467725530', 1463, 1, NULL, NULL, CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981467708', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T19:34:27.7257147' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1018, N'DH1793104467732573', 1463, 3, NULL, N'SMPL-VD-TEST456', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981467708', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T19:34:27.7321278' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1019, N'DH1793104467796746', 1465, 3, NULL, N'SMPL-VD-FAIL-CANCEL', CAST(300000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(300000.00 AS Decimal(18, 2)), N'Nguyễn Khách Hàng A', N'0981467779', N'123 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', CAST(N'2026-10-27T19:34:27.7961324' AS DateTime2), 1, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1046, N'DH1793104470710729', NULL, 1, NULL, NULL, CAST(500000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(450000.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-27T19:34:30.7127154' AS DateTime2), 149, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1047, N'DH1793107267536397', NULL, 1, NULL, NULL, CAST(500000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(450000.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-27T20:21:07.5444420' AS DateTime2), 149, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1048, N'DH1793108999982281', NULL, 1, NULL, NULL, CAST(500000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(450000.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-27T20:49:59.9906174' AS DateTime2), 149, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1049, N'DH1793152613033852', NULL, 1, NULL, NULL, CAST(500000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(450000.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-28T08:56:53.0413940' AS DateTime2), 149, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1051, N'DH1793156632192526', 14, 2, NULL, NULL, CAST(521000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(521000.00 AS Decimal(18, 2)), N'Nguyeen Nguyene', N'0983823668', N'123, An Bình, Ninh Kiều, Cần Thơ', N'', CAST(N'2026-10-28T10:03:52.2018889' AS DateTime2), 129, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1052, N'DH1793156798571415', 14, 1, NULL, NULL, CAST(192000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(30000.00 AS Decimal(18, 2)), CAST(222000.00 AS Decimal(18, 2)), N'Nguyeen Nguyene', N'0983823668', N'123, Tam Hiệp, Biên Hòa, Đồng Nai', N'', CAST(N'2026-10-28T10:06:38.5719569' AS DateTime2), 227, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1053, N'DH1793156817965824', 14, 4, NULL, N'SPX-81371244', CAST(192000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(30000.00 AS Decimal(18, 2)), CAST(222000.00 AS Decimal(18, 2)), N'Nguyeen Nguyene', N'0983823668', N'123, Tam Hiệp, Biên Hòa, Đồng Nai', N'', CAST(N'2026-10-28T10:06:57.9674020' AS DateTime2), 3, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[don_hang] ([id], [ma_don_hang], [ma_khach_hang_id], [ma_trang_thai_id], [ma_giam_gia_id], [ma_van_don], [tong_tien_hang], [tien_giam_gia], [tien_coolcash_dung], [phi_van_chuyen], [tong_thanh_toan], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_giao_hang], [ghi_chu], [ngay_tao], [ma_phuong_thuc_id], [loai_don_hang], [tien_khach_dua], [tien_thua_tra_khach], [ma_nhan_vien_id]) VALUES (1054, N'DH1793167164966586', NULL, 1, NULL, NULL, CAST(500000.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), CAST(450000.00 AS Decimal(18, 2)), N'Người Test Simplain', N'0912345678', N'Số 123 Đường Cầu Giấy, Hà Nội', N'Giao trong giờ hành chính', CAST(N'2026-10-28T12:59:24.9742977' AS DateTime2), 149, N'ONLINE', CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)), NULL)
GO
SET IDENTITY_INSERT [dbo].[don_hang] OFF
GO
SET IDENTITY_INSERT [dbo].[flash_sale] ON 
GO
INSERT [dbo].[flash_sale] ([id], [tieu_de], [thoi_gian_bat_dau], [thoi_gian_ket_thuc], [kich_hoat]) VALUES (1, N'Deal Sốc Giữa Trưa', CAST(N'2025-02-15T12:00:00.0000000' AS DateTime2), CAST(N'2025-02-15T14:00:00.0000000' AS DateTime2), 1)
GO
INSERT [dbo].[flash_sale] ([id], [tieu_de], [thoi_gian_bat_dau], [thoi_gian_ket_thuc], [kich_hoat]) VALUES (155, N'Deal Sốc Giờ Vàng 09h 28/10', CAST(N'2026-10-28T08:05:00.0000000' AS DateTime2), CAST(N'2026-10-28T23:59:59.0000000' AS DateTime2), 1)
GO
SET IDENTITY_INSERT [dbo].[flash_sale] OFF
GO
SET IDENTITY_INSERT [dbo].[form_dang] ON 
GO
INSERT [dbo].[form_dang] ([id], [ten_form_dang], [mo_ta], [kich_hoat]) VALUES (6, N'Regular Fit', N'Dáng suông vừa vặn tiêu chuẩn nam giới Việt', 1)
GO
INSERT [dbo].[form_dang] ([id], [ten_form_dang], [mo_ta], [kich_hoat]) VALUES (7, N'Slim Fit', N'Dáng ôm nhẹ tôn dáng phần ngực và eo', 1)
GO
INSERT [dbo].[form_dang] ([id], [ten_form_dang], [mo_ta], [kich_hoat]) VALUES (8, N'Oversized', N'Dáng rộng phóng khoáng trẻ trung streetwear', 1)
GO
INSERT [dbo].[form_dang] ([id], [ten_form_dang], [mo_ta], [kich_hoat]) VALUES (9, N'Relaxed Fit', N'Dáng rộng thoải mái, vận động hằng ngày', 1)
GO
INSERT [dbo].[form_dang] ([id], [ten_form_dang], [mo_ta], [kich_hoat]) VALUES (10, N'Boxy Fit', N'Dáng hộp vai rộng, thân áo vuông hiện đại', 1)
GO
INSERT [dbo].[form_dang] ([id], [ten_form_dang], [mo_ta], [kich_hoat]) VALUES (11, N'Loose Fit', N'Dáng thụng rộng rãi phong cách casual', 1)
GO
SET IDENTITY_INSERT [dbo].[form_dang] OFF
GO
SET IDENTITY_INSERT [dbo].[giao_dich_thanh_toan] ON 
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (1, 1, 3, N'TXN_20250215_001_A', NULL, CAST(470000.00 AS Decimal(18, 2)), N'THAT_BAI', NULL, CAST(N'2026-09-26T19:19:49.1333333' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (2, 1, 3, N'TXN_20250215_001_B', N'MM_99218274', CAST(470000.00 AS Decimal(18, 2)), N'THANH_CONG', NULL, CAST(N'2026-09-26T19:19:49.1333333' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (3, 285, 2, N'TXN_DH_TEST_US20_1792922404057_4275', N'SIM_TEST_1792922404283', CAST(350000.00 AS Decimal(18, 2)), N'THANH_CONG', N'{"simulated": true, "message": "Giả lập thanh toán QR thành công qua Sandbox", "time": "2026-10-25T17:00:04.283743100"}', CAST(N'2026-10-25T17:00:04.2831755' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (4, 286, 2, N'TXN_DH_TEST_US20_1792922404476_4503', N'14567890', CAST(350000.00 AS Decimal(18, 2)), N'THANH_CONG', N'{"vnp_Amount":"35000000","vnp_BankCode":"NCB","vnp_CardType":"ATM","vnp_Command":"pay","vnp_OrderInfo":"Thanh toan don hang #DH_TEST_US20_1792922404476","vnp_PayDate":"20261025120000","vnp_ResponseCode":"00","vnp_TmnCode":"SIMPLAIN01","vnp_TransactionNo":"14567890","vnp_TransactionStatus":"00","vnp_TxnRef":"TXN_DH_TEST_US20_1792922404476_4503","vnp_Version":"2.1.0","vnp_SecureHash":"fb678bc73054efa12f3f33c0e8640ee9afe1986792b7a1c640f5d40c84ab7b669c7d42ec2aedd54cf76411684513e4e0fddf3dee1fb930826f7df2fa347bd673"}', CAST(N'2026-10-25T17:00:04.5037131' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (5, 287, 2, N'TXN_DH_TEST_US20_1792922404599_4608', NULL, CAST(350000.00 AS Decimal(18, 2)), N'CHO_THANH_TOAN', NULL, CAST(N'2026-10-25T17:00:04.6084101' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (6, 289, 2, N'TXN_DH_TEST_US20_1792922404664_4677', NULL, CAST(350000.00 AS Decimal(18, 2)), N'CHO_THANH_TOAN', NULL, CAST(N'2026-10-25T17:00:04.6772853' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (7, 290, 2, N'TXN_DH_TEST_US20_1792922444797_45023', N'SIM_TEST_1792922445025', CAST(350000.00 AS Decimal(18, 2)), N'THANH_CONG', N'{"simulated": true, "message": "Giả lập thanh toán QR thành công qua Sandbox", "time": "2026-10-25T17:00:45.031584900"}', CAST(N'2026-10-25T17:00:45.0234279' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (8, 291, 2, N'TXN_DH_TEST_US20_1792922445241_45274', N'14567890', CAST(350000.00 AS Decimal(18, 2)), N'THANH_CONG', N'{"vnp_Amount":"35000000","vnp_BankCode":"NCB","vnp_CardType":"ATM","vnp_Command":"pay","vnp_OrderInfo":"Thanh toan don hang #DH_TEST_US20_1792922445241","vnp_PayDate":"20261025120000","vnp_ResponseCode":"00","vnp_TmnCode":"SIMPLAIN01","vnp_TransactionNo":"14567890","vnp_TransactionStatus":"00","vnp_TxnRef":"TXN_DH_TEST_US20_1792922445241_45274","vnp_Version":"2.1.0","vnp_SecureHash":"81c8a2b311072dcb04d0809e0a9733e7bed09ecabca99d208bc8d43748886e2ed96c1db950568599009967e84ccfc21730d2d3068ed84c2c2692ffb837e6caac"}', CAST(N'2026-10-25T17:00:45.2749360' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (9, 292, 2, N'TXN_DH_TEST_US20_1792922445373_45381', NULL, CAST(350000.00 AS Decimal(18, 2)), N'CHO_THANH_TOAN', NULL, CAST(N'2026-10-25T17:00:45.3813280' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (10, 294, 2, N'TXN_DH_TEST_US20_1792922445443_45446', NULL, CAST(350000.00 AS Decimal(18, 2)), N'CHO_THANH_TOAN', NULL, CAST(N'2026-10-25T17:00:45.4460148' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (11, 308, 2, N'TXN_DH_TEST_US20_1792925087918_87941', N'SIM_TEST_1792925087948', CAST(350000.00 AS Decimal(18, 2)), N'THANH_CONG', N'{"simulated": true, "message": "Giả lập thanh toán QR thành công qua Sandbox", "time": "2026-10-25T17:44:47.948510100"}', CAST(N'2026-10-25T17:44:47.9411829' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (12, 309, 2, N'TXN_DH_TEST_US20_1792925088001_88005', N'14567890', CAST(350000.00 AS Decimal(18, 2)), N'THANH_CONG', N'{"vnp_Amount":"35000000","vnp_BankCode":"NCB","vnp_CardType":"ATM","vnp_Command":"pay","vnp_OrderInfo":"Thanh toan don hang #DH_TEST_US20_1792925088001","vnp_PayDate":"20261025120000","vnp_ResponseCode":"00","vnp_TmnCode":"SIMPLAIN01","vnp_TransactionNo":"14567890","vnp_TransactionStatus":"00","vnp_TxnRef":"TXN_DH_TEST_US20_1792925088001_88005","vnp_Version":"2.1.0","vnp_SecureHash":"515c078138aacc21f595435013b10a8934e61794a4647dd77177b7049a4af66afb2fb45c91e94dc22d3fa7108a8e087e9e012ecb968d1f36c1fc6df77cf4cf8b"}', CAST(N'2026-10-25T17:44:48.0059337' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (13, 310, 2, N'TXN_DH_TEST_US20_1792925088057_88063', NULL, CAST(350000.00 AS Decimal(18, 2)), N'CHO_THANH_TOAN', NULL, CAST(N'2026-10-25T17:44:48.0636903' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (14, 312, 2, N'TXN_DH_TEST_US20_1792925088112_88117', NULL, CAST(350000.00 AS Decimal(18, 2)), N'CHO_THANH_TOAN', NULL, CAST(N'2026-10-25T17:44:48.1179635' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (15, 317, 2, N'TXN_DH_TEST_US20_1792925861154_61176', N'SIM_TEST_1792925861181', CAST(350000.00 AS Decimal(18, 2)), N'THANH_CONG', N'{"simulated": true, "message": "Giả lập thanh toán QR thành công qua Sandbox", "time": "2026-10-25T17:57:41.181866600"}', CAST(N'2026-10-25T17:57:41.1760911' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (16, 318, 2, N'TXN_DH_TEST_US20_1792925861239_61247', N'14567890', CAST(350000.00 AS Decimal(18, 2)), N'THANH_CONG', N'{"vnp_Amount":"35000000","vnp_BankCode":"NCB","vnp_CardType":"ATM","vnp_Command":"pay","vnp_OrderInfo":"Thanh toan don hang #DH_TEST_US20_1792925861239","vnp_PayDate":"20261025120000","vnp_ResponseCode":"00","vnp_TmnCode":"SIMPLAIN01","vnp_TransactionNo":"14567890","vnp_TransactionStatus":"00","vnp_TxnRef":"TXN_DH_TEST_US20_1792925861239_61247","vnp_Version":"2.1.0","vnp_SecureHash":"b30b1356456543d14773053062fc4901fbc76389966024e45ce938f509f3d23d6ec451d3f7c95897ae8f16768b2705b6cf2f4ed6808826aafcd8c65934060049"}', CAST(N'2026-10-25T17:57:41.2472396' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (17, 319, 2, N'TXN_DH_TEST_US20_1792925861303_61310', NULL, CAST(350000.00 AS Decimal(18, 2)), N'CHO_THANH_TOAN', NULL, CAST(N'2026-10-25T17:57:41.3104385' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (18, 321, 2, N'TXN_DH_TEST_US20_1792925861354_61361', NULL, CAST(350000.00 AS Decimal(18, 2)), N'CHO_THANH_TOAN', NULL, CAST(N'2026-10-25T17:57:41.3610859' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (19, 326, 2, N'TXN_DH1792940848730450_48773', N'SIM_TEST_1792940907376', CAST(329000.00 AS Decimal(18, 2)), N'THANH_CONG', N'{"simulated": true, "message": "Giả lập thanh toán QR thành công qua Sandbox", "time": "2026-10-25T22:08:27.377516300"}', CAST(N'2026-10-25T22:07:28.7748823' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (20, 336, 226, N'TXN-POS-1793004621053', N'POS-COUNTER', CAST(700000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T15:50:21.0185028' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (21, 337, 226, N'TXN-POS-1793004679063', N'POS-COUNTER', CAST(700000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T15:51:19.0396782' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (22, 338, 226, N'TXN-POS-1793004855106', N'POS-COUNTER', CAST(329000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T15:54:15.0838992' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (23, 339, 226, N'TXN-POS-1793004883487', N'POS-COUNTER', CAST(827000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T15:54:43.4694445' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (24, 341, 226, N'TXN-POS-1793005887507', N'POS-COUNTER', CAST(700000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T16:11:27.4695387' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (25, 342, 226, N'TXN-POS-1793006078951', N'POS-COUNTER', CAST(700000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T16:14:38.9320790' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (26, 343, 226, N'TXN-POS-1793006079978', N'POS-COUNTER', CAST(250000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T16:14:39.9646074' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (27, 344, 226, N'TXN-POS-1793006080188', N'POS-COUNTER', CAST(230000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T16:14:40.1752325' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (28, 345, 226, N'TXN-POS-1793007402387', N'POS-COUNTER', CAST(700000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T16:36:42.3586466' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (29, 346, 226, N'TXN-POS-1793007403407', N'POS-COUNTER', CAST(250000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T16:36:43.3874634' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (30, 347, 226, N'TXN-POS-1793007403617', N'POS-COUNTER', CAST(230000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T16:36:43.6069008' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (31, 348, 226, N'TXN-POS-1793007979485', N'POS-COUNTER', CAST(700000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T16:46:19.4556551' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (32, 349, 226, N'TXN-POS-1793007980443', N'POS-COUNTER', CAST(250000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T16:46:20.4266585' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (33, 350, 226, N'TXN-POS-1793007980638', N'POS-COUNTER', CAST(230000.00 AS Decimal(18, 2)), N'THANH_CONG', N'Thanh toán thành công tại quầy thu ngân', CAST(N'2026-10-26T16:46:20.6282529' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (34, 371, 2, N'TXN_DH1793084473518810_73577', N'SIM_TEST_1793084483384', CAST(162000.00 AS Decimal(18, 2)), N'THANH_CONG', N'{"simulated": true, "message": "Giả lập thanh toán QR thành công qua Sandbox", "time": "2026-10-27T14:01:23.384715"}', CAST(N'2026-10-27T14:01:13.5771081' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (35, 1051, 129, N'TXN_DH1793156632192526_32302', N'SIM_TEST_1793156681025', CAST(521000.00 AS Decimal(18, 2)), N'THANH_CONG', N'{"simulated": true, "message": "Giả lập thanh toán QR thành công qua Sandbox", "time": "2026-10-28T10:04:41.025223400"}', CAST(N'2026-10-28T10:03:52.3033482' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (36, 1052, 227, N'TXN_DH1793156798571415_98643', NULL, CAST(222000.00 AS Decimal(18, 2)), N'CHO_THANH_TOAN', NULL, CAST(N'2026-10-28T10:06:38.6431406' AS DateTime2))
GO
INSERT [dbo].[giao_dich_thanh_toan] ([id], [ma_don_hang_id], [ma_phuong_thuc_id], [ma_giao_dich_noi_bo], [ma_giao_dich_cong], [so_tien], [trang_thai], [du_lieu_phan_hoi], [ngay_tao]) VALUES (37, 1053, 3, N'TXN_DH1793156817965824_18010', NULL, CAST(222000.00 AS Decimal(18, 2)), N'CHO_THANH_TOAN', NULL, CAST(N'2026-10-28T10:06:58.0107847' AS DateTime2))
GO
SET IDENTITY_INSERT [dbo].[giao_dich_thanh_toan] OFF
GO
SET IDENTITY_INSERT [dbo].[gio_hang] ON 
GO
INSERT [dbo].[gio_hang] ([id], [ma_khach_hang_id], [ngay_cap_nhat]) VALUES (469, 14, CAST(N'2026-10-28T17:39:09.0204831' AS DateTime2))
GO
INSERT [dbo].[gio_hang] ([id], [ma_khach_hang_id], [ngay_cap_nhat]) VALUES (470, NULL, CAST(N'2026-10-28T17:41:53.8812128' AS DateTime2))
GO
SET IDENTITY_INSERT [dbo].[gio_hang] OFF
GO
SET IDENTITY_INSERT [dbo].[goi_combo] ON 
GO
INSERT [dbo].[goi_combo] ([id], [ma_combo], [ten_combo], [duong_dan_slug], [gia_combo], [so_mon_quy_dinh], [mo_ta], [kich_hoat]) VALUES (1, N'BOX3-UNDERWEAR', N'Tự Chọn Box 3 Quần Lót', N'tu-chon-box-3-quan-lot', CAST(299000.00 AS Decimal(18, 2)), 3, NULL, 0)
GO
INSERT [dbo].[goi_combo] ([id], [ma_combo], [ten_combo], [duong_dan_slug], [gia_combo], [so_mon_quy_dinh], [mo_ta], [kich_hoat]) VALUES (185, N'COMBO-3M-3000K', N'1-3', N'1-3', CAST(3000000.00 AS Decimal(18, 2)), 3, N'ffffffffffffffffff', 1)
GO
SET IDENTITY_INSERT [dbo].[goi_combo] OFF
GO
SET IDENTITY_INSERT [dbo].[hang_thanh_vien] ON 
GO
INSERT [dbo].[hang_thanh_vien] ([id], [ten_hang], [chi_tieu_nam_toi_thieu], [phan_tram_hoan_tien]) VALUES (1, N'Đồng', CAST(0.00 AS Decimal(18, 2)), CAST(1.00 AS Decimal(5, 2)))
GO
INSERT [dbo].[hang_thanh_vien] ([id], [ten_hang], [chi_tieu_nam_toi_thieu], [phan_tram_hoan_tien]) VALUES (2, N'Bạc', CAST(1000000.00 AS Decimal(18, 2)), CAST(3.00 AS Decimal(5, 2)))
GO
INSERT [dbo].[hang_thanh_vien] ([id], [ten_hang], [chi_tieu_nam_toi_thieu], [phan_tram_hoan_tien]) VALUES (3, N'Vàng', CAST(3000000.00 AS Decimal(18, 2)), CAST(5.00 AS Decimal(5, 2)))
GO
INSERT [dbo].[hang_thanh_vien] ([id], [ten_hang], [chi_tieu_nam_toi_thieu], [phan_tram_hoan_tien]) VALUES (4, N'Bạch Kim', CAST(6000000.00 AS Decimal(18, 2)), CAST(8.00 AS Decimal(5, 2)))
GO
SET IDENTITY_INSERT [dbo].[hang_thanh_vien] OFF
GO
SET IDENTITY_INSERT [dbo].[hinh_anh] ON 
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (1, 1, 1, N'https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=500&auto=format&fit=crop&q=80', 1, 1)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (2, 1, 1, N'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&auto=format&fit=crop&q=80', 0, 2)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (5, 2, 5, N'/uploads/sku_1792303780008_ccca0a.webp', 1, 1)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (6, 2, 5, N'/uploads/sku_1792303795297_b1bcbe.webp', 0, 2)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (9, 3, 9, N'https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=500&auto=format&fit=crop&q=80', 1, 1)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (17, 3, 9, N'/uploads/sku_1792419668418_5bd28c.webp', 0, 2)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (18, 3, 9, N'/uploads/sku_1792419668534_72bd40.webp', 0, 3)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (19, 3, 9, N'/uploads/sku_1792419668557_5f0bdc.webp', 0, 4)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (20, 1, 23, N'https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=600&auto=format&fit=crop&q=80', 1, 1)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (21, 1, 23, N'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600&auto=format&fit=crop&q=80', 0, 2)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (200, 1067, NULL, N'/uploads/sp_1067_1792938202243_192deb.webp', 0, 1)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (201, 1067, NULL, N'/uploads/sp_1067_1792938202266_ea95d5.webp', 1, 1)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (202, 1067, NULL, N'/uploads/sp_1067_1792938202270_95df2e.webp', 0, 3)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (203, 1067, NULL, N'/uploads/sp_1067_1792938202274_8e3e3f.webp', 0, 4)
GO
INSERT [dbo].[hinh_anh] ([id], [ma_san_pham_id], [ma_bien_the_id], [duong_dan_anh], [la_anh_dai_dien], [thu_tu]) VALUES (204, 1067, NULL, N'/uploads/sp_1067_1792938202278_3ce9db.webp', 0, 1)
GO
SET IDENTITY_INSERT [dbo].[hinh_anh] OFF
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (14, 1, CAST(8859.00 AS Decimal(18, 2)), CAST(886000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (652, 2, CAST(56000.00 AS Decimal(18, 2)), CAST(2800000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (654, 1, CAST(32300.00 AS Decimal(18, 2)), CAST(480000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (659, 1, CAST(32300.00 AS Decimal(18, 2)), CAST(480000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (660, 1, CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (661, 1, CAST(32300.00 AS Decimal(18, 2)), CAST(480000.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (662, 1, CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (663, 1, CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (678, 1, CAST(0.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (685, 1, CAST(70000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (703, 1, CAST(70000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (781, 1, CAST(70000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)))
GO
INSERT [dbo].[ho_so_hoi_vien] ([ma_khach_hang_id], [ma_hang_hien_tai_id], [so_du_coolcash], [tong_chi_tieu_nam]) VALUES (1455, 1, CAST(70000.00 AS Decimal(18, 2)), CAST(0.00 AS Decimal(18, 2)))
GO
SET IDENTITY_INSERT [dbo].[hoa_don_cho] ON 
GO
INSERT [dbo].[hoa_don_cho] ([id], [ma_hoa_don], [ma_nhan_vien_id], [ma_khach_hang_id], [ten_khach_hang], [so_dien_thoai_khach], [ghi_chu], [trang_thai], [ngay_tao]) VALUES (9, N'HDC-83546', NULL, 658, N'd3', N'0333420699', NULL, N'CHO_THANH_TOAN', CAST(N'2026-10-26T15:54:43.5466279' AS DateTime2))
GO
INSERT [dbo].[hoa_don_cho] ([id], [ma_hoa_don], [ma_nhan_vien_id], [ma_khach_hang_id], [ten_khach_hang], [so_dien_thoai_khach], [ghi_chu], [trang_thai], [ngay_tao]) VALUES (10, N'HDC-96027', 1, NULL, N'Khách lẻ tại quầy', NULL, NULL, N'CHO_THANH_TOAN', CAST(N'2026-10-26T15:54:56.0273910' AS DateTime2))
GO
INSERT [dbo].[hoa_don_cho] ([id], [ma_hoa_don], [ma_nhan_vien_id], [ma_khach_hang_id], [ten_khach_hang], [so_dien_thoai_khach], [ghi_chu], [trang_thai], [ngay_tao]) VALUES (11, N'HDC-96923', 1, NULL, N'Khách lẻ tại quầy', NULL, NULL, N'CHO_THANH_TOAN', CAST(N'2026-10-26T15:54:56.9233310' AS DateTime2))
GO
INSERT [dbo].[hoa_don_cho] ([id], [ma_hoa_don], [ma_nhan_vien_id], [ma_khach_hang_id], [ten_khach_hang], [so_dien_thoai_khach], [ghi_chu], [trang_thai], [ngay_tao]) VALUES (14, N'HDC-88243', 1, NULL, N'Khách lẻ tại quầy', NULL, NULL, N'CHO_THANH_TOAN', CAST(N'2026-10-26T16:11:28.2438679' AS DateTime2))
GO
SET IDENTITY_INSERT [dbo].[hoa_don_cho] OFF
GO
SET IDENTITY_INSERT [dbo].[khach_hang] ON 
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1, N'KH001', N'Trần Mua Hàng', N'0901234567', N'tranmuahang@gmail.com', N'$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', NULL, NULL, NULL, 1, CAST(N'2026-09-26T19:19:49.0766667' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (14, N'KH279786', N'Đỗ Nguyên', N'0929282257', N'nguyendhbth04829@gmail.com', N'$2a$10$sIlnPq7bNbPbfdhiMBitw.2dahFFYvhHbtjJBIcfTj7rB5lMhbeh6', N'/uploads/avatar_1792859726749_vn-11134207-7ra0g-m7hcjnr6jcnsbc.webp', N'Nam', CAST(N'2005-02-09' AS Date), 1, CAST(N'2026-10-13T13:17:59.9106937' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (282, N'KH21373', N'Nguyễn Văn Test', N'0989999888', N'khachhang.test@gmail.com', N'$2a$10$zcV1SRN7EU2KFmHBnnKVPewrVoyY70A0tsrl7qpYDiwe6gjnPDvwa', NULL, N'Nam', CAST(N'1998-05-20' AS Date), 1, CAST(N'2026-10-25T10:30:21.4989483' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (652, N'KH1793005887429', N'Anh Hoàng Bán Lẻ', N'0977889900', NULL, NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T16:11:27.4290723' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (654, N'KH1793006079904', N'Khách Test Loyalty', N'096679791', N'096679791@pos.simplain.vn', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T16:14:39.9049578' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (655, N'KH1793006420712', N'nn', N'0362824498', N'hello1@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T16:20:20.7128002' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (656, N'KH1793006443130', N'd2', N'0362824499', NULL, NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T16:20:43.1302973' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (657, N'KH1793006564108', N'hung', N'1234567890', NULL, NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T16:22:44.1088003' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (658, N'KH1793006668440', N'd3', N'0333420699', NULL, NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T16:24:28.4408105' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (659, N'KH1793007403343', N'Khách Test Loyalty', N'0966403232', N'0966403232@pos.simplain.vn', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T16:36:43.3438921' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (660, N'KH1793007403805', N'Khách Hàng Tìm Kiếm Test', N'0355403796', N'0355403796@pos.simplain.vn', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T16:36:43.8069549' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (661, N'KH1793007980382', N'Khách Test Loyalty', N'0966980293', N'0966980293@pos.simplain.vn', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T16:46:20.3820479' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (662, N'KH1793007980854', N'Khách Hàng Tìm Kiếm Test', N'0355980849', N'0355980849@pos.simplain.vn', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T16:46:20.8549118' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (663, N'KH1793007980931', N'Khách Hàng Đã Đăng Ký', N'0988980927', N'0988980927@pos.simplain.vn', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T16:46:20.9315575' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (678, N'KH1793008083541', N'Khách Test Trung SDT', N'0977112233', N'0977112233@pos.simplain.vn', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T16:48:03.5426342' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (679, N'KHA1793010352571', N'Nguyễn Khách Hàng A', N'0981352571', N'0981352571@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:52.8829560' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (680, N'KHB1793010352571', N'Trần Khách Hàng B', N'0982352571', N'0982352571@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:53.1958664' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (681, N'KHA1793010353337', N'Nguyễn Khách Hàng A', N'0981353337', N'0981353337@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:53.3674276' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (682, N'KHB1793010353337', N'Trần Khách Hàng B', N'0982353337', N'0982353337@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:53.3732056' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (683, N'KHA1793010353535', N'Nguyễn Khách Hàng A', N'0981353535', N'0981353535@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:53.5583111' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (684, N'KHB1793010353535', N'Trần Khách Hàng B', N'0982353535', N'0982353535@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:53.5643619' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (685, N'KHA1793010354202', N'Nguyễn Khách Hàng A', N'0981354202', N'0981354202@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:54.2251960' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (686, N'KHB1793010354202', N'Trần Khách Hàng B', N'0982354202', N'0982354202@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:54.2309216' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (687, N'KHA1793010354412', N'Nguyễn Khách Hàng A', N'0981354412', N'0981354412@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:54.4362217' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (688, N'KHB1793010354412', N'Trần Khách Hàng B', N'0982354412', N'0982354412@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:54.4406313' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (689, N'KHA1793010354529', N'Nguyễn Khách Hàng A', N'0981354529', N'0981354529@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:54.5465204' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (690, N'KHB1793010354529', N'Trần Khách Hàng B', N'0982354529', N'0982354529@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:54.5507462' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (691, N'KHA1793010354663', N'Nguyễn Khách Hàng A', N'0981354663', N'0981354663@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:54.6809045' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (692, N'KHB1793010354663', N'Trần Khách Hàng B', N'0982354663', N'0982354663@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:54.6858087' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (693, N'KHA1793010354744', N'Nguyễn Khách Hàng A', N'0981354744', N'0981354744@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:54.7598750' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (694, N'KHB1793010354744', N'Trần Khách Hàng B', N'0982354744', N'0982354744@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:54.7661399' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (695, N'KHA1793010354861', N'Nguyễn Khách Hàng A', N'0981354861', N'0981354861@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:54.8766456' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (696, N'KHB1793010354861', N'Trần Khách Hàng B', N'0982354861', N'0982354861@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:25:54.8823426' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (697, N'KHA1793010549619', N'Nguyễn Khách Hàng A', N'0981549619', N'0981549619@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:09.9006366' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (698, N'KHB1793010549619', N'Trần Khách Hàng B', N'0982549619', N'0982549619@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:10.1605536' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (699, N'KHA1793010550305', N'Nguyễn Khách Hàng A', N'0981550305', N'0981550305@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:10.3288442' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (700, N'KHB1793010550305', N'Trần Khách Hàng B', N'0982550305', N'0982550305@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:10.3351919' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (701, N'KHA1793010550483', N'Nguyễn Khách Hàng A', N'0981550483', N'0981550483@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:10.5114101' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (702, N'KHB1793010550483', N'Trần Khách Hàng B', N'0982550483', N'0982550483@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:10.5176863' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (703, N'KHA1793010551147', N'Nguyễn Khách Hàng A', N'0981551147', N'0981551147@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:11.1699116' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (704, N'KHB1793010551147', N'Trần Khách Hàng B', N'0982551147', N'0982551147@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:11.1916739' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (705, N'KHA1793010551358', N'Nguyễn Khách Hàng A', N'0981551358', N'0981551358@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:11.3840345' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (706, N'KHB1793010551358', N'Trần Khách Hàng B', N'0982551358', N'0982551358@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:11.3887836' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (707, N'KHA1793010551577', N'Nguyễn Khách Hàng A', N'0981551577', N'0981551577@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:11.5992807' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (708, N'KHB1793010551577', N'Trần Khách Hàng B', N'0982551577', N'0982551577@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:11.7048106' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (709, N'KHA1793010551812', N'Nguyễn Khách Hàng A', N'0981551812', N'0981551812@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:11.8306802' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (710, N'KHB1793010551812', N'Trần Khách Hàng B', N'0982551812', N'0982551812@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:11.8356928' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (711, N'KHA1793010551905', N'Nguyễn Khách Hàng A', N'0981551905', N'0981551905@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:11.9213547' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (712, N'KHB1793010551905', N'Trần Khách Hàng B', N'0982551905', N'0982551905@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:11.9269778' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (713, N'KHA1793010552010', N'Nguyễn Khách Hàng A', N'0981552010', N'0981552010@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:12.0263120' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (714, N'KHB1793010552010', N'Trần Khách Hàng B', N'0982552010', N'0982552010@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-26T17:29:12.0309677' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (775, N'KHA1793085793967', N'Nguyễn Khách Hàng A', N'0981793967', N'0981793967@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.0865587' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (776, N'KHB1793085793967', N'Trần Khách Hàng B', N'0982793967', N'0982793967@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.0909097' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (777, N'KHA1793085794106', N'Nguyễn Khách Hàng A', N'0981794106', N'0981794106@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.1148337' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (778, N'KHB1793085794106', N'Trần Khách Hàng B', N'0982794106', N'0982794106@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.1224058' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (779, N'KHA1793085794175', N'Nguyễn Khách Hàng A', N'0981794175', N'0981794175@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.1875140' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (780, N'KHB1793085794175', N'Trần Khách Hàng B', N'0982794175', N'0982794175@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.1875140' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (781, N'KHA1793085794353', N'Nguyễn Khách Hàng A', N'0981794353', N'0981794353@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.3676807' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (782, N'KHB1793085794353', N'Trần Khách Hàng B', N'0982794353', N'0982794353@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.3740881' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (783, N'KHA1793085794535', N'Nguyễn Khách Hàng A', N'0981794535', N'0981794535@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.5482542' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (784, N'KHB1793085794535', N'Trần Khách Hàng B', N'0982794535', N'0982794535@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.5547114' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (785, N'KHA1793085794650', N'Nguyễn Khách Hàng A', N'0981794650', N'0981794650@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.6642612' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (786, N'KHB1793085794650', N'Trần Khách Hàng B', N'0982794650', N'0982794650@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.6722876' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (787, N'KHA1793085794756', N'Nguyễn Khách Hàng A', N'0981794756', N'0981794756@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.7666564' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (788, N'KHB1793085794756', N'Trần Khách Hàng B', N'0982794756', N'0982794756@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.7691690' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (789, N'KHA1793085794829', N'Nguyễn Khách Hàng A', N'0981794829', N'0981794829@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.8386662' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (790, N'KHB1793085794829', N'Trần Khách Hàng B', N'0982794829', N'0982794829@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.8432283' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (791, N'KHA1793085794924', N'Nguyễn Khách Hàng A', N'0981794924', N'0981794924@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.9357877' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (792, N'KHB1793085794924', N'Trần Khách Hàng B', N'0982794924', N'0982794924@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T14:23:14.9405036' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1449, N'KHA1793104467139', N'Nguyễn Khách Hàng A', N'0981467139', N'0981467139@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.1602309' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1450, N'KHB1793104467139', N'Trần Khách Hàng B', N'0982467139', N'0982467139@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.1640141' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1451, N'KHA1793104467176', N'Nguyễn Khách Hàng A', N'0981467176', N'0981467176@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.1857956' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1452, N'KHB1793104467176', N'Trần Khách Hàng B', N'0982467176', N'0982467176@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.1894460' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1453, N'KHA1793104467228', N'Nguyễn Khách Hàng A', N'0981467228', N'0981467228@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.2376309' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1454, N'KHB1793104467228', N'Trần Khách Hàng B', N'0982467228', N'0982467228@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.2418249' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1455, N'KHA1793104467348', N'Nguyễn Khách Hàng A', N'0981467348', N'0981467348@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.3576110' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1456, N'KHB1793104467348', N'Trần Khách Hàng B', N'0982467348', N'0982467348@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.3611614' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1457, N'KHA1793104467487', N'Nguyễn Khách Hàng A', N'0981467487', N'0981467487@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.4961848' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1458, N'KHB1793104467487', N'Trần Khách Hàng B', N'0982467487', N'0982467487@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.4994810' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1459, N'KHA1793104467576', N'Nguyễn Khách Hàng A', N'0981467576', N'0981467576@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.5851022' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1460, N'KHB1793104467576', N'Trần Khách Hàng B', N'0982467576', N'0982467576@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.5881237' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1461, N'KHA1793104467649', N'Nguyễn Khách Hàng A', N'0981467649', N'0981467649@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.6587595' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1462, N'KHB1793104467649', N'Trần Khách Hàng B', N'0982467649', N'0982467649@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.6626388' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1463, N'KHA1793104467708', N'Nguyễn Khách Hàng A', N'0981467708', N'0981467708@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.7179075' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1464, N'KHB1793104467708', N'Trần Khách Hàng B', N'0982467708', N'0982467708@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.7222423' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1465, N'KHA1793104467779', N'Nguyễn Khách Hàng A', N'0981467779', N'0981467779@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.7900285' AS DateTime2))
GO
INSERT [dbo].[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [email], [mat_khau_hash], [anh_dai_dien], [gioi_tinh], [ngay_sinh], [trang_thai], [ngay_tao]) VALUES (1466, N'KHB1793104467779', N'Trần Khách Hàng B', N'0982467779', N'0982467779@gmail.com', NULL, NULL, NULL, NULL, 1, CAST(N'2026-10-27T19:34:27.7930719' AS DateTime2))
GO
SET IDENTITY_INSERT [dbo].[khach_hang] OFF
GO
SET IDENTITY_INSERT [dbo].[kich_thuoc] ON 
GO
INSERT [dbo].[kich_thuoc] ([id], [ma_kich_thuoc], [ten_kich_thuoc], [kich_hoat]) VALUES (1, N'S', N'S', 1)
GO
INSERT [dbo].[kich_thuoc] ([id], [ma_kich_thuoc], [ten_kich_thuoc], [kich_hoat]) VALUES (2, N'M', N'M', 1)
GO
INSERT [dbo].[kich_thuoc] ([id], [ma_kich_thuoc], [ten_kich_thuoc], [kich_hoat]) VALUES (3, N'L', N'L', 1)
GO
INSERT [dbo].[kich_thuoc] ([id], [ma_kich_thuoc], [ten_kich_thuoc], [kich_hoat]) VALUES (4, N'XL', N'XL', 1)
GO
INSERT [dbo].[kich_thuoc] ([id], [ma_kich_thuoc], [ten_kich_thuoc], [kich_hoat]) VALUES (5, N'2XL', N'2XL', 1)
GO
INSERT [dbo].[kich_thuoc] ([id], [ma_kich_thuoc], [ten_kich_thuoc], [kich_hoat]) VALUES (6, N'3XL', N'3XL', 1)
GO
SET IDENTITY_INSERT [dbo].[kich_thuoc] OFF
GO
SET IDENTITY_INSERT [dbo].[lich_su_trang_thai_don_hang] ON 
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (1, 327, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-26T14:59:18.1754648' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (2, 327, 3, 1, N'Đã giao bưu tá GHN lấy hàng tại kho', CAST(N'2026-10-26T14:59:18.3747399' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (3, 328, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-26T14:59:18.5654573' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (4, 329, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-26T14:59:18.6728650' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (5, 329, 5, 1, N'Khách hàng báo hủy vì đổi ý', CAST(N'2026-10-26T14:59:18.7220263' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (6, 330, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-26T14:59:19.5242131' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (7, 330, 3, 1, N'Kho đã đóng gói và bàn giao bưu tá GHN', CAST(N'2026-10-26T15:01:10.9546864' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (8, 330, 5, 1, N'Cập nhật trạng thái từ [Đang giao hàng] sang [Đã hủy] (Mã vận đơn: GHN-SMPL-888999)', CAST(N'2026-10-26T15:23:04.3021610' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (9, 330, 4, 1, N'Cập nhật trạng thái từ [Đã hủy] sang [Hoàn thành] (Mã vận đơn: GHN-SMPL-888999)', CAST(N'2026-10-26T15:23:13.8493735' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (10, 330, 4, 1, N'Cập nhật trạng thái từ [Hoàn thành] sang [Hoàn thành] (Mã vận đơn: GHN-SMPL-888999)', CAST(N'2026-10-26T15:23:19.2555646' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (11, 331, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-26T15:33:44.6912236' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (12, 331, 3, 1, N'Đã giao bưu tá GHN lấy hàng tại kho', CAST(N'2026-10-26T15:33:44.9213332' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (13, 332, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-26T15:33:45.1590184' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (14, 333, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-26T15:33:45.2735773' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (15, 333, 2, 1, N'Xác nhận & Chuẩn bị đơn hàng', CAST(N'2026-10-26T15:33:45.3225750' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (16, 333, 3, 1, N'Bàn giao bưu tá', CAST(N'2026-10-26T15:33:45.3721985' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (17, 333, 4, 1, N'Khách đã nhận hàng thành công', CAST(N'2026-10-26T15:33:45.4164651' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (18, 334, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-26T15:33:45.4964483' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (19, 334, 5, 1, N'Khách hàng báo hủy vì đổi ý', CAST(N'2026-10-26T15:33:45.5434110' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (20, 335, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-26T15:33:46.3061503' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (21, 336, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T15:50:21.0185028' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (22, 337, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T15:51:19.0396782' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (23, 338, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T15:54:15.0838992' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (24, 339, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T15:54:43.4694445' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (25, 340, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-26T15:59:28.2917628' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (26, 341, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T16:11:27.4695387' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (27, 342, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T16:14:38.9320790' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (28, 343, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T16:14:39.9646074' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (29, 344, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T16:14:40.1752325' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (30, 345, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T16:36:42.3586466' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (31, 346, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T16:36:43.3874634' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (32, 347, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T16:36:43.6069008' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (33, 348, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T16:46:19.4556551' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (34, 349, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T16:46:20.4266585' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (35, 350, 4, 1, N'Bán hàng và thanh toán trực tiếp tại quầy thu ngân (Tiền mặt tại quầy)', CAST(N'2026-10-26T16:46:20.6282529' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (36, 353, 5, NULL, N'Khách hàng hủy đơn: Tôi muốn thay đổi địa chỉ nhận hàng (Đổi sang địa chỉ văn phòng)', CAST(N'2026-10-26T17:25:54.3656816' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (37, 354, 3, NULL, N'Đã giao cho đơn vị vận chuyển GHTK', CAST(N'2026-10-26T17:25:54.4552610' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (38, 363, 5, NULL, N'Khách hàng hủy đơn: Tôi muốn thay đổi địa chỉ nhận hàng (Đổi sang địa chỉ văn phòng)', CAST(N'2026-10-26T17:29:11.3171589' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (39, 364, 3, NULL, N'Đã giao cho đơn vị vận chuyển GHTK', CAST(N'2026-10-26T17:29:11.4047375' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (40, 340, 5, NULL, N'Khách hàng hủy đơn: Tôi muốn thay đổi sản phẩm trong đơn (màu, size, số lượng...)', CAST(N'2026-10-26T18:15:22.9302607' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (41, 371, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-27T14:01:13.5214284' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (42, 371, 3, 1, N'OK', CAST(N'2026-10-27T14:01:58.6155923' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (43, 371, 4, 1, N'Cập nhật trạng thái từ [Đang giao hàng] sang [Hoàn thành] (Mã vận đơn: SPX-23116202)', CAST(N'2026-10-27T14:02:21.4157492' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (50, 434, 5, NULL, N'Khách hàng hủy đơn: Tôi muốn thay đổi địa chỉ nhận hàng (Đổi sang địa chỉ văn phòng)', CAST(N'2026-10-27T14:23:14.4979403' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (51, 435, 3, NULL, N'Đã giao cho đơn vị vận chuyển GHTK', CAST(N'2026-10-27T14:23:14.5665267' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (66, 371, 4, NULL, N'Khách hàng đã tạo Yêu cầu Đổi/Trả online [#RMA-20261027-3651] hình thức: Trả hàng hoàn tiền. Lý do: Sản phẩm bị lỗi kỹ thuật, đường chỉ may bị bung rách dr', CAST(N'2026-10-27T16:32:30.1912439' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (67, 371, 4, 1, N'CSKH [Nguyễn Văn Admin] cập nhật trạng thái phiếu RMA #RMA-20261027-3651 sang [DA_TIEP_NHAN]. Ghi chú kiểm định: Không có', CAST(N'2026-10-27T16:33:03.2900331' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (85, 371, 4, 1, N'CSKH [Nguyễn Văn Admin] cập nhật trạng thái phiếu RMA #RMA-20261027-3651 sang [DANG_XU_LY]. Ghi chú kiểm định: Không có', CAST(N'2026-10-27T16:49:50.4426518' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (86, 371, 4, 1, N'CSKH [Nguyễn Văn Admin] cập nhật trạng thái phiếu RMA #RMA-20261027-3651 sang [DA_TIEP_NHAN]. Ghi chú kiểm định: Không có', CAST(N'2026-10-27T16:49:53.7657598' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (87, 371, 4, 1, N'CSKH [Nguyễn Văn Admin] cập nhật trạng thái phiếu RMA #RMA-20261027-3651 sang [HOAN_THANH]. Ghi chú kiểm định: Không có', CAST(N'2026-10-27T16:49:57.9804451' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (122, 632, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-27T17:21:45.5524098' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (123, 632, 3, 1, N'Cập nhật trạng thái từ [Chờ xác nhận] sang [Đang giao hàng] (Mã vận đơn: VTP-59239998)', CAST(N'2026-10-27T17:21:59.1972463' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (124, 632, 4, 1, N'Cập nhật trạng thái từ [Đang giao hàng] sang [Hoàn thành] (Mã vận đơn: VTP-59239998)', CAST(N'2026-10-27T17:22:05.0087282' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (125, 632, 6, NULL, N'Khách hàng đã gửi Yêu cầu Đổi/Trả online [#RMA-20261027-2969] hình thức: Đổi sang sản phẩm/SKU mới. Trạng thái đơn hàng chuyển sang [Đang đổi / trả]. Lý do: Giao sai màu sắc hoặc sai mẫu so với đơn đặt hàng', CAST(N'2026-10-27T17:22:54.8220431' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (164, 727, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-27T17:43:36.8382710' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (165, 727, 3, 1, N'Cập nhật trạng thái từ [Chờ xác nhận] sang [Đang giao hàng] (Mã vận đơn: SPX-81371244)', CAST(N'2026-10-27T17:44:03.1476479' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (166, 727, 4, 1, N'Cập nhật trạng thái từ [Đang giao hàng] sang [Hoàn thành] (Mã vận đơn: SPX-81371244)', CAST(N'2026-10-27T17:44:09.5709255' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (167, 727, 6, NULL, N'Khách hàng đã gửi Yêu cầu Đổi/Trả online [#RMA-20261027-5395] hình thức: Trả hàng hoàn tiền. Trạng thái đơn hàng chuyển sang [Đang đổi / trả]. Lý do: Giao sai màu sắc hoặc sai mẫu so với đơn đặt hàng', CAST(N'2026-10-27T17:45:06.5260349' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (168, 727, 6, 1, N'CSKH [Nguyễn Văn Admin] đã DUYỆT TIẾP NHẬN yêu cầu Đổi/Trả [#RMA-20261027-5395]. Hướng dẫn khách hàng gửi hàng về kho để kiểm định.', CAST(N'2026-10-27T17:45:24.1739119' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (169, 727, 6, 1, N'CSKH [Nguyễn Văn Admin] đã ghi nhận hàng về kho và ĐANG KIỂM ĐỊNH CHẤT LƯỢNG cho RMA [#RMA-20261027-5395]. Ghi chú kiểm định: [Kiểm định kho] ggggggg', CAST(N'2026-10-27T17:45:58.8559661' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (170, 727, 7, 1, N'CSKH [Nguyễn Văn Admin] đã hoàn tất Trả hàng & Hoàn tiền qua RMA [#RMA-20261027-5395]. Đơn hàng chuyển sang trạng thái [Đã trả hàng (Hoàn tiền)]. Số tiền hoàn: 192000.00 đ. Ghi chú: [Kiểm định kho] ggggggg | Mã GD hoàn tiền: hhhhhhhhhhhhhhh', CAST(N'2026-10-27T17:46:10.2322359' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (273, 975, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-27T19:16:36.5016636' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (274, 983, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-27T19:31:37.8077291' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (275, 984, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-27T19:33:02.2081210' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (276, 985, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-27T19:33:50.9861941' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (281, 1012, 5, NULL, N'Khách hàng hủy đơn: Tôi muốn thay đổi địa chỉ nhận hàng (Đổi sang địa chỉ văn phòng)', CAST(N'2026-10-27T19:34:27.4605376' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (282, 1013, 3, NULL, N'Đã giao cho đơn vị vận chuyển GHTK', CAST(N'2026-10-27T19:34:27.5077518' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (290, 1046, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-27T19:34:30.7127154' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (291, 1047, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-27T20:21:07.5444420' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (292, 1048, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-27T20:49:59.9906174' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (293, 1049, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-28T08:56:53.0413940' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (295, 1051, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-28T10:03:52.2018889' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (296, 1052, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-28T10:06:38.5719569' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (297, 1053, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-28T10:06:57.9674020' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (298, 1053, 3, 1, N'Cập nhật trạng thái từ [Chờ xác nhận] sang [Đang giao hàng] (Mã vận đơn: SPX-81371244)', CAST(N'2026-10-28T10:10:37.0382019' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (299, 1053, 4, 1, N'Cập nhật trạng thái từ [Đang giao hàng] sang [Hoàn thành] (Mã vận đơn: SPX-81371244)', CAST(N'2026-10-28T10:11:38.0284630' AS DateTime2))
GO
INSERT [dbo].[lich_su_trang_thai_don_hang] ([id], [ma_don_hang_id], [ma_trang_thai_moi_id], [nguoi_thuc_hien_id], [ghi_chu], [ngay_chuyen]) VALUES (300, 1054, 1, NULL, N'Khởi tạo đơn hàng từ Checkout Storefront', CAST(N'2026-10-28T12:59:24.9742977' AS DateTime2))
GO
SET IDENTITY_INSERT [dbo].[lich_su_trang_thai_don_hang] OFF
GO
SET IDENTITY_INSERT [dbo].[ma_giam_gia] ON 
GO
INSERT [dbo].[ma_giam_gia] ([id], [ma_code], [ten_voucher], [loai_giam_gia], [gia_tri_giam], [giam_toi_da], [gia_tri_don_toi_thieu], [tong_so_luong], [so_luong_da_dung], [gioi_han_moi_user], [ngay_bat_dau], [ngay_ket_thuc], [kich_hoat], [ngan_sach], [ngan_sach_da_dung], [mo_ta], [ngay_tao]) VALUES (1, N'COOL20', N'Giảm 20% tối đa 40K', N'PERCENT', CAST(20.00 AS Decimal(18, 2)), CAST(40000.00 AS Decimal(18, 2)), CAST(200000.00 AS Decimal(18, 2)), 500, 45, 1, CAST(N'2025-01-01T00:00:00.0000000' AS DateTime2), CAST(N'2028-12-31T23:59:59.0000000' AS DateTime2), 1, NULL, CAST(0.00 AS Decimal(18, 2)), NULL, NULL)
GO
INSERT [dbo].[ma_giam_gia] ([id], [ma_code], [ten_voucher], [loai_giam_gia], [gia_tri_giam], [giam_toi_da], [gia_tri_don_toi_thieu], [tong_so_luong], [so_luong_da_dung], [gioi_han_moi_user], [ngay_bat_dau], [ngay_ket_thuc], [kich_hoat], [ngan_sach], [ngan_sach_da_dung], [mo_ta], [ngay_tao]) VALUES (2, N'FREESHIP30K', N'Miễn phí vận chuyển 30K', N'FIXED_AMOUNT', CAST(30000.00 AS Decimal(18, 2)), NULL, CAST(0.00 AS Decimal(18, 2)), 2000, 120, 2, CAST(N'2025-01-01T00:00:00.0000000' AS DateTime2), CAST(N'2028-12-31T23:59:59.0000000' AS DateTime2), 1, NULL, CAST(0.00 AS Decimal(18, 2)), NULL, NULL)
GO
INSERT [dbo].[ma_giam_gia] ([id], [ma_code], [ten_voucher], [loai_giam_gia], [gia_tri_giam], [giam_toi_da], [gia_tri_don_toi_thieu], [tong_so_luong], [so_luong_da_dung], [gioi_han_moi_user], [ngay_bat_dau], [ngay_ket_thuc], [kich_hoat], [ngan_sach], [ngan_sach_da_dung], [mo_ta], [ngay_tao]) VALUES (73, N'SMPL10-TVM8', N'kkkkkkkkkkkkkkkkkkk', N'PERCENT', CAST(10.00 AS Decimal(18, 2)), CAST(50000.00 AS Decimal(18, 2)), CAST(200000.00 AS Decimal(18, 2)), NULL, 1, 1, CAST(N'2026-10-27T18:34:00.0000000' AS DateTime2), CAST(N'2026-11-26T23:59:00.0000000' AS DateTime2), 1, CAST(501000.00 AS Decimal(18, 2)), CAST(25000.00 AS Decimal(18, 2)), N'', CAST(N'2026-10-27T18:36:12.2236818' AS DateTime2))
GO
SET IDENTITY_INSERT [dbo].[ma_giam_gia] OFF
GO
SET IDENTITY_INSERT [dbo].[mau_sac] ON 
GO
INSERT [dbo].[mau_sac] ([id], [ma_mau], [ten_mau], [kich_hoat]) VALUES (1, N'#000000', N'Đen', 1)
GO
INSERT [dbo].[mau_sac] ([id], [ma_mau], [ten_mau], [kich_hoat]) VALUES (2, N'#FFFFFF', N'Trắng', 1)
GO
INSERT [dbo].[mau_sac] ([id], [ma_mau], [ten_mau], [kich_hoat]) VALUES (3, N'#1E3A8A', N'Xanh Navy', 1)
GO
INSERT [dbo].[mau_sac] ([id], [ma_mau], [ten_mau], [kich_hoat]) VALUES (4, N'#4B5563', N'Xám Tiêu', 1)
GO
INSERT [dbo].[mau_sac] ([id], [ma_mau], [ten_mau], [kich_hoat]) VALUES (5, N'#065F46', N'Xanh Rêu', 1)
GO
INSERT [dbo].[mau_sac] ([id], [ma_mau], [ten_mau], [kich_hoat]) VALUES (6, N'#7F1D1D', N'Đỏ Đô', 1)
GO
SET IDENTITY_INSERT [dbo].[mau_sac] OFF
GO
SET IDENTITY_INSERT [dbo].[nhan_vien] ON 
GO
INSERT [dbo].[nhan_vien] ([id], [ma_nhan_vien], [ho_ten], [ten_dang_nhap], [mat_khau_hash], [so_dien_thoai], [email], [ma_vai_tro_id], [trang_thai], [ngay_tao]) VALUES (1, N'NV001', N'Nguyễn Văn Admin', N'superadmin', N'hashed_pass_123', NULL, NULL, 1, 1, CAST(N'2026-09-26T19:19:49.0700000' AS DateTime2))
GO
INSERT [dbo].[nhan_vien] ([id], [ma_nhan_vien], [ho_ten], [ten_dang_nhap], [mat_khau_hash], [so_dien_thoai], [email], [ma_vai_tro_id], [trang_thai], [ngay_tao]) VALUES (2, N'NV002', N'Lê CSKH', N'cskh_01', N'hashed_pass_456', NULL, N'nguyendhbth04829@gmail.com', 2, 1, CAST(N'2026-09-26T19:19:49.0700000' AS DateTime2))
GO
INSERT [dbo].[nhan_vien] ([id], [ma_nhan_vien], [ho_ten], [ten_dang_nhap], [mat_khau_hash], [so_dien_thoai], [email], [ma_vai_tro_id], [trang_thai], [ngay_tao]) VALUES (559, N'BH1', N'baN HANG', N'bh1', N'$2a$10$HCG7WV/n9dkAU3ha4HtOwO4YlxiqLU4A9duI5l/0/INjlIyOHqoa2', N'0292928225', N'bh1@gmail.com', 3, 1, CAST(N'2026-10-28T17:41:23.8520837' AS DateTime2))
GO
SET IDENTITY_INSERT [dbo].[nhan_vien] OFF
GO
SET IDENTITY_INSERT [dbo].[phuong_thuc_thanh_toan] ON 
GO
INSERT [dbo].[phuong_thuc_thanh_toan] ([id], [ma_phuong_thuc], [ten_phuong_thuc], [la_truc_tuyen], [kich_hoat], [mo_ta], [bieu_tuong], [cau_hinh]) VALUES (1, N'COD', N'Thanh toán khi nhận hàng (COD)', 0, 1, N'Khách hàng kiểm tra sản phẩm và thanh toán tiền mặt trực tiếp cho nhân viên giao hàng.', N'fa-solid fa-money-bill-wave text-emerald-600', N'{"loai": "TIEN_MAT", "phi_thu_ho": 0, "cho_kiem_hang": true}')
GO
INSERT [dbo].[phuong_thuc_thanh_toan] ([id], [ma_phuong_thuc], [ten_phuong_thuc], [la_truc_tuyen], [kich_hoat], [mo_ta], [bieu_tuong], [cau_hinh]) VALUES (2, N'VNPAY_QR', N'Cổng thanh toán VNPay (VNPAY-QR)', 1, 1, N'Quét mã QR thanh toán qua ứng dụng ngân hàng hoặc thẻ ATM nội địa, Visa, Mastercard.', N'fa-solid fa-qrcode text-blue-600', N'{"tmn_code": "SIMPLAIN01", "hash_secret": "RAI9EFCJ891283JKSDH21", "url": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html", "sandbox": true}')
GO
INSERT [dbo].[phuong_thuc_thanh_toan] ([id], [ma_phuong_thuc], [ten_phuong_thuc], [la_truc_tuyen], [kich_hoat], [mo_ta], [bieu_tuong], [cau_hinh]) VALUES (3, N'MOMO_WALLET', N'Ví điện tử MoMo', 1, 1, N'Thanh toán trực tuyến một chạm siêu tốc qua ứng dụng ví điện tử MoMo.', N'fa-solid fa-wallet text-pink-600', N'{"partner_code": "MOMOSIMPLAIN", "access_key": "KL98394821", "sandbox": true}')
GO
INSERT [dbo].[phuong_thuc_thanh_toan] ([id], [ma_phuong_thuc], [ten_phuong_thuc], [la_truc_tuyen], [kich_hoat], [mo_ta], [bieu_tuong], [cau_hinh]) VALUES (129, N'HELLO', N'hello1', 1, 1, N'123', N'/uploads/payment_1792936100130_8ec134.png', N'')
GO
INSERT [dbo].[phuong_thuc_thanh_toan] ([id], [ma_phuong_thuc], [ten_phuong_thuc], [la_truc_tuyen], [kich_hoat], [mo_ta], [bieu_tuong], [cau_hinh]) VALUES (149, N'CHECKOUT_COD_TEST', N'Thanh toán Checkout Test', 0, 1, N'Cổng thanh toán test', N'fa-solid fa-money-bill', NULL)
GO
INSERT [dbo].[phuong_thuc_thanh_toan] ([id], [ma_phuong_thuc], [ten_phuong_thuc], [la_truc_tuyen], [kich_hoat], [mo_ta], [bieu_tuong], [cau_hinh]) VALUES (226, N'TIEN_MAT', N'Tiền mặt tại quầy', 0, 1, N'Khách hàng thanh toán trực tiếp bằng tiền mặt tại quầy thu ngân', N'fa-money-bill-wave', NULL)
GO
INSERT [dbo].[phuong_thuc_thanh_toan] ([id], [ma_phuong_thuc], [ten_phuong_thuc], [la_truc_tuyen], [kich_hoat], [mo_ta], [bieu_tuong], [cau_hinh]) VALUES (227, N'CHUYEN_KHOAN', N'Chuyển khoản ngân hàng / Quét QR', 1, 1, N'Chuyển khoản trực tiếp qua ngân hàng hoặc quét mã QR quầy thu ngân', N'fa-qrcode', NULL)
GO
SET IDENTITY_INSERT [dbo].[phuong_thuc_thanh_toan] OFF
GO
SET IDENTITY_INSERT [dbo].[san_pham] ON 
GO
INSERT [dbo].[san_pham] ([id], [ma_danh_muc_id], [ma_bo_suu_tap_id], [ma_san_pham], [ten_san_pham], [duong_dan_slug], [chat_lieu], [form_dang], [mo_ta_chi_tiet], [huong_dan_chon_size], [gia_co_ban], [noi_bat], [kich_hoat], [ngay_tao], [ma_chat_lieu_id], [ma_form_dang_id], [video_url]) VALUES (1, 17, 1, N'SP-POLO-EXCOOL-01', N'Áo Polo Nam Excool Công Nghệ Làm Mát', N'ao-polo-nam-excool-cong-nghe-lam-mat', N'Vải Excool Công nghệ Làm Mát', N'Regular Fit', N'Áo Polo nam ứng dụng sợi Sorona co giãn 4 chiều, chống tia UV UPF 50+, thoáng khí và nhanh khô gấp 2 lần cotton thường.', N'Size S (48-58kg) | Size M (59-67kg) | Size L (68-75kg) | Size XL (76-83kg) | Size 2XL (84-92kg)', CAST(299000.00 AS Decimal(18, 2)), 1, 1, CAST(N'2026-10-17T21:05:43.0500000' AS DateTime2), 1, 1, N'https://www.bing.com/videos/riverview/relatedvideo?q=video+qu%e1%ba%a3n+c%c3%a1og&&mid=3C0619E1CBA1627F49573C0619E1CBA1627F4957&churl=https%3a%2f%2fwww.youtube.com%2fchannel%2fUCmezsOViRkEFB4rcTtyvgkw&FORM=VRDGAR')
GO
INSERT [dbo].[san_pham] ([id], [ma_danh_muc_id], [ma_bo_suu_tap_id], [ma_san_pham], [ten_san_pham], [duong_dan_slug], [chat_lieu], [form_dang], [mo_ta_chi_tiet], [huong_dan_chon_size], [gia_co_ban], [noi_bat], [kich_hoat], [ngay_tao], [ma_chat_lieu_id], [ma_form_dang_id], [video_url]) VALUES (2, 18, 1, N'SP-TSHIRT-COMPACT-02', N'Áo Thun Nam Cotton Compact 220gsm', N'ao-thun-nam-cotton-compact-220gsm', N'100% Cotton Compact 2 chiều', N'Relaxed Fit', N'Áo thun trơn Basic SIMPLAIN STORE dệt từ sợi Cotton Compact hạn chế xù lông, bề mặt vải mịn màng và giữ form cổ áo bền bỉ.', N'Size S (48-58kg) | Size M (59-67kg) | Size L (68-75kg) | Size XL (76-83kg)', CAST(199000.00 AS Decimal(18, 2)), 1, 1, CAST(N'2026-10-17T21:05:43.0500000' AS DateTime2), 2, 1, N'https://www.bing.com/videos/riverview/relatedvideo?q=video+qu%e1%ba%a3n+c%c3%a1og&&mid=EB4A24A9DB0DEF31B222EB4A24A9DB0DEF31B222&churl=https%3a%2f%2fwww.tiktok.com%2f%40lucaimkt&mmscn=stvo&FORM=VRDGAR')
GO
INSERT [dbo].[san_pham] ([id], [ma_danh_muc_id], [ma_bo_suu_tap_id], [ma_san_pham], [ten_san_pham], [duong_dan_slug], [chat_lieu], [form_dang], [mo_ta_chi_tiet], [huong_dan_chon_size], [gia_co_ban], [noi_bat], [kich_hoat], [ngay_tao], [ma_chat_lieu_id], [ma_form_dang_id], [video_url]) VALUES (3, 17, NULL, N'SP-POLO-CAFE-03', N'Áo Polo Nam Sợi Café Khử Mùi Tối Ưu', N'ao-polo-nam-soi-cafe-khu-mui-toi-uu', N'Vải Café Khử Mùi Phối Spandex', N'Slim Fit', N'Chất liệu vải làm từ bã cà phê tái chế thân thiện môi trường, kiểm soát mùi cơ thể vượt trội và siêu nhẹ.', N'Size M (59-67kg) | Size L (68-75kg) | Size XL (76-83kg)', CAST(329000.00 AS Decimal(18, 2)), 0, 1, CAST(N'2026-10-17T21:05:43.0500000' AS DateTime2), 3, 2, N'/uploads/video_1792936077276_bcfbd4b8.mp4')
GO
INSERT [dbo].[san_pham] ([id], [ma_danh_muc_id], [ma_bo_suu_tap_id], [ma_san_pham], [ten_san_pham], [duong_dan_slug], [chat_lieu], [form_dang], [mo_ta_chi_tiet], [huong_dan_chon_size], [gia_co_ban], [noi_bat], [kich_hoat], [ngay_tao], [ma_chat_lieu_id], [ma_form_dang_id], [video_url]) VALUES (1065, 1, NULL, N'SP-SIMPLAIN-478', N'Iphone', N'iphone', N'100% Cotton Compact 2 chiều', N'Boxy Fit', N'1235555555555555', NULL, CAST(0.00 AS Decimal(18, 2)), 0, 1, CAST(N'2026-10-25T21:17:13.0819483' AS DateTime2), 9, 10, NULL)
GO
INSERT [dbo].[san_pham] ([id], [ma_danh_muc_id], [ma_bo_suu_tap_id], [ma_san_pham], [ten_san_pham], [duong_dan_slug], [chat_lieu], [form_dang], [mo_ta_chi_tiet], [huong_dan_chon_size], [gia_co_ban], [noi_bat], [kich_hoat], [ngay_tao], [ma_chat_lieu_id], [ma_form_dang_id], [video_url]) VALUES (1066, 1, NULL, N'SP-SIMPLAIN-268', N'iPhone 16 (256GB) - Chính hãng VN/A', N'iphone-chinh-hang-vna', N'100% Cotton Compact 2 chiều', N'Boxy Fit', N'1235555555555555', NULL, CAST(33333.00 AS Decimal(18, 2)), 0, 1, CAST(N'2026-10-25T21:20:46.4933492' AS DateTime2), 9, 10, NULL)
GO
INSERT [dbo].[san_pham] ([id], [ma_danh_muc_id], [ma_bo_suu_tap_id], [ma_san_pham], [ten_san_pham], [duong_dan_slug], [chat_lieu], [form_dang], [mo_ta_chi_tiet], [huong_dan_chon_size], [gia_co_ban], [noi_bat], [kich_hoat], [ngay_tao], [ma_chat_lieu_id], [ma_form_dang_id], [video_url]) VALUES (1067, 1, NULL, N'SP-SIMPLAIN-899', N'ggggg', N'ggggg', N'100% Cotton Compact 2 chiều', N'Boxy Fit', N'123gggggggggggggggggggg', NULL, CAST(12333.00 AS Decimal(18, 2)), 0, 1, CAST(N'2026-10-25T21:23:22.2290172' AS DateTime2), 9, 10, NULL)
GO
SET IDENTITY_INSERT [dbo].[san_pham] OFF
GO
INSERT [dbo].[san_pham_trong_combo] ([ma_combo_id], [ma_san_pham_id]) VALUES (1, 1)
GO
INSERT [dbo].[san_pham_trong_combo] ([ma_combo_id], [ma_san_pham_id]) VALUES (1, 2)
GO
INSERT [dbo].[san_pham_trong_combo] ([ma_combo_id], [ma_san_pham_id]) VALUES (1, 3)
GO
INSERT [dbo].[san_pham_trong_combo] ([ma_combo_id], [ma_san_pham_id]) VALUES (185, 1)
GO
INSERT [dbo].[san_pham_trong_combo] ([ma_combo_id], [ma_san_pham_id]) VALUES (185, 2)
GO
INSERT [dbo].[san_pham_trong_combo] ([ma_combo_id], [ma_san_pham_id]) VALUES (185, 3)
GO
SET IDENTITY_INSERT [dbo].[san_pham_yeu_thich] ON 
GO
INSERT [dbo].[san_pham_yeu_thich] ([id], [ma_khach_hang_id], [ma_san_pham_id], [ngay_them]) VALUES (32, 14, 1066, CAST(N'2026-10-28T09:57:38.0230930' AS DateTime2))
GO
INSERT [dbo].[san_pham_yeu_thich] ([id], [ma_khach_hang_id], [ma_san_pham_id], [ngay_them]) VALUES (33, 14, 2, CAST(N'2026-10-28T10:00:34.3645361' AS DateTime2))
GO
SET IDENTITY_INSERT [dbo].[san_pham_yeu_thich] OFF
GO
SET IDENTITY_INSERT [dbo].[trang_thai_don_hang] ON 
GO
INSERT [dbo].[trang_thai_don_hang] ([id], [ma_trang_thai], [ten_trang_thai], [ma_mau_hex]) VALUES (1, N'CHO_XAC_NHAN', N'Chờ xác nhận', N'#F59E0B')
GO
INSERT [dbo].[trang_thai_don_hang] ([id], [ma_trang_thai], [ten_trang_thai], [ma_mau_hex]) VALUES (2, N'DANG_XU_LY', N'Đang xử lý', N'#3B82F6')
GO
INSERT [dbo].[trang_thai_don_hang] ([id], [ma_trang_thai], [ten_trang_thai], [ma_mau_hex]) VALUES (3, N'DANG_GIAO', N'Đang giao hàng', N'#8B5CF6')
GO
INSERT [dbo].[trang_thai_don_hang] ([id], [ma_trang_thai], [ten_trang_thai], [ma_mau_hex]) VALUES (4, N'HOAN_THANH', N'Hoàn thành', N'#10B981')
GO
INSERT [dbo].[trang_thai_don_hang] ([id], [ma_trang_thai], [ten_trang_thai], [ma_mau_hex]) VALUES (5, N'DA_HUY', N'Đã hủy', N'#EF4444')
GO
INSERT [dbo].[trang_thai_don_hang] ([id], [ma_trang_thai], [ten_trang_thai], [ma_mau_hex]) VALUES (6, N'DANG_DOI_TRA', N'Đang đổi / trả', N'#D97706')
GO
INSERT [dbo].[trang_thai_don_hang] ([id], [ma_trang_thai], [ten_trang_thai], [ma_mau_hex]) VALUES (7, N'DA_TRA_HANG', N'Đã trả hàng (Hoàn tiền)', N'#E11D48')
GO
INSERT [dbo].[trang_thai_don_hang] ([id], [ma_trang_thai], [ten_trang_thai], [ma_mau_hex]) VALUES (8, N'DA_DOI_HANG', N'Đã đổi hàng', N'#2563EB')
GO
SET IDENTITY_INSERT [dbo].[trang_thai_don_hang] OFF
GO
SET IDENTITY_INSERT [dbo].[vai_tro] ON 
GO
INSERT [dbo].[vai_tro] ([id], [ma_vai_tro], [ten_vai_tro], [mo_ta], [trang_thai]) VALUES (1, N'ADMIN', N'Quản trị viên tối cao', N'Toàn quyền cấu hình hệ thống', 1)
GO
INSERT [dbo].[vai_tro] ([id], [ma_vai_tro], [ten_vai_tro], [mo_ta], [trang_thai]) VALUES (2, N'CSKH', N'Chăm sóc khách hàng', N'Duyệt yêu cầu đổi trả và hỗ trợ', 1)
GO
INSERT [dbo].[vai_tro] ([id], [ma_vai_tro], [ten_vai_tro], [mo_ta], [trang_thai]) VALUES (3, N'BAN_HANG', N'Thu ngân tại quầy', N'Tạo đơn tại cửa hàng', 1)
GO
SET IDENTITY_INSERT [dbo].[vai_tro] OFF
GO
SET IDENTITY_INSERT [dbo].[vai_tro_quyen_han] ON 
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (3, 1, N'BAN_HANG_POS')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (1, 1, N'DASHBOARD')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (5, 1, N'DOI_TRA')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (4, 1, N'DON_HANG')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (10, 1, N'HE_THONG')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (9, 1, N'KHACH_HANG')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (6, 1, N'KHUYEN_MAI')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (8, 1, N'NOI_DUNG')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (2, 1, N'SAN_PHAM')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (7, 1, N'THANH_TOAN')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (11, 2, N'DASHBOARD')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (12, 2, N'DOI_TRA')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (13, 2, N'DON_HANG')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (14, 2, N'KHACH_HANG')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (15, 2, N'SAN_PHAM')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (17, 3, N'BAN_HANG_POS')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (16, 3, N'DASHBOARD')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (18, 3, N'DON_HANG')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (20, 3, N'KHUYEN_MAI')
GO
INSERT [dbo].[vai_tro_quyen_han] ([id], [ma_vai_tro_id], [ma_quyen]) VALUES (19, 3, N'SAN_PHAM')
GO
SET IDENTITY_INSERT [dbo].[vai_tro_quyen_han] OFF
GO
SET IDENTITY_INSERT [dbo].[video_san_pham] ON 
GO
INSERT [dbo].[video_san_pham] ([id], [ma_san_pham_id], [tieu_de], [video_url], [mo_ta], [kich_hoat], [ngay_tao], [ngay_cap_nhat]) VALUES (3, 3, N'Khử mùi tự nhiên cùng sợi Cafe S.Café chống tia UV', N'/uploads/video_1792936077276_bcfbd4b8.mp4', N'Kiểm nghiệm khả năng thấm hút tức thì, ngăn ngừa mùi hôi cơ thể và cảm giác nhẹ tênh khi di chuyển ngoài trời', 1, CAST(N'2026-10-24T23:06:21.0566667' AS DateTime2), CAST(N'2026-10-25T20:47:57.3035863' AS DateTime2))
GO
INSERT [dbo].[video_san_pham] ([id], [ma_san_pham_id], [tieu_de], [video_url], [mo_ta], [kich_hoat], [ngay_tao], [ngay_cap_nhat]) VALUES (12, 1, N'Nike Air Force', N'https://www.bing.com/videos/riverview/relatedvideo?q=video+qu%e1%ba%a3n+c%c3%a1og&&mid=3C0619E1CBA1627F49573C0619E1CBA1627F4957&churl=https%3a%2f%2fwww.youtube.com%2fchannel%2fUCmezsOViRkEFB4rcTtyvgkw&FORM=VRDGAR', N'qc', 1, CAST(N'2026-10-25T08:29:35.6170636' AS DateTime2), CAST(N'2026-10-25T08:49:15.2531129' AS DateTime2))
GO
INSERT [dbo].[video_san_pham] ([id], [ma_san_pham_id], [tieu_de], [video_url], [mo_ta], [kich_hoat], [ngay_tao], [ngay_cap_nhat]) VALUES (25, 2, N'Nike Air Force2', N'https://www.bing.com/videos/riverview/relatedvideo?q=video+qu%e1%ba%a3n+c%c3%a1og&&mid=EB4A24A9DB0DEF31B222EB4A24A9DB0DEF31B222&churl=https%3a%2f%2fwww.tiktok.com%2f%40lucaimkt&mmscn=stvo&FORM=VRDGAR', N'kkkk', 1, CAST(N'2026-10-25T08:50:16.8867908' AS DateTime2), NULL)
GO
SET IDENTITY_INSERT [dbo].[video_san_pham] OFF
GO
SET IDENTITY_INSERT [dbo].[voucher_so_huu] ON 
GO
INSERT [dbo].[voucher_so_huu] ([id], [ma_khach_hang_id], [ma_giam_gia_id], [da_su_dung], [ngay_nhan], [ngay_su_dung]) VALUES (1, 1, 1, 0, CAST(N'2026-09-26T19:19:49.1200000' AS DateTime2), NULL)
GO
INSERT [dbo].[voucher_so_huu] ([id], [ma_khach_hang_id], [ma_giam_gia_id], [da_su_dung], [ngay_nhan], [ngay_su_dung]) VALUES (22, 14, 73, 1, CAST(N'2026-10-27T19:14:38.2737942' AS DateTime2), CAST(N'2026-10-27T19:16:36.5819233' AS DateTime2))
GO
INSERT [dbo].[voucher_so_huu] ([id], [ma_khach_hang_id], [ma_giam_gia_id], [da_su_dung], [ngay_nhan], [ngay_su_dung]) VALUES (39, 14, 1, 0, CAST(N'2026-10-28T10:03:28.8601110' AS DateTime2), NULL)
GO
INSERT [dbo].[voucher_so_huu] ([id], [ma_khach_hang_id], [ma_giam_gia_id], [da_su_dung], [ngay_nhan], [ngay_su_dung]) VALUES (40, 14, 2, 0, CAST(N'2026-10-28T10:03:32.1865785' AS DateTime2), NULL)
GO
SET IDENTITY_INSERT [dbo].[voucher_so_huu] OFF
GO
SET IDENTITY_INSERT [dbo].[yeu_cau_doi_tra] ON 
GO
INSERT [dbo].[yeu_cau_doi_tra] ([id], [ma_yeu_cau_rma], [ma_don_hang_id], [ma_khach_hang_id], [loai_doi_tra], [ly_do], [so_tien_hoan], [thong_tin_ngan_hang], [trang_thai], [nguoi_duyet_id], [ngay_tao], [hinh_anh_minh_chung], [ghi_chu_admin], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_nhan_hang], [ghi_chu_giao_hang], [hinh_anh_bill]) VALUES (62, N'RMA-20261027-3651', 371, 14, N'TRA_HANG_HOAN_TIEN', N'Sản phẩm bị lỗi kỹ thuật, đường chỉ may bị bung rách dr', CAST(192000.00 AS Decimal(18, 2)), N'Vietcombank (VCB) - STK: 0368284499 - Chủ TK: DUY KHANH JJ8', N'HOAN_THANH', 1, CAST(N'2026-10-27T16:32:30.1213414' AS DateTime2), N'https://www.bing.com/videos/riverview/relatedvideo?q=vdeo&&mid=37F56550928CC0FD4B2237F56550928CC0FD4B22&churl=https%3a%2f%2fwww.youtube.com%2fchannel%2fUCGvGMmyEWp9hxDqmfLJbpkQ&mmscn=stvo&FORM=VRDGAR', NULL, NULL, NULL, NULL, NULL, NULL)
GO
INSERT [dbo].[yeu_cau_doi_tra] ([id], [ma_yeu_cau_rma], [ma_don_hang_id], [ma_khach_hang_id], [loai_doi_tra], [ly_do], [so_tien_hoan], [thong_tin_ngan_hang], [trang_thai], [nguoi_duyet_id], [ngay_tao], [hinh_anh_minh_chung], [ghi_chu_admin], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_nhan_hang], [ghi_chu_giao_hang], [hinh_anh_bill]) VALUES (184, N'RMA-20261027-2969', 632, 14, N'DOI_HANG', N'Giao sai màu sắc hoặc sai mẫu so với đơn đặt hàng', CAST(0.00 AS Decimal(18, 2)), NULL, N'HOAN_THANH', 1, CAST(N'2026-10-27T17:22:54.8017073' AS DateTime2), N'/uploads/rma/rma_1793096574775_5d742670.webp', NULL, N'Nguyeen Nguyene', N'0983823668', N'123, Tam Hiệp, Biên Hòa, Đồng Nai', NULL, NULL)
GO
INSERT [dbo].[yeu_cau_doi_tra] ([id], [ma_yeu_cau_rma], [ma_don_hang_id], [ma_khach_hang_id], [loai_doi_tra], [ly_do], [so_tien_hoan], [thong_tin_ngan_hang], [trang_thai], [nguoi_duyet_id], [ngay_tao], [hinh_anh_minh_chung], [ghi_chu_admin], [ten_nguoi_nhan], [so_dien_thoai_nhan], [dia_chi_nhan_hang], [ghi_chu_giao_hang], [hinh_anh_bill]) VALUES (275, N'RMA-20261027-5395', 727, 14, N'TRA_HANG_HOAN_TIEN', N'Giao sai màu sắc hoặc sai mẫu so với đơn đặt hàng', CAST(192000.00 AS Decimal(18, 2)), N'Vietcombank (VCB) - STK: 0368284499 - Chủ TK: DUY KHANH JJ8', N'HOAN_THANH', 1, CAST(N'2026-10-27T17:45:06.5031651' AS DateTime2), N'/uploads/rma/rma_1793097906492_c4a0151e.webp', N'[Kiểm định kho] ggggggg | Mã GD hoàn tiền: hhhhhhhhhhhhhhh', N'Nguyeen Nguyene', N'0983823668', N'123, Tam Hiệp, Biên Hòa, Đồng Nai', NULL, NULL)
GO
SET IDENTITY_INSERT [dbo].[yeu_cau_doi_tra] OFF
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__bai_viet__3E243EFFB3B091C9]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[bai_viet_blog] ADD UNIQUE NONCLUSTERED 
(
	[duong_dan_slug] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__bien_the__085E7884CEB1D96E]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[bien_the_san_pham] ADD UNIQUE NONCLUSTERED 
(
	[ma_sku] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__bien_the__52AEBD0A655E21DA]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[bien_the_san_pham] ADD UNIQUE NONCLUSTERED 
(
	[ma_vach_barcode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__bo_suu_t__3E243EFF375460FB]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[bo_suu_tap] ADD UNIQUE NONCLUSTERED 
(
	[duong_dan_slug] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__chat_lie__47FA28725040AD06]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[chat_lieu] ADD UNIQUE NONCLUSTERED 
(
	[ten_chat_lieu] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [chi_tiet_flash_sale_index_2]    Script Date: 28/10/2026 6:37:10 CH ******/
CREATE UNIQUE NONCLUSTERED INDEX [chi_tiet_flash_sale_index_2] ON [dbo].[chi_tiet_flash_sale]
(
	[ma_flash_sale_id] ASC,
	[ma_bien_the_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [danh_gia_san_pham_index_3]    Script Date: 28/10/2026 6:37:10 CH ******/
CREATE UNIQUE NONCLUSTERED INDEX [danh_gia_san_pham_index_3] ON [dbo].[danh_gia_san_pham]
(
	[ma_chi_tiet_don_hang_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__danh_muc__3E243EFF403FF633]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[danh_muc] ADD UNIQUE NONCLUSTERED 
(
	[duong_dan_slug] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__don_hang__0246C5EB1876FBF6]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[don_hang] ADD UNIQUE NONCLUSTERED 
(
	[ma_don_hang] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__form_dan__6AFB67515EDA29E3]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[form_dang] ADD UNIQUE NONCLUSTERED 
(
	[ten_form_dang] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__giao_dic__C7EE547EEFDCEA36]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[giao_dich_thanh_toan] ADD UNIQUE NONCLUSTERED 
(
	[ma_giao_dich_noi_bo] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [UQ_gio_hang_ma_khach_hang_id]    Script Date: 28/10/2026 6:37:10 CH ******/
CREATE UNIQUE NONCLUSTERED INDEX [UQ_gio_hang_ma_khach_hang_id] ON [dbo].[gio_hang]
(
	[ma_khach_hang_id] ASC
)
WHERE ([ma_khach_hang_id] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__goi_comb__2286B6007F27A497]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[goi_combo] ADD UNIQUE NONCLUSTERED 
(
	[ma_combo] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__goi_comb__3E243EFF7FBEB373]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[goi_combo] ADD UNIQUE NONCLUSTERED 
(
	[duong_dan_slug] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__hang_tha__340B96FD709641B1]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[hang_thanh_vien] ADD UNIQUE NONCLUSTERED 
(
	[ten_hang] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__hoa_don___DBE2D9E27FA9CD66]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[hoa_don_cho] ADD UNIQUE NONCLUSTERED 
(
	[ma_hoa_don] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__khach_ha__BD03D94C50F38A0F]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[khach_hang] ADD UNIQUE NONCLUSTERED 
(
	[so_dien_thoai] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__khach_ha__C9817AF7664C959A]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[khach_hang] ADD UNIQUE NONCLUSTERED 
(
	[ma_khach_hang] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ_khach_hang_email_filtered]    Script Date: 28/10/2026 6:37:10 CH ******/
CREATE UNIQUE NONCLUSTERED INDEX [UQ_khach_hang_email_filtered] ON [dbo].[khach_hang]
(
	[email] ASC
)
WHERE ([email] IS NOT NULL AND [email]<>'')
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__kich_thu__8C3E646FADA1FC80]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[kich_thuoc] ADD UNIQUE NONCLUSTERED 
(
	[ma_kich_thuoc] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__ma_giam___7749BF8F3C36AF59]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[ma_giam_gia] ADD UNIQUE NONCLUSTERED 
(
	[ma_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__mau_sac__0BC941DA1729AF3C]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[mau_sac] ADD UNIQUE NONCLUSTERED 
(
	[ma_mau] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__nhan_vie__363698B39D96E742]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[nhan_vien] ADD UNIQUE NONCLUSTERED 
(
	[ten_dang_nhap] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__nhan_vie__6781B7B8BF2D0FD4]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[nhan_vien] ADD UNIQUE NONCLUSTERED 
(
	[ma_nhan_vien] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__phuong_t__2B9F12F89BA41F7E]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[phuong_thuc_thanh_toan] ADD UNIQUE NONCLUSTERED 
(
	[ma_phuong_thuc] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__san_pham__3E243EFFA261A532]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[san_pham] ADD UNIQUE NONCLUSTERED 
(
	[duong_dan_slug] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__san_pham__9D25990D136B68D9]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[san_pham] ADD UNIQUE NONCLUSTERED 
(
	[ma_san_pham] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [san_pham_yeu_thich_index_0]    Script Date: 28/10/2026 6:37:10 CH ******/
CREATE UNIQUE NONCLUSTERED INDEX [san_pham_yeu_thich_index_0] ON [dbo].[san_pham_yeu_thich]
(
	[ma_khach_hang_id] ASC,
	[ma_san_pham_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__trang_th__5C69DAA91B6459AF]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[trang_thai_don_hang] ADD UNIQUE NONCLUSTERED 
(
	[ma_trang_thai] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__vai_tro__4AE1754CC4389792]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[vai_tro] ADD UNIQUE NONCLUSTERED 
(
	[ma_vai_tro] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ_vai_tro_quyen]    Script Date: 28/10/2026 6:37:10 CH ******/
CREATE UNIQUE NONCLUSTERED INDEX [UQ_vai_tro_quyen] ON [dbo].[vai_tro_quyen_han]
(
	[ma_vai_tro_id] ASC,
	[ma_quyen] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [UQ__video_sa__B2CA6DEB24EA5BD0]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[video_san_pham] ADD UNIQUE NONCLUSTERED 
(
	[ma_san_pham_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [voucher_so_huu_index_1]    Script Date: 28/10/2026 6:37:10 CH ******/
CREATE UNIQUE NONCLUSTERED INDEX [voucher_so_huu_index_1] ON [dbo].[voucher_so_huu]
(
	[ma_khach_hang_id] ASC,
	[ma_giam_gia_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__yeu_cau___4DA21518F005B1F0]    Script Date: 28/10/2026 6:37:10 CH ******/
ALTER TABLE [dbo].[yeu_cau_doi_tra] ADD UNIQUE NONCLUSTERED 
(
	[ma_yeu_cau_rma] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[bai_viet_blog] ADD  DEFAULT (getdate()) FOR [ngay_xuat_ban]
GO
ALTER TABLE [dbo].[banner_quang_cao] ADD  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[bien_dong_diem] ADD  DEFAULT (getdate()) FOR [ngay_tao]
GO
ALTER TABLE [dbo].[bien_the_san_pham] ADD  DEFAULT ((0)) FOR [so_luong]
GO
ALTER TABLE [dbo].[bien_the_san_pham] ADD  DEFAULT ((0)) FOR [gia_goc]
GO
ALTER TABLE [dbo].[bien_the_san_pham] ADD  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[bo_suu_tap] ADD  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[chat_lieu] ADD  CONSTRAINT [DF_chat_lieu_kich_hoat]  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[chi_tiet_flash_sale] ADD  DEFAULT ((0)) FOR [so_luong_da_ban]
GO
ALTER TABLE [dbo].[chi_tiet_gio_hang] ADD  DEFAULT ((1)) FOR [so_luong]
GO
ALTER TABLE [dbo].[chi_tiet_gio_hang] ADD  DEFAULT (getdate()) FOR [ngay_them]
GO
ALTER TABLE [dbo].[chi_tiet_hoa_don_cho] ADD  DEFAULT ((1)) FOR [so_luong]
GO
ALTER TABLE [dbo].[chi_tiet_hoa_don_cho] ADD  DEFAULT ((0)) FOR [don_gia]
GO
ALTER TABLE [dbo].[chi_tiet_hoa_don_cho] ADD  DEFAULT ((0)) FOR [thanh_tien]
GO
ALTER TABLE [dbo].[chi_tiet_san_pham_doi_tra] ADD  DEFAULT ((1)) FOR [so_luong_tra]
GO
ALTER TABLE [dbo].[danh_gia_san_pham] ADD  DEFAULT (getdate()) FOR [ngay_tao]
GO
ALTER TABLE [dbo].[danh_muc] ADD  DEFAULT ((0)) FOR [thu_tu_hien_thi]
GO
ALTER TABLE [dbo].[danh_muc] ADD  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[dia_chi] ADD  DEFAULT ((0)) FOR [la_mac_dinh]
GO
ALTER TABLE [dbo].[don_hang] ADD  DEFAULT ((0)) FOR [tong_tien_hang]
GO
ALTER TABLE [dbo].[don_hang] ADD  DEFAULT ((0)) FOR [tien_giam_gia]
GO
ALTER TABLE [dbo].[don_hang] ADD  DEFAULT ((0)) FOR [tien_coolcash_dung]
GO
ALTER TABLE [dbo].[don_hang] ADD  DEFAULT ((0)) FOR [phi_van_chuyen]
GO
ALTER TABLE [dbo].[don_hang] ADD  DEFAULT ((0)) FOR [tong_thanh_toan]
GO
ALTER TABLE [dbo].[don_hang] ADD  DEFAULT (getdate()) FOR [ngay_tao]
GO
ALTER TABLE [dbo].[don_hang] ADD  CONSTRAINT [DF_don_hang_loai]  DEFAULT ('ONLINE') FOR [loai_don_hang]
GO
ALTER TABLE [dbo].[don_hang] ADD  CONSTRAINT [DF_don_hang_tien_khach_dua]  DEFAULT ((0)) FOR [tien_khach_dua]
GO
ALTER TABLE [dbo].[don_hang] ADD  CONSTRAINT [DF_don_hang_tien_thua]  DEFAULT ((0)) FOR [tien_thua_tra_khach]
GO
ALTER TABLE [dbo].[flash_sale] ADD  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[form_dang] ADD  CONSTRAINT [DF_form_dang_kich_hoat]  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[giao_dich_thanh_toan] ADD  DEFAULT ('CHO_XU_LY') FOR [trang_thai]
GO
ALTER TABLE [dbo].[giao_dich_thanh_toan] ADD  DEFAULT (getdate()) FOR [ngay_tao]
GO
ALTER TABLE [dbo].[gio_hang] ADD  DEFAULT (getdate()) FOR [ngay_cap_nhat]
GO
ALTER TABLE [dbo].[goi_combo] ADD  DEFAULT ((2)) FOR [so_mon_quy_dinh]
GO
ALTER TABLE [dbo].[goi_combo] ADD  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[hinh_anh] ADD  DEFAULT ((0)) FOR [la_anh_dai_dien]
GO
ALTER TABLE [dbo].[hinh_anh] ADD  DEFAULT ((0)) FOR [thu_tu]
GO
ALTER TABLE [dbo].[ho_so_hoi_vien] ADD  DEFAULT ((0)) FOR [so_du_coolcash]
GO
ALTER TABLE [dbo].[ho_so_hoi_vien] ADD  DEFAULT ((0)) FOR [tong_chi_tieu_nam]
GO
ALTER TABLE [dbo].[hoa_don_cho] ADD  DEFAULT ('CHO_THANH_TOAN') FOR [trang_thai]
GO
ALTER TABLE [dbo].[hoa_don_cho] ADD  DEFAULT (getdate()) FOR [ngay_tao]
GO
ALTER TABLE [dbo].[khach_hang] ADD  DEFAULT ((1)) FOR [trang_thai]
GO
ALTER TABLE [dbo].[khach_hang] ADD  DEFAULT (getdate()) FOR [ngay_tao]
GO
ALTER TABLE [dbo].[kich_thuoc] ADD  CONSTRAINT [DF_kich_thuoc_kich_hoat]  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[lich_su_trang_thai_don_hang] ADD  DEFAULT (getdate()) FOR [ngay_chuyen]
GO
ALTER TABLE [dbo].[ma_giam_gia] ADD  DEFAULT ((0)) FOR [gia_tri_don_toi_thieu]
GO
ALTER TABLE [dbo].[ma_giam_gia] ADD  DEFAULT ((0)) FOR [so_luong_da_dung]
GO
ALTER TABLE [dbo].[ma_giam_gia] ADD  DEFAULT ((1)) FOR [gioi_han_moi_user]
GO
ALTER TABLE [dbo].[ma_giam_gia] ADD  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[ma_giam_gia] ADD  DEFAULT ((0)) FOR [ngan_sach_da_dung]
GO
ALTER TABLE [dbo].[ma_giam_gia] ADD  DEFAULT (getdate()) FOR [ngay_tao]
GO
ALTER TABLE [dbo].[mau_sac] ADD  CONSTRAINT [DF_mau_sac_kich_hoat]  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[nhan_vien] ADD  DEFAULT ((1)) FOR [trang_thai]
GO
ALTER TABLE [dbo].[nhan_vien] ADD  DEFAULT (getdate()) FOR [ngay_tao]
GO
ALTER TABLE [dbo].[phuong_thuc_thanh_toan] ADD  DEFAULT ((0)) FOR [la_truc_tuyen]
GO
ALTER TABLE [dbo].[phuong_thuc_thanh_toan] ADD  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[san_pham] ADD  DEFAULT ((0)) FOR [gia_co_ban]
GO
ALTER TABLE [dbo].[san_pham] ADD  DEFAULT ((0)) FOR [noi_bat]
GO
ALTER TABLE [dbo].[san_pham] ADD  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[san_pham] ADD  DEFAULT (getdate()) FOR [ngay_tao]
GO
ALTER TABLE [dbo].[san_pham_yeu_thich] ADD  DEFAULT (getdate()) FOR [ngay_them]
GO
ALTER TABLE [dbo].[vai_tro] ADD  DEFAULT ((1)) FOR [trang_thai]
GO
ALTER TABLE [dbo].[video_san_pham] ADD  DEFAULT ((1)) FOR [kich_hoat]
GO
ALTER TABLE [dbo].[video_san_pham] ADD  DEFAULT (getdate()) FOR [ngay_tao]
GO
ALTER TABLE [dbo].[voucher_so_huu] ADD  DEFAULT ((0)) FOR [da_su_dung]
GO
ALTER TABLE [dbo].[voucher_so_huu] ADD  DEFAULT (getdate()) FOR [ngay_nhan]
GO
ALTER TABLE [dbo].[yeu_cau_doi_tra] ADD  DEFAULT ((0)) FOR [so_tien_hoan]
GO
ALTER TABLE [dbo].[yeu_cau_doi_tra] ADD  DEFAULT ('CHO_DUYET') FOR [trang_thai]
GO
ALTER TABLE [dbo].[yeu_cau_doi_tra] ADD  DEFAULT (getdate()) FOR [ngay_tao]
GO
ALTER TABLE [dbo].[bai_viet_blog]  WITH CHECK ADD FOREIGN KEY([ma_tac_gia_id])
REFERENCES [dbo].[nhan_vien] ([id])
GO
ALTER TABLE [dbo].[bien_dong_diem]  WITH CHECK ADD FOREIGN KEY([ma_don_hang_id])
REFERENCES [dbo].[don_hang] ([id])
GO
ALTER TABLE [dbo].[bien_dong_diem]  WITH CHECK ADD FOREIGN KEY([ma_khach_hang_id])
REFERENCES [dbo].[khach_hang] ([id])
GO
ALTER TABLE [dbo].[bien_the_san_pham]  WITH CHECK ADD FOREIGN KEY([ma_kich_thuoc_id])
REFERENCES [dbo].[kich_thuoc] ([id])
GO
ALTER TABLE [dbo].[bien_the_san_pham]  WITH CHECK ADD FOREIGN KEY([ma_mau_sac_id])
REFERENCES [dbo].[mau_sac] ([id])
GO
ALTER TABLE [dbo].[bien_the_san_pham]  WITH CHECK ADD FOREIGN KEY([ma_san_pham_id])
REFERENCES [dbo].[san_pham] ([id])
GO
ALTER TABLE [dbo].[chi_tiet_don_hang]  WITH CHECK ADD FOREIGN KEY([ma_bien_the_id])
REFERENCES [dbo].[bien_the_san_pham] ([id])
GO
ALTER TABLE [dbo].[chi_tiet_don_hang]  WITH CHECK ADD FOREIGN KEY([ma_combo_id])
REFERENCES [dbo].[goi_combo] ([id])
GO
ALTER TABLE [dbo].[chi_tiet_don_hang]  WITH CHECK ADD FOREIGN KEY([ma_don_hang_id])
REFERENCES [dbo].[don_hang] ([id])
GO
ALTER TABLE [dbo].[chi_tiet_flash_sale]  WITH NOCHECK ADD FOREIGN KEY([ma_bien_the_id])
REFERENCES [dbo].[bien_the_san_pham] ([id])
GO
ALTER TABLE [dbo].[chi_tiet_flash_sale]  WITH NOCHECK ADD FOREIGN KEY([ma_flash_sale_id])
REFERENCES [dbo].[flash_sale] ([id])
GO
ALTER TABLE [dbo].[chi_tiet_gio_hang]  WITH NOCHECK ADD FOREIGN KEY([ma_bien_the_id])
REFERENCES [dbo].[bien_the_san_pham] ([id])
GO
ALTER TABLE [dbo].[chi_tiet_gio_hang]  WITH NOCHECK ADD FOREIGN KEY([ma_combo_id])
REFERENCES [dbo].[goi_combo] ([id])
GO
ALTER TABLE [dbo].[chi_tiet_gio_hang]  WITH NOCHECK ADD FOREIGN KEY([ma_gio_hang_id])
REFERENCES [dbo].[gio_hang] ([id])
GO
ALTER TABLE [dbo].[chi_tiet_hoa_don_cho]  WITH CHECK ADD  CONSTRAINT [FK_cthdc_bien_the] FOREIGN KEY([ma_bien_the_id])
REFERENCES [dbo].[bien_the_san_pham] ([id])
GO
ALTER TABLE [dbo].[chi_tiet_hoa_don_cho] CHECK CONSTRAINT [FK_cthdc_bien_the]
GO
ALTER TABLE [dbo].[chi_tiet_hoa_don_cho]  WITH CHECK ADD  CONSTRAINT [FK_cthdc_hoa_don_cho] FOREIGN KEY([ma_hoa_don_cho_id])
REFERENCES [dbo].[hoa_don_cho] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[chi_tiet_hoa_don_cho] CHECK CONSTRAINT [FK_cthdc_hoa_don_cho]
GO
ALTER TABLE [dbo].[chi_tiet_san_pham_doi_tra]  WITH NOCHECK ADD FOREIGN KEY([ma_bien_the_moi_id])
REFERENCES [dbo].[bien_the_san_pham] ([id])
GO
ALTER TABLE [dbo].[chi_tiet_san_pham_doi_tra]  WITH NOCHECK ADD FOREIGN KEY([ma_chi_tiet_don_hang_id])
REFERENCES [dbo].[chi_tiet_don_hang] ([id])
GO
ALTER TABLE [dbo].[chi_tiet_san_pham_doi_tra]  WITH NOCHECK ADD FOREIGN KEY([ma_yeu_cau_doi_tra_id])
REFERENCES [dbo].[yeu_cau_doi_tra] ([id])
GO
ALTER TABLE [dbo].[danh_gia_san_pham]  WITH CHECK ADD FOREIGN KEY([ma_chi_tiet_don_hang_id])
REFERENCES [dbo].[chi_tiet_don_hang] ([id])
GO
ALTER TABLE [dbo].[danh_gia_san_pham]  WITH CHECK ADD FOREIGN KEY([ma_khach_hang_id])
REFERENCES [dbo].[khach_hang] ([id])
GO
ALTER TABLE [dbo].[danh_gia_san_pham]  WITH CHECK ADD FOREIGN KEY([ma_san_pham_id])
REFERENCES [dbo].[san_pham] ([id])
GO
ALTER TABLE [dbo].[danh_muc]  WITH CHECK ADD FOREIGN KEY([ma_danh_muc_cha])
REFERENCES [dbo].[danh_muc] ([id])
GO
ALTER TABLE [dbo].[dia_chi]  WITH NOCHECK ADD FOREIGN KEY([ma_khach_hang_id])
REFERENCES [dbo].[khach_hang] ([id])
GO
ALTER TABLE [dbo].[don_hang]  WITH CHECK ADD FOREIGN KEY([ma_giam_gia_id])
REFERENCES [dbo].[ma_giam_gia] ([id])
GO
ALTER TABLE [dbo].[don_hang]  WITH CHECK ADD FOREIGN KEY([ma_khach_hang_id])
REFERENCES [dbo].[khach_hang] ([id])
GO
ALTER TABLE [dbo].[don_hang]  WITH CHECK ADD FOREIGN KEY([ma_trang_thai_id])
REFERENCES [dbo].[trang_thai_don_hang] ([id])
GO
ALTER TABLE [dbo].[don_hang]  WITH CHECK ADD  CONSTRAINT [FK_don_hang_nhan_vien] FOREIGN KEY([ma_nhan_vien_id])
REFERENCES [dbo].[nhan_vien] ([id])
GO
ALTER TABLE [dbo].[don_hang] CHECK CONSTRAINT [FK_don_hang_nhan_vien]
GO
ALTER TABLE [dbo].[don_hang]  WITH CHECK ADD  CONSTRAINT [FKgufqtsakic3qgjqfujokvnjju] FOREIGN KEY([ma_phuong_thuc_id])
REFERENCES [dbo].[phuong_thuc_thanh_toan] ([id])
GO
ALTER TABLE [dbo].[don_hang] CHECK CONSTRAINT [FKgufqtsakic3qgjqfujokvnjju]
GO
ALTER TABLE [dbo].[giao_dich_thanh_toan]  WITH NOCHECK ADD FOREIGN KEY([ma_don_hang_id])
REFERENCES [dbo].[don_hang] ([id])
GO
ALTER TABLE [dbo].[giao_dich_thanh_toan]  WITH NOCHECK ADD FOREIGN KEY([ma_phuong_thuc_id])
REFERENCES [dbo].[phuong_thuc_thanh_toan] ([id])
GO
ALTER TABLE [dbo].[gio_hang]  WITH NOCHECK ADD FOREIGN KEY([ma_khach_hang_id])
REFERENCES [dbo].[khach_hang] ([id])
GO
ALTER TABLE [dbo].[hinh_anh]  WITH CHECK ADD FOREIGN KEY([ma_bien_the_id])
REFERENCES [dbo].[bien_the_san_pham] ([id])
GO
ALTER TABLE [dbo].[hinh_anh]  WITH CHECK ADD FOREIGN KEY([ma_san_pham_id])
REFERENCES [dbo].[san_pham] ([id])
GO
ALTER TABLE [dbo].[ho_so_hoi_vien]  WITH CHECK ADD FOREIGN KEY([ma_hang_hien_tai_id])
REFERENCES [dbo].[hang_thanh_vien] ([id])
GO
ALTER TABLE [dbo].[ho_so_hoi_vien]  WITH CHECK ADD FOREIGN KEY([ma_khach_hang_id])
REFERENCES [dbo].[khach_hang] ([id])
GO
ALTER TABLE [dbo].[hoa_don_cho]  WITH CHECK ADD  CONSTRAINT [FK_hdc_khach_hang] FOREIGN KEY([ma_khach_hang_id])
REFERENCES [dbo].[khach_hang] ([id])
GO
ALTER TABLE [dbo].[hoa_don_cho] CHECK CONSTRAINT [FK_hdc_khach_hang]
GO
ALTER TABLE [dbo].[hoa_don_cho]  WITH CHECK ADD  CONSTRAINT [FK_hdc_nhan_vien] FOREIGN KEY([ma_nhan_vien_id])
REFERENCES [dbo].[nhan_vien] ([id])
GO
ALTER TABLE [dbo].[hoa_don_cho] CHECK CONSTRAINT [FK_hdc_nhan_vien]
GO
ALTER TABLE [dbo].[lich_su_trang_thai_don_hang]  WITH CHECK ADD FOREIGN KEY([ma_don_hang_id])
REFERENCES [dbo].[don_hang] ([id])
GO
ALTER TABLE [dbo].[lich_su_trang_thai_don_hang]  WITH CHECK ADD FOREIGN KEY([ma_trang_thai_moi_id])
REFERENCES [dbo].[trang_thai_don_hang] ([id])
GO
ALTER TABLE [dbo].[lich_su_trang_thai_don_hang]  WITH CHECK ADD FOREIGN KEY([nguoi_thuc_hien_id])
REFERENCES [dbo].[nhan_vien] ([id])
GO
ALTER TABLE [dbo].[nhan_vien]  WITH NOCHECK ADD FOREIGN KEY([ma_vai_tro_id])
REFERENCES [dbo].[vai_tro] ([id])
GO
ALTER TABLE [dbo].[san_pham]  WITH CHECK ADD FOREIGN KEY([ma_bo_suu_tap_id])
REFERENCES [dbo].[bo_suu_tap] ([id])
GO
ALTER TABLE [dbo].[san_pham]  WITH CHECK ADD FOREIGN KEY([ma_danh_muc_id])
REFERENCES [dbo].[danh_muc] ([id])
GO
ALTER TABLE [dbo].[san_pham_trong_combo]  WITH NOCHECK ADD FOREIGN KEY([ma_combo_id])
REFERENCES [dbo].[goi_combo] ([id])
GO
ALTER TABLE [dbo].[san_pham_trong_combo]  WITH NOCHECK ADD FOREIGN KEY([ma_san_pham_id])
REFERENCES [dbo].[san_pham] ([id])
GO
ALTER TABLE [dbo].[san_pham_yeu_thich]  WITH CHECK ADD FOREIGN KEY([ma_khach_hang_id])
REFERENCES [dbo].[khach_hang] ([id])
GO
ALTER TABLE [dbo].[san_pham_yeu_thich]  WITH CHECK ADD FOREIGN KEY([ma_san_pham_id])
REFERENCES [dbo].[san_pham] ([id])
GO
ALTER TABLE [dbo].[vai_tro_quyen_han]  WITH CHECK ADD  CONSTRAINT [FK_vai_tro_quyen_han_vai_tro] FOREIGN KEY([ma_vai_tro_id])
REFERENCES [dbo].[vai_tro] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[vai_tro_quyen_han] CHECK CONSTRAINT [FK_vai_tro_quyen_han_vai_tro]
GO
ALTER TABLE [dbo].[video_san_pham]  WITH CHECK ADD  CONSTRAINT [FK_video_san_pham_san_pham] FOREIGN KEY([ma_san_pham_id])
REFERENCES [dbo].[san_pham] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[video_san_pham] CHECK CONSTRAINT [FK_video_san_pham_san_pham]
GO
ALTER TABLE [dbo].[voucher_so_huu]  WITH NOCHECK ADD FOREIGN KEY([ma_giam_gia_id])
REFERENCES [dbo].[ma_giam_gia] ([id])
GO
ALTER TABLE [dbo].[voucher_so_huu]  WITH NOCHECK ADD FOREIGN KEY([ma_khach_hang_id])
REFERENCES [dbo].[khach_hang] ([id])
GO
ALTER TABLE [dbo].[yeu_cau_doi_tra]  WITH NOCHECK ADD FOREIGN KEY([ma_don_hang_id])
REFERENCES [dbo].[don_hang] ([id])
GO
ALTER TABLE [dbo].[yeu_cau_doi_tra]  WITH NOCHECK ADD FOREIGN KEY([ma_khach_hang_id])
REFERENCES [dbo].[khach_hang] ([id])
GO
ALTER TABLE [dbo].[yeu_cau_doi_tra]  WITH NOCHECK ADD FOREIGN KEY([nguoi_duyet_id])
REFERENCES [dbo].[nhan_vien] ([id])
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Nhân viên/Admin nào vi?t bài' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'bai_viet_blog', @level2type=N'COLUMN',@level2name=N'ma_tac_gia_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Kien thuc mac dep, Cham soc do nam, Xu huong...' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'bai_viet_blog', @level2type=N'COLUMN',@level2name=N'chuyen_muc'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'URL thân thi?n SEO: bi-quyet-mac-dep-cho-nam' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'bai_viet_blog', @level2type=N'COLUMN',@level2name=N'duong_dan_slug'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Khách click vào banner thì nh?y sang link nào' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'banner_quang_cao', @level2type=N'COLUMN',@level2name=N'duong_dan_dich'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'HOME_SLIDER, POPUP_SALE, BANNER_DANH_MUC' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'banner_quang_cao', @level2type=N'COLUMN',@level2name=N'vi_tri_hien_thi'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Giao d?ch di?m này g?n v?i don hàng nào (n?u có)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'bien_dong_diem', @level2type=N'COLUMN',@level2name=N'ma_don_hang_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'CONG_DIEM (hoàn ti?n, t?ng quà) ho?c TRU_DIEM (tiêu di?m, h?t h?n)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'bien_dong_diem', @level2type=N'COLUMN',@level2name=N'loai_bien_dong'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'S? di?m tang/gi?m (+15000 ho?c -50000)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'bien_dong_diem', @level2type=N'COLUMN',@level2name=N'so_diem'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Lý do: Hoàn ti?n don hàng CM-99, Tiêu di?m mua áo Polo...' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'bien_dong_diem', @level2type=N'COLUMN',@level2name=N'noi_dung'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'VD: POLO-COTTON-DEN-L' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'bien_the_san_pham', @level2type=N'COLUMN',@level2name=N'ma_sku'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'T?N KHO TH?T S? N?M ? ÐÂY' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'bien_the_san_pham', @level2type=N'COLUMN',@level2name=N'so_luong'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Giá bán th?c t? c?a size/màu này' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'bien_the_san_pham', @level2type=N'COLUMN',@level2name=N'gia_ban'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Giá g?ch ngang tru?c khi gi?m' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'bien_the_san_pham', @level2type=N'COLUMN',@level2name=N'gia_goc'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'UUID bó các món trong cùng 1 h?p combo' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_don_hang', @level2type=N'COLUMN',@level2name=N'nhom_combo_token'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Ðóng bang tên lúc mua' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_don_hang', @level2type=N'COLUMN',@level2name=N'ten_san_pham'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Ðóng bang SKU' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_don_hang', @level2type=N'COLUMN',@level2name=N'ma_sku'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Ðóng bang giá bán th?c t? c?a 1 cái' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_don_hang', @level2type=N'COLUMN',@level2name=N'don_gia'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'don_gia * so_luong' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_don_hang', @level2type=N'COLUMN',@level2name=N'thanh_tien'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Áp d?ng cho chính xác SKU nào (Size/Màu nào)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_flash_sale', @level2type=N'COLUMN',@level2name=N'ma_bien_the_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Giá s?c ch? áp d?ng trong khung gi? này' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_flash_sale', @level2type=N'COLUMN',@level2name=N'gia_flash_sale'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Ch? x? kho dúng N su?t v?i giá này' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_flash_sale', @level2type=N'COLUMN',@level2name=N'so_luong_mo_ban'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'B? d?m: Ð?t m?c mo_ban là l?p t?c ng?t giá sale' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_flash_sale', @level2type=N'COLUMN',@level2name=N'so_luong_da_ban'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Mã SKU c? th? (VD: Áo Ðen Size L) mà khách dang d?nh mua' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_gio_hang', @level2type=N'COLUMN',@level2name=N'ma_bien_the_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'N?u dòng này mua l?, d? NULL. N?u thu?c combo, di?n ID c?a combo vào' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_gio_hang', @level2type=N'COLUMN',@level2name=N'ma_combo_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'C?C QUAN TR?NG: UUID d? gom c?m. Ð?c gi?i thích bên du?i.' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_gio_hang', @level2type=N'COLUMN',@level2name=N'nhom_combo_token'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Ch? dích danh món nào trong don cu b? dem ra d?i' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_san_pham_doi_tra', @level2type=N'COLUMN',@level2name=N'ma_chi_tiet_don_hang_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'S? lu?ng món dem d?i' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_san_pham_doi_tra', @level2type=N'COLUMN',@level2name=N'so_luong_tra'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'N?u là Ð?I HÀNG: Tr? t?i SKU m?i c?n l?y (VD: L?y size L thay vì size M)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_san_pham_doi_tra', @level2type=N'COLUMN',@level2name=N'ma_bien_the_moi_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Kho nh?n l?i dánh giá: Con nguyen tem, Da giat, Rach vai...' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'chi_tiet_san_pham_doi_tra', @level2type=N'COLUMN',@level2name=N'tinh_trang_san_pham'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Ðánh giá vào s?n ph?m cha' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'danh_gia_san_pham', @level2type=N'COLUMN',@level2name=N'ma_san_pham_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'B?T BU?C: Ðã mua món này m?i du?c dánh giá (Tick xanh Ngu?i Mua Th?t)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'danh_gia_san_pham', @level2type=N'COLUMN',@level2name=N'ma_chi_tiet_don_hang_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Ch?m di?m t? 1 d?n 5 sao' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'danh_gia_san_pham', @level2type=N'COLUMN',@level2name=N'so_sao'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Chu?i JSON ch?a m?ng link ?nh khách ch?p feedback th?c t?' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'danh_gia_san_pham', @level2type=N'COLUMN',@level2name=N'danh_sach_anh_json'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Tr? v? chính id danh_muc d? làm menu cha/con' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'danh_muc', @level2type=N'COLUMN',@level2name=N'ma_danh_muc_cha'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'True: Ð?a ch? t? d?ng ch?n khi checkout' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'dia_chi', @level2type=N'COLUMN',@level2name=N'la_mac_dinh'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Mã hi?n th?: CM-883921' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'don_hang', @level2type=N'COLUMN',@level2name=N'ma_don_hang'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'NULL n?u khách vãng lai mua nhanh' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'don_hang', @level2type=N'COLUMN',@level2name=N'ma_khach_hang_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Áp voucher nào' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'don_hang', @level2type=N'COLUMN',@level2name=N'ma_giam_gia_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Mã buu tá tra c?u: GHTK12345, VNPOST999...' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'don_hang', @level2type=N'COLUMN',@level2name=N'ma_van_don'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'(Hàng - Gi?m - Ði?m + Ship)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'don_hang', @level2type=N'COLUMN',@level2name=N'tong_thanh_toan'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'VD: Flash Sale Gi?a Trua 12h-14h, Siêu Sale 11/11' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'flash_sale', @level2type=N'COLUMN',@level2name=N'tieu_de'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Admin có th? t?t kh?n c?p' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'flash_sale', @level2type=N'COLUMN',@level2name=N'kich_hoat'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Mã h? th?ng mình t? sinh g?i cho ngân hàng' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'giao_dich_thanh_toan', @level2type=N'COLUMN',@level2name=N'ma_giao_dich_noi_bo'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Mã do VNPay/MoMo tr? v? sau khi tr? ti?n xong (Dùng d?i soát k? toán)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'giao_dich_thanh_toan', @level2type=N'COLUMN',@level2name=N'ma_giao_dich_cong'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'S? ti?n th?c thu c?a l?n qu?t này' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'giao_dich_thanh_toan', @level2type=N'COLUMN',@level2name=N'so_tien'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'CHO_XU_LY, THANH_CONG, THAT_BAI' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'giao_dich_thanh_toan', @level2type=N'COLUMN',@level2name=N'trang_thai'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Luu toàn b? JSON Webhook c?ng tr? v? d? Dev debug khi có tranh ch?p' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'giao_dich_thanh_toan', @level2type=N'COLUMN',@level2name=N'du_lieu_phan_hoi'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'B?T BU?C có tài kho?n m?i có gi? trên DB' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'gio_hang', @level2type=N'COLUMN',@level2name=N'ma_khach_hang_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'VD: COMBO-BOX3-QUANLOT, COMBO-SUMMER' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'goi_combo', @level2type=N'COLUMN',@level2name=N'ma_combo'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Giá tr?n gói uu dãi khi mua d? s? món' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'goi_combo', @level2type=N'COLUMN',@level2name=N'gia_combo'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'S? lu?ng s?n ph?m khách B?T BU?C ph?i ch?n' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'goi_combo', @level2type=N'COLUMN',@level2name=N'so_mon_quy_dinh'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'DONG, BAC, VANG, KIM_CUONG' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'hang_thanh_vien', @level2type=N'COLUMN',@level2name=N'ten_hang'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Tích luy chi tiêu trong nam d? lên h?ng (VD: 3 tri?u d? lên Vàng)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'hang_thanh_vien', @level2type=N'COLUMN',@level2name=N'chi_tieu_nam_toi_thieu'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'H?ng Vàng du?c hoàn 5% Coolcash trên m?i don hàng' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'hang_thanh_vien', @level2type=N'COLUMN',@level2name=N'phan_tram_hoan_tien'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Khách b?m ch?n màu nào thì l?c hi?n ?nh c?a bi?n th? dó' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'hinh_anh', @level2type=N'COLUMN',@level2name=N'ma_bien_the_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Quan h? 1 - 1 v?i khach_hang' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ho_so_hoi_vien', @level2type=N'COLUMN',@level2name=N'ma_khach_hang_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'S? du ví hi?n t?i d? thanh toán (1 Coolcash = 1 VNÐ)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ho_so_hoi_vien', @level2type=N'COLUMN',@level2name=N'so_du_coolcash'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'T?ng ti?n dã mua trong 365 ngày qua d? xét h?/lên h?ng' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ho_so_hoi_vien', @level2type=N'COLUMN',@level2name=N'tong_chi_tieu_nam'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'NULL n?u là khách vãng lai ch? mua qua SÐT' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'khach_hang', @level2type=N'COLUMN',@level2name=N'mat_khau_hash'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Dùng d? g?i mã voucher sinh nh?t' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'khach_hang', @level2type=N'COLUMN',@level2name=N'ngay_sinh'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'1: Ho?t d?ng, 0: B? c?m' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'khach_hang', @level2type=N'COLUMN',@level2name=N'trang_thai'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'S, M, L, XL, 2XL' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'kich_thuoc', @level2type=N'COLUMN',@level2name=N'ma_kich_thuoc'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Nhân viên CSKH/Kho duy?t don. NULL n?u h? th?ng t? d?i' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'lich_su_trang_thai_don_hang', @level2type=N'COLUMN',@level2name=N'nguoi_thuc_hien_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Mã khách nh?p: VD COOLMATE50K, FREESHIP' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ma_giam_gia', @level2type=N'COLUMN',@level2name=N'ma_code'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Tên hi?n th?: Gi?m 50k cho don t? 200k' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ma_giam_gia', @level2type=N'COLUMN',@level2name=N'ten_voucher'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'PERCENT (gi?m %) ho?c FIXED_AMOUNT (gi?m ti?n m?t)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ma_giam_gia', @level2type=N'COLUMN',@level2name=N'loai_giam_gia'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Luu 10 (t?c 10%) ho?c 50000 (t?c 50.000d)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ma_giam_gia', @level2type=N'COLUMN',@level2name=N'gia_tri_giam'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Ch?n tr?n cho voucher %. Ví d? gi?m 20% nhung T?I ÐA 50k' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ma_giam_gia', @level2type=N'COLUMN',@level2name=N'giam_toi_da'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Ðon t? bao nhiêu ti?n m?i du?c áp' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ma_giam_gia', @level2type=N'COLUMN',@level2name=N'gia_tri_don_toi_thieu'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'NULL = Không gi?i h?n. Có s? = T?ng ngân sách phát hành toàn sàn' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ma_giam_gia', @level2type=N'COLUMN',@level2name=N'tong_so_luong'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'B? d?m: Ðã có bao nhiêu don áp thành công' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ma_giam_gia', @level2type=N'COLUMN',@level2name=N'so_luong_da_dung'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'1 tài kho?n ch? du?c dùng mã này N l?n' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ma_giam_gia', @level2type=N'COLUMN',@level2name=N'gioi_han_moi_user'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Admin có th? t?t kh?n c?p n?u b? hack/bug' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'ma_giam_gia', @level2type=N'COLUMN',@level2name=N'kich_hoat'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'DEN, TRANG, NAVY' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'mau_sac', @level2type=N'COLUMN',@level2name=N'ma_mau'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'1: Ðang làm, 0: Ðã ngh?' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'nhan_vien', @level2type=N'COLUMN',@level2name=N'trang_thai'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'COD, VNPAY_QR, MOMO, ZALOPAY' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'phuong_thuc_thanh_toan', @level2type=N'COLUMN',@level2name=N'ma_phuong_thuc'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Thanh toán khi nh?n hàng, C?ng VNPay, Ví MoMo...' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'phuong_thuc_thanh_toan', @level2type=N'COLUMN',@level2name=N'ten_phuong_thuc'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'False: COD (ti?n m?t). True: Qu?t th?/Quét QR online' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'phuong_thuc_thanh_toan', @level2type=N'COLUMN',@level2name=N'la_truc_tuyen'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'T?t di n?u c?ng VNPay dang b?o trì' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'phuong_thuc_thanh_toan', @level2type=N'COLUMN',@level2name=N'kich_hoat'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Cotton Compact, Modal, Cafe' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'san_pham', @level2type=N'COLUMN',@level2name=N'chat_lieu'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Regular, Slim-fit, Oversize' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'san_pham', @level2type=N'COLUMN',@level2name=N'form_dang'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Giá hi?n th? t?... ngoài trang ch?' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'san_pham', @level2type=N'COLUMN',@level2name=N'gia_co_ban'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Tính nang này b?t bu?c login' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'san_pham_yeu_thich', @level2type=N'COLUMN',@level2name=N'ma_khach_hang_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Luu ID s?n ph?m cha, ko luu bi?n th?. Thích là thích cái áo, chua c?n ch?n size' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'san_pham_yeu_thich', @level2type=N'COLUMN',@level2name=N'ma_san_pham_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'CHO_XAC_NHAN, DANG_XU_LY, DANG_GIAO, HOAN_THANH, DA_HUY' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'trang_thai_don_hang', @level2type=N'COLUMN',@level2name=N'ma_trang_thai'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Màu badge: #FFA500, #00FF00...' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'trang_thai_don_hang', @level2type=N'COLUMN',@level2name=N'ma_mau_hex'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Mã d?nh danh: ADMIN, CSKH, BAN_HANG' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'vai_tro', @level2type=N'COLUMN',@level2name=N'ma_vai_tro'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'1: Ho?t d?ng, 0: Khóa' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'vai_tro', @level2type=N'COLUMN',@level2name=N'trang_thai'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'True: Khách dã dùng mã này r?i, không dùng l?i du?c n?a' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'voucher_so_huu', @level2type=N'COLUMN',@level2name=N'da_su_dung'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Th?i di?m khách thanh toán don hàng b?ng mã này' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'voucher_so_huu', @level2type=N'COLUMN',@level2name=N'ngay_su_dung'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Mã phi?u: RMA-20250215-01' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'yeu_cau_doi_tra', @level2type=N'COLUMN',@level2name=N'ma_yeu_cau_rma'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Ð?i tr? cho don hàng nào' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'yeu_cau_doi_tra', @level2type=N'COLUMN',@level2name=N'ma_don_hang_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Khách hàng nào yêu c?u' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'yeu_cau_doi_tra', @level2type=N'COLUMN',@level2name=N'ma_khach_hang_id'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'DOI_HANG (Ð?i size/màu) ho?c TRA_HANG (Hoàn ti?n)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'yeu_cau_doi_tra', @level2type=N'COLUMN',@level2name=N'loai_doi_tra'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Mac chat, Hang loi, Khong ung y...' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'yeu_cau_doi_tra', @level2type=N'COLUMN',@level2name=N'ly_do'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'S? ti?n công ty ph?i bank tr? khách' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'yeu_cau_doi_tra', @level2type=N'COLUMN',@level2name=N'so_tien_hoan'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'STK - Tên Ngân Hàng - Tên Ch? Th? (VD: 1903... Techcombank Nguyen Van A)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'yeu_cau_doi_tra', @level2type=N'COLUMN',@level2name=N'thong_tin_ngan_hang'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'CHO_DUYET, DA_DUYET, DANG_GUI_HANG, HOAN_THANH, TU_CHOI' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'yeu_cau_doi_tra', @level2type=N'COLUMN',@level2name=N'trang_thai'
GO
EXEC sys.sp_addextendedproperty @name=N'Column_Description', @value=N'Nhân viên CSKH nào duy?t yêu c?u này' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'yeu_cau_doi_tra', @level2type=N'COLUMN',@level2name=N'nguoi_duyet_id'
GO
USE [Simpl4]
GO
