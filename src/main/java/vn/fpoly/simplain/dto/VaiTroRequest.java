package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Đối tượng truyền dữ liệu (DTO) khi thêm mới hoặc chỉnh sửa Vai trò (US03).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dữ liệu thêm mới hoặc cập nhật Vai trò")
public class VaiTroRequest {

    @Schema(description = "ID vai trò (khi cập nhật)", example = "1")
    private Long id;

    @Schema(description = "Mã vai trò (chữ hoa, số, gạch dưới)", example = "QUAN_LY_KHO")
    @NotBlank(message = "Mã vai trò không được để trống")
    @Size(max = 50, message = "Mã vai trò tối đa 50 ký tự")
    @Pattern(regexp = "^[A-Z0-9_]{2,50}$", message = "Mã vai trò chỉ gồm chữ in hoa, chữ số và gạch dưới (VD: ADMIN, CSKH, BAN_HANG, QUAN_LY_KHO)")
    private String maVaiTro;

    @Schema(description = "Tên hiển thị vai trò", example = "Quản lý kho hàng")
    @NotBlank(message = "Tên vai trò không được để trống")
    @Size(max = 100, message = "Tên vai trò tối đa 100 ký tự")
    private String tenVaiTro;

    @Schema(description = "Mô tả nhiệm vụ vai trò", example = "Chịu trách nhiệm quản lý nhập xuất tồn kho")
    @Size(max = 255, message = "Mô tả vai trò tối đa 255 ký tự")
    private String moTa;

    @Schema(description = "Trạng thái vai trò (1: Hoạt động, 0: Vô hiệu hóa)", example = "1")
    @NotNull(message = "Trạng thái không được để trống")
    @Builder.Default
    private Integer trangThai = 1;

    @Schema(description = "Danh sách mã quyền hạn được cấp cho vai trò (VD: DASHBOARD, SAN_PHAM, BAN_HANG_POS...)")
    private java.util.List<String> danhSachQuyen;
}
