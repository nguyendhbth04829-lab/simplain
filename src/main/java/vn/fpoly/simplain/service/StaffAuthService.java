package vn.fpoly.simplain.service;

import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.fpoly.simplain.config.JwtProperties;
import vn.fpoly.simplain.dto.StaffAuthResponse;
import vn.fpoly.simplain.dto.StaffLoginRequest;
import vn.fpoly.simplain.dto.StaffProfileResponse;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.VaiTro;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.repository.VaiTroRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Dịch vụ xử lý xác thực và phân quyền cho Nhân viên CMS (US02).
 * Đảm bảo các tiêu chí:
 * 1. Kiểm tra phân quyền theo vai trò (Role-based access).
 * 2. Từ chối đăng nhập nếu trạng thái nhân viên bị khóa (trang_thai = 0).
 */
@Service
public class StaffAuthService {

    private static final Logger log = LoggerFactory.getLogger(StaffAuthService.class);

    /**
     * Danh sách các mã vai trò được phép đăng nhập vào hệ thống quản trị CMS.
     */
    public static final List<String> CMS_ALLOWED_ROLES = Arrays.asList(
            "ADMIN", "QUAN_LY", "CSKH", "BAN_HANG", "KHO"
    );

    private final NhanVienRepository nhanVienRepository;
    private final VaiTroRepository vaiTroRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    public StaffAuthService(NhanVienRepository nhanVienRepository,
                            VaiTroRepository vaiTroRepository,
                            JwtService jwtService,
                            PasswordEncoder passwordEncoder,
                            JwtProperties jwtProperties) {
        this.nhanVienRepository = nhanVienRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
    }

    /**
     * Khởi tạo dữ liệu mẫu vai trò và tài khoản nhân viên mặc định nếu cơ sở dữ liệu chưa có.
     * Cung cấp sẵn các tài khoản để phục vụ chạy thử nghiệm và kiểm thử tiêu chí US02 ngay lập tức.
     */
    @PostConstruct
    @Transactional
    public void initDefaultStaffData() {
        try {
            // 1. Tạo vai trò mẫu nếu chưa có
            VaiTro roleAdmin = vaiTroRepository.findByMaVaiTro("ADMIN").orElseGet(() ->
                    vaiTroRepository.save(VaiTro.builder()
                            .maVaiTro("ADMIN")
                            .tenVaiTro("Quản trị viên tối cao")
                            .moTa("Toàn quyền cấu hình và quản trị hệ thống CMS")
                            .trangThai(1)
                            .build())
            );

            VaiTro roleCskh = vaiTroRepository.findByMaVaiTro("CSKH").orElseGet(() ->
                    vaiTroRepository.save(VaiTro.builder()
                            .maVaiTro("CSKH")
                            .tenVaiTro("Chăm sóc khách hàng")
                            .moTa("Duyệt yêu cầu đổi trả và chăm sóc khách hàng")
                            .trangThai(1)
                            .build())
            );

            VaiTro roleBanHang = vaiTroRepository.findByMaVaiTro("BAN_HANG").orElseGet(() ->
                    vaiTroRepository.save(VaiTro.builder()
                            .maVaiTro("BAN_HANG")
                            .tenVaiTro("Thu ngân quầy POS")
                            .moTa("Tạo hóa đơn và bán hàng trực tiếp tại quầy")
                            .trangThai(1)
                            .build())
            );

            // 2. Tạo nhân viên mẫu nếu chưa có
            if (nhanVienRepository.findByTenDangNhap("superadmin").isEmpty()) {
                nhanVienRepository.save(NhanVien.builder()
                        .maNhanVien("NV001")
                        .hoTen("Nguyễn Văn Admin")
                        .tenDangNhap("superadmin")
                        .matKhauHash(passwordEncoder.encode("Admin@123"))
                        .email("admin@simplain.vn")
                        .soDienThoai("0901112233")
                        .vaiTro(roleAdmin)
                        .trangThai(1) // Hoạt động
                        .ngayTao(LocalDateTime.now())
                        .build());
                log.info("[SEED] Đã khởi tạo tài khoản quản trị viên: superadmin / Admin@123");
            }

            if (nhanVienRepository.findByTenDangNhap("cskh_01").isEmpty()) {
                nhanVienRepository.save(NhanVien.builder()
                        .maNhanVien("NV002")
                        .hoTen("Lê CSKH")
                        .tenDangNhap("cskh_01")
                        .matKhauHash(passwordEncoder.encode("Staff@123"))
                        .email("cskh@simplain.vn")
                        .soDienThoai("0904445566")
                        .vaiTro(roleCskh)
                        .trangThai(1) // Hoạt động
                        .ngayTao(LocalDateTime.now())
                        .build());
                log.info("[SEED] Đã khởi tạo tài khoản nhân viên CSKH: cskh_01 / Staff@123");
            }

            if (nhanVienRepository.findByTenDangNhap("thungan_01").isEmpty()) {
                nhanVienRepository.save(NhanVien.builder()
                        .maNhanVien("NV003")
                        .hoTen("Phạm Thu Ngân")
                        .tenDangNhap("thungan_01")
                        .matKhauHash(passwordEncoder.encode("Staff@123"))
                        .email("thungan@simplain.vn")
                        .soDienThoai("0907778899")
                        .vaiTro(roleBanHang)
                        .trangThai(1) // Hoạt động
                        .ngayTao(LocalDateTime.now())
                        .build());
                log.info("[SEED] Đã khởi tạo tài khoản nhân viên thu ngân: thungan_01 / Staff@123");
            }

            // Tài khoản bị khóa (trang_thai = 0) để kiểm thử Tiêu chí 2: Từ chối đăng nhập nếu trạng thái bị khóa
            if (nhanVienRepository.findByTenDangNhap("nhanvien_khoa").isEmpty()) {
                nhanVienRepository.save(NhanVien.builder()
                        .maNhanVien("NV999")
                        .hoTen("Trần Khóa Tài Khoản")
                        .tenDangNhap("nhanvien_khoa")
                        .matKhauHash(passwordEncoder.encode("Staff@123"))
                        .email("khoa@simplain.vn")
                        .soDienThoai("0909999999")
                        .vaiTro(roleCskh)
                        .trangThai(0) // ĐÃ BỊ KHÓA
                        .ngayTao(LocalDateTime.now())
                        .build());
                log.info("[SEED] Đã khởi tạo tài khoản nhân viên bị khóa mẫu: nhanvien_khoa / Staff@123 (trang_thai=0)");
            }
        } catch (Exception e) {
            log.warn("[SEED] Không thể khởi tạo tự động dữ liệu mẫu nhân viên: {}", e.getMessage());
        }
    }

