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
        assertEquals(lanMot.getMaNcc(), lanHai.getMaNcc());
        assertTrue(lanMot.getMaNcc().matches("^NCC-XUONG-MAY-HUNG-PHAT-[A-F0-9]{6}$"),
                "Mã sinh ra phải ổn định theo tên đã chuẩn hóa: " + lanMot.getMaNcc());
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
