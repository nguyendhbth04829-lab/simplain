package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.CapNhatTrangThaiVanDonRequest;
import vn.fpoly.simplain.dto.LichSuTrangThaiDonHangResponse;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dịch vụ Quản lý Trạng thái Vận đơn & Ghi log Lịch sử thay đổi (US22 - Mod 5 - Hưng).
 * - Tiêu chí 1: Cập nhật trạng thái đơn hàng (Chờ duyệt, Đang xử lý, Đang giao, Hoàn thành, Đã hủy) & Mã vận đơn.
 * - Tiêu chí 2: Ghi log [lich_su_trang_thai_don_hang] kèm thông tin người thao tác, thời gian, lý do.
 * - Nghiệp vụ tích hợp: Tích điểm Loyalty khi Hoàn thành (US16), Hoàn trả tồn kho khi Hủy đơn (US21).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuanLyVanDonService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final DonHangRepository donHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final NhanVienRepository nhanVienRepository;
    private final LoyaltyHoiVienService loyaltyHoiVienService;
    private final YeuCauDoiTraRepository yeuCauDoiTraRepository;

    /**
     * Cập nhật trạng thái đơn hàng và mã vận đơn (US22 - Tiêu chí 1 & 2).
     *
     * @param donHangId ID của đơn hàng cần cập nhật
     * @param req Dữ liệu cập nhật (mã trạng thái, mã vận đơn, ghi chú)
     * @param nguoiThucHien Nhân viên thực hiện thao tác (nếu có)
     * @return Đơn hàng sau khi đã cập nhật
     */
    @Transactional(rollbackFor = Exception.class)
    public DonHang capNhatTrangThaiVanDon(Long donHangId, CapNhatTrangThaiVanDonRequest req, NhanVien nguoiThucHien) {
        if (donHangId == null) {
            throw new IllegalArgumentException("ID đơn hàng không được để trống!");
        }
        if (req == null || req.getMaTrangThai() == null || req.getMaTrangThai().isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn trạng thái mới cho đơn hàng!");
        }

        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng ID: " + donHangId));

        String maTrangThaiMoi = req.getMaTrangThai().trim();
        TrangThaiDonHang ttMoi = trangThaiDonHangRepository.findByMaTrangThai(maTrangThaiMoi)
                .orElseThrow(() -> new IllegalArgumentException("Mã trạng thái [" + maTrangThaiMoi + "] không hợp lệ!"));

        String oldMaTrangThai = (donHang.getTrangThaiDonHang() != null)
                ? donHang.getTrangThaiDonHang().getMaTrangThai()
                : "";
        String oldTenTrangThai = (donHang.getTrangThaiDonHang() != null)
                ? donHang.getTrangThaiDonHang().getTenTrangThai()
                : "Chưa rõ";

        // 1. Chặn thay đổi trạng thái khi đơn hàng đang trong tiến trình Đổi/Trả hoặc đã kết thúc Đổi/Trả
        boolean dangCoRma = yeuCauDoiTraRepository.existsByMaDonHangIdAndTrangThaiIn(
                donHangId, List.of("CHO_XU_LY", "DA_TIEP_NHAN", "DANG_XU_LY"));
        if (dangCoRma || "DANG_DOI_TRA".equalsIgnoreCase(oldMaTrangThai)) {
            throw new IllegalStateException("Đơn hàng #" + donHang.getMaDonHang()
                    + " đang trong tiến trình Đổi / Trả (RMA). Trạng thái đơn hàng được bảo vệ nghiêm ngặt và chỉ được cập nhật tự động thông qua màn hình Xét duyệt Đổi/Trả (US25)!");
        }
        if ("DA_TRA_HANG".equalsIgnoreCase(oldMaTrangThai)) {
            throw new IllegalStateException("Đơn hàng #" + donHang.getMaDonHang()
                    + " đã hoàn tất quy trình Trả hàng & Hoàn tiền. Trạng thái kết thúc, không thể thay đổi!");
        }
        if ("DA_DOI_HANG".equalsIgnoreCase(oldMaTrangThai)) {
            throw new IllegalStateException("Đơn hàng #" + donHang.getMaDonHang()
                    + " đã hoàn tất quy trình Đổi hàng mới. Trạng thái kết thúc, không được phép thay đổi!");
        }

        // Chặn chọn thủ công các trạng thái Đổi/Trả từ dropdown cập nhật vận đơn thông thường
        if ("DANG_DOI_TRA".equalsIgnoreCase(maTrangThaiMoi)
                || "DA_TRA_HANG".equalsIgnoreCase(maTrangThaiMoi)
                || "DA_DOI_HANG".equalsIgnoreCase(maTrangThaiMoi)) {
            throw new IllegalStateException("Trạng thái [" + ttMoi.getTenTrangThai()
                    + "] là trạng thái nghiệp vụ Đổi/Trả (RMA). Chỉ được hệ thống cập nhật tự động khi khách gửi yêu cầu hoặc CSKH xét duyệt!");
        }

        // 2. Validate quy trình chuyển đổi trạng thái chuẩn sàn TMĐT (Shopee Workflow)
        kiemTraChuyenDoiTrangThaiHopLe(oldMaTrangThai, maTrangThaiMoi);

        // 2. Cập nhật Mã Vận Đơn (nếu chuyển sang Đang giao và chưa có thì tự động sinh)
        if (req.getMaVanDon() != null && !req.getMaVanDon().isBlank()) {
            donHang.setMaVanDon(req.getMaVanDon().trim());
        } else if ("DANG_GIAO".equalsIgnoreCase(maTrangThaiMoi) && (donHang.getMaVanDon() == null || donHang.getMaVanDon().isBlank())) {
            // Tự động sinh mã vận đơn nếu chuyển sang Đang giao hàng mà chưa có mã vận đơn
            String maTuSinh = "SMPL-VD" + (System.currentTimeMillis() % 100000000);
            donHang.setMaVanDon(maTuSinh);
        }

        // 3. Cập nhật Trạng thái đơn hàng
        donHang.setTrangThaiDonHang(ttMoi);

        // 4. Nghiệp vụ chuyển tiếp:
        // A. Chuyển sang HOÀN THÀNH -> Tích lũy điểm Loyalty Coolcash (US16)
        if ("HOAN_THANH".equalsIgnoreCase(maTrangThaiMoi) && !"HOAN_THANH".equalsIgnoreCase(oldMaTrangThai)) {
            try {
                loyaltyHoiVienService.xuLyTichDiemVaThangHang(donHang.getId());
                log.info("US22 -> US16: Đã tích điểm Loyalty thành công cho đơn #{}", donHang.getMaDonHang());
            } catch (Exception ex) {
                log.warn("US22: Lỗi tích điểm Loyalty cho đơn #{}: {}", donHang.getMaDonHang(), ex.getMessage());
            }
        }

        // B. Chuyển sang ĐÃ HỦY -> Hoàn trả tồn kho thật cho các mặt hàng (US21)
        if ("DA_HUY".equalsIgnoreCase(maTrangThaiMoi) && !"DA_HUY".equalsIgnoreCase(oldMaTrangThai)) {
            List<ChiTietDonHang> items = chiTietDonHangRepository.findByMaDonHangId(donHang.getId());
            for (ChiTietDonHang item : items) {
                if (item.getMaBienTheId() != null && item.getSoLuong() != null && item.getSoLuong() > 0) {
                    bienTheSanPhamRepository.hoanTonKhoAtomic(item.getMaBienTheId(), item.getSoLuong());
                    log.info("US22 -> US21: Đã hoàn trả {} sản phẩm SKU: {} vào kho do hủy đơn #{}",
                            item.getSoLuong(), item.getMaSku(), donHang.getMaDonHang());
                }
            }
        }

        DonHang donHangSaved = donHangRepository.save(donHang);

        // 5. Tiêu chí 2: Ghi log [lich_su_trang_thai_don_hang]
        NhanVien actor = nguoiThucHien;
        if (actor == null && req.getNguoiThucHienId() != null) {
            actor = nhanVienRepository.findById(req.getNguoiThucHienId()).orElse(null);
        }

        StringBuilder ghiChuBuilder = new StringBuilder();
        if (req.getGhiChu() != null && !req.getGhiChu().isBlank()) {
            ghiChuBuilder.append(req.getGhiChu().trim());
        } else {
            ghiChuBuilder.append("Cập nhật trạng thái từ [")
                    .append(oldTenTrangThai)
                    .append("] sang [")
                    .append(ttMoi.getTenTrangThai())
                    .append("]");
            if (donHangSaved.getMaVanDon() != null && !donHangSaved.getMaVanDon().isBlank()) {
                ghiChuBuilder.append(" (Mã vận đơn: ").append(donHangSaved.getMaVanDon()).append(")");
            }
        }

        LichSuTrangThaiDonHang logRecord = LichSuTrangThaiDonHang.builder()
                .donHang(donHangSaved)
                .trangThaiMoi(ttMoi)
                .nguoiThucHien(actor)
                .ghiChu(ghiChuBuilder.toString())
                .ngayChuyen(LocalDateTime.now())
                .build();

        lichSuTrangThaiDonHangRepository.save(logRecord);

        log.info("US22: Đã cập nhật trạng thái đơn hàng #{} sang [{}] kèm mã vận đơn [{}], ghi log lịch sử thành công!",
                donHangSaved.getMaDonHang(), ttMoi.getTenTrangThai(), donHangSaved.getMaVanDon());

        return donHangSaved;
    }

    /**
     * Ghi nhận mốc lịch sử khởi tạo khi đơn hàng được tạo mới lần đầu (US22 & US21).
     */
    @Transactional
    public void ghiLogLichSuKhoiTao(DonHang donHang, String ghiChu) {
        if (donHang == null || donHang.getId() == null) return;

        TrangThaiDonHang tt = donHang.getTrangThaiDonHang();
        if (tt == null) {
            tt = trangThaiDonHangRepository.findById(1L).orElse(null);
        }

        String note = (ghiChu != null && !ghiChu.isBlank())
                ? ghiChu
                : "Khởi tạo đơn hàng từ Checkout Storefront";

        LichSuTrangThaiDonHang logRecord = LichSuTrangThaiDonHang.builder()
                .donHang(donHang)
                .trangThaiMoi(tt)
                .nguoiThucHien(null)
                .ghiChu(note)
                .ngayChuyen(donHang.getNgayTao() != null ? donHang.getNgayTao() : LocalDateTime.now())
                .build();

        lichSuTrangThaiDonHangRepository.save(logRecord);
    }

    /**
     * Lấy toàn bộ lịch sử trạng thái theo trình tự thời gian (Tracking Timeline) (US22).
     */
    @Transactional(readOnly = true)
    public List<LichSuTrangThaiDonHangResponse> layLichSuDonHang(Long donHangId) {
        if (donHangId == null) {
            return Collections.emptyList();
        }

        List<LichSuTrangThaiDonHang> ds = lichSuTrangThaiDonHangRepository.layLichSuTheoDonHangIdAsc(donHangId);

        return ds.stream().map(item -> {
            String tenNguoiThucHien = "Hệ thống";
            String maNv = "SYSTEM";
            Long nvId = null;

            if (item.getNguoiThucHien() != null) {
                tenNguoiThucHien = item.getNguoiThucHien().getHoTen();
                maNv = item.getNguoiThucHien().getMaNhanVien();
                nvId = item.getNguoiThucHien().getId();
            }

            String formattedTime = (item.getNgayChuyen() != null)
                    ? item.getNgayChuyen().format(DATE_FORMATTER)
                    : "";

            return LichSuTrangThaiDonHangResponse.builder()
                    .id(item.getId())
                    .donHangId(item.getDonHang().getId())
                    .maDonHang(item.getDonHang().getMaDonHang())
                    .maTrangThai(item.getTrangThaiMoi().getMaTrangThai())
                    .tenTrangThai(item.getTrangThaiMoi().getTenTrangThai())
                    .maMauHex(item.getTrangThaiMoi().getMaMauHex())
                    .nguoiThucHienId(nvId)
                    .tenNguoiThucHien(tenNguoiThucHien)
                    .maNhanVien(maNv)
                    .ghiChu(item.getGhiChu())
                    .ngayChuyen(item.getNgayChuyen())
                    .formattedNgayChuyen(formattedTime)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * Validate luồng chuyển đổi trạng thái đơn hàng theo quy trình chuẩn của sàn TMĐT (Shopee workflow).
     * Ngăn chặn việc thay đổi trạng thái tùy ý, nhảy cóc hoặc đảo ngược bất hợp lý.
     *
     * @param oldStatus Trạng thái hiện tại của đơn hàng
     * @param newStatus Trạng thái mới muốn chuyển sang
     */
    public void kiemTraChuyenDoiTrangThaiHopLe(String oldStatus, String newStatus) {
        if (oldStatus == null || oldStatus.isBlank()) return;

        // Nếu giữ nguyên trạng thái (ví dụ cập nhật lại mã vận đơn hoặc ghi chú tiến trình)
        if (oldStatus.equalsIgnoreCase(newStatus)) {
            if ("HOAN_THANH".equalsIgnoreCase(oldStatus)) {
                throw new IllegalStateException("Đơn hàng đã [Hoàn thành], quy trình xử lý đã kết thúc và không thể thay đổi trạng thái!");
            }
            if ("DA_HUY".equalsIgnoreCase(oldStatus)) {
                throw new IllegalStateException("Đơn hàng đã [Hủy], quy trình đã kết thúc và không thể thay đổi trạng thái!");
            }
            return;
        }

        switch (oldStatus.toUpperCase()) {
            case "CHO_XAC_NHAN":
                // Từ Chờ xác nhận: Chỉ được sang Đang xử lý (đóng gói), Đang giao hàng (nếu giao ngay) hoặc Đã hủy
                if (!"DANG_XU_LY".equalsIgnoreCase(newStatus)
                        && !"DANG_GIAO".equalsIgnoreCase(newStatus)
                        && !"DA_HUY".equalsIgnoreCase(newStatus)) {
                    throw new IllegalStateException("Đơn hàng đang ở trạng thái [Chờ xác nhận], chỉ có thể chuyển sang [Đang xử lý] để đóng gói, [Đang giao hàng] hoặc [Đã hủy]!");
                }
                break;

            case "DANG_XU_LY":
                // Từ Đang xử lý: Chỉ được sang Đang giao hoặc Đã hủy
                if (!"DANG_GIAO".equalsIgnoreCase(newStatus) && !"DA_HUY".equalsIgnoreCase(newStatus)) {
                    if ("CHO_XAC_NHAN".equalsIgnoreCase(newStatus)) {
                        throw new IllegalStateException("Đơn hàng đã tiếp nhận xử lý trong kho, không thể lùi lại trạng thái [Chờ xác nhận]!");
                    } else if ("HOAN_THANH".equalsIgnoreCase(newStatus)) {
                        throw new IllegalStateException("Đơn hàng đang xử lý trong kho, chưa xuất kho giao hàng nên không thể chuyển sang [Hoàn thành]!");
                    } else {
                        throw new IllegalStateException("Chuyển đổi trạng thái từ [Đang xử lý] sang [" + newStatus + "] không hợp lệ theo quy trình chuẩn!");
                    }
                }
                break;

            case "DANG_GIAO":
                // Từ Đang giao: Chỉ được sang Hoàn thành (giao thành công) hoặc Đã hủy (giao thất bại / bom hàng)
                if (!"HOAN_THANH".equalsIgnoreCase(newStatus) && !"DA_HUY".equalsIgnoreCase(newStatus)) {
                    if ("CHO_XAC_NHAN".equalsIgnoreCase(newStatus) || "DANG_XU_LY".equalsIgnoreCase(newStatus)) {
                        throw new IllegalStateException("Hàng đã xuất kho và đang trên đường giao, không thể quay ngược lại [Chờ xác nhận] hoặc [Đang xử lý]!");
                    } else {
                        throw new IllegalStateException("Chuyển đổi trạng thái từ [Đang giao hàng] sang [" + newStatus + "] không hợp lệ theo quy trình chuẩn!");
                    }
                }
                break;

            case "HOAN_THANH":
                // Trạng thái kết thúc: Khóa hoàn toàn
                throw new IllegalStateException("Đơn hàng đã ở trạng thái kết thúc [Hoàn thành], khách đã nhận hàng và đã tích điểm loyalty. Không được phép thay đổi trạng thái!");

            case "DA_HUY":
                // Trạng thái kết thúc: Khóa hoàn toàn
                throw new IllegalStateException("Đơn hàng đã ở trạng thái kết thúc [Đã hủy] và đã hoàn trả tồn kho. Không thể thay đổi trạng thái!");

            case "DANG_DOI_TRA":
                throw new IllegalStateException("Đơn hàng đang ở trạng thái [Đang đổi / trả] do có yêu cầu RMA. Chỉ được cập nhật trạng thái thông qua xét duyệt Đổi/Trả!");

            case "DA_TRA_HANG":
                throw new IllegalStateException("Đơn hàng đã ở trạng thái kết thúc [Đã trả hàng / Hoàn tiền]. Không thể thay đổi trạng thái!");

            case "DA_DOI_HANG":
                throw new IllegalStateException("Đơn hàng đã ở trạng thái kết thúc [Đã đổi hàng]. Không thể thay đổi trạng thái!");

            default:
                break;
        }
    }

    /**
     * Lấy danh sách các mã trạng thái hợp lệ tiếp theo theo quy trình Shopee.
     */
    public List<String> layDanhSachMaTrangThaiHopLeTiepTheo(String currentStatus) {
        if (currentStatus == null || currentStatus.isBlank()) {
            return List.of("CHO_XAC_NHAN", "DANG_XU_LY", "DANG_GIAO", "HOAN_THANH", "DA_HUY");
        }

        switch (currentStatus.toUpperCase()) {
            case "CHO_XAC_NHAN":
                return List.of("DANG_XU_LY", "DANG_GIAO", "DA_HUY");
            case "DANG_XU_LY":
                return List.of("DANG_GIAO", "DA_HUY", "DANG_XU_LY");
            case "DANG_GIAO":
                return List.of("HOAN_THANH", "DA_HUY", "DANG_GIAO");
            case "HOAN_THANH":
            case "DA_HUY":
            case "DANG_DOI_TRA":
            case "DA_TRA_HANG":
            case "DA_DOI_HANG":
            default:
                return Collections.emptyList();
        }
    }
}
