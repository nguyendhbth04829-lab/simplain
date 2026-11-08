package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.entity.DanhGiaSanPham;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.service.DanhGiaSanPhamService;
import vn.fpoly.simplain.service.NhanVienService;

import java.util.Map;

/**
 * Controller phục vụ Quản lý & Trả lời Đánh giá của khách hàng từ Admin/CSKH (US15).
 */
@Slf4j
@Controller
@RequestMapping("/admin/danh-gia")
@RequiredArgsConstructor
public class AdminDanhGiaController {

    public static final String COOKIE_ADMIN_TOKEN = "jwt_admin_token";

    private final DanhGiaSanPhamService danhGiaSanPhamService;
    private final NhanVienRepository nhanVienRepository;
    private final NhanVienService nhanVienService;

    /**
     * Màn hình danh sách đánh giá của khách hàng kèm bộ lọc và thống kê tổng quan.
     */
    @GetMapping({"", "/", "/hien-thi"})
    public String hienThiDanhSachDanhGia(
            @RequestParam(name = "q", required = false) String tuKhoa,
            @RequestParam(name = "soSao", required = false) Integer soSao,
            @RequestParam(name = "trangThai", defaultValue = "TAT_CA") String trangThai,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        NhanVien actor = layNhanVienHienTai(request, session);
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        Page<DanhGiaSanPham> pageResult = danhGiaSanPhamService.layDanhSachDanhGiaAdmin(
                tuKhoa, soSao, trangThai, PageRequest.of(page, size)
        );

        Map<String, Object> thongKe = danhGiaSanPhamService.layThongKeTongQuanAdmin();

        model.addAttribute("danhSachDanhGia", pageResult);
        model.addAttribute("thongKe", thongKe);
        model.addAttribute("tuKhoaHienTai", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("soSaoHienTai", soSao);
        model.addAttribute("trangThaiHienTai", trangThai);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("nhanVienHienTai", actor);

        return "admin/danh-gia/hien-thi";
    }

    /**
     * Admin / CSKH gửi câu trả lời phản hồi cho một bài đánh giá.
     */
    @PostMapping("/{id}/tra-loi")
    public String traLoiDanhGia(
            @PathVariable("id") Long id,
            @RequestParam("noiDungPhanHoi") String noiDungPhanHoi,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            NhanVien actor = layNhanVienHienTai(request, session);
            Long nhanVienId = (actor != null) ? actor.getId() : null;

            danhGiaSanPhamService.traLoiDanhGia(id, noiDungPhanHoi, nhanVienId);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã gửi câu trả lời phản hồi đánh giá thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi trả lời đánh giá ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("loiThaoTac", e.getMessage());
        }

        return "redirect:/admin/danh-gia/hien-thi";
    }

    /**
     * Xóa câu trả lời phản hồi của Admin.
     */
    @PostMapping("/{id}/xoa-tra-loi")
    public String xoaPhanHoi(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {

        try {
            danhGiaSanPhamService.xoaPhanHoi(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa nội dung phản hồi của đánh giá!");
        } catch (Exception e) {
            log.error("Lỗi khi xóa phản hồi đánh giá ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("loiThaoTac", e.getMessage());
        }

        return "redirect:/admin/danh-gia/hien-thi";
    }

    /**
     * Xóa đánh giá spam / vi phạm.
     */
    @PostMapping("/{id}/xoa")
    public String xoaDanhGia(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {

        try {
            danhGiaSanPhamService.xoaDanhGia(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa đánh giá thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi xóa đánh giá ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("loiThaoTac", e.getMessage());
        }

        return "redirect:/admin/danh-gia/hien-thi";
    }

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
