package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO biểu mẫu tiếp nhận Yêu cầu Đổi/Trả (RMA) online từ khách hàng (US24 - Form Validation).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Biểu mẫu gửi Yêu cầu Đổi/Trả hàng online (US24)")
public class TaoYeuCauDoiTraRequest {

    @NotBlank(message = "Mã đơn hàng không được để trống")
    @Schema(description = "Mã đơn hàng đã hoàn thành cần đổi trả", example = "SMPL-1729864000123")
    private String maDonHang;

    @NotBlank(message = "Vui lòng chọn hình thức: Đổi hàng mới hoặc Trả hàng hoàn tiền")
    @Pattern(regexp = "^(DOI_HANG|TRA_HANG_HOAN_TIEN)$", message = "Hình thức đổi trả không hợp lệ (chỉ chấp nhận DOI_HANG hoặc TRA_HANG_HOAN_TIEN)")
    @Schema(description = "Hình thức: DOI_HANG (đổi SKU mới) hoặc TRA_HANG_HOAN_TIEN (nhận lại tiền qua STK)", example = "DOI_HANG")
    private String loaiDoiTra;

    @NotBlank(message = "Vui lòng chọn hoặc nhập lý do yêu cầu đổi/trả")
    @Size(min = 5, max = 255, message = "Lý do yêu cầu đổi trả phải từ 5 đến 255 ký tự")
    @Schema(description = "Lý do đổi/trả tổng thể", example = "Sản phẩm bị lỗi đường may và không vừa kích cỡ")
    private String lyDo;

    @Schema(description = "Tên ngân hàng nhận tiền hoàn (khi chọn Trả hàng hoàn tiền)", example = "Vietcombank")
    private String tenNganHang;

    @Schema(description = "Số tài khoản nhận tiền hoàn (khi chọn Trả hàng hoàn tiền)", example = "1029384756")
    private String soTaiKhoan;

    @Schema(description = "Tên chủ tài khoản nhận tiền hoàn (khi chọn Trả hàng hoàn tiền)", example = "NGUYEN VAN A")
    private String tenChuTaiKhoan;

    @Size(max = 2000, message = "Đường dẫn hình ảnh minh chứng không được quá 2000 ký tự")
    @Schema(description = "Link ảnh chụp minh chứng sản phẩm lỗi (nếu có)")
    private String hinhAnhMinhChung;

    // PHẦN NHẬP ĐỊA CHỈ NHẬN HÀNG (CHUẨN SHOPEE)
    @Size(max = 150, message = "Họ tên người nhận không được quá 150 ký tự")
    @Schema(description = "Họ tên người nhận hàng đổi hoặc người gửi trả hàng", example = "Nguyễn Văn A")
    private String tenNguoiNhan;

    @Schema(description = "Số điện thoại liên hệ nhận hàng đổi trả", example = "0987654321")
    private String soDienThoaiNhan;

    @Size(max = 500, message = "Địa chỉ nhận hàng không được quá 500 ký tự")
    @Schema(description = "Địa chỉ nhận hàng đổi mới hoặc địa chỉ shipper đến lấy hàng", example = "Số 12, Ngõ 45 Đường Cầu Giấy, Hà Nội")
    private String diaChiNhanHang;

    @Schema(description = "Tỉnh / Thành phố nhận hàng trả (US13 Combobox)", example = "Hà Nội")
    private String tinhThanh;

    @Schema(description = "Quận / Huyện nhận hàng trả (US13 Combobox)", example = "Cầu Giấy")
    private String quanHuyen;

    @Schema(description = "Phường / Xã nhận hàng trả (US13 Combobox)", example = "Dịch Vọng Hậu")
    private String phuongXa;

    @Schema(description = "Địa chỉ chi tiết nhận hàng trả (Số nhà, đường)", example = "Số 12 ngõ 45 Duy Tân")
    private String diaChiChiTiet;

    @Size(max = 255, message = "Ghi chú giao nhận không được quá 255 ký tự")
    @Schema(description = "Ghi chú dành cho shipper khi đến nhận/giao hàng", example = "Giao giờ hành chính, gọi trước 15 phút")
    private String ghiChuGiaoHang;

    @Valid
    @Builder.Default
    @Schema(description = "Danh sách các mặt hàng trong đơn cũ được tick chọn đổi/trả")
    private List<ChiTietDoiTraItemRequest> items = new ArrayList<>();
}
