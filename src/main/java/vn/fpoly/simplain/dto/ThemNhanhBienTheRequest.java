package vn.fpoly.simplain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO nhận dữ liệu thêm nhanh ma trận nhiều biến thể 1 lần SIMPLAIN Matrix.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemNhanhBienTheRequest {

    private Long maSanPhamId;

    @Valid
    @NotEmpty(message = "Danh sách biến thể không được để trống!")
    private List<BienTheHangLoatItemRequest> danhSachBienThe;
}
