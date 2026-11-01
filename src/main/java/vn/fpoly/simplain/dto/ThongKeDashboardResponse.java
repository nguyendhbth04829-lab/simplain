package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.fpoly.simplain.entity.DonHang;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO chứa toàn bộ dữ liệu thống kê tổng quan và dữ liệu biểu đồ cho Dashboard (US05).
 *
 * <p>Tiêu chí cốt lõi US05:
 * 1. Thống kê tổng số lượng người dùng (Khách hàng + Nhân sự).
 * 2. Thống kê số lượng đơn hàng hôm nay (và phân loại theo trạng thái).
 * 3. Thống kê doanh thu trong ngày (VNĐ) và so sánh tăng trưởng.
 * 4. Dữ liệu biểu đồ trực quan (Doanh thu & Đơn hàng theo ngày, Tỷ trọng trạng thái đơn, Cơ cấu người dùng).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeDashboardResponse {

    // --- 1. Thống kê Người dùng (Khách hàng + Nhân sự) ---
    private long tongSoLuongNguoiDung;
    private long tongKhachHang;
    private long khachHangHoatDong;
    private long khachHangBiKhoa;
    private long khachHangMoiTrongNgay;
    private long tongNhanVien;
    private long nhanVienDangLam;
    private long nhanVienDaNghi;

    // --- 2. Thống kê Đơn hàng trong ngày (Hôm nay / Ngày được chọn) ---
    private long soDonHangHomNay;
    private long soDonHoanThanhHomNay;
    private long soDonDangXuLyHomNay;
    private long soDonHuyHomNay;
    private long soDonHomQua;
    private double tyLeTangTruongDonHang;

    // --- 3. Thống kê Doanh thu trong ngày (VNĐ) ---
    private BigDecimal doanhThuTrongNgay;
    private BigDecimal doanhThuHoanThanhTrongNgay;
    private BigDecimal doanhThuHomQua;
    private double tyLeTangTruongDoanhThu;
    private BigDecimal giaTriDonTrungBinhHomNay;

    // --- 4. Thống kê Tổng quan toàn hệ thống ---
    private long tongDonHangToanHeThong;
    private BigDecimal tongDoanhThuToanHeThong;
    private long tongDanhMuc;
    private long tongBoSuuTap;
    private long tongVaiTro;

    // --- 5. Dữ liệu Biểu đồ 1: Xu hướng Doanh thu & Đơn hàng theo ngày ---
    private List<String> nhanNgayBieuDo;
    private List<BigDecimal> duLieuDoanhThuTheoNgay;
    private List<Long> duLieuSoDonTheoNgay;

    // --- 6. Dữ liệu Biểu đồ 2: Tỷ trọng Trạng thái đơn hàng ---
    private List<String> nhanTrangThaiDonHang;
    private List<Long> duLieuSoDonTheoTrangThai;

    // --- 7. Dữ liệu Biểu đồ 3: Cơ cấu Người dùng hệ thống ---
    private List<String> nhanCoCauNguoiDung;
    private List<Long> duLieuCoCauNguoiDung;

    // --- 8. Danh sách Đơn hàng mới nhất ---
    private List<DonHang> danhSachDonHangMoiNhat;

    // --- 9. Thông tin Bộ lọc đang áp dụng ---
    private LocalDate ngayThongKe;
    private int soNgayBieuDo;
}
