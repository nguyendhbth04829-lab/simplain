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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.dto.XetDuyetRmaRequest;
import vn.fpoly.simplain.dto.YeuCauDoiTraDetailDto;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.YeuCauDoiTra;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.service.DoiTraService;
import vn.fpoly.simplain.service.NhanVienService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller Quản lý & Xét duyệt Yêu cầu Đổi/Trả (RMA) dành cho CSKH & Quản trị kho (US25 - Mod 5 - Hưng).
 * - Tiêu chí 1: Xem danh sách RMA (lọc tab trạng thái, tìm kiếm, phân trang).
 * - Tiêu chí 2: Đổi trạng thái (Approve: Đã tiếp nhận/Đang xử lý/Hoàn thành; Reject: Từ chối).
 * - Tiêu chí 3: Bổ sung ghi chú kiểm định hàng về kho.
 */
@Slf4j
@Controller
@RequestMapping("/admin/doi-tra")
@RequiredArgsConstructor
public class QuanLyDoiTraController {

    public static final String COOKIE_ADMIN_TOKEN = "jwt_admin_token";

    private final DoiTraService doiTraService;
    private final NhanVienRepository nhanVienRepository;
    private final NhanVienService nhanVienService;

    /**
     * Màn hình danh sách tất cả yêu cầu Đổi/Trả cho CSKH (US25 - Tiêu chí 1).
     */
    @GetMapping({"", "/hien-thi"})
    public String hienThiDanhSach(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "trangThai", defaultValue = "TAT_CA") String trangThai,
            @RequestParam(name = "loaiDoiTra", defaultValue = "TAT_CA") String loaiDoiTra,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        NhanVien actor = layNhanVienHienTai(request, session);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        Page<YeuCauDoiTraDetailDto> trangRma = doiTraService.layDanhSachRmaChoAdmin(
                tuKhoa, trangThai, loaiDoiTra, pageable);

        Map<String, Long> demTrangThai = doiTraService.demSoLuongRmaTheoCacTrangThai();

        model.addAttribute("trangRma", trangRma);
        model.addAttribute("demTrangThai", demTrangThai);
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("trangThaiHienTai", trangThai);
        model.addAttribute("loaiDoiTraHienTai", loaiDoiTra);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("nhanVienDangNhap", actor);

