package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;

/**
 * Chi tiết từng mặt hàng trong Phiếu nhập hàng [chi_tiet_phieu_nhap_hang].
 * Lưu trữ mã quét barcode của nhà cung cấp lớn chính hãng trên từng sản phẩm.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chi_tiet_phieu_nhap_hang")
public class ChiTietPhieuNhapHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_phieu_nhap_id", nullable = false)
    private Long maPhieuNhapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phieu_nhap_id", insertable = false, updatable = false)
    private PhieuNhapHang phieuNhapHang;

    @Column(name = "ma_bien_the_id", nullable = false)
    private Long maBienTheId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bien_the_id", insertable = false, updatable = false)
    private BienTheSanPham bienTheSanPham;

    /**
     * Mã quét Barcode / QR trên sản phẩm do nhà cung cấp chính hãng in sẵn trên tem mác.
     */
    @Column(name = "ma_vach_barcode", length = 100)
    private String maVachBarcode;

    @Column(name = "so_luong_nhap", nullable = false)
    private Integer soLuongNhap;

    @Column(name = "gia_nhap", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaNhap;

    @Column(name = "thanh_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal thanhTien;

    @Nationalized
    @Column(name = "ghi_chu", length = 255, columnDefinition = "nvarchar(255)")
    private String ghiChu;

    @Transient
    private String tenSanPham;

    @Transient
    private String maSku;

    @Transient
    private String tenMauSac;

    @Transient
    private String maKichThuoc;

    @Transient
    private String anhUrl;

    @Transient
    private Integer tonKhoHienTai;
}
