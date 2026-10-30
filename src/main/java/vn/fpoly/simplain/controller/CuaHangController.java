package vn.fpoly.simplain.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.dto.DanhGiaRequest;
import vn.fpoly.simplain.dto.HoiVienResponse;
import vn.fpoly.simplain.dto.ThongKeDanhGiaResponse;
import vn.fpoly.simplain.dto.ThongTinKhachHangResponse;
import vn.fpoly.simplain.dto.VoucherKhachHangDto;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.service.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Controller MVC phục vụ giao diện Storefront Simplain cho Khách hàng:
 * - Tìm kiếm và lọc danh sách sản phẩm đa tiêu chí (US10 - Mod 2 / Storefront).
 * - Xem trang Chi tiết sản phẩm chuyên nghiệp: Khi chọn Size/Màu nhảy ảnh tương ứng & Hiển thị số lượng tồn kho thực tế (US11 - Mod 2 / Storefront).
 */
@Controller
public class CuaHangController {

    private final SanPhamService sanPhamService;
    private final DanhMucService danhMucService;
    private final BoSuuTapService boSuuTapService;
    private final MauSacService mauSacService;
    private final KichThuocService kichThuocService;
    private final KhachHangService khachHangService;
    private final VideoSanPhamService videoSanPhamService;
    private final SanPhamYeuThichService sanPhamYeuThichService;
    private final DanhGiaSanPhamService danhGiaSanPhamService;
    private final LoyaltyHoiVienService loyaltyHoiVienService;
    private final VoucherKhachHangService voucherKhachHangService;
    private BannerQuangCaoService bannerQuangCaoService;
    private GoiComboService goiComboService;
    private FlashSaleService flashSaleService;

    @Autowired
    public void setBannerQuangCaoService(BannerQuangCaoService bannerQuangCaoService) {
        this.bannerQuangCaoService = bannerQuangCaoService;
    }

    @Autowired
    public void setGoiComboService(GoiComboService goiComboService) {
        this.goiComboService = goiComboService;
    }

    @Autowired
    public void setFlashSaleService(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
    }

    @Autowired
    public CuaHangController(SanPhamService sanPhamService,
                             DanhMucService danhMucService,
                             BoSuuTapService boSuuTapService,
                             MauSacService mauSacService,
                             KichThuocService kichThuocService,
                             KhachHangService khachHangService,
                             VideoSanPhamService videoSanPhamService,
                             SanPhamYeuThichService sanPhamYeuThichService,
                             DanhGiaSanPhamService danhGiaSanPhamService,
                             LoyaltyHoiVienService loyaltyHoiVienService,
                             VoucherKhachHangService voucherKhachHangService) {
        this.sanPhamService = sanPhamService;
        this.danhMucService = danhMucService;
        this.boSuuTapService = boSuuTapService;
        this.mauSacService = mauSacService;
        this.kichThuocService = kichThuocService;
        this.khachHangService = khachHangService;
        this.videoSanPhamService = videoSanPhamService;
        this.sanPhamYeuThichService = sanPhamYeuThichService;
        this.danhGiaSanPhamService = danhGiaSanPhamService;
        this.loyaltyHoiVienService = loyaltyHoiVienService;
        this.voucherKhachHangService = voucherKhachHangService;
    }

    /**
     * Điều hướng trang chủ gốc (/) về trang Cửa hàng Storefront Simplain (US10).
     */
    @GetMapping("/")
    public String trangChu() {
        return "redirect:/cua-hang";
    }

