package vn.fpoly.simplain.paymentmethod;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản trị phương thức thanh toán (US18).
 * Mẫu chuẩn copy theo HelloController: Controller chỉ gọi Service.
 */
@Tag(name = "Phương thức thanh toán", description = "API bật/tắt cổng thanh toán COD, VNPay, MoMo (US18)")
@RestController
@RequestMapping("/api/thanh-toan/phuong-thuc")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    /**
     * Lấy danh sách, có thể lọc ?kichHoat=true/false.
     *
     * @param kichHoat null = lấy hết
     * @return danh sách phương thức
     */
    @Operation(summary = "Lấy danh sách phương thức thanh toán")
    @GetMapping
    public List<PaymentMethodResponse> getAll(
            @Parameter(description = "Lọc trạng thái bật/tắt", example = "true")
            @RequestParam(required = false) Boolean kichHoat) {
        return paymentMethodService.findAll(kichHoat);
    }

    /**
     * Lấy chi tiết 1 phương thức.
     *
     * @param id khóa chính
     * @return chi tiết
     */
    @Operation(summary = "Lấy chi tiết phương thức thanh toán")
    @GetMapping("/{id}")
    public PaymentMethodResponse getById(
            @Parameter(description = "Id phương thức", example = "1")
            @PathVariable Long id) {
        return paymentMethodService.findById(id);
    }

    /**
     * Tạo mới phương thức (COD, VNPAY, MOMO).
     *
     * @param request JSON đã validate
     * @return bản ghi vừa tạo
     */
    @Operation(summary = "Tạo mới phương thức thanh toán")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentMethodResponse create(@Valid @RequestBody PaymentMethodRequest request) {
        return paymentMethodService.create(request);
    }

    /**
     * Sửa toàn bộ thông tin.
     *
     * @param id id cần sửa
     * @param request dữ liệu mới
     * @return bản ghi sau sửa
     */
    @Operation(summary = "Sửa phương thức thanh toán")
    @PutMapping("/{id}")
    public PaymentMethodResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PaymentMethodRequest request) {
        return paymentMethodService.update(id, request);
    }

    /**
     * Bật/tắt nhanh cho Admin.
     * Body: { "kichHoat": true }.
     *
     * @param id id cần đổi
     * @param body JSON chứa kichHoat
     * @return bản ghi sau đổi
     */
    @Operation(summary = "Bật/tắt phương thức thanh toán")
    @PatchMapping("/{id}/trang-thai")
    public PaymentMethodResponse updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        boolean kichHoat = Boolean.TRUE.equals(body.get("kichHoat"));
        return paymentMethodService.updateStatus(id, kichHoat);
    }

    /**
     * Xóa cứng (chỉ khi chưa có giao dịch).
     *
     * @param id id cần xóa
     */
    @Operation(summary = "Xóa phương thức thanh toán")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        paymentMethodService.delete(id);
    }
}
