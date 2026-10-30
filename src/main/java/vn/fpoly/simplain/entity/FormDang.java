package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

/**
 * Thực thể Form dáng [form_dang] dùng để quản lý danh mục form dáng áo/quần và liên kết với Sản phẩm.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "form_dang")
public class FormDang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Tên form dáng không được để trống")
    @Size(min = 2, max = 100, message = "Tên form dáng phải từ 2 đến 100 ký tự")
    @Nationalized
    @Column(name = "ten_form_dang", nullable = false, unique = true, length = 100, columnDefinition = "nvarchar(100)")
    private String tenFormDang;

    @Size(max = 255, message = "Mô tả form dáng không quá 255 ký tự")
    @Nationalized
    @Column(name = "mo_ta", length = 255, columnDefinition = "nvarchar(255)")
    private String moTa;

    @Builder.Default
    @Column(name = "kich_hoat", nullable = false)
    private Boolean kichHoat = true;

    @Transient
    private long soSanPhamSuDung;
}
