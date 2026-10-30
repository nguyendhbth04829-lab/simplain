package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.fpoly.simplain.dto.*;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service xử lý nghiệp vụ Quản lý Đổi/Trả hàng (RMA) Online (US24 - Mod 5 - Hưng).
 * - Tiêu chí 1: Form tick chọn sản phẩm lỗi trong đơn cũ.
 * - Tiêu chí 2: Chọn SKU mới muốn đổi hoặc Nhập STK nếu muốn hoàn tiền.
 * - Form Validation: Kiểm tra quyền sở hữu (IDOR), trạng thái đơn HOAN_THANH, thời hạn đổi trả,
 *   tồn kho SKU mới, tính toán tiền hoàn và chặn trùng lặp yêu cầu.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DoiTraService {

    private final YeuCauDoiTraRepository yeuCauDoiTraRepository;
    private final ChiTietSanPhamDoiTraRepository chiTietSanPhamDoiTraRepository;
    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final HinhAnhRepository hinhAnhRepository;
    private final LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;

    private static final List<String> TRANG_THAI_DANG_XU_LY = List.of("CHO_XU_LY", "DA_TIEP_NHAN", "DANG_XU_LY");

    /**
     * Lấy dữ liệu đơn hàng và danh sách sản phẩm để render form tạo Yêu cầu Đổi/Trả (US24 - Tiêu chí 1 & 2).
     */
    @Transactional(readOnly = true)
    public DonHangDoiTraInfoDto layThongTinDonHangDeDoiTra(String maDonHang, Long khachHangId, String soDienThoai) {
        DonHang dh = kiemTraVaLayDonHangCuaKhachHang(maDonHang, khachHangId, soDienThoai);

        // Kiểm tra trạng thái đơn: chỉ cho phép đơn đã HOAN_THANH
        boolean daHoanThanh = dh.getTrangThaiDonHang() != null &&
                ("HOAN_THANH".equalsIgnoreCase(dh.getTrangThaiDonHang().getMaTrangThai()) || dh.getTrangThaiDonHang().getId() == 4);

        if (!daHoanThanh) {
            throw new IllegalStateException("Đơn hàng #" + maDonHang + " chưa hoàn thành (giao thành công). Bạn chỉ có thể tạo yêu cầu Đổi/Trả đối với đơn đã nhận hàng.");
        }

        // Kiểm tra xem đơn đã có yêu cầu RMA đang xử lý chưa
        boolean dangCoYeuCau = yeuCauDoiTraRepository.existsByMaDonHangIdAndTrangThaiIn(dh.getId(), TRANG_THAI_DANG_XU_LY);
        if (dangCoYeuCau) {
            throw new IllegalStateException("Đơn hàng #" + maDonHang + " hiện đang có một yêu cầu Đổi/Trả đang được xử lý. Bạn không thể tạo thêm yêu cầu mới lúc này.");
        }

        List<ChiTietDonHang> danhSachChiTiet = chiTietDonHangRepository.findByMaDonHangId(dh.getId());
        List<ItemDoiTraFormDto> items = new ArrayList<>();

        for (ChiTietDonHang ct : danhSachChiTiet) {
            BienTheSanPham bt = ct.getBienThe();
            if (bt == null && ct.getMaBienTheId() != null) {
                bt = bienTheSanPhamRepository.findById(ct.getMaBienTheId()).orElse(null);
            }

            Long spId = bt != null ? bt.getMaSanPhamId() : null;
            String tenMau = bt != null ? layTenMau(bt.getMaMauSacId()) : "";
            String tenKichThuoc = bt != null ? layTenKichThuoc(bt.getMaKichThuocId()) : "";
            String imgUrl = layHinhAnhDaiDien(ct.getMaBienTheId(), spId);

            // Tìm danh sách các biến thể khả dụng của cùng sản phẩm để khách chọn đổi (soLuong > 0 và kichHoat == true)
            List<BienTheDoiTraDto> dsBienTheKhaDung = new ArrayList<>();
            if (spId != null) {
                List<BienTheSanPham> tatCaBienThe = bienTheSanPhamRepository.findByMaSanPhamIdOrderByIdDesc(spId);
                for (BienTheSanPham b : tatCaBienThe) {
                    if (Boolean.TRUE.equals(b.getKichHoat()) && b.getSoLuong() != null && b.getSoLuong() > 0) {
                        dsBienTheKhaDung.add(BienTheDoiTraDto.builder()
                                .id(b.getId())
                                .maSku(b.getMaSku())
                                .maSanPhamId(spId)
                                .tenSanPham(ct.getTenSanPham())
                                .tenMau(layTenMau(b.getMaMauSacId()))
                                .tenKichThuoc(layTenKichThuoc(b.getMaKichThuocId()))
                                .giaBan(b.getGiaBan())
                                .soLuongTon(b.getSoLuong())
                                .hinhAnhUrl(layHinhAnhDaiDien(b.getId(), spId))
                                .build());
                    }
                }
            }

            items.add(ItemDoiTraFormDto.builder()
                    .chiTietDonHangId(ct.getId())
                    .maBienTheId(ct.getMaBienTheId())
                    .maSanPhamId(spId)
                    .tenSanPham(ct.getTenSanPham())
                    .maSku(ct.getMaSku())
                    .tenMau(tenMau)
                    .tenKichThuoc(tenKichThuoc)
                    .donGia(ct.getDonGia())
                    .soLuongMua(ct.getSoLuong())
                    .thanhTien(ct.getThanhTien())
                    .hinhAnhUrl(imgUrl)
                    .danhSachBienTheDoi(dsBienTheKhaDung)
                    .build());
        }

        return DonHangDoiTraInfoDto.builder()
                .donHang(dh)
                .ngayHoanThanh(dh.getNgayTao())
                .hopLeDeDoiTra(true)
                .danhSachItem(items)
                .build();
    }

    private static final String THU_MUC_UPLOAD_RMA = "uploads/rma";

    /**
     * Tự động sinh mã giao dịch ngân hàng / mã đối soát xuất kho RMA chuẩn mực.
     * Ví dụ: TXN-REF-20261027-849201 (hoàn tiền) hoặc TXN-EX-20261027-392014 (đổi hàng).
     */
    public String sinhMaGiaoDichTuDong(String loaiDoiTra) {
        String ngay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "TRA_HANG_HOAN_TIEN".equalsIgnoreCase(loaiDoiTra) ? "TXN-REF" : "TXN-EX";
        int randomNum = ThreadLocalRandom.current().nextInt(100000, 999999);
        return prefix + "-" + ngay + "-" + randomNum;
    }

    /**
     * Tải và lưu file ảnh biên lai / bill chuyển khoản do CSKH tải lên từ máy tính.
     */
    public String luuFileBill(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path thuMuc = Paths.get(THU_MUC_UPLOAD_RMA);
            if (!Files.exists(thuMuc)) {
                Files.createDirectories(thuMuc);
            }

            String tenGoc = file.getOriginalFilename();
            String duoiTep = ".jpg";
            if (tenGoc != null && tenGoc.contains(".")) {
                duoiTep = tenGoc.substring(tenGoc.lastIndexOf(".")).toLowerCase();
            }

            if (!duoiTep.matches("^\\.(jpg|jpeg|png|webp|gif|pdf)$")) {
                duoiTep = ".jpg";
            }

            String tenFileMoi = "bill_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + duoiTep;
            Path duongDanDich = thuMuc.resolve(tenFileMoi);
            Files.copy(file.getInputStream(), duongDanDich, StandardCopyOption.REPLACE_EXISTING);

            return "/" + THU_MUC_UPLOAD_RMA + "/" + tenFileMoi;
        } catch (IOException e) {
            log.error("Lỗi khi lưu file bill RMA tải từ máy CSKH: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi lưu file ảnh biên lai / bill: " + e.getMessage(), e);
        }
    }

    /**
     * Tải và lưu các file ảnh minh chứng từ máy của khách hàng vào thư mục uploads/rma/.
     */
    public List<String> luuAnhMinhChung(MultipartFile[] files) {
        List<String> duongDanList = new ArrayList<>();
        if (files == null || files.length == 0) {
            return duongDanList;
        }

        try {
            Path thuMuc = Paths.get(THU_MUC_UPLOAD_RMA);
            if (!Files.exists(thuMuc)) {
                Files.createDirectories(thuMuc);
            }

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }

                String tenGoc = file.getOriginalFilename();
                String duoiTep = ".jpg";
                if (tenGoc != null && tenGoc.contains(".")) {
                    duoiTep = tenGoc.substring(tenGoc.lastIndexOf(".")).toLowerCase();
                }

                if (!duoiTep.matches("^\\.(jpg|jpeg|png|webp|gif)$")) {
                    log.warn("Định dạng file không được hỗ trợ: {}", duoiTep);
                    continue;
                }

                String tenFileMoi = "rma_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + duoiTep;
                Path duongDanDich = thuMuc.resolve(tenFileMoi);
                Files.copy(file.getInputStream(), duongDanDich, StandardCopyOption.REPLACE_EXISTING);

                duongDanList.add("/" + THU_MUC_UPLOAD_RMA + "/" + tenFileMoi);
            }
        } catch (IOException e) {
            log.error("Lỗi khi lưu ảnh minh chứng RMA tải lên từ máy: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi lưu file ảnh minh chứng: " + e.getMessage(), e);
        }

        return duongDanList;
    }

    /**
     * Tạo mới Yêu cầu Đổi/Trả (RMA) online với Form Validation chuyên nghiệp (US24).
     */
    @Transactional
    public YeuCauDoiTra taoYeuCauDoiTra(TaoYeuCauDoiTraRequest req, Long khachHangId, String soDienThoai) {
        return taoYeuCauDoiTra(req, null, khachHangId, soDienThoai);
    }

    /**
     * Tạo mới Yêu cầu Đổi/Trả (RMA) online kèm danh sách file ảnh tải từ máy và thông tin địa chỉ nhận hàng (US24).
     */
    @Transactional
    public YeuCauDoiTra taoYeuCauDoiTra(TaoYeuCauDoiTraRequest req, MultipartFile[] files, Long khachHangId, String soDienThoai) {
        log.info("Bắt đầu xử lý tạo yêu cầu Đổi/Trả cho đơn hàng #{}, khách ID: {}", req.getMaDonHang(), khachHangId);

        // 1. Kiểm tra đơn hàng hợp lệ & quyền sở hữu chống IDOR
        DonHang dh = kiemTraVaLayDonHangCuaKhachHang(req.getMaDonHang(), khachHangId, soDienThoai);

        boolean daHoanThanh = dh.getTrangThaiDonHang() != null &&
                ("HOAN_THANH".equalsIgnoreCase(dh.getTrangThaiDonHang().getMaTrangThai()) || dh.getTrangThaiDonHang().getId() == 4);
        if (!daHoanThanh) {
            String ttHienTai = dh.getTrangThaiDonHang() != null ? dh.getTrangThaiDonHang().getMaTrangThai() : "";
            if ("DANG_DOI_TRA".equalsIgnoreCase(ttHienTai)) {
                throw new IllegalStateException("Đơn hàng #" + dh.getMaDonHang() + " đang trong quy trình Đổi/Trả, không thể tạo thêm yêu cầu mới!");
            } else if ("DA_TRA_HANG".equalsIgnoreCase(ttHienTai)) {
                throw new IllegalStateException("Đơn hàng #" + dh.getMaDonHang() + " đã hoàn tất Trả hàng & Hoàn tiền, không thể yêu cầu đổi trả tiếp!");
            } else if ("DA_DOI_HANG".equalsIgnoreCase(ttHienTai)) {
                throw new IllegalStateException("Đơn hàng #" + dh.getMaDonHang() + " đã hoàn tất Đổi hàng sang sản phẩm mới!");
            } else {
                throw new IllegalStateException("Đơn hàng chưa hoàn tất giao hàng (trạng thái hiện tại: "
                        + (dh.getTrangThaiDonHang() != null ? dh.getTrangThaiDonHang().getTenTrangThai() : "Chưa rõ")
                        + "), không đủ điều kiện tạo yêu cầu Đổi/Trả.");
            }
        }

        // Chặn tạo trùng lặp
        if (yeuCauDoiTraRepository.existsByMaDonHangIdAndTrangThaiIn(dh.getId(), TRANG_THAI_DANG_XU_LY)) {
            throw new IllegalStateException("Đơn hàng đang có một yêu cầu Đổi/Trả chưa hoàn tất.");
        }

        // 2. Validate danh sách sản phẩm tick chọn (Tiêu chí 1)
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng tick chọn ít nhất một sản phẩm cần đổi/trả!");
        }

        List<ChiTietDoiTraItemRequest> selectedItems = req.getItems().stream()
                .filter(it -> Boolean.TRUE.equals(it.getSelected()))
                .toList();

        if (selectedItems.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng tick chọn ít nhất một sản phẩm lỗi trong đơn cũ để đổi/trả!");
        }

        Map<Long, ChiTietDonHang> mapChiTiet = new HashMap<>();
        List<ChiTietDonHang> danhSachChiTiet = chiTietDonHangRepository.findByMaDonHangId(dh.getId());
        for (ChiTietDonHang ct : danhSachChiTiet) {
            mapChiTiet.put(ct.getId(), ct);
        }

        BigDecimal tongTienHoanUocTinh = BigDecimal.ZERO;
        boolean laDoiHang = "DOI_HANG".equalsIgnoreCase(req.getLoaiDoiTra());
        boolean laHoanTien = "TRA_HANG_HOAN_TIEN".equalsIgnoreCase(req.getLoaiDoiTra());

        if (!laDoiHang && !laHoanTien) {
            throw new IllegalArgumentException("Hình thức yêu cầu không hợp lệ. Vui lòng chọn Đổi hàng hoặc Trả hàng hoàn tiền.");
        }

        // 3. Validate từng món được tick chọn
        for (ChiTietDoiTraItemRequest itemReq : selectedItems) {
            if (itemReq.getChiTietDonHangId() == null) {
                throw new IllegalArgumentException("Thông tin mặt hàng đổi trả không hợp lệ (thiếu ID dòng đơn hàng).");
            }

            ChiTietDonHang ct = mapChiTiet.get(itemReq.getChiTietDonHangId());
            if (ct == null) {
                throw new IllegalArgumentException("Sản phẩm được chọn không thuộc đơn hàng #" + dh.getMaDonHang());
            }

            if (itemReq.getSoLuongTra() == null || itemReq.getSoLuongTra() <= 0) {
                throw new IllegalArgumentException("Số lượng đổi/trả của sản phẩm '" + ct.getTenSanPham() + "' phải lớn hơn 0.");
            }

            if (itemReq.getSoLuongTra() > ct.getSoLuong()) {
                throw new IllegalArgumentException("Số lượng đổi/trả (" + itemReq.getSoLuongTra() +
                        ") vượt quá số lượng đã mua (" + ct.getSoLuong() + ") của sản phẩm: " + ct.getTenSanPham());
            }

            if (itemReq.getTinhTrangSanPham() == null || itemReq.getTinhTrangSanPham().trim().length() < 3) {
                throw new IllegalArgumentException("Vui lòng nhập rõ tình trạng lỗi của sản phẩm: " + ct.getTenSanPham());
            }

            // Nếu là ĐỔI HÀNG: Phải chọn SKU mới và kiểm tra tồn kho
            if (laDoiHang) {
                if (itemReq.getMaBienTheMoiId() == null) {
                    throw new IllegalArgumentException("Vui lòng chọn SKU mới (màu/size) muốn đổi cho sản phẩm: " + ct.getTenSanPham());
                }
                BienTheSanPham btMoi = bienTheSanPhamRepository.findById(itemReq.getMaBienTheMoiId())
                        .orElseThrow(() -> new IllegalArgumentException("Biến thể mới muốn đổi không tồn tại!"));

                if (!Boolean.TRUE.equals(btMoi.getKichHoat())) {
                    throw new IllegalArgumentException("Biến thể mới được chọn đang tạm ngừng kinh doanh.");
                }

                if (btMoi.getSoLuong() == null || btMoi.getSoLuong() < itemReq.getSoLuongTra()) {
                    throw new IllegalArgumentException("Biến thể mới được chọn (SKU: " + btMoi.getMaSku() +
                            ") không đủ số lượng tồn kho (còn: " + (btMoi.getSoLuong() != null ? btMoi.getSoLuong() : 0) + ").");
                }
            }

            // Nếu là HOÀN TIỀN: Cộng dồn tiền hoàn
            if (laHoanTien) {
                BigDecimal tienMon = ct.getDonGia().multiply(BigDecimal.valueOf(itemReq.getSoLuongTra()));
                tongTienHoanUocTinh = tongTienHoanUocTinh.add(tienMon);
            }
        }

        // 4. Validate thông tin tài khoản ngân hàng (khi Trả hàng hoàn tiền - Tiêu chí 2)
        String thongTinNganHangFormatted = null;
        if (laHoanTien) {
            if (req.getTenNganHang() == null || req.getTenNganHang().isBlank()) {
                throw new IllegalArgumentException("Vui lòng chọn hoặc nhập tên ngân hàng để nhận tiền hoàn!");
            }
            if (req.getSoTaiKhoan() == null || !req.getSoTaiKhoan().matches("^[0-9]{6,20}$")) {
                throw new IllegalArgumentException("Số tài khoản ngân hàng không hợp lệ (phải từ 6 đến 20 chữ số)!");
            }
            if (req.getTenChuTaiKhoan() == null || req.getTenChuTaiKhoan().trim().length() < 3) {
                throw new IllegalArgumentException("Vui lòng nhập chính xác tên chủ tài khoản ngân hàng (tối thiểu 3 ký tự)!");
            }

            thongTinNganHangFormatted = String.format("%s - STK: %s - Chủ TK: %s",
                    req.getTenNganHang().trim(),
                    req.getSoTaiKhoan().trim(),
                    req.getTenChuTaiKhoan().trim().toUpperCase());
        }

        // 5. Xử lý ảnh minh chứng (Tải trực tiếp từ máy + Link ngoài nếu có)
        List<String> dsAnhMinhChung = new ArrayList<>();
        if (files != null && files.length > 0) {
            List<String> filesUploaded = luuAnhMinhChung(files);
            dsAnhMinhChung.addAll(filesUploaded);
        }
        if (req.getHinhAnhMinhChung() != null && !req.getHinhAnhMinhChung().isBlank()) {
            for (String link : req.getHinhAnhMinhChung().split(",")) {
                String trimmed = link.trim();
                if (!trimmed.isEmpty() && !dsAnhMinhChung.contains(trimmed)) {
                    dsAnhMinhChung.add(trimmed);
                }
            }
        }
        String chuoiAnhMinhChung = dsAnhMinhChung.isEmpty() ? null : String.join(",", dsAnhMinhChung);

        // 6. Xử lý Địa chỉ nhận hàng / thu hồi hàng (Chuẩn Shopee)
        String tenNguoiNhan = (req.getTenNguoiNhan() != null && !req.getTenNguoiNhan().trim().isBlank())
                ? req.getTenNguoiNhan().trim()
                : (dh.getTenNguoiNhan() != null ? dh.getTenNguoiNhan() : "");
        String soDienThoaiNhan = (req.getSoDienThoaiNhan() != null && !req.getSoDienThoaiNhan().trim().isBlank())
                ? req.getSoDienThoaiNhan().trim()
                : (dh.getSoDienThoaiNhan() != null ? dh.getSoDienThoaiNhan() : "");
        String diaChiNhanHang = (req.getDiaChiNhanHang() != null && !req.getDiaChiNhanHang().trim().isBlank())
                ? req.getDiaChiNhanHang().trim()
                : "";

        // Hỗ trợ tự động ghép từ 3 dropdown Tỉnh/Thành -> Quận/Huyện -> Phường/Xã + Địa chỉ chi tiết nếu có
        if (diaChiNhanHang.isBlank() && req.getDiaChiChiTiet() != null && !req.getDiaChiChiTiet().isBlank()) {
            List<String> diaChiParts = new ArrayList<>();
            diaChiParts.add(req.getDiaChiChiTiet().trim());
            if (req.getPhuongXa() != null && !req.getPhuongXa().isBlank()) diaChiParts.add(req.getPhuongXa().trim());
            if (req.getQuanHuyen() != null && !req.getQuanHuyen().isBlank()) diaChiParts.add(req.getQuanHuyen().trim());
            if (req.getTinhThanh() != null && !req.getTinhThanh().isBlank()) diaChiParts.add(req.getTinhThanh().trim());
            diaChiNhanHang = String.join(", ", diaChiParts);
        }
        if (diaChiNhanHang.isBlank()) {
            diaChiNhanHang = (dh.getDiaChiGiaoHang() != null ? dh.getDiaChiGiaoHang() : "");
        }
        String ghiChuGiaoHang = (req.getGhiChuGiaoHang() != null && !req.getGhiChuGiaoHang().trim().isBlank())
                ? req.getGhiChuGiaoHang().trim()
                : null;

        // 7. Sinh mã RMA duy nhất
        String maRma = taoMaYeuCauRma();

        // 8. Lưu thực thể YeuCauDoiTra
        Long finalKhachHangId = dh.getKhachHang() != null ? dh.getKhachHang().getId() : khachHangId;

        YeuCauDoiTra ycdt = YeuCauDoiTra.builder()
                .maYeuCauRma(maRma)
                .maDonHangId(dh.getId())
                .maKhachHangId(finalKhachHangId)
                .loaiDoiTra(req.getLoaiDoiTra().toUpperCase())
                .lyDo(req.getLyDo().trim())
                .soTienHoan(laHoanTien ? tongTienHoanUocTinh : BigDecimal.ZERO)
                .thongTinNganHang(thongTinNganHangFormatted)
                .trangThai("CHO_XU_LY")
                .ngayTao(LocalDateTime.now())
                .hinhAnhMinhChung(chuoiAnhMinhChung)
                .tenNguoiNhan(tenNguoiNhan)
                .soDienThoaiNhan(soDienThoaiNhan)
                .diaChiNhanHang(diaChiNhanHang)
                .ghiChuGiaoHang(ghiChuGiaoHang)
                .build();

        YeuCauDoiTra saved = yeuCauDoiTraRepository.saveAndFlush(ycdt);

        // 9. Lưu các dòng ChiTietSanPhamDoiTra
        for (ChiTietDoiTraItemRequest itemReq : selectedItems) {
            ChiTietSanPhamDoiTra ctDoiTra = ChiTietSanPhamDoiTra.builder()
                    .maYeuCauDoiTraId(saved.getId())
                    .maChiTietDonHangId(itemReq.getChiTietDonHangId())
                    .soLuongTra(itemReq.getSoLuongTra())
                    .maBienTheMoiId(laDoiHang ? itemReq.getMaBienTheMoiId() : null)
                    .tinhTrangSanPham(itemReq.getTinhTrangSanPham().trim())
                    .build();
            chiTietSanPhamDoiTraRepository.save(ctDoiTra);
        }

        // 8. Chuyển đổi trạng thái đơn hàng sang DANG_DOI_TRA (Chuẩn Shopee Workflow)
        TrangThaiDonHang ttDangDoiTra = trangThaiDonHangRepository.findByMaTrangThai("DANG_DOI_TRA")
                .orElse(dh.getTrangThaiDonHang());
        dh.setTrangThaiDonHang(ttDangDoiTra);
        donHangRepository.save(dh);

        // 9. Ghi log lịch sử thay đổi trạng thái đơn hàng
        try {
            String moTaLog = String.format("Khách hàng đã gửi Yêu cầu Đổi/Trả online [#%s] hình thức: %s. Trạng thái đơn hàng chuyển sang [Đang đổi / trả]. Lý do: %s",
                    maRma,
                    laDoiHang ? "Đổi sang sản phẩm/SKU mới" : "Trả hàng hoàn tiền",
                    req.getLyDo().trim());

            LichSuTrangThaiDonHang logEntry = LichSuTrangThaiDonHang.builder()
                    .donHang(dh)
                    .trangThaiMoi(ttDangDoiTra)
                    .ghiChu(moTaLog)
                    .ngayChuyen(LocalDateTime.now())
                    .nguoiThucHien(null)
                    .build();
            lichSuTrangThaiDonHangRepository.save(logEntry);
        } catch (Exception ex) {
            log.warn("Không thể ghi log lịch sử đơn hàng khi tạo RMA: {}", ex.getMessage());
        }

        log.info("Tạo yêu cầu RMA thành công: #{}, loại: {}, trạng thái đơn hàng cập nhật: DANG_DOI_TRA", maRma, req.getLoaiDoiTra());
        return saved;
    }

    /**
     * Lấy danh sách phân trang các yêu cầu Đổi/Trả của khách hàng (US24).
     */
    @Transactional(readOnly = true)
    public Page<YeuCauDoiTraDetailDto> layDanhSachYeuCauDoiTraCuaKhachHang(
            Long khachHangId, String trangThai, String tuKhoa, Pageable pageable) {

        Page<YeuCauDoiTra> pageResult = yeuCauDoiTraRepository.timKiemCuaKhachHang(
                khachHangId, trangThai, tuKhoa, pageable);

        List<YeuCauDoiTraDetailDto> dtos = pageResult.getContent().stream()
                .map(this::chuyenDoiSangDetailDto)
                .toList();

        return new PageImpl<>(dtos, pageable, pageResult.getTotalElements());
    }

    /**
     * Lấy chi tiết một yêu cầu Đổi/Trả theo mã RMA (Bảo mật chống IDOR).
     */
    @Transactional(readOnly = true)
    public YeuCauDoiTraDetailDto layChiTietYeuCauDoiTra(String maRma, Long khachHangId) {
        YeuCauDoiTra rma = yeuCauDoiTraRepository.findByMaYeuCauRmaAndMaKhachHangId(maRma, khachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Yêu cầu Đổi/Trả #" + maRma + " không tồn tại hoặc bạn không có quyền truy cập."));

        return chuyenDoiSangDetailDto(rma);
    }

    // =========================================================================
    // NGHIỆP VỤ CSKH & QUẢN TRỊ VIÊN XÉT DUYỆT RMA (US25 - MOD 5 - HƯNG)
    // =========================================================================

    /**
     * Màn hình danh sách tất cả các yêu cầu Đổi/Trả cho CSKH & Quản trị kho (US25 - Tiêu chí 1).
     */
    @Transactional(readOnly = true)
    public Page<YeuCauDoiTraDetailDto> layDanhSachRmaChoAdmin(
            String tuKhoa, String trangThai, String loaiDoiTra, Pageable pageable) {

        String kw = (tuKhoa != null && !tuKhoa.trim().isEmpty()) ? tuKhoa.trim() : null;
        String st = (trangThai != null && !trangThai.trim().isEmpty() && !"TAT_CA".equalsIgnoreCase(trangThai)) ? trangThai.trim() : null;
        String type = (loaiDoiTra != null && !loaiDoiTra.trim().isEmpty() && !"TAT_CA".equalsIgnoreCase(loaiDoiTra)) ? loaiDoiTra.trim() : null;

        Page<YeuCauDoiTra> pageResult = yeuCauDoiTraRepository.timKiemPhanTrangChoAdmin(kw, st, type, pageable);

        List<YeuCauDoiTraDetailDto> dtos = pageResult.getContent().stream()
                .map(this::chuyenDoiSangDetailDto)
                .toList();

        return new PageImpl<>(dtos, pageable, pageResult.getTotalElements());
    }

    /**
     * Đếm số lượng yêu cầu Đổi/Trả theo từng trạng thái để hiển thị Badge trên Tab lọc (US25).
     */
    @Transactional(readOnly = true)
    public Map<String, Long> demSoLuongRmaTheoCacTrangThai() {
        Map<String, Long> map = new HashMap<>();
        map.put("TAT_CA", yeuCauDoiTraRepository.count());
        map.put("CHO_XU_LY", yeuCauDoiTraRepository.countByTrangThai("CHO_XU_LY"));
        map.put("DA_TIEP_NHAN", yeuCauDoiTraRepository.countByTrangThai("DA_TIEP_NHAN"));
        map.put("DANG_XU_LY", yeuCauDoiTraRepository.countByTrangThai("DANG_XU_LY"));
        map.put("HOAN_THANH", yeuCauDoiTraRepository.countByTrangThai("HOAN_THANH"));
        map.put("TU_CHOI", yeuCauDoiTraRepository.countByTrangThai("TU_CHOI"));
        return map;
    }

    /**
     * Lấy chi tiết phiếu RMA theo ID cho CSKH kiểm định (US25).
     */
    @Transactional(readOnly = true)
    public YeuCauDoiTraDetailDto layChiTietRmaTheoIdChoAdmin(Long id) {
        YeuCauDoiTra rma = yeuCauDoiTraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu yêu cầu Đổi/Trả ID: " + id));

        return chuyenDoiSangDetailDto(rma);
    }

    /**
     * Lấy danh sách các mã trạng thái hợp lệ tiếp theo cho phiếu RMA (dùng cho Dropdown và Giao diện).
     */
    public List<String> layDanhSachTrangThaiRmaHopLeTiepTheo(String trangThaiHienTai) {
        if (trangThaiHienTai == null || trangThaiHienTai.isBlank()) {
            return Collections.emptyList();
        }
        switch (trangThaiHienTai.toUpperCase()) {
            case "CHO_XU_LY":
                return List.of("DA_TIEP_NHAN", "TU_CHOI");
            case "DA_TIEP_NHAN":
                return List.of("DANG_XU_LY", "TU_CHOI");
            case "DANG_XU_LY":
                return List.of("HOAN_THANH", "TU_CHOI", "DANG_XU_LY");
            case "HOAN_THANH":
            case "TU_CHOI":
            default:
                return Collections.emptyList();
        }
    }

    /**
     * Validate nghiêm ngặt quy trình chuyển đổi trạng thái phiếu Đổi/Trả (RMA State Machine).
     * Ngăn chặn tuyệt đối việc nhảy cóc trạng thái (như từ 1 sang 3 luôn), đảo ngược phi logic
     * hoặc hoàn tất đổi trả khi hàng chưa về kho và chưa qua kiểm định chất lượng.
     */
    public void kiemTraChuyenDoiTrangThaiRmaHopLe(String oldStatus, String newStatus, XetDuyetRmaRequest req, YeuCauDoiTra rma) {
        if (oldStatus == null || oldStatus.isBlank()) return;
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn trạng thái mới cần cập nhật cho phiếu RMA!");
        }

        String cur = oldStatus.toUpperCase();
        String target = newStatus.toUpperCase();

        // 1. Kiểm tra trạng thái kết thúc (Terminal States)
        if ("HOAN_THANH".equalsIgnoreCase(cur)) {
            throw new IllegalStateException("Phiếu yêu cầu Đổi/Trả #" + rma.getMaYeuCauRma()
                    + " đã ở trạng thái kết thúc [Hoàn thành], tiền đã hoàn hoặc hàng mới đã xuất kho. Không được phép thay đổi trạng thái!");
        }
        if ("TU_CHOI".equalsIgnoreCase(cur)) {
            throw new IllegalStateException("Phiếu yêu cầu Đổi/Trả #" + rma.getMaYeuCauRma()
                    + " đã ở trạng thái kết thúc [Từ chối]. Quy trình đã đóng và không thể thay đổi trạng thái!");
        }

        // 2. Nếu giữ nguyên trạng thái
        if (cur.equalsIgnoreCase(target)) {
            if ("DANG_XU_LY".equalsIgnoreCase(cur)) {
                // Cho phép bổ sung / cập nhật thêm biên bản kiểm định kho
                return;
            }
            if ("DA_TIEP_NHAN".equalsIgnoreCase(cur) || "CHO_XU_LY".equalsIgnoreCase(cur)) {
                // Cho phép bổ sung ghi chú phản hồi
                return;
            }
        }

        // 3. Validate quy trình State Machine theo từng bước
        switch (cur) {
            case "CHO_XU_LY":
                // Từ Bước 1 (Chờ xử lý): Chỉ được sang Bước 2 (Đã tiếp nhận) hoặc Từ chối
                if (!"DA_TIEP_NHAN".equalsIgnoreCase(target) && !"TU_CHOI".equalsIgnoreCase(target)) {
                    if ("DANG_XU_LY".equalsIgnoreCase(target)) {
                        throw new IllegalStateException("Vi phạm quy trình: Phiếu RMA #" + rma.getMaYeuCauRma()
                                + " đang ở bước [Chờ xử lý], chưa được tiếp nhận và khách chưa gửi hàng về kho. Tuyệt đối không thể nhảy cóc sang [Đang xử lý / Kiểm định kho]!");
                    } else if ("HOAN_THANH".equalsIgnoreCase(target)) {
                        throw new IllegalStateException("Vi phạm quy trình nghiêm trọng: Phiếu RMA #" + rma.getMaYeuCauRma()
                                + " mới ở bước [Chờ xử lý], hàng chưa được gửi về và chưa kiểm định tại kho. Tuyệt đối không được nhảy cóc sang [Hoàn thành]!");
                    } else {
                        throw new IllegalStateException("Chuyển đổi trạng thái từ [Chờ xử lý] sang [" + target + "] không hợp lệ theo quy trình chuẩn!");
                    }
                }
                break;

            case "DA_TIEP_NHAN":
                // Từ Bước 2 (Đã tiếp nhận): Chỉ được sang Bước 3 (Hàng về kho kiểm định) hoặc Từ chối (quá hạn gửi)
                if (!"DANG_XU_LY".equalsIgnoreCase(target) && !"TU_CHOI".equalsIgnoreCase(target)) {
                    if ("CHO_XU_LY".equalsIgnoreCase(target)) {
                        throw new IllegalStateException("Phiếu RMA đã được tiếp nhận và đang chờ nhận kiện hàng gửi trả, không thể quay ngược lại bước [Chờ xử lý]!");
                    } else if ("HOAN_THANH".equalsIgnoreCase(target)) {
                        throw new IllegalStateException("Vi phạm quy trình kiểm soát kho: Kiện hàng chưa về kho và chưa có biên bản thẩm định chất lượng. Không được phép nhảy cóc sang [Hoàn thành]!");
                    } else {
                        throw new IllegalStateException("Chuyển đổi trạng thái từ [Đã tiếp nhận] sang [" + target + "] không hợp lệ theo quy trình chuẩn!");
                    }
                }
                break;

            case "DANG_XU_LY":
                // Từ Bước 3 (Đang kiểm định kho): Chỉ được sang Hoàn thành (đạt chuẩn) hoặc Từ chối (không đạt chuẩn)
                if (!"HOAN_THANH".equalsIgnoreCase(target) && !"TU_CHOI".equalsIgnoreCase(target) && !"DANG_XU_LY".equalsIgnoreCase(target)) {
                    if ("CHO_XU_LY".equalsIgnoreCase(target) || "DA_TIEP_NHAN".equalsIgnoreCase(target)) {
                        throw new IllegalStateException("Hàng đã tiếp nhận vào kho và đang trong tiến trình kiểm định, không thể đảo ngược tiến trình về [" + target + "]!");
                    } else {
                        throw new IllegalStateException("Chuyển đổi trạng thái từ [Đang xử lý] sang [" + target + "] không hợp lệ!");
                    }
                }
                break;

            default:
                throw new IllegalStateException("Trạng thái hiện tại [" + cur + "] không xác định trong hệ thống!");
        }

        // 4. Ràng buộc trường dữ liệu bắt buộc theo từng trạng thái đích
        if ("TU_CHOI".equalsIgnoreCase(target)) {
            if (req.getGhiChuAdmin() == null || req.getGhiChuAdmin().trim().length() < 5) {
                throw new IllegalArgumentException("Vui lòng nhập rõ chi tiết lý do từ chối yêu cầu Đổi/Trả (tối thiểu 5 ký tự)!");
            }
        }

        if ("DANG_XU_LY".equalsIgnoreCase(target)) {
            if (req.getGhiChuAdmin() == null || req.getGhiChuAdmin().trim().length() < 5) {
                throw new IllegalArgumentException("Vui lòng nhập ghi chú tiếp nhận kiện hàng về kho và bắt đầu thẩm định chất lượng (tối thiểu 5 ký tự)!");
            }
        }

        if ("HOAN_THANH".equalsIgnoreCase(target)) {
            if (req.getGhiChuAdmin() == null || req.getGhiChuAdmin().trim().length() < 5) {
                throw new IllegalArgumentException("Vui lòng nhập kết quả kiểm định đạt chuẩn và ghi chú hoàn tất trước khi phê duyệt (tối thiểu 5 ký tự)!");
            }
            if ("TRA_HANG_HOAN_TIEN".equalsIgnoreCase(rma.getLoaiDoiTra())) {
                if (req.getMaGiaoDichNganHang() == null || req.getMaGiaoDichNganHang().trim().isBlank()) {
                    throw new IllegalArgumentException("Vui lòng nhập Mã giao dịch ngân hàng / Ủy nhiệm chi hoàn tiền để xác thực thanh toán hoàn tất cho khách hàng!");
                }
            }
        }
    }

    /**
     * CSKH xét duyệt yêu cầu Đổi/Trả (RMA): Đổi trạng thái (Approve/Reject) & Ghi chú kiểm định kho (US25 - Tiêu chí 2 & 3).
     */
    @Transactional
    public YeuCauDoiTra xetDuyetYeuCauRma(Long id, XetDuyetRmaRequest req, NhanVien actor) {
        return xetDuyetYeuCauRma(id, req, null, actor);
    }

    /**
     * CSKH xét duyệt yêu cầu Đổi/Trả (RMA) kèm file ảnh bill gửi lại khách từ máy tính hoặc link.
     */
    @Transactional
    public YeuCauDoiTra xetDuyetYeuCauRma(Long id, XetDuyetRmaRequest req, MultipartFile fileBill, NhanVien actor) {
        YeuCauDoiTra rma = yeuCauDoiTraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu yêu cầu Đổi/Trả ID: " + id));

        String trangThaiMoi = req.getTrangThaiMoi();

        // 1. Validate State Machine chuyển đổi trạng thái nghiêm ngặt (Chống nhảy cóc từ 1 sang 3, đảo ngược phi logic)
        kiemTraChuyenDoiTrangThaiRmaHopLe(rma.getTrangThai(), trangThaiMoi, req, rma);

        // Xử lý gửi lại ảnh bill cho khách (Tải từ máy tính HOẶC Dán link URL)
        if (fileBill != null && !fileBill.isEmpty()) {
            String pathBill = luuFileBill(fileBill);
            rma.setHinhAnhBill(pathBill);
            log.info("Đã lưu ảnh bill tải lên từ máy tính cho RMA #{}: {}", rma.getMaYeuCauRma(), pathBill);
        } else if (req.getHinhAnhBill() != null && !req.getHinhAnhBill().isBlank()) {
            rma.setHinhAnhBill(req.getHinhAnhBill().trim());
            log.info("Đã lưu URL ảnh bill dán link cho RMA #{}: {}", rma.getMaYeuCauRma(), req.getHinhAnhBill().trim());
        }

        // 2. Bổ sung ghi chú kiểm định hàng về kho (Tiêu chí 3)
        String ghiChuMoi = req.getGhiChuAdmin() != null ? req.getGhiChuAdmin().trim() : "";
        if ("DANG_XU_LY".equalsIgnoreCase(trangThaiMoi) && !ghiChuMoi.isBlank()) {
            if (!ghiChuMoi.toLowerCase().startsWith("[kiểm định kho]") && !ghiChuMoi.toLowerCase().startsWith("kiểm định:")) {
                ghiChuMoi = "[Kiểm định kho] " + ghiChuMoi;
            }
        }

        // 3. Xử lý tồn kho khi HOÀN THÀNH (Tiêu chí 2 & Nghiệp vụ kho)
        if ("HOAN_THANH".equalsIgnoreCase(trangThaiMoi)) {
            List<ChiTietSanPhamDoiTra> chiTiets = chiTietSanPhamDoiTraRepository.findByMaYeuCauDoiTraId(rma.getId());

            if ("DOI_HANG".equalsIgnoreCase(rma.getLoaiDoiTra())) {
                for (ChiTietSanPhamDoiTra ct : chiTiets) {
                    if (ct.getMaBienTheMoiId() != null) {
                        int row = bienTheSanPhamRepository.truTonKhoAtomic(ct.getMaBienTheMoiId(), ct.getSoLuongTra());
                        if (row == 0) {
                            throw new IllegalStateException("Biến thể SKU mới muốn đổi không đủ số lượng tồn kho để xuất hàng hoàn tất!");
                        }
                    }
                    if (Boolean.TRUE.equals(req.getNhapLaiKho()) && ct.getChiTietDonHang() != null) {
                        ChiTietDonHang ctdh = ct.getChiTietDonHang();
                        if (ctdh.getMaBienTheId() != null) {
                            bienTheSanPhamRepository.hoanTonKhoAtomic(ctdh.getMaBienTheId(), ct.getSoLuongTra());
                        }
                    }
                }
            } else if ("TRA_HANG_HOAN_TIEN".equalsIgnoreCase(rma.getLoaiDoiTra())) {
                if (Boolean.TRUE.equals(req.getNhapLaiKho())) {
                    for (ChiTietSanPhamDoiTra ct : chiTiets) {
                        ChiTietDonHang ctdh = ct.getChiTietDonHang();
                        if (ctdh == null && ct.getMaChiTietDonHangId() != null) {
                            ctdh = chiTietDonHangRepository.findById(ct.getMaChiTietDonHangId()).orElse(null);
                        }
                        if (ctdh != null && ctdh.getMaBienTheId() != null) {
                            bienTheSanPhamRepository.hoanTonKhoAtomic(ctdh.getMaBienTheId(), ct.getSoLuongTra());
                        }
                    }
                }
                if (req.getMaGiaoDichNganHang() != null && !req.getMaGiaoDichNganHang().isBlank()) {
                    ghiChuMoi += (ghiChuMoi.isBlank() ? "" : " | ") + "Mã GD hoàn tiền: " + req.getMaGiaoDichNganHang().trim();
                }
            }
        }

        rma.setTrangThai(trangThaiMoi);
        if (actor != null) {
            rma.setNguoiDuyetId(actor.getId());
        }
        if (!ghiChuMoi.isBlank()) {
            rma.setGhiChuAdmin(ghiChuMoi);
        }

        YeuCauDoiTra saved = yeuCauDoiTraRepository.saveAndFlush(rma);

        // 4. Đồng bộ trạng thái đơn hàng tương ứng với tiến trình xét duyệt RMA (Chuẩn Shopee)
        try {
            DonHang dh = rma.getDonHang();
            if (dh == null && rma.getMaDonHangId() != null) {
                dh = donHangRepository.findById(rma.getMaDonHangId()).orElse(null);
            }
            if (dh != null) {
                String tenActor = actor != null ? (actor.getHoTen() != null ? actor.getHoTen() : actor.getTenDangNhap()) : "CSKH";
                TrangThaiDonHang ttMoiCuaDonHang = dh.getTrangThaiDonHang();
                String moTaLog;

                if ("TU_CHOI".equalsIgnoreCase(trangThaiMoi)) {
                    // Từ chối RMA -> Khôi phục đơn hàng về trạng thái HOAN_THANH
                    ttMoiCuaDonHang = trangThaiDonHangRepository.findByMaTrangThai("HOAN_THANH")
                            .orElse(dh.getTrangThaiDonHang());
                    moTaLog = String.format("CSKH [%s] đã TỪ CHỐI yêu cầu Đổi/Trả [#%s]. Đơn hàng được khôi phục về trạng thái [Hoàn thành]. Lý do từ chối: %s",
                            tenActor, rma.getMaYeuCauRma(), ghiChuMoi);
                } else if ("HOAN_THANH".equalsIgnoreCase(trangThaiMoi)) {
                    // Hoàn tất RMA -> Chuyển sang DA_TRA_HANG hoặc DA_DOI_HANG
                    if ("TRA_HANG_HOAN_TIEN".equalsIgnoreCase(rma.getLoaiDoiTra())) {
                        ttMoiCuaDonHang = trangThaiDonHangRepository.findByMaTrangThai("DA_TRA_HANG")
                                .orElse(dh.getTrangThaiDonHang());
                        moTaLog = String.format("CSKH [%s] đã hoàn tất Trả hàng & Hoàn tiền qua RMA [#%s]. Đơn hàng chuyển sang trạng thái [Đã trả hàng (Hoàn tiền)]. Số tiền hoàn: %s đ. Ghi chú: %s",
                                tenActor, rma.getMaYeuCauRma(),
                                rma.getSoTienHoan() != null ? rma.getSoTienHoan().toString() : "0",
                                ghiChuMoi.isBlank() ? "Không có" : ghiChuMoi);
                    } else {
                        ttMoiCuaDonHang = trangThaiDonHangRepository.findByMaTrangThai("DA_DOI_HANG")
                                .orElse(null);
                        if (ttMoiCuaDonHang == null) {
                            ttMoiCuaDonHang = trangThaiDonHangRepository.findByMaTrangThai("HOAN_THANH")
                                    .orElse(dh.getTrangThaiDonHang());
                        }
                        moTaLog = String.format("CSKH [%s] đã hoàn tất Đổi hàng sang SKU mới qua RMA [#%s]. Đã trừ kho và xuất hàng mới. Đơn hàng chuyển sang trạng thái [Đã đổi hàng]. Ghi chú: %s",
                                tenActor, rma.getMaYeuCauRma(),
                                ghiChuMoi.isBlank() ? "Không có" : ghiChuMoi);
                    }
                } else if ("DA_TIEP_NHAN".equalsIgnoreCase(trangThaiMoi)) {
                    ttMoiCuaDonHang = trangThaiDonHangRepository.findByMaTrangThai("DANG_DOI_TRA")
                            .orElse(dh.getTrangThaiDonHang());
                    moTaLog = String.format("CSKH [%s] đã DUYỆT TIẾP NHẬN yêu cầu Đổi/Trả [#%s]. Hướng dẫn khách hàng gửi hàng về kho để kiểm định.",
                            tenActor, rma.getMaYeuCauRma());
                } else if ("DANG_XU_LY".equalsIgnoreCase(trangThaiMoi)) {
                    ttMoiCuaDonHang = trangThaiDonHangRepository.findByMaTrangThai("DANG_DOI_TRA")
                            .orElse(dh.getTrangThaiDonHang());
                    moTaLog = String.format("CSKH [%s] đã ghi nhận hàng về kho và ĐANG KIỂM ĐỊNH CHẤT LƯỢNG cho RMA [#%s]. Ghi chú kiểm định: %s",
                            tenActor, rma.getMaYeuCauRma(), ghiChuMoi.isBlank() ? "Đang thẩm định" : ghiChuMoi);
                } else {
                    moTaLog = String.format("CSKH [%s] cập nhật trạng thái phiếu RMA #%s sang [%s]. Ghi chú: %s",
                            tenActor, rma.getMaYeuCauRma(), trangThaiMoi, ghiChuMoi.isBlank() ? "Không có" : ghiChuMoi);
                }

                if (ttMoiCuaDonHang != null) {
                    dh.setTrangThaiDonHang(ttMoiCuaDonHang);
                    donHangRepository.save(dh);
                }

                LichSuTrangThaiDonHang logEntry = LichSuTrangThaiDonHang.builder()
                        .donHang(dh)
                        .trangThaiMoi(ttMoiCuaDonHang != null ? ttMoiCuaDonHang : dh.getTrangThaiDonHang())
                        .ghiChu(moTaLog)
                        .ngayChuyen(LocalDateTime.now())
                        .nguoiThucHien(actor)
                        .build();
                lichSuTrangThaiDonHangRepository.save(logEntry);
            }
        } catch (Exception ex) {
            log.warn("Không thể đồng bộ trạng thái đơn hàng khi CSKH xét duyệt RMA: {}", ex.getMessage());
        }

        log.info("CSKH xét duyệt RMA #{} sang trạng thái: {}", rma.getMaYeuCauRma(), trangThaiMoi);
        return saved;
    }

    // =========================================================================
    // HELPER & CHUYỂN ĐỔI DTO
    // =========================================================================

    public YeuCauDoiTraDetailDto chuyenDoiSangDetailDto(YeuCauDoiTra rma) {
        String tenTrangThai;
        String badgeClass;

        switch (rma.getTrangThai()) {
            case "DA_TIEP_NHAN" -> {
                tenTrangThai = "Đã tiếp nhận yêu cầu";
                badgeClass = "bg-blue-100 text-blue-800 border-blue-300";
            }
            case "DANG_XU_LY" -> {
                tenTrangThai = "Đang xử lý đổi / hoàn tiền";
                badgeClass = "bg-purple-100 text-purple-800 border-purple-300";
            }
            case "HOAN_THANH" -> {
                tenTrangThai = "Đã hoàn thành đổi trả";
                badgeClass = "bg-emerald-100 text-emerald-800 border-emerald-300";
            }
            case "TU_CHOI" -> {
                tenTrangThai = "Bị từ chối";
                badgeClass = "bg-rose-100 text-rose-800 border-rose-300";
            }
            default -> {
                tenTrangThai = "Chờ tiếp nhận xử lý";
                badgeClass = "bg-amber-100 text-amber-800 border-amber-300";
            }
        }

        List<ChiTietSanPhamDoiTra> chiTiets = chiTietSanPhamDoiTraRepository.findByMaYeuCauDoiTraId(rma.getId());
        List<ChiTietSanPhamDoiTraDto> danhSachItems = new ArrayList<>();
        int tongSoLuong = 0;
        BigDecimal tongTien = BigDecimal.ZERO;

        for (ChiTietSanPhamDoiTra ct : chiTiets) {
            ChiTietDonHang ctdh = ct.getChiTietDonHang();
            if (ctdh == null && ct.getMaChiTietDonHangId() != null) {
                ctdh = chiTietDonHangRepository.findById(ct.getMaChiTietDonHangId()).orElse(null);
            }

            String tenSp = ctdh != null ? ctdh.getTenSanPham() : "Sản phẩm";
            String skuCu = ctdh != null ? ctdh.getMaSku() : "";
            BigDecimal donGia = ctdh != null ? ctdh.getDonGia() : BigDecimal.ZERO;
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(ct.getSoLuongTra()));

            tongSoLuong += ct.getSoLuongTra();
            tongTien = tongTien.add(thanhTien);

            // Thông tin biến thể cũ
            BienTheSanPham btCu = ctdh != null && ctdh.getMaBienTheId() != null ?
                    bienTheSanPhamRepository.findById(ctdh.getMaBienTheId()).orElse(null) : null;
            String phanLoaiCu = (btCu != null) ? (layTenMau(btCu.getMaMauSacId()) + " / " + layTenKichThuoc(btCu.getMaKichThuocId())) : "";
            String imgUrl = ctdh != null ? layHinhAnhDaiDien(ctdh.getMaBienTheId(), btCu != null ? btCu.getMaSanPhamId() : null) : "";

            // Thông tin biến thể mới (nếu đổi)
            String skuMoi = null;
            String phanLoaiMoi = null;
            String imgMoiUrl = null;
            if (ct.getMaBienTheMoiId() != null) {
                BienTheSanPham btMoi = bienTheSanPhamRepository.findById(ct.getMaBienTheMoiId()).orElse(null);
                if (btMoi != null) {
                    skuMoi = btMoi.getMaSku();
                    phanLoaiMoi = layTenMau(btMoi.getMaMauSacId()) + " / " + layTenKichThuoc(btMoi.getMaKichThuocId());
                    imgMoiUrl = layHinhAnhDaiDien(btMoi.getId(), btMoi.getMaSanPhamId());
                }
            }

            danhSachItems.add(ChiTietSanPhamDoiTraDto.builder()
                    .id(ct.getId())
                    .chiTietDonHangId(ct.getMaChiTietDonHangId())
                    .tenSanPham(tenSp)
                    .hinhAnhUrl(imgUrl)
                    .skuCu(skuCu)
                    .phanLoaiCu(phanLoaiCu)
                    .soLuongTra(ct.getSoLuongTra())
                    .donGia(donGia)
                    .thanhTienTra(thanhTien)
                    .tinhTrangLoi(ct.getTinhTrangSanPham())
                    .maBienTheMoiId(ct.getMaBienTheMoiId())
                    .skuMoi(skuMoi)
                    .phanLoaiMoi(phanLoaiMoi)
                    .hinhAnhMoiUrl(imgMoiUrl)
                    .build());
        }

        return YeuCauDoiTraDetailDto.builder()
                .yeuCauDoiTra(rma)
                .tenTrangThaiTiengViet(tenTrangThai)
                .badgeClass(badgeClass)
                .tongSoLuongTra(tongSoLuong)
                .tongTienUocTinh(rma.getSoTienHoan() != null && rma.getSoTienHoan().compareTo(BigDecimal.ZERO) > 0 ? rma.getSoTienHoan() : tongTien)
                .danhSachSanPhamDoiTra(danhSachItems)
                .build();
    }

    private DonHang kiemTraVaLayDonHangCuaKhachHang(String maDonHang, Long khachHangId, String soDienThoai) {
        DonHang dh = donHangRepository.findByMaDonHang(maDonHang)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng #" + maDonHang + " không tồn tại trên hệ thống."));

        boolean hopLe = false;
        if (khachHangId != null && dh.getKhachHang() != null && khachHangId.equals(dh.getKhachHang().getId())) {
            hopLe = true;
        } else if (soDienThoai != null && !soDienThoai.isBlank() && soDienThoai.equals(dh.getSoDienThoaiNhan())) {
            hopLe = true;
        }

        if (!hopLe) {
            log.warn("Cảnh báo IDOR: Khách hàng ID: {} cố gắng truy cập đơn hàng #{} của khách khác!", khachHangId, maDonHang);
            throw new IllegalArgumentException("Bạn không có quyền thao tác trên đơn hàng #" + maDonHang);
        }

        return dh;
    }

    private String taoMaYeuCauRma() {
        String ngay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String maRma;
        int count = 0;
        do {
            int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999);
            maRma = "RMA-" + ngay + "-" + randomNum;
            count++;
        } while (yeuCauDoiTraRepository.existsByMaYeuCauRma(maRma) && count < 50);
        return maRma;
    }

    private String layTenMau(Long maMauSacId) {
        if (maMauSacId == null) return "Tiêu chuẩn";
        return mauSacRepository.findById(maMauSacId)
                .map(MauSac::getTenMau)
                .orElse("Tiêu chuẩn");
    }

    private String layTenKichThuoc(Long maKichThuocId) {
        if (maKichThuocId == null) return "Freesize";
        return kichThuocRepository.findById(maKichThuocId)
                .map(KichThuoc::getTenKichThuoc)
                .orElse("Freesize");
    }

    private String layHinhAnhDaiDien(Long maBienTheId, Long maSanPhamId) {
        if (maBienTheId != null) {
            List<HinhAnh> dsAnhBt = hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(maBienTheId);
            if (!dsAnhBt.isEmpty() && dsAnhBt.get(0).getDuongDanAnh() != null) {
                return dsAnhBt.get(0).getDuongDanAnh();
            }
        }
        if (maSanPhamId != null) {
            List<HinhAnh> dsAnhSp = hinhAnhRepository.findByMaSanPhamIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(maSanPhamId);
            if (!dsAnhSp.isEmpty() && dsAnhSp.get(0).getDuongDanAnh() != null) {
                return dsAnhSp.get(0).getDuongDanAnh();
            }
        }
        return "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=300&auto=format&fit=crop&q=80";
    }
}
