package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.ChiTietFlashSaleFormItem;
import vn.fpoly.simplain.dto.TaoFlashSaleRequest;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ Thiết lập khung giờ Flash Sale (US29 - Mod 6 / Bán hàng - Khoa).
 * - Tiêu chí 1: Đặt khung giờ Bắt đầu / Kết thúc.
 * - Tiêu chí 2: Gán giá sốc và giới hạn số lượng (so_luong_mo_ban) cho biến thể.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlashSaleService {

    private final FlashSaleRepository flashSaleRepository;
    private final ChiTietFlashSaleRepository chiTietFlashSaleRepository;
    private final BienTheSanPhamRepository bienTheRepository;
    private final SanPhamRepository sanPhamRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final HinhAnhRepository hinhAnhRepository;

    /**
     * Lấy danh sách khung giờ Flash Sale có phân trang, tìm kiếm theo từ khóa và lọc trạng thái (US29).
     */
    @Transactional(readOnly = true)
    public Page<FlashSale> layDanhSachFlashSale(String tuKhoa, Boolean kichHoat, Pageable pageable) {
        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        Page<FlashSale> page = flashSaleRepository.timKiemFlashSale(tuKhoaChuan, kichHoat, pageable);

        for (FlashSale fs : page.getContent()) {
            lamGiauThongTinFlashSale(fs);
        }
        return page;
    }

    /**
     * Lấy thông tin chi tiết một đợt Flash Sale theo ID kèm danh sách biến thể tham gia.
     */
    @Transactional(readOnly = true)
    public FlashSale layTheoId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID đợt Flash Sale không được để trống!");
        }
        FlashSale fs = flashSaleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đợt Flash Sale với ID: " + id));
        lamGiauThongTinFlashSale(fs);
        return fs;
    }

    /**
     * Lấy đợt Flash Sale đang diễn ra hoặc đợt kích hoạt mới nhất để hiển thị ra Storefront / Trang chủ (US29).
     */
    @Transactional(readOnly = true)
    public FlashSale layFlashSaleHienTai() {
        LocalDateTime now = LocalDateTime.now();
        List<FlashSale> dsDangChay = flashSaleRepository.timFlashSaleDangDienRa(now);
        if (!dsDangChay.isEmpty()) {
            FlashSale fs = dsDangChay.get(0);
            lamGiauThongTinFlashSale(fs);
            return fs;
        }

        List<FlashSale> dsKichHoat = flashSaleRepository.findByKichHoatTrue();
        if (!dsKichHoat.isEmpty()) {
            // Sắp xếp ID giảm dần để lấy đợt mới nhất
            dsKichHoat.sort((a, b) -> Long.compare(b.getId(), a.getId()));
            FlashSale fs = dsKichHoat.get(0);
            lamGiauThongTinFlashSale(fs);
            return fs;
        }

        return null;
    }

    /**
     * Tìm chi tiết Flash Sale đang áp dụng cho 1 biến thể SKU cụ thể (US29).
     */
    @Transactional(readOnly = true)
    public Optional<ChiTietFlashSale> timChiTietFlashSaleDangApDungCuaBienThe(Long bienTheId) {
        if (bienTheId == null) return Optional.empty();
        List<ChiTietFlashSale> list = chiTietFlashSaleRepository.timChiTietFlashSaleDangApDung(bienTheId, LocalDateTime.now());
        if (!list.isEmpty()) {
            ChiTietFlashSale ct = list.get(0);
            BienTheSanPham bt = bienTheRepository.findById(bienTheId).orElse(null);
            if (bt != null) {
                ct.setBienTheSanPham(bt);
                ct.setMaSku(bt.getMaSku());
                ct.setGiaBanGoc(bt.getGiaBan());
                ct.setMaSanPhamId(bt.getMaSanPhamId());
            }
            return Optional.of(ct);
        }

        // Bổ sung đồng bộ với đợt Flash Sale đang kích hoạt (Storefront Active Flash Sale)
        FlashSale fs = layFlashSaleHienTai();
        if (fs != null && fs.getDanhSachChiTiet() != null) {
            for (ChiTietFlashSale ct : fs.getDanhSachChiTiet()) {
                if (bienTheId.equals(ct.getMaBienTheId())) {
                    return Optional.of(ct);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Lấy Map biến thể ID -> ChiTietFlashSale của các đợt Flash Sale đang diễn ra (US29).
     */
    @Transactional(readOnly = true)
    public Map<Long, ChiTietFlashSale> layMapFlashSaleHienTaiTheoBienTheId() {
        Map<Long, ChiTietFlashSale> map = new HashMap<>();
        List<ChiTietFlashSale> list = chiTietFlashSaleRepository.timTatCaChiTietFlashSaleDangApDung(LocalDateTime.now());
        for (ChiTietFlashSale ct : list) {
            map.put(ct.getMaBienTheId(), ct);
        }

        // Bổ sung các biến thể từ đợt Flash Sale hiện tại trên storefront nếu chưa có
        FlashSale fs = layFlashSaleHienTai();
        if (fs != null && fs.getDanhSachChiTiet() != null) {
            for (ChiTietFlashSale ct : fs.getDanhSachChiTiet()) {
                map.putIfAbsent(ct.getMaBienTheId(), ct);
            }
        }
        return map;
    }

    /**
     * Ghi nhận số lượng đã bán của biến thể trong đợt Flash Sale (US29).
     */
    @Transactional
    public void ghiNhanBanFlashSale(Long bienTheId, int soLuong) {
        if (bienTheId == null || soLuong <= 0) return;
        List<ChiTietFlashSale> list = chiTietFlashSaleRepository.timChiTietFlashSaleDangApDung(bienTheId, LocalDateTime.now());
        ChiTietFlashSale ct = null;
        if (!list.isEmpty()) {
            ct = list.get(0);
        } else {
            FlashSale fs = layFlashSaleHienTai();
            if (fs != null && fs.getDanhSachChiTiet() != null) {
                for (ChiTietFlashSale item : fs.getDanhSachChiTiet()) {
                    if (bienTheId.equals(item.getMaBienTheId())) {
                        ct = item;
                        break;
                    }
                }
            }
        }

        if (ct != null) {
            int daBan = ct.getSoLuongDaBan() != null ? ct.getSoLuongDaBan() : 0;
            ct.setSoLuongDaBan(daBan + soLuong);
            chiTietFlashSaleRepository.save(ct);
            log.info("Đã cập nhật số lượng đã bán Flash Sale cho biến thể ID [{}]: {} -> {}", bienTheId, daBan, ct.getSoLuongDaBan());
        }
    }

    /**
     * Tạo mới đợt Flash Sale và gán biến thể SKU (US29 - Tiêu chí 1 & 2).
     */
    @Transactional
    public FlashSale taoMoi(TaoFlashSaleRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Dữ liệu tạo Flash Sale không được để trống!");
        }
        chuanHoaDuLieu(req);
        validateNghiepVu(req, null);

        FlashSale fs = FlashSale.builder()
                .tieuDe(req.getTieuDe())
                .thoiGianBatDau(req.getThoiGianBatDau())
                .thoiGianKetThuc(req.getThoiGianKetThuc())
                .kichHoat(req.getKichHoat() != null ? req.getKichHoat() : true)
                .build();

        fs = flashSaleRepository.saveAndFlush(fs);

        luuDanhSachChiTietFlashSale(fs.getId(), req.getDanhSachChiTiet(), Collections.emptyMap());
        log.info("Admin đã tạo mới đợt Flash Sale ID [{}] - '{}' thành công", fs.getId(), fs.getTieuDe());
        return fs;
    }

    /**
     * Cập nhật đợt Flash Sale và đồng bộ lại danh sách biến thể SKU (US29).
     */
    @Transactional
    public FlashSale capNhat(Long id, TaoFlashSaleRequest req) {
        if (id == null) {
            throw new IllegalArgumentException("ID đợt Flash Sale không được để trống!");
        }
        if (req == null) {
            throw new IllegalArgumentException("Dữ liệu cập nhật Flash Sale không được để trống!");
        }
        chuanHoaDuLieu(req);
        req.setId(id);
        validateNghiepVu(req, id);

        FlashSale fs = flashSaleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đợt Flash Sale ID: " + id));

        fs.setTieuDe(req.getTieuDe());
        fs.setThoiGianBatDau(req.getThoiGianBatDau());
        fs.setThoiGianKetThuc(req.getThoiGianKetThuc());
        fs.setKichHoat(req.getKichHoat() != null ? req.getKichHoat() : true);
        fs = flashSaleRepository.saveAndFlush(fs);

        // Lưu giữ số lượng đã bán trước đó nếu có
        Map<Long, Integer> mapSoLuongDaBan = chiTietFlashSaleRepository.findByMaFlashSaleId(id).stream()
                .collect(Collectors.toMap(ChiTietFlashSale::getMaBienTheId,
                        ct -> ct.getSoLuongDaBan() != null ? ct.getSoLuongDaBan() : 0,
                        (a, b) -> a));

        chiTietFlashSaleRepository.xoaTheoMaFlashSaleId(id);
        chiTietFlashSaleRepository.flush();

        luuDanhSachChiTietFlashSale(id, req.getDanhSachChiTiet(), mapSoLuongDaBan);
        log.info("Admin đã cập nhật thành công đợt Flash Sale ID [{}] - '{}'", id, fs.getTieuDe());
        return fs;
    }

    /**
     * Bật / Tắt trạng thái kích hoạt của đợt Flash Sale.
     */
    @Transactional
    public boolean toggleKichHoat(Long id) {
        FlashSale fs = layTheoId(id);
        boolean trangThaiMoi = !Boolean.TRUE.equals(fs.getKichHoat());
        fs.setKichHoat(trangThaiMoi);
        flashSaleRepository.save(fs);
        log.info("Đã đổi trạng thái kích hoạt Flash Sale ID [{}] sang: {}", id, trangThaiMoi);
        return trangThaiMoi;
    }

    /**
     * Xóa đợt Flash Sale và các biến thể tham gia.
     */
    @Transactional
    public void xoa(Long id) {
        FlashSale fs = layTheoId(id);
        chiTietFlashSaleRepository.xoaTheoMaFlashSaleId(id);
        flashSaleRepository.delete(fs);
        log.info("Đã xóa hoàn toàn đợt Flash Sale ID [{}] - '{}'", id, fs.getTieuDe());
    }

    /**
     * Lấy danh sách tất cả biến thể SKU khả dụng để Admin chọn đưa vào Flash Sale (tồn kho > 0, đang kích hoạt).
     */
    @Transactional(readOnly = true)
    public List<ChiTietFlashSaleFormItem> layDanhSachBienTheKhaDungChoFlashSale() {
        List<BienTheSanPham> danhSachBienThe = bienTheRepository.findAll().stream()
                .filter(b -> Boolean.TRUE.equals(b.getKichHoat()) && b.getSoLuong() != null && b.getSoLuong() > 0)
                .sorted(Comparator.comparing(BienTheSanPham::getId).reversed())
                .collect(Collectors.toList());

        Map<Long, SanPham> mapSanPham = sanPhamRepository.findAll().stream()
                .collect(Collectors.toMap(SanPham::getId, sp -> sp, (a, b) -> a));
        Map<Long, MauSac> mapMauSac = mauSacRepository.findAll().stream()
                .collect(Collectors.toMap(MauSac::getId, ms -> ms, (a, b) -> a));
        Map<Long, KichThuoc> mapKichThuoc = kichThuocRepository.findAll().stream()
                .collect(Collectors.toMap(KichThuoc::getId, kt -> kt, (a, b) -> a));

        List<ChiTietFlashSaleFormItem> result = new ArrayList<>();
        for (BienTheSanPham bt : danhSachBienThe) {
            SanPham sp = mapSanPham.get(bt.getMaSanPhamId());
            MauSac ms = mapMauSac.get(bt.getMaMauSacId());
            KichThuoc kt = mapKichThuoc.get(bt.getMaKichThuocId());

            String anhUrl = "/images/default-product.png";
            List<HinhAnh> anhBienThe = hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(bt.getId());
            if (!anhBienThe.isEmpty() && anhBienThe.get(0).getDuongDanAnh() != null) {
                anhUrl = anhBienThe.get(0).getDuongDanAnh();
            } else if (sp != null) {
                List<HinhAnh> anhSp = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sp.getId());
                if (!anhSp.isEmpty() && anhSp.get(0).getDuongDanAnh() != null) {
                    anhUrl = anhSp.get(0).getDuongDanAnh();
                }
            }

            // Gợi ý giá sốc mặc định: giảm 20% so với giá bán
            BigDecimal giaBanGoc = bt.getGiaBan() != null ? bt.getGiaBan() : BigDecimal.ZERO;
            BigDecimal giaGoiY = giaBanGoc.multiply(new BigDecimal("0.8")).setScale(0, java.math.RoundingMode.HALF_UP);
            if (giaGoiY.compareTo(new BigDecimal("1000")) < 0) {
                giaGoiY = giaBanGoc;
            }

            result.add(ChiTietFlashSaleFormItem.builder()
                    .maBienTheId(bt.getId())
                    .maSku(bt.getMaSku())
                    .tenSanPham(sp != null ? sp.getTenSanPham() : "Sản phẩm #" + bt.getMaSanPhamId())
                    .tenMauSac(ms != null ? ms.getTenMau() : "")
                    .maMauHex(ms != null ? ms.getMaMau() : "")
                    .tenKichThuoc(formatTenKichThuocNganGon(kt))
                    .giaBanGoc(giaBanGoc)
                    .giaFlashSale(giaGoiY)
                    .tonKhoHienTai(bt.getSoLuong() != null ? bt.getSoLuong() : 0)
                    .soLuongMoBan(Math.min(bt.getSoLuong() != null ? bt.getSoLuong() : 0, 10))
                    .soLuongDaBan(0)
                    .anhDaiDienUrl(anhUrl)
                    .build());
        }
        return result;
    }

    /**
     * Lấy thông tin 1 biến thể SKU theo ID để phục vụ AJAX add row vào bảng Flash Sale.
     */
    @Transactional(readOnly = true)
    public ChiTietFlashSaleFormItem layThongTinBienTheItem(Long bienTheId) {
        BienTheSanPham bt = bienTheRepository.findById(bienTheId)
                .orElseThrow(() -> new IllegalArgumentException("Biến thể ID " + bienTheId + " không tồn tại!"));

        SanPham sp = sanPhamRepository.findById(bt.getMaSanPhamId()).orElse(null);
        MauSac ms = mauSacRepository.findById(bt.getMaMauSacId()).orElse(null);
        KichThuoc kt = kichThuocRepository.findById(bt.getMaKichThuocId()).orElse(null);

        String anhUrl = "/images/default-product.png";
        List<HinhAnh> anhBienThe = hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(bt.getId());
        if (!anhBienThe.isEmpty() && anhBienThe.get(0).getDuongDanAnh() != null) {
            anhUrl = anhBienThe.get(0).getDuongDanAnh();
        } else if (sp != null) {
            List<HinhAnh> anhSp = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sp.getId());
            if (!anhSp.isEmpty() && anhSp.get(0).getDuongDanAnh() != null) {
                anhUrl = anhSp.get(0).getDuongDanAnh();
            }
        }

        BigDecimal giaBanGoc = bt.getGiaBan() != null ? bt.getGiaBan() : BigDecimal.ZERO;
        BigDecimal giaGoiY = giaBanGoc.multiply(new BigDecimal("0.8")).setScale(0, java.math.RoundingMode.HALF_UP);
        if (giaGoiY.compareTo(new BigDecimal("1000")) < 0) {
            giaGoiY = giaBanGoc;
        }

        return ChiTietFlashSaleFormItem.builder()
                .maBienTheId(bt.getId())
                .maSku(bt.getMaSku())
                .tenSanPham(sp != null ? sp.getTenSanPham() : "Sản phẩm #" + bt.getMaSanPhamId())
                .tenMauSac(ms != null ? ms.getTenMau() : "")
                .maMauHex(ms != null ? ms.getMaMau() : "")
                .tenKichThuoc(formatTenKichThuocNganGon(kt))
                .giaBanGoc(giaBanGoc)
                .giaFlashSale(giaGoiY)
                .tonKhoHienTai(bt.getSoLuong() != null ? bt.getSoLuong() : 0)
                .soLuongMoBan(Math.min(bt.getSoLuong() != null ? bt.getSoLuong() : 0, 10))
                .soLuongDaBan(0)
                .anhDaiDienUrl(anhUrl)
                .build();
    }

    // =========================================================================
    // PRIVATE HELPER METHODS & BUSINESS VALIDATIONS
    // =========================================================================

    private void chuanHoaDuLieu(TaoFlashSaleRequest req) {
        if (req.getTieuDe() != null) {
            req.setTieuDe(req.getTieuDe().trim());
        }
    }

    private void validateNghiepVu(TaoFlashSaleRequest req, Long excludeId) {
        // 1. Tiêu chí 1: Đặt khung giờ Bắt đầu / Kết thúc
        if (req.getThoiGianBatDau() == null) {
            throw new IllegalArgumentException("Thời gian bắt đầu đợt Flash Sale không được để trống!");
        }
        if (req.getThoiGianKetThuc() == null) {
            throw new IllegalArgumentException("Thời gian kết thúc đợt Flash Sale không được để trống!");
        }
        if (!req.getThoiGianKetThuc().isAfter(req.getThoiGianBatDau())) {
            throw new IllegalArgumentException("Thời gian kết thúc đợt Flash Sale phải diễn ra sau thời gian bắt đầu!");
        }

        // Kiểm tra chồng chéo khung giờ Flash Sale
        if (Boolean.TRUE.equals(req.getKichHoat())) {
            List<FlashSale> chongCheo = flashSaleRepository.timKhungGioChongCheo(
                    req.getThoiGianBatDau(), req.getThoiGianKetThuc(), excludeId);
            if (!chongCheo.isEmpty()) {
                FlashSale trung = chongCheo.get(0);
                throw new IllegalArgumentException("Khung giờ Flash Sale bị chồng chéo với đợt ["
                        + trung.getTieuDe() + "] (" + trung.getThoiGianBatDau() + " ~ " + trung.getThoiGianKetThuc() + ") đang hoạt động!");
            }
        }

        // 2. Tiêu chí 2: Gán giá sốc và giới hạn số lượng (so_luong_mo_ban) cho biến thể
        if (req.getDanhSachChiTiet() == null || req.getDanhSachChiTiet().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất 1 biến thể sản phẩm tham gia đợt Flash Sale!");
        }

        Set<Long> setBienTheId = new HashSet<>();
        for (ChiTietFlashSaleFormItem item : req.getDanhSachChiTiet()) {
            if (item.getMaBienTheId() == null) {
                throw new IllegalArgumentException("Biến thể sản phẩm không hợp lệ!");
            }
            if (!setBienTheId.add(item.getMaBienTheId())) {
                throw new IllegalArgumentException("Biến thể SKU ID [" + item.getMaBienTheId() + "] bị thêm lặp lại nhiều lần trong cùng một đợt Flash Sale!");
            }

            BienTheSanPham bt = bienTheRepository.findById(item.getMaBienTheId())
                    .orElseThrow(() -> new IllegalArgumentException("Biến thể SKU ID [" + item.getMaBienTheId() + "] không tồn tại trên hệ thống!"));

            // Kiểm tra giá sốc Flash Sale
            if (item.getGiaFlashSale() == null || item.getGiaFlashSale().compareTo(new BigDecimal("1000")) < 0) {
                throw new IllegalArgumentException("Giá Flash Sale của biến thể [" + bt.getMaSku() + "] phải từ 1.000 VNĐ trở lên!");
            }
            if (bt.getGiaBan() != null && item.getGiaFlashSale().compareTo(bt.getGiaBan()) >= 0) {
                throw new IllegalArgumentException("Giá sốc Flash Sale của biến thể [" + bt.getMaSku() + "] ("
                        + String.format("%,.0f", item.getGiaFlashSale()) + " đ) phải nhỏ hơn giá bán thường ("
                        + String.format("%,.0f", bt.getGiaBan()) + " đ)!");
            }

            // Kiểm tra giới hạn số lượng mở bán (so_luong_mo_ban)
            if (item.getSoLuongMoBan() == null || item.getSoLuongMoBan() < 1) {
                throw new IllegalArgumentException("Số lượng mở bán của biến thể [" + bt.getMaSku() + "] phải từ 1 sản phẩm trở lên!");
            }
            int tonKho = bt.getSoLuong() != null ? bt.getSoLuong() : 0;
            if (item.getSoLuongMoBan() > tonKho) {
                throw new IllegalArgumentException("Số lượng mở bán của biến thể [" + bt.getMaSku() + "] ("
                        + item.getSoLuongMoBan() + ") không được vượt quá số lượng tồn kho khả dụng hiện tại (" + tonKho + ")!");
            }
        }
    }

    private void luuDanhSachChiTietFlashSale(Long flashSaleId, List<ChiTietFlashSaleFormItem> items, Map<Long, Integer> mapDaBan) {
        if (items == null || items.isEmpty()) return;

        for (ChiTietFlashSaleFormItem item : items) {
            int daBan = mapDaBan.getOrDefault(item.getMaBienTheId(), item.getSoLuongDaBan() != null ? item.getSoLuongDaBan() : 0);
            ChiTietFlashSale ct = ChiTietFlashSale.builder()
                    .maFlashSaleId(flashSaleId)
                    .maBienTheId(item.getMaBienTheId())
                    .giaFlashSale(item.getGiaFlashSale())
                    .soLuongMoBan(item.getSoLuongMoBan())
                    .soLuongDaBan(daBan)
                    .build();
            chiTietFlashSaleRepository.save(ct);
        }
        chiTietFlashSaleRepository.flush();
    }

    private void lamGiauThongTinFlashSale(FlashSale fs) {
        List<ChiTietFlashSale> chiTiets = chiTietFlashSaleRepository.findByMaFlashSaleId(fs.getId());

        if (chiTiets.isEmpty()) {
            fs.setDanhSachChiTiet(chiTiets);
            return;
        }

        Set<Long> bienTheIds = chiTiets.stream().map(ChiTietFlashSale::getMaBienTheId).collect(Collectors.toSet());
        Map<Long, BienTheSanPham> mapBienThe = bienTheRepository.findAllById(bienTheIds).stream()
                .collect(Collectors.toMap(BienTheSanPham::getId, bt -> bt, (a, b) -> a));

        Set<Long> sanPhamIds = mapBienThe.values().stream().map(BienTheSanPham::getMaSanPhamId).collect(Collectors.toSet());
        Map<Long, SanPham> mapSanPham = sanPhamRepository.findAllById(sanPhamIds).stream()
                .collect(Collectors.toMap(SanPham::getId, sp -> sp, (a, b) -> a));

        Map<Long, MauSac> mapMauSac = mauSacRepository.findAll().stream()
                .collect(Collectors.toMap(MauSac::getId, ms -> ms, (a, b) -> a));
        Map<Long, KichThuoc> mapKichThuoc = kichThuocRepository.findAll().stream()
                .collect(Collectors.toMap(KichThuoc::getId, kt -> kt, (a, b) -> a));

        for (ChiTietFlashSale ct : chiTiets) {
            BienTheSanPham bt = mapBienThe.get(ct.getMaBienTheId());
            if (bt != null) {
                ct.setBienTheSanPham(bt);
                ct.setMaSku(bt.getMaSku());
                ct.setGiaBanGoc(bt.getGiaBan());
                ct.setTonKhoHienTai(bt.getSoLuong());
                ct.setMaSanPhamId(bt.getMaSanPhamId());

                SanPham sp = mapSanPham.get(bt.getMaSanPhamId());
                if (sp != null) {
                    ct.setTenSanPham(sp.getTenSanPham());
                }

                MauSac ms = mapMauSac.get(bt.getMaMauSacId());
                if (ms != null) {
                    ct.setTenMauSac(ms.getTenMau());
                    ct.setMaMauHex(ms.getMaMau());
                }

                KichThuoc kt = mapKichThuoc.get(bt.getMaKichThuocId());
                if (kt != null) {
                    ct.setTenKichThuoc(formatTenKichThuocNganGon(kt));
                }

                List<HinhAnh> anhBienThe = hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(bt.getId());
                if (!anhBienThe.isEmpty() && anhBienThe.get(0).getDuongDanAnh() != null) {
                    ct.setAnhDaiDienUrl(anhBienThe.get(0).getDuongDanAnh());
                } else if (sp != null) {
                    List<HinhAnh> anhSp = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sp.getId());
                    if (!anhSp.isEmpty() && anhSp.get(0).getDuongDanAnh() != null) {
                        ct.setAnhDaiDienUrl(anhSp.get(0).getDuongDanAnh());
                    }
                }
            }
        }

        fs.setDanhSachChiTiet(chiTiets);
    }

    private String formatTenKichThuocNganGon(KichThuoc kt) {
        if (kt == null) return "";
        if (kt.getMaKichThuoc() != null && !kt.getMaKichThuoc().isBlank()) {
            return kt.getMaKichThuoc().trim();
        }
        if (kt.getTenKichThuoc() != null) {
            return kt.getTenKichThuoc().replaceAll("\\s*\\([^)]*\\)", "").replace("Size ", "").trim();
        }
        return "";
    }
}
