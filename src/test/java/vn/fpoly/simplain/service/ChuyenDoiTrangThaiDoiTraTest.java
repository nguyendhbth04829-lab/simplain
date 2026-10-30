package vn.fpoly.simplain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.CapNhatTrangThaiVanDonRequest;
import vn.fpoly.simplain.dto.ChiTietDoiTraItemRequest;
import vn.fpoly.simplain.dto.TaoYeuCauDoiTraRequest;
import vn.fpoly.simplain.dto.XetDuyetRmaRequest;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử tích hợp nghiệp vụ Validate Chuyển Đổi Trạng Thái Đơn Hàng Khi Đổi/Trả Hàng (RMA).
 * - Tự động đồng bộ trạng thái đơn hàng khi khách gửi yêu cầu RMA: HOAN_THANH -> DANG_DOI_TRA.
 * - Chặn Admin/Nhân viên can thiệp đổi trạng thái khi đơn hàng đang có RMA hoặc đã kết thúc RMA.
 * - Chặn chọn thủ công các trạng thái Đổi/Trả từ quy trình vận đơn thông thường.
 * - Tự động phục hồi về HOAN_THANH khi CSKH từ chối RMA.
 * - Tự động chuyển sang DA_TRA_HANG hoặc DA_DOI_HANG khi CSKH duyệt hoàn tất RMA.
 * - Ghi nhận lịch sử chi tiết đầy đủ (Audit Log).
 */
@SpringBootTest
@Transactional
@DisplayName("Kiểm thử Tích hợp Validate Chuyển Đổi Trạng Thái Đơn Hàng Khi Đổi Trả (RMA)")
class ChuyenDoiTrangThaiDoiTraTest {

    @Autowired
    private DoiTraService doiTraService;

    @Autowired
    private QuanLyVanDonService quanLyVanDonService;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichThuocRepository kichThuocRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;

    @Autowired
    private YeuCauDoiTraRepository yeuCauDoiTraRepository;

    private KhachHang khachHang;
    private NhanVien cskhActor;
    private BienTheSanPham btGoc;
    private BienTheSanPham btDoi;
    private DonHang donHangHoanThanh;
    private ChiTietDonHang chiTietDonHang;

