package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.fpoly.simplain.entity.BoSuuTap;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.repository.BoSuuTapRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý nghiệp vụ và Validate chuyên nghiệp cho Quản lý Bộ sưu tập kèm Banner (US06).
 *
 * <p>Đáp ứng tiêu chí US06:
 * 1. CRUD bo_suu_tap kèm tải lên hình ảnh Banner chiến dịch.
 * 2. Validate chặt chẽ tên bộ sưu tập, chuẩn URL slug, không trùng lặp dữ liệu.
 * 3. Validate file ảnh Banner tải lên (chỉ chấp nhận JPG, JPEG, PNG, WEBP, GIF, SVG và dung lượng tối đa 5MB).
 * 4. Quản lý gán / gỡ sản phẩm vào bộ sưu tập phục vụ khách xem và mua (Lookbook / Collection).
 * 5. Bảo vệ toàn vẹn dữ liệu khi xóa: Không cho xóa bộ sưu tập đang có sản phẩm gắn vào.
 */
@Service
public class BoSuuTapService {

    private static final long DUNG_LUONG_BANNER_TOI_DA = 5L * 1024 * 1024; // 5MB
    private static final List<String> DUOI_ANH_HOP_LE = Arrays.asList(".jpg", ".jpeg", ".png", ".webp", ".gif", ".svg");

    private final BoSuuTapRepository boSuuTapRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    public BoSuuTapService(BoSuuTapRepository boSuuTapRepository, JdbcTemplate jdbcTemplate) {
        this.boSuuTapRepository = boSuuTapRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Thống kê tổng số lượng bộ sưu tập, số BST có banner, đang hoạt động và tổng số sản phẩm trong BST.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> demThongKe() {
        Map<String, Long> stats = new HashMap<>();
        long tongSo = boSuuTapRepository.count();
        long soDangHoatDong = boSuuTapRepository.findAll().stream()
                .filter(b -> Boolean.TRUE.equals(b.getKichHoat()))
                .count();
        long soCoBanner = boSuuTapRepository.findAll().stream()
                .filter(b -> b.getHinhBannerUrl() != null && !b.getHinhBannerUrl().isBlank())
                .count();
        Long tongSp = 0L;
        try {
            tongSp = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [san_pham] WHERE [ma_bo_suu_tap_id] IS NOT NULL",
                    Long.class
            );
        } catch (Exception ignored) {
        }
        stats.put("tongSo", tongSo);
        stats.put("soDangHoatDong", soDangHoatDong);
        stats.put("soCoBanner", soCoBanner);
        stats.put("tongSanPhamTrongBST", tongSp != null ? tongSp : 0L);
        return stats;
    }

