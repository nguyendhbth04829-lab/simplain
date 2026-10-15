package vn.fpoly.simplain.paymentmethod;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Truy vấn phương thức thanh toán.
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    /** Tìm theo mã duy nhất (COD, VNPAY, MOMO). */
    Optional<PaymentMethod> findByMaPhuongThuc(String maPhuongThuc);

    /** Lọc theo trạng thái bật/tắt. */
    List<PaymentMethod> findByKichHoat(Boolean kichHoat);

    /** Kiểm tra trùng mã khi thêm mới. */
    boolean existsByMaPhuongThuc(String maPhuongThuc);

    /** Kiểm tra trùng mã khi cập nhật (loại trừ chính nó). */
    boolean existsByMaPhuongThucAndIdNot(String maPhuongThuc, Long id);
}
