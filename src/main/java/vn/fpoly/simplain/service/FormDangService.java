package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.FormDang;
import vn.fpoly.simplain.repository.FormDangRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;

import java.util.List;

/**
 * Service xử lý nghiệp vụ Quản lý Form dáng [form_dang].
 * Hỗ trợ CRUD đầy đủ, Tìm kiếm từ khóa, Lọc trạng thái, Phân trang và kiểm tra ràng buộc toàn vẹn.
 */
@Service
public class FormDangService {

    private final FormDangRepository formDangRepository;
    private final SanPhamRepository sanPhamRepository;

    @Autowired
    public FormDangService(FormDangRepository formDangRepository,
                           SanPhamRepository sanPhamRepository) {
        this.formDangRepository = formDangRepository;
        this.sanPhamRepository = sanPhamRepository;
    }

    @Transactional(readOnly = true)
    public Page<FormDang> layDanhSachPhanTrang(String tuKhoa, Boolean kichHoat, Pageable pageable) {
        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        Page<FormDang> page = formDangRepository.timKiemPhanTrang(tuKhoaChuan, kichHoat, pageable);
        page.getContent().forEach(fd -> {
            fd.setSoSanPhamSuDung(sanPhamRepository.countByMaFormDangId(fd.getId()));
        });
        return page;
    }

    @Transactional(readOnly = true)
    public List<FormDang> layTatCa() {
        List<FormDang> list = formDangRepository.findAll(Sort.by("id").descending());
        list.forEach(fd -> fd.setSoSanPhamSuDung(sanPhamRepository.countByMaFormDangId(fd.getId())));
        return list;
    }

    @Transactional(readOnly = true)
    public List<FormDang> layTatCaKichHoat() {
        return formDangRepository.findByKichHoatTrue(Sort.by("tenFormDang").ascending());
    }

    @Transactional(readOnly = true)
    public FormDang layTheoId(Long id) {
        FormDang fd = formDangRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy form dáng với ID: " + id));
        fd.setSoSanPhamSuDung(sanPhamRepository.countByMaFormDangId(fd.getId()));
        return fd;
    }

    @Transactional
    public FormDang them(FormDang formDang) {
        if (formDang.getTenFormDang() == null || formDang.getTenFormDang().isBlank()) {
            throw new IllegalArgumentException("Tên form dáng không được để trống!");
        }
        String tenChuan = formDang.getTenFormDang().trim();
        if (formDangRepository.existsByTenFormDangIgnoreCase(tenChuan)) {
            throw new IllegalArgumentException("Tên form dáng [" + tenChuan + "] đã tồn tại trên hệ thống!");
        }
        formDang.setTenFormDang(tenChuan);
        if (formDang.getMoTa() != null) {
            formDang.setMoTa(formDang.getMoTa().trim());
        }
        if (formDang.getKichHoat() == null) {
            formDang.setKichHoat(true);
        }
        return formDangRepository.save(formDang);
    }

    @Transactional
    public FormDang sua(Long id, FormDang duLieuMoi) {
        FormDang hienTai = layTheoId(id);
        if (duLieuMoi.getTenFormDang() == null || duLieuMoi.getTenFormDang().isBlank()) {
            throw new IllegalArgumentException("Tên form dáng không được để trống!");
        }
        String tenChuan = duLieuMoi.getTenFormDang().trim();
        if (formDangRepository.existsByTenFormDangIgnoreCaseAndIdNot(tenChuan, id)) {
            throw new IllegalArgumentException("Tên form dáng [" + tenChuan + "] đã được sử dụng ở bản ghi khác!");
        }
        hienTai.setTenFormDang(tenChuan);
        hienTai.setMoTa(duLieuMoi.getMoTa() != null ? duLieuMoi.getMoTa().trim() : null);
        if (duLieuMoi.getKichHoat() != null) {
            hienTai.setKichHoat(duLieuMoi.getKichHoat());
        }
        return formDangRepository.save(hienTai);
    }

    @Transactional
    public FormDang doiTrangThai(Long id) {
        FormDang fd = layTheoId(id);
        fd.setKichHoat(!Boolean.TRUE.equals(fd.getKichHoat()));
        return formDangRepository.save(fd);
    }

    @Transactional
    public FormDang capNhatTrangThai(Long id, Boolean kichHoat) {
        FormDang fd = layTheoId(id);
        fd.setKichHoat(kichHoat);
        return formDangRepository.save(fd);
    }

    @Transactional
    public void xoa(Long id) {
        FormDang fd = layTheoId(id);
        long soSanPham = sanPhamRepository.countByMaFormDangId(id);
        if (soSanPham > 0) {
            throw new IllegalStateException("Không thể xóa form dáng [" + fd.getTenFormDang() + "] vì đang có " + soSanPham + " sản phẩm liên kết!");
        }
        formDangRepository.delete(fd);
    }

    @Transactional(readOnly = true)
    public long demTongSo() {
        return formDangRepository.count();
    }
}
