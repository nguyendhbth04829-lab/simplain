package vn.fpoly.simplain.inbound;

import org.springframework.stereotype.Service;
import vn.fpoly.simplain.entity.BienTheSanPham;

/**
 * Chính sách phân bổ tồn kho deterministic cho đúng lát cắt Kho/Tồn kho.
 *
 * <p><strong>Quy tắc đã chốt:</strong> Simplain dùng một kho vật lý chung.
 * Kệ Offline và Web Online cùng đọc từ {@code bien_the_san_pham.so_luong};
 * hệ thống không tách thành hai rổ số lượng và không cộng dồn hai kênh.</p>
 *
 * <ul>
 *     <li>Biến thể ngừng kinh doanh: không lên kệ Offline, không hiển thị Web.</li>
 *     <li>Sản phẩm ngừng kinh doanh: vẫn có thể còn trên kệ Offline (hàng vật lý),
 *         nhưng ẩn khỏi Web Online.</li>
 *     <li>Hết hàng hoặc tồn kho âm (dữ liệu cũ): cả hai kênh đều nhận 0.</li>
 * </ul>
 *
 * <p>Policy này chỉ đọc dữ liệu, không ghi kho và không thay đổi trạng thái đơn hàng.</p>
 */
@Service
public class PhanBoTonKhoService {

    /**
     * Phân bổ một biến thể theo trạng thái kinh doanh của sản phẩm cha.
     *
     * @param bienThe                biến thể SKU, có thể null nếu không tìm thấy
     * @param sanPhamDangKinhDoanh   sản phẩm cha có đang được mở bán hay không
     * @return kết quả bất biến, luôn cùng giá trị với cùng một snapshot đầu vào
     */
    public PhanBoTonKho phanBo(BienTheSanPham bienThe, boolean sanPhamDangKinhDoanh) {
        if (bienThe == null) {
            return new PhanBoTonKho(
                    null,
                    null,
                    0,
                    false,
                    false,
                    PhanBoTonKho.NGUON_DUNG_CHUNG,
                    LyDoPhanBo.KHONG_CO_BIEN_THE
            );
        }

        // Dữ liệu cũ có thể còn số âm; luôn chuẩn hóa về 0 thay vì để lọt ra giao diện.
        int tonKhoVatLy = bienThe.getSoLuong() == null ? 0 : Math.max(0, bienThe.getSoLuong());
        boolean bienTheKichHoat = Boolean.TRUE.equals(bienThe.getKichHoat());
        boolean conHang = tonKhoVatLy > 0;

        boolean hienThiKeOffline = bienTheKichHoat && conHang;
        boolean hienThiWebOnline = bienTheKichHoat && sanPhamDangKinhDoanh && conHang;

        LyDoPhanBo lyDo;
        if (!bienTheKichHoat) {
            lyDo = LyDoPhanBo.BIEN_THE_NGUNG_KINH_DOANH;
        } else if (!conHang) {
            lyDo = LyDoPhanBo.HET_HANG;
        } else if (!sanPhamDangKinhDoanh) {
            lyDo = LyDoPhanBo.SAN_PHAM_NGUNG_KINH_DOANH;
        } else {
            lyDo = LyDoPhanBo.SAN_SANG;
        }

        return new PhanBoTonKho(
                bienThe.getId(),
                bienThe.getMaSku(),
                tonKhoVatLy,
                hienThiKeOffline,
                hienThiWebOnline,
                PhanBoTonKho.NGUON_DUNG_CHUNG,
                lyDo
        );
    }

    /**
     * Số lượng khả dụng cho kệ Offline, đã clamp và kiểm tra trạng thái biến thể.
     */
    public int soLuongKeOffline(BienTheSanPham bienThe) {
        return phanBo(bienThe, true).soLuongKeOffline();
    }

    /**
     * Số lượng khả dụng cho Web Online, đã clamp và kiểm tra cả sản phẩm cha.
     */
    public int soLuongWebOnline(BienTheSanPham bienThe, boolean sanPhamDangKinhDoanh) {
        return phanBo(bienThe, sanPhamDangKinhDoanh).soLuongWebOnline();
    }
}