        return "admin/doi-tra/hien-thi";
    }

    /**
     * Màn hình xem chi tiết phiếu RMA & Bổ sung ghi chú kiểm định hàng về kho (US25 - Tiêu chí 3).
     */
    @GetMapping("/chi-tiet/{id}")
    public String hienThiChiTiet(
            @PathVariable("id") Long id,
            HttpServletRequest request,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            NhanVien actor = layNhanVienHienTai(request, session);
            YeuCauDoiTraDetailDto rmaDetail = doiTraService.layChiTietRmaTheoIdChoAdmin(id);

            List<String> dsHopLe = doiTraService.layDanhSachTrangThaiRmaHopLeTiepTheo(rmaDetail.getYeuCauDoiTra().getTrangThai());
            model.addAttribute("rmaDetail", rmaDetail);
            model.addAttribute("nhanVienDangNhap", actor);
            model.addAttribute("dsTrangThaiHopLe", dsHopLe);

            if (!model.containsAttribute("xetDuyetRequest")) {
                String defaultNextStatus = dsHopLe.isEmpty() ? "" : dsHopLe.get(0);
                XetDuyetRmaRequest formReq = XetDuyetRmaRequest.builder()
                        .trangThaiMoi(defaultNextStatus)
                        .ghiChuAdmin(rmaDetail.getYeuCauDoiTra().getGhiChuAdmin())
                        .nhapLaiKho(false)
                        .build();
                model.addAttribute("xetDuyetRequest", formReq);
            }

            return "admin/doi-tra/chi-tiet";
        } catch (IllegalArgumentException e) {
            log.warn("Lỗi khi mở chi tiết RMA ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/doi-tra/hien-thi";
        }
    }

    /**
     * Xử lý Xét duyệt Yêu cầu Đổi/Trả (RMA): Đổi trạng thái & Bổ sung ghi chú kiểm định kho & Gửi lại ảnh bill cho khách (US25).
     */
    @PostMapping("/xet-duyet/{id}")
    public String xuLyXetDuyet(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("xetDuyetRequest") XetDuyetRmaRequest req,
            BindingResult bindingResult,
            @RequestParam(value = "fileBill", required = false) MultipartFile fileBill,
            HttpServletRequest request,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        NhanVien actor = layNhanVienHienTai(request, session);

        // Validation tùy chỉnh cho lý do khi từ chối
        if ("TU_CHOI".equalsIgnoreCase(req.getTrangThaiMoi())) {
            if (req.getGhiChuAdmin() == null || req.getGhiChuAdmin().trim().length() < 5) {
                bindingResult.rejectValue("ghiChuAdmin", "error.ghiChuAdmin", 
                        "Vui lòng nhập chi tiết lý do từ chối yêu cầu Đổi/Trả (tối thiểu 5 ký tự)!");
            }
        }

        if (bindingResult.hasErrors()) {
            try {
                YeuCauDoiTraDetailDto rmaDetail = doiTraService.layChiTietRmaTheoIdChoAdmin(id);
                model.addAttribute("rmaDetail", rmaDetail);
                model.addAttribute("nhanVienDangNhap", actor);
                model.addAttribute("dsTrangThaiHopLe", doiTraService.layDanhSachTrangThaiRmaHopLeTiepTheo(rmaDetail.getYeuCauDoiTra().getTrangThai()));
                model.addAttribute("thongBaoLoi", "Dữ liệu xét duyệt chưa hợp lệ, vui lòng kiểm tra lại các trường báo đỏ!");
                return "admin/doi-tra/chi-tiet";
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("thongBaoLoi", ex.getMessage());
                return "redirect:/admin/doi-tra/hien-thi";
            }
        }

        try {
            YeuCauDoiTra saved = doiTraService.xetDuyetYeuCauRma(id, req, fileBill, actor);

            String msg = String.format("Đã cập nhật trạng thái phiếu RMA #%s sang [%s] thành công!",
                    saved.getMaYeuCauRma(), saved.getTrangThai());
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", msg);
            return "redirect:/admin/doi-tra/chi-tiet/" + id;

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Lỗi nghiệp vụ khi xét duyệt RMA ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/doi-tra/chi-tiet/" + id;
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi xét duyệt RMA ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Lỗi xử lý: " + e.getMessage());
            return "redirect:/admin/doi-tra/chi-tiet/" + id;
        }
    }

    /**
     * API tự động sinh mã giao dịch ngân hàng / mã đối soát hoàn tiền / đổi hàng (US25).
     */
    @GetMapping("/api/sinh-ma-giao-dich")
    @ResponseBody
    public ResponseEntity<?> apiSinhMaGiaoDich(
            @RequestParam(value = "loaiDoiTra", defaultValue = "TRA_HANG_HOAN_TIEN") String loaiDoiTra) {
        try {
            String maTuDong = doiTraService.sinhMaGiaoDichTuDong(loaiDoiTra);
            return ResponseEntity.ok(Map.of("success", true, "maGiaoDich", maTuDong));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * API xem dữ liệu chi tiết phiếu RMA (Modal AJAX Quick View).
     */
    @GetMapping("/api/chi-tiet/{id}")
    @ResponseBody
    public ResponseEntity<?> layChiTietRmaAjax(@PathVariable("id") Long id) {
        try {
            YeuCauDoiTraDetailDto rmaDetail = doiTraService.layChiTietRmaTheoIdChoAdmin(id);
            return ResponseEntity.ok(rmaDetail);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * API cập nhật nhanh trạng thái & ghi chú kiểm định qua AJAX Modal.
     */
    @PostMapping("/api/xet-duyet/{id}")
    @ResponseBody
    public ResponseEntity<?> xetDuyetRmaAjax(
            @PathVariable("id") Long id,
            @RequestBody @Valid XetDuyetRmaRequest req,
            HttpServletRequest request,
            HttpSession session) {

        try {
            NhanVien actor = layNhanVienHienTai(request, session);
            YeuCauDoiTra saved = doiTraService.xetDuyetYeuCauRma(id, req, actor);

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Xét duyệt phiếu RMA #" + saved.getMaYeuCauRma() + " thành công!");
            res.put("maYeuCauRma", saved.getMaYeuCauRma());
            res.put("trangThai", saved.getTrangThai());
            res.put("ghiChuAdmin", saved.getGhiChuAdmin());
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            log.warn("Lỗi AJAX xét duyệt RMA ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // =========================================================================
    // TIỆN ÍCH LẤY NHÂN VIÊN
    // =========================================================================

    private NhanVien layNhanVienHienTai(HttpServletRequest request, HttpSession session) {
        if (session != null) {
            Object obj = session.getAttribute("nhanVienHienTai");
            if (obj instanceof XacThucNhanVienResponse xacThuc && xacThuc.getId() != null) {
                return nhanVienRepository.findById(xacThuc.getId()).orElse(null);
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
