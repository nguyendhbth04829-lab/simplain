package vn.fpoly.simplain.inbound;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.fpoly.simplain.entity.BienTheSanPham;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kho/Tồn kho - Phân bổ kệ Offline và Web Online deterministic")
class PhanBoTonKhoDeterminismTest {

    private final PhanBoTonKhoService service = new PhanBoTonKhoService();

    private BienTheSanPham bienThe(Long id, String maSku, Integer soLuong, boolean kichHoat) {
        return BienTheSanPham.builder()
                .id(id)
                .maSku(maSku)
                .soLuong(soLuong)
                .kichHoat(kichHoat)
                .build();
    }

    @Test
    @DisplayName("Cùng một snapshot đầu vào luôn cho đúng cùng một kết quả")
    void cungMotSnapshotLuonChoCungKetQua() {
        BienTheSanPham bt = bienThe(1L, "SP-AO-DEN-M", 12, true);

        PhanBoTonKho lanDau = service.phanBo(bt, true);
        for (int i = 0; i < 100; i++) {
            assertEquals(lanDau, service.phanBo(bt, true),
                    "Lần gọi thứ " + (i + 2) + " phải trả về cùng kết quả");
        }
    }

    @Test
    @DisplayName("SKU đang bán và còn hàng hiển thị ở cả hai kênh với cùng một lượng tồn")
    void conHangHienThiCaHaiKenhVoiCungMotLuongTon() {
        PhanBoTonKho ketQua = service.phanBo(bienThe(2L, "SP-AO-DEN-L", 12, true), true);

        assertEquals(12, ketQua.tonKhoVatLy());
        assertTrue(ketQua.hienThiKeOffline());
        assertTrue(ketQua.hienThiWebOnline());
        assertEquals(12, ketQua.soLuongKeOffline());
        assertEquals(12, ketQua.soLuongWebOnline());
        assertEquals(PhanBoTonKho.NGUON_DUNG_CHUNG, ketQua.nguonTonKho());
        assertEquals(LyDoPhanBo.SAN_SANG, ketQua.lyDo());
    }

    @Test
    @DisplayName("Biến thể ngừng kinh doanh không lên kệ Offline và không hiển thị Web")
    void bienTheNgungKinhDoanhKhongLenKeVaKhongLenWeb() {
        PhanBoTonKho ketQua = service.phanBo(bienThe(3L, "SP-AO-DEN-XL", 12, false), true);

        assertFalse(ketQua.hienThiKeOffline());
        assertFalse(ketQua.hienThiWebOnline());
        assertEquals(0, ketQua.soLuongKeOffline());
        assertEquals(0, ketQua.soLuongWebOnline());
        assertEquals(LyDoPhanBo.BIEN_THE_NGUNG_KINH_DOANH, ketQua.lyDo());
    }

    @Test
    @DisplayName("Sản phẩm ngừng kinh doanh vẫn còn trên kệ Offline nhưng bị ẩn khỏi Web")
    void sanPhamNgungKinhDoanhVanCoTheNamTrenKeOfflineNhungAnWeb() {
        PhanBoTonKho ketQua = service.phanBo(bienThe(4L, "SP-AO-TRANG-M", 7, true), false);

        assertTrue(ketQua.hienThiKeOffline());
        assertFalse(ketQua.hienThiWebOnline());
        assertEquals(7, ketQua.soLuongKeOffline());
        assertEquals(0, ketQua.soLuongWebOnline());
        assertEquals(LyDoPhanBo.SAN_PHAM_NGUNG_KINH_DOANH, ketQua.lyDo());
    }

    @Test
    @DisplayName("Hết hàng thì cả hai kênh đều nhận 0, không hiển thị số âm")
    void hetHangKhongHienThiOMoiKenh() {
        PhanBoTonKho ketQua = service.phanBo(bienThe(5L, "SP-QUAN-DEN-30", 0, true), true);

        assertEquals(0, ketQua.tonKhoVatLy());
        assertFalse(ketQua.hienThiKeOffline());
        assertFalse(ketQua.hienThiWebOnline());
        assertEquals(0, ketQua.soLuongKeOffline());
        assertEquals(0, ketQua.soLuongWebOnline());
        assertEquals(LyDoPhanBo.HET_HANG, ketQua.lyDo());
    }

