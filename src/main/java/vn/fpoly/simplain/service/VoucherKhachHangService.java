package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.ValidateVoucherResultDto;
import vn.fpoly.simplain.dto.VoucherKhachHangDto;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.MaGiamGia;
import vn.fpoly.simplain.entity.VoucherSoHuu;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.MaGiamGiaRepository;
import vn.fpoly.simplain.repository.VoucherSoHuuRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service xử lý nghiệp vụ Khách hàng Thu thập và Áp dụng Mã giảm giá (US27 - Mod 6 / Voucher - Khoa).
 * - Tiêu chí 1: Lưu voucher vào voucher_so_huu (Ví voucher cá nhân).
 * - Tiêu chí 2: API Validate check mã còn lượt / có hợp lệ với đơn hàng hay không.
 * - Thông báo lỗi rõ ràng, chi tiết, thân thiện để người dùng nhận biết ngay lập tức.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherKhachHangService {

    private final MaGiamGiaRepository maGiamGiaRepository;
    private final VoucherSoHuuRepository voucherSoHuuRepository;
    private final KhachHangRepository khachHangRepository;
    private final DonHangRepository donHangRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    // =========================================================================
    // TIÊU CHÍ 1: THU THẬP & LƯU VOUCHER VÀO VÍ VOUCHER_SO_HUU (US27)
    // =========================================================================

    /**
     * Khách hàng thu thập Voucher vào Ví cá nhân (voucher_so_huu).
     * Kiểm tra chặt chẽ điều kiện: Đăng nhập, trạng thái hoạt động, hạn sử dụng,
     * lượt dùng toàn hệ thống, ngân sách, giới hạn mỗi người dùng.
     */
    @Transactional
    public VoucherSoHuu luuVoucherVaoVi(Long khachHangId, Long voucherId) {
        if (khachHangId == null) {
            throw new IllegalArgumentException("Vui lòng đăng nhập tài khoản để thu thập mã giảm giá vào Ví Voucher!");
        }

        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin khách hàng trong hệ thống!"));

        if (voucherId == null) {
            throw new IllegalArgumentException("Mã chương trình giảm giá không hợp lệ!");
        }

        MaGiamGia voucher = maGiamGiaRepository.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá này không tồn tại trên hệ thống!"));

        LocalDateTime now = LocalDateTime.now();

        // 1. Kiểm tra kích hoạt
        if (!Boolean.TRUE.equals(voucher.getKichHoat())) {
            throw new IllegalArgumentException("Mã giảm giá [" + voucher.getMaCode() + "] đang tạm ngưng phát hành.");
        }

        // 2. Kiểm tra thời gian bắt đầu
        if (voucher.getNgayBatDau() != null && now.isBefore(voucher.getNgayBatDau())) {
            throw new IllegalArgumentException("Mã giảm giá [" + voucher.getMaCode() +
                    "] chưa đến thời gian áp dụng (Bắt đầu từ: " + voucher.getNgayBatDau().format(DATE_TIME_FORMATTER) + ").");
        }

        // 3. Kiểm tra hạn sử dụng
        if (voucher.getNgayKetThuc() != null && now.isAfter(voucher.getNgayKetThuc())) {
            throw new IllegalArgumentException("Mã giảm giá [" + voucher.getMaCode() +
                    "] đã hết hạn sử dụng vào lúc: " + voucher.getNgayKetThuc().format(DATE_TIME_FORMATTER) + ".");
        }

        // 4. Kiểm tra lượt phát hành toàn sàn
        if (voucher.getTongSoLuong() != null && voucher.getSoLuongDaDung() != null
                && voucher.getSoLuongDaDung() >= voucher.getTongSoLuong()) {
            throw new IllegalArgumentException("Rất tiếc! Mã giảm giá [" + voucher.getMaCode() +
                    "] đã được thu thập và sử dụng hết lượt trên toàn hệ thống.");
        }

        // 5. Kiểm tra ngân sách chương trình
        if (voucher.getNganSach() != null && voucher.getNganSachDaDung() != null
                && voucher.getNganSachDaDung().compareTo(voucher.getNganSach()) >= 0) {
            throw new IllegalArgumentException("Rất tiếc! Chương trình ưu đãi [" + voucher.getMaCode() +
                    "] đã đạt hạn mức ngân sách tối đa.");
        }

        // 6. Kiểm tra xem khách hàng đã có voucher này trong ví mà chưa sử dụng chưa
        boolean daCoChuaDung = voucherSoHuuRepository.existsByMaKhachHangIdAndMaGiamGiaIdAndDaSuDung(khachHangId, voucher.getId(), false);
        if (daCoChuaDung) {
            throw new IllegalArgumentException("Bạn đã lưu mã giảm giá [" + voucher.getMaCode() +
                    "] trong Ví Voucher rồi! Hãy mở Giỏ hàng hoặc Thanh toán để sử dụng ngay.");
        }

        // 7. Kiểm tra giới hạn mỗi khách hàng (gioiHanMoiUser)
        int gioiHanUser = voucher.getGioiHanMoiUser() != null ? voucher.getGioiHanMoiUser() : 1;
        long soLanDaNhan = voucherSoHuuRepository.countByMaKhachHangIdAndMaGiamGiaId(khachHangId, voucher.getId());
        if (soLanDaNhan >= gioiHanUser) {
            throw new IllegalArgumentException("Bạn đã đạt giới hạn nhận mã giảm giá này (Tối đa " +
                    gioiHanUser + " lượt/khách hàng)!");
        }

        // 8. Tạo bản ghi sở hữu vào bảng voucher_so_huu
        VoucherSoHuu soHuu = VoucherSoHuu.builder()
                .maKhachHangId(khachHangId)
                .maGiamGiaId(voucher.getId())
                .maGiamGia(voucher)
                .khachHang(khachHang)
                .daSuDung(false)
                .ngayNhan(now)
                .ngaySuDung(null)
                .build();

        VoucherSoHuu saved = voucherSoHuuRepository.saveAndFlush(soHuu);
        log.info("Khách hàng ID={} thu thập thành công voucher ID={} [Code={}] vào ví", khachHangId, voucher.getId(), voucher.getMaCode());
        return saved;
    }

    /**
     * Khách hàng thu thập Voucher theo mã code chữ (VD: gõ tay "COOL20").
     */
    @Transactional
    public VoucherSoHuu luuVoucherVaoViTheoMaCode(Long khachHangId, String maCode) {
        if (maCode == null || maCode.trim().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập mã giảm giá cần lưu!");
        }
        MaGiamGia voucher = maGiamGiaRepository.findByMaCodeIgnoreCase(maCode.trim())
                .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá '" + maCode.trim().toUpperCase() + "' không tồn tại trên hệ thống!"));
        return luuVoucherVaoVi(khachHangId, voucher.getId());
    }

    // =========================================================================
    // TIÊU CHÍ 2: API VALIDATE CHECK MÃ CÒN LƯỢT / HỢP LỆ ĐƠN HÀNG (US27)
    // =========================================================================

    /**
     * Kiểm tra tính hợp lệ toàn diện của mã giảm giá với đơn hàng hiện tại.
     * Trả về thông báo lỗi chi tiết, rõ ràng cho mọi kịch bản.
     */
    @Transactional(readOnly = true)
    public ValidateVoucherResultDto kiemTraVaApDungVoucher(String maCode, BigDecimal tongTienHang, Long khachHangId) {
        // Chuẩn hóa dữ liệu đầu vào
        if (maCode == null || maCode.trim().isBlank()) {
            return ValidateVoucherResultDto.builder()
                    .hopLe(false)
                    .thongBao("Vui lòng nhập mã giảm giá!")
                    .tienGiam(BigDecimal.ZERO)
                    .build();
        }

        String code = maCode.trim().toUpperCase();
        if (tongTienHang == null || tongTienHang.compareTo(BigDecimal.ZERO) < 0) {
            tongTienHang = BigDecimal.ZERO;
        }

        // 1. Kiểm tra tồn tại trong DB
        Optional<MaGiamGia> opt = maGiamGiaRepository.findByMaCodeIgnoreCase(code);
        if (opt.isEmpty()) {
            return ValidateVoucherResultDto.builder()
                    .hopLe(false)
                    .maCode(code)
                    .tienGiam(BigDecimal.ZERO)
                    .thongBao("Mã giảm giá '" + code + "' không tồn tại trên hệ thống. Vui lòng kiểm tra lại!")
                    .build();
        }

        MaGiamGia voucher = opt.get();
        LocalDateTime now = LocalDateTime.now();

        // 2. Kiểm tra kích hoạt
        if (!Boolean.TRUE.equals(voucher.getKichHoat())) {
            return ValidateVoucherResultDto.builder()
                    .hopLe(false)
                    .maCode(code)
                    .voucherId(voucher.getId())
                    .tienGiam(BigDecimal.ZERO)
                    .thongBao("Mã giảm giá '" + voucher.getMaCode() + "' đang tạm ngưng áp dụng.")
                    .build();
        }

        // 3. Kiểm tra thời điểm bắt đầu
        if (voucher.getNgayBatDau() != null && now.isBefore(voucher.getNgayBatDau())) {
            return ValidateVoucherResultDto.builder()
                    .hopLe(false)
                    .maCode(code)
                    .voucherId(voucher.getId())
                    .tienGiam(BigDecimal.ZERO)
                    .thongBao("Mã giảm giá '" + voucher.getMaCode() + "' chưa có hiệu lực. Chương trình bắt đầu từ: " +
                            voucher.getNgayBatDau().format(DATE_TIME_FORMATTER) + ".")
                    .build();
        }

        // 4. Kiểm tra thời điểm hết hạn
        if (voucher.getNgayKetThuc() != null && now.isAfter(voucher.getNgayKetThuc())) {
            return ValidateVoucherResultDto.builder()
                    .hopLe(false)
                    .maCode(code)
                    .voucherId(voucher.getId())
                    .tienGiam(BigDecimal.ZERO)
                    .thongBao("Mã giảm giá '" + voucher.getMaCode() + "' đã hết hạn sử dụng vào lúc: " +
                            voucher.getNgayKetThuc().format(DATE_TIME_FORMATTER) + ".")
                    .build();
        }

        // 5. Kiểm tra tổng lượt dùng toàn hệ thống
        if (voucher.getTongSoLuong() != null && voucher.getSoLuongDaDung() != null
                && voucher.getSoLuongDaDung() >= voucher.getTongSoLuong()) {
            return ValidateVoucherResultDto.builder()
                    .hopLe(false)
                    .maCode(code)
                    .voucherId(voucher.getId())
                    .tienGiam(BigDecimal.ZERO)
                    .thongBao("Rất tiếc! Mã giảm giá '" + voucher.getMaCode() + "' đã hết lượt sử dụng trên hệ thống.")
                    .build();
        }

        // 6. Kiểm tra giới hạn ngân sách
        if (voucher.getNganSach() != null && voucher.getNganSachDaDung() != null
                && voucher.getNganSachDaDung().compareTo(voucher.getNganSach()) >= 0) {
            return ValidateVoucherResultDto.builder()
                    .hopLe(false)
                    .maCode(code)
                    .voucherId(voucher.getId())
                    .tienGiam(BigDecimal.ZERO)
                    .thongBao("Rất tiếc! Chương trình ưu đãi '" + voucher.getMaCode() + "' đã đạt giới hạn ngân sách tối đa.")
                    .build();
        }

        // 7. Kiểm tra giới hạn sử dụng của từng khách hàng
        if (khachHangId != null) {
            int maxPerUser = voucher.getGioiHanMoiUser() != null ? voucher.getGioiHanMoiUser() : 1;
            long soDonDaDung = donHangRepository.demSoDonApDungVoucherCuaKhachHang(khachHangId, voucher.getId());
            if (soDonDaDung >= maxPerUser) {
                return ValidateVoucherResultDto.builder()
                        .hopLe(false)
                        .maCode(code)
                        .voucherId(voucher.getId())
                        .tienGiam(BigDecimal.ZERO)
                        .thongBao("Bạn đã sử dụng hết số lượt cho phép của mã '" + voucher.getMaCode() +
                                "' (Tối đa " + maxPerUser + " lượt/khách hàng).")
                        .build();
            }
        }

        // 8. Kiểm tra giá trị đơn hàng tối thiểu (Minimum Order Value)
        BigDecimal donToiThieu = (voucher.getGiaTriDonToiThieu() != null) ? voucher.getGiaTriDonToiThieu() : BigDecimal.ZERO;
        if (tongTienHang.compareTo(donToiThieu) < 0) {
            BigDecimal conThieu = donToiThieu.subtract(tongTienHang);
            String thongBaoLoi = String.format("Đơn hàng chưa đạt giá trị tối thiểu %,d đ (Hiện tại: %,d đ, bạn cần mua thêm %,d đ để áp dụng mã này)!",
                    donToiThieu.longValue(), tongTienHang.longValue(), conThieu.longValue());

            return ValidateVoucherResultDto.builder()
                    .hopLe(false)
                    .maCode(code)
                    .voucherId(voucher.getId())
                    .tenVoucher(voucher.getTenVoucher())
                    .loaiGiamGia(voucher.getLoaiGiamGia())
                    .giaTriGiam(voucher.getGiaTriGiam())
                    .giamToiDa(voucher.getGiamToiDa())
                    .giaTriDonToiThieu(donToiThieu)
                    .soTienConThieu(conThieu)
                    .tienGiam(BigDecimal.ZERO)
                    .thongBao(thongBaoLoi)
                    .build();
        }

        // 9. Tính toán số tiền chiết khấu thực tế
        BigDecimal tienGiam = BigDecimal.ZERO;
        if ("PERCENT".equalsIgnoreCase(voucher.getLoaiGiamGia())) {
            tienGiam = tongTienHang.multiply(voucher.getGiaTriGiam())
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

            // Giới hạn trần giảm tối đa (nếu có)
            if (voucher.getGiamToiDa() != null && voucher.getGiamToiDa().compareTo(BigDecimal.ZERO) > 0) {
                if (tienGiam.compareTo(voucher.getGiamToiDa()) > 0) {
                    tienGiam = voucher.getGiamToiDa();
                }
            }
        } else if ("FIXED_AMOUNT".equalsIgnoreCase(voucher.getLoaiGiamGia())) {
            tienGiam = voucher.getGiaTriGiam();
        }

        // Không cho phép số tiền giảm vượt quá tổng tiền hàng
        if (tienGiam.compareTo(tongTienHang) > 0) {
            tienGiam = tongTienHang;
        }

        BigDecimal tongThanhToanSauGiam = tongTienHang.subtract(tienGiam).max(BigDecimal.ZERO);
        String thongBaoThanhCong = String.format("Áp dụng thành công mã giảm giá '%s'! Bạn được giảm %,d đ.",
                voucher.getMaCode(), tienGiam.longValue());

        return ValidateVoucherResultDto.builder()
                .hopLe(true)
                .maCode(voucher.getMaCode())
                .voucherId(voucher.getId())
                .tenVoucher(voucher.getTenVoucher())
                .loaiGiamGia(voucher.getLoaiGiamGia())
                .giaTriGiam(voucher.getGiaTriGiam())
                .giamToiDa(voucher.getGiamToiDa())
                .giaTriDonToiThieu(donToiThieu)
                .soTienConThieu(BigDecimal.ZERO)
                .tienGiam(tienGiam)
                .tongThanhToanMoi(tongThanhToanSauGiam)
                .thongBao(thongBaoThanhCong)
                .build();
    }

    // =========================================================================
    // TIỆN ÍCH LẤY DỮ LIỆU HIỂN THỊ VÍ VOUCHER & SÀN (US27)
    // =========================================================================

    /**
     * Lấy danh sách Voucher trong Ví của khách hàng kèm phân loại Tab.
     */
    @Transactional(readOnly = true)
    public List<VoucherKhachHangDto> layDanhSachViVoucher(Long khachHangId, String tab) {
        if (khachHangId == null) {
            return Collections.emptyList();
        }

        List<VoucherSoHuu> danhSachGoc = voucherSoHuuRepository.layDanhSachVoucherCuaKhachHang(khachHangId);
        List<VoucherKhachHangDto> ketQua = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (VoucherSoHuu vsh : danhSachGoc) {
            MaGiamGia m = vsh.getMaGiamGia();
            if (m == null) continue;

            boolean daDung = Boolean.TRUE.equals(vsh.getDaSuDung());
            boolean hetHan = (m.getNgayKetThuc() != null && now.isAfter(m.getNgayKetThuc()))
                    || m.isHetLuot() || m.isHetNganSach();
            boolean chuaBatDau = m.getNgayBatDau() != null && now.isBefore(m.getNgayBatDau());
            boolean khaDung = !daDung && !hetHan && !chuaBatDau && Boolean.TRUE.equals(m.getKichHoat());

            String trangThaiHienThi;
            String trangThaiLabel;
            String trangThaiBadgeClass;

            if (daDung) {
                trangThaiHienThi = "DA_SU_DUNG";
                trangThaiLabel = "Đã sử dụng";
                trangThaiBadgeClass = "bg-slate-100 text-slate-600 border border-slate-200";
            } else if (hetHan) {
                trangThaiHienThi = "HET_HAN";
                trangThaiLabel = "Hết hạn";
                trangThaiBadgeClass = "bg-rose-50 text-rose-600 border border-rose-200";
            } else if (chuaBatDau) {
                trangThaiHienThi = "CHUA_BAT_DAU";
                trangThaiLabel = "Chưa diễn ra";
                trangThaiBadgeClass = "bg-amber-50 text-amber-700 border border-amber-200";
            } else {
                trangThaiHienThi = "KHA_DUNG";
                trangThaiLabel = "Khả dụng";
                trangThaiBadgeClass = "bg-emerald-50 text-emerald-700 border border-emerald-200";
            }

            // Lọc theo Tab yêu cầu
            if ("KHA_DUNG".equalsIgnoreCase(tab) && !khaDung) continue;
            if ("DA_DUNG".equalsIgnoreCase(tab) && !daDung) continue;
            if ("HET_HAN".equalsIgnoreCase(tab) && (!hetHan || daDung)) continue;

            ketQua.add(VoucherKhachHangDto.builder()
                    .id(vsh.getId())
                    .voucherId(m.getId())
                    .maCode(m.getMaCode())
                    .tenVoucher(m.getTenVoucher())
                    .loaiGiamGia(m.getLoaiGiamGia())
                    .giaTriGiam(m.getGiaTriGiam())
                    .giamToiDa(m.getGiamToiDa())
                    .giaTriDonToiThieu(m.getGiaTriDonToiThieu())
                    .tongSoLuong(m.getTongSoLuong())
                    .soLuongDaDung(m.getSoLuongDaDung())
                    .gioiHanMoiUser(m.getGioiHanMoiUser())
                    .ngayBatDau(m.getNgayBatDau())
                    .ngayKetThuc(m.getNgayKetThuc())
                    .moTa(m.getMoTa())
                    .daThuThap(true)
                    .daSuDung(daDung)
                    .ngayNhan(vsh.getNgayNhan())
                    .ngaySuDung(vsh.getNgaySuDung())
                    .khaDung(khaDung)
                    .trangThaiHienThi(trangThaiHienThi)
                    .trangThaiLabel(trangThaiLabel)
                    .trangThaiBadgeClass(trangThaiBadgeClass)
                    .dieuKienApDung(taoMoTaDieuKien(m))
                    .hanSuDungFormatted(m.getNgayKetThuc() != null ? m.getNgayKetThuc().format(DATE_TIME_FORMATTER) : "Không giới hạn")
                    .build());
        }

        return ketQua;
    }

    /**
     * Lấy danh sách Voucher khả dụng trên sàn để Khách hàng xem và bấm "Thu thập".
     */
    @Transactional(readOnly = true)
    public List<VoucherKhachHangDto> layDanhSachVoucherSanCoChoKhachHang(Long khachHangId) {
        LocalDateTime now = LocalDateTime.now();
        List<MaGiamGia> danhSachVoucher = maGiamGiaRepository.layDanhSachVoucherKhaDungChoKhachHang(now);
        List<VoucherKhachHangDto> ketQua = new ArrayList<>();

        // Tập hợp ID các voucher mà khách hàng đã lưu trong ví và chưa dùng
        Set<Long> setVoucherDaLuu = new HashSet<>();
        if (khachHangId != null) {
            List<VoucherSoHuu> viCuaKhach = voucherSoHuuRepository.layDanhSachChuaSuDung(khachHangId);
            for (VoucherSoHuu v : viCuaKhach) {
                setVoucherDaLuu.add(v.getMaGiamGiaId());
            }
        }

        for (MaGiamGia m : danhSachVoucher) {
            boolean daLuu = setVoucherDaLuu.contains(m.getId());
            boolean hetLuot = m.isHetLuot() || m.isHetNganSach();

            String badgeClass = daLuu
                    ? "bg-blue-50 text-blue-700 border border-blue-200"
                    : (hetLuot ? "bg-rose-50 text-rose-600 border border-rose-200" : "bg-emerald-50 text-emerald-700 border border-emerald-200");

            String label = daLuu ? "Đã lưu trong ví" : (hetLuot ? "Hết lượt" : "Sẵn sàng thu thập");

            ketQua.add(VoucherKhachHangDto.builder()
                    .id(m.getId())
                    .voucherId(m.getId())
                    .maCode(m.getMaCode())
                    .tenVoucher(m.getTenVoucher())
                    .loaiGiamGia(m.getLoaiGiamGia())
                    .giaTriGiam(m.getGiaTriGiam())
                    .giamToiDa(m.getGiamToiDa())
                    .giaTriDonToiThieu(m.getGiaTriDonToiThieu())
                    .tongSoLuong(m.getTongSoLuong())
                    .soLuongDaDung(m.getSoLuongDaDung())
                    .gioiHanMoiUser(m.getGioiHanMoiUser())
                    .ngayBatDau(m.getNgayBatDau())
                    .ngayKetThuc(m.getNgayKetThuc())
                    .moTa(m.getMoTa())
                    .daThuThap(daLuu)
                    .daSuDung(false)
                    .khaDung(!hetLuot)
                    .trangThaiHienThi(daLuu ? "DA_THU_THAP" : (hetLuot ? "HET_LUOT" : "SAN_CO"))
                    .trangThaiLabel(label)
                    .trangThaiBadgeClass(badgeClass)
                    .dieuKienApDung(taoMoTaDieuKien(m))
                    .hanSuDungFormatted(m.getNgayKetThuc() != null ? m.getNgayKetThuc().format(DATE_TIME_FORMATTER) : "Không giới hạn")
                    .build());
        }

        return ketQua;
    }

    /**
     * Ghi nhận sử dụng voucher sau khi Đặt hàng thành công:
     * - Tăng số lượng đã dùng của chương trình mã giảm giá.
     * - Cộng dồn ngân sách đã giải ngân.
     * - Cập nhật trạng thái da_su_dung = true trong ví voucher_so_huu của khách hàng.
     */
    @Transactional
    public void ghiNhanSuDungVoucher(Long voucherId, Long khachHangId, BigDecimal tienGiam) {
        if (voucherId == null) return;

        maGiamGiaRepository.findById(voucherId).ifPresent(voucher -> {
            int slHienTai = (voucher.getSoLuongDaDung() != null) ? voucher.getSoLuongDaDung() : 0;
            voucher.setSoLuongDaDung(slHienTai + 1);

            if (tienGiam != null && tienGiam.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal nsHienTai = (voucher.getNganSachDaDung() != null) ? voucher.getNganSachDaDung() : BigDecimal.ZERO;
                voucher.setNganSachDaDung(nsHienTai.add(tienGiam));
            }
            maGiamGiaRepository.save(voucher);
            log.info("Cập nhật lượt dùng voucher [{}]: Lượt dùng mới={}, Ngân sách đã dùng={}",
                    voucher.getMaCode(), voucher.getSoLuongDaDung(), voucher.getNganSachDaDung());
        });

        if (khachHangId != null) {
            voucherSoHuuRepository.findFirstByMaKhachHangIdAndMaGiamGiaIdAndDaSuDungFalseOrderByNgayNhanAsc(khachHangId, voucherId)
                    .ifPresent(vsh -> {
                        vsh.setDaSuDung(true);
                        vsh.setNgaySuDung(LocalDateTime.now());
                        voucherSoHuuRepository.save(vsh);
                        log.info("Đánh dấu voucher_so_huu ID={} của khách hàng ID={} sang ĐÃ SỬ DỤNG", vsh.getId(), khachHangId);
                    });
        }
    }

    /**
     * Đếm tổng số voucher khả dụng trong ví của khách hàng.
     */
    @Transactional(readOnly = true)
    public long demSoVoucherKhaDung(Long khachHangId) {
        if (khachHangId == null) return 0;
        return voucherSoHuuRepository.layDanhSachChuaSuDung(khachHangId).stream()
                .filter(VoucherSoHuu::isKhaDung)
                .count();
    }

    private String taoMoTaDieuKien(MaGiamGia m) {
        StringBuilder sb = new StringBuilder();
        if ("PERCENT".equalsIgnoreCase(m.getLoaiGiamGia())) {
            sb.append("Giảm ").append(m.getGiaTriGiam().stripTrailingZeros().toPlainString()).append("%");
            if (m.getGiamToiDa() != null && m.getGiamToiDa().compareTo(BigDecimal.ZERO) > 0) {
                sb.append(String.format(" (tối đa %,d đ)", m.getGiamToiDa().longValue()));
            }
        } else {
            sb.append(String.format("Giảm trực tiếp %,d đ", m.getGiaTriGiam().longValue()));
        }

        if (m.getGiaTriDonToiThieu() != null && m.getGiaTriDonToiThieu().compareTo(BigDecimal.ZERO) > 0) {
            sb.append(String.format(" cho đơn từ %,d đ", m.getGiaTriDonToiThieu().longValue()));
        } else {
            sb.append(" cho mọi đơn hàng");
        }
        return sb.toString();
    }
}
