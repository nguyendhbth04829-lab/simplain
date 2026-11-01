package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.TaoMaGiamGiaRequest;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.entity.MaGiamGia;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.service.MaGiamGiaService;
import vn.fpoly.simplain.service.NhanVienService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller Quản lý & Tạo chương trình Mã giảm giá (Voucher) dành cho Admin (US26 - Mod 6 - Khoa).
 * - Tiêu chí 1: Tạo Voucher (% hoặc Tiền mặt).
 * - Tiêu chí 2: Đặt giới hạn ngân sách, lượt dùng tối đa, đơn tối thiểu.
 */
@Slf4j
@Controller
@RequestMapping("/admin/voucher")
@RequiredArgsConstructor
public class QuanLyMaGiamGiaController {

    public static final String COOKIE_ADMIN_TOKEN = "jwt_admin_token";

    private final MaGiamGiaService maGiamGiaService;
    private final NhanVienRepository nhanVienRepository;
    private final NhanVienService nhanVienService;

    /**
     * Màn hình danh sách tất cả chương trình Mã giảm giá (US26).
     */
    @GetMapping({"", "/hien-thi"})
    public String hienThiDanhSach(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "loaiGiamGia", defaultValue = "TAT_CA") String loaiGiamGia,
            @RequestParam(name = "trangThai", defaultValue = "TAT_CA") String trangThai,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        NhanVien actor = layNhanVienHienTai(request, session);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        Page<MaGiamGia> trangVoucher = maGiamGiaService.layDanhSachPhanTrang(tuKhoa, loaiGiamGia, trangThai, pageable);
        Map<String, Long> thongKe = maGiamGiaService.thongKeSoLuongTheoTrangThai();

        model.addAttribute("trangVoucher", trangVoucher);
        model.addAttribute("thongKe", thongKe);
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("loaiGiamGiaHienTai", loaiGiamGia);
        model.addAttribute("trangThaiHienTai", trangThai);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("nhanVienDangNhap", actor);

