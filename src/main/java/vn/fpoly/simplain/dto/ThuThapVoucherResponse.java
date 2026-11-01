package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO phản hồi kết quả thao tác Thu thập / Lưu voucher vào ví voucher_so_huu (US27 - Tiêu chí 1).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThuThapVoucherResponse {

    private boolean thanhCong;
    private String thongBao;
    private boolean yeuCauDangNhap;
    private Long voucherId;
    private String maCode;
    private Long viVoucherId;
    private Integer tongSoVoucherTrongVi;
}
