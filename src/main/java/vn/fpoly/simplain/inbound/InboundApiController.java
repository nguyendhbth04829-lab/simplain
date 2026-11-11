package vn.fpoly.simplain.inbound;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Nhập kho & Nhà cung cấp", description = "API quản lý Nhà cung cấp và nhập hàng vào kho")
@RestController
@RequestMapping("/api/inbound")
public class InboundApiController {

    private final InboundService inboundService;
    private final PhieuNhapKhoRepository phieuNhapKhoRepository;
    private final LichSuHopTacService lichSuHopTacService;
    private final DonMuaHangService donMuaHangService;
    private final ChiTietKiemHangRepository chiTietKiemHangRepository;

    @Autowired
    public InboundApiController(InboundService inboundService,
                                PhieuNhapKhoRepository phieuNhapKhoRepository,
                                LichSuHopTacService lichSuHopTacService,
                                DonMuaHangService donMuaHangService,
                                ChiTietKiemHangRepository chiTietKiemHangRepository) {
        this.inboundService = inboundService;
        this.phieuNhapKhoRepository = phieuNhapKhoRepository;
        this.lichSuHopTacService = lichSuHopTacService;
        this.donMuaHangService = donMuaHangService;
        this.chiTietKiemHangRepository = chiTietKiemHangRepository;
    }

    @Operation(summary = "Lấy danh sách tất cả Nhà cung cấp")
    @GetMapping("/nha-cung-cap")
    public ResponseEntity<List<NhaCungCap>> layDanhSachNhaCungCap() {
        return ResponseEntity.ok(inboundService.layTatCaNhaCungCap());
    }

