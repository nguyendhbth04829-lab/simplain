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
            @RequestParam(name = "bienTheId", required = false) Long bienTheId,
            @RequestParam(name = "soLuong", required = false) Integer soLuong,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        Long khachHangId = layKhachHangIdTuCookie(request);
        if (khachHangId == null) {
            String redirectUrl = "/thanh-toan";
            if (bienTheId != null) {
                redirectUrl += "?bienTheId=" + bienTheId + "&soLuong=" + (soLuong != null ? soLuong : 1);
            }
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng đăng nhập để tiến hành thanh toán đơn hàng!");
            return "redirect:/khach-hang/dang-nhap?redirect=" + java.net.URLEncoder.encode(redirectUrl, java.nio.charset.StandardCharsets.UTF_8);
        }
        Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

        // 1. Kiểm tra giỏ hàng hoặc thông tin Mua ngay 1 sản phẩm đang xem
        GioHangResponse gioHang;
        if (bienTheId != null) {
            gioHang = gioHangService.layThongTinMuaNgay(bienTheId, soLuong);
            if (gioHang == null || Boolean.TRUE.equals(gioHang.getGioHangRong())) {
                redirectAttributes.addFlashAttribute("thongBaoLoi", "Sản phẩm bạn chọn không hợp lệ hoặc đã ngừng kinh doanh!");
                return "redirect:/cua-hang";
            }
        } else {
            gioHang = gioHangService.layThongTinGioHang(khachHangId, cartIdVangLai);
            if (gioHang == null || Boolean.TRUE.equals(gioHang.getGioHangRong())) {
                redirectAttributes.addFlashAttribute("thongBaoLoi", "Giỏ hàng của bạn đang trống! Hãy thêm sản phẩm trước khi thanh toán.");
                return "redirect:/gio-hang";
            }
        }
        if (Boolean.TRUE.equals(gioHang.getCoSanPhamNgungKinhDoanh())) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Sản phẩm bạn chọn đã ngừng kinh doanh. Vui lòng chọn sản phẩm khác!");
            return bienTheId != null ? "redirect:/cua-hang" : "redirect:/gio-hang";
        }

        // 2. Lấy danh sách cổng thanh toán đang BẬT kích hoạt (loại trừ Tiền mặt tại quầy TIEN_MAT)
        List<PhuongThucThanhToan> dsPhuongThuc = phuongThucThanhToanService.layDanhSachDangKichHoat()
                .stream()
                .filter(pt -> !"TIEN_MAT".equalsIgnoreCase(pt.getMaPhuongThuc()) && !"TAI_QUAY".equalsIgnoreCase(pt.getMaPhuongThuc()))
                .toList();

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
        DatHangRequest req = model.containsAttribute("datHangRequest")
                ? (DatHangRequest) model.getAttribute("datHangRequest")
                : new DatHangRequest();

        if (req.getTenNguoiNhan() == null || req.getTenNguoiNhan().isBlank()) {
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
        }
        if (bienTheId != null) {
            req.setBienTheId(bienTheId);
            req.setSoLuong(soLuong != null && soLuong > 0 ? soLuong : 1);
        }

        // 5. Tính toán chi phí ban đầu (Hàng - Voucher - Điểm + Ship)
        CheckoutTinhTienResponse chiPhiBanDau = donHangService.tinhToanChiPhi(
                gioHang.getTongTienHang(),
                null,
                BigDecimal.ZERO,
                khachHangId
        );

        // 5.1. Kiểm tra điều kiện áp dụng COD (Chống gom hàng & Chống bom hàng)
        String sdtKiemTra = (req.getSoDienThoaiNhan() != null && !req.getSoDienThoaiNhan().isBlank())
                ? req.getSoDienThoaiNhan()
                : (diaChiDuocChon != null ? diaChiDuocChon.getSoDienThoai() : sdtKhach);
        KiemTraCodResult codCheck = donHangService.kiemTraDieuKienApDungCod(khachHangId, sdtKiemTra, chiPhiBanDau.getTongThanhToan());
        chiPhiBanDau.setChoPhepCod(codCheck.isChoPhepCod());
        chiPhiBanDau.setLyDoChanCod(codCheck.getLyDoChanCod());

        if (!dsPhuongThuc.isEmpty()) {
            if (req.getPhuongThucThanhToanId() == null) {
                if (codCheck.isChoPhepCod()) {
                    req.setPhuongThucThanhToanId(dsPhuongThuc.get(0).getId());
                } else {
                    // Nếu COD bị chặn, mặc định chọn cổng trực tuyến (Online)
                    PhuongThucThanhToan ptttOnline = dsPhuongThuc.stream()
                            .filter(p -> Boolean.TRUE.equals(p.getLaTrucTuyen()) || !"COD".equalsIgnoreCase(p.getMaPhuongThuc()))
                            .findFirst().orElse(dsPhuongThuc.get(0));
                    req.setPhuongThucThanhToanId(ptttOnline.getId());
                }
            } else if (!codCheck.isChoPhepCod()) {
                // Nếu đang chọn COD nhưng bị chặn, đổi sang cổng Online
                boolean dangChonCod = dsPhuongThuc.stream()
                        .anyMatch(p -> p.getId().equals(req.getPhuongThucThanhToanId()) && ("COD".equalsIgnoreCase(p.getMaPhuongThuc()) || !Boolean.TRUE.equals(p.getLaTrucTuyen())));
                if (dangChonCod) {
                    PhuongThucThanhToan ptttOnline = dsPhuongThuc.stream()
                            .filter(p -> Boolean.TRUE.equals(p.getLaTrucTuyen()) || !"COD".equalsIgnoreCase(p.getMaPhuongThuc()))
                            .findFirst().orElse(null);
                    if (ptttOnline != null) {
                        req.setPhuongThucThanhToanId(ptttOnline.getId());
                    }
                }
            }
        }
        model.addAttribute("datHangRequest", req);

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
            @RequestParam(name = "bienTheId", required = false) Long bienTheId,
            @RequestParam(name = "soLuong", required = false) Integer soLuong,
            HttpServletRequest request) {

        Long khachHangId = layKhachHangIdTuCookie(request);
        Long cartIdVangLai = layCartIdVangLaiTuCookie(request);

        GioHangResponse gioHang;
        if (bienTheId != null) {
            gioHang = gioHangService.layThongTinMuaNgay(bienTheId, soLuong);
        } else {
            gioHang = gioHangService.layThongTinGioHang(khachHangId, cartIdVangLai);
        }
        BigDecimal tongTienHang = (gioHang != null) ? gioHang.getTongTienHang() : BigDecimal.ZERO;

        CheckoutTinhTienResponse response = donHangService.tinhToanChiPhi(
                tongTienHang,
                maVoucher,
                soDiemCoolcash,
                khachHangId
        );

        // Kiểm tra điều kiện COD động theo số tiền mới và thông tin khách hàng
        String sdtKhach = "";
        if (khachHangId != null) {
            try {
                String token = layTokenTuCookie(request);
                ThongTinKhachHangResponse kh = khachHangService.layThongTinTuToken(token);
                if (kh != null && kh.getSoDienThoai() != null) {
                    sdtKhach = kh.getSoDienThoai();
                }
            } catch (Exception ignored) {
            }
        }
        KiemTraCodResult codCheck = donHangService.kiemTraDieuKienApDungCod(khachHangId, sdtKhach, response.getTongThanhToan());
        response.setChoPhepCod(codCheck.isChoPhepCod());
        response.setLyDoChanCod(codCheck.getLyDoChanCod());

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
        if (khachHangId == null) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng đăng nhập để tiến hành đặt hàng!");
            return "redirect:/khach-hang/dang-nhap?redirect=/thanh-toan";
        }
        Long cartIdVangLai = layCartIdVangLaiTuCookie(httpRequest);

        String redirectOnError = "/thanh-toan";
        if (request.getBienTheId() != null) {
            redirectOnError += "?bienTheId=" + request.getBienTheId() + "&soLuong=" + (request.getSoLuong() != null ? request.getSoLuong() : 1);
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.datHangRequest", result);
            redirectAttributes.addFlashAttribute("datHangRequest", request);
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng kiểm tra lại thông tin nhận hàng!");
            return "redirect:" + redirectOnError;
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
            return "redirect:" + redirectOnError;
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
     * Màn hình Gateway Ví MoMo Sandbox giả lập trực tiếp (US20).
     */
    @GetMapping("/momo-gateway")
    public String hienThiMoMoGateway(
            @RequestParam(name = "orderId", required = false) String orderId,
            @RequestParam(name = "amount", required = false) Long amount,
            @RequestParam(name = "maDonHang", required = false) String maDonHang,
            @RequestParam(name = "returnUrl", required = false) String returnUrl,
            Model model) {

        model.addAttribute("orderId", orderId != null ? orderId : "");
        model.addAttribute("amount", amount != null ? amount : 0L);
        model.addAttribute("maDonHang", maDonHang != null ? maDonHang : "");
        model.addAttribute("returnUrl", returnUrl != null ? returnUrl : "/");
        return "khach-hang/momo-gateway";
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
        String maDonHang = allParams.getOrDefault("maDonHang", "");
        if (maDonHang.isBlank() && orderId != null) {
            maDonHang = orderId;
        }

        log.info("Khách hàng quay lại từ MoMo: OrderId = {}, ResultCode = {}, maDonHang = {}", orderId, resultCode, maDonHang);

        if ("0".equals(resultCode)) {
            if (!maDonHang.isBlank()) {
                try {
                    giaoDichThanhToanService.giaLapThanhToanThanhCong(maDonHang);
                } catch (Exception e) {
                    try {
                        giaoDichThanhToanService.xuLyIpnMoMo(allParams);
                    } catch (Exception ex) {
                        log.warn("Không thể xử lý MoMo return cho đơn #{}: {}", maDonHang, ex.getMessage());
                    }
                }
                return "redirect:/thanh-toan/thanh-cong/" + maDonHang;
            }
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("thongBaoLoi", message);
            return "redirect:/thanh-toan/that-bai/" + maDonHang + "?lyDo=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    /**
     * Màn hình Gateway Ví ZaloPay Sandbox giả lập trực tiếp (US20).
     */
    @GetMapping("/zalopay-gateway")
    public String hienThiZaloPayGateway(
            @RequestParam(name = "apptransid", required = false) String apptransid,
            @RequestParam(name = "amount", required = false) Long amount,
            @RequestParam(name = "maDonHang", required = false) String maDonHang,
            @RequestParam(name = "returnUrl", required = false) String returnUrl,
            Model model) {

        model.addAttribute("apptransid", apptransid != null ? apptransid : "");
        model.addAttribute("amount", amount != null ? amount : 0L);
        model.addAttribute("maDonHang", maDonHang != null ? maDonHang : "");
        model.addAttribute("returnUrl", returnUrl != null ? returnUrl : "/");
        return "khach-hang/zalopay-gateway";
    }

    /**
     * Tiếp nhận Return URL từ cổng ví ZaloPay (US20).
     */
    @GetMapping("/zalopay-return")
    public String xuLyReturnZaloPay(
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {

        String appTransId = allParams.get("apptransid");
        String status = allParams.getOrDefault("status", "1");
        String maDonHang = allParams.getOrDefault("maDonHang", "");

        log.info("Khách hàng quay lại từ ZaloPay: apptransid = {}, status = {}", appTransId, status);

        if ("1".equals(status)) {
            if (!maDonHang.isBlank()) {
                try {
                    giaoDichThanhToanService.giaLapThanhToanThanhCong(maDonHang);
                } catch (Exception e) {
                    log.warn("Không thể cập nhật tự động cho đơn #{}: {}", maDonHang, e.getMessage());
                }
                return "redirect:/thanh-toan/thanh-cong/" + maDonHang;
            }
            return "redirect:/";
        } else {
            String loi = "Thanh toán ZaloPay không thành công hoặc đã bị hủy.";
            redirectAttributes.addFlashAttribute("thongBaoLoi", loi);
            return "redirect:/thanh-toan/that-bai/" + maDonHang;
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
        try {
            jakarta.servlet.http.HttpSession session = request.getSession(false);
            if (session != null) {
                Object obj = session.getAttribute("khachHangHienTai");
                if (obj instanceof vn.fpoly.simplain.dto.XacThucResponse xt) {
                    return xt.getId();
                } else if (obj instanceof vn.fpoly.simplain.dto.ThongTinKhachHangResponse tt) {
                    return tt.getId();
                } else if (obj instanceof vn.fpoly.simplain.entity.KhachHang kh) {
                    return kh.getId();
                }
                Object khIdObj = session.getAttribute("khach_hang_id");
                if (khIdObj instanceof Long id) {
                    return id;
                }
            }
        } catch (Exception ignored) {
        }

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
