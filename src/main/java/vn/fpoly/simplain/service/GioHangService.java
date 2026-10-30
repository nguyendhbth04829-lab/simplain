package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.BocComboDto;
import vn.fpoly.simplain.dto.CapNhatSoLuongRequest;
import vn.fpoly.simplain.dto.GioHangResponse;
import vn.fpoly.simplain.dto.ItemGioHangDto;
import vn.fpoly.simplain.dto.ThemComboVaoGioRequest;
import vn.fpoly.simplain.dto.ThemGioHangRequest;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.entity.ChiTietFlashSale;
import vn.fpoly.simplain.entity.ChiTietGioHang;
import vn.fpoly.simplain.entity.GioHang;
import vn.fpoly.simplain.entity.GoiCombo;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.repository.ChiTietFlashSaleRepository;
import vn.fpoly.simplain.repository.ChiTietGioHangRepository;
import vn.fpoly.simplain.repository.GioHangRepository;
import vn.fpoly.simplain.repository.GoiComboRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Service quản lý Giỏ hàng và Tính năng bó hàng Combo (US17).
 * - Tiêu chí 1: Thêm/Sửa/Xóa biến thể vào giỏ.
 * - Tiêu chí 2: Tính năng bó hàng Combo (Sinh nhom_combo_token duy nhất).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GioHangService {

    private final GioHangRepository gioHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final GoiComboRepository goiComboRepository;
    private final BienTheSanPhamService bienTheSanPhamService;
    private final ChiTietFlashSaleRepository chiTietFlashSaleRepository;
    private final FlashSaleService flashSaleService;
    private final SanPhamRepository sanPhamRepository;

    /**
     * Lấy hoặc tạo mới giỏ hàng cho Khách hàng (đăng nhập) hoặc Khách vãng lai (qua Cart ID Cookie).
     * Tự động hợp nhất giỏ hàng vãng lai khi khách hàng đăng nhập.
     */
    @Transactional
    public GioHang layHoacTaoGioHang(Long khachHangId, Long cartIdVangLai) {
        if (khachHangId != null) {
            GioHang gioHangKhach = gioHangRepository.findByMaKhachHangId(khachHangId)
                    .orElseGet(() -> {
                        GioHang moi = GioHang.builder()
                                .maKhachHangId(khachHangId)
                                .ngayCapNhat(LocalDateTime.now())
                                .build();
                        return gioHangRepository.save(moi);
                    });

            // Nếu trước đó khách có giỏ hàng vãng lai -> hợp nhất vào giỏ khách hàng
            if (cartIdVangLai != null && !Objects.equals(gioHangKhach.getId(), cartIdVangLai)) {
                hopNhatGioHang(cartIdVangLai, gioHangKhach.getId());
            }

            return gioHangKhach;
        }

        // Khách vãng lai
        if (cartIdVangLai != null) {
            Optional<GioHang> optVangLai = gioHangRepository.findById(cartIdVangLai);
            if (optVangLai.isPresent() && optVangLai.get().getMaKhachHangId() == null) {
                return optVangLai.get();
            }
        }

        // Tạo giỏ vãng lai mới
        GioHang vangLaiMoi = GioHang.builder()
                .maKhachHangId(null)
                .ngayCapNhat(LocalDateTime.now())
                .build();
        return gioHangRepository.save(vangLaiMoi);
    }

    /**
     * Hợp nhất giỏ hàng vãng lai vào giỏ khách hàng sau khi đăng nhập.
     */
    @Transactional
    public void hopNhatGioHang(Long cartIdVangLai, Long cartIdKhachHang) {
        if (cartIdVangLai == null || cartIdKhachHang == null || Objects.equals(cartIdVangLai, cartIdKhachHang)) {
            return;
        }

        Optional<GioHang> optVangLai = gioHangRepository.findById(cartIdVangLai);
        if (optVangLai.isEmpty() || optVangLai.get().getMaKhachHangId() != null) {
            return; // Không phải giỏ vãng lai
        }

        List<ChiTietGioHang> itemsVangLai = chiTietGioHangRepository.findByMaGioHangIdOrderByNgayThemDesc(cartIdVangLai);
        for (ChiTietGioHang item : itemsVangLai) {
            if (item.getNhomComboToken() == null) {
                // Biến thể lẻ: Kiểm tra nếu trong giỏ khách đã có -> cộng dồn số lượng
                Optional<ChiTietGioHang> existing = chiTietGioHangRepository
                        .findByMaGioHangIdAndMaBienTheIdAndNhomComboTokenIsNull(cartIdKhachHang, item.getMaBienTheId());
                if (existing.isPresent()) {
                    ChiTietGioHang ex = existing.get();
                    ex.setSoLuong(ex.getSoLuong() + item.getSoLuong());
                    chiTietGioHangRepository.save(ex);
                    chiTietGioHangRepository.delete(item);
                } else {
                    item.setMaGioHangId(cartIdKhachHang);
                    chiTietGioHangRepository.save(item);
                }
            } else {
                // Bó combo: Chuyển nguyên nhóm sang giỏ khách
                item.setMaGioHangId(cartIdKhachHang);
                chiTietGioHangRepository.save(item);
            }
        }

        // Xóa giỏ vãng lai cũ
        try {
            gioHangRepository.delete(optVangLai.get());
        } catch (Exception e) {
            log.warn("Không thể xóa giỏ vãng lai cũ {}: {}", cartIdVangLai, e.getMessage());
        }
    }

    /**
     * Thêm biến thể sản phẩm lẻ vào giỏ hàng (US17 - Tiêu chí 1).
     */
    @Transactional
    public ChiTietGioHang themBienTheVaoGio(Long khachHangId, Long cartIdVangLai, ThemGioHangRequest req) {
        if (req == null || req.getMaBienTheId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn biến thể sản phẩm cần thêm vào giỏ!");
        }
        int soLuongThem = (req.getSoLuong() == null || req.getSoLuong() < 1) ? 1 : req.getSoLuong();

        BienTheSanPham bienThe = bienTheSanPhamService.layTheoId(req.getMaBienTheId());
        if (bienThe.getKichHoat() != null && !bienThe.getKichHoat()) {
            throw new IllegalArgumentException("Biến thể sản phẩm này hiện đang tạm ngừng kinh doanh!");
        }

        int tonKho = (bienThe.getSoLuong() != null) ? bienThe.getSoLuong() : 0;
        if (tonKho <= 0) {
            throw new IllegalArgumentException("Sản phẩm đã hết hàng trong kho!");
        }

        // Kiểm tra xem biến thể có đang trong khung giờ Flash Sale không (US29)
        BigDecimal donGiaApDung = (bienThe.getGiaBan() != null) ? bienThe.getGiaBan() : BigDecimal.ZERO;
        ChiTietFlashSale activeFs = null;
        if (flashSaleService != null) {
            activeFs = flashSaleService.timChiTietFlashSaleDangApDungCuaBienThe(bienThe.getId()).orElse(null);
        }
        if (activeFs == null && chiTietFlashSaleRepository != null) {
            List<ChiTietFlashSale> dsFs = chiTietFlashSaleRepository.timChiTietFlashSaleDangApDung(bienThe.getId(), LocalDateTime.now());
            if (!dsFs.isEmpty()) {
                activeFs = dsFs.get(0);
            }
        }
        if (activeFs != null) {
            int daBan = activeFs.getSoLuongDaBan() != null ? activeFs.getSoLuongDaBan() : 0;
            int soLuongMoBan = activeFs.getSoLuongMoBan() != null ? activeFs.getSoLuongMoBan() : 0;
            int fsConLai = Math.max(0, soLuongMoBan - daBan);
            if (fsConLai <= 0) {
                log.info("Biến thể [{}] đã hết suất mở bán Flash Sale, áp dụng giá bán thường.", bienThe.getMaSku());
            } else {
                if (soLuongThem > fsConLai) {
                    throw new IllegalArgumentException("Số lượng đặt mua (" + soLuongThem + ") vượt quá số lượng Flash Sale còn lại (" + fsConLai + " cái)!");
                }
                if (activeFs.getGiaFlashSale() != null) {
                    donGiaApDung = activeFs.getGiaFlashSale();
                }
            }
        }

        GioHang gioHang = layHoacTaoGioHang(khachHangId, cartIdVangLai);

        // Kiểm tra biến thể này đã có trong giỏ (mục lẻ) chưa
        Optional<ChiTietGioHang> optExisting = chiTietGioHangRepository
                .findByMaGioHangIdAndMaBienTheIdAndNhomComboTokenIsNull(gioHang.getId(), bienThe.getId());

        ChiTietGioHang chiTiet;
        if (optExisting.isPresent()) {
            chiTiet = optExisting.get();
            int tongSoLuong = chiTiet.getSoLuong() + soLuongThem;
            if (tongSoLuong > tonKho) {
                throw new IllegalArgumentException("Số lượng trong giỏ (" + tongSoLuong + ") vượt quá tồn kho hiện có (" + tonKho + " cái)!");
            }
            if (activeFs != null) {
                int daBan = activeFs.getSoLuongDaBan() != null ? activeFs.getSoLuongDaBan() : 0;
                int soLuongMoBan = activeFs.getSoLuongMoBan() != null ? activeFs.getSoLuongMoBan() : 0;
                int fsConLai = Math.max(0, soLuongMoBan - daBan);
                if (fsConLai > 0 && tongSoLuong > fsConLai) {
                    throw new IllegalArgumentException("Tổng số lượng trong giỏ (" + tongSoLuong + ") vượt quá số lượng Flash Sale khả dụng (" + fsConLai + " cái)!");
                }
            }
            chiTiet.setSoLuong(tongSoLuong);
            chiTiet.setDonGia(donGiaApDung);
        } else {
            if (soLuongThem > tonKho) {
                throw new IllegalArgumentException("Số lượng thêm (" + soLuongThem + ") vượt quá tồn kho hiện có (" + tonKho + " cái)!");
            }
            chiTiet = ChiTietGioHang.builder()
                    .maGioHangId(gioHang.getId())
                    .maBienTheId(bienThe.getId())
                    .maComboId(null)
                    .nhomComboToken(null)
                    .soLuong(soLuongThem)
                    .donGia(donGiaApDung)
                    .ngayThem(LocalDateTime.now())
                    .build();
        }

        gioHang.setNgayCapNhat(LocalDateTime.now());
        gioHangRepository.save(gioHang);
        return chiTietGioHangRepository.save(chiTiet);
    }

    /**
     * Bó hàng Combo vào giỏ: Tự động sinh nhom_combo_token duy nhất (US17 - Tiêu chí 2).
     * Gom các món con vào cùng token để đồng bộ giá ưu đãi và thao tác toàn vẹn.
     */
    @Transactional
    public String themComboVaoGio(Long khachHangId, Long cartIdVangLai, ThemComboVaoGioRequest req) {
        if (req == null || req.getMaComboId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn gói Combo cần thêm!");
        }
        if (req.getDanhSachBienTheIds() == null || req.getDanhSachBienTheIds().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn đầy đủ các món trong gói Combo!");
        }
        int soLuongCombo = (req.getSoLuongCombo() == null || req.getSoLuongCombo() < 1) ? 1 : req.getSoLuongCombo();

        GoiCombo combo = goiComboRepository.findById(req.getMaComboId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói Combo ID: " + req.getMaComboId()));

        if (combo.getKichHoat() != null && !combo.getKichHoat()) {
            throw new IllegalArgumentException("Gói Combo [" + combo.getTenCombo() + "] hiện đã ngừng áp dụng!");
        }

        int soMonQuyDinh = (combo.getSoMonQuyDinh() != null) ? combo.getSoMonQuyDinh() : 1;
        if (req.getDanhSachBienTheIds().size() != soMonQuyDinh) {
            throw new IllegalArgumentException("Gói Combo [" + combo.getTenCombo() + "] yêu cầu chọn chính xác "
                    + soMonQuyDinh + " món! (Hiện bạn đã chọn " + req.getDanhSachBienTheIds().size() + " món)");
        }

        // Kiểm tra tồn kho từng biến thể trong combo (tính tổng số lượng cần cho mỗi biến thể)
        Map<Long, Integer> skuCount = new java.util.HashMap<>();
        for (Long bienTheId : req.getDanhSachBienTheIds()) {
            skuCount.put(bienTheId, skuCount.getOrDefault(bienTheId, 0) + 1);
        }
        for (Map.Entry<Long, Integer> entry : skuCount.entrySet()) {
            Long bienTheId = entry.getKey();
            int soLuongCan = entry.getValue() * soLuongCombo;
            BienTheSanPham bt = bienTheSanPhamService.layTheoId(bienTheId);
            int tonKho = (bt.getSoLuong() != null) ? bt.getSoLuong() : 0;
            if (tonKho < soLuongCan) {
                throw new IllegalArgumentException("Sản phẩm [" + bt.getTenSanPham() + " - " + bt.getTenKichThuoc()
                        + "] trong combo không đủ tồn kho (cần " + soLuongCan + " cái, chỉ còn " + tonKho + " cái)!");
            }
        }

        GioHang gioHang = layHoacTaoGioHang(khachHangId, cartIdVangLai);

        // Tiêu chí 2: Sinh chuỗi nhom_combo_token duy nhất cho bó combo này
        String nhomComboToken = "COMBO-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();

        LocalDateTime thoiGianThem = LocalDateTime.now();
        for (Long bienTheId : req.getDanhSachBienTheIds()) {
            ChiTietGioHang monTrongCombo = ChiTietGioHang.builder()
                    .maGioHangId(gioHang.getId())
                    .maBienTheId(bienTheId)
                    .maComboId(combo.getId())
                    .nhomComboToken(nhomComboToken)
                    .soLuong(soLuongCombo)
                    .donGia(combo.getGiaCombo()) // Giá combo ưu đãi
                    .ngayThem(thoiGianThem)
                    .build();
            chiTietGioHangRepository.save(monTrongCombo);
        }

        gioHang.setNgayCapNhat(LocalDateTime.now());
        gioHangRepository.save(gioHang);

        log.info("Đã bó combo [{}] thành công với token: {} vào giỏ hàng ID: {}", combo.getMaCombo(), nhomComboToken, gioHang.getId());
        return nhomComboToken;
    }

    /**
     * Cập nhật số lượng của mục lẻ hoặc cả bó combo (US17 - Tiêu chí 1 & 2).
     */
    @Transactional
    public void capNhatSoLuong(Long khachHangId, Long cartIdVangLai, CapNhatSoLuongRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Dữ liệu cập nhật số lượng không hợp lệ!");
        }

        GioHang gioHang = layHoacTaoGioHang(khachHangId, cartIdVangLai);
        int soLuongMoi = (req.getSoLuong() == null) ? 1 : req.getSoLuong();

        // 1. Cập nhật theo nhóm Combo Token (Tiêu chí 2)
        if (req.getNhomComboToken() != null && !req.getNhomComboToken().isBlank()) {
            List<ChiTietGioHang> itemsInCombo = chiTietGioHangRepository
                    .findByMaGioHangIdAndNhomComboToken(gioHang.getId(), req.getNhomComboToken().trim());

            if (itemsInCombo.isEmpty() && khachHangId == null) {
                // Kiểm tra nếu là khách vãng lai bị lệch cartId do cookie chưa kịp đồng bộ
                List<ChiTietGioHang> itemsTimKiem = chiTietGioHangRepository.findByNhomComboToken(req.getNhomComboToken().trim());
                if (!itemsTimKiem.isEmpty()) {
                    Long maGioHangGoc = itemsTimKiem.get(0).getMaGioHangId();
                    Optional<GioHang> optGioHangGoc = gioHangRepository.findById(maGioHangGoc);
                    if (optGioHangGoc.isPresent() && optGioHangGoc.get().getMaKhachHangId() == null) {
                        gioHang = optGioHangGoc.get();
                        itemsInCombo = itemsTimKiem;
                    }
                }
            }

            if (itemsInCombo.isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy bó Combo trong giỏ hàng!");
            }

            if (soLuongMoi <= 0) {
                // Xóa toàn bộ bó combo nếu số lượng <= 0
                chiTietGioHangRepository.deleteByMaGioHangIdAndNhomComboToken(gioHang.getId(), req.getNhomComboToken().trim());
            } else {
                // Kiểm tra tồn kho của tất cả món con trước khi tăng (nhóm theo biến thể)
                Map<Long, Integer> skuCount = new java.util.HashMap<>();
                for (ChiTietGioHang it : itemsInCombo) {
                    skuCount.put(it.getMaBienTheId(), skuCount.getOrDefault(it.getMaBienTheId(), 0) + 1);
                }
                for (Map.Entry<Long, Integer> entry : skuCount.entrySet()) {
                    Long bienTheId = entry.getKey();
                    int soLuongCan = entry.getValue() * soLuongMoi;
                    BienTheSanPham bt = bienTheSanPhamService.layTheoId(bienTheId);
                    int ton = (bt.getSoLuong() != null) ? bt.getSoLuong() : 0;
                    if (soLuongCan > ton) {
                        throw new IllegalArgumentException("Món [" + bt.getTenSanPham() + " - " + bt.getTenKichThuoc()
                                + "] trong combo không đủ tồn kho (cần " + soLuongCan + " cái, chỉ còn " + ton + " cái)!");
                    }
                }

                // Cập nhật đồng bộ số lượng tất cả món trong bó
                for (ChiTietGioHang it : itemsInCombo) {
                    it.setSoLuong(soLuongMoi);
                    chiTietGioHangRepository.save(it);
                }
            }

            gioHang.setNgayCapNhat(LocalDateTime.now());
            gioHangRepository.save(gioHang);
            return;
        }

        // 2. Cập nhật mục lẻ thông thường (Tiêu chí 1)
        if (req.getChiTietGioHangId() == null) {
            throw new IllegalArgumentException("Vui lòng cung cấp ID chi tiết giỏ hàng hoặc mã nhóm Combo!");
        }

        ChiTietGioHang chiTiet = chiTietGioHangRepository.findById(req.getChiTietGioHangId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mục hàng ID: " + req.getChiTietGioHangId()));

        if (!Objects.equals(chiTiet.getMaGioHangId(), gioHang.getId())) {
            // Kiểm tra an toàn: nếu khách vãng lai bị lệch cartId do cookie chưa kịp đồng bộ
            if (khachHangId == null) {
                Optional<GioHang> optGioHangGoc = gioHangRepository.findById(chiTiet.getMaGioHangId());
                if (optGioHangGoc.isPresent() && optGioHangGoc.get().getMaKhachHangId() == null) {
                    gioHang = optGioHangGoc.get();
                } else {
                    throw new IllegalArgumentException("Mục hàng này không thuộc giỏ hàng hiện tại!");
                }
            } else {
                throw new IllegalArgumentException("Mục hàng này không thuộc giỏ hàng hiện tại!");
            }
        }

        if (soLuongMoi <= 0) {
            chiTietGioHangRepository.delete(chiTiet);
        } else {
            BienTheSanPham bt = bienTheSanPhamService.layTheoId(chiTiet.getMaBienTheId());
            int ton = (bt.getSoLuong() != null) ? bt.getSoLuong() : 0;
            if (soLuongMoi > ton) {
                throw new IllegalArgumentException("Số lượng yêu cầu (" + soLuongMoi + ") vượt quá tồn kho hiện có (" + ton + " cái)!");
            }
            ChiTietFlashSale activeFs = null;
            if (flashSaleService != null) {
                activeFs = flashSaleService.timChiTietFlashSaleDangApDungCuaBienThe(chiTiet.getMaBienTheId()).orElse(null);
            }
            if (activeFs == null && chiTietFlashSaleRepository != null) {
                List<ChiTietFlashSale> dsFs = chiTietFlashSaleRepository.timChiTietFlashSaleDangApDung(chiTiet.getMaBienTheId(), LocalDateTime.now());
                if (!dsFs.isEmpty()) {
                    activeFs = dsFs.get(0);
                }
            }
            if (activeFs != null) {
                int daBan = activeFs.getSoLuongDaBan() != null ? activeFs.getSoLuongDaBan() : 0;
                int soLuongMoBan = activeFs.getSoLuongMoBan() != null ? activeFs.getSoLuongMoBan() : 0;
                int fsConLai = Math.max(0, soLuongMoBan - daBan);
                if (fsConLai > 0 && soLuongMoi > fsConLai) {
                    throw new IllegalArgumentException("Số lượng Flash Sale cho phép tối đa là " + fsConLai + " sản phẩm!");
                }
            }
            chiTiet.setSoLuong(soLuongMoi);
            chiTietGioHangRepository.save(chiTiet);
        }

        gioHang.setNgayCapNhat(LocalDateTime.now());
        gioHangRepository.save(gioHang);
    }

    /**
     * Xóa 1 mục lẻ khỏi giỏ hàng (US17 - Tiêu chí 1).
     */
    @Transactional
    public void xoaChiTiet(Long khachHangId, Long cartIdVangLai, Long chiTietId) {
        if (chiTietId == null) {
            throw new IllegalArgumentException("ID mục cần xóa không hợp lệ!");
        }
        GioHang gioHang = layHoacTaoGioHang(khachHangId, cartIdVangLai);
        ChiTietGioHang chiTiet = chiTietGioHangRepository.findById(chiTietId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mục hàng ID: " + chiTietId));

        if (!Objects.equals(chiTiet.getMaGioHangId(), gioHang.getId())) {
            throw new IllegalArgumentException("Mục hàng này không thuộc giỏ hàng của bạn!");
        }

        // Nếu mục này thuộc combo, xóa cả bó combo tương ứng
        if (chiTiet.getNhomComboToken() != null && !chiTiet.getNhomComboToken().isBlank()) {
            chiTietGioHangRepository.deleteByMaGioHangIdAndNhomComboToken(gioHang.getId(), chiTiet.getNhomComboToken());
        } else {
            chiTietGioHangRepository.delete(chiTiet);
        }

        gioHang.setNgayCapNhat(LocalDateTime.now());
        gioHangRepository.save(gioHang);
    }

    /**
     * Xóa trọn vẹn cả Bó Combo theo nhom_combo_token (US17 - Tiêu chí 2).
     */
    @Transactional
    public void xoaCombo(Long khachHangId, Long cartIdVangLai, String nhomComboToken) {
        if (nhomComboToken == null || nhomComboToken.isBlank()) {
            throw new IllegalArgumentException("Mã nhóm Combo không hợp lệ!");
        }
        GioHang gioHang = layHoacTaoGioHang(khachHangId, cartIdVangLai);
        chiTietGioHangRepository.deleteByMaGioHangIdAndNhomComboToken(gioHang.getId(), nhomComboToken.trim());

        gioHang.setNgayCapNhat(LocalDateTime.now());
        gioHangRepository.save(gioHang);
    }

    /**
     * Xóa toàn bộ giỏ hàng (US17 - Tiêu chí 1).
     */
    @Transactional
    public void xoaHetGioHang(Long khachHangId, Long cartIdVangLai) {
        GioHang gioHang = layHoacTaoGioHang(khachHangId, cartIdVangLai);
        chiTietGioHangRepository.deleteByMaGioHangId(gioHang.getId());
        gioHang.setNgayCapNhat(LocalDateTime.now());
        gioHangRepository.save(gioHang);
    }

    /**
     * Đếm tổng số lượng món trong giỏ để hiển thị Badge Navbar.
     */
    @Transactional(readOnly = true)
    public int demTongSoLuong(Long khachHangId, Long cartIdVangLai) {
        GioHang gioHang = layHoacTaoGioHang(khachHangId, cartIdVangLai);
        List<ChiTietGioHang> tatCaChiTiet = chiTietGioHangRepository.findByMaGioHangIdOrderByNgayThemDesc(gioHang.getId());

        int tong = 0;
        // Gom combo để mỗi gói combo tính theo số lượng combo
        Map<String, Integer> comboCount = new LinkedHashMap<>();
        for (ChiTietGioHang it : tatCaChiTiet) {
            if (it.getNhomComboToken() == null) {
                tong += (it.getSoLuong() != null ? it.getSoLuong() : 1);
            } else {
                comboCount.putIfAbsent(it.getNhomComboToken(), it.getSoLuong() != null ? it.getSoLuong() : 1);
            }
        }
        for (Integer soCombo : comboCount.values()) {
            tong += soCombo;
        }
        return tong;
    }

    /**
     * Lấy toàn bộ thông tin giỏ hàng phục vụ Render giao diện giỏ hàng (US17).
     */
    @Transactional(readOnly = true)
    public GioHangResponse layThongTinGioHang(Long khachHangId, Long cartIdVangLai) {
        GioHang gioHang = layHoacTaoGioHang(khachHangId, cartIdVangLai);
        List<ChiTietGioHang> tatCaChiTiet = chiTietGioHangRepository.findByMaGioHangIdOrderByNgayThemDesc(gioHang.getId());

        List<ItemGioHangDto> danhSachLe = new ArrayList<>();
        Map<String, List<ChiTietGioHang>> mapCombo = new LinkedHashMap<>();

        // 1. Phân loại mục lẻ và bó combo
        for (ChiTietGioHang it : tatCaChiTiet) {
            if (it.getNhomComboToken() == null || it.getNhomComboToken().isBlank()) {
                danhSachLe.add(chuyenDoiSangDto(it));
            } else {
                mapCombo.computeIfAbsent(it.getNhomComboToken(), k -> new ArrayList<>()).add(it);
            }
        }

        // 2. Đóng gói các bó combo
        List<BocComboDto> danhSachCombo = new ArrayList<>();
        BigDecimal tongTienCombo = BigDecimal.ZERO;
        int tongSoLuongCombo = 0;

        for (Map.Entry<String, List<ChiTietGioHang>> entry : mapCombo.entrySet()) {
            String token = entry.getKey();
            List<ChiTietGioHang> monCons = entry.getValue();
            if (monCons.isEmpty()) continue;

            ChiTietGioHang monDau = monCons.get(0);
            Long comboId = monDau.getMaComboId();
            GoiCombo combo = (comboId != null) ? goiComboRepository.findById(comboId).orElse(null) : null;

            BigDecimal giaCombo = (combo != null && combo.getGiaCombo() != null)
                    ? combo.getGiaCombo()
                    : BigDecimal.ZERO;

            Integer soLuongCombo = (monDau.getSoLuong() != null && monDau.getSoLuong() > 0)
                    ? monDau.getSoLuong()
                    : 1;

            BigDecimal thanhTienBoc = giaCombo.multiply(BigDecimal.valueOf(soLuongCombo));
            tongTienCombo = tongTienCombo.add(thanhTienBoc);
            tongSoLuongCombo += soLuongCombo;

            List<ItemGioHangDto> danhSachMonConDto = new ArrayList<>();
            for (ChiTietGioHang con : monCons) {
                danhSachMonConDto.add(chuyenDoiSangDto(con));
            }

            BocComboDto bocComboDto = BocComboDto.builder()
                    .nhomComboToken(token)
                    .maComboId(comboId)
                    .maCombo(combo != null ? combo.getMaCombo() : "COMBO")
                    .tenCombo(combo != null ? combo.getTenCombo() : "Gói Combo Ưu Đãi")
                    .duongDanSlug(combo != null ? combo.getDuongDanSlug() : "")
                    .giaCombo(giaCombo)
                    .soLuong(soLuongCombo)
                    .thanhTien(thanhTienBoc)
                    .danhSachMonCon(danhSachMonConDto)
                    .build();

            danhSachCombo.add(bocComboDto);
        }

        // 3. Tính tổng tiền lẻ
        BigDecimal tongTienLe = BigDecimal.ZERO;
        int tongSoLuongLe = 0;
        for (ItemGioHangDto le : danhSachLe) {
            tongTienLe = tongTienLe.add(le.getThanhTien());
            tongSoLuongLe += le.getSoLuong();
        }

        BigDecimal tongTienHang = tongTienLe.add(tongTienCombo);
        int tongSoLuong = tongSoLuongLe + tongSoLuongCombo;

        return GioHangResponse.builder()
                .gioHangId(gioHang.getId())
                .tongSoLuongItem(tongSoLuong)
                .tongTienHang(tongTienHang)
                .tienGiamGia(BigDecimal.ZERO)
                .tongThanhToan(tongTienHang)
                .danhSachItemLe(danhSachLe)
                .danhSachBocCombo(danhSachCombo)
                .gioHangRong(tatCaChiTiet.isEmpty())
                .build();
    }

    /**
     * Chuyển đổi ChiTietGioHang sang ItemGioHangDto với đầy đủ thông tin biến thể, màu, size, tồn kho.
     */
    private ItemGioHangDto chuyenDoiSangDto(ChiTietGioHang chiTiet) {
        BienTheSanPham bt = null;
        try {
            bt = bienTheSanPhamService.layTheoId(chiTiet.getMaBienTheId());
        } catch (Exception e) {
            log.warn("Không tìm thấy biến thể ID: {} cho chi tiết giỏ hàng {}", chiTiet.getMaBienTheId(), chiTiet.getId());
        }

        BigDecimal giaBanGoc = (bt != null && bt.getGiaBan() != null) ? bt.getGiaBan() : BigDecimal.ZERO;
        BigDecimal donGia = giaBanGoc;
        boolean laFlashSale = false;

        // Ưu tiên giá Flash Sale nếu đang trong khung giờ vàng hợp lệ hoặc đợt Flash Sale đang kích hoạt (US29)
        ChiTietFlashSale activeFs = null;
        if (flashSaleService != null) {
            activeFs = flashSaleService.timChiTietFlashSaleDangApDungCuaBienThe(chiTiet.getMaBienTheId()).orElse(null);
        }
        if (activeFs == null && chiTietFlashSaleRepository != null) {
            List<ChiTietFlashSale> dsFs = chiTietFlashSaleRepository.timChiTietFlashSaleDangApDung(chiTiet.getMaBienTheId(), LocalDateTime.now());
            if (!dsFs.isEmpty()) {
                activeFs = dsFs.get(0);
            }
        }

        if (activeFs != null) {
            int daBan = activeFs.getSoLuongDaBan() != null ? activeFs.getSoLuongDaBan() : 0;
            int soLuongMoBan = activeFs.getSoLuongMoBan() != null ? activeFs.getSoLuongMoBan() : 0;
            if (soLuongMoBan > daBan && activeFs.getGiaFlashSale() != null) {
                donGia = activeFs.getGiaFlashSale();
                laFlashSale = true;
            }
        } else if (chiTiet.getDonGia() != null && chiTiet.getDonGia().compareTo(BigDecimal.ZERO) > 0) {
            donGia = chiTiet.getDonGia();
            if (giaBanGoc.compareTo(donGia) > 0) {
                laFlashSale = true;
            }
        }

        // Tự động đồng bộ lại donGia lưu trong chiTiet nếu giá áp dụng Flash Sale khác với DB
        if (laFlashSale && !donGia.equals(chiTiet.getDonGia())) {
            chiTiet.setDonGia(donGia);
            chiTietGioHangRepository.save(chiTiet);
        }

        int soLuong = (chiTiet.getSoLuong() != null && chiTiet.getSoLuong() > 0) ? chiTiet.getSoLuong() : 1;
        BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        int tonKho = (bt != null && bt.getSoLuong() != null) ? bt.getSoLuong() : 0;

        int phanTramGiam = 0;
        if (laFlashSale && giaBanGoc.compareTo(BigDecimal.ZERO) > 0 && giaBanGoc.compareTo(donGia) > 0) {
            phanTramGiam = giaBanGoc.subtract(donGia)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(giaBanGoc, 0, java.math.RoundingMode.HALF_UP)
                    .intValue();
        }

        String slug = "";
        if (bt != null && bt.getDuongDanSlug() != null && !bt.getDuongDanSlug().isBlank()) {
            slug = bt.getDuongDanSlug();
        } else if (bt != null && bt.getMaSanPhamId() != null && sanPhamRepository != null) {
            slug = sanPhamRepository.findById(bt.getMaSanPhamId())
                    .map(SanPham::getDuongDanSlug)
                    .orElse("");
        }

        return ItemGioHangDto.builder()
                .chiTietId(chiTiet.getId())
                .maBienTheId(chiTiet.getMaBienTheId())
                .maSanPhamId(bt != null ? bt.getMaSanPhamId() : null)
                .tenSanPham(bt != null && bt.getTenSanPham() != null ? bt.getTenSanPham() : "Sản phẩm")
                .duongDanSlug(slug)
                .maSku(bt != null ? bt.getMaSku() : "")
                .tenMau(bt != null ? bt.getTenMauSac() : "")
                .maMauHex(bt != null ? bt.getMaMauHex() : "#333333")
                .tenKichThuoc(bt != null ? bt.getTenKichThuoc() : "")
                .anhDaiDien(bt != null ? bt.getAnhDaiDienUrl() : "/images/placeholder.jpg")
                .donGia(donGia)
                .giaBanGoc(giaBanGoc)
                .laFlashSale(laFlashSale)
                .phanTramGiamGia(phanTramGiam)
                .soLuong(soLuong)
                .thanhTien(thanhTien)
                .soLuongTon(tonKho)
                .hetHang(tonKho <= 0)
                .build();
    }
}
