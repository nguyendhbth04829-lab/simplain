package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Dịch vụ Quản lý Nhập hàng từ Nhà Cung Cấp.
 * Hỗ trợ quét mã vạch Barcode sản phẩm từ các nhà cung cấp lớn chính hãng.
 * Tự động cập nhật số lượng tồn kho của biến thể khi hoàn tất phiếu nhập.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NhapHangService {

    private final NhaCungCapRepository nhaCungCapRepository;
    private final PhieuNhapHangRepository phieuNhapHangRepository;
    private final ChiTietPhieuNhapHangRepository chiTietPhieuNhapHangRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;
    private final vn.fpoly.simplain.repository.MauSacRepository mauSacRepository;
    private final vn.fpoly.simplain.repository.KichThuocRepository kichThuocRepository;

    // =========================================================================
    // 1. QUẢN LÝ NHÀ CUNG CẤP HÀNG HÓA
    // =========================================================================

    @Transactional(readOnly = true)
    public List<NhaCungCap> layTatCaNhaCungCap() {
        return nhaCungCapRepository.findAll(Sort.by("id").descending());
    }

    @Transactional(readOnly = true)
    public List<NhaCungCap> layDanhSachNhaCungCapDangHoatDong() {
        return nhaCungCapRepository.findByKichHoatTrue(Sort.by("tenNhaCungCap").ascending());
    }

    @Transactional(readOnly = true)
    public Optional<NhaCungCap> layNhaCungCapTheoId(Long id) {
        return nhaCungCapRepository.findById(id);
    }

    /**
     * Chuyển đổi trạng thái Hợp tác <-> Ngưng hợp tác với Nhà cung cấp.
     */
    @Transactional
    public NhaCungCap doiTrangThaiNhaCungCap(Long id) {
        NhaCungCap ncc = nhaCungCapRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Nhà cung cấp có ID: " + id));
        boolean trangThaiMoi = !Boolean.TRUE.equals(ncc.getKichHoat());
        ncc.setKichHoat(trangThaiMoi);
        log.info("Đã chuyển đổi trạng thái nhà cung cấp ID {} [{}] sang: {}", id, ncc.getTenNhaCungCap(), trangThaiMoi ? "ĐANG HỢP TÁC" : "NGƯNG HỢP TÁC");
        return nhaCungCapRepository.save(ncc);
    }

    /**
     * Tìm kiếm và lọc nhà cung cấp theo từ khóa và trạng thái hợp tác.
     */
    @Transactional(readOnly = true)
    public List<NhaCungCap> timKiemNhaCungCap(String tuKhoa, String trangThai) {
        List<NhaCungCap> list;
        if ("DANG_HOP_TAC".equalsIgnoreCase(trangThai)) {
            list = nhaCungCapRepository.findByKichHoat(true, Sort.by("id").descending());
        } else if ("NGUNG_HOP_TAC".equalsIgnoreCase(trangThai)) {
            list = nhaCungCapRepository.findByKichHoat(false, Sort.by("id").descending());
        } else {
            list = nhaCungCapRepository.findAll(Sort.by("id").descending());
        }

        if (tuKhoa == null || tuKhoa.trim().isBlank()) {
            return list;
        }

        String kw = tuKhoa.trim().toLowerCase();
        return list.stream().filter(ncc ->
                (ncc.getMaNhaCungCap() != null && ncc.getMaNhaCungCap().toLowerCase().contains(kw)) ||
                (ncc.getTenNhaCungCap() != null && ncc.getTenNhaCungCap().toLowerCase().contains(kw)) ||
                (ncc.getSoDienThoai() != null && ncc.getSoDienThoai().toLowerCase().contains(kw)) ||
                (ncc.getEmail() != null && ncc.getEmail().toLowerCase().contains(kw)) ||
                (ncc.getDiaChi() != null && ncc.getDiaChi().toLowerCase().contains(kw))
        ).toList();
    }

    @Transactional(readOnly = true)
    public long demNhaCungCapTatCa() {
        return nhaCungCapRepository.count();
    }

    @Transactional(readOnly = true)
    public long demNhaCungCapDangHopTac() {
        return nhaCungCapRepository.countByKichHoat(true);
    }

    @Transactional(readOnly = true)
    public long demNhaCungCapNgungHopTac() {
        return nhaCungCapRepository.countByKichHoat(false);
    }

    @Transactional
    public NhaCungCap luuNhaCungCap(NhaCungCap ncc) {
        if (ncc.getId() == null) {
            if (ncc.getMaNhaCungCap() == null || ncc.getMaNhaCungCap().isBlank()) {
                ncc.setMaNhaCungCap("NCC-" + (System.currentTimeMillis() % 1000000));
            }
            if (nhaCungCapRepository.existsByMaNhaCungCap(ncc.getMaNhaCungCap())) {
                throw new IllegalArgumentException("Mã nhà cung cấp [" + ncc.getMaNhaCungCap() + "] đã tồn tại trên hệ thống!");
            }
            if (ncc.getKichHoat() == null) {
                ncc.setKichHoat(true);
            }
            if (ncc.getChinhHang() == null) {
                ncc.setChinhHang(true);
            }
        } else {
            if (ncc.getKichHoat() == null) {
                ncc.setKichHoat(false);
            }
            if (ncc.getChinhHang() == null) {
                ncc.setChinhHang(false);
            }
        }
        return nhaCungCapRepository.save(ncc);
    }

    /**
     * Khởi tạo sẵn các Nhà cung cấp lớn chính hãng uy tín nếu chưa có dữ liệu.
     */
    @Transactional
    public void khoiTaoNhaCungCapChinhHangMacDinh() {
        if (nhaCungCapRepository.count() == 0) {
            List<NhaCungCap> dsMacDinh = List.of(
                    NhaCungCap.builder()
                            .maNhaCungCap("NCC-VINATEX")
                            .tenNhaCungCap("Tập đoàn Dệt May Việt Nam (Vinatex Official)")
                            .soDienThoai("02438257700")
                            .email("contact@vinatex.com.vn")
                            .diaChi("25 Bà Triệu, Hàng Bài, Hoàn Kiếm, Hà Nội")
                            .maSoThue("0100100008")
                            .ghiChu("Nhà cung cấp lớn chính hãng vải Cotton, sợi Pima và dệt may xuất khẩu cao cấp.")
                            .chinhHang(true)
                            .kichHoat(true)
                            .build(),
                    NhaCungCap.builder()
                            .maNhaCungCap("NCC-VIETTIEN")
                            .tenNhaCungCap("Tổng Công ty Cổ phần May Việt Tiến")
                            .soDienThoai("02838640800")
                            .email("viettien@viettien.com.vn")
                            .diaChi("07 Lê Minh Xuân, Phường 7, Tân Bình, TP. Hồ Chí Minh")
                            .maSoThue("0300401524")
                            .ghiChu("Chuyên cung cấp sơ mi Oxford, quần âu công sở và áo khoác chính hãng.")
                            .chinhHang(true)
                            .kichHoat(true)
                            .build(),
                    NhaCungCap.builder()
                            .maNhaCungCap("NCC-MAY10")
                            .tenNhaCungCap("Tổng Công ty May 10 - CTCP (Garment 10)")
                            .soDienThoai("02438276923")
                            .email("cty@may10.com.vn")
                            .diaChi("Sài Đồng, Long Biên, Hà Nội")
                            .maSoThue("0100101308")
                            .ghiChu("Cung ứng hàng thời trang nam nữ cao cấp, tem nhãn chuẩn GS1 quốc tế.")
                            .chinhHang(true)
                            .kichHoat(true)
                            .build(),
                    NhaCungCap.builder()
                            .maNhaCungCap("NCC-YKK")
                            .tenNhaCungCap("YKK Fastening Vietnam (Khóa kéo chính hãng YKK)")
                            .soDienThoai("02513836952")
                            .email("sales@ykk.com.vn")
                            .diaChi("KCN Amata, Long Bình, Biên Hòa, Đồng Nai")
                            .maSoThue("3600262100")
                            .ghiChu("Tập đoàn khóa kéo số 1 thế giới YKK Nhật Bản, mã vạch Barcode dập nổi từng sản phẩm.")
                            .chinhHang(true)
                            .kichHoat(true)
                            .build(),
                    NhaCungCap.builder()
                            .maNhaCungCap("NCC-COTTONUSA")
                            .tenNhaCungCap("Cotton USA & Supima Hi-Tech Material Co.")
                            .soDienThoai("0908123456")
                            .email("supplier@cottonusa.vn")
                            .diaChi("KCN VSIP II, Bến Cát, Bình Dương")
                            .maSoThue("3702118899")
                            .ghiChu("Nhà phân phối sợi bông Cotton Mỹ 100%, vải cá sấu Pique cao cấp.")
                            .chinhHang(true)
                            .kichHoat(true)
                            .build()
            );
            nhaCungCapRepository.saveAll(dsMacDinh);
            log.info("Đã khởi tạo thành công 5 Nhà cung cấp lớn chính hãng có mã quét Barcode.");
        }
    }

    // =========================================================================
    // 2. TÌM KIẾM SẢN PHẨM / BIẾN THỂ QUA MÃ QUÉT BARCODE CỦA NHÀ CUNG CẤP
    // =========================================================================

    /**
     * Quét hoặc tìm kiếm biến thể sản phẩm bằng Mã vạch Barcode hoặc SKU.
     */
    @Transactional(readOnly = true)
    public Optional<BienTheSanPham> quetMaVachBienThe(String maQuet) {
        if (maQuet == null || maQuet.isBlank()) {
            return Optional.empty();
        }
        String q = maQuet.trim();
        List<BienTheSanPham> list = bienTheSanPhamRepository.timKiemTheoMaQuet(q);
        if (!list.isEmpty()) {
            return Optional.of(list.get(0));
        }
        return Optional.empty();
    }

    // =========================================================================
    // 3. QUẢN LÝ PHIẾU NHẬP HÀNG & TỒN KHO
    // =========================================================================

    @Transactional(readOnly = true)
    public Page<PhieuNhapHang> layDanhSachPhieuNhap(String tuKhoa, Long maNhaCungCapId, String trangThai,
                                                    LocalDateTime tuNgay, LocalDateTime denNgay, Pageable pageable) {
        Page<PhieuNhapHang> page = phieuNhapHangRepository.timKiemPhanTrang(
                tuKhoa, maNhaCungCapId, trangThai, tuNgay, denNgay, pageable);

        // Bổ sung thông tin hiển thị
        page.getContent().forEach(p -> {
            if (p.getMaNhaCungCapId() != null) {
                nhaCungCapRepository.findById(p.getMaNhaCungCapId()).ifPresent(ncc -> {
                    p.setTenNhaCungCap(ncc.getTenNhaCungCap());
                    p.setNhaCungCapChinhHang(ncc.getChinhHang());
                });
            }
        });
        return page;
    }

    @Transactional(readOnly = true)
    public Optional<PhieuNhapHang> layPhieuNhapTheoId(Long id) {
        Optional<PhieuNhapHang> opt = phieuNhapHangRepository.findById(id);
        if (opt.isPresent()) {
            PhieuNhapHang p = opt.get();
            if (p.getMaNhaCungCapId() != null) {
                nhaCungCapRepository.findById(p.getMaNhaCungCapId()).ifPresent(ncc -> {
                    p.setTenNhaCungCap(ncc.getTenNhaCungCap());
                    p.setNhaCungCapChinhHang(ncc.getChinhHang());
                });
            }
            List<ChiTietPhieuNhapHang> chiTietList = chiTietPhieuNhapHangRepository.findByMaPhieuNhapId(p.getId());
            for (ChiTietPhieuNhapHang ct : chiTietList) {
                bienTheSanPhamRepository.findById(ct.getMaBienTheId()).ifPresent(bt -> {
                    ct.setMaSku(bt.getMaSku());
                    ct.setTonKhoHienTai(bt.getSoLuong());
                    
                    String tenMau = bt.getTenMauSac();
                    if (tenMau == null && bt.getMaMauSacId() != null) {
                        tenMau = mauSacRepository.findById(bt.getMaMauSacId())
                                .map(vn.fpoly.simplain.entity.MauSac::getTenMau)
                                .orElse("Màu #" + bt.getMaMauSacId());
                    }
                    ct.setTenMauSac(tenMau);

                    String tenSize = bt.getMaKichThuoc();
                    if (tenSize == null && bt.getMaKichThuocId() != null) {
                        tenSize = kichThuocRepository.findById(bt.getMaKichThuocId())
                                .map(vn.fpoly.simplain.entity.KichThuoc::getMaKichThuoc)
                                .orElse("Size #" + bt.getMaKichThuocId());
                    }
                    ct.setMaKichThuoc(tenSize);

                    sanPhamRepository.findById(bt.getMaSanPhamId()).ifPresent(sp -> {
                        ct.setTenSanPham(sp.getTenSanPham());
                        ct.setAnhUrl(sp.getAnhDaiDienUrl());
                    });
                });
            }
            p.setDanhSachChiTiet(chiTietList);
        }
        return opt;
    }

    /**
     * Tạo mới Phiếu nhập hàng kèm danh sách chi tiết (quét mã vạch hoặc chọn SKU).
     */
    @Transactional
    public PhieuNhapHang taoPhieuNhap(PhieuNhapHang phieu, List<ChiTietPhieuNhapHang> chiTietList, boolean hoanTatNgay) {
        if (phieu.getMaNhaCungCapId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn Nhà cung cấp hàng hóa!");
        }

        NhaCungCap ncc = nhaCungCapRepository.findById(phieu.getMaNhaCungCapId())
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại trên hệ thống!"));
        if (!Boolean.TRUE.equals(ncc.getKichHoat())) {
            throw new IllegalStateException("Nhà cung cấp [" + ncc.getTenNhaCungCap() + "] đã NGƯNG HỢP TÁC, không thể tạo phiếu nhập hàng mới!");
        }

        if (chiTietList == null || chiTietList.isEmpty()) {
            throw new IllegalArgumentException("Phiếu nhập phải có ít nhất một mặt hàng sản phẩm!");
        }

        if (phieu.getMaPhieuNhap() == null || phieu.getMaPhieuNhap().isBlank()) {
            String timeCode = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
            phieu.setMaPhieuNhap("PN-" + timeCode);
        }

        if (phieu.getNgayNhap() == null) {
            phieu.setNgayNhap(LocalDateTime.now());
        }

        int tongSoLuong = 0;
        BigDecimal tongTien = BigDecimal.ZERO;

        for (ChiTietPhieuNhapHang ct : chiTietList) {
            if (ct.getSoLuongNhap() == null || ct.getSoLuongNhap() <= 0) {
                throw new IllegalArgumentException("Số lượng nhập của từng mặt hàng phải lớn hơn 0!");
            }
            if (ct.getGiaNhap() == null || ct.getGiaNhap().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Giá nhập không được âm!");
            }
            BigDecimal tt = ct.getGiaNhap().multiply(BigDecimal.valueOf(ct.getSoLuongNhap()));
            ct.setThanhTien(tt);

            tongSoLuong += ct.getSoLuongNhap();
            tongTien = tongTien.add(tt);
        }

        phieu.setTongSoLuong(tongSoLuong);
        phieu.setTongTien(tongTien);
        phieu.setTrangThai(hoanTatNgay ? "DA_NHAP" : "CHO_NHAP");

        PhieuNhapHang savedPhieu = phieuNhapHangRepository.save(phieu);

        for (ChiTietPhieuNhapHang ct : chiTietList) {
            ct.setMaPhieuNhapId(savedPhieu.getId());
            chiTietPhieuNhapHangRepository.save(ct);

            // Nếu người dùng chọn Hoàn tất nhập ngay -> Cộng tồn kho & tính lại giá vốn bình quân tức thì
            if (hoanTatNgay) {
                congTonKhoBienThe(ct.getMaBienTheId(), ct.getSoLuongNhap(), ct.getGiaNhap(), ct.getMaVachBarcode());
            }
        }

        log.info("Đã tạo phiếu nhập [{}]: {} sản phẩm, tổng tiền {} đ, trạng thái: {}",
                savedPhieu.getMaPhieuNhap(), tongSoLuong, tongTien, savedPhieu.getTrangThai());
        return savedPhieu;
    }

    /**
     * Xác nhận Hoàn tất nhập kho cho phiếu đang ở trạng thái CHO_NHAP.
     * Tự động cộng tồn kho cho từng biến thể SKU.
     */
    @Transactional
    public PhieuNhapHang hoanTatPhieuNhap(Long phieuId) {
        PhieuNhapHang phieu = phieuNhapHangRepository.findById(phieuId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu nhập có ID: " + phieuId));

        if ("DA_NHAP".equalsIgnoreCase(phieu.getTrangThai())) {
            throw new IllegalStateException("Phiếu nhập [" + phieu.getMaPhieuNhap() + "] đã hoàn tất trước đó!");
        }
        if ("DA_HUY".equalsIgnoreCase(phieu.getTrangThai())) {
            throw new IllegalStateException("Không thể hoàn tất phiếu nhập đã bị hủy!");
        }

        List<ChiTietPhieuNhapHang> chiTietList = chiTietPhieuNhapHangRepository.findByMaPhieuNhapId(phieuId);
        for (ChiTietPhieuNhapHang ct : chiTietList) {
            congTonKhoBienThe(ct.getMaBienTheId(), ct.getSoLuongNhap(), ct.getGiaNhap(), ct.getMaVachBarcode());
        }

        phieu.setTrangThai("DA_NHAP");
        return phieuNhapHangRepository.save(phieu);
    }

    /**
     * Hủy phiếu nhập hàng (chỉ hủy được khi chưa nhập kho).
     */
    @Transactional
    public PhieuNhapHang huyPhieuNhap(Long phieuId, String lyDoHuy) {
        PhieuNhapHang phieu = phieuNhapHangRepository.findById(phieuId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu nhập có ID: " + phieuId));

        if ("DA_NHAP".equalsIgnoreCase(phieu.getTrangThai())) {
            throw new IllegalStateException("Phiếu nhập đã hoàn tất và cộng kho, không thể hủy trực tiếp! Vui lòng tạo phiếu xuất điều chỉnh nếu cần.");
        }

        phieu.setTrangThai("DA_HUY");
        if (lyDoHuy != null && !lyDoHuy.isBlank()) {
            phieu.setGhiChu((phieu.getGhiChu() != null ? phieu.getGhiChu() + " | " : "") + "Lý do hủy: " + lyDoHuy.trim());
        }
        return phieuNhapHangRepository.save(phieu);
    }

    private void congTonKhoBienThe(Long bienTheId, int soLuongNhap, BigDecimal giaNhap, String maVachBarcode) {
        bienTheSanPhamRepository.findById(bienTheId).ifPresent(bt -> {
            int tonCu = bt.getSoLuong() != null ? bt.getSoLuong() : 0;
            BigDecimal giaGocCu = bt.getGiaGoc() != null ? bt.getGiaGoc() : BigDecimal.ZERO;

            // Tính giá vốn bình quân gia quyền di động (Moving Weighted Average Cost Method)
            // Công thức: Giá vốn mới = ((Tồn cũ * Giá vốn cũ) + (Số lượng nhập mới * Giá nhập mới)) / (Tồn cũ + Số lượng nhập mới)
            BigDecimal giaGocMoi;
            if (tonCu > 0 && giaGocCu.compareTo(BigDecimal.ZERO) > 0 && giaNhap != null && giaNhap.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal tongGiaTriCu = giaGocCu.multiply(BigDecimal.valueOf(tonCu));
                BigDecimal tongGiaTriNhap = giaNhap.multiply(BigDecimal.valueOf(soLuongNhap));
                BigDecimal tongSoLuong = BigDecimal.valueOf(tonCu + soLuongNhap);
                giaGocMoi = tongGiaTriCu.add(tongGiaTriNhap).divide(tongSoLuong, 0, RoundingMode.HALF_UP);
            } else if (giaNhap != null && giaNhap.compareTo(BigDecimal.ZERO) > 0) {
                giaGocMoi = giaNhap;
            } else {
                giaGocMoi = giaGocCu;
            }

            bt.setSoLuong(tonCu + soLuongNhap);
            bt.setGiaGoc(giaGocMoi);

            // Nếu có mã vạch chính hãng mới quét và biến thể chưa có barcode, tự động cập nhật
            if (maVachBarcode != null && !maVachBarcode.isBlank() && (bt.getMaVachBarcode() == null || bt.getMaVachBarcode().isBlank())) {
                bt.setMaVachBarcode(maVachBarcode.trim());
            }
            bienTheSanPhamRepository.save(bt);
            log.info("Nhập kho SKU [{}]: Tồn cũ = {}, Nhập thêm = {}, Tồn mới = {} | Giá vốn cũ = {} đ, Giá nhập mới = {} đ -> Giá vốn BQ mới = {} đ",
                    bt.getMaSku(), tonCu, soLuongNhap, bt.getSoLuong(), giaGocCu, giaNhap, giaGocMoi);
        });
    }

    // =========================================================================
    // 4. THỐNG KÊ TỔNG QUAN NHẬP HÀNG
    // =========================================================================

    @Transactional(readOnly = true)
    public Map<String, Object> layThongKeTongQuanNhapHang() {
        Map<String, Object> map = new HashMap<>();
        long tongPhieu = phieuNhapHangRepository.count();
        long phieuChoDuyet = phieuNhapHangRepository.countByTrangThai("CHO_NHAP");
        long phieuHoanTat = phieuNhapHangRepository.countByTrangThai("DA_NHAP");
        long phieuHuy = phieuNhapHangRepository.countByTrangThai("DA_HUY");
        BigDecimal tongTienNhap = phieuNhapHangRepository.tinhTongTienNhapKhoHoanTat();
        Long tongSoLuongNhap = phieuNhapHangRepository.tinhTongSoLuongNhapKhoHoanTat();

        map.put("tongPhieu", tongPhieu);
        map.put("tongPhieuNhap", tongPhieu);
        map.put("phieuChoDuyet", phieuChoDuyet);
        map.put("phieuHoanTat", phieuHoanTat);
        map.put("phieuHuy", phieuHuy);
        map.put("tongTienNhap", tongTienNhap != null ? tongTienNhap : BigDecimal.ZERO);
        map.put("tongSoLuongNhap", tongSoLuongNhap != null ? tongSoLuongNhap : 0L);
        map.put("tongNhaCungCap", nhaCungCapRepository.count());
        map.put("soSkuSapHetHang", bienTheSanPhamRepository.countBySoLuongLessThanEqual(10));
        return map;
    }
}
