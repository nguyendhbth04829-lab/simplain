package vn.fpoly.simplain.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO Thêm/Sửa phương thức & cổng thanh toán chuyên nghiệp (US18).
 * Hỗ trợ các mẫu cổng thanh toán phổ biến: VNPAY, MOMO, VIETQR, COD, ZALOPAY, KHAC.
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhuongThucThanhToanRequest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Long id;

    @NotBlank(message = "Mã phương thức thanh toán không được để trống")
    @Size(min = 2, max = 50, message = "Mã phương thức phải từ 2 đến 50 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Mã phương thức chỉ gồm chữ cái, số, gạch dưới hoặc gạch ngang (VD: VNPAY, MOMO, VIETQR, COD)")
    private String maPhuongThuc;

    @NotBlank(message = "Tên phương thức thanh toán không được để trống")
    @Size(max = 100, message = "Tên phương thức tối đa 100 ký tự")
    private String tenPhuongThuc;

    @Builder.Default
    private String loaiCong = "KHAC"; // VNPAY, MOMO, VIETQR, COD, ZALOPAY, KHAC

    @Builder.Default
    private Boolean laTrucTuyen = false;

    @Builder.Default
    private Boolean kichHoat = true;

    @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
    private String moTa;

    @Size(max = 255, message = "Biểu tượng tối đa 255 ký tự")
    private String bieuTuong;

    /**
     * Chuỗi JSON cấu hình thô lưu vào DB
     */
    private String cauHinh;

    // --- CẤU HÌNH CHI TIẾT DÀNH CHO CỔNG VNPAY ---
    private String vnpTmnCode;
    private String vnpHashSecret;
    private String vnpUrl;
    private String vnpMoiTruong; // SANDBOX hoặc PRODUCTION

    // --- CẤU HÌNH CHI TIẾT DÀNH CHO VÍ MOMO ---
    private String momoPartnerCode;
    private String momoAccessKey;
    private String momoSecretKey;
    private String momoEndpoint;

    // --- CẤU HÌNH CHI TIẾT DÀNH CHO CHUYỂN KHOẢN VIETQR / QR SHOP ---
    private String maNganHang;         // Mã BIN ngân hàng (VD: 970422 - MB Bank, 970436 - Vietcombank)
    private String tenNganHang;        // Tên viết tắt hoặc đầy đủ
    private String soTaiKhoan;         // Số tài khoản người nhận
    private String tenChuTaiKhoan;     // Tên chủ tài khoản (In hoa không dấu)
    private String chiNhanh;           // Chi nhánh ngân hàng (tùy chọn)
    private String cuPhapChuyenKhoan;  // Mẫu nội dung CK (VD: DH {maDonHang})
    private String qrCodeUrl;          // Đường dẫn ảnh QR ngân hàng hoặc QR ví MoMo/Shop

    // --- CẤU HÌNH CHI TIẾT DÀNH CHO COD ---
    private BigDecimal phiThuHo;       // Phí thu hộ COD (mặc định 0đ)
    private BigDecimal hanMucCodToiDa; // Hạn mức tối đa cho phép ship COD
    private String chinhSachKiemHang;  // CHO_XEM_HANG, CHO_THU_HANG, KHONG_CHO_XEM

    // --- CẤU HÌNH CHI TIẾT DÀNH CHO ZALOPAY ---
    private String zaloAppId;
    private String zaloKey1;
    private String zaloKey2;
    private String zaloEndpoint;

    /**
     * Đóng gói các trường cấu hình chuyên dụng thành chuỗi JSON chuẩn để lưu trữ vào cột cau_hinh.
     */
    public String dongGoiCauHinhJson() {
        if (this.loaiCong == null || this.loaiCong.isBlank()) {
            this.loaiCong = "KHAC";
        }

        Map<String, Object> configMap = new HashMap<>();
        String loaiChuan = this.loaiCong.toUpperCase();

        switch (loaiChuan) {
            case "VNPAY" -> {
                configMap.put("tmn_code", vnpTmnCode != null ? vnpTmnCode.trim() : "");
                configMap.put("vnp_TmnCode", vnpTmnCode != null ? vnpTmnCode.trim() : "");
                configMap.put("hash_secret", vnpHashSecret != null ? vnpHashSecret.trim() : "");
                configMap.put("vnp_HashSecret", vnpHashSecret != null ? vnpHashSecret.trim() : "");
                configMap.put("url", vnpUrl != null && !vnpUrl.isBlank()
                        ? vnpUrl.trim()
                        : "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
                configMap.put("vnp_Url", configMap.get("url"));
                configMap.put("moi_truong", vnpMoiTruong != null ? vnpMoiTruong.trim() : "SANDBOX");
            }
            case "MOMO" -> {
                configMap.put("partner_code", momoPartnerCode != null ? momoPartnerCode.trim() : "");
                configMap.put("access_key", momoAccessKey != null ? momoAccessKey.trim() : "");
                configMap.put("secret_key", momoSecretKey != null ? momoSecretKey.trim() : "");
                configMap.put("endpoint", momoEndpoint != null && !momoEndpoint.isBlank()
                        ? momoEndpoint.trim()
                        : "https://test-payment.momo.vn/v2/gateway/api/create");
            }
            case "VIETQR" -> {
                configMap.put("maNganHang", maNganHang != null ? maNganHang.trim() : "970422");
                configMap.put("tenNganHang", tenNganHang != null ? tenNganHang.trim() : "MBBank");
                configMap.put("soTaiKhoan", soTaiKhoan != null ? soTaiKhoan.trim() : "");
                configMap.put("tenChuTaiKhoan", tenChuTaiKhoan != null ? tenChuTaiKhoan.trim().toUpperCase() : "");
                configMap.put("chiNhanh", chiNhanh != null ? chiNhanh.trim() : "");
                configMap.put("cuPhapChuyenKhoan", cuPhapChuyenKhoan != null && !cuPhapChuyenKhoan.isBlank()
                        ? cuPhapChuyenKhoan.trim()
                        : "DH {maDonHang}");
                configMap.put("qrCodeUrl", qrCodeUrl != null ? qrCodeUrl.trim() : "");
            }
            case "COD" -> {
                configMap.put("phiThuHo", phiThuHo != null ? phiThuHo : BigDecimal.ZERO);
                configMap.put("phi_thu_ho", phiThuHo != null ? phiThuHo : BigDecimal.ZERO);
                configMap.put("hanMucCodToiDa", hanMucCodToiDa != null ? hanMucCodToiDa : new BigDecimal("20000000"));
                configMap.put("han_muc_cod_toi_da", hanMucCodToiDa != null ? hanMucCodToiDa : new BigDecimal("20000000"));
                configMap.put("chinhSachKiemHang", chinhSachKiemHang != null ? chinhSachKiemHang.trim() : "CHO_XEM_HANG");
                configMap.put("cho_kiem_hang", !"KHONG_CHO_XEM".equalsIgnoreCase(chinhSachKiemHang));
            }
            case "ZALOPAY" -> {
                configMap.put("app_id", zaloAppId != null ? zaloAppId.trim() : "");
                configMap.put("key1", zaloKey1 != null ? zaloKey1.trim() : "");
                configMap.put("key2", zaloKey2 != null ? zaloKey2.trim() : "");
                configMap.put("endpoint", zaloEndpoint != null && !zaloEndpoint.isBlank()
                        ? zaloEndpoint.trim()
                        : "https://sb-openapi.zalopay.vn/v2/create");
            }
            default -> {
                if (cauHinh != null && !cauHinh.isBlank()) {
                    return cauHinh.trim();
                }
                return null;
            }
        }

        try {
            return MAPPER.writeValueAsString(configMap);
        } catch (Exception e) {
            log.error("Lỗi khi chuyển đổi cấu hình sang JSON: {}", e.getMessage());
            return cauHinh;
        }
    }

    /**
     * Giải mã chuỗi JSON từ cột cau_hinh và gán ngược lại vào các trường chi tiết của DTO.
     */
    public void giaiMaCauHinhJson(String jsonStr, String loaiCong) {
        if (jsonStr == null || jsonStr.isBlank()) {
            return;
        }
        this.cauHinh = jsonStr;
        this.loaiCong = loaiCong != null ? loaiCong : "KHAC";

        try {
            JsonNode node = MAPPER.readTree(jsonStr);

            // Đọc cấu hình VNPay
            if (node.has("tmn_code")) this.vnpTmnCode = node.get("tmn_code").asText();
            else if (node.has("vnp_TmnCode")) this.vnpTmnCode = node.get("vnp_TmnCode").asText();
            else if (node.has("vnpTmnCode")) this.vnpTmnCode = node.get("vnpTmnCode").asText();

            if (node.has("hash_secret")) this.vnpHashSecret = node.get("hash_secret").asText();
            else if (node.has("vnp_HashSecret")) this.vnpHashSecret = node.get("vnp_HashSecret").asText();
            else if (node.has("vnpHashSecret")) this.vnpHashSecret = node.get("vnpHashSecret").asText();

            if (node.has("url")) this.vnpUrl = node.get("url").asText();
            else if (node.has("vnp_Url")) this.vnpUrl = node.get("vnp_Url").asText();
            else if (node.has("vnpUrl")) this.vnpUrl = node.get("vnpUrl").asText();

            if (node.has("moi_truong")) this.vnpMoiTruong = node.get("moi_truong").asText();
            else if (node.has("vnpMoiTruong")) this.vnpMoiTruong = node.get("vnpMoiTruong").asText();

            // Đọc cấu hình MoMo
            if (node.has("partner_code")) this.momoPartnerCode = node.get("partner_code").asText();
            else if (node.has("partnerCode")) this.momoPartnerCode = node.get("partnerCode").asText();
            else if (node.has("momoPartnerCode")) this.momoPartnerCode = node.get("momoPartnerCode").asText();

            if (node.has("access_key")) this.momoAccessKey = node.get("access_key").asText();
            else if (node.has("accessKey")) this.momoAccessKey = node.get("accessKey").asText();
            else if (node.has("momoAccessKey")) this.momoAccessKey = node.get("momoAccessKey").asText();

            if (node.has("secret_key")) this.momoSecretKey = node.get("secret_key").asText();
            else if (node.has("secretKey")) this.momoSecretKey = node.get("secretKey").asText();
            else if (node.has("momoSecretKey")) this.momoSecretKey = node.get("momoSecretKey").asText();

            if (node.has("endpoint")) this.momoEndpoint = node.get("endpoint").asText();
            else if (node.has("momoEndpoint")) this.momoEndpoint = node.get("momoEndpoint").asText();

            // Đọc cấu hình VietQR
            if (node.has("maNganHang")) this.maNganHang = node.get("maNganHang").asText();
            else if (node.has("ma_ngan_hang")) this.maNganHang = node.get("ma_ngan_hang").asText();

            if (node.has("tenNganHang")) this.tenNganHang = node.get("tenNganHang").asText();
            else if (node.has("ten_ngan_hang")) this.tenNganHang = node.get("ten_ngan_hang").asText();

            if (node.has("soTaiKhoan")) this.soTaiKhoan = node.get("soTaiKhoan").asText();
            else if (node.has("so_tai_khoan")) this.soTaiKhoan = node.get("so_tai_khoan").asText();

            if (node.has("tenChuTaiKhoan")) this.tenChuTaiKhoan = node.get("tenChuTaiKhoan").asText();
            else if (node.has("ten_chu_tai_khoan")) this.tenChuTaiKhoan = node.get("ten_chu_tai_khoan").asText();

            if (node.has("chiNhanh")) this.chiNhanh = node.get("chiNhanh").asText();
            else if (node.has("chi_nhanh")) this.chiNhanh = node.get("chi_nhanh").asText();

            if (node.has("cuPhapChuyenKhoan")) this.cuPhapChuyenKhoan = node.get("cuPhapChuyenKhoan").asText();
            else if (node.has("cu_phap_chuyen_khoan")) this.cuPhapChuyenKhoan = node.get("cu_phap_chuyen_khoan").asText();

            if (node.has("qrCodeUrl")) this.qrCodeUrl = node.get("qrCodeUrl").asText();
            else if (node.has("qr_code_url")) this.qrCodeUrl = node.get("qr_code_url").asText();

            // Đọc cấu hình COD
            if (node.has("phiThuHo")) this.phiThuHo = new BigDecimal(node.get("phiThuHo").asText());
            else if (node.has("phi_thu_ho")) this.phiThuHo = new BigDecimal(node.get("phi_thu_ho").asText());

            if (node.has("hanMucCodToiDa")) this.hanMucCodToiDa = new BigDecimal(node.get("hanMucCodToiDa").asText());
            else if (node.has("han_muc_cod_toi_da")) this.hanMucCodToiDa = new BigDecimal(node.get("han_muc_cod_toi_da").asText());

            if (node.has("chinhSachKiemHang")) this.chinhSachKiemHang = node.get("chinhSachKiemHang").asText();
            else if (node.has("chinh_sach_kiem_hang")) this.chinhSachKiemHang = node.get("chinh_sach_kiem_hang").asText();
            else if (node.has("cho_kiem_hang")) {
                boolean choKiem = node.get("cho_kiem_hang").asBoolean(true);
                this.chinhSachKiemHang = choKiem ? "CHO_XEM_HANG" : "KHONG_CHO_XEM";
            }

            // Đọc cấu hình ZaloPay
            if (node.has("app_id")) this.zaloAppId = node.get("app_id").asText();
            else if (node.has("appId")) this.zaloAppId = node.get("appId").asText();

            if (node.has("key1")) this.zaloKey1 = node.get("key1").asText();
            else if (node.has("zaloKey1")) this.zaloKey1 = node.get("zaloKey1").asText();

            if (node.has("key2")) this.zaloKey2 = node.get("key2").asText();
            else if (node.has("zaloKey2")) this.zaloKey2 = node.get("zaloKey2").asText();

            if (node.has("endpoint")) this.zaloEndpoint = node.get("endpoint").asText();
            else if (node.has("zaloEndpoint")) this.zaloEndpoint = node.get("zaloEndpoint").asText();

        } catch (Exception e) {
            log.warn("Không thể giải mã chuỗi JSON cấu hình: {}", e.getMessage());
        }
    }
}
