package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.CapNhatTrangThaiVanDonRequest;
import vn.fpoly.simplain.dto.LichSuTrangThaiDonHangResponse;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.entity.ChiTietDonHang;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.TrangThaiDonHang;
import vn.fpoly.simplain.repository.ChiTietDonHangRepository;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.repository.TrangThaiDonHangRepository;
import vn.fpoly.simplain.service.LoyaltyHoiVienService;
import vn.fpoly.simplain.service.NhanVienService;
import vn.fpoly.simplain.service.QuanLyVanDonService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller Quản lý Đơn hàng cho Admin (Mod 5 / Đơn hàng - US21, US22).
 * Hỗ trợ tiếp nhận đơn (US21), Cập nhật trạng thái vận đơn & Ghi log lịch sử (US22).
 */
@Slf4j
@Controller
@RequestMapping("/admin/don-hang")
@RequiredArgsConstructor
public class QuanLyDonHangController {

    public static final String COOKIE_ADMIN_TOKEN = "jwt_admin_token";

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final LoyaltyHoiVienService loyaltyHoiVienService;
    private final QuanLyVanDonService quanLyVanDonService;
    private final NhanVienRepository nhanVienRepository;
    private final NhanVienService nhanVienService;

    /**
     * Màn hình danh sách đơn hàng cho Admin (US21).
     */
    @GetMapping("/hien-thi")
    public String hienThiDanhSach(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "maTrangThai", required = false) String maTrangThai,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        String kw = (tuKhoa != null && !tuKhoa.trim().isEmpty()) ? tuKhoa.trim() : null;
        String st = (maTrangThai != null && !maTrangThai.trim().isEmpty()) ? maTrangThai.trim() : null;

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<DonHang> trangDonHang = donHangRepository.timKiemPhanTrang(kw, st, pageable);

        List<TrangThaiDonHang> dsTrangThai = trangThaiDonHangRepository.findAll();

        // Thống kê nhanh
        long tongDonHang = donHangRepository.count();
        long soDonCho = donHangRepository.demTheoMaTrangThai("CHO_XAC_NHAN");
        long soDonHoanThanh = donHangRepository.demTheoMaTrangThai("HOAN_THANH");
        BigDecimal tongDoanhThu = donHangRepository.tinhTongDoanhThuToanHeThong();

        model.addAttribute("trangDonHang", trangDonHang);
        model.addAttribute("dsTrangThai", dsTrangThai);
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("maTrangThai", maTrangThai != null ? maTrangThai : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        model.addAttribute("tongDonHang", tongDonHang);
        model.addAttribute("soDonCho", soDonCho);
        model.addAttribute("soDonHoanThanh", soDonHoanThanh);
        model.addAttribute("tongDoanhThu", tongDoanhThu != null ? tongDoanhThu : BigDecimal.ZERO);

        return "admin/don-hang/hien-thi";
    }

    /**
     * API xem chi tiết mặt hàng của đơn hàng (AJAX Modal).
     */
    @GetMapping("/api/chi-tiet/{id}")
    @ResponseBody
    public List<ChiTietDonHang> layChiTietDonHang(@PathVariable("id") Long id) {
        return chiTietDonHangRepository.findByMaDonHangId(id);
    }

    /**
     * Cập nhật trạng thái đơn hàng & mã vận đơn, ghi log lịch sử (US22 - Mod 5 - Hưng).
     * Khi chuyển sang HOAN_THANH -> Tự động tích lũy Loyalty Coolcash (US16).
     * Khi chuyển sang DA_HUY -> Hoàn trả tồn kho thật (US21).
     */
    @PostMapping("/cap-nhat-trang-thai/{id}")
    public String capNhatTrangThai(
            @PathVariable("id") Long id,
            @RequestParam("maTrangThai") String maTrangThai,
            @RequestParam(name = "maVanDon", required = false) String maVanDon,
            @RequestParam(name = "ghiChu", required = false) String ghiChu,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            NhanVien actor = layNhanVienHienTai(request, session);
            CapNhatTrangThaiVanDonRequest req = CapNhatTrangThaiVanDonRequest.builder()
                    .maTrangThai(maTrangThai)
                    .maVanDon(maVanDon)
                    .ghiChu(ghiChu)
                    .build();

            DonHang donHang = quanLyVanDonService.capNhatTrangThaiVanDon(id, req, actor);

            String thongBao = "Cập nhật đơn hàng #" + donHang.getMaDonHang() + " sang trạng thái ["
                    + donHang.getTrangThaiDonHang().getTenTrangThai() + "]";
            if (donHang.getMaVanDon() != null && !donHang.getMaVanDon().isBlank()) {
                thongBao += " kèm mã vận đơn [" + donHang.getMaVanDon() + "]";
            }
            thongBao += " và đã ghi nhận log lịch sử thành công!";

            redirectAttributes.addFlashAttribute("thongBaoThanhCong", thongBao);
        } catch (Exception e) {
            log.error("Lỗi cập nhật trạng thái đơn hàng ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }

        return "redirect:/admin/don-hang/hien-thi";
    }

    /**
     * API xem Lịch sử thay đổi trạng thái & hành trình vận đơn (Tracking Timeline - US22).
     */
    @GetMapping("/api/lich-su/{id}")
    @ResponseBody
    public List<LichSuTrangThaiDonHangResponse> layLichSuDonHang(@PathVariable("id") Long id) {
        return quanLyVanDonService.layLichSuDonHang(id);
    }

    /**
     * API AJAX Cập nhật trạng thái & mã vận đơn nhanh từ Client (US22).
     */
    @PostMapping("/api/cap-nhat-trang-thai/{id}")
    @ResponseBody
    public ResponseEntity<?> capNhatTrangThaiAjax(
            @PathVariable("id") Long id,
            @RequestBody CapNhatTrangThaiVanDonRequest req,
            HttpServletRequest request,
            HttpSession session) {
        try {
            NhanVien actor = layNhanVienHienTai(request, session);
            DonHang donHang = quanLyVanDonService.capNhatTrangThaiVanDon(id, req, actor);

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Cập nhật trạng thái vận đơn thành công!");
            res.put("donHangId", donHang.getId());
            res.put("maDonHang", donHang.getMaDonHang());
            res.put("maVanDon", donHang.getMaVanDon());
            res.put("maTrangThai", donHang.getTrangThaiDonHang().getMaTrangThai());
            res.put("tenTrangThai", donHang.getTrangThaiDonHang().getTenTrangThai());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * Tiện ích lấy thông tin Nhân viên đang đăng nhập phiên làm việc CMS.
     */
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
        // Fallback tài khoản Quản trị đầu tiên nếu có
        return nhanVienRepository.findAll().stream().findFirst().orElse(null);
    }
}
