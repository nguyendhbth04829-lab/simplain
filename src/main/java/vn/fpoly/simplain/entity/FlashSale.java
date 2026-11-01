package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Khung giờ Flash Sale [flash_sale] (US29 - Mod 6 / Bán hàng - Khoa).
 * - Tiêu chí 1: Đặt khung giờ Bắt đầu / Kết thúc.
 * - Tiêu chí 2: Gán giá sốc và giới hạn số lượng (so_luong_mo_ban) cho biến thể.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flash_sale")
public class FlashSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Tiêu đề đợt Flash Sale không được để trống")
    @Size(min = 3, max = 150, message = "Tiêu đề đợt Flash Sale phải từ 3 đến 150 ký tự")
    @Nationalized
    @Column(name = "tieu_de", nullable = false, length = 150, columnDefinition = "nvarchar(150)")
    private String tieuDe;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @Column(name = "thoi_gian_bat_dau", nullable = false)
    private LocalDateTime thoiGianBatDau;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @Column(name = "thoi_gian_ket_thuc", nullable = false)
    private LocalDateTime thoiGianKetThuc;

    @Builder.Default
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;

    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @Transient
    @Builder.Default
    private List<ChiTietFlashSale> danhSachChiTiet = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.kichHoat == null) {
            this.kichHoat = true;
        }
    }

    // =========================================================================
    // TRANSIENT & CONVENIENCE METHODS
    // =========================================================================

    @Transient
    public String getTrangThai() {
        if (Boolean.FALSE.equals(kichHoat)) {
            return "TAM_DUNG";
        }
        LocalDateTime now = LocalDateTime.now();
        if (thoiGianBatDau != null && now.isBefore(thoiGianBatDau)) {
            return "SAP_DIEN_RA";
        }
        if (thoiGianKetThuc != null && now.isAfter(thoiGianKetThuc)) {
            return "DA_KET_THUC";
        }
        return "DANG_DIEN_RA";
    }

    @Transient
    public int getTongSoLuongMoBan() {
        if (danhSachChiTiet == null || danhSachChiTiet.isEmpty()) return 0;
        return danhSachChiTiet.stream()
                .mapToInt(ct -> ct.getSoLuongMoBan() != null ? ct.getSoLuongMoBan() : 0)
                .sum();
    }

    @Transient
    public int getTongSoLuongDaBan() {
        if (danhSachChiTiet == null || danhSachChiTiet.isEmpty()) return 0;
        return danhSachChiTiet.stream()
                .mapToInt(ct -> ct.getSoLuongDaBan() != null ? ct.getSoLuongDaBan() : 0)
                .sum();
    }

    @Transient
    public int getSoLuongBienThe() {
        return danhSachChiTiet != null ? danhSachChiTiet.size() : 0;
    }

    @Transient
    public double getTiLeDaBanPhanTram() {
        int moBan = getTongSoLuongMoBan();
        if (moBan <= 0) return 0.0;
        return Math.min(100.0, (double) getTongSoLuongDaBan() * 100.0 / moBan);
    }
}
