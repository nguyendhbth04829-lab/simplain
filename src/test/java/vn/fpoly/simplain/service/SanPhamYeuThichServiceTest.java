/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - Kiểm thử đơn vị tầng nghiệp vụ SanPhamYeuThichService
 */
package vn.fpoly.simplain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.fpoly.simplain.dto.SanPhamYeuThichResponse;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.dto.TrangThaiYeuThichResponse;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.entity.SanPhamYeuThich;
import vn.fpoly.simplain.exception.ChuaDangNhapException;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.repository.SanPhamRepository;
import vn.fpoly.simplain.repository.SanPhamYeuThichRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử nghiệp vụ SanPhamYeuThichService (US14)")
class SanPhamYeuThichServiceTest {

    @Mock
    private SanPhamYeuThichRepository sanPhamYeuThichRepository;

    @Mock
    private SanPhamRepository sanPhamRepository;

    @Mock
    private KhachHangService khachHangService;

    @InjectMocks
    private SanPhamYeuThichService sanPhamYeuThichService;

    private KhachHang khachHangMau;
    private SanPham sanPhamMau;
    private SanPhamYeuThich yeuThichMau;

    @BeforeEach
    void khoiTao() {
        khachHangMau = new KhachHang();
        khachHangMau.setId(1L);
        khachHangMau.setMaKhachHang("KH001");
        khachHangMau.setHoTen("Nguyễn Văn A");
        khachHangMau.setTrangThai(1);

        sanPhamMau = SanPham.builder()
                .id(100L)
                .maSanPham("SP001")
                .tenSanPham("Áo Thun Nam Simplain")
                .duongDanSlug("ao-thun-nam-simplain")
                .giaCoBan(new BigDecimal("199000"))
                .chatLieu("100% Cotton")
                .formDang("Regular")
                .kichHoat(true)
                .build();

        yeuThichMau = SanPhamYeuThich.builder()
                .id(10L)
                .maKhachHangId(1L)
                .maSanPhamId(100L)
                .ngayThem(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Lấy danh sách yêu thích: Thành công khi có sản phẩm")
    void layDanhSachYeuThich_ThanhCong() {
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamYeuThichRepository.findAllByMaKhachHangIdOrderByNgayThemDesc(1L)).thenReturn(List.of(yeuThichMau));
        when(sanPhamRepository.findByIdIn(List.of(100L))).thenReturn(List.of(sanPhamMau));

        List<SanPhamYeuThichResponse> ketQua = sanPhamYeuThichService.layDanhSachYeuThich(1L);

        assertNotNull(ketQua);
        assertEquals(1, ketQua.size());
        assertEquals("Áo Thun Nam Simplain", ketQua.get(0).tenSanPham());
        assertEquals("SP001", ketQua.get(0).maSanPham());
        assertEquals(new BigDecimal("199000"), ketQua.get(0).giaCoBan());
    }

    @Test
    @DisplayName("Lấy danh sách yêu thích: Danh sách rỗng khi chưa thích sản phẩm nào")
    void layDanhSachYeuThich_DanhSachRong() {
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamYeuThichRepository.findAllByMaKhachHangIdOrderByNgayThemDesc(1L)).thenReturn(List.of());

        List<SanPhamYeuThichResponse> ketQua = sanPhamYeuThichService.layDanhSachYeuThich(1L);

        assertNotNull(ketQua);
        assertTrue(ketQua.isEmpty());
        verify(sanPhamRepository, never()).findByIdIn(any());
    }

    @Test
    @DisplayName("Thêm vào yêu thích: Thành công sản phẩm mới")
    void themVaoYeuThich_ThanhCong() {
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamRepository.findById(100L)).thenReturn(Optional.of(sanPhamMau));
        when(sanPhamYeuThichRepository.findByMaKhachHangIdAndMaSanPhamId(1L, 100L)).thenReturn(Optional.empty());
        when(sanPhamYeuThichRepository.save(any(SanPhamYeuThich.class))).thenAnswer(invocation -> {
            SanPhamYeuThich yt = invocation.getArgument(0);
            yt.setId(11L);
            return yt;
        });

        SanPhamYeuThichResponse ketQua = sanPhamYeuThichService.themVaoYeuThich(1L, 100L);

