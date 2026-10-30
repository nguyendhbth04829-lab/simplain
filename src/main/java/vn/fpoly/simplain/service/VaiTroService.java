package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.VaiTroRequest;
import vn.fpoly.simplain.entity.VaiTro;
import vn.fpoly.simplain.entity.VaiTroQuyenHan;
import vn.fpoly.simplain.enums.ChucNangHeThong;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.repository.VaiTroQuyenHanRepository;
import vn.fpoly.simplain.repository.VaiTroRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý các nghiệp vụ quản lý Vai trò hệ thống (US03) và Phân quyền động (Dynamic RBAC).
 */
@Service
public class VaiTroService {

    private final VaiTroRepository vaiTroRepository;
    private final NhanVienRepository nhanVienRepository;
    private final VaiTroQuyenHanRepository vaiTroQuyenHanRepository;

    @Autowired
    public VaiTroService(VaiTroRepository vaiTroRepository,
                          NhanVienRepository nhanVienRepository,
                          @Autowired(required = false) VaiTroQuyenHanRepository vaiTroQuyenHanRepository) {
        this.vaiTroRepository = vaiTroRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.vaiTroQuyenHanRepository = vaiTroQuyenHanRepository;
    }

    public VaiTroService(VaiTroRepository vaiTroRepository, NhanVienRepository nhanVienRepository) {
        this(vaiTroRepository, nhanVienRepository, null);
    }

