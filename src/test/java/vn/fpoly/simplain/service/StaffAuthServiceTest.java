package vn.fpoly.simplain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Kiểm thử đơn vị nghiệp vụ Xác thực Nhân viên CMS Quản trị US02 (StaffAuthService).
 * Kiểm tra đầy đủ:
 * 1. Check phân quyền (Role-based).
 * 2. Từ chối đăng nhập nếu trạng thái nhân viên bị khóa (trang_thai = 0).
 */
@DisplayName("Kiểm thử logic Xác thực Nhân viên CMS US02 (StaffAuthService)")
public class StaffAuthServiceTest {

    private InMemoryVaiTroRepository vaiTroRepository;
    private InMemoryNhanVienRepository nhanVienRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private StaffAuthService staffAuthService;

    @BeforeEach
    void setUp() {
        vaiTroRepository = new InMemoryVaiTroRepository();
        nhanVienRepository = new InMemoryNhanVienRepository();
        passwordEncoder = new BCryptPasswordEncoder(10);

        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-for-simplain-auth-service-256-bits-super-secure!");
        jwtProperties.setExpirationMs(3600000L);
        jwtService = new JwtService(jwtProperties);

        staffAuthService = new StaffAuthService(
                nhanVienRepository,
                vaiTroRepository,
                jwtService,
                passwordEncoder,
                jwtProperties
        );

        // Khởi tạo dữ liệu mẫu
        staffAuthService.initDefaultStaffData();
    }

