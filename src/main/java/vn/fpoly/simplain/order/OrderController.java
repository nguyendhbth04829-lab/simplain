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
import vn.fpoly.simplain.order.dto.CreateOrderRequest;
import vn.fpoly.simplain.order.dto.OrderResponse;

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
}
