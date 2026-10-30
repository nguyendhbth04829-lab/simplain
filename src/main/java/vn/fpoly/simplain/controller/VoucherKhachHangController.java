package vn.fpoly.simplain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.*;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.VoucherSoHuu;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.service.KhachHangService;
import vn.fpoly.simplain.service.LoyaltyHoiVienService;
import vn.fpoly.simplain.service.SanPhamYeuThichService;
import vn.fpoly.simplain.service.VoucherKhachHangService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller phục vụ Khách hàng Thu thập và Áp dụng Mã giảm giá (US27 - Mod 6 / Voucher - Khoa).
 * - Tiêu chí 1: Thu thập & Lưu voucher vào ví voucher_so_huu.
 * - Tiêu chí 2: API Validate check mã còn lượt / có hợp lệ với đơn không (Kèm thông báo lỗi rõ ràng).
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Customer Voucher API (US27)", description = "APIs thu thập và kiểm tra áp dụng mã giảm giá của Khách hàng")
public class VoucherKhachHangController {

    private final VoucherKhachHangService voucherKhachHangService;
    private final KhachHangService khachHangService;
    private final KhachHangRepository khachHangRepository;
    private final LoyaltyHoiVienService loyaltyHoiVienService;
    private final SanPhamYeuThichService sanPhamYeuThichService;

    /**
     * Màn hình Kho Voucher & Ví Voucher của Khách hàng (US27).
     */
    @GetMapping("/khach-hang/voucher")
    public String hienThiKhoVoucher(
            @RequestParam(name = "tab", defaultValue = "san-co") String tab,
            @RequestParam(name = "subTab", defaultValue = "KHA_DUNG") String subTab,
            HttpServletRequest request,
            Model model) {

        KhachHang khachHang = layKhachHangDangNhap(request);
        Long khachHangId = (khachHang != null) ? khachHang.getId() : null;

        // Danh sách voucher có thể thu thập trên sàn
        List<VoucherKhachHangDto> dsSanCo = voucherKhachHangService.layDanhSachVoucherSanCoChoKhachHang(khachHangId);

        // Danh sách voucher trong ví cá nhân (nếu đã đăng nhập)
        List<VoucherKhachHangDto> dsViCuaToi = voucherKhachHangService.layDanhSachViVoucher(khachHangId, subTab);
        long tongVoucherKhaDung = voucherKhachHangService.demSoVoucherKhaDung(khachHangId);

        model.addAttribute("khachHangDangNhap", khachHang);
        model.addAttribute("tabHienTai", tab);
        model.addAttribute("subTabHienTai", subTab);
        model.addAttribute("dsSanCo", dsSanCo);
        model.addAttribute("dsViCuaToi", dsViCuaToi);
        model.addAttribute("tongVoucherKhaDung", tongVoucherKhaDung);

        if (khachHangId != null) {
            napThongTinBoTro(model, khachHangId);
        }

        return "khach-hang/voucher";
    }

    /**
     * API Thu thập / Lưu Voucher vào bảng voucher_so_huu theo ID (US27 - Tiêu chí 1).
     */
    @PostMapping("/khach-hang/api/voucher/thu-thap/{voucherId}")
    @ResponseBody
    @Operation(summary = "Thu thập voucher theo ID vào ví cá nhân (US27 - Tiêu chí 1)")
    public ResponseEntity<ThuThapVoucherResponse> thuThapVoucherTheoId(
            @PathVariable("voucherId") Long voucherId,
            HttpServletRequest request) {

        KhachHang khachHang = layKhachHangDangNhap(request);
        if (khachHang == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ThuThapVoucherResponse.builder()
                            .thanhCong(false)
                            .yeuCauDangNhap(true)
                            .thongBao("Vui lòng đăng nhập tài khoản để thu thập mã giảm giá vào Ví Voucher!")
                            .build()
            );
        }

