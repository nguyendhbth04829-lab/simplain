package vn.fpoly.simplain.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.entity.DanhMuc;
import vn.fpoly.simplain.entity.HinhAnh;
import vn.fpoly.simplain.entity.KichThuoc;
import vn.fpoly.simplain.entity.MauSac;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.BoSuuTapRepository;
import vn.fpoly.simplain.repository.ChatLieuRepository;
import vn.fpoly.simplain.repository.FormDangRepository;
import vn.fpoly.simplain.repository.HinhAnhRepository;
import vn.fpoly.simplain.repository.KichThuocRepository;
import vn.fpoly.simplain.repository.MauSacRepository;
import vn.fpoly.simplain.service.BienTheSanPhamService;
import vn.fpoly.simplain.service.DanhMucService;
import vn.fpoly.simplain.service.SanPhamService;

/**
 * Controller MVC Quản lý thông tin chung Sản phẩm trên giao diện Web (US08).
 *
 * <p>Đáp ứng tiêu chí US08:
 * 1. Tạo san_pham gắn với danh mục (ma_danh_muc_id) và bộ sưu tập tùy chọn (ma_bo_suu_tap_id).
 * 2. Nhập chất liệu, form dáng, mô tả chi tiết, hướng dẫn chọn size và giá bán cơ bản.
 */
@Controller
@RequestMapping("/admin/san-pham")
public class SanPhamController {

    private final SanPhamService sanPhamService;
    private final DanhMucService danhMucService;
    private final BoSuuTapRepository boSuuTapRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final FormDangRepository formDangRepository;
    private final BienTheSanPhamService bienTheSanPhamService;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final HinhAnhRepository hinhAnhRepository;

    @Autowired
    public SanPhamController(SanPhamService sanPhamService,
                             DanhMucService danhMucService,
                             BoSuuTapRepository boSuuTapRepository,
                             ChatLieuRepository chatLieuRepository,
                             FormDangRepository formDangRepository,
                             BienTheSanPhamService bienTheSanPhamService,
                             BienTheSanPhamRepository bienTheSanPhamRepository,
                             MauSacRepository mauSacRepository,
                             KichThuocRepository kichThuocRepository,
                             HinhAnhRepository hinhAnhRepository) {
        this.sanPhamService = sanPhamService;
        this.danhMucService = danhMucService;
        this.boSuuTapRepository = boSuuTapRepository;
        this.chatLieuRepository = chatLieuRepository;
        this.formDangRepository = formDangRepository;
        this.bienTheSanPhamService = bienTheSanPhamService;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
        this.mauSacRepository = mauSacRepository;
        this.kichThuocRepository = kichThuocRepository;
        this.hinhAnhRepository = hinhAnhRepository;
    }

    @GetMapping({"", "/"})
    public String chuyenHuongMacDinh() {
        return "redirect:/admin/san-pham/hien-thi";
    }

