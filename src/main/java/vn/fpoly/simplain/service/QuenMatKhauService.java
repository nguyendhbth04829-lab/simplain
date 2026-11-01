package vn.fpoly.simplain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.XacNhanDoiMatKhauRequest;
import vn.fpoly.simplain.dto.YeuCauQuenMatKhauRequest;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.QuenMatKhauOtp;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.repository.QuenMatKhauOtpRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service xử lý nghiệp vụ Quên mật khẩu và Xác thực OTP qua Gmail.
 */
@Slf4j
@Service
public class QuenMatKhauService {

    private final QuenMatKhauOtpRepository quenMatKhauOtpRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    public QuenMatKhauService(QuenMatKhauOtpRepository quenMatKhauOtpRepository,
                              KhachHangRepository khachHangRepository,
                              NhanVienRepository nhanVienRepository,
                              PasswordEncoder passwordEncoder,
                              EmailService emailService) {
        this.quenMatKhauOtpRepository = quenMatKhauOtpRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Tiếp nhận email, kiểm tra tài khoản, sinh OTP 6 số và gửi qua Gmail.
     *
     * @param request       DTO chứa email
     * @param loaiNguoiDung KHACH_HANG hoặc NHAN_VIEN
     * @return Mã OTP đã sinh
     */
    @Transactional
    public String guiOtpQuenMatKhau(YeuCauQuenMatKhauRequest request, String loaiNguoiDung) {
        String email = request.getEmail().trim().toLowerCase();
        String hoTen = "Quý khách";

        if ("KHACH_HANG".equalsIgnoreCase(loaiNguoiDung)) {
            KhachHang khachHang = khachHangRepository.findByEmail(email)
                    .orElseGet(() -> khachHangRepository.findByEmailIgnoreCase(email).orElse(null));

            if (khachHang == null) {
                throw new IllegalArgumentException("Không tìm thấy tài khoản khách hàng nào liên kết với email: " + email);
            }
            if (khachHang.getTrangThai() != null && khachHang.getTrangThai() == 0) {
                throw new IllegalStateException("Tài khoản của bạn hiện đang bị tạm khóa. Vui lòng liên hệ CSKH SIMPLAIN để được hỗ trợ!");
            }
            hoTen = khachHang.getHoTen();
        } else {
            NhanVien nhanVien = nhanVienRepository.findByEmailIgnoreCase(email).orElse(null);
            if (nhanVien == null) {
                throw new IllegalArgumentException("Không tìm thấy tài khoản nhân viên nào liên kết với email: " + email);
            }
            if (nhanVien.getTrangThai() != null && nhanVien.getTrangThai() == 0) {
                throw new IllegalStateException("Tài khoản nhân sự này hiện đang bị khóa!");
            }
            hoTen = nhanVien.getHoTen();
        }

        // Vô hiệu hóa các mã OTP cũ chưa sử dụng của email này
        List<QuenMatKhauOtp> danhSachCu = quenMatKhauOtpRepository.findByEmailAndDaSuDungFalse(email);
        for (QuenMatKhauOtp otpCu : danhSachCu) {
            otpCu.setDaSuDung(true);
        }
        if (!danhSachCu.isEmpty()) {
            quenMatKhauOtpRepository.saveAll(danhSachCu);
        }

        // Sinh mã OTP 6 số ngẫu nhiên an toàn
        int otpSo = 100000 + secureRandom.nextInt(900000);
        String maOtp = String.valueOf(otpSo);

        // Lưu bản ghi OTP có hiệu lực 10 phút
        QuenMatKhauOtp otpEntity = QuenMatKhauOtp.builder()
                .email(email)
                .maOtp(maOtp)
                .loaiNguoiDung(loaiNguoiDung.toUpperCase())
                .thoiGianTao(LocalDateTime.now())
                .thoiGianHetHan(LocalDateTime.now().plusMinutes(10))
                .daSuDung(false)
                .soLanSai(0)
                .build();
        quenMatKhauOtpRepository.save(otpEntity);

        // Gửi email qua Gmail SMTP
        emailService.guiEmailOtpQuenMatKhau(email, maOtp, hoTen);
        return maOtp;
    }

    /**
     * Xác thực mã OTP và cập nhật mật khẩu mới.
     *
     * @param request       DTO chứa email, mã OTP, mật khẩu mới
     * @param loaiNguoiDung KHACH_HANG hoặc NHAN_VIEN
     */
    @Transactional
    public void xacNhanVaDoiMatKhau(XacNhanDoiMatKhauRequest request, String loaiNguoiDung) {
        String email = request.getEmail().trim().toLowerCase();

        // 1. Kiểm tra khớp mật khẩu
        if (!request.getMatKhauMoi().equals(request.getXacNhanMatKhauMoi())) {
            throw new IllegalArgumentException("Mật khẩu mới và mật khẩu xác nhận không trùng khớp!");
        }

        // 2. Tìm OTP còn hiệu lực gần nhất
        QuenMatKhauOtp otpEntity = quenMatKhauOtpRepository
                .findTopByEmailAndLoaiNguoiDungAndDaSuDungFalseOrderByThoiGianTaoDesc(email, loaiNguoiDung.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã OTP xác thực hợp lệ cho email này hoặc mã đã hết hạn. Vui lòng gửi lại yêu cầu!"));

        // 3. Kiểm tra số lần nhập sai (chặn Brute Force)
        if (otpEntity.getSoLanSai() != null && otpEntity.getSoLanSai() >= 5) {
            otpEntity.setDaSuDung(true);
            quenMatKhauOtpRepository.save(otpEntity);
            throw new IllegalStateException("Mã OTP này đã bị khóa do nhập sai quá 5 lần. Vui lòng bấm gửi lại mã mới!");
        }

        // 4. Kiểm tra thời hạn hiệu lực (10 phút)
        if (LocalDateTime.now().isAfter(otpEntity.getThoiGianHetHan())) {
            otpEntity.setDaSuDung(true);
            quenMatKhauOtpRepository.save(otpEntity);
            throw new IllegalArgumentException("Mã OTP đã hết hiệu lực (chỉ có giá trị trong 10 phút). Vui lòng yêu cầu mã mới!");
        }

        // 5. So khớp mã OTP
        if (!otpEntity.getMaOtp().equals(request.getMaOtp().trim())) {
            int soLanSai = (otpEntity.getSoLanSai() != null ? otpEntity.getSoLanSai() : 0) + 1;
            otpEntity.setSoLanSai(soLanSai);
            quenMatKhauOtpRepository.save(otpEntity);
            int conLai = 5 - soLanSai;
            throw new IllegalArgumentException("Mã OTP không chính xác! Bạn còn " + (conLai > 0 ? conLai : 0) + " lần thử.");
        }

        // 6. Cập nhật mật khẩu mã hóa BCrypt
        String matKhauHash = passwordEncoder.encode(request.getMatKhauMoi().trim());

        if ("KHACH_HANG".equalsIgnoreCase(loaiNguoiDung)) {
            KhachHang khachHang = khachHangRepository.findByEmail(email)
                    .orElseGet(() -> khachHangRepository.findByEmailIgnoreCase(email).orElse(null));
            if (khachHang == null) {
                throw new IllegalArgumentException("Không tìm thấy khách hàng với email: " + email);
            }
            khachHang.setMatKhauHash(matKhauHash);
            khachHangRepository.save(khachHang);
        } else {
            NhanVien nhanVien = nhanVienRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên với email: " + email));
            nhanVien.setMatKhauHash(matKhauHash);
            nhanVienRepository.save(nhanVien);
        }

        // 7. Đánh dấu OTP đã sử dụng
        otpEntity.setDaSuDung(true);
        quenMatKhauOtpRepository.save(otpEntity);
        log.info("Đặt lại mật khẩu thành công qua Gmail OTP cho email: {} ({})", email, loaiNguoiDung);
    }
}
