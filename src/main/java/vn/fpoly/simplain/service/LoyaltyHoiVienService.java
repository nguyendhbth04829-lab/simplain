package vn.fpoly.simplain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.HoiVienResponse;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Service quản lý Hội viên, Tự động tích điểm Loyalty (Coolcash) và Thăng hạng thành viên (US16).
 * Tiêu chí 1: Đơn hàng hoàn thành cộng % điểm (so_du_coolcash).
 * Tiêu chí 2: Đạt mốc tự động lên Hạng (hang_thanh_vien).
 */
@Slf4j
@Service
public class LoyaltyHoiVienService {

    public static final String LOAI_BIEN_DONG_CONG = "CONG_HOAN_THANH_DON";
    public static final Long ID_TRANG_THAI_HOAN_THANH = 4L;
    public static final String MA_TRANG_THAI_HOAN_THANH = "HOAN_THANH";

    private final HangThanhVienRepository hangThanhVienRepository;
    private final HoSoHoiVienRepository hoSoHoiVienRepository;
    private final BienDongDiemRepository bienDongDiemRepository;
    private final DonHangRepository donHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final KhachHangRepository khachHangRepository;

    @Autowired
    public LoyaltyHoiVienService(HangThanhVienRepository hangThanhVienRepository,
                                 HoSoHoiVienRepository hoSoHoiVienRepository,
                                 BienDongDiemRepository bienDongDiemRepository,
                                 DonHangRepository donHangRepository,
                                 TrangThaiDonHangRepository trangThaiDonHangRepository,
                                 KhachHangRepository khachHangRepository) {
        this.hangThanhVienRepository = hangThanhVienRepository;
        this.hoSoHoiVienRepository = hoSoHoiVienRepository;
        this.bienDongDiemRepository = bienDongDiemRepository;
        this.donHangRepository = donHangRepository;
        this.trangThaiDonHangRepository = trangThaiDonHangRepository;
        this.khachHangRepository = khachHangRepository;
    }

    /**
     * Khởi tạo dữ liệu các hạng thành viên mặc định nếu bảng đang trống.
     */
    @Transactional
    public void khoiTaoHangThanhVienMacDinh() {
        if (hangThanhVienRepository.count() == 0) {
            hangThanhVienRepository.save(HangThanhVien.builder()
                    .tenHang("Đồng")
                    .chiTieuNamToiThieu(BigDecimal.ZERO)
                    .phanTramHoanTien(new BigDecimal("1.00"))
                    .build());
            hangThanhVienRepository.save(HangThanhVien.builder()
                    .tenHang("Bạc")
                    .chiTieuNamToiThieu(new BigDecimal("1000000.00"))
                    .phanTramHoanTien(new BigDecimal("3.00"))
                    .build());
            hangThanhVienRepository.save(HangThanhVien.builder()
                    .tenHang("Vàng")
                    .chiTieuNamToiThieu(new BigDecimal("3000000.00"))
                    .phanTramHoanTien(new BigDecimal("5.00"))
                    .build());
            hangThanhVienRepository.save(HangThanhVien.builder()
                    .tenHang("Bạch Kim")
                    .chiTieuNamToiThieu(new BigDecimal("6000000.00"))
                    .phanTramHoanTien(new BigDecimal("8.00"))
                    .build());
        }
    }

    /**
     * Lấy hoặc tự động khởi tạo Hồ sơ hội viên cho khách hàng.
     */
    @Transactional
    public HoSoHoiVien layHoacTaoHoSoHoiVien(Long khachHangId) {
        khoiTaoHangThanhVienMacDinh();
        return hoSoHoiVienRepository.findByMaKhachHangId(khachHangId).orElseGet(() -> {
            HangThanhVien hangMacDinh = hangThanhVienRepository.findFirstByOrderByChiTieuNamToiThieuAsc()
                    .orElseGet(() -> hangThanhVienRepository.findAll().stream().findFirst()
                            .orElseThrow(() -> new IllegalStateException("Hệ thống chưa cấu hình Hạng thành viên")));

            HoSoHoiVien moi = HoSoHoiVien.builder()
                    .maKhachHangId(khachHangId)
                    .maHangHienTaiId(hangMacDinh.getId())
                    .soDuCoolcash(BigDecimal.ZERO)
                    .tongChiTieuNam(BigDecimal.ZERO)
                    .build();
            return hoSoHoiVienRepository.save(moi);
        });
    }

