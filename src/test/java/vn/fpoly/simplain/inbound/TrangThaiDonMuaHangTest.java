package vn.fpoly.simplain.inbound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrangThaiDonMuaHangTest {
    @Test
    void chiChoPhepTienTrinhDonMuaHopLe() {
        assertTrue(TrangThaiDonMuaHang.NHAP.choPhepChuyenSang(TrangThaiDonMuaHang.CHO_NCC_PHAN_HOI));
        assertTrue(TrangThaiDonMuaHang.CHO_NCC_PHAN_HOI.choPhepChuyenSang(TrangThaiDonMuaHang.DANG_THUONG_LUONG));
        assertTrue(TrangThaiDonMuaHang.DANG_GIAO.choPhepChuyenSang(TrangThaiDonMuaHang.CHO_GIAO_BU));
        assertFalse(TrangThaiDonMuaHang.NHAP.choPhepChuyenSang(TrangThaiDonMuaHang.DA_CHOT_DON));
        assertFalse(TrangThaiDonMuaHang.DA_HOAN_TAT.choPhepChuyenSang(TrangThaiDonMuaHang.DANG_GIAO));
    }
}
