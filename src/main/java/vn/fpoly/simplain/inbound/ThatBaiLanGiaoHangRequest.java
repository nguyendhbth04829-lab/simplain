package vn.fpoly.simplain.inbound;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ThatBaiLanGiaoHangRequest {
    @NotBlank(message = "Cần ghi rõ lý do giao thất bại")
    private String lyDo;
    @NotBlank(message = "Cần chọn bên chịu trách nhiệm")
    @Pattern(regexp = "NHA_CUNG_CAP|CUA_HANG|BEN_VAN_CHUYEN|KHAC", message = "Bên chịu trách nhiệm không hợp lệ")
    private String benChiuTrachNhiem;
    private String bangChung;
}
