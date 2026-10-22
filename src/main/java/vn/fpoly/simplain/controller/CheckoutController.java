package vn.fpoly.simplain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.CheckoutKetQua;
import vn.fpoly.simplain.service.CheckoutService;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/checkout/ap-dung-voucher")
    @ResponseBody
    public Map<String, Object> apDungVoucher(@RequestParam Long maKhachHangId,
                                             @RequestParam String maCode) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            BigDecimal tienGiam = checkoutService.kiemTraMaGiamGia(maKhachHangId, maCode);
            response.put("hopLe", true);
            response.put("tienGiam", tienGiam);
            response.put("thongBao", "Áp dụng được mã " + maCode.trim().toUpperCase()
                    + " · Giảm " + tienGiam.stripTrailingZeros().toPlainString() + " đ khi checkout.");
        } catch (IllegalArgumentException loi) {
            response.put("hopLe", false);
            response.put("thongBao", loi.getMessage());
        }
        return response;
    }

    @PostMapping("/checkout/xac-nhan")
    public String xacNhan(@RequestParam Long khachHangId,
                          @RequestParam(required = false) Long diaChiId,
                          @RequestParam(required = false) String maPhuongThuc,
                          @RequestParam(required = false) String maGiamGia,
                          @RequestParam(required = false, defaultValue = "0") BigDecimal tienCoolcashDung,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            CheckoutKetQua ketQua = checkoutService.xacNhan(khachHangId, diaChiId, maPhuongThuc,
                    maGiamGia, tienCoolcashDung);
            model.addAttribute("ketQua", ketQua);
            return "khach-hang/checkout/xac-nhan";
        } catch (IllegalArgumentException loi) {
            redirectAttributes.addFlashAttribute("checkoutError", loi.getMessage());
            return "redirect:/gio-hang/hien-thi?khachHangId=" + khachHangId;
        }
    }
}
