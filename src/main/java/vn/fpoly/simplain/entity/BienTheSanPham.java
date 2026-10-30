package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Biến thể sản phẩm (SKU) [bien_the_san_pham] kết hợp Sản phẩm + Màu sắc + Kích thước,
 * quản lý Giá bán, Giá gốc và Số lượng tồn kho (US09).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bien_the_san_pham")
public class BienTheSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Vui lòng chọn sản phẩm gốc")
    @Column(name = "ma_san_pham_id", nullable = false)
    private Long maSanPhamId;

    @NotNull(message = "Vui lòng chọn màu sắc cho biến thể")
    @Column(name = "ma_mau_sac_id", nullable = false)
    private Long maMauSacId;

    @NotNull(message = "Vui lòng chọn kích thước (Size) cho biến thể")
    @Column(name = "ma_kich_thuoc_id", nullable = false)
    private Long maKichThuocId;

    @NotBlank(message = "Mã SKU không được để trống")
    @Size(min = 3, max = 100, message = "Mã SKU phải từ 3 đến 100 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Mã SKU chỉ gồm chữ cái không dấu, chữ số, dấu gạch ngang (-) hoặc gạch dưới (_) (VD: SP-POLO-01-DEN-L)")
    @Column(name = "ma_sku", nullable = false, length = 100, unique = true)
    private String maSku;

    @Size(max = 100, message = "Mã vạch Barcode không được vượt quá 100 ký tự")
    @Pattern(regexp = "^$|^[A-Za-z0-9_-]+$", message = "Mã vạch Barcode chỉ gồm chữ cái, chữ số, dấu gạch ngang (-) hoặc gạch dưới (_)")
    @Column(name = "ma_vach_barcode", length = 100, unique = true)
    private String maVachBarcode;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được nhỏ hơn 0")
    @Max(value = 999999, message = "Số lượng tồn kho không được vượt quá 999.999")
    @Builder.Default
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 0;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá bán không được nhỏ hơn 0 VNĐ")
    @DecimalMax(value = "999999999.0", message = "Giá bán không được vượt quá 999.999.999 VNĐ")
    @Builder.Default
    @Column(name = "gia_ban", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBan = BigDecimal.ZERO;

    @NotNull(message = "Giá gốc không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá gốc không được nhỏ hơn 0 VNĐ")
    @DecimalMax(value = "999999999.0", message = "Giá gốc không được vượt quá 999.999.999 VNĐ")
    @Builder.Default
    @Column(name = "gia_goc", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaGoc = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;

    @PrePersist
    protected void onCreate() {
        if (this.soLuong == null) {
            this.soLuong = 0;
        }
        if (this.giaBan == null) {
            this.giaBan = BigDecimal.ZERO;
        }
        if (this.giaGoc == null) {
            this.giaGoc = BigDecimal.ZERO;
        }
        if (this.kichHoat == null) {
            this.kichHoat = true;
        }
    }

    // Các trường tạm (@Transient) phục vụ hiển thị trực quan trên giao diện Web MVC
    @Transient
    private String maSanPham;

    @Transient
    private String tenSanPham;

    @Transient
    private String duongDanSlug;

    @Transient
    private String tenMauSac;

    @Transient
    private String maMauHex;

    @Transient
    private String maKichThuoc;

    @Transient
    private String tenKichThuoc;

    @Transient
    private String anhDaiDienUrl;

    @Transient
    @Builder.Default
    private List<HinhAnh> danhSachHinhAnh = new ArrayList<>();
}
