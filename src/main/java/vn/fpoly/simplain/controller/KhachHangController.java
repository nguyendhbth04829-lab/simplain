package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.CapNhatThongTinRequest;
import vn.fpoly.simplain.dto.DangKyRequest;
import vn.fpoly.simplain.dto.DangNhapRequest;
import vn.fpoly.simplain.dto.DiaChiRequest;
import vn.fpoly.simplain.dto.DoiMatKhauRequest;
import vn.fpoly.simplain.dto.ThongTinKhachHangResponse;
import vn.fpoly.simplain.dto.XacThucResponse;
import vn.fpoly.simplain.entity.DiaChi;
import vn.fpoly.simplain.service.DiaChiService;
import vn.fpoly.simplain.service.KhachHangService;
import vn.fpoly.simplain.service.LoyaltyHoiVienService;
import vn.fpoly.simplain.service.SanPhamYeuThichService;

import java.util.List;

/**
 * Controller MVC quản lý tài khoản Khách hàng (US01, US12), Sổ địa chỉ giao hàng (US13) và Danh sách Yêu thích (US14).
 */
@Controller
@RequestMapping("/khach-hang")
public class KhachHangController {

    public static final String COOKIE_TOKEN = "jwt_token";

    private final KhachHangService khachHangService;
    private final DiaChiService diaChiService;
    private final SanPhamYeuThichService sanPhamYeuThichService;
    private final LoyaltyHoiVienService loyaltyHoiVienService;

    @Autowired
    public KhachHangController(KhachHangService khachHangService,
                               DiaChiService diaChiService,
                               SanPhamYeuThichService sanPhamYeuThichService,
                               LoyaltyHoiVienService loyaltyHoiVienService) {
        this.khachHangService = khachHangService;
        this.diaChiService = diaChiService;
        this.sanPhamYeuThichService = sanPhamYeuThichService;
        this.loyaltyHoiVienService = loyaltyHoiVienService;
    }

    /**
     * Màn hình đăng nhập khách hàng.
     */
    @GetMapping("/dang-nhap")
    public String hienThiDangNhap(Model model, HttpServletRequest request) {
        String token = layTokenTuCookie(request);
        if (token != null) {
            try {
                khachHangService.layThongTinTuToken(token);
                return "redirect:/khach-hang/thong-tin";
            } catch (Exception ignored) {
            }
        }

        if (!model.containsAttribute("dangNhapRequest")) {
            model.addAttribute("dangNhapRequest", new DangNhapRequest());
        }
        return "khach-hang/dang-nhap";
    }

    /**
     * Xử lý gửi form đăng nhập từ giao diện web.
     */
    @PostMapping("/dang-nhap")
    public String xuLyDangNhap(@Valid @ModelAttribute("dangNhapRequest") DangNhapRequest dangNhapRequest,
                               BindingResult result,
                               HttpServletResponse response,
                               HttpSession session,
                               Model model) {
        if (result.hasErrors()) {
            return "khach-hang/dang-nhap";
        }

        try {
            XacThucResponse xacThuc = khachHangService.dangNhap(dangNhapRequest);

            // Lưu JWT token vào Cookie để duy trì phiên làm việc
            Cookie cookie = new Cookie(COOKIE_TOKEN, xacThuc.getToken());
            cookie.setPath("/");
            cookie.setMaxAge(86400); // 1 ngày
            cookie.setHttpOnly(false);
            response.addCookie(cookie);

            session.setAttribute("khachHangHienTai", xacThuc);
            return "redirect:/khach-hang/thong-tin";
        } catch (Exception e) {
            model.addAttribute("loiDangNhap", e.getMessage());
            return "khach-hang/dang-nhap";
        }
    }

    /**
     * Màn hình đăng ký tài khoản khách hàng mới.
     */
    @GetMapping("/dang-ky")
    public String hienThiDangKy(Model model, HttpServletRequest request) {
        String token = layTokenTuCookie(request);
        if (token != null) {
            try {
                khachHangService.layThongTinTuToken(token);
                return "redirect:/khach-hang/thong-tin";
            } catch (Exception ignored) {
            }
        }

        if (!model.containsAttribute("dangKyRequest")) {
            model.addAttribute("dangKyRequest", new DangKyRequest());
        }
        return "khach-hang/dang-ky";
    }

