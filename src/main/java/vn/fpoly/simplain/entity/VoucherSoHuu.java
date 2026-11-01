package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Thực thể Lưu trữ Voucher sở hữu của Khách hàng [voucher_so_huu] (US27 - Mod 6 / Voucher - Khoa).
 * - Tiêu chí 1: Lưu voucher vào ví voucher cá nhân (voucher_so_huu).
 * - Quản lý trạng thái đã sử dụng, ngày nhận và ngày áp dụng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "voucher_so_huu")
public class VoucherSoHuu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_khach_hang_id", nullable = false)
    private Long maKhachHangId;

    @Column(name = "ma_giam_gia_id", nullable = false)
    private Long maGiamGiaId;

    @Column(name = "da_su_dung")
    private Boolean daSuDung;

    @Column(name = "ngay_nhan")
    private LocalDateTime ngayNhan;

    @Column(name = "ngay_su_dung")
    private LocalDateTime ngaySuDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang_id", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_giam_gia_id", insertable = false, updatable = false)
    private MaGiamGia maGiamGia;

    @PrePersist
    protected void onCreate() {
        if (this.ngayNhan == null) {
            this.ngayNhan = LocalDateTime.now();
        }
        if (this.daSuDung == null) {
            this.daSuDung = false;
        }
    }

    /**
     * Kiểm tra voucher này trong ví còn dùng được không.
     */
    public boolean isKhaDung() {
        if (Boolean.TRUE.equals(this.daSuDung)) {
            return false;
        }
        if (this.maGiamGia == null) {
            return false;
        }
        return this.maGiamGia.isDangDienRa();
    }

    /**
     * Kiểm tra voucher đã hết hạn hoặc hết lượt/ngân sách.
     */
    public boolean isHetHieuLuc() {
        if (Boolean.TRUE.equals(this.daSuDung)) {
            return false;
        }
        if (this.maGiamGia == null) {
            return true;
        }
        return this.maGiamGia.isDaKetThuc() || this.maGiamGia.isHetLuot() || this.maGiamGia.isHetNganSach();
    }
}
