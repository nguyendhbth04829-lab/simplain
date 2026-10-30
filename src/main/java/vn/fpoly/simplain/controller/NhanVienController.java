package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import vn.fpoly.simplain.dto.DangNhapNhanVienRequest;
import vn.fpoly.simplain.dto.NhanVienRequest;
import vn.fpoly.simplain.dto.TaoDonHangNhanhRequest;
import vn.fpoly.simplain.dto.ThongKeDashboardResponse;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.repository.BoSuuTapRepository;
import vn.fpoly.simplain.repository.DanhMucRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.TrangThaiDonHangRepository;
import vn.fpoly.simplain.repository.VaiTroRepository;
import vn.fpoly.simplain.repository.YeuCauDoiTraRepository;
import vn.fpoly.simplain.service.DashboardService;
import vn.fpoly.simplain.service.NhanVienService;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Controller MVC quản lý xác thực CMS (US02), Quản lý Nhân viên CRUD (US04) và Biểu đồ Dashboard tổng quan (US05).
 */
@Controller
@RequestMapping("/admin")
public class NhanVienController {

    public static final String COOKIE_ADMIN_TOKEN = "jwt_admin_token";

    private final NhanVienService nhanVienService;
    private final DashboardService dashboardService;
    private final DanhMucRepository danhMucRepository;
    private final BoSuuTapRepository boSuuTapRepository;
    private final KhachHangRepository khachHangRepository;
    private final VaiTroRepository vaiTroRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final YeuCauDoiTraRepository yeuCauDoiTraRepository;

    @Autowired
    public NhanVienController(NhanVienService nhanVienService,
                              DashboardService dashboardService,
                              DanhMucRepository danhMucRepository,
                              BoSuuTapRepository boSuuTapRepository,
                              KhachHangRepository khachHangRepository,
                              VaiTroRepository vaiTroRepository,
                              TrangThaiDonHangRepository trangThaiDonHangRepository,
                              @Autowired(required = false) YeuCauDoiTraRepository yeuCauDoiTraRepository) {
        this.nhanVienService = nhanVienService;
        this.dashboardService = dashboardService;
        this.danhMucRepository = danhMucRepository;
        this.boSuuTapRepository = boSuuTapRepository;
        this.khachHangRepository = khachHangRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.trangThaiDonHangRepository = trangThaiDonHangRepository;
        this.yeuCauDoiTraRepository = yeuCauDoiTraRepository;
    }

