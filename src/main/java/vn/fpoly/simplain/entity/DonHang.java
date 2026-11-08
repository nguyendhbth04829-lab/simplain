package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thực thể ánh xạ bảng [don_hang] trong cơ sở dữ liệu Simpl4.
 * Phục vụ thống kê số lượng đơn hàng hôm nay, doanh thu trong ngày và biểu đồ tổng quan (US05).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "don_hang")
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_don_hang", length = 50, unique = true, nullable = false)
    private String maDonHang;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_khach_hang_id", referencedColumnName = "id")
    private KhachHang khachHang;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_trang_thai_id", referencedColumnName = "id", nullable = false)
    private TrangThaiDonHang trangThaiDonHang;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_phuong_thuc_id", referencedColumnName = "id")
    private PhuongThucThanhToan phuongThucThanhToan;

    @Column(name = "ma_giam_gia_id")
    private Long maGiamGiaId;

    @Column(name = "ma_van_don", length = 50)
    private String maVanDon;

    @Nationalized
    @Column(name = "don_vi_van_chuyen", length = 100, columnDefinition = "nvarchar(100)")
    private String donViVanChuyen;

    @Column(name = "loai_don_hang", length = 20)
    @Builder.Default
    private String loaiDonHang = "ONLINE"; // "ONLINE" hoặc "TAI_QUAY"

    @Column(name = "tien_khach_dua", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal tienKhachDua = BigDecimal.ZERO;

    @Column(name = "tien_thua_tra_khach", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal tienThuaTraKhach = BigDecimal.ZERO;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_nhan_vien_id", referencedColumnName = "id")
    private NhanVien nhanVien;

    @Column(name = "tong_tien_hang", precision = 18, scale = 2, nullable = false)
    private BigDecimal tongTienHang;

    @Column(name = "tien_giam_gia", precision = 18, scale = 2)
    private BigDecimal tienGiamGia;

    @Column(name = "tien_coolcash_dung", precision = 18, scale = 2)
    private BigDecimal tienCoolcashDung;

    @Column(name = "phi_van_chuyen", precision = 18, scale = 2)
    private BigDecimal phiVanChuyen;

    @Column(name = "tong_thanh_toan", precision = 18, scale = 2, nullable = false)
    private BigDecimal tongThanhToan;

    @Nationalized
    @Column(name = "ten_nguoi_nhan", length = 150, nullable = false, columnDefinition = "nvarchar(150)")
    private String tenNguoiNhan;

    @Column(name = "so_dien_thoai_nhan", length = 20, nullable = false)
    private String soDienThoaiNhan;

    @Nationalized
    @Column(name = "dia_chi_giao_hang", length = 500, nullable = false, columnDefinition = "nvarchar(500)")
    private String diaChiGiaoHang;

    @Nationalized
    @Column(name = "ghi_chu", length = 500, columnDefinition = "nvarchar(500)")
    private String ghiChu;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    // Thông tin hoàn tiền cho đơn thanh toán trước bị hủy hoặc giao thất bại
    @Nationalized
    @Column(name = "ngan_hang_hoan_tien", length = 100, columnDefinition = "nvarchar(100)")
    private String nganHangHoanTien;

    @Column(name = "so_tai_khoan_hoan_tien", length = 50)
    private String soTaiKhoanHoanTien;

    @Nationalized
    @Column(name = "chu_tai_khoan_hoan_tien", length = 150, columnDefinition = "nvarchar(150)")
    private String chuTaiKhoanHoanTien;

    @Nationalized
    @Column(name = "ghi_chu_hoan_tien", length = 500, columnDefinition = "nvarchar(500)")
    private String ghiChuHoanTien;

    @Nationalized
    @Column(name = "anh_bill_hoan_tien", length = 500, columnDefinition = "nvarchar(500)")
    private String anhBillHoanTien;

    @Column(name = "ma_giao_dich_hoan_tien", length = 100)
    private String maGiaoDichHoanTien;

    @Column(name = "ngay_hoan_tien")
    private LocalDateTime ngayHoanTien;

    @Column(name = "trang_thai_hoan_tien", length = 50)
    private String trangThaiHoanTien; // "CHO_HOAN_TIEN", "DA_HOAN_TIEN"

    // ==================== BỒI THƯỜNG TỪ ĐƠN VỊ VẬN CHUYỂN (US35 - ĐỐI SOÁT BỒI HOÀN 3PL) ====================
    @Column(name = "trang_thai_boi_thuong_dvvc", length = 50)
    private String trangThaiBoiThuongDvvc; // "CHO_BOI_THUONG", "DA_BOI_THUONG", "TU_CHOI_BOI_THUONG"

    @Column(name = "tien_boi_thuong_dvvc", precision = 18, scale = 2)
    private BigDecimal tienBoiThuongDvvc; // Số tiền ĐVVC phải bồi thường cho shop

    @Column(name = "tien_thuc_nhan_boi_thuong", precision = 18, scale = 2)
    private BigDecimal tienThucNhanBoiThuong; // Số tiền thực tế shop nhận được từ ĐVVC

    @Column(name = "ma_doi_soat_boi_thuong", length = 100)
    private String maDoiSoatBoiThuong; // Mã biên bản / đối soát khiếu nại (VD: KN-GHTK-DH123456)

    @Column(name = "ma_giao_dich_dvvc", length = 100)
    private String maGiaoDichDvvc; // Mã giao dịch / tham chiếu chuyển tiền của ĐVVC

    @Nationalized
    @Column(name = "ghi_chu_boi_thuong_dvvc", length = 500, columnDefinition = "nvarchar(500)")
    private String ghiChuBoiThuongDvvc; // Ghi chú đối soát, lý do đền bù hoặc phản hồi từ ĐVVC

    @Column(name = "ngay_yeu_cau_boi_thuong")
    private LocalDateTime ngayYeuCauBoiThuong; // Ngày lập hồ sơ báo mất đòi ĐVVC bồi thường

    @Column(name = "ngay_dvvc_boi_thuong")
    private LocalDateTime ngayDvvcBoiThuong; // Ngày ĐVVC chuyển tiền hoàn/bồi thường cho shop

    @PrePersist
    protected void onCreate() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.tongTienHang == null) {
            this.tongTienHang = BigDecimal.ZERO;
        }
        if (this.tienGiamGia == null) {
            this.tienGiamGia = BigDecimal.ZERO;
        }
        if (this.tienCoolcashDung == null) {
            this.tienCoolcashDung = BigDecimal.ZERO;
        }
        if (this.phiVanChuyen == null) {
            this.phiVanChuyen = BigDecimal.ZERO;
        }
        if (this.tongThanhToan == null) {
            this.tongThanhToan = BigDecimal.ZERO;
        }
        if (this.loaiDonHang == null) {
            this.loaiDonHang = "ONLINE";
        }
        if (this.tienKhachDua == null) {
            this.tienKhachDua = BigDecimal.ZERO;
        }
        if (this.tienThuaTraKhach == null) {
            this.tienThuaTraKhach = BigDecimal.ZERO;
        }
    }
}
