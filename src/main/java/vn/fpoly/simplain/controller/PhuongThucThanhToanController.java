package vn.fpoly.simplain.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.PhuongThucThanhToanRequest;
import vn.fpoly.simplain.entity.PhuongThucThanhToan;
import vn.fpoly.simplain.service.PhuongThucThanhToanService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller Quản trị Cổng thanh toán (US18).
 * - Tiêu chí 1: CRUD bảng phuong_thuc_thanh_toan (COD, VNPay, MoMo).
 * - Tính năng cốt lõi: Bật/Tắt cấu hình cổng thanh toán.
 */
@Slf4j
@Controller
@RequestMapping("/admin/phuong-thuc-thanh-toan")
@RequiredArgsConstructor
public class PhuongThucThanhToanController {

    private final PhuongThucThanhToanService phuongThucService;

    /**
     * Hiển thị danh sách Phương thức thanh toán (R trong CRUD).
     */
    @GetMapping({"", "/hien-thi"})
    public String hienThi(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "laTrucTuyen", required = false) Boolean laTrucTuyen,
            @RequestParam(name = "kichHoat", required = false) Boolean kichHoat,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(Math.max(0, page), size, Sort.by("id").ascending());
        Page<PhuongThucThanhToan> trangDuLieu = phuongThucService.layDanhSachPhanTrang(
                tuKhoa, laTrucTuyen, kichHoat, pageable);

        List<PhuongThucThanhToan> tatCa = phuongThucService.layTatCa();
        long soDangBat = tatCa.stream().filter(p -> Boolean.TRUE.equals(p.getKichHoat())).count();
        long soDangTat = tatCa.size() - soDangBat;

        model.addAttribute("danhSachPhuongThuc", trangDuLieu);
        model.addAttribute("tongSo", tatCa.size());
        model.addAttribute("soDangBat", soDangBat);
        model.addAttribute("soDangTat", soDangTat);

        model.addAttribute("tuKhoa", tuKhoa);
        model.addAttribute("laTrucTuyen", laTrucTuyen);
        model.addAttribute("kichHoat", kichHoat);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", trangDuLieu.getTotalPages());

        if (!model.containsAttribute("phuongThucMoi")) {
            model.addAttribute("phuongThucMoi", new PhuongThucThanhToanRequest());
        }

