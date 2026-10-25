package vn.fpoly.simplain.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.TrangThaiDonHang;
import vn.fpoly.simplain.order.OrderService;
import vn.fpoly.simplain.order.dto.CreateOrderItemRequest;
import vn.fpoly.simplain.order.dto.CreateOrderRequest;
import vn.fpoly.simplain.order.dto.OrderResponse;
import vn.fpoly.simplain.order.dto.UpdateOrderStatusRequest;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.TrangThaiDonHangRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Khởi tạo dữ liệu mẫu ban đầu để Trưởng nhóm và các thành viên test giao diện trực quan ngay khi bật app.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SampleDataInitializer implements CommandLineRunner {

    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final KhachHangRepository khachHangRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final DonHangRepository donHangRepository;
    private final OrderService orderService;

    @Override
    public void run(String... args) {
        initTrangThaiDonHang();
        KhachHang kh1 = initKhachHang();
        initBienTheSanPham();
        initDonHangMau(kh1);
    }

    private void initTrangThaiDonHang() {
        if (trangThaiDonHangRepository.count() == 0) {
            trangThaiDonHangRepository.saveAll(List.of(
                    TrangThaiDonHang.builder().maTrangThai("CHO_XAC_NHAN").tenTrangThai("Chờ xác nhận").maMauHex("#f59e0b").build(),
                    TrangThaiDonHang.builder().maTrangThai("CHO_GIAO_HANG").tenTrangThai("Chờ giao hàng").maMauHex("#3b82f6").build(),
                    TrangThaiDonHang.builder().maTrangThai("DANG_GIAO").tenTrangThai("Đang giao").maMauHex("#6366f1").build(),
                    TrangThaiDonHang.builder().maTrangThai("DA_GIAO").tenTrangThai("Đã giao").maMauHex("#a855f7").build(),
                    TrangThaiDonHang.builder().maTrangThai("HOAN_THANH").tenTrangThai("Hoàn thành").maMauHex("#10b981").build(),
                    TrangThaiDonHang.builder().maTrangThai("DA_HUY").tenTrangThai("Đã hủy").maMauHex("#ef4444").build()
            ));
            log.info(">> Đã khởi tạo 6 trạng thái đơn hàng mẫu.");
        }
    }

    private KhachHang initKhachHang() {
        if (khachHangRepository.count() == 0) {
            KhachHang kh1 = khachHangRepository.save(KhachHang.builder()
                    .maKhachHang("KH001")
                    .hoTen("Nguyễn Văn An")
                    .soDienThoai("0912345678")
                    .email("an.nguyen@example.com")
                    .build());
            khachHangRepository.save(KhachHang.builder()
                    .maKhachHang("KH002")
                    .hoTen("Trần Thị Bình")
                    .soDienThoai("0987654321")
                    .email("binh.tran@example.com")
                    .build());
            log.info(">> Đã khởi tạo khách hàng mẫu.");
            return kh1;
        }
        return khachHangRepository.findAll().get(0);
    }

    private void initBienTheSanPham() {
        if (bienTheSanPhamRepository.count() == 0) {
            bienTheSanPhamRepository.saveAll(List.of(
                    BienTheSanPham.builder()
                            .maSku("AT-DEN-M")
                            .maVachBarcode("BC-AT-DEN-M")
                            .giaBan(new BigDecimal("150000"))
                            .giaGoc(new BigDecimal("100000"))
                            .soLuong(30)
                            .kichHoat(true)
                            .build(),
                    BienTheSanPham.builder()
                            .maSku("AT-TRANG-L")
                            .maVachBarcode("BC-AT-TRANG-L")
                            .giaBan(new BigDecimal("150000"))
                            .giaGoc(new BigDecimal("100000"))
                            .soLuong(20)
                            .kichHoat(true)
                            .build(),
                    BienTheSanPham.builder()
                            .maSku("PL-XANH-L")
                            .maVachBarcode("BC-PL-XANH-L")
                            .giaBan(new BigDecimal("250000"))
                            .giaGoc(new BigDecimal("180000"))
                            .soLuong(15)
                            .kichHoat(true)
                            .build()
            ));
            log.info(">> Đã khởi tạo 3 biến thể sản phẩm SKU mẫu.");
        }
    }

    private void initDonHangMau(KhachHang khachHang) {
        if (donHangRepository.count() == 0 && khachHang != null) {
            BienTheSanPham sku1 = bienTheSanPhamRepository.findByMaSku("AT-DEN-M").orElse(null);
            BienTheSanPham sku2 = bienTheSanPhamRepository.findByMaSku("PL-XANH-L").orElse(null);

            if (sku1 != null && sku2 != null) {
                // Tạo đơn 1: Trạng thái CHO_XAC_NHAN
                orderService.createOrder(CreateOrderRequest.builder()
                        .maKhachHangId(khachHang.getId())
                        .tenNguoiNhan(khachHang.getHoTen())
                        .soDienThoaiNhan(khachHang.getSoDienThoai())
                        .diaChiGiaoHang("123 Lê Duẩn, Quận 1, TP. Hồ Chí Minh")
                        .tongTienHang(new BigDecimal("300000"))
                        .tongThanhToan(new BigDecimal("300000"))
                        .ghiChu("Giao giờ hành chính")
                        .items(List.of(
                                CreateOrderItemRequest.builder()
                                        .bienTheId(sku1.getId())
                                        .maSku(sku1.getMaSku())
                                        .tenSanPham("Áo Thun Basic Đen Size M")
                                        .soLuong(2)
                                        .donGia(sku1.getGiaBan())
                                        .build()
                        ))
                        .build());

                // Tạo đơn 2: Chuyển thành ĐÃ GIAO để sẵn sàng test RMA
                OrderResponse don2 = orderService.createOrder(CreateOrderRequest.builder()
                        .maKhachHangId(khachHang.getId())
                        .tenNguoiNhan(khachHang.getHoTen())
                        .soDienThoaiNhan(khachHang.getSoDienThoai())
                        .diaChiGiaoHang("456 Nguyễn Thị Minh Khai, Quận 3, TP. Hồ Chí Minh")
                        .tongTienHang(new BigDecimal("250000"))
                        .tongThanhToan(new BigDecimal("250000"))
                        .items(List.of(
                                CreateOrderItemRequest.builder()
                                        .bienTheId(sku2.getId())
                                        .maSku(sku2.getMaSku())
                                        .tenSanPham("Áo Polo Thể Thao Xanh Size L")
                                        .soLuong(1)
                                        .donGia(sku2.getGiaBan())
                                        .build()
                        ))
                        .build());

                orderService.updateOrderStatus(don2.getMaDonHang(), UpdateOrderStatusRequest.builder()
                        .maTrangThaiMoi("DA_GIAO")
                        .maVanDon("VTP-88992211")
                        .ghiChu("Đã giao thành công đến người nhận")
                        .nguoiThucHienId(1L)
                        .build());

                log.info(">> Đã khởi tạo 2 đơn hàng mẫu (1 chờ xác nhận, 1 đã giao để test RMA).");
            }
        }
    }
}
