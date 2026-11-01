package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Thực thể lưu trữ mã OTP xác thực quên mật khẩu qua Gmail.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quen_mat_khau_otp")
public class QuenMatKhauOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "ma_otp", nullable = false, length = 10)
    private String maOtp;

    @Builder.Default
    @Column(name = "loai_nguoi_dung", length = 20)
    private String loaiNguoiDung = "KHACH_HANG";

    @Builder.Default
    @Column(name = "thoi_gian_tao")
    private LocalDateTime thoiGianTao = LocalDateTime.now();

    @Column(name = "thoi_gian_het_han", nullable = false)
    private LocalDateTime thoiGianHetHan;

    @Builder.Default
    @Column(name = "da_su_dung")
    private Boolean daSuDung = false;

    @Builder.Default
    @Column(name = "so_lan_sai")
    private Integer soLanSai = 0;

    @PrePersist
    public void prePersist() {
        if (this.thoiGianTao == null) {
            this.thoiGianTao = LocalDateTime.now();
        }
        if (this.daSuDung == null) {
            this.daSuDung = false;
        }
        if (this.soLanSai == null) {
            this.soLanSai = 0;
        }
    }
}
