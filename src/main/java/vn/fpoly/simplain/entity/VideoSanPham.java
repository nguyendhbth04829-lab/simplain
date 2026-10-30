package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

/**
 * Thực thể Quản lý Video quảng cáo sản phẩm [video_san_pham].
 * Mỗi sản phẩm có 1 video quảng cáo/review thực tế (Ràng buộc Unique 1-1).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video_san_pham")
public class VideoSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Vui lòng chọn sản phẩm gắn video quảng cáo")
    @Column(name = "ma_san_pham_id", nullable = false, unique = true)
    private Long maSanPhamId;

    @NotBlank(message = "Tiêu đề video quảng cáo không được để trống")
    @Size(min = 3, max = 255, message = "Tiêu đề video phải từ 3 đến 255 ký tự")
    @Nationalized
    @Column(name = "tieu_de", nullable = false, length = 255, columnDefinition = "nvarchar(255)")
    private String tieuDe;

    @Size(max = 500, message = "Đường dẫn video URL tối đa 500 ký tự")
    @Column(name = "video_url", nullable = false, length = 500)
    private String videoUrl;

    @Size(max = 500, message = "Mô tả video không quá 500 ký tự")
    @Nationalized
    @Column(name = "mo_ta", length = 500, columnDefinition = "nvarchar(500)")
    private String moTa;

    @Builder.Default
    @Column(name = "kich_hoat", nullable = false)
    private Boolean kichHoat = true;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.kichHoat == null) {
            this.kichHoat = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }

    // Các trường Transient phục vụ hiển thị nhanh trên giao diện
    @Transient
    private String tenSanPham;

    @Transient
    private String maSanPham;

    @Transient
    private String anhSanPham;
}
