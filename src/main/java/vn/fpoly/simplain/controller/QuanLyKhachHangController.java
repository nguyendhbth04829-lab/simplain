package vn.fpoly.simplain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.HoiVienResponse;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.service.LoyaltyHoiVienService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controller quản lý Khách hàng trong CMS Quản trị:
 * Phân loại rõ ràng giữa Khách hàng lưu tại quầy (POS) và Khách hàng đăng ký tài khoản trực tuyến (Online).
 * Hỗ trợ bộ lọc đa tiêu chí, tra cứu điểm thưởng Loyalty Coolcash, xem lịch sử đơn hàng, và bật/tắt trạng thái.
 */
@Slf4j
@Controller
@RequestMapping("/admin/khach-hang")
@RequiredArgsConstructor
public class QuanLyKhachHangController {

    private final KhachHangRepository khachHangRepository;
    private final DonHangRepository donHangRepository;
    private final LoyaltyHoiVienService loyaltyHoiVienService;

    /**
     * Màn hình danh sách và lọc khách hàng theo nguồn (Online / POS) và trạng thái.
     */
    @GetMapping({"", "/", "/hien-thi"})
    public String hienThiDanhSach(
            @RequestParam(name = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(name = "nguon", required = false, defaultValue = "ALL") String nguon,
            @RequestParam(name = "trangThai", required = false) Integer trangThai,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        String nguonChuan = (nguon != null && !nguon.isBlank()) ? nguon.trim().toUpperCase() : "ALL";

        PageRequest pageable = PageRequest.of(Math.max(0, page), Math.max(5, size));
        Page<KhachHang> pageData = khachHangRepository.locKhachHangAdmin(tuKhoaChuan, nguonChuan, trangThai, pageable);

        // Thống kê số lượng theo nhóm
        long tongTatCa = khachHangRepository.count();
        long tongOnline = khachHangRepository.countKhachHangOnline();
        long tongPos = khachHangRepository.countKhachHangPos();
        long tongHoatDong = khachHangRepository.countByTrangThai(1);
        long tongKhoa = khachHangRepository.countByTrangThai(0);

        model.addAttribute("danhSachKhachHang", pageData);
        model.addAttribute("tongTatCa", tongTatCa);
        model.addAttribute("tongOnline", tongOnline);
        model.addAttribute("tongPos", tongPos);
        model.addAttribute("tongHoatDong", tongHoatDong);
        model.addAttribute("tongKhoa", tongKhoa);

        // Tiêu chí tìm kiếm / lọc
        model.addAttribute("tuKhoa", tuKhoaChuan != null ? tuKhoaChuan : "");
        model.addAttribute("nguon", nguonChuan);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("totalElements", pageData.getTotalElements());
        model.addAttribute("menuActive", "khach-hang");

        return "admin/khach-hang/hien-thi";
    }

    /**
     * Chuyển đổi trạng thái hoạt động (Kích hoạt / Tạm khóa) của khách hàng.
     */
    @PostMapping("/doi-trang-thai/{id}")
    public String doiTrangThai(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        KhachHang kh = khachHangRepository.findById(id).orElse(null);
        if (kh == null) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Không tìm thấy khách hàng cần đổi trạng thái!");
            return "redirect:/admin/khach-hang/hien-thi";
        }

        int trangThaiMoi = (kh.getTrangThai() != null && kh.getTrangThai() == 1) ? 0 : 1;
        kh.setTrangThai(trangThaiMoi);
        khachHangRepository.save(kh);

        String trangThaiText = (trangThaiMoi == 1) ? "KÍCH HOẠT" : "TẠM KHÓA";
        redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                "Đã chuyển trạng thái khách hàng [" + kh.getHoTen() + "] sang: " + trangThaiText + " thành công!");
        return "redirect:/admin/khach-hang/hien-thi";
    }

    /**
     * Thêm nhanh khách hàng mới từ trang quản trị CMS.
     */
    @PostMapping("/them-nhanh")
    public String themNhanhKhachHang(
            @RequestParam("hoTen") String hoTen,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "gioiTinh", required = false) String gioiTinh,
            @RequestParam(value = "ngaySinh", required = false) String ngaySinhStr,
            @RequestParam(value = "laTaiQuay", defaultValue = "false") boolean laTaiQuay,
            RedirectAttributes redirectAttributes) {
        try {
            if (hoTen == null || hoTen.isBlank()) {
                throw new IllegalArgumentException("Họ và tên khách hàng không được để trống!");
            }
            if (soDienThoai == null || soDienThoai.isBlank()) {
                throw new IllegalArgumentException("Số điện thoại không được để trống!");
            }

            String sdtRaw = soDienThoai.trim();
            String sdtChuan = sdtRaw.replaceAll("[^0-9]", "");
            if (sdtChuan.startsWith("84") && sdtChuan.length() >= 10) {
                sdtChuan = "0" + sdtChuan.substring(2);
            }
            if (!sdtChuan.matches("^0[35789][0-9]{8}$")) {
                throw new IllegalArgumentException("Số điện thoại '" + sdtRaw + "' không đúng định dạng di động Việt Nam (10 số, đầu 03, 05, 07, 08, 09)!");
            }

            if (khachHangRepository.existsBySoDienThoai(sdtChuan)) {
                throw new IllegalArgumentException("Số điện thoại '" + sdtChuan + "' đã tồn tại trong hệ thống!");
            }

            String emailChuan;
            if (email != null && !email.isBlank()) {
                String emailTrim = email.trim().toLowerCase();
                if (!emailTrim.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    throw new IllegalArgumentException("Địa chỉ email không đúng định dạng chuẩn!");
                }
                if (khachHangRepository.existsByEmail(emailTrim)) {
                    throw new IllegalArgumentException("Địa chỉ email '" + emailTrim + "' đã tồn tại trong hệ thống!");
                }
                emailChuan = emailTrim;
            } else if (laTaiQuay) {
                emailChuan = sdtChuan + "@pos.simplain.vn";
            } else {
                throw new IllegalArgumentException("Vui lòng nhập Email cho khách hàng đăng ký trực tuyến!");
            }

            LocalDate ngaySinh = null;
            if (ngaySinhStr != null && !ngaySinhStr.isBlank()) {
                try {
                    ngaySinh = LocalDate.parse(ngaySinhStr.trim());
                } catch (Exception ignored) {
                }
            }

            String maKh = "KH" + System.currentTimeMillis();
            KhachHang kh = KhachHang.builder()
                    .maKhachHang(maKh)
                    .hoTen(hoTen.trim())
                    .soDienThoai(sdtChuan)
                    .email(emailChuan)
                    .gioiTinh(gioiTinh != null && !gioiTinh.isBlank() ? gioiTinh.trim() : "Nam")
                    .ngaySinh(ngaySinh)
                    .trangThai(1)
                    .ngayTao(LocalDateTime.now())
                    .build();

            khachHangRepository.save(kh);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Thêm mới khách hàng [" + kh.getHoTen() + "] (" + kh.getTenNguonHienThi() + ") thành công!");
        } catch (Exception ex) {
            log.error("Lỗi thêm nhanh khách hàng: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("thongBaoLoi", ex.getMessage());
        }
        return "redirect:/admin/khach-hang/hien-thi";
    }

    /**
     * API xem chi tiết nhanh thông tin khách hàng, thẻ hội viên và các đơn hàng gần nhất.
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> chiTietKhachHangApi(@PathVariable("id") Long id) {
        KhachHang kh = khachHangRepository.findById(id).orElse(null);
        if (kh == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", kh.getId());
        data.put("maKhachHang", kh.getMaKhachHang());
        data.put("hoTen", kh.getHoTen());
        data.put("soDienThoai", kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "--");
        data.put("email", kh.getEmail() != null ? kh.getEmail() : "--");
        data.put("gioiTinh", kh.getGioiTinh() != null ? kh.getGioiTinh() : "--");
        data.put("ngaySinh", kh.getNgaySinh() != null ? kh.getNgaySinh().toString() : "--");
        data.put("trangThai", kh.getTrangThai());
        data.put("laTaiQuay", kh.isTaiQuay());
        data.put("tenNguon", kh.getTenNguonHienThi());
        data.put("ngayTao", kh.getNgayTao() != null ? kh.getNgayTao().toString() : "--");

        // Thông tin Loyalty Hội viên
        String hangThanhVien = "Khách hàng mới";
        BigDecimal soDuCoolcash = BigDecimal.ZERO;
        BigDecimal tongChiTieu = BigDecimal.ZERO;
        try {
            HoiVienResponse hoiVien = loyaltyHoiVienService.layThongTinHoiVien(kh.getId());
            if (hoiVien != null) {
                if (hoiVien.getTenHang() != null) hangThanhVien = hoiVien.getTenHang();
                if (hoiVien.getSoDuCoolcash() != null) soDuCoolcash = hoiVien.getSoDuCoolcash();
                if (hoiVien.getTongChiTieuNam() != null) tongChiTieu = hoiVien.getTongChiTieuNam();
            }
        } catch (Exception ex) {
            log.warn("Không lấy được hồ sơ hội viên cho KH #{}: {}", kh.getId(), ex.getMessage());
        }
        data.put("hangThanhVien", hangThanhVien);
        data.put("soDuCoolcash", soDuCoolcash);
        data.put("tongChiTieu", tongChiTieu);

        // Số đơn hàng và đơn hàng gần đây
        long soDonHang = donHangRepository.countByKhachHangId(kh.getId());
        data.put("soDonHang", soDonHang);

        List<DonHang> topDonHang = donHangRepository.findTop5ByKhachHangIdOrderByIdDesc(kh.getId());
        List<Map<String, Object>> donHangList = new ArrayList<>();
        for (DonHang dh : topDonHang) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", dh.getId());
            item.put("maDonHang", dh.getMaDonHang());
            item.put("ngayTao", dh.getNgayTao() != null ? dh.getNgayTao().toString() : "");
            item.put("tongTien", dh.getTongThanhToan());
            item.put("loaiDonHang", dh.getLoaiDonHang() != null ? dh.getLoaiDonHang() : "ONLINE");
            item.put("tenTrangThai", dh.getTrangThaiDonHang() != null ? dh.getTrangThaiDonHang().getTenTrangThai() : "Đang xử lý");
            donHangList.add(item);
        }
        data.put("danhSachDonHang", donHangList);

        return ResponseEntity.ok(data);
    }
}
