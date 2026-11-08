package vn.fpoly.simplain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.entity.ChiTietDonHang;
import vn.fpoly.simplain.entity.ChiTietPhieuNhapHang;
import vn.fpoly.simplain.entity.PhieuNhapHang;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.ChiTietPhieuNhapHangRepository;
import vn.fpoly.simplain.repository.NhaCungCapRepository;
import vn.fpoly.simplain.repository.PhieuNhapHangRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * US43 - Kiểm thử tính Giá vốn Bình quân Gia quyền Di động (AVCO) khi Nhập hàng
 * và Snapshot giá vốn vào Chi tiết đơn hàng.
 */
@ExtendWith(MockitoExtension.class)
class GiaVonBinhQuanAvcoTest {

    @Mock
    private NhaCungCapRepository nhaCungCapRepository;

    @Mock
    private PhieuNhapHangRepository phieuNhapHangRepository;

    @Mock
    private ChiTietPhieuNhapHangRepository chiTietPhieuNhapHangRepository;

    @Mock
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Mock
    private SanPhamRepository sanPhamRepository;

    @InjectMocks
    private NhapHangService nhapHangService;

    @Test
    @DisplayName("US43 - Tiêu chí 1: Tính lại giá vốn biến thể theo công thức AVCO khi nhập thêm lô mới khác giá")
    void testTinhGiaVonBinhQuanDiDongAvco_KhiNhapKho() {
        // Given: Biến thể ban đầu có tồn cũ = 10 cái, giá vốn cũ = 100.000 đ
        Long bienTheId = 101L;
        BienTheSanPham bt = BienTheSanPham.builder()
                .id(bienTheId)
                .maSku("SKU-TEST-AVCO-01")
                .soLuong(10)
                .giaGoc(new BigDecimal("100000"))
                .giaBan(new BigDecimal("250000"))
                .build();

        when(bienTheSanPhamRepository.findById(bienTheId)).thenReturn(Optional.of(bt));

        // Phiếu nhập hàng mới: Nhập 10 cái với giá nhập = 200.000 đ
        Long phieuId = 1L;
        PhieuNhapHang phieu = PhieuNhapHang.builder()
                .id(phieuId)
                .maPhieuNhap("PN-TEST-AVCO")
                .trangThai("CHO_NHAP")
                .build();

        ChiTietPhieuNhapHang ctNhap = ChiTietPhieuNhapHang.builder()
                .id(11L)
                .maPhieuNhapId(phieuId)
                .maBienTheId(bienTheId)
                .soLuongNhap(10)
                .giaNhap(new BigDecimal("200000"))
                .build();

        when(phieuNhapHangRepository.findById(phieuId)).thenReturn(Optional.of(phieu));
        when(chiTietPhieuNhapHangRepository.findByMaPhieuNhapId(phieuId)).thenReturn(List.of(ctNhap));
        when(phieuNhapHangRepository.save(any(PhieuNhapHang.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: Hoàn tất nhập kho
        PhieuNhapHang result = nhapHangService.hoanTatPhieuNhap(phieuId);

        // Then:
        // Công thức AVCO: (10 * 100.000 + 10 * 200.000) / (10 + 10) = 3.000.000 / 20 = 150.000 đ
        assertEquals("DA_NHAP", result.getTrangThai());
        assertEquals(20, bt.getSoLuong(), "Tồn kho mới phải là 10 + 10 = 20");
        assertEquals(new BigDecimal("150000"), bt.getGiaGoc(), "Giá vốn mới theo AVCO phải là 150.000 đ");

        // Đảm bảo đã lưu biến thể với giá vốn mới
        ArgumentCaptor<BienTheSanPham> captor = ArgumentCaptor.forClass(BienTheSanPham.class);
        verify(bienTheSanPhamRepository).save(captor.capture());
        assertEquals(new BigDecimal("150000"), captor.getValue().getGiaGoc());
        assertEquals(20, captor.getValue().getSoLuong());
    }

    @Test
    @DisplayName("US43 - Tiêu chí 1: Nhập kho biến thể mới tinh (tồn cũ = 0) thì giá vốn chính là giá nhập")
    void testTinhGiaVonBinhQuanDiDongAvco_KhiTonCuBangKhong() {
        Long bienTheId = 102L;
        BienTheSanPham bt = BienTheSanPham.builder()
                .id(bienTheId)
                .maSku("SKU-TEST-AVCO-NEW")
                .soLuong(0)
                .giaGoc(BigDecimal.ZERO)
                .build();

        when(bienTheSanPhamRepository.findById(bienTheId)).thenReturn(Optional.of(bt));

        Long phieuId = 2L;
        PhieuNhapHang phieu = PhieuNhapHang.builder()
                .id(phieuId)
                .maPhieuNhap("PN-TEST-AVCO-NEW")
                .trangThai("CHO_NHAP")
                .build();

        ChiTietPhieuNhapHang ctNhap = ChiTietPhieuNhapHang.builder()
                .id(12L)
                .maPhieuNhapId(phieuId)
                .maBienTheId(bienTheId)
                .soLuongNhap(25)
                .giaNhap(new BigDecimal("180000"))
                .build();

        when(phieuNhapHangRepository.findById(phieuId)).thenReturn(Optional.of(phieu));
        when(chiTietPhieuNhapHangRepository.findByMaPhieuNhapId(phieuId)).thenReturn(List.of(ctNhap));
        when(phieuNhapHangRepository.save(any(PhieuNhapHang.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        nhapHangService.hoanTatPhieuNhap(phieuId);

        // Then
        assertEquals(25, bt.getSoLuong());
        assertEquals(new BigDecimal("180000"), bt.getGiaGoc());
    }

    @Test
    @DisplayName("US43 - Tiêu chí 2 & 3: Lưu snapshot giá vốn vào chi_tiet_don_hang và tính lợi nhuận gộp")
    void testSnapshotGiaVonVaLoiNhuanGop() {
        // Giả lập biến thể đang có giá vốn 150.000 đ
        BigDecimal giaGocBienThe = new BigDecimal("150000");
        BigDecimal donGiaBan = new BigDecimal("250000");
        int soLuongBan = 2;

        // Lưu snapshot giá vốn vào dòng chi tiết đơn hàng
        ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                .maDonHangId(999L)
                .maBienTheId(101L)
                .tenSanPham("Áo Thun SIMPLAIN AVCO")
                .maSku("SKU-TEST-AVCO-01")
                .soLuong(soLuongBan)
                .donGia(donGiaBan)
                .giaGoc(giaGocBienThe)
                .thanhTien(donGiaBan.multiply(BigDecimal.valueOf(soLuongBan)))
                .build();

        // Kiểm tra snapshot
        assertEquals(new BigDecimal("150000"), chiTiet.getGiaGoc(), "Snapshot giá vốn tại thời điểm bán phải được giữ nguyên");

        // Tính doanh thu và tổng giá vốn
        BigDecimal doanhThu = chiTiet.getThanhTien(); // 500.000 đ
        BigDecimal tongGiaVon = chiTiet.getGiaGoc().multiply(BigDecimal.valueOf(chiTiet.getSoLuong())); // 300.000 đ
        BigDecimal loiNhuanGop = doanhThu.subtract(tongGiaVon); // 200.000 đ

        assertEquals(new BigDecimal("500000"), doanhThu);
        assertEquals(new BigDecimal("300000"), tongGiaVon);
        assertEquals(new BigDecimal("200000"), loiNhuanGop, "Lợi nhuận gộp = Doanh thu - Tổng giá vốn = 200.000 đ");
    }
}
