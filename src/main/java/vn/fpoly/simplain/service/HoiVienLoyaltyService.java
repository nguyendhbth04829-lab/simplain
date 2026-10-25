/**
 * Tác giả: Gia Minh
 * Chức năng: US16 - Dịch vụ xử lý nghiệp vụ Tự động tích điểm Loyalty (Coolcash) và Hạng thành viên
 */
package vn.fpoly.simplain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.BienDongDiemResponse;
import vn.fpoly.simplain.dto.HoSoHoiVienResponse;
import vn.fpoly.simplain.dto.SuDungDiemRequest;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service quản lý khách hàng thân thiết, tự động tích điểm Coolcash theo hạng và thăng hạng tự động.
 */
@Service
public class HoiVienLoyaltyService {

    private final HoSoHoiVienRepository hoSoHoiVienRepository;
    private final HangThanhVienRepository hangThanhVienRepository;
    private final BienDongDiemRepository bienDongDiemRepository;
    private final DonHangRepository donHangRepository;
    private final KhachHangService khachHangService;

    public HoiVienLoyaltyService(HoSoHoiVienRepository hoSoHoiVienRepository,
                                 HangThanhVienRepository hangThanhVienRepository,
                                 BienDongDiemRepository bienDongDiemRepository,
                                 DonHangRepository donHangRepository,
                                 KhachHangService khachHangService) {
        this.hoSoHoiVienRepository = hoSoHoiVienRepository;
        this.hangThanhVienRepository = hangThanhVienRepository;
        this.bienDongDiemRepository = bienDongDiemRepository;
        this.donHangRepository = donHangRepository;
        this.khachHangService = khachHangService;
    }

    /**
     * Lấy thông tin hồ sơ hội viên và quyền lợi của khách hàng hiện tại.
     *
     * @param idKhachHang ID khách hàng
     * @return DTO thông tin hội viên
     */
    @Transactional
    public HoSoHoiVienResponse layHoSoHoiVien(Long idKhachHang) {
        KhachHang khachHang = khachHangService.layKhachHangHopLe(idKhachHang);
        HoSoHoiVien hoSo = layHoacTaoHoSo(idKhachHang);

        HangThanhVien hangHienTai = hangThanhVienRepository.findById(hoSo.getMaHangHienTaiId())
                .orElseGet(this::layHangMacDinh);

        // Tìm hạng thành viên kế tiếp
        Optional<HangThanhVien> optNext = hangThanhVienRepository
                .findFirstByChiTieuNamToiThieuGreaterThanOrderByChiTieuNamToiThieuAsc(hoSo.getTongChiTieuNam());

        String tenHangNext = optNext.map(HangThanhVien::getTenHang).orElse("Hạng cao nhất");
        BigDecimal chiTieuCanThem = optNext
                .map(h -> h.getChiTieuNamToiThieu().subtract(hoSo.getTongChiTieuNam()).max(BigDecimal.ZERO))
                .orElse(BigDecimal.ZERO);

        return new HoSoHoiVienResponse(
                khachHang.getId(),
                khachHang.getHoTen(),
                hangHienTai.getId(),
                hangHienTai.getTenHang(),
                hangHienTai.getPhanTramHoanTien(),
                hoSo.getSoDuCoolcash(),
                hoSo.getTongChiTieuNam(),
                tenHangNext,
                chiTieuCanThem
        );
    }

