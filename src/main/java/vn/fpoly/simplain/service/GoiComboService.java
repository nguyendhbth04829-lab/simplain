package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.TaoGoiComboRequest;
import vn.fpoly.simplain.entity.GoiCombo;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.entity.SanPhamTrongCombo;
import vn.fpoly.simplain.repository.GoiComboRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;
import vn.fpoly.simplain.repository.SanPhamTrongComboRepository;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Service xử lý nghiệp vụ Thiết lập Gói Combo kích cầu giá fix (US28 - Mod 6 / Bán hàng - Khoa).
 * - Tiêu chí 1: Tạo gói giá fix (VD: Tự chọn 3 áo giá 299k).
 * - Tiêu chí 2: Map san_pham_trong_combo các áo được áp dụng.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoiComboService {

    private final GoiComboRepository goiComboRepository;
    private final SanPhamTrongComboRepository sanPhamTrongComboRepository;
    private final SanPhamRepository sanPhamRepository;
    private final SanPhamService sanPhamService;

    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");

    /**
     * Lấy danh sách Gói Combo phân trang và tìm kiếm (US28).
     */
    @Transactional(readOnly = true)
    public Page<GoiCombo> layDanhSachGoiCombo(String tuKhoa, Boolean kichHoat, Pageable pageable) {
        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        Page<GoiCombo> page = goiComboRepository.timKiemGoiCombo(tuKhoaChuan, kichHoat, pageable);

        for (GoiCombo combo : page.getContent()) {
            lamGiauThongTinCombo(combo);
        }
        return page;
    }

    /**
     * Lấy chi tiết Gói Combo theo ID kèm danh sách sản phẩm được map (US28).
     */
    @Transactional(readOnly = true)
    public GoiCombo layTheoId(Long id) {
        GoiCombo combo = goiComboRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Gói Combo với ID: " + id));
        lamGiauThongTinCombo(combo);
        return combo;
    }

    /**
     * Tạo mới Gói Combo giá fix và map san_pham_trong_combo (US28).
     */
    @Transactional
    public GoiCombo taoMoi(TaoGoiComboRequest req) {
        log.info("Admin tạo mới gói combo: {}", req.getMaCombo());

        // Chuẩn hóa dữ liệu
        chuanHoaDuLieu(req);

        // Kiểm tra validate nghiệp vụ
        validateNghiepVu(req, null);

        // Lưu Gói Combo vào CSDL
        GoiCombo combo = GoiCombo.builder()
                .maCombo(req.getMaCombo())
                .tenCombo(req.getTenCombo())
                .duongDanSlug(req.getDuongDanSlug())
                .giaCombo(req.getGiaCombo())
                .soMonQuyDinh(req.getSoMonQuyDinh())
                .moTa(req.getMoTa() != null ? req.getMoTa().trim() : null)
                .kichHoat(req.getKichHoat() != null ? req.getKichHoat() : true)
                .build();

        GoiCombo saved = goiComboRepository.saveAndFlush(combo);

        // Tiêu chí 2: Map san_pham_trong_combo
        luuDanhSachSanPhamTrongCombo(saved.getId(), req.getDanhSachSanPhamIds());

        lamGiauThongTinCombo(saved);
        return saved;
    }

    /**
     * Cập nhật thông tin Gói Combo và cập nhật lại map san_pham_trong_combo (US28).
     */
    @Transactional
    public GoiCombo capNhat(Long id, TaoGoiComboRequest req) {
        log.info("Admin cập nhật gói combo ID {}: {}", id, req.getMaCombo());

        GoiCombo existing = goiComboRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Gói Combo cần cập nhật với ID: " + id));

        // Chuẩn hóa dữ liệu
        chuanHoaDuLieu(req);

        // Kiểm tra validate nghiệp vụ (loại trừ ID hiện tại khi check unique)
        validateNghiepVu(req, id);

        // Cập nhật thông tin gói
        existing.setMaCombo(req.getMaCombo());
        existing.setTenCombo(req.getTenCombo());
        existing.setDuongDanSlug(req.getDuongDanSlug());
        existing.setGiaCombo(req.getGiaCombo());
        existing.setSoMonQuyDinh(req.getSoMonQuyDinh());
        existing.setMoTa(req.getMoTa() != null ? req.getMoTa().trim() : null);
        if (req.getKichHoat() != null) {
            existing.setKichHoat(req.getKichHoat());
        }

        GoiCombo updated = goiComboRepository.saveAndFlush(existing);

        // Cập nhật lại mapping san_pham_trong_combo: Xóa cũ và ghi mới
        sanPhamTrongComboRepository.xoaTheoMaComboId(id);
        luuDanhSachSanPhamTrongCombo(id, req.getDanhSachSanPhamIds());

        lamGiauThongTinCombo(updated);
        return updated;
    }

    /**
     * Bật / Tắt trạng thái kích hoạt của gói combo.
     */
    @Transactional
    public boolean toggleKichHoat(Long id) {
        GoiCombo combo = goiComboRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Gói Combo ID: " + id));
        boolean trangThaiMoi = (combo.getKichHoat() == null) || !combo.getKichHoat();
        combo.setKichHoat(trangThaiMoi);
        goiComboRepository.saveAndFlush(combo);
        return trangThaiMoi;
    }

    /**
     * Xóa gói combo.
     */
    @Transactional
    public void xoa(Long id) {
        GoiCombo combo = goiComboRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Gói Combo ID: " + id));
        sanPhamTrongComboRepository.xoaTheoMaComboId(id);
        goiComboRepository.delete(combo);
    }

    /**
     * Lấy danh sách tất cả sản phẩm đang kinh doanh để hiển thị lên form chọn sản phẩm vào combo (US28).
     */
    @Transactional(readOnly = true)
    public List<SanPham> layTatCaSanPhamKhaDungChoCombo() {
        List<SanPham> dsSanPham = sanPhamRepository.findAll();
        sanPhamService.lamGiauThongTinHienThi(dsSanPham);
        return dsSanPham;
    }

    /**
     * Sinh mã Combo tự động chuẩn hóa: COMBO-{SO_MON}M-{GIA}K hoặc ngẫu nhiên.
     */
    public String sinhMaComboTuDong(Integer soMon, BigDecimal gia) {
        String prefix = "COMBO-";
        if (soMon != null && soMon > 0) {
            prefix += soMon + "M-";
        }
        if (gia != null && gia.compareTo(BigDecimal.ZERO) > 0) {
            long giaK = gia.longValue() / 1000;
            prefix += giaK + "K";
        } else {
            prefix += UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        }

        // Đảm bảo không trùng
        String candidate = prefix;
        int counter = 1;
        while (goiComboRepository.existsByMaComboIgnoreCase(candidate)) {
            candidate = prefix + "-" + counter++;
        }
        return candidate;
    }

    /**
     * Tiện ích sinh đường dẫn slug từ tên gói combo.
     */
    public String sinhSlugTuTen(String tenCombo) {
        if (tenCombo == null || tenCombo.isBlank()) {
            return "combo-" + System.currentTimeMillis();
        }
        String normalized = Normalizer.normalize(tenCombo, Normalizer.Form.NFD);
        String withoutDiacritics = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("");
        String slug = withoutDiacritics.toLowerCase(Locale.ROOT)
                .replace("đ", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        if (slug.isBlank()) {
            slug = "combo-" + System.currentTimeMillis();
        }

        // Đảm bảo không trùng
        String candidate = slug;
        int counter = 1;
        while (goiComboRepository.existsByDuongDanSlugIgnoreCase(candidate)) {
            candidate = slug + "-" + counter++;
        }
        return candidate;
    }

    /**
     * Kiểm tra mã combo đã tồn tại hay chưa.
     */
    @Transactional(readOnly = true)
    public boolean kiemTraMaComboTonTai(String maCombo, Long excludeId) {
        if (maCombo == null || maCombo.isBlank()) {
            return false;
        }
        if (excludeId != null) {
            return goiComboRepository.existsByMaComboIgnoreCaseAndIdNot(maCombo.trim(), excludeId);
        }
        return goiComboRepository.existsByMaComboIgnoreCase(maCombo.trim());
    }

    // =========================================================================
    // PRIVATE HELPER METHODS
    // =========================================================================

    private void chuanHoaDuLieu(TaoGoiComboRequest req) {
        if (req.getMaCombo() != null) {
            req.setMaCombo(req.getMaCombo().trim().toUpperCase());
        }
        if (req.getTenCombo() != null) {
            req.setTenCombo(req.getTenCombo().trim());
        }
        if (req.getDuongDanSlug() != null && !req.getDuongDanSlug().isBlank()) {
            req.setDuongDanSlug(req.getDuongDanSlug().trim().toLowerCase(Locale.ROOT));
        } else if (req.getTenCombo() != null) {
            req.setDuongDanSlug(sinhSlugTuTen(req.getTenCombo()));
        }
    }

    private void validateNghiepVu(TaoGoiComboRequest req, Long excludeId) {
        // 1. Kiểm tra mã combo trùng lặp
        if (excludeId == null) {
            if (goiComboRepository.existsByMaComboIgnoreCase(req.getMaCombo())) {
                throw new IllegalArgumentException("Mã gói combo [" + req.getMaCombo() + "] đã tồn tại trên hệ thống! Vui lòng chọn mã khác.");
            }
        } else {
            if (goiComboRepository.existsByMaComboIgnoreCaseAndIdNot(req.getMaCombo(), excludeId)) {
                throw new IllegalArgumentException("Mã gói combo [" + req.getMaCombo() + "] đã được sử dụng bởi gói combo khác!");
            }
        }

        // 2. Kiểm tra slug format & trùng lặp
        if (!SLUG_PATTERN.matcher(req.getDuongDanSlug()).matches()) {
            throw new IllegalArgumentException("Đường dẫn slug [" + req.getDuongDanSlug() + "] không đúng định dạng. Chỉ gồm chữ thường không dấu, số và dấu gạch ngang.");
        }

        if (excludeId == null) {
            if (goiComboRepository.existsByDuongDanSlugIgnoreCase(req.getDuongDanSlug())) {
                throw new IllegalArgumentException("Đường dẫn slug [" + req.getDuongDanSlug() + "] đã tồn tại! Vui lòng đặt slug khác.");
            }
        } else {
            if (goiComboRepository.existsByDuongDanSlugIgnoreCaseAndIdNot(req.getDuongDanSlug(), excludeId)) {
                throw new IllegalArgumentException("Đường dẫn slug [" + req.getDuongDanSlug() + "] đã được sử dụng bởi gói combo khác!");
            }
        }

        // 3. Kiểm tra số món quy định
        if (req.getSoMonQuyDinh() == null || req.getSoMonQuyDinh() < 2) {
            throw new IllegalArgumentException("Số món quy định trong gói combo phải từ 2 món trở lên (Ví dụ: Tự chọn 3 áo)!");
        }

        // 4. Kiểm tra giá gói combo
        if (req.getGiaCombo() == null || req.getGiaCombo().compareTo(new BigDecimal("1000")) < 0) {
            throw new IllegalArgumentException("Giá gói combo kích cầu phải từ 1.000 VNĐ trở lên!");
        }

        // 5. Tiêu chí 2: Kiểm tra danh sách sản phẩm được map (san_pham_trong_combo)
        if (req.getDanhSachSanPhamIds() == null || req.getDanhSachSanPhamIds().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất 1 sản phẩm áp dụng cho gói combo này!");
        }

        // Loại bỏ ID trùng lặp
        Set<Long> uniqueIds = new LinkedHashSet<>(req.getDanhSachSanPhamIds());
        req.setDanhSachSanPhamIds(new ArrayList<>(uniqueIds));

        // Kiểm tra xem số sản phẩm chọn có đủ để khách hàng tự chọn đủ số món không
        if (req.getDanhSachSanPhamIds().size() < req.getSoMonQuyDinh()) {
            throw new IllegalArgumentException("Gói Combo yêu cầu " + req.getSoMonQuyDinh()
                    + " món quy định, danh sách sản phẩm áp dụng phải có ít nhất " + req.getSoMonQuyDinh()
                    + " sản phẩm để khách hàng lựa chọn (Hiện bạn mới chọn " + req.getDanhSachSanPhamIds().size() + " sản phẩm)!");
        }

        // Kiểm tra sự tồn tại của từng sản phẩm
        for (Long spId : req.getDanhSachSanPhamIds()) {
            if (!sanPhamRepository.existsById(spId)) {
                throw new IllegalArgumentException("Sản phẩm có ID [" + spId + "] không tồn tại trên hệ thống!");
            }
        }
    }

    private void luuDanhSachSanPhamTrongCombo(Long comboId, List<Long> sanPhamIds) {
        if (sanPhamIds == null || sanPhamIds.isEmpty()) return;

        for (Long spId : sanPhamIds) {
            SanPhamTrongCombo map = SanPhamTrongCombo.builder()
                    .maComboId(comboId)
                    .maSanPhamId(spId)
                    .build();
            sanPhamTrongComboRepository.save(map);
        }
        sanPhamTrongComboRepository.flush();
    }

    private void lamGiauThongTinCombo(GoiCombo combo) {
        if (combo == null || combo.getId() == null) return;

        List<Long> dsMaSp = sanPhamTrongComboRepository.layDanhSachMaSanPhamIdTheoComboId(combo.getId());
        List<SanPham> dsSp = sanPhamTrongComboRepository.layDanhSachSanPhamTheoComboId(combo.getId());
        sanPhamService.lamGiauThongTinHienThi(dsSp);

        combo.setDanhSachMaSanPham(dsMaSp);
        combo.setDanhSachSanPham(dsSp);
        combo.setSoLuongSanPhamApDung(dsMaSp.size());
    }
}
