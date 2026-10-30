package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.fpoly.simplain.dto.BienTheHangLoatItemRequest;
import vn.fpoly.simplain.dto.ThemNhanhBienTheRequest;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý nghiệp vụ và Validate chuyên nghiệp cho Quản lý Biến thể sản phẩm (SKU),
 * Quản lý Tồn kho, Giá bán/Giá gốc và Upload nhiều hình ảnh (US09).
 *
 * <p>Đáp ứng tiêu chí US09:
 * 1. Map Màu sắc (mau_sac) + Kích thước (kich_thuoc) + Sản phẩm (san_pham) tạo bien_the_san_pham.
 *    Chặn tạo trùng cặp (Sản phẩm + Màu sắc + Kích thước) và chặn trùng mã SKU / Barcode.
 * 2. Quản lý Giá bán (gia_ban), Giá gốc (gia_goc) và Số lượng tồn kho (so_luong), hỗ trợ điều chỉnh tồn kho nhanh.
 * 3. Upload nhiều hình ảnh (hinh_anh) cùng lúc cho biến thể, kiểm tra định dạng file ảnh an toàn, chọn ảnh đại diện.
 * 4. Bảo vệ toàn vẹn dữ liệu khi xóa: Chặn xóa SKU đã phát sinh trong đơn hàng, giỏ hàng, đổi trả hoặc Flash Sale.
 */
@Service
public class BienTheSanPhamService {

    private static final long DUNG_LUONG_ANH_TOI_DA = 5L * 1024 * 1024; // 5MB / ảnh
    private static final List<String> DUOI_ANH_HOP_LE = Arrays.asList(".jpg", ".jpeg", ".png", ".webp", ".gif", ".svg");

