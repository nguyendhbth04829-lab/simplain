package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

/**
 * Thực thể Danh mục sản phẩm [danh_muc] hỗ trợ phân cấp Danh mục Cha / Con (US06).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "danh_muc")
public class DanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_danh_muc_cha")
    private Long maDanhMucCha;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 150, message = "Tên danh mục phải từ 2 đến 150 ký tự")
    @Nationalized
    @Column(name = "ten_danh_muc", nullable = false, length = 150, columnDefinition = "nvarchar(150)")
    private String tenDanhMuc;

    @NotBlank(message = "Đường dẫn slug không được để trống")
    @Size(min = 2, max = 150, message = "Đường dẫn slug phải từ 2 đến 150 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug chỉ gồm chữ thường không dấu, chữ số và dấu gạch ngang (VD: ao-polo-nam)")
    @Column(name = "duong_dan_slug", nullable = false, length = 150, unique = true)
    private String duongDanSlug;

    @NotNull(message = "Thứ tự hiển thị không được để trống")
    @Min(value = 0, message = "Thứ tự hiển thị phải từ 0 trở lên")
    @Max(value = 9999, message = "Thứ tự hiển thị không được vượt quá 9999")
    @Builder.Default
    @Column(name = "thu_tu_hien_thi")
    private Integer thuTuHienThi = 0;

    @Builder.Default
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;

    /**
     * Trường tạm phục vụ hiển thị tên danh mục cha trên giao diện Web (không lưu cột riêng trong DB).
     */
    @Transient
    private String tenDanhMucCha;

    /**
     * Trường tạm phục vụ hiển thị số lượng danh mục con trực thuộc trên giao diện Web.
     */
    @Transient
    private long soDanhMucCon;

    /**
     * Danh sách các danh mục con trực thuộc phục vụ hiển thị dạng cây phân cấp Cha - Con.
     */
    @Transient
    @Builder.Default
    private java.util.List<DanhMuc> danhMucConList = new java.util.ArrayList<>();

    /**
     * Số lượng sản phẩm đang thuộc danh mục này.
     */
    @Transient
    private long soSanPham;
}
