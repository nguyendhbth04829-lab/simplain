package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.*;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.service.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller xử lý màn hình Checkout & Đặt hàng của Khách hàng (US19).
 * - Tiêu chí 1: Tính Tổng tiền = Hàng - Voucher - Điểm + Ship.
 * - Tiêu chí 2: Xác nhận địa chỉ và phương thức thanh toán.
 */
@Slf4j
@Controller
@RequestMapping("/thanh-toan")
@RequiredArgsConstructor
public class ThanhToanController {

    public static final String COOKIE_TOKEN = "jwt_token";
    public static final String COOKIE_CART = "simplain_cart_id";

    private final DonHangService donHangService;
    private final GioHangService gioHangService;
    private final PhuongThucThanhToanService phuongThucThanhToanService;
    private final DiaChiService diaChiService;
    private final LoyaltyHoiVienService loyaltyHoiVienService;
    private final KhachHangService khachHangService;
    private final GiaoDichThanhToanService giaoDichThanhToanService;
    private final vn.fpoly.simplain.service.payment.VNPayService vnPayService;

    /**
     * Màn hình Thanh toán đơn hàng / Checkout (US19).
     */
    @GetMapping
    public String hienThiTrangThanhToan(
            @RequestParam(name = "diaChiId", required = false) Long diaChiId,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        Long khachHangId = layKhachHangIdTuCookie(request);
        Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

        // 1. Kiểm tra giỏ hàng
        GioHangResponse gioHang = gioHangService.layThongTinGioHang(khachHangId, cartIdVangLai);
        if (gioHang == null || Boolean.TRUE.equals(gioHang.getGioHangRong())) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Giỏ hàng của bạn đang trống! Hãy thêm sản phẩm trước khi thanh toán.");
            return "redirect:/gio-hang";
        }

        // 2. Lấy danh sách cổng thanh toán đang BẬT kích hoạt (US18)
        List<PhuongThucThanhToan> dsPhuongThuc = phuongThucThanhToanService.layDanhSachDangKichHoat();

        // 3. Thông tin địa chỉ & Hội viên (Nếu đã đăng nhập)
        List<DiaChi> dsDiaChi = Collections.emptyList();
        DiaChi diaChiMacDinh = null;
        DiaChi diaChiDuocChon = null;
        BigDecimal soDuCoolcash = BigDecimal.ZERO;
        String hoTenKhach = "";
        String sdtKhach = "";

        if (khachHangId != null) {
            try {
                dsDiaChi = diaChiService.layDanhSach(khachHangId);
                diaChiMacDinh = diaChiService.layDiaChiMacDinh(khachHangId);
                if (diaChiId != null) {
                    try {
                        diaChiDuocChon = diaChiService.layTheoIdVaKhachHang(diaChiId, khachHangId);
                    } catch (Exception ignored) {
                    }
                }
                if (diaChiDuocChon == null) {
                    diaChiDuocChon = diaChiMacDinh;
                }

                HoiVienResponse hoiVien = loyaltyHoiVienService.layThongTinHoiVien(khachHangId);
                if (hoiVien != null && hoiVien.getSoDuCoolcash() != null) {
                    soDuCoolcash = hoiVien.getSoDuCoolcash();
                }
                String token = layTokenTuCookie(request);
                ThongTinKhachHangResponse kh = khachHangService.layThongTinTuToken(token);
                if (kh != null) {
                    hoTenKhach = kh.getHoTen() != null ? kh.getHoTen() : "";
                    sdtKhach = kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "";
                }
            } catch (Exception e) {
                log.warn("Lỗi tải thông tin khách hàng cho checkout: {}", e.getMessage());
            }
        }

        // 4. Khởi tạo đối tượng form Đặt hàng nếu chưa có
        if (!model.containsAttribute("datHangRequest")) {
            DatHangRequest req = new DatHangRequest();
            if (diaChiDuocChon != null) {
                req.setTenNguoiNhan(diaChiDuocChon.getTenNguoiNhan());
                req.setSoDienThoaiNhan(diaChiDuocChon.getSoDienThoai());
                req.setTinhThanh(diaChiDuocChon.getTinhThanh());
                req.setQuanHuyen(diaChiDuocChon.getQuanHuyen());
                req.setPhuongXa(diaChiDuocChon.getPhuongXa());
                req.setDiaChiChiTiet(diaChiDuocChon.getDiaChiChiTiet());
                req.setDiaChiGiaoHang(diaChiDuocChon.getDiaChiDayDu());
                req.setDiaChiId(diaChiDuocChon.getId());
            } else {
                req.setTenNguoiNhan(hoTenKhach);
                req.setSoDienThoaiNhan(sdtKhach);
            }

            if (!dsPhuongThuc.isEmpty()) {
                req.setPhuongThucThanhToanId(dsPhuongThuc.get(0).getId());
            }
            model.addAttribute("datHangRequest", req);
        }

