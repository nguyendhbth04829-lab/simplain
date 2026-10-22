package vn.fpoly.simplain.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.entity.ChiTietGioHang;
import vn.fpoly.simplain.entity.GioHang;
import vn.fpoly.simplain.service.GioHangService;
import vn.fpoly.simplain.service.PhuongThucThanhToanService;
import vn.fpoly.simplain.service.CheckoutService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Quản lý giỏ hàng của khách (US17).
 * Giao diện Thymeleaf để test tay. Khách thật dùng khi có đăng nhập (Duy - US01).
 */
@Controller
@RequestMapping("/gio-hang")
public class GioHangController {

    private final GioHangService gioHangService;
    private final PhuongThucThanhToanService phuongThucThanhToanService;
    private final CheckoutService checkoutService;

    public GioHangController(GioHangService gioHangService,
                             PhuongThucThanhToanService phuongThucThanhToanService,
                             CheckoutService checkoutService) {
        this.gioHangService = gioHangService;
        this.phuongThucThanhToanService = phuongThucThanhToanService;
        this.checkoutService = checkoutService;
    }

    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @RequestParam(defaultValue = "1") Long khachHangId,
                          @RequestParam(required = false) String loi) {
        GioHang gio = gioHangService.layHoacTao(khachHangId);
        List<ChiTietGioHang> chiTiet = gioHangService.layChiTiet(gio.getId());
        List<ChiTietGioHang> monLe = new ArrayList<>();
        Map<String, List<ChiTietGioHang>> nhomCombo = new LinkedHashMap<>();
        for (ChiTietGioHang dong : chiTiet) {
            if (dong.getNhomComboToken() == null) {
                monLe.add(dong);
            } else {
                nhomCombo.computeIfAbsent(dong.getNhomComboToken(), k -> new ArrayList<>()).add(dong);
            }
        }
        model.addAttribute("gio", gio);
        model.addAttribute("monLe", monLe);
        model.addAttribute("nhomCombo", nhomCombo);
        var sanPhamTheoBienThe = gioHangService.layThongTinSanPham(chiTiet);
        model.addAttribute("sanPhamTheoBienThe", sanPhamTheoBienThe);
        model.addAttribute("tongTienHang", gioHangService.tinhTongTienHang(chiTiet, sanPhamTheoBienThe));
        model.addAttribute("khachHangId", khachHangId);
        model.addAttribute("tongSoLuong", chiTiet.stream().mapToInt(c -> c.getSoLuong() == null ? 0 : c.getSoLuong()).sum());
        model.addAttribute("danhSachPhuongThucThanhToan", phuongThucThanhToanService.layDanhSachDangKichHoat());
        model.addAttribute("danhSachDiaChi", checkoutService.layDiaChi(khachHangId));
        model.addAttribute("bienTheLuaChonTheoSanPham", gioHangService.layBienTheLuaChonTheoSanPham());
        model.addAttribute("danhSachVoucher", checkoutService.layVoucherChuaDung(khachHangId));
        model.addAttribute("soDuCoolcash", checkoutService.laySoDuCoolcash(khachHangId));
        model.addAttribute("phiVanChuyen", checkoutService.getPhiVanChuyen());
        model.addAttribute("loi", loi);
        return "khach-hang/gio-hang/hien-thi";
    }

    @PostMapping("/them")
    public String them(@RequestParam(defaultValue = "1") Long khachHangId,
                       @NotNull @RequestParam Long maBienTheId,
                       @RequestParam(required = false) Long maComboId,
                       @RequestParam(required = false) String nhomComboToken,
                       @Min(1) @RequestParam(defaultValue = "1") int soLuong) {
        try {
            gioHangService.themMon(khachHangId, maBienTheId, maComboId, nhomComboToken, Math.max(soLuong, 1));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return "redirect:/gio-hang/hien-thi?khachHangId=" + khachHangId + "&loi=Biến thể hoặc combo không tồn tại trong kho";
        }
        return "redirect:/gio-hang/hien-thi?khachHangId=" + khachHangId;
    }

    @PostMapping("/sua-so-luong")
    public String suaSoLuong(@RequestParam(defaultValue = "1") Long khachHangId,
                             @RequestParam Long idDong,
                             @RequestParam int soLuong) {
        gioHangService.suaSoLuong(idDong, soLuong);
        return "redirect:/gio-hang/hien-thi?khachHangId=" + khachHangId;
    }

    @PostMapping("/doi-bien-the")
    public String doiBienThe(@RequestParam(defaultValue = "1") Long khachHangId,
                             @RequestParam Long idDong,
                             @RequestParam Long maBienTheId,
                             RedirectAttributes redirectAttributes) {
        try {
            gioHangService.doiBienThe(khachHangId, idDong, maBienTheId);
        } catch (IllegalArgumentException loi) {
            redirectAttributes.addFlashAttribute("loi", loi.getMessage());
        }
        return "redirect:/gio-hang/hien-thi?khachHangId=" + khachHangId;
    }

    /**
     * Nút -/+ tăng giảm nhanh ngoài giỏ hàng.
     */
    @GetMapping("/doi-so-luong")
    public String doiSoLuong(@RequestParam(defaultValue = "1") Long khachHangId,
                             @RequestParam Long idDong,
                             @RequestParam int delta) {
        try {
            if (delta != 0) gioHangService.doiSoLuong(idDong, delta > 0 ? 1 : -1);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return "redirect:/gio-hang/hien-thi?khachHangId=" + khachHangId + "&loi=Dòng hàng đang trỏ tới dữ liệu kho không tồn tại";
        }
        return "redirect:/gio-hang/hien-thi?khachHangId=" + khachHangId;
    }

    @GetMapping("/xoa-dong")
    public String xoaDong(@RequestParam(defaultValue = "1") Long khachHangId,
                          @RequestParam Long idDong) {
        gioHangService.xoaDong(idDong);
        return "redirect:/gio-hang/hien-thi?khachHangId=" + khachHangId;
    }

    @GetMapping("/xoa-nhom")
    public String xoaNhom(@RequestParam(defaultValue = "1") Long khachHangId,
                          @RequestParam Long maGioHangId,
                          @RequestParam String token) {
        gioHangService.xoaNhomCombo(maGioHangId, token);
        return "redirect:/gio-hang/hien-thi?khachHangId=" + khachHangId;
    }
}
