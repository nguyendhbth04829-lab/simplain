package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thực thể lưu trữ quyền hạn được cấp cho từng Vai trò (Dynamic RBAC).
 * Ánh xạ bảng vai_tro_quyen_han trong cơ sở dữ liệu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vai_tro_quyen_han", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_vai_tro_quyen", columnNames = {"ma_vai_tro_id", "ma_quyen"})
})
public class VaiTroQuyenHan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_vai_tro_id", nullable = false)
    private Long maVaiTroId;

    @Column(name = "ma_quyen", nullable = false, length = 50)
    private String maQuyen;
}
