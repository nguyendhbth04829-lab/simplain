/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - Kiểm thử đơn vị tầng nghiệp vụ HoiVienLoyaltyService
 */
package vn.fpoly.simplain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.fpoly.simplain.dto.BienDongDiemResponse;
import vn.fpoly.simplain.dto.HoSoHoiVienResponse;
import vn.fpoly.simplain.dto.SuDungDiemRequest;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử nghiệp vụ HoiVienLoyaltyService (US16)")
class HoiVienLoyaltyServiceTest {

    @Mock
    private HoSoHoiVienRepository hoSoHoiVienRepository;

    @Mock
    private HangThanhVienRepository hangThanhVienRepository;

    @Mock
    private BienDongDiemRepository bienDongDiemRepository;

    @Mock
    private DonHangRepository donHangRepository;

    @Mock
    private KhachHangService khachHangService;

    @InjectMocks
    private HoiVienLoyaltyService hoiVienLoyaltyService;

    private KhachHang khachHangMau;
    private HoSoHoiVien hoSoMau;
    private HangThanhVien hangBac;
    private HangThanhVien hangVang;
    private DonHang donHangMau;

    @BeforeEach
    void khoiTao() {
        khachHangMau = new KhachHang();
        khachHangMau.setId(1L);
        khachHangMau.setMaKhachHang("KH001");
        khachHangMau.setHoTen("Nguyễn Văn A");
        khachHangMau.setTrangThai(1);

        hangBac = HangThanhVien.builder()
                .id(2L)
                .tenHang("Thành viên Bạc")
                .chiTieuNamToiThieu(new BigDecimal("2000000"))
                .phanTramHoanTien(new BigDecimal("3.00"))
                .build();

        hangVang = HangThanhVien.builder()
                .id(3L)
                .tenHang("Thành viên Vàng")
                .chiTieuNamToiThieu(new BigDecimal("5000000"))
                .phanTramHoanTien(new BigDecimal("5.00"))
                .build();

        hoSoMau = HoSoHoiVien.builder()
                .maKhachHangId(1L)
                .maHangHienTaiId(2L)
                .soDuCoolcash(new BigDecimal("50000"))
                .tongChiTieuNam(new BigDecimal("2500000"))
                .build();

        donHangMau = DonHang.builder()
                .id(100L)
                .maDonHang("DH001")
                .maKhachHangId(1L)
                .tongThanhToan(new BigDecimal("1000000"))
                .build();
    }

    @Test
    @DisplayName("Lấy hồ sơ hội viên: Thành công và tính toán đúng tiến trình lên hạng tiếp theo")
    void layHoSoHoiVien_ThanhCong() {
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(hoSoHoiVienRepository.findById(1L)).thenReturn(Optional.of(hoSoMau));
        when(hangThanhVienRepository.findById(2L)).thenReturn(Optional.of(hangBac));
        when(hangThanhVienRepository.findFirstByChiTieuNamToiThieuGreaterThanOrderByChiTieuNamToiThieuAsc(new BigDecimal("2500000")))
                .thenReturn(Optional.of(hangVang));

        HoSoHoiVienResponse res = hoiVienLoyaltyService.layHoSoHoiVien(1L);

        assertNotNull(res);
        assertEquals("Thành viên Bạc", res.tenHangHienTai());
        assertEquals("Thành viên Vàng", res.tenHangTiepTheo());
        assertEquals(new BigDecimal("50000"), res.soDuCoolcash());
        // Cần thêm = 5,000,000 - 2,500,000 = 2,500,000
        assertEquals(new BigDecimal("2500000"), res.chiTieuCanThemDeLenHang());
    }

    @Test
    @DisplayName("Tích điểm đơn hàng hoàn tất: Tính đúng 3% hoàn tiền và cập nhật số dư")
    void tichDiemDonHangHoanThanh_ThanhCong() {
        when(donHangRepository.findById(100L)).thenReturn(Optional.of(donHangMau));
        when(bienDongDiemRepository.existsByMaDonHangIdAndLoaiBienDong(100L, "CONG_DIEM")).thenReturn(false);
        when(hoSoHoiVienRepository.findById(1L)).thenReturn(Optional.of(hoSoMau));
        when(hangThanhVienRepository.findById(2L)).thenReturn(Optional.of(hangBac));
        when(hangThanhVienRepository.findTopByChiTieuNamToiThieuLessThanEqualOrderByChiTieuNamToiThieuDesc(any()))
                .thenReturn(Optional.of(hangBac));
        when(bienDongDiemRepository.save(any(BienDongDiem.class))).thenAnswer(invocation -> {
            BienDongDiem bd = invocation.getArgument(0);
            bd.setId(10L);
            return bd;
        });

        BienDongDiemResponse res = hoiVienLoyaltyService.tichDiemDonHangHoanThanh(100L);

        assertNotNull(res);
        assertEquals("CONG_DIEM", res.loaiBienDong());
        // 1,000,000 * 3% = 30,000 điểm
        assertEquals(new BigDecimal("30000"), res.soDiem());
        // Số dư mới = 50,000 + 30,000 = 80,000
        assertEquals(new BigDecimal("80000"), hoSoMau.getSoDuCoolcash());
        // Chi tiêu mới = 2,500,000 + 1,000,000 = 3,500,000
        assertEquals(new BigDecimal("3500000"), hoSoMau.getTongChiTieuNam());
        verify(hoSoHoiVienRepository).save(hoSoMau);
    }

