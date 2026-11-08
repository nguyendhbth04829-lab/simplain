package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.CapNhatTrangThaiVanDonRequest;
import vn.fpoly.simplain.dto.LichSuTrangThaiDonHangResponse;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.entity.ChiTietDonHang;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.LichSuTrangThaiDonHang;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.TrangThaiDonHang;
import vn.fpoly.simplain.repository.ChiTietDonHangRepository;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.LichSuTrangThaiDonHangRepository;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.repository.TrangThaiDonHangRepository;
import vn.fpoly.simplain.service.LoyaltyHoiVienService;
import vn.fpoly.simplain.dto.ThongTinVanDonDto;
import vn.fpoly.simplain.service.NhanVienService;
import vn.fpoly.simplain.service.QuanLyVanDonService;
import vn.fpoly.simplain.service.shipping.DonViVanChuyenService;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
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
    private final DonViVanChuyenService donViVanChuyenService;
    private final NhanVienRepository nhanVienRepository;
    private final NhanVienService nhanVienService;
    private final LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;

    /**
     * Màn hình danh sách đơn hàng cho Admin (US21).
     */
    @GetMapping("/hien-thi")
    public String hienThiDanhSach(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "maTrangThai", required = false) String maTrangThai,
            @RequestParam(name = "loaiDonHang", required = false) String loaiDonHang,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        String kw = (tuKhoa != null && !tuKhoa.trim().isEmpty()) ? tuKhoa.trim() : null;
        String st = (maTrangThai != null && !maTrangThai.trim().isEmpty()) ? maTrangThai.trim() : null;
        String ldh = (loaiDonHang != null && !loaiDonHang.trim().isEmpty()) ? loaiDonHang.trim() : null;

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<DonHang> trangDonHang = donHangRepository.timKiemPhanTrang(kw, st, ldh, pageable);

        List<TrangThaiDonHang> dsTrangThai = trangThaiDonHangRepository.findAll();

        // Thống kê nhanh đa kênh (Omnichannel Online & POS)
        long tongDonHang = donHangRepository.count();
        long soDonCho = donHangRepository.demTheoMaTrangThai("CHO_XAC_NHAN");
        long soDonHoanThanh = donHangRepository.demTheoMaTrangThai("HOAN_THANH");
        long soDonOnline = donHangRepository.countByLoaiDonHang("ONLINE");
        long soDonPos = donHangRepository.countByLoaiDonHang("TAI_QUAY");
        BigDecimal tongDoanhThu = donHangRepository.tinhTongDoanhThuToanHeThong();

        model.addAttribute("trangDonHang", trangDonHang);
        model.addAttribute("dsTrangThai", dsTrangThai);
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("maTrangThai", maTrangThai != null ? maTrangThai : "");
        model.addAttribute("loaiDonHang", loaiDonHang != null ? loaiDonHang : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        model.addAttribute("tongDonHang", tongDonHang);
        model.addAttribute("soDonCho", soDonCho);
        model.addAttribute("soDonHoanThanh", soDonHoanThanh);
        model.addAttribute("soDonOnline", soDonOnline);
        model.addAttribute("soDonPos", soDonPos);
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
     * Khi chuyển sang DA_HUY (thông thường) -> Hoàn trả tồn kho thật (US21).
     * Khi chuyển sang HANG_THAT_LAC hoặc tích Hàng bị mất -> Tồn kho KHÔNG được cộng lại.
     */
    @PostMapping("/cap-nhat-trang-thai/{id}")
    public String capNhatTrangThai(
            @PathVariable("id") Long id,
            @RequestParam("maTrangThai") String maTrangThai,
            @RequestParam(name = "maVanDon", required = false) String maVanDon,
            @RequestParam(name = "donViVanChuyen", required = false) String donViVanChuyen,
            @RequestParam(name = "hangBiThatLac", defaultValue = "false") Boolean hangBiThatLac,
            @RequestParam(name = "chiTietBiMatIds", required = false) List<Long> chiTietBiMatIds,
            @RequestParam(name = "ghiChu", required = false) String ghiChu,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            NhanVien actor = layNhanVienHienTai(request, session);
            CapNhatTrangThaiVanDonRequest req = CapNhatTrangThaiVanDonRequest.builder()
                    .maTrangThai(maTrangThai)
                    .maVanDon(maVanDon)
                    .donViVanChuyen(donViVanChuyen)
                    .hangBiThatLac(hangBiThatLac)
                    .chiTietBiMatIds(chiTietBiMatIds)
                    .ghiChu(ghiChu)
                    .build();

            DonHang donHang = quanLyVanDonService.capNhatTrangThaiVanDon(id, req, actor);

            String thongBao = "Cập nhật đơn hàng #" + donHang.getMaDonHang() + " sang trạng thái ["
                    + donHang.getTrangThaiDonHang().getTenTrangThai() + "]";
            if (donHang.getMaVanDon() != null && !donHang.getMaVanDon().isBlank()) {
                thongBao += " kèm mã vận đơn [" + donHang.getMaVanDon() + "]";
            }
            if (chiTietBiMatIds != null && !chiTietBiMatIds.isEmpty()) {
                thongBao += " (Đã đánh dấu " + chiTietBiMatIds.size() + " món bị mất - Tồn kho KHÔNG cộng lại, các món còn lại giữ nguyên)";
            } else if (Boolean.TRUE.equals(hangBiThatLac) || "HANG_THAT_LAC".equalsIgnoreCase(maTrangThai)) {
                thongBao += " (Hàng thất lạc toàn bộ - Tồn kho KHÔNG cộng lại)";
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
     * API lấy danh sách đối tác vận chuyển bên thứ 3 (GHTK, GHN, Viettel Post, VNPost, J&T).
     */
    @GetMapping("/api/danh-sach-doi-tac-van-chuyen")
    @ResponseBody
    public ResponseEntity<?> layDanhSachDoiTacVanChuyen() {
        return ResponseEntity.ok(donViVanChuyenService.layDanhSachDoiTacVanChuyen());
    }

    /**
     * API kết nối ĐVVC bên thứ 3 & cấp mã vận đơn tự động chuẩn API.
     */
    @PostMapping("/api/tao-van-don-carrier/{id}")
    @ResponseBody
    public ResponseEntity<?> taoVanDonCarrier(
            @PathVariable("id") Long id,
            @RequestParam(name = "maDonVi", defaultValue = "GHTK") String maDonVi,
            @RequestParam(name = "loaiDichVu", defaultValue = "Giao tiêu chuẩn") String loaiDichVu,
            HttpServletRequest request,
            HttpSession session) {
        try {
            NhanVien actor = layNhanVienHienTai(request, session);
            ThongTinVanDonDto dto = donViVanChuyenService.taoVanDonTuDong(id, maDonVi, loaiDichVu, actor);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Lỗi cấp mã vận đơn đối tác cho đơn ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("thanhCong", false, "thongDiep", e.getMessage()));
        }
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
     * Admin xác nhận hoàn tiền cho đơn thanh toán trước và gửi bill chuyển khoản cho khách.
     */
    @PostMapping("/xac-nhan-hoan-tien/{id}")
    public String xacNhanHoanTien(
            @PathVariable("id") Long id,
            @RequestParam(name = "maGiaoDichHoanTien", required = false) String maGiaoDich,
            @RequestParam(name = "anhBillUrl", required = false) String anhBillUrl,
            @RequestParam(name = "anhBillFile", required = false) MultipartFile anhBillFile,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            DonHang donHang = donHangRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại!"));

            String billPath = (anhBillUrl != null && !anhBillUrl.isBlank()) ? anhBillUrl.trim() : null;
            if (anhBillFile != null && !anhBillFile.isEmpty()) {
                Path folder = Paths.get("uploads", "refunds");
                if (!Files.exists(folder)) {
                    Files.createDirectories(folder);
                }
                String originalName = anhBillFile.getOriginalFilename();
                String ext = (originalName != null && originalName.contains("."))
                        ? originalName.substring(originalName.lastIndexOf("."))
                        : ".jpg";
                String fileName = "refund_" + id + "_" + System.currentTimeMillis() + ext;
                Files.copy(anhBillFile.getInputStream(), folder.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                billPath = "/uploads/refunds/" + fileName;
            }

            donHang.setAnhBillHoanTien(billPath);
            donHang.setMaGiaoDichHoanTien(maGiaoDich != null ? maGiaoDich.trim() : null);
            donHang.setNgayHoanTien(LocalDateTime.now());
            donHang.setTrangThaiHoanTien("DA_HOAN_TIEN");
            donHangRepository.save(donHang);

            // Ghi log lịch sử hoàn tiền (US35)
            NhanVien actor = layNhanVienHienTai(request, session);
            LichSuTrangThaiDonHang logRefund = LichSuTrangThaiDonHang.builder()
                    .donHang(donHang)
                    .trangThaiMoi(donHang.getTrangThaiDonHang())
                    .nguoiThucHien(actor)
                    .ghiChu("Shop đã hoàn tiền thành công cho các sản phẩm bị thất lạc"
                            + (maGiaoDich != null && !maGiaoDich.isBlank() ? " (Mã GD: " + maGiaoDich.trim() + ")" : "")
                            + ". Quý khách vui lòng kiểm tra tài khoản ngân hàng hoặc xem biên lai chuyển khoản.")
                    .ngayChuyen(LocalDateTime.now())
                    .build();
            lichSuTrangThaiDonHangRepository.save(logRefund);

            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xác nhận hoàn tiền và gửi bill thành công cho khách hàng đơn #" + donHang.getMaDonHang() + "!");
        } catch (Exception e) {
            log.error("Lỗi xác nhận hoàn tiền: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Lỗi hoàn tiền: " + e.getMessage());
        }
        return "redirect:/admin/don-hang/hien-thi";
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
