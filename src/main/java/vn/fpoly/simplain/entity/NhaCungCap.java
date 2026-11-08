package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

/**
 * Thực thể Nhà cung cấp hàng hóa [nha_cung_cap].
 * Hỗ trợ đánh dấu nhà cung cấp lớn chính hãng có mã quét barcode trên từng sản phẩm.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nha_cung_cap")
public class NhaCungCap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Mã nhà cung cấp không được để trống")
    @Size(min = 2, max = 50, message = "Mã nhà cung cấp từ 2 đến 50 ký tự")
    @Column(name = "ma_nha_cung_cap", nullable = false, unique = true, length = 50)
    private String maNhaCungCap;

    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    @Size(min = 2, max = 255, message = "Tên nhà cung cấp từ 2 đến 255 ký tự")
    @Nationalized
    @Column(name = "ten_nha_cung_cap", nullable = false, length = 255, columnDefinition = "nvarchar(255)")
    private String tenNhaCungCap;

    @Pattern(regexp = "^(0[0-9]{9,10})?$", message = "Số điện thoại không hợp lệ")
    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "email", length = 100)
    private String email;

    @Nationalized
    @Column(name = "dia_chi", length = 500, columnDefinition = "nvarchar(500)")
    private String diaChi;

    @Column(name = "ma_so_thue", length = 50)
    private String maSoThue;

    @Nationalized
    @Column(name = "ghi_chu", length = 500, columnDefinition = "nvarchar(500)")
    private String ghiChu;

    /**
     * Đánh dấu nhà cung cấp lớn chính hãng (ví dụ Vinatex, May 10, Việt Tiến, YKK, Cotton USA...).
     * Sản phẩm từ các hãng này luôn đi kèm mã quét barcode chuẩn EAN/GS1.
     */
    @Builder.Default
    @Column(name = "chinh_hang", nullable = false)
    private Boolean chinhHang = true;

    @Builder.Default
    @Column(name = "kich_hoat", nullable = false)
    private Boolean kichHoat = true;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.chinhHang == null) {
            this.chinhHang = true;
        }
        if (this.kichHoat == null) {
            this.kichHoat = true;
        }
    }
}
