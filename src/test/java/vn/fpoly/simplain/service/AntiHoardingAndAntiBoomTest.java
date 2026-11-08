package vn.fpoly.simplain.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.DatHangRequest;
import vn.fpoly.simplain.dto.KiemTraCodResult;
import vn.fpoly.simplain.dto.ThemGioHangRequest;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử nghiệp vụ Chống gom hàng / đầu cơ / ôm hàng cả kho và Chống bom hàng COD.
 * - Tiêu chí 1: Giới hạn số lượng mua lẻ tối đa 5 cái / SKU, Flash Sale tối đa 2 cái / SKU.
 * - Tiêu chí 2: Trần giá trị COD tối đa 3.000.000 đ (trên 3 triệu bắt buộc thanh toán trực tuyến).
 * - Tiêu chí 3: Giới hạn tối đa 2 đơn hàng COD đang xử lý / đang giao của cùng số điện thoại/khách hàng.
 * - Tiêu chí 4: Chặn COD vĩnh viễn với số điện thoại / khách hàng có lịch sử từ 2 lần bom hàng (giao thất bại).
 */
@SpringBootTest
@DisplayName("Kiểm thử Nghiệp vụ Chống ôm hàng cả kho & Chống bom hàng COD")
class AntiHoardingAndAntiBoomTest {

    @Autowired
    private DonHangService donHangService;

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;

    private PhuongThucThanhToan ptttCod;
    private PhuongThucThanhToan ptttOnline;
    private TrangThaiDonHang ttChoXacNhan;
    private TrangThaiDonHang ttDaHuy;
    private TrangThaiDonHang ttDangGiao;
    private KhachHang khachTest;
    private BienTheSanPham bienTheGiaRe;
    private BienTheSanPham bienTheGiaCao;

    private static final String TEST_PHONE = "0988776655";