    @Test
    @DisplayName("TC01 - Đăng nhập Quản trị viên (ADMIN) thành công: Kiểm tra Role-based claim & Token")
    void testLogin_Success_AdminRole() {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("superadmin")
                .matKhau("Admin@123")
                .build();

        StaffAuthResponse response = staffAuthService.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken(), "Token JWT phải được cấp phát");
        assertEquals("NV001", response.getMaNhanVien());
        assertEquals("ADMIN", response.getMaVaiTro(), "Tiêu chí 1: Vai trò phải là ADMIN");
        assertEquals("Quản trị viên tối cao", response.getTenVaiTro());
        assertEquals(1, response.getTrangThai(), "Trạng thái nhân viên phải là 1 (Active)");
    }

    @Test
    @DisplayName("TC02 - Đăng nhập Chăm sóc khách hàng (CSKH) thành công: Kiểm tra phân quyền CSKH")
    void testLogin_Success_CskhRole() {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("cskh_01")
                .matKhau("Staff@123")
                .build();

        StaffAuthResponse response = staffAuthService.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("NV002", response.getMaNhanVien());
        assertEquals("CSKH", response.getMaVaiTro(), "Tiêu chí 1: Phải nhận diện đúng vai trò CSKH");
        assertEquals(1, response.getTrangThai());
    }

    @Test
    @DisplayName("TC03 - Đăng nhập bằng Email nhân viên thành công")
    void testLogin_Success_UsingEmail() {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("admin@simplain.vn")
                .matKhau("Admin@123")
                .build();

        StaffAuthResponse response = staffAuthService.login(request);

        assertNotNull(response);
        assertEquals("NV001", response.getMaNhanVien());
        assertEquals("ADMIN", response.getMaVaiTro());
    }

    @Test
    @DisplayName("TC04 - Đăng nhập bằng Số điện thoại nhân viên thành công")
    void testLogin_Success_UsingPhoneNumber() {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("0904445566")
                .matKhau("Staff@123")
                .build();

        StaffAuthResponse response = staffAuthService.login(request);

        assertNotNull(response);
        assertEquals("NV002", response.getMaNhanVien());
        assertEquals("CSKH", response.getMaVaiTro());
    }

    @Test
    @DisplayName("TC05 - Tiêu chí 2: TỪ CHỐI ĐĂNG NHẬP nếu trạng thái nhân viên bị khóa (trang_thai = 0)")
    void testLogin_Failure_AccountLocked() {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("nhanvien_khoa")
                .matKhau("Staff@123")
                .build();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> staffAuthService.login(request),
                "Tiêu chí 2: Hệ thống phải từ chối khi tài khoản bị khóa"
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("đã bị khóa hoặc ngừng hoạt động"),
                "Thông báo lỗi phải nêu rõ tài khoản nhân viên bị khóa");
    }

    @Test
    @DisplayName("TC06 - Tiêu chí 1: Từ chối đăng nhập nếu vai trò nhân viên bị tạm ngưng (trang_thai = 0)")
    void testLogin_Failure_RoleInactive() {
        // Tạo vai trò bị vô hiệu hóa
        VaiTro inactiveRole = vaiTroRepository.save(VaiTro.builder()
                .maVaiTro("KHO")
                .tenVaiTro("Quản lý kho hàng")
                .trangThai(0) // Vô hiệu hóa
                .build());

        nhanVienRepository.save(NhanVien.builder()
                .maNhanVien("NV888")
                .hoTen("Nhân Viên Kho")
                .tenDangNhap("kho_01")
                .matKhauHash(passwordEncoder.encode("Staff@123"))
                .vaiTro(inactiveRole)
                .trangThai(1)
                .build());

        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("kho_01")
                .matKhau("Staff@123")
                .build();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> staffAuthService.login(request)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("tạm ngưng hoạt động"));
    }

    @Test
    @DisplayName("TC07 - Từ chối đăng nhập khi sai mật khẩu")
    void testLogin_Failure_WrongPassword() {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("superadmin")
                .matKhau("SaiMatKhau999")
                .build();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> staffAuthService.login(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("không chính xác"));
    }

    @Test
    @DisplayName("TC08 - Từ chối đăng nhập khi tài khoản nhân viên không tồn tại")
    void testLogin_Failure_UserNotFound() {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("khong_ton_tai")
                .matKhau("MatKhau123")
                .build();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> staffAuthService.login(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("không tồn tại"));
    }

    @Test
    @DisplayName("TC09 - Từ chối khi để trống tên đăng nhập hoặc mật khẩu")
    void testLogin_Failure_BlankCredentials() {
        StaffLoginRequest blankUsername = StaffLoginRequest.builder()
                .tenDangNhap("   ")
                .matKhau("Admin@123")
                .build();

        assertThrows(ResponseStatusException.class, () -> staffAuthService.login(blankUsername));

        StaffLoginRequest blankPassword = StaffLoginRequest.builder()
                .tenDangNhap("superadmin")
                .matKhau("   ")
                .build();

        assertThrows(ResponseStatusException.class, () -> staffAuthService.login(blankPassword));
    }

    @Test
    @DisplayName("TC10 - Trích xuất hồ sơ nhân viên từ JWT Token thành công")
    void testGetStaffProfileFromToken_Success() {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("superadmin")
                .matKhau("Admin@123")
                .build();
        StaffAuthResponse authResponse = staffAuthService.login(request);

        StaffProfileResponse profile = staffAuthService.getStaffProfileFromToken(authResponse.getToken());

        assertNotNull(profile);
        assertEquals("NV001", profile.getMaNhanVien());
        assertEquals("superadmin", profile.getTenDangNhap());
        assertEquals("ADMIN", profile.getMaVaiTro());
        assertEquals("Quản trị viên tối cao", profile.getTenVaiTro());
    }

    @Test
    @DisplayName("TC11 - validateAdminAccess với vai trò ADMIN: Thành công và trả về thông tin Admin")
    void testValidateAdminAccess_Admin_Success() {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("superadmin")
                .matKhau("Admin@123")
                .build();
        StaffAuthResponse authResponse = staffAuthService.login(request);

        StaffProfileResponse adminProfile = staffAuthService.validateAdminAccess(authResponse.getToken());

        assertNotNull(adminProfile);
        assertEquals("ADMIN", adminProfile.getMaVaiTro());
        assertEquals("superadmin", adminProfile.getTenDangNhap());
    }

    @Test
    @DisplayName("TC12 - validateAdminAccess với vai trò CSKH: Bị từ chối và ném ngoại lệ 403 Forbidden")
    void testValidateAdminAccess_NonAdmin_ThrowsForbidden() {
        StaffLoginRequest request = StaffLoginRequest.builder()
                .tenDangNhap("cskh_01")
                .matKhau("Staff@123")
                .build();
        StaffAuthResponse authResponse = staffAuthService.login(request);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                staffAuthService.validateAdminAccess(authResponse.getToken()));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Từ chối truy cập: Chức năng Dashboard tổng quan chỉ dành riêng cho Quản trị viên (ADMIN)"));
    }

    @Test
    @DisplayName("TC13 - validateAdminAccess với token rỗng hoặc null: Ném ngoại lệ 401 Unauthorized")
    void testValidateAdminAccess_NullOrBlankToken_ThrowsUnauthorized() {
        ResponseStatusException ex1 = assertThrows(ResponseStatusException.class, () ->
                staffAuthService.validateAdminAccess(null));
        assertEquals(HttpStatus.UNAUTHORIZED, ex1.getStatusCode());

        ResponseStatusException ex2 = assertThrows(ResponseStatusException.class, () ->
                staffAuthService.validateAdminAccess("   "));
        assertEquals(HttpStatus.UNAUTHORIZED, ex2.getStatusCode());
    }

    // ==========================================
    // IN-MEMORY FAKE REPOSITORIES
    // ==========================================

    public static class InMemoryVaiTroRepository implements VaiTroRepository {
        private final List<VaiTro> store = new ArrayList<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public Optional<VaiTro> findByMaVaiTro(String maVaiTro) {
            return store.stream().filter(v -> v.getMaVaiTro().equalsIgnoreCase(maVaiTro)).findFirst();
        }

        @Override
        public <S extends VaiTro> S save(S entity) {
            if (entity.getId() == null) {
                entity.setId(idGen.getAndIncrement());
            }
            store.removeIf(v -> v.getId().equals(entity.getId()));
            store.add(entity);
            return entity;
        }

        @Override public Optional<VaiTro> findById(Long id) { return store.stream().filter(v -> v.getId().equals(id)).findFirst(); }
        @Override public List<VaiTro> findAll() { return new ArrayList<>(store); }
        @Override public boolean existsById(Long id) { return store.stream().anyMatch(v -> v.getId().equals(id)); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.removeIf(v -> v.getId().equals(id)); }
        @Override public void delete(VaiTro entity) { store.remove(entity); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends VaiTro> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<VaiTro> findAll(Sort sort) { return findAll(); }
        @Override public Page<VaiTro> findAll(Pageable pageable) { return null; }
        @Override public <S extends VaiTro> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public void flush() {}
        @Override public <S extends VaiTro> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends VaiTro> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<VaiTro> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}
        @Override public void deleteAllInBatch() {}
        @Override public VaiTro getOne(Long aLong) { return null; }
        @Override public VaiTro getById(Long aLong) { return null; }
        @Override public VaiTro getReferenceById(Long aLong) { return null; }
        @Override public <S extends VaiTro> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends VaiTro> List<S> findAll(Example<S> example) { return null; }
        @Override public <S extends VaiTro> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override public <S extends VaiTro> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends VaiTro> long count(Example<S> example) { return 0; }
        @Override public <S extends VaiTro> boolean exists(Example<S> example) { return false; }
        @Override public <S extends VaiTro, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<VaiTro> findAllById(Iterable<Long> longs) { return null; }
    }

    public static class InMemoryNhanVienRepository implements NhanVienRepository {
        private final List<NhanVien> store = new ArrayList<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public Optional<NhanVien> findByTenDangNhap(String tenDangNhap) {
            return store.stream().filter(nv -> nv.getTenDangNhap().equalsIgnoreCase(tenDangNhap)).findFirst();
        }

        @Override
        public Optional<NhanVien> findByEmail(String email) {
            return store.stream().filter(nv -> nv.getEmail() != null && nv.getEmail().equalsIgnoreCase(email)).findFirst();
        }

        @Override
        public Optional<NhanVien> findBySoDienThoai(String soDienThoai) {
            return store.stream().filter(nv -> nv.getSoDienThoai() != null && nv.getSoDienThoai().equals(soDienThoai)).findFirst();
        }

        @Override
        public Optional<NhanVien> findByMaNhanVien(String maNhanVien) {
            return store.stream().filter(nv -> nv.getMaNhanVien().equalsIgnoreCase(maNhanVien)).findFirst();
        }

        @Override
        public Optional<NhanVien> findByIdentifier(String identifier) {
            return store.stream().filter(nv ->
                    nv.getTenDangNhap().equalsIgnoreCase(identifier)
                    || (nv.getEmail() != null && nv.getEmail().equalsIgnoreCase(identifier))
                    || (nv.getSoDienThoai() != null && nv.getSoDienThoai().equals(identifier))
            ).findFirst();
        }

        @Override
        public <S extends NhanVien> S save(S entity) {
            if (entity.getId() == null) {
                entity.setId(idGen.getAndIncrement());
            }
            if (entity.getNgayTao() == null) {
                entity.setNgayTao(LocalDateTime.now());
            }
            store.removeIf(nv -> nv.getId().equals(entity.getId()));
            store.add(entity);
            return entity;
        }

        @Override public Optional<NhanVien> findById(Long id) { return store.stream().filter(nv -> nv.getId().equals(id)).findFirst(); }
        @Override public List<NhanVien> findAll() { return new ArrayList<>(store); }
        @Override public boolean existsById(Long id) { return store.stream().anyMatch(nv -> nv.getId().equals(id)); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.removeIf(nv -> nv.getId().equals(id)); }
        @Override public void delete(NhanVien entity) { store.remove(entity); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends NhanVien> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<NhanVien> findAll(Sort sort) { return findAll(); }
        @Override public Page<NhanVien> findAll(Pageable pageable) { return null; }
        @Override public <S extends NhanVien> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public void flush() {}
        @Override public <S extends NhanVien> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends NhanVien> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<NhanVien> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}
        @Override public void deleteAllInBatch() {}
        @Override public NhanVien getOne(Long aLong) { return null; }
        @Override public NhanVien getById(Long aLong) { return null; }
        @Override public NhanVien getReferenceById(Long aLong) { return null; }
        @Override public <S extends NhanVien> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends NhanVien> List<S> findAll(Example<S> example) { return null; }
        @Override public <S extends NhanVien> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override public <S extends NhanVien> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends NhanVien> long count(Example<S> example) { return 0; }
        @Override public <S extends NhanVien> boolean exists(Example<S> example) { return false; }
        @Override public <S extends NhanVien, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<NhanVien> findAllById(Iterable<Long> longs) { return null; }
    }
}

