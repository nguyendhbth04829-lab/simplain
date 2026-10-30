package vn.fpoly.simplain.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fpoly.simplain.dto.CapNhatTrangThaiVanDonRequest;
import vn.fpoly.simplain.dto.LichSuTrangThaiDonHangResponse;
import vn.fpoly.simplain.dto.TaoDonHangMod4Request;
import vn.fpoly.simplain.dto.TaoDonHangResponse;
import vn.fpoly.simplain.entity.ChiTietDonHang;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.exception.KhongDuTonKhoException;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.service.DonHangService;
import vn.fpoly.simplain.service.QuanLyVanDonService;
import vn.fpoly.simplain.service.TaoDonHangService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Đơn hàng & Vận đơn (Mod 5 / Đơn hàng - Hưng).
 * - US21: Tiếp nhận dữ liệu tạo mới Đơn hàng và Trừ tồn kho từ Dev 4.
 * - US22: Cập nhật trạng thái vận đơn (Chờ duyệt, Đang giao, Đã giao...) & Ghi log lịch sử.
 */
@Slf4j
@RestController
@RequestMapping("/api/don-hang")
@RequiredArgsConstructor
@Tag(name = "Mod 5: Quản lý Đơn hàng & Vận đơn", description = "Các API tạo đơn, cập nhật trạng thái vận đơn & tra cứu lịch sử (US21, US22)")
public class DonHangApiController {

    private final TaoDonHangService taoDonHangService;
    private final DonHangService donHangService;
    private final QuanLyVanDonService quanLyVanDonService;
    private final NhanVienRepository nhanVienRepository;

    /**
     * API Tạo mới Đơn hàng và Trừ tồn kho (US21 - Tiêu chí 1 & 2).
     * Endpoint: POST /api/don-hang/tao-moi (hoặc POST /api/don-hang/tao-don-hang)
     */
    @PostMapping(value = {"/tao-moi", "/tao-don-hang"})
    public ResponseEntity<?> taoDonHangTuMod4(@Valid @RequestBody TaoDonHangMod4Request request) {
        log.info("API US21: Nhận yêu cầu tạo đơn hàng từ Dev 4, Người nhận: {}, Tổng tiền: {}đ, Số mặt hàng: {}",
                request.getTenNguoiNhan(), request.getTongThanhToan(),
                request.getDanhSachChiTiet() != null ? request.getDanhSachChiTiet().size() : 0);

        try {
            TaoDonHangResponse response = taoDonHangService.taoDonHangVaTruTonKho(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (KhongDuTonKhoException e) {
            log.warn("API US21 Lỗi tồn kho: {}", e.getMessage());
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("error", "KHONG_DU_TON_KHO");
            err.put("message", e.getMessage());
            err.put("maBienTheId", e.getMaBienTheId());
            err.put("maSku", e.getMaSku());
            err.put("soLuongYeuCau", e.getSoLuongYeuCau());
            err.put("soLuongHienCo", e.getSoLuongHienCo());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        } catch (IllegalArgumentException e) {
            log.warn("API US21 Tham số không hợp lệ: {}", e.getMessage());
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("error", "DU_LIEU_KHONG_HOP_LE");
            err.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        } catch (Exception e) {
            log.error("API US21 Lỗi hệ thống khi tạo đơn hàng: {}", e.getMessage(), e);
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("error", "LOI_HE_THONG");
            err.put("message", "Đã xảy ra lỗi hệ thống khi tạo đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    /**
     * API Tra cứu thông tin chi tiết Đơn hàng theo mã đơn hàng.
     * Endpoint: GET /api/don-hang/{maDonHang}
     */
    @GetMapping("/{maDonHang}")
    public ResponseEntity<?> layChiTietDonHang(@PathVariable("maDonHang") String maDonHang) {
        try {
            DonHang donHang = donHangService.layTheoMaDonHang(maDonHang);
            List<ChiTietDonHang> chiTiets = donHangService.layChiTietTheoDonHangId(donHang.getId());

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("donHang", donHang);
            res.put("danhSachChiTiet", chiTiets);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Không tìm thấy đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }
    }

    /**
     * API Cập nhật trạng thái vận đơn & đơn hàng, ghi log lịch sử (US22 - Tiêu chí 1 & 2).
     * Endpoint: PUT /api/don-hang/{id}/trang-thai (hoặc POST /api/don-hang/{id}/cap-nhat-trang-thai)
     */
    @Operation(summary = "Cập nhật trạng thái vận đơn & đơn hàng (US22)",
               description = "Cho phép Admin/Kho cập nhật trạng thái (Chờ duyệt, Đang giao, Đã giao...), mã vận đơn và tự động ghi log vào lich_su_trang_thai_don_hang")
    @RequestMapping(value = {"/{id}/trang-thai", "/{id}/cap-nhat-trang-thai"}, method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<?> capNhatTrangThaiVanDon(
            @PathVariable("id") Long id,
            @Valid @RequestBody CapNhatTrangThaiVanDonRequest req) {
        try {
            NhanVien actor = null;
            if (req.getNguoiThucHienId() != null) {
                actor = nhanVienRepository.findById(req.getNguoiThucHienId()).orElse(null);
            }
            if (actor == null) {
                actor = nhanVienRepository.findAll().stream().findFirst().orElse(null);
            }

            DonHang donHang = quanLyVanDonService.capNhatTrangThaiVanDon(id, req, actor);

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "Cập nhật trạng thái vận đơn và ghi log lịch sử thành công!");
            res.put("donHangId", donHang.getId());
            res.put("maDonHang", donHang.getMaDonHang());
            res.put("maVanDon", donHang.getMaVanDon());
            res.put("maTrangThai", donHang.getTrangThaiDonHang().getMaTrangThai());
            res.put("tenTrangThai", donHang.getTrangThaiDonHang().getTenTrangThai());
            return ResponseEntity.ok(res);
        } catch (IllegalStateException | IllegalArgumentException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("error", "QUY_TRINH_KHONG_HOP_LE");
            err.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        } catch (Exception e) {
            log.error("API US22 Lỗi cập nhật trạng thái đơn hàng ID {}: {}", id, e.getMessage(), e);
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("error", "LOI_HE_THONG");
            err.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    /**
     * API Tra cứu Lịch sử thay đổi trạng thái & hành trình vận đơn (Tracking Timeline - US22).
     * Endpoint: GET /api/don-hang/{id}/lich-su
     */
    @Operation(summary = "Tra cứu Lịch sử trạng thái đơn hàng & vận đơn (US22)",
               description = "Lấy toàn bộ lịch sử các mốc chuyển trạng thái từ bảng lich_su_trang_thai_don_hang theo trình tự thời gian")
    @GetMapping("/{id}/lich-su")
    public ResponseEntity<?> layLichSuTrangThaiDonHang(@PathVariable("id") Long id) {
        try {
            List<LichSuTrangThaiDonHangResponse> lichSu = quanLyVanDonService.layLichSuDonHang(id);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("donHangId", id);
            res.put("tongSoMocLichSu", lichSu.size());
            res.put("danhSachLichSu", lichSu);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Lỗi lấy lịch sử: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}
