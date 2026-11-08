package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Phiếu nhập hàng từ nhà cung cấp [phieu_nhap_hang].
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phieu_nhap_hang")
public class PhieuNhapHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_phieu_nhap", nullable = false, unique = true, length = 50)
    private String maPhieuNhap;

    @Column(name = "ma_nha_cung_cap_id", nullable = false)
    private Long maNhaCungCapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nha_cung_cap_id", insertable = false, updatable = false)
    private NhaCungCap nhaCungCap;

    @Column(name = "ma_nhan_vien_id")
    private Long maNhanVienId;

    @Column(name = "ngay_nhap", nullable = false)
    private LocalDateTime ngayNhap;

    @Builder.Default
    @Column(name = "tong_so_luong", nullable = false)
    private Integer tongSoLuong = 0;

    @Builder.Default
    @Column(name = "tong_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTien = BigDecimal.ZERO;

    /**
     * Trạng thái phiếu nhập:
     * CHO_NHAP: Mới tạo dự thảo, chưa cập nhật kho
     * DA_NHAP: Đã hoàn tất nhập kho, số lượng đã được cộng vào tồn kho
     * DA_HUY: Đã hủy phiếu nhập
     */
    @Builder.Default
    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = "CHO_NHAP";

    @Nationalized
    @Column(name = "ghi_chu", length = 500, columnDefinition = "nvarchar(500)")
    private String ghiChu;

    @OneToMany(mappedBy = "phieuNhapHang", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChiTietPhieuNhapHang> danhSachChiTiet = new ArrayList<>();

    @Transient
    private String tenNhaCungCap;

    @Transient
    private Boolean nhaCungCapChinhHang;
}
