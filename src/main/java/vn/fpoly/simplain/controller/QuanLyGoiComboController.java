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
import vn.fpoly.simplain.dto.TaoGoiComboRequest;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.entity.GoiCombo;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.service.GoiComboService;
import vn.fpoly.simplain.service.NhanVienService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller Quản trị thiết lập Gói Combo kích cầu dành cho Admin (US28 - Mod 6 / Bán hàng - Khoa).
 * - Tiêu chí 1: Tạo gói giá fix (VD: Tự chọn 3 áo giá 299k).
 * - Tiêu chí 2: Map san_pham_trong_combo các áo được áp dụng.
 */
@Slf4j
@Controller
@RequestMapping("/admin/combo")
@RequiredArgsConstructor
public class QuanLyGoiComboController {

    public static final String COOKIE_ADMIN_TOKEN = "jwt_admin_token";

    private final GoiComboService goiComboService;
    private final NhanVienRepository nhanVienRepository;
    private final NhanVienService nhanVienService;

    /**
     * Màn hình danh sách Gói Combo kích cầu (US28).
     */
    @GetMapping({"", "/hien-thi"})
    public String hienThiDanhSach(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "trangThai", defaultValue = "TAT_CA") String trangThai,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        NhanVien actor = layNhanVienHienTai(request, session);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        Boolean kichHoat = null;
        if ("DANG_HOAT_DONG".equalsIgnoreCase(trangThai)) {
            kichHoat = true;
        } else if ("TAM_DUNG".equalsIgnoreCase(trangThai)) {
            kichHoat = false;
        }

        Page<GoiCombo> trangCombo = goiComboService.layDanhSachGoiCombo(tuKhoa, kichHoat, pageable);

        model.addAttribute("trangCombo", trangCombo);
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("trangThaiHienTai", trangThai);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("nhanVienDangNhap", actor);

