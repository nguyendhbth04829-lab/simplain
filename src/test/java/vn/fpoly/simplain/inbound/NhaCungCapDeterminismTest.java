package vn.fpoly.simplain.inbound;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Nhà cung cấp - Chuẩn hóa đầu vào deterministic")
class NhaCungCapDeterminismTest {

    @Mock
    private NhaCungCapRepository nhaCungCapRepository;

    @Mock
    private PhieuNhapKhoRepository phieuNhapKhoRepository;

    @Mock
    private NhatKyKhoRepository nhatKyKhoRepository;

    @Mock
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Mock
    private LichSuHopTacService lichSuHopTacService;

    @InjectMocks
    private InboundService inboundService;

    @Test
    @DisplayName("Cùng một tên xưởng luôn sinh ra cùng một mã, bất kể khoảng trắng thừa")
    void cungMotTenLuonSinhCungMotMa() {
        when(nhaCungCapRepository.existsByMaNccIgnoreCase(anyString())).thenReturn(false);
        when(nhaCungCapRepository.existsByTenNccIgnoreCase(anyString())).thenReturn(false);
        when(nhaCungCapRepository.save(any(NhaCungCap.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NhaCungCap lanMot = inboundService.taoNhaCungCap(NhaCungCapRequest.builder()
                .tenNcc("  Xưởng   May Hưng Phát  ")
                .build());
        NhaCungCap lanHai = inboundService.taoNhaCungCap(NhaCungCapRequest.builder()
                .tenNcc("Xưởng May Hưng Phát")
                .build());

        assertEquals("Xưởng May Hưng Phát", lanMot.getTenNcc());
        assertEquals(TrangThaiNhaCungCap.TIEM_NANG, lanMot.getTrangThaiHopTac());
        assertEquals(lanMot.getMaNcc(), lanHai.getMaNcc());
        assertTrue(lanMot.getMaNcc().matches("^NCC-XUONG-MAY-HUNG-PHAT-[A-F0-9]{6}$"),
                "Mã sinh ra phải ổn định theo tên đã chuẩn hóa: " + lanMot.getMaNcc());
        verify(lichSuHopTacService, times(2)).ghiNhanTrangThai(any(NhaCungCap.class), isNull(),
                eq(TrangThaiNhaCungCap.TIEM_NANG), isNull(), isNull(), eq("Khởi tạo hồ sơ nhà cung cấp."));
    }

    @Test
    @DisplayName("Chuẩn hóa đồng thời điện thoại, email, địa chỉ và ghi chú")
    void chuanHoaDongThoiCacTruongLienHe() {
        when(nhaCungCapRepository.existsByMaNccIgnoreCase(anyString())).thenReturn(false);
        when(nhaCungCapRepository.existsByTenNccIgnoreCase(anyString())).thenReturn(false);
        when(nhaCungCapRepository.save(any(NhaCungCap.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NhaCungCap ncc = inboundService.taoNhaCungCap(NhaCungCapRequest.builder()
                .tenNcc("Xưởng May A")
                .soDienThoai(" 0912 345 678 ")
                .email("  XUONG@EXAMPLE.COM ")
                .diaChi("  123   Đường Lê Lợi  ")
                .ghiChu("   ")
                .build());

        assertEquals("0912345678", ncc.getSoDienThoai());
        assertEquals("xuong@example.com", ncc.getEmail());
        assertEquals("123 Đường Lê Lợi", ncc.getDiaChi());
        assertNull(ncc.getGhiChu());
    }

    @Test
    @DisplayName("NCC mới luôn bắt đầu ở Tiềm năng và vẫn trả trường kichHoat cũ")
    void tuongThichTruongKichHoatCu() {
        when(nhaCungCapRepository.existsByMaNccIgnoreCase(anyString())).thenReturn(false);
        when(nhaCungCapRepository.existsByTenNccIgnoreCase(anyString())).thenReturn(false);
        when(nhaCungCapRepository.save(any(NhaCungCap.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NhaCungCap ncc = inboundService.taoNhaCungCap(NhaCungCapRequest.builder()
                .tenNcc("Xưởng May Cũ")
                .kichHoat(false)
                .build());

        assertEquals(TrangThaiNhaCungCap.TIEM_NANG, ncc.getTrangThaiHopTac());
        assertFalse(ncc.getKichHoat());
    }

    @Test
    @DisplayName("Không cho tạo NCC bỏ qua bước Tiềm năng")
    void tuChoiTaoNhaCungCapKhongPhaiTiemNang() {
        assertThrows(IllegalArgumentException.class, () -> inboundService.taoNhaCungCap(NhaCungCapRequest.builder()
                .tenNcc("Xưởng May Đang Hợp Tác")
                .trangThaiHopTac(TrangThaiNhaCungCap.DANG_HOP_TAC)
                .build()));
        verify(nhaCungCapRepository, never()).save(any(NhaCungCap.class));
    }

    @Test
    @DisplayName("Cập nhật trạng thái hợp tác theo enum đã chọn")
    void capNhatTrangThaiHopTac() {
        NhaCungCap ncc = NhaCungCap.builder()
                .id(7L)
                .maNcc("NCC-07")
                .tenNcc("Xưởng May G")
                .trangThaiHopTac(TrangThaiNhaCungCap.DANG_THOA_THUAN)
                .build();
        when(nhaCungCapRepository.findById(7L)).thenReturn(Optional.of(ncc));
        when(nhaCungCapRepository.save(any(NhaCungCap.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NhaCungCap capNhat = inboundService.capNhatNhaCungCap(7L, NhaCungCapRequest.builder()
                .trangThaiHopTac(TrangThaiNhaCungCap.DANG_HOP_TAC)
                .build());

        assertEquals(TrangThaiNhaCungCap.DANG_HOP_TAC, capNhat.getTrangThaiHopTac());
        verify(lichSuHopTacService).ghiNhanTrangThai(ncc, TrangThaiNhaCungCap.DANG_THOA_THUAN,
                TrangThaiNhaCungCap.DANG_HOP_TAC, null, null, null);
    }

    @Test
    @DisplayName("Không cho nhảy từ Tiềm năng thẳng sang Đang hợp tác")
    void tuChoiBoQuaBuocThoaThuan() {
        NhaCungCap ncc = NhaCungCap.builder()
                .id(8L)
                .maNcc("NCC-08")
                .tenNcc("Xưởng May H")
                .trangThaiHopTac(TrangThaiNhaCungCap.TIEM_NANG)
                .build();
        when(nhaCungCapRepository.findById(8L)).thenReturn(Optional.of(ncc));

        assertThrows(IllegalStateException.class, () -> inboundService.capNhatNhaCungCap(8L,
                NhaCungCapRequest.builder().trangThaiHopTac(TrangThaiNhaCungCap.DANG_HOP_TAC).build()));
        verify(nhaCungCapRepository, never()).save(any(NhaCungCap.class));
    }

    @Test
    @DisplayName("Nút chuyển trạng thái đưa Tiềm năng sang Đang thỏa thuận")
    void batDauThoaThuanChoNhaCungCapTiemNang() {
        NhaCungCap ncc = NhaCungCap.builder()
                .id(9L)
                .maNcc("NCC-09")
                .tenNcc("Xưởng May I")
                .trangThaiHopTac(TrangThaiNhaCungCap.TIEM_NANG)
                .build();
        when(nhaCungCapRepository.findById(9L)).thenReturn(Optional.of(ncc));
        when(nhaCungCapRepository.save(any(NhaCungCap.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NhaCungCap capNhat = inboundService.toggleTrangThaiNcc(9L);

        assertEquals(TrangThaiNhaCungCap.DANG_THOA_THUAN, capNhat.getTrangThaiHopTac());
        verify(lichSuHopTacService).ghiNhanTrangThai(ncc, TrangThaiNhaCungCap.TIEM_NANG,
                TrangThaiNhaCungCap.DANG_THOA_THUAN, null, null, null);
    }

    @Test
    @DisplayName("Quy trình trạng thái chỉ cho phép các bước hợp tác đã thống nhất")
    void kiemTraCacBuocChuyenTrangThaiHopTac() {
        assertTrue(TrangThaiNhaCungCap.TIEM_NANG.choPhepChuyenSang(TrangThaiNhaCungCap.DANG_THOA_THUAN));
        assertTrue(TrangThaiNhaCungCap.DANG_THOA_THUAN.choPhepChuyenSang(TrangThaiNhaCungCap.DANG_HOP_TAC));
        assertTrue(TrangThaiNhaCungCap.DANG_THOA_THUAN.choPhepChuyenSang(TrangThaiNhaCungCap.THOA_THUAN_THAT_BAI));
        assertTrue(TrangThaiNhaCungCap.THOA_THUAN_THAT_BAI.choPhepChuyenSang(TrangThaiNhaCungCap.DANG_THOA_THUAN));
        assertTrue(TrangThaiNhaCungCap.DANG_HOP_TAC.choPhepChuyenSang(TrangThaiNhaCungCap.TAM_NGUNG));
        assertTrue(TrangThaiNhaCungCap.TAM_NGUNG.choPhepChuyenSang(TrangThaiNhaCungCap.DANG_HOP_TAC));
        assertFalse(TrangThaiNhaCungCap.TIEM_NANG.choPhepChuyenSang(TrangThaiNhaCungCap.DANG_HOP_TAC));
    }

    @Test
    @DisplayName("Từ chối nhà cung cấp trùng tên trước khi ghi dữ liệu")
    void tuChoiNhaCungCapTrungTen() {
        when(nhaCungCapRepository.existsByMaNccIgnoreCase(anyString())).thenReturn(false);
        when(nhaCungCapRepository.existsByTenNccIgnoreCase("Xưởng May A")).thenReturn(true);

        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> inboundService.taoNhaCungCap(NhaCungCapRequest.builder()
                        .tenNcc("  Xưởng   May A ")
                        .build()));

        assertTrue(loi.getMessage().contains("đã tồn tại"));
        verify(nhaCungCapRepository, never()).save(any(NhaCungCap.class));
    }

    @Test
    @DisplayName("Từ chối mã nhà cung cấp sai định dạng thay vì tự ý sửa dữ liệu")
    void tuChoiMaNhaCungCapSaiDinhDang() {
        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> inboundService.taoNhaCungCap(NhaCungCapRequest.builder()
                        .tenNcc("Xưởng May A")
                        .maNcc("NCC CÓ DẤU CÁCH")
                        .build()));

        assertTrue(loi.getMessage().contains("Mã nhà cung cấp"));
        verify(nhaCungCapRepository, never()).save(any(NhaCungCap.class));
    }

    @Test
    @DisplayName("Danh sách nhà cung cấp luôn được sắp xếp theo mã thay vì id tự tăng")
    void danhSachNhaCungCapSapXepTheoMa() {
        when(nhaCungCapRepository.findAll(Sort.by(Sort.Direction.ASC, "maNcc"))).thenReturn(List.of());

        inboundService.layTatCaNhaCungCap();

        verify(nhaCungCapRepository).findAll(Sort.by(Sort.Direction.ASC, "maNcc"));
    }
}
