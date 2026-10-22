package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.CheckoutKetQua;
import vn.fpoly.simplain.dto.VoucherCheckoutItem;
import vn.fpoly.simplain.entity.DiaChi;
import vn.fpoly.simplain.entity.HoSoHoiVien;
import vn.fpoly.simplain.entity.PhuongThucThanhToan;
import vn.fpoly.simplain.repository.DiaChiRepository;
import vn.fpoly.simplain.repository.GioHangRepository;
import vn.fpoly.simplain.repository.HoSoHoiVienRepository;
import vn.fpoly.simplain.repository.PhuongThucThanhToanRepository;
import vn.fpoly.simplain.repository.VoucherCheckoutInfo;
import vn.fpoly.simplain.repository.VoucherCheckoutRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** Tính và xác nhận dữ liệu checkout cho US19; việc ghi đơn thuộc US21. */
@Service
public class CheckoutService {
    private final DiaChiRepository diaChiRepository;
    private final HoSoHoiVienRepository hoSoHoiVienRepository;
    private final VoucherCheckoutRepository voucherCheckoutRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final GioHangRepository gioHangRepository;
    private final GioHangService gioHangService;
    private final BigDecimal phiVanChuyen;

    public CheckoutService(DiaChiRepository diaChiRepository,
                           HoSoHoiVienRepository hoSoHoiVienRepository,
                           VoucherCheckoutRepository voucherCheckoutRepository,
                           PhuongThucThanhToanRepository phuongThucThanhToanRepository,
                           GioHangRepository gioHangRepository,
                           GioHangService gioHangService,
                           @Value("${simplain.checkout.shipping-fee:0}") BigDecimal phiVanChuyen) {
        this.diaChiRepository = diaChiRepository;
        this.hoSoHoiVienRepository = hoSoHoiVienRepository;
        this.voucherCheckoutRepository = voucherCheckoutRepository;
        this.phuongThucThanhToanRepository = phuongThucThanhToanRepository;
        this.gioHangRepository = gioHangRepository;
        this.gioHangService = gioHangService;
        this.phiVanChuyen = phiVanChuyen.max(BigDecimal.ZERO);
    }

    public List<DiaChi> layDiaChi(Long maKhachHangId) {
        return diaChiRepository.findByMaKhachHangIdOrderByLaMacDinhDescIdAsc(maKhachHangId);
    }

