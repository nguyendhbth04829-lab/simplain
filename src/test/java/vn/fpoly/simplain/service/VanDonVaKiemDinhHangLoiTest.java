package vn.fpoly.simplain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.fpoly.simplain.dto.CapNhatTrangThaiVanDonRequest;
import vn.fpoly.simplain.dto.ThongTinVanDonDto;
import vn.fpoly.simplain.dto.XetDuyetRmaRequest;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;
import vn.fpoly.simplain.service.shipping.DonViVanChuyenService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Kiểm thử nghiệp vụ Vận đơn bên thứ 3, Hàng mất khi giao không hoàn kho,
 * và Thẩm định chất lượng hàng lỗi (QC Inspection) khi đổi trả.
 */
@ExtendWith(MockitoExtension.class)
class VanDonVaKiemDinhHangLoiTest {

    @Mock
    private DonHangRepository donHangRepository;

    @Mock
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Mock
    private LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;

    @Mock
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Mock
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Mock
    private YeuCauDoiTraRepository yeuCauDoiTraRepository;

    @Mock
    private ChiTietSanPhamDoiTraRepository chiTietSanPhamDoiTraRepository;

    @InjectMocks
    private QuanLyVanDonService quanLyVanDonService;

    @InjectMocks
    private DoiTraService doiTraService;

    @Test
    @DisplayName("Đơn vị vận chuyển Service: Sinh mã vận đơn và link tra cứu đúng định dạng cho các hãng thứ 3")
    void testSinhMaVanDonBenThuBa() {
        DonViVanChuyenService service = new DonViVanChuyenService(donHangRepository, lichSuTrangThaiDonHangRepository);
        List<Map<String, Object>> dsDoiTac = service.layDanhSachDoiTacVanChuyen();
        assertEquals(5, dsDoiTac.size(), "Hệ thống hỗ trợ 5 đối tác vận chuyển bên thứ 3");

        DonHang dh = DonHang.builder()
                .id(100L)
                .maDonHang("DH888888")
                .phiVanChuyen(new BigDecimal("30000"))
                .build();
        when(donHangRepository.findById(100L)).thenReturn(Optional.of(dh));

        NhanVien nv = NhanVien.builder().id(1L).hoTen("Admin QC").build();

        // 1. GHN
        ThongTinVanDonDto ghn = service.taoVanDonTuDong(100L, "GHN", "Giao tiêu chuẩn", nv);
        assertNotNull(ghn);
        assertTrue(ghn.getMaVanDon().startsWith("GHN-VN"), "Mã GHN đúng tiền tố");
        assertTrue(ghn.getLinkTraCuu().contains("donhang.ghn.vn"), "Link tra cứu GHN chính xác");

        // 2. GHTK
        ThongTinVanDonDto ghtk = service.taoVanDonTuDong(100L, "GHTK", "Giao hỏa tốc 24h", nv);
        assertNotNull(ghtk);
        assertTrue(ghtk.getMaVanDon().startsWith("GHTK-HN"), "Mã GHTK đúng tiền tố");
        assertTrue(ghtk.getLinkTraCuu().contains("ghtk.vn"), "Link tra cứu GHTK chính xác");
    }

