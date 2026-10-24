package vn.fpoly.simplain.rma;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.fpoly.simplain.rma.dto.CreateRmaRequest;
import vn.fpoly.simplain.rma.dto.ReviewRmaRequest;
import vn.fpoly.simplain.rma.dto.RmaResponse;

import java.util.List;

/**
 * Controller tiếp nhận các yêu cầu API Đổi/Trả hàng (RMA) thuộc Module 5 (US24, US25).
 */
@Tag(name = "Đổi trả hàng (RMA)", description = "API quản lý và xử lý yêu cầu Đổi/Trả hàng (Module 5 - US24, US25)")
@RestController
@RequestMapping("/api/rma")
public class RmaController {

    private final RmaService rmaService;

    public RmaController(RmaService rmaService) {
        this.rmaService = rmaService;
    }

    /**
     * Khách hàng tạo mới yêu cầu Đổi/Trả hàng online (US24).
     */
    @Operation(
            summary = "Tạo Yêu cầu Đổi/Trả online (US24)",
            description = "Khách hàng gửi yêu cầu đổi hàng hoặc trả hàng hoàn tiền cho sản phẩm trong đơn hàng cũ, kèm lý do và số tài khoản nếu hoàn tiền."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RmaResponse createRma(@Valid @RequestBody CreateRmaRequest request) {
        return rmaService.createRma(request);
    }

    /**
     * CSKH xét duyệt yêu cầu Đổi/Trả hàng (US25).
     */
    @Operation(
            summary = "Xét duyệt Yêu cầu Đổi/Trả (US25)",
            description = "CSKH/Admin xem xét duyệt (DA_DUYET) hoặc từ chối (TU_CHOI) yêu cầu RMA, đồng thời bổ sung ghi chú kiểm định hàng về kho."
    )
    @PutMapping("/{maYeuCauRma}/review")
    public RmaResponse reviewRma(
            @Parameter(description = "Mã yêu cầu RMA cần xét duyệt", example = "RMA261024-ABCDEF")
            @PathVariable String maYeuCauRma,
            @Valid @RequestBody ReviewRmaRequest request) {
        return rmaService.reviewRma(maYeuCauRma, request);
    }

    /**
     * Xem danh sách tất cả các yêu cầu RMA (US25).
     */
    @Operation(
            summary = "Xem danh sách yêu cầu RMA (US25)",
            description = "Dành cho CSKH theo dõi danh sách các đơn RMA, có thể lọc theo trạng thái (CHO_DUYET, DA_DUYET, TU_CHOI...)."
    )
    @GetMapping
    public List<RmaResponse> getAllRma(
            @Parameter(description = "Lọc theo trạng thái yêu cầu", example = "CHO_DUYET")
            @RequestParam(required = false) String trangThai) {
        return rmaService.getAllRma(trangThai);
    }

    /**
     * Xem danh sách yêu cầu RMA của một khách hàng (US24).
     */
    @Operation(
            summary = "Lịch sử yêu cầu Đổi/Trả của khách hàng (US24)",
            description = "Khách hàng tra cứu danh sách các đơn khiếu nại đổi trả của mình."
    )
    @GetMapping("/customer/{khachHangId}")
    public List<RmaResponse> getRmaByCustomer(
            @Parameter(description = "ID khách hàng", example = "1")
            @PathVariable Long khachHangId) {
        return rmaService.getRmaByCustomer(khachHangId);
    }

    /**
     * Xem chi tiết một yêu cầu RMA theo mã.
     */
    @Operation(
            summary = "Xem chi tiết yêu cầu RMA",
            description = "Tra cứu đầy đủ thông tin chi tiết một yêu cầu RMA kèm danh sách sản phẩm và ghi chú kiểm định."
    )
    @GetMapping("/{maYeuCauRma}")
    public RmaResponse getRmaByCode(
            @Parameter(description = "Mã yêu cầu RMA", example = "RMA261024-ABCDEF")
            @PathVariable String maYeuCauRma) {
        return rmaService.getRmaByCode(maYeuCauRma);
    }
}
