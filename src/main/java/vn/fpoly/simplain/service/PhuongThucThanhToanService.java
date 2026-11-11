package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.fpoly.simplain.dto.PhuongThucThanhToanRequest;
import vn.fpoly.simplain.entity.PhuongThucThanhToan;
import vn.fpoly.simplain.repository.PhuongThucThanhToanRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Dịch vụ Quản lý Cổng thanh toán (US18).
 * - Tiêu chí 1: CRUD bảng phuong_thuc_thanh_toan (COD, VNPay, MoMo).
 * - Tính năng cốt lõi: Bật/Tắt cấu hình cổng thanh toán (đơn lẻ và hàng loạt).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PhuongThucThanhToanService {

    private static final long DUNG_LUONG_BIEU_TUONG_TOI_DA = 5L * 1024 * 1024; // 5MB
    private static final List<String> DUOI_ANH_HOP_LE = Arrays.asList(".jpg", ".jpeg", ".png", ".webp", ".gif", ".svg");

    private final PhuongThucThanhToanRepository repository;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Kiểm tra định dạng, dung lượng và lưu file ảnh biểu tượng vào thư mục uploads/.
     */
    public String luuVaValidateFileBieuTuong(MultipartFile fileBieuTuong) {
        if (fileBieuTuong == null || fileBieuTuong.isEmpty()) {
            return null;
        }

        if (fileBieuTuong.getSize() > DUNG_LUONG_BIEU_TUONG_TOI_DA) {
            throw new IllegalArgumentException("Kích thước file ảnh biểu tượng không được vượt quá 5MB!");
        }

        String tenGoc = fileBieuTuong.getOriginalFilename();
        if (tenGoc == null || tenGoc.isBlank() || !tenGoc.contains(".")) {
            throw new IllegalArgumentException("File biểu tượng tải lên không hợp lệ!");
        }

        String duoiFile = tenGoc.substring(tenGoc.lastIndexOf('.')).toLowerCase();
        if (!DUOI_ANH_HOP_LE.contains(duoiFile)) {
            throw new IllegalArgumentException(
                    "File biểu tượng tải lên phải là định dạng hình ảnh hợp lệ (JPG, PNG, WEBP, GIF, SVG)!"
            );
        }

        String contentType = fileBieuTuong.getContentType();
        if (contentType != null && !contentType.isBlank() && !contentType.toLowerCase().startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Nội dung file tải lên không phải là hình ảnh hợp lệ (Content-Type: " + contentType + ")!"
            );
        }

        try {
            String tenFileAnToan = "payment_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6) + duoiFile;
            Path thuMucUpload = Paths.get("uploads");
            if (!Files.exists(thuMucUpload)) {
                Files.createDirectories(thuMucUpload);
            }
            Files.copy(fileBieuTuong.getInputStream(), thuMucUpload.resolve(tenFileAnToan), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + tenFileAnToan;
        } catch (IOException e) {
            throw new IllegalStateException("Lỗi khi lưu file ảnh biểu tượng lên máy chủ: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách phân trang có tìm kiếm và bộ lọc.
     */
    @Transactional(readOnly = true)
    public Page<PhuongThucThanhToan> layDanhSachPhanTrang(
            String tuKhoa, Boolean laTrucTuyen, Boolean kichHoat, Pageable pageable) {
        String keyword = (tuKhoa != null && !tuKhoa.trim().isEmpty()) ? tuKhoa.trim() : null;
        return repository.timKiemPhanTrang(keyword, laTrucTuyen, kichHoat, pageable);
    }

    /**
     * Lấy tất cả phương thức thanh toán.
     */
    @Transactional(readOnly = true)
    public List<PhuongThucThanhToan> layTatCa() {
        return repository.findAll();
    }

    /**
     * Lấy danh sách phương thức đang được Bật kích hoạt (phục vụ Storefront Checkout).
     */
    @Transactional(readOnly = true)
    public List<PhuongThucThanhToan> layDanhSachDangKichHoat() {
        return repository.findByKichHoatTrueOrderByIdAsc();
    }

    /**
     * Lấy thông tin chi tiết theo ID.
     */
    @Transactional(readOnly = true)
    public PhuongThucThanhToan layTheoId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID phương thức thanh toán không hợp lệ!");
        }
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phương thức thanh toán ID: " + id));
    }

    /**
     * Thêm mới phương thức thanh toán (C trong CRUD).
     */
    @Transactional
    public PhuongThucThanhToan them(PhuongThucThanhToanRequest request) {
        return them(request, null);
    }

    /**
     * Thêm mới phương thức thanh toán kèm tải lên file ảnh biểu tượng từ máy tính (US18).
     */
    @Transactional
    public PhuongThucThanhToan them(PhuongThucThanhToanRequest request, MultipartFile fileBieuTuong) {
        if (request == null) {
            throw new IllegalArgumentException("Dữ liệu phương thức thanh toán không được để trống!");
        }

        String maChuan = request.getMaPhuongThuc() != null ? request.getMaPhuongThuc().trim().toUpperCase() : "";
        if (repository.existsByMaPhuongThucIgnoreCase(maChuan)) {
            throw new IllegalArgumentException("Mã phương thức thanh toán [" + maChuan + "] đã tồn tại trên hệ thống!");
        }

        String bieuTuong = null;
        if (fileBieuTuong != null && !fileBieuTuong.isEmpty()) {
            bieuTuong = luuVaValidateFileBieuTuong(fileBieuTuong);
        } else if (request.getBieuTuong() != null && !request.getBieuTuong().isBlank()) {
            bieuTuong = request.getBieuTuong().trim();
        } else {
            bieuTuong = Boolean.TRUE.equals(request.getLaTrucTuyen())
                    ? "fa-solid fa-credit-card text-blue-600"
                    : "fa-solid fa-money-bill-wave text-emerald-600";
        }

        PhuongThucThanhToan entity = PhuongThucThanhToan.builder()
                .maPhuongThuc(maChuan)
                .tenPhuongThuc(request.getTenPhuongThuc().trim())
                .laTrucTuyen(Boolean.TRUE.equals(request.getLaTrucTuyen()))
                .kichHoat(request.getKichHoat() == null || request.getKichHoat())
                .moTa(request.getMoTa() != null ? request.getMoTa().trim() : null)
                .bieuTuong(bieuTuong != null ? bieuTuong.trim() : null)
                .cauHinh(request.getCauHinh() != null ? request.getCauHinh().trim() : null)
                .build();

        return repository.save(entity);
    }

    /**
     * Cập nhật phương thức thanh toán (U trong CRUD).
     */
    @Transactional
    public PhuongThucThanhToan sua(Long id, PhuongThucThanhToanRequest request) {
        return sua(id, request, null);
    }

    /**
     * Cập nhật phương thức thanh toán kèm cập nhật ảnh biểu tượng mới (US18).
     */
    @Transactional
    public PhuongThucThanhToan sua(Long id, PhuongThucThanhToanRequest request, MultipartFile fileBieuTuong) {
        PhuongThucThanhToan entity = layTheoId(id);

        String maChuan = request.getMaPhuongThuc() != null ? request.getMaPhuongThuc().trim().toUpperCase() : "";
        if (repository.existsByMaPhuongThucIgnoreCaseAndIdNot(maChuan, id)) {
            throw new IllegalArgumentException("Mã phương thức thanh toán [" + maChuan + "] đã được sử dụng bởi cổng khác!");
        }

        entity.setMaPhuongThuc(maChuan);
        entity.setTenPhuongThuc(request.getTenPhuongThuc().trim());
        entity.setLaTrucTuyen(Boolean.TRUE.equals(request.getLaTrucTuyen()));
        if (request.getKichHoat() != null) {
            entity.setKichHoat(request.getKichHoat());
        }
        entity.setMoTa(request.getMoTa() != null ? request.getMoTa().trim() : null);

        if (fileBieuTuong != null && !fileBieuTuong.isEmpty()) {
            String duongDanAnh = luuVaValidateFileBieuTuong(fileBieuTuong);
            entity.setBieuTuong(duongDanAnh);
        } else if (request.getBieuTuong() != null && !request.getBieuTuong().isBlank()) {
            entity.setBieuTuong(request.getBieuTuong().trim());
        }

        entity.setCauHinh(request.getCauHinh() != null ? request.getCauHinh().trim() : null);

        return repository.save(entity);
    }

    /**
     * Bật/Tắt kích hoạt cổng thanh toán (Tính năng cốt lõi US18).
     * @return trạng thái kích hoạt mới sau khi đảo
     */
    @Transactional
    public boolean batTatKichHoat(Long id) {
        PhuongThucThanhToan entity = layTheoId(id);
        boolean trangThaiMoi = !Boolean.TRUE.equals(entity.getKichHoat());
        entity.setKichHoat(trangThaiMoi);
        repository.save(entity);
        log.info("Admin đã {} cổng thanh toán [{} - {}]",
                trangThaiMoi ? "BẬT" : "TẮT", entity.getMaPhuongThuc(), entity.getTenPhuongThuc());
        return trangThaiMoi;
    }

    /**
     * Thao tác hàng loạt: Bật hoặc Tắt kích hoạt nhiều cổng thanh toán.
     */
    @Transactional
    public int capNhatKichHoatHangLoat(List<Long> ids, boolean kichHoat) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (Long id : ids) {
            if (id != null && repository.existsById(id)) {
                PhuongThucThanhToan entity = repository.findById(id).orElse(null);
                if (entity != null) {
                    entity.setKichHoat(kichHoat);
                    repository.save(entity);
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Xóa phương thức thanh toán (D trong CRUD).
     * Chặn xóa an toàn nếu đã phát sinh dữ liệu trong giao_dich_thanh_toan.
     */
    @Transactional
    public void xoa(Long id) {
        PhuongThucThanhToan entity = layTheoId(id);

        long soGiaoDich = demGiaoDichPhatSinh(id);
        if (soGiaoDich > 0) {
            throw new IllegalStateException("Không thể xóa cổng thanh toán [" + entity.getTenPhuongThuc()
                    + "] vì đã phát sinh " + soGiaoDich + " giao dịch thực tế trên hệ thống! Bạn có thể chọn [Tắt kích hoạt] thay vì xóa.");
        }

        repository.delete(entity);
        log.info("Admin đã xóa cổng thanh toán ID: {}, Mã: {}", id, entity.getMaPhuongThuc());
    }

    /**
     * Thao tác hàng loạt: Xóa nhiều phương thức thanh toán được chọn.
     */
    @Transactional
    public List<String> xoaHangLoat(List<Long> ids) {
        List<String> thongBaoLoi = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            return thongBaoLoi;
        }

        for (Long id : ids) {
            try {
                xoa(id);
            } catch (Exception e) {
                thongBaoLoi.add("Mục ID " + id + ": " + e.getMessage());
            }
        }
        return thongBaoLoi;
    }

    /**
     * Đếm số lượng giao dịch đã phát sinh tham chiếu đến cổng thanh toán.
     */
    private long demGiaoDichPhatSinh(Long phuongThucId) {
        try {
            Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM giao_dich_thanh_toan WHERE ma_phuong_thuc_id = ?",
                    Long.class, phuongThucId);
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}
