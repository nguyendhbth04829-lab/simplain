package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý nghiệp vụ và Validate chuyên nghiệp cho Quản lý Thông tin chung Sản phẩm (US08)
 * và Tìm kiếm & Lọc danh sách sản phẩm trên Storefront cho Khách hàng (US10).
 *
 * <p>Đáp ứng tiêu chí US08 & US10:
 * 1. Tạo san_pham gắn với danh mục (ma_danh_muc_id) và bộ sưu tập tùy chọn (ma_bo_suu_tap_id).
 * 2. Nhập và chuẩn hóa chất liệu (chat_lieu), form dáng (form_dang), mô tả chi tiết (mo_ta_chi_tiet), hướng dẫn chọn size.
 * 3. Hiển thị Grid sản phẩm, Tìm kiếm text và Lọc đa tiêu chí theo Giá, Màu, Size, Danh mục trên Storefront (US10).
 * 4. Bảo vệ toàn vẹn dữ liệu khi xóa: Chặn xóa sản phẩm đang có biến thể SKU, hình ảnh, combo hoặc đánh giá.
 */
@Service
public class SanPhamService {

    public static final String ANH_MAC_DINH_STOREFRONT =
            "https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=500&auto=format&fit=crop&q=80";

    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;
    private final BoSuuTapRepository boSuuTapRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final HinhAnhRepository hinhAnhRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final FormDangRepository formDangRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SanPhamService(SanPhamRepository sanPhamRepository,
                          DanhMucRepository danhMucRepository,
                          BoSuuTapRepository boSuuTapRepository,
                          BienTheSanPhamRepository bienTheSanPhamRepository,
                          HinhAnhRepository hinhAnhRepository,
                          MauSacRepository mauSacRepository,
                          KichThuocRepository kichThuocRepository,
                          ChatLieuRepository chatLieuRepository,
                          FormDangRepository formDangRepository,
                          JdbcTemplate jdbcTemplate) {
        this.sanPhamRepository = sanPhamRepository;
        this.danhMucRepository = danhMucRepository;
        this.boSuuTapRepository = boSuuTapRepository;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
        this.hinhAnhRepository = hinhAnhRepository;
        this.mauSacRepository = mauSacRepository;
        this.kichThuocRepository = kichThuocRepository;
        this.chatLieuRepository = chatLieuRepository;
        this.formDangRepository = formDangRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Lấy danh sách sản phẩm có phân trang, tìm kiếm đa tiêu chí và lọc theo danh mục, bộ sưu tập, form dáng, trạng thái (US08).
     */
    @Transactional(readOnly = true)
    public Page<SanPham> layDanhSachPhanTrang(String tuKhoa,
                                              Long maDanhMucId,
                                              Long maBoSuuTapId,
                                              String formDang,
                                              Boolean kichHoat,
                                              Pageable pageable) {
        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        String formDangChuan = (formDang != null && !formDang.isBlank()) ? formDang.trim() : null;

        Page<SanPham> pageData = sanPhamRepository.timKiemPhanTrang(
                tuKhoaChuan, maDanhMucId, maBoSuuTapId, formDangChuan, kichHoat, pageable);

        lamGiauThongTinHienThi(pageData.getContent());
        return pageData;
    }

    /**
     * Tìm kiếm và lọc danh sách sản phẩm trên Storefront cho Khách hàng (US10):
     * 1. Hiển thị Grid sản phẩm (chỉ lấy sản phẩm đang kích hoạt mở bán, làm giàu ảnh đại diện, màu sắc, size, tồn kho).
     * 2. Bộ lọc Filter theo Danh mục (hỗ trợ cả Danh mục Cha và Con), Khoảng Giá (giaTu - giaDen hoặc mucGia),
     *    Màu sắc (maMauSacId), Kích thước/Size (maKichThuocId), Bộ sưu tập (maBoSuuTapId).
     * 3. Tìm kiếm text (tuKhoa).
     */
    @Transactional(readOnly = true)
    public Page<SanPham> timKiemVaLocStorefront(String tuKhoa,
                                                Long maDanhMucId,
                                                Long maBoSuuTapId,
                                                Long maMauSacId,
                                                Long maKichThuocId,
                                                String mucGia,
                                                BigDecimal giaTu,
                                                BigDecimal giaDen,
                                                String sapXep,
                                                int page,
                                                int size) {
        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;

        // Chuẩn hóa khoảng giá từ nút chọn nhanh (mucGia) hoặc từ ô nhập tùy chỉnh (giaTu, giaDen)
        BigDecimal minPrice = giaTu;
        BigDecimal maxPrice = giaDen;
        if (minPrice == null && maxPrice == null && mucGia != null && !mucGia.isBlank()) {
            switch (mucGia.trim().toLowerCase()) {
                case "duoi-200k" -> {
                    minPrice = BigDecimal.ZERO;
                    maxPrice = new BigDecimal("200000");
                }
                case "200k-350k" -> {
                    minPrice = new BigDecimal("200000");
                    maxPrice = new BigDecimal("350000");
                }
                case "tren-350k" -> {
                    minPrice = new BigDecimal("350000");
                    maxPrice = null;
                }
                default -> {
                }
            }
        }

        // Validate khoảng giá hợp lệ
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Mức giá tối thiểu (Giá từ) không được nhỏ hơn 0 VNĐ!");
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Mức giá tối đa (Giá đến) không được nhỏ hơn 0 VNĐ!");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Khoảng giá lọc không hợp lệ: 'Giá từ' không được lớn hơn 'Giá đến'!");
        }

        // Hỗ trợ lọc theo Danh mục Cha hoặc Danh mục Con:
        // Nếu khách hàng chọn 1 danh mục Cha, tự động lấy cả sản phẩm của danh mục Cha lẫn các danh mục Con trực thuộc
        boolean locTheoDanhMuc = (maDanhMucId != null);
        List<Long> danhSachDanhMucId = new ArrayList<>();
        if (locTheoDanhMuc) {
            danhSachDanhMucId.add(maDanhMucId);
            List<DanhMuc> danhMucCon = danhMucRepository.findByMaDanhMucChaOrderByThuTuHienThiAsc(maDanhMucId);
            for (DanhMuc dmCon : danhMucCon) {
                if (dmCon.getId() != null && !danhSachDanhMucId.contains(dmCon.getId())) {
                    danhSachDanhMucId.add(dmCon.getId());
                }
            }
        } else {
            // Danh sách giả để tránh lỗi cú pháp IN () rỗng trên SQL Server khi locTheoDanhMuc = false
            danhSachDanhMucId.add(-1L);
        }

