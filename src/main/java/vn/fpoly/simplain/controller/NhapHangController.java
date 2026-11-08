package vn.fpoly.simplain.controller;

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
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;
import vn.fpoly.simplain.service.BienTheSanPhamService;
import vn.fpoly.simplain.service.NhapHangService;
import vn.fpoly.simplain.service.SanPhamService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controller Quản lý Nhập hàng từ Nhà cung cấp.
 * Hỗ trợ quét mã vạch Barcode từ các nhà cung cấp lớn chính hãng.
 */
@Slf4j
@Controller
@RequestMapping("/admin/nhap-hang")
@RequiredArgsConstructor
public class NhapHangController {

    private final NhapHangService nhapHangService;
    private final SanPhamService sanPhamService;
    private final BienTheSanPhamService bienTheSanPhamService;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;
    private final vn.fpoly.simplain.repository.MauSacRepository mauSacRepository;
    private final vn.fpoly.simplain.repository.KichThuocRepository kichThuocRepository;

    @GetMapping({"", "/"})
    public String chuyenHuong() {
        return "redirect:/admin/nhap-hang/hien-thi";
    }

    /**
     * Màn hình danh sách Phiếu nhập hàng.
     */
    @GetMapping("/hien-thi")
    public String hienThiDanhSach(Model model,
                                 @RequestParam(defaultValue = "0") Integer page,
                                 @RequestParam(required = false) String tuKhoa,
                                 @RequestParam(required = false) Long maNhaCungCapId,
                                 @RequestParam(required = false) String trangThai) {
        if (page == null || page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        Page<PhieuNhapHang> phieuPage = nhapHangService.layDanhSachPhieuNhap(
                tuKhoa, maNhaCungCapId, trangThai, null, null, pageable);

        model.addAttribute("listPhieuNhap", phieuPage.getContent());
        model.addAttribute("pageData", phieuPage);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", phieuPage.getTotalPages());
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("maNhaCungCapLoc", maNhaCungCapId);
        model.addAttribute("trangThaiLoc", trangThai != null ? trangThai : "");
        model.addAttribute("listNhaCungCap", nhapHangService.layDanhSachNhaCungCapDangHoatDong());
        model.addAttribute("thongKe", nhapHangService.layThongKeTongQuanNhapHang());

        return "admin/nhap-hang/index";
    }

    /**
     * Màn hình Tạo phiếu nhập hàng mới (hỗ trợ máy quét mã vạch và chọn SKU).
     */
    @GetMapping("/tao-phieu")
    public String manHinhTaoPhieu(Model model,
                                  @RequestParam(required = false) Long nccId,
                                  @RequestParam(required = false) Long sanPhamId) {
        nhapHangService.khoiTaoNhaCungCapChinhHangMacDinh();

        if (nccId != null) {
            Optional<NhaCungCap> optNcc = nhapHangService.layNhaCungCapTheoId(nccId);
            if (optNcc.isPresent() && !Boolean.TRUE.equals(optNcc.get().getKichHoat())) {
                model.addAttribute("thongBaoLoi",
                        "Nhà cung cấp [" + optNcc.get().getTenNhaCungCap() + "] đã NGƯNG HỢP TÁC, không thể tạo phiếu nhập hàng!");
                nccId = null;
            }
        }

        model.addAttribute("listNhaCungCap", nhapHangService.layDanhSachNhaCungCapDangHoatDong());
        model.addAttribute("listSanPham", sanPhamService.layTatCa());
        model.addAttribute("nccDuocChon", nccId);
        model.addAttribute("sanPhamKhoiTao", sanPhamId);
        return "admin/nhap-hang/tao-phieu";
    }

    /**
     * API Quét mã vạch Barcode / QR (từ máy quét mã vạch cầm tay hoặc nhập tay).
     * Nhận diện ngay SKU sản phẩm, màu sắc, size, tồn kho và thông tin nhà cung cấp.
     */
    @GetMapping("/api/quet-barcode")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiQuetBarcode(@RequestParam String maQuet) {
        Map<String, Object> res = new HashMap<>();
        Optional<BienTheSanPham> opt = nhapHangService.quetMaVachBienThe(maQuet);

        if (opt.isPresent()) {
            BienTheSanPham bt = opt.get();
            res.put("found", true);
            res.put("bienTheId", bt.getId());
            res.put("maSku", bt.getMaSku());
            res.put("maVach", bt.getMaVachBarcode());

            String tenMau = bt.getTenMauSac();
            if (tenMau == null && bt.getMaMauSacId() != null) {
                tenMau = mauSacRepository.findById(bt.getMaMauSacId()).map(MauSac::getTenMau).orElse("Màu #" + bt.getMaMauSacId());
            }
            res.put("tenMauSac", tenMau);

            String tenSize = bt.getMaKichThuoc();
            if (tenSize == null && bt.getMaKichThuocId() != null) {
                tenSize = kichThuocRepository.findById(bt.getMaKichThuocId()).map(KichThuoc::getMaKichThuoc).orElse("Size #" + bt.getMaKichThuocId());
            }
            res.put("maKichThuoc", tenSize);

            res.put("tonKhoHienTai", bt.getSoLuong());
            res.put("giaBan", bt.getGiaBan());
            res.put("giaGocGoiY", bt.getGiaGoc() != null ? bt.getGiaGoc() : bt.getGiaBan().multiply(BigDecimal.valueOf(0.6)));

            sanPhamRepository.findById(bt.getMaSanPhamId()).ifPresent(sp -> {
                res.put("tenSanPham", sp.getTenSanPham());
                res.put("maSanPham", sp.getMaSanPham());
                res.put("anhUrl", sp.getAnhDaiDienUrl());
                res.put("chatLieu", sp.getChatLieu());
                res.put("formDang", sp.getFormDang());
            });
            return ResponseEntity.ok(res);
        }

        res.put("found", false);
        res.put("message", "Không tìm thấy sản phẩm nào có mã vạch / SKU: " + maQuet);
        return ResponseEntity.ok(res);
    }

    /**
     * API Lấy danh sách biến thể theo Sản phẩm (để chọn nhanh khi nhập hàng).
     */
    @GetMapping("/api/bien-the-theo-san-pham")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> apiBienTheTheoSanPham(@RequestParam Long sanPhamId) {
        List<BienTheSanPham> list = bienTheSanPhamRepository.findByMaSanPhamIdOrderByIdDesc(sanPhamId);
        Map<Long, MauSac> mapMau = mauSacRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(MauSac::getId, m -> m, (a, b) -> a));
        Map<Long, KichThuoc> mapSize = kichThuocRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(KichThuoc::getId, k -> k, (a, b) -> a));

