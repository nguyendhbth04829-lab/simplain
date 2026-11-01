package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO Trả về Lịch sử thay đổi trạng thái đơn hàng & hành trình vận đơn (US22 - Mod 5 - Hưng).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Lịch sử cập nhật trạng thái đơn hàng & vận đơn (US22)")
public class LichSuTrangThaiDonHangResponse {

    @Schema(description = "ID bản ghi lịch sử", example = "1")
    private Long id;

    @Schema(description = "ID đơn hàng", example = "10")
    private Long donHangId;

    @Schema(description = "Mã đơn hàng", example = "DH1730000000")
    private String maDonHang;

    @Schema(description = "Mã trạng thái mới", example = "DANG_GIAO")
    private String maTrangThai;

    @Schema(description = "Tên trạng thái hiển thị", example = "Đang giao hàng")
    private String tenTrangThai;

    @Schema(description = "Mã màu Hex trạng thái", example = "#8B5CF6")
    private String maMauHex;

    @Schema(description = "ID nhân viên thực hiện", example = "1")
    private Long nguoiThucHienId;

    @Schema(description = "Tên nhân viên thực hiện", example = "Nguyễn Văn Admin")
    private String tenNguoiThucHien;

    @Schema(description = "Mã nhân viên", example = "NV001")
    private String maNhanVien;

    @Schema(description = "Ghi chú cập nhật", example = "Đã bàn giao cho đơn vị vận chuyển GHN")
    private String ghiChu;

    @Schema(description = "Thời gian chuyển trạng thái")
    private LocalDateTime ngayChuyen;

    @Schema(description = "Chuỗi định dạng ngày giờ (dd/MM/yyyy HH:mm:ss)", example = "26/10/2026 14:30:00")
    private String formattedNgayChuyen;
}
