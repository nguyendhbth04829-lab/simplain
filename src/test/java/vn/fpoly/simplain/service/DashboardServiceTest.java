package vn.fpoly.simplain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import vn.fpoly.simplain.dto.DashboardOverviewResponse;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.TrangThaiDonHang;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.TrangThaiDonHangRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Kiểm thử đơn vị nghiệp vụ Bảng điều khiển Dashboard tổng quan US05 (DashboardService).
 * Đảm bảo các tiêu chí:
 * 1. Thống kê tổng số lượng người dùng.
 * 2. Số lượng đơn hàng hôm nay.
 * 3. Doanh thu trong ngày.
 * 4. Biểu đồ doanh thu 7 ngày gần nhất và cơ cấu trạng thái đơn hàng.
 */
@DisplayName("Kiểm thử logic Dashboard tổng quan US05 (DashboardService)")
public class DashboardServiceTest {

    private InMemoryDonHangRepository donHangRepository;
    private InMemoryTrangThaiDonHangRepository trangThaiDonHangRepository;
    private AuthServiceTest.InMemoryKhachHangRepository khachHangRepository;
    private StaffAuthServiceTest.InMemoryNhanVienRepository nhanVienRepository;
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        donHangRepository = new InMemoryDonHangRepository();
        trangThaiDonHangRepository = new InMemoryTrangThaiDonHangRepository();
        khachHangRepository = new AuthServiceTest.InMemoryKhachHangRepository();
        nhanVienRepository = new StaffAuthServiceTest.InMemoryNhanVienRepository();

        dashboardService = new DashboardService(
                donHangRepository,
                trangThaiDonHangRepository,
                khachHangRepository,
                nhanVienRepository
        );

        // Nạp dữ liệu mẫu người dùng
        khachHangRepository.save(KhachHang.builder().maKhachHang("KH001").hoTen("Trần Mua Hàng").email("tran@gmail.com").soDienThoai("0901234567").build());
        khachHangRepository.save(KhachHang.builder().maKhachHang("KH002").hoTen("Nguyễn Thu Hà").email("ha@gmail.com").soDienThoai("0902223344").build());
        nhanVienRepository.save(NhanVien.builder().maNhanVien("NV001").hoTen("Nguyễn Văn Admin").tenDangNhap("superadmin").build());