        return "admin/voucher/hien-thi";
    }

    /**
     * Màn hình Form tạo mới chương trình Mã giảm giá (US26 - Tiêu chí 1 & 2).
     */
    @GetMapping("/tao-moi")
    public String hienThiFormTaoMoi(
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        NhanVien actor = layNhanVienHienTai(request, session);

        if (!model.containsAttribute("taoRequest")) {
            // Giá trị mặc định thông minh: Bắt đầu từ ngày hôm nay, kết thúc sau 30 ngày
            LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
            LocalDateTime end = now.plusDays(30).withHour(23).withMinute(59);

            TaoMaGiamGiaRequest defaultReq = TaoMaGiamGiaRequest.builder()
                    .loaiGiamGia("PERCENT")
                    .giaTriGiam(BigDecimal.valueOf(10))
                    .giamToiDa(BigDecimal.valueOf(50000))
                    .giaTriDonToiThieu(BigDecimal.ZERO)
                    .tongSoLuong(500)
                    .gioiHanMoiUser(1)
                    .nganSach(BigDecimal.valueOf(10000000))
                    .ngayBatDau(now)
                    .ngayKetThuc(end)
                    .kichHoat(true)
                    .build();

            // Tự động sinh mã gợi ý ban đầu
            defaultReq.setMaCode(maGiamGiaService.sinhMaVoucherTuDong("PERCENT", BigDecimal.valueOf(10)));
            model.addAttribute("taoRequest", defaultReq);
        }

        model.addAttribute("nhanVienDangNhap", actor);
        model.addAttribute("isEditMode", false);
        return "admin/voucher/tao-moi";
    }

    /**
     * Xử lý lưu Tạo mới chương trình Mã giảm giá với Form Validation chuyên nghiệp (US26).
     */
    @PostMapping("/tao-moi")
    public String xuLyTaoMoi(
            @Valid @ModelAttribute("taoRequest") TaoMaGiamGiaRequest req,
            BindingResult bindingResult,
            HttpServletRequest request,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        NhanVien actor = layNhanVienHienTai(request, session);

        // Validation tùy chỉnh cho loại hình giảm giá
        if ("PERCENT".equalsIgnoreCase(req.getLoaiGiamGia())) {
            if (req.getGiaTriGiam() != null && req.getGiaTriGiam().compareTo(BigDecimal.valueOf(100)) > 0) {
                bindingResult.rejectValue("giaTriGiam", "error.giaTriGiam",
                        "Tỷ lệ giảm giá theo phần trăm không được vượt quá 100%!");
            }
            if (req.getGiamToiDa() == null || req.getGiamToiDa().compareTo(BigDecimal.ZERO) <= 0) {
                bindingResult.rejectValue("giamToiDa", "error.giamToiDa",
                        "Vui lòng nhập mức Giảm tối đa (VNĐ) khi chọn hình thức giảm %!");
            }
        } else if ("FIXED_AMOUNT".equalsIgnoreCase(req.getLoaiGiamGia())) {
            if (req.getGiaTriGiam() != null && req.getGiaTriGiam().compareTo(BigDecimal.valueOf(1000)) < 0) {
                bindingResult.rejectValue("giaTriGiam", "error.giaTriGiam",
                        "Số tiền giảm trực tiếp tối thiểu từ 1,000 đ!");
            }
            if (req.getGiaTriDonToiThieu() != null && req.getGiaTriGiam() != null
                    && req.getGiaTriDonToiThieu().compareTo(BigDecimal.ZERO) > 0
                    && req.getGiaTriGiam().compareTo(req.getGiaTriDonToiThieu()) > 0) {
                bindingResult.rejectValue("giaTriGiam", "error.giaTriGiam",
                        "Số tiền giảm không được vượt quá giá trị đơn tối thiểu!");
            }
        }

        // Validation thời gian
        if (req.getNgayBatDau() != null && req.getNgayKetThuc() != null) {
            if (!req.getNgayKetThuc().isAfter(req.getNgayBatDau())) {
                bindingResult.rejectValue("ngayKetThuc", "error.ngayKetThuc",
                        "Thời điểm kết thúc phải diễn ra sau thời điểm bắt đầu!");
            }
        }

        // Kiểm tra trùng mã code trước khi lưu
        if (req.getMaCode() != null && !req.getMaCode().isBlank()) {
            if (maGiamGiaService.kiemTraMaCodeTonTai(req.getMaCode(), null)) {
                bindingResult.rejectValue("maCode", "error.maCode",
                        "Mã voucher [" + req.getMaCode().toUpperCase().trim() + "] đã tồn tại trong hệ thống. Vui lòng chọn mã khác!");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("isEditMode", false);
            String thongBao = (bindingResult.hasFieldErrors("maCode") && bindingResult.getFieldError("maCode") != null)
                    ? bindingResult.getFieldError("maCode").getDefaultMessage()
                    : "Dữ liệu tạo voucher chưa hợp lệ, vui lòng kiểm tra các trường báo đỏ!";
            model.addAttribute("thongBaoLoi", thongBao);
            return "admin/voucher/tao-moi";
        }

        try {
            MaGiamGia saved = maGiamGiaService.taoMaGiamGia(req);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã tạo thành công chương trình Mã giảm giá [" + saved.getMaCode() + "]!");
            return "redirect:/admin/voucher/hien-thi";

        } catch (IllegalArgumentException e) {
            log.warn("Lỗi nghiệp vụ khi tạo mã giảm giá: {}", e.getMessage());
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("isEditMode", false);
            model.addAttribute("thongBaoLoi", e.getMessage());
            return "admin/voucher/tao-moi";
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi tạo mã giảm giá: {}", e.getMessage(), e);
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("isEditMode", false);
            model.addAttribute("thongBaoLoi", "Lỗi xử lý hệ thống: " + e.getMessage());
            return "admin/voucher/tao-moi";
        }
    }

    /**
     * Màn hình xem chi tiết & chỉnh sửa voucher.
     */
    @GetMapping("/chinh-sua/{id}")
    public String hienThiFormChinhSua(
            @PathVariable("id") Long id,
            HttpServletRequest request,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            NhanVien actor = layNhanVienHienTai(request, session);
            MaGiamGia voucher = maGiamGiaService.layChiTiet(id);

            TaoMaGiamGiaRequest editReq = TaoMaGiamGiaRequest.builder()
                    .maCode(voucher.getMaCode())
                    .tenVoucher(voucher.getTenVoucher())
                    .loaiGiamGia(voucher.getLoaiGiamGia())
                    .giaTriGiam(voucher.getGiaTriGiam())
                    .giamToiDa(voucher.getGiamToiDa())
                    .giaTriDonToiThieu(voucher.getGiaTriDonToiThieu())
                    .tongSoLuong(voucher.getTongSoLuong())
                    .gioiHanMoiUser(voucher.getGioiHanMoiUser())
                    .nganSach(voucher.getNganSach())
                    .ngayBatDau(voucher.getNgayBatDau())
                    .ngayKetThuc(voucher.getNgayKetThuc())
                    .kichHoat(voucher.getKichHoat())
                    .moTa(voucher.getMoTa())
                    .build();

            model.addAttribute("taoRequest", editReq);
            model.addAttribute("voucherId", id);
            model.addAttribute("voucherGoc", voucher);
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("isEditMode", true);

            return "admin/voucher/tao-moi";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/voucher/hien-thi";
        }
    }

    /**
     * Xử lý lưu Cập nhật voucher.
     */
    @PostMapping("/chinh-sua/{id}")
    public String xuLyCapNhat(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("taoRequest") TaoMaGiamGiaRequest req,
            BindingResult bindingResult,
            HttpServletRequest request,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        NhanVien actor = layNhanVienHienTai(request, session);

        // Kiểm tra trùng mã code khi cập nhật
        if (req.getMaCode() != null && !req.getMaCode().isBlank()) {
            if (maGiamGiaService.kiemTraMaCodeTonTai(req.getMaCode(), id)) {
                bindingResult.rejectValue("maCode", "error.maCode",
                        "Mã voucher [" + req.getMaCode().toUpperCase().trim() + "] đã tồn tại trong hệ thống. Vui lòng chọn mã khác!");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("voucherId", id);
            model.addAttribute("isEditMode", true);
            String thongBao = (bindingResult.hasFieldErrors("maCode") && bindingResult.getFieldError("maCode") != null)
                    ? bindingResult.getFieldError("maCode").getDefaultMessage()
                    : "Dữ liệu cập nhật chưa hợp lệ, vui lòng kiểm tra các trường báo đỏ!";
            model.addAttribute("thongBaoLoi", thongBao);
            return "admin/voucher/tao-moi";
        }

        try {
            MaGiamGia updated = maGiamGiaService.capNhatMaGiamGia(id, req);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã cập nhật mã giảm giá [" + updated.getMaCode() + "] thành công!");
            return "redirect:/admin/voucher/hien-thi";

        } catch (IllegalArgumentException e) {
            log.warn("Lỗi nghiệp vụ khi cập nhật voucher ID {}: {}", id, e.getMessage());
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("voucherId", id);
            model.addAttribute("isEditMode", true);
            model.addAttribute("thongBaoLoi", e.getMessage());
            return "admin/voucher/tao-moi";
        }
    }

    /**
     * Bật / Tắt trạng thái kích hoạt voucher.
     */
    @PostMapping("/kich-hoat/{id}")
    public String toggleKichHoat(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {
        try {
            boolean active = maGiamGiaService.thayDoiTrangThaiKichHoat(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã " + (active ? "kích hoạt mở lại" : "tạm ngưng áp dụng") + " mã giảm giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/voucher/hien-thi";
    }

    /**
     * Xóa chương trình mã giảm giá.
     */
    @PostMapping("/xoa/{id}")
    public String xoaVoucher(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {
        try {
            maGiamGiaService.xoaMaGiamGia(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa mã giảm giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/voucher/hien-thi";
    }

    /**
     * REST API sinh mã Voucher ngẫu nhiên tự động (US26).
     */
    @GetMapping("/api/sinh-ma")
    @ResponseBody
    public ResponseEntity<?> apiSinhMaTuDong(
            @RequestParam(value = "loaiGiamGia", defaultValue = "PERCENT") String loaiGiamGia,
            @RequestParam(value = "giaTriGiam", required = false) BigDecimal giaTriGiam) {
        try {
            String maMoi = maGiamGiaService.sinhMaVoucherTuDong(loaiGiamGia, giaTriGiam);
            return ResponseEntity.ok(Map.of("success", true, "maCode", maMoi));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * REST API kiểm tra mã code đã tồn tại chưa khi người dùng gõ vào ô input (Live Validation).
     */
    @GetMapping("/api/kiem-tra-ma")
    @ResponseBody
    public ResponseEntity<?> apiKiemTraMaCode(
            @RequestParam("maCode") String maCode,
            @RequestParam(value = "excludeId", required = false) Long excludeId) {
        boolean tonTai = maGiamGiaService.kiemTraMaCodeTonTai(maCode, excludeId);
        return ResponseEntity.ok(Map.of("exists", tonTai));
    }

    // =========================================================================
    // HELPER: LẤY NHÂN VIÊN ĐĂNG NHẬP
    // =========================================================================

    private NhanVien layNhanVienHienTai(HttpServletRequest request, HttpSession session) {
        if (session != null) {
            Object obj = session.getAttribute("nhanVienHienTai");
            if (obj instanceof XacThucNhanVienResponse xacThuc && xacThuc.getId() != null) {
                return nhanVienRepository.findById(xacThuc.getId()).orElse(null);
            }
            if (session.getAttribute("nhanVienDangNhap") instanceof NhanVien nv) {
                return nv;
            }
        }

        if (request != null && request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (COOKIE_ADMIN_TOKEN.equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                    try {
                        XacThucNhanVienResponse xacThuc = nhanVienService.layThongTinNhanVienTuToken(c.getValue().trim());
                        if (xacThuc != null && xacThuc.getId() != null) {
                            return nhanVienRepository.findById(xacThuc.getId()).orElse(null);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        return nhanVienRepository.findAll().stream().findFirst().orElse(null);
    }
}
