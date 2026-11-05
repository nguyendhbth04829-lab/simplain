package vn.fpoly.simplain.inbound;

import jakarta.persistence.*;
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
@Table(name = "nha_cung_cap")
public class NhaCungCap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_ncc", nullable = false, unique = true, length = 50)
    private String maNcc;

    @Nationalized
    @Column(name = "ten_ncc", nullable = false, length = 255)
    private String tenNcc;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "email", length = 100)
    private String email;

    @Nationalized
    @Column(name = "dia_chi", length = 255)
    private String diaChi;

    @Nationalized
    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Builder.Default
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;
}
