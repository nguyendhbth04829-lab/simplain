package vn.fpoly.simplain.inbound;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LichSuHopTacServiceTest {

    @Mock
    private NhaCungCapRepository nhaCungCapRepository;

    @Mock
    private LichSuHopTacNhaCungCapRepository lichSuRepository;

    @InjectMocks
    private LichSuHopTacService lichSuHopTacService;

    @Test
    void luuLichSuKhiDoiTrangThaiVaGanNhanVien() {
        NhaCungCap ncc = NhaCungCap.builder().id(3L).maNcc("NCC-03").tenNcc("Xưởng May A").build();
        when(lichSuRepository.save(any(LichSuHopTacNhaCungCap.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        lichSuHopTacService.ghiNhanTrangThai(ncc, TrangThaiNhaCungCap.TIEM_NANG,
                TrangThaiNhaCungCap.DANG_THOA_THUAN, 7L, "Nhân viên A", null);

        ArgumentCaptor<LichSuHopTacNhaCungCap> captor = ArgumentCaptor.forClass(LichSuHopTacNhaCungCap.class);
        verify(lichSuRepository).save(captor.capture());
        assertEquals(LoaiHoatDongHopTac.THAY_DOI_TRANG_THAI, captor.getValue().getLoaiHoatDong());
        assertEquals(TrangThaiNhaCungCap.TIEM_NANG, captor.getValue().getTrangThaiTruoc());
        assertEquals(TrangThaiNhaCungCap.DANG_THOA_THUAN, captor.getValue().getTrangThaiMoi());
        assertEquals(7L, captor.getValue().getMaNhanVienId());
        assertEquals("Nhân viên A", captor.getValue().getTenNhanVien());
    }

    @Test
    void khongChapNhanHoatDongThuCongKhongCoGhiChuVaKetQua() {
        LichSuHopTacRequest request = LichSuHopTacRequest.builder()
                .loaiHoatDong(LoaiHoatDongHopTac.GOI_DIEN)
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> lichSuHopTacService.themHoatDong(1L, request, null, null));
        verifyNoInteractions(nhaCungCapRepository, lichSuRepository);
    }

    @Test
    void tuChoiKetQuaQua100KyTuTruocKhiGhiDatabase() {
        LichSuHopTacRequest request = LichSuHopTacRequest.builder()
                .loaiHoatDong(LoaiHoatDongHopTac.GOI_DIEN)
                .ketQua("A".repeat(101))
                .build();

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> lichSuHopTacService.themHoatDong(1L, request, null, null));

        assertEquals("Kết quả không được vượt quá 100 ký tự", error.getMessage());
        verifyNoInteractions(nhaCungCapRepository, lichSuRepository);
    }

    @Test
    void ghiNhanHoatDongVaGanNhaCungCap() {
        NhaCungCap ncc = NhaCungCap.builder().id(4L).maNcc("NCC-04").tenNcc("Xưởng May B").build();
        when(nhaCungCapRepository.findById(4L)).thenReturn(Optional.of(ncc));
        when(lichSuRepository.save(any(LichSuHopTacNhaCungCap.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        lichSuHopTacService.themHoatDong(4L, LichSuHopTacRequest.builder()
                .loaiHoatDong(LoaiHoatDongHopTac.GOI_DIEN)
                .ghiChu("Đã trao đổi về mẫu vải")
                .ketQua("Hẹn gửi mẫu vào thứ Sáu")
                .build(), null, null);

        ArgumentCaptor<LichSuHopTacNhaCungCap> captor = ArgumentCaptor.forClass(LichSuHopTacNhaCungCap.class);
        verify(lichSuRepository).save(captor.capture());
        assertEquals(ncc, captor.getValue().getNhaCungCap());
        assertEquals(LoaiHoatDongHopTac.GOI_DIEN, captor.getValue().getLoaiHoatDong());
        assertEquals("Đã trao đổi về mẫu vải", captor.getValue().getGhiChu());
        assertEquals("Hẹn gửi mẫu vào thứ Sáu", captor.getValue().getKetQua());
    }
}