    /**
     * Tự động cộng % điểm thưởng Coolcash và tự động nâng hạng khi đơn hàng hoàn thành.
     *
     * @param maDonHangId ID đơn hàng vừa hoàn tất
     * @return DTO lịch sử cộng điểm (hoặc null nếu đã tích điểm trước đó)
     */
    @Transactional
    public BienDongDiemResponse tichDiemDonHangHoanThanh(Long maDonHangId) {
        if (maDonHangId == null || maDonHangId <= 0) {
            throw new DuLieuKhongHopLeException("Mã đơn hàng không hợp lệ");
        }

        DonHang donHang = donHangRepository.findById(maDonHangId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy đơn hàng với ID: " + maDonHangId));

        if (donHang.getMaKhachHangId() == null) {
            return null; // Đơn hàng vãng lai không có tài khoản khách hàng
        }

        // Chống tích điểm trùng lặp cho cùng một đơn hàng
        if (bienDongDiemRepository.existsByMaDonHangIdAndLoaiBienDong(maDonHangId, "CONG_DIEM")) {
            return null;
        }

        HoSoHoiVien hoSo = layHoacTaoHoSo(donHang.getMaKhachHangId());
        HangThanhVien hang = hangThanhVienRepository.findById(hoSo.getMaHangHienTaiId())
                .orElseGet(this::layHangMacDinh);

        // Số điểm hoàn = Tổng thanh toán * (Phần trăm hoàn tiền / 100)
        BigDecimal phanTram = hang.getPhanTramHoanTien() != null ? hang.getPhanTramHoanTien() : BigDecimal.ONE;
        BigDecimal soDiemCong = donHang.getTongThanhToan()
                .multiply(phanTram)
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

        // Cập nhật số dư điểm và tổng chi tiêu trong năm
        hoSo.setSoDuCoolcash(hoSo.getSoDuCoolcash().add(soDiemCong));
        BigDecimal tongChiTieuMoi = hoSo.getTongChiTieuNam().add(donHang.getTongThanhToan());
        hoSo.setTongChiTieuNam(tongChiTieuMoi);

        // Tự động thăng hạng khi đạt mốc chi tiêu tối thiểu
        hangThanhVienRepository.findTopByChiTieuNamToiThieuLessThanEqualOrderByChiTieuNamToiThieuDesc(tongChiTieuMoi)
                .ifPresent(hangMoi -> hoSo.setMaHangHienTaiId(hangMoi.getId()));

        hoSoHoiVienRepository.save(hoSo);

        // Ghi lại lịch sử biến động điểm
        BienDongDiem bienDong = BienDongDiem.builder()
                .maKhachHangId(donHang.getMaKhachHangId())
                .maDonHangId(maDonHangId)
                .loaiBienDong("CONG_DIEM")
                .soDiem(soDiemCong)
                .noiDung("Tích điểm hoàn tiền " + phanTram + "% từ đơn hàng " + donHang.getMaDonHang())
                .ngayTao(LocalDateTime.now())
                .build();

        BienDongDiem daLuu = bienDongDiemRepository.save(bienDong);
        return BienDongDiemResponse.tuThucThe(daLuu);
    }

    /**
     * Sử dụng điểm Coolcash để trừ tiền thanh toán đơn hàng.
     *
     * @param idKhachHang ID khách hàng
     * @param yeuCau      DTO yêu cầu sử dụng điểm
     * @return DTO lịch sử trừ điểm
     */
    @Transactional
    public BienDongDiemResponse suDungDiemCoolcash(Long idKhachHang, SuDungDiemRequest yeuCau) {
        khachHangService.layKhachHangHopLe(idKhachHang);
        HoSoHoiVien hoSo = layHoacTaoHoSo(idKhachHang);

        if (hoSo.getSoDuCoolcash().compareTo(yeuCau.soDiem()) < 0) {
            throw new DuLieuKhongHopLeException("Số dư điểm Coolcash không đủ để thực hiện giao dịch (Số dư hiện có: " 
                    + hoSo.getSoDuCoolcash() + " điểm)");
        }

        hoSo.setSoDuCoolcash(hoSo.getSoDuCoolcash().subtract(yeuCau.soDiem()));
        hoSoHoiVienRepository.save(hoSo);

        BienDongDiem bienDong = BienDongDiem.builder()
                .maKhachHangId(idKhachHang)
                .maDonHangId(yeuCau.maDonHangId())
                .loaiBienDong("TRU_DIEM")
                .soDiem(yeuCau.soDiem())
                .noiDung("Sử dụng điểm Coolcash giảm trừ tiền thanh toán" + (yeuCau.maDonHangId() != null ? " đơn hàng #" + yeuCau.maDonHangId() : ""))
                .ngayTao(LocalDateTime.now())
                .build();

        BienDongDiem daLuu = bienDongDiemRepository.save(bienDong);
        return BienDongDiemResponse.tuThucThe(daLuu);
    }

    /**
     * Lấy lịch sử biến động điểm thưởng của khách hàng.
     */
    @Transactional(readOnly = true)
    public List<BienDongDiemResponse> layLichSuBienDongDiem(Long idKhachHang) {
        khachHangService.layKhachHangHopLe(idKhachHang);
        return bienDongDiemRepository.findAllByMaKhachHangIdOrderByNgayTaoDesc(idKhachHang)
                .stream()
                .map(BienDongDiemResponse::tuThucThe)
                .toList();
    }

    /**
     * Lấy danh sách tất cả các hạng thành viên và quyền lợi.
     */
    @Transactional(readOnly = true)
    public List<HangThanhVien> layDanhSachHangThanhVien() {
        List<HangThanhVien> ds = hangThanhVienRepository.findAllByOrderByChiTieuNamToiThieuAsc();
        if (ds.isEmpty()) {
            return khoiTaoHangMacDinh();
        }
        return ds;
    }

    /**
     * Lấy hồ sơ hội viên hiện tại hoặc tự động tạo mới nếu chưa có.
     */
    private HoSoHoiVien layHoacTaoHoSo(Long idKhachHang) {
        return hoSoHoiVienRepository.findById(idKhachHang).orElseGet(() -> {
            HangThanhVien hangMacDinh = layHangMacDinh();
            HoSoHoiVien hoSoMoi = HoSoHoiVien.builder()
                    .maKhachHangId(idKhachHang)
                    .maHangHienTaiId(hangMacDinh.getId())
                    .soDuCoolcash(BigDecimal.ZERO)
                    .tongChiTieuNam(BigDecimal.ZERO)
                    .build();
            return hoSoHoiVienRepository.save(hoSoMoi);
        });
    }

    /**
     * Lấy hạng mặc định thấp nhất, nếu chưa có thì khởi tạo các hạng chuẩn.
     */
    private HangThanhVien layHangMacDinh() {
        return hangThanhVienRepository.findTopByOrderByChiTieuNamToiThieuAsc()
                .orElseGet(() -> {
                    List<HangThanhVien> danhSach = khoiTaoHangMacDinh();
                    return danhSach.get(0);
                });
    }

    private List<HangThanhVien> khoiTaoHangMacDinh() {
        HangThanhVien h1 = HangThanhVien.builder().tenHang("Thành viên Mới").chiTieuNamToiThieu(BigDecimal.ZERO).phanTramHoanTien(new BigDecimal("1.00")).build();
        HangThanhVien h2 = HangThanhVien.builder().tenHang("Thành viên Bạc").chiTieuNamToiThieu(new BigDecimal("2000000")).phanTramHoanTien(new BigDecimal("3.00")).build();
        HangThanhVien h3 = HangThanhVien.builder().tenHang("Thành viên Vàng").chiTieuNamToiThieu(new BigDecimal("5000000")).phanTramHoanTien(new BigDecimal("5.00")).build();
        HangThanhVien h4 = HangThanhVien.builder().tenHang("Thành viên Kim Cương").chiTieuNamToiThieu(new BigDecimal("10000000")).phanTramHoanTien(new BigDecimal("8.00")).build();
        return hangThanhVienRepository.saveAll(List.of(h1, h2, h3, h4));
    }
}
