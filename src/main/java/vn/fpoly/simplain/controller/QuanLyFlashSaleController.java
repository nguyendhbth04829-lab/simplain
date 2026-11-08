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
import vn.fpoly.simplain.dto.ChiTietFlashSaleFormItem;
import vn.fpoly.simplain.dto.TaoFlashSaleRequest;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.entity.FlashSale;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.service.FlashSaleService;
import vn.fpoly.simplain.service.NhanVienService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller Quản trị Thiết lập khung giờ Flash Sale (US29 - Mod 6 / Bán hàng - Khoa).
 * - Tiêu chí 1: Đặt khung giờ Bắt đầu / Kết thúc.
 * - Tiêu chí 2: Gán giá sốc và giới hạn số lượng (so_luong_mo_ban) cho biến thể.
 */
@Slf4j
@Controller
@RequestMapping("/admin/flash-sale")
@RequiredArgsConstructor
public class QuanLyFlashSaleController {

    public static final String COOKIE_ADMIN_TOKEN = "jwt_admin_token";

    private final FlashSaleService flashSaleService;
    private final NhanVienRepository nhanVienRepository;
    private final NhanVienService nhanVienService;

    /**
     * Màn hình danh sách các đợt Flash Sale (US29).
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

        Page<FlashSale> trangFlashSale = flashSaleService.layDanhSachFlashSale(tuKhoa, kichHoat, pageable);

        model.addAttribute("trangFlashSale", trangFlashSale);
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("trangThaiHienTai", trangThai);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("menuActive", "flash-sale");
        model.addAttribute("nhanVienDangNhap", actor);

        return "admin/flash-sale/hien-thi";
    }

    /**
     * Màn hình Form tạo mới đợt Flash Sale (US29 - Tiêu chí 1 & 2).
     */
    @GetMapping("/tao-moi")
    public String hienThiFormTaoMoi(Model model, HttpServletRequest request, HttpSession session) {
        NhanVien actor = layNhanVienHienTai(request, session);

        if (!model.containsAttribute("req")) {
            LocalDateTime nowNextHour = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.HOURS);
            TaoFlashSaleRequest req = TaoFlashSaleRequest.builder()
                    .tieuDe("Deal Sốc Giờ Vàng " + nowNextHour.format(DateTimeFormatter.ofPattern("HH'h' dd/MM")))
                    .thoiGianBatDau(nowNextHour)
                    .thoiGianKetThuc(nowNextHour.plusHours(2))
                    .kichHoat(true)
                    .build();
            model.addAttribute("req", req);
        }

        model.addAttribute("danhSachBienTheKhaDung", flashSaleService.layDanhSachBienTheKhaDungChoFlashSale());
        model.addAttribute("isEditMode", false);
        model.addAttribute("menuActive", "flash-sale");
        model.addAttribute("nhanVienDangNhap", actor);

