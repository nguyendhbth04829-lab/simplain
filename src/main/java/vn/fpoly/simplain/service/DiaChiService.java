/**
 * Tác giả: Gia Minh
 * Chức năng: US13 - Dịch vụ xử lý nghiệp vụ Quản lý sổ địa chỉ giao hàng của khách hàng
 * Cập nhật xác nhận commit US13: 25/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.CapNhatDiaChiRequest;
import vn.fpoly.simplain.dto.DiaChiResponse;
import vn.fpoly.simplain.dto.ThemDiaChiRequest;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.entity.DiaChi;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.repository.DiaChiRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service quản lý sổ địa chỉ giao hàng (CRUD, địa chỉ mặc định, kiểm tra phân cấp hành chính).
 */
@Service
public class DiaChiService {

    private final DiaChiRepository diaChiRepository;
    private final KhachHangService khachHangService;
    private final DanhMucHanhChinhService danhMucHanhChinhService;

    public DiaChiService(DiaChiRepository diaChiRepository,
                         KhachHangService khachHangService,
                         DanhMucHanhChinhService danhMucHanhChinhService) {
        this.diaChiRepository = diaChiRepository;
        this.khachHangService = khachHangService;
        this.danhMucHanhChinhService = danhMucHanhChinhService;
    }

    /**
     * Lấy toàn bộ danh sách địa chỉ thuộc sở hữu của khách hàng.
     *
     * @param idKhachHang ID khách hàng
     * @return danh sách DTO địa chỉ giao hàng
     */
    @Transactional(readOnly = true)
    public List<DiaChiResponse> layDanhSachDiaChi(Long idKhachHang) {
        khachHangService.layKhachHangHopLe(idKhachHang);
        return diaChiRepository.findAllByMaKhachHangIdOrderByIdDesc(idKhachHang)
                .stream()
                .map(DiaChiResponse::tuThucThe)
                .toList();
    }

    /**
     * Lấy thông tin chi tiết một địa chỉ theo ID và kiểm tra quyền sở hữu.
     *
     * @param idKhachHang ID khách hàng
     * @param idDiaChi    ID địa chỉ
     * @return DTO chi tiết địa chỉ
     */
    @Transactional(readOnly = true)
    public DiaChiResponse layChiTietDiaChi(Long idKhachHang, Long idDiaChi) {
        khachHangService.layKhachHangHopLe(idKhachHang);
        DiaChi diaChi = timDiaChiThuocSoHuu(idKhachHang, idDiaChi);
        return DiaChiResponse.tuThucThe(diaChi);
    }

    /**
     * Thêm mới địa chỉ giao hàng vào sổ địa chỉ của khách hàng.
     *
     * @param idKhachHang ID khách hàng
     * @param yeuCau      dữ liệu thêm mới địa chỉ
     * @return DTO địa chỉ vừa được tạo
     */
    @Transactional
    public DiaChiResponse themDiaChi(Long idKhachHang, ThemDiaChiRequest yeuCau) {
        khachHangService.layKhachHangHopLe(idKhachHang);

        // Kiểm tra tính hợp lệ của phân cấp Tỉnh -> Huyện -> Xã
        kiemTraPhanCapHanhChinh(yeuCau.tinhThanh(), yeuCau.quanHuyen(), yeuCau.phuongXa());

        long soLuongHienCo = diaChiRepository.countByMaKhachHangId(idKhachHang);
        boolean laMacDinh;

        if (soLuongHienCo == 0) {
            // Địa chỉ đầu tiên tự động là mặc định
            laMacDinh = true;
        } else if (Boolean.TRUE.equals(yeuCau.laMacDinh())) {
            // Nếu đặt làm mặc định, gỡ cờ mặc định của các địa chỉ cũ
            diaChiRepository.goTatCaMacDinh(idKhachHang);
            laMacDinh = true;
        } else {
            laMacDinh = false;
        }

        DiaChi diaChi = DiaChi.builder()
                .maKhachHangId(idKhachHang)
                .tenNguoiNhan(yeuCau.tenNguoiNhan().trim())
                .soDienThoai(yeuCau.soDienThoai().trim())
                .tinhThanh(yeuCau.tinhThanh().trim())
                .quanHuyen(yeuCau.quanHuyen().trim())
                .phuongXa(yeuCau.phuongXa().trim())
                .diaChiChiTiet(yeuCau.diaChiChiTiet().trim())
                .laMacDinh(laMacDinh)
                .build();

        DiaChi daLuu = diaChiRepository.save(diaChi);
        return DiaChiResponse.tuThucThe(daLuu);
    }

