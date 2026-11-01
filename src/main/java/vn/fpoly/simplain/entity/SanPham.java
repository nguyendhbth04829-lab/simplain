package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Thực thể Sản phẩm [san_pham] lưu trữ thông tin chung của sản phẩm thời trang SIMPLAIN STORE (US08, US10).
 *
 * <p>Đáp ứng tiêu chí US08 & US10:
 * 1. Tạo san_pham gắn với danh mục (ma_danh_muc_id) và bộ sưu tập tùy chọn (ma_bo_suu_tap_id).
 * 2. Nhập chất liệu (chat_lieu), form dáng (form_dang), mô tả chi tiết (mo_ta_chi_tiet) và hướng dẫn chọn size.
 * 3. Hỗ trợ hiển thị Grid sản phẩm, tìm kiếm và lọc đa tiêu chí trên Storefront (US10).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "san_pham")
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Vui lòng chọn danh mục cho sản phẩm")
    @Column(name = "ma_danh_muc_id", nullable = false)
    private Long maDanhMucId;

    @Column(name = "ma_bo_suu_tap_id")
    private Long maBoSuuTapId;

    @NotBlank(message = "Mã sản phẩm không được để trống")
    @Size(min = 3, max = 50, message = "Mã sản phẩm phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Mã sản phẩm chỉ gồm chữ cái không dấu, chữ số, dấu gạch ngang (-) hoặc gạch dưới (_) (VD: SP-POLO-001)")
    @Column(name = "ma_san_pham", nullable = false, length = 50, unique = true)
    private String maSanPham;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 3, max = 255, message = "Tên sản phẩm phải từ 3 đến 255 ký tự")
    @Nationalized
    @Column(name = "ten_san_pham", nullable = false, length = 255, columnDefinition = "nvarchar(255)")
    private String tenSanPham;

    @NotBlank(message = "Đường dẫn slug không được để trống")
    @Size(min = 3, max = 255, message = "Đường dẫn slug phải từ 3 đến 255 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug chỉ gồm chữ thường không dấu, chữ số và dấu gạch ngang (VD: ao-polo-nam-excool)")
    @Column(name = "duong_dan_slug", nullable = false, length = 255, unique = true)
    private String duongDanSlug;

    @Column(name = "ma_chat_lieu_id")
    private Long maChatLieuId;

    @Column(name = "ma_form_dang_id")
    private Long maFormDangId;

    @NotBlank(message = "Chất liệu sản phẩm không được để trống")
    @Size(min = 2, max = 150, message = "Chất liệu sản phẩm phải từ 2 đến 150 ký tự")
    @Nationalized
    @Column(name = "chat_lieu", length = 150, columnDefinition = "nvarchar(150)")
    private String chatLieu;

    @NotBlank(message = "Form dáng sản phẩm không được để trống")
    @Size(min = 2, max = 100, message = "Form dáng sản phẩm phải từ 2 đến 100 ký tự")
    @Nationalized
    @Column(name = "form_dang", length = 100, columnDefinition = "nvarchar(100)")
    private String formDang;

    @NotBlank(message = "Mô tả chi tiết sản phẩm không được để trống")
    @Size(min = 10, max = 5000, message = "Mô tả chi tiết sản phẩm phải từ 10 đến 5000 ký tự")
    @Nationalized
    @Column(name = "mo_ta_chi_tiet", columnDefinition = "ntext")
    private String moTaChiTiet;

    @Size(max = 3000, message = "Hướng dẫn chọn size không được vượt quá 3000 ký tự")
    @Nationalized
    @Column(name = "huong_dan_chon_size", columnDefinition = "ntext")
    private String huongDanChonSize;

    @Size(max = 500, message = "Đường dẫn video quảng cáo không được vượt quá 500 ký tự")
    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @NotNull(message = "Giá cơ bản không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá cơ bản không được nhỏ hơn 0 VNĐ")
    @DecimalMax(value = "999999999.0", message = "Giá cơ bản không được vượt quá 999.999.999 VNĐ")
    @Builder.Default
    @Column(name = "gia_co_ban", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaCoBan = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "noi_bat")
    private Boolean noiBat = false;

    @Builder.Default
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.giaCoBan == null) {
            this.giaCoBan = BigDecimal.ZERO;
        }
        if (this.noiBat == null) {
            this.noiBat = false;
        }
        if (this.kichHoat == null) {
            this.kichHoat = true;
        }
    }

    /**
     * Trường tạm phục vụ hiển thị tên danh mục trên giao diện Web.
     */
    @Transient
    private String tenDanhMuc;

    /**
     * Trường tạm phục vụ hiển thị tên bộ sưu tập trên giao diện Web.
     */
    @Transient
    private String tenBoSuuTap;

    /**
     * Trường tạm phục vụ hiển thị số lượng biến thể SKU của sản phẩm.
     */
    @Transient
    private long soBienThe;

    /**
     * Trường tạm lưu đường dẫn ảnh đại diện hiển thị trên Grid Storefront (US10).
     */
    @Transient
    private String anhDaiDienUrl;

    /**
     * Trường tạm lưu giá bán thấp nhất trong các biến thể SKU (hoặc giá cơ bản) trên Storefront (US10).
     */
    @Transient
    private BigDecimal giaThapNhat;

    /**
     * Trường tạm lưu giá bán cao nhất trong các biến thể SKU trên Storefront (US10).
     */
    @Transient
    private BigDecimal giaCaoNhat;

    /**
     * Trường tạm lưu tổng số lượng tồn kho của các biến thể đang mở bán (US10).
     */
    @Transient
    private int tongTonKho;

    /**
     * Danh sách các màu sắc đang có của sản phẩm để hiển thị chấm màu trên thẻ sản phẩm Storefront (US10).
     */
    @Transient
    @Builder.Default
    private List<MauSac> danhSachMauSac = new ArrayList<>();

    /**
     * Danh sách các kích thước (Size) đang có của sản phẩm để hiển thị trên thẻ sản phẩm Storefront (US10, US11).
     */
    @Transient
    @Builder.Default
    private List<KichThuoc> danhSachKichThuoc = new ArrayList<>();

    /**
     * Danh sách các biến thể SKU đang mở bán kèm ảnh và tồn kho thực tế phục vụ trang Chi tiết sản phẩm (US11).
     */
    @Transient
    @Builder.Default
    private List<BienTheSanPham> danhSachBienThe = new ArrayList<>();

    /**
     * Danh sách toàn bộ hình ảnh của sản phẩm và các biến thể phục vụ Gallery trang Chi tiết sản phẩm (US11).
     */
    @Transient
    @Builder.Default
    private List<HinhAnh> danhSachHinhAnh = new ArrayList<>();

    /**
     * Getter tương thích cho client/API sử dụng tên bienTheSanPhams
     */
    @JsonProperty("bienTheSanPhams")
    public List<BienTheSanPham> getBienTheSanPhams() {
        return this.danhSachBienThe != null ? this.danhSachBienThe : Collections.emptyList();
    }

    /**
     * Getter tương thích cho client/API sử dụng tên giaBanThapNhat
     */
    @JsonProperty("giaBanThapNhat")
    public BigDecimal getGiaBanThapNhat() {
        return this.giaThapNhat != null ? this.giaThapNhat : (this.giaCoBan != null ? this.giaCoBan : BigDecimal.ZERO);
    }
}