        List<Map<String, Object>> result = new ArrayList<>();
        for (BienTheSanPham bt : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", bt.getId());
            item.put("maSku", bt.getMaSku());
            item.put("maVach", bt.getMaVachBarcode());
            MauSac ms = mapMau.get(bt.getMaMauSacId());
            item.put("tenMauSac", ms != null ? ms.getTenMau() : "Màu #" + bt.getMaMauSacId());
            KichThuoc kt = mapSize.get(bt.getMaKichThuocId());
            item.put("maKichThuoc", kt != null ? kt.getMaKichThuoc() : "Size #" + bt.getMaKichThuocId());
            item.put("soLuong", bt.getSoLuong());
            item.put("giaBan", bt.getGiaBan());
            item.put("giaGoc", bt.getGiaGoc());
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Xử lý Lưu Phiếu Nhập Hàng.
     */
    @PostMapping("/luu-phieu")
    public String luuPhieuNhap(@RequestParam Long maNhaCungCapId,
                               @RequestParam(required = false) String ghiChu,
                               @RequestParam(defaultValue = "false") Boolean hoanTatNgay,
                               @RequestParam("bienTheIds") List<Long> bienTheIds,
                               @RequestParam("soLuongs") List<Integer> soLuongs,
                               @RequestParam("giaNhaps") List<BigDecimal> giaNhaps,
                               @RequestParam(value = "maVachs", required = false) List<String> maVachs,
                               RedirectAttributes redirectAttributes) {
        try {
            if (bienTheIds == null || bienTheIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng chọn hoặc quét ít nhất một mặt hàng để nhập!");
                return "redirect:/admin/nhap-hang/tao-phieu";
            }

            PhieuNhapHang phieu = PhieuNhapHang.builder()
                    .maNhaCungCapId(maNhaCungCapId)
                    .ghiChu(ghiChu)
                    .build();

            List<ChiTietPhieuNhapHang> chiTietList = new ArrayList<>();
            for (int i = 0; i < bienTheIds.size(); i++) {
                Long btId = bienTheIds.get(i);
                Integer sl = soLuongs.get(i);
                BigDecimal gia = giaNhaps.get(i);
                String mv = (maVachs != null && i < maVachs.size()) ? maVachs.get(i) : null;

                chiTietList.add(ChiTietPhieuNhapHang.builder()
                        .maBienTheId(btId)
                        .soLuongNhap(sl)
                        .giaNhap(gia)
                        .maVachBarcode(mv)
                        .build());
            }

            PhieuNhapHang saved = nhapHangService.taoPhieuNhap(phieu, chiTietList, Boolean.TRUE.equals(hoanTatNgay));

            String msg = "Tạo phiếu nhập [" + saved.getMaPhieuNhap() + "] thành công!";
            if (Boolean.TRUE.equals(hoanTatNgay)) {
                msg += " Toàn bộ số lượng đã được cộng ngay vào tồn kho các biến thể.";
            }
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", msg);
            return "redirect:/admin/nhap-hang/chi-tiet?id=" + saved.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/nhap-hang/tao-phieu";
        }
    }

    /**
     * Màn hình Xem chi tiết Phiếu nhập hàng.
     */
    @GetMapping("/chi-tiet")
    public String xemChiTietPhieu(@RequestParam Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PhieuNhapHang> opt = nhapHangService.layPhieuNhapTheoId(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Không tìm thấy phiếu nhập có ID: " + id);
            return "redirect:/admin/nhap-hang/hien-thi";
        }
        model.addAttribute("phieu", opt.get());
        return "admin/nhap-hang/chi-tiet";
    }

    /**
     * Duyệt hoàn tất nhập hàng (cộng tồn kho).
     */
    @PostMapping("/hoan-tat")
    public String hoanTatPhieu(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            PhieuNhapHang p = nhapHangService.hoanTatPhieuNhap(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã hoàn tất nhập kho cho phiếu [" + p.getMaPhieuNhap() + "]! Tồn kho đã được cộng tự động.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/nhap-hang/chi-tiet?id=" + id;
    }

    /**
     * Hủy phiếu nhập hàng.
     */
    @PostMapping("/huy")
    public String huyPhieu(@RequestParam Long id,
                           @RequestParam(required = false) String lyDoHuy,
                           RedirectAttributes redirectAttributes) {
        try {
            PhieuNhapHang p = nhapHangService.huyPhieuNhap(id, lyDoHuy);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã hủy phiếu nhập [" + p.getMaPhieuNhap() + "].");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/nhap-hang/hien-thi";
    }

    // =========================================================================
    // QUẢN LÝ DANH MỤC NHÀ CUNG CẤP
    // =========================================================================

    @GetMapping("/nha-cung-cap")
    public String danhSachNhaCungCap(@RequestParam(required = false) String q,
                                     @RequestParam(required = false, defaultValue = "TAT_CA") String trangThai,
                                     Model model) {
        nhapHangService.khoiTaoNhaCungCapChinhHangMacDinh();
        model.addAttribute("listNhaCungCap", nhapHangService.timKiemNhaCungCap(q, trangThai));
        model.addAttribute("tuKhoa", q != null ? q : "");
        model.addAttribute("trangThaiHienTai", trangThai);
        model.addAttribute("demTatCa", nhapHangService.demNhaCungCapTatCa());
        model.addAttribute("demDangHopTac", nhapHangService.demNhaCungCapDangHopTac());
        model.addAttribute("demNgungHopTac", nhapHangService.demNhaCungCapNgungHopTac());
        if (!model.containsAttribute("nccForm")) {
            model.addAttribute("nccForm", new NhaCungCap());
        }
        return "admin/nhap-hang/nha-cung-cap";
    }

    @PostMapping("/nha-cung-cap/luu")
    public String luuNhaCungCap(@ModelAttribute("nccForm") NhaCungCap ncc, RedirectAttributes redirectAttributes) {
        try {
            nhapHangService.luuNhaCungCap(ncc);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Lưu thông tin nhà cung cấp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/nhap-hang/nha-cung-cap";
    }

    /**
     * Chuyển đổi trạng thái Hợp tác / Ngưng hợp tác với Nhà cung cấp (GET redirect).
     */
    @GetMapping("/nha-cung-cap/doi-trang-thai")
    public String doiTrangThaiNhaCungCap(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            NhaCungCap ncc = nhapHangService.doiTrangThaiNhaCungCap(id);
            String trangThaiText = Boolean.TRUE.equals(ncc.getKichHoat()) ? "ĐANG HỢP TÁC" : "NGƯNG HỢP TÁC";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái nhà cung cấp [" + ncc.getTenNhaCungCap() + "] sang: " + trangThaiText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/nhap-hang/nha-cung-cap";
    }

    /**
     * API JSON Chuyển đổi trạng thái nhanh bằng AJAX (phục vụ giao diện hiện đại).
     */
    @PostMapping("/nha-cung-cap/api/doi-trang-thai")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiDoiTrangThaiNhaCungCap(@RequestParam Long id) {
        Map<String, Object> resp = new HashMap<>();
        try {
            NhaCungCap ncc = nhapHangService.doiTrangThaiNhaCungCap(id);
            resp.put("success", true);
            resp.put("kichHoat", ncc.getKichHoat());
            resp.put("tenNcc", ncc.getTenNhaCungCap());
            resp.put("trangThaiText", Boolean.TRUE.equals(ncc.getKichHoat()) ? "Đang hợp tác" : "Ngưng hợp tác");
            resp.put("message", "Đã " + (Boolean.TRUE.equals(ncc.getKichHoat()) ? "khôi phục hợp tác với" : "ngưng hợp tác với")
                    + " nhà cung cấp [" + ncc.getTenNhaCungCap() + "]!");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }
}