    public NhanVienController(NhanVienService nhanVienService,
                              DashboardService dashboardService,
                              DanhMucRepository danhMucRepository,
                              BoSuuTapRepository boSuuTapRepository,
                              KhachHangRepository khachHangRepository,
                              VaiTroRepository vaiTroRepository,
                              TrangThaiDonHangRepository trangThaiDonHangRepository) {
        this(nhanVienService, dashboardService, danhMucRepository, boSuuTapRepository, khachHangRepository, vaiTroRepository, trangThaiDonHangRepository, null);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    // =========================================================================
    // PHẦN 1: GIAO DIỆN XÁC THỰC VÀ DASHBOARD TỔNG QUAN CMS (US02 & US05)
    // =========================================================================

    /**
     * Màn hình đăng nhập hệ thống quản trị CMS dành cho nhân viên.
     */
    @GetMapping("/dang-nhap")
    public String hienThiDangNhapCms(Model model, HttpServletRequest request) {
        String token = layTokenTuCookie(request);
        if (token != null) {
            try {
                nhanVienService.layThongTinNhanVienTuToken(token);
                return "redirect:/admin/dashboard";
            } catch (Exception ignored) {
            }
        }

        if (!model.containsAttribute("dangNhapRequest")) {
            model.addAttribute("dangNhapRequest", new DangNhapNhanVienRequest());
        }
        return "admin/dang-nhap";
    }

    /**
     * Xử lý đăng nhập CMS: Kiểm tra phân quyền và từ chối nếu tài khoản bị khóa.
     */
    @PostMapping("/dang-nhap")
    public String xuLyDangNhapCms(@Valid @ModelAttribute("dangNhapRequest") DangNhapNhanVienRequest dangNhapRequest,
                                  BindingResult result,
                                  HttpServletResponse response,
                                  HttpSession session,
                                  Model model) {
        if (result.hasErrors()) {
            return "admin/dang-nhap";
        }

        try {
            XacThucNhanVienResponse xacThuc = nhanVienService.dangNhap(dangNhapRequest);

            // Lưu JWT token quản trị vào Cookie
            Cookie cookie = new Cookie(COOKIE_ADMIN_TOKEN, xacThuc.getToken());
            cookie.setPath("/");
            cookie.setMaxAge(86400); // 1 ngày
            cookie.setHttpOnly(false);
            response.addCookie(cookie);

            session.setAttribute("nhanVienHienTai", xacThuc);
            return "redirect:/admin/dashboard";
        } catch (IllegalStateException e) {
            // Trường hợp tài khoản nhân viên bị khóa hoặc vai trò bị vô hiệu hóa
            model.addAttribute("loiKhoaTaiKhoan", e.getMessage());
            model.addAttribute("thongBaoLoi", e.getMessage());
            return "admin/dang-nhap";
        } catch (Exception e) {
            // Sai tên đăng nhập hoặc mật khẩu
            model.addAttribute("loiDangNhap", e.getMessage());
            model.addAttribute("thongBaoLoi", e.getMessage());
            return "admin/dang-nhap";
        }
    }

    /**
     * Điều hướng trang gốc /admin về Dashboard CMS.
     */
    @GetMapping({"", "/"})
    public String dieuHuongTrangChuAdmin() {
        return "redirect:/admin/dashboard";
    }

    /**
     * Màn hình Biểu đồ Dashboard tổng quan SIMPLAIN STORE (US02 & US05).
     * Thống kê tổng số lượng người dùng, đơn hàng hôm nay, doanh thu trong ngày và biểu đồ trực quan.
     */
    @GetMapping("/dashboard")
    public String hienThiDashboard(
            @RequestParam(name = "ngayThongKe", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayThongKe,
            @RequestParam(name = "soNgayBieuDo", required = false, defaultValue = "7") Integer soNgayBieuDo,
            @RequestParam(name = "tuChoiQuyen", required = false) String tuChoiQuyen,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập quyền quản trị CMS để tiếp tục");
            return "redirect:/admin/dang-nhap";
        }

        XacThucNhanVienResponse nhanVien;
        try {
            nhanVien = nhanVienService.layThongTinNhanVienTuToken(token);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên làm việc đã hết hạn hoặc tài khoản bị khóa, vui lòng đăng nhập lại");
            return "redirect:/admin/dang-nhap";
        }

        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("jwtToken", token);

        if (tuChoiQuyen != null && !tuChoiQuyen.trim().isEmpty()) {
            model.addAttribute("thongBaoTuChoiQuyen", tuChoiQuyen.trim());
        }

        // Đếm số lượng RMA chờ xử lý cho CSKH / Admin
        long soRmaChoXuLy = 0;
        if (yeuCauDoiTraRepository != null) {
            try {
                soRmaChoXuLy = yeuCauDoiTraRepository.countByTrangThai("CHO_XU_LY");
            } catch (Exception ignored) {
            }
        }
        model.addAttribute("soRmaChoXuLy", soRmaChoXuLy);

        napDuLieuThongKeVaoModel(ngayThongKe, soNgayBieuDo, model);
        return "admin/dashboard";
    }

    /**
     * Xử lý tạo nhanh đơn hàng trên Dashboard để kiểm thử realtime số đơn hôm nay, doanh thu trong ngày & biểu đồ (US05).
     */
    @PostMapping("/dashboard/tao-don-nhanh")
    public String xuLyTaoDonHangNhanh(
            @Valid @ModelAttribute("taoDonHangNhanhRequest") TaoDonHangNhanhRequest taoDonHangNhanhRequest,
            BindingResult result,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        String token = layTokenTuCookie(request);
        if (token == null) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Vui lòng đăng nhập quyền quản trị CMS để tiếp tục");
            return "redirect:/admin/dang-nhap";
        }

        XacThucNhanVienResponse nhanVien;
        try {
            nhanVien = nhanVienService.layThongTinNhanVienTuToken(token);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loiDangNhap", "Phiên làm việc đã hết hạn hoặc tài khoản bị khóa, vui lòng đăng nhập lại");
            return "redirect:/admin/dang-nhap";
        }

        if (result.hasErrors()) {
            model.addAttribute("nhanVien", nhanVien);
            model.addAttribute("jwtToken", token);
            model.addAttribute("moModalTaoDon", true);
            model.addAttribute("thongBaoLoiDashboard", "Dữ liệu đơn hàng chưa hợp lệ, vui lòng kiểm tra lại các trường báo đỏ!");
            napDuLieuThongKeVaoModel(LocalDate.now(), 7, model);
            return "admin/dashboard";
        }

        try {
            DonHang donHangMoi = dashboardService.taoDonHangNhanh(taoDonHangNhanhRequest);
            redirectAttributes.addFlashAttribute("thongBaoThanhCongDashboard",
                    "Đã tạo đơn hàng [" + donHangMoi.getMaDonHang() + "] thành công! Doanh thu và biểu đồ hôm nay đã được cập nhật.");
            return "redirect:/admin/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("nhanVien", nhanVien);
            model.addAttribute("jwtToken", token);
            model.addAttribute("moModalTaoDon", true);
            model.addAttribute("thongBaoLoiDashboard", e.getMessage());
            napDuLieuThongKeVaoModel(LocalDate.now(), 7, model);
            return "admin/dashboard";
        }
    }

