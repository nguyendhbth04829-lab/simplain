package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.TaoMaGiamGiaRequest;
import vn.fpoly.simplain.entity.MaGiamGia;
import vn.fpoly.simplain.repository.MaGiamGiaRepository;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service xử lý nghiệp vụ Quản lý & Tạo chương trình Mã giảm giá (Voucher) (US26 - Khoa).
 * - Tiêu chí 1: Tạo Voucher (% hoặc Tiền mặt).
 * - Tiêu chí 2: Đặt giới hạn ngân sách, lượt dùng tối đa, đơn tối thiểu.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaGiamGiaService {

    private final MaGiamGiaRepository maGiamGiaRepository;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String KY_TU_NGAU_NHIEN = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    /**
     * Tạo mới chương trình Mã giảm giá (US26).
     */
    @Transactional
    public MaGiamGia taoMaGiamGia(TaoMaGiamGiaRequest req) {
        log.info("Bắt đầu tạo chương trình mã giảm giá: {}", req.getMaCode());

        // Chuẩn hóa mã code viết hoa không khoảng trắng
        String maChuanHoa = (req.getMaCode() != null) ? req.getMaCode().trim().toUpperCase() : "";
        req.setMaCode(maChuanHoa);

        // 1. Kiểm tra tính hợp lệ và nghiệp vụ (Validation chuyên nghiệp)
        kiemTraHopLeVaValidate(req, null);

        // 2. Chuyển đổi và thiết lập entity
        MaGiamGia mg = MaGiamGia.builder()
                .maCode(maChuanHoa)
                .tenVoucher(req.getTenVoucher().trim())
                .loaiGiamGia(req.getLoaiGiamGia())
                .giaTriGiam(req.getGiaTriGiam())
                .giamToiDa("PERCENT".equalsIgnoreCase(req.getLoaiGiamGia()) ? req.getGiamToiDa() : req.getGiaTriGiam())
                .giaTriDonToiThieu(req.getGiaTriDonToiThieu() != null ? req.getGiaTriDonToiThieu() : BigDecimal.ZERO)
                .tongSoLuong(req.getTongSoLuong())
                .soLuongDaDung(0)
                .gioiHanMoiUser(req.getGioiHanMoiUser() != null ? req.getGioiHanMoiUser() : 1)
                .nganSach(req.getNganSach())
                .nganSachDaDung(BigDecimal.ZERO)
                .ngayBatDau(req.getNgayBatDau())
                .ngayKetThuc(req.getNgayKetThuc())
                .kichHoat(Boolean.TRUE.equals(req.getKichHoat()))
                .moTa(req.getMoTa() != null ? req.getMoTa().trim() : null)
                .ngayTao(LocalDateTime.now())
                .build();

        MaGiamGia saved = maGiamGiaRepository.save(mg);
        log.info("Tạo mã giảm giá thành công: ID={}, Code={}", saved.getId(), saved.getMaCode());
        return saved;
    }

    /**
     * Cập nhật thông tin chương trình Mã giảm giá (US26).
     */
    @Transactional
    public MaGiamGia capNhatMaGiamGia(Long id, TaoMaGiamGiaRequest req) {
        log.info("Cập nhật mã giảm giá ID={}", id);

        MaGiamGia existing = maGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã giảm giá với ID: " + id));

        String maChuanHoa = (req.getMaCode() != null) ? req.getMaCode().trim().toUpperCase() : "";
        req.setMaCode(maChuanHoa);

        // Kiểm tra hợp lệ (loại trừ chính ID đang sửa khi check trùng mã)
        kiemTraHopLeVaValidate(req, id);

        existing.setMaCode(maChuanHoa);
        existing.setTenVoucher(req.getTenVoucher().trim());
        existing.setLoaiGiamGia(req.getLoaiGiamGia());
        existing.setGiaTriGiam(req.getGiaTriGiam());
        existing.setGiamToiDa("PERCENT".equalsIgnoreCase(req.getLoaiGiamGia()) ? req.getGiamToiDa() : req.getGiaTriGiam());
        existing.setGiaTriDonToiThieu(req.getGiaTriDonToiThieu() != null ? req.getGiaTriDonToiThieu() : BigDecimal.ZERO);
        existing.setTongSoLuong(req.getTongSoLuong());
        existing.setGioiHanMoiUser(req.getGioiHanMoiUser() != null ? req.getGioiHanMoiUser() : 1);
        existing.setNganSach(req.getNganSach());
        existing.setNgayBatDau(req.getNgayBatDau());
        existing.setNgayKetThuc(req.getNgayKetThuc());
        existing.setKichHoat(Boolean.TRUE.equals(req.getKichHoat()));
        existing.setMoTa(req.getMoTa() != null ? req.getMoTa().trim() : null);

        MaGiamGia updated = maGiamGiaRepository.save(existing);
        log.info("Cập nhật mã giảm giá thành công: ID={}, Code={}", updated.getId(), updated.getMaCode());
        return updated;
    }

    /**
     * Bật / Tắt trạng thái kích hoạt của voucher (Toggle Active/Inactive).
     */
    @Transactional
    public boolean thayDoiTrangThaiKichHoat(Long id) {
        MaGiamGia voucher = maGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã giảm giá với ID: " + id));

        boolean trangThaiMoi = !Boolean.TRUE.equals(voucher.getKichHoat());
        voucher.setKichHoat(trangThaiMoi);
        maGiamGiaRepository.saveAndFlush(voucher);

        log.info("Đổi trạng thái kích hoạt voucher ID={} sang: {}", id, trangThaiMoi);
        return trangThaiMoi;
    }

    /**
     * Xóa chương trình mã giảm giá (Chặn xóa nếu đã có người sử dụng).
     */
    @Transactional
    public void xoaMaGiamGia(Long id) {
        MaGiamGia voucher = maGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã giảm giá với ID: " + id));

        if (voucher.getSoLuongDaDung() != null && voucher.getSoLuongDaDung() > 0) {
            throw new IllegalStateException("Mã giảm giá [" + voucher.getMaCode() +
                    "] đã có " + voucher.getSoLuongDaDung() + " đơn hàng áp dụng. Không thể xóa để bảo toàn dữ liệu lịch sử đối soát!");
        }

        maGiamGiaRepository.delete(voucher);
        log.info("Đã xóa mã giảm giá ID={}, Code={}", id, voucher.getMaCode());
    }

    /**
     * Lấy chi tiết một chương trình mã giảm giá.
     */
    @Transactional(readOnly = true)
    public MaGiamGia layChiTiet(Long id) {
        return maGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã giảm giá với ID: " + id));
    }

    /**
     * Lấy danh sách phân trang mã giảm giá với bộ lọc đa tiêu chí (US26).
     */
    @Transactional(readOnly = true)
    public Page<MaGiamGia> layDanhSachPhanTrang(String tuKhoa, String loaiGiamGia, String trangThai, Pageable pageable) {
        String kw = (tuKhoa != null && !tuKhoa.trim().isEmpty()) ? tuKhoa.trim() : null;
        String type = (loaiGiamGia != null && !loaiGiamGia.trim().isEmpty() && !"TAT_CA".equalsIgnoreCase(loaiGiamGia)) ? loaiGiamGia.trim() : null;

        Boolean kichHoatFilter = null;
        if ("TAM_NGUNG".equalsIgnoreCase(trangThai)) {
            kichHoatFilter = false;
        }

        Page<MaGiamGia> pageData = maGiamGiaRepository.timKiemPhanTrang(kw, type, kichHoatFilter, pageable);

        // Lọc thêm theo trạng thái thời gian / tiến độ nếu cần
        if (trangThai != null && !trangThai.isBlank() && !"TAT_CA".equalsIgnoreCase(trangThai) && !"TAM_NGUNG".equalsIgnoreCase(trangThai)) {
            List<MaGiamGia> filteredList = pageData.getContent().stream()
                    .filter(m -> {
                        return switch (trangThai.toUpperCase()) {
                            case "DANG_DIEN_RA" -> m.isDangDienRa();
                            case "SAP_DIEN_RA" -> m.isSapDienRa();
                            case "DA_KET_THUC" -> m.isDaKetThuc();
                            case "HET_LUOT" -> m.isHetLuot() || m.isHetNganSach();
                            default -> true;
                        };
                    })
                    .toList();

            return new PageImpl<>(filteredList, pageable, filteredList.size());
        }

        return pageData;
    }

    /**
     * Thống kê số lượng voucher theo từng phân loại trạng thái để hiển thị Tab Badge.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> thongKeSoLuongTheoTrangThai() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Long> stats = new HashMap<>();

        stats.put("TAT_CA", maGiamGiaRepository.count());
        stats.put("DANG_DIEN_RA", maGiamGiaRepository.countByKichHoatTrueAndNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(now, now));
        stats.put("SAP_DIEN_RA", maGiamGiaRepository.countByKichHoatTrueAndNgayBatDauAfter(now));
        stats.put("DA_KET_THUC", maGiamGiaRepository.countByNgayKetThucBefore(now));
        stats.put("TAM_NGUNG", maGiamGiaRepository.countByKichHoatFalse());

        return stats;
    }

    /**
     * Tiện ích tự động sinh mã Voucher ngẫu nhiên chuyên nghiệp cho Admin.
     * Ví dụ: SMPL20-7XK9, SIMPLAIN-W8P2.
     */
    public String sinhMaVoucherTuDong(String loaiGiamGia, BigDecimal giaTriGiam) {
        String prefix = "SMPL";
        if ("PERCENT".equalsIgnoreCase(loaiGiamGia) && giaTriGiam != null && giaTriGiam.compareTo(BigDecimal.ZERO) > 0) {
            prefix = "SMPL" + giaTriGiam.intValue();
        } else if ("FIXED_AMOUNT".equalsIgnoreCase(loaiGiamGia) && giaTriGiam != null && giaTriGiam.compareTo(BigDecimal.ZERO) > 0) {
            long k = giaTriGiam.divide(BigDecimal.valueOf(1000)).longValue();
            prefix = "SMPL" + k + "K";
        }

        String maMoi;
        int maxTry = 30;
        do {
            StringBuilder sb = new StringBuilder(prefix).append("-");
            for (int i = 0; i < 4; i++) {
                sb.append(KY_TU_NGAU_NHIEN.charAt(RANDOM.nextInt(KY_TU_NGAU_NHIEN.length())));
            }
            maMoi = sb.toString();
            maxTry--;
        } while (maGiamGiaRepository.existsByMaCodeIgnoreCase(maMoi) && maxTry > 0);

        return maMoi;
    }

    /**
     * Kiểm tra tính duy nhất của mã code phục vụ AJAX validation khi gõ.
     */
    @Transactional(readOnly = true)
    public boolean kiemTraMaCodeTonTai(String maCode, Long excludeId) {
        if (maCode == null || maCode.trim().isBlank()) return false;
        String code = maCode.trim();
        if (excludeId != null) {
            return maGiamGiaRepository.existsByMaCodeIgnoreCaseAndIdNot(code, excludeId);
        }
        return maGiamGiaRepository.existsByMaCodeIgnoreCase(code);
    }

    // =========================================================================
    // NGHIỆP VỤ VALIDATION CHUYÊN NGHIỆP (US26)
    // =========================================================================

    /**
     * Thực hiện kiểm tra tính toàn vẹn nghiệp vụ của biểu mẫu tạo mã giảm giá.
     */
    public void kiemTraHopLeVaValidate(TaoMaGiamGiaRequest req, Long excludeId) {
        // 1. Kiểm tra mã voucher không trùng lặp
        String code = req.getMaCode();
        if (code == null || code.trim().length() < 3) {
            throw new IllegalArgumentException("Mã voucher phải có độ dài từ 3 đến 30 ký tự!");
        }

        boolean trungCode = (excludeId != null)
                ? maGiamGiaRepository.existsByMaCodeIgnoreCaseAndIdNot(code, excludeId)
                : maGiamGiaRepository.existsByMaCodeIgnoreCase(code);

        if (trungCode) {
            throw new IllegalArgumentException("Mã voucher [" + code + "] đã tồn tại trong hệ thống. Vui lòng chọn mã khác!");
        }

        // 2. Validate loại giảm giá và giá trị chiết khấu (Tiêu chí 1)
        if ("PERCENT".equalsIgnoreCase(req.getLoaiGiamGia())) {
            if (req.getGiaTriGiam() == null || req.getGiaTriGiam().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Giá trị phần trăm giảm giá phải lớn hơn 0%!");
            }
            if (req.getGiaTriGiam().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Tỷ lệ giảm giá theo phần trăm không được vượt quá 100%!");
            }
            if (req.getGiamToiDa() == null || req.getGiamToiDa().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Vui lòng thiết lập mức Giảm tối đa (VNĐ) khi áp dụng giảm theo % để kiểm soát ngân sách!");
            }
        } else if ("FIXED_AMOUNT".equalsIgnoreCase(req.getLoaiGiamGia())) {
            if (req.getGiaTriGiam() == null || req.getGiaTriGiam().compareTo(BigDecimal.valueOf(1000)) < 0) {
                throw new IllegalArgumentException("Số tiền giảm trực tiếp phải tối thiểu từ 1,000 đ trở lên!");
            }
            if (req.getGiaTriDonToiThieu() != null && req.getGiaTriDonToiThieu().compareTo(BigDecimal.ZERO) > 0) {
                if (req.getGiaTriGiam().compareTo(req.getGiaTriDonToiThieu()) > 0) {
                    throw new IllegalArgumentException("Số tiền giảm (" + req.getGiaTriGiam() +
                            " đ) không được lớn hơn giá trị đơn hàng tối thiểu (" + req.getGiaTriDonToiThieu() + " đ)!");
                }
            }
        } else {
            throw new IllegalArgumentException("Hình thức giảm giá không hợp lệ. Chỉ chấp nhận PERCENT (% phần trăm) hoặc FIXED_AMOUNT (Tiền mặt)!");
        }

        // 3. Validate giá trị đơn tối thiểu (Tiêu chí 2)
        if (req.getGiaTriDonToiThieu() == null || req.getGiaTriDonToiThieu().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá trị đơn hàng tối thiểu không được nhỏ hơn 0 đ!");
        }

        // 4. Validate lượt dùng tối đa (Tiêu chí 2)
        if (req.getTongSoLuong() != null && req.getTongSoLuong() <= 0) {
            throw new IllegalArgumentException("Lượt dùng tối đa nếu thiết lập phải lớn hơn hoặc bằng 1 lượt!");
        }
        if (req.getGioiHanMoiUser() == null || req.getGioiHanMoiUser() <= 0) {
            throw new IllegalArgumentException("Giới hạn lượt dùng cho mỗi khách hàng phải từ 1 lần trở lên!");
        }
        if (req.getTongSoLuong() != null && req.getGioiHanMoiUser() > req.getTongSoLuong()) {
            throw new IllegalArgumentException("Giới hạn mỗi khách (" + req.getGioiHanMoiUser() +
                    " lần) không được vượt quá tổng lượt dùng phát hành (" + req.getTongSoLuong() + " lượt)!");
        }

        // 5. Validate ngân sách chương trình (Tiêu chí 2)
        if (req.getNganSach() != null) {
            if (req.getNganSach().compareTo(BigDecimal.valueOf(1000)) < 0) {
                throw new IllegalArgumentException("Giới hạn ngân sách chương trình phải từ 1,000 đ trở lên!");
            }
            if ("FIXED_AMOUNT".equalsIgnoreCase(req.getLoaiGiamGia()) && req.getGiaTriGiam() != null) {
                if (req.getNganSach().compareTo(req.getGiaTriGiam()) < 0) {
                    throw new IllegalArgumentException("Tổng ngân sách (" + req.getNganSach() +
                            " đ) không được nhỏ hơn số tiền giảm của 1 lượt dùng (" + req.getGiaTriGiam() + " đ)!");
                }
            }
        }

        // 6. Validate thời gian áp dụng
        if (req.getNgayBatDau() == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày giờ bắt đầu hiệu lực của voucher!");
        }
        if (req.getNgayKetThuc() == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày giờ kết thúc hiệu lực của voucher!");
        }
        if (!req.getNgayKetThuc().isAfter(req.getNgayBatDau())) {
            throw new IllegalArgumentException("Thời điểm kết thúc chương trình (" + req.getNgayKetThuc() +
                    ") phải sau thời điểm bắt đầu (" + req.getNgayBatDau() + ")!");
        }
    }
}