    @Test
    @DisplayName("Tích điểm đơn hàng hoàn tất: Tự động thăng hạng khi vượt mốc chi tiêu")
    void tichDiemDonHangHoanThanh_TuDongThangHang() {
        // Đơn hàng lớn 3,000,000 -> Tổng chi tiêu 2.5tr + 3tr = 5.5tr -> Đủ lên Hạng Vàng (>= 5tr)
        donHangMau.setTongThanhToan(new BigDecimal("3000000"));

        when(donHangRepository.findById(100L)).thenReturn(Optional.of(donHangMau));
        when(bienDongDiemRepository.existsByMaDonHangIdAndLoaiBienDong(100L, "CONG_DIEM")).thenReturn(false);
        when(hoSoHoiVienRepository.findById(1L)).thenReturn(Optional.of(hoSoMau));
        when(hangThanhVienRepository.findById(2L)).thenReturn(Optional.of(hangBac));
        when(hangThanhVienRepository.findTopByChiTieuNamToiThieuLessThanEqualOrderByChiTieuNamToiThieuDesc(new BigDecimal("5500000")))
                .thenReturn(Optional.of(hangVang));
        when(bienDongDiemRepository.save(any(BienDongDiem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        hoiVienLoyaltyService.tichDiemDonHangHoanThanh(100L);

        assertEquals(3L, hoSoMau.getMaHangHienTaiId(), "Phải tự động thăng hạng lên Hạng Vàng (ID = 3)");
        verify(hoSoHoiVienRepository).save(hoSoMau);
    }

    @Test
    @DisplayName("Tích điểm đơn hàng hoàn tất: Idempotent - Nếu đã tích điểm cho đơn hàng này rồi thì bỏ qua")
    void tichDiemDonHangHoanThanh_DaTichDiem_BoQua() {
        when(donHangRepository.findById(100L)).thenReturn(Optional.of(donHangMau));
        when(bienDongDiemRepository.existsByMaDonHangIdAndLoaiBienDong(100L, "CONG_DIEM")).thenReturn(true);

        BienDongDiemResponse res = hoiVienLoyaltyService.tichDiemDonHangHoanThanh(100L);

        assertNull(res);
        verify(hoSoHoiVienRepository, never()).save(any());
        verify(bienDongDiemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Sử dụng điểm: Trừ điểm thành công khi số dư hợp lệ")
    void suDungDiemCoolcash_ThanhCong() {
        SuDungDiemRequest req = new SuDungDiemRequest(new BigDecimal("20000"), 100L);

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(hoSoHoiVienRepository.findById(1L)).thenReturn(Optional.of(hoSoMau));
        when(bienDongDiemRepository.save(any(BienDongDiem.class))).thenAnswer(invocation -> {
            BienDongDiem bd = invocation.getArgument(0);
            bd.setId(11L);
            return bd;
        });

        BienDongDiemResponse res = hoiVienLoyaltyService.suDungDiemCoolcash(1L, req);

        assertNotNull(res);
        assertEquals("TRU_DIEM", res.loaiBienDong());
        assertEquals(new BigDecimal("20000"), res.soDiem());
        // Số dư còn lại: 50,000 - 20,000 = 30,000
        assertEquals(new BigDecimal("30000"), hoSoMau.getSoDuCoolcash());
        verify(hoSoHoiVienRepository).save(hoSoMau);
    }

    @Test
    @DisplayName("Sử dụng điểm: Báo lỗi khi số dư không đủ")
    void suDungDiemCoolcash_KhongDuSoDu_NemNgoaiLe() {
        // Yêu cầu trừ 100,000 nhưng số dư chỉ có 50,000
        SuDungDiemRequest req = new SuDungDiemRequest(new BigDecimal("100000"), 100L);

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(hoSoHoiVienRepository.findById(1L)).thenReturn(Optional.of(hoSoMau));

        DuLieuKhongHopLeException ex = assertThrows(DuLieuKhongHopLeException.class,
                () -> hoiVienLoyaltyService.suDungDiemCoolcash(1L, req));

        assertTrue(ex.getMessage().contains("không đủ"));
        verify(hoSoHoiVienRepository, never()).save(any());
    }
}
