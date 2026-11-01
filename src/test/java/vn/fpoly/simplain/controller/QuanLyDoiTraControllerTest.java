package vn.fpoly.simplain.controller;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
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
 * Kiểm thử tích hợp Web MVC cho chức năng CSKH Xét duyệt Yêu cầu Đổi/Trả (RMA) (US25 - Mod 5 - Hưng).
 * - Tiêu chí 1: Xem danh sách RMA (lọc tab trạng thái, tìm kiếm, phân trang).
 * - Tiêu chí 2: Đổi trạng thái (Approve: Đã tiếp nhận/Đang xử lý/Hoàn thành; Reject: Từ chối).
 * - Tiêu chí 3: Bổ sung ghi chú kiểm định hàng về kho.
 * - Form Validation: Kiểm tra bắt buộc lý do từ chối, trừ tồn kho SKU mới khi đổi hàng thành công,
 *   chặn sửa phiếu đã kết thúc.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Kiểm thử Web MVC CSKH Xét duyệt Yêu cầu Đổi/Trả (US25)")
class QuanLyDoiTraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private YeuCauDoiTraRepository yeuCauDoiTraRepository;

    @Autowired
    private ChiTietSanPhamDoiTraRepository chiTietSanPhamDoiTraRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;

    @Autowired
    private EntityManager entityManager;

    private NhanVien cskhNhanVien;
    private KhachHang testKhachHang;
    private DonHang testDonHang;
    private ChiTietDonHang testChiTietDonHang;
    private BienTheSanPham btCu;
    private BienTheSanPham btMoi;
    private YeuCauDoiTra rmaChoXuLyDoiHang;
    private YeuCauDoiTra rmaChoXuLyHoanTien;

    @BeforeEach
    void setUp() {
        long timestamp = System.currentTimeMillis();

        // 1. Tạo vai trò và nhân viên CSKH
        VaiTro vtCskh = vaiTroRepository.findByMaVaiTro("CSKH").orElseGet(() -> {
            VaiTro vt = VaiTro.builder()
                    .maVaiTro("CSKH")
                    .tenVaiTro("Chăm sóc khách hàng")
                    .trangThai(1)
                    .build();
            return vaiTroRepository.save(vt);
        });

        String sdtNv = String.format("0977%06d", (timestamp % 1000000));
        cskhNhanVien = nhanVienRepository.save(NhanVien.builder()
                .maNhanVien("NV-CSKH-" + timestamp)
                .tenDangNhap("cskh_" + timestamp)
                .matKhauHash("hash123")
                .hoTen("Trần Thị CSKH " + timestamp)
                .soDienThoai(sdtNv)
                .email(sdtNv + "@simplain.vn")
                .vaiTro(vtCskh)
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build());

        // 2. Tạo khách hàng test
        String sdtKh = String.format("0966%06d", (timestamp % 1000000));
        testKhachHang = khachHangRepository.save(KhachHang.builder()
                .maKhachHang("KH-US25-" + timestamp)
                .hoTen("Lê Văn Mua Hàng")
                .soDienThoai(sdtKh)
                .email(sdtKh + "@simplain.vn")
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build());

        // 3. Trạng thái đơn hàng
        TrangThaiDonHang ttHoanThanh = trangThaiDonHangRepository.findByMaTrangThai("HOAN_THANH")
                .orElseGet(() -> trangThaiDonHangRepository.findById(4L).orElseThrow());

        // 4. Biến thể sản phẩm test
        btCu = bienTheSanPhamRepository.findAll().stream().findFirst().orElseGet(() -> {
            BienTheSanPham b = BienTheSanPham.builder()
                    .maSanPhamId(1L).maMauSacId(1L).maKichThuocId(1L)
                    .maSku("SKU-OLD-" + timestamp)
                    .soLuong(20)
                    .giaBan(new BigDecimal("250000"))
                    .giaGoc(new BigDecimal("300000"))
                    .kichHoat(true)
                    .build();
            return bienTheSanPhamRepository.save(b);
        });

        btMoi = bienTheSanPhamRepository.save(BienTheSanPham.builder()
                .maSanPhamId(btCu.getMaSanPhamId() != null ? btCu.getMaSanPhamId() : 1L)
                .maMauSacId(btCu.getMaMauSacId() != null ? btCu.getMaMauSacId() : 1L)
                .maKichThuocId(btCu.getMaKichThuocId() != null ? btCu.getMaKichThuocId() : 1L)
                .maSku("SKU-NEW-EXCHANGE-" + timestamp)
                .soLuong(50) // Có 50 cái tồn kho
                .giaBan(new BigDecimal("250000"))
                .giaGoc(new BigDecimal("300000"))
                .kichHoat(true)
                .build());

        // 5. Đơn hàng hoàn thành
        String maDh = "SMPL-US25-DH-" + timestamp;
        testDonHang = donHangRepository.save(DonHang.builder()
                .maDonHang(maDh)
                .khachHang(testKhachHang)
                .trangThaiDonHang(ttHoanThanh)
                .loaiDonHang("ONLINE")
                .tongTienHang(new BigDecimal("500000"))
                .phiVanChuyen(BigDecimal.ZERO)
                .tongThanhToan(new BigDecimal("500000"))
                .tenNguoiNhan("Lê Văn Mua Hàng")
                .soDienThoaiNhan(sdtKh)
                .diaChiGiaoHang("789 Cầu Giấy, Hà Nội")
                .ngayTao(LocalDateTime.now().minusDays(3))
                .build());

        testChiTietDonHang = chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .maDonHangId(testDonHang.getId())
                .maBienTheId(btCu.getId())
                .tenSanPham("Áo Sơ Mi Nam Oxford")
                .maSku(btCu.getMaSku())
                .donGia(new BigDecimal("250000"))
                .soLuong(2)
                .thanhTien(new BigDecimal("500000"))
                .build());

        // 6. Tạo 1 phiếu RMA Đổi Hàng
        rmaChoXuLyDoiHang = yeuCauDoiTraRepository.save(YeuCauDoiTra.builder()
                .maYeuCauRma("RMA-US25-EX-" + timestamp)
                .maDonHangId(testDonHang.getId())
                .maKhachHangId(testKhachHang.getId())
                .loaiDoiTra("DOI_HANG")
                .lyDo("Áo bị sứt chỉ bên sườn, muốn đổi size mới")
                .trangThai("CHO_XU_LY")
                .soTienHoan(BigDecimal.ZERO)
                .ngayTao(LocalDateTime.now().minusHours(4))
                .build());

        chiTietSanPhamDoiTraRepository.save(ChiTietSanPhamDoiTra.builder()
                .maYeuCauDoiTraId(rmaChoXuLyDoiHang.getId())
                .maChiTietDonHangId(testChiTietDonHang.getId())
                .soLuongTra(1)
                .maBienTheMoiId(btMoi.getId())
                .tinhTrangSanPham("Bung chỉ may nách áo")
                .build());

        // 7. Tạo 1 phiếu RMA Hoàn Tiền
        rmaChoXuLyHoanTien = yeuCauDoiTraRepository.save(YeuCauDoiTra.builder()
                .maYeuCauRma("RMA-US25-RF-" + timestamp)
                .maDonHangId(testDonHang.getId())
                .maKhachHangId(testKhachHang.getId())
                .loaiDoiTra("TRA_HANG_HOAN_TIEN")
                .lyDo("Giao sai màu sắc so với mô tả")
                .trangThai("CHO_XU_LY")
                .soTienHoan(new BigDecimal("250000"))
                .thongTinNganHang("Vietcombank - STK: 123456789 - LE VAN MUA HANG")
                .ngayTao(LocalDateTime.now().minusHours(2))
                .build());

        chiTietSanPhamDoiTraRepository.save(ChiTietSanPhamDoiTra.builder()
                .maYeuCauDoiTraId(rmaChoXuLyHoanTien.getId())
                .maChiTietDonHangId(testChiTietDonHang.getId())
                .soLuongTra(1)
                .tinhTrangSanPham("Sai mẫu")
                .build());
    }

    @Test
    @DisplayName("TC1: CSKH truy cập danh sách RMA -> Hiển thị danh sách và số lượng thẻ KPI (Tiêu chí 1)")
    void testHienThiDanhSachRma() throws Exception {
        mockMvc.perform(get("/admin/doi-tra/hien-thi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/doi-tra/hien-thi"))
                .andExpect(model().attributeExists("trangRma", "demTrangThai"))
                .andExpect(model().attribute("trangRma", hasProperty("content", not(empty()))))
                .andExpect(model().attribute("demTrangThai", hasKey("TAT_CA")));
    }

    @Test
    @DisplayName("TC2: Lọc danh sách RMA theo tab trạng thái CHO_XU_LY và hình thức DOI_HANG")
    void testLocDanhSachRmaTheoTrangThaiVaHinhThuc() throws Exception {
        mockMvc.perform(get("/admin/doi-tra/hien-thi")
                        .param("trangThai", "CHO_XU_LY")
                        .param("loaiDoiTra", "DOI_HANG"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/doi-tra/hien-thi"))
                .andExpect(model().attribute("trangThaiHienTai", "CHO_XU_LY"))
                .andExpect(model().attribute("loaiDoiTraHienTai", "DOI_HANG"));
    }

    @Test
    @DisplayName("TC3: Tìm kiếm RMA theo từ khóa mã RMA hoặc tên khách hàng")
    void testTimKiemRmaTheoTuKhoa() throws Exception {
        mockMvc.perform(get("/admin/doi-tra/hien-thi")
                        .param("tuKhoa", rmaChoXuLyDoiHang.getMaYeuCauRma()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/doi-tra/hien-thi"))
                .andExpect(model().attribute("trangRma", hasProperty("content", hasSize(greaterThanOrEqualTo(1)))));
    }

    @Test
    @DisplayName("TC4: Xem chi tiết phiếu RMA -> Hiển thị chi tiết mặt hàng, lỗi khách báo và thông tin hoàn tiền")
    void testXemChiTietRma() throws Exception {
        mockMvc.perform(get("/admin/doi-tra/chi-tiet/" + rmaChoXuLyHoanTien.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/doi-tra/chi-tiet"))
                .andExpect(model().attributeExists("rmaDetail", "xetDuyetRequest"))
                .andExpect(model().attribute("rmaDetail", hasProperty("danhSachSanPhamDoiTra", not(empty()))));
    }

    @Test
    @DisplayName("TC5: Xét duyệt Approve phiếu RMA -> Cập nhật sang trạng thái DA_TIEP_NHAN (Tiêu chí 2)")
    void testXetDuyetRma_Approve_TiepNhan() throws Exception {
        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyDoiHang.getId())
                        .param("trangThaiMoi", "DA_TIEP_NHAN")
                        .param("ghiChuAdmin", "Đã tiếp nhận yêu cầu đổi hàng. Shipper Simplain sẽ đến lấy hàng."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/doi-tra/chi-tiet/" + rmaChoXuLyDoiHang.getId()))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.clear();

        YeuCauDoiTra updated = yeuCauDoiTraRepository.findById(rmaChoXuLyDoiHang.getId()).orElseThrow();
        assertEquals("DA_TIEP_NHAN", updated.getTrangThai());
        assertTrue(updated.getGhiChuAdmin().contains("Đã tiếp nhận yêu cầu đổi hàng"));
    }

    @Test
    @DisplayName("TC6: Hàng về kho - Bổ sung ghi chú kiểm định chất lượng sản phẩm (Tiêu chí 3)")
    void testXetDuyetRma_KiemDinhHangVeKho() throws Exception {
        // Đặt trạng thái trước đó là DA_TIEP_NHAN theo đúng quy chuẩn State Machine
        rmaChoXuLyDoiHang.setTrangThai("DA_TIEP_NHAN");
        yeuCauDoiTraRepository.save(rmaChoXuLyDoiHang);
        entityManager.flush();

        // Cập nhật sang DANG_XU_LY kèm ghi chú kiểm định kho
        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyDoiHang.getId())
                        .param("trangThaiMoi", "DANG_XU_LY")
                        .param("ghiChuAdmin", "Đã nhận hàng trả về kho: Đúng lỗi bung nách áo, tem mác nguyên vẹn đạt chuẩn đổi."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/doi-tra/chi-tiet/" + rmaChoXuLyDoiHang.getId()))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.clear();

        YeuCauDoiTra updated = yeuCauDoiTraRepository.findById(rmaChoXuLyDoiHang.getId()).orElseThrow();
        assertEquals("DANG_XU_LY", updated.getTrangThai());
        assertTrue(updated.getGhiChuAdmin().contains("Đã nhận hàng trả về kho"));
    }

    @Test
    @DisplayName("TC7: Form Validation - Từ chối (Reject) nhưng để trống lý do -> Bị bắt lỗi validation")
    void testXetDuyetRma_TuChoiThieuLyDo_BaoLoi() throws Exception {
        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyDoiHang.getId())
                        .param("trangThaiMoi", "TU_CHOI")
                        .param("ghiChuAdmin", "")) // Để trống lý do
                .andExpect(status().isOk())
                .andExpect(view().name("admin/doi-tra/chi-tiet"))
                .andExpect(model().attributeExists("thongBaoLoi"));

        entityManager.clear();
        YeuCauDoiTra notChanged = yeuCauDoiTraRepository.findById(rmaChoXuLyDoiHang.getId()).orElseThrow();
        assertEquals("CHO_XU_LY", notChanged.getTrangThai(), "Trạng thái không được thay đổi nếu validate thất bại");
    }

    @Test
    @DisplayName("TC8: Từ chối (Reject) phiếu RMA thành công khi cung cấp đầy đủ lý do từ chối hợp lệ (Tiêu chí 2)")
    void testXetDuyetRma_TuChoiThanhCong() throws Exception {
        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyDoiHang.getId())
                        .param("trangThaiMoi", "TU_CHOI")
                        .param("ghiChuAdmin", "Sản phẩm đã qua giặt tẩy và bị cắt mất tem mác bảo hành, vi phạm chính sách."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/doi-tra/chi-tiet/" + rmaChoXuLyDoiHang.getId()))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.clear();

        YeuCauDoiTra updated = yeuCauDoiTraRepository.findById(rmaChoXuLyDoiHang.getId()).orElseThrow();
        assertEquals("TU_CHOI", updated.getTrangThai());
        assertTrue(updated.getGhiChuAdmin().contains("cắt mất tem mác"));
    }

    @Test
    @DisplayName("TC9: Hoàn tất ĐỔI HÀNG (HOAN_THANH) -> Trừ tồn kho biến thể SKU mới chính xác (Tiêu chí 2)")
    void testXetDuyetRma_HoanTatDoiHang_TruTonKho() throws Exception {
        int tonKhoMoiBanDau = btMoi.getSoLuong(); // 50

        // Đặt trạng thái trước đó là DANG_XU_LY theo đúng quy chuẩn State Machine
        rmaChoXuLyDoiHang.setTrangThai("DANG_XU_LY");
        yeuCauDoiTraRepository.save(rmaChoXuLyDoiHang);
        entityManager.flush();

        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyDoiHang.getId())
                        .param("trangThaiMoi", "HOAN_THANH")
                        .param("ghiChuAdmin", "Đã kiểm định đạt và gửi hàng mới cho khách."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/doi-tra/chi-tiet/" + rmaChoXuLyDoiHang.getId()))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.clear();

        YeuCauDoiTra updated = yeuCauDoiTraRepository.findById(rmaChoXuLyDoiHang.getId()).orElseThrow();
        assertEquals("HOAN_THANH", updated.getTrangThai());

        // Kiểm tra biến thể mới đã bị trừ 1 chiếc
        BienTheSanPham btMoiSau = bienTheSanPhamRepository.findById(btMoi.getId()).orElseThrow();
        assertEquals(tonKhoMoiBanDau - 1, btMoiSau.getSoLuong(), "Biến thể mới phải được trừ 1 chiếc trong kho");
    }

    @Test
    @DisplayName("TC10: Hoàn tất TRẢ HÀNG HOÀN TIỀN (HOAN_THANH) -> Ghi nhận mã GD chuyển khoản hoàn tiền")
    void testXetDuyetRma_HoanTatHoanTien() throws Exception {
        // Đặt trạng thái trước đó là DANG_XU_LY theo đúng quy chuẩn State Machine
        rmaChoXuLyHoanTien.setTrangThai("DANG_XU_LY");
        yeuCauDoiTraRepository.save(rmaChoXuLyHoanTien);
        entityManager.flush();

        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyHoanTien.getId())
                        .param("trangThaiMoi", "HOAN_THANH")
                        .param("maGiaoDichNganHang", "FT261027998877")
                        .param("ghiChuAdmin", "Đã chuyển khoản hoàn 250.000đ cho khách hàng thành công."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/doi-tra/chi-tiet/" + rmaChoXuLyHoanTien.getId()))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        entityManager.clear();

        YeuCauDoiTra updated = yeuCauDoiTraRepository.findById(rmaChoXuLyHoanTien.getId()).orElseThrow();
        assertEquals("HOAN_THANH", updated.getTrangThai());
        assertTrue(updated.getGhiChuAdmin().contains("FT261027998877"));
    }

    @Test
    @DisplayName("TC11: Chặn xét duyệt phiếu RMA khi đã ở trạng thái kết thúc (HOAN_THANH / TU_CHOI)")
    void testChanSuaRmaKhiDaKetThuc() throws Exception {
        // Đặt rma thành HOAN_THANH trước
        rmaChoXuLyDoiHang.setTrangThai("HOAN_THANH");
        yeuCauDoiTraRepository.saveAndFlush(rmaChoXuLyDoiHang);
        entityManager.clear();

        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyDoiHang.getId())
                        .param("trangThaiMoi", "TU_CHOI")
                        .param("ghiChuAdmin", "Cố tình từ chối đơn đã xong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("thongBaoLoi"));
    }

    @Test
    @DisplayName("TC12: API AJAX Xét duyệt RMA nhanh từ modal -> Phản hồi JSON 200 thành công")
    void testApiXetDuyetRmaAjax() throws Exception {
        String jsonBody = """
                {
                    "trangThaiMoi": "DA_TIEP_NHAN",
                    "ghiChuAdmin": "Duyệt nhanh qua modal AJAX"
                }
                """;

        mockMvc.perform(post("/admin/doi-tra/api/xet-duyet/" + rmaChoXuLyDoiHang.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.trangThai").value("DA_TIEP_NHAN"));
    }

    @Test
    @DisplayName("TC13: Chặn nhảy cóc từ CHO_XU_LY sang DANG_XU_LY (từ 1 sang 3 luôn)")
    void testChanNhayCoc_TuChoXuLySangDangXuLy() throws Exception {
        // rmaChoXuLyDoiHang đang ở CHO_XU_LY
        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyDoiHang.getId())
                        .param("trangThaiMoi", "DANG_XU_LY")
                        .param("ghiChuAdmin", "Cố tình nhảy cóc sang kiểm định kho"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/doi-tra/chi-tiet/" + rmaChoXuLyDoiHang.getId()))
                .andExpect(flash().attribute("thongBaoLoi", containsString("nhảy cóc")));

        entityManager.clear();
        YeuCauDoiTra check = yeuCauDoiTraRepository.findById(rmaChoXuLyDoiHang.getId()).orElseThrow();
        assertEquals("CHO_XU_LY", check.getTrangThai(), "Trạng thái không được đổi khi vi phạm quy trình");
    }

    @Test
    @DisplayName("TC14: Chặn nhảy cóc từ CHO_XU_LY sang HOAN_THANH")
    void testChanNhayCoc_TuChoXuLySangHoanThanh() throws Exception {
        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyDoiHang.getId())
                        .param("trangThaiMoi", "HOAN_THANH")
                        .param("ghiChuAdmin", "Cố tình hoàn tất luôn khi khách chưa gửi hàng"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/doi-tra/chi-tiet/" + rmaChoXuLyDoiHang.getId()))
                .andExpect(flash().attribute("thongBaoLoi", containsString("nhảy cóc")));

        entityManager.clear();
        YeuCauDoiTra check = yeuCauDoiTraRepository.findById(rmaChoXuLyDoiHang.getId()).orElseThrow();
        assertEquals("CHO_XU_LY", check.getTrangThai());
    }

    @Test
    @DisplayName("TC15: Chặn nhảy cóc từ DA_TIEP_NHAN sang HOAN_THANH khi chưa kiểm định kho")
    void testChanNhayCoc_TuDaTiepNhanSangHoanThanh() throws Exception {
        rmaChoXuLyDoiHang.setTrangThai("DA_TIEP_NHAN");
        yeuCauDoiTraRepository.saveAndFlush(rmaChoXuLyDoiHang);
        entityManager.clear();

        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyDoiHang.getId())
                        .param("trangThaiMoi", "HOAN_THANH")
                        .param("ghiChuAdmin", "Hàng chưa về kho mà đã đòi hoàn tất"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/doi-tra/chi-tiet/" + rmaChoXuLyDoiHang.getId()))
                .andExpect(flash().attribute("thongBaoLoi", containsString("nhảy cóc")));
    }

    @Test
    @DisplayName("TC16: Chặn đảo ngược trạng thái từ DANG_XU_LY về DA_TIEP_NHAN")
    void testChanDaoNguoc_TuDangXuLyVeDaTiepNhan() throws Exception {
        rmaChoXuLyDoiHang.setTrangThai("DANG_XU_LY");
        yeuCauDoiTraRepository.saveAndFlush(rmaChoXuLyDoiHang);
        entityManager.clear();

        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyDoiHang.getId())
                        .param("trangThaiMoi", "DA_TIEP_NHAN")
                        .param("ghiChuAdmin", "Cố tình quay lùi về đã tiếp nhận"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/doi-tra/chi-tiet/" + rmaChoXuLyDoiHang.getId()))
                .andExpect(flash().attribute("thongBaoLoi", containsString("đảo ngược")));
    }

    @Test
    @DisplayName("TC17: Chặn hoàn tất Trả hàng hoàn tiền nếu thiếu Mã giao dịch ngân hàng")
    void testChanHoanTat_ThieuMaGiaoDichNganHang() throws Exception {
        rmaChoXuLyHoanTien.setTrangThai("DANG_XU_LY");
        yeuCauDoiTraRepository.saveAndFlush(rmaChoXuLyHoanTien);
        entityManager.clear();

        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyHoanTien.getId())
                        .param("trangThaiMoi", "HOAN_THANH")
                        .param("ghiChuAdmin", "Đạt chuẩn hoàn tiền")
                        .param("maGiaoDichNganHang", "")) // Bỏ trống mã GD
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/doi-tra/chi-tiet/" + rmaChoXuLyHoanTien.getId()))
                .andExpect(flash().attribute("thongBaoLoi", containsString("Mã giao dịch ngân hàng")));
    }

    @Test
    @DisplayName("TC18: API tự động sinh mã giao dịch ngân hàng / RMA chuẩn xác")
    void testApiSinhMaGiaoDichTuDong() throws Exception {
        mockMvc.perform(get("/admin/doi-tra/api/sinh-ma-giao-dich")
                        .param("loaiDoiTra", "TRA_HANG_HOAN_TIEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.maGiaoDich", startsWith("TXN-REF-")));

        mockMvc.perform(get("/admin/doi-tra/api/sinh-ma-giao-dich")
                        .param("loaiDoiTra", "DOI_HANG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.maGiaoDich", startsWith("TXN-EX-")));
    }

    @Test
    @DisplayName("TC19: CSKH xét duyệt RMA lưu kèm link ảnh bill hoàn tiền gửi cho khách")
    void testXetDuyetKemAnhBill() throws Exception {
        rmaChoXuLyHoanTien.setTrangThai("DANG_XU_LY");
        yeuCauDoiTraRepository.saveAndFlush(rmaChoXuLyHoanTien);
        entityManager.clear();

        String linkBill = "https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=600";

        mockMvc.perform(post("/admin/doi-tra/xet-duyet/" + rmaChoXuLyHoanTien.getId())
                        .param("trangThaiMoi", "HOAN_THANH")
                        .param("ghiChuAdmin", "Đã kiểm tra chuyển khoản thành công")
                        .param("maGiaoDichNganHang", "TXN-REF-20261027-999888")
                        .param("hinhAnhBill", linkBill))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/doi-tra/chi-tiet/" + rmaChoXuLyHoanTien.getId()));

        YeuCauDoiTra updated = yeuCauDoiTraRepository.findById(rmaChoXuLyHoanTien.getId()).orElseThrow();
        assertEquals("HOAN_THANH", updated.getTrangThai());
        assertEquals(linkBill, updated.getHinhAnhBill());
    }
}
