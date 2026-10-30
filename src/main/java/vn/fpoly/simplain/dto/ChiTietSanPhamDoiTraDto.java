package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO hiển thị từng mặt hàng trong phiếu yêu cầu đổi/trả RMA (US24).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin mặt hàng trong phiếu RMA (US24)")
public class ChiTietSanPhamDoiTraDto {

    private Long id;
    private Long chiTietDonHangId;
    private String tenSanPham;
    private String hinhAnhUrl;
    private String skuCu;
    private String phanLoaiCu; // Màu + Size
    private Integer soLuongTra;
    private BigDecimal donGia;
    private BigDecimal thanhTienTra;
    private String tinhTrangLoi;

    // Thông tin nếu đổi SKU mới
    private Long maBienTheMoiId;
    private String skuMoi;
    private String phanLoaiMoi; // Màu + Size
    private String hinhAnhMoiUrl;
}
