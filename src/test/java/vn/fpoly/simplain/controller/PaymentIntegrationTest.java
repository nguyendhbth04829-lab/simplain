package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.dto.PaymentUrlResponse;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;
import vn.fpoly.simplain.service.GiaoDichThanhToanService;
import vn.fpoly.simplain.service.payment.VNPayService;
import vn.fpoly.simplain.util.CryptoUtils;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động Tích hợp Cổng thanh toán VNPay/MoMo (US20).
 * - Tiêu chí 1: Generate URL quét QR.
 * - Tiêu chí 2: Xử lý IPN Webhook trả về từ ngân hàng.
 * - Tiêu chí 3: Lưu giao_dich_thanh_toan.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử Tích hợp Cổng thanh toán (US20)")
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private GiaoDichThanhToanService giaoDichThanhToanService;

    @Autowired
    private GiaoDichThanhToanRepository giaoDichThanhToanRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    private DonHang donHangTest;
    private PhuongThucThanhToan ptttVnpay;

    @BeforeEach
    void setUp() {
        // Cổng VNPay Test
        ptttVnpay = phuongThucThanhToanRepository.findByMaPhuongThucIgnoreCase("VNPAY_QR")
                .orElseGet(() -> phuongThucThanhToanRepository.save(PhuongThucThanhToan.builder()
                        .maPhuongThuc("VNPAY_QR")
                        .tenPhuongThuc("Cổng thanh toán VNPay")
                        .laTrucTuyen(true)
                        .kichHoat(true)
                        .cauHinh("{\"tmn_code\":\"SIMPLAIN01\",\"hash_secret\":\"RAI9EFCJ891283JKSDH21\"}")
                        .build()));

        TrangThaiDonHang ttCho = trangThaiDonHangRepository.findByMaTrangThai("CHO_XAC_NHAN")
                .orElseGet(() -> trangThaiDonHangRepository.findById(1L).orElseThrow());

        // Đơn hàng test
        String maDon = "DH_TEST_US20_" + System.currentTimeMillis();
        donHangTest = donHangRepository.save(DonHang.builder()
                .maDonHang(maDon)
                .trangThaiDonHang(ttCho)
                .phuongThucThanhToan(ptttVnpay)
                .tongTienHang(new BigDecimal("350000"))
                .phiVanChuyen(BigDecimal.ZERO)
                .tongThanhToan(new BigDecimal("350000"))
                .tenNguoiNhan("Trần Thanh Toán")
                .soDienThoaiNhan("0912345678")
                .diaChiGiaoHang("123 Phố Cầu Giấy, Hà Nội")
                .ngayTao(LocalDateTime.now())
                .build());
    }

    @Test
    @DisplayName("TC1 - Generate URL thanh toán VNPay chứa chữ ký HMAC-SHA512 và mã QR (AC 1)")
    void testGenerateUrlVNPayVaQr() {
        String maGiaoDichNoiBo = "TXN_TEST_" + System.currentTimeMillis();
        String returnUrl = "http://localhost:8080/thanh-toan/vnpay-return";

        PaymentUrlResponse response = vnPayService.taoUrlThanhToan(
                donHangTest, maGiaoDichNoiBo, "127.0.0.1", returnUrl, ptttVnpay
        );

        assertNotNull(response);
        assertNotNull(response.getPaymentUrl());
        assertTrue(response.getPaymentUrl().contains("vnp_SecureHash="));
        assertTrue(response.getPaymentUrl().contains("vnp_TmnCode="));
        assertTrue(response.getPaymentUrl().contains("vnp_Amount=35000000")); // Số tiền nhân 100
        assertNotNull(response.getQrCodeUrl());
        assertEquals("VNPAY_QR", response.getMaPhuongThuc());
    }

    @Test
    @DisplayName("TC2 - Xử lý Webhook IPN VNPay thành công và cập nhật giao_dich_thanh_toan (AC 2 & 3)")
    void testXuLyIpnVNPayThanhCong() throws Exception {
        // Khởi tạo giao dịch
        GiaoDichThanhToan gd = giaoDichThanhToanService.khoiTaoGiaoDich(donHangTest, ptttVnpay);
        String txnRef = gd.getMaGiaoDichNoiBo();
        String hashSecret = "RAI9EFCJ891283JKSDH21";

        // Chuẩn bị các tham số giả lập từ VNPay
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Amount", "35000000");
        vnpParams.put("vnp_BankCode", "NCB");
        vnpParams.put("vnp_CardType", "ATM");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang #" + donHangTest.getMaDonHang());
        vnpParams.put("vnp_PayDate", "20261025120000");
        vnpParams.put("vnp_ResponseCode", "00"); // Thành công
        vnpParams.put("vnp_TmnCode", "SIMPLAIN01");
        vnpParams.put("vnp_TransactionNo", "14567890");
        vnpParams.put("vnp_TransactionStatus", "00");
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_Version", "2.1.0");

        // Tính toán chữ ký hợp lệ
        StringBuilder hashData = new StringBuilder();
        hashData.append("vnp_Amount=").append(vnpParams.get("vnp_Amount"))
                .append("&vnp_BankCode=").append(vnpParams.get("vnp_BankCode"))
                .append("&vnp_CardType=").append(vnpParams.get("vnp_CardType"))
                .append("&vnp_Command=").append(vnpParams.get("vnp_Command"))
                .append("&vnp_OrderInfo=").append(URLEncoder.encode(vnpParams.get("vnp_OrderInfo"), StandardCharsets.US_ASCII.toString()))
                .append("&vnp_PayDate=").append(vnpParams.get("vnp_PayDate"))
                .append("&vnp_ResponseCode=").append(vnpParams.get("vnp_ResponseCode"))
                .append("&vnp_TmnCode=").append(vnpParams.get("vnp_TmnCode"))
                .append("&vnp_TransactionNo=").append(vnpParams.get("vnp_TransactionNo"))
                .append("&vnp_TransactionStatus=").append(vnpParams.get("vnp_TransactionStatus"))
                .append("&vnp_TxnRef=").append(vnpParams.get("vnp_TxnRef"))
                .append("&vnp_Version=").append(vnpParams.get("vnp_Version"));

        String secureHash = CryptoUtils.hmacSHA512(hashSecret, hashData.toString());

        // Gọi Endpoint IPN Webhook
        mockMvc.perform(get("/api/payment/vnpay-ipn")
                        .param("vnp_Amount", vnpParams.get("vnp_Amount"))
                        .param("vnp_BankCode", vnpParams.get("vnp_BankCode"))
                        .param("vnp_CardType", vnpParams.get("vnp_CardType"))
                        .param("vnp_Command", vnpParams.get("vnp_Command"))
                        .param("vnp_OrderInfo", vnpParams.get("vnp_OrderInfo"))
                        .param("vnp_PayDate", vnpParams.get("vnp_PayDate"))
                        .param("vnp_ResponseCode", vnpParams.get("vnp_ResponseCode"))
                        .param("vnp_TmnCode", vnpParams.get("vnp_TmnCode"))
                        .param("vnp_TransactionNo", vnpParams.get("vnp_TransactionNo"))
                        .param("vnp_TransactionStatus", vnpParams.get("vnp_TransactionStatus"))
                        .param("vnp_TxnRef", vnpParams.get("vnp_TxnRef"))
                        .param("vnp_Version", vnpParams.get("vnp_Version"))
                        .param("vnp_SecureHash", secureHash))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.RspCode").value("00"))
                .andExpect(jsonPath("$.Message").value("Confirm Success"));

        // Kiểm tra cơ sở dữ liệu giao_dich_thanh_toan
        GiaoDichThanhToan updatedGd = giaoDichThanhToanRepository.findByMaGiaoDichNoiBo(txnRef).orElseThrow();
        assertEquals("THANH_CONG", updatedGd.getTrangThai());
        assertEquals("14567890", updatedGd.getMaGiaoDichCong());

        // Kiểm tra đơn hàng được cập nhật sang DANG_XU_LY
        DonHang updatedDh = donHangRepository.findById(donHangTest.getId()).orElseThrow();
        assertEquals("DANG_XU_LY", updatedDh.getTrangThaiDonHang().getMaTrangThai());
    }

    @Test
    @DisplayName("TC3 - Webhook IPN phát hiện chữ ký Checksum sai lệch (AC 2)")
    void testXuLyIpnVNPaySaiChecksum() throws Exception {
        GiaoDichThanhToan gd = giaoDichThanhToanService.khoiTaoGiaoDich(donHangTest, ptttVnpay);

        mockMvc.perform(get("/api/payment/vnpay-ipn")
                        .param("vnp_TxnRef", gd.getMaGiaoDichNoiBo())
                        .param("vnp_Amount", "35000000")
                        .param("vnp_ResponseCode", "00")
                        .param("vnp_SecureHash", "INVALID_HASH_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.RspCode").value("97"))
                .andExpect(jsonPath("$.Message").value("Invalid Checksum"));
    }

    @Test
    @DisplayName("TC4 - Giả lập thanh toán thành công và tự động kiểm tra trạng thái qua polling (AC 1 & 2)")
    void testGiaLapVaKiemTraTrangThai() throws Exception {
        // Giả lập thanh toán thành công
        mockMvc.perform(post("/thanh-toan/gia-lap-thanh-cong/" + donHangTest.getMaDonHang())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.redirectUrl").value("/thanh-toan/thanh-cong/" + donHangTest.getMaDonHang()));

        // Polling kiểm tra trạng thái
        mockMvc.perform(get("/thanh-toan/kiem-tra-trang-thai/" + donHangTest.getMaDonHang()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trangThai").value("THANH_CONG"))
                .andExpect(jsonPath("$.daThanhCong").value(true));
    }

    @Test
    @DisplayName("TC5 - Admin xem danh sách lịch sử giao dịch thanh toán (AC 3)")
    void testAdminXemLichSuGiaoDich() throws Exception {
        giaoDichThanhToanService.khoiTaoGiaoDich(donHangTest, ptttVnpay);

        mockMvc.perform(get("/admin/giao-dich-thanh-toan/hien-thi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/giao-dich-thanh-toan/hien-thi"))
                .andExpect(model().attributeExists("trangGiaoDich"))
                .andExpect(model().attributeExists("tongSo"))
                .andExpect(model().attributeExists("dsPhuongThuc"));
    }
}
