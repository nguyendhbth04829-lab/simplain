package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.DiaChiRequest;
import vn.fpoly.simplain.entity.DiaChi;
import vn.fpoly.simplain.repository.DiaChiRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;

import java.util.List;

/**
 * Service xử lý nghiệp vụ Quản lý Sổ địa chỉ giao hàng của Khách hàng (US13).
 */
@Service
public class DiaChiService {

    private final DiaChiRepository diaChiRepository;
    private final KhachHangRepository khachHangRepository;

    @Autowired
    public DiaChiService(DiaChiRepository diaChiRepository, KhachHangRepository khachHangRepository) {
        this.diaChiRepository = diaChiRepository;
        this.khachHangRepository = khachHangRepository;
    }

    /**
     * Lấy toàn bộ danh sách sổ địa chỉ của khách hàng, địa chỉ mặc định luôn ở đầu.
     */
    @Transactional(readOnly = true)
    public List<DiaChi> layDanhSach(Long maKhachHangId) {
        return diaChiRepository.findByMaKhachHangIdOrderByLaMacDinhDescIdDesc(maKhachHangId);
    }

    /**
     * Lấy chi tiết một địa chỉ theo ID và ID khách hàng sở hữu.
     */
    @Transactional(readOnly = true)
    public DiaChi layTheoIdVaKhachHang(Long id, Long maKhachHangId) {
        return diaChiRepository.findByIdAndMaKhachHangId(id, maKhachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ giao hàng hoặc bạn không có quyền truy cập!"));
    }

    /**
     * Lấy địa chỉ giao hàng mặc định của khách hàng.
     */
    @Transactional(readOnly = true)
    public DiaChi layDiaChiMacDinh(Long maKhachHangId) {
        return diaChiRepository.findByMaKhachHangIdAndLaMacDinhTrue(maKhachHangId)
                .orElseGet(() -> {
                    List<DiaChi> list = diaChiRepository.findByMaKhachHangIdOrderByLaMacDinhDescIdDesc(maKhachHangId);
                    return list.isEmpty() ? null : list.get(0);
                });
    }

    /**
     * Thêm mới địa chỉ giao hàng vào sổ địa chỉ (US13 - Tiêu chí 1 & 3).
     */
    @Transactional
    public DiaChi them(Long maKhachHangId, DiaChiRequest request) {
        if (!khachHangRepository.existsById(maKhachHangId)) {
            throw new IllegalArgumentException("Khách hàng không tồn tại trong hệ thống!");
        }

        long soLuongHienCo = diaChiRepository.countByMaKhachHangId(maKhachHangId);
        boolean datLamMacDinh = soLuongHienCo == 0 || Boolean.TRUE.equals(request.getLaMacDinh());

        if (datLamMacDinh) {
            diaChiRepository.boMacDinhTatCaTheoKhachHang(maKhachHangId);
        }

        DiaChi diaChiMoi = DiaChi.builder()
                .maKhachHangId(maKhachHangId)
                .tenNguoiNhan(request.getTenNguoiNhan().trim())
                .soDienThoai(request.getSoDienThoai().trim())
                .tinhThanh(request.getTinhThanh().trim())
                .quanHuyen(request.getQuanHuyen().trim())
                .phuongXa(request.getPhuongXa().trim())
                .diaChiChiTiet(request.getDiaChiChiTiet().trim())
                .laMacDinh(datLamMacDinh)
                .build();

        return diaChiRepository.save(diaChiMoi);
    }

    /**
     * Chỉnh sửa thông tin địa chỉ giao hàng (US13 - Tiêu chí 1 & 3).
     */
    @Transactional
    public DiaChi sua(Long id, Long maKhachHangId, DiaChiRequest request) {
        DiaChi hienTai = layTheoIdVaKhachHang(id, maKhachHangId);

        boolean yeuCauMacDinh = Boolean.TRUE.equals(request.getLaMacDinh());
        if (yeuCauMacDinh) {
            diaChiRepository.boMacDinhTatCaTheoKhachHang(maKhachHangId);
            hienTai.setLaMacDinh(true);
        } else {
            // Nếu địa chỉ này đang là mặc định và là địa chỉ duy nhất, giữ nguyên mặc định
            long soLuong = diaChiRepository.countByMaKhachHangId(maKhachHangId);
            if (Boolean.TRUE.equals(hienTai.getLaMacDinh()) && soLuong <= 1) {
                hienTai.setLaMacDinh(true);
            } else if (Boolean.TRUE.equals(hienTai.getLaMacDinh())) {
                // Nếu khách hàng bỏ cờ của địa chỉ mặc định hiện tại, tự động chuyển địa chỉ khác làm mặc định
                hienTai.setLaMacDinh(false);
                List<DiaChi> danhSachKhac = diaChiRepository.findByMaKhachHangIdOrderByLaMacDinhDescIdDesc(maKhachHangId);
                for (DiaChi d : danhSachKhac) {
                    if (!d.getId().equals(id)) {
                        d.setLaMacDinh(true);
                        diaChiRepository.save(d);
                        break;
                    }
                }
            } else {
                hienTai.setLaMacDinh(false);
            }
        }

        hienTai.setTenNguoiNhan(request.getTenNguoiNhan().trim());
        hienTai.setSoDienThoai(request.getSoDienThoai().trim());
        hienTai.setTinhThanh(request.getTinhThanh().trim());
        hienTai.setQuanHuyen(request.getQuanHuyen().trim());
        hienTai.setPhuongXa(request.getPhuongXa().trim());
        hienTai.setDiaChiChiTiet(request.getDiaChiChiTiet().trim());

        return diaChiRepository.save(hienTai);
    }

    /**
     * Thiết lập một địa chỉ làm địa chỉ mặc định (US13 - Tiêu chí 3).
     */
    @Transactional
    public DiaChi datLamMacDinh(Long id, Long maKhachHangId) {
        DiaChi diaChi = layTheoIdVaKhachHang(id, maKhachHangId);
        diaChiRepository.boMacDinhTatCaTheoKhachHang(maKhachHangId);
        diaChi.setLaMacDinh(true);
        return diaChiRepository.save(diaChi);
    }

    /**
     * Xóa một địa chỉ khỏi sổ địa chỉ (US13 - Tiêu chí 1).
     * Nếu địa chỉ bị xóa là mặc định, tự động gán địa chỉ còn lại làm mặc định mới.
     */
    @Transactional
    public void xoa(Long id, Long maKhachHangId) {
        DiaChi diaChi = layTheoIdVaKhachHang(id, maKhachHangId);
        boolean daTungLaMacDinh = Boolean.TRUE.equals(diaChi.getLaMacDinh());

        diaChiRepository.delete(diaChi);

        // Đảm bảo luôn có 1 địa chỉ mặc định nếu khách hàng vẫn còn địa chỉ khác
        if (daTungLaMacDinh) {
            List<DiaChi> conLai = diaChiRepository.findByMaKhachHangIdOrderByLaMacDinhDescIdDesc(maKhachHangId);
            if (!conLai.isEmpty()) {
                DiaChi diaChiKeTiep = conLai.get(0);
                diaChiKeTiep.setLaMacDinh(true);
                diaChiRepository.save(diaChiKeTiep);
            }
        }
    }
}
