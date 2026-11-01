package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.BocComboDto;
import vn.fpoly.simplain.dto.CheckoutTinhTienResponse;
import vn.fpoly.simplain.dto.DatHangRequest;
import vn.fpoly.simplain.dto.GioHangResponse;
import vn.fpoly.simplain.dto.ItemGioHangDto;
import vn.fpoly.simplain.entity.ChiTietDonHang;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.PhuongThucThanhToan;
import vn.fpoly.simplain.entity.TrangThaiDonHang;
import vn.fpoly.simplain.repository.ChiTietDonHangRepository;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.PhuongThucThanhToanRepository;
import vn.fpoly.simplain.repository.TrangThaiDonHangRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutOrderService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final KhachHangRepository khachHangRepository;
    private final GioHangService gioHangService;
    private final DonHangService donHangService;

    @Transactional(rollbackFor = Exception.class)
    public DonHang taoDonHang(Long khachHangId, Long cartId, DatHangRequest request) {
        if (request == null || request.getPhuongThucThanhToanId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn phương thức thanh toán.");
        }

        GioHangResponse gioHang = gioHangService.layThongTinGioHang(khachHangId, cartId);
        if (gioHang == null || Boolean.TRUE.equals(gioHang.getGioHangRong())) {
            throw new IllegalArgumentException("Giỏ hàng đang trống.");
        }

        PhuongThucThanhToan phuongThuc = phuongThucThanhToanRepository.findById(request.getPhuongThucThanhToanId())
                .filter(pt -> Boolean.TRUE.equals(pt.getKichHoat()))
                .orElseThrow(() -> new IllegalArgumentException("Phương thức thanh toán không tồn tại hoặc đang tắt."));
        TrangThaiDonHang trangThai = trangThaiDonHangRepository.findByMaTrangThai("CHO_XAC_NHAN")
                .orElseGet(() -> trangThaiDonHangRepository.findById(1L)
                        .orElseThrow(() -> new IllegalStateException("Chưa cấu hình trạng thái đơn hàng.")));
        KhachHang khachHang = khachHangId == null ? null : khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản khách hàng."));

        CheckoutTinhTienResponse chiPhi = donHangService.tinhToanChiPhi(
                gioHang.getTongTienHang(), null, BigDecimal.ZERO, khachHangId);
        DonHang donHang = donHangRepository.save(DonHang.builder()
                .maDonHang("DH" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase())
                .khachHang(khachHang)
                .trangThaiDonHang(trangThai)
                .phuongThucThanhToan(phuongThuc)
                .tongTienHang(chiPhi.getTongTienHang())
                .tienGiamGia(BigDecimal.ZERO)
                .tienCoolcashDung(BigDecimal.ZERO)
                .phiVanChuyen(chiPhi.getPhiVanChuyen())
                .tongThanhToan(chiPhi.getTongThanhToan())
                .tenNguoiNhan(request.getTenNguoiNhan().trim())
                .soDienThoaiNhan(request.getSoDienThoaiNhan().trim())
                .diaChiGiaoHang(request.getDiaChiGiaoHang().trim())
                .ghiChu(request.getGhiChu() == null ? null : request.getGhiChu().trim())
                .ngayTao(LocalDateTime.now())
                .build());

        List<ChiTietDonHang> chiTiet = new ArrayList<>();
        if (gioHang.getDanhSachItemLe() != null) {
            for (ItemGioHangDto item : gioHang.getDanhSachItemLe()) {
                chiTiet.add(ChiTietDonHang.builder()
                        .maDonHangId(donHang.getId())
                        .maBienTheId(item.getMaBienTheId())
                        .tenSanPham(item.getTenSanPham() == null ? "Sản phẩm" : item.getTenSanPham())
                        .maSku(item.getMaSku() == null ? "SKU-" + item.getMaBienTheId() : item.getMaSku())
                        .donGia(item.getDonGia())
                        .soLuong(item.getSoLuong())
                        .thanhTien(item.getThanhTien())
                        .build());
            }
        }
        if (gioHang.getDanhSachBocCombo() != null) {
            for (BocComboDto combo : gioHang.getDanhSachBocCombo()) {
                boolean monDau = true;
                for (ItemGioHangDto item : combo.getDanhSachMonCon()) {
                    chiTiet.add(ChiTietDonHang.builder()
                            .maDonHangId(donHang.getId())
                            .maBienTheId(item.getMaBienTheId())
                            .maComboId(combo.getMaComboId())
                            .nhomComboToken(combo.getNhomComboToken())
                            .tenSanPham(item.getTenSanPham() == null ? combo.getTenCombo() : item.getTenSanPham())
                            .maSku(item.getMaSku() == null ? "SKU-" + item.getMaBienTheId() : item.getMaSku())
                            .donGia(monDau ? combo.getGiaCombo() : BigDecimal.ZERO)
                            .soLuong(item.getSoLuong())
                            .thanhTien(monDau ? combo.getThanhTien() : BigDecimal.ZERO)
                            .build());
                    monDau = false;
                }
            }
        }
        chiTietDonHangRepository.saveAll(chiTiet);
        gioHangService.xoaHetGioHang(khachHangId, cartId);
        return donHang;
    }
}
