package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thực thể Hình ảnh sản phẩm & biến thể [hinh_anh] hỗ trợ upload nhiều hình ảnh cho mỗi SKU (US09).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hinh_anh")
public class HinhAnh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Mã sản phẩm không được để trống")
    @Column(name = "ma_san_pham_id", nullable = false)
    private Long maSanPhamId;

    @Column(name = "ma_bien_the_id")
    private Long maBienTheId;

    @NotBlank(message = "Đường dẫn hình ảnh không được để trống")
    @Size(max = 500, message = "Đường dẫn hình ảnh không được vượt quá 500 ký tự")
    @Column(name = "duong_dan_anh", nullable = false, length = 500)
    private String duongDanAnh;

    @Builder.Default
    @Column(name = "la_anh_dai_dien")
    private Boolean laAnhDaiDien = false;

    @Builder.Default
    @Column(name = "thu_tu")
    private Integer thuTu = 0;

    @PrePersist
    protected void onCreate() {
        if (this.laAnhDaiDien == null) {
            this.laAnhDaiDien = false;
        }
        if (this.thuTu == null) {
            this.thuTu = 0;
        }
    }
}
