/**
 * Tác giả: Gia Minh
 * Chức năng: US15 - Thực thể đánh giá sản phẩm trong CSDL Simpl4
 */
package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Thực thể đánh giá và nhận xét sản phẩm của khách hàng.
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

    @NotNull(message = "Mã sản phẩm không được để trống")
    @Column(name = "ma_san_pham_id", nullable = false)
    private Long maSanPhamId;

    @NotNull(message = "Mã khách hàng không được để trống")
    @Column(name = "ma_khach_hang_id", nullable = false)
    private Long maKhachHangId;

    @Column(name = "ma_chi_tiet_don_hang_id")
    private Long maChiTietDonHangId;

    @NotNull(message = "Số sao đánh giá không được để trống")
    @Min(value = 1, message = "Số sao tối thiểu là 1")
    @Max(value = 5, message = "Số sao tối đa là 5")
    @Column(name = "so_sao", nullable = false)
    private Integer soSao;

    @Column(name = "noi_dung", columnDefinition = "NTEXT")
    private String noiDung;

    @Column(name = "danh_sach_anh_json", columnDefinition = "NTEXT")
    private String danhSachAnhJson;

    @Builder.Default
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();
}