    @Test
    @DisplayName("Hàng mất trên đường giao (HANG_THAT_LAC): Tuyệt đối KHÔNG cộng lại tồn kho")
    void testHangMatTrenDuongGiaoKhongHoanTonKho() {
        TrangThaiDonHang ttDangGiao = TrangThaiDonHang.builder().id(3L).maTrangThai("DANG_GIAO").tenTrangThai("Đang giao").build();
        TrangThaiDonHang ttThatLac = TrangThaiDonHang.builder().id(8L).maTrangThai("HANG_THAT_LAC").tenTrangThai("Hàng thất lạc").build();

        PhuongThucThanhToan ptttOnline = PhuongThucThanhToan.builder()
                .id(2L)
                .maPhuongThuc("VNPAY")
                .tenPhuongThuc("VNPay QR")
                .laTrucTuyen(true)
                .build();

        DonHang dh = DonHang.builder()
                .id(100L)
                .maDonHang("DH999999")
                .trangThaiDonHang(ttDangGiao)
                .phuongThucThanhToan(ptttOnline)
                .tongThanhToan(new BigDecimal("500000"))
                .tongTienHang(new BigDecimal("500000"))
                .build();

        when(donHangRepository.findById(100L)).thenReturn(Optional.of(dh));
        when(trangThaiDonHangRepository.findByMaTrangThai("HANG_THAT_LAC")).thenReturn(Optional.of(ttThatLac));
        when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));

        CapNhatTrangThaiVanDonRequest req = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("HANG_THAT_LAC")
                .ghiChu("Bưu cục báo hàng bị rơi mất trên đường vận chuyển")
                .hangBiThatLac(true)
                .build();

        NhanVien actor = NhanVien.builder().id(1L).hoTen("Admin QC").build();
        quanLyVanDonService.capNhatTrangThaiVanDon(100L, req, actor);

        // Xác minh: KHÔNG BAO GIỜ gọi hoanTonKhoAtomic khi hàng bị thất lạc
        verify(bienTheSanPhamRepository, never()).hoanTonKhoAtomic(anyLong(), anyInt());

        // Đơn hàng chuyển sang HANG_THAT_LAC
        assertEquals("HANG_THAT_LAC", dh.getTrangThaiDonHang().getMaTrangThai());
        assertEquals("CHO_HOAN_TIEN", dh.getTrangThaiHoanTien(), "Kích hoạt hoàn tiền cho khách thanh toán online");
    }

    @Test
    @DisplayName("Thẩm định QC Đổi trả: Hàng lỗi Còn dùng được -> CỘNG TỒN KHO BÁN TIẾP")
    void testKiemDinhHangLoiConDungDuocCongTonKho() {
        DonHang dh = DonHang.builder().id(200L).maDonHang("DH200").build();
        YeuCauDoiTra rma = YeuCauDoiTra.builder()
                .id(50L)
                .maYeuCauRma("RMA123456")
                .trangThai("DANG_XU_LY")
                .loaiDoiTra("TRA_HANG_HOAN_TIEN")
                .donHang(dh)
                .khachHang(KhachHang.builder().id(1L).hoTen("Khach Hang A").build())
                .build();

        ChiTietDonHang ctdh = ChiTietDonHang.builder()
                .id(101L)
                .maBienTheId(55L)
                .maSku("SKU-AO-POLO-L")
                .build();

        ChiTietSanPhamDoiTra item = ChiTietSanPhamDoiTra.builder()
                .id(10L)
                .maYeuCauDoiTraId(50L)
                .maChiTietDonHangId(101L)
                .soLuongTra(1)
                .chiTietDonHang(ctdh)
                .tinhTrangSanPham("Khách mặc thử hơi chật, áo còn nguyên tem tag")
                .build();

        when(yeuCauDoiTraRepository.findById(50L)).thenReturn(Optional.of(rma));
        when(chiTietSanPhamDoiTraRepository.findByMaYeuCauDoiTraId(50L)).thenReturn(List.of(item));
        when(chiTietDonHangRepository.findMaBienTheIdById(101L)).thenReturn(55L);
        when(yeuCauDoiTraRepository.saveAndFlush(any(YeuCauDoiTra.class))).thenReturn(rma);

        XetDuyetRmaRequest req = XetDuyetRmaRequest.builder()
                .trangThaiMoi("HOAN_THANH")
                .ketQuaKiemDinh("CON_DUNG_DUOC")
                .maGiaoDichNganHang("TXN-VNPAY-998877")
                .tinhTrangHangThucTe("Sản phẩm nguyên vẹn đạt chuẩn 100%, nhập lại kho bán tiếp")
                .ghiChuAdmin("Duyệt nhập kho")
                .build();

        NhanVien actor = NhanVien.builder().id(1L).hoTen("Admin QC").build();
        doiTraService.xetDuyetYeuCauRma(50L, req, null, actor);

        // Hàng còn dùng được: Phải gọi hoanTonKhoAtomic để cộng lại tồn kho bán tiếp
        verify(bienTheSanPhamRepository, times(1)).hoanTonKhoAtomic(55L, 1);
        assertEquals("HOAN_THANH", rma.getTrangThai());
    }

    @Test
    @DisplayName("Thẩm định QC Đổi trả: Hàng lỗi Phế phẩm hỏng -> TRỪ VỨT BỎ, KHÔNG CỘNG KHO")
    void testKiemDinhHangLoiPhePhamKhongCongTonKho() {
        DonHang dh = DonHang.builder().id(201L).maDonHang("DH201").build();
        YeuCauDoiTra rma = YeuCauDoiTra.builder()
                .id(51L)
                .maYeuCauRma("RMA123457")
                .trangThai("DANG_XU_LY")
                .loaiDoiTra("TRA_HANG_HOAN_TIEN")
                .donHang(dh)
                .khachHang(KhachHang.builder().id(2L).hoTen("Khach Hang B").build())
                .build();

        ChiTietDonHang ctdh = ChiTietDonHang.builder()
                .id(102L)
                .maBienTheId(77L)
                .maSku("SKU-QUAN-JEAN-32")
                .build();

        ChiTietSanPhamDoiTra item = ChiTietSanPhamDoiTra.builder()
                .id(11L)
                .maYeuCauDoiTraId(51L)
                .maChiTietDonHangId(102L)
                .soLuongTra(1)
                .chiTietDonHang(ctdh)
                .tinhTrangSanPham("Rách toạc vải nát không thể dùng")
                .build();

        when(yeuCauDoiTraRepository.findById(51L)).thenReturn(Optional.of(rma));
        when(chiTietSanPhamDoiTraRepository.findByMaYeuCauDoiTraId(51L)).thenReturn(List.of(item));
        when(yeuCauDoiTraRepository.saveAndFlush(any(YeuCauDoiTra.class))).thenReturn(rma);

        XetDuyetRmaRequest req = XetDuyetRmaRequest.builder()
                .trangThaiMoi("HOAN_THANH")
                .ketQuaKiemDinh("PHE_PHAM_HONG")
                .maGiaoDichNganHang("TXN-VNPAY-998878")
                .tinhTrangHangThucTe("Áo bị thủng lỗ lớn cháy xém, không thể tái chế hay bán tiếp, xuất hủy phế phẩm")
                .ghiChuAdmin("Tiêu hủy hàng lỗi hỏng")
                .build();

        NhanVien actor = NhanVien.builder().id(1L).hoTen("Admin QC").build();
        doiTraService.xetDuyetYeuCauRma(51L, req, null, actor);

        // Hàng phế phẩm hỏng: Tuyệt đối KHÔNG ĐƯỢC gọi hoanTonKhoAtomic
        verify(bienTheSanPhamRepository, never()).hoanTonKhoAtomic(77L, 1);
        assertEquals("HOAN_THANH", rma.getTrangThai());
    }

    @Test
    @DisplayName("Thất lạc một phần: Trừ tiền sản phẩm mất vào hóa đơn, sản phẩm còn lại giữ nguyên, không hoàn kho")
    void testThatLacMotPhan_truTienHoaDon_khongHoanKho() {
        TrangThaiDonHang ttDangGiao = TrangThaiDonHang.builder().id(3L).maTrangThai("DANG_GIAO").tenTrangThai("Đang giao").build();
        TrangThaiDonHang ttThatLacMotPhan = TrangThaiDonHang.builder().id(9L).maTrangThai("THAT_LAC_MOT_PHAN").tenTrangThai("Thất lạc một phần").build();

        DonHang dh = DonHang.builder()
                .id(101L)
                .maDonHang("DH888999")
                .trangThaiDonHang(ttDangGiao)
                .tongTienHang(new BigDecimal("700000"))
                .tongThanhToan(new BigDecimal("700000"))
                .build();

        ChiTietDonHang item1 = ChiTietDonHang.builder()
                .id(1L)
                .maDonHangId(101L)
                .maBienTheId(10L)
                .tenSanPham("Áo Polo")
                .soLuong(1)
                .thanhTien(new BigDecimal("300000"))
                .build();

        ChiTietDonHang item2 = ChiTietDonHang.builder()
                .id(2L)
                .maDonHangId(101L)
                .maBienTheId(20L)
                .tenSanPham("Quần Jean")
                .soLuong(1)
                .thanhTien(new BigDecimal("400000"))
                .build();

        when(donHangRepository.findById(101L)).thenReturn(Optional.of(dh));
        when(chiTietDonHangRepository.findByMaDonHangId(101L)).thenReturn(List.of(item1, item2));
        when(trangThaiDonHangRepository.findByMaTrangThai("THAT_LAC_MOT_PHAN")).thenReturn(Optional.of(ttThatLacMotPhan));
        when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));

        CapNhatTrangThaiVanDonRequest req = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("THAT_LAC_MOT_PHAN")
                .hangBiThatLac(true)
                .chiTietBiMatIds(List.of(1L))
                .build();

        NhanVien actor = NhanVien.builder().id(1L).hoTen("Admin Kho").build();
        quanLyVanDonService.capNhatTrangThaiVanDon(101L, req, actor);

        // Trạng thái đơn chuyển sang THAT_LAC_MOT_PHAN
        assertEquals("THAT_LAC_MOT_PHAN", dh.getTrangThaiDonHang().getMaTrangThai());

        // Kiểm tra tiền bị mất (300.000) đã được trừ vào hóa đơn của khách: 700.000 - 300.000 = 400.000
        assertEquals(new BigDecimal("400000"), dh.getTongTienHang());
        assertEquals(new BigDecimal("400000"), dh.getTongThanhToan());

        // Item 1 bị mất, item 2 giữ nguyên
        assertEquals("MAT_HANG", item1.getTrangThaiItem());
        assertEquals("BINH_THUONG", item2.getTrangThaiItem());

        // Tuyệt đối không hoàn kho cho món bị mất
        verify(bienTheSanPhamRepository, never()).hoanTonKhoAtomic(anyLong(), anyInt());
    }
}