        // Khởi tạo dữ liệu mẫu đơn hàng và trạng thái
        dashboardService.initSampleDashboardData();
    }

    @Test
    @DisplayName("TC01 - Tiêu chí 1: Thống kê tổng số lượng người dùng (Khách hàng + Nhân viên)")
    void testTongNguoiDung() {
        DashboardOverviewResponse overview = dashboardService.getDashboardOverview();

        assertNotNull(overview);
        assertEquals(2L, overview.getTongKhachHang());
        assertEquals(1L, overview.getTongNhanVien());
        assertEquals(3L, overview.getTongNguoiDung(), "Tổng người dùng phải bằng khách hàng (2) + nhân viên (1)");
    }

    @Test
    @DisplayName("TC02 - Tiêu chí 2: Thống kê số lượng đơn hàng hôm nay chính xác")
    void testDonHangHomNay() {
        DashboardOverviewResponse overview = dashboardService.getDashboardOverview();

        assertNotNull(overview);
        assertTrue(overview.getDonHangHomNay() >= 5L, "Số đơn hàng hôm nay mẫu phải từ 5 đơn trở lên");
        assertTrue(overview.getTongDonHang() >= overview.getDonHangHomNay(), "Tổng đơn hàng phải lớn hơn hoặc bằng đơn hôm nay");
    }

    @Test
    @DisplayName("TC03 - Tiêu chí 3: Thống kê doanh thu trong ngày và định dạng tiền tệ VND")
    void testDoanhThuHomNay() {
        DashboardOverviewResponse overview = dashboardService.getDashboardOverview();

        assertNotNull(overview);
        assertNotNull(overview.getDoanhThuHomNay());
        assertTrue(overview.getDoanhThuHomNay().compareTo(BigDecimal.ZERO) > 0, "Doanh thu hôm nay phải lớn hơn 0");
        assertNotNull(overview.getFormattedDoanhThuHomNay());
        assertTrue(overview.getFormattedDoanhThuHomNay().contains("₫"), "Định dạng doanh thu phải có ký hiệu tiền tệ ₫");
    }

    @Test
    @DisplayName("TC04 - Kiểm tra biểu đồ doanh thu 7 ngày gần nhất đầy đủ 7 cột và tỷ lệ chiều cao")
    void testBieuDoDoanhThu7Ngay() {
        DashboardOverviewResponse overview = dashboardService.getDashboardOverview();

        assertNotNull(overview.getBieuDoDoanhThu7Ngay());
        assertEquals(7, overview.getBieuDoDoanhThu7Ngay().size(), "Biểu đồ phải có đúng 7 ngày");

        // Cột cuối cùng phải là 'Hôm nay'
        assertEquals("Hôm nay", overview.getBieuDoDoanhThu7Ngay().get(6).getDayOfWeek());
        assertTrue(overview.getBieuDoDoanhThu7Ngay().get(6).getHeightPercentage() > 0);
    }

    @Test
    @DisplayName("TC05 - Kiểm tra cơ cấu trạng thái đơn hàng (Donut / Progress chart)")
    void testThongKeTrangThaiDonHang() {
        DashboardOverviewResponse overview = dashboardService.getDashboardOverview();

        assertNotNull(overview.getThongKeTrangThaiDonHang());
        assertFalse(overview.getThongKeTrangThaiDonHang().isEmpty(), "Danh sách trạng thái không được rỗng");

        boolean hasSuccessStatus = overview.getThongKeTrangThaiDonHang().stream()
                .anyMatch(st -> "HOAN_THANH".equals(st.getStatusCode()) && st.getCount() > 0);
        assertTrue(hasSuccessStatus, "Phải có trạng thái Giao hàng thành công (HOAN_THANH)");
    }

    @Test
    @DisplayName("TC06 - Kiểm tra danh sách 5 đơn hàng mới nhất hôm nay")
    void testDonHangMoiNhat() {
        DashboardOverviewResponse overview = dashboardService.getDashboardOverview();

        assertNotNull(overview.getDonHangMoiNhat());
        assertFalse(overview.getDonHangMoiNhat().isEmpty());
        assertTrue(overview.getDonHangMoiNhat().size() <= 5, "Danh sách tối đa 5 đơn hàng");
        assertNotNull(overview.getDonHangMoiNhat().get(0).getOrderCode());
        assertNotNull(overview.getDonHangMoiNhat().get(0).getFormattedAmount());
    }

    // ==========================================
    // IN-MEMORY FAKE REPOSITORIES
    // ==========================================

    public static class InMemoryTrangThaiDonHangRepository implements TrangThaiDonHangRepository {
        private final List<TrangThaiDonHang> store = new ArrayList<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public Optional<TrangThaiDonHang> findByMaTrangThai(String maTrangThai) {
            return store.stream().filter(st -> st.getMaTrangThai().equalsIgnoreCase(maTrangThai)).findFirst();
        }

        @Override
        public <S extends TrangThaiDonHang> S save(S entity) {
            if (entity.getId() == null) {
                entity.setId(idGen.getAndIncrement());
            }
            store.removeIf(s -> s.getId().equals(entity.getId()));
            store.add(entity);
            return entity;
        }

        @Override public Optional<TrangThaiDonHang> findById(Long id) { return store.stream().filter(s -> s.getId().equals(id)).findFirst(); }
        @Override public List<TrangThaiDonHang> findAll() { return new ArrayList<>(store); }
        @Override public boolean existsById(Long id) { return store.stream().anyMatch(s -> s.getId().equals(id)); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.removeIf(s -> s.getId().equals(id)); }
        @Override public void delete(TrangThaiDonHang entity) { store.remove(entity); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends TrangThaiDonHang> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<TrangThaiDonHang> findAll(Sort sort) { return findAll(); }
        @Override public Page<TrangThaiDonHang> findAll(Pageable pageable) { return null; }
        @Override public <S extends TrangThaiDonHang> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public void flush() {}
        @Override public <S extends TrangThaiDonHang> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends TrangThaiDonHang> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<TrangThaiDonHang> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}
        @Override public void deleteAllInBatch() {}
        @Override public TrangThaiDonHang getOne(Long aLong) { return null; }
        @Override public TrangThaiDonHang getById(Long aLong) { return null; }
        @Override public TrangThaiDonHang getReferenceById(Long aLong) { return null; }
        @Override public <S extends TrangThaiDonHang> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends TrangThaiDonHang> List<S> findAll(Example<S> example) { return null; }
        @Override public <S extends TrangThaiDonHang> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override public <S extends TrangThaiDonHang> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends TrangThaiDonHang> long count(Example<S> example) { return 0; }
        @Override public <S extends TrangThaiDonHang> boolean exists(Example<S> example) { return false; }
        @Override public <S extends TrangThaiDonHang, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<TrangThaiDonHang> findAllById(Iterable<Long> longs) { return null; }
    }

    public static class InMemoryDonHangRepository implements DonHangRepository {
        private final List<DonHang> store = new ArrayList<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public long countByNgayTaoBetween(LocalDateTime start, LocalDateTime end) {
            return store.stream().filter(d -> d.getNgayTao() != null && !d.getNgayTao().isBefore(start) && !d.getNgayTao().isAfter(end)).count();
        }

        @Override
        public BigDecimal sumDoanhThuBetween(LocalDateTime start, LocalDateTime end) {
            return store.stream()
                    .filter(d -> d.getNgayTao() != null && !d.getNgayTao().isBefore(start) && !d.getNgayTao().isAfter(end))
                    .map(DonHang::getTongThanhToan)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        @Override
        public BigDecimal sumTongDoanhThu() {
            return store.stream()
                    .map(DonHang::getTongThanhToan)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        @Override
        public long countByTrangThai(TrangThaiDonHang trangThai) {
            return store.stream()
                    .filter(d -> d.getTrangThai() != null && d.getTrangThai().getId().equals(trangThai.getId()))
                    .count();
        }

        @Override
        public List<DonHang> findTop5ByOrderByNgayTaoDesc() {
            return store.stream()
                    .sorted(Comparator.comparing(DonHang::getNgayTao, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(5)
                    .collect(Collectors.toList());
        }

        @Override
        public List<DonHang> findByNgayTaoBetweenOrderByNgayTaoDesc(LocalDateTime start, LocalDateTime end) {
            return store.stream()
                    .filter(d -> d.getNgayTao() != null && !d.getNgayTao().isBefore(start) && !d.getNgayTao().isAfter(end))
                    .sorted(Comparator.comparing(DonHang::getNgayTao, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }

        @Override
        public <S extends DonHang> S save(S entity) {
            if (entity.getId() == null) {
                entity.setId(idGen.getAndIncrement());
            }
            if (entity.getNgayTao() == null) {
                entity.setNgayTao(LocalDateTime.now());
            }
            store.removeIf(d -> d.getId().equals(entity.getId()));
            store.add(entity);
            return entity;
        }

        @Override public Optional<DonHang> findById(Long id) { return store.stream().filter(d -> d.getId().equals(id)).findFirst(); }
        @Override public List<DonHang> findAll() { return new ArrayList<>(store); }
        @Override public boolean existsById(Long id) { return store.stream().anyMatch(d -> d.getId().equals(id)); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.removeIf(d -> d.getId().equals(id)); }
        @Override public void delete(DonHang entity) { store.remove(entity); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) {}
        @Override public void deleteAll(Iterable<? extends DonHang> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public List<DonHang> findAll(Sort sort) { return findAll(); }
        @Override public Page<DonHang> findAll(Pageable pageable) { return null; }
        @Override public <S extends DonHang> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public void flush() {}
        @Override public <S extends DonHang> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends DonHang> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<DonHang> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}
        @Override public void deleteAllInBatch() {}
        @Override public DonHang getOne(Long aLong) { return null; }
        @Override public DonHang getById(Long aLong) { return null; }
        @Override public DonHang getReferenceById(Long aLong) { return null; }
        @Override public <S extends DonHang> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends DonHang> List<S> findAll(Example<S> example) { return null; }
        @Override public <S extends DonHang> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override public <S extends DonHang> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends DonHang> long count(Example<S> example) { return 0; }
        @Override public <S extends DonHang> boolean exists(Example<S> example) { return false; }
        @Override public <S extends DonHang, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public List<DonHang> findAllById(Iterable<Long> longs) { return null; }
    }
}