    @Operation(summary = "Tạo mới một Nhà cung cấp")
    @PostMapping(value = "/nha-cung-cap", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NhaCungCap> taoNhaCungCap(@Valid @RequestBody NhaCungCapRequest request,
                                                     HttpSession session) {
        NhaCungCap ncc = inboundService.taoNhaCungCap(request, layNhanVienId(session), layNhanVienTen(session));
        return ResponseEntity.status(HttpStatus.CREATED).body(ncc);
    }

    @PostMapping(value = "/nha-cung-cap", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NhaCungCap> taoNhaCungCapKemLogo(
            @Valid @RequestPart("request") NhaCungCapRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            HttpSession session) {
        NhaCungCap ncc = inboundService.taoNhaCungCap(
                request, logo, layNhanVienId(session), layNhanVienTen(session));
        return ResponseEntity.status(HttpStatus.CREATED).body(ncc);
    }

    @Operation(summary = "Cập nhật thông tin Nhà cung cấp")
    @PutMapping(value = "/nha-cung-cap/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NhaCungCap> capNhatNhaCungCap(@PathVariable Long id,
                                                       @RequestBody NhaCungCapRequest request,
                                                       HttpSession session) {
        return ResponseEntity.ok(inboundService.capNhatNhaCungCap(
                id, request, layNhanVienId(session), layNhanVienTen(session)));
    }

    @PutMapping(value = "/nha-cung-cap/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NhaCungCap> capNhatNhaCungCapKemLogo(@PathVariable Long id,
            @Valid @RequestPart("request") NhaCungCapRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            HttpSession session) {
        return ResponseEntity.ok(inboundService.capNhatNhaCungCap(
                id, request, logo, layNhanVienId(session), layNhanVienTen(session)));
    }

    @Operation(summary = "Ghi nhận một hoạt động hợp tác với nhà cung cấp")
    @PostMapping("/nha-cung-cap/{id}/lich-su-hop-tac")
    public ResponseEntity<LichSuHopTacNhaCungCap> ghiNhanHoatDongHopTac(@PathVariable Long id,
                                                                        @Valid @RequestBody LichSuHopTacRequest request,
                                                                        HttpSession session) {
        LichSuHopTacNhaCungCap activity = lichSuHopTacService.themHoatDong(
                id, request, layNhanVienId(session), layNhanVienTen(session));
        return ResponseEntity.status(HttpStatus.CREATED).body(activity);
    }

    @Operation(summary = "Lấy hoạt động hợp tác mới nhất của nhà cung cấp")
    @GetMapping("/nha-cung-cap/{id}/lich-su-hop-tac/moi-nhat")
    public ResponseEntity<LichSuHopTacNhaCungCap> layHoatDongHopTacMoiNhat(@PathVariable Long id) {
        return lichSuHopTacService.layMoiNhatTheoNhaCungCap(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Xóa/Ngưng hoạt động Nhà cung cấp")
    @DeleteMapping("/nha-cung-cap/{id}")
    public ResponseEntity<Void> xoaNhaCungCap(@PathVariable Long id, HttpSession session) {
        inboundService.xoaNhaCungCap(id, layNhanVienId(session), layNhanVienTen(session));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Chuyển nhà cung cấp sang trạng thái tiếp theo trong quy trình hợp tác")
    @PutMapping("/nha-cung-cap/{id}/trang-thai")
    public ResponseEntity<NhaCungCap> toggleTrangThaiNcc(@PathVariable Long id, HttpSession session) {
        return ResponseEntity.ok(inboundService.toggleTrangThaiNcc(
                id, layNhanVienId(session), layNhanVienTen(session)));
    }

    @Operation(summary = "Thực hiện nhập kho hàng hóa từ Nhà cung cấp")
    @PostMapping("/nhap-kho")
    public ResponseEntity<NhapKhoResponse> nhapKho(@Valid @RequestBody NhapKhoRequest request) {
        NhapKhoResponse response = inboundService.thucHienNhapKho(request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Lấy danh sách đơn mua hàng")
    @GetMapping("/don-mua")
    public ResponseEntity<List<DonMuaHangResponse>> layDanhSachDonMua() {
        return ResponseEntity.ok(donMuaHangService.layDanhSach());
    }

    @Operation(summary = "Tạo đơn mua hàng nháp")
    @PostMapping("/don-mua")
    public ResponseEntity<DonMuaHangResponse> taoDonMua(@Valid @RequestBody DonMuaHangRequest request, HttpSession session) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donMuaHangService.taoDon(
                request, layNhanVienId(session), layNhanVienTen(session)));
    }

    @GetMapping("/don-mua/{id}")
    public ResponseEntity<DonMuaHangResponse> layChiTietDonMua(@PathVariable Long id) {
        return ResponseEntity.ok(donMuaHangService.layChiTiet(id));
    }

    @PutMapping("/don-mua/{id}")
    public ResponseEntity<DonMuaHangResponse> capNhatDonNhap(@PathVariable Long id,
            @Valid @RequestBody DonMuaHangRequest request, HttpSession session) {
        return ResponseEntity.ok(donMuaHangService.capNhatDonNhap(id, request,
                layNhanVienId(session), layNhanVienTen(session)));
    }

    @PutMapping("/don-mua/{id}/gia-thoa-thuan")
    public ResponseEntity<DonMuaHangResponse> capNhatGiaThoaThuan(@PathVariable Long id,
            @Valid @RequestBody DonMuaHangRequest request, HttpSession session) {
        return ResponseEntity.ok(donMuaHangService.capNhatGiaThoaThuan(id, request,
                layNhanVienId(session), layNhanVienTen(session)));
    }

    @PutMapping("/don-mua/{id}/trang-thai")
    public ResponseEntity<DonMuaHangResponse> chuyenTrangThaiDonMua(@PathVariable Long id,
            @Valid @RequestBody ChuyenTrangThaiDonMuaRequest request, HttpSession session) {
        return ResponseEntity.ok(donMuaHangService.chuyenTrangThai(id, request.getTrangThai(), request.getGhiChu(),
                layNhanVienId(session), layNhanVienTen(session)));
    }

    @PostMapping("/don-mua/{id}/lan-giao")
    public ResponseEntity<DonMuaHangResponse> batDauLanGiao(@PathVariable Long id, HttpSession session) {
        return ResponseEntity.ok(donMuaHangService.batDauLanGiao(id, layNhanVienId(session), layNhanVienTen(session)));
    }

    @PutMapping("/don-mua/{id}/lan-giao/{lanGiaoId}/that-bai")
    public ResponseEntity<DonMuaHangResponse> baoGiaoThatBai(@PathVariable Long id, @PathVariable Long lanGiaoId,
            @Valid @RequestBody ThatBaiLanGiaoHangRequest request, HttpSession session) {
        return ResponseEntity.ok(donMuaHangService.baoGiaoThatBai(id, lanGiaoId, request,
                layNhanVienId(session), layNhanVienTen(session)));
    }

    @PutMapping("/don-mua/{id}/lan-giao/{lanGiaoId}/da-giao")
    public ResponseEntity<DonMuaHangResponse> xacNhanDaGiao(@PathVariable Long id, @PathVariable Long lanGiaoId,
                                                             HttpSession session) {
        return ResponseEntity.ok(donMuaHangService.xacNhanDaGiao(id, lanGiaoId,
                layNhanVienId(session), layNhanVienTen(session)));
    }

    @PostMapping("/don-mua/{id}/nhan-hang")
    public ResponseEntity<NhapKhoResponse> tiepNhanHang(@PathVariable Long id,
            @Valid @RequestBody TiepNhanDonMuaRequest request, HttpSession session) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donMuaHangService.tiepNhanHang(
                id, request, layNhanVienId(session), layNhanVienTen(session)));
    }

    @Operation(summary = "Lấy danh sách phiếu nhập kho")
    @GetMapping("/phieu-nhap-kho")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachPhieuNhapKho() {
        List<Map<String, Object>> result = phieuNhapKhoRepository.findAllWithNhaCungCap().stream().map(p -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", p.getId());
            item.put("maPhieu", p.getMaPhieu());
            item.put("maDonMua", p.getDonMuaHang() != null ? p.getDonMuaHang().getMaDon() : "");
            item.put("nhaCungCap", p.getNhaCungCap() != null ? p.getNhaCungCap().getTenNcc() : "N/A");
            item.put("maNcc", p.getNhaCungCap() != null ? p.getNhaCungCap().getMaNcc() : "");
            item.put("ngayNhap", p.getNgayNhap());
            item.put("tongTien", p.getTongTien());
            item.put("trangThai", p.getTrangThai() != null ? p.getTrangThai().getNhan() : "");
            item.put("ketQuaKiemHang", p.getKetQuaKiemHang() != null ? p.getKetQuaKiemHang().getNhan() : "");
            item.put("ghiChu", p.getGhiChu() != null ? p.getGhiChu() : "");
            return item;
        }).toList();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Lấy chi tiết một Phiếu nhập kho")
    @GetMapping("/phieu-nhap-kho/{id}")
    public ResponseEntity<Map<String, Object>> layChiTietPhieuNhapKho(@PathVariable Long id) {
        PhieuNhapKho p = phieuNhapKhoRepository.findWithDetailsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu nhập kho có ID: " + id));

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id", p.getId());
        res.put("maPhieu", p.getMaPhieu());
        res.put("maDonMua", p.getDonMuaHang() != null ? p.getDonMuaHang().getMaDon() : "");
        res.put("lanGiaoThu", p.getLanGiaoHang() != null ? p.getLanGiaoHang().getLanThu() : null);
        res.put("ketQuaKiemHang", p.getKetQuaKiemHang() != null ? p.getKetQuaKiemHang().getNhan() : "");
        res.put("nhaCungCap", p.getNhaCungCap() != null ? p.getNhaCungCap().getTenNcc() : "N/A");
        res.put("maNcc", p.getNhaCungCap() != null ? p.getNhaCungCap().getMaNcc() : "");
        res.put("ngayNhap", p.getNgayNhap() != null ? p.getNgayNhap().toString() : "");
        res.put("tongTien", p.getTongTien());
        res.put("trangThai", p.getTrangThai() != null ? p.getTrangThai().getNhan() : "");
        res.put("ghiChu", p.getGhiChu() != null ? p.getGhiChu() : "");

        java.util.List<Map<String, Object>> chiTiets = new java.util.ArrayList<>();
        if (p.getChiTietList() != null) {
            for (ChiTietPhieuNhapKho ct : p.getChiTietList()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("maSku", ct.getBienThe() != null ? ct.getBienThe().getMaSku() : "N/A");
                item.put("soLuongNhap", ct.getSoLuongNhap());
                item.put("donGiaNhap", ct.getDonGiaNhap());
                item.put("thanhTien", ct.getThanhTien());
                chiTiets.add(item);
            }
        }
        res.put("chiTietList", chiTiets);

        java.util.List<Map<String, Object>> chiTietKiem = new java.util.ArrayList<>();
        for (ChiTietKiemHang ct : chiTietKiemHangRepository.findByPhieuNhapKhoIdOrderById(id)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("maSku", ct.getChiTietDonMua().getMaSku());
            item.put("soLuongDaGiao", ct.getSoLuongDaGiao());
            item.put("soLuongDat", ct.getSoLuongDat());
            item.put("soLuongTuChoi", ct.getSoLuongTuChoi());
            item.put("soLuongDaTra", ct.getSoLuongDaTra());
            item.put("lyDoTuChoi", ct.getLyDoTuChoi());
            chiTietKiem.add(item);
        }
        res.put("chiTietKiemHang", chiTietKiem);

        return ResponseEntity.ok(res);
    }

    /**
     * Chuẩn hóa lỗi nghiệp vụ thành một hợp đồng JSON thống nhất cho mọi máy khách.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> xuLyLoiXacThuc(MethodArgumentNotValidException ex) {
        String thongBao = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Dữ liệu nhập kho chưa hợp lệ");
        return ResponseEntity.badRequest().body(taoPhanHoiLoi("DU_LIEU_KHONG_HOP_LE", thongBao));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> xuLyLoiDuLieu(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(taoPhanHoiLoi("DU_LIEU_KHONG_HOP_LE", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> xuLyLoiTrangThai(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(taoPhanHoiLoi("XUNG_DOT_TRANG_THAI", ex.getMessage()));
    }

    private Map<String, Object> taoPhanHoiLoi(String maLoi, String thongBao) {
        Map<String, Object> phanHoi = new LinkedHashMap<>();
        phanHoi.put("trangThai", maLoi);
        phanHoi.put("thongBao", thongBao);
        // Giữ khóa message để tương thích với các đoạn JavaScript hiện có.
        phanHoi.put("message", thongBao);
        return phanHoi;
    }

    private Long layNhanVienId(HttpSession session) {
        XacThucNhanVienResponse user = layNhanVien(session);
        return user != null ? user.getId() : null;
    }

    private String layNhanVienTen(HttpSession session) {
        XacThucNhanVienResponse user = layNhanVien(session);
        return user != null ? user.getHoTen() : null;
    }

    private XacThucNhanVienResponse layNhanVien(HttpSession session) {
        if (session == null) return null;
        Object user = session.getAttribute("nhanVienHienTai");
        return user instanceof XacThucNhanVienResponse auth ? auth : null;
    }
}
