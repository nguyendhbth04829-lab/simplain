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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import vn.fpoly.simplain.dto.ThemNhanhBienTheRequest;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.service.BienTheSanPhamService;
import vn.fpoly.simplain.service.KichThuocService;
import vn.fpoly.simplain.service.MauSacService;
import vn.fpoly.simplain.service.SanPhamService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller MVC Quản lý Biến thể sản phẩm (SKU), Tồn kho, Giá bán/Giá gốc và Upload nhiều hình ảnh (US09).
 *
 * <p>Đáp ứng tiêu chí US09:
 * 1. Map Màu + Size tạo bien_the_san_pham.
 * 2. Quản lý Giá bán / Giá gốc, Tồn kho (hỗ trợ cập nhật nhanh tồn kho & giá ngay trên bảng).
 * 3. Upload nhiều hinh_anh cho từng biến thể SKU, chọn ảnh đại diện và xóa ảnh.
 */
@Controller
@RequestMapping("/admin/bien-the")
public class BienTheSanPhamController {

    private final BienTheSanPhamService bienTheService;
    private final SanPhamService sanPhamService;
    private final MauSacService mauSacService;
    private final KichThuocService kichThuocService;

    @Autowired
    public BienTheSanPhamController(BienTheSanPhamService bienTheService,
                                    SanPhamService sanPhamService,
                                    MauSacService mauSacService,
                                    KichThuocService kichThuocService) {
        this.bienTheService = bienTheService;
        this.sanPhamService = sanPhamService;
        this.mauSacService = mauSacService;
        this.kichThuocService = kichThuocService;
    }

    @GetMapping({"", "/"})
    public String chuyenHuongMacDinh() {
        return "redirect:/admin/bien-the/hien-thi";
    }

