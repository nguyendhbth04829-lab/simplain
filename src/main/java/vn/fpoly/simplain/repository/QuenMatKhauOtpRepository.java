package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.QuenMatKhauOtp;

import java.util.List;
import java.util.Optional;

/**
 * Repository truy vấn dữ liệu OTP quên mật khẩu.
 */
@Repository
public interface QuenMatKhauOtpRepository extends JpaRepository<QuenMatKhauOtp, Long> {

    /**
     * Tìm OTP còn hiệu lực gần nhất của một email theo loại người dùng.
     */
    Optional<QuenMatKhauOtp> findTopByEmailAndLoaiNguoiDungAndDaSuDungFalseOrderByThoiGianTaoDesc(String email, String loaiNguoiDung);

    /**
     * Tìm danh sách OTP chưa sử dụng của một email để vô hiệu hóa khi tạo OTP mới.
     */
    List<QuenMatKhauOtp> findByEmailAndDaSuDungFalse(String email);
}
