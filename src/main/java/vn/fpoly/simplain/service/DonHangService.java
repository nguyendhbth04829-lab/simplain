package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.*;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service xử lý nghiệp vụ Đơn hàng & Checkout thanh toán (US19).
 * - Tiêu chí 1: Tính Tổng tiền = Hàng - Voucher - Điểm + Ship.
 * - Tiêu chí 2: Xác nhận địa chỉ và phương thức thanh toán.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DonHangService {

    public static final BigDecimal PHI_SHIP_CHUAN = new BigDecimal("30000");
    public static final BigDecimal MOC_FREESHIP = new BigDecimal("300000");

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final KhachHangRepository khachHangRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final GioHangService gioHangService;
    private final LoyaltyHoiVienService loyaltyHoiVienService;
    private final TaoDonHangService taoDonHangService;
    private final HinhAnhRepository hinhAnhRepository;
    private final LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;
    private final QuanLyVanDonService quanLyVanDonService;
    private final SanPhamRepository sanPhamRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final VoucherKhachHangService voucherKhachHangService;
    private final ChiTietFlashSaleRepository chiTietFlashSaleRepository;

    /**
     * Tính toán chi phí đơn hàng động: Tổng = Hàng - Voucher - Điểm + Ship (US19 - Tiêu chí 1 & US27).
     */
    public CheckoutTinhTienResponse tinhToanChiPhi(BigDecimal tongTienHang, String maVoucher, BigDecimal soDiemCoolcash, Long khachHangId) {
        if (tongTienHang == null || tongTienHang.compareTo(BigDecimal.ZERO) < 0) {
            tongTienHang = BigDecimal.ZERO;
        }

        // 1. Phí vận chuyển: 30.000đ, Miễn phí vận chuyển khi tiền hàng >= 300.000đ
        BigDecimal phiVanChuyen = (tongTienHang.compareTo(MOC_FREESHIP) >= 0) ? BigDecimal.ZERO : PHI_SHIP_CHUAN;

        // 2. Tính tiền Voucher giảm giá với hệ thống Voucher thật (US26 & US27)
        BigDecimal tienGiamGia = BigDecimal.ZERO;
        String thongBaoVoucher = "";
        Long maGiamGiaId = null;
        BigDecimal soTienConThieu = null;
        boolean hopLe = false;
        String maChuan = (maVoucher != null) ? maVoucher.trim().toUpperCase() : "";

        if (!maChuan.isEmpty()) {
            ValidateVoucherResultDto vResult = voucherKhachHangService.kiemTraVaApDungVoucher(maChuan, tongTienHang, khachHangId);
            if (vResult.getVoucherId() != null) {
                // Áp dụng voucher từ cơ sở dữ liệu (US26 / US27)
                hopLe = vResult.isHopLe();
                tienGiamGia = vResult.getTienGiam();
                thongBaoVoucher = vResult.getThongBao();
                maGiamGiaId = vResult.getVoucherId();
                soTienConThieu = vResult.getSoTienConThieu();
            } else {
                // Hỗ trợ mã dự phòng cũ (Legacy demo codes)
                switch (maChuan) {
                    case "SIMPLAIN10":
                        tienGiamGia = tongTienHang.multiply(new BigDecimal("0.10")).setScale(0, RoundingMode.HALF_UP);
                        if (tienGiamGia.compareTo(new BigDecimal("100000")) > 0) {
                            tienGiamGia = new BigDecimal("100000");
                        }
                        thongBaoVoucher = "Áp dụng thành công mã SIMPLAIN10 (Giảm 10%)";
                        hopLe = true;
                        break;

                    case "FREESHIP":
                        tienGiamGia = phiVanChuyen;
                        phiVanChuyen = BigDecimal.ZERO;
                        thongBaoVoucher = "Áp dụng thành công mã FREESHIP (Miễn phí vận chuyển)";
                        hopLe = true;
                        break;

                    case "SIMPLAIN50K":
                        if (tongTienHang.compareTo(new BigDecimal("200000")) >= 0) {
                            tienGiamGia = new BigDecimal("50000");
                            thongBaoVoucher = "Áp dụng thành công mã SIMPLAIN50K (Giảm 50.000đ)";
                            hopLe = true;
                        } else {
                            soTienConThieu = new BigDecimal("200000").subtract(tongTienHang);
                            thongBaoVoucher = "Mã SIMPLAIN50K chỉ áp dụng cho đơn hàng từ 200.000đ!";
                        }
                        break;

                    case "WELCOME20":
                        if (tongTienHang.compareTo(new BigDecimal("100000")) >= 0) {
                            tienGiamGia = new BigDecimal("20000");
                            thongBaoVoucher = "Áp dụng thành công mã WELCOME20 (Giảm 20.000đ)";
                            hopLe = true;
                        } else {
                            soTienConThieu = new BigDecimal("100000").subtract(tongTienHang);
                            thongBaoVoucher = "Mã WELCOME20 chỉ áp dụng cho đơn hàng từ 100.000đ!";
                        }
                        break;

                    default:
                        thongBaoVoucher = vResult.getThongBao();
                        hopLe = false;
                        break;
                }
            }
        }

        // Không cho giảm vượt quá tổng tiền hàng
        if (tienGiamGia.compareTo(tongTienHang) > 0) {
            tienGiamGia = tongTienHang;
        }

        // 3. Tính tiền điểm Coolcash sử dụng (1 điểm = 1 VNĐ)
        BigDecimal tienCoolcashDung = BigDecimal.ZERO;
        BigDecimal soDiemYeuCau = (soDiemCoolcash != null && soDiemCoolcash.compareTo(BigDecimal.ZERO) > 0) ? soDiemCoolcash : BigDecimal.ZERO;

        if (soDiemYeuCau.compareTo(BigDecimal.ZERO) > 0 && khachHangId != null) {
            try {
                HoiVienResponse hoiVien = loyaltyHoiVienService.layThongTinHoiVien(khachHangId);
                BigDecimal soDuHienCo = (hoiVien != null && hoiVien.getSoDuCoolcash() != null)
                        ? hoiVien.getSoDuCoolcash()
                        : BigDecimal.ZERO;

                // Điểm dùng tối đa không quá số dư hiện có
                BigDecimal diemKhaDung = soDiemYeuCau.min(soDuHienCo);

                // Điểm dùng tối đa không vượt quá số tiền hàng còn lại sau khi trừ voucher
                BigDecimal tienHangSauVoucher = tongTienHang.subtract(tienGiamGia);
                diemKhaDung = diemKhaDung.min(tienHangSauVoucher);

                tienCoolcashDung = diemKhaDung.max(BigDecimal.ZERO);
            } catch (Exception e) {
                log.warn("Không thể lấy số dư Coolcash của khách hàng {}: {}", khachHangId, e.getMessage());
            }
        }

        // 4. CÔNG THỨC AC 1: Tổng thanh toán = Hàng - Voucher - Điểm + Ship
        BigDecimal tongThanhToan = tongTienHang
                .subtract(tienGiamGia)
                .subtract(tienCoolcashDung)
                .add(phiVanChuyen);

        if (tongThanhToan.compareTo(BigDecimal.ZERO) < 0) {
            tongThanhToan = BigDecimal.ZERO;
        }

        return CheckoutTinhTienResponse.builder()
                .hopLe(hopLe)
                .thongBao(thongBaoVoucher)
                .tongTienHang(tongTienHang)
                .tienGiamGia(tienGiamGia)
                .tienCoolcashDung(tienCoolcashDung)
                .phiVanChuyen(phiVanChuyen)
                .tongThanhToan(tongThanhToan)
                .maVoucher(maChuan)
                .maGiamGiaId(maGiamGiaId)
                .soTienConThieu(soTienConThieu)
                .soDiemCoolcash(tienCoolcashDung)
                .build();
    }

    /**
     * Tiến hành Checkout đặt hàng từ Giỏ hàng (US19).
     * - Tiêu chí 1: Tính Tổng tiền = Hàng - Voucher - Điểm + Ship.
     * - Tiêu chí 2: Xác nhận địa chỉ và phương thức thanh toán.
     */
    @Transactional
    public DonHang taoDonHangTuGioHang(Long khachHangId, Long cartIdVangLai, DatHangRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Dữ liệu đặt hàng không hợp lệ!");
        }

        // 1. Kiểm tra giỏ hàng
        GioHangResponse gioHang = gioHangService.layThongTinGioHang(khachHangId, cartIdVangLai);
        if (gioHang == null || Boolean.TRUE.equals(gioHang.getGioHangRong())) {
            throw new IllegalArgumentException("Giỏ hàng của bạn đang trống! Vui lòng chọn sản phẩm vào giỏ trước khi thanh toán.");
        }

        // 2. Validate thông tin địa chỉ người nhận (Tiêu chí 2)
        if (req.getTenNguoiNhan() == null || req.getTenNguoiNhan().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập họ và tên người nhận hàng!");
        }
        if (req.getSoDienThoaiNhan() == null || req.getSoDienThoaiNhan().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập số điện thoại người nhận hàng!");
        }

        // Tự động ghép địa chỉ chuẩn nếu người dùng chọn Tỉnh / Huyện / Xã
        if ((req.getDiaChiGiaoHang() == null || req.getDiaChiGiaoHang().trim().isEmpty())
                && req.getDiaChiChiTiet() != null && !req.getDiaChiChiTiet().isBlank()) {
            StringBuilder sb = new StringBuilder(req.getDiaChiChiTiet().trim());
            if (req.getPhuongXa() != null && !req.getPhuongXa().isBlank()) sb.append(", ").append(req.getPhuongXa().trim());
            if (req.getQuanHuyen() != null && !req.getQuanHuyen().isBlank()) sb.append(", ").append(req.getQuanHuyen().trim());
            if (req.getTinhThanh() != null && !req.getTinhThanh().isBlank()) sb.append(", ").append(req.getTinhThanh().trim());
            req.setDiaChiGiaoHang(sb.toString());
        }

        if (req.getDiaChiGiaoHang() == null || req.getDiaChiGiaoHang().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn Tỉnh/Thành, Quận/Huyện, Phường/Xã và nhập địa chỉ nhận hàng cụ thể!");
        }

        // 3. Validate Phương thức thanh toán (Tiêu chí 2)
        if (req.getPhuongThucThanhToanId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn phương thức thanh toán cho đơn hàng!");
        }
        PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(req.getPhuongThucThanhToanId())
                .orElseThrow(() -> new IllegalArgumentException("Phương thức thanh toán không tồn tại trên hệ thống!"));

        if (!Boolean.TRUE.equals(pttt.getKichHoat())) {
            throw new IllegalArgumentException("Phương thức thanh toán [" + pttt.getTenPhuongThuc() + "] hiện đang tạm tắt. Vui lòng chọn cổng thanh toán khác!");
        }

        // 4. Chuyển đổi giỏ hàng sang danh sách chi tiết đơn hàng (Mod 4 -> Mod 5)
        List<ChiTietDonHangItemRequest> dsChiTiet = new ArrayList<>();
        if (gioHang.getDanhSachItemLe() != null) {
            for (ItemGioHangDto item : gioHang.getDanhSachItemLe()) {
                dsChiTiet.add(ChiTietDonHangItemRequest.builder()
                        .maBienTheId(item.getMaBienTheId())
                        .soLuong(item.getSoLuong())
                        .donGia(item.getDonGia())
                        .thanhTien(item.getThanhTien())
                        .tenSanPham(item.getTenSanPham() + " (" + item.getTenMau() + " / " + item.getTenKichThuoc() + ")")
                        .maSku(item.getMaSku())
                        .build());
            }
        }
        if (gioHang.getDanhSachBocCombo() != null) {
            for (BocComboDto combo : gioHang.getDanhSachBocCombo()) {
                if (combo.getDanhSachMonCon() != null) {
                    for (ItemGioHangDto monCon : combo.getDanhSachMonCon()) {
                        dsChiTiet.add(ChiTietDonHangItemRequest.builder()
                                .maBienTheId(monCon.getMaBienTheId())
                                .soLuong(monCon.getSoLuong())
                                .donGia(combo.getGiaCombo())
                                .thanhTien(monCon.getThanhTien())
                                .tenSanPham("[Combo: " + combo.getTenCombo() + "] " + monCon.getTenSanPham() + " (" + monCon.getTenMau() + " / " + monCon.getTenKichThuoc() + ")")
                                .maSku(monCon.getMaSku())
                                .maComboId(combo.getMaComboId())
                                .nhomComboToken(combo.getNhomComboToken())
                                .build());
                    }
                }
            }
        }

        // 5. Tính toán chi phí chính xác theo AC 1
        CheckoutTinhTienResponse chiPhi = tinhToanChiPhi(
                gioHang.getTongTienHang(),
                req.getMaVoucher(),
                req.getSoDiemCoolcash(),
                khachHangId
        );

        // 6. Đóng gói dữ liệu từ Dev 4 gửi sang Dev 5 (US21 - Tiêu chí 1)
        TaoDonHangMod4Request mod4Req = TaoDonHangMod4Request.builder()
                .maKhachHangId(khachHangId)
                .maGiamGiaId(chiPhi.getMaGiamGiaId())
                .tenNguoiNhan(req.getTenNguoiNhan().trim())
                .soDienThoaiNhan(req.getSoDienThoaiNhan().trim())
                .diaChiGiaoHang(req.getDiaChiGiaoHang().trim())
                .ghiChu(req.getGhiChu() != null ? req.getGhiChu().trim() : null)
                .maPhuongThucId(req.getPhuongThucThanhToanId())
                .maTrangThai("CHO_XAC_NHAN")
                .tongTienHang(chiPhi.getTongTienHang())
                .tienGiamGia(chiPhi.getTienGiamGia())
                .tienCoolcashDung(chiPhi.getTienCoolcashDung())
                .phiVanChuyen(chiPhi.getPhiVanChuyen())
                .tongThanhToan(chiPhi.getTongThanhToan())
                .danhSachChiTiet(dsChiTiet)
                .build();

        // 7. Gọi Mod 5 để Insert don_hang, chi_tiet_don_hang và Giảm tồn kho thật (US21 - Tiêu chí 1 & 2)
        TaoDonHangResponse mod5Res = taoDonHangService.taoDonHangVaTruTonKho(mod4Req);

        DonHang donHangSaved = donHangRepository.findById(mod5Res.getDonHangId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy đơn hàng vừa tạo!"));

        // 8. Trừ điểm Coolcash của hội viên (nếu có sử dụng điểm)
        if (chiPhi.getTienCoolcashDung() != null && chiPhi.getTienCoolcashDung().compareTo(BigDecimal.ZERO) > 0 && khachHangId != null) {
            loyaltyHoiVienService.truDiemThanhToan(khachHangId, donHangSaved.getId(), donHangSaved.getMaDonHang(), chiPhi.getTienCoolcashDung());
        }

        // 9. Cập nhật lượt sử dụng Voucher và ngân sách chương trình (US27)
        if (chiPhi.getMaGiamGiaId() != null && chiPhi.getTienGiamGia() != null && chiPhi.getTienGiamGia().compareTo(BigDecimal.ZERO) > 0) {
            voucherKhachHangService.ghiNhanSuDungVoucher(chiPhi.getMaGiamGiaId(), khachHangId, chiPhi.getTienGiamGia());
        }

        // 9.5. Cập nhật số lượng đã bán cho sản phẩm Flash Sale (US29)
        if (gioHang.getDanhSachItemLe() != null && chiTietFlashSaleRepository != null) {
            for (ItemGioHangDto item : gioHang.getDanhSachItemLe()) {
                if (Boolean.TRUE.equals(item.getLaFlashSale())) {
                    try {
                        List<ChiTietFlashSale> dsFs = chiTietFlashSaleRepository.timChiTietFlashSaleDangApDung(item.getMaBienTheId(), LocalDateTime.now());
                        if (!dsFs.isEmpty()) {
                            ChiTietFlashSale ct = dsFs.get(0);
                            int daBan = ct.getSoLuongDaBan() != null ? ct.getSoLuongDaBan() : 0;
                            ct.setSoLuongDaBan(daBan + (item.getSoLuong() != null ? item.getSoLuong() : 1));
                            chiTietFlashSaleRepository.save(ct);
                            log.info("Đã cập nhật số lượng đã bán Flash Sale cho biến thể ID [{}]: {} -> {}", item.getMaBienTheId(), daBan, ct.getSoLuongDaBan());
                        }
                    } catch (Exception ex) {
                        log.warn("Không thể cập nhật số lượng đã bán Flash Sale cho biến thể ID [{}]: {}", item.getMaBienTheId(), ex.getMessage());
                    }
                }
            }
        }

        // 10. Xóa sạch giỏ hàng sau khi đặt thành công
        gioHangService.xoaHetGioHang(khachHangId, cartIdVangLai);

        log.info("Đặt hàng thành công qua Mod 5! Mã đơn: {}, Khách hàng: {}, Tổng thanh toán: {}đ",
                donHangSaved.getMaDonHang(), req.getTenNguoiNhan(), donHangSaved.getTongThanhToan());

        return donHangSaved;
    }

    /**
     * Lấy thông tin đơn hàng theo mã đơn để hiển thị biên nhận thanh toán thành công.
     */
    @Transactional(readOnly = true)
    public DonHang layTheoMaDonHang(String maDonHang) {
        if (maDonHang == null || maDonHang.isBlank()) {
            throw new IllegalArgumentException("Mã đơn hàng không hợp lệ!");
        }
        return donHangRepository.findByMaDonHang(maDonHang.trim())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với mã: " + maDonHang));
    }

    /**
     * Lấy danh sách chi tiết mặt hàng của đơn hàng.
     */
    @Transactional(readOnly = true)
    public List<ChiTietDonHang> layChiTietTheoDonHangId(Long donHangId) {
        if (donHangId == null) {
            return Collections.emptyList();
        }
        return chiTietDonHangRepository.findByMaDonHangId(donHangId);
    }

    // =========================================================================
    // US23: THEO DÕI LỊCH SỬ ĐƠN HÀNG CÁ NHÂN (KHÁCH HÀNG)
    // =========================================================================

    /**
     * Chuyển đổi đối tượng Entity DonHang sang DonHangKhachHangDto kèm đầy đủ chi tiết ảnh sản phẩm và lịch sử vận đơn (US23).
     */
    public DonHangKhachHangDto chuyenDoiDonHangSangDto(DonHang dh) {
        if (dh == null) return null;

        List<ChiTietDonHang> items = chiTietDonHangRepository.findByMaDonHangId(dh.getId());
        List<DonHangItemDto> itemDtos = new ArrayList<>();
        int tongSoLuong = 0;
        String hinhAnhDauTien = null;

        for (ChiTietDonHang it : items) {
            String imgUrl = null;
            String tenMau = "";
            String tenKichThuoc = "";
            Long sanPhamId = null;
            String slug = null;

            if (it.getMaBienTheId() != null) {
                // Lấy ảnh biến thể
                List<HinhAnh> dsAnhBt = hinhAnhRepository.findByMaBienTheIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(it.getMaBienTheId());
                if (!dsAnhBt.isEmpty()) {
                    imgUrl = dsAnhBt.get(0).getDuongDanAnh();
                }

                BienTheSanPham bt = bienTheSanPhamRepository.findById(it.getMaBienTheId()).orElse(null);
                if (bt != null) {
                    if (bt.getMaMauSacId() != null) {
                        MauSac ms = mauSacRepository.findById(bt.getMaMauSacId()).orElse(null);
                        if (ms != null) tenMau = ms.getTenMau();
                    }
                    if (bt.getMaKichThuocId() != null) {
                        KichThuoc kt = kichThuocRepository.findById(bt.getMaKichThuocId()).orElse(null);
                        if (kt != null) tenKichThuoc = kt.getTenKichThuoc();
                    }
                    if (bt.getMaSanPhamId() != null) {
                        sanPhamId = bt.getMaSanPhamId();
                        SanPham sp = sanPhamRepository.findById(sanPhamId).orElse(null);
                        if (sp != null) slug = sp.getDuongDanSlug();
                        if (imgUrl == null) {
                            List<HinhAnh> dsAnhSp = hinhAnhRepository.findByMaSanPhamIdOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamId);
                            if (!dsAnhSp.isEmpty()) {
                                imgUrl = dsAnhSp.get(0).getDuongDanAnh();
                            }
                        }
                    }
                }
            }

            if (hinhAnhDauTien == null && imgUrl != null) {
                hinhAnhDauTien = imgUrl;
            }

            int sl = (it.getSoLuong() != null) ? it.getSoLuong() : 1;
            tongSoLuong += sl;

            itemDtos.add(DonHangItemDto.builder()
                    .id(it.getId())
                    .maBienTheId(it.getMaBienTheId())
                    .maSanPhamId(sanPhamId)
                    .tenSanPham(it.getTenSanPham())
                    .maSku(it.getMaSku())
                    .donGia(it.getDonGia())
                    .soLuong(sl)
                    .thanhTien(it.getThanhTien())
                    .hinhAnhUrl(imgUrl)
                    .tenMau(tenMau)
                    .tenKichThuoc(tenKichThuoc)
                    .slug(slug)
                    .build());
        }

        String maTt = (dh.getTrangThaiDonHang() != null) ? dh.getTrangThaiDonHang().getMaTrangThai() : "CHO_XAC_NHAN";
        boolean coTheHuy = "CHO_XAC_NHAN".equalsIgnoreCase(maTt);

        int stepperIndex = 1;
        String moTa = "Đơn hàng đang chờ nhân viên xác nhận";
        if ("CHO_XAC_NHAN".equalsIgnoreCase(maTt)) {
            stepperIndex = 1;
            moTa = "Đơn hàng đã được ghi nhận vào hệ thống và đang chờ duyệt";
        } else if ("DANG_XU_LY".equalsIgnoreCase(maTt)) {
            stepperIndex = 2;
            moTa = "Kho Simplain đang đóng gói kiện hàng chuẩn bị gửi vận chuyển";
        } else if ("DANG_GIAO".equalsIgnoreCase(maTt)) {
            stepperIndex = 3;
            moTa = "Kiện hàng đang trên đường vận chuyển giao tận nơi đến bạn";
        } else if ("HOAN_THANH".equalsIgnoreCase(maTt)) {
            stepperIndex = 4;
            moTa = "Đơn hàng đã được giao thành công";
        } else if ("DA_HUY".equalsIgnoreCase(maTt)) {
            stepperIndex = -1;
            moTa = "Đơn hàng đã hủy";
        } else if ("DANG_DOI_TRA".equalsIgnoreCase(maTt)) {
            stepperIndex = 5;
            moTa = "Đơn hàng đang trong quy trình Đổi / Trả hàng hóa (RMA)";
        } else if ("DA_TRA_HANG".equalsIgnoreCase(maTt)) {
            stepperIndex = 6;
            moTa = "Đơn hàng đã hoàn tất Trả hàng & Hoàn tiền";
        } else if ("DA_DOI_HANG".equalsIgnoreCase(maTt)) {
            stepperIndex = 6;
            moTa = "Đơn hàng đã hoàn tất Đổi sang sản phẩm mới";
        }

        String tenDvvc = (dh.getMaVanDon() != null && !dh.getMaVanDon().isBlank())
                ? "Simplain Express & Giao Hàng Tiết Kiệm (GHTK)"
                : "Simplain Express";

        List<LichSuTrangThaiDonHangResponse> lichSu = Collections.emptyList();
        try {
            lichSu = quanLyVanDonService.layLichSuDonHang(dh.getId());
        } catch (Exception ignored) {
        }

        return DonHangKhachHangDto.builder()
                .donHang(dh)
                .danhSachItem(itemDtos)
                .tongSoLuong(tongSoLuong)
                .coTheHuy(coTheHuy)
                .stepperIndex(stepperIndex)
                .lichSuVanDon(lichSu)
                .hinhAnhDauTien(hinhAnhDauTien)
                .tenDonViVanChuyen(tenDvvc)
                .moTaTrangThaiHienTai(moTa)
                .build();
    }

    /**
     * Lấy danh sách phân trang các đơn hàng của khách hàng (US23 - Tiêu chí 1).
     */
    @Transactional(readOnly = true)
    public Page<DonHangKhachHangDto> layDanhSachDonHangCuaKhachHang(
            Long khachHangId,
            String soDienThoai,
            String maTrangThai,
            String tuKhoa,
            Pageable pageable) {
        String tt = (maTrangThai != null && !maTrangThai.isBlank() && !"TAT_CA".equalsIgnoreCase(maTrangThai))
                ? maTrangThai.trim()
                : null;
        String kw = (tuKhoa != null) ? tuKhoa.trim() : null;

        Page<DonHang> page = donHangRepository.timKiemDonHangCuaKhachHang(khachHangId, soDienThoai, tt, kw, pageable);
        return page.map(this::chuyenDoiDonHangSangDto);
    }

    /**
     * Thống kê số lượng đơn hàng theo từng trạng thái phục vụ hiển thị Badge số lượng trên Tab (US23).
     */
    @Transactional(readOnly = true)
    public Map<String, Long> demSoLuongDonHangTheoCacTrangThai(Long khachHangId, String soDienThoai) {
        Map<String, Long> counts = new HashMap<>();
        counts.put("TAT_CA", donHangRepository.demDonHangCuaKhachHangTheoTrangThai(khachHangId, soDienThoai, "TAT_CA"));
        counts.put("CHO_XAC_NHAN", donHangRepository.demDonHangCuaKhachHangTheoTrangThai(khachHangId, soDienThoai, "CHO_XAC_NHAN"));
        counts.put("DANG_XU_LY", donHangRepository.demDonHangCuaKhachHangTheoTrangThai(khachHangId, soDienThoai, "DANG_XU_LY"));
        counts.put("DANG_GIAO", donHangRepository.demDonHangCuaKhachHangTheoTrangThai(khachHangId, soDienThoai, "DANG_GIAO"));
        counts.put("HOAN_THANH", donHangRepository.demDonHangCuaKhachHangTheoTrangThai(khachHangId, soDienThoai, "HOAN_THANH"));
        counts.put("DA_HUY", donHangRepository.demDonHangCuaKhachHangTheoTrangThai(khachHangId, soDienThoai, "DA_HUY"));
        counts.put("DANG_DOI_TRA", donHangRepository.demDonHangCuaKhachHangTheoTrangThai(khachHangId, soDienThoai, "DANG_DOI_TRA"));
        counts.put("DA_TRA_HANG", donHangRepository.demDonHangCuaKhachHangTheoTrangThai(khachHangId, soDienThoai, "DA_TRA_HANG"));
        counts.put("DA_DOI_HANG", donHangRepository.demDonHangCuaKhachHangTheoTrangThai(khachHangId, soDienThoai, "DA_DOI_HANG"));
        return counts;
    }

    /**
     * Lấy thông tin chi tiết đơn hàng của khách hàng (US23 - Tiêu chí 2 - Chống IDOR).
     */
    @Transactional(readOnly = true)
    public DonHangKhachHangDto layChiTietDonHangCuaKhachHang(String maDonHang, Long khachHangId, String soDienThoai) {
        if (maDonHang == null || maDonHang.isBlank()) {
            throw new IllegalArgumentException("Mã đơn hàng không được để trống!");
        }

        DonHang dh = donHangRepository.timTheoMaDonVaKhachHang(maDonHang.trim(), khachHangId, soDienThoai)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng [" + maDonHang + "] hoặc bạn không có quyền xem đơn hàng này!"));

        return chuyenDoiDonHangSangDto(dh);
    }

    /**
     * Khách hàng tự hủy đơn hàng (US23 - Validate form chuyên nghiệp).
     */
    @Transactional(rollbackFor = Exception.class)
    public DonHang khachHangHuyDonHang(String maDonHang, Long khachHangId, String soDienThoai, HuyDonHangRequest req) {
        if (req == null || req.getLyDoHuy() == null || req.getLyDoHuy().isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn lý do hủy đơn hàng!");
        }

        DonHang dh = donHangRepository.timTheoMaDonVaKhachHang(maDonHang.trim(), khachHangId, soDienThoai)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng [" + maDonHang + "] hoặc bạn không có quyền thao tác trên đơn hàng này!"));

        String maTtHienTai = (dh.getTrangThaiDonHang() != null) ? dh.getTrangThaiDonHang().getMaTrangThai() : "";
        if (!"CHO_XAC_NHAN".equalsIgnoreCase(maTtHienTai)) {
            if ("DANG_XU_LY".equalsIgnoreCase(maTtHienTai)) {
                throw new IllegalStateException("Đơn hàng đang trong quá trình đóng gói tại kho, không thể tự hủy trên web. Vui lòng liên hệ tổng đài CSKH để được hỗ trợ!");
            } else if ("DANG_GIAO".equalsIgnoreCase(maTtHienTai)) {
                throw new IllegalStateException("Đơn hàng đã được xuất kho và đang trên đường giao hàng, không thể tự hủy!");
            } else if ("HOAN_THANH".equalsIgnoreCase(maTtHienTai)) {
                throw new IllegalStateException("Đơn hàng đã hoàn thành, không thể hủy!");
            } else if ("DA_HUY".equalsIgnoreCase(maTtHienTai)) {
                throw new IllegalStateException("Đơn hàng này đã bị hủy trước đó!");
            } else {
                throw new IllegalStateException("Trạng thái đơn hàng không hợp lệ để hủy!");
            }
        }

        TrangThaiDonHang ttHuy = trangThaiDonHangRepository.findByMaTrangThai("DA_HUY")
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy trạng thái [DA_HUY]!"));

        dh.setTrangThaiDonHang(ttHuy);
        DonHang dhSaved = donHangRepository.saveAndFlush(dh);

        // 1. Hoàn trả tồn kho thật nguyên tử (Atomic inventory restock)
        List<ChiTietDonHang> items = chiTietDonHangRepository.findByMaDonHangId(dhSaved.getId());
        for (ChiTietDonHang item : items) {
            if (item.getMaBienTheId() != null && item.getSoLuong() != null && item.getSoLuong() > 0) {
                bienTheSanPhamRepository.hoanTonKhoAtomic(item.getMaBienTheId(), item.getSoLuong());
                log.info("US23: Đã hoàn trả {} cái vào tồn kho cho biến thể #{}", item.getSoLuong(), item.getMaBienTheId());
            }
        }

        // 2. Hoàn trả điểm Coolcash nếu đơn hàng đã dùng điểm
        if (dhSaved.getTienCoolcashDung() != null && dhSaved.getTienCoolcashDung().compareTo(BigDecimal.ZERO) > 0) {
            Long targetKhId = (dhSaved.getKhachHang() != null) ? dhSaved.getKhachHang().getId() : khachHangId;
            if (targetKhId != null) {
                loyaltyHoiVienService.hoanDiemKhiHuyDon(targetKhId, dhSaved.getId(), dhSaved.getMaDonHang(), dhSaved.getTienCoolcashDung());
            }
        }

        // 3. Ghi log lịch sử trạng thái
        String note = "Khách hàng hủy đơn: " + req.getLyDoHuy().trim();
        if (req.getGhiChuChiTiet() != null && !req.getGhiChuChiTiet().isBlank()) {
            note += " (" + req.getGhiChuChiTiet().trim() + ")";
        }

        LichSuTrangThaiDonHang logRecord = LichSuTrangThaiDonHang.builder()
                .donHang(dhSaved)
                .trangThaiMoi(ttHuy)
                .nguoiThucHien(null)
                .ghiChu(note)
                .ngayChuyen(LocalDateTime.now())
                .build();
        lichSuTrangThaiDonHangRepository.save(logRecord);

        log.info("US23: Khách hàng đã hủy đơn hàng #{} thành công. Lý do: {}", dhSaved.getMaDonHang(), note);
        return dhSaved;
    }
}
