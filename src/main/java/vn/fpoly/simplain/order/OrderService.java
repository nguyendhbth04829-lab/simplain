package vn.fpoly.simplain.order;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.entity.ChiTietDonHang;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.LichSuTrangThaiDonHang;
import vn.fpoly.simplain.entity.TrangThaiDonHang;
import vn.fpoly.simplain.order.dto.CreateOrderItemRequest;
import vn.fpoly.simplain.order.dto.CreateOrderRequest;
import vn.fpoly.simplain.order.dto.OrderItemResponse;
import vn.fpoly.simplain.order.dto.OrderResponse;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.ChiTietDonHangRepository;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.LichSuTrangThaiDonHangRepository;
import vn.fpoly.simplain.repository.TrangThaiDonHangRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service xử lý toàn bộ nghiệp vụ Đơn hàng (Module 5).
 * Thực hiện logic US21: Tạo mới đơn hàng, trừ tồn kho thực tế, lưu chi tiết đơn hàng và ghi nhật ký trạng thái.
 */
@Service
public class OrderService {

    public static final String TRANG_THAI_KHOI_TAO = "CHO_XAC_NHAN";

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final KhachHangRepository khachHangRepository;
    private final LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;

    public OrderService(DonHangRepository donHangRepository,
                        ChiTietDonHangRepository chiTietDonHangRepository,
                        TrangThaiDonHangRepository trangThaiDonHangRepository,
                        BienTheSanPhamRepository bienTheSanPhamRepository,
                        KhachHangRepository khachHangRepository,
                        LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository) {
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.trangThaiDonHangRepository = trangThaiDonHangRepository;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
        this.khachHangRepository = khachHangRepository;
        this.lichSuTrangThaiDonHangRepository = lichSuTrangThaiDonHangRepository;
    }

