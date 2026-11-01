package vn.fpoly.simplain.controller;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử Web MVC tích hợp chức năng Admin Thiết Lập Khung Giờ Flash Sale (US29 - Mod 6 / Bán hàng - Khoa).
 * - Tiêu chí 1: Đặt khung giờ Bắt đầu / Kết thúc.
 * - Tiêu chí 2: Gán giá sốc và giới hạn số lượng (so_luong_mo_ban) cho biến thể.
 * - Validation:
 *   + Validate bắt buộc tiêu đề, thời gian bắt đầu, thời gian kết thúc, biến thể.
 *   + Validate thời gian kết thúc > thời gian bắt đầu.
 *   + Validate chống chồng chéo khung giờ Flash Sale.
 *   + Validate giá sốc Flash Sale phải nhỏ hơn giá bán thường.
 *   + Validate số lượng mở bán không vượt quá tồn kho khả dụng hiện tại.
 *   + Validate không trùng lặp biến thể trong cùng một đợt Flash Sale.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Kiểm thử Web MVC Admin Thiết Lập Khung Giờ Flash Sale (US29)")
class QuanLyFlashSaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Autowired
    private ChiTietFlashSaleRepository chiTietFlashSaleRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichThuocRepository kichThuocRepository;

    @Autowired
    private EntityManager entityManager;

    private FlashSale flashSaleMau;
    private BienTheSanPham bienTheTest1;
    private BienTheSanPham bienTheTest2;

    @BeforeEach
    void setUp() {
        // Đảm bảo có màu sắc & kích thước
        MauSac mauSac = mauSacRepository.findAll().stream().findFirst().orElseGet(() -> {
            MauSac ms = MauSac.builder().maMau("#000000").tenMau("Đen").kichHoat(true).build();
            return mauSacRepository.saveAndFlush(ms);
        });

        KichThuoc kichThuoc = kichThuocRepository.findAll().stream().findFirst().orElseGet(() -> {
            KichThuoc kt = KichThuoc.builder().maKichThuoc("L").tenKichThuoc("Size L").kichHoat(true).build();
            return kichThuocRepository.saveAndFlush(kt);
        });

        // Đảm bảo có sản phẩm mẫu
        SanPham sp = sanPhamRepository.findAll().stream().findFirst().orElseGet(() -> {
            SanPham s = SanPham.builder()
                    .maSanPham("SP_FS_TEST")
                    .tenSanPham("Áo Thun Flash Sale Test")
                    .duongDanSlug("ao-thun-flash-sale-test")
                    .giaCoBan(BigDecimal.valueOf(250000))
                    .kichHoat(true)
                    .build();
            return sanPhamRepository.saveAndFlush(s);
        });

        // Đảm bảo có ít nhất 2 biến thể SKU với tồn kho và giá bán xác định
        List<BienTheSanPham> dsBt = bienTheRepository.findAll();
        if (dsBt.size() < 2) {
            bienTheTest1 = BienTheSanPham.builder()
                    .maSanPhamId(sp.getId())
                    .maMauSacId(mauSac.getId())
                    .maKichThuocId(kichThuoc.getId())
                    .maSku("SKU-FS-TEST-01")
                    .giaBan(BigDecimal.valueOf(250000))
                    .giaGoc(BigDecimal.valueOf(300000))
                    .soLuong(50)
                    .kichHoat(true)
                    .build();
            bienTheTest1 = bienTheRepository.saveAndFlush(bienTheTest1);

            bienTheTest2 = BienTheSanPham.builder()
                    .maSanPhamId(sp.getId())
                    .maMauSacId(mauSac.getId())
                    .maKichThuocId(kichThuoc.getId())
                    .maSku("SKU-FS-TEST-02")
                    .giaBan(BigDecimal.valueOf(200000))
                    .giaGoc(BigDecimal.valueOf(250000))
                    .soLuong(40)
                    .kichHoat(true)
                    .build();
            bienTheTest2 = bienTheRepository.saveAndFlush(bienTheTest2);
        } else {
            bienTheTest1 = dsBt.get(0);
            bienTheTest1.setKichHoat(true);
            bienTheTest1.setGiaBan(BigDecimal.valueOf(250000));
            bienTheTest1.setSoLuong(50);
            bienTheRepository.saveAndFlush(bienTheTest1);

            bienTheTest2 = dsBt.get(1);
            bienTheTest2.setKichHoat(true);
            bienTheTest2.setGiaBan(BigDecimal.valueOf(200000));
            bienTheTest2.setSoLuong(40);
            bienTheRepository.saveAndFlush(bienTheTest2);
        }

        // Tạo sẵn một đợt Flash Sale mẫu để kiểm thử
        LocalDateTime startMau = LocalDateTime.of(2026, 11, 1, 12, 0);
        LocalDateTime endMau = LocalDateTime.of(2026, 11, 1, 14, 0);

        flashSaleMau = FlashSale.builder()
                .tieuDe("Deal Sốc Giữa Trưa Kiểm Thử")
                .thoiGianBatDau(startMau)
                .thoiGianKetThuc(endMau)
                .kichHoat(true)
                .build();
        flashSaleMau = flashSaleRepository.saveAndFlush(flashSaleMau);

        ChiTietFlashSale ctMau = ChiTietFlashSale.builder()
                .maFlashSaleId(flashSaleMau.getId())
                .maBienTheId(bienTheTest1.getId())
                .giaFlashSale(BigDecimal.valueOf(149000))
                .soLuongMoBan(20)
                .soLuongDaBan(0)
                .build();
        chiTietFlashSaleRepository.saveAndFlush(ctMau);

        entityManager.clear();
    }

    @Test
    @DisplayName("TC1: Hiển thị màn hình danh sách khung giờ Flash Sale thành công")
    void testHienThiDanhSachFlashSale_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/flash-sale/hien-thi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/flash-sale/hien-thi"))
                .andExpect(model().attributeExists("trangFlashSale"))
                .andExpect(model().attributeExists("tuKhoa"))
                .andExpect(model().attributeExists("trangThaiHienTai"))
                .andExpect(model().attribute("menuActive", "flash-sale"));
    }

    @Test
    @DisplayName("TC2: Tìm kiếm và lọc danh sách Flash Sale theo từ khóa và trạng thái")
    void testTimKiemFlashSale_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/flash-sale/hien-thi")
                        .param("tuKhoa", "Giữa Trưa")
                        .param("trangThai", "DANG_HOAT_DONG"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/flash-sale/hien-thi"))
                .andExpect(model().attributeExists("trangFlashSale"));
    }

    @Test
    @DisplayName("TC3: Hiển thị form tạo mới khung giờ Flash Sale kèm danh sách SKU khả dụng")
    void testHienThiFormTaoMoi_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/flash-sale/tao-moi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/flash-sale/tao-moi"))
                .andExpect(model().attributeExists("req"))
                .andExpect(model().attributeExists("danhSachBienTheKhaDung"))
                .andExpect(model().attribute("isEditMode", false));
    }

    @Test
    @DisplayName("TC4: Admin tạo mới đợt Flash Sale và gán biến thể giá sốc thành công")
    void testTaoMoiFlashSale_ThanhCong() throws Exception {
        mockMvc.perform(post("/admin/flash-sale/tao-moi")
                        .param("tieuDe", "Flash Sale 20h Tối Giá Sốc")
                        .param("thoiGianBatDau", "2026-11-05T20:00")
                        .param("thoiGianKetThuc", "2026-11-05T22:00")
                        .param("kichHoat", "true")
                        .param("danhSachChiTiet[0].maBienTheId", String.valueOf(bienTheTest1.getId()))
                        .param("danhSachChiTiet[0].giaFlashSale", "129000") // < 250000 (giá bán)
                        .param("danhSachChiTiet[0].soLuongMoBan", "15")   // <= 50 (tồn kho)
                        .param("danhSachChiTiet[1].maBienTheId", String.valueOf(bienTheTest2.getId()))
                        .param("danhSachChiTiet[1].giaFlashSale", "99000")  // < 200000 (giá bán)
                        .param("danhSachChiTiet[1].soLuongMoBan", "10"))  // <= 40 (tồn kho)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/flash-sale/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        // Kiểm tra database lưu đúng đợt Flash Sale và các biến thể chi tiết
        List<FlashSale> all = flashSaleRepository.findAll();
        FlashSale fsMoi = all.stream()
                .filter(f -> "Flash Sale 20h Tối Giá Sốc".equals(f.getTieuDe()))
                .findFirst()
                .orElse(null);

        assertNotNull(fsMoi, "Flash Sale mới phải được lưu thành công");
        assertEquals(LocalDateTime.of(2026, 11, 5, 20, 0), fsMoi.getThoiGianBatDau());
        assertEquals(LocalDateTime.of(2026, 11, 5, 22, 0), fsMoi.getThoiGianKetThuc());
        assertTrue(fsMoi.getKichHoat());

        long count = chiTietFlashSaleRepository.countByMaFlashSaleId(fsMoi.getId());
        assertEquals(2, count, "Đợt Flash Sale phải có đúng 2 biến thể được gán giá sốc");
        assertTrue(chiTietFlashSaleRepository.existsByMaFlashSaleIdAndMaBienTheId(fsMoi.getId(), bienTheTest1.getId()));
        assertTrue(chiTietFlashSaleRepository.existsByMaFlashSaleIdAndMaBienTheId(fsMoi.getId(), bienTheTest2.getId()));
    }

    @Test
    @DisplayName("TC5: Validate bắt buộc khi để trống các trường (tiêu đề, thời gian bắt đầu, kết thúc, biến thể)")
    void testTaoMoiFlashSale_LoiValidate_DeTrongCacTruongBatBuoc() throws Exception {
        mockMvc.perform(post("/admin/flash-sale/tao-moi")
                        .param("tieuDe", "")
                        .param("thoiGianBatDau", "")
                        .param("thoiGianKetThuc", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/flash-sale/tao-moi"))
                .andExpect(model().attributeHasFieldErrors("req", "tieuDe"))
                .andExpect(model().attributeHasFieldErrors("req", "thoiGianBatDau"))
                .andExpect(model().attributeHasFieldErrors("req", "thoiGianKetThuc"))
                .andExpect(model().attributeHasFieldErrors("req", "danhSachChiTiet"));
    }

    @Test
    @DisplayName("TC6: Validate lỗi nghiệp vụ khi thời gian kết thúc trước thời gian bắt đầu")
    void testTaoMoiFlashSale_LoiValidate_ThoiGianKetThucTruocBatDau() throws Exception {
        mockMvc.perform(post("/admin/flash-sale/tao-moi")
                        .param("tieuDe", "Flash Sale Lỗi Thời Gian")
                        .param("thoiGianBatDau", "2026-11-10T15:00")
                        .param("thoiGianKetThuc", "2026-11-10T12:00") // Kết thúc trước bắt đầu
                        .param("kichHoat", "true")
                        .param("danhSachChiTiet[0].maBienTheId", String.valueOf(bienTheTest1.getId()))
                        .param("danhSachChiTiet[0].giaFlashSale", "150000")
                        .param("danhSachChiTiet[0].soLuongMoBan", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/flash-sale/tao-moi"))
                .andExpect(model().attributeExists("thongBaoLoi"));
    }

    @Test
    @DisplayName("TC7: Validate lỗi nghiệp vụ chống chồng chéo khung giờ Flash Sale")
    void testTaoMoiFlashSale_LoiValidate_ChongCheoKhungGio() throws Exception {
        // Trùng khung giờ với flashSaleMau (2026-11-01 12:00 ~ 14:00)
        mockMvc.perform(post("/admin/flash-sale/tao-moi")
                        .param("tieuDe", "Flash Sale Bị Trùng Giờ")
                        .param("thoiGianBatDau", "2026-11-01T13:00") // Bắt đầu ở giữa khung giờ cũ
                        .param("thoiGianKetThuc", "2026-11-01T15:00")
                        .param("kichHoat", "true")
                        .param("danhSachChiTiet[0].maBienTheId", String.valueOf(bienTheTest1.getId()))
                        .param("danhSachChiTiet[0].giaFlashSale", "150000")
                        .param("danhSachChiTiet[0].soLuongMoBan", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/flash-sale/tao-moi"))
                .andExpect(model().attributeExists("thongBaoLoi"));
    }

    @Test
    @DisplayName("TC8: Validate giá sốc Flash Sale phải nhỏ hơn giá bán thường của biến thể")
    void testTaoMoiFlashSale_LoiValidate_GiaFlashSaleLonHonHoacBangGiaBan() throws Exception {
        // bienTheTest1 có giá bán là 250.000 đ -> Đặt giá Flash Sale 260.000 đ phải báo lỗi
        mockMvc.perform(post("/admin/flash-sale/tao-moi")
                        .param("tieuDe", "Flash Sale Lỗi Giá Không Sốc")
                        .param("thoiGianBatDau", "2026-11-12T10:00")
                        .param("thoiGianKetThuc", "2026-11-12T12:00")
                        .param("kichHoat", "true")
                        .param("danhSachChiTiet[0].maBienTheId", String.valueOf(bienTheTest1.getId()))
                        .param("danhSachChiTiet[0].giaFlashSale", "260000") // Lớn hơn giá bán 250k
                        .param("danhSachChiTiet[0].soLuongMoBan", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/flash-sale/tao-moi"))
                .andExpect(model().attributeExists("thongBaoLoi"));
    }

    @Test
    @DisplayName("TC9: Validate số lượng mở bán không vượt quá tồn kho khả dụng hiện tại")
    void testTaoMoiFlashSale_LoiValidate_SoLuongMoBanVuotQuaTonKho() throws Exception {
        // bienTheTest1 có tồn kho là 50 -> Mở bán 100 sản phẩm phải báo lỗi
        mockMvc.perform(post("/admin/flash-sale/tao-moi")
                        .param("tieuDe", "Flash Sale Lỗi Vượt Tồn Kho")
                        .param("thoiGianBatDau", "2026-11-14T10:00")
                        .param("thoiGianKetThuc", "2026-11-14T12:00")
                        .param("kichHoat", "true")
                        .param("danhSachChiTiet[0].maBienTheId", String.valueOf(bienTheTest1.getId()))
                        .param("danhSachChiTiet[0].giaFlashSale", "150000")
                        .param("danhSachChiTiet[0].soLuongMoBan", "100")) // Tồn kho chỉ có 50
                .andExpect(status().isOk())
                .andExpect(view().name("admin/flash-sale/tao-moi"))
                .andExpect(model().attributeExists("thongBaoLoi"));
    }

    @Test
    @DisplayName("TC10: Validate không cho phép thêm trùng biến thể trong cùng một đợt Flash Sale")
    void testTaoMoiFlashSale_LoiValidate_TrungBienTheTrongCungMotDot() throws Exception {
        mockMvc.perform(post("/admin/flash-sale/tao-moi")
                        .param("tieuDe", "Flash Sale Lỗi Trùng SKU")
                        .param("thoiGianBatDau", "2026-11-16T10:00")
                        .param("thoiGianKetThuc", "2026-11-16T12:00")
                        .param("kichHoat", "true")
                        .param("danhSachChiTiet[0].maBienTheId", String.valueOf(bienTheTest1.getId()))
                        .param("danhSachChiTiet[0].giaFlashSale", "150000")
                        .param("danhSachChiTiet[0].soLuongMoBan", "5")
                        .param("danhSachChiTiet[1].maBienTheId", String.valueOf(bienTheTest1.getId())) // Lặp lại bienTheTest1
                        .param("danhSachChiTiet[1].giaFlashSale", "140000")
                        .param("danhSachChiTiet[1].soLuongMoBan", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/flash-sale/tao-moi"))
                .andExpect(model().attributeExists("thongBaoLoi"));
    }

    @Test
    @DisplayName("TC11: Hiển thị form chỉnh sửa đợt Flash Sale và nạp danh sách biến thể")
    void testHienThiFormChinhSua_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/flash-sale/chinh-sua/" + flashSaleMau.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/flash-sale/tao-moi"))
                .andExpect(model().attribute("isEditMode", true))
                .andExpect(model().attributeExists("req"))
                .andExpect(model().attributeExists("danhSachBienTheKhaDung"));
    }

    @Test
    @DisplayName("TC12: Admin cập nhật khung giờ và gán lại biến thể Flash Sale thành công")
    void testCapNhatFlashSale_ThanhCong() throws Exception {
        mockMvc.perform(post("/admin/flash-sale/chinh-sua/" + flashSaleMau.getId())
                        .param("tieuDe", "Deal Sốc Giữa Trưa Đã Cập Nhật")
                        .param("thoiGianBatDau", "2026-11-01T12:30")
                        .param("thoiGianKetThuc", "2026-11-01T14:30")
                        .param("kichHoat", "true")
                        .param("danhSachChiTiet[0].maBienTheId", String.valueOf(bienTheTest2.getId()))
                        .param("danhSachChiTiet[0].giaFlashSale", "109000")
                        .param("danhSachChiTiet[0].soLuongMoBan", "8"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/flash-sale/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.flush();
        entityManager.clear();

        FlashSale capNhat = flashSaleRepository.findById(flashSaleMau.getId()).orElse(null);
        assertNotNull(capNhat);
        assertEquals("Deal Sốc Giữa Trưa Đã Cập Nhật", capNhat.getTieuDe());

        // Kiểm tra biến thể mới bienTheTest2 đã được gán
        assertTrue(chiTietFlashSaleRepository.existsByMaFlashSaleIdAndMaBienTheId(capNhat.getId(), bienTheTest2.getId()));
    }

    @Test
    @DisplayName("TC13: Toggle bật / tắt trạng thái kích hoạt của đợt Flash Sale")
    void testToggleKichHoat_ThanhCong() throws Exception {
        boolean trangThaiCu = flashSaleMau.getKichHoat();

        mockMvc.perform(post("/admin/flash-sale/toggle/" + flashSaleMau.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/flash-sale/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.flush();
        entityManager.clear();

        FlashSale sauToggle = flashSaleRepository.findById(flashSaleMau.getId()).orElse(null);
        assertNotNull(sauToggle);
        assertEquals(!trangThaiCu, sauToggle.getKichHoat());
    }

    @Test
    @DisplayName("TC14: Xóa đợt Flash Sale và dọn sạch bảng chi_tiet_flash_sale")
    void testXoaFlashSale_ThanhCong() throws Exception {
        FlashSale fsXoa = FlashSale.builder()
                .tieuDe("Flash Sale Để Xóa")
                .thoiGianBatDau(LocalDateTime.of(2026, 12, 1, 10, 0))
                .thoiGianKetThuc(LocalDateTime.of(2026, 12, 1, 12, 0))
                .kichHoat(false)
                .build();
        fsXoa = flashSaleRepository.saveAndFlush(fsXoa);

        ChiTietFlashSale ctXoa = ChiTietFlashSale.builder()
                .maFlashSaleId(fsXoa.getId())
                .maBienTheId(bienTheTest1.getId())
                .giaFlashSale(BigDecimal.valueOf(100000))
                .soLuongMoBan(5)
                .soLuongDaBan(0)
                .build();
        chiTietFlashSaleRepository.saveAndFlush(ctXoa);

        Long fsXoaId = fsXoa.getId();

        mockMvc.perform(post("/admin/flash-sale/xoa/" + fsXoaId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/flash-sale/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.flush();
        entityManager.clear();

        assertFalse(flashSaleRepository.existsById(fsXoaId), "Flash sale phải bị xóa khỏi CSDL");
        assertEquals(0, chiTietFlashSaleRepository.countByMaFlashSaleId(fsXoaId), "Chi tiết flash sale phải được dọn sạch sẽ");
    }

    @Test
    @DisplayName("TC15: REST API lấy thông tin chi tiết một biến thể SKU phục vụ AJAX")
    void testApiLayThongTinBienThe_ThanhCong() throws Exception {
        mockMvc.perform(get("/admin/flash-sale/api/bien-the/" + bienTheTest1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maBienTheId", is(bienTheTest1.getId().intValue())))
                .andExpect(jsonPath("$.maSku", is(bienTheTest1.getMaSku())))
                .andExpect(jsonPath("$.tonKhoHienTai", is(50)))
                .andExpect(jsonPath("$.giaBanGoc", is(250000.0)));
    }
}
