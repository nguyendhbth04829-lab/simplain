package vn.fpoly.simplain.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Danh mục Chức năng Hệ thống chuẩn phục vụ Phân quyền động (Dynamic RBAC).
 * Cho phép thiết lập quyền chi tiết cho từng Vai trò hiện tại (ADMIN, CSKH, BAN_HANG)
 * và mở rộng linh hoạt cho các vai trò mới trong tương lai (THU_KHO, MARKETING, KE_TOAN,...).
 */
@Getter
public enum ChucNangHeThong {

    DASHBOARD(
            "DASHBOARD",
            "Báo cáo Dashboard & Thống kê",
            "TỔNG QUAN",
            "fa-solid fa-chart-pie",
            "/admin/dashboard",
            "Xem các chỉ số kinh doanh, số đơn hôm nay, doanh thu trong ngày và biểu đồ realtime."
    ),

    SAN_PHAM(
            "SAN_PHAM",
            "Quản lý Hàng hóa & Sản phẩm",
            "HÀNG HÓA & KHO",
            "fa-solid fa-shirt",
            "/admin/san-pham/hien-thi",
            "Quản lý danh mục, sản phẩm, biến thể SKU, tồn kho, màu sắc, size, chất liệu và video."
    ),

    BAN_HANG_POS(
            "BAN_HANG_POS",
            "Bán hàng tại quầy (POS)",
            "BÁN HÀNG & GIAO DỊCH",
            "fa-solid fa-cash-register",
            "/admin/ban-hang-tai-quay",
            "Tạo hóa đơn chờ, quét mã, chọn sản phẩm, áp voucher/điểm và thanh toán tại quầy."
    ),

    DON_HANG(
            "DON_HANG",
            "Quản lý Đơn hàng",
            "BÁN HÀNG & GIAO DỊCH",
            "fa-solid fa-box-open",
            "/admin/don-hang/hien-thi",
            "Tra cứu, theo dõi tiến trình đơn hàng, chuyển trạng thái và in hóa đơn."
    ),

    DOI_TRA(
            "DOI_TRA",
            "Xét duyệt Đổi / Trả (RMA)",
            "CHĂM SÓC KHÁCH HÀNG",
            "fa-solid fa-arrows-rotate",
            "/admin/doi-tra/hien-thi",
            "Tiếp nhận, kiểm tra lý do đổi trả hàng của khách, phê duyệt hoặc từ chối yêu cầu RMA."
    ),

    KHUYEN_MAI(
            "KHUYEN_MAI",
            "Quản lý Khuyến mãi & Flash Sale",
            "MARKETING & KÍCH CẦU",
            "fa-solid fa-bolt",
            "/admin/voucher/hien-thi",
            "Thiết lập mã giảm giá Voucher, gói Combo kích cầu và khung giờ Flash Sale sốc."
    ),

    THANH_TOAN(
            "THANH_TOAN",
            "Cổng thanh toán & Giao dịch",
            "BÁN HÀNG & GIAO DỊCH",
            "fa-solid fa-credit-card",
            "/admin/phuong-thuc-thanh-toan/hien-thi",
            "Cấu hình phương thức thanh toán VNPay, MoMo, COD và đối soát lịch sử giao dịch."
    ),

    NOI_DUNG(
            "NOI_DUNG",
            "Truyền thông & Nội dung Web",
            "TRUYỀN THÔNG & MARKETING",
            "fa-solid fa-newspaper",
            "/admin/noi-dung/blog",
            "Quản lý bài viết Blog tin tức thời trang chuẩn SEO và các banner quảng cáo trang chủ."
    ),

    KHACH_HANG(
            "KHACH_HANG",
            "Quản lý Khách hàng & Hội viên",
            "CHĂM SÓC KHÁCH HÀNG",
            "fa-solid fa-users",
            "/admin/khach-hang/hien-thi",
            "Tra cứu hồ sơ khách hàng, phân hạng hội viên CoolClub và lịch sử tích điểm."
    ),

    HE_THONG(
            "HE_THONG",
            "Quản trị Hệ thống & Phân quyền",
            "HỆ THỐNG & NHÂN SỰ",
            "fa-solid fa-user-shield",
            "/admin/nhan-vien/hien-thi",
            "Quản lý tài khoản nhân viên, thiết lập vai trò và cấp phát phân quyền động."
    );

    private final String maQuyen;
    private final String tenChucNang;
    private final String nhomChucNang;
    private final String icon;
    private final String duongDanMacDinh;
    private final String moTa;

    ChucNangHeThong(String maQuyen, String tenChucNang, String nhomChucNang, String icon, String duongDanMacDinh, String moTa) {
        this.maQuyen = maQuyen;
        this.tenChucNang = tenChucNang;
        this.nhomChucNang = nhomChucNang;
        this.icon = icon;
        this.duongDanMacDinh = duongDanMacDinh;
        this.moTa = moTa;
    }

    public static ChucNangHeThong timTheoMa(String ma) {
        if (ma == null) return null;
        for (ChucNangHeThong cn : values()) {
            if (cn.getMaQuyen().equalsIgnoreCase(ma.trim())) {
                return cn;
            }
        }
        return null;
    }

    /**
     * Nhóm tất cả các chức năng theo nhóm phục vụ hiển thị ma trận phân quyền.
     */
    public static Map<String, List<ChucNangHeThong>> layTheoNhom() {
        return Arrays.stream(values())
                .collect(Collectors.groupingBy(
                        ChucNangHeThong::getNhomChucNang,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }
}
