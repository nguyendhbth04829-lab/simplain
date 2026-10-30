package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.fpoly.simplain.dto.*;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.PhuongThucThanhToan;
import vn.fpoly.simplain.exception.TrungSoDienThoaiException;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.repository.PhuongThucThanhToanRepository;
import vn.fpoly.simplain.service.BanHangTaiQuayService;
import vn.fpoly.simplain.service.NhanVienService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller Quản lý Bán hàng trực tiếp tại quầy (POS) cho SIMPLAIN STORE.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class BanHangTaiQuayController {

    public static final String COOKIE_ADMIN_TOKEN = "jwt_admin_token";

    private final BanHangTaiQuayService banHangTaiQuayService;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final NhanVienService nhanVienService;
    private final vn.fpoly.simplain.service.LoyaltyHoiVienService loyaltyHoiVienService;

    /**
     * Màn hình Bán hàng tại quầy (POS Fullscreen Workspace).
     */
    @GetMapping({"/admin/ban-hang-tai-quay", "/admin/pos"})
    public String hienThiManHinhPos(Model model, HttpServletRequest request, HttpSession session) {
        NhanVien actor = layNhanVienHienTai(request, session);
        List<PosHoaDonChoDto> dsHoaDonCho = banHangTaiQuayService.layDanhSachHoaDonCho();

        // Lấy phương thức thanh toán tại quầy: Tiền mặt, Chuyển khoản QR
        List<PhuongThucThanhToan> dsPttt = phuongThucThanhToanRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getKichHoat()))
                .filter(p -> "TIEN_MAT".equalsIgnoreCase(p.getMaPhuongThuc()) ||
                             "CHUYEN_KHOAN".equalsIgnoreCase(p.getMaPhuongThuc()) ||
                             "VNPAY_QR".equalsIgnoreCase(p.getMaPhuongThuc()) ||
                             "MOMO_WALLET".equalsIgnoreCase(p.getMaPhuongThuc()) ||
                             "COD".equalsIgnoreCase(p.getMaPhuongThuc()))
                .toList();

        model.addAttribute("dsHoaDonCho", dsHoaDonCho);
        model.addAttribute("dsPttt", dsPttt);
        model.addAttribute("nhanVienHienTai", actor);
        model.addAttribute("menuActive", "pos");

        return "admin/ban-hang-tai-quay/index";
    }

    // =========================================================================
    // REST APIs CHO GIAO DIỆN POS THỜI GIAN THỰC (AJAX / FETCH)
    // =========================================================================

    /**
     * Lấy danh sách tất cả các tab hóa đơn chờ.
     */
    @GetMapping("/admin/ban-hang-tai-quay/api/hoa-don-cho")
    @ResponseBody
    public ResponseEntity<?> layDanhSachHoaDonCho() {
        return ResponseEntity.ok(banHangTaiQuayService.layDanhSachHoaDonCho());
    }

    /**
     * Tạo thêm tab hóa đơn chờ mới.
     */
    @PostMapping("/admin/ban-hang-tai-quay/api/hoa-don-cho/tao")
    @ResponseBody
    public ResponseEntity<?> taoHoaDonChoMoi(HttpServletRequest request, HttpSession session) {
        try {
            NhanVien actor = layNhanVienHienTai(request, session);
            PosHoaDonChoDto hdc = banHangTaiQuayService.taoHoaDonChoMoi(actor);
            return ResponseEntity.ok(hdc);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(taoErrorMap(e.getMessage()));
        }
    }

    /**
     * Đóng / Hủy tab hóa đơn chờ.
     */
    @DeleteMapping("/admin/ban-hang-tai-quay/api/hoa-don-cho/{id}")
    @ResponseBody
    public ResponseEntity<?> dongHoaDonCho(@PathVariable("id") Long id) {
        try {
            List<PosHoaDonChoDto> ds = banHangTaiQuayService.dongHoaDonCho(id);
            return ResponseEntity.ok(ds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(taoErrorMap(e.getMessage()));
        }
    }

    /**
     * Thêm mặt hàng vào tab hóa đơn chờ.
     */
    @PostMapping("/admin/ban-hang-tai-quay/api/them-mat-hang")
    @ResponseBody
    public ResponseEntity<?> themMatHang(@RequestBody PosThemMatHangRequest req) {
        try {
            PosHoaDonChoDto updated = banHangTaiQuayService.themMatHangVaoHoaDonCho(req);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(taoErrorMap(e.getMessage()));
        }
    }

    /**
     * Cập nhật số lượng của dòng mặt hàng trong tab.
     */
    @PostMapping("/admin/ban-hang-tai-quay/api/cap-nhat-so-luong")
    @ResponseBody
    public ResponseEntity<?> capNhatSoLuong(@RequestBody PosCapNhatSoLuongRequest req) {
        try {
            PosHoaDonChoDto updated = banHangTaiQuayService.capNhatSoLuong(req);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(taoErrorMap(e.getMessage()));
        }
    }

    /**
     * Xóa một dòng mặt hàng khỏi tab.
     */
    @DeleteMapping("/admin/ban-hang-tai-quay/api/chi-tiet/{id}")
    @ResponseBody
    public ResponseEntity<?> xoaMatHang(@PathVariable("id") Long id) {
        try {
            PosHoaDonChoDto updated = banHangTaiQuayService.xoaMatHang(id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(taoErrorMap(e.getMessage()));
        }
    }

    /**
     * Cập nhật thông tin khách hàng cho hóa đơn chờ.
     */
    @PostMapping("/admin/ban-hang-tai-quay/api/cap-nhat-khach-hang")
    @ResponseBody
    public ResponseEntity<?> capNhatKhachHang(
            @RequestParam("hoaDonChoId") Long hoaDonChoId,
            @RequestParam(name = "khachHangId", required = false) Long khachHangId,
            @RequestParam(name = "tenKhachHang", required = false) String tenKhachHang,
            @RequestParam(name = "soDienThoai", required = false) String soDienThoai) {
        try {
            PosHoaDonChoDto updated = banHangTaiQuayService.capNhatKhachHangChoHoaDon(hoaDonChoId, khachHangId, tenKhachHang, soDienThoai);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(taoErrorMap(e.getMessage()));
        }
    }

    /**
     * Tìm kiếm sản phẩm / biến thể SKU hoặc quét mã vạch Barcode.
     */
    @GetMapping("/admin/ban-hang-tai-quay/api/san-pham")
    @ResponseBody
    public ResponseEntity<?> timKiemSanPham(@RequestParam(name = "q", required = false) String q) {
        try {
            List<PosItemDto> ds = banHangTaiQuayService.timKiemBienTheChoPos(q);
            return ResponseEntity.ok(ds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(taoErrorMap(e.getMessage()));
        }
    }

    /**
     * Tìm kiếm khách hàng theo Tên, SĐT, Mã KH.
     */
    @GetMapping("/admin/ban-hang-tai-quay/api/khach-hang")
    @ResponseBody
    public ResponseEntity<?> timKiemKhachHang(@RequestParam(name = "q", required = false) String q) {
        try {
            List<KhachHang> ds = banHangTaiQuayService.timKiemKhachHangChoPos(q);
            return ResponseEntity.ok(ds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(taoErrorMap(e.getMessage()));
        }
    }

    /**
     * Tạo nhanh khách hàng mới ngay trên giao diện POS.
     * Kiểm tra chặt chẽ số điện thoại không được trùng lặp.
     * Nếu trùng, trả về mã lỗi 400 và dữ liệu chi tiết khách hàng cũ để thu ngân chọn ngay.
     */
    @PostMapping("/admin/ban-hang-tai-quay/api/khach-hang/tao-nhanh")
    @ResponseBody
    public ResponseEntity<?> taoKhachHangNhanh(@RequestBody PosTaoKhachHangNhanhRequest req) {
        try {
            KhachHang kh = banHangTaiQuayService.taoKhachHangNhanh(req);
            return ResponseEntity.ok(kh);
        } catch (TrungSoDienThoaiException e) {
            Map<String, Object> err = taoErrorMap(e.getMessage());
            err.put("trungSoDienThoai", true);
            if (e.getKhachHangId() != null) {
                Map<String, Object> khCu = new HashMap<>();
                khCu.put("id", e.getKhachHangId());
                khCu.put("maKhachHang", e.getMaKhachHang());
                khCu.put("hoTen", e.getHoTen());
                khCu.put("soDienThoai", e.getSoDienThoai());
                err.put("khachHangTonTai", khCu);
            }
            return ResponseEntity.badRequest().body(err);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(taoErrorMap(e.getMessage()));
        }
    }

    /**
     * Tra cứu thông tin điểm tích lũy Coolcash và hạng hội viên của khách hàng tại quầy.
     */
    @GetMapping("/admin/ban-hang-tai-quay/api/khach-hang/{id}/diem")
    @ResponseBody
    public ResponseEntity<?> layThongTinDiemKhachHang(@PathVariable("id") Long id) {
        try {
            vn.fpoly.simplain.dto.HoiVienResponse info = loyaltyHoiVienService.layThongTinHoiVien(id);
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(taoErrorMap(e.getMessage()));
        }
    }

    /**
     * Thực hiện thanh toán & hoàn thành đơn hàng tại quầy (In hóa đơn).
     */
    @PostMapping("/admin/ban-hang-tai-quay/api/thanh-toan")
    @ResponseBody
    public ResponseEntity<?> thanhToan(
            @RequestBody PosThanhToanRequest req,
            HttpServletRequest request,
            HttpSession session) {
        try {
            NhanVien actor = layNhanVienHienTai(request, session);
            PosThanhToanResponse res = banHangTaiQuayService.thanhToan(req, actor);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Lỗi thanh toán POS: {}", e.getMessage());
            return ResponseEntity.badRequest().body(taoErrorMap(e.getMessage()));
        }
    }

    // =========================================================================
    // TIỆN ÍCH
    // =========================================================================

    private Map<String, Object> taoErrorMap(String message) {
        Map<String, Object> err = new HashMap<>();
        err.put("success", false);
        err.put("error", message);
        err.put("message", message);
        return err;
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
