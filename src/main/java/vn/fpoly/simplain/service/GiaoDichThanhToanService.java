package vn.fpoly.simplain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.PaymentUrlResponse;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;
import vn.fpoly.simplain.service.payment.MoMoService;
import vn.fpoly.simplain.service.payment.VNPayService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Dịch vụ xử lý Giao dịch thanh toán & Tích hợp Cổng thanh toán (US20).
 * - Tiêu chí 1: Generate URL quét QR (VNPay / MoMo).
 * - Tiêu chí 2: Xử lý IPN Webhook trả về từ ngân hàng (Xác thực chữ ký, Idempotent).
 * - Tiêu chí 3: Lưu giao_dich_thanh_toan.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GiaoDichThanhToanService {

    public static final String TRANG_THAI_CHO = "CHO_THANH_TOAN";
    public static final String TRANG_THAI_THANH_CONG = "THANH_CONG";
    public static final String TRANG_THAI_THAT_BAI = "THAT_BAI";
    public static final String TRANG_THAI_DA_HUY = "DA_HUY";

    private final GiaoDichThanhToanRepository giaoDichRepository;
    private final DonHangRepository donHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final VNPayService vnPayService;
    private final MoMoService moMoService;
    private final LoyaltyHoiVienService loyaltyHoiVienService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tạo hoặc lấy giao dịch thanh toán khởi tạo cho đơn hàng (US20 - Tiêu chí 3).
     */
    @Transactional
    public GiaoDichThanhToan khoiTaoGiaoDich(DonHang donHang, PhuongThucThanhToan pttt) {
        if (donHang == null || pttt == null) {
            throw new IllegalArgumentException("Đơn hàng hoặc phương thức thanh toán không hợp lệ!");
        }

        // Kiểm tra xem đã có giao dịch đang chờ cho đơn hàng này chưa
        Optional<GiaoDichThanhToan> existing = giaoDichRepository.findTopByDonHangIdOrderByIdDesc(donHang.getId());
        if (existing.isPresent() && TRANG_THAI_CHO.equals(existing.get().getTrangThai())) {
            return existing.get();
        }

        String maGiaoDichNoiBo = "TXN_" + donHang.getMaDonHang() + "_" + (System.currentTimeMillis() % 100000);

        GiaoDichThanhToan giaoDich = GiaoDichThanhToan.builder()
                .donHang(donHang)
                .phuongThucThanhToan(pttt)
                .maGiaoDichNoiBo(maGiaoDichNoiBo)
                .soTien(donHang.getTongThanhToan())
                .trangThai(TRANG_THAI_CHO)
                .ngayTao(LocalDateTime.now())
                .build();

        return giaoDichRepository.save(giaoDich);
    }

    /**
     * Sinh URL Thanh toán và mã QR tương ứng theo phương thức khách hàng chọn (US20 - Tiêu chí 1).
     */
    @Transactional
    public PaymentUrlResponse taoUrlThanhToanVaQr(DonHang donHang, HttpServletRequest request) {
        PhuongThucThanhToan pttt = donHang.getPhuongThucThanhToan();
        if (pttt == null && donHang.getId() != null) {
            // Đọc lại từ database nếu entity chưa nạp
            DonHang dhDb = donHangRepository.findById(donHang.getId()).orElse(null);
            if (dhDb != null) pttt = dhDb.getPhuongThucThanhToan();
        }

        if (pttt == null) {
            pttt = phuongThucThanhToanRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("Hệ thống chưa cấu hình phương thức thanh toán!"));
        }

        GiaoDichThanhToan giaoDich = khoiTaoGiaoDich(donHang, pttt);

        // Lấy Base URL của ứng dụng
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String baseUrl = scheme + "://" + serverName + (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort);

        String ipAddress = layIpClient(request);
        String maPt = pttt.getMaPhuongThuc() != null ? pttt.getMaPhuongThuc().toUpperCase() : "";

        if (maPt.contains("VNPAY")) {
            String returnUrl = baseUrl + "/thanh-toan/vnpay-return";
            return vnPayService.taoUrlThanhToan(donHang, giaoDich.getMaGiaoDichNoiBo(), ipAddress, returnUrl, pttt);
        } else if (maPt.contains("MOMO")) {
            String returnUrl = baseUrl + "/thanh-toan/momo-return";
            return moMoService.taoUrlThanhToan(donHang, giaoDich.getMaGiaoDichNoiBo(), ipAddress, returnUrl, pttt);
        } else {
            // Mặc định COD hoặc chuyển khoản
            String qrUrl = "https://img.vietqr.io/image/970407-19036789999-compact2.png"
                    + "?amount=" + donHang.getTongThanhToan().longValue()
                    + "&addInfo=DH%20" + donHang.getMaDonHang()
                    + "&accountName=SIMPLAIN%20STORE";

            return PaymentUrlResponse.builder()
                    .paymentUrl(baseUrl + "/thanh-toan/thanh-cong/" + donHang.getMaDonHang())
                    .qrCodeUrl(qrUrl)
                    .maDonHang(donHang.getMaDonHang())
                    .maGiaoDichNoiBo(giaoDich.getMaGiaoDichNoiBo())
                    .soTien(donHang.getTongThanhToan())
                    .tenPhuongThuc(pttt.getTenPhuongThuc())
                    .maPhuongThuc(pttt.getMaPhuongThuc())
                    .thoiGianHetHanPhut(15)
                    .build();
        }
    }

    /**
     * Xử lý IPN Webhook trả về từ VNPay (US20 - Tiêu chí 2).
     * Đảm bảo Idempotent, xác thực HMAC-SHA512 checksum và cập nhật giao_dich_thanh_toan.
     */
    @Transactional
    public Map<String, String> xuLyIpnVNPay(Map<String, String> vnpParams) {
        Map<String, String> response = new HashMap<>();

        // 1. Kiểm tra tham số cơ bản
        String vnpTxnRef = vnpParams.get("vnp_TxnRef");
        String vnpAmountStr = vnpParams.get("vnp_Amount");
        String vnpResponseCode = vnpParams.get("vnp_ResponseCode");
        String vnpTransactionNo = vnpParams.get("vnp_TransactionNo");

        if (vnpTxnRef == null || vnpAmountStr == null || vnpResponseCode == null) {
            response.put("RspCode", "99");
            response.put("Message", "Dữ liệu webhook thiếu tham số bắt buộc");
            return response;
        }

        // 2. Tìm giao dịch hoặc đơn hàng tương ứng
        GiaoDichThanhToan giaoDich = giaoDichRepository.findByMaGiaoDichNoiBo(vnpTxnRef)
                .orElse(null);

        DonHang donHang = null;
        if (giaoDich != null) {
            donHang = giaoDich.getDonHang();
        } else {
            // Thử tìm theo mã đơn hàng
            donHang = donHangRepository.findByMaDonHang(vnpTxnRef).orElse(null);
            if (donHang != null) {
                giaoDich = giaoDichRepository.findTopByDonHangIdOrderByIdDesc(donHang.getId()).orElse(null);
            }
        }

        if (donHang == null) {
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            log.warn("VNPay IPN: Không tìm thấy đơn hàng với vnp_TxnRef: {}", vnpTxnRef);
            return response;
        }

        // 3. Xác thực chữ ký số Checksum HMAC-SHA512
        PhuongThucThanhToan pttt = donHang.getPhuongThucThanhToan();
        String hashSecret = layHashSecretVNPay(pttt);

        boolean signatureValid = vnPayService.xacThucChuKy(vnpParams, hashSecret);
        if (!signatureValid) {
            response.put("RspCode", "97");
            response.put("Message", "Invalid Checksum");
            log.error("VNPay IPN: Sai chữ ký bảo mật cho đơn: {}", vnpTxnRef);
            return response;
        }

        // 4. Kiểm tra số tiền thanh toán (VNPay nhân 100)
        long vnpAmount = Long.parseLong(vnpAmountStr);
        BigDecimal amountInVnd = new BigDecimal(vnpAmount).divide(new BigDecimal(100));
        if (donHang.getTongThanhToan().compareTo(amountInVnd) != 0) {
            response.put("RspCode", "04");
            response.put("Message", "Invalid Amount");
            log.warn("VNPay IPN: Số tiền không khớp! Đơn: {}, VNPay: {}", donHang.getTongThanhToan(), amountInVnd);
            return response;
        }

        // 5. Kiểm tra Idempotent: Nếu đơn hàng đã hoàn thành hoặc đang xử lý, không lặp lại
        if (giaoDich != null && TRANG_THAI_THANH_CONG.equals(giaoDich.getTrangThai())) {
            response.put("RspCode", "02");
            response.put("Message", "Order already confirmed");
            return response;
        }

        // 6. Cập nhật giao_dich_thanh_toan (Tiêu chí 3)
        boolean isSuccess = "00".equals(vnpResponseCode);

        if (giaoDich == null) {
            giaoDich = GiaoDichThanhToan.builder()
                    .donHang(donHang)
                    .phuongThucThanhToan(pttt != null ? pttt : phuongThucThanhToanRepository.findAll().get(0))
                    .maGiaoDichNoiBo(vnpTxnRef)
                    .soTien(amountInVnd)
                    .ngayTao(LocalDateTime.now())
                    .build();
        }

        giaoDich.setMaGiaoDichCong(vnpTransactionNo);
        giaoDich.setTrangThai(isSuccess ? TRANG_THAI_THANH_CONG : TRANG_THAI_THAT_BAI);
        try {
            giaoDich.setDuLieuPhanHoi(objectMapper.writeValueAsString(vnpParams));
        } catch (Exception e) {
            giaoDich.setDuLieuPhanHoi(vnpParams.toString());
        }
        giaoDichRepository.save(giaoDich);

        // 7. Cập nhật trạng thái đơn hàng nếu thanh toán thành công
        if (isSuccess) {
            TrangThaiDonHang ttDangXuLy = trangThaiDonHangRepository.findByMaTrangThai("DANG_XU_LY")
                    .orElseGet(() -> trangThaiDonHangRepository.findById(2L).orElse(null));
            if (ttDangXuLy != null) {
                donHang.setTrangThaiDonHang(ttDangXuLy);
                donHangRepository.save(donHang);
            }
            log.info("VNPay IPN: Xác nhận thanh toán thành công đơn hàng #{}, Số tiền: {}đ",
                    donHang.getMaDonHang(), amountInVnd);
        } else {
            log.warn("VNPay IPN: Thanh toán thất bại cho đơn #{}, Mã lỗi: {}",
                    donHang.getMaDonHang(), vnpResponseCode);
        }

        response.put("RspCode", "00");
        response.put("Message", "Confirm Success");
        return response;
    }

    /**
     * Xử lý Return URL khi người dùng thanh toán trên VNPay và được điều hướng về web (US20 - Tiêu chí 2).
     */
    @Transactional
    public boolean xuLyReturnVNPay(Map<String, String> vnpParams) {
        String vnpTxnRef = vnpParams.get("vnp_TxnRef");
        String vnpResponseCode = vnpParams.get("vnp_ResponseCode");
        String vnpTransactionNo = vnpParams.get("vnp_TransactionNo");

        if (vnpTxnRef == null || vnpResponseCode == null) {
            return false;
        }

        // Tìm giao dịch
        GiaoDichThanhToan giaoDich = giaoDichRepository.findByMaGiaoDichNoiBo(vnpTxnRef).orElse(null);
        DonHang donHang = null;
        if (giaoDich != null) {
            donHang = giaoDich.getDonHang();
        } else {
            donHang = donHangRepository.findByMaDonHang(vnpTxnRef).orElse(null);
            if (donHang != null) {
                giaoDich = giaoDichRepository.findTopByDonHangIdOrderByIdDesc(donHang.getId()).orElse(null);
            }
        }

        if (donHang == null) {
            return false;
        }

        String hashSecret = layHashSecretVNPay(donHang.getPhuongThucThanhToan());
        boolean signatureValid = vnPayService.xacThucChuKy(vnpParams, hashSecret);

        if (!signatureValid) {
            log.error("VNPay Return: Chữ ký không hợp lệ cho đơn #{}", donHang.getMaDonHang());
            return false;
        }

        boolean isSuccess = "00".equals(vnpResponseCode);

        // Lưu / Cập nhật giao dịch
        if (giaoDich != null) {
            giaoDich.setMaGiaoDichCong(vnpTransactionNo);
            giaoDich.setTrangThai(isSuccess ? TRANG_THAI_THANH_CONG : TRANG_THAI_THAT_BAI);
            try {
                giaoDich.setDuLieuPhanHoi(objectMapper.writeValueAsString(vnpParams));
            } catch (Exception ignored) {
            }
            giaoDichRepository.save(giaoDich);
        }

        // Cập nhật đơn hàng
        if (isSuccess) {
            TrangThaiDonHang ttDangXuLy = trangThaiDonHangRepository.findByMaTrangThai("DANG_XU_LY")
                    .orElseGet(() -> trangThaiDonHangRepository.findById(2L).orElse(null));
            if (ttDangXuLy != null) {
                donHang.setTrangThaiDonHang(ttDangXuLy);
                donHangRepository.save(donHang);
            }
        }

        return isSuccess;
    }

    /**
     * Xử lý IPN Webhook trả về từ MoMo (US20 - Tiêu chí 2).
     */
    @Transactional
    public Map<String, Object> xuLyIpnMoMo(Map<String, String> momoParams) {
        Map<String, Object> response = new HashMap<>();

        String orderId = momoParams.get("orderId");
        String resultCode = momoParams.get("resultCode");
        String transId = momoParams.get("transId");

        if (orderId == null || resultCode == null) {
            response.put("status", 400);
            response.put("message", "Missing required parameters");
            return response;
        }

        GiaoDichThanhToan giaoDich = giaoDichRepository.findByMaGiaoDichNoiBo(orderId).orElse(null);
        DonHang donHang = null;
        if (giaoDich != null) {
            donHang = giaoDich.getDonHang();
        } else {
            donHang = donHangRepository.findByMaDonHang(orderId).orElse(null);
            if (donHang != null) {
                giaoDich = giaoDichRepository.findTopByDonHangIdOrderByIdDesc(donHang.getId()).orElse(null);
            }
        }

        if (donHang == null) {
            response.put("status", 404);
            response.put("message", "Order not found");
            return response;
        }

        // Xác thực chữ ký MoMo
        boolean valid = moMoService.xacThucChuKy(momoParams, null);
        if (!valid) {
            response.put("status", 400);
            response.put("message", "Invalid signature");
            return response;
        }

        // Idempotent
        if (giaoDich != null && TRANG_THAI_THANH_CONG.equals(giaoDich.getTrangThai())) {
            response.put("status", 200);
            response.put("message", "Order already processed");
            return response;
        }

        boolean isSuccess = "0".equals(resultCode);

        if (giaoDich != null) {
            giaoDich.setMaGiaoDichCong(transId);
            giaoDich.setTrangThai(isSuccess ? TRANG_THAI_THANH_CONG : TRANG_THAI_THAT_BAI);
            try {
                giaoDich.setDuLieuPhanHoi(objectMapper.writeValueAsString(momoParams));
            } catch (Exception ignored) {
            }
            giaoDichRepository.save(giaoDich);
        }

        if (isSuccess) {
            TrangThaiDonHang ttDangXuLy = trangThaiDonHangRepository.findByMaTrangThai("DANG_XU_LY")
                    .orElseGet(() -> trangThaiDonHangRepository.findById(2L).orElse(null));
            if (ttDangXuLy != null) {
                donHang.setTrangThaiDonHang(ttDangXuLy);
                donHangRepository.save(donHang);
            }
        }

        response.put("status", 204);
        response.put("message", "Received");
        return response;
    }

    /**
     * Giả lập Quét QR Thanh toán Thành công (Dành cho kiểm thử Sandbox nhanh trên localhost) (US20).
     */
    @Transactional
    public GiaoDichThanhToan giaLapThanhToanThanhCong(String maDonHang) {
        DonHang donHang = donHangRepository.findByMaDonHang(maDonHang)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng #" + maDonHang));

        GiaoDichThanhToan giaoDich = giaoDichRepository.findTopByDonHangIdOrderByIdDesc(donHang.getId())
                .orElseGet(() -> {
                    PhuongThucThanhToan pttt = donHang.getPhuongThucThanhToan() != null
                            ? donHang.getPhuongThucThanhToan()
                            : phuongThucThanhToanRepository.findAll().get(0);
                    return khoiTaoGiaoDich(donHang, pttt);
                });

        giaoDich.setTrangThai(TRANG_THAI_THANH_CONG);
        giaoDich.setMaGiaoDichCong("SIM_TEST_" + System.currentTimeMillis());
        giaoDich.setDuLieuPhanHoi("{\"simulated\": true, \"message\": \"Giả lập thanh toán QR thành công qua Sandbox\", \"time\": \"" + LocalDateTime.now() + "\"}");
        GiaoDichThanhToan savedGiaoDich = giaoDichRepository.save(giaoDich);

        // Chuyển trạng thái đơn hàng sang ĐANG_XU_LY (Đã thanh toán)
        TrangThaiDonHang ttDangXuLy = trangThaiDonHangRepository.findByMaTrangThai("DANG_XU_LY")
                .orElseGet(() -> trangThaiDonHangRepository.findById(2L).orElse(null));
        if (ttDangXuLy != null) {
            donHang.setTrangThaiDonHang(ttDangXuLy);
            donHangRepository.save(donHang);
        }

        log.info("Giả lập thanh toán thành công cho đơn hàng #{}", maDonHang);
        return savedGiaoDich;
    }

    /**
     * Lấy danh sách phân trang lịch sử giao dịch thanh toán cho Admin (US20 - Tiêu chí 3).
     */
    @Transactional(readOnly = true)
    public Page<GiaoDichThanhToan> layDanhSachPhanTrang(
            String tuKhoa, String trangThai, Long maPhuongThucId, Pageable pageable) {
        String kw = (tuKhoa != null && !tuKhoa.trim().isEmpty()) ? tuKhoa.trim() : null;
        String st = (trangThai != null && !trangThai.trim().isEmpty()) ? trangThai.trim() : null;
        return giaoDichRepository.timKiemPhanTrang(kw, st, maPhuongThucId, pageable);
    }

    /**
     * Lấy chi tiết một giao dịch thanh toán theo ID.
     */
    @Transactional(readOnly = true)
    public GiaoDichThanhToan layChiTiet(Long id) {
        return giaoDichRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch thanh toán ID: " + id));
    }

    /**
     * Kiểm tra trạng thái giao dịch thanh toán mới nhất của đơn hàng.
     */
    @Transactional(readOnly = true)
    public String kiemTraTrangThaiDonHang(String maDonHang) {
        return giaoDichRepository.findTopByDonHangMaDonHangOrderByIdDesc(maDonHang)
                .map(GiaoDichThanhToan::getTrangThai)
                .orElse(TRANG_THAI_CHO);
    }

    // ===================================
    // HÀM TIỆN ÍCH HỖ TRỢ NỘI BỘ
    // ===================================

    private String layHashSecretVNPay(PhuongThucThanhToan pttt) {
        if (pttt != null && pttt.getCauHinh() != null && !pttt.getCauHinh().isBlank()) {
            try {
                com.fasterxml.jackson.databind.JsonNode json = objectMapper.readTree(pttt.getCauHinh());
                if (json.has("hash_secret") && !json.get("hash_secret").asText().isBlank()) {
                    return json.get("hash_secret").asText().trim();
                } else if (json.has("vnp_HashSecret")) {
                    return json.get("vnp_HashSecret").asText().trim();
                }
            } catch (Exception ignored) {
            }
        }
        return VNPayService.VNP_DEFAULT_HASH_SECRET;
    }

    private String layIpClient(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return (ip != null && !ip.isBlank() && !"0:0:0:0:0:0:0:1".equals(ip)) ? ip : "127.0.0.1";
    }
}
