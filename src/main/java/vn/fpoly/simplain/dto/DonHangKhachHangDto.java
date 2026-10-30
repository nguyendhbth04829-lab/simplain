package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.fpoly.simplain.entity.DonHang;

import java.util.List;

/**
 * DTO tổng hợp thông tin đơn hàng cá nhân hiển thị trên giao diện của khách hàng (US23).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin đơn hàng hiển thị cho khách hàng (US23)")
public class DonHangKhachHangDto {

    private DonHang donHang;
    private List<DonHangItemDto> danhSachItem;
    private int tongSoLuong;
    private boolean coTheHuy;
    private int stepperIndex; // 1: Chờ xác nhận, 2: Đang chuẩn bị, 3: Đang giao, 4: Hoàn thành, -1: Đã hủy
    private List<LichSuTrangThaiDonHangResponse> lichSuVanDon;
    private String hinhAnhDauTien;
    private String tenDonViVanChuyen;
    private String moTaTrangThaiHienTai;
}
