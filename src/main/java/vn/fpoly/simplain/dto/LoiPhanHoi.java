package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Cấu trúc thông điệp lỗi tiếng Việt trả về cho client.
 */
@Schema(description = "Phản hồi chi tiết lỗi tiếng Việt")
public record LoiPhanHoi(

        @Schema(description = "Mã trạng thái HTTP", example = "400")
        int maTrangThai,

        @Schema(description = "Tên trạng thái", example = "DU_LIEU_KHONG_HOP_LE")
        String trangThai,

        @Schema(description = "Thông điệp lỗi", example = "Dữ liệu không hợp lệ")
        String thongDiep,

        @Schema(description = "Chi tiết lỗi của từng trường nếu có")
        Map<String, String> chiTietLoi,

        @Schema(description = "Thời điểm xảy ra lỗi")
        LocalDateTime thoiGian
) {
    public LoiPhanHoi(int maTrangThai, String trangThai, String thongDiep) {
        this(maTrangThai, trangThai, thongDiep, null, LocalDateTime.now());
    }

    public LoiPhanHoi(int maTrangThai, String trangThai, String thongDiep, Map<String, String> chiTietLoi) {
        this(maTrangThai, trangThai, thongDiep, chiTietLoi, LocalDateTime.now());
    }
}