        // 5. Tính toán chi phí ban đầu (Hàng - Voucher - Điểm + Ship)
        CheckoutTinhTienResponse chiPhiBanDau = donHangService.tinhToanChiPhi(
                gioHang.getTongTienHang(),
                null,
                BigDecimal.ZERO,
                khachHangId
        );

        model.addAttribute("gioHang", gioHang);
        model.addAttribute("dsPhuongThuc", dsPhuongThuc);
        model.addAttribute("dsDiaChi", dsDiaChi);
        model.addAttribute("diaChiMacDinh", diaChiMacDinh);
        model.addAttribute("soDuCoolcash", soDuCoolcash);
        model.addAttribute("chiPhi", chiPhiBanDau);
        model.addAttribute("daDangNhap", khachHangId != null);

        return "khach-hang/thanh-toan";
    }

    /**
     * API tính toán chi phí Checkout động qua AJAX khi nhập Voucher hoặc đổi điểm Coolcash (US19).
     */
    @PostMapping("/tinh-phi")
    @ResponseBody
    public ResponseEntity<CheckoutTinhTienResponse> tinhPhiDong(
            @RequestParam(name = "maVoucher", required = false) String maVoucher,
            @RequestParam(name = "soDiemCoolcash", required = false) BigDecimal soDiemCoolcash,
            HttpServletRequest request) {

        Long khachHangId = layKhachHangIdTuCookie(request);
        Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

        GioHangResponse gioHang = gioHangService.layThongTinGioHang(khachHangId, cartIdVangLai);
        BigDecimal tongTienHang = (gioHang != null) ? gioHang.getTongTienHang() : BigDecimal.ZERO;

        CheckoutTinhTienResponse response = donHangService.tinhToanChiPhi(
                tongTienHang,
                maVoucher,
                soDiemCoolcash,
                khachHangId
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Tiếp nhận form submit Đặt hàng / Checkout (US19).
     */
    @PostMapping("/dat-hang")
    public String datHang(
            @Valid @ModelAttribute("datHangRequest") DatHangRequest request,
            BindingResult result,
            HttpServletRequest httpRequest,
            RedirectAttributes redirectAttributes) {

        Long khachHangId = layKhachHangIdTuCookie(httpRequest);
        Long cartIdVangLai = layCartIdVangLaiTuCookie(httpRequest);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.datHangRequest", result);
            redirectAttributes.addFlashAttribute("datHangRequest", request);
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng kiểm tra lại thông tin nhận hàng!");
            return "redirect:/thanh-toan";
        }

        try {
            DonHang donHangMoi = donHangService.taoDonHangTuGioHang(khachHangId, cartIdVangLai, request);

            // Kiểm tra nếu là cổng thanh toán trực tuyến (VNPay / MoMo) thì chuyển sang màn hình QR / Thanh toán trực tuyến (US20)
            PhuongThucThanhToan pttt = donHangMoi.getPhuongThucThanhToan();
            boolean laTrucTuyen = pttt != null && (Boolean.TRUE.equals(pttt.getLaTrucTuyen())
                    || (pttt.getMaPhuongThuc() != null && (pttt.getMaPhuongThuc().toUpperCase().contains("VNPAY")
                    || pttt.getMaPhuongThuc().toUpperCase().contains("MOMO"))));

            if (laTrucTuyen) {
                return "redirect:/thanh-toan/qr/" + donHangMoi.getMaDonHang();
            }

            return "redirect:/thanh-toan/thanh-cong/" + donHangMoi.getMaDonHang();
        } catch (Exception e) {
            log.error("Lỗi khi tạo đơn hàng: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("datHangRequest", request);
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/thanh-toan";
        }
    }

    /**
     * Màn hình Quét mã QR thanh toán Cổng trực tuyến VNPay / MoMo (US20 - Tiêu chí 1).
     */
    @GetMapping("/qr/{maDonHang}")
    public String hienThiManHinhQR(
            @PathVariable("maDonHang") String maDonHang,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            DonHang donHang = donHangService.layTheoMaDonHang(maDonHang);
            vn.fpoly.simplain.dto.PaymentUrlResponse paymentInfo = giaoDichThanhToanService.taoUrlThanhToanVaQr(donHang, request);

            model.addAttribute("donHang", donHang);
            model.addAttribute("paymentInfo", paymentInfo);
            return "khach-hang/thanh-toan-qr";
        } catch (Exception e) {
            log.error("Lỗi khi tạo mã QR thanh toán cho đơn #{}: {}", maDonHang, e.getMessage());
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Không thể tạo mã QR thanh toán: " + e.getMessage());
            return "redirect:/gio-hang";
        }
    }

    /**
     * Tiếp nhận Return URL từ cổng VNPay sau khi khách hàng hoàn tất hoặc hủy thanh toán (US20 - Tiêu chí 2).
     */
    @GetMapping("/vnpay-return")
    public String xuLyReturnVNPay(
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {

        String maDonHang = allParams.get("vnp_TxnRef");
        String responseCode = allParams.get("vnp_ResponseCode");

        log.info("Khách hàng quay lại từ VNPay: TxnRef = {}, ResponseCode = {}", maDonHang, responseCode);

        boolean thanhCong = giaoDichThanhToanService.xuLyReturnVNPay(allParams);
        if (thanhCong) {
            return "redirect:/thanh-toan/thanh-cong/" + maDonHang;
        } else {
            String thongBaoLoi = vnPayService.giaiMaLoiVNPay(responseCode);
            redirectAttributes.addFlashAttribute("thongBaoLoi", thongBaoLoi);
            return "redirect:/thanh-toan/that-bai/" + maDonHang + "?lyDo=" + java.net.URLEncoder.encode(thongBaoLoi, java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    /**
     * Tiếp nhận Return URL từ ví MoMo (US20 - Tiêu chí 2).
     */
    @GetMapping("/momo-return")
    public String xuLyReturnMoMo(
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {

        String orderId = allParams.get("orderId");
        String resultCode = allParams.get("resultCode");
        String message = allParams.getOrDefault("message", "Thanh toán qua MoMo không thành công!");

        log.info("Khách hàng quay lại từ MoMo: OrderId = {}, ResultCode = {}", orderId, resultCode);

        if ("0".equals(resultCode)) {
            giaoDichThanhToanService.xuLyIpnMoMo(allParams);
            return "redirect:/thanh-toan/thanh-cong/" + orderId;
        } else {
            redirectAttributes.addFlashAttribute("thongBaoLoi", message);
            return "redirect:/thanh-toan/that-bai/" + orderId + "?lyDo=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    /**
     * Trang thông báo Thanh toán thất bại hoặc Người dùng hủy giao dịch (US20).
     */
    @GetMapping("/that-bai/{maDonHang}")
    public String hienThiThanhToanThatBai(
            @PathVariable("maDonHang") String maDonHang,
            @RequestParam(name = "lyDo", required = false) String lyDo,
            Model model) {

        try {
            DonHang donHang = donHangService.layTheoMaDonHang(maDonHang);
            model.addAttribute("donHang", donHang);
            model.addAttribute("lyDo", lyDo != null ? lyDo : "Giao dịch thanh toán chưa hoàn tất!");
            return "khach-hang/thanh-toan-that-bai";
        } catch (Exception e) {
            return "redirect:/gio-hang";
        }
    }

    /**
     * API Polling kiểm tra trạng thái thanh toán của đơn hàng (AJAX gọi định kỳ trên trang QR) (US20).
     */
    @GetMapping("/kiem-tra-trang-thai/{maDonHang}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> kiemTraTrangThai(@PathVariable("maDonHang") String maDonHang) {
        String trangThai = giaoDichThanhToanService.kiemTraTrangThaiDonHang(maDonHang);
        Map<String, Object> res = new HashMap<>();
        res.put("maDonHang", maDonHang);
        res.put("trangThai", trangThai);
        res.put("daThanhCong", "THANH_CONG".equals(trangThai));
        res.put("redirectUrl", "/thanh-toan/thanh-cong/" + maDonHang);
        return ResponseEntity.ok(res);
    }

    /**
     * API Giả lập thanh toán thành công tức thì trên Sandbox để dễ dàng kiểm thử local (US20).
     */
    @PostMapping("/gia-lap-thanh-cong/{maDonHang}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> giaLapThanhCong(@PathVariable("maDonHang") String maDonHang) {
        try {
            giaoDichThanhToanService.giaLapThanhToanThanhCong(maDonHang);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("redirectUrl", "/thanh-toan/thanh-cong/" + maDonHang);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * Trang thông báo Đặt hàng thành công & Chi tiết hóa đơn (US19).
     */
    @GetMapping("/thanh-cong/{maDonHang}")
    public String hienThiDatHangThanhCong(
            @PathVariable("maDonHang") String maDonHang,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            DonHang donHang = donHangService.layTheoMaDonHang(maDonHang);
            List<ChiTietDonHang> danhSachChiTiet = donHangService.layChiTietTheoDonHangId(donHang.getId());

            model.addAttribute("donHang", donHang);
            model.addAttribute("danhSachChiTiet", danhSachChiTiet);
            return "khach-hang/dat-hang-thanh-cong";
        } catch (Exception e) {
            log.error("Không tìm thấy đơn hàng #{}: {}", maDonHang, e.getMessage());
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Không tìm thấy thông tin đơn hàng #" + maDonHang);
            return "redirect:/gio-hang";
        }
    }

    // ==========================================
    // CÁC HÀM TIỆN ÍCH HỖ TRỢ XÁC THỰC VÀ COOKIE
    // ==========================================

    private Long layKhachHangIdTuCookie(HttpServletRequest request) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            return null;
        }
        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);
            return thongTin != null ? thongTin.getId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Long layCartIdVangLaiTuCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (COOKIE_CART.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                try {
                    return Long.parseLong(cookie.getValue().trim());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return null;
    }

    private String layTokenTuCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (COOKIE_TOKEN.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
