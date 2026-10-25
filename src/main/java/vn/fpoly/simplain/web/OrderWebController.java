package vn.fpoly.simplain.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.order.OrderService;
import vn.fpoly.simplain.order.dto.CreateOrderItemRequest;
import vn.fpoly.simplain.order.dto.CreateOrderRequest;
import vn.fpoly.simplain.order.dto.OrderResponse;
import vn.fpoly.simplain.order.dto.UpdateOrderStatusRequest;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.rma.RmaService;
import vn.fpoly.simplain.rma.dto.CreateRmaItemRequest;
import vn.fpoly.simplain.rma.dto.CreateRmaRequest;
import vn.fpoly.simplain.rma.dto.ReviewRmaRequest;
import vn.fpoly.simplain.rma.dto.RmaResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller phục vụ giao diện Web trực quan (Thymeleaf + Tailwind CSS) để Trưởng nhóm và thành viên test nhanh Module 5.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderWebController {

    private final OrderService orderService;
    private final RmaService rmaService;
    private final KhachHangRepository khachHangRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final DonHangRepository donHangRepository;

    // =========================================================================
    // 1. QUẢN LÝ ĐƠN HÀNG (US22 & US23)
    // =========================================================================

    @GetMapping({"/", "/admin/orders"})
    public String viewOrderList(Model model) {
        List<OrderResponse> orders = orderService.getAllOrders();
        List<BienTheSanPham> skus = bienTheSanPhamRepository.findAll();
        model.addAttribute("orders", orders);
        model.addAttribute("skus", skus);
        return "orders/list";
    }

    @GetMapping("/admin/orders/{maDonHang}")
    public String viewOrderDetail(@PathVariable String maDonHang, Model model) {
        OrderResponse order = orderService.getOrderByCode(maDonHang);
        model.addAttribute("order", order);
        return "orders/detail";
    }

    @PostMapping("/admin/orders/{maDonHang}/status")
    public String updateOrderStatus(@PathVariable String maDonHang,
                                    @RequestParam String maTrangThaiMoi,
                                    @RequestParam(required = false) String maVanDon,
                                    @RequestParam(required = false) String ghiChu,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(maDonHang, UpdateOrderStatusRequest.builder()
                    .maTrangThaiMoi(maTrangThaiMoi)
                    .maVanDon(maVanDon)
                    .ghiChu(ghiChu)
                    .nguoiThucHienId(1L)
                    .build());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cập nhật trạng thái đơn [" + maDonHang + "] sang [" + maTrangThaiMoi + "] thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/orders";
    }

    // =========================================================================
    // 2. TẠO ĐƠN HÀNG MỚI - TEST US21 (ĐẶT HÀNG & TRỪ TỒN KHO)
    // =========================================================================

    @GetMapping("/orders/new")
    public String showCreateOrderForm(Model model) {
        List<KhachHang> customers = khachHangRepository.findAll();
        List<BienTheSanPham> skus = bienTheSanPhamRepository.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("skus", skus);
        return "orders/create";
    }

    @PostMapping("/orders/new")
    public String handleCreateOrder(@RequestParam Long khachHangId,
                                    @RequestParam Long bienTheSanPhamId,
                                    @RequestParam Integer soLuong,
                                    @RequestParam String tenNguoiNhan,
                                    @RequestParam String soDienThoaiNhan,
                                    @RequestParam String diaChiGiaoHang,
                                    @RequestParam(required = false) String ghiChu,
                                    RedirectAttributes redirectAttributes) {
        try {
            BienTheSanPham sku = bienTheSanPhamRepository.findById(bienTheSanPhamId)
                    .orElseThrow(() -> new IllegalArgumentException("Biến thể sản phẩm không tồn tại"));

            BigDecimal tongTien = sku.getGiaBan().multiply(BigDecimal.valueOf(soLuong));

            CreateOrderRequest request = CreateOrderRequest.builder()
                    .maKhachHangId(khachHangId)
                    .tenNguoiNhan(tenNguoiNhan)
                    .soDienThoaiNhan(soDienThoaiNhan)
                    .diaChiGiaoHang(diaChiGiaoHang)
                    .tongTienHang(tongTien)
                    .tongThanhToan(tongTien)
                    .ghiChu(ghiChu)
                    .items(List.of(
                            CreateOrderItemRequest.builder()
                                    .bienTheId(sku.getId())
                                    .maSku(sku.getMaSku())
                                    .tenSanPham("Sản phẩm mã " + sku.getMaSku())
                                    .soLuong(soLuong)
                                    .donGia(sku.getGiaBan())
                                    .build()
                    ))
                    .build();

            OrderResponse order = orderService.createOrder(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Tạo đơn hàng thành công! Mã đơn: [" + order.getMaDonHang() + "]. Số lượng tồn kho biến thể đã được tự động trừ!");
            return "redirect:/admin/orders";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể tạo đơn: " + ex.getMessage());
            return "redirect:/orders/new";
        }
    }

    // =========================================================================
    // 3. QUẢN LÝ ĐỔI TRẢ RMA (US24 & US25)
    // =========================================================================

    @GetMapping("/admin/rma")
    public String viewRmaList(Model model) {
        List<RmaResponse> rmas = rmaService.getAllRma(null);
        model.addAttribute("rmas", rmas);
        return "rma/list";
    }

    @GetMapping("/admin/rma/{maYeuCauRma}")
    public String viewRmaDetail(@PathVariable String maYeuCauRma, Model model) {
        RmaResponse rma = rmaService.getRmaByCode(maYeuCauRma);
        model.addAttribute("rma", rma);
        return "rma/detail";
    }

    @PostMapping("/admin/rma/{maYeuCauRma}/review")
    public String handleReviewRma(@PathVariable String maYeuCauRma,
                                  @RequestParam String trangThaiMoi,
                                  @RequestParam(required = false) String ghiChuKiemDinh,
                                  RedirectAttributes redirectAttributes) {
        try {
            rmaService.reviewRma(maYeuCauRma, ReviewRmaRequest.builder()
                    .trangThai(trangThaiMoi)
                    .ghiChuKiemDinh(ghiChuKiemDinh)
                    .nguoiDuyetId(1L)
                    .build());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã xét duyệt yêu cầu RMA [" + maYeuCauRma + "] thành công! Trạng thái: " + trangThaiMoi);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xét duyệt: " + ex.getMessage());
        }
        return "redirect:/admin/rma";
    }

    @GetMapping("/rma/new")
    public String showCreateRmaForm(Model model) {
        // Lấy danh sách các đơn hàng đã giao hoặc hoàn thành để cho phép gửi RMA
        List<OrderResponse> eligibleOrders = orderService.getAllOrders().stream()
                .filter(o -> "DA_GIAO".equals(o.getMaTrangThai()) || "HOAN_THANH".equals(o.getMaTrangThai()))
                .toList();
        List<BienTheSanPham> skus = bienTheSanPhamRepository.findAll();
        model.addAttribute("orders", eligibleOrders);
        model.addAttribute("skus", skus);
        return "rma/create";
    }

    @PostMapping("/rma/new")
    public String handleCreateRma(@RequestParam String maDonHang,
                                  @RequestParam String loaiYeuCau,
                                  @RequestParam String lyDo,
                                  @RequestParam Long chiTietDonHangId,
                                  @RequestParam Integer soLuong,
                                  @RequestParam(required = false) Long bienTheMoiId,
                                  @RequestParam(required = false) String soTaiKhoanNganHang,
                                  @RequestParam(required = false) String tenNganHang,
                                  @RequestParam(required = false) String chuTaiKhoan,
                                  RedirectAttributes redirectAttributes) {
        try {
            DonHang donHang = donHangRepository.findByMaDonHang(maDonHang)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng mã: " + maDonHang));

            Long khId = donHang.getKhachHang() != null ? donHang.getKhachHang().getId() : 1L;

            String thongTinNganHang = null;
            if (soTaiKhoanNganHang != null && !soTaiKhoanNganHang.isBlank()) {
                thongTinNganHang = tenNganHang + " - " + soTaiKhoanNganHang + " - " + chuTaiKhoan;
            }

            CreateRmaRequest request = CreateRmaRequest.builder()
                    .donHangId(donHang.getId())
                    .khachHangId(khId)
                    .loaiDoiTra(loaiYeuCau)
                    .lyDo(lyDo)
                    .thongTinNganHang(thongTinNganHang)
                    .items(List.of(
                            CreateRmaItemRequest.builder()
                                    .chiTietDonHangId(chiTietDonHangId)
                                    .maBienTheMoiId(bienTheMoiId)
                                    .soLuongTra(soLuong)
                                    .tinhTrangSanPham(lyDo)
                                    .build()
                    ))
                    .build();

            RmaResponse rma = rmaService.createRma(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Tạo yêu cầu RMA thành công! Mã yêu cầu: [" + rma.getMaYeuCauRma() + "]. Chờ Admin duyệt!");
            return "redirect:/admin/rma";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể tạo RMA: " + ex.getMessage());
            return "redirect:/rma/new";
        }
    }
}
