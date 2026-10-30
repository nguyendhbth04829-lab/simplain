package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO biểu mẫu Xét duyệt Yêu cầu Đổi/Trả (RMA) dành cho CSKH & Quản trị kho (US25 - Form Validation).
 * - Tiêu chí 2: Đổi trạng thái (Approve / Reject / Hoàn thành).
 * - Tiêu chí 3: Bổ sung ghi chú kiểm định hàng về kho.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Biểu mẫu CSKH xét duyệt yêu cầu RMA (US25)")
public class XetDuyetRmaRequest {

    @NotBlank(message = "Vui lòng chọn trạng thái cập nhật")
    @Pattern(regexp = "^(DA_TIEP_NHAN|DANG_XU_LY|HOAN_THANH|TU_CHOI)$", 
             message = "Trạng thái không hợp lệ. Chỉ chấp nhận: DA_TIEP_NHAN, DANG_XU_LY, HOAN_THANH, TU_CHOI")
    @Schema(description = "Trạng thái mới: DA_TIEP_NHAN, DANG_XU_LY, HOAN_THANH, TU_CHOI", example = "DA_TIEP_NHAN")
    private String trangThaiMoi;

    @Size(max = 500, message = "Ghi chú kiểm định/phản hồi không được vượt quá 500 ký tự")
    @Schema(description = "Ghi chú kiểm định hàng về kho hoặc lý do từ chối", example = "Đã nhận hàng trả về kho: Đúng mô tả lỗi, tem mác nguyên vẹn.")
    private String ghiChuAdmin;

    @Builder.Default
    @Schema(description = "Tùy chọn nhập kho lại sản phẩm cũ nếu kiểm định đạt chuẩn tái chế/tái sử dụng")
    private Boolean nhapLaiKho = false;

    @Schema(description = "Mã giao dịch chuyển khoản hoàn tiền cho khách (nếu là hoàn tiền)")
    private String maGiaoDichNganHang;

    @Size(max = 1000, message = "Đường dẫn link ảnh bill không được quá 1000 ký tự")
    @Schema(description = "Link URL ảnh biên lai chuyển khoản / hóa đơn xuất đổi (khi gửi lại ảnh bill cho khách qua link)")
    private String hinhAnhBill;
}
