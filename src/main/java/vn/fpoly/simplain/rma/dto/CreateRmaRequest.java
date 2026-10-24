package vn.fpoly.simplain.rma.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO yêu cầu tạo mới Đổi/Trả hàng (RMA) online từ khách hàng (US24).
 */
@Schema(description = "Dữ liệu yêu cầu tạo mới đổi trả hàng (RMA)")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRmaRequest {

    @Schema(description = "ID đơn hàng cũ chứa sản phẩm cần đổi/trả", example = "1")
    @NotNull(message = "ID đơn hàng không được để trống")
    private Long donHangId;

    @Schema(description = "ID khách hàng yêu cầu", example = "1")
    @NotNull(message = "ID khách hàng không được để trống")
    private Long khachHangId;

    @Schema(description = "Loại yêu cầu: DOI_HANG (Đổi sản phẩm) hoặc TRA_HANG_HOAN_TIEN (Trả hàng hoàn tiền)", example = "DOI_HANG")
    @NotBlank(message = "Loại đổi trả không được để trống (DOI_HANG hoặc TRA_HANG_HOAN_TIEN)")
    private String loaiDoiTra;

    @Schema(description = "Lý do đổi trả", example = "Sản phẩm bị lỗi đường may và nhầm size")
    @NotBlank(message = "Lý do đổi trả không được để trống")
    private String lyDo;

    @Schema(description = "Số tiền yêu cầu hoàn lại (nếu chọn Trả hàng hoàn tiền)", example = "250000")
    @Builder.Default
    private BigDecimal soTienHoan = BigDecimal.ZERO;

    @Schema(description = "Thông tin số tài khoản nhận tiền hoàn (Ngân hàng, STK, Tên chủ tài khoản)", example = "Vietcombank - 1012345678 - NGUYEN VAN HUNG")
    private String thongTinNganHang;

    @Schema(description = "Danh sách các sản phẩm cần đổi/trả")
    @NotEmpty(message = "Danh sách sản phẩm đổi trả không được để trống")
    private List<@Valid CreateRmaItemRequest> items;
}
