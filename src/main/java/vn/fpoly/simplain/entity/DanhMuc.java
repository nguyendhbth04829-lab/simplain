package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
    @Column(name = "ten_danh_muc", nullable = false, length = 150)
    private String tenDanhMuc;

    @NotBlank(message = "Đường dẫn slug không được để trống")
    @Column(name = "duong_dan_slug", nullable = false, length = 150, unique = true)
    private String duongDanSlug;

    @Column(name = "thu_tu_hien_thi")
    private Integer thuTuHienThi = 0;

    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;
}
