package vn.fpoly.simplain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.fpoly.simplain.dto.CapNhatHoSoRequest;
import vn.fpoly.simplain.dto.DoiMatKhauRequest;
import vn.fpoly.simplain.dto.HoSoKhachHangResponse;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.exception.ChuaDangNhapException;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.exception.TaiKhoanBiKhoaException;
import vn.fpoly.simplain.exception.XungDotDuLieuException;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.util.MaHoaMatKhau;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử nghiệp vụ KhachHangService (US12)")
class KhachHangServiceTest {

    @Mock
    private KhachHangRepository khachHangRepository;

    @Mock
    private MaHoaMatKhau maHoaMatKhau;

    @InjectMocks
    private KhachHangService khachHangService;

    private KhachHang khachHangMau;

    @BeforeEach
    void khoiTao() {
        khachHangMau = KhachHang.builder()
                .id(1L)
                .maKhachHang("KH0001")
                .hoTen("Nguyễn Văn A")
                .soDienThoai("0987654321")
                .email("nguyenvana@gmail.com")
                .matKhauHash("chuoi_bam_mat_khau_cu")
                .anhDaiDien("https://example.com/avatar.jpg")
                .gioiTinh("Nam")
                .ngaySinh(LocalDate.of(1995, 8, 15))
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build();
    }

    // -------------------------------------------------------------
    // Kiểm thử layHoSo()
    // -------------------------------------------------------------

    @Test
    @DisplayName("layHoSo() thành công khi ID hợp lệ và tài khoản đang hoạt động")
    void layHoSo_ThanhCong() {
        when(khachHangRepository.findById(1L)).thenReturn(Optional.of(khachHangMau));

        HoSoKhachHangResponse phanHoi = khachHangService.layHoSo(1L);

        assertNotNull(phanHoi);
        assertEquals(1L, phanHoi.id());
        assertEquals("KH0001", phanHoi.maKhachHang());
        assertEquals("Nguyễn Văn A", phanHoi.hoTen());
        assertEquals("0987654321", phanHoi.soDienThoai());
        assertEquals("nguyenvana@gmail.com", phanHoi.email());
        assertEquals("Nam", phanHoi.gioiTinh());
    }

    @Test
    @DisplayName("layHoSo() ném ChuaDangNhapException khi idKhachHang null hoặc <= 0")
    void layHoSo_ChuaDangNhap_NemNgoaiLe() {
        assertThrows(ChuaDangNhapException.class, () -> khachHangService.layHoSo(null));
        assertThrows(ChuaDangNhapException.class, () -> khachHangService.layHoSo(0L));
        assertThrows(ChuaDangNhapException.class, () -> khachHangService.layHoSo(-1L));
    }