    /**
     * Xử lý Tự động tích điểm Loyalty (Coolcash) và Thăng hạng khi Đơn hàng hoàn thành (US16 - Tiêu chí 1 & 2).
     *
     * @param donHangId ID của đơn hàng vừa hoàn thành
     * @return Hồ sơ hội viên sau khi tích điểm và cập nhật hạng
     */
    @Transactional
    public HoSoHoiVien xuLyTichDiemVaThangHang(Long donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với mã ID: " + donHangId));

        if (donHang.getKhachHang() == null || donHang.getKhachHang().getId() == null) {
            // Đơn hàng vãng lai không có tài khoản khách hàng -> không tích điểm
            return null;
        }

        Long khachHangId = donHang.getKhachHang().getId();

        // Kiểm tra điều kiện trạng thái HOAN_THANH
        boolean daHoanThanh = false;
        if (donHang.getTrangThaiDonHang() != null) {
            Long maTtId = donHang.getTrangThaiDonHang().getId();
            String maTt = donHang.getTrangThaiDonHang().getMaTrangThai();
            if (ID_TRANG_THAI_HOAN_THANH.equals(maTtId) || MA_TRANG_THAI_HOAN_THANH.equalsIgnoreCase(maTt)) {
                daHoanThanh = true;
            }
        }

        if (!daHoanThanh) {
            throw new IllegalStateException("Đơn hàng chưa hoàn thành, chỉ tích điểm cho đơn hàng ở trạng thái HOÀN THÀNH!");
        }

        // Kiểm tra trùng lặp: Nếu đơn hàng này đã được tích điểm trước đó thì không cộng lại (Idempotent)
        if (bienDongDiemRepository.existsByMaDonHangIdAndLoaiBienDong(donHangId, LOAI_BIEN_DONG_CONG)) {
            return layHoacTaoHoSoHoiVien(khachHangId);
        }

        // Lấy hồ sơ hội viên và hạng hiện tại
        HoSoHoiVien hoSo = layHoacTaoHoSoHoiVien(khachHangId);
        HangThanhVien hangHienTai = hangThanhVienRepository.findById(hoSo.getMaHangHienTaiId())
                .orElseGet(() -> hangThanhVienRepository.findFirstByOrderByChiTieuNamToiThieuAsc().orElse(null));

        BigDecimal phanTramHoan = (hangHienTai != null && hangHienTai.getPhanTramHoanTien() != null)
                ? hangHienTai.getPhanTramHoanTien()
                : new BigDecimal("1.00");

        // Tiền thực tế thanh toán
        BigDecimal tongThanhToan = donHang.getTongThanhToan() != null ? donHang.getTongThanhToan() : BigDecimal.ZERO;

        // Tiêu chí 1: Tính số điểm tích luỹ = tongThanhToan * (phanTram / 100)
        BigDecimal soDiemCong = tongThanhToan
                .multiply(phanTramHoan)
                .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);

        // Cộng dồn điểm vào số dư Coolcash
        BigDecimal soDuMoi = (hoSo.getSoDuCoolcash() != null ? hoSo.getSoDuCoolcash() : BigDecimal.ZERO).add(soDiemCong);
        hoSo.setSoDuCoolcash(soDuMoi);

        // Cộng dồn chi tiêu năm
        BigDecimal tongChiTieuMoi = (hoSo.getTongChiTieuNam() != null ? hoSo.getTongChiTieuNam() : BigDecimal.ZERO).add(tongThanhToan);
        hoSo.setTongChiTieuNam(tongChiTieuMoi);

        // Tiêu chí 2: Tự động lên hạng khi đạt mốc chi tiêu (hang_thanh_vien)
        List<HangThanhVien> dsHangGiamDan = hangThanhVienRepository.findAllByOrderByChiTieuNamToiThieuDesc();
        for (HangThanhVien h : dsHangGiamDan) {
            if (tongChiTieuMoi.compareTo(h.getChiTieuNamToiThieu()) >= 0) {
                // Đạt mốc của hạng h
                hoSo.setMaHangHienTaiId(h.getId());
                break;
            }
        }

        // Lưu hồ sơ cập nhật
        HoSoHoiVien hoSoSaved = hoSoHoiVienRepository.save(hoSo);

        // Ghi nhận lịch sử biến động điểm
        BienDongDiem bienDong = BienDongDiem.builder()
                .maKhachHangId(khachHangId)
                .maDonHangId(donHangId)
                .loaiBienDong(LOAI_BIEN_DONG_CONG)
                .soDiem(soDiemCong)
                .noiDung("Tích lũy " + phanTramHoan + "% Coolcash từ đơn hàng #" + donHang.getMaDonHang() + " giao thành công")
                .ngayTao(LocalDateTime.now())
                .build();
        bienDongDiemRepository.save(bienDong);

