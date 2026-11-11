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
 * Thực thể Phương thức thanh toán [phuong_thuc_thanh_toan] (US18).
 * Tiêu chí 1: CRUD bảng phuong_thuc_thanh_toan (COD, VNPay, MoMo).
 * Tính năng chính: Bật/Tắt cấu hình cổng thanh toán.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phuong_thuc_thanh_toan")
public class PhuongThucThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Mã phương thức thanh toán không được để trống")
    @Size(min = 2, max = 50, message = "Mã phương thức phải từ 2 đến 50 ký tự")
    @Column(name = "ma_phuong_thuc", nullable = false, length = 50, unique = true)
    private String maPhuongThuc;

    @NotBlank(message = "Tên phương thức thanh toán không được để trống")
    @Size(max = 100, message = "Tên phương thức tối đa 100 ký tự")
    @Nationalized
    @Column(name = "ten_phuong_thuc", nullable = false, length = 100)
    private String tenPhuongThuc;

    @Builder.Default
    @Column(name = "la_truc_tuyen")
    private Boolean laTrucTuyen = false;

    @Builder.Default
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;

    @Nationalized
    @Column(name = "mo_ta", length = 255)
    private String moTa;

    @Column(name = "bieu_tuong", length = 255)
    private String bieuTuong;

    @Nationalized
    @Column(name = "cau_hinh", columnDefinition = "nvarchar(max)")
    private String cauHinh;
}