    /**
     * Xử lý gửi form đăng ký kèm tải ảnh đại diện từ máy tính.
     */
    @PostMapping("/dang-ky")
    public String xuLyDangKy(@Valid @ModelAttribute("dangKyRequest") DangKyRequest dangKyRequest,
                             BindingResult result,
                             @RequestParam(value = "anhDaiDienFile", required = false) MultipartFile anhDaiDienFile,
                             HttpServletResponse response,
                             HttpSession session,
                             Model model) {
        // Kiểm tra khớp mật khẩu
        if (dangKyRequest.getXacNhanMatKhau() != null && !dangKyRequest.getXacNhanMatKhau().isBlank()) {
            if (!dangKyRequest.getMatKhau().equals(dangKyRequest.getXacNhanMatKhau())) {
                result.rejectValue("xacNhanMatKhau", "error.dangKyRequest", "Mật khẩu xác nhận không trùng khớp");
            }
        }

        if (result.hasErrors()) {
            return "khach-hang/dang-ky";
        }

        try {
            XacThucResponse xacThuc = khachHangService.dangKy(dangKyRequest, anhDaiDienFile);

            // Tự động cấp Cookie JWT sau khi đăng ký
            Cookie cookie = new Cookie(COOKIE_TOKEN, xacThuc.getToken());
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            cookie.setHttpOnly(false);
            response.addCookie(cookie);

            session.setAttribute("khachHangHienTai", xacThuc);
            return "redirect:/khach-hang/thong-tin";
        } catch (Exception e) {
            model.addAttribute("loiDangKy", e.getMessage());
            return "khach-hang/dang-ky";
        }
    }

