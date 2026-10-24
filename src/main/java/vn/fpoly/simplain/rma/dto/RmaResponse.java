package vn.fpoly.simplain.rma.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO phản hồi yêu cầu Đổi/Trả hàng RMA (US24, US25).
 */
@Schema(description = "Thông tin kết quả yêu cầu đổi trả RMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RmaResponse {

    @Schema(description = "ID yêu cầu trong cơ sở dữ liệu", example = "1")
    private Long id;

    @Schema(description = "Mã yêu cầu RMA duy nhất", example = "RMA-261024-X9Y8Z7")
    private String maYeuCauRma;

    @Schema(description = "ID đơn hàng cũ", example = "1")
    private Long donHangId;

    @Schema(description = "Mã đơn hàng cũ", example = "DH261024-ABCDEF")
    private String maDonHang;

    @Schema(description = "ID khách hàng yêu cầu", example = "1")
    private Long khachHangId;

    @Schema(description = "Tên khách hàng", example = "Nguyễn Văn Hưng")
    private String tenKhachHang;

    @Schema(description = "Loại yêu cầu: DOI_HANG hoặc TRA_HANG_HOAN_TIEN", example = "DOI_HANG")
    private String loaiDoiTra;

    @Schema(description = "Lý do đổi trả", example = "Sản phẩm bị lỗi chỉ may")
    private String lyDo;

    @Schema(description = "Số tiền hoàn lại (nếu có)", example = "250000")
    private BigDecimal soTienHoan;

    @Schema(description = "Thông tin tài khoản nhận tiền hoàn", example = "Vietcombank - 0123456789")
    private String thongTinNganHang;

    @Schema(description = "Trạng thái phê duyệt: CHO_DUYET, DA_DUYET, TU_CHOI, DA_NHAN_HANG, HOAN_TAT", example = "CHO_DUYET")
    private String trangThai;

    @Schema(description = "ID nhân viên CSKH đã xử lý duyệt", example = "5")
    private Long nguoiDuyetId;

    @Schema(description = "Ghi chú kiểm định hàng về kho", example = "Đã kiểm tra hàng đạt điều kiện")
    private String ghiChuKiemDinh;

    @Schema(description = "Thời gian tạo yêu cầu", example = "2026-10-24T21:45:00")
    private LocalDateTime ngayTao;

    @Schema(description = "Danh sách các sản phẩm cần đổi/trả")
    private List<RmaItemResponse> items;
}
