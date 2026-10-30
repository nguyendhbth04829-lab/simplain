package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Thống kê số sao và lượt đánh giá của sản phẩm (US15 - Tiêu chí 3).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeDanhGiaResponse {

    private long tongDanhGia;
    private double saoTrungBinh;

    private long soLuong5Sao;
    private long soLuong4Sao;
    private long soLuong3Sao;
    private long soLuong2Sao;
    private long soLuong1Sao;

    private int phanTram5Sao;
    private int phanTram4Sao;
    private int phanTram3Sao;
    private int phanTram2Sao;
    private int phanTram1Sao;
}
