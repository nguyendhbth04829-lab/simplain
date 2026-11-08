package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kết quả kiểm tra điều kiện áp dụng thanh toán COD (Anti-Fraud / Chống Gom Hàng & Chống Bom Hàng).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KiemTraCodResult {
    private boolean choPhepCod;
    private String lyDoChanCod;
    private long soDonDangGiao;
    private long soLanBomHang;
}
