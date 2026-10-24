package vn.fpoly.simplain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import vn.fpoly.simplain.config.JwtProperties;
import vn.fpoly.simplain.dto.AuthResponse;
import vn.fpoly.simplain.dto.LoginRequest;
import vn.fpoly.simplain.dto.RegisterRequest;
import vn.fpoly.simplain.dto.VerifyTokenResponse;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.repository.KhachHangRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Kiểm thử đơn vị logic nghiệp vụ Xác thực Khách hàng US01 (AuthService & JwtService).
 */
@DisplayName("Kiểm thử logic Xác thực Khách hàng US01 (AuthService & JwtService)")
public class AuthServiceTest {

    private InMemoryKhachHangRepository repository;
    private BCryptPasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryKhachHangRepository();
        passwordEncoder = new BCryptPasswordEncoder(10);
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-for-simplain-auth-service-256-bits-super-secure!");
        jwtProperties.setExpirationMs(3600000L);
        jwtService = new JwtService(jwtProperties);
        authService = new AuthService(repository, passwordEncoder, jwtService);
    }

    @Test
    @DisplayName("TC01 - Đăng ký thành công: mã hóa BCrypt và cấp phát JWT Token")
    void registerSuccess() {
        RegisterRequest request = RegisterRequest.builder()
                .hoTen("Nguyễn Văn Duy")
                .email("duy.coolmate@test.com")
                .soDienThoai("0987654321")
                .matKhau("MatKhau@123")
                .xacNhanMatKhau("MatKhau@123")
                .build();

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("CUSTOMER", response.getVaiTro());
        assertEquals("duy.coolmate@test.com", response.getEmail());
        assertTrue(response.getMaKhachHang().startsWith("KH"));

        Optional<KhachHang> saved = repository.findByEmail("duy.coolmate@test.com");
        assertTrue(saved.isPresent());
        assertTrue(saved.get().getMatKhauHash().startsWith("$2a$") || saved.get().getMatKhauHash().startsWith("$2b$"));
        assertTrue(passwordEncoder.matches("MatKhau@123", saved.get().getMatKhauHash()));
    }

    @Test
    @DisplayName("TC02 - Đăng ký thất bại khi mật khẩu xác nhận không khớp")
    void registerFailPasswordMismatch() {
        RegisterRequest request = RegisterRequest.builder()
                .hoTen("Nguyễn Văn Duy")
                .email("duy.mismatch@test.com")
                .soDienThoai("0981112233")
                .matKhau("MatKhau@123")
                .xacNhanMatKhau("MatKhau@Khac")
                .build();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(request));
        assertTrue(ex.getReason().contains("không trùng khớp"));
    }

    @Test
    @DisplayName("TC03 - Đăng ký thất bại khi email đã tồn tại")
    void registerFailDuplicateEmail() {
        RegisterRequest first = RegisterRequest.builder()
                .hoTen("Khách Một")
                .email("trung.email@test.com")
                .soDienThoai("0981111111")
                .matKhau("MatKhau@123")
                .xacNhanMatKhau("MatKhau@123")
                .build();
        authService.register(first);

        RegisterRequest second = RegisterRequest.builder()
                .hoTen("Khách Hai")
                .email("trung.email@test.com")
                .soDienThoai("0982222222")
                .matKhau("MatKhau@456")
                .xacNhanMatKhau("MatKhau@456")
                .build();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(second));
        assertTrue(ex.getReason().contains("email này đã được sử dụng"));
    }

    @Test
    @DisplayName("TC04 - Đăng nhập thành công bằng email và mật khẩu đúng")
    void loginSuccessByEmail() {
        RegisterRequest request = RegisterRequest.builder()
                .hoTen("Trần Minh Quân")
                .email("quan.login@test.com")
                .soDienThoai("0977889900")
                .matKhau("MatKhau@Login1")
                .xacNhanMatKhau("MatKhau@Login1")
                .build();
        authService.register(request);

        LoginRequest loginRequest = LoginRequest.builder()
                .identifier("quan.login@test.com")
                .matKhau("MatKhau@Login1")
                .build();

        AuthResponse loginResponse = authService.login(loginRequest);

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getToken());
        assertEquals("quan.login@test.com", loginResponse.getEmail());
    }

    @Test
    @DisplayName("TC05 - Đăng nhập thành công bằng số điện thoại")
    void loginSuccessByPhone() {
        RegisterRequest request = RegisterRequest.builder()
                .hoTen("Lê Hoàng Nam")
                .email("nam.phone@test.com")
                .soDienThoai("0933445566")
                .matKhau("NamPass123")
                .xacNhanMatKhau("NamPass123")
                .build();
        authService.register(request);

        LoginRequest loginRequest = LoginRequest.builder()
                .identifier("0933445566")
                .matKhau("NamPass123")
                .build();

        AuthResponse loginResponse = authService.login(loginRequest);

        assertNotNull(loginResponse);
        assertEquals("nam.phone@test.com", loginResponse.getEmail());
    }

    @Test
    @DisplayName("TC06 - Đăng nhập thất bại khi sai mật khẩu")
    void loginFailWrongPassword() {
        RegisterRequest request = RegisterRequest.builder()
                .hoTen("Phạm Văn Khôi")
                .email("khoi.wrong@test.com")
                .soDienThoai("0966778899")
                .matKhau("DungPass123")
                .xacNhanMatKhau("DungPass123")
                .build();
        authService.register(request);

        LoginRequest loginRequest = LoginRequest.builder()
                .identifier("khoi.wrong@test.com")
                .matKhau("SaiPass999")
                .build();

        assertThrows(ResponseStatusException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("TC07 - Đăng nhập thất bại khi tài khoản bị khóa (trang_thai = 0)")
    void loginFailInactiveAccount() {
        RegisterRequest request = RegisterRequest.builder()
                .hoTen("Đỗ Tuấn Kiệt")
                .email("kiet.locked@test.com")
                .soDienThoai("0911223344")
                .matKhau("KietPass123")
                .xacNhanMatKhau("KietPass123")
                .build();
        authService.register(request);

        // Khóa tài khoản
        KhachHang khachHang = repository.findByEmail("kiet.locked@test.com").orElseThrow();
        khachHang.setTrangThai(0);
        repository.save(khachHang);

        LoginRequest loginRequest = LoginRequest.builder()
                .identifier("kiet.locked@test.com")
                .matKhau("KietPass123")
                .build();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(loginRequest));
        assertTrue(ex.getReason().contains("tạm khóa"));
    }

    @Test
    @DisplayName("TC08 - Cấp phát và xác minh JWT Token hợp lệ")
    void verifyJwtTokenValid() {
        KhachHang khachHang = KhachHang.builder()
                .id(100L)
                .maKhachHang("KH9999")
                .hoTen("Test JWT User")
                .email("test.jwt@coolmate.me")
                .soDienThoai("0999888777")
                .trangThai(1)
                .build();
        repository.save(khachHang);

        String token = jwtService.generateToken(khachHang);
        assertNotNull(token);

        VerifyTokenResponse verify = authService.verifyToken(token);
        assertTrue(verify.isValid());
        assertEquals("KH9999", verify.getMaKhachHang());
        assertEquals("test.jwt@coolmate.me", verify.getEmail());
    }

    @Test
    @DisplayName("TC09 - Xác minh JWT Token bị giả mạo trả về invalid")
    void verifyJwtTokenTampered() {
        KhachHang khachHang = KhachHang.builder()
                .id(101L)
                .maKhachHang("KH8888")
                .hoTen("Tampered User")
                .email("tampered@test.com")
                .soDienThoai("0999111222")
                .trangThai(1)
                .build();
        repository.save(khachHang);

        String token = jwtService.generateToken(khachHang);
        String tamperedToken = token + "corrupted";

        VerifyTokenResponse verify = authService.verifyToken(tamperedToken);
        assertFalse(verify.isValid());
    }

    @Test
    @DisplayName("TC10 - Đăng ký thất bại khi email Gmail thiếu đuôi .com (ví dụ: test@gmail.co)")
    void registerFailGmailNotCom() {
        RegisterRequest request = RegisterRequest.builder()
                .hoTen("Nguyễn Văn Duy")
                .email("12345@gmail.co")
                .soDienThoai("0987654321")
                .matKhau("MatKhau123")
                .xacNhanMatKhau("MatKhau123")
                .build();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(request));
        assertTrue(ex.getReason().contains("đầy đủ là @gmail.com"));
    }

    @Test
    @DisplayName("TC11 - Đăng ký thất bại khi họ và tên chứa chữ số")
    void registerFailNameContainsNumber() {
        RegisterRequest request = RegisterRequest.builder()
                .hoTen("Nguyễn Duy 123")
                .email("duy@gmail.com")
                .soDienThoai("0987654321")
                .matKhau("MatKhau123")
                .xacNhanMatKhau("MatKhau123")
                .build();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(request));
        assertTrue(ex.getReason().contains("chữ số"));
    }

    @Test
    @DisplayName("TC12 - Đăng ký thất bại khi số điện thoại không đủ 10 số")
    void registerFailPhoneInvalidLength() {
        RegisterRequest request = RegisterRequest.builder()
                .hoTen("Nguyễn Văn Duy")
                .email("duy@gmail.com")
                .soDienThoai("098765432") // 9 số
                .matKhau("MatKhau123")
                .xacNhanMatKhau("MatKhau123")
                .build();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(request));
        assertTrue(ex.getReason().contains("10 chữ số"));
    }

    @Test
    @DisplayName("TC13 - Đăng nhập thất bại khi tài khoản identifier sai định dạng")
    void loginFailInvalidIdentifier() {
        LoginRequest request = LoginRequest.builder()
                .identifier("sai-email@gmail.co")
                .matKhau("MatKhau123")
                .build();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(request));
        assertTrue(ex.getReason().contains("đầy đủ là @gmail.com"));
    }

    /**
     * Fake InMemoryKhachHangRepository phục vụ kiểm thử đơn vị độc lập không phụ thuộc DB ngoại vi.
     */
    public static class InMemoryKhachHangRepository implements KhachHangRepository {
        private final List<KhachHang> database = new ArrayList<>();
        private final AtomicLong idSeq = new AtomicLong(1);

        @Override
        public Optional<KhachHang> findByEmail(String email) {
            return database.stream().filter(k -> email.equalsIgnoreCase(k.getEmail())).findFirst();
        }

        @Override
        public Optional<KhachHang> findBySoDienThoai(String soDienThoai) {
            return database.stream().filter(k -> soDienThoai.equals(k.getSoDienThoai())).findFirst();
        }

        @Override
        public Optional<KhachHang> findByMaKhachHang(String maKhachHang) {
            return database.stream().filter(k -> maKhachHang.equalsIgnoreCase(k.getMaKhachHang())).findFirst();
        }

        @Override
        public boolean existsByEmail(String email) {
            return database.stream().anyMatch(k -> email.equalsIgnoreCase(k.getEmail()));
        }

        @Override
        public boolean existsBySoDienThoai(String soDienThoai) {
            return database.stream().anyMatch(k -> soDienThoai.equals(k.getSoDienThoai()));
        }

        @Override
        public boolean existsByMaKhachHang(String maKhachHang) {
            return database.stream().anyMatch(k -> maKhachHang.equalsIgnoreCase(k.getMaKhachHang()));
        }

        @Override
        public <S extends KhachHang> S save(S entity) {
            if (entity.getId() == null) {
                entity.setId(idSeq.getAndIncrement());
            } else {
                database.removeIf(k -> k.getId().equals(entity.getId()));
            }
            database.add(entity);
            return entity;
        }

        @Override
        public Optional<KhachHang> findById(Long id) {
            return database.stream().filter(k -> k.getId().equals(id)).findFirst();
        }

        @Override
        public List<KhachHang> findAll() { return new ArrayList<>(database); }

        @Override
        public void deleteAll() { database.clear(); }

        @Override
        public void flush() {}
        @Override
        public <S extends KhachHang> S saveAndFlush(S entity) { return save(entity); }
        @Override
        public <S extends KhachHang> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override
        public void deleteAllInBatch(Iterable<KhachHang> entities) {}
        @Override
        public void deleteAllByIdInBatch(Iterable<Long> longs) {}
        @Override
        public void deleteAllInBatch() {}
        @Override
        public KhachHang getOne(Long aLong) { return null; }
        @Override
        public KhachHang getById(Long aLong) { return null; }
        @Override
        public KhachHang getReferenceById(Long aLong) { return null; }
        @Override
        public <S extends KhachHang> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override
        public <S extends KhachHang> List<S> findAll(Example<S> example) { return null; }
        @Override
        public <S extends KhachHang> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override
        public <S extends KhachHang> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override
        public <S extends KhachHang> long count(Example<S> example) { return 0; }
        @Override
        public <S extends KhachHang> boolean exists(Example<S> example) { return false; }
        @Override
        public <S extends KhachHang, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override
        public <S extends KhachHang> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override
        public boolean existsById(Long aLong) { return false; }
        @Override
        public List<KhachHang> findAllById(Iterable<Long> longs) { return null; }
        @Override
        public long count() { return database.size(); }
        @Override
        public void deleteById(Long aLong) {}
        @Override
        public void delete(KhachHang entity) {}
        @Override
        public void deleteAllById(Iterable<? extends Long> longs) {}
        @Override
        public void deleteAll(Iterable<? extends KhachHang> entities) {}
        @Override
        public List<KhachHang> findAll(Sort sort) { return null; }
        @Override
        public Page<KhachHang> findAll(Pageable pageable) { return null; }
    }
}
