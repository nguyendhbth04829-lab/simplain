package vn.fpoly.simplain.inbound;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichSuHopTacRequest {

    @NotNull(message = "Loại hoạt động không được để trống")
    private LoaiHoatDongHopTac loaiHoatDong;

    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    private String ghiChu;

    @Size(max = 100, message = "Kết quả không được vượt quá 100 ký tự")
    private String ketQua;
}