        model.addAttribute("menuActive", "phuong-thuc-thanh-toan");
        return "admin/phuong-thuc-thanh-toan/hien-thi";
    }

    /**
     * Thêm mới phương thức thanh toán (C trong CRUD).
     */
    @PostMapping("/them")
    public String them(
            @Valid @ModelAttribute("phuongThucMoi") PhuongThucThanhToanRequest request,
            BindingResult result,
            @RequestParam(value = "fileBieuTuong", required = false) MultipartFile fileBieuTuong,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.phuongThucMoi", result);
            redirectAttributes.addFlashAttribute("phuongThucMoi", request);
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Dữ liệu nhập vào chưa hợp lệ, vui lòng kiểm tra lại!");
            return "redirect:/admin/phuong-thuc-thanh-toan/hien-thi";
        }

        try {
            PhuongThucThanhToan saved = phuongThucService.them(request, fileBieuTuong);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Thêm mới phương thức thanh toán [" + saved.getTenPhuongThuc() + "] thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi thêm phương thức thanh toán: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("phuongThucMoi", request);
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }

        return "redirect:/admin/phuong-thuc-thanh-toan/hien-thi";
    }

    /**
     * Cập nhật thông tin và cấu hình cổng thanh toán (U trong CRUD).
     */
    @PostMapping("/sua/{id}")
    public String sua(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute PhuongThucThanhToanRequest request,
            BindingResult result,
            @RequestParam(value = "fileBieuTuong", required = false) MultipartFile fileBieuTuong,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Dữ liệu cập nhật chưa đúng định dạng!");
            return "redirect:/admin/phuong-thuc-thanh-toan/hien-thi";
        }

        try {
            PhuongThucThanhToan updated = phuongThucService.sua(id, request, fileBieuTuong);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật thành công cấu hình cổng thanh toán [" + updated.getTenPhuongThuc() + "]!");
        } catch (Exception e) {
            log.error("Lỗi khi sửa cổng thanh toán: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }

        return "redirect:/admin/phuong-thuc-thanh-toan/hien-thi";
    }

    /**
     * Bật/Tắt (Đổi trạng thái) cổng thanh toán giống các chức năng quản lý khác (US18).
     * Gọi qua URL: /admin/phuong-thuc-thanh-toan/doi-trang-thai?id=1 hoặc /admin/phuong-thuc-thanh-toan/doi-trang-thai/1
     */
    @GetMapping("/doi-trang-thai")
    public String doiTrangThai(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean trangThaiMoi = phuongThucService.batTatKichHoat(id);
            String trangThaiText = trangThaiMoi ? "CÒN HOẠT ĐỘNG" : "TẠM NGƯNG";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái cổng thanh toán sang: " + trangThaiText);
        } catch (Exception e) {
            log.error("Lỗi khi đổi trạng thái cổng thanh toán: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/phuong-thuc-thanh-toan/hien-thi";
    }

    @GetMapping("/doi-trang-thai/{id}")
    public String doiTrangThaiPathVariable(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        return doiTrangThai(id, redirectAttributes);
    }

    /**
     * Bật/Tắt kích hoạt cổng thanh toán (Tính năng cốt lõi US18).
     * Hỗ trợ cả AJAX 1-click và form redirect.
     */
    @PostMapping("/toggle/{id}")
    public Object batTatKichHoat(
            @PathVariable("id") Long id,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        try {
            boolean trangThaiMoi = phuongThucService.batTatKichHoat(id);
            if (isAjax) {
                Map<String, Object> res = new HashMap<>();
                res.put("success", true);
                res.put("kichHoat", trangThaiMoi);
                res.put("message", trangThaiMoi
                        ? "Đã BẬT kích hoạt cổng thanh toán!"
                        : "Đã TẮT cổng thanh toán (khách hàng sẽ không thấy cổng này khi thanh toán)!");
                return ResponseEntity.ok(res);
            }

            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    trangThaiMoi ? "Đã BẬT kích hoạt cổng thanh toán thành công!" : "Đã TẮT cổng thanh toán thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi bật/tắt cổng thanh toán: {}", e.getMessage());
            if (isAjax) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(err);
            }
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }

        return "redirect:/admin/phuong-thuc-thanh-toan/hien-thi";
    }

    /**
     * Xóa phương thức thanh toán (D trong CRUD).
     * Hỗ trợ cả GET và POST để đồng bộ với các chức năng quản lý khác.
     */
    @GetMapping("/xoa")
    public String xoaTheoParam(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        return xoa(id, redirectAttributes);
    }

    @GetMapping("/xoa/{id}")
    public String xoaTheoPath(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        return xoa(id, redirectAttributes);
    }

    @PostMapping("/xoa/{id}")
    public String xoa(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            phuongThucService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Xóa phương thức thanh toán thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi xóa cổng thanh toán: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/phuong-thuc-thanh-toan/hien-thi";
    }

    /**
     * Thao tác hàng loạt (Bật hàng loạt, Tắt hàng loạt, Xóa hàng loạt).
     */
    @PostMapping("/thao-tac-hang-loat")
    public String thaoTacHangLoat(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            @RequestParam(name = "hanhDong", required = false) String hanhDong,
            RedirectAttributes redirectAttributes) {

        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng chọn ít nhất một phương thức thanh toán!");
            return "redirect:/admin/phuong-thuc-thanh-toan/hien-thi";
        }

        if ("bat".equalsIgnoreCase(hanhDong)) {
            int count = phuongThucService.capNhatKichHoatHangLoat(ids, true);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã BẬT kích hoạt thành công cho " + count + " cổng thanh toán được chọn!");
        } else if ("tat".equalsIgnoreCase(hanhDong)) {
            int count = phuongThucService.capNhatKichHoatHangLoat(ids, false);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã TẮT kích hoạt thành công cho " + count + " cổng thanh toán được chọn!");
        } else if ("xoa".equalsIgnoreCase(hanhDong)) {
            List<String> loiList = phuongThucService.xoaHangLoat(ids);
            int thanhCong = ids.size() - loiList.size();
            if (thanhCong > 0) {
                redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                        "Đã xóa thành công " + thanhCong + " cổng thanh toán!");
            }
            if (!loiList.isEmpty()) {
                redirectAttributes.addFlashAttribute("thongBaoLoi",
                        "Không thể xóa " + loiList.size() + " mục do đã phát sinh giao dịch lịch sử!");
            }
        } else {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Hành động thao tác hàng loạt không hợp lệ!");
        }

        return "redirect:/admin/phuong-thuc-thanh-toan/hien-thi";
    }

    /**
     * API JSON lấy chi tiết phục vụ Modal Chỉnh sửa / Xem cấu hình (US18).
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> layChiTietApi(@PathVariable("id") Long id) {
        try {
            PhuongThucThanhToan pt = phuongThucService.layTheoId(id);
            return ResponseEntity.ok(pt);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
