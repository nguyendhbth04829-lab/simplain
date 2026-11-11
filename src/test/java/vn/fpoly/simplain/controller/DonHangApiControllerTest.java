package vn.fpoly.simplain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.dto.CapNhatTrangThaiVanDonRequest;
import vn.fpoly.simplain.dto.ChiTietDonHangItemRequest;
import vn.fpoly.simplain.dto.TaoDonHangMod4Request;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động REST API & Dịch vụ Tạo mới Đơn hàng và Trừ tồn kho (US21 - Mod 5).
 * - Tiêu chí 1: API nhận Data từ Dev 4 (Mod 4 - Checkout / Giỏ hàng) để Insert don_hang & chi_tiet_don_hang.
 * - Tiêu chí 2: Giảm so_luong trong kho thật (Concurrency-safe / Chống over-selling).
 * - Tiêu chí 3: Rollback toàn bộ khi hết hàng / thiếu tồn kho, không trừ dở dang.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Tích hợp Tạo mới Đơn hàng & Trừ tồn kho thật (US21)")
class DonHangApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;

    private BienTheSanPham bienTheTest;
    private PhuongThucThanhToan ptttCod;

    @BeforeEach
    void setUp() {
        // Đảm bảo có phương thức COD
        ptttCod = phuongThucThanhToanRepository.findByMaPhuongThucIgnoreCase("COD")
                .orElseGet(() -> phuongThucThanhToanRepository.save(PhuongThucThanhToan.builder()
                        .maPhuongThuc("COD")
                        .tenPhuongThuc("Thanh toán khi nhận hàng")
                        .laTrucTuyen(false)
                        .kichHoat(true)
                        .build()));

        // Đảm bảo có đủ 5 trạng thái chuẩn quy trình thương mại điện tử
        ensureTrangThai("CHO_XAC_NHAN", "Chờ xác nhận");
        ensureTrangThai("DANG_XU_LY", "Đang xử lý");
        ensureTrangThai("DANG_GIAO", "Đang giao hàng");
        ensureTrangThai("HOAN_THANH", "Hoàn thành");
        ensureTrangThai("DA_HUY", "Đã hủy");

        // Lấy hoặc tạo biến thể sản phẩm có số lượng tồn kho 50 cái
        bienTheTest = bienTheSanPhamRepository.findAll().stream()
                .filter(b -> b.getId() != null)
                .findFirst()
                .orElseGet(() -> bienTheSanPhamRepository.save(BienTheSanPham.builder()
                        .maSanPhamId(1L)
                        .maMauSacId(1L)
                        .maKichThuocId(1L)
                        .maSku("SKU-US21-" + System.currentTimeMillis())
                        .giaBan(new BigDecimal("180000"))
                        .giaGoc(new BigDecimal("200000"))
                        .soLuong(50)
                        .kichHoat(true)
                        .build()));

        // Set tồn kho ổn định cho biến thể này là 50
        bienTheTest.setSoLuong(50);
        bienTheSanPhamRepository.save(bienTheTest);
    }

    @Test
    @DisplayName("TC1: API nhận Data từ Dev 4 tạo đơn hàng thành công, trừ kho thật và lưu chi tiết đơn hàng")
    void testTaoDonHangThanhCong_VaGiamTonKho() throws Exception {
        int soLuongMua = 4;
        int tonKhoBanDau = bienTheTest.getSoLuong(); // 50

        ChiTietDonHangItemRequest item = ChiTietDonHangItemRequest.builder()
                .maBienTheId(bienTheTest.getId())
                .maSku(bienTheTest.getMaSku())
                .tenSanPham("Áo Thun Nam US21")
                .soLuong(soLuongMua)
                .donGia(new BigDecimal("150000"))
                .thanhTien(new BigDecimal("600000"))
                .build();

        TaoDonHangMod4Request request = TaoDonHangMod4Request.builder()
                .tenNguoiNhan("Nguyễn Hưng Dev 5")
                .soDienThoaiNhan("0912345678")
                .diaChiGiaoHang("123 Phố Huế, Phường Phạm Đình Hổ, Quận Hai Bà Trưng, Hà Nội")
                .ghiChu("Giao hàng giờ hành chính")
                .maPhuongThucId(ptttCod.getId())
                .tongTienHang(new BigDecimal("600000"))
                .phiVanChuyen(new BigDecimal("25000"))
                .tienGiamGia(new BigDecimal("10000"))
                .tienCoolcashDung(BigDecimal.ZERO)
                .tongThanhToan(new BigDecimal("615000"))
                .danhSachChiTiet(List.of(item))
                .build();

        String jsonBody = objectMapper.writeValueAsString(request);

        String responseJson = mockMvc.perform(post("/api/don-hang/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maDonHang", notNullValue()))
                .andExpect(jsonPath("$.maDonHang", startsWith("DH")))
                .andExpect(jsonPath("$.trangThai", notNullValue()))
                .andExpect(jsonPath("$.danhSachTonKhoCapNhat", hasSize(1)))
                .andExpect(jsonPath("$.danhSachTonKhoCapNhat[0].maBienTheId", is(bienTheTest.getId().intValue())))
                .andExpect(jsonPath("$.danhSachTonKhoCapNhat[0].soLuongDaTru", is(soLuongMua)))
                .andExpect(jsonPath("$.danhSachTonKhoCapNhat[0].soLuongConLai", is(tonKhoBanDau - soLuongMua)))
                .andReturn().getResponse().getContentAsString();

        // Kiểm tra cơ sở dữ liệu thật xem tồn kho đã bị trừ chưa
        BienTheSanPham bienTheSauKhiTru = bienTheSanPhamRepository.findById(bienTheTest.getId()).orElseThrow();
        assertEquals(tonKhoBanDau - soLuongMua, bienTheSauKhiTru.getSoLuong(),
                "Tồn kho thực tế trong DB phải giảm chính xác bằng số lượng mua");

        // Lấy mã đơn hàng và kiểm tra bảng chi_tiet_don_hang
        String maDonHang = objectMapper.readTree(responseJson).get("maDonHang").asText();
        DonHang donHangDb = donHangRepository.findByMaDonHang(maDonHang).orElseThrow();
        List<ChiTietDonHang> chiTiets = chiTietDonHangRepository.findByMaDonHangId(donHangDb.getId());
        assertFalse(chiTiets.isEmpty(), "Chi tiết đơn hàng phải được lưu trong bảng chi_tiet_don_hang");
        assertEquals(soLuongMua, chiTiets.get(0).getSoLuong());
    }

    @Test
    @DisplayName("TC2: API từ chối tạo đơn khi số lượng mua vượt quá tồn kho (Over-selling check) và rollback")
    void testTaoDonHangThatBai_KhiVuotTonKho() throws Exception {
        // Đặt số lượng tồn kho còn 3 cái
        bienTheTest.setSoLuong(3);
        bienTheSanPhamRepository.save(bienTheTest);

        // Đặt mua 10 cái (vượt tồn kho 3 cái)
        ChiTietDonHangItemRequest item = ChiTietDonHangItemRequest.builder()
                .maBienTheId(bienTheTest.getId())
                .maSku(bienTheTest.getMaSku())
                .tenSanPham("Sản phẩm hết hàng test")
                .soLuong(10)
                .donGia(new BigDecimal("150000"))
                .thanhTien(new BigDecimal("1500000"))
                .build();

        TaoDonHangMod4Request request = TaoDonHangMod4Request.builder()
                .tenNguoiNhan("Khách Hàng Mua Quá Kho")
                .soDienThoaiNhan("0988776655")
                .diaChiGiaoHang("Số 1 Đại Cồ Việt, Hà Nội")
                .maPhuongThucId(ptttCod.getId())
                .tongTienHang(new BigDecimal("1500000"))
                .phiVanChuyen(BigDecimal.ZERO)
                .tongThanhToan(new BigDecimal("1500000"))
                .danhSachChiTiet(List.of(item))
                .build();

        String jsonBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/don-hang/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("KHONG_DU_TON_KHO")))
                .andExpect(jsonPath("$.soLuongYeuCau", is(10)))
                .andExpect(jsonPath("$.soLuongHienCo", is(3)))
                .andExpect(jsonPath("$.message", notNullValue()));

        // Tồn kho không được thay đổi (giữ nguyên 3)
        BienTheSanPham bienTheSauThatBai = bienTheSanPhamRepository.findById(bienTheTest.getId()).orElseThrow();
        assertEquals(3, bienTheSauThatBai.getSoLuong(), "Tồn kho không được bị trừ khi giao dịch thất bại");
    }

    @Test
    @DisplayName("TC3: Tra cứu thông tin đơn hàng theo mã đơn qua API GET /api/don-hang/{maDonHang}")
    void testTraCuuDonHang() throws Exception {
        // Tạo trước 1 đơn hàng qua API
        ChiTietDonHangItemRequest item = ChiTietDonHangItemRequest.builder()
                .maBienTheId(bienTheTest.getId())
                .maSku(bienTheTest.getMaSku())
                .tenSanPham("Sản phẩm tra cứu")
                .soLuong(1)
                .donGia(new BigDecimal("100000"))
                .thanhTien(new BigDecimal("100000"))
                .build();

        TaoDonHangMod4Request request = TaoDonHangMod4Request.builder()
                .tenNguoiNhan("Lê Tra Cứu")
                .soDienThoaiNhan("0911223344")
                .diaChiGiaoHang("456 Cầu Giấy, Hà Nội")
                .maPhuongThucId(ptttCod.getId())
                .tongTienHang(new BigDecimal("100000"))
                .phiVanChuyen(BigDecimal.ZERO)
                .tongThanhToan(new BigDecimal("100000"))
                .danhSachChiTiet(List.of(item))
                .build();

        String responseStr = mockMvc.perform(post("/api/don-hang/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String maDonHang = objectMapper.readTree(responseStr).get("maDonHang").asText();

        // Tra cứu lại
        mockMvc.perform(get("/api/don-hang/" + maDonHang))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donHang.maDonHang", is(maDonHang)))
                .andExpect(jsonPath("$.donHang.tenNguoiNhan", is("Lê Tra Cứu")))
                .andExpect(jsonPath("$.danhSachChiTiet", hasSize(1)));
    }

    @Test
    @DisplayName("TC4: Giao diện Quản lý Đơn hàng Admin hiển thị danh sách thành công")
    void testAdminHienThiDanhSachDonHang() throws Exception {
        mockMvc.perform(get("/admin/don-hang/hien-thi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/don-hang/hien-thi"))
                .andExpect(model().attributeExists("trangDonHang"))
                .andExpect(model().attributeExists("tongDonHang"));
    }

    @Test
    @DisplayName("TC5 (US22): Cập nhật trạng thái vận đơn sang DANG_GIAO kèm mã vận đơn và ghi log lịch sử")
    void testCapNhatTrangThaiVanDon_VaGhiLogLichSu() throws Exception {
        // 1. Tạo đơn hàng mới
        ChiTietDonHangItemRequest item = ChiTietDonHangItemRequest.builder()
                .maBienTheId(bienTheTest.getId())
                .maSku(bienTheTest.getMaSku())
                .tenSanPham("Sản phẩm US22 Vận Đơn")
                .soLuong(2)
                .donGia(new BigDecimal("150000"))
                .thanhTien(new BigDecimal("300000"))
                .build();

        TaoDonHangMod4Request taoReq = TaoDonHangMod4Request.builder()
                .tenNguoiNhan("Trần Vận Đơn")
                .soDienThoaiNhan("0988776655")
                .diaChiGiaoHang("789 Hoàng Hoa Thám, Hà Nội")
                .maPhuongThucId(ptttCod.getId())
                .tongTienHang(new BigDecimal("300000"))
                .phiVanChuyen(BigDecimal.ZERO)
                .tongThanhToan(new BigDecimal("300000"))
                .danhSachChiTiet(List.of(item))
                .build();

        String resStr = mockMvc.perform(post("/api/don-hang/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taoReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long donHangId = objectMapper.readTree(resStr).get("donHangId").asLong();

        // Kiểm tra log khởi tạo đã có trong lich_su_trang_thai_don_hang
        List<LichSuTrangThaiDonHang> dsLichSuBanDau = lichSuTrangThaiDonHangRepository.findByDonHangIdOrderByNgayChuyenAsc(donHangId);
        assertFalse(dsLichSuBanDau.isEmpty(), "Khi tạo đơn hàng phải có mốc log khởi tạo");
        assertEquals("CHO_XAC_NHAN", dsLichSuBanDau.get(0).getTrangThaiMoi().getMaTrangThai());

        // 2. Admin cập nhật trạng thái vận đơn sang DANG_GIAO kèm mã vận đơn
        CapNhatTrangThaiVanDonRequest updateReq = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("DANG_GIAO")
                .maVanDon("GHN-HN-998877")
                .ghiChu("Đã giao bưu tá GHN lấy hàng tại kho")
                .build();

        mockMvc.perform(put("/api/don-hang/" + donHangId + "/trang-thai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.maTrangThai", is("DANG_GIAO")))
                .andExpect(jsonPath("$.maVanDon", is("GHN-HN-998877")));

        // 3. Kiểm tra DonHang trong DB đã có mã vận đơn và trạng thái mới
        DonHang donHangDb = donHangRepository.findById(donHangId).orElseThrow();
        assertEquals("DANG_GIAO", donHangDb.getTrangThaiDonHang().getMaTrangThai());
        assertEquals("GHN-HN-998877", donHangDb.getMaVanDon());

        // 4. Kiểm tra log lịch sử được bổ sung
        List<LichSuTrangThaiDonHang> dsLichSuSau = lichSuTrangThaiDonHangRepository.findByDonHangIdOrderByNgayChuyenAsc(donHangId);
        assertEquals(2, dsLichSuSau.size(), "Phải có đúng 2 mốc lịch sử (Khởi tạo + Chuyển Đang giao)");
        assertEquals("DANG_GIAO", dsLichSuSau.get(1).getTrangThaiMoi().getMaTrangThai());
        assertTrue(dsLichSuSau.get(1).getGhiChu().contains("bưu tá GHN"));

        // 5. Kiểm tra API Tra cứu lịch sử /api/don-hang/{id}/lich-su
        mockMvc.perform(get("/api/don-hang/" + donHangId + "/lich-su"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.tongSoMocLichSu", is(2)))
                .andExpect(jsonPath("$.danhSachLichSu", hasSize(2)))
                .andExpect(jsonPath("$.danhSachLichSu[1].maTrangThai", is("DANG_GIAO")));
    }

    @Test
    @DisplayName("TC6 (US22): Hủy đơn hàng tự động hoàn trả số lượng tồn kho thật và ghi log lịch sử")
    void testCapNhatTrangThai_HuyDonHang_HoanTraTonKho() throws Exception {
        // Tồn kho ban đầu đang là 50 (từ setUp)
        int tonKhoTruoc = bienTheTest.getSoLuong();

        // 1. Tạo đơn hàng 4 sản phẩm -> Tồn kho giảm từ 50 xuống 46
        ChiTietDonHangItemRequest item = ChiTietDonHangItemRequest.builder()
                .maBienTheId(bienTheTest.getId())
                .maSku(bienTheTest.getMaSku())
                .tenSanPham("Sản phẩm hoàn kho")
                .soLuong(4)
                .donGia(new BigDecimal("100000"))
                .thanhTien(new BigDecimal("400000"))
                .build();

        TaoDonHangMod4Request taoReq = TaoDonHangMod4Request.builder()
                .tenNguoiNhan("Phạm Hủy Đơn")
                .soDienThoaiNhan("0912345678")
                .diaChiGiaoHang("123 Phố Huế, Hà Nội")
                .maPhuongThucId(ptttCod.getId())
                .tongTienHang(new BigDecimal("400000"))
                .phiVanChuyen(BigDecimal.ZERO)
                .tongThanhToan(new BigDecimal("400000"))
                .danhSachChiTiet(List.of(item))
                .build();

        String resStr = mockMvc.perform(post("/api/don-hang/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taoReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long donHangId = objectMapper.readTree(resStr).get("donHangId").asLong();

        // Kiểm tra tồn kho sau khi tạo đơn
        BienTheSanPham btSauTao = bienTheSanPhamRepository.findById(bienTheTest.getId()).orElseThrow();
        assertEquals(tonKhoTruoc - 4, btSauTao.getSoLuong(), "Tồn kho phải giảm 4 sau khi tạo đơn");

        // 2. Chuyển trạng thái sang DA_HUY
        CapNhatTrangThaiVanDonRequest huyReq = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("DA_HUY")
                .ghiChu("Khách hàng báo hủy vì đổi ý")
                .build();

        mockMvc.perform(put("/api/don-hang/" + donHangId + "/trang-thai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(huyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maTrangThai", is("DA_HUY")));

        // 3. Kiểm tra tồn kho đã được hoàn lại đầy đủ về mức ban đầu
        BienTheSanPham btSauHuy = bienTheSanPhamRepository.findById(bienTheTest.getId()).orElseThrow();
        assertEquals(tonKhoTruoc, btSauHuy.getSoLuong(), "Tồn kho phải được tự động hoàn trả 4 sản phẩm khi hủy đơn");

        // 4. Kiểm tra log lịch sử có ghi nhận lý do hủy
        List<LichSuTrangThaiDonHang> dsLog = lichSuTrangThaiDonHangRepository.findByDonHangIdOrderByNgayChuyenAsc(donHangId);
        LichSuTrangThaiDonHang logHuy = dsLog.get(dsLog.size() - 1);
        assertEquals("DA_HUY", logHuy.getTrangThaiMoi().getMaTrangThai());
        assertTrue(logHuy.getGhiChu().contains("Khách hàng báo hủy"));
    }

    @Test
    @DisplayName("TC7 (US22): Validate quy trình Shopee - Chặn chuyển trạng thái tùy ý, sai thứ tự và bảo vệ đơn kết thúc")
    void testQuyTrinhChuyenDoiTrangThaiShopee_ChanChuyenDoiBatHopLe() throws Exception {
        // 1. Tạo đơn hàng mới ở trạng thái CHO_XAC_NHAN
        ChiTietDonHangItemRequest item = ChiTietDonHangItemRequest.builder()
                .maBienTheId(bienTheTest.getId())
                .maSku(bienTheTest.getMaSku())
                .tenSanPham("Sản phẩm test quy trình")
                .soLuong(1)
                .donGia(new BigDecimal("100000"))
                .thanhTien(new BigDecimal("100000"))
                .build();

        TaoDonHangMod4Request taoReq = TaoDonHangMod4Request.builder()
                .tenNguoiNhan("Lê Shopee Flow")
                .soDienThoaiNhan("0988776655")
                .diaChiGiaoHang("456 Cầu Giấy, Hà Nội")
                .maPhuongThucId(ptttCod.getId())
                .tongTienHang(new BigDecimal("100000"))
                .phiVanChuyen(BigDecimal.ZERO)
                .tongThanhToan(new BigDecimal("100000"))
                .danhSachChiTiet(List.of(item))
                .build();

        String resStr = mockMvc.perform(post("/api/don-hang/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taoReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long donHangId = objectMapper.readTree(resStr).get("donHangId").asLong();

        // 2. Chặn: CHO_XAC_NHAN không được nhảy cóc lên HOAN_THANH
        CapNhatTrangThaiVanDonRequest reqNhayCoc = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("HOAN_THANH")
                .ghiChu("Cố tình nhảy cóc lên Hoàn thành")
                .build();

        mockMvc.perform(put("/api/don-hang/" + donHangId + "/trang-thai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqNhayCoc)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error", is("QUY_TRINH_KHONG_HOP_LE")));

        // 3. Hợp lệ: CHO_XAC_NHAN -> DANG_XU_LY
        CapNhatTrangThaiVanDonRequest reqXuLy = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("DANG_XU_LY")
                .ghiChu("Xác nhận & Chuẩn bị đơn hàng")
                .build();

        mockMvc.perform(put("/api/don-hang/" + donHangId + "/trang-thai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqXuLy)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maTrangThai", is("DANG_XU_LY")));

        // 4. Chặn: DANG_XU_LY không được quay ngược về CHO_XAC_NHAN
        CapNhatTrangThaiVanDonRequest reqQuayLui = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("CHO_XAC_NHAN")
                .ghiChu("Cố tình quay lùi về Chờ xác nhận")
                .build();

        mockMvc.perform(put("/api/don-hang/" + donHangId + "/trang-thai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqQuayLui)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error", is("QUY_TRINH_KHONG_HOP_LE")));

        // 5. Hợp lệ: DANG_XU_LY -> DANG_GIAO (kèm mã vận đơn)
        CapNhatTrangThaiVanDonRequest reqGiao = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("DANG_GIAO")
                .maVanDon("GHN-VALIDATE-123")
                .ghiChu("Bàn giao bưu tá")
                .build();

        mockMvc.perform(put("/api/don-hang/" + donHangId + "/trang-thai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqGiao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maTrangThai", is("DANG_GIAO")));

        // 6. Chặn: DANG_GIAO không được quay ngược về DANG_XU_LY
        mockMvc.perform(put("/api/don-hang/" + donHangId + "/trang-thai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqXuLy)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error", is("QUY_TRINH_KHONG_HOP_LE")));

        // 7. Hợp lệ: DANG_GIAO -> HOAN_THANH
        CapNhatTrangThaiVanDonRequest reqHoanThanh = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("HOAN_THANH")
                .ghiChu("Khách đã nhận hàng thành công")
                .build();

        mockMvc.perform(put("/api/don-hang/" + donHangId + "/trang-thai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqHoanThanh)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maTrangThai", is("HOAN_THANH")));

        // 8. Chặn tuyệt đối: HOAN_THANH là trạng thái kết thúc, bị khóa, không được đổi sang bất kỳ trạng thái nào khác (kể cả Hủy)
        CapNhatTrangThaiVanDonRequest reqHuySauHoanThanh = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("DA_HUY")
                .ghiChu("Cố tình hủy khi đã hoàn thành")
                .build();

        mockMvc.perform(put("/api/don-hang/" + donHangId + "/trang-thai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqHuySauHoanThanh)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error", is("QUY_TRINH_KHONG_HOP_LE")));
    }

    private void ensureTrangThai(String ma, String ten) {
        trangThaiDonHangRepository.findByMaTrangThai(ma)
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai(ma)
                        .tenTrangThai(ten)
                        .build()));
    }
}
