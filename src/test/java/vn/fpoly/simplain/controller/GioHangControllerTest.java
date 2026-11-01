package vn.fpoly.simplain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import vn.fpoly.simplain.dto.CapNhatSoLuongRequest;
import vn.fpoly.simplain.dto.ThemComboVaoGioRequest;
import vn.fpoly.simplain.dto.ThemGioHangRequest;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;
import vn.fpoly.simplain.util.JwtUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động Quản lý Giỏ hàng và Bó hàng Combo (US17).
 * - Tiêu chí 1: Thêm/Sửa/Xóa biến thể vào giỏ.
 * - Tiêu chí 2: Tính năng bó hàng Combo (Sinh nhom_combo_token).
 * Chạy lệnh: .\mvnw.cmd -Dtest=GioHangControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Giỏ hàng & Bó hàng Combo (US17)")
class GioHangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private ChiTietGioHangRepository chiTietGioHangRepository;

    @Autowired
    private GoiComboRepository goiComboRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichThuocRepository kichThuocRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    private static final String EMAIL_TEST = "us17.cart@gmail.com";
    private static final String SDT_TEST = "0987171717";
    private static final String SKU_TEST_1 = "SKU-US17-01";
    private static final String SKU_TEST_2 = "SKU-US17-02";
    private static final String MA_COMBO_TEST = "COMBO-US17-TEST";

    private KhachHang khachHangTest;
    private String jwtTokenTest;
    private DanhMuc danhMucTest;
    private SanPham sanPhamTest;
    private MauSac mauSacTest;
    private KichThuoc kichThuocTest;
    private BienTheSanPham bienThe1;
    private BienTheSanPham bienThe2;
    private GoiCombo comboTest;

    @BeforeEach
    void setUp() {
        // 1. Tạo Khách hàng & Token JWT
        khachHangTest = khachHangRepository.save(KhachHang.builder()
                .maKhachHang("KH-US17-TEST")
                .hoTen("Nguyễn Văn Trí US17")
                .email(EMAIL_TEST)
                .soDienThoai(SDT_TEST)
                .matKhauHash("$2a$10$abcdefghijklmnopqrstuv")
                .trangThai(1)
                .build());
        jwtTokenTest = jwtUtil.generateToken(khachHangTest);

        // 2. Tạo Danh mục, Sản phẩm, Màu, Size
        danhMucTest = danhMucRepository.save(DanhMuc.builder()
                .tenDanhMuc("Danh mục US17")
                .duongDanSlug("danh-muc-us17")
                .thuTuHienThi(99)
                .build());

        sanPhamTest = sanPhamRepository.save(SanPham.builder()
                .maDanhMucId(danhMucTest.getId())
                .maSanPham("SP-US17-01")
                .tenSanPham("Áo Polo Nam Trải Nghiệm US17")
                .duongDanSlug("ao-polo-nam-us17")
                .chatLieu("100% Cotton Compact")
                .formDang("Regular Fit")
                .moTaChiTiet("Mô tả chi tiết sản phẩm phục vụ kiểm thử giỏ hàng US17.")
                .giaCoBan(new BigDecimal("250000"))
                .kichHoat(true)
                .build());

        mauSacTest = mauSacRepository.findAll().stream().findFirst().orElseGet(() ->
                mauSacRepository.save(MauSac.builder().maMau("#171717").tenMau("Mau US17").build())
        );

        kichThuocTest = kichThuocRepository.findAll().stream().findFirst().orElseGet(() ->
                kichThuocRepository.save(KichThuoc.builder().maKichThuoc("US17-L").tenKichThuoc("Size US17-L").build())
        );

        // 3. Tạo 2 Biến thể SKU
        bienThe1 = bienTheRepository.save(BienTheSanPham.builder()
                .maSanPhamId(sanPhamTest.getId())
                .maMauSacId(mauSacTest.getId())
                .maKichThuocId(kichThuocTest.getId())
                .maSku(SKU_TEST_1)
                .maVachBarcode("BC-" + SKU_TEST_1)
                .giaBan(new BigDecimal("199000"))
                .giaGoc(new BigDecimal("120000"))
                .soLuong(20) // Tồn kho 20 cái
                .kichHoat(true)
                .build());

        bienThe2 = bienTheRepository.save(BienTheSanPham.builder()
                .maSanPhamId(sanPhamTest.getId())
                .maMauSacId(mauSacTest.getId())
                .maKichThuocId(kichThuocTest.getId())
                .maSku(SKU_TEST_2)
                .maVachBarcode("BC-" + SKU_TEST_2)
                .giaBan(new BigDecimal("219000"))
                .giaGoc(new BigDecimal("130000"))
                .soLuong(15) // Tồn kho 15 cái
                .kichHoat(true)
                .build());

        // 4. Tạo Gói Combo (Quy định 2 món, giá 350.000đ)
        comboTest = goiComboRepository.save(GoiCombo.builder()
                .maCombo(MA_COMBO_TEST)
                .tenCombo("Combo 2 Áo Polo Tiết Kiệm US17")
                .duongDanSlug("combo-2-ao-polo-us17")
                .giaCombo(new BigDecimal("350000"))
                .soMonQuyDinh(2)
                .moTa("Combo ưu đãi đặc biệt US17")
                .kichHoat(true)
                .build());
    }

    @AfterEach
    void tearDown() {
        try {
            // Dọn dẹp chi tiết giỏ hàng và giỏ hàng
            chiTietGioHangRepository.deleteAll();
            gioHangRepository.deleteAll();

            if (comboTest != null && comboTest.getId() != null) {
                goiComboRepository.deleteById(comboTest.getId());
            }
            if (bienThe1 != null && bienThe1.getId() != null) {
                bienTheRepository.deleteById(bienThe1.getId());
            }
            if (bienThe2 != null && bienThe2.getId() != null) {
                bienTheRepository.deleteById(bienThe2.getId());
            }
            if (sanPhamTest != null && sanPhamTest.getId() != null) {
                sanPhamRepository.deleteById(sanPhamTest.getId());
            }
            if (danhMucTest != null && danhMucTest.getId() != null) {
                danhMucRepository.deleteById(danhMucTest.getId());
            }
            if (khachHangTest != null && khachHangTest.getId() != null) {
                khachHangRepository.deleteById(khachHangTest.getId());
            }
        } catch (Exception ignored) {
        }
    }

    // =========================================================================
    // TIÊU CHÍ 1: THÊM / SỬA / XÓA BIẾN THỂ VÀO GIỎ
    // =========================================================================

    @Test
    @DisplayName("TC1.1 - Thêm biến thể lẻ vào giỏ hàng thành công")
    void themBienTheVaoGio_thanhCong() throws Exception {
        ThemGioHangRequest req = ThemGioHangRequest.builder()
                .maBienTheId(bienThe1.getId())
                .soLuong(2)
                .build();

        mockMvc.perform(post("/gio-hang/them")
                        .cookie(new Cookie("jwt_token", jwtTokenTest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.tongSoLuong", is(2)));

        // Kiểm tra trong CSDL
        GioHang gioHang = gioHangRepository.findByMaKhachHangId(khachHangTest.getId()).orElse(null);
        assertNotNull(gioHang);

        List<ChiTietGioHang> items = chiTietGioHangRepository.findByMaGioHangIdOrderByNgayThemDesc(gioHang.getId());
        assertEquals(1, items.size());
        assertEquals(bienThe1.getId(), items.get(0).getMaBienTheId());
        assertEquals(2, items.get(0).getSoLuong());
        assertNull(items.get(0).getNhomComboToken(), "Hàng mua lẻ không có nhomComboToken");
    }

    @Test
    @DisplayName("TC1.2 - Thêm biến thể vượt quá tồn kho phải bị từ chối")
    void themBienTheVaoGio_thatBai_khiSoLuongVuotTonKho() throws Exception {
        // bienThe1 tồn kho là 20 cái -> thêm 50 cái sẽ bị chặn
        ThemGioHangRequest req = ThemGioHangRequest.builder()
                .maBienTheId(bienThe1.getId())
                .soLuong(50)
                .build();

        mockMvc.perform(post("/gio-hang/them")
                        .cookie(new Cookie("jwt_token", jwtTokenTest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("tồn kho")));
    }

    @Test
    @DisplayName("TC1.3 - Sửa số lượng của biến thể trong giỏ hàng thành công")
    void suaSoLuongBienThe_thanhCong() throws Exception {
        // Thêm trước biến thể số lượng = 1
        ThemGioHangRequest addReq = ThemGioHangRequest.builder()
                .maBienTheId(bienThe1.getId())
                .soLuong(1)
                .build();
        mockMvc.perform(post("/gio-hang/them")
                .cookie(new Cookie("jwt_token", jwtTokenTest))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addReq)));

        GioHang gioHang = gioHangRepository.findByMaKhachHangId(khachHangTest.getId()).orElseThrow();
        ChiTietGioHang item = chiTietGioHangRepository.findByMaGioHangIdOrderByNgayThemDesc(gioHang.getId()).get(0);

        // Cập nhật số lượng lên 5
        CapNhatSoLuongRequest updateReq = CapNhatSoLuongRequest.builder()
                .chiTietGioHangId(item.getId())
                .soLuong(5)
                .build();

        mockMvc.perform(post("/gio-hang/cap-nhat")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .cookie(new Cookie("jwt_token", jwtTokenTest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        ChiTietGioHang sauCapNhat = chiTietGioHangRepository.findById(item.getId()).orElseThrow();
        assertEquals(5, sauCapNhat.getSoLuong());
    }

    @Test
    @DisplayName("TC1.3b - Sửa số lượng biến thể bằng Form POST (x-www-form-urlencoded) không bị lỗi 415")
    void suaSoLuongBienThe_bangFormPost_thanhCong() throws Exception {
        // Thêm trước biến thể số lượng = 1
        ThemGioHangRequest addReq = ThemGioHangRequest.builder()
                .maBienTheId(bienThe1.getId())
                .soLuong(1)
                .build();
        mockMvc.perform(post("/gio-hang/them")
                .cookie(new Cookie("jwt_token", jwtTokenTest))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addReq)));

        GioHang gioHang = gioHangRepository.findByMaKhachHangId(khachHangTest.getId()).orElseThrow();
        ChiTietGioHang item = chiTietGioHangRepository.findByMaGioHangIdOrderByNgayThemDesc(gioHang.getId()).get(0);

        // Submit form POST giống như trình duyệt khi bấm nút + / -
        mockMvc.perform(post("/gio-hang/cap-nhat")
                        .cookie(new Cookie("jwt_token", jwtTokenTest))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("chiTietGioHangId", String.valueOf(item.getId()))
                        .param("nhomComboToken", "")
                        .param("soLuong", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gio-hang"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        ChiTietGioHang sauCapNhat = chiTietGioHangRepository.findById(item.getId()).orElseThrow();
        assertEquals(4, sauCapNhat.getSoLuong());
    }

    @Test
    @DisplayName("TC1.4 - Xóa biến thể lẻ khỏi giỏ hàng thành công")
    void xoaBienTheKhoiGio_thanhCong() throws Exception {
        // Thêm mục
        ThemGioHangRequest addReq = ThemGioHangRequest.builder()
                .maBienTheId(bienThe1.getId())
                .soLuong(3)
                .build();
        mockMvc.perform(post("/gio-hang/them")
                .cookie(new Cookie("jwt_token", jwtTokenTest))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addReq)));

        GioHang gioHang = gioHangRepository.findByMaKhachHangId(khachHangTest.getId()).orElseThrow();
        ChiTietGioHang item = chiTietGioHangRepository.findByMaGioHangIdOrderByNgayThemDesc(gioHang.getId()).get(0);

        // Xóa
        mockMvc.perform(post("/gio-hang/xoa/" + item.getId())
                        .header("X-Requested-With", "XMLHttpRequest")
                        .cookie(new Cookie("jwt_token", jwtTokenTest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        assertFalse(chiTietGioHangRepository.existsById(item.getId()), "Mục hàng phải bị xóa hoàn toàn khỏi giỏ");
    }

    @Test
    @DisplayName("TC1.5 - Xóa toàn bộ giỏ hàng (Làm trống giỏ)")
    void xoaHetGioHang_thanhCong() throws Exception {
        // Thêm 2 mặt hàng vào giỏ
        mockMvc.perform(post("/gio-hang/them")
                .cookie(new Cookie("jwt_token", jwtTokenTest))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ThemGioHangRequest.builder().maBienTheId(bienThe1.getId()).soLuong(1).build())));
        mockMvc.perform(post("/gio-hang/them")
                .cookie(new Cookie("jwt_token", jwtTokenTest))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ThemGioHangRequest.builder().maBienTheId(bienThe2.getId()).soLuong(2).build())));

        GioHang gioHang = gioHangRepository.findByMaKhachHangId(khachHangTest.getId()).orElseThrow();
        assertEquals(2, chiTietGioHangRepository.countByMaGioHangId(gioHang.getId()));

        // Gọi xóa hết
        mockMvc.perform(post("/gio-hang/xoa-het")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .cookie(new Cookie("jwt_token", jwtTokenTest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        assertEquals(0, chiTietGioHangRepository.countByMaGioHangId(gioHang.getId()), "Giỏ hàng phải sạch bóng không còn mục nào");
    }

    // =========================================================================
    // TIÊU CHÍ 2: TÍNH NĂNG BÓ HÀNG COMBO (SINH NHOM_COMBO_TOKEN)
    // =========================================================================

    @Test
    @DisplayName("TC2.1 - Bó hàng Combo thành công: Tự động sinh nhom_combo_token duy nhất")
    void boHangCombo_thanhCong_sinhNhomComboToken() throws Exception {
        // Combo quy định 2 món -> chọn bienThe1 và bienThe2
        ThemComboVaoGioRequest req = ThemComboVaoGioRequest.builder()
                .maComboId(comboTest.getId())
                .danhSachBienTheIds(Arrays.asList(bienThe1.getId(), bienThe2.getId()))
                .soLuongCombo(1)
                .build();

        MvcResult result = mockMvc.perform(post("/gio-hang/them-combo")
                        .cookie(new Cookie("jwt_token", jwtTokenTest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.nhomComboToken", notNullValue()))
                .andExpect(jsonPath("$.nhomComboToken", startsWith("COMBO-")))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String sinhToken = objectMapper.readTree(responseBody).get("nhomComboToken").asText();
        assertNotNull(sinhToken);
        assertTrue(sinhToken.startsWith("COMBO-"), "nhom_combo_token phải có định dạng tiền tố COMBO-");

        // Kiểm tra trong DB: Cả 2 chi tiết con đều phải mang đúng token này
        GioHang gioHang = gioHangRepository.findByMaKhachHangId(khachHangTest.getId()).orElseThrow();
        List<ChiTietGioHang> itemsInCombo = chiTietGioHangRepository.findByMaGioHangIdAndNhomComboToken(gioHang.getId(), sinhToken);
        assertEquals(2, itemsInCombo.size(), "Bó combo phải có chính xác 2 món con");
        for (ChiTietGioHang item : itemsInCombo) {
            assertEquals(sinhToken, item.getNhomComboToken(), "Tất cả món con phải cùng mã nhom_combo_token");
            assertEquals(comboTest.getId(), item.getMaComboId(), "Phải trỏ đúng maComboId");
            assertEquals(1, item.getSoLuong(), "Số lượng từng món con ban đầu là 1");
        }
    }

    @Test
    @DisplayName("TC2.2 - Bó hàng Combo thất bại nếu không chọn đủ số món quy định")
    void boHangCombo_thatBai_khiKhongDuSoMonQuyDinh() throws Exception {
        // Combo quy định 2 món nhưng chỉ gửi 1 món
        ThemComboVaoGioRequest req = ThemComboVaoGioRequest.builder()
                .maComboId(comboTest.getId())
                .danhSachBienTheIds(List.of(bienThe1.getId()))
                .soLuongCombo(1)
                .build();

        mockMvc.perform(post("/gio-hang/them-combo")
                        .cookie(new Cookie("jwt_token", jwtTokenTest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("chọn chính xác 2 món")));
    }

    @Test
    @DisplayName("TC2.3 - Cập nhật số lượng Bó Combo: Đồng bộ số lượng cho toàn bộ các món trong nhóm token")
    void capNhatSoLuongBocCombo_dongBoTatCaMonCon() throws Exception {
        // 1. Thêm gói combo
        ThemComboVaoGioRequest req = ThemComboVaoGioRequest.builder()
                .maComboId(comboTest.getId())
                .danhSachBienTheIds(Arrays.asList(bienThe1.getId(), bienThe2.getId()))
                .soLuongCombo(1)
                .build();
        MvcResult res = mockMvc.perform(post("/gio-hang/them-combo")
                .cookie(new Cookie("jwt_token", jwtTokenTest))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andReturn();

        String token = objectMapper.readTree(res.getResponse().getContentAsString()).get("nhomComboToken").asText();

        // 2. Cập nhật số lượng bó combo từ 1 lên 3
        CapNhatSoLuongRequest updateReq = CapNhatSoLuongRequest.builder()
                .nhomComboToken(token)
                .soLuong(3)
                .build();

        mockMvc.perform(post("/gio-hang/cap-nhat")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .cookie(new Cookie("jwt_token", jwtTokenTest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // 3. Kiểm tra DB: Tất cả món trong token phải đồng bộ thành số lượng 3
        GioHang gioHang = gioHangRepository.findByMaKhachHangId(khachHangTest.getId()).orElseThrow();
        List<ChiTietGioHang> itemsInCombo = chiTietGioHangRepository.findByMaGioHangIdAndNhomComboToken(gioHang.getId(), token);
        assertEquals(2, itemsInCombo.size());
        for (ChiTietGioHang it : itemsInCombo) {
            assertEquals(3, it.getSoLuong(), "Số lượng tất cả món con trong bó combo phải đồng bộ bằng 3");
        }
    }

    @Test
    @DisplayName("TC2.4 - Xóa trọn vẹn Bó Combo theo token: Đảm bảo tính toàn vẹn của gói combo")
    void xoaBocCombo_xoaToanVenTatCaMonCon() throws Exception {
        // 1. Thêm combo
        ThemComboVaoGioRequest req = ThemComboVaoGioRequest.builder()
                .maComboId(comboTest.getId())
                .danhSachBienTheIds(Arrays.asList(bienThe1.getId(), bienThe2.getId()))
                .soLuongCombo(1)
                .build();
        MvcResult res = mockMvc.perform(post("/gio-hang/them-combo")
                .cookie(new Cookie("jwt_token", jwtTokenTest))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andReturn();

        String token = objectMapper.readTree(res.getResponse().getContentAsString()).get("nhomComboToken").asText();
        GioHang gioHang = gioHangRepository.findByMaKhachHangId(khachHangTest.getId()).orElseThrow();

        // 2. Xóa theo mã token
        mockMvc.perform(post("/gio-hang/xoa-combo/" + token)
                        .header("X-Requested-With", "XMLHttpRequest")
                        .cookie(new Cookie("jwt_token", jwtTokenTest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // 3. Kiểm tra: Không còn bất kỳ món con nào của token này trong DB
        List<ChiTietGioHang> conLai = chiTietGioHangRepository.findByMaGioHangIdAndNhomComboToken(gioHang.getId(), token);
        assertTrue(conLai.isEmpty(), "Toàn bộ món con của bó combo phải bị xóa trọn vẹn");
    }

    // =========================================================================
    // TEST VIEW & API BỔ TRỢ
    // =========================================================================

    @Test
    @DisplayName("TC3.1 - Hiển thị giao diện Giỏ hàng (GET /gio-hang)")
    void hienThiGiaoDienGioHang_thanhCong() throws Exception {
        mockMvc.perform(get("/gio-hang")
                        .cookie(new Cookie("jwt_token", jwtTokenTest)))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/gio-hang"))
                .andExpect(model().attributeExists("gioHang"));
    }

    @Test
    @DisplayName("TC3.2 - API lấy số lượng giỏ hàng cho Header Badge (GET /gio-hang/api/dem-so-luong)")
    void apiDemSoLuongGioHang_chinhXac() throws Exception {
        // Thêm 1 biến thể lẻ với số lượng 2
        mockMvc.perform(post("/gio-hang/them")
                .cookie(new Cookie("jwt_token", jwtTokenTest))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ThemGioHangRequest.builder().maBienTheId(bienThe1.getId()).soLuong(2).build())));

        mockMvc.perform(get("/gio-hang/api/dem-so-luong")
                        .cookie(new Cookie("jwt_token", jwtTokenTest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tongSoLuong", is(2)));
    }
}
