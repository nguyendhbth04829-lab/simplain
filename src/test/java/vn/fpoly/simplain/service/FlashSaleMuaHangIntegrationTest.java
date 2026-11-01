package vn.fpoly.simplain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.DatHangRequest;
import vn.fpoly.simplain.dto.GioHangResponse;
import vn.fpoly.simplain.dto.ItemGioHangDto;
import vn.fpoly.simplain.dto.ThemGioHangRequest;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Kiểm thử Tích hợp Mua sản phẩm trong khung giờ Flash Sale (US29 & US17 & US19)")
class FlashSaleMuaHangIntegrationTest {

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private DonHangService donHangService;

    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Autowired
    private ChiTietFlashSaleRepository chiTietFlashSaleRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichThuocRepository kichThuocRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Test
    @DisplayName("Khách mua sản phẩm Flash Sale được áp dụng giá sốc và giới hạn số lượng mở bán")
    void testMuaSanPhamFlashSaleThanhCongVaCapNhatSoLuongDaBan() {
        // 1. Lấy hoặc tạo dữ liệu mẫu: Màu, Size, Sản phẩm, Biến thể
        MauSac ms = mauSacRepository.findAll().stream().findFirst().orElseGet(() ->
                mauSacRepository.save(MauSac.builder().tenMau("Đen Test").maMau("#090909").kichHoat(true).build()));
        KichThuoc kt = kichThuocRepository.findAll().stream().findFirst().orElseGet(() ->
                kichThuocRepository.save(KichThuoc.builder().tenKichThuoc("L Test").maKichThuoc("LT").kichHoat(true).build()));
        String randomSuffix = String.valueOf(System.currentTimeMillis()).substring(7);
        SanPham sp = sanPhamRepository.findAll().stream().findFirst().orElseGet(() ->
                sanPhamRepository.save(SanPham.builder()
                        .tenSanPham("Áo Test Flash Sale " + randomSuffix)
                        .maSanPham("SP-FS-" + randomSuffix)
                        .giaCoBan(new BigDecimal("300000"))
                        .chatLieu("Cotton")
                        .formDang("Regular")
                        .moTaChiTiet("Mô tả chi tiết sản phẩm")
                        .duongDanSlug("ao-test-fs-" + randomSuffix)
                        .kichHoat(true)
                        .build()));

        BienTheSanPham bt = bienTheSanPhamRepository.save(BienTheSanPham.builder()
                .maSanPhamId(sp.getId())
                .maMauSacId(ms.getId())
                .maKichThuocId(kt.getId())
                .maSku("SKU-FS-" + randomSuffix)
                .giaBan(new BigDecimal("300000"))
                .soLuong(50)
                .kichHoat(true)
                .build());

        // 2. Tạo đợt Flash Sale đang diễn ra
        FlashSale fs = flashSaleRepository.save(FlashSale.builder()
                .tieuDe("Flash Sale Đang Hoạt Động Test")
                .thoiGianBatDau(LocalDateTime.now().minusHours(1))
                .thoiGianKetThuc(LocalDateTime.now().plusHours(2))
                .kichHoat(true)
                .build());

        BigDecimal giaFlashSale = new BigDecimal("180000");
        ChiTietFlashSale ctFs = chiTietFlashSaleRepository.save(ChiTietFlashSale.builder()
                .maFlashSaleId(fs.getId())
                .maBienTheId(bt.getId())
                .giaFlashSale(giaFlashSale)
                .soLuongMoBan(5)
                .soLuongDaBan(0)
                .build());

        GioHang gioHangEntity = gioHangService.layHoacTaoGioHang(null, null);
        Long testCartId = gioHangEntity.getId();

        // 3. Thêm 2 cái vào giỏ hàng
        ThemGioHangRequest themReq = new ThemGioHangRequest();
        themReq.setMaBienTheId(bt.getId());
        themReq.setSoLuong(2);

        gioHangService.themBienTheVaoGio(null, testCartId, themReq);

        // 4. Kiểm tra thông tin giỏ hàng
        GioHangResponse gioHang = gioHangService.layThongTinGioHang(null, testCartId);
        assertNotNull(gioHang);
        assertEquals(1, gioHang.getDanhSachItemLe().size());

        ItemGioHangDto item = gioHang.getDanhSachItemLe().get(0);
        assertEquals(2, item.getSoLuong());
        assertEquals(giaFlashSale, item.getDonGia(), "Đơn giá trong giỏ phải là giá Flash Sale");
        assertEquals(new BigDecimal("360000"), item.getThanhTien(), "Thành tiền phải tính theo giá Flash Sale");
        assertTrue(Boolean.TRUE.equals(item.getLaFlashSale()), "Mục hàng phải được đánh dấu là Flash Sale");
        assertEquals(new BigDecimal("300000"), item.getGiaBanGoc());
        assertEquals(40, item.getPhanTramGiamGia(), "Giảm giá phải là 40%");

        // 5. Kiểm tra giới hạn số lượng mở bán (so_luong_mo_ban = 5): Thêm quá số lượng còn lại sẽ báo lỗi
        ThemGioHangRequest vuotQuaReq = new ThemGioHangRequest();
        vuotQuaReq.setMaBienTheId(bt.getId());
        vuotQuaReq.setSoLuong(10); // Đã có 2, thêm 10 = 12 > 5

        assertThrows(IllegalArgumentException.class, () -> {
            gioHangService.themBienTheVaoGio(null, testCartId, vuotQuaReq);
        });

        // 6. Thực hiện Đặt hàng (Checkout)
        PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getKichHoat()))
                .findFirst()
                .orElseGet(() -> phuongThucThanhToanRepository.save(PhuongThucThanhToan.builder()
                        .maPhuongThuc("COD-FS-" + randomSuffix)
                        .tenPhuongThuc("Thanh toán khi nhận hàng")
                        .kichHoat(true)
                        .build()));

        DatHangRequest datHangReq = new DatHangRequest();
        datHangReq.setTenNguoiNhan("Nguyễn Văn Mua Flash Sale");
        datHangReq.setSoDienThoaiNhan("0912345678");
        datHangReq.setDiaChiGiaoHang("123 Đường Test, Quận 1, TP HCM");
        datHangReq.setPhuongThucThanhToanId(pttt.getId());

        DonHang donHang = donHangService.taoDonHangTuGioHang(null, testCartId, datHangReq);
        assertNotNull(donHang);
        assertNotNull(donHang.getMaDonHang());

        // 7. Xác nhận số lượng đã bán của Flash Sale đã được cập nhật tự động từ 0 lên 2
        List<ChiTietFlashSale> dsMoi = chiTietFlashSaleRepository.timChiTietFlashSaleDangApDung(bt.getId(), LocalDateTime.now());
        assertFalse(dsMoi.isEmpty());
        assertEquals(2, dsMoi.get(0).getSoLuongDaBan(), "Số lượng đã bán của Flash Sale phải tự động tăng lên 2 sau khi đặt hàng thành công");
    }
}
