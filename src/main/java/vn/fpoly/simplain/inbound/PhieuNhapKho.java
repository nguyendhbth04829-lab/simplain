package vn.fpoly.simplain.inbound;

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
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phieu_nhap_kho")
public class PhieuNhapKho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_phieu", nullable = false, unique = true, length = 50)
    private String maPhieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_ncc_id", nullable = false)
    private NhaCungCap nhaCungCap;

    @Column(name = "ma_nhan_vien_id")
    private Long maNhanVienId;

    @Column(name = "ngay_nhap")
    private LocalDateTime ngayNhap;

    @Column(name = "tong_tien", precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 30)
    @Builder.Default
    private TrangThaiPhieuNhapKho trangThai = TrangThaiPhieuNhapKho.CHO_NHAP;

    @Nationalized
    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "phieuNhapKho", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChiTietPhieuNhapKho> chiTietList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (ngayNhap == null) ngayNhap = LocalDateTime.now();
        if (trangThai == null) trangThai = TrangThaiPhieuNhapKho.CHO_NHAP;
        if (tongTien == null) tongTien = BigDecimal.ZERO;
    }

    /**
     * Cổng chuyển trạng thái duy nhất của phiếu nhập kho.
     * Mọi thay đổi trạng thái phải đi qua đây để hệ thống từ chối các bước nhảy không hợp lệ.
     */
    public void chuyenSang(TrangThaiPhieuNhapKho trangThaiMoi) {
        if (trangThaiMoi == null) {
            throw new IllegalArgumentException("Trạng thái đích của phiếu nhập kho không được để trống");
        }
        if (trangThai == null) {
            trangThai = TrangThaiPhieuNhapKho.CHO_NHAP;
        }
        if (!trangThai.coTheChuyenSang(trangThaiMoi)) {
            throw new IllegalStateException(
                    "Không thể chuyển phiếu nhập kho từ [" + trangThai.getNhan() + "] sang [" + trangThaiMoi.getNhan() + "]");
        }
        this.trangThai = trangThaiMoi;
    }
}
