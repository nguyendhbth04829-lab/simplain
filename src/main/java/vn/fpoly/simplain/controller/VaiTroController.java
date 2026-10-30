package vn.fpoly.simplain.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.VaiTroRequest;
import vn.fpoly.simplain.entity.VaiTro;
import vn.fpoly.simplain.enums.ChucNangHeThong;
import vn.fpoly.simplain.service.VaiTroService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller MVC quản lý danh sách Vai trò trên giao diện Web dành cho Quản trị viên (US03).
 *
 * <p>Tiêu chí chấp nhận:
 * 1. Thêm/Sửa/Xóa vai_tro.
 * 2. Không cho xóa vai trò đang có nhân viên sử dụng.
 * 3. Phân quyền động cho các chức năng trong hệ thống.
 */
@Controller
@RequestMapping("/admin/vai-tro")
public class VaiTroController {

    private final VaiTroService vaiTroService;

    @Autowired
    public VaiTroController(VaiTroService vaiTroService) {
        this.vaiTroService = vaiTroService;
    }

    /**
     * Màn hình danh sách vai trò có phân trang và tìm kiếm.
     */
    @GetMapping({"", "/", "/hien-thi"})
    public String hienThiDanhSach(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<VaiTro> pageData = vaiTroService.layDanhSach(tuKhoa, pageable);

        // Lấy số lượng nhân viên và danh sách quyền đang sử dụng từng vai trò
        Map<Long, Long> soNhanVienMap = new HashMap<>();
        Map<Long, List<String>> quyenCuaCacVaiTroMap = new HashMap<>();
        for (VaiTro vt : pageData.getContent()) {
            soNhanVienMap.put(vt.getId(), vaiTroService.demSoNhanVien(vt.getId()));
            quyenCuaCacVaiTroMap.put(vt.getId(), vaiTroService.layDanhSachQuyenCuaVaiTro(vt.getId()));
        }

        model.addAttribute("pageData", pageData);
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("soNhanVienMap", soNhanVienMap);
        model.addAttribute("quyenCuaCacVaiTroMap", quyenCuaCacVaiTroMap);
        model.addAttribute("chucNangNhomMap", ChucNangHeThong.layTheoNhom());

        if (!model.containsAttribute("vaiTroRequest")) {
            model.addAttribute("vaiTroRequest", new VaiTroRequest());
        }

        return "admin/vai-tro/hien-thi";
    }

    /**
     * Màn hình xem chi tiết và form chỉnh sửa vai trò.
     */
    @GetMapping("/view/{id}")
    public String hienThiChiTiet(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            VaiTro vaiTro = vaiTroService.layTheoId(id);
            long soNhanVien = vaiTroService.demSoNhanVien(id);
            List<String> danhSachQuyen = vaiTroService.layDanhSachQuyenCuaVaiTro(id);

            if (!model.containsAttribute("vaiTroRequest")) {
                VaiTroRequest request = VaiTroRequest.builder()
                        .id(vaiTro.getId())
                        .maVaiTro(vaiTro.getMaVaiTro())
                        .tenVaiTro(vaiTro.getTenVaiTro())
                        .moTa(vaiTro.getMoTa())
                        .trangThai(vaiTro.getTrangThai())
                        .danhSachQuyen(danhSachQuyen)
                        .build();
                model.addAttribute("vaiTroRequest", request);
            }

            model.addAttribute("vaiTro", vaiTro);
            model.addAttribute("soNhanVien", soNhanVien);
            model.addAttribute("danhSachQuyenHienTai", danhSachQuyen);
            model.addAttribute("chucNangNhomMap", ChucNangHeThong.layTheoNhom());
            return "admin/vai-tro/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/vai-tro/hien-thi";
        }
    }

    /**
     * Xử lý thêm mới vai trò từ giao diện web.
     */
    @PostMapping("/them")
    public String xuLyThem(@Valid @ModelAttribute("vaiTroRequest") VaiTroRequest request,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            return hienThiDanhSach(null, 0, 5, model);
        }

        try {
            vaiTroService.them(request);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Thêm mới vai trò thành công!");
            return "redirect:/admin/vai-tro/hien-thi";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            redirectAttributes.addFlashAttribute("vaiTroRequest", request);
            return "redirect:/admin/vai-tro/hien-thi";
        }
    }

    /**
     * Xử lý cập nhật vai trò từ giao diện web.
     */
    @PostMapping("/sua/{id}")
    public String xuLySua(@PathVariable("id") Long id,
                          @Valid @ModelAttribute("vaiTroRequest") VaiTroRequest request,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (result.hasErrors()) {
            try {
                VaiTro vaiTro = vaiTroService.layTheoId(id);
                model.addAttribute("vaiTro", vaiTro);
                model.addAttribute("soNhanVien", vaiTroService.demSoNhanVien(id));
                model.addAttribute("chucNangNhomMap", ChucNangHeThong.layTheoNhom());
            } catch (Exception ignored) {
            }
            return "admin/vai-tro/view";
        }

        try {
            vaiTroService.sua(id, request);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Cập nhật vai trò thành công!");
            return "redirect:/admin/vai-tro/hien-thi";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/vai-tro/view/" + id;
        }
    }

    /**
     * Xử lý xóa vai trò (US03 - Kiểm tra ràng buộc nhân viên đang sử dụng).
     */
    @GetMapping("/xoa/{id}")
    public String xuLyXoa(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            vaiTroService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Xóa vai trò thành công!");
        } catch (IllegalStateException e) {
            // Không cho xóa vì đang có nhân viên sử dụng
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/vai-tro/hien-thi";
    }

    /**
     * Chuyển đổi trạng thái hoạt động (Hoạt động / Vô hiệu hóa) của vai trò.
     */
    @GetMapping("/doi-trang-thai/{id}")
    public String xuLyDoiTrangThai(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            VaiTro vt = vaiTroService.doiTrangThai(id);
            String trangThaiText = (vt.getTrangThai() != null && vt.getTrangThai() == 1) ? "HOẠT ĐỘNG" : "VÔ HIỆU HÓA";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái vai trò [" + vt.getTenVaiTro() + "] sang: " + trangThaiText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/vai-tro/hien-thi";
    }

    /**
     * Thao tác hàng loạt trên các vai trò đã chọn (Kích hoạt / Tạm ngưng hàng loạt).
     */
    @PostMapping("/thao-tac-hang-loat")
    public String thaoTacHangLoat(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            @RequestParam(name = "hanhDong") String hanhDong,
            RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng chọn ít nhất một vai trò để thực hiện!");
            return "redirect:/admin/vai-tro/hien-thi";
        }

        int count = 0;
        int targetStatus = "KICH_HOAT".equalsIgnoreCase(hanhDong) ? 1 : 0;
        for (Long id : ids) {
            try {
                VaiTro vt = vaiTroService.layTheoId(id);
                if (vt.getTrangThai() == null || vt.getTrangThai() != targetStatus) {
                    vaiTroService.capNhatTrangThai(id, targetStatus);
                    count++;
                }
            } catch (Exception ignored) {
            }
        }

        String actionText = targetStatus == 1 ? "kích hoạt" : "tạm ngưng";
        redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                "Đã " + actionText + " thành công " + count + " vai trò đã chọn.");
        return "redirect:/admin/vai-tro/hien-thi";
    }
}