    /**
     * Xử lý đăng nhập vào hệ thống quản trị CMS cho Nhân viên (US02).
     *
     * @param request Thông tin đăng nhập gồm tên đăng nhập/email/sđt và mật khẩu
     * @return Phản hồi xác thực kèm JWT Token và vai trò
     */
    @Transactional(readOnly = true)
    public StaffAuthResponse login(StaffLoginRequest request) {
        if (request == null || request.getTenDangNhap() == null || request.getTenDangNhap().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên đăng nhập không được để trống.");
        }
        if (request.getMatKhau() == null || request.getMatKhau().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống.");
        }

        String identifier = request.getTenDangNhap().trim();
        log.info("Xử lý đăng nhập CMS cho nhân viên: identifier={}", identifier);

        // 1. Tìm kiếm nhân viên theo tên đăng nhập, email hoặc số điện thoại
        NhanVien nhanVien = nhanVienRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản nhân viên không tồn tại trong hệ thống."));

        // 2. Tiêu chí 2: Từ chối đăng nhập nếu trạng thái nhân viên bị khóa
        if (nhanVien.getTrangThai() == null || nhanVien.getTrangThai() != 1) {
            log.warn("TỪ CHỐI ĐĂNG NHẬP: Tài khoản nhân viên [{}] (mã {}) đang ở trạng thái bị khóa (trang_thai={})",
                    nhanVien.getTenDangNhap(), nhanVien.getMaNhanVien(), nhanVien.getTrangThai());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Tài khoản nhân viên đã bị khóa hoặc ngừng hoạt động. Vui lòng liên hệ Quản trị viên!");
        }

        // 3. Kiểm tra mật khẩu mã hóa BCrypt
        if (!passwordEncoder.matches(request.getMatKhau(), nhanVien.getMatKhauHash())) {
            log.warn("Đăng nhập thất bại: Mật khẩu không chính xác cho tài khoản [{}]", identifier);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không chính xác.");
        }

        // 4. Tiêu chí 1: Check phân quyền (Role-based)
        VaiTro vaiTro = nhanVien.getVaiTro();
        if (vaiTro == null) {
            log.error("Lỗi phân quyền: Tài khoản [{}] chưa được liên kết vai trò", nhanVien.getTenDangNhap());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản nhân viên chưa được gán vai trò hợp lệ trong hệ thống.");
        }

        if (vaiTro.getTrangThai() == null || vaiTro.getTrangThai() != 1) {
            log.warn("TỪ CHỐI ĐĂNG NHẬP: Vai trò [{}] của nhân viên [{}] đang bị tạm ngưng hoạt động",
                    vaiTro.getMaVaiTro(), nhanVien.getTenDangNhap());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Vai trò [" + vaiTro.getTenVaiTro() + "] hiện đang bị tạm ngưng hoạt động trong hệ thống.");
        }

        // Kiểm tra xem vai trò có quyền truy cập vào CMS hay không
        if (!CMS_ALLOWED_ROLES.contains(vaiTro.getMaVaiTro().toUpperCase())) {
            log.warn("TỪ CHỐI ĐĂNG NHẬP: Vai trò [{}] không có quyền truy cập vào CMS", vaiTro.getMaVaiTro());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Vai trò [" + vaiTro.getTenVaiTro() + "] không có quyền truy cập vào hệ thống quản trị CMS.");
        }

        // 5. Cấp phát JWT Token kèm Role-based claims
        String token = jwtService.generateStaffToken(nhanVien, vaiTro);
        log.info("Nhân viên [{}] (vai trò: {}) đã đăng nhập thành công vào CMS. Đã cấp phát JWT Token.",
                nhanVien.getTenDangNhap(), vaiTro.getMaVaiTro());

        return StaffAuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresInMs(jwtProperties.getExpirationMs())
                .maNhanVien(nhanVien.getMaNhanVien())
                .hoTen(nhanVien.getHoTen())
                .tenDangNhap(nhanVien.getTenDangNhap())
                .email(nhanVien.getEmail())
                .soDienThoai(nhanVien.getSoDienThoai())
                .maVaiTro(vaiTro.getMaVaiTro())
                .tenVaiTro(vaiTro.getTenVaiTro())
                .trangThai(nhanVien.getTrangThai())
                .message("Đăng nhập CMS thành công với vai trò " + vaiTro.getTenVaiTro())
                .build();
    }