    public BigDecimal laySoDuCoolcash(Long maKhachHangId) {
        return hoSoHoiVienRepository.findById(maKhachHangId)
                .map(HoSoHoiVien::getSoDuCoolcash)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getPhiVanChuyen() {
        return phiVanChuyen;
    }

    public BigDecimal kiemTraMaGiamGia(Long maKhachHangId, String maCode) {
        var gio = gioHangRepository.findByMaKhachHangId(maKhachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giỏ hàng."));
        var chiTiet = gioHangService.layChiTiet(gio.getId());
        if (chiTiet.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống.");
        }
        Map<Long, vn.fpoly.simplain.repository.ThongTinSanPhamGioHang> sanPhamTheoBienThe =
                gioHangService.layThongTinSanPham(chiTiet);
        if (sanPhamTheoBienThe.size() != chiTiet.stream().map(d -> d.getMaBienTheId()).distinct().count()) {
            throw new IllegalArgumentException("Giỏ hàng có sản phẩm không còn tồn tại.");
        }
        BigDecimal tongTienHang = gioHangService.tinhTongTienHang(chiTiet, sanPhamTheoBienThe);
        return tinhTienGiamGia(maKhachHangId, maCode, tongTienHang);
    }

    public List<VoucherCheckoutItem> layVoucherChuaDung(Long maKhachHangId) {
        return voucherCheckoutRepository.layVoucherChuaDung(maKhachHangId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public CheckoutKetQua xacNhan(Long maKhachHangId, Long diaChiId, String maPhuongThuc,
                                  String maGiamGia, BigDecimal tienCoolcashDung) {
        if (maKhachHangId == null || diaChiId == null) {
            throw new IllegalArgumentException("Vui lòng chọn địa chỉ giao hàng.");
        }

        DiaChi diaChi = diaChiRepository.findByIdAndMaKhachHangId(diaChiId, maKhachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Địa chỉ không thuộc tài khoản khách hàng này."));
        PhuongThucThanhToan phuongThuc = phuongThucThanhToanRepository
                .findByMaPhuongThucAndKichHoatTrue(maPhuongThuc)
                .orElseThrow(() -> new IllegalArgumentException("Phương thức thanh toán không còn hoạt động."));

        var gio = gioHangRepository.findByMaKhachHangId(maKhachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giỏ hàng."));
        var chiTiet = gioHangService.layChiTiet(gio.getId());
        if (chiTiet.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống.");
        }

        Map<Long, vn.fpoly.simplain.repository.ThongTinSanPhamGioHang> sanPhamTheoBienThe =
                gioHangService.layThongTinSanPham(chiTiet);
        if (sanPhamTheoBienThe.size() != chiTiet.stream().map(d -> d.getMaBienTheId()).distinct().count()) {
            throw new IllegalArgumentException("Giỏ hàng có sản phẩm không còn tồn tại. Vui lòng cập nhật giỏ trước khi checkout.");
        }
        BigDecimal tongTienHang = gioHangService.tinhTongTienHang(chiTiet, sanPhamTheoBienThe);
        BigDecimal tienGiamGia = tinhTienGiamGia(maKhachHangId, maGiamGia, tongTienHang);

        BigDecimal diemDung = tienCoolcashDung == null ? BigDecimal.ZERO : tienCoolcashDung.max(BigDecimal.ZERO);
        BigDecimal soDu = laySoDuCoolcash(maKhachHangId);
        BigDecimal toiDaDuocDung = tongTienHang.subtract(tienGiamGia).max(BigDecimal.ZERO);
        diemDung = diemDung.min(soDu).min(toiDaDuocDung);

        BigDecimal tongThanhToan = tongTienHang.subtract(tienGiamGia).subtract(diemDung)
                .add(phiVanChuyen).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        int tongSoLuong = chiTiet.stream().mapToInt(d -> d.getSoLuong() == null ? 0 : d.getSoLuong()).sum();
        String codeDaApDung = maGiamGia == null || maGiamGia.isBlank() ? null : maGiamGia.trim().toUpperCase(Locale.ROOT);
        return new CheckoutKetQua(maKhachHangId, diaChi, phuongThuc, codeDaApDung,
                tongTienHang, tienGiamGia, diemDung, phiVanChuyen, tongThanhToan, tongSoLuong);
    }

    private BigDecimal tinhTienGiamGia(Long maKhachHangId, String maGiamGia, BigDecimal tongTienHang) {
        if (maGiamGia == null || maGiamGia.isBlank()) {
            return BigDecimal.ZERO;
        }
        VoucherCheckoutInfo voucher = voucherCheckoutRepository
                .timVoucherHopLe(maKhachHangId, maGiamGia.trim(), LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá không hợp lệ, đã hết hạn hoặc chưa được thu thập."));
        if (tongTienHang.compareTo(Objects.requireNonNullElse(voucher.giaTriDonToiThieu(), BigDecimal.ZERO)) < 0) {
            throw new IllegalArgumentException("Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã giảm giá.");
        }

        BigDecimal tienGiam;
        if ("PERCENT".equalsIgnoreCase(voucher.loaiGiamGia())) {
            tienGiam = tongTienHang.multiply(voucher.giaTriGiam())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (voucher.giamToiDa() != null) {
                tienGiam = tienGiam.min(voucher.giamToiDa());
            }
        } else {
            tienGiam = voucher.giaTriGiam();
        }
        return tienGiam.max(BigDecimal.ZERO).min(tongTienHang).setScale(2, RoundingMode.HALF_UP);
    }
}
