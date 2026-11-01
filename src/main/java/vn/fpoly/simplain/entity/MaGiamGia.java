package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Thực thể Chương trình Mã giảm giá / Voucher [ma_giam_gia] (US26 - Mod 6 / Voucher - Khoa).
 * - Tiêu chí 1: Tạo Voucher (% hoặc Tiền mặt).
 * - Tiêu chí 2: Đặt giới hạn ngân sách, lượt dùng tối đa, đơn tối thiểu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ma_giam_gia")
public class MaGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(min = 3, max = 50, message = "Mã voucher phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Mã voucher chỉ gồm chữ cái in hoa, chữ số và dấu gạch nối (VD: SALE20, SIMPLAIN50K)")
    @Column(name = "ma_code", nullable = false, length = 50, unique = true)
    private String maCode;

    @NotBlank(message = "Tên chương trình voucher không được để trống")
    @Size(min = 3, max = 150, message = "Tên voucher phải từ 3 đến 150 ký tự")
    @Nationalized
    @Column(name = "ten_voucher", nullable = false, length = 150, columnDefinition = "nvarchar(150)")
    private String tenVoucher;

    @NotBlank(message = "Loại giảm giá không được để trống")
    @Pattern(regexp = "^(PERCENT|FIXED_AMOUNT)$", message = "Loại giảm giá phải là PERCENT (% phần trăm) hoặc FIXED_AMOUNT (Số tiền cố định)")
    @Column(name = "loai_giam_gia", nullable = false, length = 20)
    private String loaiGiamGia;

    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị giảm phải lớn hơn 0")
    @Column(name = "gia_tri_giam", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaTriGiam;

    @Column(name = "giam_toi_da", precision = 18, scale = 2)
    private BigDecimal giamToiDa;

    @Builder.Default
    @NotNull(message = "Giá trị đơn tối thiểu không được để trống")
    @DecimalMin(value = "0.00", message = "Giá trị đơn tối thiểu không được âm")
    @Column(name = "gia_tri_don_toi_thieu", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaTriDonToiThieu = BigDecimal.ZERO;

    @Column(name = "tong_so_luong")
    private Integer tongSoLuong;

    @Builder.Default
    @Column(name = "so_luong_da_dung", nullable = false)
    private Integer soLuongDaDung = 0;

    @Builder.Default
    @Column(name = "gioi_han_moi_user", nullable = false)
    private Integer gioiHanMoiUser = 1;

    @Column(name = "ngan_sach", precision = 18, scale = 2)
    private BigDecimal nganSach;

    @Builder.Default
    @Column(name = "ngan_sach_da_dung", precision = 18, scale = 2, nullable = false)
    private BigDecimal nganSachDaDung = BigDecimal.ZERO;

    @NotNull(message = "Ngày bắt đầu áp dụng không được để trống")
    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Builder.Default
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;

    @Nationalized
    @Column(name = "mo_ta", length = 500, columnDefinition = "nvarchar(500)")
    private String moTa;

    @Builder.Default
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.soLuongDaDung == null) {
            this.soLuongDaDung = 0;
        }
        if (this.nganSachDaDung == null) {
            this.nganSachDaDung = BigDecimal.ZERO;
        }
        if (this.gioiHanMoiUser == null || this.gioiHanMoiUser <= 0) {
            this.gioiHanMoiUser = 1;
        }
        if (this.giaTriDonToiThieu == null) {
            this.giaTriDonToiThieu = BigDecimal.ZERO;
        }
        if (this.kichHoat == null) {
            this.kichHoat = true;
        }
        if (this.maCode != null) {
            this.maCode = this.maCode.trim().toUpperCase();
        }
    }

    // =========================================================================
    // HELPER METHODS: ĐÁNH GIÁ TRẠNG THÁI & TIẾN ĐỘ VOUCHER
    // =========================================================================

    public boolean isTamNgung() {
        return Boolean.FALSE.equals(this.kichHoat);
    }

    public boolean isSapDienRa() {
        if (isTamNgung()) return false;
        return LocalDateTime.now().isBefore(this.ngayBatDau);
    }

    public boolean isDaKetThuc() {
        return LocalDateTime.now().isAfter(this.ngayKetThuc);
    }

    public boolean isHetLuot() {
        return this.tongSoLuong != null && this.soLuongDaDung != null && this.soLuongDaDung >= this.tongSoLuong;
    }

    public boolean isHetNganSach() {
        return this.nganSach != null && this.nganSachDaDung != null
                && this.nganSach.compareTo(BigDecimal.ZERO) > 0
                && this.nganSachDaDung.compareTo(this.nganSach) >= 0;
    }

    public boolean isDangDienRa() {
        if (isTamNgung() || isDaKetThuc() || isSapDienRa() || isHetLuot() || isHetNganSach()) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(this.ngayBatDau) && !now.isAfter(this.ngayKetThuc);
    }

    /**
     * Tên trạng thái phục vụ hiển thị Badge thân thiện.
     */
    public String getTrangThaiLabel() {
        if (isTamNgung()) return "Tạm ngưng";
        if (isDaKetThuc()) return "Đã hết hạn";
        if (isHetLuot()) return "Hết lượt dùng";
        if (isHetNganSach()) return "Hết ngân sách";
        if (isSapDienRa()) return "Sắp diễn ra";
        return "Đang diễn ra";
    }

    /**
     * Class CSS màu sắc Badge trạng thái chuẩn Tailwind.
     */
    public String getTrangThaiBadgeClass() {
        if (isTamNgung()) {
            return "bg-slate-100 text-slate-700 border-slate-300";
        }
        if (isDaKetThuc()) {
            return "bg-rose-100 text-rose-800 border-rose-300";
        }
        if (isHetLuot() || isHetNganSach()) {
            return "bg-amber-100 text-amber-800 border-amber-300";
        }
        if (isSapDienRa()) {
            return "bg-sky-100 text-sky-800 border-sky-300";
        }
        return "bg-emerald-100 text-emerald-800 border-emerald-300";
    }

    /**
     * Tính % lượt dùng đã phát hành (0 - 100).
     */
    public int getPhanTramSuDung() {
        if (this.tongSoLuong == null || this.tongSoLuong <= 0) {
            return 0;
        }
        int daDung = this.soLuongDaDung != null ? this.soLuongDaDung : 0;
        int pct = (int) Math.round(((double) daDung / this.tongSoLuong) * 100);
        return Math.min(pct, 100);
    }

    /**
     * Tính % ngân sách đã giải ngân (0 - 100).
     */
    public int getPhanTramNganSach() {
        if (this.nganSach == null || this.nganSach.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        BigDecimal daChi = this.nganSachDaDung != null ? this.nganSachDaDung : BigDecimal.ZERO;
        BigDecimal pct = daChi.divide(this.nganSach, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return Math.min(pct.intValue(), 100);
    }
}