    /**
     * Tạo mới đơn hàng và trừ tồn kho (US21).
     * Toàn bộ thao tác chạy trong một Transaction (ACID): nếu tồn kho không đủ hoặc lỗi, tự động rollback.
     *
     * @param request Dữ liệu đơn hàng nhận từ phía Checkout
     * @return OrderResponse thông tin đơn hàng đã tạo
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // 1. Lấy hoặc tự động tạo trạng thái mặc định "CHO_XAC_NHAN"
        TrangThaiDonHang trangThaiKhoiTao = trangThaiDonHangRepository.findByMaTrangThai(TRANG_THAI_KHOI_TAO)
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai(TRANG_THAI_KHOI_TAO)
                        .tenTrangThai("Chờ xác nhận")
                        .maMauHex("#F59E0B")
                        .build()));

        // 2. Tìm khách hàng nếu có mã khách hàng
        KhachHang khachHang = null;
        if (request.getMaKhachHangId() != null) {
            khachHang = khachHangRepository.findById(request.getMaKhachHangId()).orElse(null);
        }

        // 3. Kiểm tra số lượng tồn kho của từng biến thể trước khi trừ kho
        List<BienTheSanPham> bienTheCanTru = new ArrayList<>();
        for (CreateOrderItemRequest item : request.getItems()) {
            BienTheSanPham bienThe = bienTheSanPhamRepository.findById(item.getBienTheId())
                    .orElseGet(() -> bienTheSanPhamRepository.findByMaSku(item.getMaSku())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Không tìm thấy biến thể sản phẩm có ID: " + item.getBienTheId() + " (SKU: " + item.getMaSku() + ")")));

            int tonKhoHienTai = bienThe.getSoLuong() != null ? bienThe.getSoLuong() : 0;
            if (tonKhoHienTai < item.getSoLuong()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Sản phẩm '" + item.getTenSanPham() + "' (SKU: " + item.getMaSku()
                                + ") không đủ tồn kho. Tồn kho hiện tại: " + tonKhoHienTai
                                + ", số lượng yêu cầu: " + item.getSoLuong());
            }

            // Trừ số lượng tồn kho
            bienThe.setSoLuong(tonKhoHienTai - item.getSoLuong());
            bienTheCanTru.add(bienThe);
        }

        // Cập nhật tồn kho thật vào cơ sở dữ liệu
        bienTheSanPhamRepository.saveAll(bienTheCanTru);

        // 4. Sinh mã đơn hàng duy nhất: DH-yyMMdd-XXXX
        String maDonHang = generateOrderCode();

        // 5. Lưu thực thể Đơn hàng
        DonHang donHang = DonHang.builder()
                .maDonHang(maDonHang)
                .khachHang(khachHang)
                .trangThai(trangThaiKhoiTao)
                .maGiamGiaId(request.getMaGiamGiaId())
                .tongTienHang(request.getTongTienHang())
                .tienGiamGia(request.getTienGiamGia() != null ? request.getTienGiamGia() : BigDecimal.ZERO)
                .tienCoolcashDung(request.getTienCoolcashDung() != null ? request.getTienCoolcashDung() : BigDecimal.ZERO)
                .phiVanChuyen(request.getPhiVanChuyen() != null ? request.getPhiVanChuyen() : BigDecimal.ZERO)
                .tongThanhToan(request.getTongThanhToan())
                .tenNguoiNhan(request.getTenNguoiNhan())
                .soDienThoaiNhan(request.getSoDienThoaiNhan())
                .diaChiGiaoHang(request.getDiaChiGiaoHang())
                .ghiChu(request.getGhiChu())
                .ngayTao(LocalDateTime.now())
                .build();

        donHang = donHangRepository.save(donHang);

        // 6. Lưu danh sách Chi tiết đơn hàng
        List<ChiTietDonHang> chiTietList = new ArrayList<>();
        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (int i = 0; i < request.getItems().size(); i++) {
            CreateOrderItemRequest itemRequest = request.getItems().get(i);
            BienTheSanPham bienThe = bienTheCanTru.get(i);

            BigDecimal thanhTien = itemRequest.getDonGia().multiply(BigDecimal.valueOf(itemRequest.getSoLuong()));

            ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                    .donHang(donHang)
                    .bienThe(bienThe)
                    .maComboId(itemRequest.getMaComboId())
                    .nhomComboToken(itemRequest.getNhomComboToken())
                    .tenSanPham(itemRequest.getTenSanPham())
                    .maSku(itemRequest.getMaSku())
                    .donGia(itemRequest.getDonGia())
                    .soLuong(itemRequest.getSoLuong())
                    .thanhTien(thanhTien)
                    .build();

            chiTietList.add(chiTiet);
        }

        chiTietList = chiTietDonHangRepository.saveAll(chiTietList);

        for (ChiTietDonHang ct : chiTietList) {
            itemResponses.add(OrderItemResponse.builder()
                    .id(ct.getId())
                    .bienTheId(ct.getBienThe() != null ? ct.getBienThe().getId() : null)
                    .maSku(ct.getMaSku())
                    .tenSanPham(ct.getTenSanPham())
                    .donGia(ct.getDonGia())
                    .soLuong(ct.getSoLuong())
                    .thanhTien(ct.getThanhTien())
                    .build());
        }

        // 7. Ghi nhận lịch sử trạng thái ban đầu của đơn hàng
        LichSuTrangThaiDonHang lichSu = LichSuTrangThaiDonHang.builder()
                .donHang(donHang)
                .trangThaiMoi(trangThaiKhoiTao)
                .ghiChu("Đơn hàng mới được tạo từ hệ thống Checkout (US21)")
                .ngayChuyen(LocalDateTime.now())
                .build();
        lichSuTrangThaiDonHangRepository.save(lichSu);

        // 8. Trả về OrderResponse
        return OrderResponse.builder()
                .id(donHang.getId())
                .maDonHang(donHang.getMaDonHang())
                .maTrangThai(trangThaiKhoiTao.getMaTrangThai())
                .tenTrangThai(trangThaiKhoiTao.getTenTrangThai())
                .tenNguoiNhan(donHang.getTenNguoiNhan())
                .soDienThoaiNhan(donHang.getSoDienThoaiNhan())
                .diaChiGiaoHang(donHang.getDiaChiGiaoHang())
                .maVanDon(donHang.getMaVanDon())
                .tongTienHang(donHang.getTongTienHang())
                .tienGiamGia(donHang.getTienGiamGia())
                .tienCoolcashDung(donHang.getTienCoolcashDung())
                .phiVanChuyen(donHang.getPhiVanChuyen())
                .tongThanhToan(donHang.getTongThanhToan())
                .ghiChu(donHang.getGhiChu())
                .ngayTao(donHang.getNgayTao())
                .thongBao("Tạo đơn hàng thành công và đã trừ tồn kho")
                .items(itemResponses)
                .build();
    }

    /**
     * Tìm đơn hàng theo mã đơn hàng.
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByCode(String maDonHang) {
        DonHang donHang = donHangRepository.findByMaDonHang(maDonHang)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng: " + maDonHang));
        return toOrderResponse(donHang);
    }

    /**
     * Tìm đơn hàng theo ID.
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        DonHang donHang = donHangRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng có ID: " + id));
        return toOrderResponse(donHang);
    }

    /**
     * Chuyển đổi entity DonHang sang DTO OrderResponse.
     */
    private OrderResponse toOrderResponse(DonHang donHang) {
        List<ChiTietDonHang> chiTiets = chiTietDonHangRepository.findByDonHangId(donHang.getId());
        List<OrderItemResponse> itemResponses = chiTiets.stream().map(ct -> OrderItemResponse.builder()
                .id(ct.getId())
                .bienTheId(ct.getBienThe() != null ? ct.getBienThe().getId() : null)
                .maSku(ct.getMaSku())
                .tenSanPham(ct.getTenSanPham())
                .donGia(ct.getDonGia())
                .soLuong(ct.getSoLuong())
                .thanhTien(ct.getThanhTien())
                .build()).toList();

        return OrderResponse.builder()
                .id(donHang.getId())
                .maDonHang(donHang.getMaDonHang())
                .maTrangThai(donHang.getTrangThai() != null ? donHang.getTrangThai().getMaTrangThai() : null)
                .tenTrangThai(donHang.getTrangThai() != null ? donHang.getTrangThai().getTenTrangThai() : null)
                .tenNguoiNhan(donHang.getTenNguoiNhan())
                .soDienThoaiNhan(donHang.getSoDienThoaiNhan())
                .diaChiGiaoHang(donHang.getDiaChiGiaoHang())
                .maVanDon(donHang.getMaVanDon())
                .tongTienHang(donHang.getTongTienHang())
                .tienGiamGia(donHang.getTienGiamGia())
                .tienCoolcashDung(donHang.getTienCoolcashDung())
                .phiVanChuyen(donHang.getPhiVanChuyen())
                .tongThanhToan(donHang.getTongThanhToan())
                .ghiChu(donHang.getGhiChu())
                .ngayTao(donHang.getNgayTao())
                .thongBao("Lấy thông tin đơn hàng thành công")
                .items(itemResponses)
                .build();
    }

    private String generateOrderCode() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "DH" + datePart + "-" + randomPart;
    }
}
