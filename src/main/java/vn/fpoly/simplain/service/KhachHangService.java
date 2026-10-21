/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - Xử lý nghiệp vụ xem hồ sơ, cập nhật thông tin cá nhân và đổi mật khẩu
 */
package vn.fpoly.simplain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.CapNhatHoSoRequest;
import vn.fpoly.simplain.dto.DoiMatKhauRequest;
import vn.fpoly.simplain.dto.HoSoKhachHangResponse;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.exception.ChuaDangNhapException;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.exception.TaiKhoanBiKhoaException;
import vn.fpoly.simplain.exception.XungDotDuLieuException;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.util.MaHoaMatKhau;

/**
 * Lớp dịch vụ xử lý nghiệp vụ Hồ sơ khách hàng và Đổi mật khẩu (US12).
 */
@Service
public class KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final MaHoaMatKhau maHoaMatKhau;

    public KhachHangService(KhachHangRepository khachHangRepository, MaHoaMatKhau maHoaMatKhau) {
        this.khachHangRepository = khachHangRepository;
        this.maHoaMatKhau = maHoaMatKhau;
    }

    /**
     * Lấy thông tin hồ sơ của khách hàng theo ID.
     *
     * @param idKhachHang ID khách hàng
     * @return thông tin chi tiết hồ sơ
     */
    @Transactional(readOnly = true)
    public HoSoKhachHangResponse layHoSo(Long idKhachHang) {
        KhachHang khachHang = layKhachHangHopLe(idKhachHang);
        return HoSoKhachHangResponse.tuThucThe(khachHang);
    }

    /**
     * Cập nhật thông tin cá nhân khách hàng (Họ tên, SĐT, Email, Giới tính, Ngày sinh, Ảnh).
     *
     * @param idKhachHang ID khách hàng
     * @param yeuCau      thông tin cập nhật
     * @return thông tin hồ sơ sau khi cập nhật
     */
    @Transactional
    public HoSoKhachHangResponse capNhatHoSo(Long idKhachHang, CapNhatHoSoRequest yeuCau) {
        KhachHang khachHang = layKhachHangHopLe(idKhachHang);

        // Kiểm tra chống trùng lặp email nếu có thay đổi
        if (yeuCau.email() != null && !yeuCau.email().isBlank()) {
            String emailMoi = yeuCau.email().trim();
            if (khachHangRepository.existsByEmailAndIdNot(emailMoi, idKhachHang)) {
                throw new XungDotDuLieuException("Email '" + emailMoi + "' đã được sử dụng bởi tài khoản khác");
            }
            khachHang.setEmail(emailMoi);
        } else {
            khachHang.setEmail(null);
        }

        // Kiểm tra chống trùng lặp số điện thoại nếu có thay đổi
        if (yeuCau.soDienThoai() != null && !yeuCau.soDienThoai().isBlank()) {
            String sdtMoi = yeuCau.soDienThoai().trim();
            if (khachHangRepository.existsBySoDienThoaiAndIdNot(sdtMoi, idKhachHang)) {
                throw new XungDotDuLieuException("Số điện thoại '" + sdtMoi + "' đã được sử dụng bởi tài khoản khác");
            }
            khachHang.setSoDienThoai(sdtMoi);
        } else {
            khachHang.setSoDienThoai(null);
        }

        khachHang.setHoTen(yeuCau.hoTen().trim());
        khachHang.setGioiTinh(yeuCau.gioiTinh() != null ? yeuCau.gioiTinh().trim() : null);
        khachHang.setNgaySinh(yeuCau.ngaySinh());
        khachHang.setAnhDaiDien(yeuCau.anhDaiDien() != null ? yeuCau.anhDaiDien().trim() : null);

        KhachHang daLuu = khachHangRepository.save(khachHang);
        return HoSoKhachHangResponse.tuThucThe(daLuu);
    }

    /**
     * Thực hiện đổi mật khẩu tài khoản cho khách hàng.
     *
     * @param idKhachHang ID khách hàng
     * @param yeuCau      thông tin đổi mật khẩu
     * @return thông báo thành công
     */
    @Transactional
    public ThongBaoResponse doiMatKhau(Long idKhachHang, DoiMatKhauRequest yeuCau) {
        KhachHang khachHang = layKhachHangHopLe(idKhachHang);

        // Kiểm tra xác nhận mật khẩu mới
        if (!yeuCau.matKhauMoi().equals(yeuCau.xacNhanMatKhauMoi())) {
            throw new DuLieuKhongHopLeException("Xác nhận mật khẩu mới không trùng khớp");
        }

        // Kiểm tra tính chính xác của mật khẩu cũ
        if (khachHang.getMatKhauHash() == null || !maHoaMatKhau.kiemTra(yeuCau.matKhauCu(), khachHang.getMatKhauHash())) {
            throw new DuLieuKhongHopLeException("Mật khẩu hiện tại không chính xác");
        }

        // Kiểm tra mật khẩu mới không được trùng mật khẩu cũ
        if (yeuCau.matKhauCu().equals(yeuCau.matKhauMoi())) {
            throw new DuLieuKhongHopLeException("Mật khẩu mới không được trùng với mật khẩu hiện tại");
        }

        // Băm mật khẩu mới và lưu vào CSDL
        khachHang.setMatKhauHash(maHoaMatKhau.maHoa(yeuCau.matKhauMoi()));
        khachHangRepository.save(khachHang);

        return new ThongBaoResponse("Đổi mật khẩu thành công");
    }

    /**
     * Lấy và kiểm tra tính hợp lệ của khách hàng (tồn tại, không bị khóa, đã xác thực ID).
     */
    public KhachHang layKhachHangHopLe(Long idKhachHang) {
        if (idKhachHang == null || idKhachHang <= 0) {
            throw new ChuaDangNhapException("Khách hàng chưa đăng nhập hoặc thiếu thông tin định danh (X-Customer-Id)");
        }

        KhachHang khachHang = khachHangRepository.findById(idKhachHang)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy thông tin khách hàng với ID: " + idKhachHang));

        if (khachHang.getTrangThai() != null && khachHang.getTrangThai() == 0) {
            throw new TaiKhoanBiKhoaException("Tài khoản khách hàng đã bị khóa, vui lòng liên hệ bộ phận hỗ trợ");
        }

        return khachHang;
    }
}