        // Thiết lập tiêu chí sắp xếp trên Grid sản phẩm
        Sort sort = taoSortStorefront(sapXep);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), sort);

        Page<SanPham> pageData = sanPhamRepository.timKiemStorefront(
                tuKhoaChuan,
                locTheoDanhMuc,
                danhSachDanhMucId,
                maBoSuuTapId,
                minPrice,
                maxPrice,
                maMauSacId,
                maKichThuocId,
                pageable
        );

        lamGiauThongTinHienThi(pageData.getContent());
        return pageData;
    }

    private Sort taoSortStorefront(String sapXep) {
        if (sapXep == null || sapXep.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "noiBat").and(Sort.by(Sort.Direction.DESC, "id"));
        }
        return switch (sapXep.trim().toLowerCase()) {
            case "gia-tang" -> Sort.by(Sort.Direction.ASC, "giaCoBan").and(Sort.by(Sort.Direction.DESC, "id"));
            case "gia-giam" -> Sort.by(Sort.Direction.DESC, "giaCoBan").and(Sort.by(Sort.Direction.DESC, "id"));
            case "ten-az" -> Sort.by(Sort.Direction.ASC, "tenSanPham");
            case "ten-za" -> Sort.by(Sort.Direction.DESC, "tenSanPham");
            case "moi-nhat" -> Sort.by(Sort.Direction.DESC, "id");
            default -> Sort.by(Sort.Direction.DESC, "noiBat").and(Sort.by(Sort.Direction.DESC, "id"));
        };
    }

    /**
     * Làm giàu thông tin hiển thị cho danh sách sản phẩm (tên danh mục, bộ sưu tập, ảnh đại diện,
     * khoảng giá biến thể, tổng tồn kho, danh sách màu sắc và kích thước đang có).
     */
    public void lamGiauThongTinHienThi(List<SanPham> danhSachSanPham) {
        if (danhSachSanPham == null || danhSachSanPham.isEmpty()) {
            return;
        }

        Map<Long, String> mapDanhMuc = danhMucRepository.findAll().stream()
                .collect(Collectors.toMap(DanhMuc::getId, DanhMuc::getTenDanhMuc, (a, b) -> a));

        Map<Long, String> mapBoSuuTap = boSuuTapRepository.findAll().stream()
                .collect(Collectors.toMap(BoSuuTap::getId, BoSuuTap::getTenBoSuuTap, (a, b) -> a));

        Map<Long, MauSac> mapMauSac = mauSacRepository.findAll().stream()
                .collect(Collectors.toMap(MauSac::getId, m -> m, (a, b) -> a));

        Map<Long, KichThuoc> mapKichThuoc = kichThuocRepository.findAll().stream()
                .collect(Collectors.toMap(KichThuoc::getId, k -> k, (a, b) -> a));

        for (SanPham sp : danhSachSanPham) {
            if (sp.getMaDanhMucId() != null) {
                sp.setTenDanhMuc(mapDanhMuc.getOrDefault(sp.getMaDanhMucId(), "Danh mục #" + sp.getMaDanhMucId()));
            }
            if (sp.getMaBoSuuTapId() != null) {
                sp.setTenBoSuuTap(mapBoSuuTap.getOrDefault(sp.getMaBoSuuTapId(), "BST #" + sp.getMaBoSuuTapId()));
            }

            List<BienTheSanPham> tatCaBienThe = bienTheSanPhamRepository.findByMaSanPhamIdOrderByIdDesc(sp.getId());
            sp.setSoBienThe(tatCaBienThe.size());

            List<BienTheSanPham> bienTheKichHoat = tatCaBienThe.stream()
                    .filter(bt -> Boolean.TRUE.equals(bt.getKichHoat()))
                    .toList();

            int tongTon = bienTheKichHoat.stream()
                    .mapToInt(bt -> bt.getSoLuong() != null ? bt.getSoLuong() : 0)
                    .sum();
            sp.setTongTonKho(tongTon);

            // Tính khoảng giá thấp nhất / cao nhất từ biến thể đang kích hoạt (fallback về giá cơ bản)
            BigDecimal giaMin = bienTheKichHoat.stream()
                    .map(BienTheSanPham::getGiaBan)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .orElse(sp.getGiaCoBan() != null ? sp.getGiaCoBan() : BigDecimal.ZERO);

            BigDecimal giaMax = bienTheKichHoat.stream()
                    .map(BienTheSanPham::getGiaBan)
                    .filter(Objects::nonNull)
                    .max(BigDecimal::compareTo)
                    .orElse(giaMin);

            sp.setGiaThapNhat(giaMin);
            sp.setGiaCaoNhat(giaMax);

            // Lấy toàn bộ ảnh của sản phẩm từ bảng hinh_anh (Ưu tiên ảnh cấp sản phẩm)
            List<HinhAnh> danhSachAnhSp = new ArrayList<>(
                    hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sp.getId())
            );
            if (danhSachAnhSp.isEmpty()) {
                danhSachAnhSp.addAll(hinhAnhRepository.findByMaSanPhamIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(sp.getId()));
            }
            if (!danhSachAnhSp.isEmpty() && danhSachAnhSp.get(0).getDuongDanAnh() != null && !danhSachAnhSp.get(0).getDuongDanAnh().isBlank()) {
                sp.setAnhDaiDienUrl(danhSachAnhSp.get(0).getDuongDanAnh());
            } else {
                sp.setAnhDaiDienUrl(ANH_MAC_DINH_STOREFRONT);
                danhSachAnhSp.add(HinhAnh.builder()
                        .id(0L)
                        .maSanPhamId(sp.getId())
                        .duongDanAnh(ANH_MAC_DINH_STOREFRONT)
                        .laAnhDaiDien(true)
                        .thuTu(1)
                        .build());
            }
            sp.setDanhSachHinhAnh(danhSachAnhSp);

            // Sắp xếp biến thể theo ID tăng dần để biến thể mặc định luôn ổn định
            List<BienTheSanPham> bienTheSapXep = new ArrayList<>(bienTheKichHoat);
            bienTheSapXep.sort(Comparator.comparing(BienTheSanPham::getId));

            // Tập hợp danh sách Màu sắc, Kích thước duy nhất và làm giàu từng biến thể SKU (US11)
            Map<Long, MauSac> mauDuyNhat = new LinkedHashMap<>();
            Map<Long, KichThuoc> sizeDuyNhat = new LinkedHashMap<>();
            for (BienTheSanPham bt : bienTheSapXep) {
                bt.setMaSanPham(sp.getMaSanPham());
                bt.setTenSanPham(sp.getTenSanPham());

                if (bt.getMaMauSacId() != null && mapMauSac.containsKey(bt.getMaMauSacId())) {
                    MauSac ms = mapMauSac.get(bt.getMaMauSacId());
                    mauDuyNhat.putIfAbsent(bt.getMaMauSacId(), ms);
                    bt.setTenMauSac(ms.getTenMau());
                    bt.setMaMauHex(ms.getMaMau());
                } else {
                    bt.setTenMauSac("Màu #" + bt.getMaMauSacId());
                    bt.setMaMauHex("#000000");
                }

                if (bt.getMaKichThuocId() != null && mapKichThuoc.containsKey(bt.getMaKichThuocId())) {
                    KichThuoc kt = mapKichThuoc.get(bt.getMaKichThuocId());
                    sizeDuyNhat.putIfAbsent(bt.getMaKichThuocId(), kt);
                    bt.setMaKichThuoc(kt.getMaKichThuoc());
                    String sizeRutGon = (kt.getMaKichThuoc() != null && !kt.getMaKichThuoc().isBlank())
                            ? kt.getMaKichThuoc()
                            : (kt.getTenKichThuoc() != null ? kt.getTenKichThuoc().replaceAll("\\s*\\([^)]*\\)", "").trim() : "");
                    bt.setTenKichThuoc(sizeRutGon);
                } else {
                    bt.setMaKichThuoc("SIZE#" + bt.getMaKichThuocId());
                }

                List<HinhAnh> dsAnhBienThe = hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(bt.getId());
                bt.setDanhSachHinhAnh(dsAnhBienThe);
                if (!dsAnhBienThe.isEmpty() && dsAnhBienThe.get(0).getDuongDanAnh() != null && !dsAnhBienThe.get(0).getDuongDanAnh().isBlank()) {
                    bt.setAnhDaiDienUrl(dsAnhBienThe.get(0).getDuongDanAnh());
                } else {
                    bt.setAnhDaiDienUrl(sp.getAnhDaiDienUrl());
                }
            }
            sp.setDanhSachMauSac(new ArrayList<>(mauDuyNhat.values()));
            sp.setDanhSachKichThuoc(new ArrayList<>(sizeDuyNhat.values()));
            sp.setDanhSachBienThe(bienTheSapXep);
        }
    }

    /**
     * Lấy chi tiết sản phẩm hiển thị trên Storefront cho Khách hàng theo ID (US11).
     * Chỉ cho phép xem sản phẩm đang mở bán (kichHoat = true).
     */
    @Transactional(readOnly = true)
    public SanPham layChiTietSanPhamStorefront(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống!");
        }
        SanPham sp = sanPhamRepository.findById(id)
                .filter(item -> Boolean.TRUE.equals(item.getKichHoat()))
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh!"));
        lamGiauThongTinHienThi(Collections.singletonList(sp));
        return sp;
    }

    /**
     * Lấy chi tiết sản phẩm hiển thị trên Storefront cho Khách hàng theo đường dẫn Slug (US11).
     */
    @Transactional(readOnly = true)
    public SanPham layChiTietSanPhamTheoSlugStorefront(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("Đường dẫn sản phẩm không hợp lệ!");
        }
        SanPham sp = sanPhamRepository.findByDuongDanSlug(slug.trim())
                .filter(item -> Boolean.TRUE.equals(item.getKichHoat()))
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh!"));
        lamGiauThongTinHienThi(Collections.singletonList(sp));
        return sp;
    }

    /**
     * Lấy danh sách sản phẩm liên quan cùng danh mục trên Storefront (US11).
     */
    @Transactional(readOnly = true)
    public List<SanPham> laySanPhamLienQuanStorefront(Long sanPhamHienTaiId, Long maDanhMucId, int gioiHan) {
        List<SanPham> tatCaCungDanhMuc = sanPhamRepository.findAll().stream()
                .filter(sp -> Boolean.TRUE.equals(sp.getKichHoat()))
                .filter(sp -> !Objects.equals(sp.getId(), sanPhamHienTaiId))
                .sorted(Comparator.comparing(
                        (SanPham sp) -> Objects.equals(sp.getMaDanhMucId(), maDanhMucId) ? 0 : 1
                ).thenComparing(SanPham::getId, Comparator.reverseOrder()))
                .limit(Math.max(gioiHan, 1))
                .toList();
        lamGiauThongTinHienThi(tatCaCungDanhMuc);
        return tatCaCungDanhMuc;
    }

    /**
     * Lấy tất cả sản phẩm.
     */
    @Transactional(readOnly = true)
    public List<SanPham> layTatCa() {
        return sanPhamRepository.findAll();
    }

    /**
     * Đếm tổng số lượng sản phẩm trên hệ thống.
     */
    @Transactional(readOnly = true)
    public long demTongSanPham() {
        return sanPhamRepository.count();
    }

    /**
     * Đếm số lượng sản phẩm đang mở bán (kichHoat = true).
     */
    @Transactional(readOnly = true)
    public long demSanPhamDangBan() {
        return sanPhamRepository.countByKichHoatTrue();
    }

    /**
     * Đếm số lượng sản phẩm nổi bật (noiBat = true).
     */
    @Transactional(readOnly = true)
    public long demSanPhamNoiBat() {
        return sanPhamRepository.countByNoiBatTrue();
    }

    /**
     * Lấy chi tiết sản phẩm theo ID kèm thông tin tên danh mục, bộ sưu tập và số biến thể.
     */
    @Transactional(readOnly = true)
    public SanPham layTheoId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống!");
        }
        SanPham sp = sanPhamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + id));

        if (sp.getMaDanhMucId() != null) {
            danhMucRepository.findById(sp.getMaDanhMucId())
                    .ifPresent(dm -> sp.setTenDanhMuc(dm.getTenDanhMuc()));
        }
        if (sp.getMaBoSuuTapId() != null) {
            boSuuTapRepository.findById(sp.getMaBoSuuTapId())
                    .ifPresent(bst -> sp.setTenBoSuuTap(bst.getTenBoSuuTap()));
        }
        if (sp.getMaChatLieuId() == null && sp.getChatLieu() != null) {
            chatLieuRepository.findByTenChatLieu(sp.getChatLieu().trim())
                    .ifPresent(cl -> sp.setMaChatLieuId(cl.getId()));
        }
        if (sp.getMaFormDangId() == null && sp.getFormDang() != null) {
            formDangRepository.findByTenFormDang(sp.getFormDang().trim())
                    .ifPresent(fd -> sp.setMaFormDangId(fd.getId()));
        }
        sp.setSoBienThe(demSoBienTheCuaSanPham(sp.getId()));

        List<HinhAnh> dsAnh = layDanhSachHinhAnhSanPham(sp.getId());
        sp.setDanhSachHinhAnh(dsAnh);
        if (!dsAnh.isEmpty() && dsAnh.get(0).getDuongDanAnh() != null && !dsAnh.get(0).getDuongDanAnh().isBlank()) {
            sp.setAnhDaiDienUrl(dsAnh.get(0).getDuongDanAnh());
        } else {
            sp.setAnhDaiDienUrl(ANH_MAC_DINH_STOREFRONT);
        }

        return sp;
    }

    /**
     * Thêm mới sản phẩm gắn với danh mục, bộ sưu tập và thông tin chất liệu, form dáng, mô tả chi tiết (US08).
     */
    @Transactional
    public SanPham them(SanPham sanPham) {
        chuanHoaVaValidateCoBan(sanPham);

        // 1. Kiểm tra danh mục tồn tại
        if (!danhMucRepository.existsById(sanPham.getMaDanhMucId())) {
            throw new IllegalArgumentException("Danh mục được chọn không tồn tại trên hệ thống!");
        }

        // 2. Kiểm tra bộ sưu tập tồn tại (nếu có chọn)
        if (sanPham.getMaBoSuuTapId() != null && !boSuuTapRepository.existsById(sanPham.getMaBoSuuTapId())) {
            throw new IllegalArgumentException("Bộ sưu tập được chọn không tồn tại trên hệ thống!");
        }

        // 3. Kiểm tra trùng mã sản phẩm
        if (sanPhamRepository.existsByMaSanPhamIgnoreCase(sanPham.getMaSanPham())) {
            throw new IllegalArgumentException("Mã sản phẩm [" + sanPham.getMaSanPham() + "] đã tồn tại trên hệ thống!");
        }

        // 4. Kiểm tra trùng tên sản phẩm
        if (sanPhamRepository.existsByTenSanPhamIgnoreCase(sanPham.getTenSanPham())) {
            throw new IllegalArgumentException("Tên sản phẩm [" + sanPham.getTenSanPham() + "] đã tồn tại trên hệ thống!");
        }

        // 5. Kiểm tra trùng đường dẫn slug
        if (sanPhamRepository.existsByDuongDanSlugIgnoreCase(sanPham.getDuongDanSlug())) {
            throw new IllegalArgumentException("Đường dẫn slug [" + sanPham.getDuongDanSlug() + "] đã được sử dụng!");
        }

        if (sanPham.getMaChatLieuId() != null) {
            chatLieuRepository.findById(sanPham.getMaChatLieuId())
                    .ifPresent(cl -> sanPham.setChatLieu(cl.getTenChatLieu()));
        } else if (sanPham.getChatLieu() != null && !sanPham.getChatLieu().isBlank()) {
            chatLieuRepository.findByTenChatLieu(sanPham.getChatLieu().trim())
                    .ifPresent(cl -> sanPham.setMaChatLieuId(cl.getId()));
        }

        if (sanPham.getMaFormDangId() != null) {
            formDangRepository.findById(sanPham.getMaFormDangId())
                    .ifPresent(fd -> sanPham.setFormDang(fd.getTenFormDang()));
        } else if (sanPham.getFormDang() != null && !sanPham.getFormDang().isBlank()) {
            formDangRepository.findByTenFormDang(sanPham.getFormDang().trim())
                    .ifPresent(fd -> sanPham.setMaFormDangId(fd.getId()));
        }

        if (sanPham.getNgayTao() == null) {
            sanPham.setNgayTao(LocalDateTime.now());
        }

        return sanPhamRepository.save(sanPham);
    }

    /**
     * Cập nhật thông tin chung sản phẩm (US08).
     */
    @Transactional
    public SanPham sua(Long id, SanPham duLieuMoi) {
        SanPham hienTai = layTheoId(id);
        chuanHoaVaValidateCoBan(duLieuMoi);

        // 1. Kiểm tra danh mục tồn tại
        if (!danhMucRepository.existsById(duLieuMoi.getMaDanhMucId())) {
            throw new IllegalArgumentException("Danh mục được chọn không tồn tại trên hệ thống!");
        }

        // 2. Kiểm tra bộ sưu tập tồn tại (nếu có chọn)
        if (duLieuMoi.getMaBoSuuTapId() != null && !boSuuTapRepository.existsById(duLieuMoi.getMaBoSuuTapId())) {
            throw new IllegalArgumentException("Bộ sưu tập được chọn không tồn tại trên hệ thống!");
        }

        // 3. Kiểm tra trùng mã sản phẩm (loại trừ chính ID đang sửa)
        if (sanPhamRepository.existsByMaSanPhamIgnoreCaseAndIdNot(duLieuMoi.getMaSanPham(), id)) {
            throw new IllegalArgumentException("Mã sản phẩm [" + duLieuMoi.getMaSanPham() + "] đã được sử dụng ở sản phẩm khác!");
        }

        // 4. Kiểm tra trùng tên sản phẩm (loại trừ chính ID đang sửa)
        if (sanPhamRepository.existsByTenSanPhamIgnoreCaseAndIdNot(duLieuMoi.getTenSanPham(), id)) {
            throw new IllegalArgumentException("Tên sản phẩm [" + duLieuMoi.getTenSanPham() + "] đã tồn tại ở sản phẩm khác!");
        }

        // 5. Kiểm tra trùng đường dẫn slug (loại trừ chính ID đang sửa)
        if (sanPhamRepository.existsByDuongDanSlugIgnoreCaseAndIdNot(duLieuMoi.getDuongDanSlug(), id)) {
            throw new IllegalArgumentException("Đường dẫn slug [" + duLieuMoi.getDuongDanSlug() + "] đã được sử dụng ở sản phẩm khác!");
        }

        hienTai.setMaDanhMucId(duLieuMoi.getMaDanhMucId());
        hienTai.setMaBoSuuTapId(duLieuMoi.getMaBoSuuTapId());
        hienTai.setMaSanPham(duLieuMoi.getMaSanPham());
        hienTai.setTenSanPham(duLieuMoi.getTenSanPham());
        hienTai.setDuongDanSlug(duLieuMoi.getDuongDanSlug());

        hienTai.setMaChatLieuId(duLieuMoi.getMaChatLieuId());
        hienTai.setMaFormDangId(duLieuMoi.getMaFormDangId());
        if (duLieuMoi.getMaChatLieuId() != null) {
            chatLieuRepository.findById(duLieuMoi.getMaChatLieuId())
                    .ifPresent(cl -> hienTai.setChatLieu(cl.getTenChatLieu()));
        } else if (duLieuMoi.getChatLieu() != null) {
            hienTai.setChatLieu(duLieuMoi.getChatLieu());
            chatLieuRepository.findByTenChatLieu(duLieuMoi.getChatLieu().trim())
                    .ifPresent(cl -> hienTai.setMaChatLieuId(cl.getId()));
        }

        if (duLieuMoi.getMaFormDangId() != null) {
            formDangRepository.findById(duLieuMoi.getMaFormDangId())
                    .ifPresent(fd -> hienTai.setFormDang(fd.getTenFormDang()));
        } else if (duLieuMoi.getFormDang() != null) {
            hienTai.setFormDang(duLieuMoi.getFormDang());
            formDangRepository.findByTenFormDang(duLieuMoi.getFormDang().trim())
                    .ifPresent(fd -> hienTai.setMaFormDangId(fd.getId()));
        }

        hienTai.setMoTaChiTiet(duLieuMoi.getMoTaChiTiet());
        hienTai.setHuongDanChonSize(duLieuMoi.getHuongDanChonSize());
        if (duLieuMoi.getVideoUrl() != null && !duLieuMoi.getVideoUrl().isBlank()) {
            hienTai.setVideoUrl(duLieuMoi.getVideoUrl());
        }
        hienTai.setGiaCoBan(duLieuMoi.getGiaCoBan());
        hienTai.setNoiBat(duLieuMoi.getNoiBat());
        hienTai.setKichHoat(duLieuMoi.getKichHoat());

        return sanPhamRepository.save(hienTai);
    }

    /**
     * Đổi nhanh trạng thái Kinh doanh (Đang mở bán / Ngừng kinh doanh) của sản phẩm.
     */
    @Transactional
    public SanPham doiTrangThai(Long id) {
        SanPham sp = layTheoId(id);
        boolean trangThaiMoi = (sp.getKichHoat() == null || !sp.getKichHoat());
        sp.setKichHoat(trangThaiMoi);
        return sanPhamRepository.save(sp);
    }

    /**
     * Đổi nhanh trạng thái Nổi bật (Best Seller / Thường) của sản phẩm.
     */
    @Transactional
    public SanPham doiNoiBat(Long id) {
        SanPham sp = layTheoId(id);
        boolean noiBatMoi = (sp.getNoiBat() == null || !sp.getNoiBat());
        sp.setNoiBat(noiBatMoi);
        return sanPhamRepository.save(sp);
    }

    /**
     * Xóa sản phẩm có kiểm tra ràng buộc dữ liệu biến thể, hình ảnh, combo, đánh giá.
     */
    @Transactional
    public void xoa(Long id) {
        SanPham sp = layTheoId(id);

        long soBienThe = demSoBienTheCuaSanPham(id);
        if (soBienThe > 0) {
            throw new IllegalStateException(
                    "Không thể xóa sản phẩm [" + sp.getTenSanPham() + "] vì đang có " + soBienThe + " biến thể SKU thuộc sản phẩm này!"
            );
        }

        long soHinhAnh = demBanGhiThamChieu("hinh_anh", id);
        if (soHinhAnh > 0) {
            throw new IllegalStateException(
                    "Không thể xóa sản phẩm [" + sp.getTenSanPham() + "] vì đang có " + soHinhAnh + " hình ảnh gắn với sản phẩm này!"
            );
        }

        long soCombo = demBanGhiThamChieu("san_pham_trong_combo", id);
        if (soCombo > 0) {
            throw new IllegalStateException(
                    "Không thể xóa sản phẩm [" + sp.getTenSanPham() + "] vì sản phẩm đang nằm trong gói Combo khuyến mãi!"
            );
        }

        try {
            sanPhamRepository.delete(sp);
            sanPhamRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "Không thể xóa sản phẩm [" + sp.getTenSanPham() + "] do đang được tham chiếu bởi dữ liệu biến thể hoặc đơn hàng trên hệ thống!"
            );
        }
    }

    private void chuanHoaVaValidateCoBan(SanPham sp) {
        if (sp == null) {
            throw new IllegalArgumentException("Dữ liệu sản phẩm không được để trống!");
        }

        if (sp.getMaDanhMucId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn danh mục cho sản phẩm!");
        }

        String maSp = sp.getMaSanPham() != null ? sp.getMaSanPham().trim().toUpperCase() : "";
        if (maSp.isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống!");
        }
        if (maSp.length() < 3 || maSp.length() > 50) {
            throw new IllegalArgumentException("Mã sản phẩm phải từ 3 đến 50 ký tự!");
        }
        if (!maSp.matches("^[A-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Mã sản phẩm chỉ gồm chữ cái không dấu, chữ số, dấu gạch ngang (-) hoặc gạch dưới (_)!");
        }
        sp.setMaSanPham(maSp);

        String tenSp = sp.getTenSanPham() != null ? sp.getTenSanPham().trim() : "";
        if (tenSp.isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
        }
        if (tenSp.length() < 3 || tenSp.length() > 255) {
            throw new IllegalArgumentException("Tên sản phẩm phải từ 3 đến 255 ký tự!");
        }
        sp.setTenSanPham(tenSp);

        String slug = sp.getDuongDanSlug() != null ? sp.getDuongDanSlug().trim().toLowerCase() : "";
        if (slug.isEmpty()) {
            throw new IllegalArgumentException("Đường dẫn slug không được để trống!");
        }
        if (slug.length() < 3 || slug.length() > 255) {
            throw new IllegalArgumentException("Đường dẫn slug phải từ 3 đến 255 ký tự!");
        }
        if (!slug.matches("^[a-z0-9]+(-[a-z0-9]+)*$")) {
            throw new IllegalArgumentException("Slug chỉ gồm chữ thường không dấu, chữ số và dấu gạch ngang (VD: ao-polo-nam-excool)!");
        }
        sp.setDuongDanSlug(slug);

        String chatLieu = sp.getChatLieu() != null ? sp.getChatLieu().trim() : "";
        if (chatLieu.isEmpty()) {
            throw new IllegalArgumentException("Chất liệu sản phẩm không được để trống!");
        }
        if (chatLieu.length() < 2 || chatLieu.length() > 150) {
            throw new IllegalArgumentException("Chất liệu sản phẩm phải từ 2 đến 150 ký tự!");
        }
        sp.setChatLieu(chatLieu);

        String formDang = sp.getFormDang() != null ? sp.getFormDang().trim() : "";
        if (formDang.isEmpty()) {
            throw new IllegalArgumentException("Form dáng sản phẩm không được để trống!");
        }
        if (formDang.length() < 2 || formDang.length() > 100) {
            throw new IllegalArgumentException("Form dáng sản phẩm phải từ 2 đến 100 ký tự!");
        }
        sp.setFormDang(formDang);

        String moTa = sp.getMoTaChiTiet() != null ? sp.getMoTaChiTiet().trim() : "";
        if (moTa.isEmpty()) {
            throw new IllegalArgumentException("Mô tả chi tiết sản phẩm không được để trống!");
        }
        if (moTa.length() < 10 || moTa.length() > 5000) {
            throw new IllegalArgumentException("Mô tả chi tiết sản phẩm phải từ 10 đến 5000 ký tự!");
        }
        sp.setMoTaChiTiet(moTa);

        if (sp.getHuongDanChonSize() != null) {
            String hdSize = sp.getHuongDanChonSize().trim();
            if (hdSize.length() > 3000) {
                throw new IllegalArgumentException("Hướng dẫn chọn size không được vượt quá 3000 ký tự!");
            }
            sp.setHuongDanChonSize(hdSize.isEmpty() ? null : hdSize);
        }

        if (sp.getGiaCoBan() == null) {
            throw new IllegalArgumentException("Giá cơ bản không được để trống!");
        }
        if (sp.getGiaCoBan().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá cơ bản không được nhỏ hơn 0 VNĐ!");
        }
        if (sp.getGiaCoBan().compareTo(new BigDecimal("999999999")) > 0) {
            throw new IllegalArgumentException("Giá cơ bản không được vượt quá 999.999.999 VNĐ!");
        }

        if (sp.getNoiBat() == null) {
            sp.setNoiBat(false);
        }
        if (sp.getKichHoat() == null) {
            sp.setKichHoat(true);
        }
    }

    private long demSoBienTheCuaSanPham(Long sanPhamId) {
        return demBanGhiThamChieu("bien_the_san_pham", sanPhamId);
    }

    private long demBanGhiThamChieu(String tenBang, Long sanPhamId) {
        try {
            Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [" + tenBang + "] WHERE [ma_san_pham_id] = ?",
                    Long.class,
                    sanPhamId
            );
            return count != null ? count : 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Lấy toàn bộ hình ảnh thuộc sản phẩm (ảnh bìa đại diện và toàn bộ ảnh phụ).
     */
    @Transactional(readOnly = true)
    public List<HinhAnh> layDanhSachHinhAnhSanPham(Long sanPhamId) {
        if (sanPhamId == null) {
            return Collections.emptyList();
        }
        List<HinhAnh> list = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
        if (list.isEmpty()) {
            list = hinhAnhRepository.findByMaSanPhamIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
        }
        return list;
    }

    /**
     * Tải lên và lưu danh sách hình ảnh (File từ máy tính hoặc Link URL) cho sản phẩm (chuẩn sàn TMĐT SIMPLAIN).
     */
    @Transactional
    public int luuDanhSachHinhAnhSanPham(Long sanPhamId, MultipartFile[] filesAnh, String duongDanAnhUrl) {
        if (sanPhamId == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống!");
        }
        SanPham sp = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + sanPhamId));

        List<MultipartFile> filesHopLe = locVaValidateDanhSachFileAnh(filesAnh);
        boolean coUrl = duongDanAnhUrl != null && !duongDanAnhUrl.isBlank();

        if (filesHopLe.isEmpty() && !coUrl) {
            return 0;
        }

        List<HinhAnh> hienCo = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
        boolean daCoAnhDaiDien = hienCo.stream().anyMatch(ha -> Boolean.TRUE.equals(ha.getLaAnhDaiDien()));
        int thuTuTiepTheo = hienCo.size() + 1;
        int soAnhDaLuu = 0;

        // 1. Lưu các file upload từ máy tính
        if (!filesHopLe.isEmpty()) {
            try {
                Path thuMucUpload = Paths.get("uploads");
                if (!Files.exists(thuMucUpload)) {
                    Files.createDirectories(thuMucUpload);
                }

                for (MultipartFile file : filesHopLe) {
                    String tenGoc = Objects.requireNonNull(file.getOriginalFilename());
                    String duoiFile = tenGoc.substring(tenGoc.lastIndexOf('.')).toLowerCase();
                    String tenFileAnToan = "sp_" + sanPhamId + "_" + System.currentTimeMillis() + "_" +
                            UUID.randomUUID().toString().substring(0, 6) + duoiFile;

                    Files.copy(file.getInputStream(), thuMucUpload.resolve(tenFileAnToan), StandardCopyOption.REPLACE_EXISTING);

                    boolean datLamDaiDien = !daCoAnhDaiDien;
                    if (datLamDaiDien) {
                        daCoAnhDaiDien = true;
                    }

                    HinhAnh hinhAnh = HinhAnh.builder()
                            .maSanPhamId(sanPhamId)
                            .maBienTheId(null)
                            .duongDanAnh("/uploads/" + tenFileAnToan)
                            .laAnhDaiDien(datLamDaiDien)
                            .thuTu(thuTuTiepTheo++)
                            .build();
                    hinhAnhRepository.save(hinhAnh);
                    soAnhDaLuu++;
                }
            } catch (IOException e) {
                throw new IllegalStateException("Lỗi khi lưu file hình ảnh sản phẩm lên máy chủ: " + e.getMessage());
            }
        }

        // 2. Lưu các URL ảnh (hỗ trợ nhiều link phân cách bằng phẩy hoặc xuống dòng)
        if (coUrl) {
            String[] urls = duongDanAnhUrl.split("[,\\r\\n]+");
            for (String rawUrl : urls) {
                String cleanUrl = rawUrl.trim();
                if (!cleanUrl.isEmpty()) {
                    boolean datLamDaiDien = !daCoAnhDaiDien;
                    if (datLamDaiDien) {
                        daCoAnhDaiDien = true;
                    }
                    HinhAnh hinhAnh = HinhAnh.builder()
                            .maSanPhamId(sanPhamId)
                            .maBienTheId(null)
                            .duongDanAnh(cleanUrl)
                            .laAnhDaiDien(datLamDaiDien)
                            .thuTu(thuTuTiepTheo++)
                            .build();
                    hinhAnhRepository.save(hinhAnh);
                    soAnhDaLuu++;
                }
            }
        }

        hinhAnhRepository.flush();
        return soAnhDaLuu;
    }

    /**
     * Tải lên và lưu riêng biệt Ảnh đại diện (Cover/Avatar) và Album Ảnh phụ (Secondary Images) cho sản phẩm (US08, US09).
     * <p>Cho phép người dùng quản lý độc lập ảnh bìa chính và các góc chụp chi tiết.</p>
     */
    @Transactional
    public void luuAnhChoSanPhamTachBiet(Long sanPhamId,
                                         MultipartFile fileAnhDaiDien,
                                         String urlAnhDaiDien,
                                         MultipartFile[] filesAnhPhu,
                                         String urlsAnhPhu) {
        if (sanPhamId == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống!");
        }

        boolean coFileDaiDien = fileAnhDaiDien != null && !fileAnhDaiDien.isEmpty();
        boolean coUrlDaiDien = urlAnhDaiDien != null && !urlAnhDaiDien.isBlank();
        boolean coAnhDaiDienMoi = coFileDaiDien || coUrlDaiDien;

        // 1. Xử lý Ảnh đại diện nếu người dùng cung cấp riêng
        if (coAnhDaiDienMoi) {
            // Đặt các ảnh đại diện cũ thành ảnh phụ
            List<HinhAnh> dsHienCo = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
            for (HinhAnh ha : dsHienCo) {
                if (Boolean.TRUE.equals(ha.getLaAnhDaiDien())) {
                    ha.setLaAnhDaiDien(false);
                }
            }
            hinhAnhRepository.saveAll(dsHienCo);

            if (coFileDaiDien) {
                MultipartFile validated = validateMotFileAnh(fileAnhDaiDien);
                if (validated != null) {
                    luuMotFileAnh(sanPhamId, validated, true, 1);
                }
            } else {
                luuMotUrlAnh(sanPhamId, urlAnhDaiDien.trim(), true, 1);
            }
        }

        // 2. Xử lý Album Ảnh phụ
        List<MultipartFile> filesPhuHopLe = locVaValidateDanhSachFileAnh(filesAnhPhu);
        boolean coUrlsPhu = urlsAnhPhu != null && !urlsAnhPhu.isBlank();

        // Kiểm tra xem hiện tại đã có ảnh đại diện chưa (nếu chưa có thì ảnh phụ đầu tiên sẽ làm ảnh đại diện)
        List<HinhAnh> dsSauDaiDien = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
        boolean daCoAnhDaiDien = dsSauDaiDien.stream().anyMatch(ha -> Boolean.TRUE.equals(ha.getLaAnhDaiDien()));
        int thuTuTiepTheo = dsSauDaiDien.size() + 1;

        if (!filesPhuHopLe.isEmpty()) {
            for (MultipartFile file : filesPhuHopLe) {
                boolean datLamDaiDien = !daCoAnhDaiDien;
                if (datLamDaiDien) {
                    daCoAnhDaiDien = true;
                }
                luuMotFileAnh(sanPhamId, file, datLamDaiDien, datLamDaiDien ? 1 : thuTuTiepTheo++);
            }
        }

        if (coUrlsPhu) {
            String[] urls = urlsAnhPhu.split("[,\\r\\n]+");
            for (String rawUrl : urls) {
                String cleanUrl = rawUrl.trim();
                if (!cleanUrl.isEmpty()) {
                    boolean datLamDaiDien = !daCoAnhDaiDien;
                    if (datLamDaiDien) {
                        daCoAnhDaiDien = true;
                    }
                    luuMotUrlAnh(sanPhamId, cleanUrl, datLamDaiDien, datLamDaiDien ? 1 : thuTuTiepTheo++);
                }
            }
        }

        hinhAnhRepository.flush();
    }

    /**
     * Cập nhật hoặc thay thế riêng Ảnh đại diện chính (Cover Image) cho sản phẩm.
     */
    @Transactional
    public HinhAnh capNhatAnhDaiDienSanPham(Long sanPhamId, MultipartFile fileAnhDaiDien, String urlAnhDaiDien) {
        if (sanPhamId == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống!");
        }
        boolean coFile = fileAnhDaiDien != null && !fileAnhDaiDien.isEmpty();
        boolean coUrl = urlAnhDaiDien != null && !urlAnhDaiDien.isBlank();
        if (!coFile && !coUrl) {
            throw new IllegalArgumentException("Vui lòng chọn file ảnh đại diện hoặc nhập đường dẫn URL ảnh!");
        }

        // Đổi các ảnh đại diện cũ thành ảnh phụ
        List<HinhAnh> dsHienCo = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
        for (HinhAnh ha : dsHienCo) {
            if (Boolean.TRUE.equals(ha.getLaAnhDaiDien())) {
                ha.setLaAnhDaiDien(false);
            }
        }
        hinhAnhRepository.saveAll(dsHienCo);

        HinhAnh anhMoi;
        if (coFile) {
            MultipartFile validated = validateMotFileAnh(fileAnhDaiDien);
            anhMoi = luuMotFileAnh(sanPhamId, validated, true, 1);
        } else {
            anhMoi = luuMotUrlAnh(sanPhamId, urlAnhDaiDien.trim(), true, 1);
        }
        hinhAnhRepository.flush();
        return anhMoi;
    }

    /**
     * Tải lên bổ sung các ảnh phụ cho sản phẩm mà không làm thay đổi ảnh đại diện hiện tại.
     */
    @Transactional
    public int themAnhPhuChoSanPham(Long sanPhamId, MultipartFile[] filesAnhPhu, String urlsAnhPhu) {
        if (sanPhamId == null) {
            throw new IllegalArgumentException("ID sản phẩm không được để trống!");
        }
        List<MultipartFile> filesHopLe = locVaValidateDanhSachFileAnh(filesAnhPhu);
        boolean coUrl = urlsAnhPhu != null && !urlsAnhPhu.isBlank();
        if (filesHopLe.isEmpty() && !coUrl) {
            return 0;
        }

        List<HinhAnh> dsHienCo = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
        boolean daCoDaiDien = dsHienCo.stream().anyMatch(ha -> Boolean.TRUE.equals(ha.getLaAnhDaiDien()));
        int thuTuTiepTheo = dsHienCo.size() + 1;
        int count = 0;

        for (MultipartFile file : filesHopLe) {
            boolean datLamDaiDien = !daCoDaiDien;
            if (datLamDaiDien) {
                daCoDaiDien = true;
            }
            luuMotFileAnh(sanPhamId, file, datLamDaiDien, datLamDaiDien ? 1 : thuTuTiepTheo++);
            count++;
        }

        if (coUrl) {
            String[] urls = urlsAnhPhu.split("[,\\r\\n]+");
            for (String rawUrl : urls) {
                String cleanUrl = rawUrl.trim();
                if (!cleanUrl.isEmpty()) {
                    boolean datLamDaiDien = !daCoDaiDien;
                    if (datLamDaiDien) {
                        daCoDaiDien = true;
                    }
                    luuMotUrlAnh(sanPhamId, cleanUrl, datLamDaiDien, datLamDaiDien ? 1 : thuTuTiepTheo++);
                    count++;
                }
            }
        }
        hinhAnhRepository.flush();
        return count;
    }

    /**
     * Lấy riêng Ảnh đại diện chính của sản phẩm.
     */
    @Transactional(readOnly = true)
    public HinhAnh layAnhDaiDienSanPham(Long sanPhamId) {
        if (sanPhamId == null) return null;
        List<HinhAnh> ds = layDanhSachHinhAnhSanPham(sanPhamId);
        return ds.stream()
                .filter(ha -> Boolean.TRUE.equals(ha.getLaAnhDaiDien()))
                .findFirst()
                .orElse(!ds.isEmpty() ? ds.get(0) : null);
    }

    /**
     * Lấy danh sách các Ảnh phụ của sản phẩm (loại trừ ảnh đại diện chính).
     */
    @Transactional(readOnly = true)
    public List<HinhAnh> layDanhSachAnhPhuSanPham(Long sanPhamId) {
        if (sanPhamId == null) return Collections.emptyList();
        List<HinhAnh> ds = layDanhSachHinhAnhSanPham(sanPhamId);
        HinhAnh daiDien = layAnhDaiDienSanPham(sanPhamId);
        if (daiDien == null) return Collections.emptyList();
        return ds.stream()
                .filter(ha -> !Objects.equals(ha.getId(), daiDien.getId()))
                .toList();
    }

    /**
     * Đặt một hình ảnh làm Ảnh bìa / Ảnh đại diện chính (Cover Image) cho sản phẩm (chuẩn sàn TMĐT).
     */
    @Transactional
    public HinhAnh datAnhDaiDienSanPham(Long sanPhamId, Long hinhAnhId) {
        if (sanPhamId == null || hinhAnhId == null) {
            throw new IllegalArgumentException("ID sản phẩm và ID hình ảnh không được để trống!");
        }
        HinhAnh anhChon = hinhAnhRepository.findById(hinhAnhId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hình ảnh với ID: " + hinhAnhId));

        if (!Objects.equals(anhChon.getMaSanPhamId(), sanPhamId)) {
            throw new IllegalArgumentException("Hình ảnh không thuộc về sản phẩm này!");
        }

        List<HinhAnh> dsAnhSp = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
        if (dsAnhSp.isEmpty()) {
            dsAnhSp = hinhAnhRepository.findByMaSanPhamIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
        }

        for (HinhAnh ha : dsAnhSp) {
            boolean laChon = ha.getId().equals(hinhAnhId);
            ha.setLaAnhDaiDien(laChon);
            if (laChon) {
                ha.setThuTu(1);
            }
        }
        hinhAnhRepository.saveAll(dsAnhSp);
        hinhAnhRepository.flush();
        return anhChon;
    }

    /**
     * Xóa một hình ảnh khỏi sản phẩm. Nếu là ảnh bìa đại diện thì tự động đôn ảnh phụ đầu tiên lên làm ảnh bìa.
     */
    @Transactional
    public void xoaHinhAnhSanPham(Long sanPhamId, Long hinhAnhId) {
        if (sanPhamId == null || hinhAnhId == null) {
            throw new IllegalArgumentException("ID sản phẩm và ID hình ảnh không được để trống!");
        }
        HinhAnh anh = hinhAnhRepository.findById(hinhAnhId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hình ảnh với ID: " + hinhAnhId));

        if (!Objects.equals(anh.getMaSanPhamId(), sanPhamId)) {
            throw new IllegalArgumentException("Hình ảnh không thuộc về sản phẩm này!");
        }

        boolean dangLaDaiDien = Boolean.TRUE.equals(anh.getLaAnhDaiDien());
        String duongDan = anh.getDuongDanAnh();

        hinhAnhRepository.delete(anh);
        hinhAnhRepository.flush();

        // Xóa file vật lý nếu nằm trong thư mục uploads nội bộ
        if (duongDan != null && duongDan.startsWith("/uploads/")) {
            try {
                String tenFile = duongDan.substring("/uploads/".length());
                Path pathFile = Paths.get("uploads").resolve(tenFile);
                Files.deleteIfExists(pathFile);
            } catch (Exception ignored) {
            }
        }

        // Tự động đôn ảnh tiếp theo lên làm ảnh bìa đại diện
        if (dangLaDaiDien) {
            List<HinhAnh> conLai = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
            if (conLai.isEmpty()) {
                conLai = hinhAnhRepository.findByMaSanPhamIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
            }
            if (!conLai.isEmpty()) {
                HinhAnh dauTien = conLai.get(0);
                dauTien.setLaAnhDaiDien(true);
                dauTien.setThuTu(1);
                hinhAnhRepository.save(dauTien);
                hinhAnhRepository.flush();
            }
        }
    }

    /**
     * Đổi thứ tự hiển thị của ảnh phụ (tiến/lùi) để sắp xếp bộ sưu tập ảnh theo ý muốn.
     */
    @Transactional
    public void doiThuTuHinhAnhSanPham(Long sanPhamId, Long hinhAnhId, String huongDi) {
        if (sanPhamId == null || hinhAnhId == null) {
            return;
        }
        List<HinhAnh> dsAnh = hinhAnhRepository.findByMaSanPhamIdAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
        if (dsAnh.size() < 2) {
            return;
        }

        int viTri = -1;
        for (int i = 0; i < dsAnh.size(); i++) {
            if (dsAnh.get(i).getId().equals(hinhAnhId)) {
                viTri = i;
                break;
            }
        }
        if (viTri == -1) {
            return;
        }

        int viTriHoanDoi = -1;
        if ("trai".equalsIgnoreCase(huongDi) || "len".equalsIgnoreCase(huongDi)) {
            if (viTri > 0) viTriHoanDoi = viTri - 1;
        } else if ("phai".equalsIgnoreCase(huongDi) || "xuong".equalsIgnoreCase(huongDi)) {
            if (viTri < dsAnh.size() - 1) viTriHoanDoi = viTri + 1;
        }

        if (viTriHoanDoi != -1) {
            HinhAnh a = dsAnh.get(viTri);
            HinhAnh b = dsAnh.get(viTriHoanDoi);

            Integer thuTuTemp = a.getThuTu();
            a.setThuTu(b.getThuTu() != null ? b.getThuTu() : viTri);
            b.setThuTu(thuTuTemp != null ? thuTuTemp : viTriHoanDoi);

            Boolean daiDienTemp = a.getLaAnhDaiDien();
            a.setLaAnhDaiDien(b.getLaAnhDaiDien());
            b.setLaAnhDaiDien(daiDienTemp);

            hinhAnhRepository.save(a);
            hinhAnhRepository.save(b);
            hinhAnhRepository.flush();
        }
    }

    private List<MultipartFile> locVaValidateDanhSachFileAnh(MultipartFile[] filesAnh) {
        List<MultipartFile> filesHopLe = new ArrayList<>();
        if (filesAnh == null || filesAnh.length == 0) {
            return filesHopLe;
        }

        long maxBytes = 10L * 1024 * 1024; // 10MB
        List<String> duoiHopLe = List.of(".jpg", ".jpeg", ".png", ".webp", ".gif", ".svg");

        for (MultipartFile file : filesAnh) {
            if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
                continue;
            }
            if (file.getSize() > maxBytes) {
                throw new IllegalArgumentException("File ảnh [" + file.getOriginalFilename() +
                        "] vượt quá kích thước cho phép 10MB!");
            }
            String tenGoc = file.getOriginalFilename().toLowerCase();
            boolean dungDinhDang = duoiHopLe.stream().anyMatch(tenGoc::endsWith);
            if (!dungDinhDang) {
                throw new IllegalArgumentException("File [" + file.getOriginalFilename() +
                        "] không đúng định dạng ảnh hợp lệ (chỉ chấp nhận: JPG, JPEG, PNG, WEBP, GIF, SVG)!");
            }
            filesHopLe.add(file);
        }
        return filesHopLe;
    }

    private MultipartFile validateMotFileAnh(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            return null;
        }
        long maxBytes = 10L * 1024 * 1024; // 10MB
        List<String> duoiHopLe = List.of(".jpg", ".jpeg", ".png", ".webp", ".gif", ".svg");
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("File ảnh [" + file.getOriginalFilename() +
                    "] vượt quá kích thước cho phép 10MB!");
        }
        String tenGoc = file.getOriginalFilename().toLowerCase();
        boolean dungDinhDang = duoiHopLe.stream().anyMatch(tenGoc::endsWith);
        if (!dungDinhDang) {
            throw new IllegalArgumentException("File [" + file.getOriginalFilename() +
                    "] không đúng định dạng ảnh hợp lệ (chỉ chấp nhận: JPG, JPEG, PNG, WEBP, GIF, SVG)!");
        }
        return file;
    }

    private HinhAnh luuMotFileAnh(Long sanPhamId, MultipartFile file, boolean laAnhDaiDien, int thuTu) {
        try {
            Path thuMucUpload = Paths.get("uploads");
            if (!Files.exists(thuMucUpload)) {
                Files.createDirectories(thuMucUpload);
            }
            String tenGoc = Objects.requireNonNull(file.getOriginalFilename());
            String duoiFile = tenGoc.substring(tenGoc.lastIndexOf('.')).toLowerCase();
            String prefix = laAnhDaiDien ? "thumb_" : "sp_";
            String tenFileAnToan = prefix + sanPhamId + "_" + System.currentTimeMillis() + "_" +
                    UUID.randomUUID().toString().substring(0, 6) + duoiFile;

            Files.copy(file.getInputStream(), thuMucUpload.resolve(tenFileAnToan), StandardCopyOption.REPLACE_EXISTING);

            HinhAnh hinhAnh = HinhAnh.builder()
                    .maSanPhamId(sanPhamId)
                    .maBienTheId(null)
                    .duongDanAnh("/uploads/" + tenFileAnToan)
                    .laAnhDaiDien(laAnhDaiDien)
                    .thuTu(thuTu)
                    .build();
            return hinhAnhRepository.save(hinhAnh);
        } catch (IOException e) {
            throw new IllegalStateException("Lỗi khi lưu file hình ảnh sản phẩm: " + e.getMessage());
        }
    }

    private HinhAnh luuMotUrlAnh(Long sanPhamId, String url, boolean laAnhDaiDien, int thuTu) {
        HinhAnh hinhAnh = HinhAnh.builder()
                .maSanPhamId(sanPhamId)
                .maBienTheId(null)
                .duongDanAnh(url.trim())
                .laAnhDaiDien(laAnhDaiDien)
                .thuTu(thuTu)
                .build();
        return hinhAnhRepository.save(hinhAnh);
    }
}