        try {
            VoucherSoHuu vsh = voucherKhachHangService.luuVoucherVaoVi(khachHang.getId(), voucherId);
            long tongKhaDung = voucherKhachHangService.demSoVoucherKhaDung(khachHang.getId());
            String code = (vsh.getMaGiamGia() != null) ? vsh.getMaGiamGia().getMaCode() : "";

            return ResponseEntity.ok(
                    ThuThapVoucherResponse.builder()
                            .thanhCong(true)
                            .yeuCauDangNhap(false)
                            .voucherId(voucherId)
                            .maCode(code)
                            .viVoucherId(vsh.getId())
                            .tongSoVoucherTrongVi((int) tongKhaDung)
                            .thongBao("Thu thập thành công mã giảm giá [" + code + "]! Đã lưu vào ví voucher của bạn.")
                            .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ThuThapVoucherResponse.builder()
                            .thanhCong(false)
                            .yeuCauDangNhap(false)
                            .voucherId(voucherId)
                            .thongBao(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Lỗi khi khách hàng ID={} thu thập voucher ID={}: {}", khachHang.getId(), voucherId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ThuThapVoucherResponse.builder()
                            .thanhCong(false)
                            .yeuCauDangNhap(false)
                            .thongBao("Hệ thống gặp sự cố khi lưu voucher: " + e.getMessage())
                            .build()
            );
        }
    }

    /**
     * API Thu thập Voucher qua nhập mã Code chữ (US27 - Tiêu chí 1).
     */
    @PostMapping("/khach-hang/api/voucher/thu-thap-ma")
    @ResponseBody
    @Operation(summary = "Thu thập voucher theo mã code chữ vào ví cá nhân (US27 - Tiêu chí 1)")
    public ResponseEntity<ThuThapVoucherResponse> thuThapVoucherTheoMa(
            @RequestParam("maCode") String maCode,
            HttpServletRequest request) {

        KhachHang khachHang = layKhachHangDangNhap(request);
        if (khachHang == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ThuThapVoucherResponse.builder()
                            .thanhCong(false)
                            .yeuCauDangNhap(true)
                            .thongBao("Vui lòng đăng nhập tài khoản để thu thập mã giảm giá vào Ví Voucher!")
                            .build()
            );
        }

        try {
            VoucherSoHuu vsh = voucherKhachHangService.luuVoucherVaoViTheoMaCode(khachHang.getId(), maCode);
            long tongKhaDung = voucherKhachHangService.demSoVoucherKhaDung(khachHang.getId());

            return ResponseEntity.ok(
                    ThuThapVoucherResponse.builder()
                            .thanhCong(true)
                            .yeuCauDangNhap(false)
                            .voucherId(vsh.getMaGiamGiaId())
                            .maCode(vsh.getMaGiamGia().getMaCode())
                            .viVoucherId(vsh.getId())
                            .tongSoVoucherTrongVi((int) tongKhaDung)
                            .thongBao("Thu thập thành công mã giảm giá [" + vsh.getMaGiamGia().getMaCode() + "]! Đã lưu vào ví voucher của bạn.")
                            .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ThuThapVoucherResponse.builder()
                            .thanhCong(false)
                            .yeuCauDangNhap(false)
                            .thongBao(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Lỗi khi khách hàng ID={} thu thập mã code {}: {}", khachHang.getId(), maCode, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ThuThapVoucherResponse.builder()
                            .thanhCong(false)
                            .yeuCauDangNhap(false)
                            .thongBao("Hệ thống gặp sự cố: " + e.getMessage())
                            .build()
            );
        }
    }

    /**
     * API Validate kiểm tra tính hợp lệ của mã giảm giá với đơn hàng qua JSON (US27 - Tiêu chí 2).
     */
    @PostMapping(value = {"/khach-hang/api/voucher/kiem-tra", "/api/voucher/kiem-tra"}, consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "Kiểm tra mã giảm giá còn lượt / có hợp lệ với đơn không qua JSON (US27 - Tiêu chí 2)")
    public ResponseEntity<ValidateVoucherResultDto> kiemTraHopLeVoucherJson(
            @RequestBody ValidateVoucherRequest bodyReq,
            HttpServletRequest request) {

        String code = bodyReq != null ? bodyReq.getMaCode() : null;
        BigDecimal tongTien = bodyReq != null ? bodyReq.getTongTienHang() : null;
        Long khId = bodyReq != null ? bodyReq.getKhachHangId() : null;
        if (khId == null) {
            KhachHang khachHang = layKhachHangDangNhap(request);
            if (khachHang != null) khId = khachHang.getId();
        }

        return ResponseEntity.ok(voucherKhachHangService.kiemTraVaApDungVoucher(code, tongTien, khId));
    }