    /**
     * Hiển thị danh sách biến thể SKU có phân trang, tìm kiếm và lọc theo sản phẩm, màu sắc, kích thước, trạng thái.
     */
    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @RequestParam(defaultValue = "0") Integer page,
                          @RequestParam(required = false) String tuKhoa,
                          @RequestParam(required = false) Long maSanPhamId,
                          @RequestParam(required = false) Long maMauSacId,
                          @RequestParam(required = false) Long maKichThuocId,
                          @RequestParam(required = false) Boolean kichHoat) {
        if (page == null || page < 0) {
            page = 0;
        }
        int size = 6;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<BienTheSanPham> bienThePage = bienTheService.layDanhSachPhanTrang(
                tuKhoa, maSanPhamId, maMauSacId, maKichThuocId, kichHoat, pageable);

        model.addAttribute("listBienThe", bienThePage.getContent());
        model.addAttribute("pageData", bienThePage);
        model.addAttribute("listSanPham", sanPhamService.layTatCa());
        model.addAttribute("listMauSac", mauSacService.layTatCa());
        model.addAttribute("listKichThuoc", kichThuocService.layTatCa());

        if (!model.containsAttribute("bienThe")) {
            BienTheSanPham formMacDinh = new BienTheSanPham();
            if (maSanPhamId != null) {
                formMacDinh.setMaSanPhamId(maSanPhamId);
            }
            model.addAttribute("bienThe", formMacDinh);
        }

        model.addAttribute("tongBienThe", bienTheService.demTongBienThe());
        model.addAttribute("tongTonKho", bienTheService.tinhTongTonKho());
        model.addAttribute("soSkuSapHetHang", bienTheService.demSkuSapHetHang());
        model.addAttribute("tongHinhAnh", bienTheService.demTongHinhAnh());

        model.addAttribute("page", page);
        model.addAttribute("totalPages", bienThePage.getTotalPages());
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("maSanPhamLoc", maSanPhamId);
        model.addAttribute("maMauSacLoc", maMauSacId);
        model.addAttribute("maKichThuocLoc", maKichThuocId);
        model.addAttribute("kichHoatLoc", kichHoat);

        return "admin/bien-the/hien-thi";
    }

    /**
     * Xử lý tạo mới biến thể SKU (Map Sản phẩm + Màu sắc + Kích thước), nhập Giá bán/Giá gốc, Tồn kho và Upload nhiều hình ảnh.
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("bienThe") BienTheSanPham bienThe,
                      BindingResult result,
                      @RequestParam(value = "filesAnh", required = false) MultipartFile[] filesAnh,
                      @RequestParam(value = "duongDanAnhUrl", required = false) String duongDanAnhUrl,
                      Model model,
                      RedirectAttributes redirectAttributes,
                      @RequestParam(defaultValue = "0") Integer page) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu biến thể SKU chưa hợp lệ, vui lòng kiểm tra lại các trường báo đỏ!");
            return hienThi(model, page, null, null, null, null, null);
        }

        try {
            BienTheSanPham saved = bienTheService.them(bienThe, filesAnh, duongDanAnhUrl);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Tạo mới biến thể SKU [" + saved.getMaSku() + "] (" + saved.getTenMauSac() + " - " +
                    saved.getMaKichThuoc() + ") kèm " + saved.getDanhSachHinhAnh().size() + " hình ảnh thành công!");
            return "redirect:/admin/bien-the/hien-thi";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return hienThi(model, page, null, null, null, null, null);
        }
    }

    /**
     * Hiển thị màn hình chi tiết, chỉnh sửa biến thể SKU và quản lý bộ sưu tập nhiều hình ảnh.
     */
    @GetMapping("/view")
    public String view(Model model,
                       @RequestParam Long id,
                       RedirectAttributes redirectAttributes) {
        try {
            BienTheSanPham bt = bienTheService.layTheoId(id);
            model.addAttribute("bienThe", bt);
            model.addAttribute("listSanPham", sanPhamService.layTatCa());
            model.addAttribute("listMauSac", mauSacService.layTatCa());
            model.addAttribute("listKichThuoc", kichThuocService.layTatCa());
            return "admin/bien-the/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/bien-the/hien-thi";
        }
    }

    /**
     * Xử lý cập nhật biến thể SKU, Giá bán/Giá gốc, Tồn kho và tải thêm nhiều hình ảnh.
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("bienThe") BienTheSanPham bienThe,
                         BindingResult result,
                         @RequestParam(value = "filesAnh", required = false) MultipartFile[] filesAnh,
                         @RequestParam(value = "duongDanAnhUrl", required = false) String duongDanAnhUrl,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu cập nhật biến thể SKU chưa hợp lệ, vui lòng kiểm tra lại!");
            napDuLieuPhuChoTrangView(model, bienThe.getId());
            return "admin/bien-the/view";
        }

        try {
            BienTheSanPham updated = bienTheService.sua(bienThe.getId(), bienThe, filesAnh, duongDanAnhUrl);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật biến thể SKU [" + updated.getMaSku() + "] thành công!");
            return "redirect:/admin/bien-the/hien-thi";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            napDuLieuPhuChoTrangView(model, bienThe.getId());
            return "admin/bien-the/view";
        }
    }

    /**
     * Cập nhật nhanh Số lượng tồn kho & Giá bán/Giá gốc ngay trên bảng danh sách (US09 Tiêu chí 2).
     */
    @PostMapping("/cap-nhat-ton-kho")
    public String capNhatTonKho(@RequestParam Long id,
                                @RequestParam Integer soLuong,
                                @RequestParam BigDecimal giaBan,
                                @RequestParam(required = false) BigDecimal giaGoc,
                                RedirectAttributes redirectAttributes) {
        try {
            BienTheSanPham bt = bienTheService.capNhatNhanhTonKhoVaGia(id, soLuong, giaBan, giaGoc);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã cập nhật tồn kho & giá bán cho SKU [" + bt.getMaSku() + "]: Tồn kho = " + bt.getSoLuong());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/bien-the/hien-thi";
    }

    /**
     * Upload bổ sung nhiều hình ảnh trực tiếp trong màn hình chi tiết biến thể SKU (US09 Tiêu chí 3).
     */
    @PostMapping("/upload-anh")
    public String uploadAnh(@RequestParam Long id,
                            @RequestParam(value = "filesAnh", required = false) MultipartFile[] filesAnh,
                            @RequestParam(value = "duongDanAnhUrl", required = false) String duongDanAnhUrl,
                            RedirectAttributes redirectAttributes) {
        try {
            int soAnh = bienTheService.uploadThemNhieuHinhAnh(id, filesAnh, duongDanAnhUrl);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã tải lên thành công " + soAnh + " hình ảnh mới cho biến thể SKU!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/bien-the/view?id=" + id;
    }

    /**
     * Chọn một hình ảnh làm Ảnh đại diện chính của biến thể SKU.
     */
    @GetMapping("/chon-anh-dai-dien")
    public String chonAnhDaiDien(@RequestParam Long hinhAnhId,
                                 @RequestParam Long bienTheId,
                                 RedirectAttributes redirectAttributes) {
        try {
            bienTheService.chonAnhDaiDien(hinhAnhId);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã đặt ảnh đại diện chính cho biến thể SKU!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/bien-the/view?id=" + bienTheId;
    }

    /**
     * Xóa một hình ảnh khỏi biến thể SKU.
     */
    @GetMapping("/xoa-anh")
    public String xoaAnh(@RequestParam Long hinhAnhId,
                         @RequestParam Long bienTheId,
                         RedirectAttributes redirectAttributes) {
        try {
            bienTheService.xoaHinhAnh(hinhAnhId);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa hình ảnh khỏi biến thể SKU!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/bien-the/view?id=" + bienTheId;
    }

    /**
     * Đổi nhanh trạng thái Kích hoạt (Đang mở bán / Tạm ngừng) của biến thể SKU.
     */
    @GetMapping("/doi-trang-thai")
    public String doiTrangThai(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            BienTheSanPham bt = bienTheService.doiTrangThai(id);
            String trangThaiText = Boolean.TRUE.equals(bt.getKichHoat()) ? "ĐANG MỞ BÁN" : "TẠM NGỪNG";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái biến thể SKU [" + bt.getMaSku() + "] sang: " + trangThaiText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/bien-the/hien-thi";
    }

    /**
     * Xóa biến thể SKU (Bảo vệ toàn vẹn nếu SKU đã phát sinh trong đơn hàng, giỏ hàng, đổi trả hoặc Flash Sale).
     */
    @GetMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            bienTheService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Xóa biến thể sản phẩm (SKU) thành công!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/bien-the/hien-thi";
    }

    private void napDuLieuPhuChoTrangView(Model model, Long bienTheId) {
        model.addAttribute("listSanPham", sanPhamService.layTatCa());
        model.addAttribute("listMauSac", mauSacService.layTatCa());
        model.addAttribute("listKichThuoc", kichThuocService.layTatCa());
        if (bienTheId != null) {
            try {
                BienTheSanPham hienTai = bienTheService.layTheoId(bienTheId);
                BienTheSanPham formModel = (BienTheSanPham) model.getAttribute("bienThe");
                if (formModel != null) {
                    formModel.setDanhSachHinhAnh(hienTai.getDanhSachHinhAnh());
                    formModel.setAnhDaiDienUrl(hienTai.getAnhDaiDienUrl());
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Thao tác hàng loạt trên danh sách ID biến thể SKU được chọn (Mở bán hoặc Tạm ngừng).
     */
    @PostMapping("/thao-tac-hang-loat")
    public String thaoTacHangLoat(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "hanhDong") String hanhDong,
                                  RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng tích chọn ít nhất một biến thể SKU để thao tác hàng loạt!");
            return "redirect:/admin/bien-the/hien-thi";
        }

        boolean batHoatDong = "KICH_HOAT".equalsIgnoreCase(hanhDong);
        int soLuong = 0;
        for (Long id : ids) {
            try {
                BienTheSanPham bt = bienTheService.layTheoId(id);
                if (bt != null && !Boolean.valueOf(batHoatDong).equals(bt.getKichHoat())) {
                    bienTheService.doiTrangThai(id);
                    soLuong++;
                }
            } catch (Exception ignored) {
            }
        }

        String trangThaiMoi = batHoatDong ? "ĐANG MỞ BÁN" : "TẠM NGỪNG";
        redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                "Đã chuyển trạng thái " + soLuong + " biến thể SKU đã chọn sang: " + trangThaiMoi + " thành công!");
        return "redirect:/admin/bien-the/hien-thi";
    }

    /**
     * API Tự động sinh mã SKU và Mã vạch Barcode theo Sản phẩm + Màu sắc + Kích thước.
     * Endpoint: GET /admin/bien-the/api/sinh-ma?maSanPhamId=...&maMauSacId=...&maKichThuocId=...
     */
    @GetMapping("/api/sinh-ma")
    @ResponseBody
    public ResponseEntity<Map<String, String>> apiSinhMaTuDong(
            @RequestParam(required = false) Long maSanPhamId,
            @RequestParam(required = false) Long maMauSacId,
            @RequestParam(required = false) Long maKichThuocId) {
        String sku = bienTheService.sinhMaSkuTuDong(maSanPhamId, maMauSacId, maKichThuocId);
        String barcode = bienTheService.sinhMaVachBarcodeTuDong(sku);

        Map<String, String> res = new HashMap<>();
        res.put("sku", sku);
        res.put("barcode", barcode);
        return ResponseEntity.ok(res);
    }

    /**
     * API Thêm nhanh nhiều biến thể SKU cùng 1 lần - SIMPLAIN Matrix (Ma trận phân loại hàng).
     * Endpoint: POST /admin/bien-the/api/them-hang-loat
     */
    @PostMapping(value = "/api/them-hang-loat", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> apiThemHangLoat(@Valid @RequestBody ThemNhanhBienTheRequest request) {
        try {
            List<BienTheSanPham> ketQua = bienTheService.themHangLoat(request);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("soLuong", ketQua.size());
            res.put("message", "Đã tạo/cập nhật thành công " + ketQua.size() + " biến thể phân loại cho sản phẩm!");
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Đã xảy ra lỗi hệ thống khi thêm biến thể: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}
