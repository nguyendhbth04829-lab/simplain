package vn.fpoly.simplain.inbound;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhaCungCapSkuRequest {
    @NotNull(message = "Danh sách SKU không được để trống dữ liệu")
    private List<@NotBlank(message = "Mã SKU không được để trống") String> maSku;
}