    /**
     * Hiển thị danh sách sản phẩm có phân trang, tìm kiếm đa tiêu chí và lọc theo danh mục, bộ sưu tập, form dáng, trạng thái.
     */
    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @RequestParam(defaultValue = "0") Integer page,
                          @RequestParam(required = false) String tuKhoa,
                          @RequestParam(required = false) Long maDanhMucId,
                          @RequestParam(required = false) Long maBoSuuTapId,
                          @RequestParam(required = false) Long maChatLieuId,
                          @RequestParam(required = false) String formDang,
                          @RequestParam(required = false) java.math.BigDecimal giaTu,
                          @RequestParam(required = false) java.math.BigDecimal giaDen,
                          @RequestParam(required = false) Boolean kichHoat) {
        if (page == null || page < 0) {
            page = 0;
        }
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<SanPham> sanPhamPage = sanPhamService.layDanhSachPhanTrang(
                tuKhoa, maDanhMucId, maBoSuuTapId, formDang, null, maChatLieuId, giaTu, giaDen, kichHoat, pageable);

        var listDanhMuc = danhMucService.layTatCa();
        var listBoSuuTap = boSuuTapRepository.findAll(Sort.by("id").descending());
        var listChatLieu = chatLieuRepository.findByKichHoatTrue(Sort.by("tenChatLieu").ascending());
        var listFormDang = formDangRepository.findByKichHoatTrue(Sort.by("tenFormDang").ascending());

        for (SanPham sp : sanPhamPage.getContent()) {
            Long ton = bienTheSanPhamRepository.tinhTongTonKhoTheoSanPham(sp.getId());
            sp.setTongTonKho(ton != null ? ton.intValue() : 0);
            Long sapHet = bienTheSanPhamRepository.demBienTheSapHetHangTheoSanPham(sp.getId(), 10);
            sp.setSoBienTheSapHet(sapHet != null ? sapHet : 0L);
            Long tongBt = bienTheSanPhamRepository.countByMaSanPhamId(sp.getId());
            sp.setSoBienThe(tongBt != null ? tongBt : 0L);
        }

        model.addAttribute("listSanPham", sanPhamPage.getContent());
        model.addAttribute("pageData", sanPhamPage);
        model.addAttribute("listDanhMuc", listDanhMuc);
        model.addAttribute("listBoSuuTap", listBoSuuTap);
        model.addAttribute("listChatLieu", listChatLieu);
        model.addAttribute("listFormDang", listFormDang);

        if (!model.containsAttribute("sanPham")) {
            model.addAttribute("sanPham", new SanPham());
        }

        model.addAttribute("tongSanPham", sanPhamService.demTongSanPham());
        model.addAttribute("sanPhamDangBan", sanPhamService.demSanPhamDangBan());
        model.addAttribute("sanPhamNoiBat", sanPhamService.demSanPhamNoiBat());
        model.addAttribute("tongDanhMuc", listDanhMuc.size());
        model.addAttribute("tongTonKhoToanHeThong", bienTheSanPhamService.tinhTongTonKho());
        model.addAttribute("tongBienTheSapHetToanHeThong", bienTheSanPhamService.demSkuSapHetHang());

        model.addAttribute("page", page);
        model.addAttribute("totalPages", sanPhamPage.getTotalPages());
        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa : "");
        model.addAttribute("maDanhMucLoc", maDanhMucId);
        model.addAttribute("maBoSuuTapLoc", maBoSuuTapId);
        model.addAttribute("maChatLieuLoc", maChatLieuId);
        model.addAttribute("formDangLoc", formDang != null ? formDang : "");
        model.addAttribute("giaTuLoc", giaTu);
        model.addAttribute("giaDenLoc", giaDen);
        model.addAttribute("kichHoatLoc", kichHoat);

        return "admin/san-pham/hien-thi";
    }

    /**
     * Xử lý thêm mới sản phẩm gắn với danh mục, chất liệu, form dáng, mô tả chi tiết (US08)
     * và hỗ trợ tải lên riêng biệt Ảnh đại diện chính (Cover Image) và Album ảnh phụ chi tiết.
     */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("sanPham") SanPham sanPham,
                      BindingResult result,
                      @RequestParam(value = "fileAnhDaiDien", required = false) MultipartFile fileAnhDaiDien,
                      @RequestParam(value = "urlAnhDaiDien", required = false) String urlAnhDaiDien,
                      @RequestParam(value = "filesAnhPhu", required = false) MultipartFile[] filesAnhPhu,
                      @RequestParam(value = "urlsAnhPhu", required = false) String urlsAnhPhu,
                      @RequestParam(value = "filesAnh", required = false) MultipartFile[] filesAnh,
                      @RequestParam(value = "duongDanAnhUrl", required = false) String duongDanAnhUrl,
                      Model model,
                      RedirectAttributes redirectAttributes,
                      @RequestParam(defaultValue = "0") Integer page) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu sản phẩm chưa hợp lệ, vui lòng kiểm tra lại các trường báo đỏ!");
            return hienThi(model, page, null, null, null, null, null, null, null, null);
        }

        try {
            SanPham saved = sanPhamService.them(sanPham);

            boolean coAnhMoiTachBiet = (fileAnhDaiDien != null && !fileAnhDaiDien.isEmpty())
                    || (urlAnhDaiDien != null && !urlAnhDaiDien.isBlank())
                    || (filesAnhPhu != null && filesAnhPhu.length > 0)
                    || (urlsAnhPhu != null && !urlsAnhPhu.isBlank());

            if (coAnhMoiTachBiet) {
                sanPhamService.luuAnhChoSanPhamTachBiet(saved.getId(), fileAnhDaiDien, urlAnhDaiDien, filesAnhPhu, urlsAnhPhu);
            } else if ((filesAnh != null && filesAnh.length > 0) || (duongDanAnhUrl != null && !duongDanAnhUrl.isBlank())) {
                sanPhamService.luuDanhSachHinhAnhSanPham(saved.getId(), filesAnh, duongDanAnhUrl);
            }

            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Thêm mới sản phẩm [" + saved.getMaSanPham() + " - " + saved.getTenSanPham() + "] thành công!");
            return "redirect:/admin/san-pham/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            return hienThi(model, page, null, null, null, null, null, null, null, null);
        }
    }

    /**
     * Hiển thị màn hình chi tiết và cập nhật thông tin chung sản phẩm kèm bộ sưu tập ảnh sản phẩm.
     */
    @GetMapping("/view")
    public String view(Model model,
                       @RequestParam Long id,
                       RedirectAttributes redirectAttributes) {
        try {
            SanPham sp = sanPhamService.layTheoId(id);
            List<HinhAnh> tatCaAnh = sanPhamService.layDanhSachHinhAnhSanPham(id);
            HinhAnh anhDaiDien = sanPhamService.layAnhDaiDienSanPham(id);
            List<HinhAnh> dsAnhPhu = sanPhamService.layDanhSachAnhPhuSanPham(id);

            model.addAttribute("sanPham", sp);
            model.addAttribute("anhDaiDien", anhDaiDien);
            model.addAttribute("danhSachAnhPhu", dsAnhPhu);
            model.addAttribute("danhSachHinhAnh", tatCaAnh);
            model.addAttribute("listDanhMuc", danhMucService.layTatCa());
            model.addAttribute("listBoSuuTap", boSuuTapRepository.findAll(Sort.by("id").descending()));
            model.addAttribute("listChatLieu", chatLieuRepository.findByKichHoatTrue(Sort.by("tenChatLieu").ascending()));
            model.addAttribute("listFormDang", formDangRepository.findByKichHoatTrue(Sort.by("tenFormDang").ascending()));
            return "admin/san-pham/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
            return "redirect:/admin/san-pham/hien-thi";
        }
    }

    /**
     * Xử lý cập nhật thông tin chung sản phẩm (US08).
     */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("sanPham") SanPham sanPham,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("thongBaoLoi", "Dữ liệu cập nhật sản phẩm chưa hợp lệ, vui lòng kiểm tra lại!");
            model.addAttribute("listDanhMuc", danhMucService.layTatCa());
            model.addAttribute("listBoSuuTap", boSuuTapRepository.findAll(Sort.by("id").descending()));
            model.addAttribute("listChatLieu", chatLieuRepository.findByKichHoatTrue(Sort.by("tenChatLieu").ascending()));
            model.addAttribute("listFormDang", formDangRepository.findByKichHoatTrue(Sort.by("tenFormDang").ascending()));
            return "admin/san-pham/view";
        }

        try {
            SanPham updated = sanPhamService.sua(sanPham.getId(), sanPham);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Cập nhật sản phẩm [" + updated.getMaSanPham() + " - " + updated.getTenSanPham() + "] thành công!");
            return "redirect:/admin/san-pham/hien-thi";
        } catch (IllegalArgumentException e) {
            model.addAttribute("thongBaoLoi", e.getMessage());
            model.addAttribute("listDanhMuc", danhMucService.layTatCa());
            model.addAttribute("listBoSuuTap", boSuuTapRepository.findAll(Sort.by("id").descending()));
            model.addAttribute("listChatLieu", chatLieuRepository.findByKichHoatTrue(Sort.by("tenChatLieu").ascending()));
            model.addAttribute("listFormDang", formDangRepository.findByKichHoatTrue(Sort.by("tenFormDang").ascending()));
            return "admin/san-pham/view";
        }
    }

    /**
     * Đổi nhanh trạng thái Kinh doanh (Đang mở bán / Ngừng kinh doanh).
     */
    @GetMapping("/doi-trang-thai")
    public String doiTrangThai(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            SanPham sp = sanPhamService.doiTrangThai(id);
            String trangThaiText = Boolean.TRUE.equals(sp.getKichHoat()) ? "ĐANG MỞ BÁN" : "NGỪNG KINH DOANH";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã chuyển trạng thái sản phẩm [" + sp.getTenSanPham() + "] sang: " + trangThaiText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/san-pham/hien-thi";
    }

    /**
     * Đổi nhanh trạng thái Sản phẩm nổi bật (Best Seller / Thường).
     */
    @GetMapping("/doi-noi-bat")
    public String doiNoiBat(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            SanPham sp = sanPhamService.doiNoiBat(id);
            String noiBatText = Boolean.TRUE.equals(sp.getNoiBat()) ? "SẢN PHẨM NỔI BẬT (BEST SELLER)" : "SẢN PHẨM THƯỜNG";
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã cập nhật đánh dấu sản phẩm [" + sp.getTenSanPham() + "] thành: " + noiBatText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/san-pham/hien-thi";
    }

    /**
     * Xử lý xóa sản phẩm (Bảo vệ toàn vẹn nếu sản phẩm đang có biến thể, hình ảnh hoặc nằm trong combo).
     */
    @GetMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            sanPhamService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Xóa sản phẩm thành công!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/san-pham/hien-thi";
    }

    /**
     * Thao tác hàng loạt trên danh sách ID sản phẩm được chọn (Mở bán hoặc Ngừng kinh doanh).
     */
    @PostMapping("/thao-tac-hang-loat")
    public String thaoTacHangLoat(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "hanhDong", required = false) String hanhDong,
                                  RedirectAttributes redirectAttributes) {
        if (hanhDong == null || hanhDong.isBlank()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Hành động thao tác hàng loạt không hợp lệ!");
            return "redirect:/admin/san-pham/hien-thi";
        }

        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", "Vui lòng tích chọn ít nhất một sản phẩm để thao tác hàng loạt!");
            return "redirect:/admin/san-pham/hien-thi";
        }

        boolean batHoatDong = "KICH_HOAT".equalsIgnoreCase(hanhDong);
        int soLuong = 0;
        for (Long id : ids) {
            try {
                SanPham sp = sanPhamService.layTheoId(id);
                if (sp != null && !Boolean.valueOf(batHoatDong).equals(sp.getKichHoat())) {
                    sanPhamService.doiTrangThai(id);
                    soLuong++;
                }
            } catch (Exception ignored) {
            }
        }

        String trangThaiMoi = batHoatDong ? "ĐANG MỞ BÁN" : "NGỪNG KINH DOANH";
        redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                "Đã chuyển trạng thái " + soLuong + " sản phẩm đã chọn sang: " + trangThaiMoi + " thành công!");
        return "redirect:/admin/san-pham/hien-thi";
    }

    // =========================================================================
    // QUẢN LÝ ALBUM HÌNH ẢNH SẢN PHẨM (ẢNH BÌA & ẢNH PHỤ CHUẨN SÀN TMĐT SIMPLAIN)
    // =========================================================================

    /**
     * Upload bổ sung hình ảnh (File hoặc URL) cho sản phẩm từ trang Chi tiết (view.html).
     */
    @PostMapping("/upload-anh")
    public String uploadAnh(@RequestParam Long id,
                            @RequestParam(value = "filesAnh", required = false) MultipartFile[] filesAnh,
                            @RequestParam(value = "duongDanAnhUrl", required = false) String duongDanAnhUrl,
                            RedirectAttributes redirectAttributes) {
        try {
            int soAnh = sanPhamService.luuDanhSachHinhAnhSanPham(id, filesAnh, duongDanAnhUrl);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã tải lên thành công " + soAnh + " hình ảnh cho sản phẩm!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/san-pham/view?id=" + id;
    }

    /**
     * Cập nhật riêng Ảnh đại diện chính (Cover Image) cho sản phẩm từ trang Chi tiết (view.html).
     */
    @PostMapping("/cap-nhat-anh-dai-dien")
    public String capNhatAnhDaiDien(@RequestParam Long sanPhamId,
                                    @RequestParam(value = "fileAnhDaiDien", required = false) MultipartFile fileAnhDaiDien,
                                    @RequestParam(value = "urlAnhDaiDien", required = false) String urlAnhDaiDien,
                                    RedirectAttributes redirectAttributes) {
        try {
            sanPhamService.capNhatAnhDaiDienSanPham(sanPhamId, fileAnhDaiDien, urlAnhDaiDien);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã cập nhật Ảnh đại diện chính cho sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/san-pham/view?id=" + sanPhamId;
    }

    /**
     * Tải lên bổ sung các Ảnh phụ chi tiết cho sản phẩm từ trang Chi tiết (view.html).
     */
    @PostMapping("/them-anh-phu")
    public String themAnhPhu(@RequestParam Long sanPhamId,
                             @RequestParam(value = "filesAnhPhu", required = false) MultipartFile[] filesAnhPhu,
                             @RequestParam(value = "urlsAnhPhu", required = false) String urlsAnhPhu,
                             RedirectAttributes redirectAttributes) {
        try {
            int count = sanPhamService.themAnhPhuChoSanPham(sanPhamId, filesAnhPhu, urlsAnhPhu);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã thêm thành công " + count + " ảnh phụ vào Album!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/san-pham/view?id=" + sanPhamId;
    }

    /**
     * Đặt ảnh được chọn làm Ảnh bìa / Ảnh đại diện chính cho sản phẩm.
     */
    @GetMapping("/chon-anh-dai-dien")
    public String chonAnhDaiDien(@RequestParam Long hinhAnhId,
                                 @RequestParam Long sanPhamId,
                                 RedirectAttributes redirectAttributes) {
        try {
            sanPhamService.datAnhDaiDienSanPham(sanPhamId, hinhAnhId);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong",
                    "Đã đặt làm Ảnh bìa (Ảnh đại diện chính) sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/san-pham/view?id=" + sanPhamId;
    }

    /**
     * Xóa một hình ảnh khỏi sản phẩm.
     */
    @GetMapping("/xoa-anh")
    public String xoaAnh(@RequestParam Long hinhAnhId,
                         @RequestParam Long sanPhamId,
                         RedirectAttributes redirectAttributes) {
        try {
            sanPhamService.xoaHinhAnhSanPham(sanPhamId, hinhAnhId);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã xóa hình ảnh khỏi sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/san-pham/view?id=" + sanPhamId;
    }

    /**
     * Thay đổi vị trí / thứ tự hiển thị của hình ảnh sản phẩm.
     */
    @GetMapping("/doi-thu-tu-anh")
    public String doiThuTuAnh(@RequestParam Long hinhAnhId,
                              @RequestParam Long sanPhamId,
                              @RequestParam String huongDi,
                              RedirectAttributes redirectAttributes) {
        try {
            sanPhamService.doiThuTuHinhAnhSanPham(sanPhamId, hinhAnhId, huongDi);
            redirectAttributes.addFlashAttribute("thongBaoThanhCong", "Đã cập nhật thứ tự hình ảnh thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("thongBaoLoi", e.getMessage());
        }
        return "redirect:/admin/san-pham/view?id=" + sanPhamId;
    }

    // =========================================================================
    // REST API DÀNH CHO POPUP MODAL QUẢN LÝ ẢNH NHANH TRÊN DANH SÁCH (hien-thi.html)
    // =========================================================================

    /**
     * Lấy danh sách ảnh của sản phẩm qua AJAX (tách riêng ảnh đại diện và ảnh phụ).
     */
    @GetMapping("/api/danh-sach-anh")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiLayDanhSachAnh(@RequestParam Long sanPhamId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            SanPham sp = sanPhamService.layTheoId(sanPhamId);
            List<HinhAnh> ds = sanPhamService.layDanhSachHinhAnhSanPham(sanPhamId);
            HinhAnh daiDien = sanPhamService.layAnhDaiDienSanPham(sanPhamId);
            List<HinhAnh> dsPhu = sanPhamService.layDanhSachAnhPhuSanPham(sanPhamId);

            resp.put("success", true);
            resp.put("sanPhamId", sp.getId());
            resp.put("maSanPham", sp.getMaSanPham());
            resp.put("tenSanPham", sp.getTenSanPham());
            resp.put("anhDaiDienUrl", sp.getAnhDaiDienUrl());
            resp.put("anhDaiDien", daiDien);
            resp.put("danhSachAnhPhu", dsPhu);
            resp.put("danhSachHinhAnh", ds);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * Cập nhật riêng Ảnh đại diện qua AJAX.
     */
    @PostMapping("/api/cap-nhat-anh-dai-dien")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiCapNhatAnhDaiDien(
            @RequestParam Long sanPhamId,
            @RequestParam(value = "fileAnhDaiDien", required = false) MultipartFile fileAnhDaiDien,
            @RequestParam(value = "urlAnhDaiDien", required = false) String urlAnhDaiDien) {
        Map<String, Object> resp = new HashMap<>();
        try {
            sanPhamService.capNhatAnhDaiDienSanPham(sanPhamId, fileAnhDaiDien, urlAnhDaiDien);
            List<HinhAnh> ds = sanPhamService.layDanhSachHinhAnhSanPham(sanPhamId);
            HinhAnh daiDien = sanPhamService.layAnhDaiDienSanPham(sanPhamId);
            List<HinhAnh> dsPhu = sanPhamService.layDanhSachAnhPhuSanPham(sanPhamId);
            SanPham sp = sanPhamService.layTheoId(sanPhamId);
            resp.put("success", true);
            resp.put("message", "Đã cập nhật Ảnh đại diện chính thành công!");
            resp.put("anhDaiDien", daiDien);
            resp.put("danhSachAnhPhu", dsPhu);
            resp.put("danhSachHinhAnh", ds);
            resp.put("anhDaiDienUrl", sp.getAnhDaiDienUrl());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * Thêm riêng Ảnh phụ qua AJAX.
     */
    @PostMapping("/api/them-anh-phu")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiThemAnhPhu(
            @RequestParam Long sanPhamId,
            @RequestParam(value = "filesAnhPhu", required = false) MultipartFile[] filesAnhPhu,
            @RequestParam(value = "urlsAnhPhu", required = false) String urlsAnhPhu) {
        Map<String, Object> resp = new HashMap<>();
        try {
            int count = sanPhamService.themAnhPhuChoSanPham(sanPhamId, filesAnhPhu, urlsAnhPhu);
            List<HinhAnh> ds = sanPhamService.layDanhSachHinhAnhSanPham(sanPhamId);
            HinhAnh daiDien = sanPhamService.layAnhDaiDienSanPham(sanPhamId);
            List<HinhAnh> dsPhu = sanPhamService.layDanhSachAnhPhuSanPham(sanPhamId);
            SanPham sp = sanPhamService.layTheoId(sanPhamId);
            resp.put("success", true);
            resp.put("message", "Đã thêm thành công " + count + " ảnh phụ vào Album!");
            resp.put("anhDaiDien", daiDien);
            resp.put("danhSachAnhPhu", dsPhu);
            resp.put("danhSachHinhAnh", ds);
            resp.put("anhDaiDienUrl", sp.getAnhDaiDienUrl());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * Upload ảnh mới qua AJAX trong popup modal (hỗ trợ cả chọn làm ảnh đại diện).
     */
    @PostMapping("/api/upload-anh")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiUploadAnh(@RequestParam Long sanPhamId,
                                                            @RequestParam(value = "filesAnh", required = false) MultipartFile[] filesAnh,
                                                            @RequestParam(value = "duongDanAnhUrl", required = false) String duongDanAnhUrl,
                                                            @RequestParam(value = "laAnhDaiDien", required = false) Boolean laAnhDaiDien) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (Boolean.TRUE.equals(laAnhDaiDien)) {
                MultipartFile fileDauTien = (filesAnh != null && filesAnh.length > 0) ? filesAnh[0] : null;
                sanPhamService.capNhatAnhDaiDienSanPham(sanPhamId, fileDauTien, duongDanAnhUrl);
            } else {
                sanPhamService.luuDanhSachHinhAnhSanPham(sanPhamId, filesAnh, duongDanAnhUrl);
            }
            List<HinhAnh> ds = sanPhamService.layDanhSachHinhAnhSanPham(sanPhamId);
            HinhAnh daiDien = sanPhamService.layAnhDaiDienSanPham(sanPhamId);
            List<HinhAnh> dsPhu = sanPhamService.layDanhSachAnhPhuSanPham(sanPhamId);
            SanPham sp = sanPhamService.layTheoId(sanPhamId);
            resp.put("success", true);
            resp.put("message", "Đã cập nhật hình ảnh thành công!");
            resp.put("anhDaiDien", daiDien);
            resp.put("danhSachAnhPhu", dsPhu);
            resp.put("danhSachHinhAnh", ds);
            resp.put("anhDaiDienUrl", sp.getAnhDaiDienUrl());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * Chọn ảnh bìa qua AJAX.
     */
    @PostMapping("/api/chon-anh-dai-dien")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiChonAnhDaiDien(@RequestParam Long sanPhamId,
                                                                 @RequestParam Long hinhAnhId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            sanPhamService.datAnhDaiDienSanPham(sanPhamId, hinhAnhId);
            List<HinhAnh> ds = sanPhamService.layDanhSachHinhAnhSanPham(sanPhamId);
            SanPham sp = sanPhamService.layTheoId(sanPhamId);
            resp.put("success", true);
            resp.put("message", "Đã đặt làm Ảnh bìa chính thành công!");
            resp.put("danhSachHinhAnh", ds);
            resp.put("anhDaiDienUrl", sp.getAnhDaiDienUrl());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * Xóa ảnh qua AJAX.
     */
    @PostMapping("/api/xoa-anh")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiXoaAnh(@RequestParam Long sanPhamId,
                                                         @RequestParam Long hinhAnhId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            sanPhamService.xoaHinhAnhSanPham(sanPhamId, hinhAnhId);
            List<HinhAnh> ds = sanPhamService.layDanhSachHinhAnhSanPham(sanPhamId);
            SanPham sp = sanPhamService.layTheoId(sanPhamId);
            resp.put("success", true);
            resp.put("message", "Đã xóa hình ảnh thành công!");
            resp.put("danhSachHinhAnh", ds);
            resp.put("anhDaiDienUrl", sp.getAnhDaiDienUrl());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * Đổi vị trí ảnh qua AJAX.
     */
    @PostMapping("/api/doi-thu-tu-anh")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiDoiThuTuAnh(@RequestParam Long sanPhamId,
                                                              @RequestParam Long hinhAnhId,
                                                              @RequestParam String huongDi) {
        Map<String, Object> resp = new HashMap<>();
        try {
            sanPhamService.doiThuTuHinhAnhSanPham(sanPhamId, hinhAnhId, huongDi);
            List<HinhAnh> ds = sanPhamService.layDanhSachHinhAnhSanPham(sanPhamId);
            SanPham sp = sanPhamService.layTheoId(sanPhamId);
            resp.put("success", true);
            resp.put("message", "Đã di chuyển vị trí ảnh thành công!");
            resp.put("danhSachHinhAnh", ds);
            resp.put("anhDaiDienUrl", sp.getAnhDaiDienUrl());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // =========================================================================
    // API GỘP QUẢN LÝ BIẾN THỂ TRỰC TIẾP TRONG SẢN PHẨM & XEM CHI TIẾT TỒN KHO
    // =========================================================================

    /**
     * API JSON Lấy chi tiết đầy đủ sản phẩm (bao gồm toàn bộ thuộc tính, album ảnh và danh sách biến thể tồn kho).
     */
    @GetMapping("/api/chi-tiet-day-du")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiLayChiTietDayDu(@RequestParam Long sanPhamId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            SanPham sp = sanPhamService.layTheoId(sanPhamId);
            List<HinhAnh> dsAnh = sanPhamService.layDanhSachHinhAnhSanPham(sanPhamId);
            List<BienTheSanPham> dsBienThe = bienTheSanPhamRepository.findByMaSanPhamId(sanPhamId);

            Long ton = bienTheSanPhamRepository.tinhTongTonKhoTheoSanPham(sanPhamId);
            Long sapHet = bienTheSanPhamRepository.demBienTheSapHetHangTheoSanPham(sanPhamId, 10);

            Map<String, Object> spData = new HashMap<>();
            spData.put("id", sp.getId());
            spData.put("maSanPham", sp.getMaSanPham());
            spData.put("tenSanPham", sp.getTenSanPham());
            spData.put("slug", sp.getDuongDanSlug());
            spData.put("giaBan", sp.getGiaCoBan());
            spData.put("giaGoc", sp.getGiaCoBan());
            spData.put("kichHoat", sp.getKichHoat());
            spData.put("anhDaiDienUrl", sp.getAnhDaiDienUrl());
            spData.put("huongDanChonSize", sp.getHuongDanChonSize());
            spData.put("moTaChiTiet", sp.getMoTaChiTiet());

            // Thuộc tính danh mục & phân loại
            DanhMuc dm = null;
            if (sp.getMaDanhMucId() != null) {
                try {
                    dm = danhMucService.layTheoId(sp.getMaDanhMucId());
                } catch (Exception ignored) {}
            }
            String tenDm = dm != null ? dm.getTenDanhMuc() : (sp.getTenDanhMuc() != null ? sp.getTenDanhMuc() : "Chưa phân loại");
            String tenDmCha = "Danh mục gốc";
            if (dm != null && dm.getMaDanhMucCha() != null) {
                try {
                    DanhMuc dmCha = danhMucService.layTheoId(dm.getMaDanhMucCha());
                    if (dmCha != null) {
                        tenDmCha = dmCha.getTenDanhMuc();
                    }
                } catch (Exception ignored) {}
            }

            spData.put("tenDanhMuc", tenDm);
            spData.put("tenDanhMucCha", tenDmCha);
            spData.put("tenBoSuuTap", sp.getTenBoSuuTap() != null ? sp.getTenBoSuuTap() : "Không có");
            spData.put("tenChatLieu", sp.getChatLieu() != null ? sp.getChatLieu() : "Chưa xác định");
            spData.put("tenFormDang", sp.getFormDang() != null ? sp.getFormDang() : "Tiêu chuẩn");

            spData.put("tongTonKho", ton != null ? ton : 0L);
            spData.put("soBienThe", dsBienThe.size());
            spData.put("soBienTheSapHet", sapHet != null ? sapHet : 0L);

            // Dữ liệu biến thể kèm thông tin màu/size
            Map<Long, MauSac> mapMau = mauSacRepository.findAll().stream()
                    .collect(java.util.stream.Collectors.toMap(MauSac::getId, m -> m, (a, b) -> a));
            Map<Long, KichThuoc> mapSize = kichThuocRepository.findAll().stream()
                    .collect(java.util.stream.Collectors.toMap(KichThuoc::getId, k -> k, (a, b) -> a));

            List<Map<String, Object>> listBtDto = dsBienThe.stream().map(bt -> {
                Map<String, Object> mapBt = new HashMap<>();
                mapBt.put("id", bt.getId());
                mapBt.put("maSku", bt.getMaSku());
                mapBt.put("maVachBarcode", bt.getMaVachBarcode() != null ? bt.getMaVachBarcode() : "");
                mapBt.put("soLuongTon", bt.getSoLuong() != null ? bt.getSoLuong() : 0);
                mapBt.put("giaBan", bt.getGiaBan());
                mapBt.put("giaGoc", bt.getGiaGoc());
                mapBt.put("kichHoat", bt.getKichHoat());
                MauSac ms = mapMau.get(bt.getMaMauSacId());
                mapBt.put("tenMauSac", ms != null ? ms.getTenMau() : (bt.getTenMauSac() != null ? bt.getTenMauSac() : ""));
                mapBt.put("maMauHex", ms != null ? ms.getMaMau() : (bt.getMaMauHex() != null ? bt.getMaMauHex() : "#cccccc"));
                mapBt.put("maMauSacId", bt.getMaMauSacId());
                KichThuoc kt = mapSize.get(bt.getMaKichThuocId());
                mapBt.put("tenKichThuoc", kt != null ? kt.getTenKichThuoc() : (bt.getTenKichThuoc() != null ? bt.getTenKichThuoc() : ""));
                mapBt.put("maKichThuocId", bt.getMaKichThuocId());
                mapBt.put("sapHetHang", bt.getSoLuong() != null && bt.getSoLuong() <= 10);
                mapBt.put("anhDaiDienUrl", bt.getAnhDaiDienUrl() != null ? bt.getAnhDaiDienUrl() : sp.getAnhDaiDienUrl());
                return mapBt;
            }).toList();

            resp.put("success", true);
            resp.put("sanPham", spData);
            resp.put("danhSachHinhAnh", dsAnh);
            resp.put("danhSachBienThe", listBtDto);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "Lỗi lấy thông tin: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * API JSON Danh mục Màu sắc và Kích thước phục vụ Modal thêm biến thể trực tiếp.
     */
    @GetMapping("/api/danh-muc-mau-size")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiLayDanhMucMauSize() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("listMauSac", mauSacRepository.findByKichHoatTrueOrderByTenMauAsc());
        resp.put("listKichThuoc", kichThuocRepository.findByKichHoatTrueOrderByThuTuHienThiAsc());
        return ResponseEntity.ok(resp);
    }

    /**
     * API JSON Thêm trực tiếp 1 biến thể vào sản phẩm ngay trên màn hình sản phẩm.
     */
    @PostMapping("/api/them-bien-the-truc-tiep")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiThemBienTheTrucTiep(
            @RequestParam Long sanPhamId,
            @RequestParam Long maMauSacId,
            @RequestParam Long maKichThuocId,
            @RequestParam java.math.BigDecimal giaBan,
            @RequestParam(required = false) java.math.BigDecimal giaGoc,
            @RequestParam Integer soLuongTon,
            @RequestParam(required = false) String maSku,
            @RequestParam(required = false) String maVachBarcode) {
        Map<String, Object> resp = new HashMap<>();
        try {
            // Kiểm tra trùng lặp màu + size
            boolean daCo = bienTheSanPhamRepository.findByMaSanPhamIdAndMaMauSacIdAndMaKichThuocId(
                    sanPhamId, maMauSacId, maKichThuocId).isPresent();
            if (daCo) {
                resp.put("success", false);
                resp.put("message", "Biến thể với Màu sắc và Kích cỡ này đã tồn tại trong sản phẩm!");
                return ResponseEntity.badRequest().body(resp);
            }

            // Sinh mã SKU tự động nếu chưa có
            String sku = (maSku != null && !maSku.trim().isEmpty())
                    ? maSku.trim().toUpperCase()
                    : bienTheSanPhamService.sinhMaSkuTuDong(sanPhamId, maMauSacId, maKichThuocId);

            if (bienTheSanPhamRepository.existsByMaSku(sku)) {
                sku = sku + "-" + System.currentTimeMillis() % 1000;
            }

            // Sinh mã Barcode nếu chưa có
            String barcode = (maVachBarcode != null && !maVachBarcode.trim().isEmpty())
                    ? maVachBarcode.trim()
                    : bienTheSanPhamService.sinhMaVachBarcodeTuDong(sku);

            BienTheSanPham bt = BienTheSanPham.builder()
                    .maSanPhamId(sanPhamId)
                    .maMauSacId(maMauSacId)
                    .maKichThuocId(maKichThuocId)
                    .maSku(sku)
                    .maVachBarcode(barcode)
                    .giaBan(giaBan)
                    .giaGoc(giaGoc != null ? giaGoc : giaBan)
                    .soLuong(soLuongTon != null ? soLuongTon : 0)
                    .kichHoat(true)
                    .build();

            BienTheSanPham saved = bienTheSanPhamRepository.save(bt);
            resp.put("success", true);
            resp.put("message", "Thêm biến thể [" + saved.getMaSku() + "] thành công!");
            resp.put("bienTheId", saved.getId());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "Lỗi tạo biến thể: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * API JSON Cập nhật nhanh biến thể trực tiếp (tồn kho, giá bán, mã vạch).
     */
    @PostMapping("/api/sua-bien-the-truc-tiep")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiSuaBienTheTrucTiep(
            @RequestParam Long id,
            @RequestParam Integer soLuongTon,
            @RequestParam java.math.BigDecimal giaBan,
            @RequestParam(required = false) java.math.BigDecimal giaGoc,
            @RequestParam(required = false) String maVachBarcode) {
        Map<String, Object> resp = new HashMap<>();
        try {
            BienTheSanPham bt = bienTheSanPhamRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy biến thể #" + id));

            bt.setSoLuong(soLuongTon);
            bt.setGiaBan(giaBan);
            if (giaGoc != null) bt.setGiaGoc(giaGoc);
            if (maVachBarcode != null && !maVachBarcode.trim().isEmpty()) {
                bt.setMaVachBarcode(maVachBarcode.trim());
            }

            bienTheSanPhamRepository.save(bt);
            resp.put("success", true);
            resp.put("message", "Cập nhật biến thể thành công!");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "Lỗi cập nhật: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * API JSON Bật/Tắt trạng thái biến thể trực tiếp.
     */
    @PostMapping("/api/doi-trang-thai-bien-the-truc-tiep")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiDoiTrangThaiBienTheTrucTiep(@RequestParam Long id) {
        Map<String, Object> resp = new HashMap<>();
        try {
            BienTheSanPham bt = bienTheSanPhamService.doiTrangThai(id);
            resp.put("success", true);
            resp.put("kichHoat", bt.getKichHoat());
            resp.put("message", "Đã " + (Boolean.TRUE.equals(bt.getKichHoat()) ? "kích hoạt" : "tạm ngưng") + " biến thể!");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "Lỗi đổi trạng thái: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * API JSON Xóa biến thể trực tiếp.
     */
    @PostMapping("/api/xoa-bien-the-truc-tiep")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiXoaBienTheTrucTiep(@RequestParam Long id) {
        Map<String, Object> resp = new HashMap<>();
        try {
            bienTheSanPhamService.xoa(id);
            resp.put("success", true);
            resp.put("message", "Đã xóa biến thể thành công!");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "Lỗi xóa biến thể: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }
}