        return "admin/combo/hien-thi";
    }

    /**
     * Màn hình Form tạo mới Gói Combo (US28 - Tiêu chí 1 & 2).
     */
    @GetMapping("/tao-moi")
    public String hienThiFormTaoMoi(Model model, HttpServletRequest request, HttpSession session) {
        NhanVien actor = layNhanVienHienTai(request, session);

        if (!model.containsAttribute("req")) {
            TaoGoiComboRequest req = TaoGoiComboRequest.builder()
                    .soMonQuyDinh(3)
                    .giaCombo(new BigDecimal("299000"))
                    .kichHoat(true)
                    .build();
            req.setMaCombo(goiComboService.sinhMaComboTuDong(3, new BigDecimal("299000")));
            model.addAttribute("req", req);
        }

        List<SanPham> dsSanPham = goiComboService.layTatCaSanPhamKhaDungChoCombo();
        model.addAttribute("danhSachTatCaSanPham", dsSanPham);
        model.addAttribute("isEditMode", false);
        model.addAttribute("nhanVienDangNhap", actor);

        return "admin/combo/tao-moi";
    }

    /**
     * Xử lý Submit Tạo mới Gói Combo (US28).
     */
    @PostMapping("/tao-moi")
    public String xuLyTaoMoi(
            @Valid @ModelAttribute("req") TaoGoiComboRequest req,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpSession session) {

        NhanVien actor = layNhanVienHienTai(request, session);

        // 1. Kiểm tra lỗi Annotation Validate
        if (bindingResult.hasErrors()) {
            model.addAttribute("danhSachTatCaSanPham", goiComboService.layTatCaSanPhamKhaDungChoCombo());
            model.addAttribute("isEditMode", false);
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("thongBaoLoi", "Dữ liệu nhập vào chưa hợp lệ. Vui lòng kiểm tra lại các trường báo đỏ!");
            return "admin/combo/tao-moi";
        }

        // 2. Gọi Service xử lý nghiệp vụ & bắt lỗi nghiệp vụ
        try {
            GoiCombo combo = goiComboService.taoMoi(req);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Thiết lập thành công gói combo [" + combo.getTenCombo() + "] (" + combo.getMaCombo() + ") với giá fix "
                            + String.format("%,d", combo.getGiaCombo().longValue()) + " đ cho " + combo.getSoMonQuyDinh()
                            + " món quy định!");
            return "redirect:/admin/combo/hien-thi";
        } catch (IllegalArgumentException e) {
            log.warn("Lỗi nghiệp vụ khi tạo combo: {}", e.getMessage());
            model.addAttribute("danhSachTatCaSanPham", goiComboService.layTatCaSanPhamKhaDungChoCombo());
            model.addAttribute("isEditMode", false);
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("thongBaoLoi", e.getMessage());
            return "admin/combo/tao-moi";
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi tạo combo: ", e);
            model.addAttribute("danhSachTatCaSanPham", goiComboService.layTatCaSanPhamKhaDungChoCombo());
            model.addAttribute("isEditMode", false);
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("thongBaoLoi", "Có lỗi hệ thống xảy ra khi lưu gói combo. Vui lòng thử lại!");
            return "admin/combo/tao-moi";
        }
    }

    /**
     * Màn hình Form Chỉnh sửa Gói Combo (US28).
     */
    @GetMapping("/chinh-sua/{id}")
    public String hienThiFormChinhSua(
            @PathVariable("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpSession session) {

        NhanVien actor = layNhanVienHienTai(request, session);

        try {
            GoiCombo combo = goiComboService.layTheoId(id);

            if (!model.containsAttribute("req")) {
                TaoGoiComboRequest req = TaoGoiComboRequest.builder()
                        .id(combo.getId())
                        .maCombo(combo.getMaCombo())
                        .tenCombo(combo.getTenCombo())
                        .duongDanSlug(combo.getDuongDanSlug())
                        .giaCombo(combo.getGiaCombo())
                        .soMonQuyDinh(combo.getSoMonQuyDinh())
                        .moTa(combo.getMoTa())
                        .kichHoat(combo.getKichHoat())
                        .danhSachSanPhamIds(combo.getDanhSachMaSanPham())
                        .build();
                model.addAttribute("req", req);
            }

            model.addAttribute("comboGoc", combo);
            model.addAttribute("danhSachTatCaSanPham", goiComboService.layTatCaSanPhamKhaDungChoCombo());
            model.addAttribute("isEditMode", true);
            model.addAttribute("nhanVienDangNhap", actor);

            return "admin/combo/tao-moi";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/combo/hien-thi";
        }
    }

    /**
     * Xử lý Submit Cập nhật Gói Combo (US28).
     */
    @PostMapping("/chinh-sua/{id}")
    public String xuLyCapNhat(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("req") TaoGoiComboRequest req,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpSession session) {

        NhanVien actor = layNhanVienHienTai(request, session);
        req.setId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("danhSachTatCaSanPham", goiComboService.layTatCaSanPhamKhaDungChoCombo());
            model.addAttribute("isEditMode", true);
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("thongBaoLoi", "Dữ liệu cập nhật chưa hợp lệ. Vui lòng kiểm tra lại các trường báo đỏ!");
            return "admin/combo/tao-moi";
        }

        try {
            GoiCombo updated = goiComboService.capNhat(id, req);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật thành công gói combo [" + updated.getTenCombo() + "] (" + updated.getMaCombo() + ")!");
            return "redirect:/admin/combo/hien-thi";
        } catch (IllegalArgumentException e) {
            log.warn("Lỗi nghiệp vụ khi cập nhật combo ID {}: {}", id, e.getMessage());
            model.addAttribute("danhSachTatCaSanPham", goiComboService.layTatCaSanPhamKhaDungChoCombo());
            model.addAttribute("isEditMode", true);
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("thongBaoLoi", e.getMessage());
            return "admin/combo/tao-moi";
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi cập nhật combo: ", e);
            model.addAttribute("danhSachTatCaSanPham", goiComboService.layTatCaSanPhamKhaDungChoCombo());
            model.addAttribute("isEditMode", true);
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("thongBaoLoi", "Có lỗi hệ thống xảy ra khi cập nhật gói combo. Vui lòng thử lại!");
            return "admin/combo/tao-moi";
        }
    }

    /**
     * Bật / Tắt trạng thái kích hoạt Gói Combo (US28).
     */
    @PostMapping("/toggle/{id}")
    public String toggleKichHoat(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean trangThaiMoi = goiComboService.toggleKichHoat(id);
            GoiCombo combo = goiComboService.layTheoId(id);
            String trangThaiText = trangThaiMoi ? "KÍCH HOẠT áp dụng" : "TẠM DỪNG";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển gói combo [" + combo.getTenCombo() + "] sang trạng thái: " + trangThaiText + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/combo/hien-thi";
    }

    /**
     * Xóa Gói Combo (US28).
     */
    @PostMapping("/xoa/{id}")
    public String xoaCombo(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            GoiCombo combo = goiComboService.layTheoId(id);
            goiComboService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã xóa thành công gói combo [" + combo.getTenCombo() + "] khỏi hệ thống!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Không thể xóa gói combo: " + e.getMessage());
        }
        return "redirect:/admin/combo/hien-thi";
    }

    /**
     * API AJAX sinh mã combo tự động theo số món và giá.
     */
    @GetMapping("/api/sinh-ma")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sinhMaCombo(
            @RequestParam(name = "soMon", required = false) Integer soMon,
            @RequestParam(name = "gia", required = false) BigDecimal gia) {
        String ma = goiComboService.sinhMaComboTuDong(soMon, gia);
        return ResponseEntity.ok(Collections.singletonMap("maCombo", ma));
    }

    /**
     * API AJAX sinh slug tự động từ tên gói combo.
     */
    @GetMapping("/api/sinh-slug")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sinhSlug(@RequestParam(name = "ten", defaultValue = "") String ten) {
        String slug = goiComboService.sinhSlugTuTen(ten);
        return ResponseEntity.ok(Collections.singletonMap("slug", slug));
    }

    /**
     * API AJAX kiểm tra mã combo đã tồn tại chưa (live validation).
     */
    @GetMapping("/api/kiem-tra-ma")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> kiemTraMaCombo(
            @RequestParam("maCombo") String maCombo,
            @RequestParam(name = "excludeId", required = false) Long excludeId) {
        boolean tonTai = goiComboService.kiemTraMaComboTonTai(maCombo, excludeId);
        return ResponseEntity.ok(Collections.singletonMap("tonTai", tonTai));
    }

    // =========================================================================
    // PRIVATE HELPER METHODS
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
