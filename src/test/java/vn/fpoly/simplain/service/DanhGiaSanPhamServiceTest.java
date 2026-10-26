/**
 * Tác giả: Gia Minh
 * Chức năng: US15 - Kiểm thử đơn vị tầng nghiệp vụ DanhGiaSanPhamService
 * Cập nhật xác nhận commit US15: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.fpoly.simplain.dto.DanhGiaResponse;
import vn.fpoly.simplain.dto.TaoDanhGiaRequest;
import vn.fpoly.simplain.dto.ThongKeDanhGiaResponse;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.exception.XungDotDuLieuException;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử nghiệp vụ DanhGiaSanPhamService (US15)")
class DanhGiaSanPhamServiceTest {

    @Mock
    private DanhGiaSanPhamRepository danhGiaSanPhamRepository;

    @Mock
    private SanPhamRepository sanPhamRepository;

    @Mock
    private KhachHangService khachHangService;

    @Mock
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Mock
    private DonHangRepository donHangRepository;

    @Mock
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @InjectMocks
    private DanhGiaSanPhamService danhGiaSanPhamService;

    private KhachHang khachHangMau;
    private SanPham sanPhamMau;
    private ChiTietDonHang chiTietDonHangMau;
    private DonHang donHangMau;
    private TrangThaiDonHang trangThaiHoanThanh;

    @BeforeEach
    void khoiTao() {
        khachHangMau = new KhachHang();
        khachHangMau.setId(1L);
        khachHangMau.setMaKhachHang("KH001");
        khachHangMau.setHoTen("Nguyễn Văn A");
        khachHangMau.setTrangThai(1);

        sanPhamMau = SanPham.builder()
                .id(10L)
                .maSanPham("SP001")
                .tenSanPham("Áo Polo Nam Simplain")
                .kichHoat(true)
                .build();

        donHangMau = DonHang.builder()
                .id(100L)
                .maDonHang("DH001")
                .maKhachHangId(1L)
                .maTrangThaiId(5L)
                .build();

        chiTietDonHangMau = ChiTietDonHang.builder()
                .id(50L)
                .maDonHangId(100L)
                .maBienTheId(200L)
                .tenSanPham("Áo Polo Nam Simplain")
                .maSku("SKU-POLO-M")
                .build();

        trangThaiHoanThanh = TrangThaiDonHang.builder()
                .id(5L)
                .maTrangThai("HOAN_THANH")
                .tenTrangThai("Hoàn thành")
                .build();
    }

    @Test
    @DisplayName("Tạo đánh giá: Thành công khi đáp ứng đầy đủ điều kiện")
    void taoDanhGia_ThanhCong() {
        TaoDanhGiaRequest req = new TaoDanhGiaRequest(10L, 50L, 5, "Sản phẩm tuyệt vời!", List.of("https://img.com/1.jpg"));

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamRepository.findById(10L)).thenReturn(Optional.of(sanPhamMau));
        when(chiTietDonHangRepository.findById(50L)).thenReturn(Optional.of(chiTietDonHangMau));
        when(donHangRepository.findById(100L)).thenReturn(Optional.of(donHangMau));
        when(trangThaiDonHangRepository.findById(5L)).thenReturn(Optional.of(trangThaiHoanThanh));
        when(danhGiaSanPhamRepository.existsByMaChiTietDonHangId(50L)).thenReturn(false);
        when(danhGiaSanPhamRepository.save(any(DanhGiaSanPham.class))).thenAnswer(invocation -> {
            DanhGiaSanPham dg = invocation.getArgument(0);
            dg.setId(1L);
            return dg;
        });

        DanhGiaResponse ketQua = danhGiaSanPhamService.taoDanhGia(1L, req);

        assertNotNull(ketQua);
        assertEquals(1L, ketQua.id());
        assertEquals(5, ketQua.soSao());
        assertEquals("Sản phẩm tuyệt vời!", ketQua.noiDung());
        assertEquals("Áo Polo Nam Simplain", ketQua.tenSanPham());
        assertEquals("Nguyễn Văn A", ketQua.tenKhachHang());
        assertEquals(1, ketQua.danhSachAnh().size());
        verify(danhGiaSanPhamRepository).save(any(DanhGiaSanPham.class));
    }

    @Test
    @DisplayName("Tạo đánh giá: Thất bại khi chi tiết đơn hàng đã được đánh giá trước đó (chống trùng)")
    void taoDanhGia_DaTungDanhGia_NemNgoaiLe() {
        TaoDanhGiaRequest req = new TaoDanhGiaRequest(10L, 50L, 5, "Đánh giá lại", List.of());

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamRepository.findById(10L)).thenReturn(Optional.of(sanPhamMau));
        when(chiTietDonHangRepository.findById(50L)).thenReturn(Optional.of(chiTietDonHangMau));
        when(donHangRepository.findById(100L)).thenReturn(Optional.of(donHangMau));
        when(trangThaiDonHangRepository.findById(5L)).thenReturn(Optional.of(trangThaiHoanThanh));
        when(danhGiaSanPhamRepository.existsByMaChiTietDonHangId(50L)).thenReturn(true);

        assertThrows(XungDotDuLieuException.class, () -> danhGiaSanPhamService.taoDanhGia(1L, req));
        verify(danhGiaSanPhamRepository, never()).save(any());
    }

    @Test
    @DisplayName("Tạo đánh giá: Thất bại khi đơn hàng thuộc về khách hàng khác")
    void taoDanhGia_DonHangCuaNguoiKhac_NemNgoaiLe() {
        donHangMau.setMaKhachHangId(2L); // Thuộc về khách hàng 2
        TaoDanhGiaRequest req = new TaoDanhGiaRequest(10L, 50L, 5, "Nhận xét", List.of());

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamRepository.findById(10L)).thenReturn(Optional.of(sanPhamMau));
        when(chiTietDonHangRepository.findById(50L)).thenReturn(Optional.of(chiTietDonHangMau));
        when(donHangRepository.findById(100L)).thenReturn(Optional.of(donHangMau));

        DuLieuKhongHopLeException ex = assertThrows(DuLieuKhongHopLeException.class,
                () -> danhGiaSanPhamService.taoDanhGia(1L, req));

        assertTrue(ex.getMessage().contains("khách hàng khác"));
        verify(danhGiaSanPhamRepository, never()).save(any());
    }

    @Test
    @DisplayName("Tạo đánh giá: Thất bại khi đơn hàng ở trạng thái đã hủy")
    void taoDanhGia_DonHangBiHuy_NemNgoaiLe() {
        TrangThaiDonHang ttHuy = TrangThaiDonHang.builder().id(6L).maTrangThai("HUY").tenTrangThai("Đã hủy").build();
        donHangMau.setMaTrangThaiId(6L);

        TaoDanhGiaRequest req = new TaoDanhGiaRequest(10L, 50L, 4, "Tốt", List.of());

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(sanPhamRepository.findById(10L)).thenReturn(Optional.of(sanPhamMau));
        when(chiTietDonHangRepository.findById(50L)).thenReturn(Optional.of(chiTietDonHangMau));
        when(donHangRepository.findById(100L)).thenReturn(Optional.of(donHangMau));
        when(trangThaiDonHangRepository.findById(6L)).thenReturn(Optional.of(ttHuy));

        DuLieuKhongHopLeException ex = assertThrows(DuLieuKhongHopLeException.class,
                () -> danhGiaSanPhamService.taoDanhGia(1L, req));

        assertTrue(ex.getMessage().contains("hoàn thành hoặc giao thành công"));
    }

    @Test
    @DisplayName("Thống kê đánh giá: Tính điểm trung bình và phân bố sao chính xác")
    void layThongKeDanhGia_ThanhCong() {
        DanhGiaSanPham dg1 = DanhGiaSanPham.builder().id(1L).maSanPhamId(10L).soSao(5).build();
        DanhGiaSanPham dg2 = DanhGiaSanPham.builder().id(2L).maSanPhamId(10L).soSao(5).build();
        DanhGiaSanPham dg3 = DanhGiaSanPham.builder().id(3L).maSanPhamId(10L).soSao(4).build();

        when(danhGiaSanPhamRepository.findAllByMaSanPhamIdOrderByNgayTaoDesc(10L))
                .thenReturn(List.of(dg1, dg2, dg3));

        ThongKeDanhGiaResponse tk = danhGiaSanPhamService.layThongKeDanhGia(10L);

        assertNotNull(tk);
        assertEquals(3, tk.tongSoDanhGia());
        assertEquals(2, tk.soLuong5Sao());
        assertEquals(1, tk.soLuong4Sao());
        assertEquals(0, tk.soLuong1Sao());
        assertEquals(4.7, tk.diemTrungBinh()); // (5+5+4)/3 = 4.666 -> 4.7
    }

    @Test
    @DisplayName("Thống kê đánh giá: Sản phẩm chưa có đánh giá trả về mặc định 0 sao")
    void layThongKeDanhGia_ChuaCoDanhGia_TraVe0() {
        when(danhGiaSanPhamRepository.findAllByMaSanPhamIdOrderByNgayTaoDesc(10L)).thenReturn(List.of());

        ThongKeDanhGiaResponse tk = danhGiaSanPhamService.layThongKeDanhGia(10L);

        assertNotNull(tk);
        assertEquals(0, tk.tongSoDanhGia());
        assertEquals(0.0, tk.diemTrungBinh());
    }
}