    /**
     * Màn hình xem hồ sơ thông tin khách hàng, đổi mật khẩu và Sổ địa chỉ giao hàng (US12, US13).
     */
    @GetMapping({"/thong-tin", "/so-dia-chi"})
    public String hienThiThongTin(
            @RequestParam(name = "tab", required = false) String tab,
            @RequestParam(name = "redirect", required = false) String redirect,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập để xem thông tin tài khoản");
            return "redirect:/khach-hang/dang-nhap";
        }

        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);
            String tabMacDinh = "thong-tin";
            if (request.getRequestURI().endsWith("/so-dia-chi") || "so-dia-chi".equalsIgnoreCase(tab)) {
                tabMacDinh = "so-dia-chi";
            } else if ("doi-mat-khau".equalsIgnoreCase(tab)) {
                tabMacDinh = "doi-mat-khau";
            } else if ("hoi-vien".equalsIgnoreCase(tab)) {
                tabMacDinh = "hoi-vien";
            } else if ("don-hang".equalsIgnoreCase(tab)) {
                return "redirect:/khach-hang/don-hang";
            }
            boSungDuLieuTrangThongTin(model, thongTin, token, tabMacDinh);
            model.addAttribute("redirect", redirect);
            return "khach-hang/thong-tin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại");
            return "redirect:/khach-hang/dang-nhap";
        }
    }

    /**
     * Bổ sung đầy đủ dữ liệu model cho trang thông tin (Hồ sơ, Mật khẩu, Sổ địa chỉ)
     * tránh lỗi thiếu binding object khi Thymeleaf render.
     */
    private void boSungDuLieuTrangThongTin(Model model, ThongTinKhachHangResponse thongTin, String token, String tabActive) {
        model.addAttribute("thongTin", thongTin);
        model.addAttribute("jwtToken", token);
        model.addAttribute("danhSachDiaChi", diaChiService.layDanhSach(thongTin.getId()));
        model.addAttribute("diaChiMacDinh", diaChiService.layDiaChiMacDinh(thongTin.getId()));
        model.addAttribute("soLuongYeuThich", sanPhamYeuThichService.demSoLuongYeuThich(thongTin.getId()));
        model.addAttribute("danhSachYeuThich", sanPhamYeuThichService.layDanhSachSanPhamYeuThich(thongTin.getId()));
        model.addAttribute("hoiVien", loyaltyHoiVienService.layThongTinHoiVien(thongTin.getId()));

        if (!model.containsAttribute("capNhatThongTinRequest")) {
            CapNhatThongTinRequest capNhatReq = CapNhatThongTinRequest.builder()
                    .hoTen(thongTin.getHoTen())
                    .email(thongTin.getEmail())
                    .soDienThoai(thongTin.getSoDienThoai())
                    .gioiTinh(thongTin.getGioiTinh())
                    .ngaySinh(thongTin.getNgaySinh())
                    .build();
            model.addAttribute("capNhatThongTinRequest", capNhatReq);
        }
        if (!model.containsAttribute("doiMatKhauRequest")) {
            model.addAttribute("doiMatKhauRequest", new DoiMatKhauRequest());
        }
        if (!model.containsAttribute("diaChiRequest")) {
            model.addAttribute("diaChiRequest", new DiaChiRequest());
        }
        if (!model.containsAttribute("tabActive")) {
            model.addAttribute("tabActive", tabActive);
        }
    }

    /**
     * Xử lý Cập nhật thông tin cá nhân khách hàng (US12 - Tiêu chí 1).
     */
    @PostMapping("/cap-nhat-thong-tin")
    public String xuLyCapNhatThongTin(@Valid @ModelAttribute("capNhatThongTinRequest") CapNhatThongTinRequest capNhatRequest,
                                      BindingResult result,
                                      @RequestParam(value = "anhDaiDienFile", required = false) MultipartFile anhDaiDienFile,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap";
        }

        ThongTinKhachHangResponse thongTinHienTai;
        try {
            thongTinHienTai = khachHangService.layThongTinTuToken(token);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập không hợp lệ!");
            return "redirect:/khach-hang/dang-nhap";
        }

        if (result.hasErrors()) {
            boSungDuLieuTrangThongTin(model, thongTinHienTai, token, "thong-tin");
            return "khach-hang/thong-tin";
        }

        try {
            XacThucResponse xacThuc = khachHangService.capNhatThongTin(thongTinHienTai.getId(), capNhatRequest, anhDaiDienFile);

            // Cập nhật lại Cookie token mới
            Cookie cookie = new Cookie(COOKIE_TOKEN, xacThuc.getToken());
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            cookie.setHttpOnly(false);
            response.addCookie(cookie);

            session.setAttribute("khachHangHienTai", xacThuc);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Cập nhật thông tin cá nhân thành công!");
            return "redirect:/khach-hang/thong-tin";
        } catch (Exception e) {
            boSungDuLieuTrangThongTin(model, thongTinHienTai, token, "thong-tin");
            model.addAttribute("loiCapNhat", e.getMessage());
            return "khach-hang/thong-tin";
        }
    }

    /**
     * Xử lý Đổi mật khẩu tài khoản khách hàng (US12 - Tiêu chí 2).
     */
    @PostMapping("/doi-mat-khau")
    public String xuLyDoiMatKhau(@Valid @ModelAttribute("doiMatKhauRequest") DoiMatKhauRequest doiMatKhauRequest,
                                 BindingResult result,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap";
        }

        ThongTinKhachHangResponse thongTinHienTai;
        try {
            thongTinHienTai = khachHangService.layThongTinTuToken(token);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên đăng nhập không hợp lệ!");
            return "redirect:/khach-hang/dang-nhap";
        }

        if (doiMatKhauRequest.getMatKhauMoi() != null && doiMatKhauRequest.getXacNhanMatKhauMoi() != null) {
            if (!doiMatKhauRequest.getMatKhauMoi().equals(doiMatKhauRequest.getXacNhanMatKhauMoi())) {
                result.rejectValue("xacNhanMatKhauMoi", "error.doiMatKhauRequest", "Xác nhận mật khẩu mới không trùng khớp!");
            }
        }

        if (result.hasErrors()) {
            boSungDuLieuTrangThongTin(model, thongTinHienTai, token, "doi-mat-khau");
            return "khach-hang/thong-tin";
        }

        try {
            XacThucResponse xacThuc = khachHangService.doiMatKhau(thongTinHienTai.getId(), doiMatKhauRequest);

            // Cập nhật lại Cookie token mới
            Cookie cookie = new Cookie(COOKIE_TOKEN, xacThuc.getToken());
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            cookie.setHttpOnly(false);
            response.addCookie(cookie);

            session.setAttribute("khachHangHienTai", xacThuc);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đổi mật khẩu tài khoản thành công!");
            return "redirect:/khach-hang/thong-tin";
        } catch (Exception e) {
            boSungDuLieuTrangThongTin(model, thongTinHienTai, token, "doi-mat-khau");
            model.addAttribute("loiDoiMatKhau", e.getMessage());
            return "khach-hang/thong-tin";
        }
    }

    /**
     * Thêm mới địa chỉ vào sổ địa chỉ (US13 - Tiêu chí 1 & 3).
     */
    @PostMapping("/dia-chi/them")
    public String themDiaChi(
            @Valid @ModelAttribute("diaChiRequest") DiaChiRequest diaChiRequest,
            BindingResult result,
            @RequestParam(name = "redirect", required = false) String redirect,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap";
        }

        ThongTinKhachHangResponse thongTin;
        try {
            thongTin = khachHangService.layThongTinTuToken(token);
        } catch (Exception e) {
            return "redirect:/khach-hang/dang-nhap";
        }

        if (result.hasErrors()) {
            boSungDuLieuTrangThongTin(model, thongTin, token, "so-dia-chi");
            model.addAttribute("loiDiaChi", "Dữ liệu địa chỉ chưa hợp lệ, vui lòng kiểm tra lại thông tin!");
            model.addAttribute("moModalThemDiaChi", true);
            model.addAttribute("redirect", redirect);
            return "khach-hang/thong-tin";
        }

        try {
            DiaChi dcMoi = diaChiService.them(thongTin.getId(), diaChiRequest);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Thêm mới địa chỉ giao hàng thành công!");
            if (redirect != null && !redirect.isBlank()) {
                String target = redirect;
                if (target.startsWith("/thanh-toan") && dcMoi != null) {
                    target = target + (target.contains("?") ? "&" : "?") + "diaChiId=" + dcMoi.getId();
                }
                return "redirect:" + target;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDiaChi", e.getMessage());
        }

        String extraRedirect = (redirect != null && !redirect.isBlank()) ? ("&redirect=" + redirect) : "";
        return "redirect:/khach-hang/thong-tin?tab=so-dia-chi" + extraRedirect;
    }

    /**
     * Cập nhật địa chỉ trong sổ địa chỉ (US13 - Tiêu chí 1 & 3).
     */
    @PostMapping("/dia-chi/sua")
    public String suaDiaChi(
            @Valid @ModelAttribute("diaChiRequest") DiaChiRequest diaChiRequest,
            BindingResult result,
            @RequestParam(name = "redirect", required = false) String redirect,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập lại!");
            return "redirect:/khach-hang/dang-nhap";
        }

        ThongTinKhachHangResponse thongTin;
        try {
            thongTin = khachHangService.layThongTinTuToken(token);
        } catch (Exception e) {
            return "redirect:/khach-hang/dang-nhap";
        }

        if (diaChiRequest.getId() == null) {
            redirectAttributes.addFlashAttribute("loiDiaChi", "Mã địa chỉ cần cập nhật không hợp lệ!");
            return "redirect:/khach-hang/thong-tin?tab=so-dia-chi";
        }

        if (result.hasErrors()) {
            boSungDuLieuTrangThongTin(model, thongTin, token, "so-dia-chi");
            model.addAttribute("loiDiaChi", "Dữ liệu cập nhật địa chỉ chưa hợp lệ!");
            model.addAttribute("redirect", redirect);
            return "khach-hang/thong-tin";
        }

        try {
            diaChiService.sua(diaChiRequest.getId(), thongTin.getId(), diaChiRequest);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Cập nhật địa chỉ giao hàng thành công!");
            if (redirect != null && !redirect.isBlank()) {
                String target = redirect;
                if (target.startsWith("/thanh-toan")) {
                    target = target + (target.contains("?") ? "&" : "?") + "diaChiId=" + diaChiRequest.getId();
                }
                return "redirect:" + target;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDiaChi", e.getMessage());
        }

        String extraRedirect = (redirect != null && !redirect.isBlank()) ? ("&redirect=" + redirect) : "";
        return "redirect:/khach-hang/thong-tin?tab=so-dia-chi" + extraRedirect;
    }

    /**
     * Thiết lập địa chỉ mặc định (US13 - Tiêu chí 3).
     */
    @GetMapping("/dia-chi/dat-mac-dinh/{id}")
    public String datMacDinh(
            @PathVariable("id") Long id,
            @RequestParam(name = "redirect", required = false) String redirect,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            return "redirect:/khach-hang/dang-nhap";
        }

        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);
            diaChiService.datLamMacDinh(id, thongTin.getId());
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã thiết lập làm địa chỉ giao hàng mặc định!");
            if (redirect != null && !redirect.isBlank()) {
                String target = redirect;
                if (target.startsWith("/thanh-toan")) {
                    target = target + (target.contains("?") ? "&" : "?") + "diaChiId=" + id;
                }
                return "redirect:" + target;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDiaChi", e.getMessage());
        }
        return "redirect:/khach-hang/thong-tin?tab=so-dia-chi";
    }

    /**
     * Xóa địa chỉ giao hàng (US13 - Tiêu chí 1).
     */
    @GetMapping("/dia-chi/xoa/{id}")
    public String xoaDiaChi(
            @PathVariable("id") Long id,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            return "redirect:/khach-hang/dang-nhap";
        }

        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);
            diaChiService.xoa(id, thongTin.getId());
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa địa chỉ giao hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDiaChi", e.getMessage());
        }
        return "redirect:/khach-hang/thong-tin?tab=so-dia-chi";
    }

    /**
     * API lấy chi tiết địa chỉ dạng JSON để điền tự động vào Modal Sửa địa chỉ (US13).
     */
    @GetMapping("/dia-chi/api/{id}")
    @ResponseBody
    public ResponseEntity<?> layChiTietDiaChiApi(@PathVariable("id") Long id, HttpServletRequest request) {
        String token = layTokenTuCookie(request);
        if (token == null) {
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }
        try {
            ThongTinKhachHangResponse thongTin = khachHangService.layThongTinTuToken(token);
            DiaChi diaChi = diaChiService.layTheoIdVaKhachHang(id, thongTin.getId());
            return ResponseEntity.ok(diaChi);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Đăng xuất tài khoản khách hàng.
     */
    @GetMapping("/dang-xuat")
    public String dangXuat(HttpServletResponse response, HttpSession session) {
        Cookie cookie = new Cookie(COOKIE_TOKEN, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        session.invalidate();
        return "redirect:/khach-hang/dang-nhap?dangXuat=true";
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
