package vn.fpoly.simplain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.fpoly.simplain.dto.AuthResponse;
import vn.fpoly.simplain.dto.CustomerProfileResponse;
import vn.fpoly.simplain.dto.LoginRequest;
import vn.fpoly.simplain.dto.RegisterRequest;
import vn.fpoly.simplain.dto.VerifyTokenResponse;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.repository.KhachHangRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * Dịch vụ xử lý đăng ký, đăng nhập và xác thực khách hàng.
 * Thực hiện kiểm tra ràng buộc dữ liệu chặt chẽ (Format Email, Gmail .com, SĐT 10 số, Họ tên, BCrypt).
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,10}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0|\\+84)(3[2-9]|5[2689]|7[06-9]|8[1-9]|9[0-9])[0-9]{7}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s]{2,150}$");

    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(KhachHangRepository khachHangRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.khachHangRepository = khachHangRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Đăng ký tài khoản khách hàng mới: kiểm tra hợp lệ toàn diện, mã hóa mật khẩu và cấp phát JWT.
     *
     * @param request Thông tin đăng ký
     * @return Kết quả xác thực kèm JWT Token
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Xử lý đăng ký tài khoản cho email: {}", request.getEmail());

        // 1. Kiểm tra toàn bộ điều kiện validation nghiệp vụ chặt chẽ
        validateRegistrationRules(request);

        String email = request.getEmail().trim().toLowerCase();
        String phone = request.getSoDienThoai().trim().replaceAll("\\s+", "");
        if (phone.startsWith("+84")) {
            phone = "0" + phone.substring(3);
        }

        // 2. Kiểm tra trùng lặp email trong cơ sở dữ liệu
        if (khachHangRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Địa chỉ email này đã được sử dụng. Vui lòng đăng nhập hoặc dùng email khác.");
        }

        // 3. Kiểm tra trùng lặp số điện thoại trong cơ sở dữ liệu
        if (khachHangRepository.existsBySoDienThoai(phone)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại này đã được đăng ký tài khoản khác.");
        }

        // 4. Sinh mã khách hàng tự động duy nhất (dạng KH + số)
        String maKhachHang = generateUniqueCustomerCode();

        // 5. Mã hóa mật khẩu bằng BCrypt
        String matKhauHash = passwordEncoder.encode(request.getMatKhau());

        // 6. Lưu thực thể khách hàng vào cơ sở dữ liệu
        KhachHang khachHang = KhachHang.builder()
                .maKhachHang(maKhachHang)
                .hoTen(request.getHoTen().trim())
                .email(email)
                .soDienThoai(phone)
                .matKhauHash(matKhauHash)
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .anhDaiDien("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80")
                .build();

        KhachHang savedCustomer = khachHangRepository.save(khachHang);
        log.info("Đã tạo mới khách hàng thành công: mã {}", savedCustomer.getMaKhachHang());

        // 7. Cấp phát JWT Token
        String token = jwtService.generateToken(savedCustomer);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs())
                .maKhachHang(savedCustomer.getMaKhachHang())
                .hoTen(savedCustomer.getHoTen())
                .email(savedCustomer.getEmail())
                .soDienThoai(savedCustomer.getSoDienThoai())
                .vaiTro("CUSTOMER")
                .message("Đăng ký tài khoản Simplain thành công!")
                .build();
    }

    /**
     * Đăng nhập khách hàng bằng email hoặc số điện thoại, so khớp mật khẩu mã hóa BCrypt.
     *
     * @param request Thông tin đăng nhập
     * @return Kết quả xác thực kèm JWT Token
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Kiểm tra hợp lệ dữ liệu đăng nhập
        validateLoginRules(request);

        String identifier = request.getIdentifier().trim();
        log.info("Xử lý đăng nhập tài khoản cho identifier: {}", identifier);

        // 1. Tìm kiếm khách hàng theo email hoặc số điện thoại
        Optional<KhachHang> optionalKhachHang;
        if (identifier.contains("@")) {
            optionalKhachHang = khachHangRepository.findByEmail(identifier.toLowerCase());
        } else {
            String phone = identifier.replaceAll("\\s+", "");
            if (phone.startsWith("+84")) {
                phone = "0" + phone.substring(3);
            }
            optionalKhachHang = khachHangRepository.findBySoDienThoai(phone);
        }

        if (optionalKhachHang.isEmpty()) {
            optionalKhachHang = khachHangRepository.findByMaKhachHang(identifier);
        }

        if (optionalKhachHang.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản hoặc mật khẩu không chính xác.");
        }

        KhachHang khachHang = optionalKhachHang.get();

        // 2. Kiểm tra trạng thái hoạt động của tài khoản
        if (khachHang.getTrangThai() == null || khachHang.getTrangThai() != 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản của bạn đang bị tạm khóa. Vui lòng liên hệ CSKH Coolmate/Simplain.");
        }

        // 3. So khớp mật khẩu với mã hash BCrypt
        if (khachHang.getMatKhauHash() == null || !passwordEncoder.matches(request.getMatKhau(), khachHang.getMatKhauHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản hoặc mật khẩu không chính xác.");
        }

        // 4. Cấp phát JWT Token
        String token = jwtService.generateToken(khachHang);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs())
                .maKhachHang(khachHang.getMaKhachHang())
                .hoTen(khachHang.getHoTen())
                .email(khachHang.getEmail())
                .soDienThoai(khachHang.getSoDienThoai())
                .vaiTro("CUSTOMER")
                .message("Đăng nhập thành công! Chào mừng bạn quay trở lại Simplain.")
                .build();
    }

    /**
     * Xác minh tính hợp lệ của JWT Token và kiểm tra trạng thái tài khoản trong hệ thống.
     *
     * @param token Chuỗi JWT Token
     * @return Kết quả xác minh
     */
    @Transactional(readOnly = true)
    public VerifyTokenResponse verifyToken(String token) {
        VerifyTokenResponse response = jwtService.verifyToken(token);
        if (!response.isValid() || response.getMaKhachHang() == null) {
            return response;
        }

        Optional<KhachHang> khachHangOpt = khachHangRepository.findByMaKhachHang(response.getMaKhachHang());
        if (khachHangOpt.isEmpty() || khachHangOpt.get().getTrangThai() != 1) {
            return VerifyTokenResponse.builder()
                    .valid(false)
                    .maKhachHang(response.getMaKhachHang())
                    .message("Tài khoản gắn với Token này không tồn tại hoặc đã bị khóa.")
                    .build();
        }

        return response;
    }

    /**
     * Lấy thông tin hồ sơ khách hàng hiện tại dựa trên JWT Token.
     *
     * @param authorizationHeader Header Authorization (Bearer <token>)
     * @return Thông tin hồ sơ khách hàng
     */
    @Transactional(readOnly = true)
    public CustomerProfileResponse getCurrentUserProfile(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thiếu hoặc sai định dạng Authorization Bearer Token.");
        }

        String token = authorizationHeader.substring(7).trim();
        VerifyTokenResponse verify = verifyToken(token);
        if (!verify.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, verify.getMessage());
        }

        KhachHang khachHang = khachHangRepository.findByMaKhachHang(verify.getMaKhachHang())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin khách hàng."));

        return CustomerProfileResponse.builder()
                .id(khachHang.getId())
                .maKhachHang(khachHang.getMaKhachHang())
                .hoTen(khachHang.getHoTen())
                .email(khachHang.getEmail())
                .soDienThoai(khachHang.getSoDienThoai())
                .anhDaiDien(khachHang.getAnhDaiDien())
                .gioiTinh(khachHang.getGioiTinh())
                .ngaySinh(khachHang.getNgaySinh())
                .trangThai(khachHang.getTrangThai())
                .ngayTao(khachHang.getNgayTao())
                .build();
    }

    // =========================================================================
    // CÁC HÀM VALIDATE CHẶT CHẼ TOÀN BỘ ĐIỀU KIỆN
    // =========================================================================

    /**
     * Kiểm tra ràng buộc hợp lệ cho yêu cầu đăng ký.
     */
    public void validateRegistrationRules(RegisterRequest request) {
        // 1. Kiểm tra Họ và tên
        if (request.getHoTen() == null || request.getHoTen().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ và tên không được để trống.");
        }
        String hoTen = request.getHoTen().trim();
        if (hoTen.length() < 2 || hoTen.length() > 150) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ và tên phải từ 2 đến 150 ký tự.");
        }
        if (hoTen.matches(".*\\d.*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ và tên không được chứa chữ số.");
        }
        if (!NAME_PATTERN.matcher(hoTen).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ và tên chỉ được chứa chữ cái tiếng Việt hoặc tiếng Anh, không chứa ký tự đặc biệt.");
        }

        // 2. Kiểm tra Email chặt chẽ (domain, gmail phải có đủ .com)
        validateEmailStrict(request.getEmail());

        // 3. Kiểm tra Số điện thoại di động Việt Nam (10 số)
        validatePhoneStrict(request.getSoDienThoai());

        // 4. Kiểm tra Mật khẩu
        validatePasswordStrict(request.getMatKhau());

        // 5. Kiểm tra Mật khẩu xác nhận
        if (request.getXacNhanMatKhau() == null || !request.getMatKhau().equals(request.getXacNhanMatKhau())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu xác nhận không trùng khớp với mật khẩu đã nhập.");
        }
    }

    /**
     * Kiểm tra ràng buộc hợp lệ cho yêu cầu đăng nhập.
     */
    public void validateLoginRules(LoginRequest request) {
        if (request.getIdentifier() == null || request.getIdentifier().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập Email hoặc Số điện thoại.");
        }
        if (request.getMatKhau() == null || request.getMatKhau().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống.");
        }

        String identifier = request.getIdentifier().trim();
        if (identifier.contains("@")) {
            validateEmailStrict(identifier);
        } else {
            validatePhoneStrict(identifier);
        }
    }

    /**
     * Kiểm tra định dạng Email chặt chẽ:
     * - Bắt buộc đúng cấu trúc user@domain.tld
     * - Không chứa khoảng trắng
     * - Nếu tên miền là gmail thì BẮT BUỘC phải đủ @gmail.com (chặn @gmail.co, @gmail.c...)
     * - Tên miền yahoo/outlook/hotmail cũng phải đúng đuôi chuẩn.
     */
    public void validateEmailStrict(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống.");
        }
        String cleanEmail = email.trim().toLowerCase();

        if (cleanEmail.contains(" ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được chứa khoảng trắng.");
        }

        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Định dạng email không hợp lệ (ví dụ: ten@gmail.com).");
        }

        String[] parts = cleanEmail.split("@");
        if (parts.length != 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không hợp lệ.");
        }

        String domain = parts[1];

        // Ràng buộc nghiêm ngặt: Gmail BẮT BUỘC phải đủ đuôi .com
        if (domain.startsWith("gmail.")) {
            if (!domain.equals("gmail.com")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Địa chỉ Gmail phải có đuôi đầy đủ là @gmail.com (bạn đang nhập @" + domain + ").");
            }
        } else if (domain.startsWith("yahoo.")) {
            if (!domain.equals("yahoo.com") && !domain.equals("yahoo.com.vn") && !domain.equals("yahoo.vn")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Địa chỉ Yahoo Mail phải có đuôi hợp lệ (@yahoo.com hoặc @yahoo.com.vn).");
            }
        } else if (domain.startsWith("outlook.")) {
            if (!domain.equals("outlook.com")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Địa chỉ Outlook phải có đuôi đầy đủ là @outlook.com.");
            }
        } else if (domain.startsWith("hotmail.")) {
            if (!domain.equals("hotmail.com")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Địa chỉ Hotmail phải có đuôi đầy đủ là @hotmail.com.");
            }
        }

        String[] domainParts = domain.split("\\.");
        String tld = domainParts[domainParts.length - 1];
        if (tld.length() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Đuôi tên miền email phải có ít nhất 2 ký tự (ví dụ: .com, .vn, .edu.vn).");
        }
    }

    /**
     * Kiểm tra số điện thoại di động Việt Nam:
     * - Phải gồm đúng 10 chữ số
     * - Đầu số hợp lệ: 03x, 05x, 07x, 08x, 09x (hoặc +84)
     * - Không chứa ký tự chữ cái hay ký tự đặc biệt.
     */
    public void validatePhoneStrict(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại không được để trống.");
        }
        String cleanPhone = phone.trim().replaceAll("\\s+", "");

        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Số điện thoại phải gồm đúng 10 chữ số của các nhà mạng Việt Nam (bắt đầu bằng 03, 05, 07, 08, 09).");
        }
    }

    /**
     * Kiểm tra mật khẩu:
     * - Từ 6 đến 50 ký tự
     * - Không chứa khoảng trắng
     * - Phải chứa cả chữ cái và chữ số.
     */
    public void validatePasswordStrict(String password) {
        if (password == null || password.length() < 6 || password.length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu phải từ 6 đến 50 ký tự.");
        }
        if (password.contains(" ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được chứa khoảng trắng.");
        }
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu phải chứa ít nhất 1 chữ cái và 1 chữ số để đảm bảo an toàn.");
        }
    }

    private String generateUniqueCustomerCode() {
        for (int i = 0; i < 10; i++) {
            long randomNum = ThreadLocalRandom.current().nextLong(100000, 999999);
            String candidate = "KH" + randomNum;
            if (!khachHangRepository.existsByMaKhachHang(candidate)) {
                return candidate;
            }
        }
        return "KH" + (System.currentTimeMillis() % 1000000);
    }
}
