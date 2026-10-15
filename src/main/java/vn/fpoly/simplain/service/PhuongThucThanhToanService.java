package vn.fpoly.simplain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.fpoly.simplain.entity.PhuongThucThanhToan;
import vn.fpoly.simplain.repository.PhuongThucThanhToanRepository;

/**
 * Xử lý logic phương thức thanh toán.
 * Controller chỉ gọi sang đây, không gọi Repository trực tiếp.
 */
@Service
public class PhuongThucThanhToanService {

    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    public PhuongThucThanhToanService(PhuongThucThanhToanRepository phuongThucThanhToanRepository) {
        this.phuongThucThanhToanRepository = phuongThucThanhToanRepository;
    }

    /**
     * Lấy trang danh sách, tìm theo mã HOẶC tên + lọc trạng thái.
     *
     * @param page số trang bắt đầu từ 0
     * @param ten từ khóa tìm mã/tên, có thể null
     * @param kichHoat lọc kích hoạt, null là tất cả
     * @param trucTuyen lọc trực tuyến, null là tất cả
     * @return trang phương thức thanh toán
     */
    public Page<PhuongThucThanhToan> layTrang(int page, String ten, Boolean kichHoat, Boolean trucTuyen) {
        if (page < 0) page = 0;
        String tuKhoa = ten == null ? null : ten.trim();
        if ((tuKhoa == null || tuKhoa.isEmpty()) && kichHoat == null && trucTuyen == null) {
            return phuongThucThanhToanRepository.findAll(PageRequest.of(page, 5));
        }
        return phuongThucThanhToanRepository.timKiem(tuKhoa, kichHoat, trucTuyen, PageRequest.of(page, 5));
    }

    /** Giữ tương thích chỗ cũ chỉ tìm theo tên. */
    public Page<PhuongThucThanhToan> layTrang(int page, String ten) {
        return layTrang(page, ten, null, null);
    }

    /**
     * Lưu mới hoặc cập nhật. Kiểm tra trùng mã trước khi lưu.
     *
     * @param entity dữ liệu từ form
     * @return thông báo lỗi nếu trùng mã, null nếu hợp lệ
     */
    public String luu(PhuongThucThanhToan entity) {
        boolean trungMa;
        if (entity.getId() == null) {
            trungMa = phuongThucThanhToanRepository.existsByMaPhuongThuc(entity.getMaPhuongThuc());
        } else {
            trungMa = phuongThucThanhToanRepository.existsByMaPhuongThucAndIdNot(entity.getMaPhuongThuc(), entity.getId());
        }
        if (trungMa) {
            return "Mã phương thức đã tồn tại";
        }
        // Chuẩn hóa mã: viết hoa, không dấu cách
        entity.setMaPhuongThuc(entity.getMaPhuongThuc().trim().toUpperCase());
        phuongThucThanhToanRepository.save(entity);
        return null;
    }

    /**
     * Bật/tắt nhanh một cổng thanh toán.
     *
     * @param id mã bản ghi
     */
    public void doiTrangThai(Long id) {
        PhuongThucThanhToan entity = phuongThucThanhToanRepository.findById(id).orElse(null);
        if (entity != null) {
            entity.setKichHoat(!Boolean.TRUE.equals(entity.getKichHoat()));
            phuongThucThanhToanRepository.save(entity);
        }
    }

    public PhuongThucThanhToan timTheoId(Long id) {
        return phuongThucThanhToanRepository.findById(id).orElse(null);
    }
}
