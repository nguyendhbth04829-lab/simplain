package vn.fpoly.simplain.controller;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
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
import vn.fpoly.simplain.util.JwtUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.mock.web.MockMultipartFile;

/**
 * Kiểm thử tích hợp Web MVC cho chức năng Khách hàng tạo Yêu cầu Đổi/Trả (RMA) Online (US24).
 * - Tiêu chí 1: Form tick chọn sản phẩm lỗi trong đơn cũ.
 * - Tiêu chí 2: Chọn SKU mới muốn đổi hoặc Nhập STK nếu muốn hoàn tiền.
 * - Form Validation & Bảo mật: Chống IDOR, kiểm tra tính hợp lệ trạng thái HOAN_THANH,
 *   kiểm tra tồn kho SKU mới, tính toán tiền hoàn và chặn trùng lặp yêu cầu.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Kiểm thử Web MVC Khách hàng tạo Yêu cầu Đổi/Trả Online (US24)")
class DoiTraKhachHangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private YeuCauDoiTraRepository yeuCauDoiTraRepository;

    @Autowired
    private ChiTietSanPhamDoiTraRepository chiTietSanPhamDoiTraRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EntityManager entityManager;

    private KhachHang khachHangA;
    private KhachHang khachHangB;
    private Cookie cookieKhachA;
    private Cookie cookieKhachB;
    private DonHang donHangHoanThanh;
    private DonHang donHangChuaHoanThanh;
    private ChiTietDonHang chiTiet1;
    private BienTheSanPham bienTheMoi1;

    @BeforeEach
    void setUp() {
        long timestamp = System.currentTimeMillis();

        // 1. Tạo khách hàng A (chính) và khách hàng B (test IDOR)
        String sdtA = String.format("0981%06d", (timestamp % 1000000));
        khachHangA = khachHangRepository.save(KhachHang.builder()
                .maKhachHang("KH-RMA-A-" + timestamp)
                .hoTen("Nguyễn Văn Test RMA A")
                .soDienThoai(sdtA)
                .email(sdtA + "@simplain.vn")
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build());
        cookieKhachA = new Cookie(KhachHangController.COOKIE_TOKEN, jwtUtil.generateToken(khachHangA));

        String sdtB = String.format("0982%06d", (timestamp % 1000000));
        khachHangB = khachHangRepository.save(KhachHang.builder()
                .maKhachHang("KH-RMA-B-" + timestamp)
                .hoTen("Trần Thị Test RMA B")
                .soDienThoai(sdtB)
                .email(sdtB + "@simplain.vn")
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build());
        cookieKhachB = new Cookie(KhachHangController.COOKIE_TOKEN, jwtUtil.generateToken(khachHangB));

        // 2. Lấy trạng thái đơn hàng
        TrangThaiDonHang ttHoanThanh = trangThaiDonHangRepository.findByMaTrangThai("HOAN_THANH")
                .orElseGet(() -> trangThaiDonHangRepository.findById(4L).orElseThrow());
        TrangThaiDonHang ttChoXacNhan = trangThaiDonHangRepository.findByMaTrangThai("CHO_XAC_NHAN")
                .orElseGet(() -> trangThaiDonHangRepository.findById(1L).orElseThrow());

        // 3. Biến thể sản phẩm test
        BienTheSanPham btCu = bienTheSanPhamRepository.findAll().stream().findFirst().orElseGet(() -> {
            BienTheSanPham b = BienTheSanPham.builder()
                    .maSanPhamId(1L)
                    .maMauSacId(1L)
                    .maKichThuocId(1L)
                    .maSku("SKU-OLD-" + timestamp)
                    .soLuong(50)
                    .giaBan(new BigDecimal("250000"))
                    .giaGoc(new BigDecimal("300000"))
                    .kichHoat(true)
                    .build();
            return bienTheSanPhamRepository.save(b);
        });

        bienTheMoi1 = bienTheSanPhamRepository.findByMaSkuIgnoreCase("SKU-NEW-" + timestamp).orElseGet(() -> {
            BienTheSanPham b = BienTheSanPham.builder()
                    .maSanPhamId(btCu.getMaSanPhamId() != null ? btCu.getMaSanPhamId() : 1L)
                    .maMauSacId(btCu.getMaMauSacId() != null ? btCu.getMaMauSacId() : 1L)
                    .maKichThuocId(btCu.getMaKichThuocId() != null ? btCu.getMaKichThuocId() : 1L)
                    .maSku("SKU-NEW-" + timestamp)
                    .soLuong(100)
                    .giaBan(new BigDecimal("250000"))
                    .giaGoc(new BigDecimal("300000"))
                    .kichHoat(true)
                    .build();
            return bienTheSanPhamRepository.save(b);
        });

        // 4. Tạo đơn hàng đã HOAN_THANH của khách A
        String maDonHt = "SMPL-RMA-HT-" + timestamp;
        donHangHoanThanh = DonHang.builder()
                .maDonHang(maDonHt)
                .khachHang(khachHangA)
                .trangThaiDonHang(ttHoanThanh)
                .loaiDonHang("ONLINE")
                .tongTienHang(new BigDecimal("500000"))
                .phiVanChuyen(BigDecimal.ZERO)
                .tongThanhToan(new BigDecimal("500000"))
                .tenNguoiNhan("Nguyễn Văn Test RMA A")
                .soDienThoaiNhan(sdtA)
                .diaChiGiaoHang("123 Phố Huế, Hà Nội")
                .maVanDon("SMPL-VD-TEST-999")
                .ngayTao(LocalDateTime.now().minusDays(2))
                .build();
        donHangHoanThanh = donHangRepository.save(donHangHoanThanh);

        chiTiet1 = ChiTietDonHang.builder()
                .maDonHangId(donHangHoanThanh.getId())
                .maBienTheId(btCu.getId())
                .tenSanPham("Áo Polo Simplain Cotton")
                .maSku(btCu.getMaSku())
                .donGia(new BigDecimal("250000"))
                .soLuong(2)
                .thanhTien(new BigDecimal("500000"))
                .build();
        chiTiet1 = chiTietDonHangRepository.save(chiTiet1);

        // 5. Tạo đơn hàng CHO_XAC_NHAN của khách A (test chặn đơn chưa hoàn thành)
        String maDonChuaHt = "SMPL-RMA-CXN-" + timestamp;
        donHangChuaHoanThanh = DonHang.builder()
                .maDonHang(maDonChuaHt)
                .khachHang(khachHangA)
                .trangThaiDonHang(ttChoXacNhan)
                .loaiDonHang("ONLINE")
                .tongTienHang(new BigDecimal("250000"))
                .phiVanChuyen(new BigDecimal("30000"))
                .tongThanhToan(new BigDecimal("280000"))
                .tenNguoiNhan("Nguyễn Văn Test RMA A")
                .soDienThoaiNhan(sdtA)
                .diaChiGiaoHang("123 Phố Huế, Hà Nội")
                .ngayTao(LocalDateTime.now())
                .build();
        donHangChuaHoanThanh = donHangRepository.save(donHangChuaHoanThanh);

        ChiTietDonHang chiTiet2 = ChiTietDonHang.builder()
                .maDonHangId(donHangChuaHoanThanh.getId())
                .maBienTheId(btCu.getId())
                .tenSanPham("Áo Polo Simplain Cotton")
                .maSku(btCu.getMaSku())
                .donGia(new BigDecimal("250000"))
                .soLuong(1)
                .thanhTien(new BigDecimal("250000"))
                .build();
        chiTietDonHangRepository.save(chiTiet2);
    }

    @Test
    @DisplayName("TC1: Khách chưa đăng nhập cố gắng mở form tạo Đổi/Trả -> Chuyển hướng đến đăng nhập")
    void testMoFormDoiTra_ChuaDangNhap_ChuyenHuongLogin() throws Exception {
        mockMvc.perform(get("/khach-hang/don-hang/" + donHangHoanThanh.getMaDonHang() + "/doi-tra"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("/khach-hang/dang-nhap?redirect=")));
    }

    @Test
    @DisplayName("TC2: Khách đã đăng nhập mở form Đổi/Trả cho đơn HOÀN THÀNH -> Hiển thị form kèm danh sách sản phẩm và SKU khả dụng")
    void testMoFormDoiTra_ThanhCong() throws Exception {
        mockMvc.perform(get("/khach-hang/don-hang/" + donHangHoanThanh.getMaDonHang() + "/doi-tra")
                        .cookie(cookieKhachA))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/doi-tra/tao-yeu-cau"))
                .andExpect(model().attributeExists("donHangInfo", "taoYeuCauRequest"))
                .andExpect(model().attribute("donHangInfo", hasProperty("danhSachItem", not(empty()))));
    }

    @Test
    @DisplayName("TC3: Bảo mật chống IDOR - Khách B cố tình tạo Đổi/Trả cho đơn hàng của Khách A -> Bị từ chối")
    void testMoFormDoiTra_IDOR_BiChan() throws Exception {
        mockMvc.perform(get("/khach-hang/don-hang/" + donHangHoanThanh.getMaDonHang() + "/doi-tra")
                        .cookie(cookieKhachB))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("loiTruyCap"));
    }

    @Test
    @DisplayName("TC4: Chặn tạo Đổi/Trả khi đơn hàng chưa giao thành công (chưa HOÀN THÀNH)")
    void testMoFormDoiTra_DonChuaHoanThanh_BiChan() throws Exception {
        mockMvc.perform(get("/khach-hang/don-hang/" + donHangChuaHoanThanh.getMaDonHang() + "/doi-tra")
                        .cookie(cookieKhachA))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("loiTruyCap"));
    }

    @Test
    @DisplayName("TC5: Validate Form - Gửi yêu cầu nhưng không tick chọn sản phẩm nào -> Trả về lỗi validation")
    void testValidateForm_KhongTickChonSanPham_BaoLoi() throws Exception {
        mockMvc.perform(post("/khach-hang/don-hang/" + donHangHoanThanh.getMaDonHang() + "/doi-tra")
                        .cookie(cookieKhachA)
                        .param("maDonHang", donHangHoanThanh.getMaDonHang())
                        .param("loaiDoiTra", "DOI_HANG")
                        .param("lyDo", "Sản phẩm bị lỗi kỹ thuật đường chỉ may")
                        .param("items[0].selected", "false")
                        .param("items[0].chiTietDonHangId", String.valueOf(chiTiet1.getId()))
                        .param("items[0].soLuongTra", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/doi-tra/tao-yeu-cau"))
                .andExpect(model().attributeExists("loiValidation"));
    }

    @Test
    @DisplayName("TC6: Validate Form - Chọn ĐỔI HÀNG nhưng không chọn SKU mới muốn đổi sang -> Báo lỗi validation")
    void testValidateForm_DoiHangThieuSkuMoi_BaoLoi() throws Exception {
        mockMvc.perform(post("/khach-hang/don-hang/" + donHangHoanThanh.getMaDonHang() + "/doi-tra")
                        .cookie(cookieKhachA)
                        .param("maDonHang", donHangHoanThanh.getMaDonHang())
                        .param("loaiDoiTra", "DOI_HANG")
                        .param("lyDo", "Sản phẩm bị lỗi đường may nên muốn đổi")
                        .param("items[0].selected", "true")
                        .param("items[0].chiTietDonHangId", String.valueOf(chiTiet1.getId()))
                        .param("items[0].soLuongTra", "1")
                        .param("items[0].tinhTrangSanPham", "Rách đường chỉ may cổ áo")
                        // Thiếu maBienTheMoiId
                        .param("items[0].maBienTheMoiId", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/doi-tra/tao-yeu-cau"))
                .andExpect(model().attributeExists("loiValidation"));
    }

    @Test
    @DisplayName("TC7: Validate Form - Chọn HOÀN TIỀN nhưng STK ngân hàng không hợp lệ -> Báo lỗi validation")
    void testValidateForm_HoanTienStkSai_BaoLoi() throws Exception {
        mockMvc.perform(post("/khach-hang/don-hang/" + donHangHoanThanh.getMaDonHang() + "/doi-tra")
                        .cookie(cookieKhachA)
                        .param("maDonHang", donHangHoanThanh.getMaDonHang())
                        .param("loaiDoiTra", "TRA_HANG_HOAN_TIEN")
                        .param("lyDo", "Sản phẩm không vừa người, muốn hoàn tiền")
                        .param("tenNganHang", "Vietcombank")
                        .param("soTaiKhoan", "ABC1234") // STK chứa chữ -> Không hợp lệ
                        .param("tenChuTaiKhoan", "NGUYEN VAN TEST")
                        .param("items[0].selected", "true")
                        .param("items[0].chiTietDonHangId", String.valueOf(chiTiet1.getId()))
                        .param("items[0].soLuongTra", "1")
                        .param("items[0].tinhTrangSanPham", "Mặc bị chật không vừa"))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/doi-tra/tao-yeu-cau"))
                .andExpect(model().attributeExists("loiValidation"));
    }

    @Test
    @DisplayName("TC8: Tạo yêu cầu ĐỔI HÀNG hợp lệ (Tiêu chí 1 & 2) -> Lưu RMA, lưu chi tiết SKU mới, chuyển hướng thành công")
    void testTaoYeuCauDoiTra_DoiHang_ThanhCong() throws Exception {
        mockMvc.perform(post("/khach-hang/don-hang/" + donHangHoanThanh.getMaDonHang() + "/doi-tra")
                        .cookie(cookieKhachA)
                        .param("maDonHang", donHangHoanThanh.getMaDonHang())
                        .param("loaiDoiTra", "DOI_HANG")
                        .param("lyDo", "Sản phẩm bị bung đường may, tôi muốn đổi sang size mới")
                        .param("items[0].selected", "true")
                        .param("items[0].chiTietDonHangId", String.valueOf(chiTiet1.getId()))
                        .param("items[0].soLuongTra", "1")
                        .param("items[0].tinhTrangSanPham", "Rách đường may sườn áo")
                        .param("items[0].maBienTheMoiId", String.valueOf(bienTheMoi1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/khach-hang/doi-tra/RMA-*"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.clear();

        List<YeuCauDoiTra> dsa = yeuCauDoiTraRepository.findByMaDonHangIdOrderByIdDesc(donHangHoanThanh.getId());
        assertFalse(dsa.isEmpty(), "Phải tạo thành công bản ghi YeuCauDoiTra");

        YeuCauDoiTra rma = dsa.get(0);
        assertEquals("DOI_HANG", rma.getLoaiDoiTra());
        assertEquals("CHO_XU_LY", rma.getTrangThai());
        assertTrue(rma.getMaYeuCauRma().startsWith("RMA-"));

        List<ChiTietSanPhamDoiTra> dsCt = chiTietSanPhamDoiTraRepository.findByMaYeuCauDoiTraId(rma.getId());
        assertEquals(1, dsCt.size());
        assertEquals(bienTheMoi1.getId(), dsCt.get(0).getMaBienTheMoiId());
        assertEquals(1, dsCt.get(0).getSoLuongTra());
    }

    @Test
    @DisplayName("TC9: Tạo yêu cầu TRẢ HÀNG & HOÀN TIỀN hợp lệ (Tiêu chí 1 & 2) -> Tính chính xác số tiền hoàn & lưu STK")
    void testTaoYeuCauDoiTra_HoanTien_ThanhCong() throws Exception {
        mockMvc.perform(post("/khach-hang/don-hang/" + donHangHoanThanh.getMaDonHang() + "/doi-tra")
                        .cookie(cookieKhachA)
                        .param("maDonHang", donHangHoanThanh.getMaDonHang())
                        .param("loaiDoiTra", "TRA_HANG_HOAN_TIEN")
                        .param("lyDo", "Giao sai màu sắc so với mô tả, tôi muốn trả hàng hoàn lại tiền")
                        .param("tenNganHang", "MB Bank")
                        .param("soTaiKhoan", "098111223399")
                        .param("tenChuTaiKhoan", "NGUYEN VAN TEST RMA A")
                        .param("items[0].selected", "true")
                        .param("items[0].chiTietDonHangId", String.valueOf(chiTiet1.getId()))
                        .param("items[0].soLuongTra", "2") // Trả 2 cái * 250k = 500k
                        .param("items[0].tinhTrangSanPham", "Sai màu sắc hoàn toàn"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/khach-hang/doi-tra/RMA-*"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.clear();

        List<YeuCauDoiTra> dsa = yeuCauDoiTraRepository.findByMaDonHangIdOrderByIdDesc(donHangHoanThanh.getId());
        assertFalse(dsa.isEmpty());

        YeuCauDoiTra rma = dsa.get(0);
        assertEquals("TRA_HANG_HOAN_TIEN", rma.getLoaiDoiTra());
        assertEquals(0, new BigDecimal("500000").compareTo(rma.getSoTienHoan()), "Tiền hoàn phải đúng 500.000 đ");
        assertTrue(rma.getThongTinNganHang().contains("098111223399"));
        assertTrue(rma.getThongTinNganHang().contains("MB Bank"));
    }

    @Test
    @DisplayName("TC10: Xem danh sách và chi tiết phiếu RMA của cá nhân -> Hiển thị đầy đủ thông tin")
    void testXemDanhSachVaChiTietRma() throws Exception {
        // Tạo trước 1 phiếu RMA
        YeuCauDoiTra rma = YeuCauDoiTra.builder()
                .maYeuCauRma("RMA-TEST-VIEW-01")
                .maDonHangId(donHangHoanThanh.getId())
                .maKhachHangId(khachHangA.getId())
                .loaiDoiTra("DOI_HANG")
                .lyDo("Lỗi sản phẩm kiểm thử")
                .trangThai("CHO_XU_LY")
                .ngayTao(LocalDateTime.now())
                .build();
        rma = yeuCauDoiTraRepository.save(rma);

        ChiTietSanPhamDoiTra ct = ChiTietSanPhamDoiTra.builder()
                .maYeuCauDoiTraId(rma.getId())
                .maChiTietDonHangId(chiTiet1.getId())
                .soLuongTra(1)
                .maBienTheMoiId(bienTheMoi1.getId())
                .tinhTrangSanPham("Lỗi vải xước")
                .build();
        chiTietSanPhamDoiTraRepository.save(ct);

        // 1. Xem danh sách
        mockMvc.perform(get("/khach-hang/doi-tra")
                        .cookie(cookieKhachA))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/doi-tra/danh-sach"))
                .andExpect(model().attributeExists("danhSachRma"));

        // 2. Xem chi tiết
        mockMvc.perform(get("/khach-hang/doi-tra/" + rma.getMaYeuCauRma())
                        .cookie(cookieKhachA))
                .andExpect(status().isOk())
                .andExpect(view().name("khach-hang/doi-tra/chi-tiet"))
                .andExpect(model().attributeExists("rmaDetail"))
                .andExpect(model().attribute("rmaDetail", hasProperty("danhSachSanPhamDoiTra", not(empty()))));
    }

    @Test
    @DisplayName("TC11: Tải ảnh minh chứng từ máy (Multipart) và nhập địa chỉ nhận hàng -> Lưu file vào uploads/rma và lưu thông tin nhận hàng")
    void testTaoYeuCauDoiTra_TaiAnhTuMayVaDiaChiNhanHang_ThanhCong() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
                "hinhAnhFiles",
                "loi_duong_chi_01.jpg",
                "image/jpeg",
                "fake image content 1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "hinhAnhFiles",
                "loi_vai_rach_02.png",
                "image/png",
                "fake image content 2".getBytes()
        );

        mockMvc.perform(multipart("/khach-hang/don-hang/" + donHangHoanThanh.getMaDonHang() + "/doi-tra")
                        .file(file1)
                        .file(file2)
                        .cookie(cookieKhachA)
                        .param("maDonHang", donHangHoanThanh.getMaDonHang())
                        .param("loaiDoiTra", "DOI_HANG")
                        .param("lyDo", "Sản phẩm bị bung rách đường may tại phần vai áo")
                        .param("tenNguoiNhan", "Trần Thị Nhận Hàng Mới")
                        .param("soDienThoaiNhan", "0912345678")
                        .param("diaChiNhanHang", "Số 99 Phố Cầu Giấy, Phường Dịch Vọng, Quận Cầu Giấy, Hà Nội")
                        .param("ghiChuGiaoHang", "Giao trong giờ hành chính, gọi trước khi đến")
                        .param("items[0].selected", "true")
                        .param("items[0].chiTietDonHangId", String.valueOf(chiTiet1.getId()))
                        .param("items[0].soLuongTra", "1")
                        .param("items[0].tinhTrangSanPham", "Rách chỉ vai")
                        .param("items[0].maBienTheMoiId", String.valueOf(bienTheMoi1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/khach-hang/doi-tra/RMA-*"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.clear();

        List<YeuCauDoiTra> dsa = yeuCauDoiTraRepository.findByMaDonHangIdOrderByIdDesc(donHangHoanThanh.getId());
        assertFalse(dsa.isEmpty());

        YeuCauDoiTra rma = dsa.get(0);
        assertEquals("Trần Thị Nhận Hàng Mới", rma.getTenNguoiNhan());
        assertEquals("0912345678", rma.getSoDienThoaiNhan());
        assertEquals("Số 99 Phố Cầu Giấy, Phường Dịch Vọng, Quận Cầu Giấy, Hà Nội", rma.getDiaChiNhanHang());
        assertEquals("Giao trong giờ hành chính, gọi trước khi đến", rma.getGhiChuGiaoHang());

        assertNotNull(rma.getHinhAnhMinhChung());
        assertTrue(rma.getHinhAnhMinhChung().contains("/uploads/rma/rma_"));
        List<String> listAnh = rma.getDanhSachHinhAnh();
        assertEquals(2, listAnh.size(), "Phải lưu đủ 2 ảnh minh chứng tải từ máy");
    }

    @Test
    @DisplayName("TC13: Tạo yêu cầu Đổi/Trả với địa chỉ nhận hàng từ 3 combobox Tỉnh/Huyện/Xã + Số nhà tự động ghép")
    void testTaoYeuCauDoiTra_DiaChiDropdownCascaded() throws Exception {
        mockMvc.perform(post("/khach-hang/don-hang/" + donHangHoanThanh.getMaDonHang() + "/doi-tra")
                        .cookie(cookieKhachA)
                        .param("maDonHang", donHangHoanThanh.getMaDonHang())
                        .param("loaiDoiTra", "DOI_HANG")
                        .param("lyDo", "Đổi size do mặc bị rộng eo và dài")
                        .param("tenNguoiNhan", "Lê Văn Nhận")
                        .param("soDienThoaiNhan", "0988776655")
                        .param("diaChiChiTiet", "Số 15 ngõ 180 đường Duy Tân")
                        .param("phuongXa", "Dịch Vọng Hậu")
                        .param("quanHuyen", "Cầu Giấy")
                        .param("tinhThanh", "Hà Nội")
                        .param("items[0].selected", "true")
                        .param("items[0].chiTietDonHangId", String.valueOf(chiTiet1.getId()))
                        .param("items[0].soLuongTra", "1")
                        .param("items[0].tinhTrangSanPham", "Rộng eo")
                        .param("items[0].maBienTheMoiId", String.valueOf(bienTheMoi1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/khach-hang/doi-tra/RMA-*"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.clear();

        List<YeuCauDoiTra> dsa = yeuCauDoiTraRepository.findByMaDonHangIdOrderByIdDesc(donHangHoanThanh.getId());
        assertFalse(dsa.isEmpty());

        YeuCauDoiTra rma = dsa.get(0);
        assertEquals("Lê Văn Nhận", rma.getTenNguoiNhan());
        assertEquals("0988776655", rma.getSoDienThoaiNhan());
        assertEquals("Số 15 ngõ 180 đường Duy Tân, Dịch Vọng Hậu, Cầu Giấy, Hà Nội", rma.getDiaChiNhanHang());
    }
}