        return "admin/flash-sale/tao-moi";
    }

    /**
     * Xử lý Submit Tạo mới Flash Sale (US29).
     */
    @PostMapping("/tao-moi")
    public String xuLyTaoMoi(
            @Valid @ModelAttribute("req") TaoFlashSaleRequest req,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpSession session) {

        NhanVien actor = layNhanVienHienTai(request, session);

        if (bindingResult.hasErrors()) {
            model.addAttribute("danhSachBienTheKhaDung", flashSaleService.layDanhSachBienTheKhaDungChoFlashSale());
            model.addAttribute("isEditMode", false);
            model.addAttribute("menuActive", "flash-sale");
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("thongBaoLoi", "Dữ liệu thiết lập Flash Sale chưa hợp lệ. Vui lòng kiểm tra lại các trường báo đỏ!");
            return "admin/flash-sale/tao-moi";
        }

        try {
            FlashSale fs = flashSaleService.taoMoi(req);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Thiết lập thành công đợt Flash Sale [" + fs.getTieuDe() + "] với " + fs.getDanhSachChiTiet().size() + " biến thể giá sốc!");
            return "redirect:/admin/flash-sale/hien-thi";
        } catch (IllegalArgumentException e) {
            log.warn("Lỗi nghiệp vụ khi tạo Flash Sale: {}", e.getMessage());
            model.addAttribute("danhSachBienTheKhaDung", flashSaleService.layDanhSachBienTheKhaDungChoFlashSale());
            model.addAttribute("isEditMode", false);
            model.addAttribute("menuActive", "flash-sale");
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("thongBaoLoi", e.getMessage());
            return "admin/flash-sale/tao-moi";
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi tạo Flash Sale: ", e);
            model.addAttribute("danhSachBienTheKhaDung", flashSaleService.layDanhSachBienTheKhaDungChoFlashSale());
            model.addAttribute("isEditMode", false);
            model.addAttribute("menuActive", "flash-sale");
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("thongBaoLoi", "Đã xảy ra sự cố hệ thống: " + e.getMessage());
            return "admin/flash-sale/tao-moi";
        }
    }

    /**
     * Màn hình Form Chỉnh sửa đợt Flash Sale (US29).
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
            FlashSale fs = flashSaleService.layTheoId(id);

            if (!model.containsAttribute("req")) {
                List<ChiTietFlashSaleFormItem> items = fs.getDanhSachChiTiet().stream()
                        .map(ct -> ChiTietFlashSaleFormItem.builder()
                                .maBienTheId(ct.getMaBienTheId())
                                .giaFlashSale(ct.getGiaFlashSale())
                                .soLuongMoBan(ct.getSoLuongMoBan())
                                .soLuongDaBan(ct.getSoLuongDaBan())
                                .maSku(ct.getMaSku())
                                .tenSanPham(ct.getTenSanPham())
                                .tenMauSac(ct.getTenMauSac())
                                .maMauHex(ct.getMaMauHex())
                                .tenKichThuoc(ct.getTenKichThuoc())
                                .giaBanGoc(ct.getGiaBanGoc())
                                .tonKhoHienTai(ct.getTonKhoHienTai())
                                .anhDaiDienUrl(ct.getAnhDaiDienUrl())
                                .build())
                        .collect(Collectors.toList());

                TaoFlashSaleRequest req = TaoFlashSaleRequest.builder()
                        .id(fs.getId())
                        .tieuDe(fs.getTieuDe())
                        .thoiGianBatDau(fs.getThoiGianBatDau())
                        .thoiGianKetThuc(fs.getThoiGianKetThuc())
                        .kichHoat(fs.getKichHoat())
                        .danhSachChiTiet(items)
                        .build();
                model.addAttribute("req", req);
            }

            model.addAttribute("flashSaleGoc", fs);
            model.addAttribute("danhSachBienTheKhaDung", flashSaleService.layDanhSachBienTheKhaDungChoFlashSale());
            model.addAttribute("isEditMode", true);
            model.addAttribute("menuActive", "flash-sale");
            model.addAttribute("nhanVienDangNhap", actor);

            return "admin/flash-sale/tao-moi";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/flash-sale/hien-thi";
        }
    }

    /**
     * Xử lý Submit Cập nhật đợt Flash Sale (US29).
     */
    @PostMapping("/chinh-sua/{id}")
    public String xuLyCapNhat(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("req") TaoFlashSaleRequest req,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpSession session) {

        NhanVien actor = layNhanVienHienTai(request, session);
        req.setId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("danhSachBienTheKhaDung", flashSaleService.layDanhSachBienTheKhaDungChoFlashSale());
            model.addAttribute("isEditMode", true);
            model.addAttribute("menuActive", "flash-sale");
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("thongBaoLoi", "Dữ liệu cập nhật Flash Sale chưa hợp lệ. Vui lòng kiểm tra lại!");
            return "admin/flash-sale/tao-moi";
        }

        try {
            FlashSale updated = flashSaleService.capNhat(id, req);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật thành công đợt Flash Sale [" + updated.getTieuDe() + "]!");
            return "redirect:/admin/flash-sale/hien-thi";
        } catch (IllegalArgumentException e) {
            log.warn("Lỗi nghiệp vụ khi cập nhật Flash Sale ID {}: {}", id, e.getMessage());
            model.addAttribute("danhSachBienTheKhaDung", flashSaleService.layDanhSachBienTheKhaDungChoFlashSale());
            model.addAttribute("isEditMode", true);
            model.addAttribute("menuActive", "flash-sale");
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("thongBaoLoi", e.getMessage());
            return "admin/flash-sale/tao-moi";
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi cập nhật Flash Sale: ", e);
            model.addAttribute("danhSachBienTheKhaDung", flashSaleService.layDanhSachBienTheKhaDungChoFlashSale());
            model.addAttribute("isEditMode", true);
            model.addAttribute("menuActive", "flash-sale");
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("thongBaoLoi", "Đã xảy ra sự cố hệ thống: " + e.getMessage());
            return "admin/flash-sale/tao-moi";
        }
    }

    /**
     * Bật / Tắt trạng thái kích hoạt của đợt Flash Sale.
     */
    @PostMapping("/toggle/{id}")
    public String toggleKichHoat(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean active = flashSaleService.toggleKichHoat(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    active ? "Đã KÍCH HOẠT lại đợt Flash Sale!" : "Đã TẠM DỪNG đợt Flash Sale!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Không thể cập nhật trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/flash-sale/hien-thi";
    }

    /**
     * Xóa hoàn toàn đợt Flash Sale.
     */
    @PostMapping("/xoa/{id}")
    public String xoaFlashSale(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            FlashSale fs = flashSaleService.layTheoId(id);
            flashSaleService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã xóa thành công đợt Flash Sale [" + fs.getTieuDe() + "] khỏi hệ thống!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Không thể xóa Flash Sale: " + e.getMessage());
        }
        return "redirect:/admin/flash-sale/hien-thi";
    }

    /**
     * REST API lấy chi tiết 1 biến thể SKU để phục vụ JavaScript dynamic row addition.
     */
    @GetMapping("/api/bien-the/{id}")
    @ResponseBody
    public ResponseEntity<?> apiLayThongTinBienThe(@PathVariable("id") Long bienTheId) {
        try {
            ChiTietFlashSaleFormItem item = flashSaleService.layThongTinBienTheItem(bienTheId);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
