package vn.fpoly.simplain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thực thể Vai trò nhân viên, ánh xạ bảng 'vai_tro' trong cơ sở dữ liệu.
 * Đại diện cho các nhóm quyền hạn trong hệ thống CMS quản trị: ADMIN, CSKH, BAN_HANG,...
 */
@Entity
@Table(name = "vai_tro")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaiTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_vai_tro", unique = true, nullable = false, length = 50)
    private String maVaiTro;

    @Column(name = "ten_vai_tro", nullable = false, length = 100)
    private String tenVaiTro;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    @Builder.Default
    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai = 1;
}
