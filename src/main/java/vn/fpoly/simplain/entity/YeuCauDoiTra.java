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
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Yêu cầu Đổi/Trả (RMA - Return Merchandise Authorization) [yeu_cau_doi_tra] (US24).
 * Cho phép khách hàng gửi yêu cầu đổi size/màu (SKU mới) hoặc trả hàng hoàn tiền online.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "yeu_cau_doi_tra")
public class YeuCauDoiTra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_yeu_cau_rma", length = 50, nullable = false, unique = true)
    private String maYeuCauRma;

    @Column(name = "ma_don_hang_id", nullable = false)
    private Long maDonHangId;

    @Column(name = "ma_khach_hang_id", nullable = false)
    private Long maKhachHangId;

    /**
     * Phân loại yêu cầu:
     * - DOI_HANG: Khách đổi sang SKU/biến thể mới (màu, size).
     * - TRA_HANG_HOAN_TIEN: Khách trả hàng và nhận lại tiền qua tài khoản ngân hàng.
     */
    @Column(name = "loai_doi_tra", length = 50, nullable = false)
    private String loaiDoiTra;

    @Nationalized
    @Column(name = "ly_do", length = 255, nullable = false, columnDefinition = "nvarchar(255)")
    private String lyDo;

    @Column(name = "so_tien_hoan", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal soTienHoan = BigDecimal.ZERO;

    @Nationalized
    @Column(name = "thong_tin_ngan_hang", length = 255, columnDefinition = "nvarchar(255)")
    private String thongTinNganHang;

    /**
     * Trạng thái xử lý RMA:
     * - CHO_XU_LY: Đang chờ nhân viên/admin tiếp nhận xem xét
     * - DA_TIEP_NHAN: Đã tiếp nhận yêu cầu, chờ gửi hàng về kho
     * - DANG_XU_LY: Đã nhận hàng và đang tiến hành đổi hàng / chuyển khoản hoàn tiền
     * - HOAN_THANH: Yêu cầu đổi trả đã hoàn tất thành công
     * - TU_CHOI: Yêu cầu bị từ chối do vi phạm chính sách hoặc không hợp lệ
     */
    @Column(name = "trang_thai", length = 50, nullable = false)
    @Builder.Default
    private String trangThai = "CHO_XU_LY";

    @Column(name = "nguoi_duyet_id")
    private Long nguoiDuyetId;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Nationalized
    @Column(name = "hinh_anh_minh_chung", length = 2000, columnDefinition = "nvarchar(2000)")
    private String hinhAnhMinhChung;

    @Nationalized
    @Column(name = "ten_nguoi_nhan", length = 150, columnDefinition = "nvarchar(150)")
    private String tenNguoiNhan;

    @Column(name = "so_dien_thoai_nhan", length = 20)
    private String soDienThoaiNhan;

    @Nationalized
    @Column(name = "dia_chi_nhan_hang", length = 500, columnDefinition = "nvarchar(500)")
    private String diaChiNhanHang;

    @Nationalized
    @Column(name = "ghi_chu_giao_hang", length = 255, columnDefinition = "nvarchar(255)")
    private String ghiChuGiaoHang;

    @Nationalized
    @Column(name = "ghi_chu_admin", length = 500, columnDefinition = "nvarchar(500)")
    private String ghiChuAdmin;

    @Nationalized
    @Column(name = "hinh_anh_bill", length = 1000, columnDefinition = "nvarchar(1000)")
    private String hinhAnhBill;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don_hang_id", insertable = false, updatable = false)
    private DonHang donHang;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang_id", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_duyet_id", insertable = false, updatable = false)
    private NhanVien nguoiDuyet;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "yeuCauDoiTra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietSanPhamDoiTra> chiTietList = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.trangThai == null || this.trangThai.isBlank()) {
            this.trangThai = "CHO_XU_LY";
        }
        if (this.soTienHoan == null) {
            this.soTienHoan = BigDecimal.ZERO;
        }
    }

    /**
     * Tách chuỗi URL ảnh minh chứng thành danh sách để hiển thị album ảnh chuẩn Shopee.
     */
    public List<String> getDanhSachHinhAnh() {
        List<String> list = new ArrayList<>();
        if (this.hinhAnhMinhChung != null && !this.hinhAnhMinhChung.isBlank()) {
            String[] parts = this.hinhAnhMinhChung.split(",");
            for (String p : parts) {
                String trimmed = p.trim();
                if (!trimmed.isEmpty()) {
                    list.add(trimmed);
                }
            }
        }
        return list;
    }
}
