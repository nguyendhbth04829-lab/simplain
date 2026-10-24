package vn.fpoly.simplain.rma.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO yêu cầu xét duyệt đơn RMA từ CSKH (US25).
 */
@Schema(description = "Dữ liệu xét duyệt yêu cầu đổi trả RMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRmaRequest {

    @Schema(description = "Trạng thái phê duyệt mới: DA_DUYET, TU_CHOI, DA_NHAN_HANG, HOAN_TAT", example = "DA_DUYET")
    @NotBlank(message = "Trạng thái phê duyệt không được để trống")
    private String trangThai;

    @Schema(description = "ID nhân viên CSKH hoặc Admin xử lý xét duyệt", example = "5")
    @NotNull(message = "ID người duyệt không được để trống")
    private Long nguoiDuyetId;

    @Schema(description = "Ghi chú kiểm định hàng về kho hoặc lý do từ chối", example = "Đã kiểm định tem mác hợp lệ, đồng ý đổi hàng")
    private String ghiChuKiemDinh;
}
