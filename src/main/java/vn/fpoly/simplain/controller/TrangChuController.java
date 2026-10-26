/**
 * Tác giả: Gia Minh
 * Chức năng: Điều hướng Cổng thông tin trang chủ Simplain Storefront & Hub kiểm thử
 */
package vn.fpoly.simplain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.repository.KhachHangRepository;

import java.util.List;

/**
 * Bộ điều khiển giao diện trang chủ trung tâm kết nối toàn bộ hệ thống Simplain.
 */
@Controller
@RequiredArgsConstructor
@Tag(name = "Trang Chủ", description = "Cổng thông tin điều hướng trung tâm và kiểm thử hệ thống")
public class TrangChuController {

    private final KhachHangRepository khachHangRepository;

    /**
     * Hiển thị giao diện trang chủ trung tâm với đầy đủ liên kết kiểm thử các User Story.
     *
     * @param idKhachHang Mã ID khách hàng đang chọn thử nghiệm (mặc định là 1)
     * @param model Đối tượng Model truyền dữ liệu sang view Thymeleaf
     * @return Tên template trang chủ
     */
    @GetMapping({"/", "/trang-chu"})
    @Operation(summary = "Hiển thị trang chủ trung tâm")
    public String hienThiTrangChu(
            @RequestParam(name = "idKhachHang", defaultValue = "1") Long idKhachHang,
            Model model) {
        
        List<KhachHang> danhSachKhachHang = khachHangRepository.findAll();
        
        model.addAttribute("idKhachHangHienTai", idKhachHang);
        model.addAttribute("danhSachKhachHang", danhSachKhachHang);
        
        return "trang-chu";
    }
}
