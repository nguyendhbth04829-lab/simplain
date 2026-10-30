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
import vn.fpoly.simplain.entity.ChatLieu;
import vn.fpoly.simplain.service.ChatLieuService;

import java.util.List;

/**
 * Controller MVC Quản lý Chất liệu vải [chat_lieu] trong CMS Admin.
 * Hỗ trợ đầy đủ CRUD, Tìm kiếm từ khóa, Lọc trạng thái kích hoạt và Phân trang chuẩn SIMPLAIN.
 */
@Controller
@RequestMapping("/admin/chat-lieu")
public class ChatLieuController {

    private final ChatLieuService chatLieuService;

    @Autowired
    public ChatLieuController(ChatLieuService chatLieuService) {
        this.chatLieuService = chatLieuService;
    }

    @GetMapping({"", "/"})
    public String chuyenHuongMacDinh() {
        return "redirect:/admin/chat-lieu/hien-thi";
    }

    /**
     * Hiển thị danh sách chất liệu vải có phân trang, tìm kiếm và lọc trạng thái.
     */
    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @RequestParam(defaultValue = "0") Integer page,
                          @RequestParam(required = false) String tuKhoa,
                          @RequestParam(required = false) Boolean kichHoat) {
        if (page == null || page < 0) {
            page = 0;
        }
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ChatLieu> pageData = chatLieuService.layDanhSachPhanTrang(tuKhoa, kichHoat, pageable);

        model.addAttribute("listChatLieu", pageData.getContent());
        model.addAttribute("pageData", pageData);
        if (!model.containsAttribute("chatLieu")) {
            model.addAttribute("chatLieu", new ChatLieu());
        }

        model.addAttribute("page", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("kichHoatLoc", kichHoat);
        model.addAttribute("tongChatLieu", chatLieuService.demTongSo());

        return "admin/chat-lieu/hien-thi";
    }

    /**
     * Xử lý thêm mới chất liệu vải.
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("chatLieu") ChatLieu chatLieu,
                      BindingResult result,
                      Model model,
                      RedirectAttributes redirectAttributes,
                      @RequestParam(defaultValue = "0") Integer page) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu chất liệu vải chưa hợp lệ, vui lòng kiểm tra lại!");
            return hienThi(model, page, null, null);
        }
        try {
            ChatLieu saved = chatLieuService.them(chatLieu);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Thêm mới chất liệu vải [" + saved.getTenChatLieu() + "] thành công!");
            return "redirect:/admin/chat-lieu/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return hienThi(model, page, null, null);
        }
    }

    /**
     * Xử lý cập nhật thông tin chất liệu vải.
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("chatLieu") ChatLieu chatLieu,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         @RequestParam(defaultValue = "0") Integer page) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu cập nhật chất liệu vải chưa hợp lệ!");
            return hienThi(model, page, null, null);
        }
        try {
            ChatLieu updated = chatLieuService.sua(chatLieu.getId(), chatLieu);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật chất liệu vải [" + updated.getTenChatLieu() + "] thành công!");
            return "redirect:/admin/chat-lieu/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return hienThi(model, page, null, null);
        }
    }

    /**
     * Đổi nhanh trạng thái (Đang sử dụng / Ngưng sử dụng).
     */
    @GetMapping("/doi-trang-thai")
    public String doiTrangThai(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            ChatLieu cl = chatLieuService.doiTrangThai(id);
            String trangThai = Boolean.TRUE.equals(cl.getKichHoat()) ? "ĐANG SỬ DỤNG" : "NGƯNG SỬ DỤNG";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái chất liệu [" + cl.getTenChatLieu() + "] sang: " + trangThai);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/chat-lieu/hien-thi";
    }

    /**
     * Xóa chất liệu vải (có kiểm tra ràng buộc không cho xóa nếu có sản phẩm liên kết).
     */
    @GetMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            chatLieuService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Xóa chất liệu vải thành công!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/chat-lieu/hien-thi";
    }

    /**
     * Thao tác hàng loạt trên các chất liệu vải đã chọn (Kích hoạt / Ngưng sử dụng hàng loạt).
     */
    @PostMapping("/thao-tac-hang-loat")
    public String thaoTacHangLoat(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            @RequestParam(name = "hanhDong") String hanhDong,
            RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng chọn ít nhất một chất liệu vải để thực hiện!");
            return "redirect:/admin/chat-lieu/hien-thi";
        }

        int count = 0;
        boolean targetStatus = "KICH_HOAT".equalsIgnoreCase(hanhDong);
        for (Long id : ids) {
            try {
                ChatLieu cl = chatLieuService.layTheoId(id);
                if (cl.getKichHoat() == null || cl.getKichHoat() != targetStatus) {
                    chatLieuService.capNhatTrangThai(id, targetStatus);
                    count++;
                }
            } catch (Exception ignored) {
            }
        }

        String actionText = targetStatus ? "kích hoạt" : "ngưng sử dụng";
        redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                "Đã " + actionText + " thành công " + count + " chất liệu vải đã chọn.");
        return "redirect:/admin/chat-lieu/hien-thi";
    }
}