    /**
     * Đếm số lượng sản phẩm nhóm theo từng bộ sưu tập (SQL đơn gom nhóm tối ưu).
     */
    public Map<Long, Long> demSoSanPhamTheoBoSuuTap() {
        Map<Long, Long> map = new HashMap<>();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT [ma_bo_suu_tap_id], COUNT(*) as cnt FROM [san_pham] WHERE [ma_bo_suu_tap_id] IS NOT NULL GROUP BY [ma_bo_suu_tap_id]"
            );
            for (Map<String, Object> row : rows) {
                Long bstId = ((Number) row.get("ma_bo_suu_tap_id")).longValue();
                Long cnt = ((Number) row.get("cnt")).longValue();
                map.put(bstId, cnt);
            }
        } catch (Exception ignored) {
        }
        return map;
    }

    /**
     * Lấy danh sách bộ sưu tập có phân trang, tìm kiếm theo từ khóa và lọc theo trạng thái.
     */
    @Transactional(readOnly = true)
    public Page<BoSuuTap> layDanhSachPhanTrang(String tuKhoa, Boolean kichHoat, Pageable pageable) {
        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        Page<BoSuuTap> page = boSuuTapRepository.timKiemPhanTrang(tuKhoaChuan, kichHoat, pageable);
        Map<Long, Long> countMap = demSoSanPhamTheoBoSuuTap();
        page.getContent().forEach(b -> b.setSoSanPham(countMap.getOrDefault(b.getId(), 0L)));
        return page;
    }

    /**
     * Lấy danh sách tất cả các bộ sưu tập.
     */
    @Transactional(readOnly = true)
    public List<BoSuuTap> layTatCa() {
        List<BoSuuTap> list = boSuuTapRepository.findAll();
        Map<Long, Long> countMap = demSoSanPhamTheoBoSuuTap();
        list.forEach(b -> b.setSoSanPham(countMap.getOrDefault(b.getId(), 0L)));
        return list;
    }

    /**
     * Lấy thông tin bộ sưu tập theo ID.
     */
    @Transactional(readOnly = true)
    public BoSuuTap layTheoId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID bộ sưu tập không được để trống!");
        }
        BoSuuTap bst = boSuuTapRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ sưu tập với ID: " + id));
        bst.setSoSanPham(demSoSanPhamThuocBoSuuTap(id));
        return bst;
    }

    /**
     * Lấy thông tin bộ sưu tập theo đường dẫn slug.
     */
    @Transactional(readOnly = true)
    public BoSuuTap layTheoSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("Đường dẫn slug không được để trống!");
        }
        BoSuuTap bst = boSuuTapRepository.findByDuongDanSlug(slug.trim())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ sưu tập với slug: " + slug));
        bst.setSoSanPham(demSoSanPhamThuocBoSuuTap(bst.getId()));
        return bst;
    }

    /**
     * Lấy danh sách các sản phẩm đang thuộc một bộ sưu tập (kèm đầy đủ thông tin giá, ảnh, danh mục, tồn kho).
     */
    @Transactional(readOnly = true)
    public List<SanPham> layDanhSachSanPhamTrongBST(Long boSuuTapId) {
        if (boSuuTapId == null || sanPhamRepository == null) {
            return Collections.emptyList();
        }
        List<SanPham> list = sanPhamRepository.findByMaBoSuuTapIdOrderByIdDesc(boSuuTapId);
        if (sanPhamService != null) {
            sanPhamService.lamGiauThongTinHienThi(list);
        }
        return list;
    }

    /**
     * Lấy danh sách sản phẩm khả dụng để thêm vào bộ sưu tập (chưa thuộc BST này, có thể lọc theo danh mục hoặc từ khóa).
     */
    @Transactional(readOnly = true)
    public List<SanPham> layDanhSachSanPhamKhaDung(Long boSuuTapIdHienTai, String tuKhoa, Long maDanhMucId) {
        if (sanPhamRepository == null) {
            return Collections.emptyList();
        }
        List<SanPham> tatCa = sanPhamRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        if (sanPhamService != null) {
            sanPhamService.lamGiauThongTinHienThi(tatCa);
        }

        return tatCa.stream()
                .filter(sp -> Boolean.TRUE.equals(sp.getKichHoat()))
                // Loại trừ sản phẩm đang thuộc BST này rồi
                .filter(sp -> !Objects.equals(sp.getMaBoSuuTapId(), boSuuTapIdHienTai))
                .filter(sp -> {
                    if (maDanhMucId != null && !Objects.equals(sp.getMaDanhMucId(), maDanhMucId)) {
                        return false;
                    }
                    if (tuKhoa != null && !tuKhoa.isBlank()) {
                        String tk = tuKhoa.trim().toLowerCase();
                        boolean matchTen = sp.getTenSanPham() != null && sp.getTenSanPham().toLowerCase().contains(tk);
                        boolean matchMa = sp.getMaSanPham() != null && sp.getMaSanPham().toLowerCase().contains(tk);
                        return matchTen || matchMa;
                    }
                    return true;
                })
                .toList();
    }

    /**
     * Thêm hàng loạt sản phẩm vào bộ sưu tập.
     */
    @Transactional
    public int themSanPhamVaoBoSuuTap(Long boSuuTapId, List<Long> sanPhamIds) {
        BoSuuTap bst = layTheoId(boSuuTapId);
        if (sanPhamIds == null || sanPhamIds.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng tích chọn ít nhất 1 sản phẩm để thêm vào bộ sưu tập!");
        }
        if (sanPhamRepository == null) {
            return 0;
        }

        List<SanPham> danhSachCanThem = sanPhamRepository.findAllById(sanPhamIds);
        for (SanPham sp : danhSachCanThem) {
            sp.setMaBoSuuTapId(bst.getId());
        }
        sanPhamRepository.saveAll(danhSachCanThem);
        return danhSachCanThem.size();
    }

    /**
     * Gỡ bỏ một sản phẩm khỏi bộ sưu tập (trả về trạng thái chưa gán BST).
     */
    @Transactional
    public void xoaSanPhamKhoiBoSuuTap(Long boSuuTapId, Long sanPhamId) {
        if (sanPhamId == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống!");
        }
        if (sanPhamRepository == null) {
            return;
        }
        SanPham sp = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + sanPhamId));
        if (Objects.equals(sp.getMaBoSuuTapId(), boSuuTapId)) {
            sp.setMaBoSuuTapId(null);
            sanPhamRepository.save(sp);
        }
    }

    /**
     * Thêm mới bộ sưu tập kèm upload ảnh Banner.
     */
    @Transactional
    public BoSuuTap them(BoSuuTap boSuuTap, MultipartFile fileBanner) {
        chuanHoaVaValidateCoBan(boSuuTap);

        // 1. Kiểm tra trùng tên bộ sưu tập
        if (boSuuTapRepository.existsByTenBoSuuTapIgnoreCase(boSuuTap.getTenBoSuuTap())) {
            throw new IllegalArgumentException("Tên bộ sưu tập [" + boSuuTap.getTenBoSuuTap() + "] đã tồn tại trên hệ thống!");
        }

        // 2. Kiểm tra trùng đường dẫn slug
        if (boSuuTapRepository.existsByDuongDanSlugIgnoreCase(boSuuTap.getDuongDanSlug())) {
            throw new IllegalArgumentException("Đường dẫn slug [" + boSuuTap.getDuongDanSlug() + "] đã được sử dụng!");
        }

        // 3. Xử lý và validate file ảnh Banner nếu người dùng chọn tải lên
        if (fileBanner != null && !fileBanner.isEmpty()) {
            String duongDanBanner = luuVaValidateFileBanner(fileBanner);
            boSuuTap.setHinhBannerUrl(duongDanBanner);
        } else if (boSuuTap.getHinhBannerUrl() != null && !boSuuTap.getHinhBannerUrl().isBlank()) {
            boSuuTap.setHinhBannerUrl(boSuuTap.getHinhBannerUrl().trim());
        }

        return boSuuTapRepository.save(boSuuTap);
    }

    /**
     * Cập nhật bộ sưu tập kèm thay đổi hoặc giữ nguyên ảnh Banner.
     */
    @Transactional
    public BoSuuTap sua(Long id, BoSuuTap duLieuMoi, MultipartFile fileBanner) {
        BoSuuTap hienTai = layTheoId(id);
        chuanHoaVaValidateCoBan(duLieuMoi);

        // 1. Kiểm tra trùng tên bộ sưu tập (loại trừ chính ID đang sửa)
        if (boSuuTapRepository.existsByTenBoSuuTapIgnoreCaseAndIdNot(duLieuMoi.getTenBoSuuTap(), id)) {
            throw new IllegalArgumentException("Tên bộ sưu tập [" + duLieuMoi.getTenBoSuuTap() + "] đã tồn tại ở bộ sưu tập khác!");
        }

        // 2. Kiểm tra trùng đường dẫn slug (loại trừ chính ID đang sửa)
        if (boSuuTapRepository.existsByDuongDanSlugIgnoreCaseAndIdNot(duLieuMoi.getDuongDanSlug(), id)) {
            throw new IllegalArgumentException("Đường dẫn slug [" + duLieuMoi.getDuongDanSlug() + "] đã được sử dụng ở bộ sưu tập khác!");
        }

        // 3. Xử lý ảnh Banner mới hoặc giữ nguyên ảnh Banner cũ
        if (fileBanner != null && !fileBanner.isEmpty()) {
            String duongDanBannerMoi = luuVaValidateFileBanner(fileBanner);
            hienTai.setHinhBannerUrl(duongDanBannerMoi);
        } else if (duLieuMoi.getHinhBannerUrl() != null && !duLieuMoi.getHinhBannerUrl().isBlank()) {
            hienTai.setHinhBannerUrl(duLieuMoi.getHinhBannerUrl().trim());
        }

        hienTai.setTenBoSuuTap(duLieuMoi.getTenBoSuuTap());
        hienTai.setDuongDanSlug(duLieuMoi.getDuongDanSlug());
        hienTai.setKichHoat(duLieuMoi.getKichHoat());

        return boSuuTapRepository.save(hienTai);
    }

    /**
     * Đổi nhanh trạng thái Kích hoạt / Đã ẩn của bộ sưu tập.
     */
    @Transactional
    public BoSuuTap doiTrangThai(Long id) {
        BoSuuTap bst = layTheoId(id);
        boolean trangThaiMoi = (bst.getKichHoat() == null || !bst.getKichHoat());
        bst.setKichHoat(trangThaiMoi);
        return boSuuTapRepository.save(bst);
    }

    /**
     * Xóa bộ sưu tập có kiểm tra ràng buộc sản phẩm đang sử dụng.
     */
    @Transactional
    public void xoa(Long id) {
        BoSuuTap bst = layTheoId(id);

        long soSanPham = demSoSanPhamThuocBoSuuTap(id);
        if (soSanPham > 0) {
            throw new IllegalStateException(
                    "Không thể xóa bộ sưu tập [" + bst.getTenBoSuuTap() + "] vì đang có " + soSanPham + " sản phẩm thuộc bộ sưu tập này!"
            );
        }

        try {
            boSuuTapRepository.delete(bst);
            boSuuTapRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "Không thể xóa bộ sưu tập [" + bst.getTenBoSuuTap() + "] do đang có sản phẩm tham chiếu trên hệ thống!"
            );
        }
    }

    /**
     * Kiểm tra định dạng, dung lượng và lưu file ảnh Banner vào thư mục uploads/.
     */
    public String luuVaValidateFileBanner(MultipartFile fileBanner) {
        if (fileBanner.getSize() > DUNG_LUONG_BANNER_TOI_DA) {
            throw new IllegalArgumentException("Kích thước file ảnh banner không được vượt quá 5MB!");
        }

        String tenGoc = fileBanner.getOriginalFilename();
        if (tenGoc == null || tenGoc.isBlank() || !tenGoc.contains(".")) {
            throw new IllegalArgumentException("File banner tải lên không hợp lệ!");
        }

        String duoiFile = tenGoc.substring(tenGoc.lastIndexOf('.')).toLowerCase();
        if (!DUOI_ANH_HOP_LE.contains(duoiFile)) {
            throw new IllegalArgumentException(
                    "File banner tải lên phải là định dạng hình ảnh hợp lệ (JPG, PNG, WEBP, GIF, SVG)!"
            );
        }

        String contentType = fileBanner.getContentType();
        if (contentType != null && !contentType.isBlank() && !contentType.toLowerCase().startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Nội dung file tải lên không phải là hình ảnh hợp lệ (Content-Type: " + contentType + ")!"
            );
        }

        try {
            String tenFileAnToan = "banner_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6) + duoiFile;
            Path thuMucUpload = Paths.get("uploads");
            if (!Files.exists(thuMucUpload)) {
                Files.createDirectories(thuMucUpload);
            }
            Files.copy(fileBanner.getInputStream(), thuMucUpload.resolve(tenFileAnToan), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + tenFileAnToan;
        } catch (IOException e) {
            throw new IllegalStateException("Lỗi khi lưu file ảnh banner lên máy chủ: " + e.getMessage());
        }
    }

    private void chuanHoaVaValidateCoBan(BoSuuTap bst) {
        if (bst == null) {
            throw new IllegalArgumentException("Dữ liệu bộ sưu tập không được để trống!");
        }

        String ten = bst.getTenBoSuuTap() != null ? bst.getTenBoSuuTap().trim() : "";
        if (ten.isEmpty()) {
            throw new IllegalArgumentException("Tên bộ sưu tập không được để trống!");
        }
        if (ten.length() < 2 || ten.length() > 150) {
            throw new IllegalArgumentException("Tên bộ sưu tập phải từ 2 đến 150 ký tự!");
        }
        bst.setTenBoSuuTap(ten);

        String slug = bst.getDuongDanSlug() != null ? bst.getDuongDanSlug().trim().toLowerCase() : "";
        if (slug.isEmpty()) {
            throw new IllegalArgumentException("Đường dẫn slug không được để trống!");
        }
        if (!slug.matches("^[a-z0-9]+(-[a-z0-9]+)*$")) {
            throw new IllegalArgumentException("Slug chỉ gồm chữ thường không dấu, chữ số và dấu gạch ngang (VD: bst-thu-dong-2026)!");
        }
        bst.setDuongDanSlug(slug);

        if (bst.getKichHoat() == null) {
            bst.setKichHoat(true);
        }
    }

    public long demSoSanPhamThuocBoSuuTap(Long boSuuTapId) {
        try {
            Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [san_pham] WHERE [ma_bo_suu_tap_id] = ?",
                    Long.class,
                    boSuuTapId
            );
            return count != null ? count : 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }
}