    @BeforeEach
    void setUp() {
        // 1. Tạo hoặc lấy phương thức COD & Online
        ptttCod = phuongThucThanhToanRepository.findByMaPhuongThuc("COD")
                .orElseGet(() -> phuongThucThanhToanRepository.save(PhuongThucThanhToan.builder()
                        .maPhuongThuc("COD")
                        .tenPhuongThuc("Thanh toán khi nhận hàng (COD)")
                        .laTrucTuyen(false)
                        .kichHoat(true)
                        .build()));

        ptttOnline = phuongThucThanhToanRepository.findByMaPhuongThuc("VNPAY")
                .orElseGet(() -> phuongThucThanhToanRepository.save(PhuongThucThanhToan.builder()
                        .maPhuongThuc("VNPAY")
                        .tenPhuongThuc("Cổng thanh toán VNPay")
                        .laTrucTuyen(true)
                        .kichHoat(true)
                        .build()));

        // 2. Trạng thái đơn hàng
        ttChoXacNhan = trangThaiDonHangRepository.findByMaTrangThai("CHO_XAC_NHAN")
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("CHO_XAC_NHAN")
                        .tenTrangThai("Chờ xác nhận")
                        .build()));

        ttDangGiao = trangThaiDonHangRepository.findByMaTrangThai("DANG_GIAO")
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("DANG_GIAO")
                        .tenTrangThai("Đang giao hàng")
                        .build()));

        ttDaHuy = trangThaiDonHangRepository.findByMaTrangThai("DA_HUY")
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("DA_HUY")
                        .tenTrangThai("Đã hủy")
                        .build()));

        // 3. Khách hàng test với SĐT duy nhất
        String phone = "09" + String.format("%08d", (System.nanoTime() % 100000000));
        khachTest = khachHangRepository.save(KhachHang.builder()
                .maKhachHang("KH-ANTIBOOM-" + System.currentTimeMillis() + "-" + (System.nanoTime() % 1000))
                .hoTen("Khách Hàng Anti-Fraud Test")
                .soDienThoai(phone)
                .email("antifraud_" + System.nanoTime() + "@test.com")
                .matKhauHash("dummy")
                .trangThai(1)
                .build());

        // 4. Biến thể sản phẩm test (dùng biến thể có sẵn trong DB để đảm bảo đủ ràng buộc màu/size)
        bienTheGiaRe = bienTheSanPhamRepository.findAll().stream()
                .filter(b -> Boolean.TRUE.equals(b.getKichHoat()) && b.getSoLuong() != null && b.getSoLuong() >= 10)
                .findFirst()
                .orElse(null);
    }

    @AfterEach
    void tearDown() {
        try {
            gioHangService.xoaHetGioHang(khachTest.getId(), null);
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("TC1: Chặn thêm vào giỏ quá 5 sản phẩm cho cùng một SKU lẻ (Chống gom hàng)")
    void kiemTraChanThemVaoGioQua5SanPham() {
        ThemGioHangRequest req = ThemGioHangRequest.builder()
                .maBienTheId(bienTheGiaRe.getId())
                .soLuong(6) // Vượt quá 5 cái
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            gioHangService.themBienTheVaoGio(khachTest.getId(), null, req);
        });

        assertTrue(ex.getMessage().contains("tối đa 5 sản phẩm") || ex.getMessage().contains("gom hàng"));
    }

    @Test
    @DisplayName("TC2: Chặn đặt đơn hàng COD vượt quá trần 3.000.000 đ")
    void kiemTraChanDonHangCodVuotQua3Trieu() {
        // Kiểm tra hàm kiểm tra điều kiện COD trực tiếp
        KiemTraCodResult resultCod = donHangService.kiemTraDieuKienApDungCod(
                khachTest.getId(),
                khachTest.getSoDienThoai(),
                new BigDecimal("3500000") // 3.5 triệu > 3 triệu
        );

        assertFalse(resultCod.isChoPhepCod());
        assertNotNull(resultCod.getLyDoChanCod());
        assertTrue(resultCod.getLyDoChanCod().contains("3.000.000"));

        // Với đơn dưới 3 triệu, cho phép COD bình thường
        KiemTraCodResult resultOk = donHangService.kiemTraDieuKienApDungCod(
                khachTest.getId(),
                khachTest.getSoDienThoai(),
                new BigDecimal("500000")
        );
        assertTrue(resultOk.isChoPhepCod());
    }

    @Test
    @DisplayName("TC3: Chặn đơn COD khi khách hàng/SĐT đang có từ 2 đơn COD đang xử lý / đang giao")
    void kiemTraChanKhachCoTu2DonCodDangGiao() {
        // Tạo 2 đơn COD đang chờ giao cho khách hàng này
        for (int i = 1; i <= 2; i++) {
            donHangRepository.save(DonHang.builder()
                    .maDonHang("DH-ACTIVE-COD-" + i + "-" + System.currentTimeMillis())
                    .khachHang(khachTest)
                    .tenNguoiNhan(khachTest.getHoTen())
                    .soDienThoaiNhan(khachTest.getSoDienThoai())
                    .diaChiGiaoHang("Địa chỉ test")
                    .phuongThucThanhToan(ptttCod)
                    .trangThaiDonHang(ttChoXacNhan)
                    .tongTienHang(new BigDecimal("200000"))
                    .tongThanhToan(new BigDecimal("200000"))
                    .build());
        }

        KiemTraCodResult result = donHangService.kiemTraDieuKienApDungCod(
                khachTest.getId(),
                khachTest.getSoDienThoai(),
                new BigDecimal("200000")
        );

        assertFalse(result.isChoPhepCod());
        assertTrue(result.getLyDoChanCod().contains("đơn hàng COD chưa hoàn tất"));
    }

    @Test
    @DisplayName("TC4: Chặn COD vĩnh viễn với khách hàng/SĐT có lịch sử từ 2 lần bom hàng (giao thất bại)")
    void kiemTraChanKhachTungBomHang2Lan() {
        // Tạo 2 đơn COD từng bị hủy kèm ghi chú bom hàng
        for (int i = 1; i <= 2; i++) {
            DonHang dhBom = donHangRepository.save(DonHang.builder()
                    .maDonHang("DH-BOM-" + i + "-" + System.currentTimeMillis())
                    .khachHang(khachTest)
                    .tenNguoiNhan(khachTest.getHoTen())
                    .soDienThoaiNhan(khachTest.getSoDienThoai())
                    .diaChiGiaoHang("Địa chỉ test")
                    .phuongThucThanhToan(ptttCod)
                    .trangThaiDonHang(ttDaHuy)
                    .ghiChu("Khách từ chối nhận hàng - Bom hàng")
                    .tongTienHang(new BigDecimal("300000"))
                    .tongThanhToan(new BigDecimal("300000"))
                    .build());

            // Ghi log lịch sử từng ở trạng thái Đang giao hàng
            lichSuTrangThaiDonHangRepository.save(LichSuTrangThaiDonHang.builder()
                    .donHang(dhBom)
                    .trangThaiMoi(ttDangGiao)
                    .ghiChu("Shipper xuất kho giao hàng")
                    .ngayChuyen(LocalDateTime.now().minusDays(1))
                    .build());
        }

        KiemTraCodResult result = donHangService.kiemTraDieuKienApDungCod(
                khachTest.getId(),
                khachTest.getSoDienThoai(),
                new BigDecimal("150000")
        );

        assertFalse(result.isChoPhepCod());
        assertTrue(result.getLyDoChanCod().contains("giao hàng thất bại") || result.getLyDoChanCod().contains("từ chối nhận"));
    }
}
