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
@Table(name = "thuoc_tinh_bien_the", uniqueConstraints =
        @UniqueConstraint(name = "UQ_thuoc_tinh_bien_the_ma", columnNames = "ma_thuoc_tinh"))
public class ThuocTinhBienThe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 80)
    @Column(name = "ma_thuoc_tinh", nullable = false, length = 80)
    private String maThuocTinh;

    @NotBlank
    @Size(max = 120)
    @Nationalized
    @Column(name = "ten_thuoc_tinh", nullable = false, length = 120)
    private String tenThuocTinh;

    @Builder.Default
    @Column(name = "kich_hoat", nullable = false)
    private Boolean kichHoat = true;

    @Builder.Default
    @Column(name = "thu_tu", nullable = false)
    private Integer thuTu = 0;
}