        assertNotNull(ketQua);
        assertEquals(11L, ketQua.id());
        assertEquals(100L, ketQua.maSanPhamId());
        assertEquals("Áo Thun Nam Simplain", ketQua.tenSanPham());
        verify(sanPhamYeuThichRepository).save(any(SanPhamYeuThich.class));
    }

    @Test
    @DisplayName("Thêm vào yêu thích: Idempotent - Nếu đã thích rồi thì trả về bản ghi hiện tại mà không lưu trùng")
    void themVaoYeuThich_DaThich_TraVeBanGhiCu() {
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamRepository.findById(100L)).thenReturn(Optional.of(sanPhamMau));
        when(sanPhamYeuThichRepository.findByMaKhachHangIdAndMaSanPhamId(1L, 100L)).thenReturn(Optional.of(yeuThichMau));

        SanPhamYeuThichResponse ketQua = sanPhamYeuThichService.themVaoYeuThich(1L, 100L);

        assertNotNull(ketQua);
        assertEquals(10L, ketQua.id());
        verify(sanPhamYeuThichRepository, never()).save(any(SanPhamYeuThich.class));
    }

    @Test
    @DisplayName("Thêm vào yêu thích: Thất bại khi sản phẩm không tồn tại")
    void themVaoYeuThich_SanPhamKhongTonTai_NemNgoaiLe() {
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamRepository.findById(999L)).thenReturn(Optional.empty());

        KhongTimThayException ex = assertThrows(KhongTimThayException.class,
                () -> sanPhamYeuThichService.themVaoYeuThich(1L, 999L));

        assertTrue(ex.getMessage().contains("Không tìm thấy sản phẩm"));
        verify(sanPhamYeuThichRepository, never()).save(any(SanPhamYeuThich.class));
    }

    @Test
    @DisplayName("Thêm vào yêu thích: Thất bại khi sản phẩm đã ngừng kinh doanh")
    void themVaoYeuThich_SanPhamNgungKinhDoanh_NemNgoaiLe() {
        sanPhamMau.setKichHoat(false);
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamRepository.findById(100L)).thenReturn(Optional.of(sanPhamMau));

        DuLieuKhongHopLeException ex = assertThrows(DuLieuKhongHopLeException.class,
                () -> sanPhamYeuThichService.themVaoYeuThich(1L, 100L));

        assertTrue(ex.getMessage().contains("ngừng kinh doanh"));
        verify(sanPhamYeuThichRepository, never()).save(any(SanPhamYeuThich.class));
    }

    @Test
    @DisplayName("Thêm vào yêu thích: Thất bại khi chưa đăng nhập")
    void themVaoYeuThich_ChuaDangNhap_NemNgoaiLe() {
        when(khachHangService.layKhachHangHopLe(null)).thenThrow(new ChuaDangNhapException("Vui lòng đăng nhập"));

        assertThrows(ChuaDangNhapException.class,
                () -> sanPhamYeuThichService.themVaoYeuThich(null, 100L));
    }

    @Test
    @DisplayName("Xóa khỏi yêu thích: Thành công")
    void xoaKhoiYeuThich_ThanhCong() {
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamYeuThichRepository.findByMaKhachHangIdAndMaSanPhamId(1L, 100L)).thenReturn(Optional.of(yeuThichMau));

        ThongBaoResponse phanHoi = sanPhamYeuThichService.xoaKhoiYeuThich(1L, 100L);

        assertEquals("Đã xóa sản phẩm khỏi danh sách yêu thích", phanHoi.thongDiep());
        verify(sanPhamYeuThichRepository).delete(yeuThichMau);
    }

    @Test
    @DisplayName("Xóa khỏi yêu thích: Báo lỗi khi sản phẩm chưa có trong danh sách")
    void xoaKhoiYeuThich_ChuaYeuThich_NemNgoaiLe() {
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamYeuThichRepository.findByMaKhachHangIdAndMaSanPhamId(1L, 100L)).thenReturn(Optional.empty());

        KhongTimThayException ex = assertThrows(KhongTimThayException.class,
                () -> sanPhamYeuThichService.xoaKhoiYeuThich(1L, 100L));

        assertTrue(ex.getMessage().contains("chưa có trong danh sách"));
        verify(sanPhamYeuThichRepository, never()).delete(any(SanPhamYeuThich.class));
    }

    @Test
    @DisplayName("Toggle yêu thích: Chưa thích -> Thêm vào yêu thích (daYeuThich = true)")
    void toggleYeuThich_ChuaThich_ChuyenSangThich() {
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamRepository.findById(100L)).thenReturn(Optional.of(sanPhamMau));
        when(sanPhamYeuThichRepository.findByMaKhachHangIdAndMaSanPhamId(1L, 100L)).thenReturn(Optional.empty());

        TrangThaiYeuThichResponse ketQua = sanPhamYeuThichService.toggleYeuThich(1L, 100L);

        assertNotNull(ketQua);
        assertTrue(ketQua.daYeuThich());
        assertEquals("Đã thêm sản phẩm vào danh sách yêu thích", ketQua.thongDiep());
        verify(sanPhamYeuThichRepository).save(any(SanPhamYeuThich.class));
    }

    @Test
    @DisplayName("Toggle yêu thích: Đã thích -> Bỏ yêu thích (daYeuThich = false)")
    void toggleYeuThich_DaThich_ChuyenSangBoThich() {
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamRepository.findById(100L)).thenReturn(Optional.of(sanPhamMau));
        when(sanPhamYeuThichRepository.findByMaKhachHangIdAndMaSanPhamId(1L, 100L)).thenReturn(Optional.of(yeuThichMau));

        TrangThaiYeuThichResponse ketQua = sanPhamYeuThichService.toggleYeuThich(1L, 100L);

        assertNotNull(ketQua);
        assertFalse(ketQua.daYeuThich());
        assertEquals("Đã bỏ yêu thích sản phẩm", ketQua.thongDiep());
        verify(sanPhamYeuThichRepository).delete(yeuThichMau);
    }

    @Test
    @DisplayName("Kiểm tra đã yêu thích: Trả về đúng kết quả boolean")
    void kiemTraDaYeuThich_KetQuaDung() {
        when(sanPhamYeuThichRepository.existsByMaKhachHangIdAndMaSanPhamId(1L, 100L)).thenReturn(true);
        when(sanPhamYeuThichRepository.existsByMaKhachHangIdAndMaSanPhamId(1L, 200L)).thenReturn(false);

        assertTrue(sanPhamYeuThichService.kiemTraDaYeuThich(1L, 100L));
        assertFalse(sanPhamYeuThichService.kiemTraDaYeuThich(1L, 200L));
        assertFalse(sanPhamYeuThichService.kiemTraDaYeuThich(null, 100L));
    }

    @Test
    @DisplayName("Đếm số lượng yêu thích: Trả về chính xác số lượng")
    void demSoLuongYeuThich_ThanhCong() {
        when(sanPhamYeuThichRepository.countByMaKhachHangId(1L)).thenReturn(5L);

        long soLuong = sanPhamYeuThichService.demSoLuongYeuThich(1L);

        assertEquals(5L, soLuong);
        assertEquals(0L, sanPhamYeuThichService.demSoLuongYeuThich(null));
    }
}
