package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.*;
import vn.fpoly.simplain.entity.YeuCauDoiTra;
import vn.fpoly.simplain.service.DoiTraService;
import vn.fpoly.simplain.service.KhachHangService;
import vn.fpoly.simplain.service.LoyaltyHoiVienService;
import vn.fpoly.simplain.service.SanPhamYeuThichService;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller phục vụ Khách hàng tạo & theo dõi Yêu cầu Đổi/Trả (RMA) Online (US24 - Mod 5 - Hưng).
 * - Tiêu chí 1: Form tick chọn sản phẩm lỗi trong đơn cũ.
 * - Tiêu chí 2: Chọn SKU mới muốn đổi hoặc Nhập STK nếu muốn hoàn tiền.
 * - Form Validation chuyên nghiệp & Bảo mật chống IDOR.
 */
@Slf4j
@Controller
@RequestMapping("/khach-hang")
@RequiredArgsConstructor
public class DoiTraKhachHangController {

    private final DoiTraService doiTraService;
    private final KhachHangService khachHangService;
    private final LoyaltyHoiVienService loyaltyHoiVienService;
    private final SanPhamYeuThichService sanPhamYeuThichService;

    /**
     * Màn hình danh sách tất cả Yêu cầu Đổi/Trả của khách hàng (US24).
     */
    @GetMapping("/doi-tra")
    public String hienThiDanhSachDoiTra(
            @RequestParam(name = "trangThai", defaultValue = "TAT_CA") String trangThai,
            @RequestParam(name = "q", required = false) String tuKhoa,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập để xem danh sách yêu cầu Đổi/Trả");
            return "redirect:/khach-hang/dang-nhap?redirect=/khach-hang/doi-tra";
        }

        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);

            if (page < 0) page = 0;
            if (size <= 0) size = 10;

            Page<YeuCauDoiTraDetailDto> pageResult = doiTraService.layDanhSachYeuCauDoiTraCuaKhachHang(
                    thongTin.getId(),
                    trangThai,
                    tuKhoa,
                    PageRequest.of(page, size)
            );

            model.addAttribute("thongTin", thongTin);
            model.addAttribute("danhSachRma", pageResult);
            model.addAttribute("trangThaiHienTai", trangThai);
            model.addAttribute("tuKhoaHienTai", tuKhoa != null ? tuKhoa : "");
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", pageResult.getTotalPages());
            model.addAttribute("totalElements", pageResult.getTotalElements());

            napThongTinBoTro(model, thongTin.getId());

            return "khach-hang/doi-tra/danh-sach";

        } catch (Exception e) {
            log.warn("Lỗi phiên đăng nhập khi xem danh sách đổi trả: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap?redirect=/khach-hang/doi-tra";
        }
    }

    /**
     * Màn hình biểu mẫu tạo Yêu cầu Đổi/Trả cho một đơn hàng cũ đã hoàn thành (US24 - Tiêu chí 1 & 2).
     */
    @GetMapping("/don-hang/{maDonHang}/doi-tra")
    public String hienThiFormTaoYeuCauDoiTra(
            @PathVariable("maDonHang") String maDonHang,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập để tạo yêu cầu Đổi/Trả hàng");
            return "redirect:/khach-hang/dang-nhap?redirect=/khach-hang/don-hang/" + maDonHang + "/doi-tra";
        }

        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);

            DonHangDoiTraInfoDto donHangInfo = doiTraService.layThongTinDonHangDeDoiTra(
                    maDonHang, thongTin.getId(), thongTin.getSoDienThoai());

            if (!model.containsAttribute("taoYeuCauRequest")) {
                TaoYeuCauDoiTraRequest formReq = new TaoYeuCauDoiTraRequest();
                formReq.setMaDonHang(maDonHang);
                formReq.setLoaiDoiTra("DOI_HANG"); // Mặc định hình thức đổi hàng

                // Gán mặc định thông tin nhận hàng từ đơn hàng gốc (Chuẩn Shopee)
                if (donHangInfo.getDonHang() != null) {
                    formReq.setTenNguoiNhan(donHangInfo.getDonHang().getTenNguoiNhan());
                    formReq.setSoDienThoaiNhan(donHangInfo.getDonHang().getSoDienThoaiNhan());
                    formReq.setDiaChiNhanHang(donHangInfo.getDonHang().getDiaChiGiaoHang());
                }

                List<ChiTietDoiTraItemRequest> itemRequests = new ArrayList<>();
                for (ItemDoiTraFormDto itemDto : donHangInfo.getDanhSachItem()) {
                    ChiTietDoiTraItemRequest itemReq = ChiTietDoiTraItemRequest.builder()
                            .selected(false)
                            .chiTietDonHangId(itemDto.getChiTietDonHangId())
                            .soLuongTra(1)
                            .tinhTrangSanPham("")
                            .maBienTheMoiId(itemDto.getDanhSachBienTheDoi().isEmpty() ? null : itemDto.getDanhSachBienTheDoi().get(0).getId())
                            .build();
                    itemRequests.add(itemReq);
                }
                formReq.setItems(itemRequests);
                model.addAttribute("taoYeuCauRequest", formReq);
            }

            model.addAttribute("thongTin", thongTin);
            model.addAttribute("donHangInfo", donHangInfo);
            napThongTinBoTro(model, thongTin.getId());

            return "khach-hang/doi-tra/tao-yeu-cau";

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Không thể mở form đổi trả cho đơn #{}: {}", maDonHang, e.getMessage());
            redirectAttributes.addFlashAttribute("loiTruyCap", e.getMessage());
            return "redirect:/khach-hang/don-hang/" + maDonHang;
        } catch (Exception e) {
            log.error("Lỗi khi mở form đổi trả đơn #{}: {}", maDonHang, e.getMessage());
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap";
        }
    }

    /**
     * Xử lý gửi biểu mẫu Yêu cầu Đổi/Trả online (US24 - Form Validation).
     */
    @PostMapping("/don-hang/{maDonHang}/doi-tra")
    public String xuLyTaoYeuCauDoiTra(
            @PathVariable("maDonHang") String maDonHang,
            @Valid @ModelAttribute("taoYeuCauRequest") TaoYeuCauDoiTraRequest req,
            BindingResult bindingResult,
            @RequestParam(value = "hinhAnhFiles", required = false) MultipartFile[] hinhAnhFiles,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập để gửi yêu cầu Đổi/Trả");
            return "redirect:/khach-hang/dang-nhap";
        }

        ThongTinKhachHangResponse thongTin;
        try {
            thongTin = khachHangService.layThongTinTuToken(token);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn");
            return "redirect:/khach-hang/dang-nhap";
        }

        // Validate tùy chỉnh bổ sung cho form
        boolean coItemDuocChon = req.getItems() != null && req.getItems().stream().anyMatch(it -> Boolean.TRUE.equals(it.getSelected()));
        if (!coItemDuocChon) {
            bindingResult.reject("error.selectedItems", "Vui lòng tick chọn ít nhất một sản phẩm lỗi trong đơn hàng để đổi/trả!");
        }

        boolean laDoiHang = "DOI_HANG".equalsIgnoreCase(req.getLoaiDoiTra());
        boolean laHoanTien = "TRA_HANG_HOAN_TIEN".equalsIgnoreCase(req.getLoaiDoiTra());

        if (laDoiHang && req.getItems() != null) {
            for (ChiTietDoiTraItemRequest it : req.getItems()) {
                if (Boolean.TRUE.equals(it.getSelected()) && it.getMaBienTheMoiId() == null) {
                    bindingResult.reject("error.maBienTheMoi", "Vui lòng chọn SKU mới (màu/size) muốn đổi cho tất cả các sản phẩm đã tick chọn!");
                    break;
                }
            }
        }

        if (laHoanTien) {
            if (req.getTenNganHang() == null || req.getTenNganHang().isBlank()) {
                bindingResult.rejectValue("tenNganHang", "error.tenNganHang", "Vui lòng chọn hoặc nhập tên ngân hàng nhận tiền hoàn!");
            }
            if (req.getSoTaiKhoan() == null || !req.getSoTaiKhoan().matches("^[0-9]{6,20}$")) {
                bindingResult.rejectValue("soTaiKhoan", "error.soTaiKhoan", "Số tài khoản ngân hàng không hợp lệ (phải từ 6 đến 20 chữ số)!");
            }
            if (req.getTenChuTaiKhoan() == null || req.getTenChuTaiKhoan().trim().length() < 3) {
                bindingResult.rejectValue("tenChuTaiKhoan", "error.tenChuTaiKhoan", "Vui lòng nhập tên chủ tài khoản nhận hoàn tiền (tối thiểu 3 ký tự)!");
            }
        }

        if (bindingResult.hasErrors()) {
            try {
                DonHangDoiTraInfoDto donHangInfo = doiTraService.layThongTinDonHangDeDoiTra(
                        maDonHang, thongTin.getId(), thongTin.getSoDienThoai());
                model.addAttribute("thongTin", thongTin);
                model.addAttribute("donHangInfo", donHangInfo);
                napThongTinBoTro(model, thongTin.getId());
                model.addAttribute("loiValidation", "Thông tin yêu cầu chưa hợp lệ. Vui lòng kiểm tra lại các trường được thông báo đỏ!");
                return "khach-hang/doi-tra/tao-yeu-cau";
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("loiTruyCap", ex.getMessage());
                return "redirect:/khach-hang/don-hang/" + maDonHang;
            }
        }

        try {
            req.setMaDonHang(maDonHang);
            YeuCauDoiTra saved = doiTraService.taoYeuCauDoiTra(req, hinhAnhFiles, thongTin.getId(), thongTin.getSoDienThoai());

            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Gửi Yêu cầu Đổi/Trả #" + saved.getMaYeuCauRma() + " thành công! Bộ phận CSKH sẽ liên hệ và xử lý trong vòng 24h.");
            return "redirect:/khach-hang/doi-tra/" + saved.getMaYeuCauRma();

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Lỗi nghiệp vụ khi tạo RMA: {}", e.getMessage());
            try {
                DonHangDoiTraInfoDto donHangInfo = doiTraService.layThongTinDonHangDeDoiTra(
                        maDonHang, thongTin.getId(), thongTin.getSoDienThoai());
                model.addAttribute("thongTin", thongTin);
                model.addAttribute("donHangInfo", donHangInfo);
                napThongTinBoTro(model, thongTin.getId());
                model.addAttribute("loiNghiepVu", e.getMessage());
                return "khach-hang/doi-tra/tao-yeu-cau";
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("loiTruyCap", e.getMessage());
                return "redirect:/khach-hang/don-hang/" + maDonHang;
            }
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi tạo RMA: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("loiTruyCap", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            return "redirect:/khach-hang/don-hang/" + maDonHang;
        }
    }

    /**
     * Màn hình xem chi tiết tiến trình một Yêu cầu Đổi/Trả (RMA).
     */
    @GetMapping("/doi-tra/{maRma}")
    public String hienThiChiTietYeuCauDoiTra(
            @PathVariable("maRma") String maRma,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập để xem chi tiết phiếu Đổi/Trả");
            return "redirect:/khach-hang/dang-nhap?redirect=/khach-hang/doi-tra/" + maRma;
        }

        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);

            YeuCauDoiTraDetailDto rmaDetail = doiTraService.layChiTietYeuCauDoiTra(maRma, thongTin.getId());

            model.addAttribute("thongTin", thongTin);
            model.addAttribute("rmaDetail", rmaDetail);
            napThongTinBoTro(model, thongTin.getId());

            return "khach-hang/doi-tra/chi-tiet";

        } catch (IllegalArgumentException e) {
            log.warn("Lỗi truy cập chi tiết RMA #{}: {}", maRma, e.getMessage());
            redirectAttributes.addFlashAttribute("loiTruyCap", e.getMessage());
            return "redirect:/khach-hang/doi-tra";
        } catch (Exception e) {
            log.error("Lỗi phiên đăng nhập khi xem RMA #{}: {}", maRma, e.getMessage());
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap";
        }
    }

    // =========================================================================
    // TIỆN ÍCH HỖ TRỢ
    // =========================================================================

    private void napThongTinBoTro(Model model, Long khachHangId) {
        try {
            model.addAttribute("soLuongYeuThich", sanPhamYeuThichService.demSoLuongYeuThich(khachHangId));
            model.addAttribute("hoiVien", loyaltyHoiVienService.layThongTinHoiVien(khachHangId));
        } catch (Exception ignored) {
        }
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
}