    /**
     * Lấy danh sách vai trò có tìm kiếm và phân trang.
     *
     * @param tuKhoa từ khóa tìm kiếm theo tên hoặc mã
     * @param pageable đối tượng phân trang
     * @return Trang danh sách vai trò
     */
    public Page<VaiTro> layDanhSach(String tuKhoa, Pageable pageable) {
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            String kw = tuKhoa.trim();
            return vaiTroRepository.findByTenVaiTroContainingIgnoreCaseOrMaVaiTroContainingIgnoreCase(kw, kw, pageable);
        }
        return vaiTroRepository.findAll(pageable);
    }

    /**
     * Lấy toàn bộ danh sách vai trò.
     */
    public List<VaiTro> layTatCa() {
        return vaiTroRepository.findAll();
    }

    /**
     * Lấy thông tin một vai trò theo ID.
     *
     * @param id ID của vai trò
     * @return Đối tượng VaiTro
     */
    public VaiTro layTheoId(Long id) {
        return vaiTroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò có ID = " + id));
    }

    /**
     * Thêm mới vai trò vào hệ thống.
     *
     * @param request dữ liệu vai trò
     * @return VaiTro sau khi lưu
     */
    @Transactional
    public VaiTro them(VaiTroRequest request) {
        String ma = request.getMaVaiTro().trim().toUpperCase();

        // Kiểm tra trùng mã vai trò
        if (vaiTroRepository.existsByMaVaiTro(ma)) {
            throw new IllegalArgumentException("Mã vai trò '" + ma + "' đã tồn tại trong hệ thống");
        }

        VaiTro vaiTro = VaiTro.builder()
                .maVaiTro(ma)
                .tenVaiTro(request.getTenVaiTro().trim())
                .moTa(request.getMoTa() != null ? request.getMoTa().trim() : null)
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : 1)
                .build();

        VaiTro saved = vaiTroRepository.save(vaiTro);

        // Lưu quyền hạn nếu có truyền danh sách quyền
        if (request.getDanhSachQuyen() != null) {
            capNhatQuyenCuaVaiTro(saved.getId(), request.getDanhSachQuyen());
        } else if ("ADMIN".equalsIgnoreCase(saved.getMaVaiTro())) {
            // Nếu là Admin thì gán toàn quyền mặc định
            List<String> all = Arrays.stream(ChucNangHeThong.values()).map(ChucNangHeThong::getMaQuyen).collect(Collectors.toList());
            capNhatQuyenCuaVaiTro(saved.getId(), all);
        }

        return saved;
    }

    /**
     * Cập nhật thông tin vai trò.
     *
     * @param id ID của vai trò
     * @param request dữ liệu cập nhật
     * @return VaiTro sau khi cập nhật
     */
    @Transactional
    public VaiTro sua(Long id, VaiTroRequest request) {
        VaiTro vaiTro = layTheoId(id);
        String ma = request.getMaVaiTro().trim().toUpperCase();

        // Kiểm tra trùng mã vai trò với vai trò khác
        if (vaiTroRepository.existsByMaVaiTroAndIdNot(ma, id)) {
            throw new IllegalArgumentException("Mã vai trò '" + ma + "' đã được sử dụng bởi vai trò khác");
        }

        vaiTro.setMaVaiTro(ma);
        vaiTro.setTenVaiTro(request.getTenVaiTro().trim());
        vaiTro.setMoTa(request.getMoTa() != null ? request.getMoTa().trim() : null);
        vaiTro.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : 1);

        VaiTro updated = vaiTroRepository.save(vaiTro);

        // Cập nhật phân quyền động nếu danh sách quyền được gửi lên
        if (request.getDanhSachQuyen() != null) {
            capNhatQuyenCuaVaiTro(id, request.getDanhSachQuyen());
        }

        return updated;
    }

    /**
     * Lấy danh sách mã quyền hạn của vai trò.
     */
    public List<String> layDanhSachQuyenCuaVaiTro(Long vaiTroId) {
        if (vaiTroQuyenHanRepository == null) {
            return new ArrayList<>();
        }
        List<VaiTroQuyenHan> list = vaiTroQuyenHanRepository.findByMaVaiTroId(vaiTroId);
        if (list.isEmpty()) {
            VaiTro vt = vaiTroRepository.findById(vaiTroId).orElse(null);
            if (vt != null && "ADMIN".equalsIgnoreCase(vt.getMaVaiTro())) {
                return Arrays.stream(ChucNangHeThong.values()).map(ChucNangHeThong::getMaQuyen).collect(Collectors.toList());
            }
        }
        return list.stream().map(VaiTroQuyenHan::getMaQuyen).collect(Collectors.toList());
    }

    /**
     * Cập nhật toàn bộ quyền hạn cho vai trò.
     */
    @Transactional
    public void capNhatQuyenCuaVaiTro(Long vaiTroId, List<String> danhSachQuyen) {
        if (vaiTroQuyenHanRepository == null) return;
        vaiTroQuyenHanRepository.deleteByMaVaiTroId(vaiTroId);
        if (danhSachQuyen != null && !danhSachQuyen.isEmpty()) {
            List<VaiTroQuyenHan> entities = danhSachQuyen.stream()
                    .filter(q -> q != null && !q.trim().isEmpty())
                    .map(String::trim)
                    .distinct()
                    .map(q -> VaiTroQuyenHan.builder().maVaiTroId(vaiTroId).maQuyen(q).build())
                    .collect(Collectors.toList());
            vaiTroQuyenHanRepository.saveAll(entities);
        }
    }

    /**
     * Chuyển đổi trạng thái hoạt động (Hoạt động / Vô hiệu hóa) của vai trò.
     */
    @Transactional
    public VaiTro doiTrangThai(Long id) {
        VaiTro vt = layTheoId(id);
        vt.setTrangThai(vt.getTrangThai() != null && vt.getTrangThai() == 1 ? 0 : 1);
        return vaiTroRepository.save(vt);
    }

    /**
     * Cập nhật trạng thái trực tiếp của vai trò.
     */
    @Transactional
    public VaiTro capNhatTrangThai(Long id, Integer trangThai) {
        VaiTro vt = layTheoId(id);
        vt.setTrangThai(trangThai);
        return vaiTroRepository.save(vt);
    }

    /**
     * Xóa vai trò (US03 - Tiêu chí 2: Không cho xóa vai trò đang có nhân viên sử dụng).
     *
     * @param id ID của vai trò cần xóa
     */
    @Transactional
    public void xoa(Long id) {
        VaiTro vaiTro = layTheoId(id);

        // Tiêu chí cốt lõi US03: Kiểm tra xem vai trò này có nhân viên nào đang sử dụng không
        long soNhanVien = nhanVienRepository.countByVaiTroId(id);
        if (soNhanVien > 0) {
            throw new IllegalStateException("Không thể xóa vai trò [" + vaiTro.getTenVaiTro() +
                    "] vì đang có " + soNhanVien + " nhân viên đang sử dụng! Bạn có thể chuyển trạng thái sang Vô hiệu hóa thay thế.");
        }

        vaiTroRepository.delete(vaiTro);
    }

    /**
     * Đếm số lượng nhân viên đang sử dụng vai trò này.
     *
     * @param vaiTroId ID của vai trò
     * @return số nhân viên
     */
    public long demSoNhanVien(Long vaiTroId) {
        return nhanVienRepository.countByVaiTroId(vaiTroId);
    }
}
