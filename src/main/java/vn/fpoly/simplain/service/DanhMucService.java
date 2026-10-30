package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.DanhMuc;
import vn.fpoly.simplain.repository.DanhMucRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý nghiệp vụ và Validate chuyên nghiệp cho Quản lý Danh mục sản phẩm (US06).
 *
 * <p>Đáp ứng tiêu chí US06:
 * 1. CRUD danh_muc đầy đủ (Thêm, Sửa, Xóa, Đổi trạng thái, Tìm kiếm & Phân trang).
 * 2. Hỗ trợ phân cấp Danh mục Cha / Con (Validate chống tự nhận chính mình làm cha & chống vòng lặp cha-con).
 * 3. Bảo vệ toàn vẹn dữ liệu khi xóa: Không cho xóa danh mục đang có danh mục con hoặc đang có sản phẩm.
 */
@Service
public class DanhMucService {

    private final DanhMucRepository danhMucRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DanhMucService(DanhMucRepository danhMucRepository, JdbcTemplate jdbcTemplate) {
        this.danhMucRepository = danhMucRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Lấy danh sách danh mục có phân trang, tìm kiếm theo từ khóa, lọc theo danh mục cha và trạng thái.
     */
    @Transactional(readOnly = true)
    public Page<DanhMuc> layDanhSachPhanTrang(String tuKhoa, Long maDanhMucCha, Boolean kichHoat, Pageable pageable) {
        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        Page<DanhMuc> pageData = danhMucRepository.timKiemPhanTrang(tuKhoaChuan, maDanhMucCha, kichHoat, pageable);

        // Nạp thông tin tên danh mục cha, số lượng danh mục con và số sản phẩm
        Map<Long, String> mapTenDanhMuc = danhMucRepository.findAll().stream()
                .collect(Collectors.toMap(DanhMuc::getId, DanhMuc::getTenDanhMuc, (a, b) -> a));
        Map<Long, Long> soSanPhamMap = demSoSanPhamTheoDanhMuc();

        pageData.getContent().forEach(dm -> {
            if (dm.getMaDanhMucCha() != null) {
                dm.setTenDanhMucCha(mapTenDanhMuc.getOrDefault(dm.getMaDanhMucCha(), "Danh mục #" + dm.getMaDanhMucCha()));
            } else {
                dm.setTenDanhMucCha(null);
            }
            dm.setSoDanhMucCon(danhMucRepository.countByMaDanhMucCha(dm.getId()));
            dm.setSoSanPham(soSanPhamMap.getOrDefault(dm.getId(), 0L));
        });

        return pageData;
    }

    /**
     * Lấy toàn bộ cây phân cấp Danh mục Cha - Con (Hierarchy Tree).
     * Phục vụ hiển thị trực quan dạng cây thư mục, gom nhóm danh mục con dưới danh mục cha.
     */
    @Transactional(readOnly = true)
    public List<DanhMuc> layCayDanhMuc(String tuKhoa, Boolean kichHoat) {
        List<DanhMuc> tatCa = danhMucRepository.findAll(Sort.by("thuTuHienThi").ascending().and(Sort.by("id").descending()));
        Map<Long, Long> soSanPhamMap = demSoSanPhamTheoDanhMuc();

        // Nạp tên danh mục cha và số sản phẩm
        Map<Long, DanhMuc> mapTatCa = tatCa.stream().collect(Collectors.toMap(DanhMuc::getId, dm -> dm, (a, b) -> a));
        tatCa.forEach(dm -> {
            dm.setSoSanPham(soSanPhamMap.getOrDefault(dm.getId(), 0L));
            if (dm.getMaDanhMucCha() != null) {
                DanhMuc cha = mapTatCa.get(dm.getMaDanhMucCha());
                dm.setTenDanhMucCha(cha != null ? cha.getTenDanhMuc() : "Danh mục #" + dm.getMaDanhMucCha());
            }
        });

        // Gom nhóm con theo cha
        Map<Long, List<DanhMuc>> conTheoChaMap = tatCa.stream()
                .filter(dm -> dm.getMaDanhMucCha() != null)
                .collect(Collectors.groupingBy(DanhMuc::getMaDanhMucCha));

        String kw = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim().toLowerCase() : null;

        List<DanhMuc> ketQua = new ArrayList<>();
        for (DanhMuc goc : tatCa) {
            if (goc.getMaDanhMucCha() == null) {
                List<DanhMuc> dsCon = new ArrayList<>(conTheoChaMap.getOrDefault(goc.getId(), Collections.emptyList()));

                // Áp dụng bộ lọc kích hoạt nếu có
                if (kichHoat != null) {
                    dsCon.removeIf(c -> !Objects.equals(c.getKichHoat(), kichHoat));
                }

                goc.setDanhMucConList(dsCon);
                goc.setSoDanhMucCon(dsCon.size());

                // Áp dụng bộ lọc từ khóa nếu có
                if (kw != null) {
                    boolean gocKhop = goc.getTenDanhMuc().toLowerCase().contains(kw) || goc.getDuongDanSlug().toLowerCase().contains(kw);
                    boolean coConKhop = dsCon.stream().anyMatch(c -> c.getTenDanhMuc().toLowerCase().contains(kw) || c.getDuongDanSlug().toLowerCase().contains(kw));
                    if (gocKhop || coConKhop) {
                        ketQua.add(goc);
                    }
                } else {
                    if (kichHoat == null || Objects.equals(goc.getKichHoat(), kichHoat) || !dsCon.isEmpty()) {
                        ketQua.add(goc);
                    }
                }
            }
        }

        // Trường hợp đặc biệt: Danh mục con bị mồ côi (nếu cha bị lỗi hoặc không tìm thấy)
        Set<Long> rootIds = tatCa.stream().filter(dm -> dm.getMaDanhMucCha() == null).map(DanhMuc::getId).collect(Collectors.toSet());
        List<DanhMuc> moCoi = tatCa.stream()
                .filter(dm -> dm.getMaDanhMucCha() != null && !rootIds.contains(dm.getMaDanhMucCha()))
                .collect(Collectors.toList());
        if (!moCoi.isEmpty()) {
            DanhMuc nhomMoCoi = DanhMuc.builder()
                    .id(-1L)
                    .tenDanhMuc("Danh mục chưa phân nhóm cha")
                    .duongDanSlug("chua-phan-nhom")
                    .danhMucConList(moCoi)
                    .soDanhMucCon(moCoi.size())
                    .kichHoat(true)
                    .thuTuHienThi(9999)
                    .build();
            ketQua.add(nhomMoCoi);
        }

        return ketQua;
    }

    /**
     * Thống kê số lượng theo từng cấp bậc phân cấp phục vụ hiển thị KPI Cards.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> demThongKePhanCap() {
        List<DanhMuc> tatCa = danhMucRepository.findAll();
        Map<String, Long> stats = new HashMap<>();
        long tongSo = tatCa.size();
        long soGoc = tatCa.stream().filter(dm -> dm.getMaDanhMucCha() == null).count();
        long soCon = tatCa.stream().filter(dm -> dm.getMaDanhMucCha() != null).count();
        long soDangHoatDong = tatCa.stream().filter(dm -> Boolean.TRUE.equals(dm.getKichHoat())).count();

        stats.put("tongSo", tongSo);
        stats.put("soGoc", soGoc);
        stats.put("soCon", soCon);
        stats.put("soDangHoatDong", soDangHoatDong);
        return stats;
    }

    /**
     * Lấy danh sách các danh mục gốc (Cấp 1).
     */
    @Transactional(readOnly = true)
    public List<DanhMuc> layDanhSachDanhMucGoc() {
        return danhMucRepository.findByMaDanhMucChaIsNullOrderByThuTuHienThiAsc();
    }

    /**
     * Đếm số sản phẩm gán cho từng danh mục qua 1 truy vấn tổng hợp.
     */
    public Map<Long, Long> demSoSanPhamTheoDanhMuc() {
        Map<Long, Long> map = new HashMap<>();
        try {
            jdbcTemplate.query(
                    "SELECT [ma_danh_muc_id], COUNT(*) AS cnt FROM [san_pham] WHERE [ma_danh_muc_id] IS NOT NULL GROUP BY [ma_danh_muc_id]",
                    rs -> {
                        map.put(rs.getLong("ma_danh_muc_id"), rs.getLong("cnt"));
                    }
            );
        } catch (Exception ignored) {
        }
        return map;
    }

    /**
     * Lấy tất cả danh mục để hiển thị trong dropdown chọn Danh mục cha.
     */
    @Transactional(readOnly = true)
    public List<DanhMuc> layTatCa() {
        return danhMucRepository.findAll(Sort.by("thuTuHienThi").ascending().and(Sort.by("id").descending()));
    }

    /**
     * Lấy chi tiết một danh mục theo ID.
     */
    @Transactional(readOnly = true)
    public DanhMuc layTheoId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID danh mục không được để trống!");
        }
        DanhMuc dm = danhMucRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + id));
        dm.setSoDanhMucCon(danhMucRepository.countByMaDanhMucCha(dm.getId()));
        dm.setDanhMucConList(danhMucRepository.findByMaDanhMucChaOrderByThuTuHienThiAsc(dm.getId()));
        dm.setSoSanPham(demSoSanPhamThuocDanhMuc(dm.getId()));
        if (dm.getMaDanhMucCha() != null) {
            danhMucRepository.findById(dm.getMaDanhMucCha())
                    .ifPresent(cha -> dm.setTenDanhMucCha(cha.getTenDanhMuc()));
        }
        return dm;
    }

    /**
     * Thêm mới danh mục sản phẩm (Hỗ trợ gán danh mục cha).
     */
    @Transactional
    public DanhMuc them(DanhMuc danhMuc) {
        chuanHoaVaValidateCoBan(danhMuc);

        // 1. Kiểm tra trùng tên danh mục
        if (danhMucRepository.existsByTenDanhMucIgnoreCase(danhMuc.getTenDanhMuc())) {
            throw new IllegalArgumentException("Tên danh mục [" + danhMuc.getTenDanhMuc() + "] đã tồn tại trên hệ thống!");
        }

        // 2. Kiểm tra trùng đường dẫn slug
        if (danhMucRepository.existsByDuongDanSlugIgnoreCase(danhMuc.getDuongDanSlug())) {
            throw new IllegalArgumentException("Đường dẫn slug [" + danhMuc.getDuongDanSlug() + "] đã được sử dụng!");
        }

        // 3. Kiểm tra danh mục cha có tồn tại không (nếu có chọn)
        if (danhMuc.getMaDanhMucCha() != null) {
            if (!danhMucRepository.existsById(danhMuc.getMaDanhMucCha())) {
                throw new IllegalArgumentException("Danh mục cha được chọn không tồn tại trên hệ thống!");
            }
        }

        return danhMucRepository.save(danhMuc);
    }

    /**
     * Cập nhật thông tin danh mục sản phẩm và kiểm tra chặt chẽ phân cấp Cha / Con.
     */
    @Transactional
    public DanhMuc sua(Long id, DanhMuc duLieuMoi) {
        DanhMuc hienTai = layTheoId(id);
        chuanHoaVaValidateCoBan(duLieuMoi);

        // 1. Kiểm tra trùng tên danh mục (loại trừ chính nó)
        if (danhMucRepository.existsByTenDanhMucIgnoreCaseAndIdNot(duLieuMoi.getTenDanhMuc(), id)) {
            throw new IllegalArgumentException("Tên danh mục [" + duLieuMoi.getTenDanhMuc() + "] đã tồn tại ở danh mục khác!");
        }

        // 2. Kiểm tra trùng đường dẫn slug (loại trừ chính nó)
        if (danhMucRepository.existsByDuongDanSlugIgnoreCaseAndIdNot(duLieuMoi.getDuongDanSlug(), id)) {
            throw new IllegalArgumentException("Đường dẫn slug [" + duLieuMoi.getDuongDanSlug() + "] đã được sử dụng ở danh mục khác!");
        }

        // 3. Validate quan hệ Danh mục Cha - Con
        Long maChaMoi = duLieuMoi.getMaDanhMucCha();
        if (maChaMoi != null) {
            // Không được chọn chính mình làm danh mục cha
            if (maChaMoi.equals(id)) {
                throw new IllegalArgumentException("Một danh mục không thể chọn chính nó làm danh mục cha!");
            }

            // Danh mục cha phải tồn tại
            DanhMuc danhMucCha = danhMucRepository.findById(maChaMoi)
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục cha được chọn không tồn tại trên hệ thống!"));

            // Chống vòng lặp phân cấp (Circular Dependency: chọn con/cháu của chính mình làm cha)
            kiemTraVongLapPhanCap(id, danhMucCha);
        }

        hienTai.setTenDanhMuc(duLieuMoi.getTenDanhMuc());
        hienTai.setDuongDanSlug(duLieuMoi.getDuongDanSlug());
        hienTai.setMaDanhMucCha(maChaMoi);
        hienTai.setThuTuHienThi(duLieuMoi.getThuTuHienThi());
        hienTai.setKichHoat(duLieuMoi.getKichHoat());

        return danhMucRepository.save(hienTai);
    }

    /**
     * Đổi nhanh trạng thái Kích hoạt / Đã ẩn của danh mục.
     */
    @Transactional
    public DanhMuc doiTrangThai(Long id) {
        DanhMuc dm = layTheoId(id);
        boolean trangThaiMoi = (dm.getKichHoat() == null || !dm.getKichHoat());
        dm.setKichHoat(trangThaiMoi);
        return danhMucRepository.save(dm);
    }

    /**
     * Xóa danh mục sản phẩm có kiểm tra ràng buộc danh mục con và sản phẩm.
     */
    @Transactional
    public void xoa(Long id) {
        DanhMuc dm = layTheoId(id);

        // 1. Chặn xóa nếu danh mục đang có danh mục con trực thuộc
        long soDanhMucCon = danhMucRepository.countByMaDanhMucCha(id);
        if (soDanhMucCon > 0) {
            throw new IllegalStateException(
                    "Không thể xóa danh mục [" + dm.getTenDanhMuc() + "] vì đang chứa " + soDanhMucCon + " danh mục con trực thuộc! Vui lòng xóa hoặc chuyển danh mục con trước."
            );
        }

        // 2. Chặn xóa nếu đang có sản phẩm thuộc danh mục này
        long soSanPham = demSoSanPhamThuocDanhMuc(id);
        if (soSanPham > 0) {
            throw new IllegalStateException(
                    "Không thể xóa danh mục [" + dm.getTenDanhMuc() + "] vì đang có " + soSanPham + " sản phẩm thuộc danh mục này!"
            );
        }

        try {
            danhMucRepository.delete(dm);
            danhMucRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "Không thể xóa danh mục [" + dm.getTenDanhMuc() + "] do đang được tham chiếu bởi dữ liệu sản phẩm trên hệ thống!"
            );
        }
    }

    private void chuanHoaVaValidateCoBan(DanhMuc dm) {
        if (dm == null) {
            throw new IllegalArgumentException("Dữ liệu danh mục không được để trống!");
        }

        String ten = dm.getTenDanhMuc() != null ? dm.getTenDanhMuc().trim() : "";
        if (ten.isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }
        if (ten.length() < 2 || ten.length() > 150) {
            throw new IllegalArgumentException("Tên danh mục phải từ 2 đến 150 ký tự!");
        }
        dm.setTenDanhMuc(ten);

        String slug = dm.getDuongDanSlug() != null ? dm.getDuongDanSlug().trim().toLowerCase() : "";
        if (slug.isEmpty()) {
            throw new IllegalArgumentException("Đường dẫn slug không được để trống!");
        }
        if (!slug.matches("^[a-z0-9]+(-[a-z0-9]+)*$")) {
            throw new IllegalArgumentException("Slug chỉ gồm chữ thường không dấu, chữ số và dấu gạch ngang (VD: ao-polo-nam)!");
        }
        dm.setDuongDanSlug(slug);

        if (dm.getThuTuHienThi() == null) {
            dm.setThuTuHienThi(0);
        } else if (dm.getThuTuHienThi() < 0 || dm.getThuTuHienThi() > 9999) {
            throw new IllegalArgumentException("Thứ tự hiển thị phải từ 0 đến 9999!");
        }

        if (dm.getKichHoat() == null) {
            dm.setKichHoat(true);
        }
    }

    private void kiemTraVongLapPhanCap(Long idDanhMucDangSua, DanhMuc danhMucChaDuKien) {
        Set<Long> daDuyet = new HashSet<>();
        daDuyet.add(idDanhMucDangSua);

        Long cursorChaId = danhMucChaDuKien.getId();
        int gioiHanCap = 0;

        while (cursorChaId != null && gioiHanCap < 50) {
            if (daDuyet.contains(cursorChaId)) {
                throw new IllegalArgumentException(
                        "Không thể chọn danh mục [" + danhMucChaDuKien.getTenDanhMuc() + "] làm danh mục cha vì đây là danh mục con/cháu (gây vòng lặp phân cấp)!"
                );
            }
            daDuyet.add(cursorChaId);
            Optional<DanhMuc> chaTiepTheo = danhMucRepository.findById(cursorChaId);
            cursorChaId = chaTiepTheo.map(DanhMuc::getMaDanhMucCha).orElse(null);
            gioiHanCap++;
        }
    }

    private long demSoSanPhamThuocDanhMuc(Long danhMucId) {
        try {
            Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [san_pham] WHERE [ma_danh_muc_id] = ?",
                    Long.class,
                    danhMucId
            );
            return count != null ? count : 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }
}
