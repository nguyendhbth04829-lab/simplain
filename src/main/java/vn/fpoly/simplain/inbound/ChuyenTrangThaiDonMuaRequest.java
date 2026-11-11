package vn.fpoly.simplain.inbound;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChuyenTrangThaiDonMuaRequest {
    @NotNull(message = "Trạng thái mới không được để trống")
    private TrangThaiDonMuaHang trangThai;
    private String ghiChu;
}
