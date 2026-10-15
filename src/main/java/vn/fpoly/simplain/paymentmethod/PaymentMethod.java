package vn.fpoly.simplain.paymentmethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thực thể phương thức thanh toán.
 * Khớp bảng {@code phuong_thuc_thanh_toan} trong file db34tbdasua.sql.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phuong_thuc_thanh_toan")
public class PaymentMethod {

    /** Khóa chính tự tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Mã duy nhất: COD, VNPAY, MOMO. */
    @Column(name = "ma_phuong_thuc", nullable = false, length = 50, unique = true)
    private String maPhuongThuc;

    /** Tên hiển thị: Thanh toán khi nhận hàng, VNPay... */
    @Column(name = "ten_phuong_thuc", nullable = false, length = 100)
    private String tenPhuongThuc;

    /** true = cổng trực tuyến (VNPay/MoMo), false = COD. */
    @Column(name = "la_truc_tuyen")
    private Boolean laTrucTuyen = false;

    /** true = đang bật cho khách chọn, false = tạm tắt. */
    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;
}