    /**
     * Xác minh JWT Token và lấy thông tin hồ sơ nhân viên phục vụ hiển thị CMS và API.
     *
     * @param token Chuỗi JWT Token
     * @return Thông tin hồ sơ nhân viên kèm chi tiết phân quyền
     */
    @Transactional(readOnly = true)
    public StaffProfileResponse getStaffProfileFromToken(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token xác thực không được để trống.");
        }

        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Claims claims = jwtService.extractAllClaims(cleanToken);

        String userType = claims.get("userType", String.class);
        if (userType != null && !"STAFF".equalsIgnoreCase(userType)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token này không phải là token của nhân viên quản trị CMS.");
        }

        String maNhanVien = claims.get("maNhanVien", String.class);
        if (maNhanVien == null) {
            maNhanVien = claims.getSubject();
        }

        NhanVien nhanVien = nhanVienRepository.findByMaNhanVien(maNhanVien)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin nhân viên từ token."));

        // Kiểm tra lại trạng thái nhân viên
        if (nhanVien.getTrangThai() == null || nhanVien.getTrangThai() != 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản nhân viên đã bị khóa hoặc ngừng hoạt động.");
        }

        VaiTro vaiTro = nhanVien.getVaiTro();
        return StaffProfileResponse.builder()
                .id(nhanVien.getId())
                .maNhanVien(nhanVien.getMaNhanVien())
                .hoTen(nhanVien.getHoTen())
                .tenDangNhap(nhanVien.getTenDangNhap())
                .email(nhanVien.getEmail())
                .soDienThoai(nhanVien.getSoDienThoai())
                .maVaiTro(vaiTro != null ? vaiTro.getMaVaiTro() : null)
                .tenVaiTro(vaiTro != null ? vaiTro.getTenVaiTro() : null)
                .moTaVaiTro(vaiTro != null ? vaiTro.getMoTa() : null)
                .trangThai(nhanVien.getTrangThai())
                .ngayTao(nhanVien.getNgayTao())
                .build();
    }

    /**
     * Xác thực JWT Token và kiểm tra nghiêm ngặt vai trò Quản trị viên (ADMIN).
     * CHỈ CÓ TÀI KHOẢN CÓ VAI TRÒ ADMIN MỚI ĐƯỢC PHÉP TRUY CẬP (US05).
     * Ném ResponseStatusException(HttpStatus.FORBIDDEN) nếu tài khoản có vai trò khác (CSKH, BAN_HANG, KHO,...).
     *
     * @param token Chuỗi JWT Token (có thể có hoặc không có tiền tố 'Bearer ')
     * @return StaffProfileResponse thông tin hồ sơ của Quản trị viên
     */
    @Transactional(readOnly = true)
    public StaffProfileResponse validateAdminAccess(String token) {
        StaffProfileResponse staff = getStaffProfileFromToken(token);
        if (!"ADMIN".equalsIgnoreCase(staff.getMaVaiTro())) {
            log.warn("TỪ CHỐI TRUY CẬP: Nhân viên [{}] (vai trò: {}) cố gắng truy cập tính năng Dashboard dành riêng cho Admin",
                    staff.getTenDangNhap(), staff.getMaVaiTro());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Từ chối truy cập: Chức năng Dashboard tổng quan chỉ dành riêng cho Quản trị viên (ADMIN). Tài khoản hiện tại có vai trò: "
                            + staff.getTenVaiTro() + " (" + staff.getMaVaiTro() + ").");
        }
        return staff;
    }
}
