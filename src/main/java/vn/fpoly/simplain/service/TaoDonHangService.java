package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.CapNhatTonKhoDto;
import vn.fpoly.simplain.dto.ChiTietDonHangItemRequest;
import vn.fpoly.simplain.dto.TaoDonHangMod4Request;
import vn.fpoly.simplain.dto.TaoDonHangResponse;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.exception.KhongDuTonKhoException;
import vn.fpoly.simplain.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Tạo mới Đơn hàng và Trừ tồn kho trong kho thật (US21 - Mod 5 / Đơn hàng - Hưng).
 * - Tiêu chí 1: Nhận Data từ Dev 4 (Mod 4 - Checkout/Cart) để Insert don_hang & chi_tiet_don_hang.
 * - Tiêu chí 2: Giảm so_luong trong kho thật nguyên tử (Concurrency-safe / Chống Over-selling).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaoDonHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final KhachHangRepository khachHangRepository;
    private final LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;

    /**
     * Tạo mới đơn hàng và trừ tồn kho trong kho thật theo giao dịch an toàn (US21).
     * Toàn bộ thao tác chạy trong một Transaction duy nhất; nếu kho không đủ hàng sẽ Rollback ngay lập tức.
     *
     * @param request Dữ liệu đơn hàng nhận từ Mod 4
     * @return Thông tin đơn hàng vừa tạo kèm lịch sử trừ tồn kho
     */
    @Transactional(rollbackFor = Exception.class)
    public TaoDonHangResponse taoDonHangVaTruTonKho(TaoDonHangMod4Request request) {
        if (request == null) {
            throw new IllegalArgumentException("Dữ liệu đặt hàng không được để trống!");
        }

        if (request.getDanhSachChiTiet() == null || request.getDanhSachChiTiet().isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất 1 mặt hàng chi tiết!");
        }

        // 1. Kiểm tra Phương thức thanh toán (US18)
        PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(request.getMaPhuongThucId())
                .orElseThrow(() -> new IllegalArgumentException("Phương thức thanh toán ID: " + request.getMaPhuongThucId() + " không tồn tại!"));

        if (!Boolean.TRUE.equals(pttt.getKichHoat())) {
            throw new IllegalArgumentException("Phương thức thanh toán [" + pttt.getTenPhuongThuc() + "] hiện đang bị tắt!");
        }

        // 2. Kiểm tra Khách hàng (nếu có tài khoản)
        KhachHang khachHang = null;
        if (request.getMaKhachHangId() != null) {
            khachHang = khachHangRepository.findById(request.getMaKhachHangId()).orElse(null);
        }

        // 3. Tiêu chí 2: Kiểm tra tồn kho trước của tất cả các biến thể
        for (ChiTietDonHangItemRequest item : request.getDanhSachChiTiet()) {
            BienTheSanPham bt = bienTheSanPhamRepository.findById(item.getMaBienTheId())
                    .orElseThrow(() -> new IllegalArgumentException("Biến thể sản phẩm ID " + item.getMaBienTheId() + " không tồn tại!"));

            int tonKhoHienCo = (bt.getSoLuong() != null) ? bt.getSoLuong() : 0;
            if (tonKhoHienCo < item.getSoLuong()) {
                log.warn("Không đủ hàng trong kho! SKU: {}, Yêu cầu: {}, Hiện có: {}", bt.getMaSku(), item.getSoLuong(), tonKhoHienCo);
                throw new KhongDuTonKhoException(bt.getId(), bt.getMaSku(), item.getSoLuong(), tonKhoHienCo);
            }
        }

        // 4. Tiêu chí 2: Giảm so_luong trong kho thật nguyên tử (Atomic decrement)
        List<CapNhatTonKhoDto> danhSachTonKhoCapNhat = new ArrayList<>();
        int tongSoLuongSanPham = 0;

        for (ChiTietDonHangItemRequest item : request.getDanhSachChiTiet()) {
            int rowsUpdated = bienTheSanPhamRepository.truTonKhoAtomic(item.getMaBienTheId(), item.getSoLuong());
            if (rowsUpdated != 1) {
                // Nếu không cập nhật được (do xung đột đa luồng hoặc hết hàng ngay thời điểm trừ)
                BienTheSanPham bt = bienTheSanPhamRepository.findById(item.getMaBienTheId()).orElseThrow();
                throw new KhongDuTonKhoException(bt.getId(), bt.getMaSku(), item.getSoLuong(), bt.getSoLuong());
            }

            // Đọc lại số lượng tồn kho mới sau khi trừ thành công
            BienTheSanPham btSauTru = bienTheSanPhamRepository.findById(item.getMaBienTheId()).orElseThrow();
            danhSachTonKhoCapNhat.add(CapNhatTonKhoDto.builder()
                    .maBienTheId(btSauTru.getId())
                    .maSku(btSauTru.getMaSku())
                    .soLuongDaTru(item.getSoLuong())
                    .soLuongConLai(btSauTru.getSoLuong())
                    .build());

            tongSoLuongSanPham += item.getSoLuong();
        }

        // 5. Tiêu chí 1: Sinh mã đơn hàng và Insert don_hang
        String maDonHang = "DH" + System.currentTimeMillis() + (int) (Math.random() * 900 + 100);

        String maTrangThai = (request.getMaTrangThai() != null && !request.getMaTrangThai().isBlank())
                ? request.getMaTrangThai()
                : "CHO_XAC_NHAN";

        TrangThaiDonHang trangThai = trangThaiDonHangRepository.findByMaTrangThai(maTrangThai)
                .orElseGet(() -> trangThaiDonHangRepository.findById(1L).orElseThrow(
                        () -> new IllegalStateException("Hệ thống chưa thiết lập danh mục trạng thái đơn hàng!")));

        DonHang donHang = DonHang.builder()
                .maDonHang(maDonHang)
                .khachHang(khachHang)
                .trangThaiDonHang(trangThai)
                .phuongThucThanhToan(pttt)
                .maGiamGiaId(request.getMaGiamGiaId())
                .tongTienHang(request.getTongTienHang())
                .tienGiamGia(request.getTienGiamGia() != null ? request.getTienGiamGia() : java.math.BigDecimal.ZERO)
                .tienCoolcashDung(request.getTienCoolcashDung() != null ? request.getTienCoolcashDung() : java.math.BigDecimal.ZERO)
                .phiVanChuyen(request.getPhiVanChuyen() != null ? request.getPhiVanChuyen() : java.math.BigDecimal.ZERO)
                .tongThanhToan(request.getTongThanhToan())
                .tenNguoiNhan(request.getTenNguoiNhan().trim())
                .soDienThoaiNhan(request.getSoDienThoaiNhan().trim())
                .diaChiGiaoHang(request.getDiaChiGiaoHang().trim())
                .ghiChu(request.getGhiChu() != null ? request.getGhiChu().trim() : null)
                .ngayTao(LocalDateTime.now())
                .build();

        DonHang donHangSaved = donHangRepository.save(donHang);

        // 6. Tiêu chí 1: Insert chi_tiet_don_hang
        for (ChiTietDonHangItemRequest item : request.getDanhSachChiTiet()) {
            BienTheSanPham bt = bienTheSanPhamRepository.findById(item.getMaBienTheId()).orElseThrow();

            String tenSp = (item.getTenSanPham() != null && !item.getTenSanPham().isBlank())
                    ? item.getTenSanPham().trim()
                    : (bt.getTenSanPham() != null ? bt.getTenSanPham() : "Sản phẩm SKU: " + bt.getMaSku());

            String maSku = (item.getMaSku() != null && !item.getMaSku().isBlank())
                    ? item.getMaSku().trim()
                    : bt.getMaSku();

            ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                    .maDonHangId(donHangSaved.getId())
                    .maBienTheId(bt.getId())
                    .maComboId(item.getMaComboId())
                    .nhomComboToken(item.getNhomComboToken())
                    .tenSanPham(tenSp)
                    .maSku(maSku)
                    .donGia(item.getDonGia())
                    .soLuong(item.getSoLuong())
                    .thanhTien(item.getThanhTien())
                    .build();

            chiTietDonHangRepository.save(chiTiet);
        }

        // 7. Ghi nhận log lịch sử khởi tạo đơn hàng (US22)
        try {
            LichSuTrangThaiDonHang logKhoiTao = LichSuTrangThaiDonHang.builder()
                    .donHang(donHangSaved)
                    .trangThaiMoi(trangThai)
                    .nguoiThucHien(null)
                    .ghiChu("Khởi tạo đơn hàng từ Checkout Storefront")
                    .ngayChuyen(donHangSaved.getNgayTao() != null ? donHangSaved.getNgayTao() : LocalDateTime.now())
                    .build();
            lichSuTrangThaiDonHangRepository.save(logKhoiTao);
        } catch (Exception ex) {
            log.warn("Không thể ghi log lịch sử đơn hàng #{}: {}", donHangSaved.getMaDonHang(), ex.getMessage());
        }

        log.info("US21: Tạo mới thành công đơn hàng #{}, Tổng tiền: {}đ, Đã trừ kho cho {} mặt hàng",
                donHangSaved.getMaDonHang(), donHangSaved.getTongThanhToan(), danhSachTonKhoCapNhat.size());

        return TaoDonHangResponse.builder()
                .success(true)
                .message("Tạo mới đơn hàng và trừ tồn kho thành công!")
                .donHangId(donHangSaved.getId())
                .maDonHang(donHangSaved.getMaDonHang())
                .tongThanhToan(donHangSaved.getTongThanhToan())
                .trangThai(trangThai.getTenTrangThai())
                .ngayTao(donHangSaved.getNgayTao())
                .tongSoLuongSanPham(tongSoLuongSanPham)
                .danhSachTonKhoCapNhat(danhSachTonKhoCapNhat)
                .build();
    }
}
