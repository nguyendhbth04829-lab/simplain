/**
 * Tác giả: Gia Minh
 * Chức năng: US13 - Kiểm thử đơn vị tầng nghiệp vụ DiaChiService
 * Cập nhật xác nhận commit US13: 25/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.fpoly.simplain.dto.CapNhatDiaChiRequest;
import vn.fpoly.simplain.dto.DiaChiResponse;
import vn.fpoly.simplain.dto.ThemDiaChiRequest;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.entity.DiaChi;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.repository.DiaChiRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử nghiệp vụ DiaChiService (US13)")
class DiaChiServiceTest {

    @Mock
    private DiaChiRepository diaChiRepository;

    @Mock
    private KhachHangService khachHangService;

    @Mock
    private DanhMucHanhChinhService danhMucHanhChinhService;

    @InjectMocks
    private DiaChiService diaChiService;

    private KhachHang khachHangMau;
    private DiaChi diaChiMau;

    @BeforeEach
    void khoiTao() {
        khachHangMau = new KhachHang();
        khachHangMau.setId(1L);
        khachHangMau.setMaKhachHang("KH001");
        khachHangMau.setHoTen("Nguyễn Văn A");
        khachHangMau.setTrangThai(1);

        diaChiMau = new DiaChi();
        diaChiMau.setId(10L);
        diaChiMau.setMaKhachHangId(1L);
        diaChiMau.setTenNguoiNhan("Nguyễn Văn A");
        diaChiMau.setSoDienThoai("0987654321");
        diaChiMau.setTinhThanh("Hà Nội");
        diaChiMau.setQuanHuyen("Quận Cầu Giấy");
        diaChiMau.setPhuongXa("Phường Dịch Vọng");
        diaChiMau.setDiaChiChiTiet("Số 123 đường Xuân Thủy");
        diaChiMau.setLaMacDinh(true);
    }

    @Test
    @DisplayName("Lấy danh sách địa chỉ: Thành công trả về danh sách theo khách hàng")
    void layDanhSachDiaChi_ThanhCong() {
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(diaChiRepository.findAllByMaKhachHangIdOrderByIdDesc(1L)).thenReturn(List.of(diaChiMau));

        List<DiaChiResponse> ketQua = diaChiService.layDanhSachDiaChi(1L);

        assertNotNull(ketQua);
        assertEquals(1, ketQua.size());
        assertEquals("Nguyễn Văn A", ketQua.get(0).tenNguoiNhan());
        assertEquals("Hà Nội", ketQua.get(0).tinhThanh());
        assertTrue(ketQua.get(0).laMacDinh());
        verify(diaChiRepository).findAllByMaKhachHangIdOrderByIdDesc(1L);
    }

    @Test
    @DisplayName("Lấy chi tiết địa chỉ: Thành công khi địa chỉ thuộc sở hữu của khách hàng")
    void layChiTietDiaChi_ThanhCong() {
        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(diaChiRepository.findByIdAndMaKhachHangId(10L, 1L)).thenReturn(Optional.of(diaChiMau));

        DiaChiResponse ketQua = diaChiService.layChiTietDiaChi(1L, 10L);

        assertNotNull(ketQua);
        assertEquals(10L, ketQua.id());
        assertEquals("0987654321", ketQua.soDienThoai());
    }

    @Test
    @DisplayName("Lấy chi tiết địa chỉ: Thất bại khi địa chỉ thuộc khách hàng khác (cô lập tài khoản)")
    void layChiTietDiaChi_KhongPhaiChuSoHuu_NemNgoaiLe() {
        when(khachHangService.layKhachHangHopLe(2L)).thenReturn(khachHangMau);
        when(diaChiRepository.findByIdAndMaKhachHangId(10L, 2L)).thenReturn(Optional.empty());

        KhongTimThayException ex = assertThrows(KhongTimThayException.class,
                () -> diaChiService.layChiTietDiaChi(2L, 10L));

        assertTrue(ex.getMessage().contains("Không tìm thấy địa chỉ"));
    }

    @Test
    @DisplayName("Thêm địa chỉ: Địa chỉ đầu tiên tự động đặt làm mặc định dù cờ gửi lên là false")
    void themDiaChi_DiaChiDauTien_TuDongDatMacDinh() {
        ThemDiaChiRequest req = new ThemDiaChiRequest(
                "Nguyễn Văn A",
                "0987654321",
                "Hà Nội",
                "Quận Cầu Giấy",
                "Phường Dịch Vọng",
                "Số 123",
                false // Gửi false nhưng là địa chỉ đầu tiên
        );

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(danhMucHanhChinhService.kiemTraHopLe("Hà Nội", "Quận Cầu Giấy", "Phường Dịch Vọng")).thenReturn(true);
        when(diaChiRepository.countByMaKhachHangId(1L)).thenReturn(0L);
        when(diaChiRepository.save(any(DiaChi.class))).thenAnswer(invocation -> {
            DiaChi dc = invocation.getArgument(0);
            dc.setId(100L);
            return dc;
        });

        DiaChiResponse ketQua = diaChiService.themDiaChi(1L, req);

        assertNotNull(ketQua);
        assertTrue(ketQua.laMacDinh(), "Địa chỉ đầu tiên phải tự động là mặc định");
        verify(diaChiRepository, never()).goTatCaMacDinh(anyLong());
        verify(diaChiRepository).save(any(DiaChi.class));
    }

    @Test
    @DisplayName("Thêm địa chỉ: Có sẵn địa chỉ, thêm mới với cờ mặc định = true thì gỡ mặc định các địa chỉ cũ")
    void themDiaChi_DiaChiThuHai_DatMacDinh_GoMacDinhCu() {
        ThemDiaChiRequest req = new ThemDiaChiRequest(
                "Nguyễn Văn B",
                "0912345678",
                "Hà Nội",
                "Quận Ba Đình",
                "Phường Kim Mã",
                "Số 456",
                true
        );

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(danhMucHanhChinhService.kiemTraHopLe("Hà Nội", "Quận Ba Đình", "Phường Kim Mã")).thenReturn(true);
        when(diaChiRepository.countByMaKhachHangId(1L)).thenReturn(1L);
        when(diaChiRepository.save(any(DiaChi.class))).thenAnswer(invocation -> {
            DiaChi dc = invocation.getArgument(0);
            dc.setId(101L);
            return dc;
        });

        DiaChiResponse ketQua = diaChiService.themDiaChi(1L, req);

        assertNotNull(ketQua);
        assertTrue(ketQua.laMacDinh());
        verify(diaChiRepository).goTatCaMacDinh(1L);
        verify(diaChiRepository).save(any(DiaChi.class));
    }

    @Test
    @DisplayName("Thêm địa chỉ: Thất bại khi danh mục hành chính sai lệch phân cấp")
    void themDiaChi_DanhMucHanhChinhSai_NemNgoaiLe() {
        ThemDiaChiRequest req = new ThemDiaChiRequest(
                "Nguyễn Văn A",
                "0987654321",
                "Hà Nội",
                "Quận Cầu Giấy",
                "Phường Không Tồn Tại",
                "Số 123",
                false
        );

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(danhMucHanhChinhService.kiemTraHopLe("Hà Nội", "Quận Cầu Giấy", "Phường Không Tồn Tại")).thenReturn(false);

        DuLieuKhongHopLeException ex = assertThrows(DuLieuKhongHopLeException.class,
                () -> diaChiService.themDiaChi(1L, req));

        assertTrue(ex.getMessage().contains("không hợp lệ"));
        verify(diaChiRepository, never()).save(any(DiaChi.class));
    }

    @Test
    @DisplayName("Cập nhật địa chỉ: Thành công khi dữ liệu hợp lệ")
    void capNhatDiaChi_ThanhCong() {
        diaChiMau.setLaMacDinh(false);

        CapNhatDiaChiRequest req = new CapNhatDiaChiRequest(
                "Trần Thị C",
                "0909090909",
                "Thành phố Hồ Chí Minh",
                "Quận 1",
                "Phường Bến Nghé",
                "789 Lê Lợi",
                false
        );

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(danhMucHanhChinhService.kiemTraHopLe("Thành phố Hồ Chí Minh", "Quận 1", "Phường Bến Nghé")).thenReturn(true);
        when(diaChiRepository.findByIdAndMaKhachHangId(10L, 1L)).thenReturn(Optional.of(diaChiMau));
        when(diaChiRepository.save(any(DiaChi.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DiaChiResponse ketQua = diaChiService.capNhatDiaChi(1L, 10L, req);

        assertNotNull(ketQua);
        assertEquals("Trần Thị C", ketQua.tenNguoiNhan());
        assertEquals("0909090909", ketQua.soDienThoai());
        assertEquals("Thành phố Hồ Chí Minh", ketQua.tinhThanh());
        assertFalse(ketQua.laMacDinh());
    }

    @Test
    @DisplayName("Cập nhật địa chỉ: Chuyển sang mặc định thành công và gỡ mặc định các địa chỉ khác")
    void capNhatDiaChi_DoiThanhMacDinh_GoMacDinhCacDiaChiKhac() {
        diaChiMau.setLaMacDinh(false);

        CapNhatDiaChiRequest req = new CapNhatDiaChiRequest(
                "Nguyễn Văn A",
                "0987654321",
                "Hà Nội",
                "Quận Cầu Giấy",
                "Phường Dịch Vọng",
                "Số 123",
                true
        );

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(danhMucHanhChinhService.kiemTraHopLe("Hà Nội", "Quận Cầu Giấy", "Phường Dịch Vọng")).thenReturn(true);
        when(diaChiRepository.findByIdAndMaKhachHangId(10L, 1L)).thenReturn(Optional.of(diaChiMau));
        when(diaChiRepository.save(any(DiaChi.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DiaChiResponse ketQua = diaChiService.capNhatDiaChi(1L, 10L, req);

        assertTrue(ketQua.laMacDinh());
        verify(diaChiRepository).goMacDinhNgoaiTru(1L, 10L);
    }

    @Test
    @DisplayName("Xóa địa chỉ: Xóa địa chỉ thường thành công")
    void xoaDiaChi_DiaChiThuong_ThanhCong() {
        diaChiMau.setLaMacDinh(false);

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(diaChiRepository.findByIdAndMaKhachHangId(10L, 1L)).thenReturn(Optional.of(diaChiMau));

        ThongBaoResponse phanHoi = diaChiService.xoaDiaChi(1L, 10L);

        assertEquals("Xóa địa chỉ nhận hàng thành công", phanHoi.thongDiep());
        verify(diaChiRepository).delete(diaChiMau);
        verify(diaChiRepository, never()).findFirstByMaKhachHangIdOrderByIdAsc(anyLong());
    }

    @Test
    @DisplayName("Xóa địa chỉ: Xóa địa chỉ mặc định tự động kế thừa quyền mặc định cho địa chỉ còn lại")
    void xoaDiaChi_DiaChiMacDinh_TuDongKeThuaMacDinh() {
        diaChiMau.setLaMacDinh(true);

        DiaChi diaChiKeTiep = new DiaChi();
        diaChiKeTiep.setId(11L);
        diaChiKeTiep.setMaKhachHangId(1L);
        diaChiKeTiep.setLaMacDinh(false);

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(diaChiRepository.findByIdAndMaKhachHangId(10L, 1L)).thenReturn(Optional.of(diaChiMau));
        when(diaChiRepository.findFirstByMaKhachHangIdOrderByIdAsc(1L)).thenReturn(Optional.of(diaChiKeTiep));

        ThongBaoResponse phanHoi = diaChiService.xoaDiaChi(1L, 10L);

        assertEquals("Xóa địa chỉ nhận hàng thành công", phanHoi.thongDiep());
        verify(diaChiRepository).delete(diaChiMau);
        verify(diaChiRepository).findFirstByMaKhachHangIdOrderByIdAsc(1L);
        assertTrue(diaChiKeTiep.getLaMacDinh());
        verify(diaChiRepository).save(diaChiKeTiep);
    }

    @Test
    @DisplayName("Đặt làm mặc định: Thành công cập nhật và gỡ các địa chỉ khác")
    void datLamMacDinh_ThanhCong() {
        diaChiMau.setLaMacDinh(false);

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(diaChiRepository.findByIdAndMaKhachHangId(10L, 1L)).thenReturn(Optional.of(diaChiMau));
        when(diaChiRepository.save(any(DiaChi.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DiaChiResponse ketQua = diaChiService.datLamMacDinh(1L, 10L);

        assertTrue(ketQua.laMacDinh());
        verify(diaChiRepository).goMacDinhNgoaiTru(1L, 10L);
        verify(diaChiRepository).save(diaChiMau);
    }

    @Test
    @DisplayName("Đặt làm mặc định: Báo lỗi khi địa chỉ không thuộc sở hữu")
    void datLamMacDinh_KhongThuocSoHuu_NemNgoaiLe() {
        when(khachHangService.layKhachHangHopLe(99L)).thenReturn(khachHangMau);
        when(diaChiRepository.findByIdAndMaKhachHangId(10L, 99L)).thenReturn(Optional.empty());

        assertThrows(KhongTimThayException.class, () -> diaChiService.datLamMacDinh(99L, 10L));
        verify(diaChiRepository, never()).goTatCaMacDinh(anyLong());
    }

    @Test
    @DisplayName("Cập nhật địa chỉ: Khách hàng chỉ có 1 địa chỉ thì không được bỏ mặc định")
    void capNhatDiaChi_ChiCoMotDiaChi_BoMacDinh_BatBuocGiuNguyenMacDinh() {
        diaChiMau.setLaMacDinh(true);

        CapNhatDiaChiRequest req = new CapNhatDiaChiRequest(
                "Nguyễn Văn A",
                "0987654321",
                "Hà Nội",
                "Quận Cầu Giấy",
                "Phường Dịch Vọng",
                "Số 123",
                false // Cố tình bỏ cờ mặc định
        );

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(danhMucHanhChinhService.kiemTraHopLe("Hà Nội", "Quận Cầu Giấy", "Phường Dịch Vọng")).thenReturn(true);
        when(diaChiRepository.findByIdAndMaKhachHangId(10L, 1L)).thenReturn(Optional.of(diaChiMau));
        when(diaChiRepository.countByMaKhachHangId(1L)).thenReturn(1L); // Chỉ có đúng 1 địa chỉ
        when(diaChiRepository.save(any(DiaChi.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DiaChiResponse ketQua = diaChiService.capNhatDiaChi(1L, 10L, req);

        assertTrue(ketQua.laMacDinh(), "Nếu chỉ có 1 địa chỉ thì bắt buộc phải luôn là địa chỉ mặc định");
    }

    @Test
    @DisplayName("Cập nhật địa chỉ: Không phải chủ sở hữu ném ngoại lệ KhongTimThayException")
    void capNhatDiaChi_KhongPhaiChuSoHuu_NemNgoaiLe() {
        CapNhatDiaChiRequest req = new CapNhatDiaChiRequest(
                "Kẻ tấn công",
                "0911111111",
                "Hà Nội",
                "Quận Cầu Giấy",
                "Phường Dịch Vọng",
                "Số 999",
                false
        );

        when(khachHangService.layKhachHangHopLe(2L)).thenReturn(khachHangMau);
        when(diaChiRepository.findByIdAndMaKhachHangId(10L, 2L)).thenReturn(Optional.empty());

        assertThrows(KhongTimThayException.class,
                () -> diaChiService.capNhatDiaChi(2L, 10L, req));
        verify(diaChiRepository, never()).save(any(DiaChi.class));
    }

    @Test
    @DisplayName("Xóa địa chỉ: Không phải chủ sở hữu ném ngoại lệ KhongTimThayException")
    void xoaDiaChi_KhongPhaiChuSoHuu_NemNgoaiLe() {
        when(khachHangService.layKhachHangHopLe(2L)).thenReturn(khachHangMau);
        when(diaChiRepository.findByIdAndMaKhachHangId(10L, 2L)).thenReturn(Optional.empty());

        assertThrows(KhongTimThayException.class,
                () -> diaChiService.xoaDiaChi(2L, 10L));
        verify(diaChiRepository, never()).delete(any(DiaChi.class));
    }

    @Test
    @DisplayName("Xóa địa chỉ: Xóa địa chỉ duy nhất thành công và không ném lỗi kế thừa")
    void xoaDiaChi_DiaChiDuyNhat_XoaThanhCongKhongKeThua() {
        diaChiMau.setLaMacDinh(true);

        when(khachHangService.layKhachHangHopLe(1L)).thenReturn(khachHangMau);
        when(diaChiRepository.findByIdAndMaKhachHangId(10L, 1L)).thenReturn(Optional.of(diaChiMau));
        when(diaChiRepository.findFirstByMaKhachHangIdOrderByIdAsc(1L)).thenReturn(Optional.empty()); // Không còn ai khác

        ThongBaoResponse phanHoi = diaChiService.xoaDiaChi(1L, 10L);

        assertEquals("Xóa địa chỉ nhận hàng thành công", phanHoi.thongDiep());
        verify(diaChiRepository).delete(diaChiMau);
        verify(diaChiRepository, never()).save(any(DiaChi.class));
    }
}
