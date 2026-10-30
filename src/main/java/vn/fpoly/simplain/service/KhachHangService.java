package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.fpoly.simplain.dto.CapNhatThongTinRequest;
import vn.fpoly.simplain.dto.DangKyRequest;
import vn.fpoly.simplain.dto.DangNhapRequest;
import vn.fpoly.simplain.dto.DoiMatKhauRequest;
import vn.fpoly.simplain.dto.ThongTinKhachHangResponse;
import vn.fpoly.simplain.dto.XacThucResponse;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.util.JwtUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service xử lý các nghiệp vụ xác thực khách hàng (Đăng ký, Đăng nhập, Mã hóa mật khẩu, Cấp phát JWT).
 */
@Service
public class KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public KhachHangService(KhachHangRepository khachHangRepository,
                            PasswordEncoder passwordEncoder,
                            JwtUtil jwtUtil) {
        this.khachHangRepository = khachHangRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Nghiệp vụ đăng ký tài khoản khách hàng mới.
     *
     * @param request dữ liệu đăng ký
     * @param anhFile file ảnh đại diện tải lên từ máy tính (nếu có)
     * @return kết quả xác thực kèm JWT Token
     */
    @Transactional
    public XacThucResponse dangKy(DangKyRequest request, MultipartFile anhFile) {
        // 1. Kiểm tra xác nhận mật khẩu
        if (request.getXacNhanMatKhau() != null && !request.getXacNhanMatKhau().isBlank()) {
            if (!request.getMatKhau().equals(request.getXacNhanMatKhau())) {
                throw new IllegalArgumentException("Mật khẩu xác nhận không trùng khớp");
            }
        }

        // 2. Kiểm tra trùng lặp email
        String email = request.getEmail().trim().toLowerCase();
        if (khachHangRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email '" + email + "' đã tồn tại trong hệ thống");
        }

        // 3. Kiểm tra định dạng và trùng lặp số điện thoại
        String sdtRaw = request.getSoDienThoai().trim();
        String sdt = sdtRaw.replaceAll("[^0-9]", "");
        if (sdt.startsWith("84") && sdt.length() >= 10) {
            sdt = "0" + sdt.substring(2);
        }
        if (sdt.isBlank()) {
            sdt = sdtRaw;
        }
        if (!sdt.matches("^0[35789][0-9]{8}$")) {
            throw new IllegalArgumentException("Số điện thoại không đúng định dạng di động Việt Nam (phải gồm 10 chữ số, ví dụ: 0912345678, 0388888888)!");
        }
        Optional<KhachHang> khTrung = khachHangRepository.findBySoDienThoai(sdt);
        if (khTrung.isPresent()) {
            throw new IllegalArgumentException("Số điện thoại '" + sdt + "' đã tồn tại trong hệ thống (thuộc về khách hàng '" + khTrung.get().getHoTen() + "')!");
        }

        // 4. Sinh mã khách hàng tự động
        String maKhachHang = sinhMaKhachHang();

        // 5. Mã hóa mật khẩu bằng BCrypt
        String matKhauHash = passwordEncoder.encode(request.getMatKhau());

        // 6. Xử lý lưu ảnh đại diện nếu có
        String anhDaiDienUrl = luuAnhDaiDien(anhFile);

        // 7. Tạo đối tượng Entity và lưu vào database
        KhachHang khachHang = KhachHang.builder()
                .maKhachHang(maKhachHang)
                .hoTen(request.getHoTen().trim())
                .email(email)
                .soDienThoai(sdt)
                .matKhauHash(matKhauHash)
                .anhDaiDien(anhDaiDienUrl)
                .gioiTinh(request.getGioiTinh())
                .ngaySinh(request.getNgaySinh())
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build();

        KhachHang daLuu = khachHangRepository.save(khachHang);

        // 8. Cấp phát JWT Token
        String token = jwtUtil.generateToken(daLuu);

        return XacThucResponse.builder()
                .token(token)
                .loaiToken("Bearer")
                .id(daLuu.getId())
                .maKhachHang(daLuu.getMaKhachHang())
                .hoTen(daLuu.getHoTen())
                .email(daLuu.getEmail())
                .soDienThoai(daLuu.getSoDienThoai())
                .anhDaiDien(daLuu.getAnhDaiDien())
                .thongBao("Đăng ký tài khoản thành công!")
                .build();
    }

    /**
     * Nghiệp vụ đăng nhập tài khoản bằng email hoặc số điện thoại.
     *
     * @param request dữ liệu đăng nhập
     * @return kết quả xác thực kèm JWT Token
     */
    public XacThucResponse dangNhap(DangNhapRequest request) {
        String taiKhoan = request.getTaiKhoan().trim();

        // 1. Tìm kiếm theo email hoặc số điện thoại
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByEmailOrSoDienThoai(taiKhoan, taiKhoan);
        if (khachHangOpt.isEmpty()) {
            throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác");
        }

        KhachHang khachHang = khachHangOpt.get();

        // 2. Kiểm tra trạng thái hoạt động
        if (khachHang.getTrangThai() != null && khachHang.getTrangThai() == 0) {
            throw new IllegalStateException("Tài khoản đã bị tạm khóa, vui lòng liên hệ quản trị viên");
        }

        // 3. So khớp mật khẩu với mã băm BCrypt
        if (!passwordEncoder.matches(request.getMatKhau(), khachHang.getMatKhauHash())) {
            throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác");
        }

        // 4. Cấp phát JWT Token mới
        String token = jwtUtil.generateToken(khachHang);

        return XacThucResponse.builder()
                .token(token)
                .loaiToken("Bearer")
                .id(khachHang.getId())
                .maKhachHang(khachHang.getMaKhachHang())
                .hoTen(khachHang.getHoTen())
                .email(khachHang.getEmail())
                .soDienThoai(khachHang.getSoDienThoai())
                .anhDaiDien(khachHang.getAnhDaiDien())
                .thongBao("Đăng nhập thành công!")
                .build();
    }

    /**
     * Xác minh JWT Token và lấy thông tin chi tiết hồ sơ khách hàng.
     *
     * @param rawToken chuỗi token hoặc Bearer token
     * @return hồ sơ khách hàng
     */
    public ThongTinKhachHangResponse layThongTinTuToken(String rawToken) {
        String token = chuanHoaToken(rawToken);
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("JWT Token không hợp lệ hoặc đã hết hạn");
        }

        Long khachHangId = jwtUtil.extractCustomerId(token);
        String subject = jwtUtil.extractSubject(token);

        KhachHang khachHang = null;
        if (khachHangId != null) {
            khachHang = khachHangRepository.findById(khachHangId).orElse(null);
        }
        if (khachHang == null && subject != null) {
            khachHang = khachHangRepository.findByEmailOrSoDienThoai(subject, subject).orElse(null);
        }

        if (khachHang == null) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng tương ứng với token");
        }

        return ThongTinKhachHangResponse.builder()
                .id(khachHang.getId())
                .maKhachHang(khachHang.getMaKhachHang())
                .hoTen(khachHang.getHoTen())
                .soDienThoai(khachHang.getSoDienThoai())
                .email(khachHang.getEmail())
                .anhDaiDien(khachHang.getAnhDaiDien())
                .gioiTinh(khachHang.getGioiTinh())
                .ngaySinh(khachHang.getNgaySinh())
                .trangThai(khachHang.getTrangThai())
                .ngayTao(khachHang.getNgayTao())
                .build();
    }

    /**
     * Nghiệp vụ Cập nhật thông tin cá nhân khách hàng (US12 - Tiêu chí 1).
     * Cập nhật: Tên, SĐT, Email, Giới tính, Ngày sinh và Ảnh đại diện (nếu có).
     */
    @Transactional
    public XacThucResponse capNhatThongTin(Long khachHangId, CapNhatThongTinRequest request, MultipartFile anhFile) {
        KhachHang khachHang = layTheoId(khachHangId);

        // 1. Kiểm tra họ và tên
        if (request.getHoTen() == null || request.getHoTen().isBlank()) {
            throw new IllegalArgumentException("Họ và tên không được để trống!");
        }

        // 2. Kiểm tra trùng lặp email với tài khoản khác
        String email = request.getEmail().trim().toLowerCase();
        if (khachHangRepository.existsByEmailAndIdNot(email, khachHangId)) {
            throw new IllegalArgumentException("Email '" + email + "' đã được sử dụng bởi tài khoản khác!");
        }

        // 3. Kiểm tra định dạng và trùng lặp số điện thoại với tài khoản khác
        String sdtRaw = request.getSoDienThoai().trim();
        String sdt = sdtRaw.replaceAll("[^0-9]", "");
        if (sdt.startsWith("84") && sdt.length() >= 10) {
            sdt = "0" + sdt.substring(2);
        }
        if (sdt.isBlank()) {
            sdt = sdtRaw;
        }
        if (!sdt.matches("^0[35789][0-9]{8}$")) {
            throw new IllegalArgumentException("Số điện thoại không đúng định dạng di động Việt Nam (phải gồm 10 chữ số, ví dụ: 0912345678, 0388888888)!");
        }
        if (khachHangRepository.existsBySoDienThoaiAndIdNot(sdt, khachHangId)) {
            throw new IllegalArgumentException("Số điện thoại '" + sdt + "' đã được sử dụng bởi tài khoản khác trong hệ thống!");
        }

        // 4. Kiểm tra tính hợp lệ của ngày sinh
        if (request.getNgaySinh() != null && request.getNgaySinh().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không hợp lệ (không thể lớn hơn ngày hiện tại)!");
        }

        // 5. Lưu ảnh đại diện mới nếu khách hàng tải lên
        if (anhFile != null && !anhFile.isEmpty()) {
            String anhMoi = luuAnhDaiDien(anhFile);
            if (anhMoi != null) {
                khachHang.setAnhDaiDien(anhMoi);
            }
        }

        // 6. Cập nhật các trường thông tin
        khachHang.setHoTen(request.getHoTen().trim());
        khachHang.setEmail(email);
        khachHang.setSoDienThoai(sdt);
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setNgaySinh(request.getNgaySinh());

        KhachHang daCapNhat = khachHangRepository.save(khachHang);

        // 7. Cấp lại JWT Token mới phản ánh thông tin cập nhật
        String tokenMoi = jwtUtil.generateToken(daCapNhat);

        return XacThucResponse.builder()
                .token(tokenMoi)
                .loaiToken("Bearer")
                .id(daCapNhat.getId())
                .maKhachHang(daCapNhat.getMaKhachHang())
                .hoTen(daCapNhat.getHoTen())
                .email(daCapNhat.getEmail())
                .soDienThoai(daCapNhat.getSoDienThoai())
                .anhDaiDien(daCapNhat.getAnhDaiDien())
                .thongBao("Cập nhật thông tin cá nhân thành công!")
                .build();
    }

    /**
     * Nghiệp vụ Đổi mật khẩu tài khoản khách hàng (US12 - Tiêu chí 2).
     */
    @Transactional
    public XacThucResponse doiMatKhau(Long khachHangId, DoiMatKhauRequest request) {
        KhachHang khachHang = layTheoId(khachHangId);

        // 1. Kiểm tra mật khẩu hiện tại
        if (request.getMatKhauHienTai() == null || request.getMatKhauHienTai().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu hiện tại!");
        }
        if (!passwordEncoder.matches(request.getMatKhauHienTai(), khachHang.getMatKhauHash())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác!");
        }

        // 2. Kiểm tra mật khẩu mới
        if (request.getMatKhauMoi() == null || request.getMatKhauMoi().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu mới!");
        }
        if (request.getMatKhauMoi().length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự!");
        }

        // 3. Kiểm tra xác nhận mật khẩu mới
        if (!request.getMatKhauMoi().equals(request.getXacNhanMatKhauMoi())) {
            throw new IllegalArgumentException("Xác nhận mật khẩu mới không trùng khớp!");
        }

        // 4. Kiểm tra mật khẩu mới không được trùng mật khẩu cũ
        if (passwordEncoder.matches(request.getMatKhauMoi(), khachHang.getMatKhauHash())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu hiện tại!");
        }

        // 5. Mã hóa mật khẩu mới bằng BCrypt và lưu vào database
        khachHang.setMatKhauHash(passwordEncoder.encode(request.getMatKhauMoi()));
        KhachHang daCapNhat = khachHangRepository.save(khachHang);

        // 6. Cấp JWT Token mới
        String tokenMoi = jwtUtil.generateToken(daCapNhat);

        return XacThucResponse.builder()
                .token(tokenMoi)
                .loaiToken("Bearer")
                .id(daCapNhat.getId())
                .maKhachHang(daCapNhat.getMaKhachHang())
                .hoTen(daCapNhat.getHoTen())
                .email(daCapNhat.getEmail())
                .soDienThoai(daCapNhat.getSoDienThoai())
                .anhDaiDien(daCapNhat.getAnhDaiDien())
                .thongBao("Đổi mật khẩu tài khoản thành công!")
                .build();
    }

    /**
     * Lấy thực thể Khách hàng theo ID.
     */
    public KhachHang layTheoId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID khách hàng không được để trống!");
        }
        return khachHangRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin khách hàng với ID: " + id));
    }

    /**
     * Chuẩn hóa token, loại bỏ tiền tố Bearer nếu có.
     */
    public String chuanHoaToken(String rawToken) {
        if (rawToken == null) {
            throw new IllegalArgumentException("Token không được để trống");
        }
        String token = rawToken.trim();
        if (token.startsWith("Bearer ") || token.startsWith("bearer ")) {
            token = token.substring(7).trim();
        }
        return token;
    }

    private String sinhMaKhachHang() {
        String ma;
        do {
            long duoi = Math.abs(System.currentTimeMillis() % 1000000);
            ma = "KH" + duoi;
        } while (khachHangRepository.existsByMaKhachHang(ma));
        return ma;
    }

    private String luuAnhDaiDien(MultipartFile anhFile) {
        if (anhFile == null || anhFile.isEmpty()) {
            return null;
        }
        try {
            String tenGoc = anhFile.getOriginalFilename();
            String tenSach = (tenGoc != null) ? tenGoc.replaceAll("\\s+", "_") : "avatar.jpg";
            String tenTep = "avatar_" + System.currentTimeMillis() + "_" + tenSach;
            Path thuMuc = Paths.get("uploads/");
            if (!Files.exists(thuMuc)) {
                Files.createDirectories(thuMuc);
            }
            Files.copy(anhFile.getInputStream(), thuMuc.resolve(tenTep), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + tenTep;
        } catch (IOException e) {
            return null;
        }
    }
}
