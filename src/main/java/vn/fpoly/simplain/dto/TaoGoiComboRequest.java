package vn.fpoly.simplain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Form DTO phục vụ Admin thiết lập / cập nhật Gói Combo kích cầu giá fix (US28 - Mod 6).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoGoiComboRequest {

    private Long id;

    @NotBlank(message = "Mã gói combo không được để trống")
    @Size(min = 3, max = 50, message = "Mã gói combo phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Mã combo chỉ gồm chữ cái, chữ số, gạch ngang (-) hoặc gạch dưới (_)")
    private String maCombo;

    @NotBlank(message = "Tên gói combo không được để trống")
    @Size(min = 3, max = 255, message = "Tên gói combo phải từ 3 đến 255 ký tự")
    private String tenCombo;

    @NotBlank(message = "Đường dẫn slug không được để trống")
    @Size(min = 3, max = 255, message = "Đường dẫn slug phải từ 3 đến 255 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug chỉ gồm chữ thường không dấu, số và gạch ngang (VD: tu-chon-3-ao-polo-299k)")
    private String duongDanSlug;

    @NotNull(message = "Giá trọn gói combo không được để trống")
    @DecimalMin(value = "1000.0", message = "Giá gói combo tối thiểu phải từ 1.000 VNĐ")
    @DecimalMax(value = "999999999.0", message = "Giá gói combo không được vượt quá 999.999.999 VNĐ")
    private BigDecimal giaCombo;

    @NotNull(message = "Số món quy định không được để trống")
    @Min(value = 2, message = "Gói combo kích cầu yêu cầu tối thiểu từ 2 món trở lên (VD: Tự chọn 3 áo)")
    @Max(value = 20, message = "Số món quy định trong một gói combo tối đa là 20 món")
    private Integer soMonQuyDinh;

    private String moTa;

    @Builder.Default
    private Boolean kichHoat = true;

    /**
     * Danh sách ID các sản phẩm (áo, quần...) được áp dụng trong gói combo [san_pham_trong_combo].
     */
    @NotEmpty(message = "Vui lòng chọn ít nhất 1 sản phẩm áp dụng cho gói combo này")
    @Builder.Default
    private List<Long> danhSachSanPhamIds = new ArrayList<>();
}
