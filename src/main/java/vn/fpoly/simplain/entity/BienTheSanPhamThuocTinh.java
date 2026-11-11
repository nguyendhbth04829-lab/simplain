package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bien_the_san_pham_thuoc_tinh",
        uniqueConstraints = @UniqueConstraint(name = "UQ_bien_the_thuoc_tinh", columnNames = {"ma_bien_the_id", "ma_thuoc_tinh_id"}))
public class BienTheSanPhamThuocTinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bien_the_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_bien_the_thuoc_tinh_bien_the"))
    private BienTheSanPham bienThe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thuoc_tinh_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_bien_the_thuoc_tinh_thuoc_tinh"))
    private ThuocTinhBienThe thuocTinh;

    @NotBlank
    @Size(max = 500)
    @Nationalized
    @Column(name = "gia_tri", nullable = false, length = 500)
    private String giaTri;
}