        return hoSoSaved;
    }

    /**
     * Lấy thông tin Hội viên, Hạng và Tiến trình thăng hạng của khách hàng.
     */
    @Transactional(readOnly = true)
    public HoiVienResponse layThongTinHoiVien(Long khachHangId) {
        khoiTaoHangThanhVienMacDinh();
        HoSoHoiVien hoSo = hoSoHoiVienRepository.findByMaKhachHangId(khachHangId)
                .orElseGet(() -> {
                    HangThanhVien hangMacDinh = hangThanhVienRepository.findFirstByOrderByChiTieuNamToiThieuAsc().orElse(null);
                    return HoSoHoiVien.builder()
                            .maKhachHangId(khachHangId)
                            .maHangHienTaiId(hangMacDinh != null ? hangMacDinh.getId() : 1L)
                            .soDuCoolcash(BigDecimal.ZERO)
                            .tongChiTieuNam(BigDecimal.ZERO)
                            .build();
                });

        KhachHang kh = khachHangRepository.findById(khachHangId).orElse(null);
        HangThanhVien hangHienTai = hangThanhVienRepository.findById(hoSo.getMaHangHienTaiId())
                .orElseGet(() -> hangThanhVienRepository.findFirstByOrderByChiTieuNamToiThieuAsc().orElse(null));

        List<HangThanhVien> dsHangTangDan = hangThanhVienRepository.findAllByOrderByChiTieuNamToiThieuAsc();

        // Tìm hạng tiếp theo
        HangThanhVien hangTiepTheo = null;
        if (hangHienTai != null) {
            for (HangThanhVien h : dsHangTangDan) {
                if (h.getChiTieuNamToiThieu().compareTo(hangHienTai.getChiTieuNamToiThieu()) > 0) {
                    hangTiepTheo = h;
                    break;
                }
            }
        }

        BigDecimal chiTieuHienTai = hoSo.getTongChiTieuNam() != null ? hoSo.getTongChiTieuNam() : BigDecimal.ZERO;
        String tenHangTiep = "Đã đạt Hạng cao nhất (VIP)";
        BigDecimal chiTieuMocTiep = chiTieuHienTai;
        BigDecimal chiTieuCanThem = BigDecimal.ZERO;
        int phanTramTienTrinh = 100;

        if (hangTiepTheo != null) {
            tenHangTiep = hangTiepTheo.getTenHang();
            chiTieuMocTiep = hangTiepTheo.getChiTieuNamToiThieu();
            chiTieuCanThem = chiTieuMocTiep.subtract(chiTieuHienTai).max(BigDecimal.ZERO);

            BigDecimal mocHienTai = (hangHienTai != null && hangHienTai.getChiTieuNamToiThieu() != null)
                    ? hangHienTai.getChiTieuNamToiThieu()
                    : BigDecimal.ZERO;

            BigDecimal khoangCach = chiTieuMocTiep.subtract(mocHienTai);
            BigDecimal daDat = chiTieuHienTai.subtract(mocHienTai).max(BigDecimal.ZERO);

            if (khoangCach.compareTo(BigDecimal.ZERO) > 0) {
                phanTramTienTrinh = daDat.multiply(new BigDecimal("100"))
                        .divide(khoangCach, 0, RoundingMode.DOWN)
                        .min(new BigDecimal("100"))
                        .intValue();
            }
        }

        List<BienDongDiem> dsBienDong = bienDongDiemRepository.findByMaKhachHangIdOrderByNgayTaoDesc(khachHangId);

        return HoiVienResponse.builder()
                .maKhachHangId(khachHangId)
                .maKhachHang(kh != null ? kh.getMaKhachHang() : "")
                .hoTen(kh != null ? kh.getHoTen() : "")
                .maHangHienTaiId(hangHienTai != null ? hangHienTai.getId() : 1L)
                .tenHang(hangHienTai != null ? hangHienTai.getTenHang() : "Đồng")
                .phanTramHoanTien(hangHienTai != null ? hangHienTai.getPhanTramHoanTien() : new BigDecimal("1.00"))
                .chiTieuToiThieuHangHienTai(hangHienTai != null ? hangHienTai.getChiTieuNamToiThieu() : BigDecimal.ZERO)
                .soDuCoolcash(hoSo.getSoDuCoolcash() != null ? hoSo.getSoDuCoolcash() : BigDecimal.ZERO)
                .tongChiTieuNam(chiTieuHienTai)
                .tenHangTiepTheo(tenHangTiep)
                .chiTieuMocTiepTheo(chiTieuMocTiep)
                .chiTieuCanThemDeLenHang(chiTieuCanThem)
                .phanTramTienTrinh(phanTramTienTrinh)
                .danhSachBienDong(dsBienDong != null ? dsBienDong : Collections.emptyList())
                .build();
    }

    /**
     * Cập nhật trạng thái đơn hàng sang HOAN_THANH và tự động kích hoạt tiến trình tích điểm + thăng hạng.
     */
    @Transactional
    public HoSoHoiVien hoanThanhDonHangVaTichDiem(Long donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + donHangId));

        TrangThaiDonHang ttHoanThanh = trangThaiDonHangRepository.findById(ID_TRANG_THAI_HOAN_THANH)
                .orElseGet(() -> trangThaiDonHangRepository.findAll().stream()
                        .filter(t -> MA_TRANG_THAI_HOAN_THANH.equalsIgnoreCase(t.getMaTrangThai()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Chưa cấu hình trạng thái HOAN_THANH")));

        donHang.setTrangThaiDonHang(ttHoanThanh);
        donHangRepository.save(donHang);

        return xuLyTichDiemVaThangHang(donHangId);
    }

    /**
     * Trừ điểm Coolcash khi khách hàng sử dụng để thanh toán đơn hàng (US19).
     */
    @Transactional
    public void truDiemThanhToan(Long khachHangId, Long donHangId, String maDonHang, BigDecimal soDiemTru) {
        if (khachHangId == null || soDiemTru == null || soDiemTru.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        HoSoHoiVien hoSo = layHoacTaoHoSoHoiVien(khachHangId);
        BigDecimal soDuHienCo = hoSo.getSoDuCoolcash() != null ? hoSo.getSoDuCoolcash() : BigDecimal.ZERO;

        if (soDiemTru.compareTo(soDuHienCo) > 0) {
            throw new IllegalArgumentException("Số điểm Coolcash yêu cầu (" + soDiemTru + ") vượt quá số dư hiện có (" + soDuHienCo + ")!");
        }

        hoSo.setSoDuCoolcash(soDuHienCo.subtract(soDiemTru));
        hoSoHoiVienRepository.save(hoSo);

        BienDongDiem bienDong = BienDongDiem.builder()
                .maKhachHangId(khachHangId)
                .maDonHangId(donHangId)
                .loaiBienDong("TRU_THANH_TOAN_DON")
                .soDiem(soDiemTru.negate())
                .noiDung("Sử dụng " + soDiemTru.stripTrailingZeros().toPlainString() + " Coolcash thanh toán đơn hàng #" + maDonHang)
                .ngayTao(LocalDateTime.now())
                .build();
        bienDongDiemRepository.save(bienDong);
    }

    /**
     * Hoàn lại điểm Coolcash khi đơn hàng bị hủy (US23).
     */
    @Transactional
    public void hoanDiemKhiHuyDon(Long khachHangId, Long donHangId, String maDonHang, BigDecimal soDiemHoan) {
        if (khachHangId == null || soDiemHoan == null || soDiemHoan.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        HoSoHoiVien hoSo = layHoacTaoHoSoHoiVien(khachHangId);
        BigDecimal soDuHienCo = hoSo.getSoDuCoolcash() != null ? hoSo.getSoDuCoolcash() : BigDecimal.ZERO;
        hoSo.setSoDuCoolcash(soDuHienCo.add(soDiemHoan));
        hoSoHoiVienRepository.save(hoSo);

        BienDongDiem bienDong = BienDongDiem.builder()
                .maKhachHangId(khachHangId)
                .maDonHangId(donHangId)
                .loaiBienDong("HOAN_DIEM_HUY_DON")
                .soDiem(soDiemHoan)
                .noiDung("Hoàn lại " + soDiemHoan.stripTrailingZeros().toPlainString() + " Coolcash từ đơn hàng bị hủy #" + maDonHang)
                .ngayTao(LocalDateTime.now())
                .build();
        bienDongDiemRepository.save(bienDong);
        log.info("Loyalty US23: Đã hoàn lại {} điểm Coolcash cho khách #{} từ đơn hủy #{}", soDiemHoan, khachHangId, maDonHang);
    }
}
