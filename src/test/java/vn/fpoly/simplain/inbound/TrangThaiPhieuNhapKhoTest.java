package vn.fpoly.simplain.inbound;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Trạng thái phiếu nhập kho - State machine")
class TrangThaiPhieuNhapKhoTest {

    @Test
    @DisplayName("Phiếu mới chỉ được chuyển sang Đã nhập hoặc Đã hủy")
    void phieuMoiChiDuocChuyenSangTrangThaiHopLe() {
        PhieuNhapKho phieu = PhieuNhapKho.builder().build();
        assertEquals(TrangThaiPhieuNhapKho.CHO_NHAP, phieu.getTrangThai());
        assertTrue(TrangThaiPhieuNhapKho.CHO_NHAP.coTheChuyenSang(TrangThaiPhieuNhapKho.DA_NHAP));
        assertTrue(TrangThaiPhieuNhapKho.CHO_NHAP.coTheChuyenSang(TrangThaiPhieuNhapKho.DA_HUY));
        assertFalse(TrangThaiPhieuNhapKho.CHO_NHAP.coTheChuyenSang(TrangThaiPhieuNhapKho.CHO_NHAP));

        phieu.chuyenSang(TrangThaiPhieuNhapKho.DA_NHAP);
        assertEquals(TrangThaiPhieuNhapKho.DA_NHAP, phieu.getTrangThai());
    }

    @Test
    @DisplayName("Trạng thái cuối không thể chuyển tiếp")
    void trangThaiCuoiKhongTheChuyenTiep() {
        PhieuNhapKho phieu = PhieuNhapKho.builder().build();
        phieu.chuyenSang(TrangThaiPhieuNhapKho.DA_NHAP);

        assertThrows(IllegalStateException.class,
                () -> phieu.chuyenSang(TrangThaiPhieuNhapKho.DA_HUY));

        assertFalse(TrangThaiPhieuNhapKho.DA_NHAP.coTheChuyenSang(TrangThaiPhieuNhapKho.DA_HUY));
        assertFalse(TrangThaiPhieuNhapKho.DA_HUY.coTheChuyenSang(TrangThaiPhieuNhapKho.DA_NHAP));
    }
}
