package vn.fpoly.simplain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Phản hồi Ajax khi khách hàng nhấn tim / toggle sản phẩm yêu thích (US14).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YeuThichToggleResponse {

    /**
     * Trạng thái thực hiện thành công hay thất bại.
     */
    private boolean thanhCong;

    /**
     * Trạng thái sau toggle: true nếu vừa được thêm vào yêu thích, false nếu vừa bị bỏ thích.
     */
    private boolean daYeuThich;

    /**
     * Thông báo hiển thị cho người dùng (Toast notification).
     */
    private String thongBao;

    /**
     * Mã ID sản phẩm vừa thao tác.
     */
    private Long maSanPhamId;

    /**
     * Tổng số lượng sản phẩm khách hàng đang yêu thích (để cập nhật Badge trên Header).
     */
    private long tongSoYeuThich;

    /**
     * Cờ đánh dấu nếu chưa đăng nhập (để client mở modal chuyển hướng đăng nhập).
     */
    @Builder.Default
    private boolean chuaDangNhap = false;
}