    @BeforeEach
    void setUp() {
        long ts = System.currentTimeMillis();

        // 1. Tạo hoặc lấy các trạng thái đơn hàng cần thiết
        ensureTrangThai("HOAN_THANH", "Hoàn thành", "#10B981");
        ensureTrangThai("DANG_DOI_TRA", "Đang đổi / trả", "#D97706");
        ensureTrangThai("DA_TRA_HANG", "Đã trả hàng (Hoàn tiền)", "#E11D48");
        ensureTrangThai("DA_DOI_HANG", "Đã đổi hàng", "#2563EB");
        ensureTrangThai("CHO_XAC_NHAN", "Chờ xác nhận", "#6B7280");

        // 2. Tạo khách hàng
        khachHang = khachHangRepository.save(KhachHang.builder()
                .maKhachHang("KH-RMA-" + ts)
                .hoTen("Nguyễn Khách Test")
                .soDienThoai(String.format("0988%06d", (ts % 1000000)))
                .email("rma_test_" + ts + "@example.com")
                .matKhauHash("secret123")
                .trangThai(1)
                .build());

        // 3. Tạo nhân viên CSKH
        VaiTro vt = vaiTroRepository.findByMaVaiTro("CSKH").orElseGet(() ->
                vaiTroRepository.save(VaiTro.builder().maVaiTro("CSKH").tenVaiTro("CSKH").trangThai(1).build())
        );
        cskhActor = nhanVienRepository.save(NhanVien.builder()
                .maNhanVien("NV-RMA-" + ts)
                .hoTen("Trần CSKH Test")
                .tenDangNhap("cskh_" + ts)
                .soDienThoai(String.format("0977%06d", (ts % 1000000)))
                .matKhauHash("secret123")
                .vaiTro(vt)
                .trangThai(1)
                .build());

        // 4. Lấy dữ liệu biến thể sản phẩm có sẵn
        btGoc = bienTheSanPhamRepository.findAll().stream().findFirst().orElseGet(() ->
                bienTheSanPhamRepository.save(BienTheSanPham.builder()
                        .maSanPhamId(1L)
                        .maMauSacId(1L)
                        .maKichThuocId(1L)
                        .maSku("SKU-GOC-" + ts)
                        .maVachBarcode("BAR-GOC-" + ts)
                        .giaBan(new BigDecimal("200000"))
                        .giaGoc(new BigDecimal("200000"))
                        .soLuong(20)
                        .kichHoat(true)
                        .build())
        );

        btDoi = bienTheSanPhamRepository.save(BienTheSanPham.builder()
                .maSanPhamId(btGoc.getMaSanPhamId() != null ? btGoc.getMaSanPhamId() : 1L)
                .maMauSacId(btGoc.getMaMauSacId() != null ? btGoc.getMaMauSacId() : 1L)
                .maKichThuocId(btGoc.getMaKichThuocId() != null ? btGoc.getMaKichThuocId() : 1L)
                .maSku("SKU-DOI-" + ts)
                .maVachBarcode("BAR-DOI-" + ts)
                .giaBan(new BigDecimal("200000"))
                .giaGoc(new BigDecimal("200000"))
                .soLuong(15)
                .kichHoat(true)
                .build());

        // 5. Tạo đơn hàng đã HOAN_THANH
        TrangThaiDonHang ttHoanThanh = trangThaiDonHangRepository.findByMaTrangThai("HOAN_THANH").orElseThrow();
        donHangHoanThanh = donHangRepository.save(DonHang.builder()
                .maDonHang("DH-RMA-TEST-" + ts)
                .khachHang(khachHang)
                .tenNguoiNhan("Nguyễn Khách Test")
                .soDienThoaiNhan(khachHang.getSoDienThoai())
                .diaChiGiaoHang("123 Cầu Giấy, Hà Nội")
                .tongTienHang(new BigDecimal("400000"))
                .tongThanhToan(new BigDecimal("400000"))
                .trangThaiDonHang(ttHoanThanh)
                .ngayTao(LocalDateTime.now().minusDays(2))
                .build());

        chiTietDonHang = chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .maDonHangId(donHangHoanThanh.getId())
                .maBienTheId(btGoc.getId())
                .maSku(btGoc.getMaSku())
                .tenSanPham("Áo Thun RMA Test")
                .soLuong(2)
                .donGia(new BigDecimal("200000"))
                .thanhTien(new BigDecimal("400000"))
                .build());
    }

    private void ensureTrangThai(String ma, String ten, String hex) {
        if (!trangThaiDonHangRepository.existsByMaTrangThai(ma)) {
            trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                    .maTrangThai(ma)
                    .tenTrangThai(ten)
                    .maMauHex(hex)
                    .build());
        }
    }

    @Test
    @DisplayName("1. Khi khách tạo RMA: Đơn hàng tự động chuyển sang DANG_DOI_TRA và ghi log lịch sử")
    void testKhachTaoRma_DonHangChuyenSangDangDoiTra() {
        ChiTietDoiTraItemRequest item = ChiTietDoiTraItemRequest.builder()
                .chiTietDonHangId(chiTietDonHang.getId())
                .selected(true)
                .soLuongTra(1)
                .maBienTheMoiId(btDoi.getId())
                .tinhTrangSanPham("Áo bị chật, muốn đổi size")
                .build();

        TaoYeuCauDoiTraRequest req = TaoYeuCauDoiTraRequest.builder()
                .maDonHang(donHangHoanThanh.getMaDonHang())
                .loaiDoiTra("DOI_HANG")
                .lyDo("Không vừa kích cỡ")
                .tenNguoiNhan("Nguyễn Khách Test")
                .soDienThoaiNhan(khachHang.getSoDienThoai())
                .diaChiNhanHang("123 Cầu Giấy, Hà Nội")
                .items(List.of(item))
                .build();

        var rma = doiTraService.taoYeuCauDoiTra(req, khachHang.getId(), null);

        assertNotNull(rma);
        assertNotNull(rma.getMaYeuCauRma());

        // Kiểm tra trạng thái đơn hàng đã chuyển sang DANG_DOI_TRA
        DonHang updatedDh = donHangRepository.findById(donHangHoanThanh.getId()).orElseThrow();
        assertEquals("DANG_DOI_TRA", updatedDh.getTrangThaiDonHang().getMaTrangThai());

        // Kiểm tra log lịch sử
        List<LichSuTrangThaiDonHang> logs = lichSuTrangThaiDonHangRepository.layLichSuTheoDonHangIdAsc(updatedDh.getId());
        assertFalse(logs.isEmpty());
        LichSuTrangThaiDonHang latestLog = logs.get(logs.size() - 1);
        assertEquals("DANG_DOI_TRA", latestLog.getTrangThaiMoi().getMaTrangThai());
        assertTrue(latestLog.getGhiChu().contains("Đang đổi / trả"));
    }

    @Test
    @DisplayName("2. Chặn nhân viên tự ý đổi trạng thái đơn hàng khi đơn hàng đang có RMA (DANG_DOI_TRA)")
    void testChanNhanVienDoiTrangThai_KhiDonHangDangCoRma() {
        // Trước tiên tạo 1 RMA để đơn chuyển sang DANG_DOI_TRA
        testKhachTaoRma_DonHangChuyenSangDangDoiTra();

        // Nhân viên cố gắng cập nhật trạng thái đơn hàng sang CHO_XAC_NHAN hoặc HOAN_THANH
        CapNhatTrangThaiVanDonRequest updateReq = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("CHO_XAC_NHAN")
                .ghiChu("Cố tình can thiệp")
                .build();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                quanLyVanDonService.capNhatTrangThaiVanDon(donHangHoanThanh.getId(), updateReq, cskhActor)
        );

        assertTrue(ex.getMessage().contains("tiến trình Đổi / Trả (RMA)"));
    }

    @Test
    @DisplayName("3. Chặn nhân viên chọn thủ công các trạng thái Đổi/Trả từ dropdown cập nhật vận đơn thông thường")
    void testChanChonThuCongTrangThaiDoiTra() {
        CapNhatTrangThaiVanDonRequest updateReq = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("DANG_DOI_TRA")
                .ghiChu("Cố tình chọn trạng thái RMA")
                .build();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                quanLyVanDonService.capNhatTrangThaiVanDon(donHangHoanThanh.getId(), updateReq, cskhActor)
        );

        assertTrue(ex.getMessage().contains("nghiệp vụ Đổi/Trả (RMA)"));
    }

    @Test
    @DisplayName("4. Khi CSKH Từ chối RMA: Đơn hàng tự động phục hồi về HOAN_THANH và ghi log lý do")
    void testCskhTuChoiRma_DonHangPhucHoiHoanThanh() {
        // Tạo RMA
        testKhachTaoRma_DonHangChuyenSangDangDoiTra();
        YeuCauDoiTra rma = yeuCauDoiTraRepository.findByMaDonHangIdOrderByIdDesc(donHangHoanThanh.getId()).get(0);

        // CSKH từ chối
        XetDuyetRmaRequest xdReq = XetDuyetRmaRequest.builder()
                .trangThaiMoi("TU_CHOI")
                .ghiChuAdmin("Sản phẩm đã bị giặt rách, không đủ điều kiện đổi trả theo chính sách")
                .build();

        doiTraService.xetDuyetYeuCauRma(rma.getId(), xdReq, cskhActor);

        // Đơn hàng được khôi phục về HOAN_THANH
        DonHang updatedDh = donHangRepository.findById(donHangHoanThanh.getId()).orElseThrow();
        assertEquals("HOAN_THANH", updatedDh.getTrangThaiDonHang().getMaTrangThai());

        // Kiểm tra log ghi nhận lý do từ chối
        List<LichSuTrangThaiDonHang> logs = lichSuTrangThaiDonHangRepository.layLichSuTheoDonHangIdAsc(updatedDh.getId());
        LichSuTrangThaiDonHang latestLog = logs.get(logs.size() - 1);
        assertEquals("HOAN_THANH", latestLog.getTrangThaiMoi().getMaTrangThai());
        assertTrue(latestLog.getGhiChu().contains("TỪ CHỐI"));
        assertTrue(latestLog.getGhiChu().contains("Sản phẩm đã bị giặt rách"));
    }

    @Test
    @DisplayName("5. Khi CSKH Duyệt hoàn tất Trả hàng hoàn tiền: Đơn hàng chuyển sang DA_TRA_HANG và khóa sửa đổi")
    void testCskhHoanTatTraHang_DonHangChuyenSangDaTraHang() {
        // Tạo RMA Trả hàng hoàn tiền
        ChiTietDoiTraItemRequest item = ChiTietDoiTraItemRequest.builder()
                .chiTietDonHangId(chiTietDonHang.getId())
                .selected(true)
                .soLuongTra(1)
                .tinhTrangSanPham("Hàng lỗi chỉ may")
                .build();

        TaoYeuCauDoiTraRequest req = TaoYeuCauDoiTraRequest.builder()
                .maDonHang(donHangHoanThanh.getMaDonHang())
                .loaiDoiTra("TRA_HANG_HOAN_TIEN")
                .lyDo("Hàng lỗi nhà sản xuất")
                .tenNganHang("MB Bank")
                .soTaiKhoan("0988888888")
                .tenChuTaiKhoan("NGUYEN KHACH TEST")
                .tenNguoiNhan("Nguyễn Khách Test")
                .soDienThoaiNhan(khachHang.getSoDienThoai())
                .diaChiNhanHang("123 Cầu Giấy, Hà Nội")
                .items(List.of(item))
                .build();

        var rmaResponse = doiTraService.taoYeuCauDoiTra(req, khachHang.getId(), null);
        YeuCauDoiTra rma = yeuCauDoiTraRepository.findByMaYeuCauRma(rmaResponse.getMaYeuCauRma()).orElseThrow();

        // Tuân thủ State Machine: Đặt rma đã qua tiếp nhận và đang thẩm định kho (DANG_XU_LY) trước khi hoàn tất
        rma.setTrangThai("DANG_XU_LY");
        yeuCauDoiTraRepository.saveAndFlush(rma);

        // CSKH duyệt hoàn tất
        XetDuyetRmaRequest xdReq = XetDuyetRmaRequest.builder()
                .trangThaiMoi("HOAN_THANH")
                .ghiChuAdmin("Đã nhận lại hàng và chuyển khoản hoàn tiền thành công")
                .maGiaoDichNganHang("TXN-MB-99998888")
                .build();

        doiTraService.xetDuyetYeuCauRma(rma.getId(), xdReq, cskhActor);

        // Đơn hàng chuyển sang DA_TRA_HANG
        DonHang updatedDh = donHangRepository.findById(donHangHoanThanh.getId()).orElseThrow();
        assertEquals("DA_TRA_HANG", updatedDh.getTrangThaiDonHang().getMaTrangThai());

        // Chặn nhân viên can thiệp sau khi đơn hàng đã kết thúc hoàn tiền
        CapNhatTrangThaiVanDonRequest updateReq = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("HOAN_THANH")
                .ghiChu("Cố tình đổi lại")
                .build();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                quanLyVanDonService.capNhatTrangThaiVanDon(donHangHoanThanh.getId(), updateReq, cskhActor)
        );
        assertTrue(ex.getMessage().contains("Trả hàng") || ex.getMessage().contains("kết thúc"));
    }

    @Test
    @DisplayName("6. Khi CSKH Duyệt hoàn tất Đổi hàng: Đơn hàng chuyển sang DA_DOI_HANG và trừ kho SKU mới")
    void testCskhHoanTatDoiHang_DonHangChuyenSangDaDoiHang() {
        int tonKhoMoiBanDau = btDoi.getSoLuong();

        // Tạo RMA Đổi hàng
        testKhachTaoRma_DonHangChuyenSangDangDoiTra();
        YeuCauDoiTra rma = yeuCauDoiTraRepository.findByMaDonHangIdOrderByIdDesc(donHangHoanThanh.getId()).get(0);

        // Tuân thủ State Machine: Kiện hàng đã về kho và đang kiểm định chất lượng (DANG_XU_LY)
        rma.setTrangThai("DANG_XU_LY");
        yeuCauDoiTraRepository.saveAndFlush(rma);

        // CSKH duyệt hoàn tất
        XetDuyetRmaRequest xdReq = XetDuyetRmaRequest.builder()
                .trangThaiMoi("HOAN_THANH")
                .ghiChuAdmin("Đã kiểm định hàng cũ đạt chuẩn, đã gửi hàng mới cho khách")
                .build();

        doiTraService.xetDuyetYeuCauRma(rma.getId(), xdReq, cskhActor);

        // Đơn hàng chuyển sang DA_DOI_HANG
        DonHang updatedDh = donHangRepository.findById(donHangHoanThanh.getId()).orElseThrow();
        assertEquals("DA_DOI_HANG", updatedDh.getTrangThaiDonHang().getMaTrangThai());

        // Kiểm tra tồn kho SKU mới bị trừ đi 1
        BienTheSanPham btMoiSauDoi = bienTheSanPhamRepository.findById(btDoi.getId()).orElseThrow();
        assertEquals(tonKhoMoiBanDau - 1, btMoiSauDoi.getSoLuong());

        // Chặn nhân viên can thiệp sau khi đơn hàng đã kết thúc đổi hàng
        CapNhatTrangThaiVanDonRequest updateReq = CapNhatTrangThaiVanDonRequest.builder()
                .maTrangThai("HOAN_THANH")
                .ghiChu("Cố tình đổi lại")
                .build();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                quanLyVanDonService.capNhatTrangThaiVanDon(donHangHoanThanh.getId(), updateReq, cskhActor)
        );
        assertTrue(ex.getMessage().contains("Đã đổi hàng") || ex.getMessage().contains("kết thúc"));
    }

    @Test
    @DisplayName("7. Chặn nhảy cóc từ CHO_XU_LY sang DANG_XU_LY (từ 1 sang 3 luôn) khi duyệt RMA")
    void testChanNhayCocRma_TuChoXuLySangDangXuLy() {
        testKhachTaoRma_DonHangChuyenSangDangDoiTra();
        YeuCauDoiTra rma = yeuCauDoiTraRepository.findByMaDonHangIdOrderByIdDesc(donHangHoanThanh.getId()).get(0);
        assertEquals("CHO_XU_LY", rma.getTrangThai());

        XetDuyetRmaRequest jumpReq = XetDuyetRmaRequest.builder()
                .trangThaiMoi("DANG_XU_LY")
                .ghiChuAdmin("Cố tình nhảy từ 1 sang 3 luôn")
                .build();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                doiTraService.xetDuyetYeuCauRma(rma.getId(), jumpReq, cskhActor)
        );
        assertTrue(ex.getMessage().contains("nhảy cóc") && ex.getMessage().contains("Đang xử lý / Kiểm định kho"));
    }

    @Test
    @DisplayName("8. Chặn nhảy cóc từ CHO_XU_LY sang HOAN_THANH khi duyệt RMA")
    void testChanNhayCocRma_TuChoXuLySangHoanThanh() {
        testKhachTaoRma_DonHangChuyenSangDangDoiTra();
        YeuCauDoiTra rma = yeuCauDoiTraRepository.findByMaDonHangIdOrderByIdDesc(donHangHoanThanh.getId()).get(0);

        XetDuyetRmaRequest jumpReq = XetDuyetRmaRequest.builder()
                .trangThaiMoi("HOAN_THANH")
                .ghiChuAdmin("Cố tình hoàn tất luôn khi chưa có hàng về kho")
                .build();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                doiTraService.xetDuyetYeuCauRma(rma.getId(), jumpReq, cskhActor)
        );
        assertTrue(ex.getMessage().contains("nhảy cóc") && ex.getMessage().contains("Hoàn thành"));
    }
}
