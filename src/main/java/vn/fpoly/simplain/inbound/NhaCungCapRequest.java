package vn.fpoly.simplain.inbound;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NhaCungCapRequest {

    private String maNcc;

    @NotBlank(message = "Tên nhà cung cấp / Xưởng may không được để trống")
    private String tenNcc;

    private String soDienThoai;
    private String email;
    private String diaChi;
    private String ghiChu;
    private Boolean kichHoat;
}