    private void napDuLieuThongKeVaoModel(LocalDate ngayThongKe, Integer soNgayBieuDo, Model model) {
        ThongKeDashboardResponse thongKe;
        try {
            thongKe = dashboardService.layThongKeDashboard(ngayThongKe, soNgayBieuDo);
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoiBoLoc", e.getMessage());
            thongKe = dashboardService.layThongKeDashboard(LocalDate.now(), 7);
        }

        model.addAttribute("thongKe", thongKe);

        // Các biến trực tiếp phục vụ hiển thị & kiểm thử tiêu chí US05
        model.addAttribute("tongSoLuongNguoiDung", thongKe.getTongSoLuongNguoiDung());
        model.addAttribute("soDonHangHomNay", thongKe.getSoDonHangHomNay());
        model.addAttribute("doanhThuTrongNgay", thongKe.getDoanhThuTrongNgay());

        model.addAttribute("tongDanhMuc", thongKe.getTongDanhMuc());
        model.addAttribute("tongBoSuuTap", thongKe.getTongBoSuuTap());
        model.addAttribute("tongKhachHang", thongKe.getTongKhachHang());
        model.addAttribute("tongVaiTro", thongKe.getTongVaiTro());
        model.addAttribute("tongNhanVien", thongKe.getTongNhanVien());
        model.addAttribute("danhSachTrangThaiDonHang", trangThaiDonHangRepository.findAll());

        if (!model.containsAttribute("taoDonHangNhanhRequest")) {
            model.addAttribute("taoDonHangNhanhRequest", TaoDonHangNhanhRequest.builder()
                    .tongTienHang(new BigDecimal("499000"))
                    .tienGiamGia(BigDecimal.ZERO)
                    .phiVanChuyen(new BigDecimal("25000"))
                    .maTrangThai("HOAN_THANH")
                    .build());
        }
    }

    /**
     * Đăng xuất khỏi hệ thống quản trị CMS.
     */
    @GetMapping("/dang-xuat")
    public String dangXuatCms(HttpServletResponse response, HttpSession session) {
        Cookie cookie = new Cookie(COOKIE_ADMIN_TOKEN, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        session.invalidate();
        return "redirect:/admin/dang-nhap?dangXuat=true";
    }

    // =========================================================================
    // PHẦN 2: GIAO DIỆN WEB QUẢN LÝ NHÂN SỰ (US04 - THYMELEAF)
    // =========================================================================

    /**
     * Màn hình danh sách nhân viên có phân trang, tìm kiếm và lọc theo vai trò, trạng thái.
     */
    @GetMapping({"/nhan-vien", "/nhan-vien/", "/nhan-vien/hien-thi"})
    public String hienThiDanhSachNhanVien(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "vaiTroId", required = false) Long vaiTroId,
            @RequestParam(name = "trangThai", required = false) Integer trangThai,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            Model model,
            HttpServletRequest request,
            HttpSession session) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<NhanVien> pageData = nhanVienService.layDanhSach(tuKhoa, vaiTroId, trangThai, pageable);

