package vn.fpoly.simplain.inbound;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InboundService - Các bất biến deterministic ở tầng hệ thống")
class InboundServiceDeterminismTest {

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
    @DisplayName("Gộp dòng trùng SKU, xử lý theo thứ tự ổn định và chỉ ghi một bút toán kho cho mỗi SKU")
    void gopDongTrungSkuVaXuLyTheoThuTuOnDinh() {
        NhaCungCap ncc = NhaCungCap.builder()
                .maNcc("NCC-01")
                .tenNcc("Xưởng May A")
                .kichHoat(true)
                .build();
        when(nhaCungCapRepository.findByMaNccIgnoreCase("NCC-01")).thenReturn(Optional.of(ncc));

        BienTheSanPham bienTheA = BienTheSanPham.builder()
                .id(1L)
                .maSku("SP-A")
                .soLuong(10)
                .giaGoc(new BigDecimal("100.00"))
                .kichHoat(true)
                .build();
        BienTheSanPham bienTheB = BienTheSanPham.builder()
                .id(2L)
                .maSku("SP-B")
                .soLuong(0)
                .giaGoc(new BigDecimal("0.00"))
                .kichHoat(true)
                .build();

        when(bienTheSanPhamRepository.findByMaSkuIgnoreCaseForUpdate("SP-A")).thenReturn(Optional.of(bienTheA));
        when(bienTheSanPhamRepository.findByMaSkuIgnoreCaseForUpdate("SP-B")).thenReturn(Optional.of(bienTheB));
        when(bienTheSanPhamRepository.save(any(BienTheSanPham.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(nhatKyKhoRepository.save(any(NhatKyKho.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(phieuNhapKhoRepository.save(any(PhieuNhapKho.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NhapKhoRequest request = NhapKhoRequest.builder()
                .maNhaCungCap("NCC-01")
                .ghiChu("Lô thử nghiệm")
                .danhSachHang(List.of(
                        dongHang("SP-B", 2, "200.00"),
                        dongHang("SP-A", 5, "100.00"),
                        dongHang("sp-b", 3, "300.00")
                ))
                .build();

        NhapKhoResponse response = inboundService.thucHienNhapKho(request, 99L);

        assertEquals("THANH_CONG", response.getTrangThai());
        assertEquals(2, response.getChiTiet().size());
        assertEquals("SP-A", response.getChiTiet().get(0).getMaSku());
        assertEquals("SP-B", response.getChiTiet().get(1).getMaSku());
        assertEquals(5, response.getChiTiet().get(0).getSoLuongNhap());
        assertEquals(5, response.getChiTiet().get(1).getSoLuongNhap());
        assertEquals(new BigDecimal("260.00"), response.getChiTiet().get(1).getDonGiaNhap());
        assertEquals(new BigDecimal("1800.00"), response.getTongTien());
        assertEquals(5, bienTheB.getSoLuong());
        assertEquals(new BigDecimal("260.00"), bienTheB.getGiaGoc());

        InOrder thuTuKhoa = inOrder(bienTheSanPhamRepository);
        thuTuKhoa.verify(bienTheSanPhamRepository).findByMaSkuIgnoreCaseForUpdate("SP-A");
        thuTuKhoa.verify(bienTheSanPhamRepository).findByMaSkuIgnoreCaseForUpdate("SP-B");

        ArgumentCaptor<NhatKyKho> butToanKho = ArgumentCaptor.forClass(NhatKyKho.class);
        verify(nhatKyKhoRepository, times(2)).save(butToanKho.capture());
        assertEquals(List.of(1L, 2L),
                butToanKho.getAllValues().stream().map(NhatKyKho::getMaBienTheId).toList());
        assertEquals(List.of(5, 5),
                butToanKho.getAllValues().stream().map(NhatKyKho::getSoLuongThayDoi).toList());

        ArgumentCaptor<PhieuNhapKho> phieuCaptor = ArgumentCaptor.forClass(PhieuNhapKho.class);
        verify(phieuNhapKhoRepository).save(phieuCaptor.capture());
        PhieuNhapKho phieu = phieuCaptor.getValue();
        assertEquals(TrangThaiPhieuNhapKho.DA_NHAP, phieu.getTrangThai());
        assertEquals(2, phieu.getChiTietList().size());
        assertEquals(new BigDecimal("1800.00"), phieu.getTongTien());
    }

    @Test
    @DisplayName("Từ chối nhà cung cấp đang tạm ngưng trước khi chạm vào tồn kho")
    void tuChoiNhaCungCapDangTamNgung() {
        NhaCungCap ncc = NhaCungCap.builder()
                .maNcc("NCC-02")
                .tenNcc("Xưởng May B")
                .kichHoat(false)
                .build();
        when(nhaCungCapRepository.findByMaNccIgnoreCase("NCC-02")).thenReturn(Optional.of(ncc));

        NhapKhoRequest request = NhapKhoRequest.builder()
                .maNhaCungCap("NCC-02")
                .danhSachHang(List.of(dongHang("SP-A", 1, "100.00")))
                .build();

        IllegalStateException loi = assertThrows(IllegalStateException.class,
                () -> inboundService.thucHienNhapKho(request, 1L));

        assertTrue(loi.getMessage().contains("tạm ngưng"));
        verifyNoInteractions(bienTheSanPhamRepository, nhatKyKhoRepository, phieuNhapKhoRepository);
    }

    @Test
    @DisplayName("Không ghi bất kỳ dữ liệu nào khi có SKU không tồn tại")
    void khongGhiDuLieuKhiSkuKhongTonTai() {
        NhaCungCap ncc = NhaCungCap.builder()
                .maNcc("NCC-03")
                .tenNcc("Xưởng May C")
                .kichHoat(true)
                .build();
        when(nhaCungCapRepository.findByMaNccIgnoreCase("NCC-03")).thenReturn(Optional.of(ncc));
        when(bienTheSanPhamRepository.findByMaSkuIgnoreCaseForUpdate("SP-KHONG-CO"))
                .thenReturn(Optional.empty());

        NhapKhoRequest request = NhapKhoRequest.builder()
                .maNhaCungCap("NCC-03")
                .danhSachHang(List.of(dongHang("SP-KHONG-CO", 1, "100.00")))
                .build();

        assertThrows(IllegalArgumentException.class, () -> inboundService.thucHienNhapKho(request, 1L));

        verify(bienTheSanPhamRepository, never()).save(any(BienTheSanPham.class));
        verifyNoInteractions(nhatKyKhoRepository, phieuNhapKhoRepository);
    }

    private NhapKhoRequest.ChiTietNhapItem dongHang(String maSku, int soLuong, String donGia) {
        return NhapKhoRequest.ChiTietNhapItem.builder()
                .maSku(maSku)
                .soLuongNhap(soLuong)
                .donGiaNhap(new BigDecimal(donGia))
                .build();
    }
}
