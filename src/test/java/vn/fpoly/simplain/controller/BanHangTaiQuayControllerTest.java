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
import vn.fpoly.simplain.dto.*;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động tích hợp cho Chức năng Bán hàng trực tiếp tại quầy (POS).
 * 1. Khởi tạo và quản lý đa tab Hóa đơn chờ (Multi-tab POS bills).
 * 2. Thêm mặt hàng vào giỏ hàng tại quầy và cập nhật số lượng.
 * 3. Tìm kiếm biến thể sản phẩm theo SKU / Barcode.
 * 4. Thanh toán tại quầy: trừ tồn kho thật, hoàn thành đơn ngay lập tức,
 *    ghi log trạng thái, lưu giao dịch thanh toán và tính toán tiền thừa trả khách.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Tích hợp Bán hàng trực tiếp tại quầy (POS)")
class BanHangTaiQuayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private HoaDonChoRepository hoaDonChoRepository;

    @Autowired
    private ChiTietHoaDonChoRepository chiTietHoaDonChoRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private HoSoHoiVienRepository hoSoHoiVienRepository;

    private BienTheSanPham bienTheTest;
    private PhuongThucThanhToan ptttTienMat;

    @BeforeEach
    void setUp() {
        // Đảm bảo có phương thức thanh toán TIEN_MAT
        ptttTienMat = phuongThucThanhToanRepository.findByMaPhuongThucIgnoreCase("TIEN_MAT")
                .orElseGet(() -> phuongThucThanhToanRepository.save(PhuongThucThanhToan.builder()
                        .maPhuongThuc("TIEN_MAT")
                        .tenPhuongThuc("Tiền mặt tại quầy")
                        .laTrucTuyen(false)
                        .kichHoat(true)
                        .build()));

        // Đảm bảo có trạng thái HOAN_THANH
        trangThaiDonHangRepository.findByMaTrangThai("HOAN_THANH")
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("HOAN_THANH")
                        .tenTrangThai("Hoàn thành")
                        .build()));

        // Chuẩn bị biến thể test có tồn kho 50 cái
        bienTheTest = bienTheSanPhamRepository.findAll().stream()
                .filter(b -> b.getId() != null)
                .findFirst()
                .orElseGet(() -> bienTheSanPhamRepository.save(BienTheSanPham.builder()
                        .maSanPhamId(1L)
                        .maMauSacId(1L)
                        .maKichThuocId(1L)
                        .maSku("SKU-POS-" + System.currentTimeMillis())
                        .giaBan(new BigDecimal("250000"))
                        .giaGoc(new BigDecimal("300000"))
                        .soLuong(50)
                        .kichHoat(true)
                        .build()));

        bienTheTest.setSoLuong(50);
        bienTheTest.setGiaBan(new BigDecimal("250000"));
        bienTheSanPhamRepository.save(bienTheTest);
    }

    @Test
    @DisplayName("TC1: Hiển thị giao diện màn hình bán hàng tại quầy (POS)")
    void testHienThiManHinhPos() throws Exception {
        mockMvc.perform(get("/admin/ban-hang-tai-quay"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/ban-hang-tai-quay/index"))
                .andExpect(model().attributeExists("dsHoaDonCho", "dsPttt", "menuActive"));
    }

    @Test
    @DisplayName("TC2: API lấy danh sách hóa đơn chờ tự động tạo tab đầu tiên nếu chưa có")
    void testLayDanhSachHoaDonCho_TuDongTaoTab() throws Exception {
        mockMvc.perform(get("/admin/ban-hang-tai-quay/api/hoa-don-cho"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].maHoaDon", notNullValue()));
    }

    @Test
    @DisplayName("TC3: Thêm mặt hàng vào tab hóa đơn chờ, kiểm tra tồn kho và thành tiền")
    void testThemMatHangVaoHoaDonCho() throws Exception {
        // 1. Tạo tab mới
        String resTabStr = mockMvc.perform(post("/admin/ban-hang-tai-quay/api/hoa-don-cho/tao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        Long hdcId = objectMapper.readTree(resTabStr).get("id").asLong();

        // 2. Thêm 2 sản phẩm vào tab
        PosThemMatHangRequest themReq = PosThemMatHangRequest.builder()
                .maHoaDonChoId(hdcId)
                .maBienTheId(bienTheTest.getId())
                .soLuong(2)
                .build();

        String resThemStr = mockMvc.perform(post("/admin/ban-hang-tai-quay/api/them-mat-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(themReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tongSoLuong", is(2)))
                .andExpect(jsonPath("$.danhSachMatHang", hasSize(1)))
                .andExpect(jsonPath("$.danhSachMatHang[0].soLuong", is(2)))
                .andReturn().getResponse().getContentAsString();

        assertEquals(500000.0, objectMapper.readTree(resThemStr).get("danhSachMatHang").get(0).get("thanhTien").asDouble());

        // 3. Đóng tab
        mockMvc.perform(delete("/admin/ban-hang-tai-quay/api/hoa-don-cho/" + hdcId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TC4: Thanh toán tại quầy thành công: Tạo đơn hàng hoàn thành, trừ tồn kho và tính tiền thừa chuẩn xác")
    void testThanhToanTaiQuay_ThanhCongVaTruKho() throws Exception {
        int tonKhoTruoc = bienTheTest.getSoLuong(); // 50

        // 1. Tạo tab hóa đơn
        String resTabStr = mockMvc.perform(post("/admin/ban-hang-tai-quay/api/hoa-don-cho/tao"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long hdcId = objectMapper.readTree(resTabStr).get("id").asLong();

        // 2. Thêm 3 sản phẩm vào hóa đơn (Đơn giá 250k -> Tổng 750k)
        PosThemMatHangRequest themReq = PosThemMatHangRequest.builder()
                .maHoaDonChoId(hdcId)
                .maBienTheId(bienTheTest.getId())
                .soLuong(3)
                .build();

        mockMvc.perform(post("/admin/ban-hang-tai-quay/api/them-mat-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(themReq)))
                .andExpect(status().isOk());

        // 3. Thực hiện thanh toán: Giảm giá 50k -> Khách cần trả 700k. Khách đưa 1.000.000đ -> Tiền thối 300.000đ
        PosThanhToanRequest ttReq = PosThanhToanRequest.builder()
                .maHoaDonChoId(hdcId)
                .tenKhachHang("Anh Hoàng Bán Lẻ")
                .soDienThoaiKhach("0977889900")
                .maPhuongThucId(ptttTienMat.getId())
                .tienGiamGia(new BigDecimal("50000"))
                .tienKhachDua(new BigDecimal("1000000"))
                .ghiChu("Khách mua trực tiếp tại quầy")
                .build();

        String resCheckoutStr = mockMvc.perform(post("/admin/ban-hang-tai-quay/api/thanh-toan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ttReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.loaiDonHang", is("TAI_QUAY")))
                .andReturn().getResponse().getContentAsString();

        com.fasterxml.jackson.databind.JsonNode resJson = objectMapper.readTree(resCheckoutStr);
        assertEquals(750000.0, resJson.get("tongTienHang").asDouble());
        assertEquals(50000.0, resJson.get("tienGiamGia").asDouble());
        assertEquals(700000.0, resJson.get("tongThanhToan").asDouble());
        assertEquals(1000000.0, resJson.get("tienKhachDua").asDouble());
        assertEquals(300000.0, resJson.get("tienThuaTraKhach").asDouble());

        Long donHangId = resJson.get("donHangId").asLong();

        // 4. Kiểm tra DonHang trong Database
        DonHang dhDb = donHangRepository.findById(donHangId).orElseThrow();
        assertEquals("TAI_QUAY", dhDb.getLoaiDonHang());
        assertEquals("HOAN_THANH", dhDb.getTrangThaiDonHang().getMaTrangThai());
        assertEquals(new BigDecimal("700000.00"), dhDb.getTongThanhToan());
        assertEquals(new BigDecimal("1000000.00"), dhDb.getTienKhachDua());
        assertEquals(new BigDecimal("300000.00"), dhDb.getTienThuaTraKhach());

        // 5. Kiểm tra Tồn kho đã được trừ đúng 3 cái
        BienTheSanPham btSau = bienTheSanPhamRepository.findById(bienTheTest.getId()).orElseThrow();
        assertEquals(tonKhoTruoc - 3, btSau.getSoLuong(), "Tồn kho phải được trừ đúng 3 sản phẩm sau khi bán tại quầy");

        // 6. Kiểm tra Hóa đơn chờ đã được dọn dẹp sau khi thanh toán
        assertFalse(hoaDonChoRepository.findById(hdcId).isPresent(), "Tab hóa đơn chờ phải được xóa sau khi thanh toán");
    }

    @Test
    @DisplayName("TC5: Chặn thanh toán tiền mặt khi tiền khách đưa nhỏ hơn tổng tiền phải trả")
    void testThanhToan_TienKhachDuaKhongDu_BiChan() throws Exception {
        // Tạo tab và thêm 1 sản phẩm (250k)
        String resTabStr = mockMvc.perform(post("/admin/ban-hang-tai-quay/api/hoa-don-cho/tao"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long hdcId = objectMapper.readTree(resTabStr).get("id").asLong();

        PosThemMatHangRequest themReq = PosThemMatHangRequest.builder()
                .maHoaDonChoId(hdcId)
                .maBienTheId(bienTheTest.getId())
                .soLuong(1)
                .build();
        mockMvc.perform(post("/admin/ban-hang-tai-quay/api/them-mat-hang")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(themReq)));

        // Cố tình đưa 100k khi tổng là 250k
        PosThanhToanRequest ttReq = PosThanhToanRequest.builder()
                .maHoaDonChoId(hdcId)
                .maPhuongThucId(ptttTienMat.getId())
                .tienKhachDua(new BigDecimal("100000"))
                .build();

        mockMvc.perform(post("/admin/ban-hang-tai-quay/api/thanh-toan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ttReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("chưa đủ")));

        // Cleanup tab
        mockMvc.perform(delete("/admin/ban-hang-tai-quay/api/hoa-don-cho/" + hdcId));
    }

    @Test
    @DisplayName("TC6: Thanh toán tại quầy tự động lưu khách hàng, tích điểm Loyalty và trừ điểm khi dùng Coolcash")
    void testThanhToan_LuuKhachHangVaDungDiemLoyalty() throws Exception {
        String sdtTest = "0966" + (System.currentTimeMillis() % 1000000);
        String tenKhTest = "Khách Test Loyalty";

        // Bước 1: Khách mới mua hàng tại quầy lần đầu -> Hệ thống tự động tạo KhachHang và HoSoHoiVien
        String resTab1Str = mockMvc.perform(post("/admin/ban-hang-tai-quay/api/hoa-don-cho/tao"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long hdcId1 = objectMapper.readTree(resTab1Str).get("id").asLong();

        PosThemMatHangRequest themReq1 = PosThemMatHangRequest.builder()
                .maHoaDonChoId(hdcId1)
                .maBienTheId(bienTheTest.getId())
                .soLuong(1)
                .build();
        mockMvc.perform(post("/admin/ban-hang-tai-quay/api/them-mat-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(themReq1)))
                .andExpect(status().isOk());

        PosThanhToanRequest ttReq1 = PosThanhToanRequest.builder()
                .maHoaDonChoId(hdcId1)
                .tenKhachHang(tenKhTest)
                .soDienThoaiKhach(sdtTest)
                .maPhuongThucId(ptttTienMat.getId())
                .tienKhachDua(new BigDecimal("250000"))
                .build();

        mockMvc.perform(post("/admin/ban-hang-tai-quay/api/thanh-toan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ttReq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Kiểm tra khách hàng đã được lưu tự động
        KhachHang khDb = khachHangRepository.findBySoDienThoai(sdtTest).orElseThrow();
        assertEquals(tenKhTest, khDb.getHoTen());

        // Kiểm tra API lấy thông tin điểm hội viên
        mockMvc.perform(get("/admin/ban-hang-tai-quay/api/khach-hang/" + khDb.getId() + "/diem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maKhachHangId", is(khDb.getId().intValue())))
                .andExpect(jsonPath("$.tenHang", notNullValue()));

        // Bước 2: Nạp thêm 50,000 điểm cho khách để test áp dụng giảm giá điểm tích lũy
        HoSoHoiVien hs = hoSoHoiVienRepository.findByMaKhachHangId(khDb.getId()).orElseThrow();
        hs.setSoDuCoolcash(new BigDecimal("50000"));
        hoSoHoiVienRepository.save(hs);

        // Bước 3: Mua đơn thứ 2, dùng 20,000 điểm Coolcash để giảm trừ
        String resTab2Str = mockMvc.perform(post("/admin/ban-hang-tai-quay/api/hoa-don-cho/tao"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long hdcId2 = objectMapper.readTree(resTab2Str).get("id").asLong();

        PosThemMatHangRequest themReq2 = PosThemMatHangRequest.builder()
                .maHoaDonChoId(hdcId2)
                .maBienTheId(bienTheTest.getId())
                .soLuong(1)
                .build();
        mockMvc.perform(post("/admin/ban-hang-tai-quay/api/them-mat-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(themReq2)))
                .andExpect(status().isOk());

        PosThanhToanRequest ttReq2 = PosThanhToanRequest.builder()
                .maHoaDonChoId(hdcId2)
                .maKhachHangId(khDb.getId())
                .tenKhachHang(tenKhTest)
                .soDienThoaiKhach(sdtTest)
                .maPhuongThucId(ptttTienMat.getId())
                .tienCoolcashDung(new BigDecimal("20000"))
                .tienKhachDua(new BigDecimal("230000")) // 250k - 20k = 230k
                .build();

        String resCheckout2 = mockMvc.perform(post("/admin/ban-hang-tai-quay/api/thanh-toan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ttReq2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andReturn().getResponse().getContentAsString();

        com.fasterxml.jackson.databind.JsonNode json2 = objectMapper.readTree(resCheckout2);
        assertEquals(250000.0, json2.get("tongTienHang").asDouble());
        assertEquals(20000.0, json2.get("tienCoolcashDung").asDouble());
        assertEquals(230000.0, json2.get("tongThanhToan").asDouble());
        assertEquals(0.0, json2.get("tienThuaTraKhach").asDouble());

        // Kiểm tra số dư điểm trong DB đã trừ 20,000 điểm và cộng điểm tích mới
        HoSoHoiVien hsSau = hoSoHoiVienRepository.findByMaKhachHangId(khDb.getId()).orElseThrow();
        assertTrue(hsSau.getSoDuCoolcash().compareTo(new BigDecimal("30000")) >= 0,
                "Số dư phải còn ít nhất 30,000 điểm (50k - 20k + điểm tích mới)");
    }

    @Test
    @DisplayName("TC7: Tìm kiếm khách hàng bằng số điện thoại (hỗ trợ cả định dạng có dấu cách) và tìm ngay sau khi tạo nhanh")
    void testTimKiemKhachHang_TheoSoDienThoaiVaTaoNhanh() throws Exception {
        String sdtGoc = String.format("0355%06d", (System.currentTimeMillis() % 1000000));
        String sdtCoDauCach = sdtGoc.substring(0, 4) + " " + sdtGoc.substring(4);
        String tenKh = "Khách Hàng Tìm Kiếm Test";

        // 1. Tạo nhanh khách hàng tại quầy với số điện thoại có dấu cách
        PosTaoKhachHangNhanhRequest taoReq = PosTaoKhachHangNhanhRequest.builder()
                .hoTen(tenKh)
                .soDienThoai(sdtCoDauCach)
                .email("")
                .build();

        mockMvc.perform(post("/admin/ban-hang-tai-quay/api/khach-hang/tao-nhanh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taoReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.hoTen", is(tenKh)));

        // 2. Tìm kiếm bằng số điện thoại liền mạch (không dấu cách)
        mockMvc.perform(get("/admin/ban-hang-tai-quay/api/khach-hang?q=" + sdtGoc))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].hoTen", is(tenKh)));

        // 3. Tìm kiếm bằng số điện thoại có dấu cách
        mockMvc.perform(get("/admin/ban-hang-tai-quay/api/khach-hang?q=" + sdtCoDauCach))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].hoTen", is(tenKh)));

        // 4. Tìm kiếm bằng tên khách hàng
        mockMvc.perform(get("/admin/ban-hang-tai-quay/api/khach-hang?q=" + tenKh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].hoTen", is(tenKh)));
    }

    @Test
    @DisplayName("TC8: Báo lỗi khi tạo khách hàng với số điện thoại sai định dạng (không đủ 10 số hoặc sai đầu số)")
    void testTaoKhachHang_SaiDinhDangSoDienThoai() throws Exception {
        // Test SĐT không đủ 10 chữ số
        PosTaoKhachHangNhanhRequest reqNgan = PosTaoKhachHangNhanhRequest.builder()
                .hoTen("Khách SĐT Ngắn")
                .soDienThoai("0912345")
                .build();

        mockMvc.perform(post("/admin/ban-hang-tai-quay/api/khach-hang/tao-nhanh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqNgan)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error", containsString("không đúng định dạng")));

        // Test SĐT sai đầu số mạng Việt Nam (ví dụ 012...)
        PosTaoKhachHangNhanhRequest reqDauSoSai = PosTaoKhachHangNhanhRequest.builder()
                .hoTen("Khách Sai Đầu Số")
                .soDienThoai("0123456789")
                .build();

        mockMvc.perform(post("/admin/ban-hang-tai-quay/api/khach-hang/tao-nhanh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDauSoSai)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error", containsString("không đúng định dạng")));
    }

    @Test
    @DisplayName("TC9: Validate chuyên nghiệp không cho trùng số điện thoại đã có trong hệ thống và báo lỗi kèm thông tin khách cũ")
    void testTaoKhachHang_BaoLoiTrungSoDienThoai() throws Exception {
        String sdtTonTai = String.format("0988%06d", (System.currentTimeMillis() % 1000000));
        String tenKhCu = "Khách Hàng Đã Đăng Ký";

        // 1. Tạo trước khách hàng đầu tiên
        PosTaoKhachHangNhanhRequest req1 = PosTaoKhachHangNhanhRequest.builder()
                .hoTen(tenKhCu)
                .soDienThoai(sdtTonTai)
                .build();

        String resKh1 = mockMvc.perform(post("/admin/ban-hang-tai-quay/api/khach-hang/tao-nhanh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        Long khCuId = objectMapper.readTree(resKh1).get("id").asLong();

        // 2. Thử tạo khách hàng thứ hai với CÙNG SỐ ĐIỆN THOẠI (kể cả có thêm khoảng trắng hoặc tiền tố +84)
        String sdtCoKhoangTrang = "+84 " + sdtTonTai.substring(1, 4) + " " + sdtTonTai.substring(4);
        PosTaoKhachHangNhanhRequest req2 = PosTaoKhachHangNhanhRequest.builder()
                .hoTen("Khách Hàng Thứ Hai Cố Tình Trùng")
                .soDienThoai(sdtCoKhoangTrang)
                .build();

        mockMvc.perform(post("/admin/ban-hang-tai-quay/api/khach-hang/tao-nhanh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.trungSoDienThoai", is(true)))
                .andExpect(jsonPath("$.khachHangTonTai.id", is(khCuId.intValue())))
                .andExpect(jsonPath("$.khachHangTonTai.hoTen", is(tenKhCu)))
                .andExpect(jsonPath("$.error", containsString(sdtTonTai)))
                .andExpect(jsonPath("$.error", containsString(tenKhCu)));
    }
}
