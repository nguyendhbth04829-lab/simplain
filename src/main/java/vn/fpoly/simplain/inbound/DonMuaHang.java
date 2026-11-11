package vn.fpoly.simplain.inbound;

import jakarta.persistence.*;
import lombok.*;
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
@Table(name = "don_mua_hang")
public class DonMuaHang {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_don", nullable = false, unique = true, length = 50)
    private String maDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_ncc_id", nullable = false)
    private NhaCungCap nhaCungCap;

    @Column(name = "ma_nhan_vien_id")
    private Long maNhanVienId;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 40)
    @Builder.Default
    private TrangThaiDonMuaHang trangThai = TrangThaiDonMuaHang.NHAP;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "tong_tien", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Nationalized
    @Column(name = "dieu_kien_giao_hang", nullable = false, length = 255)
    @Builder.Default
    private String dieuKienGiaoHang = "Nhà cung cấp chịu trách nhiệm giao đến cửa hàng";

    @Nationalized
    @Column(name = "ghi_chu", length = 1000)
    private String ghiChu;

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "donMuaHang", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChiTietDonMuaHang> chiTietList = new ArrayList<>();

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "donMuaHang", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LanGiaoHang> lanGiaoList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
        if (trangThai == null) trangThai = TrangThaiDonMuaHang.NHAP;
        if (tongTien == null) tongTien = BigDecimal.ZERO;
        if (dieuKienGiaoHang == null || dieuKienGiaoHang.isBlank()) {
            dieuKienGiaoHang = "Nhà cung cấp chịu trách nhiệm giao đến cửa hàng";
        }
    }

    public void chuyenSang(TrangThaiDonMuaHang moi) {
        if (trangThai == null || !trangThai.choPhepChuyenSang(moi)) {
            throw new IllegalStateException("Không thể chuyển đơn mua từ ["
                    + (trangThai == null ? "chưa xác định" : trangThai.getNhan()) + "] sang ["
                    + (moi == null ? "chưa xác định" : moi.getNhan()) + "]");
        }
        trangThai = moi;
    }
}
