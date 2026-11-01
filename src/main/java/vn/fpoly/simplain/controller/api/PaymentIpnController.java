package vn.fpoly.simplain.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fpoly.simplain.service.GiaoDichThanhToanService;

import java.util.Map;

/**
 * Controller tiếp nhận Webhook IPN từ các cổng thanh toán ngân hàng (VNPay / MoMo) (US20 - Tiêu chí 2).
 * Phục vụ đồng bộ kết quả thanh toán bất đồng bộ (Asynchronous Server-to-Server).
 */
@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentIpnController {

    private final GiaoDichThanhToanService giaoDichThanhToanService;

    /**
     * Webhook IPN VNPay (US20 - Tiêu chí 2).
     * VNPay có thể gọi qua GET hoặc POST kèm các tham số vnp_*.
     */
    @RequestMapping(value = "/vnpay-ipn", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, String>> tiepNhanIpnVNPay(@RequestParam Map<String, String> allParams) {
        log.info("Nhận Webhook IPN từ VNPay: TxnRef = {}, ResponseCode = {}, Amount = {}",
                allParams.get("vnp_TxnRef"), allParams.get("vnp_ResponseCode"), allParams.get("vnp_Amount"));

        Map<String, String> ketQua = giaoDichThanhToanService.xuLyIpnVNPay(allParams);
        return ResponseEntity.ok(ketQua);
    }

    /**
     * Webhook IPN MoMo (US20 - Tiêu chí 2).
     * MoMo gọi qua POST request body hoặc query params.
     */
    @PostMapping("/momo-ipn")
    public ResponseEntity<Map<String, Object>> tiepNhanIpnMoMo(
            @RequestParam(required = false) Map<String, String> queryParams,
            @RequestBody(required = false) Map<String, String> bodyParams) {

        Map<String, String> merged = new java.util.HashMap<>();
        if (queryParams != null) merged.putAll(queryParams);
        if (bodyParams != null) merged.putAll(bodyParams);

        log.info("Nhận Webhook IPN từ MoMo: OrderId = {}, ResultCode = {}",
                merged.get("orderId"), merged.get("resultCode"));

        Map<String, Object> ketQua = giaoDichThanhToanService.xuLyIpnMoMo(merged);
        return ResponseEntity.ok(ketQua);
    }
}
