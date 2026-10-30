package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.KichThuoc;
import vn.fpoly.simplain.repository.KichThuocRepository;

import java.util.List;

/**
 * Dịch vụ xử lý nghiệp vụ và Validate chuyên nghiệp cho Quản lý Kích thước sản phẩm [kich_thuoc] (US07).
 *
 * <p>Đáp ứng tiêu chí US07:
 * 1. Thêm / Sửa / Xóa dữ liệu kich_thuoc (S, M, L, XL, 2XL, 3XL...).
 * 2. Chuẩn hóa mã kích thước in hoa và chặn trùng lặp mã kích thước hoặc tên kích thước.
 * 3. Bảo vệ toàn vẹn dữ liệu khi xóa: Không cho xóa kích thước đang được sử dụng bởi biến thể sản phẩm.
 */
@Service
public class KichThuocService {

    private final KichThuocRepository kichThuocRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public KichThuocService(KichThuocRepository kichThuocRepository, JdbcTemplate jdbcTemplate) {
        this.kichThuocRepository = kichThuocRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Lấy danh sách kích thước có phân trang và tìm kiếm theo từ khóa.
     */
    @Transactional(readOnly = true)
    public Page<KichThuoc> layDanhSachPhanTrang(String tuKhoa, Pageable pageable) {
        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        Page<KichThuoc> pageData = kichThuocRepository.timKiemPhanTrang(tuKhoaChuan, pageable);
        pageData.getContent().forEach(kt -> kt.setSoBienTheSuDung(demSoBienTheSuDungKichThuoc(kt.getId())));
        return pageData;
    }

    /**
     * Lấy toàn bộ danh sách kích thước.
     */
    @Transactional(readOnly = true)
    public List<KichThuoc> layTatCa() {
        return kichThuocRepository.findAll();
    }

    /**
     * Lấy chi tiết kích thước theo ID.
     */
    @Transactional(readOnly = true)
    public KichThuoc layTheoId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID kích thước không được để trống!");
        }
        KichThuoc kt = kichThuocRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kích thước với ID: " + id));
        kt.setSoBienTheSuDung(demSoBienTheSuDungKichThuoc(kt.getId()));
        return kt;
    }

    /**
     * Thêm mới kích thước sản phẩm (VD: S, M, L, XL, 2XL).
     */
    @Transactional
    public KichThuoc them(KichThuoc kichThuoc) {
        chuanHoaVaValidateCoBan(kichThuoc);

        // 1. Kiểm tra trùng mã kích thước
        if (kichThuocRepository.existsByMaKichThuocIgnoreCase(kichThuoc.getMaKichThuoc())) {
            throw new IllegalArgumentException("Mã kích thước [" + kichThuoc.getMaKichThuoc() + "] đã tồn tại trên hệ thống!");
        }

        // 2. Kiểm tra trùng tên kích thước
        if (kichThuocRepository.existsByTenKichThuocIgnoreCase(kichThuoc.getTenKichThuoc())) {
            throw new IllegalArgumentException("Tên kích thước [" + kichThuoc.getTenKichThuoc() + "] đã tồn tại trên hệ thống!");
        }

        return kichThuocRepository.save(kichThuoc);
    }

    /**
     * Cập nhật thông tin kích thước sản phẩm.
     */
    @Transactional
    public KichThuoc sua(Long id, KichThuoc duLieuMoi) {
        KichThuoc hienTai = layTheoId(id);
        chuanHoaVaValidateCoBan(duLieuMoi);

        // 1. Kiểm tra trùng mã kích thước (loại trừ chính ID đang sửa)
        if (kichThuocRepository.existsByMaKichThuocIgnoreCaseAndIdNot(duLieuMoi.getMaKichThuoc(), id)) {
            throw new IllegalArgumentException("Mã kích thước [" + duLieuMoi.getMaKichThuoc() + "] đã được sử dụng ở kích thước khác!");
        }

        // 2. Kiểm tra trùng tên kích thước (loại trừ chính ID đang sửa)
        if (kichThuocRepository.existsByTenKichThuocIgnoreCaseAndIdNot(duLieuMoi.getTenKichThuoc(), id)) {
            throw new IllegalArgumentException("Tên kích thước [" + duLieuMoi.getTenKichThuoc() + "] đã tồn tại ở kích thước khác!");
        }

        hienTai.setMaKichThuoc(duLieuMoi.getMaKichThuoc());
        hienTai.setTenKichThuoc(duLieuMoi.getTenKichThuoc());
        if (duLieuMoi.getKichHoat() != null) {
            hienTai.setKichHoat(duLieuMoi.getKichHoat());
        }
        return kichThuocRepository.save(hienTai);
    }

    /**
     * Chuyển đổi trạng thái hoạt động (Còn hoạt động / Tạm ngưng) của kích thước.
     */
    @Transactional
    public KichThuoc doiTrangThai(Long id) {
        KichThuoc kt = layTheoId(id);
        kt.setKichHoat(!Boolean.TRUE.equals(kt.getKichHoat()));
        return kichThuocRepository.save(kt);
    }

    /**
     * Xóa kích thước có kiểm tra ràng buộc biến thể sản phẩm đang sử dụng.
     */
    @Transactional
    public void xoa(Long id) {
        KichThuoc kt = layTheoId(id);

        long soBienThe = demSoBienTheSuDungKichThuoc(id);
        if (soBienThe > 0) {
            throw new IllegalStateException(
                    "Không thể xóa kích thước [" + kt.getMaKichThuoc() + " - " + kt.getTenKichThuoc() + "] vì đang có " + soBienThe + " biến thể sản phẩm sử dụng!"
            );
        }

        try {
            kichThuocRepository.delete(kt);
            kichThuocRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "Không thể xóa kích thước [" + kt.getMaKichThuoc() + "] do đang được tham chiếu bởi biến thể sản phẩm!"
            );
        }
    }

    private void chuanHoaVaValidateCoBan(KichThuoc kt) {
        if (kt == null) {
            throw new IllegalArgumentException("Dữ liệu kích thước không được để trống!");
        }

        String ma = kt.getMaKichThuoc() != null ? kt.getMaKichThuoc().trim().toUpperCase() : "";
        if (ma.isEmpty()) {
            throw new IllegalArgumentException("Mã kích thước không được để trống!");
        }
        if (!ma.matches("^[A-Z0-9_-]{1,50}$")) {
            throw new IllegalArgumentException("Mã kích thước chỉ gồm chữ cái, chữ số, dấu gạch ngang (VD: S, M, L, XL, 2XL, 30)!");
        }
        kt.setMaKichThuoc(ma);

        String ten = kt.getTenKichThuoc() != null ? kt.getTenKichThuoc().trim() : "";
        if (ten.isEmpty()) {
            throw new IllegalArgumentException("Tên kích thước không được để trống!");
        }
        if (ten.length() > 50) {
            throw new IllegalArgumentException("Tên kích thước không được vượt quá 50 ký tự!");
        }
        kt.setTenKichThuoc(ten);
    }

    private long demSoBienTheSuDungKichThuoc(Long kichThuocId) {
        try {
            Long soBienThe = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [bien_the_san_pham] WHERE [ma_kich_thuoc_id] = ?",
                    Long.class,
                    kichThuocId
            );
            return soBienThe != null ? soBienThe : 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }
}