    @Test
    @DisplayName("layHoSo() ném KhongTimThayException khi không tìm thấy khách hàng")
    void layHoSo_KhongTimThay_NemNgoaiLe() {
        when(khachHangRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(KhongTimThayException.class, () -> khachHangService.layHoSo(99L));
    }

    @Test
    @DisplayName("layHoSo() ném TaiKhoanBiKhoaException khi tài khoản bị khóa (trangThai = 0)")
    void layHoSo_TaiKhoanBiKhoa_NemNgoaiLe() {
        khachHangMau.setTrangThai(0);
        when(khachHangRepository.findById(1L)).thenReturn(Optional.of(khachHangMau));

        assertThrows(TaiKhoanBiKhoaException.class, () -> khachHangService.layHoSo(1L));
    }

    // -------------------------------------------------------------
    // Kiểm thử capNhatHoSo()
    // -------------------------------------------------------------

    @Test
    @DisplayName("capNhatHoSo() thành công với đầy đủ các trường hợp lệ")
    void capNhatHoSo_ThanhCong() {
        CapNhatHoSoRequest yeuCau = new CapNhatHoSoRequest(
                "Nguyễn Văn B",
                "0912345678",
                "nguyenvanb@gmail.com",
                "Nam",
                LocalDate.of(1996, 1, 1),
                "https://example.com/avatar_moi.jpg"
        );

        when(khachHangRepository.findById(1L)).thenReturn(Optional.of(khachHangMau));
        when(khachHangRepository.existsByEmailAndIdNot("nguyenvanb@gmail.com", 1L)).thenReturn(false);
        when(khachHangRepository.existsBySoDienThoaiAndIdNot("0912345678", 1L)).thenReturn(false);
        when(khachHangRepository.save(any(KhachHang.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HoSoKhachHangResponse phanHoi = khachHangService.capNhatHoSo(1L, yeuCau);

        assertNotNull(phanHoi);
        assertEquals("Nguyễn Văn B", phanHoi.hoTen());
        assertEquals("0912345678", phanHoi.soDienThoai());
        assertEquals("nguyenvanb@gmail.com", phanHoi.email());
        assertEquals("https://example.com/avatar_moi.jpg", phanHoi.anhDaiDien());
        verify(khachHangRepository, times(1)).save(khachHangMau);
    }

    @Test
    @DisplayName("capNhatHoSo() ném XungDotDuLieuException khi email mới đã bị người khác dùng")
    void capNhatHoSo_TrungEmail_NemNgoaiLe() {
        CapNhatHoSoRequest yeuCau = new CapNhatHoSoRequest(
                "Nguyễn Văn B",
                "0987654321",
                "trunglap@gmail.com",
                "Nam",
                null,
                null
        );

        when(khachHangRepository.findById(1L)).thenReturn(Optional.of(khachHangMau));
        when(khachHangRepository.existsByEmailAndIdNot("trunglap@gmail.com", 1L)).thenReturn(true);

        XungDotDuLieuException ex = assertThrows(XungDotDuLieuException.class, () -> khachHangService.capNhatHoSo(1L, yeuCau));
        assertTrue(ex.getMessage().contains("Email 'trunglap@gmail.com' đã được sử dụng"));
        verify(khachHangRepository, never()).save(any());
    }

    @Test
    @DisplayName("capNhatHoSo() ném XungDotDuLieuException khi SĐT mới đã bị người khác dùng")
    void capNhatHoSo_TrungSoDienThoai_NemNgoaiLe() {
        CapNhatHoSoRequest yeuCau = new CapNhatHoSoRequest(
                "Nguyễn Văn B",
                "0909999888",
                "nguyenvana@gmail.com",
                "Nam",
                null,
                null
        );

        when(khachHangRepository.findById(1L)).thenReturn(Optional.of(khachHangMau));
        when(khachHangRepository.existsByEmailAndIdNot("nguyenvana@gmail.com", 1L)).thenReturn(false);
        when(khachHangRepository.existsBySoDienThoaiAndIdNot("0909999888", 1L)).thenReturn(true);

        XungDotDuLieuException ex = assertThrows(XungDotDuLieuException.class, () -> khachHangService.capNhatHoSo(1L, yeuCau));
        assertTrue(ex.getMessage().contains("Số điện thoại '0909999888' đã được sử dụng"));
        verify(khachHangRepository, never()).save(any());
    }

    // -------------------------------------------------------------
    // Kiểm thử doiMatKhau()
    // -------------------------------------------------------------

    @Test
    @DisplayName("doiMatKhau() thành công khi mật khẩu cũ đúng và mật khẩu mới hợp lệ")
    void doiMatKhau_ThanhCong() {
        DoiMatKhauRequest yeuCau = new DoiMatKhauRequest(
                "MatKhauCu123@",
                "MatKhauMoi456@",
                "MatKhauMoi456@"
        );

        when(khachHangRepository.findById(1L)).thenReturn(Optional.of(khachHangMau));
        when(maHoaMatKhau.kiemTra("MatKhauCu123@", "chuoi_bam_mat_khau_cu")).thenReturn(true);
        when(maHoaMatKhau.maHoa("MatKhauMoi456@")).thenReturn("chuoi_bam_moi");
        when(khachHangRepository.save(any(KhachHang.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ThongBaoResponse phanHoi = khachHangService.doiMatKhau(1L, yeuCau);

        assertNotNull(phanHoi);
        assertEquals("Đổi mật khẩu thành công", phanHoi.thongDiep());
        assertEquals("chuoi_bam_moi", khachHangMau.getMatKhauHash());
        verify(khachHangRepository, times(1)).save(khachHangMau);
    }

    @Test
    @DisplayName("doiMatKhau() ném DuLieuKhongHopLeException khi xác nhận mật khẩu mới không khớp")
    void doiMatKhau_KhongKhopXacNhan_NemNgoaiLe() {
        DoiMatKhauRequest yeuCau = new DoiMatKhauRequest(
                "MatKhauCu123@",
                "MatKhauMoi456@",
                "KhacBiet789@"
        );

        when(khachHangRepository.findById(1L)).thenReturn(Optional.of(khachHangMau));

        DuLieuKhongHopLeException ex = assertThrows(DuLieuKhongHopLeException.class, () -> khachHangService.doiMatKhau(1L, yeuCau));
        assertEquals("Xác nhận mật khẩu mới không trùng khớp", ex.getMessage());
        verify(khachHangRepository, never()).save(any());
    }

    @Test
    @DisplayName("doiMatKhau() ném DuLieuKhongHopLeException khi mật khẩu cũ không đúng")
    void doiMatKhau_SaiMatKhauCu_NemNgoaiLe() {
        DoiMatKhauRequest yeuCau = new DoiMatKhauRequest(
                "MatKhauSai!",
                "MatKhauMoi456@",
                "MatKhauMoi456@"
        );

        when(khachHangRepository.findById(1L)).thenReturn(Optional.of(khachHangMau));
        when(maHoaMatKhau.kiemTra("MatKhauSai!", "chuoi_bam_mat_khau_cu")).thenReturn(false);

        DuLieuKhongHopLeException ex = assertThrows(DuLieuKhongHopLeException.class, () -> khachHangService.doiMatKhau(1L, yeuCau));
        assertEquals("Mật khẩu hiện tại không chính xác", ex.getMessage());
        verify(khachHangRepository, never()).save(any());
    }

    @Test
    @DisplayName("doiMatKhau() ném DuLieuKhongHopLeException khi mật khẩu mới trùng với mật khẩu cũ")
    void doiMatKhau_TrungMatKhauCu_NemNgoaiLe() {
        DoiMatKhauRequest yeuCau = new DoiMatKhauRequest(
                "MatKhauCu123@",
                "MatKhauCu123@",
                "MatKhauCu123@"
        );

        when(khachHangRepository.findById(1L)).thenReturn(Optional.of(khachHangMau));
        when(maHoaMatKhau.kiemTra("MatKhauCu123@", "chuoi_bam_mat_khau_cu")).thenReturn(true);

        DuLieuKhongHopLeException ex = assertThrows(DuLieuKhongHopLeException.class, () -> khachHangService.doiMatKhau(1L, yeuCau));
        assertEquals("Mật khẩu mới không được trùng với mật khẩu hiện tại", ex.getMessage());
        verify(khachHangRepository, never()).save(any());
    }
}