    @Test
    @DisplayName("Tồn kho âm từ dữ liệu cũ được cắt về 0 thay vì lọt ra giao diện")
    void tonKhoAmDuLieuCuDuocCatVeKhong() {
        PhanBoTonKho ketQua = service.phanBo(bienThe(6L, "SP-QUAN-XANH-30", -5, true), true);

        assertEquals(0, ketQua.tonKhoVatLy());
        assertEquals(0, ketQua.soLuongKeOffline());
        assertEquals(0, ketQua.soLuongWebOnline());
        assertEquals(LyDoPhanBo.HET_HANG, ketQua.lyDo());
    }

    @Test
    @DisplayName("Không có biến thể thì trả về phân bổ rỗng, không ném lỗi")
    void khongCoBienTheTraVePhanBoRong() {
        PhanBoTonKho ketQua = service.phanBo(null, true);

        assertNull(ketQua.bienTheId());
        assertNull(ketQua.maSku());
        assertEquals(0, ketQua.tonKhoVatLy());
        assertEquals(0, ketQua.soLuongKeOffline());
        assertEquals(0, ketQua.soLuongWebOnline());
        assertEquals(LyDoPhanBo.KHONG_CO_BIEN_THE, ketQua.lyDo());
    }

    @Test
    @DisplayName("Mọi tổ hợp trạng thái đều không phân bổ vượt tồn kho vật lý")
    void moiToHopTrangThaiKhongPhanBoVuotTonKhoVatLy() {
        Random random = new Random(20261104L);

        for (int i = 0; i < 500; i++) {
            int soLuong = random.nextInt(206) - 5; // -5..200, bao gồm dữ liệu âm
            boolean bienTheKichHoat = random.nextBoolean();
            boolean sanPhamDangKinhDoanh = random.nextBoolean();

            BienTheSanPham bt = bienThe((long) i, "SP-TEST-" + i, soLuong, bienTheKichHoat);
            PhanBoTonKho ketQua = service.phanBo(bt, sanPhamDangKinhDoanh);

            int tonKhoVatLy = Math.max(0, soLuong);
            assertEquals(tonKhoVatLy, ketQua.tonKhoVatLy());
            assertTrue(ketQua.soLuongKeOffline() >= 0);
            assertTrue(ketQua.soLuongWebOnline() >= 0);
            assertTrue(ketQua.soLuongKeOffline() <= tonKhoVatLy);
            assertTrue(ketQua.soLuongWebOnline() <= tonKhoVatLy);

            if (!bienTheKichHoat || tonKhoVatLy == 0) {
                assertEquals(0, ketQua.soLuongKeOffline());
                assertEquals(0, ketQua.soLuongWebOnline());
                assertFalse(ketQua.hienThiKeOffline());
                assertFalse(ketQua.hienThiWebOnline());
            }
            if (!sanPhamDangKinhDoanh) {
                assertEquals(0, ketQua.soLuongWebOnline());
                assertFalse(ketQua.hienThiWebOnline());
            }
            if (bienTheKichHoat && tonKhoVatLy > 0) {
                assertEquals(tonKhoVatLy, ketQua.soLuongKeOffline());
            }

            assertEquals(PhanBoTonKho.NGUON_DUNG_CHUNG, ketQua.nguonTonKho());
        }
    }

    @Test
    @DisplayName("Hai kênh là hai góc nhìn của cùng một tồn, không phải hai rổ cộng dồn")
    void haiKenhKhongPhaiHaiRoCongDon() {
        PhanBoTonKho ketQua = service.phanBo(bienThe(7L, "SP-AO-NAVY-L", 9, true), true);

        assertEquals(9, ketQua.tonKhoVatLy());
        assertEquals(ketQua.tonKhoVatLy(), ketQua.soLuongKeOffline(),
                "Kệ Offline không được nhân đôi tồn");
        assertEquals(ketQua.tonKhoVatLy(), ketQua.soLuongWebOnline(),
                "Web Online không được nhân đôi tồn");
    }
}
