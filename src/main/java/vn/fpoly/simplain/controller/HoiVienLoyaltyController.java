/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - Controller phục vụ giao diện Web Thymeleaf Hội viên và Điểm thưởng Coolcash
 */
package vn.fpoly.simplain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.fpoly.simplain.dto.BienDongDiemResponse;
import vn.fpoly.simplain.dto.HoSoHoiVienResponse;
import vn.fpoly.simplain.entity.HangThanhVien;
import vn.fpoly.simplain.service.HoiVienLoyaltyService;

import java.util.List;

/**
 * Controller phục vụ màn hình web quản lý hồ sơ hội viên và ví điểm Coolcash.
 */
@Controller
@RequestMapping("/khach-hang/hoi-vien")
public class HoiVienLoyaltyController {

    private final HoiVienLoyaltyService hoiVienLoyaltyService;

    public HoiVienLoyaltyController(HoiVienLoyaltyService hoiVienLoyaltyService) {
        this.hoiVienLoyaltyService = hoiVienLoyaltyService;
    }

    /**
     * Màn hình tổng quan hồ sơ hội viên, hạng thẻ và lịch sử tích/tiêu điểm.
     */
    @GetMapping
    public String hienThiTongQuan(@RequestParam(defaultValue = "1") Long idKhachHang, Model model) {
        try {
            HoSoHoiVienResponse hoSo = hoiVienLoyaltyService.layHoSoHoiVien(idKhachHang);
            List<BienDongDiemResponse> lichSu = hoiVienLoyaltyService.layLichSuBienDongDiem(idKhachHang);
            List<HangThanhVien> danhSachHang = hoiVienLoyaltyService.layDanhSachHangThanhVien();

            model.addAttribute("hoSo", hoSo);
            model.addAttribute("lichSuDiem", lichSu);
            model.addAttribute("danhSachHang", danhSachHang);
            model.addAttribute("idKhachHang", idKhachHang);
        } catch (Exception e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            model.addAttribute("idKhachHang", idKhachHang);
        }
        return "khach-hang/hoi-vien/tong-quan";
    }
}