    private final BienTheSanPhamRepository bienTheRepository;
    private final HinhAnhRepository hinhAnhRepository;
    private final SanPhamRepository sanPhamRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BienTheSanPhamService(BienTheSanPhamRepository bienTheRepository,
                                 HinhAnhRepository hinhAnhRepository,
                                 SanPhamRepository sanPhamRepository,
                                 MauSacRepository mauSacRepository,
                                 KichThuocRepository kichThuocRepository,
                                 JdbcTemplate jdbcTemplate) {
        this.bienTheRepository = bienTheRepository;
        this.hinhAnhRepository = hinhAnhRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.mauSacRepository = mauSacRepository;
        this.kichThuocRepository = kichThuocRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Lấy danh sách biến thể SKU có phân trang, tìm kiếm theo từ khóa và lọc theo sản phẩm, màu sắc, kích thước, trạng thái.
     */
    @Transactional(readOnly = true)
    public Page<BienTheSanPham> layDanhSachPhanTrang(String tuKhoa,
                                                     Long maSanPhamId,
                                                     Long maMauSacId,
                                                     Long maKichThuocId,
                                                     Boolean kichHoat,
                                                     Pageable pageable) {
        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        Page<BienTheSanPham> pageData = bienTheRepository.timKiemPhanTrang(
                tuKhoaChuan, maSanPhamId, maMauSacId, maKichThuocId, kichHoat, pageable);

        napThongTinHienThiChoDanhSach(pageData.getContent());
        return pageData;
    }

    /**
     * Lấy chi tiết một biến thể SKU theo ID kèm danh sách toàn bộ hình ảnh.
     */
    @Transactional(readOnly = true)
    public BienTheSanPham layTheoId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID biến thể sản phẩm không được để trống!");
        }
        BienTheSanPham bt = bienTheRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy biến thể sản phẩm với ID: " + id));
        napThongTinHienThiChoDanhSach(Collections.singletonList(bt));
        return bt;
    }

    /**
     * Đếm tổng số biến thể SKU.
     */
    @Transactional(readOnly = true)
    public long demTongBienThe() {
        return bienTheRepository.count();
    }

    /**
     * Tính tổng số lượng tồn kho của toàn bộ SKU.
     */
    @Transactional(readOnly = true)
    public long tinhTongTonKho() {
        return bienTheRepository.tinhTongTonKho();
    }

    /**
     * Đếm số lượng SKU sắp hết hàng hoặc hết hàng (soLuong <= 5).
     */
    @Transactional(readOnly = true)
    public long demSkuSapHetHang() {
        return bienTheRepository.countBySoLuongLessThanEqual(5);
    }

    /**
     * Đếm tổng số hình ảnh sản phẩm & biến thể.
     */
    @Transactional(readOnly = true)
    public long demTongHinhAnh() {
        return hinhAnhRepository.count();
    }

    /**
     * Tạo mới biến thể sản phẩm (Map Sản phẩm + Màu sắc + Kích thước), quản lý giá/tồn kho và upload nhiều hình ảnh (US09).
     */
    @Transactional
    public BienTheSanPham them(BienTheSanPham bienThe, MultipartFile[] filesAnh, String duongDanAnhUrl) {
        chuanHoaVaValidateCoBan(bienThe);

        // 1. Kiểm tra Sản phẩm, Màu sắc, Kích thước có tồn tại không
        SanPham sp = sanPhamRepository.findById(bienThe.getMaSanPhamId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm gốc được chọn không tồn tại trên hệ thống!"));
        MauSac ms = mauSacRepository.findById(bienThe.getMaMauSacId())
                .orElseThrow(() -> new IllegalArgumentException("Màu sắc được chọn không tồn tại trên hệ thống!"));
        KichThuoc kt = kichThuocRepository.findById(bienThe.getMaKichThuocId())
                .orElseThrow(() -> new IllegalArgumentException("Kích thước (Size) được chọn không tồn tại trên hệ thống!"));

        // 2. Kiểm tra trùng cặp (Sản phẩm + Màu sắc + Kích thước)
        if (bienTheRepository.existsByMaSanPhamIdAndMaMauSacIdAndMaKichThuocId(
                bienThe.getMaSanPhamId(), bienThe.getMaMauSacId(), bienThe.getMaKichThuocId())) {
            throw new IllegalArgumentException(
                    "Biến thể của sản phẩm [" + sp.getTenSanPham() + "] với Màu [" + ms.getTenMau() +
                    "] và Size [" + kt.getMaKichThuoc() + "] đã tồn tại trên hệ thống!"
            );
        }

        // 3. Kiểm tra trùng mã SKU
        if (bienTheRepository.existsByMaSkuIgnoreCase(bienThe.getMaSku())) {
            throw new IllegalArgumentException("Mã SKU [" + bienThe.getMaSku() + "] đã tồn tại trên hệ thống!");
        }

        // 4. Kiểm tra trùng mã vạch Barcode
        if (bienTheRepository.existsByMaVachBarcodeIgnoreCase(bienThe.getMaVachBarcode())) {
            throw new IllegalArgumentException("Mã vạch Barcode [" + bienThe.getMaVachBarcode() + "] đã tồn tại trên hệ thống!");
        }

        // 5. Validate danh sách file ảnh tải lên TRƯỚC KHI lưu để đảm bảo tính nguyên tử (rollback nếu file lỗi)
        List<MultipartFile> danhSachFileHopLe = locVaValidateDanhSachFileAnh(filesAnh);

        BienTheSanPham saved = bienTheRepository.save(bienThe);

        // 6. Lưu nhiều hình ảnh gắn với biến thể và sản phẩm
        luuDanhSachHinhAnhSauValidate(saved.getMaSanPhamId(), saved.getId(), danhSachFileHopLe, duongDanAnhUrl);

        return layTheoId(saved.getId());
    }

    /**
     * Cập nhật thông tin biến thể SKU, Giá bán/Giá gốc, Tồn kho và tải thêm nhiều hình ảnh mới (US09).
     */
    @Transactional
    public BienTheSanPham sua(Long id, BienTheSanPham duLieuMoi, MultipartFile[] filesAnh, String duongDanAnhUrl) {
        BienTheSanPham hienTai = layTheoId(id);
        chuanHoaVaValidateCoBan(duLieuMoi);

        // 1. Kiểm tra Sản phẩm, Màu sắc, Kích thước tồn tại
        SanPham sp = sanPhamRepository.findById(duLieuMoi.getMaSanPhamId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm gốc được chọn không tồn tại trên hệ thống!"));
        MauSac ms = mauSacRepository.findById(duLieuMoi.getMaMauSacId())
                .orElseThrow(() -> new IllegalArgumentException("Màu sắc được chọn không tồn tại trên hệ thống!"));
        KichThuoc kt = kichThuocRepository.findById(duLieuMoi.getMaKichThuocId())
                .orElseThrow(() -> new IllegalArgumentException("Kích thước (Size) được chọn không tồn tại trên hệ thống!"));

        // 2. Kiểm tra trùng cặp (Sản phẩm + Màu sắc + Kích thước) loại trừ chính ID đang sửa
        if (bienTheRepository.existsByMaSanPhamIdAndMaMauSacIdAndMaKichThuocIdAndIdNot(
                duLieuMoi.getMaSanPhamId(), duLieuMoi.getMaMauSacId(), duLieuMoi.getMaKichThuocId(), id)) {
            throw new IllegalArgumentException(
                    "Biến thể của sản phẩm [" + sp.getTenSanPham() + "] với Màu [" + ms.getTenMau() +
                    "] và Size [" + kt.getMaKichThuoc() + "] đã tồn tại ở SKU khác!"
            );
        }

        // 3. Kiểm tra trùng mã SKU (loại trừ chính ID đang sửa)
        if (bienTheRepository.existsByMaSkuIgnoreCaseAndIdNot(duLieuMoi.getMaSku(), id)) {
            throw new IllegalArgumentException("Mã SKU [" + duLieuMoi.getMaSku() + "] đã được sử dụng ở biến thể khác!");
        }

        // 4. Kiểm tra trùng mã vạch Barcode (loại trừ chính ID đang sửa)
        if (bienTheRepository.existsByMaVachBarcodeIgnoreCaseAndIdNot(duLieuMoi.getMaVachBarcode(), id)) {
            throw new IllegalArgumentException("Mã vạch Barcode [" + duLieuMoi.getMaVachBarcode() + "] đã được sử dụng ở biến thể khác!");
        }

        // 5. Validate danh sách file ảnh mới nếu có
        List<MultipartFile> danhSachFileHopLe = locVaValidateDanhSachFileAnh(filesAnh);

        hienTai.setMaSanPhamId(duLieuMoi.getMaSanPhamId());
        hienTai.setMaMauSacId(duLieuMoi.getMaMauSacId());
        hienTai.setMaKichThuocId(duLieuMoi.getMaKichThuocId());
        hienTai.setMaSku(duLieuMoi.getMaSku());
        hienTai.setMaVachBarcode(duLieuMoi.getMaVachBarcode());
        hienTai.setSoLuong(duLieuMoi.getSoLuong());
        hienTai.setGiaBan(duLieuMoi.getGiaBan());
        hienTai.setGiaGoc(duLieuMoi.getGiaGoc());
        hienTai.setKichHoat(duLieuMoi.getKichHoat());

        BienTheSanPham updated = bienTheRepository.save(hienTai);

        // 6. Lưu thêm các hình ảnh mới (nếu người dùng chọn upload thêm)
        luuDanhSachHinhAnhSauValidate(updated.getMaSanPhamId(), updated.getId(), danhSachFileHopLe, duongDanAnhUrl);

        return layTheoId(updated.getId());
    }

    /**
     * Cập nhật nhanh Số lượng tồn kho & Giá bán trực tiếp từ bảng danh sách (US09 Tiêu chí 2).
     */
    @Transactional
    public BienTheSanPham capNhatNhanhTonKhoVaGia(Long id, Integer soLuongMoi, BigDecimal giaBanMoi, BigDecimal giaGocMoi) {
        BienTheSanPham bt = layTheoId(id);

        if (soLuongMoi == null || soLuongMoi < 0 || soLuongMoi > 999999) {
            throw new IllegalArgumentException("Số lượng tồn kho phải từ 0 đến 999.999!");
        }
        if (giaBanMoi == null || giaBanMoi.compareTo(BigDecimal.ZERO) < 0 || giaBanMoi.compareTo(new BigDecimal("999999999")) > 0) {
            throw new IllegalArgumentException("Giá bán phải từ 0 đến 999.999.999 VNĐ!");
        }
        if (giaGocMoi != null) {
            if (giaGocMoi.compareTo(BigDecimal.ZERO) < 0 || giaGocMoi.compareTo(new BigDecimal("999999999")) > 0) {
                throw new IllegalArgumentException("Giá gốc phải từ 0 đến 999.999.999 VNĐ!");
            }
            bt.setGiaGoc(giaGocMoi);
        }

        bt.setSoLuong(soLuongMoi);
        bt.setGiaBan(giaBanMoi);
        return bienTheRepository.save(bt);
    }

    /**
     * Upload bổ sung nhiều hình ảnh cho một biến thể SKU.
     */
    @Transactional
    public int uploadThemNhieuHinhAnh(Long bienTheId, MultipartFile[] filesAnh, String duongDanAnhUrl) {
        BienTheSanPham bt = layTheoId(bienTheId);
        List<MultipartFile> danhSachFileHopLe = locVaValidateDanhSachFileAnh(filesAnh);
        boolean coUrl = duongDanAnhUrl != null && !duongDanAnhUrl.isBlank();

        if (danhSachFileHopLe.isEmpty() && !coUrl) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất 1 file hình ảnh hoặc nhập đường dẫn ảnh để tải lên!");
        }

        return luuDanhSachHinhAnhSauValidate(bt.getMaSanPhamId(), bt.getId(), danhSachFileHopLe, duongDanAnhUrl);
    }

    /**
     * Đặt một hình ảnh làm Ảnh đại diện chính cho biến thể SKU.
     */
    @Transactional
    public HinhAnh chonAnhDaiDien(Long hinhAnhId) {
        HinhAnh anhChon = hinhAnhRepository.findById(hinhAnhId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hình ảnh với ID: " + hinhAnhId));

        if (anhChon.getMaBienTheId() != null) {
            List<HinhAnh> danhSachCungBienThe = hinhAnhRepository
                    .findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(anhChon.getMaBienTheId());
            for (HinhAnh ha : danhSachCungBienThe) {
                ha.setLaAnhDaiDien(ha.getId().equals(hinhAnhId));
            }
            hinhAnhRepository.saveAll(danhSachCungBienThe);
        } else {
            anhChon.setLaAnhDaiDien(true);
            hinhAnhRepository.save(anhChon);
        }
        return anhChon;
    }

    /**
     * Xóa một hình ảnh của biến thể SKU.
     */
    @Transactional
    public Long xoaHinhAnh(Long hinhAnhId) {
        HinhAnh anh = hinhAnhRepository.findById(hinhAnhId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hình ảnh với ID: " + hinhAnhId));
        Long maBienTheId = anh.getMaBienTheId();
        boolean dangLaDaiDien = Boolean.TRUE.equals(anh.getLaAnhDaiDien());

        hinhAnhRepository.delete(anh);
        hinhAnhRepository.flush();

        // Nếu vừa xóa ảnh đại diện thì tự động đôn ảnh đầu tiên còn lại lên làm ảnh đại diện
        if (dangLaDaiDien && maBienTheId != null) {
            List<HinhAnh> conLai = hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(maBienTheId);
            if (!conLai.isEmpty()) {
                HinhAnh dauTien = conLai.get(0);
                dauTien.setLaAnhDaiDien(true);
                hinhAnhRepository.save(dauTien);
            }
        }
        return maBienTheId;
    }

    /**
     * Đổi nhanh trạng thái Kích hoạt (Đang bán / Tạm ngừng) của biến thể SKU.
     */
    @Transactional
    public BienTheSanPham doiTrangThai(Long id) {
        BienTheSanPham bt = layTheoId(id);
        boolean trangThaiMoi = (bt.getKichHoat() == null || !bt.getKichHoat());
        bt.setKichHoat(trangThaiMoi);
        return bienTheRepository.save(bt);
    }

    /**
     * Xóa biến thể SKU (Chặn xóa nếu đã phát sinh đơn hàng, giỏ hàng, đổi trả hoặc Flash Sale).
     */
    @Transactional
    public void xoa(Long id) {
        BienTheSanPham bt = layTheoId(id);

        long soChiTietDonHang = demBanGhiThamChieu("chi_tiet_don_hang", "ma_bien_the_id", id);
        if (soChiTietDonHang > 0) {
            throw new IllegalStateException(
                    "Không thể xóa biến thể SKU [" + bt.getMaSku() + "] vì đã phát sinh trong " + soChiTietDonHang + " chi tiết đơn hàng!"
            );
        }

        long soGioHang = demBanGhiThamChieu("chi_tiet_gio_hang", "ma_bien_the_id", id);
        if (soGioHang > 0) {
            throw new IllegalStateException(
                    "Không thể xóa biến thể SKU [" + bt.getMaSku() + "] vì đang có khách hàng lưu trong giỏ hàng!"
            );
        }

        long soDoiTra = demBanGhiThamChieu("chi_tiet_san_pham_doi_tra", "ma_bien_the_moi_id", id);
        if (soDoiTra > 0) {
            throw new IllegalStateException(
                    "Không thể xóa biến thể SKU [" + bt.getMaSku() + "] vì đang được tham chiếu trong phiếu đổi trả!"
            );
        }

        long soFlashSale = demBanGhiThamChieu("chi_tiet_flash_sale", "ma_bien_the_id", id);
        if (soFlashSale > 0) {
            throw new IllegalStateException(
                    "Không thể xóa biến thể SKU [" + bt.getMaSku() + "] vì đang nằm trong chương trình Flash Sale!"
            );
        }

        try {
            // Xóa các hình ảnh gắn riêng với biến thể này trước khi xóa biến thể
            hinhAnhRepository.deleteByMaBienTheId(id);
            hinhAnhRepository.flush();

            bienTheRepository.delete(bt);
            bienTheRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "Không thể xóa biến thể SKU [" + bt.getMaSku() + "] do đang được tham chiếu bởi dữ liệu giao dịch trên hệ thống!"
            );
        }
    }

    private void napThongTinHienThiChoDanhSach(List<BienTheSanPham> danhSach) {
        if (danhSach == null || danhSach.isEmpty()) {
            return;
        }

        Map<Long, SanPham> mapSanPham = sanPhamRepository.findAll().stream()
                .collect(Collectors.toMap(SanPham::getId, sp -> sp, (a, b) -> a));
        Map<Long, MauSac> mapMauSac = mauSacRepository.findAll().stream()
                .collect(Collectors.toMap(MauSac::getId, ms -> ms, (a, b) -> a));
        Map<Long, KichThuoc> mapKichThuoc = kichThuocRepository.findAll().stream()
                .collect(Collectors.toMap(KichThuoc::getId, kt -> kt, (a, b) -> a));

        for (BienTheSanPham bt : danhSach) {
            SanPham sp = mapSanPham.get(bt.getMaSanPhamId());
            if (sp != null) {
                bt.setMaSanPham(sp.getMaSanPham());
                bt.setTenSanPham(sp.getTenSanPham());
                bt.setDuongDanSlug(sp.getDuongDanSlug());
            } else {
                bt.setTenSanPham("Sản phẩm #" + bt.getMaSanPhamId());
            }

            MauSac ms = mapMauSac.get(bt.getMaMauSacId());
            if (ms != null) {
                bt.setTenMauSac(ms.getTenMau());
                bt.setMaMauHex(ms.getMaMau());
            } else {
                bt.setTenMauSac("Màu #" + bt.getMaMauSacId());
                bt.setMaMauHex("#000000");
            }

            KichThuoc kt = mapKichThuoc.get(bt.getMaKichThuocId());
            if (kt != null) {
                bt.setMaKichThuoc(kt.getMaKichThuoc());
                bt.setTenKichThuoc(kt.getTenKichThuoc());
            } else {
                bt.setMaKichThuoc("SIZE#" + bt.getMaKichThuocId());
            }

            List<HinhAnh> dsAnh = hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(bt.getId());
            bt.setDanhSachHinhAnh(dsAnh);
            if (!dsAnh.isEmpty()) {
                bt.setAnhDaiDienUrl(dsAnh.get(0).getDuongDanAnh());
            } else {
                bt.setAnhDaiDienUrl(null);
            }
        }
    }

    private void chuanHoaVaValidateCoBan(BienTheSanPham bt) {
        if (bt == null) {
            throw new IllegalArgumentException("Dữ liệu biến thể sản phẩm không được để trống!");
        }

        if (bt.getMaSanPhamId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn sản phẩm gốc!");
        }
        if (bt.getMaMauSacId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn màu sắc cho biến thể!");
        }
        if (bt.getMaKichThuocId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn kích thước (Size) cho biến thể!");
        }

        String sku = bt.getMaSku() != null ? bt.getMaSku().trim().toUpperCase() : "";
        if (sku.isEmpty()) {
            // Tự động sinh mã SKU chuẩn theo [MÃ_SP]-[MÀU]-[SIZE]
            sku = sinhMaSkuTuDong(bt.getMaSanPhamId(), bt.getMaMauSacId(), bt.getMaKichThuocId());
        }
        if (sku.length() < 3 || sku.length() > 100) {
            throw new IllegalArgumentException("Mã SKU phải từ 3 đến 100 ký tự!");
        }
        if (!sku.matches("^[A-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Mã SKU chỉ gồm chữ cái không dấu, chữ số, dấu gạch ngang (-) hoặc gạch dưới (_)!");
        }
        bt.setMaSku(sku);

        String barcode = bt.getMaVachBarcode() != null ? bt.getMaVachBarcode().trim().toUpperCase() : "";
        if (barcode.isEmpty()) {
            // Tự động sinh mã Barcode duy nhất dựa theo SKU để tránh lỗi UNIQUE NULL trên SQL Server
            barcode = sinhMaVachBarcodeTuDong(sku);
        } else {
            if (barcode.length() > 100) {
                throw new IllegalArgumentException("Mã vạch Barcode không được vượt quá 100 ký tự!");
            }
            if (!barcode.matches("^[A-Z0-9_-]+$")) {
                throw new IllegalArgumentException("Mã vạch Barcode chỉ gồm chữ cái, chữ số, dấu gạch ngang (-) hoặc gạch dưới (_)!");
            }
        }
        bt.setMaVachBarcode(barcode);

        if (bt.getSoLuong() == null) {
            throw new IllegalArgumentException("Số lượng tồn kho không được để trống!");
        }
        if (bt.getSoLuong() < 0 || bt.getSoLuong() > 999999) {
            throw new IllegalArgumentException("Số lượng tồn kho phải từ 0 đến 999.999!");
        }

        if (bt.getGiaBan() == null) {
            throw new IllegalArgumentException("Giá bán không được để trống!");
        }
        if (bt.getGiaBan().compareTo(BigDecimal.ZERO) < 0 || bt.getGiaBan().compareTo(new BigDecimal("999999999")) > 0) {
            throw new IllegalArgumentException("Giá bán phải từ 0 đến 999.999.999 VNĐ!");
        }

        if (bt.getGiaGoc() == null) {
            bt.setGiaGoc(BigDecimal.ZERO);
        } else if (bt.getGiaGoc().compareTo(BigDecimal.ZERO) < 0 || bt.getGiaGoc().compareTo(new BigDecimal("999999999")) > 0) {
            throw new IllegalArgumentException("Giá gốc phải từ 0 đến 999.999.999 VNĐ!");
        }

        if (bt.getKichHoat() == null) {
            bt.setKichHoat(true);
        }
    }

    private List<MultipartFile> locVaValidateDanhSachFileAnh(MultipartFile[] filesAnh) {
        List<MultipartFile> hopLe = new ArrayList<>();
        if (filesAnh == null || filesAnh.length == 0) {
            return hopLe;
        }

        for (MultipartFile file : filesAnh) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            if (file.getSize() > DUNG_LUONG_ANH_TOI_DA) {
                throw new IllegalArgumentException("Kích thước mỗi file ảnh không được vượt quá 5MB!");
            }
            String tenGoc = file.getOriginalFilename();
            if (tenGoc == null || tenGoc.isBlank() || !tenGoc.contains(".")) {
                throw new IllegalArgumentException("Tên file ảnh tải lên không hợp lệ!");
            }
            String duoiFile = tenGoc.substring(tenGoc.lastIndexOf('.')).toLowerCase();
            if (!DUOI_ANH_HOP_LE.contains(duoiFile)) {
                throw new IllegalArgumentException(
                        "File [" + tenGoc + "] không hợp lệ! Chỉ chấp nhận định dạng hình ảnh (JPG, PNG, WEBP, GIF, SVG)!"
                );
            }
            String contentType = file.getContentType();
            if (contentType != null && !contentType.isBlank() && !contentType.toLowerCase().startsWith("image/")) {
                throw new IllegalArgumentException(
                        "Nội dung file [" + tenGoc + "] không phải là hình ảnh hợp lệ!"
                );
            }
            hopLe.add(file);
        }
        return hopLe;
    }

    private int luuDanhSachHinhAnhSauValidate(Long maSanPhamId,
                                              Long maBienTheId,
                                              List<MultipartFile> danhSachFileHopLe,
                                              String duongDanAnhUrl) {
        List<HinhAnh> hienCo = hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(maBienTheId);
        boolean daCoAnhDaiDien = hienCo.stream().anyMatch(ha -> Boolean.TRUE.equals(ha.getLaAnhDaiDien()));
        int thuTuTiepTheo = hienCo.size() + 1;
        int soAnhDaLuu = 0;

        // 1. Lưu các file ảnh upload từ máy tính
        if (danhSachFileHopLe != null && !danhSachFileHopLe.isEmpty()) {
            try {
                Path thuMucUpload = Paths.get("uploads");
                if (!Files.exists(thuMucUpload)) {
                    Files.createDirectories(thuMucUpload);
                }

                for (MultipartFile file : danhSachFileHopLe) {
                    String tenGoc = Objects.requireNonNull(file.getOriginalFilename());
                    String duoiFile = tenGoc.substring(tenGoc.lastIndexOf('.')).toLowerCase();
                    String tenFileAnToan = "sku_" + System.currentTimeMillis() + "_" +
                            UUID.randomUUID().toString().substring(0, 6) + duoiFile;

                    Files.copy(file.getInputStream(), thuMucUpload.resolve(tenFileAnToan), StandardCopyOption.REPLACE_EXISTING);

                    boolean datLamDaiDien = !daCoAnhDaiDien;
                    if (datLamDaiDien) {
                        daCoAnhDaiDien = true;
                    }

                    HinhAnh hinhAnh = HinhAnh.builder()
                            .maSanPhamId(maSanPhamId)
                            .maBienTheId(maBienTheId)
                            .duongDanAnh("/uploads/" + tenFileAnToan)
                            .laAnhDaiDien(datLamDaiDien)
                            .thuTu(thuTuTiepTheo++)
                            .build();
                    hinhAnhRepository.save(hinhAnh);
                    soAnhDaLuu++;
                }
            } catch (IOException e) {
                throw new IllegalStateException("Lỗi khi lưu file hình ảnh biến thể lên máy chủ: " + e.getMessage());
            }
        }

        // 2. Lưu thêm đường dẫn ảnh URL (hỗ trợ nhập 1 hoặc nhiều URL phân cách bằng dấu phẩy / xuống dòng)
        if (duongDanAnhUrl != null && !duongDanAnhUrl.isBlank()) {
            String[] urls = duongDanAnhUrl.split("[,\\r\\n]+");
            for (String rawUrl : urls) {
                String cleanUrl = rawUrl.trim();
                if (!cleanUrl.isEmpty()) {
                    boolean datLamDaiDien = !daCoAnhDaiDien;
                    if (datLamDaiDien) {
                        daCoAnhDaiDien = true;
                    }
                    HinhAnh hinhAnh = HinhAnh.builder()
                            .maSanPhamId(maSanPhamId)
                            .maBienTheId(maBienTheId)
                            .duongDanAnh(cleanUrl)
                            .laAnhDaiDien(datLamDaiDien)
                            .thuTu(thuTuTiepTheo++)
                            .build();
                    hinhAnhRepository.save(hinhAnh);
                    soAnhDaLuu++;
                }
            }
        }

        return soAnhDaLuu;
    }

    private long demBanGhiThamChieu(String tenBang, String tenCot, Long bienTheId) {
        try {
            Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [" + tenBang + "] WHERE [" + tenCot + "] = ?",
                    Long.class,
                    bienTheId
            );
            return count != null ? count : 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Tự động sinh mã SKU theo format chuẩn: [MÃ_SP]-[MÀU]-[SIZE] (ví dụ: SP01-DEN-L).
     * Tự động kiểm tra và thêm hậu tố số tăng dần nếu đã tồn tại SKU trùng lặp.
     */
    public String sinhMaSkuTuDong(Long maSanPhamId, Long maMauSacId, Long maKichThuocId) {
        SanPham sp = (maSanPhamId != null) ? sanPhamRepository.findById(maSanPhamId).orElse(null) : null;
        MauSac ms = (maMauSacId != null) ? mauSacRepository.findById(maMauSacId).orElse(null) : null;
        KichThuoc kt = (maKichThuocId != null) ? kichThuocRepository.findById(maKichThuocId).orElse(null) : null;

        String maSp = (sp != null && sp.getMaSanPham() != null && !sp.getMaSanPham().isBlank())
                ? sp.getMaSanPham().trim().toUpperCase()
                : (sp != null ? "SP" + sp.getId() : "SP");

        String maMau = "COLOR";
        if (ms != null) {
            String tenMau = ms.getTenMau() != null ? ms.getTenMau() : "";
            maMau = boDauTiengVietVaKyTuDacBiet(tenMau);
            if (maMau.isBlank()) maMau = "MAU" + ms.getId();
        }

        String maSize = "SIZE";
        if (kt != null) {
            String sizeStr = kt.getMaKichThuoc() != null ? kt.getMaKichThuoc() : kt.getTenKichThuoc();
            maSize = boDauTiengVietVaKyTuDacBiet(sizeStr);
            if (maSize.isBlank()) maSize = "SZ" + kt.getId();
        }

        String baseSku = maSp + "-" + maMau + "-" + maSize;
        String candidateSku = baseSku;
        int count = 1;
        while (bienTheRepository.existsByMaSkuIgnoreCase(candidateSku)) {
            candidateSku = baseSku + "_" + count;
            count++;
        }
        return candidateSku;
    }

    /**
     * Tự động sinh mã vạch Barcode chuẩn duy nhất từ SKU.
     */
    public String sinhMaVachBarcodeTuDong(String sku) {
        if (sku == null || sku.isBlank()) {
            sku = "SKU" + System.currentTimeMillis();
        }
        String baseBarcode = "BC-" + sku.trim().toUpperCase();
        String candidateBarcode = baseBarcode;
        int count = 1;
        while (bienTheRepository.existsByMaVachBarcodeIgnoreCase(candidateBarcode)) {
            candidateBarcode = baseBarcode + "_" + count;
            count++;
        }
        return candidateBarcode;
    }

    /**
     * Bỏ dấu tiếng Việt và loại bỏ ký tự đặc biệt để tạo SKU/Barcode an toàn.
     */
    public String boDauTiengVietVaKyTuDacBiet(String text) {
        if (text == null) return "";
        return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace("đ", "D").replace("Đ", "D")
                .toUpperCase()
                .replaceAll("[^A-Z0-9]", "");
    }

    /**
     * Thêm nhanh nhiều biến thể SKU cùng một lúc - SIMPLAIN Matrix (Ma trận phân loại hàng).
     * Hỗ trợ tự sinh SKU, Barcode, cập nhật tồn kho nếu đã tồn tại và lưu hàng loạt an toàn trong 1 Transaction.
     */
    @Transactional
    public List<BienTheSanPham> themHangLoat(ThemNhanhBienTheRequest request) {
        if (request == null || request.getDanhSachBienThe() == null || request.getDanhSachBienThe().isEmpty()) {
            throw new IllegalArgumentException("Danh sách biến thể phân loại không được để trống!");
        }

        List<BienTheSanPham> ketQua = new ArrayList<>();

        for (BienTheHangLoatItemRequest item : request.getDanhSachBienThe()) {
            if (item.getMaMauSacId() == null || item.getMaKichThuocId() == null) {
                continue;
            }

            Long targetSpId = item.getMaSanPhamId() != null ? item.getMaSanPhamId() : request.getMaSanPhamId();
            if (targetSpId == null) {
                throw new IllegalArgumentException("Vui lòng chọn sản phẩm gốc cho từng biến thể!");
            }

            SanPham sp = sanPhamRepository.findById(targetSpId)
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm gốc không tồn tại trên hệ thống (ID: " + targetSpId + ")!"));

            // Kiểm tra xem cặp (Sản phẩm + Màu + Size) đã có trên hệ thống chưa
            Optional<BienTheSanPham> btOpt = bienTheRepository.findAll().stream()
                    .filter(b -> Objects.equals(b.getMaSanPhamId(), targetSpId)
                            && Objects.equals(b.getMaMauSacId(), item.getMaMauSacId())
                            && Objects.equals(b.getMaKichThuocId(), item.getMaKichThuocId()))
                    .findFirst();

            if (btOpt.isPresent()) {
                // Đã tồn tại -> Cập nhật giá bán, giá gốc và cộng dồn số lượng tồn kho
                BienTheSanPham btCu = btOpt.get();
                if (item.getGiaBan() != null && item.getGiaBan().compareTo(BigDecimal.ZERO) >= 0) {
                    btCu.setGiaBan(item.getGiaBan());
                }
                if (item.getGiaGoc() != null && item.getGiaGoc().compareTo(BigDecimal.ZERO) >= 0) {
                    btCu.setGiaGoc(item.getGiaGoc());
                }
                if (item.getSoLuong() != null && item.getSoLuong() >= 0) {
                    btCu.setSoLuong((btCu.getSoLuong() != null ? btCu.getSoLuong() : 0) + item.getSoLuong());
                }
                btCu.setKichHoat(item.getKichHoat() != null ? item.getKichHoat() : true);
                ketQua.add(bienTheRepository.save(btCu));
            } else {
                // Tạo mới biến thể
                String sku = item.getMaSku();
                if (sku == null || sku.isBlank()) {
                    sku = sinhMaSkuTuDong(targetSpId, item.getMaMauSacId(), item.getMaKichThuocId());
                } else {
                    sku = sku.trim().toUpperCase();
                    if (bienTheRepository.existsByMaSkuIgnoreCase(sku)) {
                        sku = sinhMaSkuTuDong(targetSpId, item.getMaMauSacId(), item.getMaKichThuocId());
                    }
                }

                String barcode = item.getMaVachBarcode();
                if (barcode == null || barcode.isBlank()) {
                    barcode = sinhMaVachBarcodeTuDong(sku);
                } else {
                    barcode = barcode.trim().toUpperCase();
                    if (bienTheRepository.existsByMaVachBarcodeIgnoreCase(barcode)) {
                        barcode = sinhMaVachBarcodeTuDong(sku);
                    }
                }

                BigDecimal giaBan = item.getGiaBan() != null ? item.getGiaBan() : (sp.getGiaCoBan() != null ? sp.getGiaCoBan() : BigDecimal.ZERO);
                BigDecimal giaGoc = item.getGiaGoc() != null ? item.getGiaGoc() : giaBan;
                Integer soLuong = item.getSoLuong() != null ? item.getSoLuong() : 0;

                BienTheSanPham btMoi = BienTheSanPham.builder()
                        .maSanPhamId(targetSpId)
                        .maMauSacId(item.getMaMauSacId())
                        .maKichThuocId(item.getMaKichThuocId())
                        .maSku(sku)
                        .maVachBarcode(barcode)
                        .giaBan(giaBan)
                        .giaGoc(giaGoc)
                        .soLuong(soLuong)
                        .kichHoat(item.getKichHoat() != null ? item.getKichHoat() : true)
                        .build();

                ketQua.add(bienTheRepository.saveAndFlush(btMoi));
            }
        }

        return ketQua;
    }
}
