package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

/**
 * Thực thể Vai trò ánh xạ bảng vai_tro trong cơ sở dữ liệu Simpl4.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vai_tro")
public class VaiTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Mã vai trò không được để trống")
    @Column(name = "ma_vai_tro", nullable = false, unique = true, length = 50)
    private String maVaiTro;

    @NotBlank(message = "Tên vai trò không được để trống")
    @Nationalized
    @Column(name = "ten_vai_tro", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String tenVaiTro;

    @Nationalized
    @Column(name = "mo_ta", length = 255, columnDefinition = "nvarchar(255)")
    private String moTa;

    @Builder.Default
    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai = 1;
}