        model.addAttribute("pageData", pageData);
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("vaiTroId", vaiTroId);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("danhSachVaiTro", vaiTroRepository.findAll());

        if (!model.containsAttribute("nhanVienRequest")) {
            model.addAttribute("nhanVienRequest", new NhanVienRequest());
        }

        // Lấy thông tin user đang đăng nhập để hiển thị và kiểm tra quyền
        model.addAttribute("tenDangNhapHienTai", layTenDangNhapHienTai(request, session));

        return "admin/nhan-vien/hien-thi";
    }

    /**
     * Màn hình xem chi tiết và form chỉnh sửa thông tin nhân viên, gán lại vai trò.
     */
    @GetMapping("/nhan-vien/view/{id}")
    public String hienThiChiTietNhanVien(@PathVariable("id") Long id,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        try {
            NhanVien nv = nhanVienService.layTheoId(id);

            if (!model.containsAttribute("nhanVienRequest")) {
                NhanVienRequest request = NhanVienRequest.builder()
                        .id(nv.getId())
                        .maNhanVien(nv.getMaNhanVien())
                        .hoTen(nv.getHoTen())
                        .tenDangNhap(nv.getTenDangNhap())
                        .soDienThoai(nv.getSoDienThoai())
                        .email(nv.getEmail())
                        .maVaiTroId(nv.getVaiTro() != null ? nv.getVaiTro().getId() : null)
                        .trangThai(nv.getTrangThai())
                        .build();
                model.addAttribute("nhanVienRequest", request);
            }

            model.addAttribute("nhanVien", nv);
            model.addAttribute("danhSachVaiTro", vaiTroRepository.findAll());
            return "admin/nhan-vien/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/nhan-vien/hien-thi";
        }
    }

    /**
     * Xử lý thêm mới nhân viên từ form giao diện web.
     */
    @PostMapping("/nhan-vien/them")
    public String xuLyThemNhanVien(@Valid @ModelAttribute("nhanVienRequest") NhanVienRequest request,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes,
                                   Model model,
                                   HttpServletRequest httpRequest,
                                   HttpSession session) {
        if (request.getMatKhau() == null || request.getMatKhau().trim().length() < 6) {
            result.rejectValue("matKhau", "error.matKhau", "Mật khẩu tạo mới cho nhân viên phải có ít nhất 6 ký tự");
        }

        if (result.hasErrors()) {
            String loiDauTien = result.getAllErrors().get(0).getDefaultMessage();
            model.addAttribute("thongBaoLoi", "Dữ liệu không hợp lệ: " + loiDauTien);
            model.addAttribute("moModalThem", true);
            return hienThiDanhSachNhanVien(null, null, null, 0, 5, model, httpRequest, session);
        }

        try {
            nhanVienService.them(request);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Thêm mới nhân viên [" + request.getHoTen() + "] thành công!");
            return "redirect:/admin/nhan-vien/hien-thi";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            redirectAttributes.addFlashAttribute("nhanVienRequest", request);
            redirectAttributes.addFlashAttribute("moModalThem", true);
            return "redirect:/admin/nhan-vien/hien-thi";
        }
    }

    /**
     * Xử lý cập nhật thông tin nhân viên, gán vai trò từ form giao diện web.
     */
    @PostMapping("/nhan-vien/sua/{id}")
    public String xuLySuaNhanVien(@PathVariable("id") Long id,
                                  @Valid @ModelAttribute("nhanVienRequest") NhanVienRequest request,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (result.hasErrors()) {
            try {
                NhanVien nv = nhanVienService.layTheoId(id);
                model.addAttribute("nhanVien", nv);
                model.addAttribute("danhSachVaiTro", vaiTroRepository.findAll());
            } catch (Exception ignored) {
            }
            String loiDauTien = result.getAllErrors().get(0).getDefaultMessage();
            model.addAttribute("thongBaoLoi", "Dữ liệu cập nhật chưa hợp lệ: " + loiDauTien);
            return "admin/nhan-vien/view";
        }

        try {
            nhanVienService.sua(id, request);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Cập nhật thông tin nhân viên thành công!");
            return "redirect:/admin/nhan-vien/hien-thi";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            redirectAttributes.addFlashAttribute("nhanVienRequest", request);
            return "redirect:/admin/nhan-vien/view/" + id;
        }
    }

    /**
     * Tiêu chí 3: Active / Deactive nhanh tài khoản nhân sự từ giao diện web.
     */
    @GetMapping({"/nhan-vien/doi-trang-thai/{id}", "/nhan-vien/doi-trang-thai"})
    public String xuLyDoiTrangThai(@PathVariable(value = "id", required = false) Long pathId,
                                   @RequestParam(value = "id", required = false) Long paramId,
                                   HttpServletRequest request,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Long targetId = pathId != null ? pathId : paramId;
        if (targetId == null) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Không tìm thấy ID nhân viên cần đổi trạng thái!");
            return "redirect:/admin/nhan-vien/hien-thi";
        }
        try {
            String currentUser = layTenDangNhapHienTai(request, session);
            NhanVien nv = nhanVienService.doiTrangThai(targetId, currentUser);
            String trangThaiText = (nv.getTrangThai() != null && nv.getTrangThai() == 1) ? "KÍCH HOẠT" : "KHÓA";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái tài khoản [" + nv.getTenDangNhap() + "] sang: " + trangThaiText + " thành công!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/nhan-vien/hien-thi";
    }

    /**
     * Xử lý xóa nhân viên có kiểm tra an toàn và toàn vẹn dữ liệu.
     */
    @GetMapping("/nhan-vien/xoa/{id}")
    public String xuLyXoaNhanVien(@PathVariable("id") Long id,
                                  HttpServletRequest request,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            String currentUser = layTenDangNhapHienTai(request, session);
            nhanVienService.xoa(id, currentUser);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Xóa nhân viên thành công!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Lỗi khi xóa: " + e.getMessage());
        }
        return "redirect:/admin/nhan-vien/hien-thi";
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private String layTokenTuCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (COOKIE_ADMIN_TOKEN.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String layTenDangNhapHienTai(HttpServletRequest request, HttpSession session) {
        // 1. Lấy từ Session
        Object sessObj = session.getAttribute("nhanVienHienTai");
        if (sessObj instanceof XacThucNhanVienResponse) {
            return ((XacThucNhanVienResponse) sessObj).getTenDangNhap();
        }
        // 2. Lấy từ Cookie JWT
        String token = layTokenTuCookie(request);
        if (token != null) {
            try {
                return nhanVienService.layThongTinNhanVienTuToken(token).getTenDangNhap();
            } catch (Exception ignored) {
            }
        }
        return "ADMIN_SYSTEM";
    }

    /**
     * Thao tác hàng loạt trên danh sách ID nhân viên được chọn (Kích hoạt hoặc Khóa).
     */
    @PostMapping("/nhan-vien/thao-tac-hang-loat")
    public String thaoTacHangLoat(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "hanhDong") String hanhDong,
                                  HttpServletRequest request,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng tích chọn ít nhất một nhân viên để thao tác hàng loạt!");
            return "redirect:/admin/nhan-vien/hien-thi";
        }

        String currentUser = layTenDangNhapHienTai(request, session);
        boolean batHoatDong = "KICH_HOAT".equalsIgnoreCase(hanhDong);
        int targetTrangThai = batHoatDong ? 1 : 0;
        int soLuong = 0;
        for (Long id : ids) {
            try {
                NhanVien nv = nhanVienService.layTheoId(id);
                if (nv != null && (nv.getTrangThai() == null || nv.getTrangThai() != targetTrangThai)) {
                    nhanVienService.doiTrangThai(id, currentUser);
                    soLuong++;
                }
            } catch (Exception ignored) {
            }
        }

        String trangThaiMoi = batHoatDong ? "KÍCH HOẠT" : "KHÓA TẠM NGƯNG";
        redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                "Đã chuyển trạng thái " + soLuong + " nhân viên đã chọn sang: " + trangThaiMoi + " thành công!");
        return "redirect:/admin/nhan-vien/hien-thi";
    }
}