    /**
     * API Validate kiểm tra tính hợp lệ của mã giảm giá với đơn hàng qua Form Params / Query Params (US27 - Tiêu chí 2).
     */
    @PostMapping({"/khach-hang/api/voucher/kiem-tra", "/api/voucher/kiem-tra"})
    @ResponseBody
    @Operation(summary = "Kiểm tra mã giảm giá còn lượt / có hợp lệ với đơn không qua Form / Query (US27 - Tiêu chí 2)")
    public ResponseEntity<ValidateVoucherResultDto> kiemTraHopLeVoucherParams(
            @RequestParam(name = "maCode", required = false) String paramMaCode,
            @RequestParam(name = "tongTienHang", required = false) BigDecimal paramTongTienHang,
            @RequestParam(name = "khachHangId", required = false) Long paramKhachHangId,
            HttpServletRequest request) {

        Long khId = paramKhachHangId;
        if (khId == null) {
            KhachHang khachHang = layKhachHangDangNhap(request);
            if (khachHang != null) khId = khachHang.getId();
        }

        return ResponseEntity.ok(voucherKhachHangService.kiemTraVaApDungVoucher(paramMaCode, paramTongTienHang, khId));
    }

    /**
     * API Lấy danh sách Voucher khả dụng trong Ví để mở Popup chọn khi Checkout (US27).
     */
    @GetMapping("/khach-hang/api/voucher/vi-voucher")
    @ResponseBody
    @Operation(summary = "Lấy danh sách Voucher khả dụng trong ví của khách hàng (US27)")
    public ResponseEntity<?> layDanhSachViVoucherApi(HttpServletRequest request) {
        KhachHang khachHang = layKhachHangDangNhap(request);
        if (khachHang == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    java.util.Map.of("success", false, "message", "Vui lòng đăng nhập để xem ví voucher!")
            );
        }

        List<VoucherKhachHangDto> danhSach = voucherKhachHangService.layDanhSachViVoucher(khachHang.getId(), "KHA_DUNG");
        return ResponseEntity.ok(danhSach);
    }

    /**
     * API Lấy danh sách Voucher sẵn có trên sàn để hiển thị (US27).
     */
    @GetMapping("/khach-hang/api/voucher/san-co")
    @ResponseBody
    @Operation(summary = "Lấy danh sách Voucher sẵn có trên sàn để thu thập (US27)")
    public ResponseEntity<List<VoucherKhachHangDto>> layDanhSachSanCoApi(HttpServletRequest request) {
        KhachHang khachHang = layKhachHangDangNhap(request);
        Long khId = (khachHang != null) ? khachHang.getId() : null;
        List<VoucherKhachHangDto> danhSach = voucherKhachHangService.layDanhSachVoucherSanCoChoKhachHang(khId);
        return ResponseEntity.ok(danhSach);
    }

    // =========================================================================
    // TIỆN ÍCH HỖ TRỢ
    // =========================================================================

    private KhachHang layKhachHangDangNhap(HttpServletRequest request) {
        String token = layTokenTuCookie(request);
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);
            if (thongTin != null && thongTin.getId() != null) {
                return khachHangRepository.findById(thongTin.getId()).orElse(null);
            }
        } catch (Exception e) {
            log.debug("Không thể lấy thông tin khách hàng từ JWT token: {}", e.getMessage());
        }
        return null;
    }

    private String layTokenTuCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (KhachHangController.COOKIE_TOKEN.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void napThongTinBoTro(Model model, Long khachHangId) {
        try {
            model.addAttribute("soLuongYeuThich", sanPhamYeuThichService.demSoLuongYeuThich(khachHangId));
            model.addAttribute("hoiVien", loyaltyHoiVienService.layThongTinHoiVien(khachHangId));
        } catch (Exception ignored) {
        }
    }
}
