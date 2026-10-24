package vn.fpoly.simplain.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import vn.fpoly.simplain.order.dto.CreateOrderRequest;
import vn.fpoly.simplain.order.dto.OrderResponse;
import vn.fpoly.simplain.order.dto.OrderStatusHistoryResponse;
import vn.fpoly.simplain.order.dto.UpdateOrderStatusRequest;

import java.util.List;

/**
 * Controller tiếp nhận các yêu cầu API liên quan đến Đơn hàng (Module 5).
 */
@Tag(name = "Đơn hàng", description = "API quản lý và xử lý Đơn hàng (Module 5 - US21, US22, US23)")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Tạo mới đơn hàng và trừ tồn kho (US21).
     * Endpoint nhận payload từ Checkout (Dev 4) để lưu đơn hàng, chi tiết đơn hàng và giảm tồn kho.
     */
    @Operation(
            summary = "Tạo mới Đơn hàng và Trừ tồn kho (US21)",
            description = "Nhận dữ liệu checkout từ hệ thống để tạo đơn hàng, lưu chi tiết đơn hàng và tự động trừ số lượng tồn kho thật của các biến thể sản phẩm."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    /**
     * Cập nhật trạng thái đơn hàng và mã vận đơn (US22).
     */
    @Operation(
            summary = "Cập nhật trạng thái vận đơn (US22)",
            description = "Dành cho Admin/Kho cập nhật tiến trình đơn (Chờ giao hàng, Đang giao, Đã giao, Hủy...), bổ sung mã vận đơn và tự động ghi nhật ký lịch sử."
    )
    @PutMapping("/{maDonHang}/status")
    public OrderResponse updateOrderStatus(
            @Parameter(description = "Mã đơn hàng cần cập nhật", example = "DH261024-ABCDEF")
            @PathVariable String maDonHang,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateOrderStatus(maDonHang, request);
    }

    /**
     * Lấy danh sách lịch sử đơn hàng của một khách hàng (US23).
     */
    @Operation(
            summary = "Theo dõi Lịch sử đơn hàng cá nhân (US23)",
            description = "Khách hàng tra cứu danh sách toàn bộ các đơn hàng đã đặt của mình, sắp xếp theo thời gian mới nhất."
    )
    @GetMapping("/customer/{khachHangId}")
    public List<OrderResponse> getOrdersByCustomer(
            @Parameter(description = "ID khách hàng", example = "1")
            @PathVariable Long khachHangId) {
        return orderService.getOrdersByCustomer(khachHangId);
    }

    /**
     * Lấy toàn bộ danh sách đơn hàng trong hệ thống (Admin/Kho).
     */
    @Operation(
            summary = "Lấy tất cả đơn hàng (Admin/Kho)",
            description = "Tra cứu danh sách tất cả các đơn hàng trong hệ thống, sắp xếp theo ngày tạo mới nhất."
    )
    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * Lấy thông tin chi tiết một đơn hàng theo mã đơn hàng (US23).
     */
    @Operation(
            summary = "Xem chi tiết Đơn hàng theo mã (US23)",
            description = "Tra cứu thông tin chi tiết đơn hàng, danh sách sản phẩm và trạng thái vận đơn qua mã đơn hàng."
    )
    @GetMapping("/{maDonHang}")
    public OrderResponse getOrderByCode(
            @Parameter(description = "Mã đơn hàng cần tra cứu (VD: DH261024-ABCDEF)", example = "DH261024-ABCDEF")
            @PathVariable String maDonHang) {
        return orderService.getOrderByCode(maDonHang);
    }

    /**
     * Lấy dòng thời gian tiến trình trạng thái của đơn hàng (US22, US23).
     */
    @Operation(
            summary = "Xem dòng thời gian lịch sử trạng thái đơn hàng (US22, US23)",
            description = "Xem chi tiết nhật ký các lần chuyển đổi trạng thái, người thực hiện và ghi chú tiến trình vận chuyển."
    )
    @GetMapping("/{maDonHang}/history")
    public List<OrderStatusHistoryResponse> getOrderHistory(
            @Parameter(description = "Mã đơn hàng cần xem lịch sử", example = "DH261024-ABCDEF")
            @PathVariable String maDonHang) {
        return orderService.getOrderHistory(maDonHang);
    }
}
