package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thực thể Đánh giá sản phẩm [danh_gia_san_pham] (US15).
 * Lưu nhận xét, số sao (1-5 sao) và danh sách ảnh dạng JSON từ khách hàng đã mua hàng hoàn thành.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "danh_gia_san_pham")
public class DanhGiaSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_san_pham_id", nullable = false)
    private Long maSanPhamId;

    @Column(name = "ma_khach_hang_id", nullable = false)
    private Long maKhachHangId;

    @Column(name = "ma_chi_tiet_don_hang_id")
    private Long maChiTietDonHangId;

    @Column(name = "so_sao", nullable = false)
    private Integer soSao;

    @Nationalized
    @Column(name = "noi_dung", columnDefinition = "ntext")
    private String noiDung;

    @Nationalized
    @Column(name = "danh_sach_anh_json", columnDefinition = "ntext")
    private String danhSachAnhJson;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_san_pham_id", insertable = false, updatable = false)
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang_id", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chi_tiet_don_hang_id", insertable = false, updatable = false)
    private ChiTietDonHang chiTietDonHang;

    @Transient
    @Builder.Default
    private List<String> danhSachAnh = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.soSao == null) {
            this.soSao = 5;
        }
    }
}
