package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.MauSac;
import vn.fpoly.simplain.repository.MauSacRepository;

import java.util.List;

/**
 * Dịch vụ xử lý nghiệp vụ và Validate chuyên nghiệp cho Quản lý Màu sắc sản phẩm [mau_sac] (US07).
 *
 * <p>Đáp ứng tiêu chí US07:
 * 1. Thêm / Sửa / Xóa dữ liệu mau_sac (Mã màu Hex #RRGGBB hoặc mã màu định danh & Tên màu tiếng Việt).
 * 2. Chuẩn hóa mã màu viết hoa (VD: #1e3a8a -> #1E3A8A) và chặn trùng lặp mã màu hoặc tên màu.
 * 3. Bảo vệ toàn vẹn dữ liệu khi xóa: Không cho xóa màu sắc đang được sử dụng trong biến thể sản phẩm hoặc hình ảnh sản phẩm.
 */
@Service
public class MauSacService {

    private final MauSacRepository mauSacRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MauSacService(MauSacRepository mauSacRepository, JdbcTemplate jdbcTemplate) {
        this.mauSacRepository = mauSacRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Lấy danh sách màu sắc có phân trang và tìm kiếm theo từ khóa.
     */
    @Transactional(readOnly = true)
    public Page<MauSac> layDanhSachPhanTrang(String tuKhoa, Pageable pageable) {
        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        Page<MauSac> pageData = mauSacRepository.timKiemPhanTrang(tuKhoaChuan, pageable);
        pageData.getContent().forEach(ms -> ms.setSoBienTheSuDung(demSoBienTheSuDungMauSac(ms.getId())));
        return pageData;
    }

    /**
     * Lấy toàn bộ danh sách màu sắc.
     */
    @Transactional(readOnly = true)
    public List<MauSac> layTatCa() {
        return mauSacRepository.findAll();
    }

    /**
     * Lấy chi tiết màu sắc theo ID.
     */
    @Transactional(readOnly = true)
    public MauSac layTheoId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID màu sắc không được để trống!");
        }
        MauSac ms = mauSacRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy màu sắc với ID: " + id));
        ms.setSoBienTheSuDung(demSoBienTheSuDungMauSac(ms.getId()));
        return ms;
    }

    /**
     * Thêm mới màu sắc sản phẩm.
     */
    @Transactional
    public MauSac them(MauSac mauSac) {
        chuanHoaVaValidateCoBan(mauSac);

        // 1. Kiểm tra trùng mã màu (Hex)
        if (mauSacRepository.existsByMaMauIgnoreCase(mauSac.getMaMau())) {
            throw new IllegalArgumentException("Mã màu [" + mauSac.getMaMau() + "] đã tồn tại trên hệ thống!");
        }

        // 2. Kiểm tra trùng tên màu sắc
        if (mauSacRepository.existsByTenMauIgnoreCase(mauSac.getTenMau())) {
            throw new IllegalArgumentException("Tên màu sắc [" + mauSac.getTenMau() + "] đã tồn tại trên hệ thống!");
        }

        return mauSacRepository.save(mauSac);
    }

    /**
     * Cập nhật thông tin màu sắc sản phẩm.
     */
    @Transactional
    public MauSac sua(Long id, MauSac duLieuMoi) {
        MauSac hienTai = layTheoId(id);
        chuanHoaVaValidateCoBan(duLieuMoi);

        // 1. Kiểm tra trùng mã màu (loại trừ chính ID đang sửa)
        if (mauSacRepository.existsByMaMauIgnoreCaseAndIdNot(duLieuMoi.getMaMau(), id)) {
            throw new IllegalArgumentException("Mã màu [" + duLieuMoi.getMaMau() + "] đã được sử dụng ở màu sắc khác!");
        }

        // 2. Kiểm tra trùng tên màu (loại trừ chính ID đang sửa)
        if (mauSacRepository.existsByTenMauIgnoreCaseAndIdNot(duLieuMoi.getTenMau(), id)) {
            throw new IllegalArgumentException("Tên màu sắc [" + duLieuMoi.getTenMau() + "] đã tồn tại ở màu sắc khác!");
        }

        hienTai.setMaMau(duLieuMoi.getMaMau());
        hienTai.setTenMau(duLieuMoi.getTenMau());
        if (duLieuMoi.getKichHoat() != null) {
            hienTai.setKichHoat(duLieuMoi.getKichHoat());
        }
        return mauSacRepository.save(hienTai);
    }

    /**
     * Chuyển đổi trạng thái hoạt động (Còn hoạt động / Tạm ngưng) của màu sắc.
     */
    @Transactional
    public MauSac doiTrangThai(Long id) {
        MauSac ms = layTheoId(id);
        ms.setKichHoat(!Boolean.TRUE.equals(ms.getKichHoat()));
        return mauSacRepository.save(ms);
    }

    /**
     * Xóa màu sắc có kiểm tra ràng buộc biến thể sản phẩm và hình ảnh sản phẩm.
     */
    @Transactional
    public void xoa(Long id) {
        MauSac ms = layTheoId(id);

        long soBienThe = demSoBienTheSuDungMauSac(id);
        if (soBienThe > 0) {
            throw new IllegalStateException(
                    "Không thể xóa màu sắc [" + ms.getTenMau() + " (" + ms.getMaMau() + ")] vì đang được sử dụng bởi " + soBienThe + " biến thể/hình ảnh sản phẩm!"
            );
        }

        try {
            mauSacRepository.delete(ms);
            mauSacRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "Không thể xóa màu sắc [" + ms.getTenMau() + "] do đang được tham chiếu bởi sản phẩm trên hệ thống!"
            );
        }
    }

    private void chuanHoaVaValidateCoBan(MauSac ms) {
        if (ms == null) {
            throw new IllegalArgumentException("Dữ liệu màu sắc không được để trống!");
        }

        String maMau = ms.getMaMau() != null ? ms.getMaMau().trim().toUpperCase() : "";
        if (maMau.isEmpty()) {
            throw new IllegalArgumentException("Mã màu (Hex) không được để trống!");
        }
        if (!maMau.matches("^(#[0-9A-F]{3}([0-9A-F]{3})?|[A-Z0-9_-]{2,50})$")) {
            throw new IllegalArgumentException("Mã màu phải là mã Hex hợp lệ (VD: #000000, #1E3A8A) hoặc mã màu viết liền không khoảng trắng!");
        }
        ms.setMaMau(maMau);

        String tenMau = ms.getTenMau() != null ? ms.getTenMau().trim() : "";
        if (tenMau.isEmpty()) {
            throw new IllegalArgumentException("Tên màu sắc không được để trống!");
        }
        if (tenMau.length() < 2 || tenMau.length() > 100) {
            throw new IllegalArgumentException("Tên màu sắc phải từ 2 đến 100 ký tự!");
        }
        ms.setTenMau(tenMau);
    }

    private long demSoBienTheSuDungMauSac(Long mauSacId) {
        try {
            Long soBienThe = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [bien_the_san_pham] WHERE [ma_mau_sac_id] = ?",
                    Long.class,
                    mauSacId
            );
            return soBienThe != null ? soBienThe : 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }
}
