package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thực thể Yêu cầu Đổi/Trả (RMA), ánh xạ bảng 'yeu_cau_doi_tra'.
 * Quản lý quy trình khiếu nại, đổi sản phẩm hoặc trả hàng hoàn tiền (US24, US25).
 */
@Entity
@Table(name = "yeu_cau_doi_tra")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YeuCauDoiTra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_yeu_cau_rma", unique = true, nullable = false, length = 50)
    private String maYeuCauRma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don_hang_id", nullable = false)
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang_id", nullable = false)
    private KhachHang khachHang;

    @Column(name = "loai_doi_tra", nullable = false, length = 50)
    private String loaiDoiTra;

    @Column(name = "ly_do", nullable = false, length = 255)
    private String lyDo;

    @Builder.Default
    @Column(name = "so_tien_hoan", precision = 18, scale = 2)
    private BigDecimal soTienHoan = BigDecimal.ZERO;

    @Column(name = "thong_tin_ngan_hang", length = 255)
    private String thongTinNganHang;

    @Builder.Default
    @Column(name = "trang_thai", nullable = false, length = 50)
    private String trangThai = "CHO_DUYET";

    @Column(name = "nguoi_duyet_id")
    private Long nguoiDuyetId;

    @Column(name = "ghi_chu_kiem_dinh", length = 500)
    private String ghiChuKiemDinh;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.trangThai == null) {
            this.trangThai = "CHO_DUYET";
        }
        if (this.soTienHoan == null) {
            this.soTienHoan = BigDecimal.ZERO;
        }
    }
}