    /**
     * Cập nhật thông tin địa chỉ giao hàng.
     *
     * @param idKhachHang ID khách hàng
     * @param idDiaChi    ID địa chỉ cần cập nhật
     * @param yeuCau      dữ liệu cập nhật
     * @return DTO địa chỉ sau khi cập nhật
     */
    @Transactional
    public DiaChiResponse capNhatDiaChi(Long idKhachHang, Long idDiaChi, CapNhatDiaChiRequest yeuCau) {
        khachHangService.layKhachHangHopLe(idKhachHang);
        DiaChi diaChi = timDiaChiThuocSoHuu(idKhachHang, idDiaChi);

        // Kiểm tra phân cấp hành chính
        kiemTraPhanCapHanhChinh(yeuCau.tinhThanh(), yeuCau.quanHuyen(), yeuCau.phuongXa());

        boolean laMacDinhHienTai = Boolean.TRUE.equals(diaChi.getLaMacDinh());
        boolean datLaMacDinh = Boolean.TRUE.equals(yeuCau.laMacDinh());

        if (datLaMacDinh && !laMacDinhHienTai) {
            // Đổi địa chỉ này thành mặc định -> gỡ cờ các địa chỉ khác
            diaChiRepository.goMacDinhNgoaiTru(idKhachHang, idDiaChi);
            diaChi.setLaMacDinh(true);
        } else if (!datLaMacDinh && laMacDinhHienTai) {
            // Nếu bỏ cờ mặc định của địa chỉ đang là mặc định
            long soLuong = diaChiRepository.countByMaKhachHangId(idKhachHang);
            if (soLuong <= 1) {
                // Nếu chỉ có 1 địa chỉ duy nhất thì bắt buộc phải giữ mặc định
                diaChi.setLaMacDinh(true);
            } else {
                diaChi.setLaMacDinh(false);
                // Gán một địa chỉ khác làm mặc định thay thế
                Optional<DiaChi> diaChiKhac = diaChiRepository.findAllByMaKhachHangIdOrderByIdDesc(idKhachHang)
                        .stream()
                        .filter(d -> !d.getId().equals(idDiaChi))
                        .findFirst();
                if (diaChiKhac.isPresent()) {
                    DiaChi thayThe = diaChiKhac.get();
                    thayThe.setLaMacDinh(true);
                    diaChiRepository.save(thayThe);
                }
            }
        }

        diaChi.setTenNguoiNhan(yeuCau.tenNguoiNhan().trim());
        diaChi.setSoDienThoai(yeuCau.soDienThoai().trim());
        diaChi.setTinhThanh(yeuCau.tinhThanh().trim());
        diaChi.setQuanHuyen(yeuCau.quanHuyen().trim());
        diaChi.setPhuongXa(yeuCau.phuongXa().trim());
        diaChi.setDiaChiChiTiet(yeuCau.diaChiChiTiet().trim());

        DiaChi daLuu = diaChiRepository.save(diaChi);
        return DiaChiResponse.tuThucThe(daLuu);
    }

    /**
     * Xóa một địa chỉ khỏi sổ địa chỉ của khách hàng.
     *
     * @param idKhachHang ID khách hàng
     * @param idDiaChi    ID địa chỉ cần xóa
     * @return thông báo thành công
     */
    @Transactional
    public ThongBaoResponse xoaDiaChi(Long idKhachHang, Long idDiaChi) {
        khachHangService.layKhachHangHopLe(idKhachHang);
        DiaChi diaChi = timDiaChiThuocSoHuu(idKhachHang, idDiaChi);

        boolean daTungLaMacDinh = Boolean.TRUE.equals(diaChi.getLaMacDinh());
        diaChiRepository.delete(diaChi);

        // Nếu xóa địa chỉ đang là mặc định, tự động gán địa chỉ còn lại gần nhất làm mặc định
        if (daTungLaMacDinh) {
            Optional<DiaChi> diaChiConLai = diaChiRepository.findFirstByMaKhachHangIdOrderByIdAsc(idKhachHang);
            if (diaChiConLai.isPresent()) {
                DiaChi dcKeThua = diaChiConLai.get();
                dcKeThua.setLaMacDinh(true);
                diaChiRepository.save(dcKeThua);
            }
        }

        return new ThongBaoResponse("Xóa địa chỉ nhận hàng thành công");
    }

    /**
     * Đặt một địa chỉ cụ thể làm địa chỉ mặc định của khách hàng.
     *
     * @param idKhachHang ID khách hàng
     * @param idDiaChi    ID địa chỉ
     * @return DTO địa chỉ vừa được đặt làm mặc định
     */
    @Transactional
    public DiaChiResponse datLamMacDinh(Long idKhachHang, Long idDiaChi) {
        khachHangService.layKhachHangHopLe(idKhachHang);
        DiaChi diaChi = timDiaChiThuocSoHuu(idKhachHang, idDiaChi);

        diaChiRepository.goMacDinhNgoaiTru(idKhachHang, idDiaChi);
        diaChi.setLaMacDinh(true);

        DiaChi daLuu = diaChiRepository.save(diaChi);
        return DiaChiResponse.tuThucThe(daLuu);
    }

    /**
     * Tìm địa chỉ và xác thực quyền sở hữu của khách hàng.
     */
    private DiaChi timDiaChiThuocSoHuu(Long idKhachHang, Long idDiaChi) {
        if (idDiaChi == null || idDiaChi <= 0) {
            throw new DuLieuKhongHopLeException("ID địa chỉ không hợp lệ");
        }
        return diaChiRepository.findByIdAndMaKhachHangId(idDiaChi, idKhachHang)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy địa chỉ với ID: " + idDiaChi + " thuộc khách hàng hiện tại"));
    }

    /**
     * Xác thực phân cấp hành chính chuẩn Việt Nam.
     */
    private void kiemTraPhanCapHanhChinh(String tinhThanh, String quanHuyen, String phuongXa) {
        if (!danhMucHanhChinhService.kiemTraHopLe(tinhThanh, quanHuyen, phuongXa)) {
            throw new DuLieuKhongHopLeException("Phân cấp hành chính không hợp lệ: " + phuongXa + ", " + quanHuyen + ", " + tinhThanh);
        }
    }
}