    /**
     * Hiển thị trang Storefront Tìm kiếm & Lọc danh sách sản phẩm (US10).
     * Hỗ trợ các đường dẫn: /cua-hang, /cua-hang/san-pham, /san-pham
     */
    @GetMapping({"/cua-hang", "/cua-hang/san-pham", "/san-pham"})
    public String hienThiCuaHang(@RequestParam(name = "tuKhoa", defaultValue = "") String tuKhoa,
                                 @RequestParam(name = "maDanhMucId", required = false) Long maDanhMucId,
                                 @RequestParam(name = "maBoSuuTapId", required = false) Long maBoSuuTapId,
                                 @RequestParam(name = "maMauSacId", required = false) Long maMauSacId,
                                 @RequestParam(name = "maKichThuocId", required = false) Long maKichThuocId,
                                 @RequestParam(name = "mucGia", defaultValue = "") String mucGia,
                                 @RequestParam(name = "giaTu", defaultValue = "") String giaTuStr,
                                 @RequestParam(name = "giaDen", defaultValue = "") String giaDenStr,
                                 @RequestParam(name = "sapXep", defaultValue = "noi-bat") String sapXep,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "9") int size,
                                 HttpServletRequest request,
                                 Model model) {

        napThongTinKhachHangDangNhap(request, model);

        BigDecimal giaTu = null;
        BigDecimal giaDen = null;
        String loiLocGia = null;

        try {
            if (giaTuStr != null && !giaTuStr.isBlank()) {
                giaTu = new BigDecimal(giaTuStr.trim().replace(",", "").replace(".", ""));
            }
            if (giaDenStr != null && !giaDenStr.isBlank()) {
                giaDen = new BigDecimal(giaDenStr.trim().replace(",", "").replace(".", ""));
            }
        } catch (NumberFormatException e) {
            loiLocGia = "Khoảng giá nhập vào phải là số hợp lệ (VD: 150000 đến 350000)!";
        }

        Page<SanPham> pageSanPham;
        if (loiLocGia != null) {
            model.addAttribute("loiLoc", loiLocGia);
            pageSanPham = sanPhamService.timKiemVaLocStorefront(
                    tuKhoa, maDanhMucId, maBoSuuTapId, maMauSacId, maKichThuocId,
                    "", null, null, sapXep, page, size);
        } else {
            try {
                pageSanPham = sanPhamService.timKiemVaLocStorefront(
                        tuKhoa, maDanhMucId, maBoSuuTapId, maMauSacId, maKichThuocId,
                        mucGia, giaTu, giaDen, sapXep, page, size);
            } catch (IllegalArgumentException e) {
                model.addAttribute("loiLoc", e.getMessage());
                pageSanPham = sanPhamService.timKiemVaLocStorefront(
                        tuKhoa, maDanhMucId, maBoSuuTapId, maMauSacId, maKichThuocId,
                        "", null, null, sapXep, page, size);
            }
        }

        // Danh sách dữ liệu cho Sidebar Bộ lọc (chỉ hiển thị Danh mục & BST đang kích hoạt)
        List<DanhMuc> danhSachDanhMuc = danhMucService.layTatCa().stream()
                .filter(dm -> Boolean.TRUE.equals(dm.getKichHoat()))
                .toList();
        List<BoSuuTap> danhSachBoSuuTap = boSuuTapService.layTatCa().stream()
                .filter(bst -> Boolean.TRUE.equals(bst.getKichHoat()))
                .toList();
        List<BoSuuTap> danhSachBanner = boSuuTapService.layTatCa().stream()
                .filter(bst -> Boolean.TRUE.equals(bst.getKichHoat())
                        && bst.getHinhBannerUrl() != null
                        && !bst.getHinhBannerUrl().isBlank())
                .toList();
        List<MauSac> danhSachMauSac = mauSacService.layTatCa();
        List<KichThuoc> danhSachKichThuoc = kichThuocService.layTatCa();

        // Đếm số lượng bộ lọc đang được áp dụng
        int soBoLocDangApDung = 0;
        if (tuKhoa != null && !tuKhoa.isBlank()) soBoLocDangApDung++;
        if (maDanhMucId != null) soBoLocDangApDung++;
        if (maBoSuuTapId != null) soBoLocDangApDung++;
        if (maMauSacId != null) soBoLocDangApDung++;
        if (maKichThuocId != null) soBoLocDangApDung++;
        if ((mucGia != null && !mucGia.isBlank()) || giaTu != null || giaDen != null) soBoLocDangApDung++;

        model.addAttribute("pageSanPham", pageSanPham);
        model.addAttribute("danhSachSanPham", pageSanPham.getContent());
        model.addAttribute("tongKetQua", pageSanPham.getTotalElements());

        model.addAttribute("danhSachDanhMuc", danhSachDanhMuc);
        model.addAttribute("danhSachBoSuuTap", danhSachBoSuuTap);
        model.addAttribute("danhSachBanner", danhSachBanner);
        model.addAttribute("danhSachMauSac", danhSachMauSac);
        model.addAttribute("danhSachKichThuoc", danhSachKichThuoc);

        // Banner Quảng Cáo Quản Trị Động (US30 - Mod 6)
        if (bannerQuangCaoService != null) {
            List<vn.fpoly.simplain.entity.BannerQuangCao> bannerHero = bannerQuangCaoService.layBannerTheoViTri("HERO_SLIDER");
            if (bannerHero.isEmpty()) {
                bannerHero = bannerQuangCaoService.layBannerTheoViTri("STORE_TOP");
            }
            List<vn.fpoly.simplain.entity.BannerQuangCao> bannerMiddle = bannerQuangCaoService.layBannerTheoViTri("HOME_MIDDLE");
            List<vn.fpoly.simplain.entity.BannerQuangCao> bannerPopup = bannerQuangCaoService.layBannerTheoViTri("POPUP");
            List<vn.fpoly.simplain.entity.BannerQuangCao> bannerFooter = bannerQuangCaoService.layBannerTheoViTri("FOOTER");

            model.addAttribute("danhSachBannerQuangCao", bannerHero);
            model.addAttribute("bannerMiddle", !bannerMiddle.isEmpty() ? bannerMiddle.get(0) : null);
            model.addAttribute("bannerPopup", !bannerPopup.isEmpty() ? bannerPopup.get(0) : null);
            model.addAttribute("bannerFooter", !bannerFooter.isEmpty() ? bannerFooter.get(0) : null);
        }

        // Đợt Flash Sale Đang Diễn Ra / Mới Nhất (US29 - Mod 6)
        if (flashSaleService != null) {
            try {
                vn.fpoly.simplain.entity.FlashSale flashSale = flashSaleService.layFlashSaleHienTai();
                model.addAttribute("flashSaleHienTai", flashSale);
            } catch (Exception ignored) {
                model.addAttribute("flashSaleHienTai", null);
            }
        }

        // Danh Sách Gói Combo Kích Cầu Ưu Đãi (US28 - Mod 6)
        if (goiComboService != null) {
            try {
                org.springframework.data.domain.Page<vn.fpoly.simplain.entity.GoiCombo> pageCombo = goiComboService.layDanhSachGoiCombo(
                        null, true, org.springframework.data.domain.PageRequest.of(0, 4));
                model.addAttribute("danhSachComboTrangChu", pageCombo.getContent());
            } catch (Exception ignored) {
                model.addAttribute("danhSachComboTrangChu", Collections.emptyList());
            }
        }

        model.addAttribute("tuKhoa", tuKhoa != null ? tuKhoa.trim() : "");
        model.addAttribute("maDanhMucId", maDanhMucId);
        model.addAttribute("maBoSuuTapId", maBoSuuTapId);
        model.addAttribute("maMauSacId", maMauSacId);
        model.addAttribute("maKichThuocId", maKichThuocId);
        model.addAttribute("mucGia", mucGia != null ? mucGia.trim() : "");
        model.addAttribute("giaTu", giaTu != null ? giaTu.toPlainString() : "");
        model.addAttribute("giaDen", giaDen != null ? giaDen.toPlainString() : "");
        model.addAttribute("sapXep", sapXep);
        model.addAttribute("soBoLocDangApDung", soBoLocDangApDung);

        return "khach-hang/cua-hang";
    }

    /**
     * Hiển thị danh sách các Bộ sưu tập thời trang (Lookbook) đang mở bán.
     */
    @GetMapping({"/bo-suu-tap", "/cua-hang/bo-suu-tap"})
    public String danhSachBoSuuTap(HttpServletRequest request, Model model) {
        napThongTinKhachHangDangNhap(request, model);

        List<BoSuuTap> danhSachBoSuuTap = boSuuTapService.layTatCa().stream()
                .filter(bst -> Boolean.TRUE.equals(bst.getKichHoat()))
                .toList();

        model.addAttribute("danhSachBoSuuTap", danhSachBoSuuTap);
        return "khach-hang/bo-suu-tap-danh-sach";
    }

    /**
     * Xem chi tiết Bộ sưu tập theo Slug kèm Banner lớn và danh sách sản phẩm để khách xem và mua.
     */
    @GetMapping({"/bo-suu-tap/{slug}", "/cua-hang/bo-suu-tap/{slug}"})
    public String chiTietBoSuuTap(@PathVariable("slug") String slug,
                                  @RequestParam(name = "sapXep", defaultValue = "noi-bat") String sapXep,
                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "size", defaultValue = "12") int size,
                                  HttpServletRequest request,
                                  Model model) {
        napThongTinKhachHangDangNhap(request, model);

        BoSuuTap bst;
        try {
            bst = boSuuTapService.layTheoSlug(slug);
        } catch (Exception e) {
            return "redirect:/cua-hang";
        }

        if (!Boolean.TRUE.equals(bst.getKichHoat())) {
            return "redirect:/cua-hang";
        }

        Page<SanPham> pageSanPham = sanPhamService.timKiemVaLocStorefront(
                "", null, bst.getId(), null, null,
                "", null, null, sapXep, page, size
        );

        model.addAttribute("boSuuTap", bst);
        model.addAttribute("pageSanPham", pageSanPham);
        model.addAttribute("danhSachSanPham", pageSanPham.getContent());
        model.addAttribute("tongKetQua", pageSanPham.getTotalElements());
        model.addAttribute("sapXep", sapXep);

        // Danh sách BST khác để khách hàng tiếp tục khám phá
        List<BoSuuTap> bstKhac = boSuuTapService.layTatCa().stream()
                .filter(b -> Boolean.TRUE.equals(b.getKichHoat()) && !Objects.equals(b.getId(), bst.getId()))
                .limit(4)
                .toList();
        model.addAttribute("bstKhac", bstKhac);

        return "khach-hang/bo-suu-tap-chi-tiet";
    }

    /**
     * Xem trang Chi tiết sản phẩm trên Storefront Simplain theo ID (US11).
     * Hỗ trợ các đường dẫn: /cua-hang/san-pham/{id}, /san-pham/{id}
     *
     * <p>Đáp ứng tiêu chí US11:
     * 1. Khi chọn Size/Màu nhảy ảnh tương ứng (hỗ trợ cả tham số mauId, sizeId, bienTheId và chuyển đổi tức thì bằng JS).
     * 2. Hiển thị số lượng tồn kho thực tế của biến thể đang chọn và tổng tồn kho sản phẩm.
     */
    @GetMapping({"/cua-hang/san-pham/{id}", "/san-pham/{id}"})
    public String hienThiChiTietSanPham(@PathVariable("id") Long id,
                                        @RequestParam(name = "mauId", required = false) Long mauId,
                                        @RequestParam(name = "sizeId", required = false) Long sizeId,
                                        @RequestParam(name = "bienTheId", required = false) Long bienTheId,
                                        HttpServletRequest request,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        try {
            SanPham sp = sanPhamService.layChiTietSanPhamStorefront(id);
            return chuanBiDuLieuChiTietSanPham(sp, mauId, sizeId, bienTheId, request, model);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("loiLoc", e.getMessage());
            return "redirect:/cua-hang";
        }
    }

    /**
     * Xem trang Chi tiết sản phẩm theo tham số query (?id=...) hoặc tự động chọn sản phẩm đầu tiên nếu không truyền id (US11).
     */
    @GetMapping("/cua-hang/chi-tiet")
    public String hienThiChiTietTheoThamSo(@RequestParam(name = "id", required = false) Long id,
                                           @RequestParam(name = "mauId", required = false) Long mauId,
                                           @RequestParam(name = "sizeId", required = false) Long sizeId,
                                           @RequestParam(name = "bienTheId", required = false) Long bienTheId,
                                           HttpServletRequest request,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        try {
            Long targetId = id;
            if (targetId == null) {
                List<SanPham> dsMoBan = sanPhamService.layTatCa().stream()
                        .filter(sp -> Boolean.TRUE.equals(sp.getKichHoat()))
                        .toList();
                if (dsMoBan.isEmpty()) {
                    redirectAttributes.addFlashAttribute("loiLoc", "Hiện chưa có sản phẩm nào đang mở bán!");
                    return "redirect:/cua-hang";
                }
                targetId = dsMoBan.get(0).getId();
            }
            SanPham sp = sanPhamService.layChiTietSanPhamStorefront(targetId);
            return chuanBiDuLieuChiTietSanPham(sp, mauId, sizeId, bienTheId, request, model);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("loiLoc", e.getMessage());
            return "redirect:/cua-hang";
        }
    }

    /**
     * Xem trang Chi tiết sản phẩm theo đường dẫn Slug chuẩn SEO (US11).
     * Hỗ trợ cả /cua-hang/san-pham/slug/{slug}, /san-pham/slug/{slug} và /cua-hang/{slug}.
     */
    @GetMapping({"/cua-hang/san-pham/slug/{slug}", "/san-pham/slug/{slug}", "/cua-hang/{slug:[a-z0-9-]+}"})
    public String hienThiChiTietTheoSlug(@PathVariable("slug") String slug,
                                         @RequestParam(name = "mauId", required = false) Long mauId,
                                         @RequestParam(name = "sizeId", required = false) Long sizeId,
                                         @RequestParam(name = "bienTheId", required = false) Long bienTheId,
                                         HttpServletRequest request,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        if ("san-pham".equals(slug) || "bo-suu-tap".equals(slug) || "chi-tiet".equals(slug)) {
            return "redirect:/cua-hang";
        }
        try {
            SanPham sp = sanPhamService.layChiTietSanPhamTheoSlugStorefront(slug);
            return chuanBiDuLieuChiTietSanPham(sp, mauId, sizeId, bienTheId, request, model);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("loiLoc", e.getMessage());
            return "redirect:/cua-hang";
        }
    }

    private String chuanBiDuLieuChiTietSanPham(SanPham sp,
                                               Long mauId,
                                               Long sizeId,
                                               Long bienTheId,
                                               HttpServletRequest request,
                                               Model model) {
        napThongTinKhachHangDangNhap(request, model);

        List<BienTheSanPham> dsBienThe = sp.getDanhSachBienThe();
        BienTheSanPham bienTheDangChon = xacDinhBienTheDangChon(dsBienThe, mauId, sizeId, bienTheId);

        String anhChinhHienThi = (bienTheDangChon != null && bienTheDangChon.getAnhDaiDienUrl() != null && !bienTheDangChon.getAnhDaiDienUrl().isBlank())
                ? bienTheDangChon.getAnhDaiDienUrl()
                : sp.getAnhDaiDienUrl();

        int soLuongTonKhoHienThi = (bienTheDangChon != null && bienTheDangChon.getSoLuong() != null)
                ? bienTheDangChon.getSoLuong()
                : sp.getTongTonKho();

        BigDecimal giaBanHienThi = (bienTheDangChon != null && bienTheDangChon.getGiaBan() != null)
                ? bienTheDangChon.getGiaBan()
                : sp.getGiaCoBan();

        BigDecimal giaGocHienThi = giaBanHienThi;
        ChiTietFlashSale chiTietFlashSaleDangChon = null;
        Map<Long, Map<String, Object>> mapFlashSaleData = new HashMap<>();

        // Kiểm tra khung giờ Flash Sale đang diễn ra (US29)
        if (flashSaleService != null) {
            Map<Long, ChiTietFlashSale> mapFs = flashSaleService.layMapFlashSaleHienTaiTheoBienTheId();
            if (mapFs != null) {
                for (Map.Entry<Long, ChiTietFlashSale> entry : mapFs.entrySet()) {
                    ChiTietFlashSale ct = entry.getValue();
                    int daBan = ct.getSoLuongDaBan() != null ? ct.getSoLuongDaBan() : 0;
                    int moBan = ct.getSoLuongMoBan() != null ? ct.getSoLuongMoBan() : 0;
                    Map<String, Object> fsInfo = new HashMap<>();
                    fsInfo.put("giaFlashSale", ct.getGiaFlashSale());
                    fsInfo.put("giaBanGoc", ct.getGiaBanGoc());
                    fsInfo.put("soLuongMoBan", moBan);
                    fsInfo.put("soLuongDaBan", daBan);
                    fsInfo.put("conLai", Math.max(0, moBan - daBan));
                    fsInfo.put("phanTramGiamGia", ct.getPhanTramGiamGia());
                    mapFlashSaleData.put(entry.getKey(), fsInfo);
                }
            }

            if (bienTheDangChon != null && mapFlashSaleData.containsKey(bienTheDangChon.getId())) {
                Map<String, Object> curFs = mapFlashSaleData.get(bienTheDangChon.getId());
                int conLai = (int) curFs.get("conLai");
                if (conLai > 0) {
                    giaBanHienThi = (BigDecimal) curFs.get("giaFlashSale");
                    chiTietFlashSaleDangChon = mapFs.get(bienTheDangChon.getId());
                }
            }
        }

        String maSkuHienThi = (bienTheDangChon != null && bienTheDangChon.getMaSku() != null)
                ? bienTheDangChon.getMaSku()
                : sp.getMaSanPham();

        String barcodeHienThi = (bienTheDangChon != null && bienTheDangChon.getMaVachBarcode() != null)
                ? bienTheDangChon.getMaVachBarcode()
                : ("BC-" + maSkuHienThi);

        Long mauDangChonId = (bienTheDangChon != null) ? bienTheDangChon.getMaMauSacId() : mauId;
        Long sizeDangChonId = (bienTheDangChon != null) ? bienTheDangChon.getMaKichThuocId() : sizeId;

        List<DanhMuc> danhSachDanhMuc = danhMucService.layTatCa().stream()
                .filter(dm -> Boolean.TRUE.equals(dm.getKichHoat()))
                .toList();

        List<SanPham> sanPhamLienQuan = sanPhamService.laySanPhamLienQuanStorefront(sp.getId(), sp.getMaDanhMucId(), 4);

        // Lấy video quảng cáo sản phẩm từ bảng video_san_pham (Mỗi sản phẩm 1 video)
        VideoSanPham videoSanPham = videoSanPhamService.layVideoKichHoatTheoSanPhamId(sp.getId()).orElse(null);
        if (videoSanPham != null) {
            sp.setVideoUrl(videoSanPham.getVideoUrl());
        }

        model.addAttribute("sanPham", sp);
        model.addAttribute("videoSanPham", videoSanPham);
        model.addAttribute("danhSachBienThe", dsBienThe);
        model.addAttribute("bienTheDangChon", bienTheDangChon);
        model.addAttribute("chiTietFlashSaleDangChon", chiTietFlashSaleDangChon);
        model.addAttribute("giaGocHienThi", giaGocHienThi);
        model.addAttribute("mapFlashSaleData", mapFlashSaleData);
        model.addAttribute("anhChinhHienThi", anhChinhHienThi);
        model.addAttribute("soLuongTonKhoHienThi", soLuongTonKhoHienThi);
        model.addAttribute("giaBanHienThi", giaBanHienThi);
        model.addAttribute("maSkuHienThi", maSkuHienThi);
        model.addAttribute("barcodeHienThi", barcodeHienThi);
        model.addAttribute("mauDangChonId", mauDangChonId);
        model.addAttribute("sizeDangChonId", sizeDangChonId);
        model.addAttribute("danhSachDanhMuc", danhSachDanhMuc);
        model.addAttribute("sanPhamLienQuan", sanPhamLienQuan);

        boolean daYeuThich = false;
        List<ChiTietDonHang> dsChiTietDuDieuKien = Collections.emptyList();
        ThongTinKhachHangResponse kh = (ThongTinKhachHangResponse) model.getAttribute("khachHangDangNhap");
        if (kh != null && sp.getId() != null) {
            daYeuThich = sanPhamYeuThichService.kiemTraDaYeuThich(kh.getId(), sp.getId());
            dsChiTietDuDieuKien = danhGiaSanPhamService.layDanhSachChiTietDonHangDuDieuKien(kh.getId(), sp.getId());
        }
        model.addAttribute("daYeuThich", daYeuThich);
        model.addAttribute("dsChiTietDuDieuKien", dsChiTietDuDieuKien);

        // Dữ liệu Đánh giá sản phẩm đã mua (US15)
        ThongKeDanhGiaResponse thongKeDanhGia = danhGiaSanPhamService.layThongKeDanhGiaTheoSanPham(sp.getId());
        List<DanhGiaSanPham> danhSachDanhGia = danhGiaSanPhamService.layDanhSachDanhGiaTheoSanPham(sp.getId());
        model.addAttribute("thongKeDanhGia", thongKeDanhGia);
        model.addAttribute("danhSachDanhGia", danhSachDanhGia);
        if (!model.containsAttribute("danhGiaRequest")) {
            model.addAttribute("danhGiaRequest", new DanhGiaRequest());
        }

        return "khach-hang/chi-tiet-san-pham";
    }

    private BienTheSanPham xacDinhBienTheDangChon(List<BienTheSanPham> dsBienThe,
                                                  Long mauId,
                                                  Long sizeId,
                                                  Long bienTheId) {
        if (dsBienThe == null || dsBienThe.isEmpty()) {
            return null;
        }
        if (bienTheId != null) {
            for (BienTheSanPham bt : dsBienThe) {
                if (Objects.equals(bt.getId(), bienTheId)) {
                    return bt;
                }
            }
        }
        if (mauId != null && sizeId != null) {
            for (BienTheSanPham bt : dsBienThe) {
                if (Objects.equals(bt.getMaMauSacId(), mauId) && Objects.equals(bt.getMaKichThuocId(), sizeId)) {
                    return bt;
                }
            }
        }
        if (mauId != null) {
            for (BienTheSanPham bt : dsBienThe) {
                if (Objects.equals(bt.getMaMauSacId(), mauId)) {
                    return bt;
                }
            }
        }
        if (sizeId != null) {
            for (BienTheSanPham bt : dsBienThe) {
                if (Objects.equals(bt.getMaKichThuocId(), sizeId)) {
                    return bt;
                }
            }
        }
        return dsBienThe.get(0);
    }

    private void napThongTinKhachHangDangNhap(HttpServletRequest request, Model model) {
        ThongTinKhachHangResponse kh = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (KhachHangController.COOKIE_TOKEN.equals(cookie.getName())
                        && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                    try {
                        kh = khachHangService.layThongTinTuToken(cookie.getValue());
                        model.addAttribute("khachHangDangNhap", kh);

                        // Nạp danh sách ID sản phẩm khách hàng đang yêu thích (US14)
                        List<Long> danhSachYeuThichIds = sanPhamYeuThichService.layDanhSachMaSanPhamYeuThich(kh.getId());
                        model.addAttribute("danhSachYeuThichIds", danhSachYeuThichIds);
                        model.addAttribute("soLuongYeuThich", danhSachYeuThichIds.size());

                        // Nạp thông tin Hạng thành viên & Điểm tích lũy (US16)
                        if (loyaltyHoiVienService != null) {
                            HoiVienResponse hoiVien = loyaltyHoiVienService.layThongTinHoiVien(kh.getId());
                            model.addAttribute("hoiVien", hoiVien);
                        }
                    } catch (Exception ignored) {
                    }
                    break;
                }
            }
        }

        // Nạp danh sách Voucher hot khả dụng trên sàn để hiển thị lên Trang chủ (US27)
        if (voucherKhachHangService != null) {
            try {
                Long khachHangId = (kh != null) ? kh.getId() : null;
                List<VoucherKhachHangDto> dsVoucher = voucherKhachHangService.layDanhSachVoucherSanCoChoKhachHang(khachHangId);
                model.addAttribute("dsVoucherHot", dsVoucher);
            } catch (Exception ignored) {
                model.addAttribute("dsVoucherHot", Collections.emptyList());
            }
        }
    }
}
